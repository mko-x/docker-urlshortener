/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2015 m-ko-x.de Markus Kosmal
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package de.mko.web.lightlink

import java.net.URL
import java.util.Date

import scala.collection.JavaConversions.asScalaBuffer
import scala.collection.breakOut

import com.datastax.driver.core.Cluster
import com.datastax.driver.core.querybuilder.{ QueryBuilder => q }
import com.github.nscala_time.time.Imports.DateTime
import com.github.nscala_time.time.Imports.richAbstractInstant

import akka.actor.Actor
import akka.actor.Props
import akka.pattern.pipe

trait DatabaseComponent {
  self: AkkaComponent =>

  def cassandraSeeds: List[String]

  case class GetUrl(id: String)
  case class GetUrlResult(id: String, url: URL)
  case object UrlNotFoundException extends RuntimeException

  case class VisitUrl(id: String)

  case class GetVisits(id: String)
  case class GetVisitsResult(id: String, visits: Map[Date, Long])

  case class StoreUrl(id: String, url: URL)
  case object StoreUrlResult
  case object StoreUrlAlreadyExistsException extends RuntimeException

  lazy val databaseActor = actorSystem.actorOf(Props(new DatabaseActor))

  lazy val cluster = {
    val cluster = (Cluster.builder /: cassandraSeeds)(_ addContactPoint _).build.connect
    cluster.execute("""
      CREATE KEYSPACE IF NOT EXISTS lightlink WITH replication = {
        'class': 'SimpleStrategy',
        'replication_factor': 2
      }
    """)

    cluster.execute("""
      CREATE COLUMNFAMILY IF NOT EXISTS lightlink.urls (
        id text,
        url text,
        PRIMARY KEY (id)
      )
    """)

    cluster.execute("""
      CREATE COLUMNFAMILY IF NOT EXISTS lightlink.visits (
        id text,
        time timestamp,
        visits counter,
        PRIMARY KEY (id, time)
      )
    """)

    cluster
  }

  class DatabaseActor extends Actor {
    import context.dispatcher

    val fetchQuery = cluster.prepare(
      q.select("url")
        .from("lightlink", "urls")
        .where(q.eq("id", q.bindMarker)))

    val visitQuery = cluster.prepare(
      q.update("lightlink", "visits")
        .`with`(q.incr("visits"))
        .where(q.eq("id", q.bindMarker))
        .and(q.eq("time", q.bindMarker))
        .using(q.ttl(31 * 24 * 60 * 60)))

    val fetchVisitsQuery = cluster.prepare(
      q.select("time", "visits")
        .from("lightlink", "visits")
        .where(q.eq("id", q.bindMarker))
        .and(q.gt("time", DateTime.now.withMillisOfHour(0).minusDays(30).date)))

    val storeQuery = cluster.prepare(
      q.insertInto("lightlink", "urls")
        .ifNotExists
        .value("id", q.bindMarker)
        .value("url", q.bindMarker))

    override def receive = {
      case GetUrl(id) =>
        cluster.executeAsync(fetchQuery.bind(id)).map { row =>
          if (row.isExhausted) throw UrlNotFoundException
          else GetUrlResult(id, new URL(row.one.getString("url")))
        }.pipeTo(sender)
      case VisitUrl(id) =>
        cluster.executeAsync(visitQuery.bind(id, DateTime.now.withMillisOfHour(0).date))
      case GetVisits(id) =>
        cluster.executeAsync(fetchVisitsQuery.bind(id)).map { row =>
          if (row.isExhausted) throw UrlNotFoundException
          else {
            val visits: Map[Date, Long] = row.all.toList.map { row =>
              row.getDate("time") -> row.getLong("visits")
            }(breakOut)
            GetVisitsResult(id, visits)
          }
        }.pipeTo(sender)
      case StoreUrl(id, url) =>
        cluster.executeAsync(storeQuery.bind(id, url.toString)).map(_.one.getBool("[applied]")).map {
          case true => StoreUrlResult
          case _ => throw StoreUrlAlreadyExistsException
        }.pipeTo(sender)
    }
  }
}

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

import java.util.Locale
import java.util.TimeZone

import scala.collection.JavaConverters.asScalaBufferConverter

import com.typesafe.scalalogging.StrictLogging

object Boot extends App with StrictLogging {
  TimeZone.setDefault(TimeZone.getTimeZone("UTC"))
  Locale.setDefault(Locale.DE)

  val applicationContext = new AnyRef with AkkaComponent with HttpComponent with DatabaseComponent with ConfigurationComponent {
    override def cassandraSeeds = config.getStringList("cassandra.seeds").asScala.toList

    override val httpBindAddress = config.getString("http.bind.address")

    override val httpBindPort = config.getInt("http.bind.port")
  }
  applicationContext.cluster
  applicationContext.httpActor
  logger.info("Started url shortening engine.")
}

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
package de.mko.web

import scala.concurrent.ExecutionContextExecutor
import scala.concurrent.Promise
import scala.language.implicitConversions
import scala.util.Try

import com.github.nscala_time.time.Imports.DateTime
import com.google.common.util.concurrent.ListenableFuture
import com.typesafe.config.Config

import spray.http.Uri

package object lightlink {
  implicit def functionToRunnable[T](f: () => T) = new Runnable {
    override def run = f()
  }

  implicit def listenableFutureToFuture[T](lf: ListenableFuture[T])(implicit c: ExecutionContextExecutor) = {
    val promise = Promise[T]
    lf.addListener(() => promise.success(lf.get), c)
    promise.future
  }

  implicit class RichConfig(conf: Config) {
    def getOptionalString(path: String) = Try(conf.getString(path)).toOption
  }

  implicit class RichString(uri: Uri) {
    def /(segment: String) = uri.withPath(uri.path / segment)
  }

  implicit class RichDateTime(d: DateTime) {
    def withMillisOfHour(millis: Int) = d
      .withMinuteOfHour((millis / 1000 / 60) % 60)
      .withSecondOfMinute((millis / 1000) % 60)
      .withMillisOfSecond(millis % 1000)
  }
}

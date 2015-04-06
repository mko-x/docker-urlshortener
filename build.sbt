/*The MIT License (MIT)
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
 
name := "lightlink"

organization := "de.mko.web.shorten"

version := "1.0"

scalaVersion := "2.11.2"

scalacOptions ++= Seq("-feature", "-unchecked", "-deprecation")

libraryDependencies ++= Seq(
  "ch.qos.logback" % "logback-classic" % "1.1.2",
  "com.typesafe.scala-logging" %% "scala-logging" % "3.1.0",
  "com.typesafe" % "config" % "1.2.1",
  "com.typesafe.akka" %% "akka-actor" % "2.3.6",
  "com.typesafe.akka" %% "akka-slf4j" % "2.3.6",
  "io.spray" %% "spray-can" % "1.3.1",
  "io.spray" %% "spray-routing" % "1.3.1",
  "org.json4s" %% "json4s-native" % "3.2.10",
  "com.datastax.cassandra" % "cassandra-driver-core" % "2.1.1",
  "org.xerial.snappy" % "snappy-java" % "1.0.5",
  "net.jpountz.lz4" % "lz4" % "1.2.0",
  "com.github.nscala-time" %% "nscala-time" % "1.4.0"
)

unmanagedSourceDirectories in Compile <++= baseDirectory { base =>
  Seq(
    base / "src" / "main" / "resources",
    base / "src" / "pack" / "etc")
}

unmanagedClasspath in Runtime <+= baseDirectory map { base =>
  Attributed.blank(base / "src" / "pack" / "etc")
}

packSettings

packMain := Map("lightlink" -> "de.mko.web.shorten.Boot")

packJvmOpts := Map("lightlink" -> Seq("-Dlogback.configurationFile=${PROG_HOME}/etc/logback.xml"))

packExtraClasspath := Map("lightlink" -> Seq("${PROG_HOME}/etc"))

packGenerateWindowsBatFile := false

releaseSettings

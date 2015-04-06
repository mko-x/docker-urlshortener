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
 
jsSettings

sourceDirectory in (Compile, JsKeys.js) <<= (sourceDirectory in Compile)(_ / "javascript")
includeFilter in (Compile, JsKeys.js) := "*.jsm"
resourceManaged in (Compile, JsKeys.js) <<= (resourceManaged in Compile)(_ / "de" / "mko" / "web" / "lightlink")
resourceGenerators in Compile <+= (JsKeys.js in Compile)
compile in Compile <<= compile in Compile dependsOn (JsKeys.js in Compile)

webResourceSettings
webResources ++= Map(
  "bootstrap.min.css" -> "http://maxcdn.bootstrapcdn.com/bootstrap/3.1.1/css/bootstrap.min.css",
  "bootstrap-theme.min.css" -> "http://maxcdn.bootstrapcdn.com/bootstrap/3.1.1/css/bootstrap-theme.min.css")
resourceGenerators in Compile <+= resolveWebResources
managedResourceDirectories in Compile <+= webResourcesBase

lessSettings

sourceDirectories in (Compile, LessKeys.less) <<= (sourceDirectory in Compile, webResourcesBase) { (srcDir, wrb) =>
  Seq(srcDir / "less", wrb)
}

resourceManaged in (Compile, LessKeys.less) <<= (resourceManaged in Compile)(_ / "de" / "mko" / "web" / "lightlink")
resourceGenerators in Compile <+= (LessKeys.less in Compile)

LessKeys.less in Compile <<= LessKeys.less in Compile dependsOn (resolveWebResources in Compile)
compile in Compile <<= compile in Compile dependsOn (LessKeys.less in Compile)

unmanagedSourceDirectories in Compile <++= baseDirectory { base =>
  Seq(
    base / "src" / "main" / "javascript",
    base / "src" / "main" / "less")
}

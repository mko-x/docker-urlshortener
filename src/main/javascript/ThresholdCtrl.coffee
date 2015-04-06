### The MIT License (MIT)

 Copyright (c) 2015 m-ko-x.de Markus Kosmal

 Permission is hereby granted, free of charge, to any person obtaining a copy
 of this software and associated documentation files (the "Software"), to deal
 in the Software without restriction, including without limitation the rights
 to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 copies of the Software, and to permit persons to whom the Software is
 furnished to do so, subject to the following conditions:
 
 The above copyright notice and this permission notice shall be included in all
 copies or substantial portions of the Software.
 
 THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 SOFTWARE.
###

lightlink = angular.module "lightlink"

lightlink.controller "ThresholdCtrl", ["$scope", "lightlinkBaam", ($scope, lightlinkBaam) ->
  $scope.link = ""
  $scope.linkHasError = false

  $scope.name = ""
  $scope.nameHasError = false

  $scope.shortened = null
  $scope.shortenedStats = null

  urlRegex = /^(.*)\/([^/]+)$/

  $scope.shorten = (link, id) ->
    lightlinkBaam.shorten
      "id" : id
    , link
    , (r, headers) ->
      $scope.shortened = headers("Location")
      [_, prefix, id] = urlRegex.exec($scope.shortened)
      $scope.shortenedStats = "#{prefix}/stats/#{id}"
      $scope.linkHasError = false
      $scope.nameHasError = false
    , (err) ->
      if (err.status == 400)
        $scope.linkHasError = true
      else if (err.status == 409)
        $scope.linkHasError = false
        $scope.nameHasError = true
      $scope.shortened = null
      $scope.shortenedStats = null
]

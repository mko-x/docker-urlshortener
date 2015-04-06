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

lightlink.controller "ClassificationCtrl", ["$scope", "$routeParams", "lightlinkBaam", ($scope, $routeParams, lightlinkBaam) ->
  $scope.noGraphs = false

  extractData = (d, dateFormat) ->
    offsets : (Date.create(e.offset).format(dateFormat) for e in d)
    visits : (e.count for e in d)

  createChart = (id, data) ->
    ctx = document.getElementById(id).getContext("2d")
    data =
      labels : data.offsets
      datasets : [
        fillColor : "rgba(97, 196, 25, .2)"
        strokeColor : "rgba(47, 146, 1, 1)"
        pointColor : "rgba(122, 221, 50, 1)"
        pointStrokeColor : "#ccc"
        pointHighlightFill : "#ccc"
        pointHighlightStroke : "rgba(122, 221, 50, 1)"
        data : data.visits
      ]
    new Chart(ctx).Line(data, (responsive : true))

  updateChart = (chart, data) ->
    for i in [0..chart.datasets[0].points.length-1]
      chart.datasets[0].points[i].value = data.visits[i]
    chart.update()

  last24HoursChart = null
  last30DaysChart = null

  reload = ->
    lightlinkBaam.visits
      id : $routeParams.id
    , (r) ->
      $scope.noGraphs = false
      if(last24HoursChart)
        updateChart(last24HoursChart, extractData(r.last24Hours, "{HH}:00"))
      else
        last24HoursChart = createChart("last24HoursChart", extractData(r.last24Hours, "{HH}:00"))

      if(last30DaysChart)
        updateChart(last30DaysChart, extractData(r.last30Days, "{MM}-{dd}"))
      else
        last30DaysChart = createChart("last30DaysChart", extractData(r.last30Days, "{MM}-{dd}"))
    , (err) ->
      $scope.noGraphs = true

  reloadLoop = ->
    reload()
    setTimeout(reloadLoop, 5000)

  reloadLoop()
]

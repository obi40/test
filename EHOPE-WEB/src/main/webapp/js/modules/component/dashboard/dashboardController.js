define(['app', 'util', 'commonData'], function (app, util, commonData) {
    'use strict';
    app.controller('dashboardCtrl', [
        '$scope',
        'dashboardService',
        '$window',
        function (
            $scope,
            dashboardService,
            $window
        ) {
            $scope.counters = [
                {
                    label: util.systemMessages.totalOrders,
                    icon: "fas fa-clipboard",
                    amount: 0,
                    color: "#AB47BC",
                    getData: function () {
                        var counter = this;
                        counter.fetchingData = true;
                        dashboardService.getTotalVisits().then(function (response) {
                            counter.amount = response.data;
                            counter.fetchingData = false;
                        });
                    },
                    fetchingData: false
                },
                {
                    label: util.systemMessages.newPatients,
                    icon: "fas fa-users",
                    amount: 0,
                    color: "#ef5350",
                    getData: function () {
                        var counter = this;
                        counter.fetchingData = true;
                        dashboardService.getTotalNewPatients().then(function (response) {
                            counter.amount = response.data;
                            counter.fetchingData = false;
                        });
                    },
                    fetchingData: false
                },
                {
                    label: "Coming Soon...",
                    icon: "fas fa-question",
                    amount: "#",
                    color: "#26A69A"
                },
                {
                    label: "Coming Soon...",
                    icon: "fas fa-question",
                    amount: "#",
                    color: "#78909C"
                }
            ];
            for (var idx = 0; idx < $scope.counters.length; idx++) {
                var counter = $scope.counters[idx];
                if (counter.getData != null) {
                    counter.getData();
                }
            }

            $scope.patientsStatus = {
                id: "patientsStatus",
                label: util.systemMessages.patient,
                fetchingData: false,
                chartOptions: {
                    dataSource: new kendo.data.DataSource({
                        transport: {
                            read: function (e) {
                                dashboardService.getTotalActivePatients().then(function (response) {

                                    var total = response.data.active + response.data.inactive;
                                    var data = [
                                        {
                                            category: util.systemMessages.active,
                                            value: util.round((response.data.active / total), 2)
                                        },
                                        {
                                            category: util.systemMessages.inactive,
                                            value: util.round((response.data.inactive / total), 2)
                                        }];

                                    e.success(data);
                                });
                            }
                        }
                    }),
                    legend: {
                        visible: false
                    },
                    chartArea: {
                        background: ""
                    },
                    seriesDefaults: {
                        labels: {
                            visible: true,
                            background: "transparent",//
                            template: "#= category #: \n #= value#%"
                        }
                    },
                    series: [{
                        type: "pie",
                        startAngle: 150,
                        field: "value",
                        padding: 50
                    }],
                    tooltip: {
                        visible: true,
                        format: "{0}%"
                    },
                    render: function (e) {
                        $scope.patientsStatus.fetchingData = false;
                    }
                }
            };
            $scope.refreshChart = function (chart) {
                chart.chartOptions.dataSource.read();
            };
            $scope.charts = [$scope.patientsStatus];
            function generateCharts() {
                //todo:add general settings
                for (var idx = 0; idx < $scope.charts.length; idx++) {
                    var chart = $scope.charts[idx];
                    chart.fetchingData = true;
                    $("#" + chart.id).kendoChart(chart.chartOptions);
                }
            }

            angular.element(document).ready(function () {
                generateCharts();
                $(document).bind("kendo:skinChange", generateCharts);
            });

            angular.element($window).bind('resize', function () {
                //auto resize charts
                kendo.resize($("div.k-chart[data-role='chart']"));
            });
        }
    ]);
});

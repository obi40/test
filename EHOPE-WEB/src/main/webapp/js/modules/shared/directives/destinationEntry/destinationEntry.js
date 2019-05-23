define(['app', 'util', 'commonData', 'config'], function (app, util, commonData, config) {
    'use strict';
    app.directive('destinationEntry', function () {
        return {
            restrict: 'E',
            replace: false,
            scope: {},
            templateUrl: "./" + config.lisDir + "/modules/shared/directives/destinationEntry/destination-entry-view.html",
            controller: ['$scope', 'destinationEntryService', 'WizardHandler',
                function ($scope, destinationEntryService, WizardHandler) {

                    $scope.userLocale = util.userLocale;
                    $scope.testDestinations = null;
                    $scope.multipleDestinations = true;//to show/hide message
                    var visitRid = null;

                    $scope.$on(commonData.events.activateDestinationEntry, function (event, params) {
                        visitRid = params;
                        activateDirective();
                    });

                    function activateDirective() {
                        $scope.activateDirective = true;
                        $scope.multipleDestinations = true;//reset
                        $scope.testDestinations = null;//reset
                        destinationEntryService.getDestinationEntryData(visitRid).then(function (response) {
                            var testActuals = response.data.testsActuals;
                            $scope.testDestinations = [];
                            for (var idx = 0; idx < testActuals.length; idx++) {
                                $scope.testDestinations.push({
                                    testActual: testActuals[idx],
                                    destinations: [],
                                    selectedDestinationRid: testActuals[idx].testDestination.rid,
                                    previousDestinationRid: testActuals[idx].testDestination.rid
                                });
                            }
                            var destinations = response.data.destinations;
                            // map tests with their active destinations
                            for (var idx = 0; idx < destinations.length; idx++) {
                                for (var i = 0; i < $scope.testDestinations.length; i++) {
                                    if ($scope.testDestinations[i].testActual.testDefinition.rid === destinations[idx].testDefinition.rid) {
                                        $scope.testDestinations[i].destinations.push(destinations[idx]);
                                    }
                                }
                            }

                            var tdLength = $scope.testDestinations.length - 1;
                            for (var idx = tdLength; idx >= 0; idx--) {
                                if ($scope.testDestinations[idx].destinations.length == 1) {
                                    $scope.testDestinations.splice(idx, 1);
                                }
                            }
                            //skip
                            if ($scope.testDestinations.length == 0) {
                                $scope.multipleDestinations = false;
                                WizardHandler.wizard().next();
                                $scope.$emit(commonData.events.exitDestinationEntry, null);
                            }

                        });


                        $scope.submit = function () {
                            var wrapper = {};
                            //since the radio button group uses the rid to work correctly
                            //so we set the actual selected destination
                            for (var idx = 0; idx < $scope.testDestinations.length; idx++) {
                                var td = $scope.testDestinations[idx];
                                //send only the changed tests destinations
                                if (td.previousDestinationRid === td.selectedDestinationRid) {
                                    continue;
                                }
                                for (var i = 0; i < td.destinations.length; i++) {
                                    if (td.selectedDestinationRid === td.destinations[i].rid) {
                                        wrapper[td.testActual.rid] = td.destinations[i];
                                        break;
                                    }
                                }
                            }
                            if ($scope.testDestinations.length === 0 || util.isObjectEmpty(wrapper)) {
                                $scope.$emit(commonData.events.exitDestinationEntry, null);
                                return;
                            }

                            destinationEntryService.updateActualTestsDestinations(wrapper).then(function () {
                                $scope.$emit(commonData.events.exitDestinationEntry, null);
                            });


                        };

                    }
                }]
        }
    });
});
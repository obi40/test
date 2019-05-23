define(['app', 'util', 'config'], function (app, util, config) {
    'use strict';
    app.directive('testSpecimenSetup', function () {
        return {
            restrict: 'E', // This means that it will be used as an element.
            replace: true,
            scope: {
                options: "=options"
                //{
                //  testDefinition: { testSpecimens: [] }
                //}
            },
            templateUrl: "./" + config.lisDir + "/modules/shared/directives/testSpecimenSetup/test-specimen-setup-view.html",
            controller: ['$scope', '$filter', '$q', 'testSpecimenSetupService', 'lovService',
                function ($scope, $filter, $q, testSpecimenSetupService, lovService) {

                    $scope.stabilityPattern = '^[1-9][0-9]*[hHdD]$';
                    $scope.stabilityHint = "(eg. 12h, 2d)";

                    var stabilityUnitValues = [];

                    $scope.containerTypeValues = [];
                    $scope.containerTypeOptions = [];

                    $scope.temperatureValues = []
                    $scope.temperatureOptions = [];

                    var getContainerTypes = lovService.getLkpByClass({ className: "LkpContainerType" })
                        .then(function (data) {
                            $scope.containerTypeValues = data;
                        });

                    var getTemperatures = lovService.getLkpByClass({ className: "LkpSpecimenTemperature" })
                        .then(function (data) {
                            $scope.temperatureValues = data;
                        });

                    var getStabilityUnits = lovService.getLkpByClass({ className: "LkpSpecimenStabilityUnit" })
                        .then(function (data) {
                            stabilityUnitValues = data;
                        });

                    $q.all([getContainerTypes, getTemperatures, getStabilityUnits]).then(function () {
                        $scope.options.testDefinition.testSpecimenList = [];
                        if ($scope.options.testDefinition.testSpecimens) {
                            var tempSpecimens = angular.copy($scope.options.testDefinition.testSpecimens);
                            for (var i = 0; i < tempSpecimens.length; i++) {
                                if (tempSpecimens[i].isDefault) {
                                    $scope.options.testDefinition.testSpecimenList[0] = tempSpecimens[i];
                                    addSpecimenData(0);
                                } else {
                                    $scope.options.testDefinition.testSpecimenList[1] = tempSpecimens[i];
                                    addSpecimenData(1);
                                }
                            }
                        }
                        var existingLength = $scope.options.testDefinition.testSpecimenList.length;
                        for (; existingLength < 2; existingLength++) {
                            $scope.addSpecimen(existingLength === 0); //first one is default
                        }
                    });

                    function addSpecimenData(i) {
                        $scope.containerTypeOptions.push({
                            className: "LkpContainerType",
                            name: "lkpContainerType",
                            labelText: "containerType",
                            valueField: "name." + util.userLocale,
                            required: true,
                            selectedValue: $scope.options.testDefinition.testSpecimenList[i].containerType,
                            data: $scope.containerTypeValues
                        });

                        $scope.temperatureOptions.push({
                            className: "LkpSpecimenTemperature",
                            name: "specimenTemperature",
                            labelText: "specimenTemperature",
                            valueField: "name." + util.userLocale,
                            required: false,
                            selectedValue: $scope.options.testDefinition.testSpecimenList[i].specimenTemperature,
                            data: $scope.temperatureValues
                        });
                        var stabilityDigit = $scope.options.testDefinition.testSpecimenList[i].stabilityDigit;
                        var stabilityUnit = $scope.options.testDefinition.testSpecimenList[i].stabilityUnit;
                        if (stabilityDigit !== null && stabilityUnit) {
                            $scope.options.testDefinition.testSpecimenList[i].stability = stabilityDigit + stabilityUnit.code.substr(0, 1);
                        }
                    }

                    $scope.addSpecimen = function (isDefault) {
                        var newSpecimen = {
                            isDefault: isDefault,
                            containerType: null,
                            specimenTemperature: null,
                            stabilityUnitValues: null,
                            markedForDeletion: !isDefault,
                            containerCount: 1
                        };
                        $scope.options.testDefinition.testSpecimenList.push(newSpecimen);
                        addSpecimenData(isDefault ? 0 : 1);
                    }

                    $scope.showPediatric = function () {
                        $scope.options.testDefinition.testSpecimenList[1].markedForDeletion = false;
                    }

                    $scope.deleteSpecimen = function () {
                        $scope.options.testDefinition.testSpecimenList[1].markedForDeletion = true;
                    };

                    $scope.stabilityChange = function (index, form) {
                        var regex = /^(\d+)([hd])$/i;
                        var specimen = $scope.options.testDefinition.testSpecimenList[index];
                        var stability = specimen.stability;
                        specimen.stabilityDigit = null;
                        specimen.stabilityUnit = null;
                        try {
                            var foundStability = stability.match(regex);
                            if (foundStability) {
                                specimen.stabilityDigit = +foundStability[1];
                                specimen.stabilityUnit = findStabilityUnit(foundStability[2]);
                            }
                        } catch (e) { }
                    };

                    function findStabilityUnit(unitCode) {
                        for (var i = 0; i < stabilityUnitValues.length; i++) {
                            if (stabilityUnitValues[i].code.startsWith(unitCode)) {
                                return stabilityUnitValues[i];
                            }
                        }
                    }

                }]
        }
    });
});
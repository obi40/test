define(['app', 'util', 'config'], function (app, util, config) {
    'use strict';
    app.directive('testInterpretationSetup', function () {
        return {
            restrict: 'E', // This means that it will be used as an element.
            replace: true,
            scope: {
                options: "=options"
                //{
                //  testDefinition: the test definition
                //}
            },
            templateUrl: "./" + config.lisDir + "/modules/shared/directives/testInterpretationSetup/test-interpretation-view.html",
            controller: ['$scope', '$mdDialog', 'labUnitService',
                function ($scope, $mdDialog, labUnitService) {

                    for (var i = 0; i < $scope.options.testDefinition.interpretations.length; i++) {
                        var interpretation = $scope.options.testDefinition.interpretations[i];
                        if (typeof interpretation.minConcentrationValue === "number") {
                            if (interpretation.minConcentrationComparator) {
                                interpretation.minValueData = interpretation.minConcentrationComparator + interpretation.minConcentrationValue;
                            } else {
                                interpretation.minValueData = interpretation.minConcentrationValue;
                            }
                        }
                        if (typeof interpretation.maxConcentrationValue === "number") {
                            if (interpretation.maxConcentrationComparator) {
                                interpretation.maxValueData = interpretation.maxConcentrationComparator + interpretation.maxConcentrationValue;
                            } else {
                                interpretation.maxValueData = interpretation.maxConcentrationValue;
                            }
                        }
                        interpretation.description = generateInterpretationDescription(interpretation);
                    }
                    $scope.options.testDefinition.interpretationList = angular.copy($scope.options.testDefinition.interpretations);

                    labUnitService.getLabUnitList()
                        .then(function (response) {
                            $scope.unitOptions = {
                                className: "LabUnit",
                                name: "unit",
                                labelText: "unit",
                                valueField: "unitOfMeasure",
                                required: true,
                                selectedValue: $scope.options.testDefinition.allergyUnit,
                                data: response.data
                            };
                        });

                    $scope.changeUnit = function (selectedUnit) {
                        $scope.options.testDefinition.allergyUnit = selectedUnit;
                    }

                    function generateInterpretationDescription(interpretation) {
                        var description = "";
                        if (interpretation.minConcentrationComparator) {
                            description += interpretation.minConcentrationComparator;
                        }
                        if (typeof interpretation.minConcentrationValue === "number") {
                            description += interpretation.minConcentrationValue;
                        }
                        if (typeof interpretation.minConcentrationValue === "number" &&
                            typeof interpretation.maxConcentrationValue === "number") {
                            description += " - ";
                        }
                        if (interpretation.maxConcentrationComparator) {
                            description += interpretation.maxConcentrationComparator;
                        }
                        if (typeof interpretation.maxConcentrationValue === "number") {
                            description += interpretation.maxConcentrationValue;
                        }
                        return description;
                    }

                    function comparePrintOrder(a, b) {
                        return a.printOrder === b.printOrder ? 0 : a.printOrder - b.printOrder;
                    }

                    $scope.openInterpretationDialog = function (interpretationListWrapper) {
                        $mdDialog.show({
                            controller: ["$scope", "$mdDialog", "interpretationListWrapper",
                                function ($scope, $mdDialog, interpretationListWrapper) {
                                    interpretationListWrapper.interpretationList.sort(comparePrintOrder);
                                    $scope.interpretations = angular.copy(interpretationListWrapper.interpretationList);

                                    var decimals = interpretationListWrapper.allergyDecimals;

                                    $scope.minValueHint = "eg. >=10, >12, 15";
                                    $scope.maxValueHint = "eg. <=25, <36, 47";
                                    $scope.minValuePattern = '^(>|>=)?[0-9]+([.](?=[0-9])[0-9]{0,' + decimals + '})?$';
                                    $scope.maxValuePattern = '^(<|<=)?[0-9]+([.](?=[0-9])[0-9]{0,' + decimals + '})?$';

                                    $scope.addInterpretation = function () {
                                        $scope.interpretations.push({
                                            printOrder: $scope.interpretations.length + 1
                                        });
                                    }

                                    $scope.deleteInterpretation = function (interpretation) {
                                        interpretation.markedForDeletion = true;
                                        interpretation.printOrder = -1;
                                    }

                                    $scope.valueMessages = { invalid: util.systemMessages.minLessThanMax };
                                    $scope.oneValueMessages = { invalid: util.systemMessages.minOrMaxConcentrationValueShouldBeFilled };

                                    $scope.interpretationValueChange = function (interpretation, form) {
                                        form.valueValidator.$setTouched();
                                        var minValueData = interpretation.minValueData;
                                        var maxValueData = interpretation.maxValueData;
                                        var minValueRegex = /^(>|>=)?((\d+)(\.?)(\d*))$/;
                                        var maxValueRegex = /^(<|<=)?((\d+)(\.?)(\d*))$/;

                                        //reset
                                        interpretation.minConcentrationComparator = null;
                                        interpretation.minConcentrationValue = null;
                                        interpretation.maxConcentrationComparator = null;
                                        interpretation.maxConcentrationValue = null;

                                        try {
                                            var foundMin = minValueData.toString().match(minValueRegex);
                                        } catch (e) { }
                                        try {
                                            var foundMax = maxValueData.toString().match(maxValueRegex);
                                        } catch (e) { }

                                        if (foundMin && foundMax) {
                                            var smallestAmount = decimals > 0 ? 1 : 0;
                                            for (var i = 0; i < decimals; i++) {
                                                smallestAmount /= 10;
                                            }
                                            var min = +foundMin[2];
                                            var max = +foundMax[2];
                                            if (foundMin[1] === '>') {
                                                min += smallestAmount;
                                            }
                                            if (foundMax[1] === '<') {
                                                max -= smallestAmount;
                                            }
                                            if (max < min) {
                                                form.valueValidator.$setValidity("invalid", false); //invalid
                                                return;
                                            } else {
                                                form.valueValidator.$setValidity("invalid", true); //valid
                                                interpretation.minConcentrationComparator = foundMin[1];
                                                interpretation.minConcentrationValue = +foundMin[2];
                                                interpretation.maxConcentrationComparator = foundMax[1];
                                                interpretation.maxConcentrationValue = +foundMax[2];
                                            }
                                        } else if (foundMin) {
                                            form.valueValidator.$setValidity("invalid", true); //valid
                                            interpretation.minConcentrationComparator = foundMin[1];
                                            interpretation.minConcentrationValue = +foundMin[2];
                                        } else if (foundMax) {
                                            form.valueValidator.$setValidity("invalid", true); //valid
                                            interpretation.maxConcentrationComparator = foundMax[1];
                                            interpretation.maxConcentrationValue = +foundMax[2];
                                        }

                                        if (interpretation.minConcentrationComparator === null &&
                                            interpretation.minConcentrationValue === null &&
                                            interpretation.maxConcentrationComparator === null &&
                                            interpretation.maxConcentrationValue === null) {
                                            form.oneValueValidator.$setValidity("invalid", false);//invalid
                                            return false;
                                        } else {
                                            form.oneValueValidator.$setValidity("invalid", true);//valid
                                            return true;
                                        }
                                    }

                                    $scope.submit = function () {
                                        var allValid = true;
                                        for (var i = 0; i < $scope.interpretations.length; i++) {
                                            var interpretation = $scope.interpretations[i];
                                            if (!interpretation.markedForDeletion) {
                                                if ($scope.interpretationValueChange(interpretation, interpretation.form)) {
                                                    interpretation.description = generateInterpretationDescription(interpretation);
                                                } else {
                                                    allValid = false;
                                                    break;
                                                }
                                            }
                                        }
                                        if (!allValid) {
                                            return;
                                        }

                                        $scope.interpretations.sort(comparePrintOrder);

                                        interpretationListWrapper.interpretationList = $scope.interpretations;
                                        $mdDialog.cancel();
                                    };

                                    $scope.cancel = function () {
                                        $mdDialog.cancel();
                                    };
                                }],
                            locals: {
                                interpretationListWrapper: interpretationListWrapper
                            },
                            templateUrl: './' + config.lisDir + '/modules/dialogs/test-interpretation.html',
                            parent: angular.element(document.body),
                            clickOutsideToClose: false,
                            fullscreen: false,
                            onComplete: function (scope, element) {
                                $(".test-interpretation-container").sortable({
                                    items: "> .interpretation",
                                    cursor: "move",
                                    handle: ".drag-handle",
                                    axis: "y",
                                    beforeStop: function (event, ui) {
                                        var printOrder = 1;
                                        $(ui.item[0]).parent().children('.interpretation').each(function (index, element) {
                                            var itemIndex = $(this).attr("data-index");
                                            if (itemIndex) {
                                                scope.interpretations[itemIndex].printOrder = printOrder++;
                                            }
                                        });
                                    }
                                });
                            }
                        });
                    }

                }]
        }
    });
});
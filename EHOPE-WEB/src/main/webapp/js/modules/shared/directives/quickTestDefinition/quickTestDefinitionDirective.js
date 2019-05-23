define(['app', 'util', 'commonData', 'config'], function (app, util, commonData, config) {
    'use strict';
    app.directive('quickTestDefinition', function () {
        return {
            restrict: 'E', // This means that it will be used as an element.
            replace: true,
            scope: {
                quickDefinitionOptions: "=quickDefinitionOptions"
                //quickDefinitionOptions.testDefinition: the testDefinition object to edit
                //quickDefinitionOptions.mode: "add"/"edit"
            },
            templateUrl: "./" + config.lisDir + "/modules/shared/directives/quickTestDefinition/quick-test-definition-view.html",
            controller: ['$scope', 'quickTestDefinitionService', 'testSelectionService', 'lovService',
                function ($scope, quickTestDefinitionService, testSelectionService, lovService) {

                    $scope.specimenType = {
                        className: "LkpSpecimenType",
                        name: "specimenType",
                        labelText: "specimenType",
                        valueField: "name." + util.userLocale,
                        required: true,
                        selectedValue: null
                    };

                    $scope.lkpReportType = {
                        className: "LkpReportType",
                        name: "lkpReportType",
                        labelText: "reportType",
                        valueField: "name." + util.userLocale,
                        selectedValue: null,
                        required: true
                    };

                    lovService.getLkpByClass({ className: "LkpReportType" }).then(function (data) {
                        $scope.lkpReportType.data = data;
                        //pre select default report
                        if ($scope.lkpReportType.selectedValue == null) {
                            for (var idx = 0; idx < data.length; idx++) {
                                if (data[idx].code === 'DEFAULT') {
                                    $scope.lkpReportType.selectedValue = data[idx];
                                    break;
                                }
                            }
                        }
                    });

                    if ($scope.quickDefinitionOptions.mode === 'add') {
                        $scope.quickDefinitionOptions.testDefinition = {
                            isActive: true,
                            isPanel: false,
                            isSeparatePage: false,
                            isSeparateSample: false,
                            isAllowRepetition: false,
                            isRepetitionSeparateSample: false,
                            isRepetitionChargeable: false,
                            interpretations: [],
                            destinations: []
                        };
                    } else {
                        $scope.specimenType.selectedValue = $scope.quickDefinitionOptions.testDefinition.specimenType;
                        $scope.lkpReportType.selectedValue = $scope.quickDefinitionOptions.testDefinition.lkpReportType;

                    }

                    testSelectionService.getSectionsWithType()
                        .then(function (response) {
                            $scope.sectionLov = {
                                className: "LabSection",
                                name: "section",
                                labelText: "section",
                                valueField: ("name." + util.userLocale),
                                selectedValue: null,
                                required: true,
                                data: response.data
                            };
                            if ($scope.quickDefinitionOptions.mode === 'edit') {
                                $scope.sectionLov.selectedValue = $scope.quickDefinitionOptions.testDefinition.section;
                            }
                        });



                    $scope.specimenSetupOptions = $scope.quickDefinitionOptions;
                    $scope.resultSetupOptions = $scope.quickDefinitionOptions;
                    $scope.interpretationSetupOptions = $scope.quickDefinitionOptions;
                    $scope.questionSetupOptions = $scope.quickDefinitionOptions;
                    $scope.disclaimerSetupOptions = $scope.quickDefinitionOptions;
                    // $scope.componentSetupOptions = $scope.quickDefinitionOptions;
                    $scope.feesSetupOptions = $scope.quickDefinitionOptions;
                    $scope.destinationSetupOptions = $scope.quickDefinitionOptions;

                    $scope.changeSection = function (section) {
                        $scope.resultSetupOptions.changeSection(section);
                    }

                    $scope.submitQuickTestDefinition = function () {
                        var testDef = angular.copy($scope.quickDefinitionOptions.testDefinition);
                        $scope.sectionLov.assignValues(testDef, [$scope.sectionLov, $scope.specimenType, $scope.lkpReportType]);
                        var tempResults = angular.copy($scope.resultSetupOptions.results);
                        for (var i = 0; i < tempResults.length; i++) {
                            tempResults[i].normalRangeList = [];
                            for (var tabKey in tempResults[i].normalRangeTabs) {
                                if (tempResults[i].normalRangeTabs.hasOwnProperty(tabKey)) {
                                    for (var k = 0; k < tempResults[i].normalRangeTabs[tabKey].normalRanges.length; k++) {
                                        delete tempResults[i].normalRangeTabs[tabKey].normalRanges[k].form;
                                        tempResults[i].normalRangeTabs[tabKey].normalRanges[k].testDestination = tempResults[i].normalRangeTabs[tabKey].testDestination;
                                    }
                                    tempResults[i].normalRangeList = tempResults[i].normalRangeList.concat(tempResults[i].normalRangeTabs[tabKey].normalRanges);
                                }
                            }
                        }
                        // var tempExtraTests = [];
                        // for (var i = 0; i < $scope.componentSetupOptions.tempComponents.length; i++) {
                        //     var tempExtraTest = {
                        //         extraTest: $scope.componentSetupOptions.tempComponents[i],
                        //         alwaysPerformed: $scope.componentSetupOptions.tempComponents[i].alwaysPerformed,
                        //         entryType: $scope.componentSetupOptions.tempComponents[i].entryType
                        //     };
                        //     tempExtraTests.push(tempExtraTest);
                        // }
                        // testDef.extraTestList = tempExtraTests;
                        testDef.testResultList = tempResults;
                        testDef.prices = angular.copy($scope.feesSetupOptions.testPrices);
                        testDef.testQuestionList = $scope.questionSetupOptions.getQuestions();
                        testDef.testDisclaimerList = $scope.disclaimerSetupOptions.getDisclaimers();

                        quickTestDefinitionService.quickTestDefinition(testDef)
                            .then(function (response) {
                                util.createToast(util.systemMessages.success, "success");
                                $scope.$emit("quickTestDefinitionSuccess");
                            });
                    };

                    $scope.isSeparatePageDisabled = false;

                    $scope.checkReportType = function () {
                        if ($scope.lkpReportType.selectedValue && $scope.lkpReportType.selectedValue.code) {
                            switch ($scope.lkpReportType.selectedValue.code) {
                                case 'STOOL':
                                case 'CBC':
                                    $scope.quickDefinitionOptions.testDefinition.isSeparatePage = false;
                                    $scope.isSeparatePageDisabled = true;
                                    break;
                                case 'ALLERGY':
                                case 'CULTURE':
                                case 'PROTEIN_ELECTRO':
                                case 'URINE':
                                case 'TRIPLE':
                                case 'NEONATAL':
                                    $scope.quickDefinitionOptions.testDefinition.isSeparatePage = true;
                                    $scope.isSeparatePageDisabled = true;
                                    break;
                                default:
                                case 'DEFAULT':
                                    $scope.isSeparatePageDisabled = false;
                                    break;
                            }

                        }
                    };

                    $scope.checkReportType();

                }]
        }
    });
});
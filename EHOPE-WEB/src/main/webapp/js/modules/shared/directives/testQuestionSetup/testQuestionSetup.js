define(['app', 'util', 'config'], function (app, util, config) {
    'use strict';
    app.directive('testQuestionSetup', function () {
        return {
            restrict: 'E',
            replace: true,
            scope: {
                options: "=options"
            },
            templateUrl: "./" + config.lisDir + "/modules/shared/directives/testQuestionSetup/test-question-setup-view.html",
            controller: ['$scope', 'testQuestionSetupService', 'commonMethods', 'lovService',
                function ($scope, testQuestionSetupService, commonMethods, lovService) {
                    $scope.patternPercent = config.regexpPercent;
                    $scope.patternNum = config.regexpNum;
                    $scope.testQuestions = [];
                    var dummyRid = -1;
                    var allQuestionTypes = [];
                    var questionTypeLovObj = null;
                    commonMethods.retrieveMetaData("TestQuestion").then(function (response) {
                        $scope.metaData = response.data;
                        lovService.getLkpByClass({ className: "LkpQuestionType" }).then(function (data) {
                            allQuestionTypes = data;//init
                            questionTypeLovObj = {//init
                                className: "LkpQuestionType",
                                name: $scope.metaData.lkpQuestionType.name,
                                labelText: "questionType",
                                valueField: "name." + util.userLocale,
                                selectedValue: null,
                                required: $scope.metaData.lkpQuestionType.notNull,
                                data: allQuestionTypes
                            };
                            //in case it is a new test
                            if ($scope.options.testDefinition.rid != null) {
                                testQuestionSetupService.getQuestions($scope.options.testDefinition.rid).then(function (response) {
                                    var data = response.data;
                                    if (data === null || data.length > 0) {
                                        for (var idx = 0; idx < data.length; idx++) {
                                            data[idx]["questionTypeLov"] = angular.copy(questionTypeLovObj);
                                            data[idx]["questionTypeLov"].selectedValue = data[idx].lkpQuestionType;
                                        }
                                        $scope.testQuestions = data;
                                    }
                                });
                            }
                        });
                    });

                    $scope.add = function () {
                        var lov = angular.copy(questionTypeLovObj);
                        lov.selectedValue = allQuestionTypes[0];
                        $scope.testQuestions.push({
                            rid: dummyRid--,
                            description: null,
                            standardCode: null,
                            lkpQuestionType: lov.selectedValue,
                            questionTypeLov: lov,
                            testDefinition: $scope.options.testDefinition,
                            markedForDeletion: false
                        });
                    };

                    $scope.delete = function (questionObj) {
                        for (var idx = 0; idx < $scope.testQuestions.length; idx++) {
                            if (questionObj.rid === $scope.testQuestions[idx].rid) {
                                $scope.testQuestions[idx].markedForDeletion = true;
                                break;
                            }
                        }
                    };

                    $scope.options["getQuestions"] = function () {
                        var result = angular.copy($scope.testQuestions);
                        for (var idx = 0; idx < result.length; idx++) {
                            if (result[idx].rid < 0) {
                                delete result[idx].rid;//remove dummy rid
                            }
                        }
                        return result;
                    };

                }]
        }
    });
});
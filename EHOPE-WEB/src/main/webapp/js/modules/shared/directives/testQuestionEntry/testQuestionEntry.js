define(['app', 'util', 'commonData', 'config'], function (app, util, commonData, config) {
    'use strict';
    app.directive('testQuestionEntry', function () {
        return {
            restrict: 'E',
            replace: false,
            scope: {},
            templateUrl: "./" + config.lisDir + "/modules/shared/directives/testQuestionEntry/test-question-entry-view.html",
            controller: ['$scope', 'testQuestionEntryService', 'WizardHandler', function ($scope, testQuestionEntryService, WizardHandler) {
                $scope.patternPercent = config.regexpPercent;
                $scope.patternNum = config.regexpNum;
                $scope.visitRid = null;
                $scope.questionList = null;
                $scope.$on(commonData.events.activateTestQuestionForm, function (event, params) {
                    $scope.visitRid = params;
                    activateDirective();
                });

                function activateDirective() {
                    $scope.activateDirective = true;
                    $scope.locale = util.userLocale;
                    $scope.questionList = {};
                    $scope.isObjectEmpty = function (obj) {
                        return util.isObjectEmpty(obj);
                    };
                    function init() {
                        $scope.questionList = {};//reset
                        testQuestionEntryService.getTestQuestionEntryData($scope.visitRid).then(function (response) {
                            var data = response.data;
                            if (data.length === 0) {
                                WizardHandler.wizard().next();
                                $scope.$emit(commonData.events.exitTestQuestionForm, {});
                                return;
                            }
                            for (var idx = 0; idx < data.length; idx++) {
                                var answer = data[idx];//can be a dummy or a previous answer
                                var questionTypeCode = answer.testQuestion.lkpQuestionType.code;
                                answer["questionKey"] = questionTypeCode + "_" + answer.testQuestion.rid;//for form validation
                                if (answer.answerDate != null) {
                                    answer["timeTxt"] = kendo.toString(new Date(answer.answerDate), "t");
                                }
                                if ($scope.questionList[answer.labTestActual.rid] == null) {
                                    $scope.questionList[answer.labTestActual.rid] = {};
                                    $scope.questionList[answer.labTestActual.rid]["answers"] = [answer];
                                    $scope.questionList[answer.labTestActual.rid]["test"] = answer.labTestActual.testDefinition;
                                } else {
                                    $scope.questionList[answer.labTestActual.rid]["answers"].push(answer);
                                }
                            }

                        });
                    }

                    init();

                    $scope.submitTestQuestion = function () {
                        if ($scope.isObjectEmpty($scope.questionList)) {
                            $scope.$emit(commonData.events.exitTestQuestionForm, {});
                            return;
                        }
                        var answers = [];
                        for (var key in $scope.questionList) {
                            var obj = $scope.questionList[key];
                            for (var i = 0; i < obj.answers.length; i++) {
                                var answer = obj.answers[i];
                                var questionTypeCode = answer.testQuestion.lkpQuestionType.code;
                                //update dates
                                if (questionTypeCode === "DATE") {
                                    var d = new Date(answer.answerDate);
                                    d.setHours(0, 0, 0);
                                    answer.answerDate = d;
                                } else if (questionTypeCode === "DATE_TIME") {
                                    var d = new Date(answer.answerDate);
                                    util.setKendoTimeInDate(d, answer.timeTxt);
                                    answer.answerDate = d;
                                }
                                answers.push(answer);
                            }
                        }
                        testQuestionEntryService.answerQuestions(answers).then(function () {
                            init();
                            $scope.$emit(commonData.events.exitTestQuestionForm, {});
                        });

                    }
                }
            }]
        }
    });
});
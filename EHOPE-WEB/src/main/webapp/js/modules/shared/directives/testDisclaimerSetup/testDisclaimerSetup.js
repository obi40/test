define(['app', 'util', 'config'], function (app, util, config) {
    'use strict';
    app.directive('testDisclaimerSetup', function () {
        return {
            restrict: 'E',
            replace: true,
            scope: {
                options: "=options"
            },
            templateUrl: "./" + config.lisDir + "/modules/shared/directives/testDisclaimerSetup/test-disclaimer-setup-view.html",
            controller: ['$scope', 'testDisclaimerSetupService', 'commonMethods',
                function ($scope, testDisclaimerSetupService, commonMethods) {
                    $scope.testDisclaimers = [];
                    var dummyRid = -1;

                    commonMethods.retrieveMetaData("TestDisclaimer").then(function (response) {
                        $scope.metaData = response.data;

                        if ($scope.options.testDefinition.rid != null) {
                            testDisclaimerSetupService.getDisclaimers($scope.options.testDefinition.rid).then(function (response) {
                                var data = response.data;
                                if (data != null || data.length > 0) {
                                    $scope.testDisclaimers = data;
                                }
                            });
                        }

                    });


                    $scope.add = function () {
                        $scope.testDisclaimers.push({
                            rid: dummyRid--,
                            title: null,
                            description: null,
                            testDefinition: $scope.options.testDefinition,
                            markedForDeletion: false
                        });
                    };

                    $scope.delete = function (disclaimerObj) {
                        for (var idx = 0; idx < $scope.testDisclaimers.length; idx++) {
                            if (disclaimerObj.rid === $scope.testDisclaimers[idx].rid) {
                                $scope.testDisclaimers[idx].markedForDeletion = true;
                                break;
                            }
                        }
                    };

                    $scope.options["getDisclaimers"] = function () {
                        var result = angular.copy($scope.testDisclaimers);
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
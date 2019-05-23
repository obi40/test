define(['app', 'util', 'config'], function (app, util, config) {
    'use strict';
    app.directive('branchForm', function () {
        return {
            restrict: 'E',
            replace: false,
            scope: {
                branch: "=branch",
                options: "=options"
            },
            templateUrl: "./" + config.lisDir + "/modules/shared/directives/branchForm/branch-form-view.html",
            controller: ['$scope', 'commonMethods',
                function ($scope, commonMethods) {
                    $scope.branchMetaData = null;
                    $scope.branchTransfields = null;
                    $scope.patternNum = config.regexpNum;
                    function prepareDir(metaData) {
                        $scope.countryCityOptions = {
                            country: {
                                name: metaData.country.name,
                                required: metaData.country.notNull,
                                onChange: onCountryChange
                            },
                            city: {
                                name: metaData.city.name,
                                required: metaData.city.notNull
                            }
                        };
                        $scope.branchTransfields = {
                            name: util.getTransFieldLanguages("name", "name", null, $scope.branchMetaData.name.notNull),
                            address: util.getTransFieldLanguages("address", "address", null, $scope.branchMetaData.address.notNull)
                        };
                    }
                    $scope.options["clear"] = function () {
                        $scope.branch = null;
                        $scope.branchForm.$setPristine();
                        $scope.branchForm.$setUntouched();
                    };
                    $scope.onMobilePatternChange = function () {
                        $scope.dummyMobileNumber = null;
                    };
                    function onCountryChange(selectedCountry) {
                        if (selectedCountry == null || $scope.branch == null) {
                            return;
                        }
                        if ($scope.branch.mobilePattern == null || $scope.branch.mobilePattern == "") {
                            //appending a hash so the ui-mask can work otherwise it wont recognize the phoneCode alone as a regex
                            $scope.branch.mobilePattern = selectedCountry.phoneCode + "#";
                        }
                    }
                    commonMethods.retrieveMetaData("LabBranch").then(function (response) {
                        $scope.branchMetaData = response.data;
                        prepareDir($scope.branchMetaData);
                    });

                    //form validation, watching the valid since branchForm does not changes for some reason
                    $scope.$watch("branchForm.$valid", function (newVal, oldVal) {
                        if (newVal != null) {
                            $scope.options.form = $scope.branchForm;
                        }
                    });

                }]
        }
    });
});




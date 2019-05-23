define(['app', 'util'], function (app, util) {
    'use strict';

    app.directive('passwordVerify', function () {
        return {
            restrict: 'A',
            require: '?ngModel', // get a hold of NgModelController
            link: function (scope, elem, attrs, ngModel) {
                if (!ngModel){
                    throw "ng-model is required!";
                }

                // watch own value and re-validate on change
                scope.$watch(attrs.ngModel, function () {
                    validate();
                });

                // observe the other value and re-validate on change
                attrs.$observe('passwordVerify', function (val) {
                    validate();
                });

                var validate = function () {
                    // values
                    var val1 = ngModel.$viewValue;
                    var val2 = attrs.passwordVerify;
                    
                    // To fix that if the inputs are not required, set their ng-models as nulls in the controller
                    if (val1 === null) {
                        ngModel.$setValidity('passwordMatch', true);
                        return;
                    }
                    // set validity
                    ngModel.$setValidity('passwordMatch', val1 === val2);
                };
            }/*,

                $scope.cssStyle = {
                    'background-color': '#b40000',
                    'border-radius': '5px',
                    'color': 'rgba(255,255,255,0.87)',
                    'font-weight': 'bold',
                    'padding': '2px',
                    'font-variant': 'all-small-caps',
                    'text-shadow': '0 0 2px #000'
                };
                var range = "{6,20}";
                var validRegex = new RegExp("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[\!\@\#\$\%\^\&\*\.\-\_])." + range + "$");
                var mediumRegex = new RegExp("^(?=.*[A-Z].*[A-Z])|(?=.*\\d.*\\d)|(?=.*[\!\@\#\$\%\^\&\*\.\-\_].*[\!\@\#\$\%\^\&\*\.\-\_])." + range + "$");
                var hardRegex = new RegExp("^(?=.*[A-Z].*[A-Z])(?=.*\\d.*\\d)(?=.*[\!\@\#\$\%\^\&\*\.\-\_].*[\!\@\#\$\%\^\&\*\.\-\_]).{14,20}$");

                $scope.$watch("password", function () {

                    var password = $scope.password;
                    if (password === undefined) {
                        $scope.cssStyle["background-color"] = "#b40000";
                        //$scope.level = $filter('translate')('simple');
                    }
                    if (validRegex.test(password)) {
                        $scope.valid = true;
                        if (hardRegex.test(password)) {
                            $scope.cssStyle["background-color"] = "#00b400";
                            //$scope.level = $filter('translate')('complicated');
                        } else if (mediumRegex.test(password)) {
                            $scope.cssStyle["background-color"] = "#ffc700";
                            //$scope.level = $filter('translate')('complex');
                        } else {
                            $scope.cssStyle["background-color"] = "#b40000";
                            //$scope.level = $filter('translate')('simple');
                        }
                    } else {
                        $scope.valid = false;
                    }

                });


            }]*/
        }
    });
});
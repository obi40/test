define(['app', 'config'], function (app, config) {
    'use strict';
    app.directive('circularMenu', function () {
        return {
            restrict: 'E', // This means that it will be used as an element.
            replace: true,
            templateUrl: "./" + config.lisDir + "/modules/shared/directives/circularMenu/circular-menu-view.html",
            scope: {
                icons: '=icons'
                //[{label: '', iconClass: '', authorityChecker: '', disable: '', action: '', confirmClick: boolean}]
            },
            controller: ['$scope', function ($scope) {
                $scope.isOpen = false;
                if ($scope.icons.length <= 6) {
                    $scope.quantityClass = "qn-6";
                } else if ($scope.icons.length <= 8) {
                    $scope.quantityClass = "qn-8";
                } else {
                    $scope.quantityClass = "qn-10";
                }
                $scope.clickAction = function (clickAction) {
                    return $scope.$eval(clickAction);
                }
            }]
        }
    });
});
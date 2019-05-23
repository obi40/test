define(['app', 'util'], function (app, util) {
    'use strict';
    app.controller('landingPageCtrl', [
        '$scope', '$state', 'commonMethods',
        function ($scope, $state, commonMethods) {
            $scope.navMenuItems = commonMethods.getNavMenuItems();
            $scope.itemsToView = $scope.navMenuItems;
            $scope.showBackButton = false;

            $scope.navClick = function (item) {
                if (item.subItems) {
                    $scope.itemsToView = item.subItems;
                    $scope.showBackButton = true;
                } else if (item.route) {
                    $state.go(item.route);
                }
            }

            $scope.back = function () {
                $scope.itemsToView = $scope.navMenuItems;
                $scope.showBackButton = false;
            }

        }
    ]);
});

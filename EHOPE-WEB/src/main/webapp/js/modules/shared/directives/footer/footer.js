define(['app', 'util', 'config'], function (app, util, config) {
    'use strict';

    app.directive('lisFooter', function () {
        return {
            restrict: 'E', //This menas that it will be used as an element.
            replace: true,
            templateUrl: "./" + config.lisDir + "/modules/shared/directives/footer/footer.html",
            controller: ['$scope', function ($scope) {
                $scope.versionNo = config.versionNo;
            }]
        }
    });
});
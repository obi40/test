define(['app', 'config'], function (app, config) {
    'use strict';
    app.directive('inputMessages', function () {
        return {
            restrict: 'E',
            replace: true,
            scope: {
                inputErrors: "=inputErrors",
                messages: "=?messages"
            },
            templateUrl: "./" + config.lisDir + "/modules/shared/directives/inputMessages/inputMessages.html",
            controller: ['$scope', function ($scope) {
            }]
        }
    });
});
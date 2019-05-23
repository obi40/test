define(['app', 'util', 'commonData'], function (app, util, commonData) {
    'use strict';
    app.controller('testsCtrl', ['$scope',
        function ($scope) {
            util.waitForDirective("testSelectionReady", commonData.events.activateTestSelection, $scope, null);
        }
    ]);
});
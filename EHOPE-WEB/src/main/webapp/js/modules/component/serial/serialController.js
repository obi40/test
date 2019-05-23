define(['app', 'util'], function (app, util) {
    'use strict';
    app.controller('serialCtrl', [
        '$scope',
        'serialFormService',
        function (
            $scope,
            serialFormService
        ) {
            $scope.serials = [];
            $scope.serialsFormOptions = {
                serials: $scope.serials,
                singleSubmit: true,
                submit: function (serial) {
                    if ($scope.serialsFormOptions.isInvalid()) {
                        return;
                    }
                    serialFormService.updateSerials([serial]).then(function () {
                        util.createToast(util.systemMessages.success, "success");
                        $scope.serialsFormOptions.prepareSerials();
                    });
                }
            };
        }
    ]);
});

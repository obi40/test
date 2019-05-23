define(['app', 'util'], function (app, util) {
    'use strict';
    app.controller('smsCtrl', [
        '$scope',
        '$location',
        '$state',
        'smsService',
        'commonMethods',
        function (
            $scope,
            $location,
            $state,
            smsService,
            commonMethods
        ) {
            util.fullWebsiteView($scope);
            util.clearUtilData();
            var token = $location.search().t;//token in url
            var serviceUrl = null;
            var parameters = null;
            $scope.filePath = null;
            $scope.openFile = function () {
                util.$window.location.assign($scope.filePath);
            };
            if (token) {
                commonMethods.getCustomTokenData(token).then(function (response) {
                    serviceUrl = response.data.serviceUrl;
                    parameters = response.data.parameters;
                    var options = { headers: { "Authorization": token } };
                    if (serviceUrl == "generateVisitResults") {//because it is a multiple reports
                        options["responseType"] = "blob";
                    }
                    util.createApiRequest(serviceUrl + ".srvc", JSON.stringify(parameters), options).then(function (response) {
                        var blob = new Blob([response.data], { type: "application/pdf" });
                        $scope.filePath = util.$window.URL.createObjectURL(blob);
                    }).catch(function () {
                        util.createToast(util.systemMessages.somethingWrong, "error");
                        $state.go('login');
                    });
                }).catch(function () {
                    util.createToast(util.systemMessages.invalidToken, "error");
                    $state.go('login');
                });
            } else {
                $state.go('login');
            }
        }
    ]);
});

define(['app', 'config', 'util'], function (app, config, util) {
    'use strict';
    app.service('requestFormManagementService', ["$http", function ($http) {
        this.getRequestForms = function (data) {
            return util.createApiRequest("getRequestForms.srvc", JSON.stringify(data));
        };

        this.addRequestForm = function (e) {
            return $http({
                data: JSON.stringify(e),
                method: "POST",
                url: config.server + config.api_path + "addRequestForm.srvc"
            });
        };

        this.editRequestForm = function (e) {
            return $http({
                data: JSON.stringify(e),
                method: "POST",
                url: config.server + config.api_path + "editRequestForm.srvc"
            });
        };

        this.activateRequestForm = function (rid) {
            return $http({
                data: JSON.stringify(rid),
                method: "POST",
                url: config.server + config.api_path + "activateRequestForm.srvc"
            });
        };

        this.deactivateRequestForm = function (rid) {
            return $http({
                data: JSON.stringify(rid),
                method: "POST",
                url: config.server + config.api_path + "deactivateRequestForm.srvc"
            });
        };

        this.getRequestFormTests = function (rid) {
            return $http({
                data: JSON.stringify(rid),
                method: "POST",
                url: config.server + config.api_path + "getRequestFormTests.srvc"
            });
        };

        this.getRequestFormTestsWithDestinations = function (data) {
            return util.createApiRequest("getRequestFormTestsWithDestinations.srvc", JSON.stringify(data));
        };

        this.saveRequestFormTests = function (requestForm) {
            return $http({
                data: JSON.stringify(requestForm),
                method: "POST",
                url: config.server + config.api_path + "saveRequestFormTests.srvc"
            });
        };
    }]);
});

define(['app', 'util'], function (app, util) {
    'use strict';
    app.service('insuranceNetworkService', function () {

        this.getInsNetworkList = function () {
            return util.createApiRequest("getInsNetworkList.srvc");
        };

        this.createInsNetwork = function (data) {
            return util.createApiRequest("createInsNetwork.srvc", JSON.stringify(data));
        };

        this.updateInsNetwork = function (data) {
            return util.createApiRequest("updateInsNetwork.srvc", JSON.stringify(data));
        };

        this.deleteInsNetwork = function (data) {
            return util.createApiRequest("deleteInsNetwork.srvc", JSON.stringify(data));
        };

    });
});

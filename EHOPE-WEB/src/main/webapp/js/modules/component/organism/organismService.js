define(['app', 'util'], function (app, util) {
    'use strict';
    app.service('organismService', function () {
        this.getOrganismList = function (data) {
            return util.createApiRequest("getOrganismList.srvc");
        };

        this.getOrganismPage = function (data) {
            return util.createApiRequest("getOrganismPage.srvc", JSON.stringify(data));
        };

        this.addOrganism = function (data) {
            return util.createApiRequest("createOrganism.srvc", JSON.stringify(data));
        };

        this.updateOrganism = function (data) {
            return util.createApiRequest("updateOrganism.srvc", JSON.stringify(data));
        };

        this.deleteOrganism = function (data) {
            return util.createApiRequest("deleteOrganism.srvc", JSON.stringify(data));
        };

        this.importOrganisms = function (file) {
            var data = new FormData();
            data.append("organisms", file);
            return util.createApiRequest("importOrganisms.srvc", data, {
                transformRequest: angular.identity,
                headers: {
                    'Content-Type': undefined
                }
            });
        };
    });
});

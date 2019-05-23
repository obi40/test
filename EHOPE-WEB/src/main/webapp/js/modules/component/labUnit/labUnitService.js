define(['app', 'util'], function (app, util) {
    'use strict';
    app.service('labUnitService', function () {

        this.getLabUnitPage = function (data) {
            return util.createApiRequest("getLabUnitPage.srvc", JSON.stringify(data));
        };

        this.getLabUnitList = function () {
            return util.createApiRequest("getLabUnitList.srvc");
        };

        this.createLabUnit = function (data) {
            return util.createApiRequest("createLabUnit.srvc", JSON.stringify(data));
        };

        this.updateLabUnit = function (data) {
            return util.createApiRequest("updateLabUnit.srvc", JSON.stringify(data));
        };

        this.deleteLabUnit = function (data) {
            return util.createApiRequest("deleteLabUnit.srvc", JSON.stringify(data));
        };
    });
});

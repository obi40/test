define(['app', 'util'], function (app, util) {
    'use strict';
    app.service('serialFormService', function () {
        this.getSerialsData = function (data) {
            return util.createApiRequest("getSerialsData.srvc", JSON.stringify(data));
        };
        this.createSerials = function (data) {
            return util.createApiRequest("createSerials.srvc", JSON.stringify(data));
        };
        this.updateSerials = function (data) {
            return util.createApiRequest("updateSerials.srvc", JSON.stringify(data));
        };
    });
});
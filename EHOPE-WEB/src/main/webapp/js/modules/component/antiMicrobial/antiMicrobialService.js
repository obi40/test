define(['app', 'util'], function (app, util) {
    'use strict';
    app.service('antiMicrobialService', function () {

        this.getAntiMicrobialList = function () {
            return util.createApiRequest("getAntiMicrobialList.srvc");
        };

        this.getAntiMicrobialPage = function (data) {
            return util.createApiRequest("getAntiMicrobialPage.srvc", JSON.stringify(data));
        };

        this.addAntiMicrobial = function (data) {
            return util.createApiRequest("createAntiMicrobial.srvc", JSON.stringify(data));
        };

        this.updateAntiMicrobial = function (data) {
            return util.createApiRequest("updateAntiMicrobial.srvc", JSON.stringify(data));
        };

        this.deleteAntiMicrobial = function (data) {
            return util.createApiRequest("deleteAntiMicrobial.srvc", JSON.stringify(data));
        };
    });
});

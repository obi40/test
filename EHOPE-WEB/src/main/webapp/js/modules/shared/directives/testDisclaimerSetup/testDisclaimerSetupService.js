define(['app', 'util'], function (app, util) {
    'use strict';
    app.service('testDisclaimerSetupService', function () {

        this.getDisclaimers = function (data) {
            return util.createApiRequest("getDisclaimers.srvc", JSON.stringify(data));
        };

    });
});
define(['app', 'util'], function (app, util) {
    'use strict';
    app.service('dashboardService', function () {
        this.getTotalVisits = function () {
            return util.createApiRequest("getTotalVisits.srvc");
        };
        this.getTotalActivePatients = function () {
            return util.createApiRequest("getTotalActivePatients.srvc");
        };
        this.getTotalNewPatients = function () {
            return util.createApiRequest("getTotalNewPatients.srvc");
        };
    });
});

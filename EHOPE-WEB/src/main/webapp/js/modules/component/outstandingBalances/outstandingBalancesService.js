define(['app', 'util'], function (app, util) {
    'use strict';
    app.service('outstandingBalancesService', function () {

        this.getOutstandingBalanceVisits = function (data) {
            return util.createApiRequest("getOutstandingBalanceVisits.srvc", JSON.stringify(data));
        };

        this.generateOutstandingBalancesReport = function (data) {
            return util.createApiRequest("generateOutstandingBalancesReport.srvc", JSON.stringify(data), { responseType: "blob" });
        };

        this.generatePatientOutstandingBalancesReport = function (data) {
            return util.createApiRequest("generatePatientOutstandingBalancesReport.srvc", JSON.stringify(data), { responseType: "blob" });
        };

    });
});

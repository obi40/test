define(['app', 'util'], function (app, util) {
    'use strict';
    app.service('dailyReportsService', function () {
        this.generateDailyCashPaymentsReport = function (data) {
            return util.createApiRequest("generateDailyCashPaymentsReport.srvc", JSON.stringify(data), { responseType: "blob" });
        };

        this.generateDailyIncomeReport = function (data) {
            return util.createApiRequest("generateDailyIncomeReport.srvc", JSON.stringify(data), { responseType: "blob" });
        };

        this.generateClaimReport = function (data) {
            return util.createApiRequest("generateClaimReport.srvc", JSON.stringify(data), { responseType: "blob" });
        };

        this.generateDailyCreditPaymentReport = function (data) {
            return util.createApiRequest("generateDailyCreditPaymentReport.srvc", JSON.stringify(data), { responseType: "blob" });
        };

        this.generateReferralOutReport = function (data) {
            return util.createApiRequest("generateReferralOutReport.srvc", JSON.stringify(data), { responseType: "blob" });
        };
    });
});

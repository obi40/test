define(['app', 'util'], function (app, util) {
    'use strict';
    app.service('orderManagementService', function () {
        this.REQUESTED = "REQUESTED";
        this.VALIDATED = "VALIDATED";
        this.COLLECTED = "COLLECTED";
        this.IN_PROGRESS = "IN_PROGRESS";
        this.RESULTS_ENTERED = "RESULTS_ENTERED";
        this.FINALIZED = "FINALIZED";
        this.CANCELLED = "CANCELLED";
        this.CLOSED = "CLOSED";
        this.ABORTED = "ABORTED";

        this.getVisitOrderManagementData = function (data) {
            return util.createApiRequest("getVisitOrderManagementData.srvc", JSON.stringify(data));
        };
        this.getSamplesByVisitOrderManagement = function (data) {
            return util.createApiRequest("getSamplesByVisitOrderManagement.srvc", JSON.stringify(data));
        };
        this.getTestsBySampleOrderManagement = function (data) {
            return util.createApiRequest("getTestsBySampleOrderManagement.srvc", JSON.stringify(data));
        };
        this.getTestOrderManagementData = function (data) {
            return util.createApiRequest("getTestOrderManagementData.srvc", JSON.stringify(data));
        };
        this.getVisitSampleTestHistory = function (data) {
            return util.createApiRequest("getVisitSampleTestHistory.srvc", JSON.stringify(data));
        };
        this.changeVisitSampleTestStatus = function (data) {
            return util.createApiRequest("changeVisitSampleTestStatus.srvc", JSON.stringify(data));
        };
        this.changeSampleTestListStatus = function (data) {
            return util.createApiRequest("changeSampleTestListStatus.srvc", JSON.stringify(data));
        };
    });
});

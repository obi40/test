define(['app', 'util'], function (app, util) {
    'use strict';
    app.service('orderFormService', function () {
        this.createVisit = function (data) {
            return util.createApiRequest("createVisit.srvc", JSON.stringify(data));
        };
        this.updateVisitWizard = function (data) {
            return util.createApiRequest("updateVisitWizard.srvc", JSON.stringify(data));
        };
        this.updateVisitPayment = function (data) {
            return util.createApiRequest("updateVisitPayment.srvc", JSON.stringify(data));
        };
        this.updateVisitAppointment = function (data) {
            return util.createApiRequest("updateVisitAppointment.srvc", JSON.stringify(data));
        };
        this.fetchVisit = function (data) {
            return util.createApiRequest("fetchVisit.srvc", JSON.stringify(data));
        };
    });
});
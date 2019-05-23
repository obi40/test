define(['app', 'util', 'commonData'], function (app, util, commonData) {
    'use strict';
    app.service('sampleSeparationService', function () {
        this.getSamplePage = function (data) {
            return util.createApiRequest("getSamplePage.srvc", JSON.stringify(data));
        };
        this.validateSample = function (data) {
            return util.createApiRequest("validateSample.srvc", JSON.stringify(data));
        };
        this.setSamples = function (data) {
            return util.createApiRequest("setSamples.srvc", JSON.stringify(data));
        };
        this.deleteSample = function (data) {
            return util.createApiRequest("deleteSample.srvc", JSON.stringify(data));
        };
        this.sendToMachine = function (visitRid, samplesRid) {
            var wrapper = {
                visitRid: visitRid,
                samplesTests: samplesRid
            };
            return util.createApiRequest("sendToMachine.srvc", JSON.stringify(wrapper));
        };
        this.getVisitSampleSeparation = function (data) {
            return util.createApiRequest("getVisitSampleSeparation.srvc", JSON.stringify(data));
        };

        this.printSample = function (data) {
            return util.createApiRequest("printSample.srvc", JSON.stringify(data), { responseType: "blob" });
        };
        this.printAllSamples = function (data) {
            return util.createApiRequest("printAllSamples.srvc", JSON.stringify(data), { responseType: "blob" });
        };
        this.printSampleWorksheet = function (data) {
            return util.createApiRequest("printSampleWorksheet.srvc", JSON.stringify(data), { responseType: "blob" });
        };
        this.printAllWorksheets = function (data) {
            return util.createApiRequest("printAllSampleWorksheets.srvc", JSON.stringify(data), { responseType: "blob" });
        };

        this.generateAppointmentCard = function (data) {
            return util.createApiRequest("generateAppointmentCard.srvc", JSON.stringify(data), { responseType: "blob" });
        };


    });
});

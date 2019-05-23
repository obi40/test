define(['app', 'util'], function (app, util) {
    'use strict';
    app.service('importDataService', function () {
        var uploadOptions = {
            transformRequest: angular.identity,
            headers: {
                'Content-Type': undefined
            },
            responseType: "blob"
        };
        var blobType = {
            responseType: "blob"
        };
        this.downloadPatientsTemplate = function () {
            return util.createApiRequest("downloadPatientsTemplate.srvc", null, blobType);
        };
        this.uploadPatientsTemplate = function (file) {
            var data = new FormData();
            data.append("file", file);
            return util.createApiRequest("uploadPatientsTemplate.srvc", data, uploadOptions);
        };

        this.downloadDoctorsTemplate = function () {
            return util.createApiRequest("downloadDoctorsTemplate.srvc", null, blobType);
        };
        this.uploadDoctorsTemplate = function (file) {
            var data = new FormData();
            data.append("file", file);
            return util.createApiRequest("uploadDoctorsTemplate.srvc", data, uploadOptions);
        };

        this.downloadTestDefinitionsTemplate = function () {
            return util.createApiRequest("downloadTestDefinitionsTemplate.srvc", null, blobType);
        };
        this.uploadTestDefinitionsTemplate = function (file) {
            var data = new FormData();
            data.append("file", file);
            return util.createApiRequest("uploadTestDefinitionsTemplate.srvc", data, uploadOptions);
        };

        this.downloadTestResultsTemplate = function () {
            return util.createApiRequest("downloadTestResultsTemplate.srvc", null, blobType);
        };
        this.downloadTestQuestionsTemplate = function () {
            return util.createApiRequest("downloadTestQuestionsTemplate.srvc", null, blobType);
        };
        this.downloadTestPricingTemplate = function () {
            return util.createApiRequest("downloadTestPricingTemplate.srvc", null, blobType);
        };

        this.downloadHistoricalOrdersTemplate = function () {
            return util.createApiRequest("downloadHistoricalOrdersTemplate.srvc", null, blobType);
        };
        this.uploadHistoricalOrdersTemplate = function (file) {
            var data = new FormData();
            data.append("file", file);
            return util.createApiRequest("uploadHistoricalOrdersTemplate.srvc", data, uploadOptions);
        };
    });
});

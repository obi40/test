define(['app', 'config'], function (app, config) {
    'use strict';
    app.service('patientFormService', ["$http", function ($http) {
        //TODO: USE util.createApiRequest(...)
        this.addPatient = function (image, artifacts, patientData, fingerprintBase64) {
            var payLoad = new FormData();
            if (image) {
                payLoad.append("image", image);
            }
            if (artifacts && artifacts.length) {
                for (var i = 0; i < artifacts.length; i++) {
                    payLoad.append("artifacts", artifacts[i].rawFile);
                }
            }
            if (fingerprintBase64) {
                payLoad.append("fingerprint", fingerprintBase64);
            }
            payLoad.append("patient", JSON.stringify(patientData));
            return $http({
                method: "POST",
                url: config.server + config.api_path + "addPatient.srvc",
                transformRequest: angular.identity,
                headers: {
                    'Content-Type': undefined
                },
                data: payLoad
            });
        };

        this.getSimilarPatientList = function (patient) {
            return $http({
                method: "POST",
                url: config.server + config.api_path + "getSimilarPatientList.srvc",
                data: JSON.stringify(patient)
            });
        };

        this.editPatient = function (imageWrapper, artifactsToUpload, artifactIdsToDelete, patientData, fingerprintBase64) {
            var payLoad = new FormData();
            var image = imageWrapper.fileModel;
            var deleteImage = !image && !imageWrapper.oldFile;
            if (image && typeof image === "object") {
                payLoad.append("image", image);
            }
            if (artifactsToUpload && artifactsToUpload.length) {
                for (var i = 0; i < artifactsToUpload.length; i++) {
                    payLoad.append("artifacts", artifactsToUpload[i].rawFile);
                }
            }
            payLoad.append("artifactsToDelete", artifactIdsToDelete);
            payLoad.append("deleteImage", deleteImage);
            if (fingerprintBase64) {
                payLoad.append("fingerprint", fingerprintBase64);
            }
            payLoad.append("patient", JSON.stringify(patientData));
            return $http({
                method: "POST",
                url: config.server + config.api_path + "editPatient.srvc",
                transformRequest: angular.identity,
                headers: {
                    'Content-Type': undefined
                },
                data: payLoad
            });
        }
    }]);
});

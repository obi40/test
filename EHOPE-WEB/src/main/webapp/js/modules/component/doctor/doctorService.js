define(['app', 'util'], function (app, util) {
    'use strict';
    app.service('doctorService', function () {
        this.getDoctorPage = function (data) {
            return util.createApiRequest("getDoctorPage.srvc", JSON.stringify(data));
        };
        this.getAllDoctors = function () {
            return util.createApiRequest("getAllDoctors.srvc");
        };
        this.addDoctor = function (data) {
            return util.createApiRequest("addDoctor.srvc", JSON.stringify(data));
        };
        this.editDoctor = function (data) {
            return util.createApiRequest("editDoctor.srvc", JSON.stringify(data));
        };
        this.deleteDoctor = function (data) {
            return util.createApiRequest("deleteDoctor.srvc", data);
        };
    });
});

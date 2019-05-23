define(['app', 'util'], function (app, util) {
    'use strict';
    app.service('patientInsuranceFormService', function () {
        this.addPatientInsurance = function (data) {
            return util.createApiRequest("addPatientInsurance.srvc", JSON.stringify(data));
        };

        this.editPatientInsurance = function (data) {
            return util.createApiRequest("editPatientInsurance.srvc", JSON.stringify(data));
        };

    });
});
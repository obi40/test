define(['app', 'util', 'commonData'], function (app, util, commonData) {
    'use strict';
    app.controller('patientRegistrationCtrl', [
        '$scope',
        function ($scope) {
            $scope.newPatient = {
                artifacts: [],
                isActive: true,
                isBloodSamplePhobia: false,
                isAllergies: false,
                isDiabetes: false,
                isDifficultyFindingVeins: false,
                isHypertension: false,
                isPregnancy: false,
                inInsurer: false,
                isHeartDisease: false,
                isHardTemperedPatient: false,
                isBlackListed: false,
                isVip: false,
                isVerified: false,
                isSmsNotification: false,
                isWhatsappNotification: false,
                isEmailNotification: false,
                firstName: {},
                secondName: {},
                thirdName: {},
                lastName: {}
            };
            $scope.patientFormOptions = {
                formMode: "add"
            }
            util.waitForDirective("patientFormReady", commonData.events.activatePatientForm, $scope, {});
        }
    ]);
});
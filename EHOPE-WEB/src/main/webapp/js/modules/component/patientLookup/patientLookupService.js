define(['app', 'config', 'util'], function (app, config, util) {
    'use strict';
    app.service('patientLookupService', ["$mdDialog", function ($mdDialog) {
        this.getPatientLookupPage = function (data) {
            return util.createApiRequest("getPatientLookupPage.srvc", JSON.stringify(data));
        };
        this.findPatientRidByFingerprint = function (data) {
            return util.createApiRequest("findPatientRidByFingerprint.srvc", JSON.stringify(data));
        };
        this.getPatientPage = function (data) {
            return util.createApiRequest("getPatientPage.srvc", JSON.stringify(data));
        };
        this.activatePatient = function (rid) {
            return util.createApiRequest("activatePatient.srvc", rid);
        };
        this.deactivatePatient = function (rid) {
            return util.createApiRequest("deactivatePatient.srvc", rid);
        };
        this.mergePatientsDialog = function (fromPatientRid, sucessCallback) {
            $mdDialog.show({
                controller: ["$scope", "$mdDialog", "patientLookupService", function ($scope, $mdDialog, patientLookupService) {
                    $scope.userLocale = util.userLocale;
                    $scope.to = null;
                    $scope.toPatientInfoOptions = {
                        patientRid: null
                    };
                    $scope.fromPatientInfoOptions = {
                        patientRid: fromPatientRid
                    };
                    $scope.patientSearchOptions = {
                        service: patientLookupService.getPatientLookupPage,
                        callback: function (filters) {
                            if (filters == null || filters.length == 0 || filters.length >= 2) {
                                $scope.toPatientInfoOptions.patientRid = null;
                                $scope.to = null;//reset
                                return;
                            }
                            $scope.to = filters[0].value;
                            $scope.toPatientInfoOptions.patientRid = $scope.to;
                            //if we have this function then this is not the first time,
                            //if it is then the directive will fetch automatically
                            if ($scope.toPatientInfoOptions.refresh) {
                                $scope.toPatientInfoOptions.refresh();
                            }
                        },
                        skeleton: {
                            code: "fullName",
                            description: "fullName",
                            image: "image"
                        },
                        dynamicLang: { code: true, description: true },
                        filterList: ["firstName", "secondName", "thirdName", "lastName", "fullName", "nationalId", "mobileNo", "secondaryMobileNo", "fileNo"],
                        staticFilters:
                            [
                                { field: "isActive", value: true, operator: "eq" },
                                { field: "mergedToPatientInfo", value: null, operator: "isnull" },
                                { field: "rid", value: fromPatientRid, operator: "neq" },
                            ]
                    };

                    $scope.merge = function () {
                        var wrapper = {
                            "from": fromPatientRid,
                            "to": $scope.to
                        };
                        return util.createApiRequest("mergePatients.srvc", JSON.stringify(wrapper)).then(function () {
                            util.createToast(util.systemMessages.success, "success");
                            $mdDialog.cancel();
                            sucessCallback($scope.patientSearchOptions.selectedItem)
                        });
                    };
                    $scope.cancel = function () {
                        $mdDialog.cancel();
                    };
                }],
                templateUrl: './' + config.lisDir + '/modules/dialogs/merge-patient.html',
                parent: angular.element(document.body),
                clickOutsideToClose: true
            }).then(function () { }, function () { });
        };
    }]);
});
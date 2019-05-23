define(['app', 'util', 'commonData', 'config'], function (app, util, commonData, config) {
    'use strict';
    app.directive('patientInsuranceForm', function () {
        return {
            restrict: 'E', // This means that it will be used as an element.
            replace: false,
            scope: {
                selectedInsurance: "=selectedInsurance",
                formMode: "=formMode", // add, edit
                wiz: "=wiz" //named wiz to avoid conflict with the "wizard" directive [optional]
            },
            templateUrl: "./" + config.lisDir + "/modules/shared/directives/patientInsuranceForm/patient-insurance-form-view.html",
            controller: ['$scope', 'commonMethods', 'patientInsuranceFormService', 'clientManagementService',
                function ($scope, commonMethods, patientInsuranceFormService, clientManagementService) {
                    $scope.callPlanListener = false;
                    $scope.patternPercent = config.regexpPercent;
                    $scope.patternNum = config.regexpNum;
                    $scope.locale = util.userLocale;
                    $scope.providerLkp = null;
                    $scope.providerPlanLkp = null;
                    $scope.insuranceDependencyType = null;

                    commonMethods.retrieveMetaData("EmrPatientInsuranceInfo")
                        .then(function (response) {
                            $scope.insuranceMetaData = response.data;
                            $scope.insuranceDependencyType = {
                                className: "LkpDependencyType",
                                name: $scope.insuranceMetaData.lkpDependencyType.name,
                                valueField: ("name." + util.userLocale),
                                labelText: "dependencyType",
                                selectedValue: $scope.selectedInsurance.lkpDependencyType,//set value
                                required: $scope.insuranceMetaData.lkpDependencyType.notNull
                            };
                            clientManagementService.getInsProviderList(false)
                                .then(function (data) {
                                    $scope.providerLkp = {
                                        className: "InsProvider",
                                        name: $scope.insuranceMetaData.insProvider.name,
                                        valueField: "customLabel",
                                        labelText: "insProvider",
                                        selectedValue: $scope.selectedInsurance.insProvider,//set value
                                        required: $scope.insuranceMetaData.insProvider.notNull,
                                        data: data
                                    };
                                    $scope.providerPlanLkp = {
                                        className: "InsProviderPlan",
                                        name: $scope.insuranceMetaData.insProviderPlan.name,
                                        valueField: ("name." + util.userLocale),
                                        labelText: "insProviderPlan",
                                        selectedValue: $scope.selectedInsurance.insProviderPlan,
                                        required: $scope.insuranceMetaData.insProviderPlan.notNull,
                                        data: []
                                    };
                                });

                        });

                    $scope.issueDateChanged = function () {
                        if ($scope.selectedInsurance && $scope.selectedInsurance.issueDate > $scope.selectedInsurance.expiryDate) {
                            $scope.selectedInsurance.expiryDate = new Date($scope.selectedInsurance.issueDate);
                        }
                    };

                    $scope.copyPatientName = function () {
                        if ($scope.selectedInsurance.subscriber != null && $scope.selectedInsurance.subscriber != "") {
                            $scope.selectedInsurance.subscriber = "";
                        } else {
                            $scope.selectedInsurance.subscriber = $scope.selectedInsurance.patient.fullName[util.userLocale] || "";
                        }
                    };

                    $scope.changePatientShare = function () {
                        $scope.selectedInsurance.coveragePercentage = 100 - $scope.selectedInsurance.patientShare;
                    }

                    $scope.submitPatientInsurance = function (valid) {
                        if (valid) {
                            var insProviderPlan = $scope.providerPlanLkp.selectedValue;
                            var insProvider = $scope.providerLkp.selectedValue;
                            var patient = angular.copy($scope.selectedInsurance.patient);

                            $scope.selectedInsurance.lkpDependencyType = $scope.insuranceDependencyType.selectedValue;
                            $scope.selectedInsurance.insProvider = insProvider;
                            $scope.selectedInsurance.insProviderPlan = insProviderPlan;
                            $scope.selectedInsurance.patient = patient;

                            if ($scope.formMode === "add") {
                                patientInsuranceFormService.addPatientInsurance($scope.selectedInsurance)
                                    .then(function (response) {
                                        $scope.selectedInsurance = response.data;
                                        $scope.selectedInsurance.patient = patient;
                                        if (!$scope.wiz) {
                                            util.createToast(util.systemMessages.success, "success");
                                        }
                                        var params = {
                                            insProviderPlan: insProviderPlan,
                                            selectedInsurance: $scope.selectedInsurance,
                                            insProvider: insProvider
                                        }
                                        $scope.$emit(commonData.events.exitPatientInsuranceForm, params);
                                    });
                            } else if ($scope.formMode === "edit") {
                                patientInsuranceFormService.editPatientInsurance($scope.selectedInsurance)
                                    .then(function (response) {
                                        $scope.selectedInsurance.patient = patient;
                                        if (!$scope.wiz) {
                                            util.createToast(util.systemMessages.success, "success");
                                        }
                                        $scope.$emit(commonData.events.exitPatientInsuranceForm, response.data);
                                    });
                            }
                        }
                    };


                    $scope.providerListener = function (insProvider) {
                        if (insProvider == null) {
                            return;
                        }
                        var filterablePageRequest = {
                            filters: [
                                { field: "insProvider.rid", value: insProvider.rid, operator: "eq" }
                            ]
                        };
                        return clientManagementService.getInsProviderPlanListByProvider(filterablePageRequest)
                            .then(function (response) {
                                var data = response.data;
                                $scope.providerPlanLkp.updateData(data);
                                $scope.providerPlanLkp.selectedValue = $scope.selectedInsurance.insProviderPlan;//set value if exists 
                                //If it is simple then inject value into form, we pull lkps later on submission  
                                if (insProvider.isSimple) {
                                    $scope.providerPlanLkp.selectedValue = data[0];
                                    $scope.providerPlanListener();
                                }
                            });

                    }

                    $scope.providerPlanListener = function () {
                        if ($scope.callPlanListener || $scope.formMode === "add") {
                            // to auto fill the coveragePercentage when selecting the plan
                            if ($scope.providerLkp && $scope.providerLkp.selectedValue !== undefined) {
                                $scope.selectedInsurance.coveragePercentage = $scope.providerPlanLkp.selectedValue.coveragePercentage;
                                $scope.selectedInsurance.patientShare = 100 - $scope.selectedInsurance.coveragePercentage;
                            }
                        } else {
                            $scope.callPlanListener = true;
                        }
                    };
                }]
        }
    });
});




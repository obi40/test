define(['app', 'util', 'commonData', 'config'], function (app, util, commonData, config) {
    'use strict';
    app.directive('orderForm', function () {
        return {
            restrict: 'E',
            replace: false,
            scope: {
                order: "=order",
                options: "=options"
            },
            templateUrl: "./" + config.lisDir + "/modules/shared/directives/orderForm/order-form-view.html",
            controller: ['$scope', 'commonMethods', 'clientManagementService', 'lovService',
                'doctorService', 'patientInsuranceFormService', '$mdDialog', '$filter', '$q',
                function ($scope, commonMethods, clientManagementService, lovService,
                    doctorService, patientInsuranceFormService, $mdDialog, $filter, $q) {

                    if ($scope.options == null) {
                        return;
                    }
                    angular.element(function () {
                        if ($scope.options.autoInit) {
                            $scope.options.init();
                        }
                    });

                    $scope.options["setOrder"] = function (order) {
                        $scope.order = order;
                        $scope.visitTypeLkp.selectedValue = $scope.order.visitType;
                        $scope.doctorsLov.selectedValue = $scope.order.doctor;
                        prepareHealthCareProvider();
                        if ($scope.providersLov != null && $scope.providersPlanLov != null && $scope.order.providerPlan != null) {
                            $scope.insuranceTypeLkp.selectedValue = $scope.order.providerPlan.insProvider.insuranceType;
                            $scope.providersLov.selectedValue = $scope.order.providerPlan.insProvider;
                            $scope.providersPlanLov.selectedValue = $scope.order.providerPlan;
                        }
                    };
                    $scope.options["getOrder"] = function () {
                        $scope.visitTypeLkp.assignValues($scope.order, [$scope.visitTypeLkp, $scope.doctorsLov]);
                        return $scope.order;
                    };
                    $scope.options["clear"] = function () {
                        $scope.order = null;
                        $scope.options.form.$setPristine();
                        $scope.options.form.$setUntouched();
                        $scope.visitTypeLkp.clearLkps([$scope.visitTypeLkp, $scope.doctorsLov]);
                    };
                    $scope.order.isInsurance = false;
                    $scope.patientProviderPlanInsLov = null;//provider->plan lov
                    $scope.visitTypeLkp = null;
                    $scope.insuranceTypeLkp = null;
                    $scope.doctorsLov = null;
                    $scope.currentDate = new Date();
                    var allProviders = [];
                    $scope.providersLov = null;
                    $scope.providersPlanLov = null;
                    $scope.options["initiated"] = false;
                    $scope.options["init"] = function () {
                        $scope.providersLov = {
                            className: "InsProvider",
                            valueField: "label",
                            labelText: "referralCompany",
                            selectedValue: null,
                            required: true,
                            name: "insProvider",
                            data: [],
                            onParentChange: providersLovOnParentChange
                        };
                        $scope.providersPlanLov = {
                            className: "InsProviderPlan",
                            name: "insProviderPlan",
                            labelText: "insProviderPlan",
                            valueField: ("name." + util.userLocale),
                            selectedValue: null,
                            required: true,
                            data: [],
                            onParentChange: providersPlanLovOnParentChange
                        };
                        return $q.all([lovService.getLkpByClass({ className: "LkpInsuranceType" }),
                        clientManagementService.getInsProviderList(false),
                        fetchMetaVisitProvider()]).then(function (response) {
                            var insTypeData = response[0];
                            for (var idx = insTypeData.length - 1; idx >= 0; idx--) {
                                var obj = insTypeData[idx];
                                if (obj.code != "LAB" && obj.code != "LAB_NETWORK") {
                                    insTypeData.splice(idx, 1);
                                }
                            }
                            $scope.insuranceTypeLkp = {
                                className: "LkpInsuranceType",
                                valueField: "name." + util.userLocale,
                                labelText: "referralType",
                                selectedValue: null,
                                required: true,
                                name: "insuranceType",
                                data: insTypeData
                            };
                            var insData = response[1];
                            for (var idx = insData.length - 1; idx >= 0; idx--) {
                                var obj = insData[idx];
                                if (obj.insuranceType.code == "LAB" || obj.insuranceType.code == "LAB_NETWORK") {
                                    var parent = obj.parentProvider;
                                    var label = parent != null ? parent.name[util.userLocale] : "";
                                    //If there is a parent
                                    if (label.length > 0) {
                                        label = label + commonData.arrow + obj.name[util.userLocale];
                                    } else {
                                        label = obj.name[util.userLocale];
                                    }
                                    obj["label"] = label;
                                } else {
                                    insData.splice(idx, 1);
                                }
                            }
                            allProviders = insData;
                            $scope.options.initiated = true;
                        });
                    };
                    function fetchMetaVisitProvider() {

                        return commonMethods.retrieveMetaData("EmrVisit").then(function (response) {
                            $scope.visitMetaData = response.data;
                            return $q.all([lovService.getLkpByClass({ className: "LkpVisitType" }), doctorService.getAllDoctors(),
                            prepareHealthCareProvider()])
                                .then(function (response) {
                                    var visitTypes = response[0];
                                    $scope.visitTypeLkp = {
                                        className: "LkpVisitType",
                                        valueField: "name." + util.userLocale,
                                        labelText: "visitType",
                                        selectedValue: $scope.order.visitType,
                                        required: $scope.visitMetaData.visitType.notNull,
                                        name: $scope.visitMetaData.visitType.name,
                                        data: visitTypes
                                    };
                                    //pre select walk in
                                    if ($scope.visitTypeLkp.selectedValue == null) {
                                        for (var idx = 0; idx < visitTypes.length; idx++) {
                                            if (visitTypes[idx].code == "WALK_IN") {
                                                $scope.visitTypeLkp.selectedValue = visitTypes[idx];
                                                break;
                                            }
                                        }
                                    }

                                    $scope.doctorsLov = {
                                        className: "Doctor",
                                        valueField: "name." + util.userLocale,
                                        labelText: "doctor",
                                        selectedValue: null,
                                        required: $scope.visitMetaData.doctor.notNull,
                                        name: $scope.visitMetaData.doctor.name,
                                        data: response[1].data,
                                        noneLabel: util.systemMessages.selfRequest
                                    };

                                });
                        });

                    }

                    function providersLovOnParentChange(selectedInsuranceType) {
                        $scope.order.patientInsuranceInfo = null;//remove
                        $scope.patientProviderPlanInsLov.selectedValue = null;//remove
                        //$q so we can use onParentChange
                        return $q(function (resolve, reject) {
                            var newData = [];
                            for (var idx = 0; idx < allProviders.length; idx++) {
                                var obj = allProviders[idx];
                                if (obj.insuranceType.code == selectedInsuranceType.code) {
                                    newData.push(obj);
                                }
                            }
                            $scope.providersLov.updateData(newData);
                            resolve();
                        });

                    };
                    $scope.changePatientShare = function (patientInsuranceInfo) {
                        patientInsuranceInfo.coveragePercentage = 100 - patientInsuranceInfo.patientShare;
                    };
                    function providersPlanLovOnParentChange(selectedProvider) {
                        if (selectedProvider == null) {
                            return;
                        }
                        var filterablePageRequest = {
                            filters: [{ field: "insProvider.rid", value: selectedProvider.rid, operator: "eq" }]
                        };
                        return clientManagementService.getInsProviderPlanListByProvider(filterablePageRequest)
                            .then(function (response) {
                                $scope.providersPlanLov.updateData(response.data);
                                if (selectedProvider.isSimple || $scope.providersPlanLov.data.length == 1) {
                                    $scope.providersPlanLov.selectedValue = response.data[0];
                                    $scope.order.providerPlan = $scope.providersPlanLov.selectedValue;
                                }
                            });

                    };

                    $scope.providersPlanLovChange = function (selectedProviderPlan) {
                        $scope.order.providerPlan = selectedProviderPlan;
                    };
                    $scope.patientProviderPlanLovChange = function (lovObject) {
                        if (lovObject.invalid) {
                            $scope.options.form.InsProvider.$setValidity("invalid", false);
                            $scope.order.providerPlan = null;
                            $scope.order.patientInsuranceInfo = null;
                            return;
                        }
                        $scope.options.form.InsProvider.$setValidity("invalid", true);
                        $scope.order.providerPlan = lovObject.insProviderPlan;
                        $scope.order.patientInsuranceInfo = lovObject.patientInsuranceInfo;
                    }
                    function prepareHealthCareProvider() {
                        var patientInfo = angular.copy($scope.order.emrPatientInfo);
                        // show a list of provider -> plan
                        return clientManagementService.getProviderWithPlanListByPatient(patientInfo).then(function (response) {
                            var lkpData = [];
                            var data = response.data;
                            for (var dataKey in data) {
                                var label = "";
                                var obj = data[dataKey];
                                obj.patientInsuranceInfo.patientShare = 100 - obj.patientInsuranceInfo.coveragePercentage;

                                if (obj.insProvider.parentProvider != null) {
                                    label = obj.insProvider.parentProvider.name[util.userLocale] + commonData.arrow;
                                }
                                if (obj.insProvider.isSimple) {
                                    label += obj.insProvider.name[util.userLocale];
                                } else {
                                    label += obj.insProvider.name[util.userLocale] + commonData.arrow + obj.insProviderPlan.name[util.userLocale];
                                }
                                if (new Date(obj.patientInsuranceInfo.expiryDate) < new Date()) {
                                    label += " (" + $filter('translate')('expired') + ")";
                                    obj["invalid"] = true;
                                }
                                if (obj.patientInsuranceInfo.issueDate != null && new Date(obj.patientInsuranceInfo.issueDate) > new Date()) {
                                    label += " (" + $filter('translate')('notYetEffective') + ")";
                                    obj["invalid"] = true;
                                }
                                obj["customLabel"] = label;
                                obj["rid"] = obj.patientInsuranceInfo.rid;
                                lkpData.push(obj);
                            }
                            lkpData.sort(function (a, b) { return (a.customLabel > b.customLabel) ? 1 : ((b.customLabel > a.customLabel) ? -1 : 0); });

                            //$scope.patientProviderPlanInsLov.updateData  in case didnt open the lov (ng-if)
                            if ($scope.patientProviderPlanInsLov != null && $scope.patientProviderPlanInsLov.updateData != null) {
                                $scope.patientProviderPlanInsLov.updateData(lkpData);
                            } else {
                                $scope.patientProviderPlanInsLov = {
                                    className: "InsProvider",
                                    name: $scope.visitMetaData.patientInsuranceInfo.name,
                                    valueField: "customLabel",
                                    labelText: "insuranceCompany",
                                    required: true,
                                    selectedValue: null,
                                    data: lkpData
                                };
                            }
                            $scope.patientProviderPlanInsLov.selectedValue = $scope.order.patientInsuranceInfo != null ? $scope.order.patientInsuranceInfo : null;

                        });
                    }

                    //radio button listener
                    $scope.isInsuranceListener = function () {
                        if ($scope.order.isInsurance) {
                            return;
                        }
                        //remove everything related to insurance
                        if ($scope.patientProviderPlanInsLov != null) {
                            $scope.patientProviderPlanInsLov.selectedValue = null;
                        }
                        if ($scope.providersPlanLov != null) {
                            $scope.providersPlanLov.selectedValue = null;
                        }
                        if ($scope.providersLov != null) {
                            $scope.providersLov.selectedValue = null;
                        }
                        if ($scope.order.providerPlan != null) {
                            $scope.order.providerPlan = null;
                        }
                        if ($scope.order.patientInsuranceInfo != null) {
                            $scope.order.patientInsuranceInfo = null;
                        }

                    };

                    $scope.showCreateDoctorDialog = function (ev) {
                        $mdDialog.show({
                            controller: ["$scope", function ($scope) {
                                $scope.patternNum = config.regexpNum;
                                $scope.doctor = {
                                    name: {},
                                    description: {}
                                };
                                $scope.cancel = function () {
                                    $mdDialog.cancel();
                                };
                                $scope.transFields = {
                                    name: util.getTransFieldLanguages("name", "name", null, true),
                                    description: util.getTransFieldLanguages("description", "description", null, false)
                                };
                                $scope.submit = function () {
                                    doctorService.addDoctor($scope.doctor)
                                        .then(function (response) {
                                            util.createToast(util.systemMessages.success, "success");
                                            $scope.cancel();
                                            updateDoctorValues(response.data);
                                        })
                                };
                            }],
                            templateUrl: './' + config.lisDir + '/modules/dialogs/create-doctor.html',
                            parent: angular.element(document.body),
                            targetEvent: ev,
                            clickOutsideToClose: true,
                            fullscreen: false // Only for -xs, -sm breakpoints.
                        });
                    };

                    function updateDoctorValues(newDoctor) {
                        doctorService.getAllDoctors()
                            .then(function (response) {
                                $scope.doctorsLov.updateData(response.data);
                                for (var idx = 0; idx < response.data.length; idx++) {
                                    if (response.data[idx].rid == newDoctor.rid) {
                                        $scope.doctorsLov.selectedValue = response.data[idx];
                                        break;
                                    }
                                }
                            });
                    }

                    $scope.showCreatePatientInsuranceDialog = function (ev) {
                        $mdDialog.show({
                            controller: ["$scope", "patient", "callback", function ($scope, patient, callback) {
                                $scope.patternPercent = config.regexpPercent;
                                $scope.patternNum = config.regexpNum;
                                $scope.newPatientInsurance = {
                                    isActive: true,
                                    isDefault: false,
                                    isVip: false
                                };
                                $scope.providerLkp = null;
                                $scope.providerPlanLkp = null;
                                $scope.insuranceDependencyType = null;
                                $scope.cancel = function () {
                                    $mdDialog.cancel();
                                };
                                function providerListener(insProvider) {
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
                                            $scope.providerPlanLkp.data = data;
                                            //If it is simple then inject value into form, we pull lkps later on submission  
                                            if (insProvider.isSimple) {
                                                $scope.providerPlanLkp.selectedValue = data[0];
                                            } else if (data.length == 1) {
                                                $scope.providerPlanLkp.selectedValue = data[0];
                                            } else {
                                                $scope.providerPlanLkp.selectedValue = null;
                                            }
                                            $scope.providerPlanListener();
                                        });
                                }
                                commonMethods.retrieveMetaData("EmrPatientInsuranceInfo")
                                    .then(function (response) {
                                        $scope.insuranceMetaData = response.data;
                                        $scope.insuranceDependencyType = {
                                            className: "LkpDependencyType",
                                            name: $scope.insuranceMetaData.lkpDependencyType.name,
                                            valueField: ("name." + util.userLocale),
                                            labelText: "dependencyType",
                                            selectedValue: null,
                                            required: $scope.insuranceMetaData.lkpDependencyType.notNull
                                        };
                                        clientManagementService.getInsProviderList(false)
                                            .then(function (data) {
                                                $scope.providerLkp = {
                                                    className: "InsProvider",
                                                    name: $scope.insuranceMetaData.insProvider.name,
                                                    valueField: "customLabel",
                                                    labelText: "insProvider",
                                                    selectedValue: null,
                                                    required: $scope.insuranceMetaData.insProvider.notNull,
                                                    data: data
                                                };
                                                $scope.providerPlanLkp = {
                                                    className: "InsProviderPlan",
                                                    name: $scope.insuranceMetaData.insProviderPlan.name,
                                                    valueField: ("name." + util.userLocale),
                                                    labelText: "insProviderPlan",
                                                    selectedValue: null,
                                                    required: $scope.insuranceMetaData.insProviderPlan.notNull,
                                                    data: [],
                                                    onParentChange: providerListener
                                                };
                                            });

                                    });

                                $scope.issueDateChanged = function () {
                                    if ($scope.newPatientInsurance.issueDate > $scope.newPatientInsurance.expiryDate) {
                                        $scope.newPatientInsurance.expiryDate = new Date($scope.newPatientInsurance.issueDate);
                                    }
                                };

                                $scope.changePatientShare = function (patientInsurance) {
                                    patientInsurance.coveragePercentage = 100 - patientInsurance.patientShare;
                                };

                                $scope.submit = function (invalid) {
                                    if (invalid) {
                                        return;
                                    }
                                    var insProviderPlan = $scope.providerPlanLkp.selectedValue;
                                    var insProvider = $scope.providerLkp.selectedValue;
                                    $scope.newPatientInsurance.lkpDependencyType = $scope.insuranceDependencyType.selectedValue;
                                    $scope.newPatientInsurance.insProvider = insProvider;
                                    $scope.newPatientInsurance.insProviderPlan = insProviderPlan;
                                    $scope.newPatientInsurance.patient = patient;
                                    patientInsuranceFormService.addPatientInsurance($scope.newPatientInsurance).then(function (response) {
                                        util.createToast(util.systemMessages.success, "success");
                                        callback(response.data);
                                        $scope.cancel();
                                    });

                                };
                                $scope.copyPatientName = function () {
                                    if ($scope.newPatientInsurance.subscriber != null && $scope.newPatientInsurance.subscriber != "") {
                                        $scope.newPatientInsurance.subscriber = "";
                                    } else {
                                        $scope.newPatientInsurance.subscriber = patient.fullName[util.userLocale] || "";
                                    }
                                };
                                $scope.providerPlanListener = function () {
                                    // to auto fill the coveragePercentage when selecting the plan
                                    if ($scope.providerPlanLkp && $scope.providerPlanLkp.selectedValue) {
                                        $scope.newPatientInsurance.coveragePercentage = $scope.providerPlanLkp.selectedValue.coveragePercentage;
                                        $scope.newPatientInsurance.patientShare = 100 - $scope.newPatientInsurance.coveragePercentage;
                                    }
                                };

                            }],
                            templateUrl: './' + config.lisDir + '/modules/dialogs/create-patient-insurance.html',
                            parent: angular.element(document.body),
                            targetEvent: ev,
                            clickOutsideToClose: true,
                            fullscreen: false,
                            locals: {
                                patient: $scope.order.emrPatientInfo,
                                callback: updatePatientInsuranceValues
                            }
                        });
                    };
                    function updatePatientInsuranceValues(newInsurance) {
                        $scope.order.patientInsuranceInfo = newInsurance;//to set it in patientProviderPlanInsLov
                        prepareHealthCareProvider();
                    }
                }]
        }
    });
});

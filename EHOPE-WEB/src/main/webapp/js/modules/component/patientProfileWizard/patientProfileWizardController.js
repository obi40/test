define(['app', 'util', 'commonData'], function (app, util, commonData) {
    'use strict';
    app.controller('patientProfileWizardCtrl', [
        '$scope',
        '$rootScope',
        '$state',
        'paymentFormService',
        'orderFormService',
        'patientInsuranceFormService',
        '$timeout',
        function ($scope,
            $rootScope,
            $state,
            paymentFormService,
            orderFormService,
            patientInsuranceFormService,
            $timeout) {
            if ($rootScope.currentPatient == null) {
                util.exitPage();
                return;
            }
            $scope.selectedTests = [];
            $scope.testActualList = [];
            $scope.selectedGroups = null;
            $scope.editOrderTests = null;
            $scope.editOrderGroups = null;
            $scope.primaryLang = util.userLocale;
            $scope.patient = $rootScope.currentPatient;
            $scope.didPay = false;
            $scope.patientInfoOptions = {
                patientRid: $scope.patient.rid
            };
            $scope.newPatientInsurance = {
                patient: $scope.patient,
                isActive: true,
                isVip: false,
                isDefault: true
            };
            $scope.newVisit = {
                isStat: false,
                visitDate: new Date(),
                emrPatientInfo: $scope.patient,
                isWhatsappNotification: $scope.patient.isWhatsappNotification,
                isSmsNotification: $scope.patient.isSmsNotification,
                isEmailNotification: $scope.patient.isEmailNotification
            };
            $scope.orderFormOptions = {
                autoInit: false,
                isOrderDateDisabled: $rootScope.visitRid != null//editing order
            };
            if ($scope.patient.isInsurer) {//new patient registration with isInsurance
                util.waitForDirective("patientInsuranceFormReady", commonData.events.activatePatientInsuranceForm, $scope, {});
            } else {
                if ($rootScope.visitRid != null) {//editing order
                    fetchVisitData($rootScope.visitRid).then(function (response) {
                        $scope.newVisit = response.data;
                        $scope.newVisit["isInsurance"] = $scope.newVisit.providerPlan != null ? true : false;
                        $scope.editOrderTests = [];//reset
                        $scope.editOrderGroups = [];//reset
                        if ($scope.newVisit.labSamples != null && $scope.newVisit.labSamples.length > 0) {
                            for (var idx = 0; idx < $scope.newVisit.labSamples.length; idx++) {
                                var sample = $scope.newVisit.labSamples[idx];
                                if (sample.labTestActualSet != null && sample.labTestActualSet.length > 0) {
                                    for (var i = 0; i < sample.labTestActualSet.length; i++) {
                                        var obj = sample.labTestActualSet[i].testDefinition;
                                        obj["isCancelled"] = sample.labTestActualSet[i].lkpOperationStatus.code === commonData.operationStatus.CANCELLED;
                                        $scope.editOrderTests.push(obj);
                                    }
                                }
                            }
                        }
                        if ($scope.newVisit.visitGroups != null && $scope.newVisit.visitGroups.length > 0) {
                            for (var idx = 0; idx < $scope.newVisit.visitGroups.length; idx++) {
                                var vg = $scope.newVisit.visitGroups[idx];
                                $scope.editOrderGroups.push(vg.testGroup);
                            }
                        }
                        $timeout(function () {//wait for directive to finish
                            $scope.orderFormOptions.init().then(function () {
                                $scope.orderFormOptions.setOrder($scope.newVisit);
                            });
                        });
                    });
                } else {//creating new order
                    $scope.orderFormOptions.autoInit = true;
                }
            }
            //reset
            $scope.$on('$destroy', function () {
                delete $rootScope.visitRid;
                delete $rootScope.currentPatient;
            });

            $scope.$on(commonData.events.exitPatientInsuranceForm, function () {
                $scope.orderFormOptions.init();
            });

            $scope.$on(commonData.events.exitOrderForm, function () {
                var data = {
                    insProviderPlan: $scope.newVisit.providerPlan,
                    editOrderTests: $scope.editOrderTests,
                    editOrderGroups: $scope.editOrderGroups,
                };
                $scope.$broadcast(commonData.events.activateTestSelection, data);
            });

            $scope.$on(commonData.events.exitTestSelection, function (event, params) {
                $scope.testActualList = params.testActualList;
                $scope.selectedTests = params.selectedTests;
                $scope.selectedGroups = params.selectedGroups;
                $scope.$broadcast(commonData.events.activateDestinationEntry, $scope.newVisit.rid);
            });

            $scope.$on(commonData.events.exitDestinationEntry, function () {
                $scope.$broadcast(commonData.events.activateTestQuestionForm, $scope.newVisit.rid);
            });

            $scope.$on(commonData.events.exitTestQuestionForm, function () {
                //Skip if visit type was referal
                if ($scope.newVisit.visitType != null && $scope.newVisit.visitType.code === "REFERRAL") {
                    paymentFormService.skipPayment($scope.newVisit, null, function () {
                        $scope.$emit(commonData.events.exitPaymentForm, null);
                    });
                } else {
                    var data = {
                        selectedGroups: $scope.selectedGroups,
                        newVisit: $scope.newVisit,
                        isEditOrder: $rootScope.visitRid != null
                    };
                    $scope.$broadcast(commonData.events.activatePaymentForm, data);
                }
            });

            $scope.$on(commonData.events.exitPaymentForm, function () {
                $rootScope.sampleSeparationVisitRid = $scope.newVisit.rid;
                $rootScope.sampleSeparationIsWizard = true;
                $state.go("sample-separation");
            });

            $scope.submitOrder = function () {
                $scope.newVisit = $scope.orderFormOptions.getOrder();
                if ($scope.newVisit.patientInsuranceInfo != null) {
                    patientInsuranceFormService.editPatientInsurance($scope.newVisit.patientInsuranceInfo).then(function (response) {
                        $scope.newPatientInsurance = response.data;
                        saveOrder();
                    });
                } else {
                    saveOrder();
                }
            };
            function saveOrder() {
                if ($scope.newVisit.rid == null) {
                    orderFormService.createVisit($scope.newVisit).then(function (response) {
                        $scope.newVisit = response.data;//to get rid
                        visitCallback();
                    });
                } else {
                    orderFormService.updateVisitWizard($scope.newVisit).then(function () {
                        visitCallback();
                    });
                }
            }
            function visitCallback() {
                fetchVisitData($scope.newVisit.rid).then(function (response) {
                    $scope.newVisit = response.data;
                    $scope.orderFormOptions.setOrder($scope.newVisit);
                    $scope.newVisit["isInsurance"] = $scope.newVisit.providerPlan != null ? true : false;
                    $scope.$emit(commonData.events.exitOrderForm, null);
                });
            }
            function fetchVisitData(visitRid) {
                return orderFormService.fetchVisit(visitRid);
            }



        }
    ])
});
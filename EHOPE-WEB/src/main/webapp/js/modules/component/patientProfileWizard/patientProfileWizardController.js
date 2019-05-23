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
                    generateEditOrderData();
                } else {//creating new order
                    $scope.orderFormOptions.autoInit = true;
                }
            }

            function generateEditOrderData() {
                fetchVisitData($rootScope.visitRid).then(function (response) {
                    $scope.newVisit = response.data;
                    $scope.newVisit["isInsurance"] = $scope.newVisit.providerPlan != null ? true : false;
                    $scope.editOrderTests = [];//reset
                    $scope.editOrderGroups = [];//reset
                    if ($scope.newVisit.labSamples != null && $scope.newVisit.labSamples.length > 0) {
                        var testActuals = [];
                        for (var idx = 0; idx < $scope.newVisit.labSamples.length; idx++) {
                            var sample = $scope.newVisit.labSamples[idx];
                            if (sample.labTestActualSet != null && sample.labTestActualSet.length > 0) {
                                for (var i = 0; i < sample.labTestActualSet.length; i++) {
                                    testActuals.push(sample.labTestActualSet[i]);
                                }
                            }
                        }
                        prepareTests(testActuals);
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
                    }, 1000);
                });
            }
            function prepareTests(testActuals) {
                var testActualsLength = testActuals.length - 1;
                //create parent object and add all tests related to it
                OUTER: for (var idx = testActualsLength; idx >= 0; idx--) {
                    var testActual = testActuals[idx];
                    for (var y = 0; y < $scope.editOrderTests.length; y++) {
                        var td = $scope.editOrderTests[y];
                        if (td.testDefRid === testActual.testDefinition.rid) {
                            continue OUTER;
                        }
                    }
                    var obj = {
                        testDefRid: testActual.testDefinition.rid,
                        label: testActual.testDefinition.standardCode,
                        differentSampleMaxAmount: testActual.testDefinition.differentSampleMaxAmount,
                        children: [],
                        isParent: true
                    };
                    $scope.editOrderTests.push(obj);
                }
                for (var idx = 0; idx < $scope.editOrderTests.length; idx++) {
                    var parentTestDef = $scope.editOrderTests[idx];
                    for (var i = 0; i < testActuals.length; i++) {
                        var testActual = testActuals[i];
                        var testDef = testActual.testDefinition;
                        if (parentTestDef.testDefRid !== testDef.rid) {
                            continue;
                        }
                        testDef["isCancelled"] = testActual.lkpOperationStatus.code === commonData.operationStatus.CANCELLED;
                        testDef["sourceActualTest"] = testActual.sourceActualTest;
                        parentTestDef.children.push(testDef);
                        if (testDef.isCancelled) {
                            parentTestDef.cancelled = parentTestDef.cancelled != null ? (parentTestDef.cancelled + 1) : 1;
                        } else if (testDef.isAllowRepetitionDifferentSample || testDef.isAllowRepetitionSameSample) {
                            if (testDef.sourceActualTest == null) {
                                parentTestDef.amount = parentTestDef.amount != null ? (parentTestDef.amount + 1) : 1;
                            } else {
                                parentTestDef.reordered = parentTestDef.reordered != null ? (parentTestDef.reordered + 1) : 1;
                            }
                        }

                    }
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
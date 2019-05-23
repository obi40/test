define(['app', 'util', 'config', 'commonData'], function (app, util, config, commonData) {
    'use strict';
    app.service('paymentFormService',
        ["$mdDialog", "lovService", "patientProfileService", "orderFormService",
            function ($mdDialog, lovService, patientProfileService, orderFormService) {

                this.getTestsPricing = function (data) {
                    return util.createApiRequest("getTestsPricing.srvc", JSON.stringify(data));
                };
                this.getTestsPricingNoVisit = function (map) {
                    return util.createApiRequest("getTestsPricingNoVisit.srvc", JSON.stringify(map));
                };
                this.getTestsCoverageDetail = function (data) {
                    return util.createApiRequest("getTestsCoverageDetail.srvc", JSON.stringify(data));
                };
                this.payment = function (data) {
                    return util.createApiRequest("payment.srvc", JSON.stringify(data));
                };
                this.partialPayment = function (data) {
                    return util.createApiRequest("partialPayment.srvc", JSON.stringify(data));
                };
                this.skipPayment = function (visit, paymentWrapperList, callback) {
                    //not sending the payment types,amount of user
                    var data = {
                        patientVisit: { rid: visit.rid, approvalNumber: visit.approvalNumber },
                        paymentWrapperList: [{ paymentInformationList: [] }]
                    };
                    data.paymentWrapperList[0].paymentInformationList = this.preparePaymentAuthorizationList(paymentWrapperList);
                    util.createApiRequest("skipPayment.srvc", JSON.stringify(data)).then(function () {
                        util.createToast(util.systemMessages.success, "success");
                        callback();
                    });
                };
                this.refundPayment = function (data) {
                    return util.createApiRequest("refundPayment.srvc", JSON.stringify(data));
                };
                this.getRefundInfo = function (data) {
                    return util.createApiRequest("getRefundInfo.srvc", JSON.stringify(data));
                };
                this.getPreviousPaymentDialogData = function (data) {
                    return util.createApiRequest("getPreviousPaymentDialogData.srvc", JSON.stringify(data));
                };
                this.areTestsWithoutInsPricing = function (data) {
                    return util.createApiRequest("areTestsWithoutInsPricing.srvc", JSON.stringify(data));
                };
                this.preparePaymentAuthorizationList = function (paymentWrapperList) {
                    var result = [];
                    for (var paymentWrapperKey in paymentWrapperList) {
                        var payObj = angular.copy(paymentWrapperList[paymentWrapperKey]);
                        if (!payObj.parentTest) {//because there might be a test with children, this test wont have the required information
                            continue;
                        }
                        var obj = {
                            insCoverageDetail: payObj.insCoverageDetail,
                            billMasterItem: payObj.billMasterItem,
                            isAuthorized: payObj.isAuthorized,
                            comment: payObj.comment
                        }
                        // we only adding to the first place in the array, we dont care about adding tests -> list of payment information
                        // just get the payment information
                        result.push(obj);
                    }
                    return result;
                }

                this.paymentDialog = function (data) {
                    //injecting paymentFormService into dialog since it can't be reached from within
                    var isPartial = data.isPartial;
                    var isEditOrder = data.isEditOrder;
                    var previousPaidAmount = data.previousPaidAmount;
                    var total = data.total;
                    var prevGeneralDisPercentage = data.prevGeneralDisPercentage;
                    var prevGeneralDisAmount = data.prevGeneralDisAmount;
                    var visit = data.visit;
                    var finishCallback = data.finishCallback;//for finish button or a callback
                    var successCallback = data.successCallback;//if payment is success, so we can show hide some button on the actual page outside the dialog
                    var paymentWrapperList = data.paymentWrapperList;
                    var isInsured = data.isInsured;
                    $mdDialog.show({
                        controller: ["$scope", "$mdDialog", "paymentFormService", function ($scope, $mdDialog, paymentFormService) {
                            $scope.patternPercent = config.regexpPercent;
                            $scope.patternNum = config.regexpNum;
                            $scope.currency = util.userCurrency;
                            $scope.visit = visit;
                            $scope.isEditOrder = isEditOrder;
                            $scope.previousPaidAmount = previousPaidAmount;
                            $scope.feesAfterDiscount = total;
                            $scope.isInsured = isInsured;
                            var originalFees = total;
                            $scope.testPaymentTotal = total;
                            $scope.testPaymentChange = 0;
                            var id = -1;
                            $scope.successPayment = false;
                            $scope.isPercentage = true;
                            $scope.isAmount = true;
                            $scope.isPartial = isPartial;
                            $scope.discountPercentage = prevGeneralDisPercentage;
                            $scope.discountAmount = prevGeneralDisAmount;
                            var timezoneOffset = new Date().getTimezoneOffset();
                            var timezoneId = new Date().toString().split('(')[1].slice(0, -1).replace('Daylight', 'Standard');
                            lovService.getLkpByClass({ "className": "LkpPaymentMethod" }).then(function (data) {
                                $scope.paymentMethodLkp = {
                                    className: "LkpPaymentMethod",
                                    name: "lkpPaymentMethod",
                                    labelText: "paymentMethod",
                                    valueField: ("name." + util.userLocale),
                                    selectedValue: null,
                                    required: true,
                                    data: data
                                };
                                $scope.testPaymentList = [
                                    {
                                        id: id--,
                                        changeRate: null,
                                        amount: originalFees,
                                        lkp: angular.copy($scope.paymentMethodLkp),
                                        lkpPaymentMethod: angular.copy(data[0]),
                                        lkpPaymentCurrency: null
                                    }
                                ];

                            });

                            $scope.cancel = function () {
                                $mdDialog.cancel();
                            };

                            $scope.finish = function () {
                                if (finishCallback) {
                                    if (!isPartial) {//only update visit flags if in wizard
                                        var wrapper = {
                                            visitRid: ($scope.visit.rid + ""),
                                            isSmsNotification: $scope.visit.isSmsNotification.toString(),
                                            isEmailNotification: $scope.visit.isEmailNotification.toString()
                                        };
                                        orderFormService.updateVisitPayment(wrapper).then(function () {
                                            finishCallback();
                                        });
                                    } else {
                                        finishCallback();
                                    }
                                }
                                $scope.cancel();
                            };
                            function fullPayment() {
                                var discountValue = null;
                                if ($scope.isAmount && $scope.discountAmount != null) {
                                    discountValue = $scope.discountAmount;
                                } else if ($scope.isPercentage && $scope.discountPercentage != null && $scope.discountPercentage != "") {
                                    discountValue = $scope.discountPercentage;
                                }
                                if (isNaN(discountValue)) {
                                    discountValue = null;
                                }

                                var map = {
                                    patientVisit: { rid: $scope.visit.rid, approvalNumber: $scope.visit.approvalNumber },
                                    paymentWrapperList: [{ paymentInformationList: [] }],
                                    testPaymentList: $scope.testPaymentList
                                };
                                if (discountValue != null) {
                                    if ($scope.isPercentage) {
                                        map["generalDiscountPercentage"] = discountValue;
                                    } else {
                                        map["generalDiscountAmount"] = discountValue;
                                    }
                                }
                                map.paymentWrapperList[0].paymentInformationList = paymentFormService.preparePaymentAuthorizationList(paymentWrapperList);
                                paymentFormService.payment(map).then(function () {
                                    util.createToast(util.systemMessages.success, "success");
                                    //$scope.visit = response.data;
                                    $scope.successPayment = true;
                                    if (successCallback) {
                                        successCallback();
                                    }
                                });
                            }
                            function partialPayment() {
                                var map = {
                                    patientVisit: { rid: $scope.visit.rid },
                                    testPaymentList: $scope.testPaymentList
                                };
                                paymentFormService.partialPayment(map).then(function () {
                                    util.createToast(util.systemMessages.success, "success");
                                    $scope.successPayment = true;
                                    $scope.finish();
                                });
                            }
                            $scope.submit = function (isValid) {
                                if (!isValid) {
                                    return;
                                }
                                if ($scope.isPartial) {
                                    partialPayment();
                                } else {
                                    fullPayment();
                                }
                            };

                            $scope.discountPercentDeduct = function (discountValue, isPercentage) {
                                if (discountValue == null || isPercentage == null) {
                                    $scope.feesAfterDiscount = originalFees;
                                    $scope.isPercentage = true;
                                    $scope.isAmount = true;
                                    $scope.discountAmount = null;
                                    $scope.discountPercentage = null;
                                    calculateTotalAndChange();
                                    return;
                                }
                                //to know if it is a percent or amount
                                if (isPercentage) {
                                    $scope.feesAfterDiscount = ((100.00 - discountValue) / 100.00) * originalFees;
                                    $scope.discountAmount = util.round(originalFees - $scope.feesAfterDiscount);
                                    $scope.isPercentage = true;
                                    $scope.isAmount = false;
                                } else {
                                    var discountAmount = util.round(discountValue / originalFees, 10);//in percentage
                                    $scope.discountPercentage = util.round(discountAmount * 100);
                                    $scope.feesAfterDiscount = originalFees - (discountAmount * originalFees);
                                    $scope.isPercentage = false;
                                    $scope.isAmount = true;
                                }
                                $scope.feesAfterDiscount = util.round($scope.feesAfterDiscount);
                                if (isNaN($scope.feesAfterDiscount)) {
                                    $scope.feesAfterDiscount = originalFees;
                                }
                                calculateTotalAndChange();
                            };

                            function calculateTotalAndChange() {
                                $scope.testPaymentTotal = 0;
                                for (var payTestKey in $scope.testPaymentList) {
                                    if (isNaN($scope.testPaymentList[payTestKey].amount)) {
                                        continue;
                                    }
                                    $scope.testPaymentTotal = $scope.testPaymentTotal + $scope.testPaymentList[payTestKey].amount;
                                }

                                $scope.testPaymentChange = 0;
                                if ($scope.testPaymentTotal >= $scope.feesAfterDiscount) {
                                    $scope.testPaymentChange = util.round(($scope.testPaymentTotal - $scope.feesAfterDiscount));
                                }
                            }
                            $scope.onTestPaymentChange = function () {
                                calculateTotalAndChange();
                            };

                            $scope.addTestPayment = function () {
                                $scope.testPaymentList.push({
                                    id: id--,
                                    changeRate: null,
                                    amount: null,
                                    lkp: angular.copy($scope.paymentMethodLkp),
                                    lkpPaymentMethod: null,
                                    lkpPaymentCurrency: null
                                });
                            };

                            $scope.deleteTestPayment = function (payment) {
                                for (var idx = $scope.testPaymentList.length - 1; idx >= 0; idx--) {
                                    if ($scope.testPaymentList[idx].id == payment.id) {
                                        $scope.testPaymentList.splice(idx, 1);
                                        break;
                                    }
                                }
                            };

                            $scope.generateInvoiceReport = function () {
                                $scope.invoiceInfo = {
                                    visitRid: $scope.visit.rid,
                                    timezoneOffset: timezoneOffset,
                                    timezoneId: timezoneId
                                };
                                patientProfileService.printInvoiceReport($scope.invoiceInfo)
                                    .then(function (response) {
                                        var fileName = $scope.visit.emrPatientInfo.fullName[util.userNamePrimary] + "-" + $scope.visit.admissionNumber; //commonData.reportNames.results
                                        util.fileHandler(response.data, { type: "application/pdf", name: fileName });
                                    });
                            };

                            $scope.generateInsuranceInvoiceReport = function () {
                                $scope.insuranceInvoiceInfo = {
                                    visitRid: $scope.visit.rid,
                                    timezoneOffset: timezoneOffset,
                                    timezoneId: timezoneId
                                };
                                patientProfileService.printInsuranceInvoiceReport($scope.insuranceInvoiceInfo)
                                    .then(function (response) {
                                        util.fileHandler(response.data, { type: "application/pdf", name: commonData.reportNames.insuranceInvoice });
                                    });
                            };

                        }],
                        templateUrl: './' + config.lisDir + '/modules/dialogs/payment.html',
                        parent: angular.element(document.body),
                        clickOutsideToClose: true
                    }).then(function () {

                    }, function () {

                    });
                };
            }]);
});
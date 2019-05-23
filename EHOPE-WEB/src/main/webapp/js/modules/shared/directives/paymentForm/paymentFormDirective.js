define(['app', 'util', 'config', 'commonData'], function (app, util, config, commonData) {
    'use strict';
    app.directive('paymentForm', function () {
        return {
            restrict: 'E',
            replace: false,
            scope: {
                wiz: "=wiz", //named wiz to avoid conflict with the "wizard" directive [optional],
                didPay: "=didPay"
            },
            templateUrl: "./" + config.lisDir + "/modules/shared/directives/paymentForm/payment-form-view.html",
            controller: ['$scope', 'paymentFormService', 'billingManagementService',
                'priceListService', 'sampleSeparationService', 'orderFormService',
                function ($scope, paymentFormService, billingManagementService,
                    priceListService, sampleSeparationService, orderFormService) {

                    $scope.patternPercent = config.regexpPercent;
                    $scope.patternNum = config.regexpNum;
                    $scope.selectedGroups = null;
                    $scope.newPatientInsurance = null;
                    $scope.insProviderPlan = null;
                    $scope.newVisit = null;
                    $scope.forms = {};
                    var testList = [];
                    var isEditOrder = false;
                    $scope.$on(commonData.events.activatePaymentForm, function (event, params) {
                        $scope.selectedGroups = params.selectedGroups;
                        $scope.newVisit = params.newVisit;
                        $scope.insProviderPlan = $scope.newVisit.providerPlan;
                        isEditOrder = params.isEditOrder;
                        if ($scope.newVisit.patientInsuranceInfo) {
                            $scope.newPatientInsurance = angular.copy($scope.newVisit.patientInsuranceInfo);
                        }
                        activateDirective();
                    });

                    function activateDirective() {
                        if ($scope.activateDirective) {
                            $("#paymentWrapperTreeList").data("kendoTreeList").dataSource.read();

                            //re show/hide columns
                            if ($scope.insProviderPlan) {
                                $("#paymentWrapperTreeList").data("kendoTreeList").showColumn("isAuthorized");
                                $("#paymentWrapperTreeList").data("kendoTreeList").showColumn("percentage");
                            } else {
                                $("#paymentWrapperTreeList").data("kendoTreeList").hideColumn("isAuthorized");
                                $("#paymentWrapperTreeList").data("kendoTreeList").hideColumn("percentage");
                            }
                            if ($scope.selectedGroups == null || $scope.selectedGroups.length === 0) {
                                $("#paymentWrapperTreeList").data("kendoTreeList").hideColumn("groupPercentage");
                            } else {
                                $("#paymentWrapperTreeList").data("kendoTreeList").showColumn("groupPercentage");
                            }
                        }

                        $scope.exitEmit = function () {
                            $scope.$emit(commonData.events.exitPaymentForm, null);
                        };

                        $scope.skipPayment = function () {
                            paymentFormService.skipPayment($scope.newVisit, $scope.paymentWrapperList, $scope.exitEmit);
                        };
                        $scope.authorizeAllListener = function (value) {
                            for (var paymentWrapperKey in $scope.paymentWrapperList) {
                                var payObj = $scope.paymentWrapperList[paymentWrapperKey];
                                if (!payObj.parentTest) {//because there might be a test with children, this test wont have the required information
                                    continue;
                                }
                                payObj.isAuthorized = value;
                            }
                            $scope.calculatePrice();
                        };

                        $scope.isAuthorizedListener = function (value, rid) {
                            for (var paymentWrapperKey in $scope.paymentWrapperList) {
                                var payObj = $scope.paymentWrapperList[paymentWrapperKey];
                                if (!payObj.parentTest) {//because there might be a test with children, this test wont have the required information
                                    continue;
                                }
                                if (payObj.rid == rid) {
                                    payObj.isAuthorized = value;
                                    break;
                                }
                            }
                            $scope.calculatePrice();
                        };

                        $scope.activateDirective = true;
                        $scope.paymentWrapperChanged = false;
                        $scope.didPay = false;
                        $scope.paymentWrapperList = [];
                        var originalAuthorization = [];// to hold the original authorization yes or no , so it can be still disaplyed correctly after returning from the database
                        var paymentWrapperDataSource = new kendo.data.TreeListDataSource({
                            transport: {
                                read: function (e) {
                                    paymentFormService.getTestsCoverageDetail($scope.newVisit.rid).then(function (response) {
                                        $scope.paymentWrapperList = preparePaymentWrapper(response.data);
                                        for (var resultKey in $scope.paymentWrapperList) {
                                            if ($scope.paymentWrapperList[resultKey].parentTest) {
                                                // since we didnt calculate anything then set all totals to zero
                                                $scope.paymentWrapperList[resultKey].charge = 0;
                                                var obj = {
                                                    item: $scope.paymentWrapperList[resultKey].billMasterItem,
                                                    approval: $scope.paymentWrapperList[resultKey].insCoverageDetail ?
                                                        $scope.paymentWrapperList[resultKey].insCoverageDetail.needAuthorization : false
                                                };
                                                originalAuthorization.push(obj);
                                            }
                                        }
                                        e.success($scope.paymentWrapperList);
                                        $scope.calculatePrice();
                                    });
                                },
                                update: function (e) {
                                    for (var rowKey in $scope.paymentWrapperList) {
                                        var billMasterItemRid = e.data.billMasterItem.rid;
                                        if ($scope.paymentWrapperList[rowKey].billMasterItem &&
                                            billMasterItemRid == $scope.paymentWrapperList[rowKey].billMasterItem.rid) {
                                            $scope.paymentWrapperList[rowKey] = e.data;
                                            e.success(e.data);
                                        }
                                    }
                                },
                                destroy: function (e) {
                                    //removing tests only
                                    var testRid = null;
                                    for (var idx = 0; idx < $scope.paymentWrapperList.length; idx++) {
                                        if (e.data.rid == $scope.paymentWrapperList[idx].rid) {
                                            $scope.paymentWrapperList.splice(idx, 1);
                                            testRid = e.data.rid;
                                            e.success($scope.paymentWrapperList);
                                            break;
                                        }
                                    }
                                    for (var idx = 0; idx < testList.length; idx++) {
                                        if (testRid == testList[idx].rid) {
                                            testList.splice(idx, 1);
                                            break;
                                        }
                                    }
                                }
                            },
                            schema: {
                                model: {
                                    id: "rid",
                                    parentId: "parentTestRid",
                                    fields: {
                                        rid: { type: "number" },
                                        parentTestRid: { type: "number", nullable: true },
                                        parentTest: { type: "object", editable: false },
                                        description: { type: "string", editable: false },
                                        cptCode: { type: "string", editable: false },
                                        price: { type: "number", editable: false },
                                        percentage: { type: "number", editable: false },
                                        charge: { type: "number", editable: false },
                                        isAuthorized: { type: "boolean", editable: false },
                                        comment: { type: "string", editable: true }
                                    },
                                    expanded: false
                                }
                            },
                            aggregate: [{ field: "charge", aggregate: "sum" }]
                        });

                        $scope.paymentWrapperTreeListOptions = {
                            columns: [
                                {
                                    field: "code",
                                    expandable: true,
                                    title: util.systemMessages.standardCode,
                                    template: function (dataItem) {
                                        if (!dataItem.parentTest) {
                                            return dataItem.standardCode;
                                        } else if (dataItem.rid > 0) {//incase this test has only one payment record so we can display this record on the same record as the test
                                            return dataItem.standardCode;
                                        }
                                        return "";
                                    }
                                },
                                {
                                    field: "description",
                                    title: util.systemMessages.testDescription,
                                    template: function (dataItem) {
                                        if (!dataItem.parentTest) {
                                            return dataItem.description;
                                        } else if (dataItem.rid > 0) {//incase this test has only one payment record so we can display this record on the same record as the test
                                            return dataItem.description;
                                        }
                                        return "";
                                    }
                                },
                                {
                                    field: "cptCode",
                                    title: util.systemMessages.cptCode,
                                    template: function (dataItem) {
                                        if (!dataItem.parentTest) {
                                            return dataItem.cptCode;
                                        }
                                        return dataItem.billMasterItem.cptCode;
                                    }
                                },
                                // {
                                //     field: "needAuthorization",
                                //     title: util.systemMessages.needApproval,
                                //     template: function (dataItem) {
                                //         if (!dataItem.parentTest) {
                                //             return "";
                                //         } else if (!dataItem.isFetched) {
                                //             return commonData.na;
                                //         }/* else if (!dataItem.insCoverageDetail || dataItem.exceededMaxAmount) {
                                //             return util.systemMessages.no;
                                //         }
                                //         return dataItem.insCoverageDetail.needAuthorization ? util.systemMessages.yes : util.systemMessages.no;*/
                                //         // get the original need authorization to display, we dont care about the used coverage detail in the end
                                //         // just show me if it needed an apporval when we first fetched it
                                //         for (var originalAppKey in originalAuthorization) {
                                //             var rid = dataItem.billMasterItem.rid;
                                //             if (originalAuthorization[originalAppKey].item.rid == rid) {
                                //                 if (originalAuthorization[originalAppKey].approval) {
                                //                     return "<span class='error-color'><span class='bold'>" + util.systemMessages.yes + "</span></span>";
                                //                 } else {
                                //                     return "<span>" + util.systemMessages.no + "</span>";
                                //                 }
                                //             }
                                //         }

                                //     }
                                // },
                                {
                                    field: "isAuthorized",
                                    title: util.systemMessages.isApproved,
                                    hidden: ($scope.insProviderPlan == null),
                                    template: function (dataItem) {
                                        if (!dataItem.parentTest) {
                                            return "";
                                        } else if (!dataItem.isFetched) {
                                            return commonData.na;
                                        }
                                        var rid = angular.copy(dataItem.rid);
                                        if (rid < 0) {// discard the minus if it has a one since ng-model does not accept it
                                            rid = "_" + (rid * -1);
                                        }
                                        var name = "isAuthorized" + rid;
                                        $scope[name] = dataItem.isAuthorized;

                                        return '<div id="' + name + '" class="text-center">' +
                                            '<md-checkbox ng-model="' + name + '" ng-change="isAuthorizedListener(' + name + ',' + dataItem.rid + ')" aria-label="util.systemMessages.isApproved"></md-checkbox>'
                                            + '</div>';

                                    }
                                },
                                {
                                    field: "comment",
                                    title: util.systemMessages.comment,
                                    template: function (dataItem) {
                                        if (!dataItem.parentTest) {
                                            return "";
                                        } else if (!dataItem.isFetched) {
                                            return commonData.na;
                                        }
                                        return dataItem.comment ? dataItem.comment : "";
                                    }
                                },
                                {
                                    field: "price",
                                    title: util.systemMessages.originalPrice,
                                    template: function (dataItem) {
                                        if (!dataItem.parentTest) {
                                            return "";
                                        } else if (!dataItem.isFetched) {
                                            return commonData.na;
                                        }
                                        return dataItem.billPricing ? dataItem.billPricing.price + " " + util.userCurrency : "";
                                    }
                                },
                                {
                                    field: "percentage",//ins percentage
                                    title: util.systemMessages.coInsurance,
                                    hidden: ($scope.insProviderPlan == null),
                                    template: function (dataItem) {
                                        if (!dataItem.parentTest) {
                                            return "";
                                        } else if (!dataItem.isFetched) {
                                            return commonData.na;
                                        } else if (dataItem.percentage) {
                                            return (100 - dataItem.percentage) + "%";
                                        } else if (dataItem.billPricing && dataItem.billPricing.price) {
                                            return "100%";
                                        }

                                    }
                                },
                                {
                                    field: "groupPercentage",//test group percentage
                                    title: util.systemMessages.coGroupPercentage,
                                    hidden: ($scope.selectedGroups == null || $scope.selectedGroups.length === 0),
                                    template: function (dataItem) {
                                        if (!dataItem.parentTest) {
                                            return "";
                                        } else if (!dataItem.isFetched) {
                                            return commonData.na;
                                        } else if (dataItem.groupPercentage != null) {
                                            return (100 - dataItem.groupPercentage) + "%";
                                        } else if (dataItem.groupAmount != null) {
                                            var p = util.round((dataItem.groupAmount / dataItem.groupTotal) * 100);
                                            return (100 - p) + "%";
                                        } else if (dataItem.billPricing && dataItem.billPricing.price) {
                                            return "100%";
                                        }

                                    }
                                },
                                {
                                    field: "charge",
                                    title: util.systemMessages.patientShare,
                                    template: function (dataItem) {
                                        if (!dataItem.parentTest) {
                                            return "";
                                        } else if (!dataItem.isFetched) {
                                            return commonData.na;
                                        }
                                        return dataItem.charge ? dataItem.charge + " " + util.userCurrency : "";
                                    },
                                    footerTemplate: function (data) {
                                        if (isNaN(data.sum)) {
                                            return "";
                                        }
                                        return "{{'total' | translate}}:" + util.round(data.sum) + " " + util.userCurrency;
                                    },
                                    footerAttributes: {
                                        "class": "table-footer-cell",
                                        style: "text-align: " + util.direction == "ltr" ? "right" : "left" + "; font-size: 14px"
                                    }
                                }
                            ],
                            dataSource: paymentWrapperDataSource,
                            selectable: "single row",
                            change: function (e) {
                                var selectedRows = this.select();
                                if (selectedRows.length > 0) {
                                    $scope.selectedPayment = this.dataItem(selectedRows[0]);
                                    //can only select master items(fetched) or tests.
                                    if ($scope.selectedPayment.isFetched != null && $scope.selectedPayment.isFetched === false) {
                                        e.sender.element.find("tr[data-uid='" + $scope.selectedPayment.uid + "']")
                                            .removeClass('k-state-selected k-state-selecting');
                                        $scope.selectedPayment = null;
                                        $scope.paymentWrapperChanged = false;
                                    }

                                } else {
                                    $scope.selectedPayment = null;
                                }
                            }
                        };

                        $scope.disableDeleteTest = function () {
                            //disable if null or it exists in a package
                            if ($scope.selectedPayment == null || $scope.selectedPayment.parentTestRid != null) {
                                return true;
                            }
                            for (var idx = 0; idx < $scope.selectedGroups.length; idx++) {
                                var group = $scope.selectedGroups[idx];
                                if (group.isProfile) {
                                    continue;
                                }
                                for (var i = 0; i < group.groupDefinitions.length; i++) {
                                    if (group.groupDefinitions[i].testDefinition.rid === $scope.selectedPayment.rid) {
                                        return true;
                                    }
                                }
                            }
                            return false;
                        };

                        $scope.editPaymentWrapper = function (dataItem) {
                            $("#paymentWrapperTreeList").data("kendoTreeList").editRow(dataItem);
                            $("#paymentWrapperTreeList").data("kendoTreeList").select($("tr[data-uid=" + dataItem.uid + "]"));
                            $scope.paymentWrapperChanged = true;
                        };

                        $scope.savePaymentWrapperChanges = function () {
                            $("#paymentWrapperTreeList").data("kendoTreeList").dataSource.sync();
                            $scope.paymentWrapperChanged = false;
                            $scope.calculatePrice();
                        };

                        // $scope.deletePaymentWrapper = function (dataItem) {
                        //     $("#paymentWrapperTreeList").data("kendoTreeList").dataSource.remove(dataItem);
                        //     $("#paymentWrapperTreeList").data("kendoTreeList").dataSource.sync();
                        //     $scope.selectedPayment = null;
                        //     $scope.paymentWrapperChanged = false;
                        //     $scope.calculatePrice();
                        // };

                        function preparePaymentWrapper(data) {
                            var result = [];
                            testList = [];
                            var id = -1;
                            for (var key in data) {
                                var keyObj = data[key];
                                var test = keyObj.testDefinition;
                                test["parentTestRid"] = null;
                                if (data[key].paymentInformationList.length == 1) {
                                    var payObj = data[key].paymentInformationList[0];
                                    test["parentTest"] = {};
                                    for (var keyPay in payObj) {
                                        test[keyPay] = payObj[keyPay];
                                    }
                                } else {
                                    for (var keyPay in data[key].paymentInformationList) {
                                        var payObj = data[key].paymentInformationList[keyPay];
                                        payObj["parentTestRid"] = keyObj.testDefinition.rid;
                                        payObj["parentTest"] = keyObj.testDefinition;
                                        payObj["rid"] = id--;
                                        result.push(payObj);
                                    }
                                }
                                testList.push(test);
                            }

                            result = result.concat(testList);
                            return result;
                        }

                        $scope.calculatePrice = function () {
                            //calculates price , also this updates some flags
                            if ($scope.paymentWrapperList.length < 1) {
                                return;
                            }
                            var map = {
                                patientVisit: { rid: $scope.newVisit.rid },
                                paymentWrapperList: [{ paymentInformationList: [] }]
                            };
                            var testsRid = [];
                            for (var idx = 0; idx < testList.length; idx++) {
                                testsRid.push(testList[idx].rid);
                            }
                            map.paymentWrapperList[0].paymentInformationList = paymentFormService.preparePaymentAuthorizationList($scope.paymentWrapperList);
                            paymentFormService.getTestsPricing(map).then(function (response) {
                                if ($scope.insProviderPlan != null) {
                                    paymentFormService.areTestsWithoutInsPricing({ "testsRid": testsRid, "providerPlanRid": $scope.insProviderPlan.rid + "" });
                                }
                                var data = response.data;
                                $scope.paymentWrapperList = preparePaymentWrapper(data.result);
                                $scope.masterItemsNoPriceList = data.masterItemsNoPriceList;
                                $scope.total = data.total;
                                $("#paymentWrapperTreeList").data("kendoTreeList").dataSource.data($scope.paymentWrapperList);
                                $scope.paymentWrapperChanged = false;
                                $scope.selectedPayment = null;
                            });
                        };

                        $scope.createBillPricings = function (isValid) {
                            if (!isValid) {
                                return;
                            }

                            priceListService.getDefaultBillPriceList().then(function (response) {
                                var billPricings = [];
                                for (var masterItemKey in $scope.masterItemsNoPriceList) {
                                    var masterItemObj = $scope.masterItemsNoPriceList[masterItemKey];
                                    masterItemObj.billPricings = null;
                                    billPricings.push({
                                        price: masterItemObj.price,
                                        startDate: masterItemObj.startDate,
                                        endDate: masterItemObj.endDate,
                                        billPriceList: response.data,
                                        billMasterItem: masterItemObj
                                    });
                                }
                                billingManagementService.addBillPricings(billPricings).then(function () {
                                    $scope.calculatePrice();
                                });
                            });
                        };

                        $scope.paymentDialog = function () {
                            if (isEditOrder) {
                                paymentFormService.getPreviousPaymentDialogData($scope.newVisit.rid).then(function (response) {
                                    var total = $scope.total;
                                    var previousPaidAmount = response.data.previousPaidAmount;
                                    var prevGeneralDisPercentage = response.data.prevGeneralDisPercentage;
                                    var prevGeneralDisAmount = response.data.prevGeneralDisAmount;
                                    total = util.round(total - previousPaidAmount);
                                    if (total < 0) {
                                        total = 0;
                                    }
                                    var data =
                                    {
                                        isPartial: false,
                                        total: total,
                                        visit: $scope.newVisit,
                                        isEditOrder: isEditOrder,
                                        previousPaidAmount: previousPaidAmount,
                                        finishCallback: $scope.exitEmit,
                                        prevGeneralDisPercentage: prevGeneralDisPercentage,
                                        prevGeneralDisAmount: prevGeneralDisAmount,
                                        isInsured: $scope.insProviderPlan != null,
                                        successCallback: function () {
                                            //so we can show a finish button on wizard if user clicked pay then closed dialog
                                            $scope.didPay = true;
                                        },
                                        paymentWrapperList: $scope.paymentWrapperList
                                    }
                                    paymentFormService.paymentDialog(data);
                                });
                            } else {
                                var data =
                                {
                                    isPartial: false,
                                    total: $scope.total,
                                    visit: $scope.newVisit,
                                    isEditOrder: isEditOrder,
                                    finishCallback: $scope.exitEmit,
                                    isInsured: $scope.insProviderPlan != null,
                                    successCallback: function () {
                                        //so we can show a finish button on wizard if user clicked pay then closed dialog
                                        $scope.didPay = true;
                                    },
                                    paymentWrapperList: $scope.paymentWrapperList
                                }
                                paymentFormService.paymentDialog(data);
                            }
                        };

                        $scope.startEndDateListener = function (masterItem) {
                            if (masterItem.startDate > masterItem.endDate) {
                                var date = new Date(masterItem.startDate);
                                date.setDate(date.getDate() + 1);
                                masterItem.endDate = date;
                            }
                        }

                    }
                }]
        }
    });
});
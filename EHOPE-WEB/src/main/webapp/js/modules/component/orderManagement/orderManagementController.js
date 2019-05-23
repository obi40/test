define(['app', 'util', 'config', 'commonData'], function (app, util, config, commonData) {
    'use strict';
    app.controller('orderManagementCtrl',
        ['$scope', '$filter', '$state', '$rootScope', '$timeout', '$mdDialog', 'lovService', 'orderManagementService',
            'sampleSeparationService', 'patientProfileService', 'patientLookupService', 'testDefinitionManagementService',
            'paymentFormService', 'testResultEntryService', 'artifactService', 'orderFormService',
            function ($scope, $filter, $state, $rootScope, $timeout, $mdDialog, lovService, orderManagementService,
                sampleSeparationService, patientProfileService, patientLookupService, testDefinitionManagementService,
                paymentFormService, testResultEntryService, artifactService, orderFormService) {
                $scope.patternNum = config.regexpNum;
                $scope.selectedObject = null;
                $scope.operationStatusList = null;
                $scope.isCancelled = false;//is object cancelled?
                $scope.isClosed = false;//is object closed?
                $scope.isPaid = { value: false, isPartial: false };//is object paid?
                var paidStatus = null;
                var cancelledStatus = null;
                $scope.isTreeGrid = true;
                $scope.advancedSearch = false;
                var lastWeekDate = new Date();
                lastWeekDate.setDate(lastWeekDate.getDate() - 1);
                lastWeekDate.setHours(0, 0, 0, 0);// so we get day without time since we want to show the full day orders
                var currentDate = new Date();
                currentDate.setHours(0, 0, 0, 0);// so we get day without time since we want to show the full day orders
                $scope.emrPatientInfoType = "EmrPatientInfo";
                $scope.emrVisitType = "EmrVisit";
                $scope.labSampleType = "LabSample";
                $scope.labTestActualType = "LabTestActual";
                $scope.REQUESTED = commonData.operationStatus.REQUESTED;
                $scope.VALIDATED = commonData.operationStatus.VALIDATED;
                $scope.COLLECTED = commonData.operationStatus.COLLECTED;
                $scope.IN_PROGRESS = commonData.operationStatus.IN_PROGRESS;
                $scope.RESULTS_ENTERED = commonData.operationStatus.RESULTS_ENTERED;
                $scope.FINALIZED = commonData.operationStatus.FINALIZED;
                $scope.CANCELLED = commonData.operationStatus.CANCELLED;
                $scope.CLOSED = commonData.operationStatus.CLOSED;
                $scope.ABORTED = commonData.operationStatus.ABORTED;
                var timezoneOffset = new Date().getTimezoneOffset();
                var timezoneId = new Date().toString().split('(')[1].slice(0, -1).replace('Daylight', 'Standard');
                $scope.autoCompleteCriteria = $scope.emrPatientInfoType;
                $scope.selectedVisit = null;
                $scope.userLocale = util.userLocale;
                var searchType = null;
                $scope.visitTypeLkp = {
                    className: "LkpVisitType",
                    name: "visitType",
                    labelText: "visitType",
                    valueField: "name." + util.userLocale,
                    selectedValue: null,
                    required: false
                };
                $scope.filters = {
                    admissionNumber: null,
                    barcode: null,
                    nationalId: null,
                    firstName: null,
                    lastName: null,
                    mobileNo: null,
                    fileNo: null,
                    standardCode: null,
                    description: null,
                    aliases: null,
                    secondaryCode: null,
                    visitDateFrom: lastWeekDate,
                    visitDateTo: currentDate
                };
                var autoCompleteFilters = [];
                $scope.orderManagementSearchOptions = {
                    callback: function (filters) {
                        if (filters == null) {
                            autoCompleteFilters = null;
                            return;
                        }
                        autoCompleteFilters = null;
                        if (filters.length > 0) {
                            autoCompleteFilters = filters;
                        }
                        $scope.refreshGrids();
                    },
                    disabled: false
                };

                $scope.autoCompleteCriteriaListener = function () {
                    //Change auto complete service,etc to match the selected radio button.
                    //The following filterLists reflects what the query actually search on in the back end if
                    //user didnt find the specific record
                    delete $scope.orderManagementSearchOptions.dynamicLang;
                    if ($scope.autoCompleteCriteria == $scope.emrPatientInfoType) {
                        $scope.orderManagementSearchOptions.service = patientLookupService.getPatientPage;
                        $scope.orderManagementSearchOptions.dynamicLang = { code: true, description: true };
                        $scope.orderManagementSearchOptions.skeleton = { code: "fullName", description: "fullName", image: "image" };
                        $scope.orderManagementSearchOptions.filterList = ["firstName", "secondName", "thirdName", "lastName", "fullName", "nationalId", "mobileNo", "secondaryMobileNo", "fileNo"];
                        $scope.orderManagementSearchOptions.staticFilters = null;
                        searchType = "PATIENT";
                    } else if ($scope.autoCompleteCriteria == $scope.emrVisitType) {
                        $scope.orderManagementSearchOptions.service = patientProfileService.getVisitPage;
                        $scope.orderManagementSearchOptions.skeleton = { code: "admissionNumber", description: "admissionNumber" };
                        $scope.orderManagementSearchOptions.filterList = ["admissionNumber"];
                        $scope.orderManagementSearchOptions.staticFilters = null;
                        searchType = "VISIT";
                    } else if ($scope.autoCompleteCriteria == $scope.labSampleType) {
                        $scope.orderManagementSearchOptions.service = sampleSeparationService.getSamplePage;
                        $scope.orderManagementSearchOptions.skeleton = { code: "barcode", description: "barcode" };
                        $scope.orderManagementSearchOptions.filterList = ["barcode"];
                        $scope.orderManagementSearchOptions.staticFilters = null;
                        searchType = "SAMPLE";
                    } else {
                        $scope.orderManagementSearchOptions.service = testDefinitionManagementService.getTestDefinitionLookup;
                        $scope.orderManagementSearchOptions.skeleton = { code: "standardCode", description: "description" };
                        $scope.orderManagementSearchOptions.filterList = ["description", "standardCode", "aliases", "secondaryCode"];
                        $scope.orderManagementSearchOptions.staticFilters = [{ field: "isActive", value: true, operator: "eq" }];
                        searchType = "TEST";
                    }
                    //since we call this function when loading the page and it may not be initialized
                    if ($scope.orderManagementSearchOptions.reset) {
                        $scope.orderManagementSearchOptions.reset();
                    }

                };

                $scope.clearForm = function () {
                    for (var key in $scope.filters) {
                        $scope.filters[key] = null;
                        //in case we left any rid filters
                        if (key.endsWith("Rid")) {
                            delete $scope.filters[key];
                        }
                    }
                    $scope.visitTypeLkp.selectedValue = null;
                    $scope.selectedObject = null;
                    if ($scope.orderManagementForm != null) {
                        $scope.orderManagementForm.$setPristine();
                        $scope.orderManagementForm.$setUntouched();
                    }
                    $scope.orderManagementSearchOptions.reset();
                };
                $scope.viewSampleSeparation = function () {
                    sampleSeparationService.getVisitSampleSeparation($scope.selectedVisit.rid).then(function (response) {
                        $rootScope.sampleSeparationVisitRid = response.data.rid;
                        $state.go("sample-separation");
                    });
                };

                $scope.refundVisit = function () {
                    paymentFormService.getRefundInfo($scope.selectedVisit.rid).then(function (response) {
                        $mdDialog.show({
                            controller: ["$scope", "$mdDialog", "visit", "dialogCallback", "refundInfo",
                                function ($scope, $mdDialog, visit, dialogCallback, refundInfo) {
                                    $scope.visit = visit;
                                    $scope.payments = null;
                                    $scope.paymentMethodLkp = null;
                                    $scope.maxChequeAmount = null;
                                    $scope.isRefundDisabled = true;
                                    $scope.cancelledAmounts = refundInfo.cancelledAmounts;
                                    var previousPayments = refundInfo.payments;
                                    var hideCheque = true;
                                    var dummyRid = -1;
                                    for (var idx = 0; idx < previousPayments.length; idx++) {
                                        if (previousPayments[idx].lkpPaymentMethod.code === "CHEQUE" && $scope.cancelledAmounts >= previousPayments[idx].amount) {
                                            hideCheque = false;
                                            $scope.maxChequeAmount = previousPayments[idx].amount;
                                            break;
                                        }
                                    }
                                    lovService.getLkpByClass({ "className": "LkpPaymentMethod" }).then(function (data) {
                                        $scope.payments = [];
                                        var lkpData = [];
                                        for (var idx = 0; idx < data.length; idx++) {
                                            if (data[idx].code === "CASH" || data[idx].code === "VOUCHER") {
                                                $scope.payments.push(
                                                    {
                                                        rid: dummyRid--,
                                                        lkpPaymentMethod: data[idx],
                                                        amount: 0,
                                                        maxAmount: $scope.cancelledAmounts,
                                                        minAmount: 0
                                                    }
                                                );
                                                lkpData.push(data[idx]);
                                            } else if (data[idx].code === "CHEQUE" && !hideCheque) {
                                                $scope.payments.push(
                                                    {
                                                        rid: dummyRid--,
                                                        lkpPaymentMethod: data[idx],
                                                        amount: $scope.maxChequeAmount,
                                                        maxAmount: $scope.maxChequeAmount,
                                                        minAmount: $scope.maxChequeAmount
                                                    }
                                                );
                                                lkpData.push(data[idx]);
                                            }
                                        }
                                        $scope.paymentMethodLkp = {
                                            className: "LkpPaymentMethod",
                                            name: "lkpPaymentMethod",
                                            labelText: "paymentMethod",
                                            valueField: "name." + util.userLocale,
                                            selectedValue: null,
                                            required: true,
                                            data: lkpData
                                        };

                                    });

                                    $scope.$watch("payments", function () {
                                        if ($scope.payments == null || $scope.payments.length === 0) {
                                            $scope.isRefundDisabled = true;
                                            return;
                                        }
                                        var refundTotal = 0;
                                        for (var idx = 0; idx < $scope.payments.length; idx++) {
                                            if (isNaN($scope.payments[idx].amount)) {
                                                continue;
                                            }
                                            refundTotal += $scope.payments[idx].amount;
                                        }
                                        if (refundTotal === 0 || $scope.cancelledAmounts < refundTotal) {
                                            $scope.isRefundDisabled = true;
                                            return;
                                        }
                                        $scope.isRefundDisabled = false;
                                    }, true);

                                    $scope.refundPayment = function () {
                                        var tempPayments = angular.copy($scope.payments);
                                        var length = tempPayments.length - 1;
                                        for (var idx = length; idx >= 0; idx--) {
                                            if (!isNaN(tempPayments[idx].amount) && tempPayments[idx].amount <= 0) {
                                                tempPayments.splice(idx, 1);
                                            }
                                        }

                                        var map = {
                                            patientVisit: $scope.visit,
                                            testPaymentList: tempPayments
                                        };
                                        paymentFormService.refundPayment(map).then(function () {
                                            util.createToast(util.systemMessages.success, "success");
                                            $scope.cancel();
                                            dialogCallback();
                                        });
                                    };

                                    $scope.cancel = function () {
                                        $mdDialog.cancel();
                                    };
                                }],
                            templateUrl: './' + config.lisDir + '/modules/dialogs/refund-visit.html',
                            parent: angular.element(document.body),
                            targetEvent: event,
                            clickOutsideToClose: true,
                            locals: {
                                visit: $scope.selectedVisit,
                                dialogCallback: $scope.refreshGrids,
                                refundInfo: response.data
                            }
                        }).then(function () {
                        }, function () {
                        });
                    });
                };

                $scope.editVisit = function () {
                    orderFormService.fetchVisit($scope.selectedVisit.rid).then(function () {
                        $scope.selectedVisit.emrPatientInfo.isInsurer = false;
                        $rootScope.currentPatient = $scope.selectedVisit.emrPatientInfo;
                        $rootScope.visitRid = $scope.selectedVisit.rid;
                        $state.go("patient-profile-wizard");
                    });
                };
                $scope.createVisit = function () {
                    $scope.selectedVisit.emrPatientInfo.isInsurer = false;
                    $rootScope.currentPatient = $scope.selectedVisit.emrPatientInfo;
                    $state.go("patient-profile-wizard");
                };
                //Hyperlink in grid
                $scope.viewVisit = function (visitRid) {
                    var key = null;
                    var data = [];
                    if ($scope.isTreeGrid) {
                        data = paginationTreeGridDataSource.data();
                    } else {
                        key = "emrVisit";
                        data = gridDataSource.data();
                    }
                    $rootScope.currentPatient = null;
                    for (var idx = 0; idx < data.length; idx++) {
                        var obj = data[idx];
                        if (key != null) {
                            obj = obj[key];
                        }
                        if (obj.rid == visitRid) {
                            $rootScope.orderToSelect = obj;// to pre select
                            $rootScope.patientProfileMode = "view"; // directive mode
                            $rootScope.patientTabToSelect = 2;// "Orders" tab
                            $rootScope.currentPatient = obj.emrPatientInfo;
                            $rootScope.orderHyperLink = obj.admissionNumber;
                            break;
                        }
                    }
                };
                var wrapper = {
                    "className": "LkpOperationStatus",
                    "filterablePageRequest": {
                        "filters": [
                            {
                                "field": "code",
                                "value": $scope.ABORTED,
                                "operator": "neq"
                            },
                            {
                                "field": "code",
                                "value": $scope.CLOSED,
                                "operator": "neq"
                            }
                        ],
                        "sortList": [
                            {
                                "direction": "ASC",
                                "property": "arrangement"
                            }
                        ]
                    }
                };
                lovService.getAnyLkpByClass(wrapper).then(function (response) {
                    var data = response.data;
                    cancelledStatus = {
                        idx: -1,// to be able to remove the cancelled from the data to re push it
                        data: null,
                        code: $scope.CANCELLED
                    };
                    //this is not a status from the LkpOperationStatuses
                    paidStatus = {
                        code: "PAID",
                        data: null,
                        icon: "fas fa-file-invoice-dollar",
                        name: {},
                        style: [],
                        authority: "ADD_PAYMENT",
                        current: false
                    };
                    paidStatus.name[util.userLocale] = util.systemMessages.payment;
                    for (var idx = 0; idx < data.length; idx++) {
                        data[idx]["data"] = null;
                        data[idx]["authority"] = data[idx].code + "_OPERATION_STATUS";
                        data[idx]["style"] = [];
                        data[idx]["current"] = false;
                        switch (data[idx].code) {
                            case $scope.REQUESTED: data[idx]["icon"] = "fas fa-tasks"; break;
                            case $scope.VALIDATED: data[idx]["icon"] = "fas fa-check"; break;
                            case $scope.COLLECTED: data[idx]["icon"] = "fas fa-vial"; break;
                            case $scope.IN_PROGRESS: data[idx]["icon"] = "fas fa-spinner"; break;
                            case $scope.RESULTS_ENTERED: data[idx]["icon"] = "fas fa-keyboard"; break;
                            case $scope.FINALIZED: data[idx]["icon"] = "fas fa-clipboard-check"; break;
                            case $scope.CANCELLED:
                                data[idx]["icon"] = "fas fa-ban";
                                cancelledStatus.idx = idx;
                                cancelledStatus.data = data[idx];
                                break;
                        }
                    }
                    data.splice(cancelledStatus.idx, 1);//remove the cancelled 
                    data.splice(0, 0, cancelledStatus.data);//make cancelled first element
                    data.push(paidStatus);
                    $scope.operationStatusList = data;
                });
                function payment() {
                    if ($scope.selectedVisit == null || $scope.isPaid.value) {//visit is paid fully
                        return;
                    }
                    function finishCallback() {
                        $scope.refreshGrids();
                    }
                    // we treat all as partials
                    var data = {
                        isPartial: true,
                        total: ($scope.selectedVisit.totalAmount - $scope.selectedVisit.paidAmount),
                        visit: $scope.selectedVisit,
                        finishCallback: finishCallback,
                        isEditOrder: false
                    };
                    paymentFormService.paymentDialog(data);
                };
                function resultsEntryDialog(toFinalize) {
                    var filterResultEntry = {
                        rid: $scope.selectedObject.rid,
                        fetchedStatus: toFinalize ? $scope.RESULTS_ENTERED : null//fetch these tests with this operation status
                    };
                    if ($scope.selectedObject.type == $scope.emrVisitType) {
                        filterResultEntry["type"] = null;//we already fetch the whole visit
                    } else if ($scope.selectedObject.type == $scope.labSampleType) {
                        filterResultEntry["type"] = "sample";
                    } else {
                        filterResultEntry["type"] = "test";
                    }
                    $mdDialog.show({
                        controller: ["$scope", "$mdDialog", "visit", "filterResultEntry",
                            function ($scope, $mdDialog, visit, filterResultEntry) {
                                $scope.noEdit = toFinalize;
                                $scope.visit = visit;
                                $scope.filterResultEntry = filterResultEntry;
                                $scope.dialogAction = {
                                    dialog: $mdDialog,
                                    type: toFinalize ? "finalize" : "save"
                                };
                                $scope.cancel = function () {
                                    $mdDialog.cancel();
                                };
                                $scope.save = function () {
                                    $scope.dialogAction.submit();
                                }
                                $scope.reorderTests = function () {
                                    $scope.dialogAction.reorderTests();
                                }
                            }],
                        templateUrl: './' + config.lisDir + '/modules/dialogs/test-result-entry-pop.html',
                        parent: angular.element(document.body),
                        clickOutsideToClose: false,
                        locals: {
                            visit: $scope.selectedVisit,
                            filterResultEntry: filterResultEntry
                        }
                    }).then(function () {
                        $scope.refreshGrids();//refresh grid after user clicked outside popup or close(...)
                        $scope.generateResultsReport();
                    }, function () {
                        //cancel (failure) event
                        $scope.refreshGrids()
                    });
                }

                $scope.checkSampleStatus = function () {
                    if ($scope.selectedObject == null || $scope.selectedObject.lkpOperationStatus.code == $scope.CANCELLED
                        || $scope.selectedObject.lkpOperationStatus.code == $scope.REQUESTED
                        || $scope.selectedObject.lkpOperationStatus.code == $scope.VALIDATED) {
                        return true;
                    } else {
                        return false;
                    }
                }

                $scope.amendResults = function () {
                    resultsEntryDialog(false);
                }

                $scope.changeStatus = function (status) {
                    //If clicked on the same status then do nothing
                    // for (var i = 0; i < $scope.operationStatusList.length; i++) {
                    //     if ($scope.operationStatusList[i].current == true && $scope.operationStatusList[i].code == status.code) {
                    //         return;
                    //     }
                    // }
                    if ($scope.selectedObject == null) {
                        return;
                    }
                    if ($scope.isCancelled || $scope.isClosed || status.code == $scope.REQUESTED) {
                        return;
                    }
                    if (status.code == paidStatus.code) {
                        payment();
                        return;
                    }
                    //show dialog only if the visit has samples in it or hte sample has a tests in it,also enable for all tests
                    if (status.code == $scope.RESULTS_ENTERED &&
                        ($scope.selectedObject.type == $scope.labTestActualType ||
                            (($scope.selectedObject.type == $scope.emrVisitType && $scope.selectedObject.labSamples != null && $scope.selectedObject.labSamples.length > 0)) ||
                            (($scope.selectedObject.type == $scope.labSampleType && $scope.selectedObject.labTestActualSet != null && $scope.selectedObject.labTestActualSet.length > 0)))) {
                        resultsEntryDialog(false);
                        return;
                    }
                    // if (status.code == $scope.COLLECTED && $scope.selectedObject.type == $scope.emrVisitType
                    //     && $scope.selectedObject.labSamples != null && $scope.selectedObject.labSamples.length > 0) {

                    //     var samplesRid = {};
                    //     for (var idx = 0; idx < $scope.selectedObject.labSamples.length; idx++) {
                    //         samplesRid[$scope.selectedObject.labSamples[idx].rid] = [];
                    //     }
                    //     sampleSeparationService.sendToMachine($scope.selectedObject.rid, samplesRid).then(function () {
                    //         util.createToast(util.systemMessages.success, "success");
                    //         $scope.refreshGrids();
                    //     });
                    //     return;
                    // }
                    if (status.code == cancelledStatus.code) {
                        cancelDialog(status);
                        return;
                    }
                    if (status.code == $scope.FINALIZED) {
                        var wrapper = {
                            visitRid: $scope.selectedVisit.rid,
                            fetchedStatus: $scope.RESULTS_ENTERED
                        };
                        //check wither if there are any results for the fetched tests otherwise change the status
                        testResultEntryService.getTestActualListWithResultsByVisit(wrapper).then(function (response) {
                            var hasResults = false;
                            var data = response.data;
                            for (var i = 0; i < data.length; i++) {
                                var testActual = data[i];
                                if (testActual.labTestActualResults.length > 0) {
                                    hasResults = true;
                                }
                            }
                            if (hasResults) {
                                resultsEntryDialog(true);
                            }
                        });
                        return;
                    }
                    //Other statuses 
                    changeObjectStatus($scope.selectedObject.rid, $scope.selectedObject.type, status.code, null);
                };
                function changeObjectStatus(objectRid, objectType, statusCode, cancelReason) {
                    var wrapper = {
                        rid: objectRid,
                        operationStatus: statusCode,
                        type: objectType,
                        visitRid: $scope.selectedVisit.rid
                    };
                    if (cancelReason != null && cancelReason != "") {
                        wrapper["comment"] = cancelReason;
                    }
                    return orderManagementService.changeVisitSampleTestStatus(wrapper).then(function () {
                        util.createToast(util.systemMessages.success, "success");
                        $scope.refreshGrids();
                    });
                }
                $scope.populateStatusChain = function () {
                    if ($scope.selectedObject == null || $scope.selectedObject.rid == null) {
                        return;
                    }
                    var wrapper = {
                        type: $scope.selectedObject.type,
                        filterablePageRequest: {
                            "filters": [{
                                "field": $scope.selectedObject.type.charAt(0).toLowerCase() + $scope.selectedObject.type.slice(1) + ".rid",
                                "value": $scope.selectedObject.rid,
                                "operator": "eq"
                            }],
                            "sortList": [{ "direction": "ASC", "property": "operationDate" }]
                        }
                    };
                    orderManagementService.getVisitSampleTestHistory(wrapper).then(function (response) {
                        populateObjectStatus(response.data);
                    });
                };
                $scope.blurStatus = function (event) {
                    // to blur the status after mouse leaving it, fixing a ui bug
                    $(event.target).blur();
                };

                $scope.generateResultsReport = function () {
                    patientProfileService.resultsReportDialog($scope.selectedVisit.rid);
                };
                function cancelDialog(status) {
                    if ($scope.selectedObject == null) {
                        return;
                    }
                    $mdDialog.show({
                        controller: ["$scope", "$mdDialog", "selectedObject", "status",
                            function ($scope, $mdDialog, selectedObject, status) {
                                $scope.reason = null;
                                $scope.submit = function () {
                                    changeObjectStatus(selectedObject.rid, selectedObject.type, status.code, $scope.reason).then(function () {
                                        $scope.cancel();
                                    });
                                };
                                $scope.cancel = function () {
                                    $mdDialog.cancel();
                                };
                            }],
                        templateUrl: './' + config.lisDir + '/modules/dialogs/operation-cancel-reason.html',
                        parent: angular.element(document.body),
                        targetEvent: event,
                        clickOutsideToClose: true,
                        locals: {
                            selectedObject: $scope.selectedObject,
                            status: status
                        }
                    }).then(function () {
                    }, function () {
                        $scope.refreshGrids();//refresh grid after user clicked outside popup or close(...)
                    });
                }

                //print sample barcode
                $scope.printSample = function () {
                    if ($scope.selectedObject.type == $scope.emrVisitType) {
                        $scope.visitInformation = {
                            visitRid: $scope.selectedVisit.rid,
                            timezoneOffset: timezoneOffset,
                            timezoneId: timezoneId,
                        };

                        sampleSeparationService.printAllSamples($scope.visitInformation).then(function (response) {
                            util.fileHandler(response.data, { type: "application/pdf", name: commonData.reportNames.sample });
                        });
                    } else if ($scope.selectedObject.type == $scope.labSampleType) {
                        $scope.sampleInformation = {
                            sampleRid: $scope.selectedObject.rid,
                            timezoneOffset: timezoneOffset,
                            timezoneId: timezoneId,
                        };

                        sampleSeparationService.printSample($scope.sampleInformation).then(function (response) {
                            util.fileHandler(response.data, { type: "application/pdf", name: commonData.reportNames.sample });
                        });
                    } else {
                        $scope.sampleInformation = {
                            sampleRid: $scope.selectedObject.labSample.rid,
                            timezoneOffset: timezoneOffset,
                            timezoneId: timezoneId,
                        };

                        sampleSeparationService.printSample($scope.sampleInformation).then(function (response) {
                            util.fileHandler(response.data, { type: "application/pdf", name: commonData.reportNames.sample });
                        });
                    }
                };
                $scope.generateInvoiceReport = function () {
                    $scope.invoiceInfo = {
                        visitRid: $scope.selectedVisit.rid,
                        timezoneOffset: timezoneOffset,
                        timezoneId: timezoneId,
                    };
                    patientProfileService.printInvoiceReport($scope.invoiceInfo).then(function (response) {
                        var fileName = $scope.selectedVisit.emrPatientInfo.fullName[util.userNamePrimary] + "-" + $scope.selectedVisit.admissionNumber; //commonData.reportNames.results
                        util.fileHandler(response.data, { type: "application/pdf", name: fileName });
                    });
                };
                $scope.generateInsuranceInvoiceReport = function () {
                    $scope.insuranceInvoiceInfo = {
                        visitRid: $scope.selectedVisit.rid,
                        timezoneOffset: timezoneOffset,
                        timezoneId: timezoneId
                    };
                    patientProfileService.printInsuranceInvoiceReport($scope.insuranceInvoiceInfo)
                        .then(function (response) {
                            util.fileHandler(response.data, { type: "application/pdf", name: commonData.reportNames.insuranceInvoice });
                        });
                };
                //print sample worksheet
                $scope.printWorksheet = function () {
                    var fileName = $scope.selectedVisit.emrPatientInfo.fullName[util.userNamePrimary] + "-" + $scope.selectedVisit.admissionNumber; //commonData.reportNames.results

                    if ($scope.selectedObject.type == $scope.emrVisitType) {
                        $scope.visitInformation = {
                            visitRid: $scope.selectedVisit.rid,
                            timezoneOffset: timezoneOffset,
                            timezoneId: timezoneId,
                        };

                        sampleSeparationService.printAllWorksheets($scope.visitInformation).then(function (response) {
                            util.fileHandler(response.data, { type: "application/pdf", name: fileName });
                        });
                    } else if ($scope.selectedObject.type == $scope.labSampleType) {
                        $scope.sampleInformation = {
                            sampleRid: $scope.selectedObject.rid,
                            timezoneOffset: timezoneOffset,
                            timezoneId: timezoneId,
                        };

                        sampleSeparationService.printSampleWorksheet($scope.sampleInformation).then(function (response) {
                            util.fileHandler(response.data, { type: "application/pdf", name: fileName });
                        });
                    } else {
                        $scope.sampleInformation = {
                            sampleRid: $scope.selectedObject.labSample.rid,
                            timezoneOffset: timezoneOffset,
                            timezoneId: timezoneId,
                        };

                        sampleSeparationService.printSampleWorksheet($scope.sampleInformation).then(function (response) {
                            util.fileHandler(response.data, { type: "application/pdf", name: fileName });
                        });
                    }
                };

                $scope.showArtifactDialog = function () {
                    switch ($scope.selectedObject.type) {
                        case $scope.emrVisitType:
                            artifactService.showArtifactDialog($scope.selectedVisit, "order");
                            break;
                        case $scope.labTestActualType:
                            artifactService.showArtifactDialog($scope.selectedObject, "actualTest");
                            break;
                    }
                }

                function populateObjectStatus(data) {
                    if ($scope.selectedObject == null) {
                        return;
                    }
                    //reset
                    $scope.isCancelled = false;
                    $scope.isClosed = false;
                    $scope.isPaid = { value: false, isPartial: false };
                    for (var idx = 0; idx < $scope.operationStatusList.length; idx++) {
                        $scope.operationStatusList[idx].data = null;
                        $scope.operationStatusList[idx].style = [];
                        $scope.operationStatusList[idx].current = false;
                    }
                    // To know if the visit is paid or not
                    var totalAmount = $scope.selectedVisit.totalAmount;
                    var remainingAmount = totalAmount - $scope.selectedVisit.paidAmount;
                    if ($scope.selectedVisit != null) {
                        $scope.isPaid.value = remainingAmount <= 0;
                        if (!$scope.isPaid.value) {
                            $scope.isPaid.isPartial = $scope.selectedVisit.paidAmount != null && $scope.selectedVisit.paidAmount > 0;
                        }
                    }
                    // To know if the status is cancelled and to get the last status rid to show it on ui
                    var lastStatusRid = -1;
                    for (var idx = 0; idx < data.length; idx++) {
                        if (data[idx].newOperationStatus.code == $scope.CLOSED) {
                            $scope.isClosed = true;
                        }
                        if (data[idx].newOperationStatus.code == cancelledStatus.code) {
                            $scope.isCancelled = true;
                            lastStatusRid = data[idx - 1].rid;
                            break;
                        } else if (idx + 1 == data.length) {
                            lastStatusRid = data[idx].rid;
                        }
                    }
                    //applying the current status,paid is excluded since it does not have rid and does not exit in the data
                    for (var idx = 0; idx < data.length; idx++) {
                        for (var i = 0; i < $scope.operationStatusList.length; i++) {
                            if (data[idx].newOperationStatus.rid == $scope.operationStatusList[i].rid) {
                                $scope.operationStatusList[i].data = data[idx];
                                if (data[idx].rid == lastStatusRid) {
                                    $scope.operationStatusList[i].current = true;
                                    $scope.operationStatusList[i].style.push("current-status");
                                }
                                break;
                            }
                        }
                    }
                    // applying classes
                    for (var idx = 0; idx < $scope.operationStatusList.length; idx++) {
                        var obj = $scope.operationStatusList[idx];
                        //special classes
                        if (obj.code == paidStatus.code) {
                            obj.data = {
                                totalAmount: totalAmount,
                                remainingAmount: remainingAmount < 0 ? 0 : remainingAmount//if payed more then make it 0
                            };
                            if ($scope.isPaid.value) {
                                obj.style.push("paid-fully-color");
                            } else {
                                if (!$scope.isCancelled) {
                                    obj.style.push("clickable-item");
                                }
                                if ($scope.isPaid.isPartial) {
                                    obj.style.push("paid-partially-color");
                                } else {
                                    obj.style.push("paid-none-color");
                                }
                            }

                        } else {
                            if ($scope.isCancelled && obj.code == cancelledStatus.code) {
                                obj.style.push("cancelled-status-color");
                            }
                            if (!$scope.isCancelled && !$scope.isClosed && obj.code != $scope.REQUESTED) {
                                obj.style.push("clickable-item");
                            }
                        }
                        obj.style = obj.style.join(" ");
                    }

                }
                $scope.search = function () {
                    $scope.selectedObject = null;
                    $scope.selectedVisit = null;
                    if ($scope.isTreeGrid) {
                        paginationTreeGridDataSource.page(1);//reset
                    } else if (!$scope.isTreeGrid) {
                        gridDataSource.read();
                    }
                };

                function getValue(object, field, value) {
                    //get the "field" directly from the "object" or use the "value" to get the object data
                    if (object[field]) {
                        return value != null ? (util.getDeepValueInObj(object, field + "." + value)) : object[field];
                    }
                    return null;
                }

                function getUserFilters() {
                    //null means no auto complete filter
                    var typeFilter = {
                        field: "type",
                        value: searchType
                    };
                    var result = [];
                    var tempFilters = [];
                    if (autoCompleteFilters != null && autoCompleteFilters.length > 0) {
                        tempFilters = angular.copy(autoCompleteFilters);
                        if (tempFilters.length === 1 && tempFilters[0].field === "rid") {
                            tempFilters[0].value = tempFilters[0].value.toString();//cast to string for easier back end conversion to long
                            switch ($scope.autoCompleteCriteria) {//set rid depending on the radio selection
                                case $scope.emrPatientInfoType: tempFilters[0].field = "patientRid"; break;
                                case $scope.emrVisitType: tempFilters[0].field = "visitRid"; break;
                                case $scope.labSampleType: tempFilters[0].field = "sampleRid"; break;
                                case $scope.labTestActualType: tempFilters[0].field = "testRid"; break;
                            }
                            result.push(tempFilters[0]);
                        } else {
                            for (var idx = 0; idx < tempFilters.length; idx++) {
                                var obj = tempFilters[idx];
                                obj.value = (typeof obj.value === "string") ? obj.value.toLowerCase() : obj.value + "";
                                //since this will be used by the generic query creator , change field name to match the required field
                                if ($scope.isTreeGrid) {//visit fetch
                                    if (searchType === "PATIENT") {
                                        obj.field = "emrPatientInfo." + obj.field;
                                    }
                                }

                                result.push(obj);
                            }
                        }
                        if ($scope.filters.visitDateFrom) {
                            result.push({
                                "field": "visitDateFrom",
                                "value": $scope.filters.visitDateFrom,
                                "operator": "gte",
                                "junctionOperator": "And"
                            });
                        }
                        if ($scope.filters.visitDateTo) {
                            result.push({
                                "field": "visitDateTo",
                                "value": $scope.filters.visitDateTo,
                                "operator": "lte",
                                "junctionOperator": "And"
                            });
                        }
                    } else {
                        typeFilter.value = null;
                        tempFilters = angular.copy($scope.filters);
                        for (var key in tempFilters) {
                            //skip null values
                            if (tempFilters[key] == null || tempFilters[key] == "") {
                                continue;
                            }
                            //lower case the string values
                            var value = typeof tempFilters[key] === "string" ? tempFilters[key].toLowerCase() : tempFilters[key];
                            var filter = {
                                field: key,
                                value: value
                            };
                            result.push(filter);
                        }
                    }
                    result.push(typeFilter);
                    //Add lkps
                    if ($scope.visitTypeLkp.selectedValue != null) {
                        result.push({
                            field: "visitTypeCode",
                            value: $scope.visitTypeLkp.selectedValue.code.toLowerCase()
                        });
                    }
                    return result;
                }
                $scope.toggleGrid = function () {
                    $scope.selectedObject = null;
                    $scope.selectedVisit = null;
                    $scope.isTreeGrid = !$scope.isTreeGrid;
                    $scope.refreshGrids();
                };
                $scope.toggleAdvSearch = function () {
                    $scope.advancedSearch = !$scope.advancedSearch;
                    if ($scope.advancedSearch) {
                        autoCompleteFilters = null;
                    } else {
                        $scope.filters = {
                            admissionNumber: null,
                            barcode: null,
                            nationalId: null,
                            firstName: null,
                            lastName: null,
                            mobileNo: null,
                            fileNo: null,
                            standardCode: null,
                            description: null,
                            aliases: null,
                            visitDateFrom: $scope.filters.visitDateFrom,
                            visitDateTo: $scope.filters.visitDateTo
                        };
                        $scope.visitTypeLkp.selectedValue = null;
                    }
                };

                $scope.refreshGrids = function () {
                    if ($scope.isTreeGrid) {
                        paginationTreeGridDataSource.read();
                    } else if (!$scope.isTreeGrid) {
                        gridDataSource.read();
                    }
                };
                function onGridChangeListener(e) {
                    $scope.selectedObject = e.sender.dataItem(e.sender.select());
                    if ($scope.selectedObject != null) {
                        $scope.selectedVisit = $scope.selectedObject.emrVisit ? $scope.selectedObject.emrVisit : $scope.selectedObject;
                        $scope.populateStatusChain();
                    } else {
                        $scope.selectedVisit = null;
                    }
                }
                // we paginate the data from another datasource then set it to the tree grid
                var treeData = null;
                var expandedObj = null;
                var treeGridDataSource = new kendo.data.TreeListDataSource({
                    transport: {
                        read: function (e) {
                            if (treeData == null) {
                                e.success([]);
                                return;
                            }
                            //if we requested to expand a row
                            if (e.data.id && expandedObj != null) {
                                if (expandedObj.type == $scope.emrVisitType) {
                                    orderManagementService.getSamplesByVisitOrderManagement(expandedObj.rid).then(function (response) {
                                        e.success(response.data);
                                    });
                                } else if (expandedObj.type == $scope.labSampleType) {
                                    orderManagementService.getTestsBySampleOrderManagement(expandedObj.rid).then(function (response) {
                                        e.success(response.data);
                                    });
                                }
                            } else {
                                expandedObj = null;
                                e.success(treeData);
                            }
                        }
                    },
                    schema: {
                        parse: function (data) {
                            //parsing the data depending on the exapnded row 
                            if (data.length < 1) {
                                return data;
                            }
                            var result = [];
                            //we didn't expand
                            if (expandedObj == null) {
                                for (var idx = 0; idx < data.length; idx++) {
                                    var visit = data[idx];
                                    visit["parentRid"] = null;
                                    visit["hasChildren"] = visit.labSamples != null && visit.labSamples.length > 0;
                                    //columns data
                                    for (var key in visit.emrPatientInfo.fullName) {
                                        visit[("patientName_" + key)] = visit.emrPatientInfo.fullName[key];
                                    }
                                    visit["descriptionLocale"] = "";
                                    visit["lkpOperationStatusLocale"] = getValue(visit, "lkpOperationStatus", ("name." + util.userLocale));
                                    visit["visitDateLocale"] = $filter("dateTimeFormat")(getValue(visit, "visitDate"));// jackson will throw an error if we modified the visitDate itself
                                    visit["visitTypeLocale"] = getValue(visit, "visitType", ("name." + util.userLocale));
                                    visit["sectionLocale"] = "";
                                    visit["fileNo"] = getValue(visit, "emrPatientInfo", "fileNo");
                                    visit["age"] = getValue(visit, "emrPatientInfo", "age");
                                    visit["sex"] = getValue(visit, "emrPatientInfo", ("gender.name." + util.userLocale));
                                    visit["doctor"] = getValue(visit, "doctor", ("name." + util.userLocale));
                                    visit["mobileNo"] = getValue(visit, "emrPatientInfo", "mobileNo");
                                    visit["branchLocale"] = getValue(visit, "branch", ("name." + util.userLocale));
                                    visit["normalRangeText"] = "";
                                    //extra data to be used when doing some api or js functions
                                    visit["type"] = $scope.emrVisitType;
                                    result.push(visit);
                                }
                            } else if (expandedObj.type == $scope.emrVisitType) {//expanded a visit
                                for (var i = 0; i < data.length; i++) {
                                    var labSample = data[i];
                                    labSample["parentRid"] = expandedObj.rid;
                                    labSample["hasChildren"] = labSample.labTestActualSet != null && labSample.labTestActualSet.length > 0;
                                    //columns data
                                    labSample["descriptionLocale"] = "";
                                    labSample["lkpOperationStatusLocale"] = getValue(labSample, "lkpOperationStatus", ("name." + util.userLocale));
                                    labSample["visitDate"] = "";
                                    labSample["sectionLocale"] = "";
                                    labSample["fileNo"] = "";
                                    labSample["age"] = "";
                                    labSample["sex"] = "";
                                    labSample["doctor"] = "";
                                    labSample["mobileNo"] = "";
                                    labSample["branchLocale"] = "";
                                    labSample["normalRangeText"] = "";
                                    //extra data to be used when doing some api or js functions
                                    labSample["type"] = $scope.labSampleType;
                                    labSample["emrPatientInfo"] = expandedObj.emrPatientInfo;
                                    labSample["emrVisit"] = expandedObj;
                                    result.push(labSample);
                                }
                            } else if (expandedObj.type == $scope.labSampleType) {//expanded a sample
                                for (var i = 0; i < data.length; i++) {
                                    var labTestActual = data[i];
                                    labTestActual["parentRid"] = expandedObj.rid;
                                    labTestActual["hasChildren"] = false;
                                    //columns data
                                    labTestActual["descriptionLocale"] = getValue(labTestActual, "testDefinition", "description");
                                    labTestActual["lkpOperationStatusLocale"] = getValue(labTestActual, "lkpOperationStatus", ("name." + util.userLocale));
                                    labTestActual["visitDate"] = "";
                                    labTestActual["sectionLocale"] = getValue(labTestActual, "testDefinition", ("section.name." + util.userLocale));
                                    labTestActual["fileNo"] = "";
                                    labTestActual["age"] = "";
                                    labTestActual["sex"] = "";
                                    labTestActual["doctor"] = "";
                                    labTestActual["mobileNo"] = "";
                                    labTestActual["branchLocale"] = "";
                                    labTestActual["normalRangeText"] = getValue(labTestActual, "testDefinition", "normalRangeText");
                                    //extra data to be used when doing some api or js functions
                                    labTestActual["emrVisit"] = expandedObj.emrVisit;
                                    labTestActual["emrPatientInfo"] = expandedObj.emrPatientInfo;
                                    labTestActual["type"] = $scope.labTestActualType;
                                    result.push(labTestActual);
                                }
                            }
                            return result;
                        },
                        model: {
                            id: "rid",
                            parentId: "parentRid",
                            fields: {
                                "rid": { type: "number" },
                                "parentRid": { type: "number", nullable: true },
                                "admissionNumber": { type: "string" },
                                "descriptionLocale": { type: "string" },
                                "lkpOperationStatusLocale": { type: "string" },
                                "visitDateLocale": { type: "string" },
                                "visitTypeLocale": { type: "string" },
                                "sectionLocale": { type: "string" },
                                "fileNo": { type: "string" },
                                "age": { type: "number" },
                                "sex": { type: "string" },
                                "doctor": { type: "string" },
                                "mobileNo": { type: "string" },
                                "branchLocale": { type: "string" },
                                "normalRangeText": { type: "string" }
                            },
                            expanded: false
                        }
                    }
                });
                var orderManagementTreeGridColumns = [
                    {
                        field: "admissionNumber",
                        title: util.systemMessages.orderNumber,
                        template: function (dataItem) {
                            var value = "";
                            if (dataItem.type == $scope.emrVisitType) {
                                value = util.systemMessages.order + ": " + '<a href="patient-profile" ng-click="viewVisit(' + dataItem.rid + ')">' + getValue(dataItem, "admissionNumber") + '</a>';
                            } else if (dataItem.type == $scope.labSampleType) {
                                value = util.systemMessages.sample;
                                if (dataItem.lkpContainerType != null) {
                                    value += "/" + dataItem.lkpContainerType.name[util.userLocale];
                                }
                                value += ": " + dataItem.barcode;
                            } else if (dataItem.type == $scope.labTestActualType) {
                                value = util.systemMessages.test + ": " + dataItem.testDefinition.standardCode;
                            }
                            return value;
                        },
                        expandable: true
                    },
                    {
                        field: "descriptionLocale",
                        title: util.systemMessages.description
                    },
                    {
                        field: "lkpOperationStatusLocale",
                        title: util.systemMessages.operationStatus
                    },
                    {
                        field: "visitDateLocale",
                        title: util.systemMessages.orderDate
                    },
                    {
                        field: "sectionLocale",
                        title: util.systemMessages.section
                    },
                    {
                        field: "visitTypeLocale",
                        title: util.systemMessages.visitType,
                        hidden: true
                    },
                    {
                        field: "fileNo",
                        title: util.systemMessages.fileNumber,
                        hidden: true
                    },
                    {
                        field: "age",
                        title: util.systemMessages.age,
                        hidden: true
                    },
                    {
                        field: "sex",
                        title: util.systemMessages.sex,
                        hidden: true
                    },
                    {
                        field: "doctor",
                        title: util.systemMessages.doctor,
                        hidden: true
                    },
                    {
                        field: "mobileNo",
                        title: util.systemMessages.mobileNumber,
                        hidden: true
                    },
                    {
                        field: "branchLocale",
                        title: util.systemMessages.branch,
                        hidden: true
                    },
                    {
                        field: "normalRangeText",
                        title: util.systemMessages.normalRangeText,
                        hidden: true
                    }
                ];
                var orderManagementGridColumns = [
                    {
                        field: "admissionNumber",
                        title: util.systemMessages.orderNumber,
                        template: function (dataItem) {
                            return '<a href="patient-profile" ng-click="viewVisit(' + dataItem.emrVisit.rid + ')">' + dataItem.emrVisit.admissionNumber + '</a>';
                        }
                    },
                    {
                        field: "barcode",
                        title: util.systemMessages.barcode
                    },
                    {
                        field: "standardCodeLocale",
                        title: util.systemMessages.standardCode
                    },
                    {
                        field: "descriptionLocale",
                        title: util.systemMessages.description
                    },
                    {
                        field: "lkpOperationStatusLocale",
                        title: util.systemMessages.operationStatus
                    },
                    {
                        field: "visitDateLocale",
                        title: util.systemMessages.orderDate
                    },
                    {
                        field: "sectionLocale",
                        title: util.systemMessages.section
                    },
                    {
                        field: "visitTypeLocale",
                        title: util.systemMessages.visitType,
                        hidden: true
                    },
                    {
                        field: "fileNo",
                        title: util.systemMessages.fileNumber,
                        hidden: true
                    },
                    {
                        field: "age",
                        title: util.systemMessages.age,
                        hidden: true
                    },
                    {
                        field: "sex",
                        title: util.systemMessages.sex,
                        hidden: true
                    },
                    {
                        field: "doctor",
                        title: util.systemMessages.doctor,
                        hidden: true
                    },
                    {
                        field: "mobileNo",
                        title: util.systemMessages.mobileNumber,
                        hidden: true
                    },
                    {
                        field: "branchLocale",
                        title: util.systemMessages.branch,
                        hidden: true
                    },
                    {
                        field: "normalRangeText",
                        title: util.systemMessages.normalRangeText,
                        hidden: true
                    }
                ];
                var languages = angular.copy(util.user.tenantLanguages);
                languages.sort(function (a, b) { return (a.isPrimary === b.isPrimary) ? 0 : a.isPrimary ? -1 : 1 });//primary first
                for (var objKey in languages) {
                    var obj = languages[objKey];
                    var column =
                    {
                        field: ("patientName_" + obj.comLanguage.locale),
                        title: $filter("translator")("name", obj.comLanguage.locale)
                    };

                    orderManagementTreeGridColumns.splice(1 + parseInt(objKey), 0, column);
                    orderManagementGridColumns.splice(2 + parseInt(objKey), 0, column);
                }
                $scope.orderManagementTreeGridOptions = {
                    columns: orderManagementTreeGridColumns,
                    dataSource: treeGridDataSource,
                    filterable: false,
                    sortable: false,
                    autoBind: false,
                    dataBound: function (e) {
                        if ($scope.selectedVisit == null) {
                            return;
                        }
                        var grid = e.sender;
                        var dataSource = grid.dataSource;
                        var gridData = dataSource.data();
                        for (var idx = 0; idx < gridData.length; idx++) {
                            if (gridData[idx].rid == $scope.selectedVisit.rid) {
                                break;
                            }
                        }
                        var treelist = $("#orderManagementTreeGrid").data("kendoTreeList");
                        var row = treelist.content.find("tr:visible").eq(idx);
                        treelist.select(row);
                    },
                    expand: function (e) {
                        //get the expanded object
                        expandedObj = angular.copy(e.model);
                    },
                    change: function (e) {
                        //$rootScope.$broadcast("reauthorize");
                        onGridChangeListener(e);
                    }
                };
                //sort hack, to get the sort list from the grid since it does not support sorting with back end
                // var sortList = [];
                // var originalSortFn = kendo.data.TreeListDataSource.fn.sort;
                // kendo.data.TreeListDataSource.fn.sort = function (e) {
                //     if (arguments.length > 0) {
                //         if (e === null) {
                //             sortList = [];
                //         } else {
                //             sortList = e;
                //         }
                //         paginationTreeGridDataSource.sort({});
                //     }
                //     return originalSortFn.apply(this, arguments);
                // };
                // this is the actual datasource that is doing the pagination
                var paginationTreeGridDataSource = new kendo.data.DataSource({

                    transport: {
                        read: function (e) {
                            e.data.filters = getUserFilters();
                            e.data["sortList"] = [];
                            e.data["sortList"].push({ "direction": "DESC", "property": "visitDate" });
                            e.data["page"] = pager.page() == 0 ? pager.page() : pager.page() - 1;//dont decrease when page is zero
                            e.data["size"] = pager.pageSize();
                            orderManagementService.getVisitOrderManagementData(e.data).then(function (response) {
                                treeData = response.data.content;
                                treeGridDataSource.read();
                                e.success(response.data);
                            }).catch(function (error) {
                                e.error(error);
                            });
                        }
                    },
                    pageSize: config.gridPageSizes[0],
                    page: 1,
                    serverPaging: true,
                    schema: {
                        data: "content",
                        total: "totalElements",
                        model: {
                            id: "rid"
                        }
                    }
                });
                var pager = $("#pager").kendoPager({
                    dataSource: paginationTreeGridDataSource,
                    pageSizes: config.gridPageSizes,
                    buttonCount: config.gridPageButtonCount
                }).data("kendoPager");

                var gridDataSource = new kendo.data.DataSource({
                    pageSize: config.gridPageSizes[0],
                    page: 1,
                    serverPaging: true,
                    serverFiltering: true,
                    transport: {
                        read: function (e) {
                            e.data = util.createFilterablePageRequest(gridDataSource);
                            e.data["filters"] = getUserFilters();
                            e.data["sortList"].push({ "direction": "DESC", "property": "creationDate" });
                            orderManagementService.getTestOrderManagementData(e.data).then(function (response) {
                                e.success(response.data);
                            }).catch(function (error) {
                                e.error(error);
                            });
                        }
                    },
                    schema: {
                        parse: function (data) {
                            for (var idx = 0; idx < data.content.length; idx++) {
                                var labTestActual = data.content[idx];
                                var sample = labTestActual.labSample;
                                var visit = sample.emrVisit;
                                //columns data
                                labTestActual["barcode"] = getValue(sample, "barcode");
                                if (sample.lkpContainerType != null) {
                                    labTestActual["barcode"] += " (" + sample.lkpContainerType.name[util.userLocale] + ")";
                                }
                                for (var key in visit.emrPatientInfo.fullName) {
                                    labTestActual[("patientName_" + key)] = visit.emrPatientInfo.fullName[key];
                                }
                                labTestActual["descriptionLocale"] = getValue(labTestActual, "testDefinition", "description");
                                labTestActual["standardCodeLocale"] = getValue(labTestActual, "testDefinition", "standardCode");
                                labTestActual["lkpOperationStatusLocale"] = getValue(labTestActual, "lkpOperationStatus", ("name." + util.userLocale));
                                labTestActual["visitDateLocale"] = $filter("dateTimeFormat")(getValue(visit, "visitDate"));
                                labTestActual["sectionLocale"] = getValue(labTestActual, "testDefinition", ("section.name." + util.userLocale));
                                labTestActual["visitTypeLocale"] = getValue(visit, "visitType", ("name." + util.userLocale));
                                labTestActual["fileNo"] = getValue(visit, "emrPatientInfo", "fileNo");
                                labTestActual["age"] = getValue(visit, "emrPatientInfo", "age");
                                labTestActual["sex"] = getValue(visit, "emrPatientInfo", ("gender.name." + util.userLocale));
                                labTestActual["doctor"] = getValue(visit, "doctor", ("name." + util.userLocale));
                                labTestActual["mobileNo"] = getValue(visit, "emrPatientInfo", "mobileNo");
                                labTestActual["branchLocale"] = getValue(visit, "branch", ("name." + util.userLocale));
                                labTestActual["normalRangeText"] = getValue(labTestActual, "testDefinition", "normalRangeText");
                                //extra data to be used when doing some api or js functions
                                labTestActual["type"] = $scope.labTestActualType;
                                labTestActual["emrVisit"] = visit;
                                labTestActual["emrPatientInfo"] = visit.emrPatientInfo;
                            }
                            return data;
                        },
                        data: "content",
                        total: "totalElements",
                        model: {
                            id: "rid",
                            fields: {
                                "rid": { type: "number" },
                                "admissionNumber": { type: "string" },
                                "barcode": { type: "string" },
                                "standardCodeLocale": { type: "string" },
                                "descriptionLocale": { type: "string" },
                                "lkpOperationStatusLocale": { type: "string" },
                                "visitDateLocale": { type: "string" },
                                "visitTypeLocale": { type: "string" },
                                "sectionLocale": { type: "string" },
                                "fileNo": { type: "string" },
                                "age": { type: "number" },
                                "sex": { type: "string" },
                                "doctor": { type: "string" },
                                "mobileNo": { type: "string" },
                                "branchLocale": { type: "string" },
                                "normalRangeText": { type: "string" }
                            }
                        }
                    }
                });
                $scope.orderManagementGridOptions = {
                    columns: orderManagementGridColumns,
                    filterable: false,
                    sortable: false,
                    autoBind: false,
                    dataSource: gridDataSource,
                    dataBound: function (e) {
                        //pre select
                        var gridId = "#orderManagementGrid";
                        var grid = e.sender;
                        var dataSource = grid.dataSource;
                        var gridData = dataSource.data();
                        if ($scope.selectedObject == null) {
                            return;
                        }
                        var obj = null;
                        for (var idx = 0; idx < gridData.length; idx++) {
                            if (gridData[idx].rid == $scope.selectedObject.rid) {
                                obj = gridData[idx];
                                break;
                            }
                        }
                        if (obj == null) {
                            return;
                        }
                        var tr = $(gridId + " .k-grid-content tbody tr[data-uid=" + obj.uid + "]");
                        grid.select(tr);
                    },
                    change: function (e) {
                        onGridChangeListener(e);
                    }
                };
                // so grids load and auto complete loads
                $timeout(function () {
                    $scope.autoCompleteCriteriaListener();
                    $scope.refreshGrids();
                });


            }
        ]);
});
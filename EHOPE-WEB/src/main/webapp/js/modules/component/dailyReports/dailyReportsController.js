define(['app', 'config', 'util', 'commonData'], function (app, config, util, commonData) {
    'use strict';
    app.controller('dailyReportsCtrl', [
        '$scope', 'dailyReportsService', 'clientManagementService', 'branchFormService', 'lovService', '$filter',
        function ($scope, dailyReportsService, clientManagementService, branchFormService, lovService, $filter) {

            var yesterday = new Date();
            yesterday.setDate(yesterday.getDate() - 1);
            yesterday.setHours(0, 0, 0, 0);// so we get day without time since we want to show the full day orders
            var today = new Date();
            today.setHours(0, 0, 0, 0);// so we get day without time since we want to show the full day orders

            var timezoneOffset = new Date().getTimezoneOffset();
            var timezoneId = new Date().toString().split('(')[1].slice(0, -1).replace('Daylight', 'Standard');
            var filters = [
                {
                    "field": "isActive",
                    "value": true,
                    "operator": "eq"
                }
            ];

            var branchIdToUse = null;
            if (util.user.branch) {
                branchIdToUse = util.user.branch.rid;
            } else {
                prepareBranchLovs();
            }

            function prepareBranchLovs() {
                branchFormService.getLabBranchList({ filters: filters }).then(function (response) {
                    for (var idx = 0; idx < response.data.length; idx++) {
                        var label = "";
                        label += response.data[idx].name[util.userLocale];
                        response.data[idx]["customLabel"] = label;
                    }

                    var obj = {};
                    var label = "";
                    label += $filter('translate')('all');
                    obj["customLabel"] = label;
                    obj["rid"] = -1;
                    response.data.splice(0, 0, obj);

                    //Claim Branch
                    $scope.claimBranchLkp = {
                        className: "LabBranch",
                        name: "insProvider",
                        valueField: "customLabel",
                        labelText: "branch",
                        selectedValue: null,
                        required: true,
                        data: response.data
                    };

                    //Daily Income
                    $scope.dailyIncomeBranchLkp = {
                        className: "LabBranch",
                        name: "insProvider",
                        valueField: "customLabel",
                        labelText: "branch",
                        selectedValue: null,
                        required: true,
                        data: response.data
                    };

                    //Daily Cash
                    $scope.dailyCashBranchLkp = {
                        className: "LabBranch",
                        name: "insProvider",
                        valueField: "customLabel",
                        labelText: "branch",
                        selectedValue: null,
                        required: true,
                        data: response.data
                    };

                    //Daily Credit Payment
                    $scope.dailyCreditPaymentBranchLkp = {
                        className: "LabBranch",
                        name: "insProvider",
                        valueField: "customLabel",
                        labelText: "branch",
                        selectedValue: null,
                        required: true,
                        data: response.data
                    };

                });
            }

            lovService.getLkpByClass({ "className": "LkpTestDestinationType" })
                .then(function (data) {

                    for (var idx = 0; idx < data.length; idx++) {
                        if (data[idx].code === "WORKBENCH") {
                            data.pop();
                        } else {
                            var label = "";
                            label += data[idx].name[util.userLocale];
                            data[idx]["customLabel"] = label;
                        }
                    }

                    var obj = {};
                    var label = "";
                    label += $filter('translate')('all');
                    obj["customLabel"] = label;
                    obj["rid"] = -1;
                    data.splice(0, 0, obj);


                    $scope.testDestinationLkp = {
                        className: "LkpTestDestinationType",
                        name: "lkpTestDestinationType",
                        labelText: "destinationType",
                        valueField: "customLabel",
                        selectedValue: null,
                        required: true,
                        data: data
                    };
                });









            lovService.getLkpByClass({ "className": "LkpPaymentMethod" })
                .then(function (data) {

                    for (var idx = 0; idx < data.length; idx++) {
                        var label = "";
                        label += data[idx].name[util.userLocale];
                        data[idx]["customLabel"] = label;
                    }

                    var obj = {};
                    var label = "";
                    label += $filter('translate')('all');
                    obj["customLabel"] = label;
                    obj["rid"] = -1;
                    data.splice(0, 0, obj);


                    $scope.paymentMethodLkp = {
                        className: "LkpPaymentMethod",
                        name: "lkpPaymentMethod",
                        labelText: "paymentMethod",
                        valueField: "customLabel",
                        selectedValue: null,
                        required: true,
                        data: data
                    };
                });

            clientManagementService.getInsProviderList()
                .then(function (data) {
                    for (var idx = 0; idx < data.length; idx++) {
                        var label = "";
                        if (data[idx].parentProvider != null) {
                            label = label + data[idx].parentProvider.name[util.userLocale] + commonData.arrow;
                        }
                        label += data[idx].name[util.userLocale];
                        data[idx]["customLabel"] = label;
                    }

                    var obj = {};
                    var label = "";
                    label += $filter('translate')('all');
                    obj["customLabel"] = label;
                    obj["rid"] = -1;
                    data.splice(0, 0, obj);

                    $scope.providerLkp = {
                        className: "InsProvider",
                        name: "insProvider",
                        valueField: "customLabel",
                        labelText: "insProvider",
                        selectedValue: null,
                        required: true,
                        data: data
                    };

                    $scope.referralProviderLkp = {
                        className: "InsProvider",
                        name: "insProvider",
                        valueField: "customLabel",
                        labelText: "insProvider",
                        selectedValue: null,
                        required: true,
                        data: data,
                        onParentChange: loadUpdatedProviders
                    };
                });

            function loadUpdatedProviders(testDestination) {
                if (testDestination.rid == -1) {
                    return clientManagementService.getAllClientsList().then(function (response) {
                        var data = response.data;
                        for (var idx = 0; idx < data.length; idx++) {
                            var label = "";
                            if (data[idx].parentProvider != null) {
                                label = label + data[idx].parentProvider.name[util.userLocale] + commonData.arrow;
                            }
                            label += data[idx].name[util.userLocale];
                            data[idx]["customLabel"] = label;
                        }
                        var obj = {};
                        var label = "";
                        label += $filter('translate')('all');
                        obj["customLabel"] = label;
                        obj["rid"] = -1;
                        data.splice(0, 0, obj);

                        $scope.referralProviderLkp.data = data;
                    });
                } else {
                    ////////////////////////////////////////
                    return clientManagementService.getClientList({
                        branchRidToExclude: null,
                        purpose: "DESTINATION",
                        type: testDestination.code
                    }).then(function (response) {
                        var data = response.data;
                        for (var idx = 0; idx < data.length; idx++) {
                            var label = "";
                            if (data[idx].parentProvider != null) {
                                label = label + data[idx].parentProvider.name[util.userLocale] + commonData.arrow;
                            }
                            label += data[idx].name[util.userLocale];
                            data[idx]["customLabel"] = label;
                        }

                        var obj = {};
                        var label = "";
                        label += $filter('translate')('all');
                        obj["customLabel"] = label;
                        obj["rid"] = -1;
                        data.splice(0, 0, obj);

                        $scope.referralProviderLkp.data = data;
                    });
                    ////////////////////////////////////////
                }
            }

            $scope.filters = {
                dailyCashDateFrom: yesterday,
                dailyCashDateTo: today,

                dailyIncomeDateFrom: yesterday,
                dailyIncomeDateTo: today,

                claimDateFrom: yesterday,
                claimDateTo: today,

                dailyCreditPaymentDateFrom: yesterday,
                dailyCreditPaymentDateTo: today,

                referralOutDateFrom: yesterday,
                referralOutDateTo: today,
            };

            $scope.generateDailyCashPaymentsReport = function () {
                var dailyCashBranchId = angular.copy(branchIdToUse);
                if ($scope.dailyCashBranchLkp) {
                    dailyCashBranchId = $scope.dailyCashBranchLkp.selectedValue.rid
                }
                $scope.dailyCashFilters = {
                    dailyCashDateFrom: $scope.filters.dailyCashDateFrom,
                    dailyCashDateTo: $scope.filters.dailyCashDateTo,
                    branchRid: dailyCashBranchId,
                    paymentTypeRid: null,
                    timezoneOffset: timezoneOffset,
                    timezoneId: timezoneId,
                };

                if ($scope.paymentMethodLkp.selectedValue.rid != -1) {
                    $scope.dailyCashFilters.paymentTypeRid = $scope.paymentMethodLkp.selectedValue.rid;
                }

                dailyReportsService.generateDailyCashPaymentsReport($scope.dailyCashFilters)
                    .then(function (response) {
                        util.fileHandler(response.data, { type: "application/pdf", name: commonData.reportNames.dailyCash });
                    });
            };

            $scope.generateDailyIncomeReport = function () {
                var dailyIncomeBranchId = angular.copy(branchIdToUse);
                if ($scope.dailyIncomeBranchLkp) {
                    dailyIncomeBranchId = $scope.dailyIncomeBranchLkp.selectedValue.rid
                }
                $scope.dailyIncomeFilters = {
                    dailyIncomeDateFrom: $scope.filters.dailyIncomeDateFrom,
                    dailyIncomeDateTo: $scope.filters.dailyIncomeDateTo,
                    branchRid: dailyIncomeBranchId,
                    timezoneOffset: timezoneOffset,
                    timezoneId: timezoneId,
                };

                dailyReportsService.generateDailyIncomeReport($scope.dailyIncomeFilters)
                    .then(function (response) {
                        util.fileHandler(response.data, { type: "application/pdf", name: commonData.reportNames.dailyIncome });
                    });
            };

            $scope.generateDailyCreditPaymentReport = function () {
                var dailyCreditPaymentBranchId = angular.copy(branchIdToUse);
                if ($scope.dailyCreditPaymentBranchLkp) {
                    dailyCreditPaymentBranchId = $scope.dailyCreditPaymentBranchLkp.selectedValue.rid
                }
                $scope.dailyCreditPaymentFilters = {
                    dailyCreditPaymentDateFrom: $scope.filters.dailyCreditPaymentDateFrom,
                    dailyCreditPaymentDateTo: $scope.filters.dailyCreditPaymentDateTo,
                    branchRid: dailyCreditPaymentBranchId,
                    timezoneOffset: timezoneOffset,
                    timezoneId: timezoneId,
                };

                dailyReportsService.generateDailyCreditPaymentReport($scope.dailyCreditPaymentFilters)
                    .then(function (response) {
                        util.fileHandler(response.data, { type: "application/pdf", name: commonData.reportNames.dailyCredit });
                    });
            };

            $scope.generateClaimReport = function () {
                var claimBranchId = angular.copy(branchIdToUse);
                if ($scope.claimBranchLkp) {
                    claimBranchId = $scope.claimBranchLkp.selectedValue.rid
                }
                $scope.claimFilters = {
                    claimDateFrom: $scope.filters.claimDateFrom,
                    claimDateTo: $scope.filters.claimDateTo,
                    providerRid: $scope.providerLkp.selectedValue.rid,
                    branchRid: claimBranchId,
                    isReportSummarized: $scope.isReportSummarized,
                    timezoneOffset: timezoneOffset,
                    timezoneId: timezoneId,
                };

                var reportName = commonData.reportNames.claimDetailed;
                if ($scope.isReportSummarized) {
                    reportName = commonData.reportNames.claimSummarized;
                } else {
                    reportName = commonData.reportNames.claimDetailed;
                }

                dailyReportsService.generateClaimReport($scope.claimFilters)
                    .then(function (response) {
                        util.fileHandler(response.data, { type: "application/pdf", name: reportName });
                    });
            };

            $scope.generateReferralOutReport = function () {
                $scope.referralFilters = {
                    referralOutDateFrom: $scope.filters.referralOutDateFrom,
                    referralOutDateTo: $scope.filters.referralOutDateTo,
                    insuranceRid: -1,
                    destinationTypeRid: $scope.testDestinationLkp.selectedValue.rid,
                    timezoneOffset: timezoneOffset,
                    timezoneId: timezoneId,
                };

                dailyReportsService.generateReferralOutReport($scope.referralFilters)
                    .then(function (response) {
                        util.fileHandler(response.data, { type: "application/pdf", name: commonData.reportNames.referralOut });
                    });
            };

        }
    ]);
});

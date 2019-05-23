define(['app', 'config', 'util', 'commonData'], function (app, config, util, commonData) {
    'use strict';
    app.controller('outstandingBalancesCtrl', [
        '$scope', '$filter', '$rootScope', 'outstandingBalancesService', 'patientProfileService', 'paymentFormService', 'patientLookupService',
        function ($scope, $filter, $rootScope, outstandingBalancesService, patientProfileService, paymentFormService, patientLookupService) {
            $scope.selectedVisit = null;
            var yesterday = new Date();
            yesterday.setDate(yesterday.getDate() - 1);
            yesterday.setHours(0, 0, 0, 0);// so we get day without time since we want to show the full day orders
            var today = new Date();
            today.setHours(0, 0, 0, 0);// so we get day without time since we want to show the full day orders
            $scope.filters = {
                visitDateFrom: yesterday,
                visitDateTo: today,
                patientRid: null
            };
            if ($rootScope.outStandingPatient) {
                $scope.filters.patientRid = $rootScope.outStandingPatient.rid;
                $scope.filters.visitDateFrom = null;//remove date
                $scope.filters.visitDateTo = null;//remove date
                delete $rootScope.outStandingPatient;
            }
            function patientAutocompleteCallback(filters) {
                //We only care about rid filter not the other filters from the auto search 
                if (filters == null) {
                    return;
                } else if (filters.length === 0) {
                    $scope.filters.patientRid = null;//reset
                } else if (filters.length === 1) {
                    $scope.filters.patientRid = filters[0].value.toString();
                }
                $scope.refreshGrid();
            }
            $scope.patientSearchOptions = {
                service: patientLookupService.getPatientLookupPage,
                callback: patientAutocompleteCallback,
                skeleton: {
                    code: "fullName",
                    description: "fullName",
                    image: "image"
                },
                dynamicLang: { code: true, description: true },
                disabled: false,
                filterList: ["firstName", "secondName", "thirdName", "lastName", "fullName", "nationalId", "mobileNo", "secondaryMobileNo", "fileNo"]
            };

            $scope.search = function () {
                outstandingBalancesDataSource.page(0);
            };
            function getFilters() {
                var filters = [];
                if ($scope.outstandingBalancesForm.$valid) {
                    for (var key in $scope.filters) {
                        if ($scope.filters[key] !== null) {
                            filters.push({ field: key, value: $scope.filters[key] });
                        }
                    }
                }
                return filters;
            }
            function getSortList(currentSortList) {
                var sortList = [];
                //replace remainingAmount by the actual columns
                if (currentSortList != null && currentSortList.length > 0 && currentSortList[0].property == "remainingAmount") {
                    var direction = currentSortList[0].direction;
                    sortList.push({ "direction": direction, "property": "totalAmount" });
                    sortList.push({ "direction": direction, "property": "paidAmount" });
                } else {
                    sortList = currentSortList;
                }
                return sortList;

            }
            var outstandingBalancesDataSource = $scope.outstandingBalancesDataSource = new kendo.data.DataSource({
                pageSize: config.gridPageSizes[0],
                page: 1,
                transport: {
                    read: function (e) {
                        e.data = util.createFilterablePageRequest(outstandingBalancesDataSource);
                        e.data.filters = getFilters();
                        e.data.sortList = getSortList(e.data.sortList);
                        outstandingBalancesService.getOutstandingBalanceVisits(e.data)
                            .then(function (response) {
                                e.success(response.data);
                            }).catch(function (error) {
                                e.error(error);
                            });
                    }
                },
                serverPaging: true,
                sort: { field: "visitDate", dir: "desc" },
                schema: {
                    total: "totalElements",
                    data: "content",
                    model: {
                        id: "rid",
                        fields: {
                            "visitDate": { type: "date" },
                            "orderNumber": { type: "string" },
                            "emrPatientInfo": { type: "object" },
                            "totalAmount": { type: "number" },
                            "remainingAmount": { type: "number" }
                        }
                    },
                    parse: function (response) {
                        for (var i = 0; i < response.content.length; i++) {
                            var item = response.content[i];
                            item.remainingAmount = util.round(item.totalAmount - item.paidAmount);
                        }
                        return response;
                    }
                }
            });
            $scope.outstandingBalancesGridOptions = {
                dataSource: outstandingBalancesDataSource,
                filterable: false,
                columns: [
                    {
                        field: "admissionNumber",
                        title: util.systemMessages.orderNumber,
                    },
                    {
                        field: "visitDate",
                        title: util.systemMessages.orderDate,
                        template: function (dataItem) {
                            var date = dataItem.visitDate;
                            return date ? $filter("dateTimeFormat")(date) : "";
                        }
                    },
                    {
                        field: "emrPatientInfo",
                        title: util.systemMessages.patient,
                        template: function (dataItem) {
                            return dataItem.emrPatientInfo.fullName[util.userNamePrimary];
                        }
                    },
                    {
                        field: "totalAmount",
                        title: util.systemMessages.totalAmount,
                        template: function (dataItem) {
                            return dataItem.totalAmount + " " + util.userCurrency;
                        }
                    },
                    {
                        field: "remainingAmount",
                        title: util.systemMessages.remainingAmount,
                        template: function (dataItem) {
                            return dataItem.remainingAmount + " " + util.userCurrency;
                        }
                    }
                ],
                dataBinding: function () {
                    $scope.selectedVisit = null;
                },
                change: function (e) {
                    var selectedRows = e.sender.select();
                    if (selectedRows.length > 0) {
                        $scope.selectedVisit = this.dataItem(selectedRows[0]);
                    } else {
                        $scope.selectedVisit = null;
                    }
                }
            };

            $scope.generatePatientOutstandingBalances = function () {
                $scope.patientInfo = {
                    visitRid: $scope.selectedVisit.rid,
                    visitDateFrom: $scope.filters.visitDateFrom,
                    visitDateTo: $scope.filters.visitDateTo,
                    timezoneOffset: new Date().getTimezoneOffset(),
                    timezoneId: new Date().toString().split('(')[1].slice(0, -1).replace('Daylight', 'Standard'),
                };

                outstandingBalancesService.generatePatientOutstandingBalancesReport($scope.patientInfo)
                    .then(function (response) {
                        util.fileHandler(response.data, { type: "application/pdf", name: commonData.reportNames.outstandingBalance });
                    });
            };

            $scope.generateAllOutstandingBalances = function () {
                $scope.filtersInfo = {
                    patientRid: $scope.filters.patientRid,
                    visitDateFrom: $scope.filters.visitDateFrom,
                    visitDateTo: $scope.filters.visitDateTo,
                    timezoneOffset: new Date().getTimezoneOffset(),
                    timezoneId: new Date().toString().split('(')[1].slice(0, -1).replace('Daylight', 'Standard'),
                };

                outstandingBalancesService.generateOutstandingBalancesReport($scope.filtersInfo)
                    .then(function (response) {
                        util.fileHandler(response.data, { type: "application/pdf", name: commonData.reportNames.outstandingBalance });
                    });
            };


            $scope.printAll = function () {
                var filterablePageRequest = util.createFilterablePageRequest(outstandingBalancesDataSource);
                filterablePageRequest.filters = getFilters();
                filterablePageRequest.sortList = getSortList(filterablePageRequest.sortList);
                outstandingBalancesService.generateOutstandingBalancesReport(filterablePageRequest)
                    .then(function (response) {
                        util.fileHandler(response.data, { type: "application/pdf", name: commonData.reportNames.outstandingBalance }, true);
                    });
            }

            $scope.pay = function () {
                var data =
                {
                    isPartial: true,
                    total: ($scope.selectedVisit.totalAmount - $scope.selectedVisit.paidAmount),
                    visit: $scope.selectedVisit,
                    finishCallback: $scope.refreshGrid,
                    isEditOrder: false
                }
                paymentFormService.paymentDialog(data);
            }

            $scope.refreshGrid = function () {
                outstandingBalancesDataSource.page(0);
            };
        }
    ]);
});

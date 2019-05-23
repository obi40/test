define(['app', 'config', 'util', 'commonData'], function (app, config, util, commonData) {
    'use strict';
    app.controller('patientProfileCtrl',
        ['$rootScope', '$scope', 'patientProfileService', 'artifactService', '$state', 'clientManagementService', 'lovService',
            function ($rootScope, $scope, patientProfileService, artifactService, $state, clientManagementService, lovService) {

                if (!$rootScope.currentPatient) {
                    util.exitPage();
                    return;
                }

                var timezoneOffset = new Date().getTimezoneOffset();
                var timezoneId = new Date().toString().split('(')[1].slice(0, -1).replace('Daylight', 'Standard');

                $scope.tabToSelect = $rootScope.patientTabToSelect === undefined ? 0 : $rootScope.patientTabToSelect;
                $rootScope.patientTabToSelect = undefined;

                $scope.patientProfileMode = $rootScope.patientProfileMode;

                $scope.editInsuranceInfoTemplate = config.lisDir + "/modules/component/patientProfile/insurance-info-edit.html";

                $scope.patient = { rid: $rootScope.currentPatient.rid };
                $scope.patientInfoOptions = {
                    patientRid: $rootScope.currentPatient.rid
                };
                $scope.patientFormOptions = {
                    formMode: "edit",
                    onEditCallback: function () {
                        $scope.patientInfoOptions.refresh();
                    }
                };
                $scope.languages = util.languages;
                $scope.locale = util.userLocale;
                $scope.forms = {};

                patientProfileService.getPatientInfo($rootScope.currentPatient.rid)
                    .then(function (response) {
                        $rootScope.currentPatient = $scope.patient = response.data;
                        util.waitForDirective("patientFormReady", commonData.events.enterPatientForm, $scope, $scope.patient);
                    });

                //#region update insurance info

                var insuranceFetched = false;
                $scope.insuranceList = [];

                $scope.onInsuranceTabSelection = function () {
                    if (!insuranceFetched) {
                        patientInsuranceDataSource.read();
                        insuranceFetched = true;
                    }
                }
                $scope.selectedInsurance = null;

                $scope.insuranceEditMode = 'viewAll';

                $scope.viewAllInsurance = function () {
                    $("#patientInsuranceGrid").data("kendoGrid").trigger("change");
                    $scope.insuranceEditMode = "viewAll";
                    patientInsuranceDataSource.read();
                }

                $scope.$on(commonData.events.exitPatientInsuranceForm, function () {
                    $scope.viewAllInsurance();
                });

                $scope.addInsurance = function () {
                    $scope.selectedInsurance = {
                        isActive: false,
                        isVip: false,
                        isDefault: false,
                        patient: $scope.patient
                    };
                    $scope.insuranceEditMode = "add";
                    // $scope.$broadcast(commonData.events.enterPatientInsuranceForm, $scope.selectedInsurance);
                    // the broadcast was replaced, we are now rendering the directive using ng-if each time
                };

                $scope.editInsurance = function () {
                    $scope.insuranceEditMode = "edit";
                    // $scope.$broadcast(commonData.events.enterPatientInsuranceForm, $scope.selectedInsurance);
                };
                $scope.activateInsurance = function () {
                    patientProfileService.activatePatientInsurance($scope.selectedInsurance.rid)
                        .then(function (response) {
                            util.createToast(util.systemMessages.success, "success");
                            patientInsuranceDataSource.read();
                        });
                };
                $scope.deactivateInsurance = function () {
                    patientProfileService.deactivatePatientInsurance($scope.selectedInsurance.rid)
                        .then(function (response) {
                            util.createToast(util.systemMessages.success, "success");
                            patientInsuranceDataSource.read();
                        });
                };
                $scope.triggerDefaultInsurance = function () {
                    $scope.selectedInsurance["patient"] = $scope.patient;//since it is not fetched in grid
                    patientProfileService.triggerDefaultInsurance($scope.selectedInsurance)
                        .then(function () {
                            util.createToast(util.systemMessages.success, "success");
                            patientInsuranceDataSource.read();
                        });
                };

                var patientInsuranceDataSource = new kendo.data.DataSource({
                    pageSize: config.gridPageSizes[0],
                    page: 1,
                    serverPaging: true,
                    serverFiltering: true,
                    serverSorting: true,
                    sort: { field: "updateDate", dir: "desc" },
                    transport: {
                        read: function (e) {
                            var filterMap = {
                                lkpDependencyTypeRid: "lkpDependencyType.rid",
                                insProviderPlanRid: "insProviderPlan.rid"
                            };
                            e.data = util.createFilterablePageRequest($scope.patientInsuranceGridOptions.dataSource, filterMap);
                            var patientFilter = {
                                field: "patient",
                                value: $scope.patient.rid,
                                operator: "eq"
                            };
                            var addPatientFilter = true;
                            for (var i = 0; i < e.data.filters.length; i++) {
                                var filter = e.data.filters[i];
                                if (filter.field === patientFilter.field
                                    && filter.operator === patientFilter.operator
                                    && filter.value === patientFilter.value) {
                                    addPatientFilter = false;
                                    break;
                                }
                            }
                            if (addPatientFilter) {
                                e.data.filters.push(patientFilter);
                            }
                            for (var i = 0; i < e.data.sortList.length; i++) {
                                if (e.data.sortList[i].property === "patientShare") {
                                    e.data.sortList[i].property = "coveragePercentage"
                                    switch (e.data.sortList[i].direction) {
                                        case "ASC":
                                            e.data.sortList[i].direction = "DESC";
                                            break;
                                        case "DESC":
                                            e.data.sortList[i].direction = "ASC";
                                            break;
                                    }
                                }
                            }
                            patientProfileService.getPatientInsuranceList(e)
                                .then(function (response) {
                                    $scope.insuranceList = response.data.content;
                                    e.success(response.data);
                                }).catch(function (response) {
                                    e.error(response);
                                });
                        },
                    },
                    schema: {
                        parse: function (response) {
                            for (var i = 0; i < response.content.length; i++) {
                                response.content[i].patientShare = 100 - response.content[i].coveragePercentage;
                            }
                            return response;
                        },
                        total: "totalElements",
                        data: "content",
                        model: {
                            id: "rid",
                            fields: {
                                rid: { type: "number" },
                                lkpDependencyTypeRid: { from: "lkpDependencyType.rid", type: "lov" },
                                lkpDependencyType: { defaultValue: {} },
                                insProvider: { type: "object" },
                                insProviderPlanRid: { from: "insProviderPlan.rid", type: "lov" },
                                insProviderPlan: { defaultValue: {} },
                                subscriber: { type: "string" },
                                cardNumber: { type: "string" },
                                policyNo: { type: "text" },
                                coveragePercentage: { type: "number" },
                                issueDate: { type: "date" },
                                expiryDate: { type: "date" },
                                isActive: { type: "boolean" }
                            }
                        }
                    }
                });

                $scope.refreshPatientInsuranceGrid = function () {
                    patientInsuranceDataSource.read();
                };
                $scope.patientInsuranceGridOptions = {
                    autoBind: false,
                    columns: [
                        {
                            field: "lkpDependencyTypeRid",
                            title: "{{ 'dependencyType' | translate }}",
                            filterable: {
                                ui: function (element) {
                                    util.createLovFilter(element, { className: "LkpDependencyType" }, lovService.getLkpByClass);
                                }
                            },
                            template: function (dataItem) {
                                return dataItem.lkpDependencyType.name[util.userLocale];
                            }
                        },
                        {
                            hidden: true,
                            field: "insProvider",
                            title: "{{ 'insProvider' | translate }}",
                            template: function (dataItem) {
                                var label = "";
                                if (dataItem.insProvider.parentProvider != null) {
                                    label += dataItem.insProvider.parentProvider.name[util.userLocale] + commonData.arrow;
                                }
                                label += dataItem.insProvider.name[util.userLocale];
                                return label;
                            }
                        },
                        {
                            field: "insProviderPlanRid",
                            title: "{{ 'insProviderPlan' | translate }}",
                            filterable: {
                                ui: function (element) {
                                    util.createLovFilter(element, null, clientManagementService.getInsProviderPlanList);
                                }
                            },
                            template: function (dataItem) {
                                var template = "";
                                if (dataItem.insProviderPlan !== null && dataItem.insProviderPlan.code != "SIMPLE") {
                                    template = dataItem.insProviderPlan.name[util.userLocale];
                                }
                                return template;
                            }
                        },
                        {
                            field: "subscriber",
                            title: "{{ 'subscriberName' | translate }}"
                        },
                        {
                            hidden: true,
                            field: "cardNumber",
                            title: "{{ 'cardNumber' | translate }}"
                        },
                        {
                            hidden: true,
                            field: "policyNo",
                            title: "{{ 'policyNumber' | translate }}"
                        },
                        {
                            field: "patientShare",
                            title: "{{ 'patientShare' | translate }}"
                        },
                        {
                            hidden: true,
                            field: "issueDate",
                            title: "{{ 'issueDate' | translate }}",
                            format: "{0: " + config.dateFormat + "}"
                        },
                        {
                            hidden: true,
                            field: "expiryDate",
                            title: "{{ 'expiryDate' | translate }}",
                            format: "{0: " + config.dateFormat + "}"
                        },
                        {
                            hidden: true,
                            field: "isActive",
                            title: "{{ 'active' | translate }}",
                            template: "{{ #: isActive # ? 'yes' : 'no' | translate }}"
                        },
                        {
                            hidden: true,
                            field: "isVip",
                            title: "{{ 'vip' | translate }}",
                            template: "{{ #: isVip # ? 'yes' : 'no' | translate }}"
                        },
                        {
                            hidden: true,
                            field: "isDefault",
                            title: "{{ 'default' | translate }}",
                            template: "{{ #: isDefault # ? 'yes' : 'no' | translate }}"
                        }
                    ],
                    dataSource: patientInsuranceDataSource,
                    dataBound: function (e) {
                        $scope.selectedInsurance = null;
                    },
                    change: function (e) {
                        var selectedRows = this.select();
                        if (selectedRows.length > 0) {
                            $scope.selectedInsurance = this.dataItem(selectedRows[0]);
                            $scope.selectedInsurance.patient = $scope.patient;
                        } else {
                            $scope.selectedInsurance = null;
                        }
                    }
                };

                //#endregion

                //#region orders

                var ordersFetched = false;

                $scope.onOrderTabSelection = function () {
                    if (!ordersFetched) {
                        orderDataSource.read();
                        ordersFetched = true;
                    }
                };
                var allProviderPlans = [];
                clientManagementService.getInsProviderPlanList()
                    .then(function (data) {
                        allProviderPlans = data;
                    });
                var orderDataSource = new kendo.data.DataSource({
                    pageSize: config.gridPageSizes[0],
                    page: 1,
                    serverPaging: true,
                    serverFiltering: true,
                    sort: { field: "visitDate", dir: "desc" },
                    transport: {
                        read: function (e) {
                            var filterMap = { providerPlanRid: "providerPlan.rid" };

                            e.data = util.createFilterablePageRequest(orderDataSource, filterMap);
                            var patientFilter = {
                                field: "emrPatientInfo",
                                value: $scope.patient.rid,
                                operator: "eq"
                            };
                            var addPatientFilter = true;
                            for (var i = 0; i < e.data.filters.length; i++) {
                                var filter = e.data.filters[i];
                                if (filter.field === patientFilter.field
                                    && filter.operator === patientFilter.operator
                                    && filter.value === patientFilter.value) {
                                    addPatientFilter = false;
                                    break;
                                }
                            }
                            if (addPatientFilter) {
                                e.data.filters.push(patientFilter);
                            }
                            // if this is set then we add a filter then stop execution of the api since calling 
                            // orderDataSource.filter() executes transport->read again
                            if ($rootScope.orderHyperLink != null) {
                                orderDataSource.filter({ field: "admissionNumber", operator: "eq", value: $rootScope.orderHyperLink });
                                $rootScope.orderHyperLink = null;
                                e.success([]);
                                return;
                            }
                            patientProfileService.getVisitPageByPatient(e.data)
                                .then(function (response) {
                                    e.success(response.data);
                                }).catch(function (response) {
                                    e.error(response);
                                });
                        },
                    },
                    schema: {
                        total: "totalElements",
                        data: "content",
                        parse: function (response) {
                            // if it is actually has a content (it won't when we user e.success([]) )
                            if (response.content != null) {
                                for (var i = 0; i < response.content.length; i++) {
                                    if (response.content[i].providerPlan === null) {
                                        response.content[i].providerPlan = {
                                            rid: -1
                                        };
                                    }
                                }
                            }

                            return response;
                        },
                        model: {
                            id: "rid",
                            fields: {
                                rid: { type: "number" },
                                visitDate: { type: "date" },
                                admissionNumber: { type: "string" },
                                providerPlanRid: { from: "providerPlan.rid", type: "lov" },
                                providerPlan: { defaultValue: {} },
                                lkpOperationStatusRid: { from: "lkpOperationStatus.rid", type: "lov" },
                                lkpOperationStatus: { defaultValue: {} }
                            }
                        }
                    }
                });

                $scope.orderGridOptions = {
                    autoBind: false,
                    columns: [
                        {
                            field: "admissionNumber",
                            title: "{{ 'orderNumber' | translate }}"
                        },
                        {
                            field: "visitDate",
                            title: "{{ 'orderDate' | translate }}",
                            format: "{0: " + config.dateTimeFormat + "}"
                        },
                        {
                            field: "providerPlanRid",
                            title: "{{ 'insuranceCompany' | translate }}",
                            filterable: {
                                ui: function (element) {
                                    util.createListFilter(element, allProviderPlans, "name." + util.userLocale);
                                }
                            },
                            template: function (dataItem) {
                                if (dataItem.providerPlan.rid === -1) {
                                    return "";
                                }
                                return dataItem.providerPlan.name[util.userLocale];
                            }
                        },
                        {
                            field: "refDocName",
                            title: "{{ 'refDocName' | translate }}"
                        },
                        {
                            field: "lkpOperationStatusRid",
                            title: util.systemMessages.operationStatus,
                            template: function (dataItem) {
                                if (dataItem.lkpOperationStatus.rid === 0) {
                                    return "";
                                }
                                return dataItem.lkpOperationStatus.name[util.userLocale];
                            }
                        }
                    ],
                    dataSource: orderDataSource,
                    dataBound: function (e) {
                        $scope.selectedOrder = null;
                        if ($rootScope.orderToSelect !== undefined) {
                            var data = e.sender.dataSource.data();
                            for (var i = 0; i < data.length; i++) {
                                if (data[i].admissionNumber === $rootScope.orderToSelect.admissionNumber) {
                                    e.sender.select("tr:eq(" + i + ")");
                                    $rootScope.orderToSelect = undefined;
                                    break;
                                }
                            }
                        }
                    },
                    change: function (e) {
                        var selectedRows = this.select();
                        if (selectedRows.length > 0) {
                            $scope.selectedOrder = this.dataItem(selectedRows[0]);
                            $scope.$broadcast("getActualResults", $scope.selectedOrder);
                        } else {
                            $scope.selectedOrder = null;
                        }
                    }
                };
                $scope.refreshOrderGrid = function () {
                    orderDataSource.read();
                };
                $scope.selectedOrder = null;

                $scope.addOrder = function () {
                    $scope.patient.isInsurer = false;
                    $rootScope.currentPatient = $scope.patient;
                    $state.go("patient-profile-wizard");
                };

                $scope.generateInvoiceReport = function () {
                    $scope.invoiceInfo = {
                        visitRid: $scope.selectedOrder.rid,
                        timezoneOffset: timezoneOffset,
                        timezoneId: timezoneId,
                    };

                    patientProfileService.printInvoiceReport($scope.invoiceInfo)
                        .then(function (response) {
                            var fileName = $scope.patient.fullName[util.userNamePrimary] + "-" + $scope.selectedOrder.admissionNumber; //commonData.reportNames.results
                            util.fileHandler(response.data, { type: "application/pdf", name: fileName });
                        });
                };

                $scope.generateInsuranceInvoiceReport = function () {
                    $scope.insuranceInvoiceInfo = {
                        visitRid: $scope.selectedOrder.rid,
                        timezoneOffset: timezoneOffset,
                        timezoneId: timezoneId,
                    };

                    patientProfileService.printInsuranceInvoiceReport($scope.insuranceInvoiceInfo)
                        .then(function (response) {
                            util.fileHandler(response.data, { type: "application/pdf", name: commonData.reportNames.insuranceInvoice });
                        });
                };

                $scope.checkIfInsuranceInvoiceCanBePrinted = function () {
                    if ($scope.selectedOrder == null) {
                        return true;
                    }
                    if ($scope.selectedOrder.providerPlan == null) {
                        return true;
                    }
                    if ($scope.selectedOrder.providerPlanRid == -1) {
                        return true;
                    }
                    return false;
                };

                $scope.generateResultsReport = function () {
                    patientProfileService.resultsReportDialog($scope.selectedOrder.rid);
                };

                $scope.showArtifactDialog = function () {
                    artifactService.showArtifactDialog($scope.selectedOrder, "order");
                }

                //#endregion

                //#region orderTestResults

                var orderTestResultsDataSource = new kendo.data.DataSource({
                    pageSize: config.gridPageSizes[0],
                    page: 1,
                    batch: true,
                    transport: {
                        read: function (e) {
                            patientProfileService.getActualTestResultsByVisit($scope.selectedOrder.rid)
                                .then(function (response) {
                                    e.success(response.data);
                                }).catch(function (response) {
                                    e.error(response);
                                });
                        },
                        update: function (e) {
                            patientProfileService.editActualTestResult({ actualResults: e.data.models, order: $scope.selectedOrder })
                                .then(function (response) {
                                    e.success(response.data);
                                    util.createToast(util.systemMessages.success, "success");
                                }).catch(function (response) {
                                    e.error(response);
                                });
                        }
                    },
                    schema: {
                        model: {
                            id: "rid",
                            fields: {
                                rid: { type: "number" },
                                testDefinitionId: { type: "number", editable: false },
                                labResult: { type: "object", editable: false },
                                "labResult.description": { editable: false },
                                "labResult.testDefinition.normalRangeText": { editable: false }
                            }
                        }
                    },
                    group: {
                        field: "testDefinitionId",
                        aggregates: [
                            { field: "testDefinitionId", aggregate: "count" }
                        ]
                    }
                });

                $scope.orderTestResultsDataSource = orderTestResultsDataSource;

                $scope.orderTestResultsGridOptions = {
                    autoBind: false,
                    columns: [
                        {
                            hidden: true,
                            field: "testDefinitionId",
                            groupHeaderTemplate: function (e) {
                                var template = "{{ 'test' | translate }}: " + e.items[0].labTestActual.testDefinition.reportingDescription;
                                return template;
                            }
                        },
                        {
                            field: "labResult.description",
                            title: "{{ 'resultName' | translate }}"
                        },
                        {
                            field: "result1Value",
                            title: "{{ 'resultValue' | translate }}",
                            // template: function (dataItem) {
                            //     var unitOfMeasure = dataItem.labResult.unitOfMeasure.unitOfMeasure;
                            //     unitOfMeasure = unitOfMeasure === "NONE" ? "" : " " + unitOfMeasure;
                            //     var result = dataItem.resultValue;
                            //     result = result === null ? "" : result;
                            //     return result + unitOfMeasure;
                            // }
                        },
                        {
                            field: "result2Value",
                            title: "{{ 'resultValue' | translate }}",
                            // template: function (dataItem) {
                            //     var unitOfMeasure = dataItem.labResult.unitOfMeasure.unitOfMeasure;
                            //     unitOfMeasure = unitOfMeasure === "NONE" ? "" : " " + unitOfMeasure;
                            //     var result = dataItem.resultSiValue;
                            //     result = result === null ? "" : result;
                            //     return result + unitOfMeasure;
                            // }
                        },
                        {
                            field: "labResult.testDefinition.normalRangeText",
                            title: "{{ 'normalRangeText' | translate }}",
                            template: function (dataItem) {
                                if (dataItem.labResult.testDefinition.normalRangeText === null) {
                                    return "";
                                }
                                var template = '<span class="ellipsis">' +
                                    '<md-tooltip md-direction="top" class="flexible-tooltip">' + dataItem.labResult.testDefinition.normalRangeText + '</md-tooltip>' +
                                    dataItem.labResult.testDefinition.normalRangeText +
                                    '</span>';
                                return template;
                            }
                        }
                    ],
                    editable: $scope.patientProfileMode === "edit",
                    dataSource: orderTestResultsDataSource
                };

                $scope.saveOrderTestResultChanges = function () {
                    orderTestResultsDataSource.sync();
                }

                $scope.cancelOrderTestResultChanges = function () {
                    orderTestResultsDataSource.cancelChanges();
                }
                //#endregion


                //#region historicalResults

                $scope.selectedHistoricalOrder = null;

                var historicalOrdersFetched = false;

                $scope.onHistoricalOrdersTabSelection = function () {
                    if (!historicalOrdersFetched) {
                        historicalOrderDataSource.read();
                        historicalOrdersFetched = true;
                    }
                };

                var historicalOrderDataSource = new kendo.data.DataSource({
                    pageSize: config.gridPageSizes[0],
                    page: 1,
                    serverPaging: true,
                    serverFiltering: true,
                    sort: { field: "orderDate", dir: "desc" },
                    filter: { field: "patientFileNo", operator: "eq", value: $scope.patient.fileNo },
                    transport: {
                        read: function (e) {
                            e.data = util.createFilterablePageRequest(historicalOrderDataSource);
                            for (var i = 0; i < e.data.filters.length; i++) {
                                var filter = e.data.filters[i];
                                if (filter.field === "patientFileNo") {
                                    filter.value = $scope.patient.fileNo;
                                    break;
                                }
                            }
                            patientProfileService.getHistoricalOrderPage(e.data)
                                .then(function (response) {
                                    e.success(response.data);
                                }).catch(function (response) {
                                    e.error(response);
                                });
                        },
                    },
                    schema: {
                        total: "totalElements",
                        data: "content",
                        model: {
                            id: "rid",
                            fields: {
                                rid: { type: "number" },
                                orderNumber: { type: "string" },
                                orderDate: { type: "date" },
                                patientFileNo: { type: "string" }
                            }
                        }
                    }
                });

                $scope.historicalOrdersGridOptions = {
                    autoBind: false,
                    columns: [
                        {
                            field: "orderNumber",
                            title: "{{ 'orderNumber' | translate }}",
                        },
                        {
                            field: "orderDate",
                            title: "{{ 'orderDate' | translate }}",
                            format: "{0: " + config.dateFormat + "}"
                        }
                    ],
                    dataSource: historicalOrderDataSource,
                    dataBound: function (e) {
                        $scope.selectedHistoricalOrder = null;
                    },
                    change: function (e) {
                        var selectedRows = this.select();
                        if (selectedRows.length > 0) {
                            $scope.selectedHistoricalOrder = this.dataItem(selectedRows[0]);
                            historicalTestDataSource.page(0);
                        } else {
                            $scope.selectedHistoricalOrder = null;
                        }
                    }
                };

                $scope.selectedHistoricalTest = null;

                var historicalTestDataSource = new kendo.data.DataSource({
                    pageSize: config.gridPageSizes[0],
                    page: 1,
                    serverPaging: true,
                    serverFiltering: true,
                    filter: { field: "order.rid", operator: "eq", value: $scope.selectedHistoricalOrder },
                    transport: {
                        read: function (e) {
                            e.data = util.createFilterablePageRequest(historicalTestDataSource);
                            for (var i = 0; i < e.data.filters.length; i++) {
                                var filter = e.data.filters[i];
                                if (filter.field === "order.rid") {
                                    filter.value = $scope.selectedHistoricalOrder.rid;
                                    break;
                                }
                            }
                            patientProfileService.getHistoricalTestPage(e.data)
                                .then(function (response) {
                                    e.success(response.data);
                                }).catch(function (response) {
                                    e.error(response);
                                });
                        },
                    },
                    schema: {
                        total: "totalElements",
                        data: "content",
                        model: {
                            id: "rid",
                            fields: {
                                rid: { type: "number" },
                                testCode: { type: "string" },
                                comments: { type: "string" }
                            }
                        }
                    }
                });

                $scope.historicalTestsGridOptions = {
                    autoBind: false,
                    columns: [
                        {
                            field: "testCode",
                            title: "{{ 'standardCode' | translate }}"
                        },
                        {
                            field: "comments",
                            title: "{{ 'comments' | translate }}"
                        }
                    ],
                    dataSource: historicalTestDataSource,
                    dataBound: function (e) {
                        $scope.selectedHistoricalTest = null;
                    },
                    change: function (e) {
                        var selectedRows = this.select();
                        if (selectedRows.length > 0) {
                            $scope.selectedHistoricalTest = this.dataItem(selectedRows[0]);
                            historicalResultDataSource.page(0);
                        } else {
                            $scope.selectedHistoricalTest = null;
                        }
                    }
                };

                var historicalResultDataSource = new kendo.data.DataSource({
                    pageSize: config.gridPageSizes[0],
                    page: 1,
                    serverPaging: true,
                    serverFiltering: true,
                    filter: { field: "test.rid", operator: "eq", value: $scope.selectedHistoricalTest },
                    transport: {
                        read: function (e) {
                            e.data = util.createFilterablePageRequest(historicalResultDataSource);
                            for (var i = 0; i < e.data.filters.length; i++) {
                                var filter = e.data.filters[i];
                                if (filter.field === "test.rid") {
                                    filter.value = $scope.selectedHistoricalTest.rid;
                                    break;
                                }
                            }
                            patientProfileService.getHistoricalResultPage(e.data)
                                .then(function (response) {
                                    e.success(response.data);
                                }).catch(function (response) {
                                    e.error(response);
                                });
                        },
                    },
                    schema: {
                        total: "totalElements",
                        data: "content",
                        model: {
                            id: "rid",
                            fields: {
                                rid: { type: "number" },
                                resultCode: { type: "string" },
                                resultValue: { type: "string" }
                            }
                        }
                    }
                });

                $scope.historicalResultsGridOptions = {
                    autoBind: false,
                    columns: [
                        {
                            field: "resultCode",
                            title: "{{ 'standardCode' | translate }}"
                        },
                        {
                            field: "resultValue",
                            title: "{{ 'value' | translate }}",
                            template: function (dataItem) {
                                return dataItem.resultValue + " " + dataItem.convUnit;
                            }
                        },
                        {
                            field: "normalRangePrefix",
                            title: "{{ 'normalRangePrefix' | translate }}"
                        },
                        {
                            field: "convNormalRange",
                            title: "{{ 'convNormalRange' | translate }}"
                        },
                        {
                            field: "siNormalRange",
                            title: "{{ 'siNormalRange' | translate }}"
                        },
                        // {
                        //     field: "convUnit",
                        //     title: "{{ 'convUnit' | translate }}"
                        // },
                        {
                            field: "siUnit",
                            title: "{{ 'siUnit' | translate }}"
                        }
                    ],
                    dataSource: historicalResultDataSource
                };

                //#endregion

                $scope.goToPatientLookup = function () {
                    $state.go("patient-lookup");
                }

            }
        ]);
});

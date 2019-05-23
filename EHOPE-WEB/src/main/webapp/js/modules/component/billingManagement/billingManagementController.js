define(['app', 'config', 'util', 'routes'], function (app, config, util, routes) {
    'use strict';
    app.controller('billingManagementCtrl', [
        '$scope', '$filter', '$rootScope', '$mdDialog', 'commonMethods',
        'lovService', 'billingManagementService', 'priceListService', 'testDefinitionManagementService',
        function (
            $scope, $filter, $rootScope, $mdDialog, commonMethods,
            lovService, billingManagementService, priceListService, testDefinitionManagementService
        ) {

            $scope.viewMode = "billingClassification";

            function removeChip(chip) {
                util.removeGridChip(chip, $("#billTestItemsGrid").data("kendoGrid"));
            };

            $scope.billTestItemsChipsOptions = {
                data: [],
                label: "standardCode",
                onRemove: removeChip
            };

            $scope.setView = function (view) {
                $scope.viewMode = view;
                if (view === "masterItemDetails") {
                    billMasterItemDataSource.read();
                    $rootScope.pageTitleName = $scope.selectedBillClassification.name + " | " + $filter('translate')('masterItemDetails');
                } else {
                    $rootScope.pageTitleName = $filter('translate')(routes.routes["billing-management"].views.main.data.pageName);
                }
            }

            $scope.masterItemDetailsTemplate = config.lisDir + "/modules/component/billingManagement/billing-master-item-details.html";

            //#region billingClassification
            $scope.billClassificationsChanged = false;

            // commonMethods.retrieveMetaData("BillClassification")
            //     .then(function successCallback(resp) {
            //         $scope.billClassificationMetaData = resp.data;
            //     }).catch(function (response) {
            //         console.log(response);
            //     });

            var billClassificationDataSource = new kendo.data.TreeListDataSource({
                transport: {
                    read: function (e) {
                        var filterMap = { "parentClassification": "parentClassification.rid" };
                        e.data = util.createFilterablePageRequest($scope.billClassificationTreeListOptions.dataSource, filterMap);
                        billingManagementService.getBillClassificationList(e)
                            .then(function (response) {
                                e.success(response.data);
                            }, function (response) {
                                e.error(response);
                            });
                    },
                    create: function (e) {
                        billingManagementService.addBillClassification(e)
                            .then(function (response) {
                                e.success(response.data);
                                util.createToast(util.systemMessages.success, "success");
                            }, function (response) {
                                e.error(response);
                                util.gridErrorTooltip(response.data);
                            });
                    },
                    update: function (e) {
                        if (toActivate) {
                            billingManagementService.activateBillClassification(toActivate)
                                .then(function (response) {
                                    e.success(response.data);
                                    util.createToast(util.systemMessages.success, "success");
                                    toActivate = null;
                                    $scope.selectedBillClassificationIsActive = true;
                                }, function (response) {
                                    e.error(response);
                                });
                        } else if (toDeactivate) {
                            billingManagementService.deactivateBillClassification(toDeactivate)
                                .then(function (response) {
                                    e.success(response.data);
                                    util.createToast(util.systemMessages.success, "success");
                                    toDeactivate = null;
                                    $scope.selectedBillClassificationIsActive = false;
                                }, function (response) {
                                    e.error(response);
                                });
                        } else {
                            billingManagementService.editBillClassification(e)
                                .then(function (response) {
                                    e.success(response.data);
                                    util.createToast(util.systemMessages.success, "success");
                                }, function (response) {
                                    e.error(response);
                                    util.gridErrorTooltip(response.data);
                                });
                        }
                    }
                },
                sync: function () {
                    $scope.billClassificationsChanged = false;
                },
                serverFiltering: false,
                serverSorting: false,
                sort: { field: "code", dir: "asc" },
                schema: {
                    model: {
                        id: "rid",
                        parentId: "parentClassificationId",
                        fields: {
                            parentClassificationId: { type: "number", defaultValue: null, nullable: true },
                            code: { type: "string", validation: { required: true, maxLength: "50" }, defaultValue: null },
                            name: { type: "string", validation: { required: true }, defaultValue: null },
                            parentClassification: { type: "lov", defaultValue: null },
                            isActive: { type: "boolean", defaultValue: true, editable: false }
                        },
                        expanded: false
                    }
                }
            });
            $scope.billClassificationTreeListOptions = {
                editable: true,
                columns: [
                    {
                        field: "code",
                        expandable: true,
                        title: "{{ 'code' | translate }}"
                    },
                    {
                        field: "name",
                        title: "{{ 'name' | translate }}"
                    },
                    {
                        field: "parentClassification",
                        title: "{{ 'parent' | translate }}",
                        filterable: {
                            ui: function (element) {
                                util.createLovFilter(element, 0, billingManagementService.getParentBillClassifications);
                            }
                        },
                        template: function (dataItem) {
                            var template = "{{ 'none' | translate }}";
                            if (dataItem.parentClassification !== null) {
                                template = dataItem.parentClassification.code + " | " + dataItem.parentClassification.name;
                            }
                            return template;
                        },
                        editor: parentClassificationEditor
                    },
                    {
                        field: "isActive",
                        title: "{{ 'active' | translate }}",
                        template: "{{ #: isActive # ? 'yes' : 'no' | translate }}"
                    }
                ],
                dataSource: billClassificationDataSource,
                dataBound: function (e) {
                    if (!e.sender.select().length) {
                        $scope.selectedBillClassification = null;
                    }
                },
                edit: function (e) {
                    e.sender.select($("tr[data-uid=" + e.model.uid + "]"));
                    $scope.billClassificationsChanged = true;
                },
                change: function (e) {
                    var selectedRows = this.select();
                    if (selectedRows.length > 0) {
                        $scope.selectedBillClassification = this.dataItem(selectedRows[0]);
                        $scope.selectedBillClassificationIsActive = this.dataItem(selectedRows[0]).isActive;
                    } else {
                        $scope.selectedBillClassification = null;
                    }
                }
            };

            $scope.addBillClassification = function () {
                $("#billClassificationTreeList").data("kendoTreeList").addRow();
            }

            $scope.editBillClassification = function (dataItem) {
                $("#billClassificationTreeList").data("kendoTreeList").editRow(dataItem);
            }

            $scope.saveBillClassificationChanges = function () {
                billClassificationDataSource.sync();
            }

            var toActivate = null;
            $scope.activateBillClassification = function () {
                toActivate = $scope.selectedBillClassification.rid;
                var dataItem = $("#billClassificationTreeList").data("kendoTreeList").dataItem($scope.selectedBillClassification);
                dataItem.isActive = true;
                dataItem.dirty = true;
                billClassificationDataSource.sync();
            }

            var toDeactivate = null;
            $scope.deactivateBillClassification = function () {
                toDeactivate = $scope.selectedBillClassification.rid;
                var dataItem = $("#billClassificationTreeList").data("kendoTreeList").dataItem($scope.selectedBillClassification);
                dataItem.isActive = false;
                dataItem.dirty = true;
                billClassificationDataSource.sync();
            }

            $scope.cancelBillClassificationChanges = function () {
                billClassificationDataSource.cancelChanges();
                $scope.billClassificationsChanged = false;
            }

            $scope.refreshTreeList = function () {
                billClassificationDataSource.read();
            }

            //#endregion

            //#region billingMasterItem

            $scope.billMasterItemsChanged = false;

            var billMasterItemDataSource = new kendo.data.DataSource({
                pageSize: config.gridPageSizes[0],
                page: 1,
                transport: {
                    read: function (e) {
                        var filterMap = { "typeRid": "type.rid" };
                        e.data = util.createFilterablePageRequest($scope.billMasterItemsGridOptions.dataSource, filterMap);
                        if ($scope.selectedBillClassification !== null) {
                            var classificationFilter = {
                                field: "billClassification",
                                value: $scope.selectedBillClassification.rid,
                                operator: "eq"
                            };
                            var addClassificationFilter = true;
                            for (var i = 0; i < e.data.filters.length; i++) {
                                var filter = e.data.filters[i];
                                if (filter.field === classificationFilter.field
                                    && filter.operator === classificationFilter.operator
                                    && filter.value === classificationFilter.value) {
                                    addClassificationFilter = false;
                                    break;
                                }
                            }
                            if (addClassificationFilter) {
                                e.data.filters.push(classificationFilter);
                            }
                        }
                        billingManagementService.getBillMasterItemList(e)
                            .then(function (response) {
                                e.success(response.data);
                            }).catch(function (response) {
                                e.error(response);
                            });
                    },
                    create: function (e) {
                        billingManagementService.addBillMasterItem(e)
                            .then(function successCallback(response) {
                                e.success(response.data);
                                billMasterItemDataSource.read();
                                util.createToast(util.systemMessages.success, "success");
                                $scope.billMasterItemsChanged = false;
                            }, function errorCallback(response) {
                                e.error(response);
                            });
                    },
                    update: function (e) {
                        billingManagementService.editBillMasterItem(e)
                            .then(function successCallback(response) {
                                e.success(response.data);
                                billMasterItemDataSource.read();
                                util.createToast(util.systemMessages.success, "success");
                                $scope.billMasterItemsChanged = false;
                            }, function errorCallback(response) {
                                e.error(response);
                            });
                    }
                },
                serverPaging: true,
                serverFiltering: true,
                schema: {
                    total: "totalElements",
                    data: "content",
                    model: {
                        id: "rid",
                        fields: {
                            rid: { type: "number" },
                            cptCode: { type: "string", editable: true },
                            code: { type: "string", editable: true, validation: { required: true } },
                            isActive: { type: "boolean", editable: false },
                            typeRid: { type: "lov" },
                            type: { defaultValue: {}, editable: true, validation: { required: true } }
                        }
                    }
                }
            });
            $scope.billMasterItemsGridOptions = {
                autoBind: false,
                editable: "inline",
                columns: [
                    {
                        field: "cptCode",
                        title: "{{ 'cptCode' | translate }}"
                    },
                    {
                        field: "code",
                        title: "{{ 'code' | translate }}"
                    },
                    {
                        field: "typeRid",
                        title: "{{ 'type' | translate }}",
                        editor: billItemTypeEditor,
                        filterable: {
                            ui: function (element) {
                                util.createLovFilter(element, { className: "LkpBillItemType" }, lovService.getLkpByClass);
                            }
                        },
                        template: function (dataItem) {
                            return dataItem.type.name[util.userLocale];
                        }
                    },
                    {
                        field: "isActive",
                        title: "{{ 'active' | translate }}",
                        template: "{{ #: isActive # ? 'yes' : 'no' | translate }}"
                    }
                ],
                dataSource: billMasterItemDataSource,
                dataBound: function (e) {
                    $scope.selectedBillMasterItem = null;
                },
                change: function (e) {
                    var selectedRows = this.select();
                    if (selectedRows.length > 0) {
                        $scope.selectedBillMasterItem = this.dataItem(selectedRows[0]);
                        $scope.billTestItemsChipsOptions.data = [];
                        for (var i = 0; i < $scope.selectedBillMasterItem.billTestItems.length; i++) {
                            $scope.billTestItemsChipsOptions.data.push($scope.selectedBillMasterItem.billTestItems[i].testDefinition);
                        }
                        $scope.selectedBillMasterItemIsActive = this.dataItem(selectedRows[0]).isActive;
                        if ($scope.selectedBillMasterItem.rid !== undefined) {
                            billPricingDataSource.read();
                            billTestItemsDataSource.read();
                        }
                    } else {
                        $scope.selectedBillMasterItem = null;
                    }
                }
            };

            function billItemTypeEditor(container, options) {
                dropDownListEditor(container, options, "LkpBillItemType");
            }

            $scope.addBillMasterItem = function () {
                $scope.selectedBillMasterItem = null;
                var newBillMasterItem = {
                    billClassification: $scope.selectedBillClassification,
                    billTestItems: [],
                    billPricings: [],
                    cptCode: null,
                    code: null,
                    isActive: true,
                    type: {
                        name: {}
                    }
                };
                newBillMasterItem = util.addGridRow(newBillMasterItem, billMasterItemDataSource);
                $scope.editBillMasterItem(newBillMasterItem);
            }

            $scope.editBillMasterItem = function (dataItem) {
                util.editGridRow(dataItem, "billMasterItemsGrid");
                $scope.billMasterItemsChanged = true;
            }

            $scope.saveBillMasterItemChanges = function (event) {
                if ($scope.selectedBillMasterItem.type.code !== "TEST") {
                    var title = $filter('translate')('warning');
                    var textContent = $filter('translate')('changeMasterItemTypeNotTest');
                    var confirm = $mdDialog.confirm()
                        .title(title)
                        .textContent(textContent)
                        .clickOutsideToClose(true)
                        .targetEvent(event)
                        .ok(util.systemMessages.ok)
                        .cancel(util.systemMessages.cancel);
                    $mdDialog.show(confirm)
                        .then(function () {
                            saveBillMasterItemChangesHelper()
                        }, function () {

                        });
                } else {
                    saveBillMasterItemChangesHelper();
                }
            }

            function saveBillMasterItemChangesHelper() {
                billMasterItemDataSource.sync();
            }

            $scope.activateBillMasterItem = function () {
                return billingManagementService.activateBillMasterItem($scope.selectedBillMasterItem.rid)
                    .then(function successCallback(response) {
                        util.createToast(util.systemMessages.success, "success");
                        billMasterItemDataSource.read();
                    }, function errorCallback(response) {

                    });
            }

            $scope.deactivateBillMasterItem = function () {
                return billingManagementService.deactivateBillMasterItem($scope.selectedBillMasterItem.rid)
                    .then(function successCallback(response) {
                        util.createToast(util.systemMessages.success, "success");
                        billMasterItemDataSource.read();
                    }, function errorCallback(response) {

                    });
            }

            $scope.cancelBillMasterItemChanges = function () {
                billMasterItemDataSource.cancelChanges();
                $scope.billMasterItemsChanged = false;
            }

            //#endregion

            //#region billTestItemsGrid

            $scope.runOnChangeBillTestItem = { value: true };

            var billTestItemsDataSource = new kendo.data.DataSource({
                pageSize: config.gridPageSizes[0],
                page: 1,
                transport: {
                    read: function (e) {
                        e.data = util.createFilterablePageRequest($scope.billTestItemsGridOptions.dataSource);
                        for (var i = 0; i < quickSearchFilters.length; i++) {
                            e.data.filters.push(quickSearchFilters[i]);
                        }
                        testDefinitionManagementService.getTestDefinitionPage(e.data)
                            .then(function successCallback(response) {
                                e.success(response.data);
                                if (quickSearchFilters.length > 0 && quickSearchFilters[0].field === "rid") {
                                    var grid = $("#billTestItemsGrid").data("kendoGrid");
                                    grid.select("tr:eq(0)");
                                }
                            }, function errorCallback(response) {
                                e.error(response);
                            });
                    }
                },
                serverPaging: true,
                serverFiltering: true,
                schema: {
                    total: "totalElements",
                    data: "content",
                    model: {
                        id: "rid",
                        fields: {
                            rid: { type: "number", editable: false },
                            description: { type: "string", editable: false },
                            standardCode: { type: "string", editable: false }
                        }
                    }
                }
            });

            $scope.billTestItemsGridOptions = {
                columns: [
                    {
                        selectable: true,
                        width: "50px"
                    },
                    {
                        field: "standardCode",
                        title: "{{ 'standardCode' | translate }}"
                    },
                    {
                        field: "description",
                        title: "{{ 'description' | translate }}"
                    }
                ],
                dataSource: billTestItemsDataSource,
                autoBind: false,
                selectable: false,
                dataBound: function (e) {
                    util.gridSelectionDataBound(e.sender, $scope.billTestItemsChipsOptions.data, $scope.runOnChangeBillTestItem);
                },
                change: function (e) {
                    util.gridSelectionChange(e.sender, $scope.billTestItemsChipsOptions.data, $scope.runOnChangeBillTestItem);
                }
            }

            var quickSearchFilters = [];

            var testAutocompleteCallback = function (filters) {
                quickSearchFilters = filters;
                billTestItemsDataSource.page(0);
            }

            $scope.testSearchOptions = {
                service: testDefinitionManagementService.getTestDefinitionLookup,
                callback: testAutocompleteCallback,
                skeleton: {
                    code: "standardCode",
                    description: "description"
                },
                filterList: ["description", "standardCode", "aliases", "secondaryCode"],
                staticFilters: [
                    {
                        field: "isActive",
                        value: true,
                        operator: "eq"
                    }
                ]
            };

            $scope.saveBillTestItems = function () {
                $scope.selectedBillMasterItem.billTestItemList = [];
                for (var i = 0; i < $scope.billTestItemsChipsOptions.data.length; i++) {
                    $scope.selectedBillMasterItem.billTestItemList.push({
                        testDefinition: $scope.billTestItemsChipsOptions.data[i]
                    });
                }
                var objectToSend = {};
                angular.copy($scope.selectedBillMasterItem, objectToSend);
                delete objectToSend["billPricings"];
                billingManagementService.saveBillTestItems(objectToSend)
                    .then(function successCallback(response) {
                        util.createToast(util.systemMessages.success, "success");
                    }, function errorCallback(response) {

                    });
            }

            //#endregion

            //#region billPricingsGrid

            $scope.billPricingsChanged = false;

            commonMethods.retrieveMetaData("BillPricing")
                .then(function successCallback(resp) {
                    $scope.billPricingMetaData = resp.data;
                });

            var billPricingDataSource = new kendo.data.DataSource({
                pageSize: config.gridPageSizes[0],
                page: 1,
                transport: {
                    read: function (e) {
                        var filterMap = { "billPriceList": "billPriceList.rid" };
                        e.data = util.createFilterablePageRequest($scope.billPricingsGridOptions.dataSource, filterMap);
                        if ($scope.selectedBillMasterItem !== null) {
                            var masterItemFilter = {
                                field: "billMasterItem",
                                value: $scope.selectedBillMasterItem.rid,
                                operator: "eq"
                            };
                            var addMasterItemFilter = true;
                            for (var i = 0; i < e.data.filters.length; i++) {
                                var filter = e.data.filters[i];
                                if (filter.field === masterItemFilter.field
                                    && filter.operator === masterItemFilter.operator
                                    && filter.value === masterItemFilter.value) {
                                    addMasterItemFilter = false;
                                    break;
                                }
                            }
                            if (addMasterItemFilter) {
                                e.data.filters.push(masterItemFilter);
                            }
                        }
                        billingManagementService.getBillPricingList(e)
                            .then(function successCallback(response) {
                                e.success(response.data);
                            }, function errorCallback(response) {
                                e.error(response);
                            });
                    },
                    create: function (e) {
                        billingManagementService.addBillPricing(e)
                            .then(function successCallback(response) {
                                e.success(response.data);
                                util.createToast(util.systemMessages.success, "success");
                                billPricingDataSource.read();
                                $scope.billPricingsChanged = false;
                            }, function errorCallback(response) {
                                e.error(response);
                            });
                    },
                    update: function (e) {
                        billingManagementService.editBillPricing(e)
                            .then(function successCallback(response) {
                                e.success(response.data);
                                util.createToast(util.systemMessages.success, "success");
                                billPricingDataSource.read();
                                $scope.billPricingsChanged = false;
                            }, function errorCallback(response) {
                                e.error(response);
                            });
                    }
                },
                serverPaging: true,
                serverFiltering: true,
                schema: {
                    total: "totalElements",
                    data: "content",
                    model: {
                        id: "rid",
                        fields: {
                            rid: { type: "number", editable: false },
                            price: { type: "number", editable: true, validation: { required: true } },
                            startDate: { type: "date", editable: true, validation: { required: true } },
                            endDate: { type: "date", editable: true },
                            billPriceList: { type: "lov", editable: true, validation: { required: true } }
                        }
                    }
                }
            });
            $scope.billPricingsGridOptions = {
                autoBind: false,
                editable: "inline",
                columns: [
                    {
                        field: "price",
                        title: "{{ 'price' | translate }}",
                        template: function (dataItem) {
                            return dataItem.price + " " + util.userCurrency;
                        },
                        editor: function (container, options) {
                            $('<input name="' + options.field + '"/>')
                                .appendTo(container)
                                .kendoNumericTextBox({
                                    min: 0,
                                    decimals: $scope.billPricingMetaData.price.fraction,
                                    round: false,
                                    restrictDecimals: true
                                })
                        }
                        // editor: function (container, options) {
                        //     $scope.priceData = options.model.price;
                        //     var template = '<input type="text" name="' + options.field
                        //         + '" ng-model="priceData" ng-pattern="/^[0-9]{0,' + $scope.billPricingMetaData.price.integer + '}' +
                        //         '([.][0-9]{1,' + $scope.billPricingMetaData.price.fraction + '})?$/" />';
                        //     angular.element(container).append(template);
                        // }
                    },
                    {
                        field: "startDate",
                        title: "{{ 'startDate' | translate }}",
                        format: "{0: " + config.dateFormat + "}",
                        editor: function (container, options) {
                            $('<input id="pricingStartDate" name="' + options.field + '"/>')
                                .appendTo(container)
                                .kendoDatePicker({
                                    format: config.dateFormat,
                                    change: function () {
                                        var value = this.value();
                                        var endDatePicker = $("#pricingEndDate").data("kendoDatePicker");
                                        var endDateValue = endDatePicker.value();
                                        if (endDateValue != null && endDateValue.getTime() <= value.getTime()) {
                                            endDatePicker.value(null);
                                            endDatePicker.trigger("change");
                                        }
                                        endDatePicker.min(value);
                                    }
                                });
                        }
                    },
                    {
                        field: "endDate",
                        title: "{{ 'endDate' | translate }}",
                        format: "{0: " + config.dateFormat + "}",
                        editor: function (container, options) {
                            $('<input id="pricingEndDate" name="' + options.field + '"/>')
                                .appendTo(container)
                                .kendoDatePicker({
                                    format: config.dateFormat,
                                    min: options.model.startDate
                                });
                        }
                    },
                    {
                        field: "billPriceList",
                        title: "{{ 'priceList' | translate }}",
                        filterable: {
                            ui: function (element) {
                                util.createLovFilter(element, null, priceListService.getBillPriceLists);
                            }
                        },
                        template: function (dataItem) {
                            if (dataItem.billPriceList === null) {
                                return "{{ 'none' | translate }}";
                            }
                            return dataItem.billPriceList.name[util.userLocale];
                        },
                        editor: billPriceListEditor
                    }
                ],
                dataSource: billPricingDataSource,
                dataBound: function (e) {
                    $scope.selectedBillPricing = null;
                },
                change: function (e) {
                    var selectedRows = this.select();
                    if (selectedRows.length > 0) {
                        $scope.selectedBillPricing = this.dataItem(selectedRows[0]);
                    } else {
                        $scope.selectedBillPricing = null;
                    }
                }
            };

            $scope.addBillPricing = function () {
                $scope.selectedBillPricing = null;
                var newBillPricing = {
                    billMasterItem: $scope.selectedBillMasterItem,
                    startDate: new Date(),
                    endDate: null,
                    price: 0,
                    billPriceList: {
                        name: {}
                    }
                };
                newBillPricing = util.addGridRow(newBillPricing, billPricingDataSource);
                $scope.editBillPricing(newBillPricing);
            }

            $scope.editBillPricing = function (dataItem) {
                util.editGridRow(dataItem, "billPricingsGrid");
                $scope.billPricingsChanged = true;
            }

            $scope.saveBillPricingChanges = function () {
                billPricingDataSource.sync();
            }

            $scope.cancelBillPricingChanges = function () {
                billPricingDataSource.cancelChanges();
                $scope.billPricingsChanged = false;
            }

            $scope.showBillPriceListDialog = function (ev) {
                $mdDialog.show({
                    controller: ["$scope", function ($scope) {
                        $scope.transFields = {
                            name: util.getTransFieldLanguages("name", "name", null, true)
                        };
                        $scope.createBillPriceList = function () {
                            if ($scope.billingPriceListForm.$invalid) {
                                return;
                            }
                            var newBillingPriceList = {
                                isDefault: $scope.isDefault,
                                name: {}
                            }

                            for (var i in util.languages) {
                                var nameLang = $scope.transFields.name[i];
                                newBillingPriceList.name[nameLang.language] = nameLang.value;
                            }
                            priceListService.createBillPriceList(newBillingPriceList)
                                .then(function () {
                                    util.createToast(util.systemMessages.success, "success");
                                    $mdDialog.cancel();
                                });
                        };
                    }],
                    templateUrl: './' + config.lisDir + '/modules/dialogs/create-price-list.html',
                    parent: angular.element(document.body),
                    targetEvent: ev,
                    clickOutsideToClose: true,
                    fullscreen: false // Only for -xs, -sm breakpoints.
                });
            };

            //#endregion

            //#region dropDownListEditor 

            function getLkpByClass(e, className) {
                lovService.getLkpByClass({ className: className }).then(function successCallback(response) {
                    e.success(response);
                }).catch(function (response) {
                    e.error(response);
                });
            }

            function getParentBillClassifications(e, options) {
                billingManagementService.getParentBillClassifications(options.model.rid)
                    .then(function successCallback(response) {
                        response.data.unshift({
                            rid: null
                        });
                        e.success(response.data);
                    }).catch(function (response) {
                        e.error(response);
                    });
            }

            function getBillPriceLists(e, options) {
                priceListService.getBillPriceLists()
                    .then(function successCallback(data) {
                        e.success(data);
                    }).catch(function (response) {
                        e.error(response);
                    });
            }

            function dropDownListTemplate(type, dataItem) {
                switch (type) {
                    default:
                    case "lkp":
                    case "billPriceLists":
                        if (dataItem.rid === null) {
                            return $filter('translate')('none');
                        }
                        return dataItem.name[util.userLocale];
                    case "parentBillClassifications":
                        if (dataItem.rid === null) {
                            return $filter('translate')('none');
                        }
                        return dataItem.name + " | " + dataItem.code;
                }
            }

            function parentClassificationEditor(container, options) {
                dropDownListEditorHelper(container, options, "parentBillClassifications");
            }

            function billPriceListEditor(container, options) {
                dropDownListEditorHelper(container, options, "billPriceLists");
            }

            function dropDownListEditor(container, options, className) {
                dropDownListEditorHelper(container, options, "lkp", className);
            }

            function dropDownListEditorHelper(container, options, type, className) {
                var dropDownListDataSource = new kendo.data.DataSource({
                    schema: {
                        model: {
                            id: "rid"
                        }
                    },
                    transport: {
                        read: function (e) {
                            switch (type) {
                                default:
                                case "lkp":
                                    getLkpByClass(e, className);
                                    break;
                                case "parentBillClassifications":
                                    getParentBillClassifications(e, options);
                                    break;
                                case "billPriceLists":
                                    getBillPriceLists(e, options);
                                    break;
                            }
                        }
                    }
                });

                var fieldName = options.field;
                if (fieldName.endsWith("Rid")) {
                    fieldName = fieldName.substring(0, fieldName.indexOf("Rid"));
                }

                $('<input name="' + fieldName + '"/>')
                    .appendTo(container)
                    .kendoDropDownList({
                        dataValueField: "rid",
                        valueTemplate: function (dataItem) {
                            return dropDownListTemplate(type, dataItem);
                        },
                        template: function (dataItem) {
                            return dropDownListTemplate(type, dataItem);
                        },
                        dataSource: dropDownListDataSource,
                        dataBound: function (e) {
                            var selectedIndex = e.sender.select();
                            e.sender.select(selectedIndex >= 0 ? selectedIndex : 0);
                            e.sender.trigger("change");
                        }
                    });
            }

            //#endregion
        }
    ]);
});

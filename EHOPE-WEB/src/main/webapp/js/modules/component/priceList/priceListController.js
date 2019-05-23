define(['app', 'config', 'util'], function (app, config, util) {
    'use strict';
    app.controller('priceListCtrl', [
        '$scope',
        'priceListService',
        function (
            $scope,
            priceListService
        ) {
            $scope.billPriceList = null;

            $scope.refreshGrid = function () {
                $scope.billPriceListsGrid.dataSource.read();
                $scope.billPriceListsGrid.refresh();
            };

            var dataSource = new kendo.data.DataSource({
                pageSize: config.gridPageSizes[0],
                page: 1,
                transport: {
                    read: function (e) {
                        priceListService.getBillPriceLists().then(function (data) {
                            e.success(data);
                        }).catch(function (error) {
                            e.error(error);
                        });
                    },
                    create: function (e) {
                        priceListService.createBillPriceList(e.data).then(function () {
                            util.createToast(util.systemMessages.success, "success");
                            $scope.refreshGrid();
                        }).catch(function (error) {
                            e.error(error);
                        });
                    },
                    update: function (e) {
                        priceListService.updateBillPriceList(e.data).then(function () {
                            util.createToast(util.systemMessages.success, "success");
                            $scope.refreshGrid();
                        }).catch(function (error) {
                            e.error(error);
                        });
                    },
                    destroy: function (e) {
                        priceListService.deleteBillPriceList(e.data).then(function () {
                            util.createToast(util.systemMessages.success, "success");
                            $scope.refreshGrid();
                        }).catch(function (error) {
                            e.error(error);
                        });
                    }
                },
                schema: {
                    parse: function (response) {
                        for (var idx = 0; idx < response.length; idx++) {
                            response[idx]["nameLocale"] = response[idx].name[util.userLocale];
                        }
                        return response;
                    },
                    model: {
                        id: "rid",
                        fields: {
                            "isDefault": {
                                type: "boolean",
                                editable: true,
                                nullable: false
                            },
                            "nameLocale": {
                                type: "string",
                                editable: true,
                                nullable: false
                            }
                        }
                    }
                }
            });

            $scope.billPriceListsGridOptions = {
                columns:
                    [
                        {
                            field: "nameLocale",
                            title: util.systemMessages.name,
                            editor: function (container, options) {
                                util.createTransFieldEditor(container, options, "name");
                            }
                        },
                        {
                            field: "isDefault",
                            title: util.systemMessages.default,
                            template: function (dataItem) {
                                return dataItem.isDefault ? util.systemMessages.yes : util.systemMessages.no;
                            }
                        }
                    ],
                editable: "inline",
                dataSource: dataSource,
                dataBound: function (e) {
                    var gridData = e.sender.dataSource.data();
                    var billPriceListDefaultUID = "";
                    for (var billPriceKey in gridData) {
                        if (gridData[billPriceKey].isDefault) {
                            billPriceListDefaultUID = gridData[billPriceKey].uid;
                        }
                    }

                    $("#billPriceListsGrid").kendoTooltip({
                        filter: "tr[data-uid='" + billPriceListDefaultUID + "']",
                        position: "bottom",
                        content: function () {
                            return util.systemMessages.billingPriceListDefaultInfo;
                        }
                    }).data("kendoTooltip");

                },
                change: function (e) {
                    $scope.billPriceList = $scope.billPriceListsGrid.dataItem($scope.billPriceListsGrid.select());
                    if (disableIsDefaultRow($scope.billPriceList)) {
                        e.sender.element.find("tr[data-uid='" + $scope.billPriceList.uid + "']")
                            .removeClass('k-state-selected k-state-selecting');
                        $scope.billPriceList = null;// so we disable the action buttons
                        return;
                    }
                }
            };

            $scope.saveBillPriceList = function () {
                dataSource.sync();
                $scope.billPriceList = null;
                $scope.billPriceListChanged = false;
            };

            $scope.addBillPriceList = function () {
                var newBillPriceList = {
                    nameLocale: "",
                    isDefault: false
                };
                newBillPriceList = util.addGridRow(newBillPriceList, dataSource);
                $scope.editBillPriceList(newBillPriceList);
            };

            $scope.editBillPriceList = function (dataItem) {
                util.editGridRow(dataItem, "billPriceListsGrid");
                $scope.billPriceListChanged = true;
            };

            $scope.deleteBillPriceList = function () {
                util.deleteGridRow($scope.billPriceList, dataSource);
                $scope.billPriceList = null;
            };

            $scope.cancelBillPriceListChanges = function () {
                dataSource.cancelChanges();
                $scope.billPriceListChanged = false;
            };

            // callback function for disabling the row
            var disableIsDefaultRow = function (item) {
                return item.isDefault;
            };
        }
    ]);
});

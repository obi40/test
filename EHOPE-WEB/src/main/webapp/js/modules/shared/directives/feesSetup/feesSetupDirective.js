define(['app', 'util', 'config'], function (app, util, config) {
    'use strict';
    app.directive('feesSetup', function () {
        return {
            restrict: 'E', // This means that it will be used as an element.
            replace: true,
            scope: {
                options: "=options"
            },
            templateUrl: "./" + config.lisDir + "/modules/shared/directives/feesSetup/fees-setup-view.html",
            controller: ['$scope', '$filter', 'priceListService', 'commonMethods',
                function ($scope, $filter, priceListService, commonMethods) {

                    $scope.priceChanged = false;

                    $scope.options.testPrices = [];
                    var testMasterItem = null;
                    try {
                        for (var i = 0; i < $scope.options.testDefinition.billTestItems.length; i++) {
                            var billTestItem = $scope.options.testDefinition.billTestItems[i];
                            var billMasterItem = billTestItem.billMasterItem;
                            if (billMasterItem.type.code === "TEST") {
                                testMasterItem = angular.copy(billMasterItem);
                                delete testMasterItem.billPricings;
                                for (var j = 0; j < billMasterItem.billPricings.length; j++) {
                                    var billPricing = billMasterItem.billPricings[j];
                                    // billPricing.billMasterItemRid = billMasterItem.rid;
                                    // billPricing.billTestItemRid = billTestItem.rid;
                                    billPricing.billMasterItem = testMasterItem;
                                    $scope.options.testPrices.push(billPricing);
                                }
                                //only one masterItem of type TEST is allowed
                                break;
                            }
                        }
                    } catch (e) { }

                    commonMethods.retrieveMetaData("BillPricing")
                        .then(function successCallback(resp) {
                            $scope.billPricingMetaData = resp.data;
                        });

                    $scope.addPrice = function () {
                        var grid = $("#priceGrid").data("kendoGrid");
                        grid.addRow();
                    };

                    $scope.editPrice = function (dataItem) {
                        if (!$scope.selectedPrice) {
                            return;
                        }
                        var grid = $("#priceGrid").data("kendoGrid");
                        grid.editRow(dataItem);
                    };

                    $scope.savePrice = function () {
                        var grid = $("#priceGrid").data("kendoGrid");
                        grid.saveChanges();
                    };

                    $scope.cancelPrice = function () {
                        var grid = $("#priceGrid").data("kendoGrid");
                        grid.cancelChanges();
                        $scope.priceChanged = false;
                    };

                    // var changedItemUid = null;

                    var priceDataSource = new kendo.data.DataSource({
                        pageSize: config.gridPageSizes[0],
                        page: 1,
                        transport: {
                            read: function (e) {
                                e.success($scope.options.testPrices);
                            },
                            create: function (e) {
                                var price = e.data;
                                e.success(price);
                                // if (checkDateIntersection()) {
                                //     e.success(price);
                                // } else {
                                //     e.error();
                                // }
                            },
                            update: function (e) {
                                var price = e.data;
                                e.success(price);
                                // if (checkDateIntersection()) {
                                //     e.success(price);
                                // } else {
                                //     e.error();
                                // }
                            }
                        },
                        sync: function (e) {
                            $scope.priceChanged = false;
                            $scope.options.testPrices = e.sender.data();
                        },
                        change: function (e) {
                            // if (e.action === "itemchange") {
                            //     changedItemUid = e.items[0].uid;
                            // }
                        },
                        schema: {
                            model: {
                                id: "rid",
                                fields: {
                                    billPriceList: { type: "lov" },
                                    price: { type: "number" },
                                    startDate: { type: "date" },
                                    endDate: { type: "date", defaultValue: null },
                                    billMasterItem: { defaultValue: testMasterItem }
                                }
                            }
                        }
                    });

                    // function checkDateIntersection() {
                    //     var data = priceDataSource.data();
                    //     var changedItem = priceDataSource.getByUid(changedItemUid);
                    //     var valid = true;
                    //     for (var i = 0; i < data.length; i++) {
                    //         var item = data[i];
                    //         if (item.uid !== changedItem.uid && item.billPriceList.rid === changedItem.billPriceList.rid) {
                    //             if (intersectedPeriods(changedItem, item)) {
                    //                 valid = false;
                    //                 break;
                    //             }
                    //         }
                    //     }
                    //     return valid;
                    // }

                    // function intersectedPeriods(item1, item2) {
                    //     var startDate1 = item1.startDate;
                    //     var endDate1 = item1.endDate;
                    //     var startDate2 = item2.startDate;
                    //     var endDate2 = item2.endDate;

                    //     if (startDate1 == null || startDate2 == null || (endDate1 == null && endDate2 == null)) {
                    //         throw new BusinessException("invalid parameters", "invalidParameters", ErrorSeverity.ERROR);
                    //     }

                    //     console.log(startDate1, endDate1, startDate2, endDate2);

                    //     // if ((endDate2 != null && isBetween(startDate1, startDate2, endDate2))
                    //     //     || (endDate1 != null && isBetween(startDate2, startDate1, endDate1))
                    //     //     || (endDate1 == null && isAfterOrEqual(startDate2, startDate1))
                    //     //     || (endDate2 == null && isAfterOrEqual(startDate1, startDate2))) {
                    //     //     return true;
                    //     // }
                    //     // return false;
                    // }

                    $scope.priceGridOptions = {
                        dataSource: priceDataSource,
                        editable: "inline",
                        columns: [
                            {
                                field: "billPriceList",
                                title: "{{ 'priceList' | translate}}",
                                filterable: {
                                    ui: function (element) {
                                        util.createLovFilter(element, null, priceListService.getBillPriceLists);
                                    }
                                },
                                template: function (dataItem) {
                                    var template = "{{ 'none' | translate }}";
                                    try {
                                        template = dataItem.billPriceList.name[util.userLocale];
                                        if (dataItem.billPriceList.isDefault) {
                                            template += " ({{ 'default' | translate }})";
                                        }
                                    } catch (e) { }
                                    return template;
                                },
                                editor: billPriceListEditor
                            },
                            {
                                field: "price",
                                title: "{{ 'price' | translate }}",
                                template: function (dataItem) {
                                    return dataItem.price + " " + util.userCurrency;
                                },
                                editor: function (container, options) {
                                    var requiredKeyword = "";
                                    if ($scope.billPricingMetaData.price.notNull) {
                                        requiredKeyword = "required";
                                    }
                                    $('<input ' + requiredKeyword + ' name="' + options.field + '"/>')
                                        .appendTo(container)
                                        .kendoNumericTextBox({
                                            min: 0,
                                            decimals: $scope.billPricingMetaData.price.fraction,
                                            round: false,
                                            restrictDecimals: true
                                        })
                                }
                            },
                            {
                                field: "startDate",
                                title: "{{ 'startDate' | translate }}",
                                format: "{0: " + config.dateFormat + "}",
                                editor: function (container, options) {
                                    var requiredKeyword = "";
                                    if ($scope.billPricingMetaData.startDate.notNull) {
                                        requiredKeyword = "required";
                                    }
                                    $('<input ' + requiredKeyword + ' id="pricingStartDate" name="' + options.field + '"/>')
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
                            }
                        ],
                        dataBinding: function () {
                            $scope.selectedPrice = null;
                        },
                        edit: function (e) {
                            e.sender.select($("tr[data-uid=" + e.model.uid + "]"));
                            $scope.priceChanged = true;
                        },
                        change: function (e) {
                            var selectedRows = e.sender.select();
                            if (selectedRows.length > 0) {
                                $scope.selectedPrice = this.dataItem(selectedRows[0]);
                            } else {
                                $scope.selectedPrice = null;
                            }
                        }
                    };

                    function billPriceListEditor(container, options) {
                        var dropDownListDataSource = new kendo.data.DataSource({
                            schema: {
                                model: {
                                    id: "rid"
                                }
                            },
                            transport: {
                                read: function (e) {
                                    priceListService.getBillPriceLists()
                                        .then(function successCallback(data) {
                                            e.success(data);
                                        }).catch(function (response) {
                                            e.error(response);
                                        });
                                }
                            }
                        });

                        var fieldName = options.field;
                        if (fieldName.endsWith("Rid")) {
                            fieldName = fieldName.substring(0, fieldName.indexOf("Rid"));
                        }

                        var requiredKeyword = "";
                        if ($scope.billPricingMetaData.billPriceList.notNull) {
                            requiredKeyword = "required";
                        }
                        $('<input ' + requiredKeyword + ' name="' + fieldName + '"/>')
                            .appendTo(container)
                            .kendoDropDownList({
                                dataValueField: "rid",
                                valueTemplate: function (dataItem) {
                                    var template = $filter('translate')('none');
                                    try {
                                        template = dataItem.name[util.userLocale];
                                        if (dataItem.isDefault) {
                                            template += " (" + $filter('translate')('default') + ")";
                                        }
                                    } catch (e) { }
                                    return template;
                                },
                                template: function (dataItem) {
                                    var template = $filter('translate')('none');
                                    try {
                                        template = dataItem.name[util.userLocale];
                                        if (dataItem.isDefault) {
                                            template += " (" + $filter('translate')('default') + ")";
                                        }
                                    } catch (e) { }
                                    return template;
                                },
                                dataSource: dropDownListDataSource,
                                dataBound: function (e) {
                                    var selectedIndex = e.sender.select();
                                    e.sender.select(selectedIndex >= 0 ? selectedIndex : 0);
                                    e.sender.trigger("change");
                                }
                            });
                    }
                }]
        }
    });
});
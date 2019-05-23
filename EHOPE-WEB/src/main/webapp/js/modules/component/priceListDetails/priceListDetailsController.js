define(['app', 'config', 'util'], function (app, config, util) {
    'use strict';
    app.controller('priceListDetailsCtrl', [
        '$scope',
        'priceListDetailsService',
        'priceListService',
        '$mdDialog',
        'testDefinitionManagementService',
        '$timeout',
        function (
            $scope,
            priceListDetailsService,
            priceListService,
            $mdDialog,
            testDefinitionManagementService,
            $timeout
        ) {
            $scope.masterItem = null;
            $scope.masterItemGridOptions = null;
            var priceLists = [];
            var pricingFieldName = "pricing_";
            $scope.masterItemChanged = false;
            var defaultPriceList = null;
            var dataSource = null;
            var masterItemGridColumns = [
                {
                    field: "standardCode",
                    title: util.systemMessages.standardCode,
                    width: 300,
                    locked: true
                },
                {
                    field: "description",
                    title: util.systemMessages.description,
                    width: 700,
                    locked: true
                },
                {
                    field: "code",
                    title: util.systemMessages.code,
                    width: 200,
                    locked: true,
                    hidden: true
                },
                {
                    field: "isActive",
                    title: util.systemMessages.active,
                    width: 100,
                    locked: true,
                    hidden: true,
                    template: function (dataItem) { return dataItem.isActive ? util.systemMessages.yes : util.systemMessages.no; }
                }
            ];
            var masterItemGridModel = {
                id: "rid",
                fields: {
                    rid: { type: "number" },
                    code: { type: "string" },
                    standardCode: { type: "string" },
                    description: { type: "string" },
                    isActive: { type: "boolean" }
                }
            };
            var autocompleteFilters = [];
            function autocompleteCallback(filters) {
                if (filters == null) {
                    return;
                }
                autocompleteFilters = [];
                if (filters.length == 1) {
                    autocompleteFilters.push({ "field": "testRid", "value": filters[0].value });
                } else {
                    for (var idx = 0; idx < filters.length; idx++) {
                        autocompleteFilters.push({ "field": filters[idx].field, "value": filters[idx].value });
                    }
                }
                $scope.refreshGrid();
            }
            $scope.testDefinitionSearchOptions = {
                service: testDefinitionManagementService.getTestDefinitionLookup,
                callback: autocompleteCallback,
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

            $scope.refreshGrid = function () {
                dataSource.read();
            };

            function prepareMasterItem(masterItem, priceListColumns) {
                //assuming that we only hook master item with a single test
                if (masterItem.billTestItems != null &&
                    masterItem.billTestItems.length > 0 &&
                    masterItem.billTestItems[0].testDefinition != null) {
                    masterItem["standardCode"] = masterItem.billTestItems[0].testDefinition.standardCode;
                    masterItem["description"] = masterItem.billTestItems[0].testDefinition.description;
                } else {
                    masterItem["standardCode"] = "";
                    masterItem["description"] = "";
                }
                for (var i = 0; i < priceListColumns.length; i++) {
                    var obj = priceListColumns[i];
                    masterItem[obj.column.field] = getActivePricing(masterItem, obj.priceList, obj.column.field);
                }
            }

            $scope.pricingInfo = function (event, masterItemRid, fieldName) {

                var masterItem = dataSource.get(masterItemRid);
                var priceList = masterItem[fieldName + "_list"][0].billPriceList;
                var data = {
                    masterItem: masterItem,
                    priceList: priceList
                };
                $mdDialog.show({
                    controller: ["$scope", "$mdDialog", "data", function ($scope, $mdDialog, data) {
                        $scope.masterItem = angular.copy(data.masterItem);
                        $scope.priceList = data.priceList;
                        $scope.userLocale = util.userLocale;
                        $scope.currency = util.userCurrency;
                        if ($scope.masterItem.billPricings.length > 0) {
                            $scope.masterItem.billPricings = $scope.masterItem[fieldName + "_list"];
                            $scope.masterItem.billPricings.sort(function (a, b) {
                                return new Date(a.startDate) - new Date(b.startDate);
                            });
                        }

                        $scope.cancel = function () {
                            $mdDialog.cancel();
                        };
                    }],
                    templateUrl: './' + config.lisDir + '/modules/dialogs/master-item-details.html',
                    parent: angular.element(document.body),
                    targetEvent: event,
                    clickOutsideToClose: true,
                    locals: { "data": data }
                }).then(function () { }, function () { });
            };
            function pricingTemplate(dataItem, fieldName) {
                var priceLabel = "<span class='bold $'>#</span>";
                var value = "";
                if (dataItem[fieldName] != null) {
                    value = dataItem[fieldName].price + " " + util.userCurrency;
                } else {
                    value = "(" + util.systemMessages.expired + ")";
                    priceLabel = priceLabel.replace("$", "expired");//add a class
                }
                priceLabel = priceLabel.replace("#", value);
                // there are no pricing for this item in this price list
                var hasPricings = dataItem[fieldName + "_list"].length < 1;

                return '<div class="pricing ' + (hasPricings ? "no-display" : "") + '">' +
                    priceLabel +
                    '<span class="info">' +
                    '<md-icon class="clickable-item" md-font-icon="fas fa-info-circle" ng-click="pricingInfo($event,' + dataItem.rid + ',\'' + fieldName + '\')">' +
                    '<md-tooltip md-direction="top">{{"info" | translate}}</md-tooltip>' +
                    '</md-icon>' +
                    '<span>' +
                    '</div>';
            }
            function generateColumn(pl, fieldName) {
                var obj = {
                    field: fieldName,
                    title: pl.name[util.userLocale],
                    width: 300,
                    template: function (dataItem) {
                        return pricingTemplate(dataItem, fieldName);
                    }
                };
                return obj;
            }
            priceListService.getBillPriceLists().then(function (data) {
                priceLists = data;
                var priceListColumns = [];// so we sort these array without affecting the original order of the other grid columns
                var defaultPL = null;//just so it can has the same operation as other price lists but will be added later
                for (var idx = 0; idx < data.length; idx++) {
                    var pl = data[idx];
                    var fieldName = pricingFieldName + pl.rid;
                    var obj = generateColumn(pl, fieldName);

                    masterItemGridModel.fields[fieldName] = { type: "object" };
                    if (pl.isDefault) {
                        defaultPriceList = pl;//store the default price list globally 
                        obj.title = obj.title + " (" + util.systemMessages.default + ") ";
                        defaultPL = obj;
                    } else {
                        priceListColumns.push(obj);
                    }
                }
                //sort by name of price list
                priceListColumns.sort(function (a, b) {
                    if (a.title < b.title) return -1;
                    if (a.title > b.title) return 1;
                    return 0;
                });
                priceListColumns.splice(0, 0, defaultPL);//add default price list to first place
                masterItemGridColumns = masterItemGridColumns.concat(priceListColumns);
                initDataSource();
                $scope.masterItemGridOptions = {
                    height: 500,
                    columns: masterItemGridColumns,
                    dataSource: dataSource,
                    filterable: false,
                    sortable: false,
                    dataBound: function (e) {
                        $timeout(function () {
                            //to change grid's fixed height according to the fetched rows to fix the scrolling issue in the grid
                            var lockedContent = e.sender.wrapper.children(".k-grid-content-locked")
                            var content = e.sender.wrapper.children(".k-grid-content");
                            e.sender.wrapper.height("");
                            lockedContent.height("");
                            content.height("");
                            e.sender.wrapper.height(e.sender.wrapper.height());
                            e.sender.resize();
                        });
                    },
                    change: function (e) {
                        $scope.masterItem = e.sender.dataItem(e.sender.select());
                    }
                };
            });

            function getActivePricing(masterItem, priceList, fieldName) {
                //If the returned value is null then there are no pricings in masterItem that belongs to this priceList
                masterItem[fieldName + "_list"] = [];
                var activePricing = null;
                if (masterItem.billPricings.length == 0) {
                    return activePricing;
                }
                var priceListPricings = [];
                //Collect all pricings that are in masterItem that belongs to the priceList
                for (var idx = 0; idx < masterItem.billPricings.length; idx++) {
                    var pricing = masterItem.billPricings[idx];
                    if (pricing.billPriceList.rid == priceList.rid) {
                        priceListPricings.push(pricing);
                    }
                }
                if (priceListPricings.length < 1) {
                    return activePricing;
                }

                var nullEndDatePricing = null;
                //get the active one
                for (var idx = 0; idx < priceListPricings.length; idx++) {
                    var pricing = priceListPricings[idx];
                    masterItem[fieldName + "_list"].push(pricing);
                    if (pricing.endDate == null) {
                        nullEndDatePricing = pricing;
                        continue;
                    }
                    var pricingStartDate = new Date(pricing.startDate);
                    var currentDate = new Date();
                    var pricingEndDate = new Date(pricing.endDate);
                    if (kendo.date.isInDateRange(currentDate, pricingStartDate, pricingEndDate)) {
                        activePricing = pricing;
                    }
                }
                //Get the null one which is the one that has a start date but no end date
                if (activePricing == null && nullEndDatePricing != null) {
                    activePricing = nullEndDatePricing;
                }
                return activePricing;
            }


            function initDataSource() {
                dataSource = new kendo.data.DataSource({
                    pageSize: config.gridPageSizes[0],
                    page: 1,
                    serverPaging: true,
                    serverFiltering: true,
                    transport: {
                        read: function (e) {
                            e.data = util.createFilterablePageRequest($scope.masterItemGridOptions.dataSource);
                            if (autocompleteFilters.length > 0) {
                                e.data["filters"] = autocompleteFilters;
                            }
                            priceListDetailsService.getMasterItemPriceListByTest(e.data).then(function (response) {
                                e.success(response.data);
                            }).catch(function (error) {
                                e.error(error);
                            });
                        }
                    },
                    schema: {
                        parse: function (data) {
                            var priceListColumns = [];
                            for (var idx = 0; idx < masterItemGridColumns.length; idx++) {
                                var col = masterItemGridColumns[idx];
                                if (col.field.indexOf(pricingFieldName) == -1) {
                                    continue;
                                }
                                var rid = parseInt(col.field.substring(col.field.indexOf("_") + 1));
                                var pl = null;
                                for (var i = 0; i < priceLists.length; i++) {
                                    if (rid == priceLists[i].rid) {
                                        pl = priceLists[i];
                                        break;
                                    }
                                }
                                priceListColumns.push({
                                    column: col,
                                    priceList: pl//to get data for getActivePricing(...) to run
                                });
                            }
                            for (var idx = 0; idx < data.content.length; idx++) {
                                var masterItem = data.content[idx];
                                prepareMasterItem(masterItem, priceListColumns);
                            }
                            return data;
                        },
                        data: "content",
                        total: "totalElements",
                        model: masterItemGridModel
                    }
                });
            }
        }
    ]);
});



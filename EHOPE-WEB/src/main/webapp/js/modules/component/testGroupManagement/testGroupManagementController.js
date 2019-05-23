define(['app', 'config', 'util'], function (app, config, util) {
    'use strict';
    app.controller('testGroupManagementCtrl', [
        '$scope',
        'testGroupManagementService',
        'testDefinitionManagementService',
        'commonMethods',
        'priceListService',
        function (
            $scope,
            testGroupManagementService,
            testDefinitionManagementService,
            commonMethods,
            priceListService
        ) {
            $scope.selectedGroup = null;
            $scope.selectedGroupDetail = null;
            $scope.groupDetailChanged = false;
            $scope.isEdit = false;
            $scope.groupMetaData = null;
            var defaultPricelist = null;
            var runOnChange = { value: true };
            var testSearchFilters = [];
            var priceLists = null;
            var dummyRid = -1;
            $scope.userCurrency = util.userCurrency;
            $scope.testChipsOptions = {
                data: [],
                label: "standardCode",
                onRemove: function (chip) {
                    var gridData = testDataSource.data();
                    for (var idx = 0; idx < gridData.length; idx++) {
                        var item = gridData[idx];
                        if (item.rid !== chip.rid) {
                            continue;
                        }
                        var grid = $("#testGrid").data("kendoGrid");
                        util.removeGridChip(chip, grid);
                        return;//if we found the test in the grid then trigger the box that will trigger the calculateFinalPrice otherwise trigger it
                    }
                    calculateFinalPrice(chip, false);
                }
            };
            $scope.testSearchOptions = {
                service: testDefinitionManagementService.getTestDefinitionLookup,
                callback: function (filters) {
                    if ($scope.testSearchOptions.selectedItem != null && $scope.testSearchOptions.selectedItem.rid !== -1) {
                        $scope.testSearchOptions.reset();
                        var filterPage = {
                            "filters": [{ "field": "rid", "value": $scope.testSearchOptions.selectedItem.rid, "operator": "eq" }],
                            "page": 0,
                            "size": 1,
                            "sortList": []
                        };
                        testDefinitionManagementService.getTestsDefaultPricingPage(filterPage).then(function (response) {
                            var test = response.data.content[0];
                            if (test == null) {
                                return;
                            }
                            setTestPrices(test);
                            addToChips(test);
                            var gridData = testDataSource.data();
                            for (var idx = 0; idx < gridData.length; idx++) {
                                var item = gridData[idx];
                                if (item.rid !== test.rid) {
                                    continue;
                                }
                                var row = $("#testGrid").find("tr[data-uid='" + item.uid + "']");
                                var checkBox = row.find("input[type=checkbox]");
                                checkBox.trigger("click");
                                return;//if we found the test in the grid then trigger the box that will trigger the calculateFinalPrice otherwise trigger it
                            }
                            calculateFinalPrice(test, true);
                        });
                    } else {
                        testSearchFilters = filters;
                        testDataSource.page(0);
                    }
                },
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

            commonMethods.retrieveMetaData("TestGroup").then(function (response) {
                $scope.groupMetaData = response.data;
            });
            priceListService.getBillPriceLists().then(function (data) {
                priceLists = [];
                var defaultPricelistIdx = null;
                for (var idx = 0; idx < data.length; idx++) {
                    if (data[idx].isDefault) {
                        defaultPricelistIdx = idx;
                        defaultPricelist = angular.copy(data[idx]);
                        data[idx]["nameLocale"] = data[idx].name[util.userLocale] + "(" + util.systemMessages.default + ")";
                    } else {
                        data[idx]["nameLocale"] = data[idx].name[util.userLocale];
                        priceLists.push(data[idx]);
                    }
                }
                priceLists.splice(0, 0, data[defaultPricelistIdx]);
            });
            function generatePriceListsTemplate(map) {
                var result = "";
                for (var idx = 0; idx < priceLists.length; idx++) {
                    var pricelist = priceLists[idx];
                    if (map[pricelist.rid] == null) {
                        continue;
                    }
                    var template = "";
                    template += pricelist.nameLocale + "= " + "<span class='bold'>" + map[priceLists[idx].rid] + " " + util.userCurrency + "</span>";
                    template += "<br>";
                    result += template;
                }
                return result;
            }

            function addToChips(test) {
                for (var idx = 0; idx < $scope.testChipsOptions.data.length; idx++) {
                    if (test.rid === $scope.testChipsOptions.data[idx].rid) {
                        return;
                    }
                }
                $scope.testChipsOptions.data.push(test);
            }

            function generateSelectedTests() {
                var result = [];
                for (var idx = 0; idx < $scope.testChipsOptions.data.length; idx++) {
                    result.push({
                        rid: dummyRid--,
                        testDefinition: $scope.testChipsOptions.data[idx],
                        testGroup: null
                    });
                }
                return result;
            }

            function setGroupTotalPrice(group, pricelistRid, tests) {
                //either group or group detail
                var tests = tests != null ? tests : $scope.testChipsOptions.data;
                group.totalPrice = 0;
                for (var idx = 0; idx < tests.length; idx++) {
                    var obj = tests[idx];
                    var price = getTestPrice(obj, pricelistRid);
                    group.totalPrice += price;
                }
            }
            function getTestPrice(test, pricelistRid) {
                //get the price of this test by this pricelist or fallback to default price
                var price = null;
                var defaultPrice = null;
                for (var i = 0; i < test.prices.length; i++) {
                    if (pricelistRid === test.prices[i].pricelist.rid) {
                        price = test.prices[i].price;
                        break;
                    } else if (defaultPricelist.rid === test.prices[i].pricelist.rid) {
                        defaultPrice = test.prices[i].price;
                    }
                }
                if (price === null) {
                    price = defaultPrice;
                }
                return price;
            }
            function calculateFinalPrice(test, isChecked) {
                if ($scope.selectedGroup == null) {
                    return;
                }
                //sometimes setGroupTotalPrice(...) is fired when the test is already removed in 
                //kendo change and bind event, so we make sure that we only add a test price if it is not already added
                //same for removing a test
                var modifyTotalPrice = true;
                if (isChecked) {
                    var exists = false;
                    for (var idx = 0; idx < $scope.testChipsOptions.data.length; idx++) {
                        if ($scope.testChipsOptions.data[idx].rid === test.rid) {
                            exists = true;
                            break;
                        }
                    }
                    if (exists) {
                        modifyTotalPrice = false;
                    }
                } else {
                    var notExists = true;
                    for (var idx = 0; idx < $scope.testChipsOptions.data.length; idx++) {
                        if ($scope.testChipsOptions.data[idx].rid === test.rid) {
                            notExists = false;
                            break;
                        }
                    }
                    if (notExists) {
                        modifyTotalPrice = false;
                    }
                }
                var upOrDown = isChecked ? 1 : -1;
                var testDefaultPrice = getTestPrice(test, defaultPricelist.rid) * upOrDown;
                setGroupTotalPrice($scope.selectedGroup, defaultPricelist.rid);
                if (modifyTotalPrice) {
                    $scope.selectedGroup.totalPrice += util.round(testDefaultPrice);
                }
                $scope.caluclateGroupDiscounts($scope.selectedGroup);
                var details = groupDetailDataSource.data();
                for (var idx = 0; idx < details.length; idx++) {
                    var groupDetail = details[idx];
                    var testPrice = getTestPrice(test, groupDetail.priceList.rid) * upOrDown;
                    setGroupTotalPrice(groupDetail, groupDetail.priceList.rid);
                    if (modifyTotalPrice) {
                        groupDetail.totalPrice += util.round(testPrice);
                    }
                }
            }
            $scope.isProfileListener = function () {
                if ($scope.selectedGroup.isProfile) {
                    $scope.selectedGroup.discountAmount = null;//reset
                    $scope.selectedGroup.discountPercentage = null;//reset
                    //$scope.caluclateGroupDiscounts($scope.selectedGroup);//reset
                }
                //$scope.refreshGroupDetailGrid();
            };
            $scope.caluclateGroupDiscounts = function (group) {
                if (group.discountAmount != null) {
                    group.groupPrice = group.totalPrice - group.discountAmount;
                    group.discountPercentage = null;//reset
                } else if (group.discountPercentage != null) {
                    group.groupPrice = group.totalPrice * ((100 - group.discountPercentage) / 100);
                    group.discountAmount = null;//reset
                } else {
                    group.groupPrice = group.totalPrice;
                }
                group.groupPrice = util.round(group.groupPrice);
            }
            var groupDataSource = new kendo.data.DataSource({
                pageSize: config.gridPageSizes[0],
                page: 1,
                transport: {
                    read: function (e) {
                        e.data = util.createFilterablePageRequest(groupDataSource);
                        testGroupManagementService.getGroupsPage(e.data).then(function (response) {
                            e.success(response.data);
                        }).catch(function (error) {
                            e.error(error);
                        });
                    }
                },
                serverPaging: true,
                serverFiltering: true,
                schema: {
                    parse: function (data) {
                        for (var idx = 0; idx < data.content.length; idx++) {
                            var group = data.content[idx];
                            //calculate the price of this package
                            //not using setGroupTotalPrice because the chips are not yet populated
                            group.totalPrice = 0;
                            if (group.groupDefinitions != null) {
                                var tests = []
                                for (var i = 0; i < group.groupDefinitions.length; i++) {
                                    var test = group.groupDefinitions[i].testDefinition;
                                    setTestPrices(test);
                                    tests.push(test);
                                }
                                setGroupTotalPrice(group, defaultPricelist.rid, tests);//set the inital total price
                                $scope.caluclateGroupDiscounts(group);
                            }
                        }
                        return data;
                    },
                    total: "totalElements",
                    data: "content",
                    model: {
                        id: "rid",
                        fields: {
                            name: { type: "string" },
                            isProfile: { type: "boolean" },
                            totalPrice: { type: "number" },
                            discountAmount: { type: "number" },
                            discountPercentage: { type: "number" }
                        }
                    }
                }
            });

            $scope.groupGridOptions = {
                dataSource: groupDataSource,
                columns: [
                    {
                        field: "name",
                        title: "{{ 'name' | translate}}",
                    },
                    {
                        field: "isProfile",
                        title: "{{ 'profile' | translate}}",
                        template: function (dataItem) {
                            return dataItem.isProfile ? util.systemMessages.yes : util.systemMessages.no;
                        }
                    },
                    {
                        field: "isActive",
                        title: "{{ 'active' | translate}}",
                        template: function (dataItem) {
                            return dataItem.isActive ? util.systemMessages.yes : util.systemMessages.no;
                        }
                    },
                    {
                        field: "discountAmount",
                        title: "{{ 'discountAmount' | translate}}"
                    },
                    {
                        field: "discountPercentage",
                        title: "{{ 'discountPercentage' | translate}}",
                        template: function (dataItem) {
                            return dataItem.discountPercentage ? dataItem.discountPercentage + "%" : "";
                        }
                    },
                    {
                        field: "totalPrice",
                        title: "{{ 'total' | translate}}",
                        filterable: false,
                        sortable: false,
                        template: function (dataItem) {
                            var map = {};
                            if (!dataItem.isProfile) {
                                map[defaultPricelist.rid] = dataItem.groupPrice;
                            } else {
                                var tests = []
                                for (var i = 0; i < dataItem.groupDefinitions.length; i++) {
                                    tests.push(dataItem.groupDefinitions[i].testDefinition);
                                }
                                for (var idx = 0; idx < dataItem.groupDetails.length; idx++) {
                                    var groupDetail = dataItem.groupDetails[idx];
                                    setGroupTotalPrice(groupDetail, groupDetail.priceList.rid, tests);
                                    map[groupDetail.priceList.rid] = groupDetail.totalPrice;
                                }
                            }
                            if (Object.keys(map).length === 0) {
                                return "";
                            }
                            return generatePriceListsTemplate(map);
                        }
                    }
                ],
                change: function (e) {
                    clearTestGridSelection();
                    var selectedRows = e.sender.select();
                    if (selectedRows.length > 0) {
                        $scope.selectedGroup = this.dataItem(selectedRows[0]);
                    } else {
                        $scope.selectedGroup = null;
                    }
                }
            };
            $scope.clear = function () {
                $scope.selectedGroup = {
                    isProfile: false,
                    isActive: true,
                    totalPrice: 0
                };
                $scope.testChipsOptions.data = [];
                clearTestGridSelection();
                $scope.refreshGroupDetailGrid();
                $scope.testSearchOptions.reset();
            };
            $scope.createGroup = function () {
                $scope.clear();
                $scope.toggleView();
            };
            $scope.editGroup = function () {
                refreshTestGrid();
                //populate chips
                for (var idx = 0; idx < $scope.selectedGroup.groupDefinitions.length; idx++) {
                    var test = $scope.selectedGroup.groupDefinitions[idx].testDefinition;
                    addToChips(test);
                }
                $scope.refreshGroupDetailGrid();
                $scope.toggleView();
            };
            $scope.deleteGroup = function () {
                return testGroupManagementService.deleteTestGroup($scope.selectedGroup).then(function () {
                    util.createToast(util.systemMessages.success, "success");
                    $scope.refreshGroupGrid();
                });
            };
            $scope.refreshGroupGrid = function () {
                $scope.selectedGroup = null;
                clearTestGridSelection();
                groupDataSource.read();
            };
            $scope.submitGroup = function () {
                $scope.selectedGroup.groupDefinitions = generateSelectedTests();
                $scope.selectedGroup.groupDetails = groupDetailDataSource.data();
                if ($scope.selectedGroup.rid == null) {
                    testGroupManagementService.createTestGroup($scope.selectedGroup).then(function () {
                        util.createToast(util.systemMessages.success, "success");
                        $scope.refreshGroupGrid();
                        $scope.refreshGroupDetailGrid();
                        $scope.toggleView();
                    });
                } else {
                    testGroupManagementService.updateTestGroup($scope.selectedGroup).then(function () {
                        util.createToast(util.systemMessages.success, "success");
                        $scope.refreshGroupGrid();
                        $scope.refreshGroupDetailGrid();
                        $scope.toggleView();
                    });
                }
            };
            $scope.toggleView = function () {
                $scope.isEdit = !$scope.isEdit;
                if (!$scope.isEdit) {
                    var grid = $("#groupGrid").data("kendoGrid");
                    grid.clearSelection();
                }
            };

            var groupDetailDataSource = new kendo.data.DataSource({
                pageSize: config.gridPageSizes[0],
                page: 1,
                transport: {
                    read: function (e) {
                        if ($scope.selectedGroup == null || $scope.selectedGroup.rid == null || !$scope.selectedGroup.isProfile) {
                            e.success([]);
                            return;
                        }
                        e.data = util.createFilterablePageRequest(groupDetailDataSource);
                        e.data.filters.push({ "field": "group.rid", "value": $scope.selectedGroup.rid, "operator": "eq" });
                        testGroupManagementService.getTestGroupDetails(e.data).then(function (response) {
                            e.success(response.data);
                        }).catch(function (error) {
                            e.error(error);
                        });
                    },
                    create: function (e) {
                        setGroupTotalPrice(e.data, e.data.priceList.rid);
                        e.success(e.data);
                    },
                    update: function (e) {
                        setGroupTotalPrice(e.data, e.data.priceList.rid);
                        e.success(e.data);
                    },
                    destroy: function (e) {
                        e.success();
                    }
                },
                schema: {
                    parse: function (data) {
                        for (var idx = 0; idx < data.length; idx++) {
                            setGroupTotalPrice(data[idx], data[idx].priceList.rid);
                        }
                        return data;
                    },
                    model: {
                        id: "rid",
                        fields: {
                            priceList: { editable: true, type: "lov" },
                            totalPrice: { editable: false, type: "number" }
                        }
                    }
                }
            });

            function onGroupDetailPriceListChange(e) {
                var selectedPriceList = this.dataItem(e.sender.select()[0]);
                if (selectedPriceList == null || $scope.selectedGroupDetail == null) {
                    return;
                }
                setGroupTotalPrice($scope.selectedGroupDetail, selectedPriceList.rid);
            }

            $scope.groupDetailGridOptions = {
                dataSource: groupDetailDataSource,
                autoBind: false,
                editable: "inline",
                columns: [
                    {
                        field: "priceList",
                        title: "{{ 'priceList' | translate}}",
                        filterable: {
                            ui: function (element) {
                                util.createListFilter(element, priceLists, "nameLocale");
                            }
                        },
                        editor: function (container, options) {
                            util.createListEditor(container, options, priceLists, "nameLocale", { "change": onGroupDetailPriceListChange });
                        },
                        template: function (dataItem) {
                            for (var idx = 0; idx < priceLists.length; idx++) {
                                if (dataItem.priceList.rid === priceLists[idx].rid) {
                                    return priceLists[idx].nameLocale;
                                }
                            }
                            return "";
                        }
                    },
                    {
                        field: "totalPrice",
                        title: "{{ 'total' | translate}}"
                    },
                    // {
                    //     field: "discountAmount",
                    //     title: "{{ 'discountAmount' | translate}}",
                    //     editor: function (container, options) {
                    //         $('<input name="' + options.field + '"/>')
                    //             .appendTo(container)
                    //             .kendoNumericTextBox({ min: 0 });
                    //     }
                    // },
                    // {
                    //     field: "discountPercentage",
                    //     title: "{{ 'discountPercentage' | translate}}",
                    //     editor: function (container, options) {
                    //         $('<input name="' + options.field + '"/>')
                    //             .appendTo(container)
                    //             .kendoNumericTextBox(config.kendoPercentageFormat);
                    //     },
                    //     template: function (dataItem) {
                    //         return dataItem.discountPercentage != null ? dataItem.discountPercentage + "%" : "";
                    //     }
                    // },
                    // {
                    //     field: "groupPrice",
                    //     title: "{{ 'packagePrice' | translate}}"
                    // }
                ],
                filter: function (e) {
                    var filterMap = {
                        "priceList": "priceList.rid"
                    };
                    util.createListFilterHandler(e, filterMap);
                },
                change: function (e) {
                    var selectedRows = e.sender.select();
                    if (selectedRows.length > 0) {
                        $scope.selectedGroupDetail = this.dataItem(selectedRows[0]);
                    } else {
                        $scope.selectedGroupDetail = null;
                    }
                }
            };

            function setTestPrices(test) {
                // create an array that have all the active pricing of this test
                test["prices"] = [];
                for (var idx = 0; idx < test.billTestItems.length; idx++) {
                    var masterItem = test.billTestItems[idx].billMasterItem;
                    for (var i = 0; i < masterItem.billPricings.length; i++) {
                        var obj = {
                            price: masterItem.billPricings[i].price,
                            pricelist: masterItem.billPricings[i].billPriceList
                        };
                        test.prices.push(obj);
                    }
                }
            }

            $scope.addGroupDetail = function () {
                var newGroupDetail = {
                    rid: dummyRid--,
                    priceList: defaultPricelist
                };
                setGroupTotalPrice(newGroupDetail, defaultPricelist.rid);
                newGroupDetail = util.addGridRow(newGroupDetail, groupDetailDataSource);
                $scope.editGroupDetail(newGroupDetail);
            };

            $scope.editGroupDetail = function (dataItem) {
                util.editGridRow(dataItem, "groupDetailGrid");
                $scope.groupDetailChanged = true;
            };

            $scope.saveGroupDetailChanges = function () {
                groupDetailDataSource.sync();
                $scope.selectedGroupDetail = null;
                $scope.groupDetailChanged = false;
            };

            $scope.cancelChangesGroupDetail = function () {
                groupDetailDataSource.cancelChanges();
                $scope.selectedGroupDetail = null;
                $scope.groupDetailChanged = false;
            };

            $scope.deleteGroupDetail = function () {
                util.deleteGridRow($scope.selectedGroupDetail, groupDetailDataSource);
                $scope.selectedGroupDetail = null;
            };

            $scope.refreshGroupDetailGrid = function () {
                $scope.selectedGroupDetail = null;
                groupDetailDataSource.read();
            };

            function clearTestGridSelection() {
                var grid = $("#testGrid").data("kendoGrid");
                grid._selectedIds = {};
                grid.clearSelection();
                $scope.testChipsOptions.data = [];
            }

            function refreshTestGrid() {
                clearTestGridSelection();
                var grid = $("#testGrid").data("kendoGrid");
                grid.dataSource.read();
                grid.refresh();
            }

            var testDataSource = new kendo.data.DataSource({
                pageSize: config.gridPageSizes[0],
                page: 1,
                transport: {
                    read: function (e) {
                        e.data = util.createFilterablePageRequest(testDataSource);
                        for (var i = 0; i < testSearchFilters.length; i++) {
                            e.data.filters.push(testSearchFilters[i]);
                        }
                        testDefinitionManagementService.getTestsDefaultPricingPage(e.data).then(function (response) {
                            e.success(response.data);
                        }).catch(function (error) {
                            e.error(error);
                        });
                    }
                },
                serverPaging: true,
                serverFiltering: true,
                schema: {
                    parse: function (data) {
                        for (var idx = 0; idx < data.content.length; idx++) {
                            setTestPrices(data.content[idx]);
                        }
                        return data;
                    },
                    total: "totalElements",
                    data: "content",
                    model: {
                        id: "rid",
                        fields: {
                            description: { type: "string" },
                            price: { type: "number" },
                            standardCode: { type: "string" }
                        }
                    }
                }
            });

            $scope.testGridOptions = {
                columns: [
                    {
                        selectable: true,
                        width: "50px",
                    },
                    {
                        field: "standardCode",
                        title: "{{ 'standardCode' | translate }}"
                    },
                    {
                        field: "price",
                        title: "{{ 'price' | translate }}",
                        sortable: false,
                        template: function (dataItem) {
                            var map = {};
                            for (var idx = 0; idx < dataItem.prices.length; idx++) {
                                map[dataItem.prices[idx].pricelist.rid] = dataItem.prices[idx].price;
                            }
                            return generatePriceListsTemplate(map);
                        }
                    },
                    {
                        field: "description",
                        title: "{{ 'description' | translate }}"
                    }
                ],
                filterable: false,
                sortable: true,
                selectable: false,
                persistSelection: false,
                dataSource: testDataSource,
                dataBound: function (e) {
                    util.gridSelectionDataBound(e.sender, $scope.testChipsOptions.data, runOnChange, undefined);
                    $('tr:not([data-uid]) th input[type=checkbox]').prop('disabled', true);//disable select all checkbox, many issues
                    $('input[type=checkbox]').click(function (e) {
                        var test = testDataSource.getByUid($(this).parent().parent().attr("data-uid"));
                        if (test != null) {
                            calculateFinalPrice(test, $(this).is(':checked'));
                        }
                    });
                },
                change: function onChange(e) {
                    util.gridSelectionChange(e.sender, $scope.testChipsOptions.data, runOnChange);
                }
            };

        }
    ]);
});

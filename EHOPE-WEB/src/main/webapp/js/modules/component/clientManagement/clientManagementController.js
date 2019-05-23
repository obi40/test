define(['app', 'config', 'util'], function (app, config, util) {
    'use strict';
    app.controller('clientManagementCtrl', [
        '$scope',
        'clientManagementService',
        'insuranceNetworkService',
        'priceListService',
        'billingManagementService',
        'lovService',
        'tenantFormService',
        'branchFormService',
        function (
            $scope,
            clientManagementService,
            insuranceNetworkService,
            priceListService,
            billingManagementService,
            lovService,
            tenantFormService,
            branchFormService
        ) {
            $scope.insProvider = null;
            $scope.insProviderParent = null;
            $scope.insProviderPlan = null;
            $scope.insCoverageDetail = null;
            $scope.userLocale = util.userLocale;
            $scope.insProviderChanged = false;
            $scope.insProviderPlanChanged = false;
            $scope.insCoverageDetailChanged = false;
            $scope.showInsProviderGrid = true;//show/hide
            $scope.showInsProviderPlanGrid = false;//show/hide
            $scope.showInsCovDetailGrid = false;//show/hide
            $scope.insProviderGridName = "insProviderGridName";//to know what grid to refresh
            $scope.insProviderPlanGridName = "insProviderPlanGridName";//to know what grid to refresh
            $scope.insCovDetailGridName = "insCovDetailGridName";//to know what grid to refresh
            $scope.breadcrumbOptions = {
                baseCallback: function () {
                    $scope.insProvider = null;
                    $scope.insProviderParent = null;
                    $scope.insProviderPlan = null;
                    $scope.insCoverageDetail = null;
                    toggleInsuranceTypeCol(false);
                    $scope.refreshGrids($scope.insProviderGridName);
                }
            };
            var insuranceTypes = [];
            var insuranceNetworks = [];
            var countries = [];
            var priceLists = [];
            var coverageDetailScopes = [];
            var tenants = [];
            var branches = [];
            lovService.getLkpByClass({ className: "LkpInsuranceType" }).then(function (data) {
                insuranceTypes = data;
            });
            lovService.getAnyLkpByClass({ className: "LkpCountry" }).then(function (response) {
                countries = response.data;
            });
            lovService.getLkpByClass({ className: "LkpCoverageDetailScope" }).then(function (data) {
                coverageDetailScopes = data;
            });
            insuranceNetworkService.getInsNetworkList().then(function (response) {
                insuranceNetworks = response.data;
            });
            priceListService.getBillPriceLists().then(function (data) {
                priceLists = data;
            });
            tenantFormService.getTenants().then(function (response) {
                tenants = response.data;
                tenants.splice(0, 0, { rid: -1, name: "None" });//add a none option
                var filters = [
                    {
                        "field": "isActive",
                        "value": true,
                        "operator": "eq"
                    }
                ];
                branchFormService.getLabBranchListExcluded({ filters: filters }).then(function (response) {
                    branches = response.data;
                    var obj = {
                        tenantId: -1,
                        rid: -1,
                        name: {}
                    };
                    obj.name[util.userLocale] = "None";
                    branches.splice(0, 0, obj);//add a none option
                    // add tenant name so we can group the drop down list by it
                    for (var idx = 0; idx < branches.length; idx++) {
                        var obj = branches[idx];
                        for (var i = 0; i < tenants.length; i++) {
                            if (obj.tenantId == tenants[i].rid) {
                                obj["tenantName"] = tenants[i].name;
                                break;
                            }
                        }
                    }

                });
            });
            $scope.refreshGrids = function (datasourceName) {
                if (datasourceName == $scope.insProviderGridName) {
                    insProviderDataSource.page(0);
                    insProviderDataSource.read();
                    $scope.showInsProviderGrid = true;
                    $scope.showInsProviderPlanGrid = false;
                    $scope.showInsCovDetailGrid = false;
                } else if (datasourceName == $scope.insProviderPlanGridName) {
                    insProviderPlanDataSource.read();
                    $scope.showInsProviderPlanGrid = true;
                    $scope.showInsProviderGrid = false;
                    $scope.showInsCovDetailGrid = false;
                } else if (datasourceName == $scope.insCovDetailGridName) {
                    insCoverageDetailDataSource.read();
                    $scope.showInsCovDetailGrid = true;
                    $scope.showInsProviderPlanGrid = false;
                    $scope.showInsProviderGrid = false;
                }


            };
            function filterBranchesByTenant() {
                var data = angular.copy(branches);
                if ($scope.insProviderParent != null && $scope.insProviderParent.insuranceTenant != null &&
                    $scope.insProviderParent.insuranceTenant.rid >= 0) {
                    for (var idx = data.length - 1; idx >= 0; idx--) {
                        if (data[idx].rid == -1) {//keep the "None"
                            continue;
                        }
                        if ($scope.insProviderParent.insuranceTenant.rid !== data[idx].tenantId) {
                            data.splice(idx, 1);
                        }
                    }
                }
                return data;
            }
            function clearInsProvider(insProvider) {
                var ip = angular.copy(insProvider);
                // to remove dummy objects if it was not picked
                // so spring dont throw exception
                if (ip.insNetwork.rid == null || ip.insNetwork.rid == -1) {
                    delete ip.insNetwork;
                }
                if (ip.insuranceType.rid == null || ip.insuranceType.rid == -1) {
                    delete ip.insuranceType;
                }
                if (ip.insuranceTenant.rid == null || ip.insuranceTenant.rid == -1) {
                    delete ip.insuranceTenant;
                }
                if (ip.insuranceBranch.rid == null || ip.insuranceBranch.rid == -1) {
                    delete ip.insuranceBranch;
                }
                return ip;
            }
            var insuranceTypeColumn = {
                field: "insuranceType",
                title: util.systemMessages.insuranceType,
                filterable: {
                    ui: function (element) {
                        util.createLovFilter(element, { className: "LkpInsuranceType" }, lovService.getLkpByClass);
                    }
                },
                editor: function (container, options) {
                    util.createListEditor(container, options, insuranceTypes, ("name." + $scope.userLocale));
                },
                template: function (dataItem) {
                    return dataItem.insuranceType && dataItem.insuranceType.name ? dataItem.insuranceType.name[$scope.userLocale] : "";
                }
            };
            var insuranceTenantColumn = {
                field: "insuranceTenant",
                title: util.systemMessages.tenant,
                filterable: {
                    ui: function (element) {
                        util.createListFilter(element, tenants, "name");
                    }
                },
                editor: function (container, options) {
                    util.createListEditor(container, options, tenants, "name");
                },
                template: function (dataItem) {
                    return dataItem.insuranceTenant && dataItem.insuranceTenant.name ? dataItem.insuranceTenant.name : "";
                }
            };
            var insuranceBranchColumn = {
                field: "insuranceBranch",
                title: util.systemMessages.branch,
                hidden: true,
                filterable: {
                    ui: function (element) {
                        var data = filterBranchesByTenant();
                        util.createListFilter(element, data, ("name." + util.userLocale), { "_group": [{ field: "tenantName" }] });
                    }
                },
                editor: function (container, options) {
                    var branches = filterBranchesByTenant();
                    var insData = insProviderDataSource.data();
                    for (var idx = branches.length - 1; idx >= 0; idx--) {
                        for (var i = 0; i < insData.length; i++) {
                            if (insData[i].insuranceBranch != null && insData[i].insuranceBranch.rid === branches[idx].rid) {
                                branches.splice(idx, 1);
                                break;
                            }
                        }
                    }
                    util.createListEditor(container, options, branches, ("name." + util.userLocale), null, { "_group": [{ field: "tenantName" }] });
                },
                template: function (dataItem) {
                    return dataItem.insuranceBranch && dataItem.insuranceBranch.name ? dataItem.insuranceBranch.name[util.userLocale] : "";
                }
            };
            var insProviderDataSource = new kendo.data.DataSource({
                pageSize: config.gridPageSizes[0],
                page: 1,
                transport: {
                    read: function (e) {
                        clientManagementService.getInsParentProviderList().then(function (response) {
                            e.success(response.data);
                        }).catch(function (error) {
                            e.error(error);
                        });
                    },
                    create: function (e) {
                        e.data = clearInsProvider(e.data);
                        clientManagementService.createInsProvider(e.data).then(function () {
                            util.createToast(util.systemMessages.success, "success");
                            $scope.refreshGrids($scope.insProviderGridName);
                        }).catch(function (error) {
                            e.error(error);
                            $scope.refreshGrids($scope.insProviderGridName);
                        });
                    },
                    update: function (e) {
                        e.data = clearInsProvider(e.data);
                        clientManagementService.updateInsProvider(e.data).then(function () {
                            util.createToast(util.systemMessages.success, "success");
                            $scope.refreshGrids($scope.insProviderGridName);
                        }).catch(function (error) {
                            e.error(error);
                            $scope.refreshGrids($scope.insProviderGridName);
                        });
                    }
                },
                schema: {
                    parse: function (data) {
                        var result = [];
                        // get the children of the type
                        if ($scope.insProviderParent != null &&
                            ($scope.insProviderParent.insuranceType.code == "TPA" ||
                                $scope.insProviderParent.insuranceType.code == "LAB_NETWORK" ||
                                $scope.insProviderParent.insuranceType.code == "SELF_FUNDED")) {
                            for (var idx = 0; idx < data.length; idx++) {
                                if ($scope.insProviderParent.rid == data[idx].masterProvider.rid) {
                                    data = data[idx].providers;
                                    break;
                                }
                            }
                        }
                        for (var idx = 0; idx < data.length; idx++) {
                            var obj = null;
                            if (data[idx].masterProvider != null) {
                                obj = data[idx].masterProvider;
                                obj["balanceLocale"] = data[idx].masterBalance;
                                obj["isParent"] = true;
                            } else {
                                obj = data[idx];
                                obj["balanceLocale"] = obj.balance != null ? obj.balance : 0;
                                obj["isParent"] = false;
                            }
                            obj["nameLocale"] = obj.name[$scope.userLocale] || "";
                            obj["addressLocale"] = obj.address[$scope.userLocale] || "";
                            obj["insNetwork"] = obj.insNetwork ? obj.insNetwork : { rid: -1 };//nullable, -1 so the search works
                            obj["insuranceType"] = obj.insuranceType ? obj.insuranceType : { rid: -1 };//nullable, -1 so the search works
                            obj["insuranceTenant"] = obj.insuranceTenant ? obj.insuranceTenant : { rid: -1 };//nullable, -1 so the search works
                            obj["insuranceBranch"] = obj.insuranceBranch ? obj.insuranceBranch : { rid: -1 };//nullable, -1 so the search works
                            result.push(obj);
                        }
                        return result;
                    },
                    model: {
                        id: "rid",
                        fields: {
                            "rid": {
                                type: "number",
                                editable: false,
                                nullable: true
                            },
                            "code": {
                                editable: true,
                                type: "string",
                                validation: { required: true }
                            },
                            "nameLocale": {
                                editable: true,
                                type: "string"
                            },
                            "coveragePercentage": {
                                editable: true,
                                type: "number"
                            },
                            "discount": {
                                editable: true,
                                type: "number"
                            },
                            "addressLocale": {
                                editable: true,
                                type: "string"
                            },
                            "remarks": {
                                editable: true,
                                type: "string"
                            },
                            "contactInformation": {
                                editable: true,
                                type: "string"
                            },
                            "insNetwork": {
                                editable: true,
                                type: "lov"
                            },
                            "insuranceType": {
                                editable: true,
                                type: "lov"
                            },
                            "isActive": {
                                editable: true,
                                type: "boolean"
                            },
                            "isAutoApprove": {
                                editable: true,
                                type: "boolean"
                            },
                            "isNetAmount": {
                                editable: true,
                                type: "boolean"
                            },
                            "lkpCountry": {
                                editable: true,
                                type: "lov"
                            },
                            "balanceLocale": {
                                editable: false,//its created in the back end
                                type: "number"
                            },
                            "priceList": {
                                editable: true,
                                type: "lov"
                            },
                            "insuranceTenant": {
                                editable: true,
                                type: "lov"
                            },
                            "insuranceBranch": {
                                editable: true,
                                type: "lov"
                            }

                        }
                    }
                }
            });
            $scope.insProvidersGridOptions = {
                columns: [
                    {
                        field: "code",
                        title: util.systemMessages.code
                    },
                    {
                        field: "nameLocale",
                        title: util.systemMessages.name,
                        editor: function (container, options) {
                            util.createTransFieldEditor(container, options, "name");
                        },
                        template: function (dataItem) {
                            if (dataItem.insuranceType.code == "LAB" ||
                                dataItem.insuranceType.code == "PUBLIC_SECTOR" ||
                                dataItem.insuranceType.code == "INDIVIDUALS") {
                                return dataItem.nameLocale;
                            } else {
                                return '<div class="anchor-txt" ng-click="insProviderLink(' + dataItem.rid + ')">' + dataItem.nameLocale + '</div>';
                            }
                        }
                    },
                    {
                        field: "priceList",
                        title: util.systemMessages.priceList,
                        filterable: {
                            ui: function (element) {
                                util.createListFilter(element, priceLists, ("name." + $scope.userLocale));
                            }
                        },
                        editor: function (container, options) {
                            util.createListEditor(container, options, priceLists, ("name." + $scope.userLocale));
                        },
                        template: function (dataItem) {
                            return dataItem.priceList ? dataItem.priceList.name[$scope.userLocale] : "";
                        }
                    },
                    {
                        field: "coveragePercentage",
                        title: util.systemMessages.coveragePercentage,
                        editor: function (container, options) {
                            $('<input name="' + options.field + '"/>')
                                .appendTo(container)
                                .kendoNumericTextBox(config.kendoPercentageFormat);
                        },
                        template: function (dataItem) {
                            return dataItem.coveragePercentage != null ? dataItem.coveragePercentage + "%" : "";
                        }
                    },
                    {
                        field: "discount",
                        title: util.systemMessages.discount,
                        editor: function (container, options) {
                            $('<input name="' + options.field + '"/>')
                                .appendTo(container)
                                .kendoNumericTextBox(config.kendoPercentageFormat);
                        },
                        template: function (dataItem) {
                            return dataItem.discount != null ? dataItem.discount + "%" : "";
                        }
                    },
                    insuranceTypeColumn,
                    {
                        field: "balanceLocale",
                        title: util.systemMessages.balance,
                        template: function (dataItem) {
                            var content = "";
                            var style = "";
                            if (dataItem.balanceLocale < 0) {
                                var balance = dataItem.balanceLocale * -1;
                                balance = util.round(balance);
                                content = "(" + balance + ") " + util.userCurrency;
                                style = "negative";
                            } else {
                                content = util.round(dataItem.balanceLocale) + " " + util.userCurrency;
                                if (dataItem.balanceLocale > 0) {// dont use style of balance is zero
                                    style = "positive";
                                }
                            }

                            return "<div class=\"" + style + "\">" + content + "</div>";
                        }
                    },
                    {
                        field: "isActive",
                        title: util.systemMessages.active,
                        template: function (dataItem) {
                            return dataItem.isActive ? util.systemMessages.yes : util.systemMessages.no;
                        }
                    },
                    insuranceTenantColumn,
                    insuranceBranchColumn,
                    {
                        field: "insNetwork",
                        title: util.systemMessages.insNetwork,
                        hidden: true,
                        filterable: {
                            ui: function (element) {
                                util.createListFilter(element, insuranceNetworks, ("name." + $scope.userLocale));
                            }
                        },
                        editor: function (container, options) {
                            util.createListEditor(container, options, insuranceNetworks, ("name." + $scope.userLocale));
                        },
                        template: function (dataItem) {
                            return dataItem.insNetwork && dataItem.insNetwork.name ? dataItem.insNetwork.name[$scope.userLocale] : "";
                        }
                    },
                    {
                        field: "addressLocale",
                        title: util.systemMessages.address,
                        hidden: true,
                        editor: function (container, options) {
                            util.createTransFieldEditor(container, options, "address");
                        }
                    },
                    {
                        field: "remarks",
                        title: util.systemMessages.remarks,
                        hidden: true
                    },
                    {
                        field: "contactInformation",
                        title: util.systemMessages.contactInfo,
                        hidden: true
                    },
                    {
                        field: "isNetAmount",
                        title: util.systemMessages.netAmount,
                        hidden: false,
                        template: function (dataItem) {
                            return dataItem.isNetAmount ? util.systemMessages.yes : util.systemMessages.no;
                        }
                    },
                    {
                        field: "isAutoApprove",
                        title: util.systemMessages.autoApprove,
                        hidden: true,
                        template: function (dataItem) {
                            return dataItem.isAutoApprove ? util.systemMessages.yes : util.systemMessages.no;
                        }
                    },
                    {
                        field: "lkpCountry",
                        title: util.systemMessages.country,
                        hidden: true,
                        filterable: {
                            ui: function (element) {
                                util.createListFilter(element, countries, ("name." + $scope.userLocale));
                            }
                        },
                        editor: function (container, options) {
                            util.createListEditor(container, options, countries, ("name." + $scope.userLocale));
                        },
                        template: function (dataItem) {
                            return dataItem.lkpCountry ? dataItem.lkpCountry.name[$scope.userLocale] : "";
                        }
                    }
                ],
                dataSource: insProviderDataSource,
                editable: "inline",
                filter: function (e) {
                    var filterMap = {
                        "insNetwork": "insNetwork.rid",
                        "insuranceType": "insuranceType.rid",
                        "lkpCountry": "lkpCountry.rid",
                        "priceList": "priceList.rid",
                        "insuranceTenant": "insuranceTenant.rid",
                        "insuranceBranch": "insuranceBranch.rid"
                    };
                    util.createListFilterHandler(e, filterMap);
                },
                change: function (e) {
                    $scope.insProvider = e.sender.dataItem(e.sender.select());
                    $scope.insProviderParent = $scope.insProvider != null ? $scope.insProvider.parentProvider : null;
                }
            };
            $scope.saveInsProvider = function () {
                insProviderDataSource.sync();
                $scope.insProvider = null;
                $scope.insProviderChanged = false;
            };
            $scope.addInsProvider = function () {
                var insProvider = {
                    code: "",
                    name: {},
                    nameLocale: "",
                    address: {},
                    addressLocale: "",
                    coveragePercentage: 0,
                    discount: 0,
                    isActive: true,
                    isAutoApprove: false,
                    isNetAmount: true,
                    balanceLocale: 0,
                    insNetwork: { rid: -1 },//we cant inject random value here, because it may lead to false data
                    insuranceType: insuranceTypes[0],
                    lkpCountry: countries[0],
                    priceList: priceLists[0],
                    insuranceTenant: { rid: -1 },//we cant inject random value here, because it may lead to false data
                    insuranceBranch: { rid: -1 }//we cant inject random value here, because it may lead to false data
                };
                if ($scope.insProviderParent != null &&
                    ($scope.insProviderParent.insuranceType.code == "TPA" ||
                        $scope.insProviderParent.insuranceType.code == "LAB_NETWORK" ||
                        $scope.insProviderParent.insuranceType.code == "SELF_FUNDED")) {
                    insProvider.parentProvider = $scope.insProviderParent;
                    insProvider.coveragePercentage = $scope.insProviderParent.coveragePercentage;
                    insProvider.discount = $scope.insProviderParent.discount;
                    insProvider.lkpCountry = $scope.insProviderParent.lkpCountry;
                    insProvider.priceList = $scope.insProviderParent.priceList;
                    insProvider.insuranceType = $scope.insProviderParent.insuranceType ? $scope.insProviderParent.insuranceType : { rid: -1 };
                    insProvider.insNetwork = $scope.insProviderParent.insNetwork ? $scope.insProviderParent.insNetwork : { rid: -1 };
                }
                insProvider = util.addGridRow(insProvider, insProviderDataSource);
                $scope.editInsProvider(insProvider);
            };
            $scope.editInsProvider = function (dataItem) {
                util.editGridRow(dataItem, "insProvidersGrid");
                $scope.insProviderChanged = true;
            };
            $scope.cancelInsProviderChanges = function () {
                insProviderDataSource.cancelChanges();
                $scope.insProviderChanged = false;
            };
            var insProviderPlanDataSource = new kendo.data.DataSource({
                pageSize: config.gridPageSizes[0],
                page: 1,
                transport: {
                    read: function (e) {

                        var filterablePageRequest = {
                            filters: [
                                { field: "insProvider.rid", value: $scope.insProvider.rid, operator: "eq" }
                            ]
                        };
                        clientManagementService.getInsProviderPlanListByProvider(filterablePageRequest).then(function (response) {
                            e.success(response.data);
                        }).catch(function (error) {
                            e.error(error);
                        });
                    },
                    create: function (e) {
                        clientManagementService.createInsProviderPlan(e.data).then(function (response) {
                            util.createToast(util.systemMessages.success, "success");
                            e.success(response.data);
                            $scope.refreshGrids($scope.insProviderPlanGridName);
                        }).catch(function (error) {
                            e.error(error);
                            $scope.refreshGrids($scope.insProviderPlanGridName);
                        });
                    },
                    update: function (e) {
                        clientManagementService.updateInsProviderPlan(e.data).then(function (response) {
                            util.createToast(util.systemMessages.success, "success");
                            e.success(response.data);
                            $scope.refreshGrids($scope.insProviderPlanGridName);
                        }).catch(function (error) {
                            e.error(error);
                            $scope.refreshGrids($scope.insProviderPlanGridName);
                        });
                    },
                    destroy: function (e) {
                        clientManagementService.deleteInsProviderPlan(e.data).then(function () {
                            util.createToast(util.systemMessages.success, "success");
                            $scope.refreshGrids($scope.insProviderPlanGridName);
                        }).catch(function (error) {
                            e.error(error);
                            $scope.refreshGrids($scope.insProviderPlanGridName);
                        });
                    }
                },
                schema: {
                    parse: function (response) {
                        for (var idx = 0; idx < response.length; idx++) {
                            response[idx]["nameLocale"] = response[idx].name[$scope.userLocale] || "";
                            response[idx]["descriptionLocale"] = response[idx].description[$scope.userLocale] || "";
                        }
                        return response;
                    },
                    model: {
                        id: "rid",
                        fields: {
                            "nameLocale": {
                                type: "string",
                                editable: true
                            },
                            "descriptionLocale": {
                                type: "string",
                                editable: true
                            },
                            "coveragePercentage": {
                                type: "number",
                                editable: true,
                                nullable: true
                            },
                            "code": {
                                type: "string",
                                editable: true,
                                validation: {
                                    required: true
                                }
                            },
                            "isFixed": {
                                type: "boolean",
                                editable: true
                            },
                            "isActive": {
                                type: "boolean",
                                editable: true
                            },
                            "billPriceList": {
                                type: "lov",
                                editable: true
                            }
                        }
                    }
                }
            });
            $scope.insProviderPlansGridOptions = {
                columns: [{
                    field: "code",
                    title: util.systemMessages.code
                },
                {
                    field: "nameLocale",
                    title: util.systemMessages.name,
                    editor: function (container, options) {
                        util.createTransFieldEditor(container, options, "name");
                    },
                    template: function (dataItem) {
                        return '<div class="anchor-txt" ng-click="insProviderPlanLink(' + dataItem.rid + ')">' + dataItem.nameLocale + '</div>';
                    }
                },
                {
                    field: "descriptionLocale",
                    title: util.systemMessages.description,
                    editor: function (container, options) {
                        util.createTransFieldEditor(container, options, "description");
                    }
                },
                {
                    field: "coveragePercentage",
                    title: util.systemMessages.coveragePercentage,
                    template: function (dataItem) {
                        return dataItem.coveragePercentage + "%";
                    }
                },
                {
                    field: "isFixed",
                    title: util.systemMessages.fixed,
                    template: function (dataItem) {
                        return dataItem.isFixed ? util.systemMessages.yes : util.systemMessages.no;
                    }
                },
                {
                    field: "isActive",
                    title: util.systemMessages.active,
                    template: function (dataItem) {
                        return dataItem.isActive ? util.systemMessages.yes : util.systemMessages.no;
                    }
                },
                {
                    field: "billPriceList",
                    title: util.systemMessages.priceList,
                    filterable: {
                        ui: function (element) {
                            util.createListFilter(element, priceLists, ("name." + $scope.userLocale));
                        }
                    },
                    editor: function (container, options) {
                        util.createListEditor(container, options, priceLists, ("name." + $scope.userLocale));
                    },
                    template: function (dataItem) {
                        // for some reason this is getting called before init the drop down list and it becomes undefined
                        if (dataItem.billPriceList == null) {
                            return "";
                        }
                        return dataItem.billPriceList.name[$scope.userLocale];
                    }
                }],
                editable: "inline",
                dataSource: insProviderPlanDataSource,
                autoBind: false,
                filter: function (e) {
                    var filterMap = {
                        "billPriceList": "billPriceList.rid"
                    };
                    util.createListFilterHandler(e, filterMap);
                },
                change: function (e) {
                    $scope.insProviderPlan = e.sender.dataItem(e.sender.select());
                }
            };
            function toggleInsuranceTypeCol(hide) {
                for (var i = 0; i < $scope.insProvidersGridOptions.columns.length; i++) {
                    var fieldName = $scope.insProvidersGridOptions.columns[i].field;
                    if (fieldName == "insuranceType" || fieldName == "insuranceTenant") {
                        $scope.insProvidersGridOptions.columns[i].hidden = hide;
                    }
                    if (fieldName == "insuranceBranch") {
                        // Only show branch column if the parent has a valid insuranceTenant
                        if (!hide == false && $scope.insProviderParent != null && $scope.insProviderParent.insuranceTenant != null &&
                            $scope.insProviderParent.insuranceTenant.rid != -1) {
                            $scope.insProvidersGridOptions.columns[i].hidden = !hide;
                        } else if (!hide == true) {
                            $scope.insProvidersGridOptions.columns[i].hidden = !hide;
                        }
                    }
                }
                $("#insProvidersGrid").data("kendoGrid").setOptions({
                    columns: $scope.insProvidersGridOptions.columns
                });

            }
            $scope.insProviderLink = function (rid) {
                if (rid != null) {
                    var data = insProviderDataSource.data();
                    for (var idx = 0; idx < data.length; idx++) {
                        if (rid == data[idx].rid) {
                            $scope.insProvider = data[idx];
                            break;
                        }
                    }
                }
                //So it can be used inside the grid template
                if ($scope.insProvider == null) {
                    return;
                }
                var bc = {
                    data: $scope.insProvider,
                    label: $scope.insProvider.name[$scope.userLocale],
                    level: 1,
                    callback: function () {
                        toggleInsuranceTypeCol(false);
                        $scope.refreshGrids($scope.insProviderPlanGridName);
                    }
                };
                $scope.insProvider["label"] = $scope.insProvider.name[$scope.userLocale];
                if ($scope.insProvider.isParent) {
                    var code = $scope.insProvider.insuranceType.code;
                    if (code == "TPA" || code == "LAB_NETWORK" || code == "SELF_FUNDED") {
                        $scope.insProviderParent = $scope.insProvider;
                        bc.callback = function () {
                            toggleInsuranceTypeCol(true);
                            $scope.refreshGrids($scope.insProviderGridName);
                        };
                        toggleInsuranceTypeCol(true);
                        insProviderDataSource.filter({});
                        $scope.refreshGrids($scope.insProviderGridName);
                    } else {
                        toggleInsuranceTypeCol(false);
                        $scope.refreshGrids($scope.insProviderPlanGridName);
                    }
                } else {
                    bc.level = 2;
                    $scope.refreshGrids($scope.insProviderPlanGridName);
                }
                $scope.breadcrumbOptions.crumb(bc);
            }
            $scope.insProviderPlanLink = function (rid) {
                if (rid != null) {
                    var data = insProviderPlanDataSource.data();
                    for (var idx = 0; idx < data.length; idx++) {
                        if (rid == data[idx].rid) {
                            $scope.insProviderPlan = data[idx];
                            break;
                        }
                    }
                }
                if ($scope.insProviderPlan == null) {
                    return;
                }
                var bc = {
                    data: $scope.insProviderPlan,
                    label: $scope.insProviderPlan.name[$scope.userLocale],
                    callback: function () {
                        $scope.refreshGrids($scope.insProviderPlanGridName);
                    }
                };
                if ($scope.insProvider.isParent) {
                    bc.level = 2;
                } else {
                    bc.level = 3;
                }
                $scope.refreshGrids($scope.insCovDetailGridName);
                $scope.breadcrumbOptions.crumb(bc);
            }
            $scope.saveInsProviderPlan = function () {
                insProviderPlanDataSource.sync();
                $scope.insProviderPlan = null;
                $scope.insProviderPlanChanged = false;
            };
            $scope.addInsProviderPlan = function () {
                var newInsProviderPlan = {
                    nameLocale: "",
                    descriptionLocale: "",
                    code: "",
                    coveragePercentage: ($scope.insProvider.coveragePercentage ? $scope.insProvider.coveragePercentage : 0),
                    isFixed: false,
                    isSimple: false,
                    isActive: true,
                    billPriceList: ($scope.insProvider.priceList ? $scope.insProvider.priceList : priceLists[0]),
                    insProvider: $scope.insProvider
                };
                newInsProviderPlan = util.addGridRow(newInsProviderPlan, insProviderPlanDataSource);
                $scope.editInsProviderPlan(newInsProviderPlan);
            };
            $scope.editInsProviderPlan = function (dataItem) {
                util.editGridRow(dataItem, "insProviderPlansGrid");
                $scope.insProviderPlanChanged = true;
            };
            $scope.deleteInsProviderPlan = function () {
                util.deleteGridRow($scope.insProviderPlan, insProviderPlanDataSource);
                $scope.insProviderPlan = null;
            };
            $scope.cancelInsProviderPlanChanges = function () {
                insProviderPlanDataSource.cancelChanges();
                $scope.insProviderPlanChanged = false;
            };

            var insCoverageDetailDataSource = new kendo.data.DataSource({
                pageSize: config.gridPageSizes[0],
                page: 1,
                transport: {
                    read: function (e) {
                        clientManagementService.getInsCoverageDetailListByPlan($scope.insProviderPlan).then(function (response) {
                            e.success(response.data);
                        }).catch(function (error) {
                            e.error(error);
                        });
                    },
                    create: function (e) {
                        e.data = assignCoverageDetailByScope(e.data);
                        clientManagementService.createInsCoverageDetail(e.data).then(function (response) {
                            util.createToast(util.systemMessages.success, "success");
                            e.success(response.data);
                            $scope.refreshGrids($scope.insCovDetailGridName);
                        }).catch(function (error) {
                            e.error(error);
                        });
                    },
                    update: function (e) {
                        e.data = assignCoverageDetailByScope(e.data);
                        clientManagementService.updateInsCoverageDetail(e.data).then(function (response) {
                            util.createToast(util.systemMessages.success, "success");
                            e.success(response.data);
                            $scope.refreshGrids($scope.insCovDetailGridName);
                        }).catch(function (error) {
                            e.error(error);
                        });
                    },
                    destroy: function (e) {
                        clientManagementService.deleteInsCoverageDetail(e.data).then(function () {
                            util.createToast(util.systemMessages.success, "success");
                            $scope.refreshGrids($scope.insCovDetailGridName);
                        }).catch(function (error) {
                            e.error(error);
                            $scope.refreshGrids($scope.insCovDetailGridName);
                        });
                    }
                },
                schema: {
                    model: {
                        id: "rid",
                        fields: {
                            "percentage": {
                                type: "number",
                                editable: true
                            },
                            "lkpCoverageDetailScope": {
                                editable: true
                            },
                            "billMasterItem": {
                                editable: true
                            },
                            "maxAmount": {
                                type: "number",
                                editable: true
                            },
                            "needAuthorization": {
                                type: "boolean",
                                editable: true,
                            },
                            "isCovered": {
                                type: "boolean",
                                editable: true,
                            },
                            "isActive": {
                                type: "boolean",
                                editable: true,
                            }
                        }
                    }
                }
            });

            function assignCoverageDetailByScope(insCoverageDetail) {
                //Returns an insCoverageDetail with the current plan, selected scope , selected item/class
                insCoverageDetail.insProviderPlan = $scope.insProviderPlan;
                var coverageDetailKey = "#" + coverageDetailEditorId;
                var coverageDetailDropdownlist = $(coverageDetailKey).data("kendoDropDownList");
                var coverageScope = coverageDetailDropdownlist.dataItem(); //get the selected scope
                // kendo assign the new value wither it is different or same editor on the same field that it populate the row with it
                // so get the value from any of these and assign it to the correct field by the selected scope.
                var value = insCoverageDetail.billMasterItem || insCoverageDetail.billClassification;
                if (coverageScope.code == "CLASS") {
                    insCoverageDetail.billClassification = value;
                    insCoverageDetail.billMasterItem = null;
                } else {
                    insCoverageDetail.billMasterItem = value;
                    insCoverageDetail.billClassification = null;
                }
                return insCoverageDetail;
            }

            var coverageDetailEditorId = "coverageDetailScopeEditor";

            function lkpCoverageDetailScopeDropDownEditor(container, options) {
                var dataSource = new kendo.data.DataSource({
                    schema: {
                        model: {
                            id: "rid",
                            fields: {
                                rid: {
                                    type: "number"
                                }
                            }
                        }
                    },
                    data: coverageDetailScopes
                });
                $('<input id="' + coverageDetailEditorId + '" required name="' + options.field + '"/>')
                    .appendTo(container)
                    .kendoDropDownList({
                        dataValueField: "rid",
                        valueTemplate: function (dataItem) {
                            return dataItem.name[$scope.userLocale];
                        },
                        template: function (dataItem) {
                            return dataItem.name[$scope.userLocale];
                        },
                        dataSource: dataSource,
                        dataBound: function (e) {
                            var selectedIndex = e.sender.select();
                            e.sender.select(selectedIndex >= 0 ? selectedIndex : 0);
                            e.sender.trigger("change");
                        }
                    });
            }

            function globalEditor(container, options) {
                //this editor for bill master item and bill classifications
                $('<input required name="' + options.field + '"/>')
                    .appendTo(container)
                    .kendoDropDownList({
                        dataValueField: "rid",
                        cascadeFrom: coverageDetailEditorId,
                        valueTemplate: function (dataItem) {
                            return dataItem.code;
                        },
                        template: function (dataItem) {
                            return dataItem.code;
                        },
                        filter: "contains",
                        dataSource: {
                            serverFiltering: true,
                            schema: {
                                model: {
                                    id: "rid"
                                }
                            },
                            transport: {
                                read: function (e) {
                                    var searchValue = "";
                                    var filters = e.data.filter.filters;
                                    for (var filterKey in filters) {
                                        // get the searched value in the drop down list
                                        if (filters[filterKey].operator === "contains") {
                                            searchValue = filters[filterKey].value;
                                        }
                                    }
                                    var key = "#" + coverageDetailEditorId;
                                    var dropdownlist = $(key).data("kendoDropDownList");
                                    var dataItem = dropdownlist.dataItem();
                                    // display the correct data by the selected scope along with the search value
                                    if (dataItem.code === "CLASS") {
                                        billingManagementService.filterBillClassificationList(searchValue).then(function (response) {
                                            e.success(response.data);
                                        });
                                    } else {
                                        billingManagementService.filterBillMasterItemList(searchValue).then(function (response) {
                                            e.success(response.data);
                                        });
                                    }
                                }
                            }
                        },
                        dataBound: function (e) {
                            var selectedIndex = e.sender.select();
                            e.sender.select(selectedIndex >= 0 ? selectedIndex : 0);
                            e.sender.trigger("change");
                        }
                    });
            };

            function genericEditor(container, options) {
                //display a drop down list by the scope of the row
                if (options.model.lkpCoverageDetailScope.code === "CLASS") {
                    globalEditor(container, {
                        field: "billClassification"
                    });
                } else {
                    globalEditor(container, {
                        field: "billMasterItem"
                    });
                }
            };
            var itemClassLabel = (util.systemMessages.billingMasterItem + "/" + util.systemMessages.billingClassification);

            $scope.insCoverageDetailGridOptions = {
                columns: [
                    {
                        field: "lkpCoverageDetailScope",
                        title: util.systemMessages.coverageDetailScope,
                        editor: lkpCoverageDetailScopeDropDownEditor,
                        template: function (dataItem) {
                            // for some reason this is getting called before init the drop down list and it becomes undefined
                            if (dataItem.lkpCoverageDetailScope == undefined) {
                                return dataItem;
                            }
                            return dataItem.lkpCoverageDetailScope.name[$scope.userLocale];
                        }
                    },
                    {
                        field: "billMasterItem",
                        title: itemClassLabel,
                        editor: genericEditor,
                        template: function (dataItem) {
                            // for some reason this is getting called before init the drop down list and it becomes undefined
                            if (dataItem.billMasterItem == undefined && dataItem.billClassification == undefined) {
                                return "";
                            }
                            if (dataItem.billMasterItem) {
                                return dataItem.billMasterItem.code;
                            } else {
                                return dataItem.billClassification.code;
                            }
                        }
                    },
                    {
                        field: "percentage",
                        title: util.systemMessages.percentage,
                        template: function (dataItem) {
                            return dataItem.percentage + "%";
                        },
                        editor: function (container, options) {
                            $('<input name="' + options.field + '"/>')
                                .appendTo(container)
                                .kendoNumericTextBox(config.kendoPercentageFormat);
                        }
                    },
                    {
                        field: "maxAmount",
                        title: util.systemMessages.maxAmount
                    },
                    {
                        field: "needAuthorization",
                        title: util.systemMessages.needAuthority,
                        template: function (dataItem) {
                            return dataItem.needAuthorization ? util.systemMessages.yes : util.systemMessages.no;
                        }
                    },
                    {
                        field: "isCovered",
                        title: util.systemMessages.isCovered,
                        template: function (dataItem) {
                            return dataItem.isCovered ? util.systemMessages.yes : util.systemMessages.no;
                        }
                    },
                    {
                        field: "isActive",
                        title: util.systemMessages.active,
                        template: function (dataItem) {
                            return dataItem.isActive ? util.systemMessages.yes : util.systemMessages.no;
                        }
                    }
                ],
                editable: "inline",
                dataSource: insCoverageDetailDataSource,
                autoBind: false,
                change: function (e) {
                    $scope.insCoverageDetail = e.sender.dataItem(e.sender.select());
                }
            };
            $scope.saveInsCoverageDetail = function () {
                insCoverageDetailDataSource.sync();
                $scope.insCoverageDetail = null;
                $scope.insCoverageDetailChanged = false;
            };
            $scope.addInsCoverageDetail = function () {
                var newInsCoverageDetail = {
                    lkpCoverageDetailScope: coverageDetailScopes[0],
                    lkpCoverageDetailScopeLocale: coverageDetailScopes[0].name[$scope.userLocale],
                    insProviderPlan: $scope.insProviderPlan,
                    needAuthorization: false,
                    isCovered: false,
                    isActive: true,
                    maxAmount: 0,
                    percentage: 0
                };
                if ($scope.insProvider.parentProvider != null && $scope.insProvider.parentProvider.insuranceType.code == "TPA") {
                    newInsCoverageDetail.needAuthorization = true;
                    newInsCoverageDetail.isCovered = true;
                }
                newInsCoverageDetail = util.addGridRow(newInsCoverageDetail, insCoverageDetailDataSource);
                $scope.editInsCoverageDetail(newInsCoverageDetail);
            };
            $scope.editInsCoverageDetail = function (dataItem) {
                util.editGridRow(dataItem, "insCoverageDetailsGrid");
                $scope.insCoverageDetailChanged = true;
            };
            $scope.deleteInsCoverageDetail = function () {
                util.deleteGridRow($scope.insCoverageDetail, insCoverageDetailDataSource);
                $scope.insCoverageDetail = null;
            };
            $scope.cancelInsCoverageDetailChanges = function () {
                insCoverageDetailDataSource.cancelChanges();
                $scope.insCoverageDetailChanged = false;
            }
        }
    ]);
});
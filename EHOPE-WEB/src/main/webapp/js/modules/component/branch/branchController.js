define(['app', 'util', 'config'], function (app, util, config) {
    'use strict';
    app.controller('branchCtrl', [
        '$scope',
        'branchService',
        'branchFormService',
        function (
            $scope,
            branchService,
            branchFormService
        ) {
            $scope.mainTemplate = true;
            $scope.branch = null;
            $scope.userLocale = util.userLocale;
            $scope.editBranchTemplate = config.lisDir + "/modules/component/branch/branch-edit.html";
            $scope.branchFormOptions = {};
            $scope.refreshGrid = function () {
                refresh();
                $scope.clearForm();
            };
            function refresh() {
                dataSource.read();
            }
            $scope.back = function () {
                $scope.toggleView();
                $scope.clearForm();
            };
            $scope.toggleView = function () {
                $scope.mainTemplate = !$scope.mainTemplate;
            };
            $scope.clearForm = function () {
                $scope.branchFormOptions.clear();
                // in case we called this function when we are inside the edit 
                if ($scope.mainTemplate == false) {
                    $scope.branch = { isActive: true };
                }
            };
            $scope.createMode = function () {
                $scope.toggleView();
                $scope.branch = { isActive: true };
            };
            $scope.updateMode = function () {
                $scope.toggleView();
            };
            $scope.activateBranch = function () {
                branchFormService.activateBranch($scope.branch).then(function () {
                    util.createToast(util.systemMessages.success, "success");
                    $scope.refreshGrid();
                });
            };
            $scope.deactivateBranch = function () {
                branchFormService.deactivateBranch($scope.branch).then(function () {
                    util.createToast(util.systemMessages.success, "success");
                    $scope.refreshGrid();
                });
            };
            $scope.submitBranch = function () {
                // the below api accept only lists
                if ($scope.branch.rid == null) {
                    branchFormService.createBranch([$scope.branch]).then(function (response) {
                        util.createToast(util.systemMessages.success, "success");
                        $scope.branch = response.data[0];
                        refresh();
                    });
                } else {
                    branchFormService.updateBranch([$scope.branch]).then(function (response) {
                        util.createToast(util.systemMessages.success, "success");
                        $scope.branch = response.data[0];
                        refresh();
                    });
                }
            };

            var dataSource = new kendo.data.DataSource({
                pageSize: config.gridPageSizes[0],
                page: 1,
                transport: {
                    read: function (e) {
                        branchFormService.getBranches().then(function (response) {
                            e.success(response.data);
                            // get branch if it was set
                            if ($scope.branch != null && $scope.branch.rid != null) {
                                for (var idx = 0; idx < response.data.length; idx++) {
                                    if (response.data[idx].rid == $scope.branch.rid) {
                                        $scope.branch = response.data[idx];
                                        break;
                                    }
                                }
                            }

                        }).catch(function (error) {
                            e.error(error);
                        });
                    }
                },
                schema: {
                    parse: function (data) {
                        for (var idx = 0; idx < data.length; idx++) {
                            data[idx]["nameLocale"] = data[idx].name[util.userLocale];
                            data[idx]["addressLocale"] = data[idx].address[util.userLocale];
                            data[idx]["cityLocale"] = data[idx].city.name[util.userLocale];
                            data[idx]["countryLocale"] = data[idx].country.name[util.userLocale];
                        }
                        return data;
                    },
                    model: {
                        id: "rid",
                        fields: {
                            "code": {
                                type: "string"
                            },
                            "nameLocale": {
                                type: "string"
                            },
                            "phoneNo": {
                                type: "string"
                            },
                            "addressLocale": {
                                type: "string"
                            },
                            "cityLocale": {
                                type: "string"
                            },
                            "countryLocale": {
                                type: "string"
                            },
                            "isActive": {
                                type: "boolean"
                            },
                            "mobilePattern": {
                                type: "string"
                            },
                            "balance": {
                                type: "number"
                            }
                        }
                    }
                }
            });

            $scope.branchGridOptions = {
                columns: [
                    {
                        field: "code",
                        title: util.systemMessages.code
                    },
                    {
                        field: "nameLocale",
                        title: util.systemMessages.name
                    },
                    {
                        field: "phoneNo",
                        title: util.systemMessages.phone
                    },
                    {
                        field: "addressLocale",
                        title: util.systemMessages.address
                    },
                    {
                        field: "countryLocale",
                        title: util.systemMessages.country
                    },
                    {
                        field: "cityLocale",
                        title: util.systemMessages.city
                    },
                    {
                        field: "isActive",
                        title: util.systemMessages.active,
                        template: function (dataItem) {
                            return dataItem.isActive ? util.systemMessages.yes : util.systemMessages.no;
                        }
                    },
                    {
                        field: "mobilePattern",
                        title: util.systemMessages.mobilePattern
                    },
                    {
                        field: "balance",
                        title: util.systemMessages.balance,
                        template: function (dataItem) {
                            var content = "";
                            var style = "";
                            if (dataItem.balance < 0) {
                                var balance = dataItem.balance * -1;
                                balance = util.round(balance);
                                content = "(" + balance + ") " + util.userCurrency;
                                style = "negative";
                            } else {
                                content = util.round(dataItem.balance) + " " + util.userCurrency;
                                if (dataItem.balance > 0) {// dont use style of balance is zero
                                    style = "positive";
                                }
                            }

                            return "<div class=\"" + style + "\">" + content + "</div>";
                        }
                    }
                ],
                dataSource: dataSource,
                change: function () {
                    $scope.branch = $scope.branchGrid.dataItem($scope.branchGrid.select());
                }
            };
        }
    ]);
});

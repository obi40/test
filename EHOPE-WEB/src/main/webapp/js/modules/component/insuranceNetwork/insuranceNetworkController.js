define(['app', 'config', 'util'], function (app, config, util) {
    'use strict';
    app.controller('insuranceNetworkCtrl', [
        '$scope',
        'insuranceNetworkService',
        function (
            $scope,
            insuranceNetworkService
        ) {
            $scope.insNetwork = null;
            $scope.insNetworkChanged = false;

            $scope.refreshGrid = function () {
                $scope.insNetworksGrid.dataSource.read();
                $scope.insNetworksGrid.refresh();
            };

            var dataSource = new kendo.data.DataSource({
                pageSize: config.gridPageSizes[0],
                page: 1,
                transport: {
                    read: function (e) {
                        insuranceNetworkService.getInsNetworkList().then(function (response) {
                            e.success(response.data);
                        }).catch(function (error) {
                            e.error(error);
                        });
                    },
                    create: function (e) {
                        insuranceNetworkService.createInsNetwork(e.data).then(function () {
                            util.createToast(util.systemMessages.success, "success");
                            $scope.refreshGrid();
                        }).catch(function (error) {
                            e.error(error);
                        });
                    },
                    update: function (e) {
                        insuranceNetworkService.updateInsNetwork(e.data).then(function () {
                            util.createToast(util.systemMessages.success, "success");
                            $scope.refreshGrid();
                        }).catch(function (error) {
                            e.error(error);
                        });
                    },
                    destroy: function (e) {
                        insuranceNetworkService.deleteInsNetwork(e.data).then(function () {
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
                            "nameLocale": {
                                type: "string",
                                editable: true
                            },
                            "code": {
                                type: "string",
                                editable: true,
                                nullable: false,
                                validation: { required: true }
                            }
                        }
                    }
                }
            });

            $scope.insNetworksGridOptions = {
                columns: [
                    {
                        field: "code",
                        title: util.systemMessages.code
                    }, {
                        field: "nameLocale",
                        title: util.systemMessages.name,
                        editor: function (container, options) {
                            util.createTransFieldEditor(container, options, "name");
                        }
                    }],
                editable: "inline",
                dataSource: dataSource,
                change: function () {
                    $scope.insNetwork = $scope.insNetworksGrid.dataItem($scope.insNetworksGrid.select());

                }
            };

            $scope.saveInsNetwork = function () {
                dataSource.sync();
                $scope.insNetwork = null;
                $scope.insNetworkChanged = false;
            };

            $scope.addInsNetwork = function () {
                var newInsNetwork = {
                    nameLocale: "",
                    code: ""
                };
                newInsNetwork = util.addGridRow(newInsNetwork, dataSource);
                $scope.editInsNetwork(newInsNetwork);
            };

            $scope.editInsNetwork = function (dataItem) {
                util.editGridRow(dataItem, "insNetworksGrid");
                $scope.insNetworkChanged = true;
            };

            $scope.deleteInsNetwork = function () {
                util.deleteGridRow($scope.insNetwork, dataSource);
                $scope.insNetwork = null;
            };

            $scope.cancelInsNetworkChanges = function () {
                dataSource.cancelChanges();
                $scope.insNetworkChanged = false;
            };
        }
    ]);
});

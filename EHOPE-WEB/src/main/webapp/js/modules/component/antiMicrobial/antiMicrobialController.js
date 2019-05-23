define(['app', 'config', 'util'], function (app, config, util) {
    'use strict';
    app.controller('antiMicrobialCtrl', [
        '$scope', 'antiMicrobialService', 'lovService',
        function ($scope, antiMicrobialService, lovService) {
            $scope.antiMicrobial = null;
            $scope.antiMicrobialChanged = false;

            var dataSource = new kendo.data.DataSource({
                pageSize: config.gridPageSizes[0],
                page: 1,
                transport: {
                    read: function (e) {
                        e.data = util.createFilterablePageRequest(dataSource);
                        antiMicrobialService.getAntiMicrobialPage(e.data)
                            .then(function (response) {
                                e.success(response.data);
                            }).catch(function (error) {
                                e.error(error);
                            });
                    },
                    create: function (e) {
                        antiMicrobialService.addAntiMicrobial($scope.antiMicrobial)
                            .then(function (response) {
                                util.createToast(util.systemMessages.success, "success");
                                e.success({ content: response.data });
                            }).catch(function (error) {
                                e.error(error);
                            });
                    },
                    update: function (e) {
                        antiMicrobialService.updateAntiMicrobial($scope.antiMicrobial)
                            .then(function (response) {
                                util.createToast(util.systemMessages.success, "success");
                                e.success({ content: response.data });
                            }).catch(function (error) {
                                e.error(error);
                            });
                    },
                    destroy: function (e) {
                        antiMicrobialService.deleteAntiMicrobial(e.data.rid)
                            .then(function (response) {
                                util.createToast(util.systemMessages.success, "success");
                                e.success(response.data);
                            }).catch(function (error) {
                                e.error(error);
                                $("#antiMicrobialGrid").data("kendoGrid").cancelChanges();
                            });
                    }
                },
                sync: function () {
                    $scope.antiMicrobialChanged = false;
                },
                serverPaging: true,
                serverFiltering: true,
                schema: {
                    data: "content",
                    total: "totalElements",
                    model: {
                        id: "rid",
                        fields: {
                            code: { type: "string", validation: { required: true } },
                            name: { type: "string", validation: { required: true } }
                        }
                    }
                }
            });

            $scope.antiMicrobialGridOptions = {
                editable: "inline",
                dataSource: dataSource,
                columns: [
                    {
                        field: "code",
                        title: util.systemMessages.code
                    },
                    {
                        field: "name",
                        title: util.systemMessages.name
                    }
                ],
                dataBinding: function () {
                    $scope.antiMicrobial = null;
                },
                edit: function (e) {
                    e.sender.select($("tr[data-uid=" + e.model.uid + "]"));
                    $scope.antiMicrobialChanged = true;
                },
                change: function (e) {
                    var selectedRows = e.sender.select();
                    if (selectedRows.length > 0) {
                        $scope.antiMicrobial = this.dataItem(selectedRows[0]);
                    } else {
                        $scope.antiMicrobial = null;
                    }
                }
            };

            $scope.addAntiMicrobial = function () {
                var grid = $("#antiMicrobialGrid").data("kendoGrid");
                grid.addRow();
            };

            $scope.editAntiMicrobial = function (dataItem) {
                if ($scope.antiMicrobialChanged) {
                    return;
                }
                var grid = $("#antiMicrobialGrid").data("kendoGrid");
                grid.editRow(dataItem);
            };

            $scope.saveAntiMicrobial = function () {
                $("#antiMicrobialGrid").data("kendoGrid").saveChanges();
            };

            $scope.cancelAntiMicrobialChanges = function () {
                var grid = $("#antiMicrobialGrid").data("kendoGrid");
                grid.cancelChanges();
                $scope.antiMicrobialChanged = false;
            };

            $scope.deleteAntiMicrobial = function () {
                util.deleteGridRow($scope.antiMicrobial, dataSource);
                $scope.antiMicrobial = null;
            };

            $scope.refreshGrid = function () {
                $scope.antiMicrobialGrid.dataSource.read();
            };
        }
    ]);
});

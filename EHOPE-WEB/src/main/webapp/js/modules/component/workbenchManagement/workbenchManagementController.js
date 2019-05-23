define(['app', 'config', 'util', 'commonData'], function (app, config, util, commonData) {
    'use strict';
    app.controller('workbenchManagementCtrl', [
        '$scope', 'workbenchManagementService',
        function ($scope, workbenchManagementService) {
            $scope.selectedWorkbench = null;

            var workbenchDataSource = new kendo.data.DataSource({
                pageSize: config.gridPageSizes[0],
                page: 1,
                transport: {
                    read: function (e) {
                        e.data = util.createFilterablePageRequest(workbenchDataSource);
                        workbenchManagementService.getWorkbenchPage(e.data)
                            .then(function (response) {
                                e.success(response.data);
                            }).catch(function (error) {
                                e.error(error);
                            });
                    },
                    create: function (e) {
                        workbenchManagementService.addWorkbench($scope.selectedWorkbench)
                            .then(function (response) {
                                util.createToast(util.systemMessages.success, "success");
                                e.success({ content: response.data });
                            }).catch(function (error) {
                                e.error(error);
                            });
                    },
                    update: function (e) {
                        workbenchManagementService.editWorkbench($scope.selectedWorkbench)
                            .then(function (response) {
                                util.createToast(util.systemMessages.success, "success");
                                e.success({ content: response.data });
                            }).catch(function (error) {
                                e.error(error);
                            });
                    },
                    destroy: function (e) {
                        workbenchManagementService.deleteWorkbench(e.data.rid)
                            .then(function (response) {
                                util.createToast(util.systemMessages.success, "success");
                                e.success(response.data);
                            }).catch(function (error) {
                                e.error(error);
                                $("#workbenchGrid").data("kendoGrid").cancelChanges();
                            });
                    }
                },
                sync: function () {
                    $scope.workbenchChanged = false;
                },
                serverPaging: true,
                serverFiltering: true,
                sort: { field: "rid", dir: "desc" },
                schema: {
                    total: "totalElements",
                    data: "content",
                    model: {
                        id: "rid",
                        fields: {
                            name: { type: "trans" }
                        }
                    }
                }
            });

            $scope.workbenchGridOptions = {
                dataSource: workbenchDataSource,
                editable: "inline",
                columns: [
                    {
                        field: "name",
                        title: "{{ 'name' | translate}}",
                        template: function (dataItem) {
                            if (dataItem.name && dataItem.name[util.userLocale]) {
                                return dataItem.name[util.userLocale];
                            }
                            return "";
                        },
                        editor: function (container, options) {
                            util.createTransFieldEditor(container, options, "name");
                        }
                    }
                ],
                dataBinding: function () {
                    $scope.selectedWorkbench = null;
                },
                edit: function (e) {
                    e.sender.select($("tr[data-uid=" + e.model.uid + "]"));
                    $scope.workbenchChanged = true;
                },
                change: function (e) {
                    var selectedRows = e.sender.select();
                    if (selectedRows.length > 0) {
                        $scope.selectedWorkbench = this.dataItem(selectedRows[0]);
                    } else {
                        $scope.selectedWorkbench = null;
                    }
                }
            };

            $scope.addWorkbench = function () {
                var grid = $("#workbenchGrid").data("kendoGrid");
                grid.addRow();
            };

            $scope.editWorkbench = function (dataItem) {
                if ($scope.workbenchChanged) {
                    return;
                }
                var grid = $("#workbenchGrid").data("kendoGrid");
                grid.editRow(dataItem);
            };

            $scope.saveChanges = function () {
                var grid = $("#workbenchGrid").data("kendoGrid");
                grid.saveChanges();
            };

            $scope.cancelChanges = function () {
                var requestFormGrid = $("#workbenchGrid").data("kendoGrid");
                requestFormGrid.cancelChanges();
                $scope.workbenchChanged = false;
            };

            $scope.deleteWorkbench = function () {
                util.deleteGridRow($scope.selectedWorkbench, workbenchDataSource);
            };

            $scope.refreshGrid = function () {
                workbenchDataSource.read();
            };

        }
    ]);
});

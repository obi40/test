define(['app', 'config', 'util'], function (app, config, util) {
    'use strict';
    app.controller('labUnitCtrl', [
        '$scope', 'labUnitService',
        function ($scope, labUnitService) {
            $scope.labUnit = null;
            $scope.labUnitChanged = false;

            var dataSource = new kendo.data.DataSource({
                pageSize: config.gridPageSizes[0],
                page: 1,
                transport: {
                    read: function (e) {
                        e.data = util.createFilterablePageRequest(dataSource);
                        labUnitService.getLabUnitPage(e.data).then(function (response) {
                            e.success(response.data);
                        }).catch(function (error) {
                            e.error(error);
                        });
                    },
                    create: function (e) {
                        labUnitService.createLabUnit($scope.labUnit).then(function (response) {
                            util.createToast(util.systemMessages.success, "success");
                            $scope.refreshGrid();
                            $scope.labUnit = null;
                            $scope.labUnitChanged = false;
                        }).catch(function (error) {
                            e.error(error);
                        });
                    },
                    update: function (e) {
                        labUnitService.updateLabUnit($scope.labUnit).then(function (response) {
                            util.createToast(util.systemMessages.success, "success");
                            $scope.refreshGrid();
                            $scope.labUnit = null;
                            $scope.labUnitChanged = false;
                        }).catch(function (error) {
                            e.error(error);
                        });
                    },
                    destroy: function (e) {
                        labUnitService.deleteLabUnit(e.data.rid).then(function (response) {
                            util.createToast(util.systemMessages.success, "success");
                            e.success(response.data);
                        }).catch(function (error) {
                            e.error(error);
                            $("#LabUnitsGrid").data("kendoGrid").cancelChanges();
                        });
                    }
                },
                sync: function () {
                    $scope.labUnitChanged = false;
                },
                serverPaging: true,
                serverFiltering: true,
                schema: {
                    data: "content",
                    total: "totalElements",
                    model: {
                        id: "rid",
                        fields: {
                            "unitOfMeasure": {
                                type: "string",
                                editable: true,
                                nullable: false
                            },
                            "recommendedReportAbbreviation": {
                                type: "string",
                                editable: true
                            }
                        }
                    }
                }
            });


            $scope.labUnitsGridOptions = {
                editable: "inline",
                dataSource: dataSource,
                columns: [
                    {
                        field: "unitOfMeasure",
                        title: util.systemMessages.unitOfMeasure,
                        template: function (dataItem) {
                            return dataItem.unitOfMeasure;
                        }
                    },
                    {
                        field: "recommendedReportAbbreviation",
                        title: util.systemMessages.recommendedReportAbbreviation,
                        template: function (dataItem) {
                            return dataItem.recommendedReportAbbreviation;
                        }
                    }
                ],
                dataBinding: function () {
                    $scope.labUnit = null;
                },
                edit: function (e) {
                    e.sender.select($("tr[data-uid=" + e.model.uid + "]"));
                    $scope.labUnitChanged = true;
                },
                change: function (e) {
                    var selectedRows = e.sender.select();
                    if (selectedRows.length > 0) {
                        $scope.labUnit = this.dataItem(selectedRows[0]);
                    } else {
                        $scope.labUnit = null;
                    }
                }
            };


            $scope.addLabUnit = function () {
                var grid = $("#labUnitsGrid").data("kendoGrid");
                grid.addRow();
            };

            $scope.editLabUnit = function (dataItem) {
                if ($scope.labUnitChanged) {
                    return;
                }
                var grid = $("#labUnitsGrid").data("kendoGrid");
                grid.editRow(dataItem);
            };

            $scope.saveLabUnit = function () {
                var grid = $("#labUnitsGrid").data("kendoGrid");
                grid.saveChanges();
            };

            $scope.cancelLabUnitChanges = function () {
                var requestFormGrid = $("#labUnitsGrid").data("kendoGrid");
                requestFormGrid.cancelChanges();
                $scope.labUnitChanged = false;
            };

            $scope.deleteLabUnit = function () {
                util.deleteGridRow($scope.labUnit, dataSource);
                $scope.labUnit = null;
            };

            $scope.refreshGrid = function () {
                $scope.labUnitsGrid.dataSource.read();
            };

        }
    ]);
});

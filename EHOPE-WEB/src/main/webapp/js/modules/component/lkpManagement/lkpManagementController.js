define(['app', 'config', 'util'], function (app, config, util) {
    'use strict';
    app.controller('lkpManagementCtrl', [
        '$scope', 'lkpManagementService', 'lovService',
        function ($scope, lkpManagementService, lovService) {
            $scope.selectedLkp = null;
            $scope.ruleCss = (util.direction === "ltr") ? "left-rule" : "right-rule";
            $scope.lkpMasterList = [];
            $scope.selectedMasterLkp = null;
            var allLkpMaster = [];
            $scope.userLocale = util.userLocale;
            lkpManagementService.getLkpMasterList()
                .then(function (response) {
                    $scope.lkpMasterList = response.data;
                    allLkpMaster = $scope.lkpMasterList;
                    //select the first by default
                    $scope.selectedMasterLkp = $scope.lkpMasterList[0];
                    dataSource.read();
                });

            $scope.searchLkpMasterList = function () {
                $scope.selectedLkp = null;
                $scope.lkpMasterList = allLkpMaster;
                if ($scope.searchLkpMaster.length <= 0) {
                    return;
                }
                var searchedData = [];
                var regex = new RegExp("^.*(" + $scope.searchLkpMaster + ").*$", "i");
                for (var idx = 0; idx < $scope.lkpMasterList.length; idx++) {
                    var obj = $scope.lkpMasterList[idx];
                    if (obj.name[util.userLocale] != undefined && regex.test(obj.name[util.userLocale])) {
                        searchedData.push(obj);
                    }
                }
                $scope.lkpMasterList = searchedData;
            };

            $scope.highlightSelected = function (lkp, isAdd) {
                if (isAdd) {
                    $("#lkp_" + lkp.rid).closest("md-list-item").addClass("highlight");
                } else {
                    $("#lkp_" + lkp.rid).closest("md-list-item").removeClass("highlight");
                }
            }

            $scope.viewLkp = function (lkp) {
                if (lkp.rid === $scope.selectedMasterLkp.rid) {// dont run if it is the same last selected Lkp
                    return;
                }
                $scope.highlightSelected($scope.selectedMasterLkp, false);
                $scope.selectedMasterLkp = lkp;
                $scope.highlightSelected($scope.selectedMasterLkp, true);
                dataSource.read();
            };

            $scope.refreshGrid = function () {
                dataSource.read();
            };

            var dataSource = new kendo.data.DataSource({
                pageSize: config.gridPageSizes[0],
                page: 1,
                transport: {
                    read: function (e) {
                        var lkpWrapper = {
                            "className": $scope.selectedMasterLkp.entity
                        };
                        if ($scope.selectedMasterLkp.entity === "LkpContainerType") {
                            $scope.lkpsGridOptions.columns.push({
                                field: "color",
                                width: "10%",
                                title: util.systemMessages.color,
                                template: function (dataItem) {
                                    return "<div class='color-display-block' style='background-color: " + dataItem.color + ";'></div>";
                                },
                                editor: function (container, options) {
                                    $('<input required name="' + options.field + '"/>')
                                        .appendTo(container)
                                        .kendoColorPicker({
                                            buttons: false,
                                            palette: "basic"
                                        });
                                }
                            });
                            $("#lkpsGrid").data("kendoGrid").setOptions({
                                columns: $scope.lkpsGridOptions.columns
                            });
                        } else {
                            for (var i = 0; i < $scope.lkpsGridOptions.columns.length; i++) {
                                if ($scope.lkpsGridOptions.columns[i].field === "color") {
                                    $scope.lkpsGridOptions.columns.splice(i, 1);
                                    $("#lkpsGrid").data("kendoGrid").setOptions({
                                        columns: $scope.lkpsGridOptions.columns
                                    });
                                    break;
                                }
                            }
                        }
                        lovService.getLkpByClass(lkpWrapper).then(function (data) {
                            e.success(data);
                        }).catch(function (error) {
                            e.error(error);
                        });
                    },
                    create: function (e) {
                        var map = {
                            className: $scope.selectedMasterLkp.entity,
                            object: JSON.stringify(e.data)
                        };
                        lovService.createLkp(map)
                            .then(function (response) {
                                util.createToast(util.systemMessages.success, "success");
                                e.success(parseResponseObject(response.data));
                            }).catch(function (error) {
                                e.error(error);
                            });
                    },
                    update: function (e) {
                        var map = {
                            className: $scope.selectedMasterLkp.entity,
                            object: JSON.stringify(e.data)
                        };
                        lovService.updateLkp(map)
                            .then(function (response) {
                                util.createToast(util.systemMessages.success, "success");
                                e.success(parseResponseObject(response.data));
                            }).catch(function (error) {
                                e.error(error);
                            });
                    },
                    destroy: function (e) {
                        var map = {
                            className: $scope.selectedMasterLkp.entity,
                            object: JSON.stringify(e.data)
                        };
                        lovService.deleteLkp(map).then(function (response) {
                            e.success(response.data);
                            util.createToast(util.systemMessages.success, "success");
                        }).catch(function (error) {
                            e.error(error);
                            $scope.refreshGrid();
                        });
                    }
                },
                sync: function () {
                    $scope.lkpChanged = false;
                },
                schema: {
                    parse: function (response) {
                        for (var idx = 0; idx < response.length; idx++) {
                            response[idx] = parseResponseObject(response[idx]);
                        }
                        return response;
                    },
                    model: {
                        id: "rid",
                        fields: {
                            "code": {
                                editable: true,
                                nullable: false,
                                type: "string",
                                validation: { required: true }
                            },
                            "nameLocale": {
                                editable: true,
                                type: "string"
                            },
                            "descriptionLocale": {
                                editable: true,
                                type: "string"
                            }
                        }
                    }
                }
            });

            function parseResponseObject(responseObject) {
                responseObject["nameLocale"] = responseObject.name[util.userLocale];
                responseObject["descriptionLocale"] = responseObject.description[util.userLocale];
                return responseObject;
            }

            $scope.lkpsGridOptions = {
                columns: [
                    {
                        field: "code",
                        title: util.systemMessages.code,
                        width: "10%"
                    },
                    {
                        field: "nameLocale",
                        title: util.systemMessages.name,
                        editor: function (container, options) {
                            util.createTransFieldEditor(container, options, "name");
                        }
                    },
                    {
                        field: "descriptionLocale",
                        title: util.systemMessages.description,
                        editor: function (container, options) {
                            util.createTransFieldEditor(container, options, "description");
                        }
                    }],
                dataSource: dataSource,
                editable: "inline",
                autoBind: false,
                dataBound: function () {
                    $scope.selectedLkp = null;
                },
                edit: function (e) {
                    e.sender.select($("tr[data-uid=" + e.model.uid + "]"));
                    $scope.lkpChanged = true;
                },
                change: function () {
                    $scope.selectedLkp = $scope.lkpsGrid.dataItem($scope.lkpsGrid.select());
                }
            };

            $scope.saveLkp = function () {
                $("#lkpsGrid").data("kendoGrid").saveChanges();
            };

            $scope.addLkp = function () {
                var grid = $("#lkpsGrid").data("kendoGrid");
                grid.addRow();
            };

            $scope.editLkp = function (dataItem) {
                if ($scope.lkpChanged) {
                    return;
                }
                var grid = $("#lkpsGrid").data("kendoGrid");
                grid.editRow(dataItem);
            };

            $scope.deleteLkp = function () {
                util.deleteGridRow($scope.selectedLkp, dataSource);
            };

            $scope.cancelLkpChanges = function () {
                dataSource.cancelChanges();
                $scope.lkpChanged = false;
            };

        }
    ]);
});
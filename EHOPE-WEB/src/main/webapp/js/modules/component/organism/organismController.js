define(['app', 'config', 'util'], function (app, config, util) {
    'use strict';
    app.controller('organismCtrl', [
        '$scope', 'organismService', 'lovService',
        function ($scope, organismService, lovService) {

            $scope.organism = null;
            $scope.organismChanged = false;

            var dataSource = new kendo.data.DataSource({
                pageSize: config.gridPageSizes[0],
                page: 1,
                transport: {
                    read: function (e) {
                        var filterMap = { "typeRid": "type.rid" };
                        e.data = util.createFilterablePageRequest(dataSource, filterMap);
                        organismService.getOrganismPage(e.data)
                            .then(function (response) {
                                e.success(response.data);
                            }).catch(function (error) {
                                e.error(error);
                            });
                    },
                    create: function (e) {
                        organismService.addOrganism($scope.organism)
                            .then(function (response) {
                                util.createToast(util.systemMessages.success, "success");
                                e.success({ content: response.data });
                            }).catch(function (error) {
                                e.error(error);
                            });
                    },
                    update: function (e) {
                        organismService.updateOrganism($scope.organism)
                            .then(function (response) {
                                util.createToast(util.systemMessages.success, "success");
                                e.success({ content: response.data });
                            }).catch(function (error) {
                                e.error(error);
                            });
                    },
                    destroy: function (e) {
                        organismService.deleteOrganism(e.data.rid)
                            .then(function (response) {
                                util.createToast(util.systemMessages.success, "success");
                                e.success(response.data);
                            }).catch(function (error) {
                                e.error(error);
                                $("#organismGrid").data("kendoGrid").cancelChanges();
                            });
                    }
                },
                sync: function () {
                    $scope.organismChanged = false;
                },
                serverPaging: true,
                serverFiltering: true,
                schema: {
                    data: "content",
                    total: "totalElements",
                    model: {
                        id: "rid",
                        fields: {
                            code: { type: "string" },
                            name: { type: "string" },
                            typeRid: { type: "lov" },
                            type: { defaultValue: {}, editable: true, validation: { required: true } }
                        }
                    }
                }
            });

            $scope.organismGridOptions = {
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
                    },
                    {
                        field: "typeRid",
                        title: "{{ 'type' | translate }}",
                        editor: organismTypeEditor,
                        filterable: {
                            ui: function (element) {
                                util.createLovFilter(element, { className: "LkpOrganismType" }, lovService.getLkpByClass);
                            }
                        },
                        template: function (dataItem) {
                            if (dataItem && dataItem.type && dataItem.type.name) {
                                return dataItem.type.name[util.userLocale];
                            }
                            return util.systemMessages.none;
                        }
                    }
                ],
                dataBinding: function () {
                    $scope.organism = null;
                },
                edit: function (e) {
                    e.sender.select($("tr[data-uid=" + e.model.uid + "]"));
                    $scope.organismChanged = true;
                },
                change: function (e) {
                    var selectedRows = e.sender.select();
                    if (selectedRows.length > 0) {
                        $scope.organism = this.dataItem(selectedRows[0]);
                    } else {
                        $scope.organism = null;
                    }
                }
            };

            $scope.addOrganism = function () {
                var grid = $("#organismGrid").data("kendoGrid");
                grid.addRow();
            };

            $scope.editOrganism = function (dataItem) {
                if ($scope.organismChanged) {
                    return;
                }
                var grid = $("#organismGrid").data("kendoGrid");
                grid.editRow(dataItem);
            };

            $scope.saveOrganism = function () {
                $("#organismGrid").data("kendoGrid").saveChanges();
            };

            $scope.cancelOrganismChanges = function () {
                var grid = $("#organismGrid").data("kendoGrid");
                grid.cancelChanges();
                $scope.organismChanged = false;
            };

            $scope.deleteOrganism = function () {
                util.deleteGridRow($scope.organism, dataSource);
                $scope.organism = null;
            };

            $scope.refreshGrid = function () {
                $scope.organismGrid.dataSource.read();
            };

            function organismTypeEditor(container, options) {
                dropDownListEditor(container, options, "LkpOrganismType");
            }

            function dropDownListEditor(container, options, className) {
                var dropDownListDataSource = new kendo.data.DataSource({
                    schema: {
                        model: {
                            id: "rid"
                        }
                    },
                    transport: {
                        read: function (e) {
                            lovService.getLkpByClass({ className: className })
                                .then(function successCallback(response) {
                                    response.unshift({
                                        rid: null
                                    });
                                    e.success(response);
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

                $('<input name="' + fieldName + '"/>')
                    .appendTo(container)
                    .kendoDropDownList({
                        dataValueField: "rid",
                        valueTemplate: function (dataItem) {
                            if (dataItem.rid === null) {
                                return util.systemMessages.none;
                            }
                            return dataItem.name[util.userLocale];
                        },
                        template: function (dataItem) {
                            if (dataItem.rid === null) {
                                return util.systemMessages.none;
                            }
                            return dataItem.name[util.userLocale];
                        },
                        dataSource: dropDownListDataSource,
                        dataBound: function (e) {
                            var selectedIndex = e.sender.select();
                            e.sender.select(selectedIndex >= 0 ? selectedIndex : 0);
                            e.sender.trigger("change");
                        }
                    });
            }

            $scope.organismFileWrapper = {
                oldFile: null,
                fileModel: null, // must be null
                types: ["xls", "xlsx"],
                labelCode: ""
            };

            $scope.importOrganisms = function (event) {
                if ($scope.organismFileWrapper.fileModel == null) {
                    return;
                }

                event.target.disabled = true;//to disable the button till request is back, so we can keep the uploaded file if request failed
                organismService.importOrganisms($scope.organismFileWrapper.fileModel)
                    .then(function (response) {
                        util.createToast(util.systemMessages.success, "success");
                        $scope.organismFileWrapper.reset();
                    }).catch(function (error) {
                        event.target.disabled = false;//to disable the button till request is back, so we can keep the uploaded file if request failed
                    });
            };
        }
    ]);
});

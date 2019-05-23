define(['app', 'config', 'util'], function (app, config, util) {
    'use strict';
    app.controller('sectionCtrl', [
        '$scope', 'sectionService', 'lovService', 'billingManagementService', '$mdDialog',
        function ($scope, sectionService, lovService, billingManagementService, $mdDialog) {
            $scope.section = null;
            $scope.sectionChanged = false;
            var sectionOldType = null;

            var dataSource = new kendo.data.DataSource({
                pageSize: config.gridPageSizes[0],
                page: 1,
                transport: {
                    read: function (e) {
                        var filterMap = { "typeRid": "type.rid" };
                        e.data = util.createFilterablePageRequest(dataSource, filterMap);
                        sectionService.getSectionPage(e.data)
                            .then(function (response) {
                                e.success(response.data);
                            }).catch(function (error) {
                                e.error(error);
                            });
                    },
                    create: function (e) {
                        $scope.section.classification = [];
                        $scope.section.classification.push($scope.section.singleClassification);

                        sectionService.addSection($scope.section)
                            .then(function (response) {
                                util.createToast(util.systemMessages.success, "success");
                                e.success({ content: response.data });
                                $scope.refreshGrid(); //this is needed because the rank changes on 2 elements simultaneously
                            }).catch(function (error) {
                                e.error(error);
                            });
                    },
                    update: function (e) {
                        $scope.section.classification = [];
                        $scope.section.classification.push($scope.section.singleClassification);

                        sectionService.updateSection($scope.section)
                            .then(function (response) {
                                util.createToast(util.systemMessages.success, "success");
                                e.success({ content: response.data });
                                $scope.refreshGrid(); //this is needed because the rank changes on 2 elements simultaneously
                            }).catch(function (error) {
                                e.error(error);
                            });
                    },
                    destroy: function (e) {
                        sectionService.deleteSection(e.data.rid)
                            .then(function (response) {
                                util.createToast(util.systemMessages.success, "success");
                                e.success(response.data);
                            }).catch(function (error) {
                                e.error(error);
                                $("#sectionsGrid").data("kendoGrid").cancelChanges();
                            });
                    }
                },
                sync: function () {
                    $scope.sectionChanged = false;
                },
                serverPaging: true,
                serverFiltering: true,
                schema: {
                    parse: function (response) {
                        if (response.content.length) {
                            for (var i = 0; i < response.content.length; i++) {
                                response.content[i].oldRank = response.content[i].rank;
                                response.content[i].singleClassification = response.content[i].classification[0];
                            }
                        } else {
                            response.content.oldRank = response.content.rank;
                            response.content.singleClassification = response.content.classification[0];
                        }
                        return response;
                    },
                    data: "content",
                    total: "totalElements",
                    model: {
                        id: "rid",
                        fields: {
                            "name": {
                                type: "trans",
                                editable: true
                            },
                            "isActive": {
                                type: "boolean",
                                editable: true
                            },
                            "rank": {
                                type: "number",
                                editable: true,
                                nullable: false
                            },
                            "oldRank": {
                                type: "number",
                                editable: false
                            },
                            typeRid: {
                                type: "lov"
                            },
                            type: {
                                defaultValue: {},
                                editable: true,
                                validation: { required: true }
                            }
                        }
                    }
                }
            });

            $scope.sectionsGridOptions = {
                editable: "inline",
                dataSource: dataSource,
                columns: [
                    {
                        field: "name",
                        title: util.systemMessages.name,
                        template: function (dataItem) {
                            if (dataItem.name && dataItem.name[util.userLocale]) {
                                return dataItem.name[util.userLocale];
                            }
                            return "";
                        },
                        editor: function (container, options) {
                            util.createTransFieldEditor(container, options, "name");
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
                        field: "rank",
                        title: util.systemMessages.ranking
                    },
                    {
                        field: "typeRid",
                        title: "{{ 'type' | translate }}",
                        editor: sectionTypeEditor,
                        filterable: {
                            ui: function (element) {
                                util.createLovFilter(element, { className: "LkpSectionType" }, lovService.getLkpByClass);
                            }
                        },
                        template: function (dataItem) {
                            if (dataItem.type && dataItem.type.name) {
                                return dataItem.type.name[util.userLocale];
                            }
                            return util.systemMessages.none;
                        }
                    },
                    {
                        field: "singleClassification",
                        title: "{{ 'billingClassification' | translate }}",
                        editor: classificationEditor,
                        sortable: false,
                        filterable: false,
                        template: function (dataItem) {
                            if (dataItem.singleClassification) {
                                return dataItem.singleClassification.name;
                            }
                            return util.systemMessages.none;
                        }
                    }
                ],
                dataBinding: function () {
                    $scope.section = null;
                },
                edit: function (e) {
                    e.sender.select($("tr[data-uid=" + e.model.uid + "]"));
                    $scope.sectionChanged = true;
                },
                change: function (e) {
                    var selectedRows = e.sender.select();
                    if (selectedRows.length > 0) {
                        $scope.section = this.dataItem(selectedRows[0]);
                    } else {
                        $scope.section = null;
                    }
                }
            };

            $scope.addSection = function () {
                var grid = $("#sectionsGrid").data("kendoGrid");
                grid.addRow();
            };

            $scope.editSection = function (dataItem) {
                if ($scope.sectionChanged) {
                    return;
                }
                var grid = $("#sectionsGrid").data("kendoGrid");
                grid.editRow(dataItem);
                sectionOldType = dataItem.type;
            };

            $scope.saveSection = function () {
                if (sectionOldType && sectionOldType.code === "MICROBIOLOGY" &&
                    (!$scope.section.type || $scope.section.type.code !== "MICROBIOLOGY")) {
                    var title = util.systemMessages.warning;
                    var textContent = util.systemMessages.changeSectionTypeFromMicrobiology;
                    var confirm = $mdDialog.confirm()
                        .title(title)
                        .textContent(textContent)
                        .clickOutsideToClose(true)
                        .targetEvent(event)
                        .ok(util.systemMessages.ok)
                        .cancel(util.systemMessages.cancel);
                    $mdDialog.show(confirm)
                        .then(function () {
                            $("#sectionsGrid").data("kendoGrid").saveChanges();
                        }, function () {

                        });
                } else {
                    $("#sectionsGrid").data("kendoGrid").saveChanges();
                }
            };

            $scope.cancelSectionChanges = function () {
                var grid = $("#sectionsGrid").data("kendoGrid");
                grid.cancelChanges();
                $scope.sectionChanged = false;
            };

            $scope.deleteSection = function () {
                util.deleteGridRow($scope.section, dataSource);
                $scope.section = null;
            };

            $scope.refreshGrid = function () {
                $scope.sectionsGrid.dataSource.read();
            };

            function classificationEditor(container, options) {
                dropDownListEditor(container, options, "BillClassification");
            }

            function sectionTypeEditor(container, options) {
                dropDownListEditor(container, options, "LkpSectionType");
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
                            if (className === "BillClassification") {
                                billingManagementService.getBillClassificationList({ data: { filters: [] } })
                                    .then(function (response) {
                                        e.success(response.data);
                                    }).catch(function (response) {
                                        e.error(response);
                                    });
                            } else {
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
                            if (className === "BillClassification") {
                                return dataItem.name;
                            }
                            return dataItem.name[util.userLocale];
                        },
                        template: function (dataItem) {
                            if (dataItem.rid === null) {
                                return util.systemMessages.none;
                            }
                            if (className === "BillClassification") {
                                return dataItem.name;
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
        }
    ]);
});

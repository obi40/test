define(['app', 'util', 'config'], function (app, util, config) {
    'use strict';
    app.directive('destinationSetup', function () {
        return {
            restrict: 'E', // This means that it will be used as an element.
            replace: true,
            scope: {
                options: "=options" //options.testDefinition.destinations
            },
            templateUrl: "./" + config.lisDir + "/modules/shared/directives/destinationSetup/destination-setup-view.html",
            controller: ['$scope', '$filter', '$q', 'lovService', 'clientManagementService', 'workbenchManagementService',
                function ($scope, $filter, $q, lovService, clientManagementService, workbenchManagementService) {

                    if (!$scope.options.testDefinition.destinations) {
                        $scope.options.testDefinition.destinationList = [];
                    } else {
                        $scope.options.testDefinition.destinationList = angular.copy($scope.options.testDefinition.destinations);
                    }

                    //TODO: ADD GROUPS TO DROP DOWN LISTS

                    $scope.destinationChanged = false;

                    $scope.addDestination = function () {
                        var grid = $("#destinationGrid").data("kendoGrid");
                        grid.addRow();
                    };

                    $scope.editDestination = function (dataItem) {
                        if (!$scope.selectedDestination) {
                            return;
                        }
                        var grid = $("#destinationGrid").data("kendoGrid");
                        grid.editRow(dataItem);
                    };

                    $scope.saveDestination = function () {
                        var grid = $("#destinationGrid").data("kendoGrid");
                        grid.saveChanges();
                    };

                    $scope.cancelDestination = function () {
                        var grid = $("#destinationGrid").data("kendoGrid");
                        grid.cancelChanges();
                        grid.saveChanges();
                        $scope.destinationChanged = false;
                    };

                    var destinationDataSource = new kendo.data.DataSource({
                        pageSize: config.gridPageSizes[0],
                        page: 1,
                        transport: {
                            read: function (e) {
                                e.success($scope.options.testDefinition.destinationList);
                            },
                            create: function (e) {
                                var destination = e.data;
                                e.success(destination);
                            },
                            update: function (e) {
                                var destination = e.data;
                                e.success(destination);
                            }
                        },
                        sync: function (e) {
                            $scope.destinationChanged = false;
                            var data = e.sender.data();
                            for (var i = 0; i < data.length; i++) {
                                var dataItem = data[i];
                                if (dataItem.type.code === "WORKBENCH") {
                                    dataItem.destinationBranch = null;
                                    dataItem.workbench = dataItem.compositeDestination;
                                } else {
                                    dataItem.workbench = null;
                                    dataItem.destinationBranch = dataItem.compositeDestination;
                                }
                            }
                            $scope.options.testDefinition.destinationList = data;
                            destinationDataSource.read();
                        },
                        schema: {
                            parse: function (response) {
                                for (var i = 0; i < response.length; i++) {
                                    if (response[i].type.code === "WORKBENCH") {
                                        response[i].compositeDestination = response[i].workbench;
                                    } else {
                                        response[i].compositeDestination = response[i].destinationBranch;
                                    }
                                }
                                return response;
                            },
                            model: {
                                id: "rid",
                                fields: {
                                    source: { type: "lov" },
                                    type: { type: "lov" },
                                    destinationBranch: { type: "lov" },
                                    workbench: { type: "lov" },
                                    compositeDestination: { type: "lov" },
                                    isActive: { type: "boolean", defaultValue: true }
                                }
                            }
                        }
                    });

                    $scope.destinationGridOptions = {
                        dataSource: destinationDataSource,
                        editable: "inline",
                        filterable: false,
                        columns: [
                            {
                                field: "source",
                                title: "{{ 'sourceBranch' | translate}}",
                                template: function (dataItem) {
                                    var template = "{{ 'none' | translate }}";
                                    try {
                                        template = dataItem.source.name[util.userLocale];
                                    } catch (e) { }
                                    return template;
                                },
                                editor: sourceEditor
                            },
                            {
                                field: "type",
                                title: "{{ 'type' | translate}}",
                                template: function (dataItem) {
                                    var template = "{{ 'none' | translate }}";
                                    try {
                                        template = dataItem.type.name[util.userLocale];
                                    } catch (e) { }
                                    return template;
                                },
                                editor: destinationTypeEditor
                            },
                            {
                                field: "compositeDestination",
                                title: "{{ 'destination' | translate}}",
                                template: function (dataItem) {
                                    var template = "{{ 'none' | translate }}";
                                    try {
                                        template = dataItem.compositeDestination.name[util.userLocale];
                                    } catch (e) { }
                                    return template;
                                },
                                editor: destinationEditor
                            },
                            {
                                field: "isActive",
                                title: "{{ 'active' | translate }}",
                                template: "{{ #: isActive # ? 'yes' : 'no' | translate }}"
                            }
                        ],
                        dataBinding: function () {
                            $scope.selectedDestination = null;
                        },
                        edit: function (e) {
                            e.sender.select($("tr[data-uid=" + e.model.uid + "]"));
                            $scope.destinationChanged = true;
                        },
                        change: function (e) {
                            var selectedRows = e.sender.select();
                            if (selectedRows.length > 0) {
                                $scope.selectedDestination = this.dataItem(selectedRows[0]);
                            } else {
                                $scope.selectedDestination = null;
                            }
                        }
                    };

                    var sourceEditorId = "sourceEditor";
                    var destinationTypeEditorId = "destinationTypeEditor";
                    var destinationEditorId = "destinationEditor";

                    function sourceEditor(container, options) {
                        var dropDownListDataSource = new kendo.data.DataSource({
                            schema: {
                                model: {
                                    id: "rid"
                                }
                            },
                            transport: {
                                read: function (e) {
                                    clientManagementService.getClientList({
                                        purpose: "SOURCE",
                                        type: "LOCAL"
                                    })
                                        .then(function (response) {
                                            e.success(response.data);
                                        }).catch(function (response) {
                                            e.error(response);
                                        });
                                }
                            }
                        });

                        $('<input required id="' + sourceEditorId + '" name="' + options.field + '"/>')
                            .appendTo(container)
                            .kendoDropDownList({
                                dataValueField: "rid",
                                optionLabel: $filter('translate')('selectValue'),
                                valueTemplate: function (dataItem) {
                                    return dropDownListTemplate("name." + util.userLocale, dataItem);
                                },
                                template: function (dataItem) {
                                    return dropDownListTemplate("name." + util.userLocale, dataItem);
                                },
                                dataSource: dropDownListDataSource
                            });
                    }

                    function destinationTypeEditor(container, options) {
                        var dropDownListDataSource = new kendo.data.DataSource({
                            serverFiltering: true,
                            schema: {
                                model: {
                                    id: "rid"
                                }
                            },
                            transport: {
                                read: function (e) {
                                    lovService.getLkpByClass({ className: "LkpTestDestinationType" })
                                        .then(function (response) {
                                            e.success(response);
                                        }).catch(function (response) {
                                            e.error(response);
                                        });
                                }
                            }
                        });

                        $('<input required id="' + destinationTypeEditorId + '" name="' + options.field + '"/>')
                            .appendTo(container)
                            .kendoDropDownList({
                                dataValueField: "rid",
                                cascadeFrom: sourceEditorId,
                                optionLabel: $filter('translate')('selectValue'),
                                valueTemplate: function (dataItem) {
                                    return dropDownListTemplate("name." + util.userLocale, dataItem);
                                },
                                template: function (dataItem) {
                                    return dropDownListTemplate("name." + util.userLocale, dataItem);
                                },
                                dataSource: dropDownListDataSource
                            });
                    }

                    function destinationEditor(container, options) {
                        var dropDownListDataSource = new kendo.data.DataSource({
                            serverFiltering: true,
                            schema: {
                                model: {
                                    id: "rid"
                                }
                            },
                            transport: {
                                read: function (e) {
                                    var destinationTypeDropDownList = $("#" + destinationTypeEditorId).data("kendoDropDownList");
                                    var destinationType = destinationTypeDropDownList.dataItem();
                                    switch (destinationType.code) {
                                        case "WORKBENCH":
                                            workbenchManagementService.getWorkbenchList($scope.selectedDestination.source.insuranceBranch.rid)
                                                .then(function (response) {
                                                    e.success(response.data);
                                                })
                                                .catch(function (response) {
                                                    e.error(response);
                                                });
                                            break;
                                        case "LOCAL":
                                            clientManagementService.getClientList(
                                                {
                                                    branchRidToExclude: $scope.selectedDestination.source.rid,
                                                    purpose: "DESTINATION",
                                                    type: "LOCAL"
                                                })
                                                .then(function (response) {
                                                    e.success(response.data);
                                                })
                                                .catch(function (response) {
                                                    e.error(response);
                                                });
                                            break;
                                        case "ACCULAB":
                                            clientManagementService.getClientList(
                                                {
                                                    type: "ACCULAB"
                                                })
                                                .then(function (response) {
                                                    e.success(response.data);
                                                })
                                                .catch(function (response) {
                                                    e.error(response);
                                                });
                                            break;
                                        case "EXTERNAL":
                                            clientManagementService.getClientList(
                                                {
                                                    type: "EXTERNAL"
                                                })
                                                .then(function (response) {
                                                    e.success(response.data);
                                                })
                                                .catch(function (response) {
                                                    e.error(response);
                                                });
                                            break;
                                    }
                                }
                            }
                        });

                        $('<input required id="' + destinationEditorId + '" name="' + options.field + '"/>')
                            .appendTo(container)
                            .kendoDropDownList({
                                dataValueField: "rid",
                                optionLabel: $filter('translate')('selectValue'),
                                cascadeFrom: destinationTypeEditorId,
                                valueTemplate: function (dataItem) {
                                    return dropDownListTemplate("name." + util.userLocale, dataItem);
                                },
                                template: function (dataItem) {
                                    return dropDownListTemplate("name." + util.userLocale, dataItem);
                                },
                                dataSource: dropDownListDataSource
                            });
                    }

                    function dropDownListTemplate(field, dataItem) {
                        var template = $filter('translate')('none');
                        try {
                            template = util.getDeepValueInObj(dataItem, field);
                        } catch (e) { }
                        return template;
                    }

                }]
        }
    });
});
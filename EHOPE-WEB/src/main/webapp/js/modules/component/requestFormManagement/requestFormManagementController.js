define(['app', 'config', 'util'], function (app, config, util) {
    'use strict';
    app.controller('requestFormManagementCtrl', [
        '$scope', 'requestFormManagementService', 'testDefinitionManagementService', 'testSelectionService',
        function ($scope, requestFormManagementService, testDefinitionManagementService, testSelectionService) {

            //#region RequestForm

            $scope.selectedRequestForm = null;
            $scope.requestFormChanged = false;
            var quickSearchFilters = [];
            var sectionFilter = null;

            $scope.activateRequestForm = function () {
                requestFormManagementService.activateRequestForm($scope.selectedRequestForm.rid)
                    .then(function () {
                        util.createToast(util.systemMessages.success, "success");
                        requestFormDataSource.read();
                    });
            };

            $scope.deactivateRequestForm = function () {
                requestFormManagementService.deactivateRequestForm($scope.selectedRequestForm.rid)
                    .then(function () {
                        util.createToast(util.systemMessages.success, "success");
                        requestFormDataSource.read();
                    });
            };

            $scope.addRequestForm = function () {
                var grid = $("#requestFormGrid").data("kendoGrid");
                grid.addRow();
            };

            $scope.editRequestForm = function (dataItem) {
                var grid = $("#requestFormGrid").data("kendoGrid");
                grid.editRow(dataItem);
            };

            $scope.saveRequestForm = function () {
                var grid = $("#requestFormGrid").data("kendoGrid");
                grid.saveChanges();
            };

            $scope.cancelRequestForm = function () {
                var requestFormGrid = $("#requestFormGrid").data("kendoGrid");
                requestFormGrid.cancelChanges();
                $scope.requestFormChanged = false;
            };

            $scope.refreshRequestForms = function () {
                requestFormDataSource.read();
            }

            var requestFormDataSource = new kendo.data.DataSource({
                pageSize: config.gridPageSizes[0],
                page: 1,
                transport: {
                    read: function (e) {
                        requestFormManagementService.getRequestForms([])
                            .then(function (response) {
                                e.success(response.data);
                            }).catch(function (error) {
                                e.error(error);
                            });
                    },
                    create: function (e) {
                        requestFormManagementService.addRequestForm($scope.selectedRequestForm)
                            .then(function (response) {
                                util.createToast(util.systemMessages.success, "success");
                                e.success(response.data);
                            }).catch(function (error) {
                                e.error(error);
                            });
                    }, update: function (e) {
                        requestFormManagementService.editRequestForm($scope.selectedRequestForm)
                            .then(function (response) {
                                util.createToast(util.systemMessages.success, "success");
                                e.success(response.data)
                            }).catch(function (error) {
                                e.error(error);
                            });
                    }
                },
                sync: function () {
                    $scope.requestFormChanged = false;
                },
                sort: { field: "rid", dir: "desc" },
                schema: {
                    model: {
                        id: "rid",
                        fields: {
                            name: { type: "trans" },
                            description: { type: "trans" },
                            isActive: { type: "boolean", editable: false, defaultValue: true }
                        }
                    }
                }
            });

            $scope.requestFormGridOptions = {
                dataSource: requestFormDataSource,
                editable: "inline",
                columns: [
                    {
                        field: "name",
                        title: "{{ 'name' | translate}}",
                        sortable: false,
                        filterable: false,
                        template: function (dataItem) {
                            if (dataItem.name) {
                                return dataItem.name[util.userLocale];
                            }
                            return "";
                        },
                        editor: function (container, options) {
                            util.createTransFieldEditor(container, options, "name");
                        }
                    },
                    {
                        field: "description",
                        title: "{{ 'description' | translate }}",
                        sortable: false,
                        filterable: false,
                        template: function (dataItem) {
                            if (dataItem.description) {
                                return dataItem.description[util.userLocale];
                            }
                            return "";
                        },
                        editor: function (container, options) {
                            util.createTransFieldEditor(container, options, "description");
                        }
                    },
                    {
                        field: "isActive",
                        title: "{{ 'active' | translate }}",
                        template: "{{ #: isActive # ? 'yes' : 'no' | translate }}"
                    }
                ],
                dataBinding: function () {
                    $scope.selectedRequestForm = null;
                },
                edit: function (e) {
                    e.sender.select($("tr[data-uid=" + e.model.uid + "]"));
                    $scope.requestFormChanged = true;
                },
                change: function (e) {
                    var selectedRows = e.sender.select();
                    if (selectedRows.length > 0) {
                        var newlySelected = this.dataItem(selectedRows[0]);
                        if ($scope.selectedRequestForm == newlySelected) {
                            return;
                        } else {
                            $scope.selectedRequestForm = newlySelected;
                        }
                        $scope.selectedRequestFormIsActive = $scope.selectedRequestForm.isActive;
                        $scope.requestFormTestsChipsOptions.data = [];
                        if ($scope.selectedRequestForm.rid) {
                            requestFormManagementService.getRequestFormTests($scope.selectedRequestForm.rid)
                                .then(function (response) {
                                    var sections = response.data;
                                    for (var i = 0; i < sections.length; i++) {
                                        var tests = sections[i].testDefinitionList;
                                        for (var j = 0; j < tests.length; j++) {
                                            $scope.requestFormTestsChipsOptions.data.push(tests[j]);
                                        }
                                    }
                                    //to select the items in the test-grid automatically
                                    util.gridSelectionDataBound($("#requestFormTestGrid").data("kendoGrid"), $scope.requestFormTestsChipsOptions.data, $scope.runOnChangeRequestFormTests);
                                });
                        }
                    } else {
                        $scope.selectedRequestForm = null;
                    }
                }
            };
            //#endregion

            //#region requestFormTest

            $scope.runOnChangeRequestFormTests = { value: true };

            var requestFormTestDataSource = new kendo.data.DataSource({
                pageSize: config.gridPageSizes[0],
                page: 1,
                transport: {
                    read: function (e) {
                        e.data = util.createFilterablePageRequest(requestFormTestDataSource);
                        //here we add the quick search filters to the grid filters
                        if (quickSearchFilters.length > 0) {
                            for (var i = 0; i < quickSearchFilters.length; i++) {
                                e.data.filters.push(quickSearchFilters[i]);
                            }
                        } else {
                            e.data.filters.push({
                                field: "isActive",
                                value: true,
                                operator: "eq"
                            });
                        }

                        if (sectionFilter !== null) {
                            e.data.filters.push(sectionFilter);
                        }
                        testDefinitionManagementService.getTestDefinitionPage(e.data)
                            .then(function (response) {
                                e.success(response.data);
                                if (quickSearchFilters.length > 0 && quickSearchFilters[0].field === "rid") {
                                    var grid = $("#requestFormTestGrid").data("kendoGrid");
                                    grid.select("tr:eq(0)");
                                }
                            }).catch(function (error) {
                                e.error(error);
                            });
                    }
                },
                serverPaging: true,
                serverFiltering: true,
                schema: {
                    total: "totalElements",
                    data: "content",
                    model: {
                        id: "rid",
                        fields: {
                            id: { type: "number" },
                            description: { type: "string" },
                            standardCode: { type: "string" }
                        }
                    }
                }
            });

            $scope.requestFormTestOptions = {
                autoBind: true,
                dataSource: requestFormTestDataSource,
                selectable: false,
                columns: [
                    {
                        selectable: true,
                        width: "50px"
                    },
                    {
                        field: "standardCode",
                        title: "{{ 'standardCode' | translate }}"
                    },
                    {
                        field: "description",
                        title: "{{ 'description' | translate}}"
                    }
                ],
                dataBound: function (e) {
                    util.gridSelectionDataBound(e.sender, $scope.requestFormTestsChipsOptions.data, $scope.runOnChangeRequestFormTests);
                },
                change: function (e) {
                    util.gridSelectionChange(e.sender, $scope.requestFormTestsChipsOptions.data, $scope.runOnChangeRequestFormTests);
                }
            };

            $scope.requestFormTestsChipsOptions = {
                data: [],
                label: "standardCode",
                tooltip: "description",
                onRemove: removeChip
            };

            function removeChip(chip) {
                util.removeGridChip(chip, $("#requestFormTestGrid").data("kendoGrid"));
            };

            $scope.saveRequestFormTests = function () {
                $scope.selectedRequestForm.testRequestFormTestList = [];
                for (var i = 0; i < $scope.requestFormTestsChipsOptions.data.length; i++) {
                    $scope.selectedRequestForm.testRequestFormTestList.push({
                        testDefinition: $scope.requestFormTestsChipsOptions.data[i]
                    });
                }
                requestFormManagementService.saveRequestFormTests($scope.selectedRequestForm)
                    .then(function (response) {
                        util.createToast(util.systemMessages.success, "success");
                    });
            }

            testSelectionService.getAllSections()
                .then(function (response) {
                    $scope.sectionLov.data = response.data;
                });

            $scope.sectionLov = {
                className: "LabSection",
                name: "section",
                labelText: "section",
                valueField: ("name." + util.userLocale),
                selectedValue: null,
                required: false,
                data: []
            };

            var testAutocompleteCallback = function (filters) {
                quickSearchFilters = filters;
                requestFormTestDataSource.page(0);
            }

            $scope.testSearchOptions = {
                service: testDefinitionManagementService.getTestDefinitionLookup,
                callback: testAutocompleteCallback,
                skeleton: {
                    code: "standardCode",
                    description: "description"
                },
                filterList: ["description", "standardCode", "aliases", "secondaryCode"],
                staticFilters: [
                    {
                        field: "isActive",
                        value: true,
                        operator: "eq"
                    }
                ]
            }

            $scope.applySectionFilter = function () {
                if ($scope.sectionLov.selectedValue !== null && $scope.sectionLov.selectedValue !== undefined) {
                    sectionFilter = {
                        field: "section",
                        operator: "eq",
                        value: $scope.sectionLov.selectedValue.rid
                    };
                } else {
                    sectionFilter = null;
                }
                requestFormTestDataSource.page(0);
            }
            //#endregion
        }
    ]);
});
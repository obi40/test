define(['app', 'util', 'config'], function (app, util, config) {
    'use strict';
    app.directive('testComponentSetup', function () {
        return {
            restrict: 'E', // This means that it will be used as an element.
            replace: true,
            scope: {
                options: "=options"
                //{
                //  testDefinition: the test definition
                //}
            },
            templateUrl: "./" + config.lisDir + "/modules/shared/directives/testComponentSetup/test-component-view.html",
            controller: ['$scope', 'testDefinitionManagementService', 'lovService',
                function ($scope, testDefinitionManagementService, lovService) {

                    var testDefinition = $scope.options.testDefinition;
                    $scope.runOnChangeComponent = { value: true };
                    $scope.options.tempComponents = [];

                    var reflexTestEntryType = null;
                    var componentTestEntryType = null;

                    lovService.getLkpByClass({ className: "LkpTestEntryType" })
                        .then(function (response) {
                            for (var i = 0; i < response.length; i++) {
                                if (response[i].code === "component") {
                                    componentTestEntryType = response[i];
                                } else if (response[i].code === "reflexTest") {
                                    reflexTestEntryType = response[i];
                                }
                            }
                            for (var i = 0; i < testDefinition.extraTests.length; i++) {
                                var extraTest = testDefinition.extraTests[i].extraTest;
                                extraTest.alwaysPerformed = testDefinition.extraTests[i].alwaysPerformed;
                                if (testDefinition.extraTests[i].entryType.code === "component") {
                                    extraTest.entryType = componentTestEntryType;
                                    $scope.options.tempComponents.push(extraTest);
                                }
                            }
                            componentDataSource.read();
                        });

                    var componentQuickSearchFilters = [];

                    var componentDataSource = new kendo.data.DataSource({
                        pageSize: config.gridPageSizes[0],
                        page: 1,
                        filter: { field: "rid", operator: "neq", value: testDefinition.rid },
                        transport: {
                            read: function (e) {
                                e.data = util.createFilterablePageRequest(componentDataSource);
                                for (var i = 0; i < componentQuickSearchFilters.length; i++) {
                                    e.data.filters.push(componentQuickSearchFilters[i]);
                                }
                                testDefinitionManagementService.getTestDefinitionPage(e.data)
                                    .then(function successCallback(response) {
                                        e.success(response.data);
                                        if (componentQuickSearchFilters.length > 0 && componentQuickSearchFilters[0].field === "rid") {
                                            var grid = $("#componentGrid").data("kendoGrid");
                                            grid.select("tr:eq(0)");
                                        }
                                    }, function errorCallback(response) {
                                        e.error(response);
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
                                    rid: { type: "number" },
                                    description: { type: "string" },
                                    standardCode: { type: "string" },
                                    alwaysPerformed: { type: "boolean" }
                                }
                            },
                            parse: function (response) {
                                for (var i = 0; i < response.content.length; i++) {
                                    response.content[i].alwaysPerformed = true;
                                    response.content[i].entryType = componentTestEntryType;
                                }
                                return response;
                            }
                        }
                    });

                    function componentAutocompleteCallback(filters) {
                        componentQuickSearchFilters = filters;
                        componentDataSource.page(0);
                    }

                    $scope.componentSearchOptions = {
                        service: testDefinitionManagementService.getTestDefinitionLookup,
                        callback: componentAutocompleteCallback,
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
                    };

                    $scope.componentGridOptions = {
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
                                title: "{{ 'description' | translate }}"
                            }
                        ],
                        autoBind: false,
                        selectable: false,
                        dataSource: componentDataSource,
                        dataBound: function (e) {
                            util.gridSelectionDataBound(e.sender, $scope.options.tempComponents, $scope.runOnChangeComponent);
                        },
                        change: function (e) {
                            util.gridSelectionChange(e.sender, $scope.options.tempComponents, $scope.runOnChangeComponent);
                        }
                    }

                    $scope.removeChip = function (chip, gridId) {
                        util.removeGridChip(chip, $("#" + gridId).data("kendoGrid"));
                    }
                }]
        }
    });
});
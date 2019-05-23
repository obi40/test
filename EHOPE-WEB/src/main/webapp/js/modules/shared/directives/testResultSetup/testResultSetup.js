define(['app', 'util', 'config'], function (app, util, config) {
    'use strict';
    app.directive('testResultSetup', function () {
        return {
            restrict: 'E', // This means that it will be used as an element.
            replace: true,
            scope: {
                options: "=options"
                //{
                //  mode: "add"/"edit",
                //  testDefinition: the test definition to get results for
                //  form: the form object to evaluate
                //  results: the results array to fill (can be used to return data),
                //  changeSection: funtion to be called when section changes
                //}
            },
            templateUrl: "./" + config.lisDir + "/modules/shared/directives/testResultSetup/test-result-setup-view.html",
            controller: ['$scope', '$filter', '$timeout', 'testResultSetupService', 'labUnitService', 'lovService', '$mdDialog',
                function ($scope, $filter, $timeout, testResultSetupService, labUnitService, lovService, $mdDialog) {

                    $scope.resultValueTypes = [];
                    var resultValueTypesData = [];
                    var resultValueTypesForMicro = [];
                    var resultValueTypesForAllergy = [];
                    var resultValueTypesToUse = resultValueTypesData;

                    $scope.comprehensiveResultOptions = [];
                    var comprehensiveResults = [];

                    $scope.primaryUnitOptions = [];
                    $scope.secondaryUnitOptions = [];
                    var unitOptionsData = [];
                    $scope.primaryUnitTypeOptions = [];
                    var primaryUnitTypeOptionsData = [];

                    $scope.totalVisibleResults = 0;

                    $scope.headerClick = function (resultIndex) {
                        if ($scope.selectedResultIndex === resultIndex) {
                            $scope.selectedResultIndex = null;
                        } else {
                            $scope.selectedResultIndex = resultIndex;
                        }
                    };

                    $("#masterResultForm").sortable({
                        items: "> .test-result-item",
                        cursor: "move",
                        handle: ".result-drag-handle",
                        axis: "y",
                        beforeStop: function (event, ui) {
                            var printOrder = 1;
                            $(ui.item[0]).parent().children('.test-result-item').each(function (index, element) {
                                var itemIndex = $(this).attr("data-index");
                                if (itemIndex) {
                                    $scope.options.results[itemIndex].printOrder = printOrder++;
                                }
                            });
                        }
                    });

                    $scope.options.changeSection = function (section) {
                        for (var j = 0; j < $scope.resultValueTypes.length; j++) {
                            if (section.type && section.type.code === "MICROBIOLOGY") {
                                resultValueTypesToUse = resultValueTypesForMicro;
                            } else if (section.type && section.type.code === "ALLERGY") {
                                resultValueTypesToUse = resultValueTypesForAllergy;
                            } else {
                                resultValueTypesToUse = resultValueTypesData;
                            }
                            if ($scope.resultValueTypes[j].selectedValue) {
                                if (($scope.resultValueTypes[j].selectedValue.code === "ORG" && (!section.type || section.type.code !== "MICROBIOLOGY")) ||
                                    ($scope.resultValueTypes[j].selectedValue.code === "QN_QL" && (!section.type || section.type.code !== "ALLERGY"))) {
                                    $scope.resultValueTypes[j].selectedValue = null;
                                    $scope.resultValueTypes[j].searchValue = null;
                                }
                            }
                            $scope.resultValueTypes[j].updateData(resultValueTypesToUse);
                        }
                    }

                    $scope.isComprehensiveChange = function (result) {
                        var idx = -1;
                        for (var i = 0; i < comprehensiveResults.length; i++) {
                            var comprehensiveResult = comprehensiveResults[i];
                            if (comprehensiveResult.rid) {
                                if (result.rid === comprehensiveResult.rid) {
                                    idx = i;
                                    break;
                                }
                            } else if (result.$$hashKey === comprehensiveResult.$$hashKey) {
                                idx = i;
                                break;
                            }
                        }

                        // var idx = comprehensiveResults.map(function (t) {
                        //     return t.$$hashKey;
                        // }).indexOf(result.$$hashKey);

                        if (idx < 0) {
                            comprehensiveResults.push(result);
                        } else {
                            comprehensiveResults.splice(idx, 1);
                        }
                        for (var i = 0; i < $scope.comprehensiveResultOptions.length; i++) {
                            if (typeof $scope.comprehensiveResultOptions[i].updateData === "function") {
                                $scope.comprehensiveResultOptions[i].updateData(comprehensiveResults);
                            }
                        }
                    }

                    $scope.addResult = function () {
                        $scope.options.results.push({
                            resultValueType: null,
                            testCodedResultMappings: [],
                            normalRanges: [],
                            narrativeTemplates: [],
                            primaryUnitType: null,
                            printOrder: $scope.options.results.length + 1,
                            isRequired: true,
                            isComprehensive: false,
                            isDifferential: false
                        });
                        addResultInfo($scope.options.results.length - 1);
                        $scope.headerClick($scope.options.results.length - 1);
                    };

                    if ($scope.options.mode === "edit") {
                        $scope.options.testDefinition.testResults.sort(comparePrintOrder);
                        $scope.options.results = angular.copy($scope.options.testDefinition.testResults);
                        delete $scope.options.testDefinition.testResults;
                        for (var i = 0; i < $scope.options.results.length; i++) {
                            addResultInfo(i);
                        }
                        fetchLkpData(false);
                    } else {
                        $scope.options.results = [];
                        fetchLkpData(true);
                    }

                    function isBaseResultType(type) {
                        switch (type.code) {
                            case "QN":
                            case "QN_SC":
                            case "NAR":
                            case "CE":
                            case "RATIO":
                                return true;
                        }
                        return false;
                    }

                    function isForMicroResultType(type) {
                        switch (type.code) {
                            case "ORG":
                                return true;
                        }
                        return isBaseResultType(type);
                    }

                    function isForAllergyResultType(type) {
                        switch (type.code) {
                            case "QN_QL":
                                return true;
                        }
                    }

                    function fetchLkpData(autoAddResult) {
                        lovService.getLkpByClass({ className: "LkpResultValueType" })
                            .then(function (data) {
                                var tempBaseData = data.filter(isBaseResultType);
                                for (var i = 0; i < tempBaseData.length; i++) {
                                    resultValueTypesData.push(tempBaseData[i]);
                                }
                                var tempMicroData = data.filter(isForMicroResultType);
                                for (var i = 0; i < tempMicroData.length; i++) {
                                    resultValueTypesForMicro.push(tempMicroData[i]);
                                }
                                var tempAllergyData = data.filter(isForAllergyResultType);
                                for (var i = 0; i < tempAllergyData.length; i++) {
                                    resultValueTypesForAllergy.push(tempAllergyData[i]);
                                }

                                if ($scope.options.testDefinition.section && $scope.options.testDefinition.section.type) {
                                    switch ($scope.options.testDefinition.section.type.code) {
                                        case "MICROBIOLOGY":
                                            resultValueTypesToUse = resultValueTypesForMicro;
                                            break;
                                        case "ALLERGY":
                                            resultValueTypesToUse = resultValueTypesForAllergy;
                                            break;
                                    }
                                } else {
                                    resultValueTypesToUse = resultValueTypesData;
                                }
                                for (var j = 0; j < $scope.resultValueTypes.length; j++) {
                                    $scope.resultValueTypes[j].updateData(resultValueTypesToUse);
                                }
                                if (autoAddResult) {
                                    $scope.addResult();
                                }
                            });

                        lovService.getLkpByClass({ className: "LkpUnitType" })
                            .then(function (data) {
                                primaryUnitTypeOptionsData = data;
                                for (var j = 0; j < $scope.primaryUnitTypeOptions.length; j++) {
                                    $scope.primaryUnitTypeOptions[j].updateData(primaryUnitTypeOptionsData);
                                }
                            });

                        labUnitService.getLabUnitList()
                            .then(function (response) {
                                unitOptionsData = response.data;
                                for (var j = 0; j < $scope.primaryUnitOptions.length; j++) {
                                    $scope.primaryUnitOptions[j].updateData(unitOptionsData);
                                    $scope.secondaryUnitOptions[j].updateData(unitOptionsData);
                                }
                            });
                    }

                    function comparePrintOrder(a, b) {
                        return a.printOrder === b.printOrder ? 0 : a.printOrder - b.printOrder;
                    }

                    function addResultInfo(i) {
                        if ($scope.options.results[i].isComprehensive) {
                            comprehensiveResults.push($scope.options.results[i]);
                        }
                        $scope.options.results[i].normalRanges.sort(comparePrintOrder);
                        $scope.options.results[i].normalRangeTabs = { "Default": { title: "Default", testDestination: null, normalRanges: [] } };
                        for (var k = 0; k < $scope.options.testDefinition.destinations.length; k++) {
                            var destination = $scope.options.testDefinition.destinations[k];
                            var typeCode = destination.type.code;
                            var tabKey = typeCode + "|";
                            if (typeCode === "WORKBENCH") {
                                tabKey += destination.workbench.name[util.userLocale];
                            } else {
                                tabKey += destination.destinationBranch.code;
                            }
                            $scope.options.results[i].normalRangeTabs[tabKey] = { title: tabKey, testDestination: destination, normalRanges: [] };
                        }
                        for (var j = 0; j < $scope.options.results[i].normalRanges.length; j++) {
                            var normalRange = $scope.options.results[i].normalRanges[j];
                            var tabKey = "Default";
                            if (normalRange.testDestination) {
                                var destination = normalRange.testDestination;
                                var typeCode = destination.type.code;
                                var tabKey = typeCode + "|";
                                if (typeCode === "WORKBENCH") {
                                    tabKey += destination.workbench.name[util.userLocale];
                                } else {
                                    tabKey += destination.destinationBranch.code;
                                }
                            }
                            $scope.options.results[i].normalRangeTabs[tabKey].normalRanges.push(normalRange);
                        }

                        $scope.totalVisibleResults++;
                        $scope.options.results[i].testDefinition = $scope.options.testDefinition;
                        $scope.options.results[i].testCodedResultList = [];
                        for (var k = 0; k < $scope.options.results[i].testCodedResultMappings.length; k++) {
                            $scope.options.results[i].testCodedResultList.push($scope.options.results[i].testCodedResultMappings[k].testCodedResult);
                        }
                        $scope.options.results[i].narrativeTemplateList = angular.copy($scope.options.results[i].narrativeTemplates);

                        $scope.resultValueTypes.push({
                            className: "LkpResultValueType",
                            name: "resultValueType",
                            labelText: "resultValueType",
                            valueField: "name." + util.userLocale,
                            required: true,
                            selectedValue: $scope.options.results[i].resultValueType,
                            data: resultValueTypesToUse
                        });

                        $scope.comprehensiveResultOptions.push({
                            className: "comprehensiveResult",
                            name: "comprehensiveResult",
                            labelText: "comprehensiveResult",
                            valueField: "standardCode",
                            required: true,
                            selectedValue: $scope.options.results[i].comprehensiveResult,
                            data: comprehensiveResults
                        });

                        var primaryUnitLabel = "convUnit";
                        var secondaryUnitLabel = "siUnit";
                        if ($scope.options.results[i].primaryUnitType !== null && $scope.options.results[i].primaryUnitType.code === 'SI') {
                            primaryUnitLabel = "siUnit";
                            secondaryUnitLabel = "convUnit";
                        }
                        $scope.primaryUnitOptions.push({
                            className: "LabUnit",
                            name: "primaryUnit",
                            labelText: primaryUnitLabel,
                            valueField: "unitOfMeasure",
                            required: true,
                            selectedValue: $scope.options.results[i].primaryUnit,
                            data: unitOptionsData
                        });
                        $scope.secondaryUnitOptions.push({
                            className: "LabUnit",
                            name: "secondaryUnit",
                            labelText: secondaryUnitLabel,
                            valueField: "unitOfMeasure",
                            required: true,
                            selectedValue: $scope.options.results[i].secondaryUnit,
                            data: unitOptionsData
                        });
                        $scope.primaryUnitTypeOptions.push({
                            className: "LkpUnitType",
                            name: "primaryUnitType",
                            labelText: "primaryUnitType",
                            valueField: "name." + util.userLocale,
                            required: true,
                            selectedValue: $scope.options.results[i].primaryUnitType,
                            data: primaryUnitTypeOptionsData
                        });
                        generateMetaData($scope.options.results[i], i);
                        $timeout(function () {
                            for (var tabKey in $scope.options.results[i].normalRangeTabs) {
                                if ($scope.options.results[i].normalRangeTabs.hasOwnProperty(tabKey)) {
                                    chipSorter(tabKey, i);
                                }
                            }
                        }, 0);
                    }

                    function chipSorter(tabKey, i) {
                        $(".lis-chip-container[result-index='" + i + "'][destination-key='" + tabKey + "']").sortable({
                            items: "> .lis-chip",
                            cursor: "move",
                            containment: ".lis-chip-container[result-index='" + i + "'][destination-key='" + tabKey + "']",
                            beforeStop: function (event, ui) {
                                var printOrder = 1;
                                $(ui.item[0]).parent().children('.lis-chip').each(function (chipIndex, element) {
                                    var itemIndex = $(this).attr("data-index");
                                    if (itemIndex) {
                                        $scope.options.results[i].normalRangeTabs[tabKey].normalRanges[itemIndex].printOrder = printOrder++;
                                    }
                                });
                            }
                        });
                    }

                    $scope.changeType = function (selectedType, params) {
                        var result = params[0];
                        var index = params[1];
                        result.resultValueType = selectedType;
                        generateMetaData(result, index);
                    }

                    $scope.changePrimaryUnit = function (selectedUnit, result) {
                        result.primaryUnit = selectedUnit;
                        for (var tabKey in result.normalRangeTabs) {
                            if (result.normalRangeTabs.hasOwnProperty(tabKey)) {
                                for (var i = 0; i < result.normalRangeTabs[tabKey].normalRanges.length; i++) {
                                    generateNormalRangeDescription(result, result.normalRangeTabs[tabKey].normalRanges[i]);
                                }
                            }
                        }
                    }

                    $scope.changeComprehensiveResult = function (selectedType, params) {
                        var result = params[0];
                        result.comprehensiveResult = selectedType;
                    }

                    $scope.changePrimaryUnitType = function (selectedType, params) {
                        var result = params[0];
                        result.primaryUnitType = selectedType;
                        if (selectedType) {
                            if (selectedType.code === 'SI') {
                                $scope.primaryUnitOptions[params[1]].labelText = util.systemMessages.siUnit;
                                $scope.secondaryUnitOptions[params[1]].labelText = util.systemMessages.convUnit;
                            } else {
                                $scope.primaryUnitOptions[params[1]].labelText = util.systemMessages.convUnit;
                                $scope.secondaryUnitOptions[params[1]].labelText = util.systemMessages.siUnit;
                            }
                        }
                    }

                    $scope.changeSecondaryUnit = function (selectedUnit, result) {
                        result.secondaryUnit = selectedUnit;
                    }

                    $scope.deleteResult = function (i) {
                        $scope.options.results[i].markedForDeletion = true;
                        $scope.totalVisibleResults--;
                    };

                    function generateMetaData(result, index) {
                        var metaData = {
                            standardCode: { notNull: true },
                            printOrder: { notNull: true },
                            primaryUnit: { notNull: false },
                            secondaryUnit: { notNull: false },
                            primaryDecimals: { notNull: false },
                            secondaryDecimals: { notNull: false },
                            factor: { notNull: false },
                            primaryUnitType: { notNull: false }
                        };
                        $scope.primaryUnitTypeOptions[index].required = false;
                        $scope.primaryUnitOptions[index].required = false;
                        $scope.secondaryUnitOptions[index].required = false;
                        if (result.resultValueType) {
                            switch (result.resultValueType.code) {
                                case "QN":
                                    metaData.primaryUnit = { notNull: true };
                                    metaData.primaryDecimals = { notNull: true };
                                    metaData.primaryUnitType = { notNull: true };
                                    $scope.primaryUnitTypeOptions[index].required = true;
                                    $scope.primaryUnitOptions[index].required = true;
                                    break;
                                case "QN_SC":
                                    metaData.primaryUnit = { notNull: true };
                                    metaData.secondaryUnit = { notNull: true };
                                    metaData.primaryDecimals = { notNull: true };
                                    metaData.secondaryDecimals = { notNull: true };
                                    metaData.factor = { notNull: true };
                                    metaData.primaryUnitType = { notNull: true };
                                    $scope.primaryUnitTypeOptions[index].required = true;
                                    $scope.primaryUnitOptions[index].required = true;
                                    $scope.secondaryUnitOptions[index].required = true;
                                    break;
                                default:
                                    break;
                            }
                        }
                        result.metaData = metaData;
                    }

                    $scope.$on('saveTestResults', function (event, data) {
                        $scope.saveResults();
                    });

                    $scope.saveResults = function () {
                        $scope.options.testDefinition.testResultList = angular.copy($scope.options.results);
                    };

                    $scope.checkCodedNormalRange = function (result, form) {
                        checkCodedNormalRange(result, form);
                    };

                    function checkCodedNormalRange(result, form) {
                        for (var tabKey in result.normalRangeTabs) {
                            if (result.normalRangeTabs.hasOwnProperty(tabKey)) {
                                for (var i = 0; i < result.normalRangeTabs[tabKey].normalRanges.length; i++) {
                                    var normalRange = result.normalRangeTabs[tabKey].normalRanges[i];
                                    var codedResult = normalRange.codedResult;
                                    //match using the rid because object equality is weird
                                    var idx = result.testCodedResultList.map(function (t) {
                                        return t.rid;
                                    }).indexOf(codedResult.rid);
                                    if (idx < 0) {
                                        form.normalRangeValidator.$setValidity("invalid", false); //invalid
                                        form.$$element.find("md-tab-item:contains('" + tabKey + "')").filter(function () {
                                            return $(this).text() === tabKey;
                                        }).addClass("red-text");
                                        break;
                                    } else {
                                        form.normalRangeValidator.$setValidity("invalid", true); //valid
                                        form.$$element.find("md-tab-item:contains('" + tabKey + "')").filter(function () {
                                            return $(this).text() === tabKey;
                                        }).removeClass("red-text");
                                    }
                                }
                            }
                        }
                    }

                    //#region narrative-templates

                    $scope.addNarrativeTemplate = function (result) {
                        result.narrativeTemplateList.push({
                            text: ""
                        });
                    }

                    $scope.deleteNarrativeTemplate = function (params) {
                        var result = params[0];
                        var index = params[1];
                        result.narrativeTemplateList[index].markedForDeletion = true;
                    }

                    //#endregion

                    //#region coded-result-dialog

                    $scope.openCodedResultsDialog = function (selectedResult, form) {
                        openCodedResultsDialog(selectedResult, form);
                    };

                    $scope.normalRangeValidationMessages = { invalid: $filter('translate')('normalRangeCodedResultValidation') };

                    function openCodedResultsDialog(selectedResult, form) {
                        $mdDialog.show({
                            controller: ["$scope", "$mdDialog", "result", "form",
                                function ($scope, $mdDialog, result, form) {

                                    $scope.result = result;
                                    var codedResultsDataSource = new kendo.data.DataSource({
                                        pageSize: config.gridPageSizes[0],
                                        page: 1,
                                        transport: {
                                            read: function (e) {
                                                e.data = util.createFilterablePageRequest(codedResultsDataSource);
                                                testResultSetupService.getTestCodedResults(e.data)
                                                    .then(function (response) {
                                                        e.success(response.data);
                                                    })
                                                    .catch(function (response) {
                                                        e.error(response);
                                                    });
                                            },
                                            create: function (e) {
                                                delete e.data.rid;
                                                delete e.data.version;
                                                testResultSetupService.addTestCodedResult(e.data)
                                                    .then(function (response) {
                                                        util.createToast(util.systemMessages.success, "success");
                                                        e.success({ content: response.data });
                                                    }).catch(function (error) {
                                                        e.error(error);
                                                    });
                                            },
                                            update: function (e) {
                                                testResultSetupService.editTestCodedResult(e.data)
                                                    .then(function (response) {
                                                        util.createToast(util.systemMessages.success, "success");
                                                        e.success({ content: response.data });
                                                    }).catch(function (error) {
                                                        e.error(error);
                                                    });
                                            },
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
                                                    version: { type: "number" },
                                                    code: { validation: { required: true } },
                                                    value: { validation: { required: true } }
                                                }
                                            }
                                        }
                                    });

                                    $scope.runOnChangeCodedResults = { value: true };

                                    $scope.codedResultsGridOptions = {
                                        columns: [{
                                            selectable: true, width: "50px"
                                        }, {
                                            field: "code",
                                            title: util.systemMessages.code
                                        }, {
                                            field: "value",
                                            title: util.systemMessages.value
                                        },
                                        {
                                            command: [{
                                                name: "edit",
                                                text: { edit: "", update: "", cancel: "" },
                                                className: "md-button md-fab tiny-button",
                                                iconClass: {
                                                    edit: "fas fa-edit",
                                                    update: "fas fa-save",
                                                    cancel: "fas fa-undo"
                                                }
                                            }],
                                            title: "&nbsp;",
                                            width: "110px"
                                        }],
                                        edit: function (e) {
                                            var container = $(e.container);
                                            var checkbox = container.find(">td>input[type='checkbox']");
                                            checkbox.attr("disabled", true);
                                            var saveButton = container.find("a.k-button.k-grid-update");
                                            var cancelButton = container.find("a.k-button.k-grid-cancel");
                                            saveButton.addClass("md-button md-fab tiny-button");
                                            cancelButton.addClass("md-button md-fab tiny-button");
                                            saveButton.prop("title", $filter('translate')('save'));
                                            cancelButton.prop("title", $filter('translate')('cancel'));
                                        },
                                        editable: "inline",
                                        selectable: false,
                                        dataSource: codedResultsDataSource,
                                        dataBound: function (e) {
                                            util.gridSelectionDataBound(e.sender, $scope.codedResultMappingOptions.data, $scope.runOnChangeCodedResults);
                                            $("#codedResultsGrid .k-grid-edit").prop("title", $filter('translate')('edit'));
                                        },
                                        change: function (e) {
                                            util.gridSelectionChange(e.sender, $scope.codedResultMappingOptions.data, $scope.runOnChangeCodedResults);
                                            checkCodedNormalRange($scope.result, form);
                                        }
                                    };

                                    $scope.codedResultMappingOptions = {
                                        data: $scope.result.testCodedResultList,
                                        label: "code",
                                        tooltip: "value",
                                        onRemove: removeChip
                                    };

                                    function removeChip(chip) {
                                        util.removeGridChip(chip, $("#codedResultsGrid").data("kendoGrid"));
                                    };

                                    $scope.saveCodedResultMappings = function () {
                                        var mappings = [];
                                        for (var i = 0; i < $scope.codedResultMappingOptions.data.length; i++) {
                                            var copiedResult = angular.copy($scope.result);
                                            delete copiedResult.testDefinition;
                                            mappings.push({
                                                testResult: copiedResult,
                                                testCodedResult: $scope.codedResultMappingOptions.data[i]
                                            });
                                        }
                                        copiedResult.testCodedResultMappingList = mappings;
                                    }

                                    $scope.cancel = function () {
                                        $mdDialog.cancel();
                                    };

                                    $scope.addCodedResult = function () {
                                        var grid = $("#codedResultsGrid").data("kendoGrid");
                                        grid.addRow();
                                    };
                                }],
                            locals: {
                                result: selectedResult,
                                form: form
                            },
                            templateUrl: './' + config.lisDir + '/modules/dialogs/coded-results.html',
                            parent: angular.element(document.body),
                            clickOutsideToClose: false,
                            fullscreen: false
                        });
                    }

                    //#endregion

                    //#region normalRanges

                    function generateNormalRangeDescription(result, normalRange) {
                        var descArray = [];
                        if (normalRange.sex) {
                            descArray.push(normalRange.sex.code);
                        }
                        var ageFromComparator = "";
                        if (normalRange.ageFromComparator) {
                            ageFromComparator = normalRange.ageFromComparator;
                        }
                        var ageToComparator = "";
                        if (normalRange.ageToComparator) {
                            ageToComparator = normalRange.ageToComparator;
                        }

                        var minValueComparator = "";
                        if (normalRange.minValueComparator) {
                            minValueComparator = normalRange.minValueComparator;
                        }
                        var maxValueComparator = "";
                        if (normalRange.maxValueComparator) {
                            maxValueComparator = normalRange.maxValueComparator;
                        }

                        if (typeof normalRange.ageFrom === "number" && normalRange.ageFrom >= 0 &&
                            typeof normalRange.ageTo === "number" && normalRange.ageTo >= 0) {
                            if (normalRange.ageToUnit.code === normalRange.ageFromUnit.code) {
                                descArray.push(
                                    ageFromComparator + normalRange.ageFrom + "-" +
                                    ageToComparator + normalRange.ageTo + " " +
                                    normalRange.ageToUnit.code);
                            } else {
                                descArray.push(
                                    ageFromComparator + normalRange.ageFrom + " " + normalRange.ageFromUnit.code + "-" +
                                    ageToComparator + normalRange.ageTo + " " + normalRange.ageToUnit.code);
                            }
                        } else if (typeof normalRange.ageFrom === "number" && normalRange.ageFrom >= 0) {
                            descArray.push(ageFromComparator + normalRange.ageFrom + " " + normalRange.ageFromUnit.code);
                        } else if (typeof normalRange.ageTo === "number" && normalRange.ageTo >= 0) {
                            descArray.push(ageToComparator + normalRange.ageTo + " " + normalRange.ageToUnit.code);
                        }

                        if (normalRange.criterionName && normalRange.criterionValue) {
                            descArray.push(normalRange.criterionName + ": " + normalRange.criterionValue);
                        } else if (normalRange.criterionName) {
                            descArray.push(normalRange.criterionName);
                        } else if (normalRange.criterionValue) {
                            descArray.push(normalRange.criterionValue);
                        }

                        switch (result.resultValueType.code) {
                            case "CE":
                                descArray.push(normalRange.codedResult.code);
                                break;
                            case "QN":
                            case "QN_SC":
                                var valueWithUnit;
                                if (typeof normalRange.minValue === "number" && typeof normalRange.maxValue === "number") {
                                    valueWithUnit = minValueComparator + normalRange.minValue.toFixed(result.primaryDecimals) + "-" +
                                        maxValueComparator + normalRange.maxValue.toFixed(result.primaryDecimals);
                                } else if (typeof normalRange.minValue === "number") {
                                    valueWithUnit = minValueComparator + normalRange.minValue.toFixed(result.primaryDecimals);
                                } else if (typeof normalRange.maxValue === "number") {
                                    valueWithUnit = maxValueComparator + normalRange.maxValue.toFixed(result.primaryDecimals);
                                }
                                valueWithUnit += " " + result.primaryUnit.unitOfMeasure;
                                descArray.push(valueWithUnit);
                                break;
                            case "RATIO":
                                descArray.push(normalRange.ratio);
                                break;
                        }
                        normalRange.description = descArray.join("|");
                    }

                    $scope.deleteNormalRange = function (normalRange) {
                        normalRange.markedForDeletion = true;
                        normalRange.printOrder = -1;
                    };

                    $scope.openNormalRangesDialog = function (selectedResult, resultForm, resultIndex, tabKey) {
                        $mdDialog.show({
                            controller: ["$scope", "$mdDialog", '$filter', "result",
                                function ($scope, $mdDialog, $filter, result) {
                                    $scope.ageFromHint = "(eg. >=20d, >12w, 4m, 1y)";
                                    $scope.ageToHint = "(eg. <20d, <=12w, 4m, 1y)";
                                    $scope.resultType = result.resultValueType.code;
                                    $scope.normalRanges = [];
                                    $scope.ageFromPattern = /^(>|>=)?[1-9][0-9]*[dwmy]$/i;
                                    $scope.ageToPattern = /^(<|<=)?[1-9][0-9]*[dwmy]$/i;
                                    $scope.minValueHint = "(eg. >=25, >16, 9)";
                                    $scope.maxValueHint = "(eg. <25, <=16, 9, Up to 8)";
                                    $scope.minValuePattern = '^(>|>=)?[0-9]+([.](?=[0-9])[0-9]{0,' + result.primaryDecimals + '})?$';
                                    $scope.maxValuePattern = new RegExp('^(<|<=|up ?to ?)?[0-9]+([.](?=[0-9])[0-9]{0,' + result.primaryDecimals + '})?$', 'i');
                                    $scope.rerunAndPanicPattern = '^[0-9]+([.](?=[0-9])[0-9]{0,' + result.primaryDecimals + '})?$';
                                    $scope.ratioPattern = /^(<|>)?\s*[0-9]+\s*:\s*[0-9]+$/;
                                    $scope.ratioHint = "(eg. <1:250, >1:100, 1:6080)";
                                    $scope.genderOptions = [];
                                    $scope.genderValues = [];
                                    $scope.ageUnitValues = [];
                                    $scope.codedResultOptions = [];
                                    lovService.getLkpByClass({ className: "LkpGender" })
                                        .then(function (data) {
                                            $scope.genderValues = data;
                                            for (var j = 0; j < $scope.genderOptions.length; j++) {
                                                if ($scope.genderOptions[j].updateData) {
                                                    $scope.genderOptions[j].updateData($scope.genderValues);
                                                }
                                            }
                                        });
                                    lovService.getLkpByClass({ className: "LkpAgeUnit" })
                                        .then(function (data) {
                                            $scope.ageUnitValues = data;
                                            //sort the normal ranges first
                                            var ranges = result.normalRangeTabs[tabKey].normalRanges;
                                            ranges.sort(comparePrintOrder);

                                            //add the already existing normal ranges to the list
                                            for (var i = 0; i < ranges.length; i++) {
                                                $scope.normalRanges.push(angular.copy(ranges[i]));
                                                addNormalRangeInfo($scope.normalRanges.length - 1);
                                            }
                                        });
                                    $scope.addNormalRange = function () {
                                        $scope.normalRanges.push({
                                            sex: null,
                                            ageFrom: null,
                                            ageFromUnit: $scope.ageUnitValues[0],
                                            ageTo: null,
                                            ageToUnit: $scope.ageUnitValues[0],
                                            codedResult: null,
                                            isActive: true,
                                            printOrder: $scope.normalRanges.length + 1
                                        });
                                        addNormalRangeInfo($scope.normalRanges.length - 1);
                                    };
                                    function addNormalRangeInfo(i) {
                                        $scope.genderOptions.push({
                                            className: "LkpGender",
                                            name: "sex",
                                            labelText: $filter('translate')('sex'),
                                            valueField: "name." + util.userLocale,
                                            required: false,
                                            selectedValue: $scope.normalRanges[i].sex,
                                            data: $scope.genderValues,
                                            noneLabel: $filter('translate')('any')
                                        });
                                        $scope.normalRanges[i].stateChangeDateLabel = $scope.normalRanges[i].isActive ? util.systemMessages["activationDate"] : util.systemMessages["deactivationDate"];
                                        $scope.normalRanges[i].ageFromData = convertAgeData($scope.normalRanges[i].ageFromComparator, $scope.normalRanges[i].ageFrom, $scope.normalRanges[i].ageFromUnit);
                                        $scope.normalRanges[i].ageToData = convertAgeData($scope.normalRanges[i].ageToComparator, $scope.normalRanges[i].ageTo, $scope.normalRanges[i].ageToUnit);

                                        $scope.normalRanges[i].minValueData = convertNormalData($scope.normalRanges[i].minValueComparator, $scope.normalRanges[i].minValue, result.primaryDecimals);
                                        $scope.normalRanges[i].maxValueData = convertNormalData($scope.normalRanges[i].maxValueComparator, $scope.normalRanges[i].maxValue, result.primaryDecimals);

                                        $scope.codedResultOptions.push({
                                            name: "codedResult",
                                            labelText: $filter('translate')('codedResult'),
                                            valueField: "code",
                                            required: true,
                                            selectedValue: $scope.normalRanges[i].codedResult,
                                            data: result.testCodedResultList
                                        });
                                    }

                                    function convertAgeData(ageComparator, ageValue, ageUnit) {
                                        var ageData;
                                        if (ageValue && ageUnit) {
                                            ageData = ageValue + ageUnit.code.substr(0, 1);
                                            if (ageComparator) {
                                                ageData = ageComparator + ageData;
                                            }
                                        }
                                        return ageData;
                                    }
                                    function convertNormalData(normalComparator, normalValue, decimals) {
                                        var normalData;
                                        if (typeof normalValue === "number") {
                                            normalData = normalValue.toFixed(decimals);
                                            if (normalComparator) {
                                                normalData = normalComparator + normalData;
                                            }
                                        }
                                        return normalData;
                                    }

                                    $scope.deleteNormalRange = function (normalRange) {
                                        normalRange.markedForDeletion = true;
                                        normalRange.printOrder = -1;
                                    };

                                    $scope.ageMessages = { invalid: $filter('translate')('chronologicalAgeRange') };
                                    $scope.valueMessages = { invalid: $filter('translate')('minLessThanMax') };
                                    $scope.oneValueMessages = { invalid: $filter('translate')('minOrMaxNormalValueShouldBeFilled') };

                                    $scope.normalValueChange = function (index, form) {
                                        form.valueValidator.$setTouched();
                                        var normalRange = $scope.normalRanges[index];
                                        var minValueRegex = /^(>|>=)?((\d+)(\.?)(\d*))$/;
                                        var maxValueRegex = /^(<|<=|up\s?to\s?)?((\d+)(\.?)(\d*))$/i;
                                        var minValueData = normalRange.minValueData;
                                        var maxValueData = normalRange.maxValueData;

                                        //reset
                                        normalRange.minValueComparator = null;
                                        normalRange.minValue = null;
                                        normalRange.maxValueComparator = null;
                                        normalRange.maxValue = null;

                                        try {
                                            var foundMin = minValueData.toString().match(minValueRegex);
                                        } catch (e) { }
                                        try {
                                            var foundMax = maxValueData.toString().match(maxValueRegex);
                                        } catch (e) { }

                                        if (foundMin && foundMax) {
                                            var smallestAmount = result.primaryDecimals > 0 ? 1 : 0;
                                            for (var i = 0; i < result.primaryDecimals; i++) {
                                                smallestAmount /= 10;
                                            }
                                            var min = +foundMin[2];
                                            var max = +foundMax[2];
                                            if (foundMin[1] === '>') {
                                                min += smallestAmount;
                                            }
                                            if (foundMax[1] === '<') {
                                                max -= smallestAmount;
                                            }
                                            if (max < min) {
                                                form.valueValidator.$setValidity("invalid", false); //invalid
                                                return true;
                                            } else {
                                                form.valueValidator.$setValidity("invalid", true); //valid
                                                normalRange.minValueComparator = foundMin[1];
                                                normalRange.minValue = +foundMin[2];
                                                normalRange.maxValueComparator = foundMax[1];
                                                normalRange.maxValue = +foundMax[2];
                                            }
                                        } else if (foundMin) {
                                            form.valueValidator.$setValidity("invalid", true); //valid
                                            normalRange.minValueComparator = foundMin[1];
                                            normalRange.minValue = +foundMin[2];
                                        } else if (foundMax) {
                                            form.valueValidator.$setValidity("invalid", true); //valid
                                            normalRange.maxValueComparator = foundMax[1];
                                            normalRange.maxValue = +foundMax[2];
                                        }

                                        if (normalRange.minValueComparator === null &&
                                            normalRange.minValue === null &&
                                            normalRange.maxValueComparator === null &&
                                            normalRange.maxValue === null) {
                                            form.oneValueValidator.$setValidity("invalid", false);//invalid
                                            return true;
                                        } else {
                                            form.oneValueValidator.$setValidity("invalid", true);//valid
                                            return false;
                                        }
                                    };

                                    $scope.ageChange = function (index, form) {
                                        form.ageValidator.$setTouched();
                                        var fromRegex = /^(>|>=)?(\d+)([dwmy])$/i;
                                        var toRegex = /^(<|<=)?(\d+)([dwmy])$/i;
                                        var normalRange = $scope.normalRanges[index];
                                        var ageFromData = normalRange.ageFromData;
                                        var ageToData = normalRange.ageToData;

                                        try {
                                            var foundFrom = ageFromData.match(fromRegex);
                                        } catch (e) { }
                                        try {
                                            var foundTo = ageToData.match(toRegex);
                                        } catch (e) { }

                                        normalRange.ageFrom = null;
                                        normalRange.ageFromUnit = null;
                                        normalRange.ageTo = null;
                                        normalRange.ageToUnit = null;

                                        if (foundFrom && foundTo
                                            && convertAgeToMs(foundTo[1], foundTo[2], foundTo[3]) < convertAgeToMs(foundFrom[1], foundFrom[2], foundFrom[3])) {
                                            form.ageValidator.$setValidity("invalid", false); //invalid
                                        } else {
                                            form.ageValidator.$setValidity("invalid", true); //valid
                                            if (foundFrom) {
                                                normalRange.ageFromComparator = foundFrom[1];
                                                normalRange.ageFrom = +foundFrom[2];
                                                normalRange.ageFromUnit = findAgeUnit(foundFrom[3]);
                                            }
                                            if (foundTo) {
                                                normalRange.ageToComparator = foundTo[1];
                                                normalRange.ageTo = +foundTo[2];
                                                normalRange.ageToUnit = findAgeUnit(foundTo[3]);
                                            }
                                        }
                                    };

                                    function findAgeUnit(unitCode) {
                                        for (var i = 0; i < $scope.ageUnitValues.length; i++) {
                                            if ($scope.ageUnitValues[i].code.startsWith(unitCode.toLowerCase())) {
                                                return $scope.ageUnitValues[i];
                                            }
                                        }
                                    }

                                    var DAY_MS = 86400000;
                                    var WEEK_MS = 604800000;
                                    var MONTH_MS = 2592000000;
                                    var YEAR_MS = 31536000000;
                                    function convertAgeToMs(comparator, age, unitCode) {
                                        var value;
                                        unitCode = unitCode.toLowerCase();
                                        switch (unitCode) {
                                            case 'd':
                                                value = age * DAY_MS;
                                                break;
                                            case 'w':
                                                value = age * WEEK_MS;
                                                break;
                                            case 'm':
                                                value = age * MONTH_MS;
                                                break;
                                            case 'y':
                                                value = age * YEAR_MS;
                                                break;
                                        }
                                        switch (comparator) {
                                            case '<':
                                                value--;
                                                break;
                                            case '>':
                                                value++;
                                                break;
                                        }
                                        return value;
                                    }

                                    $scope.submit = function () {
                                        var allValid = true;
                                        for (var i = 0; i < $scope.normalRanges.length; i++) {
                                            var normalRange = $scope.normalRanges[i];
                                            if (!normalRange.markedForDeletion && (result.resultValueType.code === 'QN' || result.resultValueType.code === 'QN_SC')) {
                                                if ($scope.normalValueChange(i, $scope.normalRanges[i].form)) {
                                                    allValid = false;
                                                }
                                            }
                                            $scope.normalRanges[i].sex = $scope.genderOptions[i].selectedValue;
                                            $scope.normalRanges[i].codedResult = $scope.codedResultOptions[i].selectedValue;
                                            generateNormalRangeDescription(result, $scope.normalRanges[i]);
                                        }
                                        if (!allValid) {
                                            return;
                                        }
                                        $scope.normalRanges.sort(comparePrintOrder);
                                        result.normalRangeTabs[tabKey].normalRanges = angular.copy($scope.normalRanges);
                                        if (result.resultValueType.code === "CE") {
                                            checkCodedNormalRange(result, resultForm);
                                        }
                                        chipSorter(tabKey, resultIndex);
                                        $mdDialog.cancel();
                                    };
                                    $scope.cancel = function () {
                                        $mdDialog.cancel();
                                    };
                                }],
                            locals: {
                                result: selectedResult,
                                resultForm: resultForm
                            },
                            onComplete: function (scope, element) {
                                $(".normal-ranges-container").sortable({
                                    items: "> div.normal-range-item",
                                    cursor: "move",
                                    handle: ".drag-handle",
                                    axis: "y",
                                    beforeStop: function (event, ui) {
                                        var printOrder = 1;
                                        $(ui.item[0]).parent().children('div.normal-range-item').each(function (index, element) {
                                            var itemIndex = $(this).attr("data-index");
                                            if (itemIndex) {
                                                scope.normalRanges[itemIndex].printOrder = printOrder++;
                                            }
                                        });
                                    }
                                });
                            },
                            templateUrl: './' + config.lisDir + '/modules/dialogs/normal-ranges.html',
                            parent: angular.element(document.body),
                            clickOutsideToClose: false,
                            fullscreen: false
                        });
                    }

                    //#endregion

                }]
        }
    });
});
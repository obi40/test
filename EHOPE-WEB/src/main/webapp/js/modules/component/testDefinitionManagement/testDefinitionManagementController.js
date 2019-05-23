define(['app', 'config', 'util'], function (app, config, util) {
	'use strict';
	app.controller('testDefinitionManagementCtrl', [
		'$scope', '$timeout', 'testDefinitionManagementService', 'labUnitService', 'testSelectionService',
		'$filter', 'commonMethods', 'lovService', 'WizardHandler',
		function ($scope, $timeout, testDefinitionManagementService, labUnitService, testSelectionService,
			$filter, commonMethods, lovService, WizardHandler) {

			//#region quickAddEdit

			$scope.quickAdd = function () {
				quickFunction('add');
			};

			$scope.quickEdit = function () {
				quickFunction('edit');
			};

			$scope.$on("quickTestDefinitionSuccess", function () {
				$scope.setView("viewAll");
				testDefinitionDataSource.read();
			});

			function quickFunction(mode) {
				$scope.quickDefinitionOptions = {
					mode: mode
				};
				if (mode === "edit") {
					var selectedRows = $scope.testDefinitionGrid.select();
					var testRid = $scope.testDefinitionGrid.dataItem(selectedRows[0]).rid;
					testDefinitionManagementService.isTestDefinitionEditable(testRid).then(function (response) {
						switch (response.data) {
							case "EDITABLE":
								testDefinitionManagementService.getQuickTestDefinition(testRid)
									.then(function (response) {
										$scope.quickDefinitionOptions.testDefinition = response.data;
										$scope.setView("quickEdit");
									});
								break;
							case "INACTIVE":
								util.createToast($filter('translate')('inactiveTestUneditable'), "warning", 7000);
								break;
							case "USED":
								util.createToast($filter('translate')('usedTestUneditable'), "warning", 7000);
								break;
						}
					});
				} else if (mode === "add") {
					$scope.setView("quickAdd");
				}
			}

			//#endregion

			//#region autocomplete

			function autocompleteCallback(filters) {
				quickSearchFilters = filters;
				testDefinitionDataSource.page(0);
			}

			$scope.testDefinitionSearchOptions = {
				service: testDefinitionManagementService.getTestDefinitionLookup,
				callback: autocompleteCallback,
				skeleton: {
					code: "standardCode",
					description: "description"
				},
				filterList: ["description", "standardCode", "aliases", "secondaryCode"],
				staticFilters: [
					{
						field: "isActive",
						value: null,
						operator: "eq"
					}
				]
			};

			//#endregion

			$scope.testDefinitionOverviewForm = {};
			$scope.testDefinitionComponentForm = {};
			$scope.testDefinitionReflexTestForm = {};
			$scope.testDefinitionSpecimenForm = {};
			$scope.testDefinitionSpecimenInfoForm = {};
			$scope.testDefinitionClinicalAndInterpretiveForm = {};
			$scope.testDefinitionPerformanceForm = {};
			$scope.testDefinitionFeesAndCodingForm = {};
			$scope.testDefinitionOtherForm = {};
			$scope.testDefinitionQuestionForm = {};
			$scope.testDefinitionResultForm = {};
			var quickSearchFilters = []; //this list is for the global search
			$scope.lovs = [];

			var reflexTestEntryType = null;
			var componentTestEntryType = null;

			lovService.getLkpByClass({ className: "LkpTestEntryType" }).then(function (response) {
				for (var i = 0; i < response.length; i++) {
					if (response[i].code === "component") {
						componentTestEntryType = response[i];
					} else if (response[i].code === "reflexTest") {
						reflexTestEntryType = response[i];
					}
				}
			});

			resetSelectedTestDefinition();

			function resetSelectedTestDefinition() {
				$scope.selectedTestDefinition = {
					extraTests: [],
					testQuestions: [],
					testResults: [],
					interpretations: [],
					lkpContainerType: null,
					lkpTestingMethod: null,
					lkpReportType: null,
					section: null,
					specimen: null,
					loincAttributes: null,
					isPanel: false,
					isActive: true,
					isSeparatePage: false
				};
				$scope.tempReflexTests = [];
				$scope.tempComponents = [];
			}

			$scope.clearForm = function () {
				// WizardHandler.wizard().reset();
				for (var i = $scope.tempReflexTests.length - 1; i >= 0; i--) {
					util.removeGridChip($scope.tempReflexTests[i], $("#reflexTestGrid").data("kendoGrid"));
				}
				for (var i = $scope.tempComponents.length - 1; i >= 0; i--) {
					util.removeGridChip($scope.tempComponents[i], $("#componentGrid").data("kendoGrid"));
				}

				resetSelectedTestDefinition();

				for (var i = 0; i < $scope.lovs.length; i++) {
					$scope.lovs[i].selectedValue = null;
				}

				testQuestionDataSource.data($scope.selectedTestDefinition.testQuestions);
				testQuestionDataSource.sync();
			}

			$scope.viewSingleTestDefinitionTemplate = config.lisDir + "/modules/component/testDefinitionManagement/test-definition-single-view.html";
			$scope.editTestDefinitionTemplate = config.lisDir + "/modules/component/testDefinitionManagement/test-definition-single-edit.html";

			$scope.exitValidation = function () {
				var formName = WizardHandler.wizard().currentStepDescription();
				var currentStep = WizardHandler.wizard().currentStepNumber();
				for (var key in $scope.forms) {
					if ($scope.forms.hasOwnProperty(key)) {
						if (key === formName) {
							return $scope.forms[key].$valid;
						}
					}
				}
				return false;
			}

			$scope.forms = {};
			$scope.submitForm = function () {

				// testDefinitionOverviewForm
				// testDefinitionComponentForm
				// testDefinitionReflexTestForm
				// testDefinitionSpecimenForm
				// testDefinitionSpecimenInfoForm
				// testDefinitionClinicalAndInterpretiveForm
				// testDefinitionPerformanceForm
				// testDefinitionFeesAndCodingForm
				// testDefinitionOtherForm
				// testDefinitionQuestionForm
				// testDefinitionResultForm

				for (var key in $scope.forms) {
					if ($scope.forms.hasOwnProperty(key)) {
						if (!$scope.forms[key].$valid) {
							//if any form is invalid, the API cannot be called
							return;
						}
					}
				}

				$scope.$broadcast('saveTestResults');

				$scope.selectedTestDefinition.testQuestionList = testQuestionDataSource.data();
				for (var i = 0; i < $scope.selectedTestDefinition.testQuestionList.length; i++) {
					$scope.selectedTestDefinition.testQuestionList[i].testQuestionOptionList = $scope.selectedTestDefinition.testQuestionList[i].testQuestionOptions;
				}
				if ($scope.selectedTestDefinition.specimen !== null) {
					$scope.selectedTestDefinition.specimen.specimenType = $scope.lkpSpecimenType.selectedValue;
					$scope.selectedTestDefinition.specimen.specimenTemperature = $scope.lkpSpecimenTemperature.selectedValue;
					$scope.selectedTestDefinition.specimen.stabilityUnit = $scope.lkpSpecimenStabilityUnit.selectedValue;
				}

				$scope.selectedTestDefinition.lkpContainerType = $scope.lkpContainerType.selectedValue;
				$scope.selectedTestDefinition.lkpReportType = $scope.lkpReportType.selectedValue;
				$scope.selectedTestDefinition.lkpTestingMethod = $scope.lkpTestingMethod.selectedValue;
				$scope.selectedTestDefinition.section = $scope.section.selectedValue;
				$scope.selectedTestDefinition.loincAttributes = $scope.loincAttributes.selectedValue;

				$scope.selectedTestDefinition.extraTests = [];
				$scope.selectedTestDefinition.extraTestList = [];
				for (var i = 0; i < $scope.tempReflexTests.length; i++) {
					var reflexTest = {
						extraTest: $scope.tempReflexTests[i],
						entryType: reflexTestEntryType,
						alwaysPerformed: $scope.tempReflexTests[i].alwaysPerformed
					};
					$scope.selectedTestDefinition.extraTestList.push(reflexTest);
				}
				for (var i = 0; i < $scope.tempComponents.length; i++) {
					var component = {
						extraTest: $scope.tempComponents[i],
						entryType: componentTestEntryType,
						alwaysPerformed: $scope.tempComponents[i].alwaysPerformed
					};
					$scope.selectedTestDefinition.extraTestList.push(component);
				}

				if ($scope.viewMode === "add") {
					testDefinitionManagementService.addTestDefinition($scope.selectedTestDefinition)
						.then(function (e) {
							util.createToast(util.systemMessages.success, "success");
							$scope.viewMode = "viewAll";
						});
				} else {
					testDefinitionManagementService.editTestDefinition($scope.selectedTestDefinition)
						.then(function (e) {
							util.createToast(util.systemMessages.success, "success");
							$scope.viewMode = "viewAll";
						});
				}
			}

			//#region lovs

			$scope.lkpTestingMethod = {
				className: "LkpTestingMethod",
				name: "lkpTestingMethod",
				labelText: "testingMethod",
				valueField: "name." + util.userLocale,
				selectedValue: null
			};

			$scope.lkpContainerType = {
				className: "LkpContainerType",
				name: "lkpContainerType",
				labelText: "containerType",
				valueField: "name." + util.userLocale,
				selectedValue: null
			};

			$scope.lkpReportType = {
				className: "LkpReportType",
				name: "lkpReportType",
				labelText: "reportType",
				valueField: "name." + util.userLocale,
				selectedValue: null
			};

			$scope.section = {
				className: "LabSection",
				name: "labSection",
				labelText: "section",
				valueField: ("name." + util.userLocale),
				selectedValue: null,
				required: true,
				data: []
			};

			$scope.loincAttributes = {
				className: "LoincAttributes",
				name: "loincAttributes",
				labelText: "loincAttributes",
				valueField: "name." + util.userLocale,
				selectedValue: null
			};

			$scope.lkpSpecimenType = {
				className: "LkpSpecimenType",
				name: "lkpSpecimenType",
				labelText: "specimenType",
				valueField: "name." + util.userLocale,
				selectedValue: null
			};

			$scope.lkpSpecimenTemperature = {
				className: "LkpSpecimenTemperature",
				name: "lkpSpecimenTemperature",
				labelText: "specimenTemperature",
				valueField: "name." + util.userLocale,
				selectedValue: null
			};

			$scope.lkpSpecimenStabilityUnit = {
				className: "LkpSpecimenStabilityUnit",
				name: "lkpSpecimenStabilityUnit",
				labelText: "stabilityUnit",
				valueField: "name." + util.userLocale,
				selectedValue: null
			};

			//#endregion

			//#region metaData

			function retrieveMetaData() {
				if ($scope.testDefinitionMetaData === undefined) {
					commonMethods.retrieveMetaData("TestDefinition")
						.then(function successCallback(resp) {
							$scope.testDefinitionMetaData = resp.data;

							$scope.lkpTestingMethod.required = $scope.testDefinitionMetaData.lkpTestingMethod.notNull;

							$scope.lkpContainerType.required = $scope.testDefinitionMetaData.lkpContainerType.notNull;

							$scope.section.required = $scope.testDefinitionMetaData.section.notNull;

							$scope.loincAttributes.required = $scope.testDefinitionMetaData.loincAttributes.notNull;

							$scope.lkpReportType.required = $scope.testDefinitionMetaData.lkpReportType.notNull;

							// $scope.lkpSpecimenType.required = true;
							// $scope.lkpSpecimenTemperature.required = true;
							// $scope.lkpSpecimenStabilityUnit.required = true;

							$scope.lovs = [
								$scope.lkpTestingMethod,
								$scope.lkpContainerType,
								$scope.section,
								$scope.loincAttributes,
								$scope.lkpSpecimenType,
								$scope.lkpSpecimenTemperature,
								$scope.lkpSpecimenStabilityUnit,
								$scope.lkpReportType
							];
						});
				}
			}

			//#endregion

			$scope.viewMode = "viewAll"; //viewAll, viewSingle, edit, add

			$scope.setView = function (view) {
				$scope.viewMode = view;
				if ($scope.viewMode === "viewSingle" || $scope.viewMode === "edit") {
					componentQuickSearchFilters = [];
					reflexQuickSearchFilters = [];
					getTestDefinition();
				} else if ($scope.viewMode === "viewAll") {
					//WizardHandler.wizard().reset();
				} else if ($scope.viewMode === "add") {
					componentQuickSearchFilters = [];
					reflexQuickSearchFilters = [];
					reflexTestDataSource.read();
					componentDataSource.read();
				}
				if ($scope.viewMode === "edit" || $scope.viewMode === "add") {
					if ($scope.section.data.length === 0) {
						testSelectionService.getAllSections()
							.then(function (response) {
								$scope.section.data = response.data;
							});
					}
				}
			}

			function getTestDefinition() {
				$scope.questionOptionChipData = [];
				var selectedRows = $scope.testDefinitionGrid.select();
				$scope.selectedTestDefinition = $scope.testDefinitionGrid.dataItem(selectedRows[0]);
				testDefinitionManagementService.getTestDefinition({ rid: $scope.selectedTestDefinition.rid, mode: $scope.viewMode })
					.then(function (response) {
						resetSelectedTestDefinition();
						$scope.selectedTestDefinition = response.data;
						if ($scope.viewMode === "edit") {
							$scope.resultSetupOptions.mode = "edit";
							$scope.resultSetupOptions.testDefinition = $scope.selectedTestDefinition;
							$scope.questionSetupOptions.testDefinition = $scope.selectedTestDefinition;
							retrieveMetaData();
							for (var i = 0; i < $scope.selectedTestDefinition.extraTests.length; i++) {
								var extraTest = $scope.selectedTestDefinition.extraTests[i].extraTest;
								extraTest.alwaysPerformed = $scope.selectedTestDefinition.extraTests[i].alwaysPerformed;
								if ($scope.selectedTestDefinition.extraTests[i].entryType.code === reflexTestEntryType.code) {
									$scope.tempReflexTests.push(extraTest);
								} else {
									$scope.tempComponents.push(extraTest);
								}
							}

							$scope.lkpContainerType.selectedValue = $scope.selectedTestDefinition.lkpContainerType;
							$scope.lkpTestingMethod.selectedValue = $scope.selectedTestDefinition.lkpTestingMethod;
							$scope.section.selectedValue = $scope.selectedTestDefinition.section;
							$scope.loincAttributes.selectedValue = $scope.selectedTestDefinition.loincAttributes;

							$scope.lkpReportType.selectedValue = $scope.selectedTestDefinition.lkpReportType;

							if ($scope.selectedTestDefinition.specimen !== null) {
								$scope.lkpSpecimenType.selectedValue = $scope.selectedTestDefinition.specimen.specimenType;
								$scope.lkpSpecimenTemperature.selectedValue = $scope.selectedTestDefinition.specimen.specimenTemperature;
								$scope.lkpSpecimenStabilityUnit.selectedValue = $scope.selectedTestDefinition.specimen.stabilityUnit;
							}

							testQuestionDataSource.data($scope.selectedTestDefinition.testQuestions);
							testQuestionDataSource.sync();

							reflexTestDataSource.read();
							componentDataSource.read();
						}
					}).catch(function (response) {
						resetSelectedTestDefinition();
					});
			}

			$scope.activate = function () {
				var selectedRows = $scope.testDefinitionGrid.select();
				var dataItem = $scope.testDefinitionGrid.dataItem(selectedRows[0]);
				var testDefinitionFetch = {
					rid: dataItem.rid
				}
				return testDefinitionManagementService.activateTestDefinition(testDefinitionFetch)
					.then(function (response) {
						util.createToast(util.systemMessages.success, "success");
						testDefinitionDataSource.read();
					});
			}

			$scope.deactivate = function () {
				var selectedRows = $scope.testDefinitionGrid.select();
				var dataItem = $scope.testDefinitionGrid.dataItem(selectedRows[0]);
				var testDefinitionFetch = {
					rid: dataItem.rid
				}
				return testDefinitionManagementService.deactivateTestDefinition(testDefinitionFetch)
					.then(function (response) {
						util.createToast(util.systemMessages.success, "success");
						testDefinitionDataSource.read();
					});
			}

			$scope.edit = function () {
				var selectedRows = $scope.testDefinitionGrid.select();
				var testRid = $scope.testDefinitionGrid.dataItem(selectedRows[0]).rid;
				testDefinitionManagementService.isTestDefinitionEditable(testRid)
					.then(function (response) {
						switch (response.data) {
							case "EDITABLE":
								$scope.setView("edit");
								break;
							case "INACTIVE":
								util.createToast($filter('translate')('inactiveTestUneditable'), "warning", 7000);
								break;
							case "USED":
								util.createToast($filter('translate')('usedTestUneditable'), "warning", 7000);
								break;
						}
					});
			}

			$scope.add = function () {
				$scope.clearForm();
				retrieveMetaData();
				$scope.setView("add");
				$scope.resultSetupOptions.testDefinition = $scope.selectedTestDefinition;
				$scope.resultSetupOptions.mode = "add";
				$scope.questionSetupOptions.testDefinition = $scope.selectedTestDefinition;
			}

			$scope.filterItem = function (item) {
				var ignoreList = ["version", "createdBy", "creationDate", "tenantId", "rid", "auditable",
					"primaryLangDiscriminator", "secondaryLangDiscriminator", "updateDate",
					"updatedBy", "class_"];
				var result = {};
				for (var key in item) {
					var value = item[key];
					if (value == null || value == "null") {
						value = "";
					}
					if (item.hasOwnProperty(key)
						&& ignoreList.indexOf(key) < 0) {
						result[key] = value;
					}
				}
				return result;
			}

			$scope.locale = util.userLocale;

			//#region testDefinitionGrid

			var testDefinitionDataSource = new kendo.data.DataSource({
				pageSize: config.gridPageSizes[0],
				page: 1,
				transport: {
					read: function (e) {
						var filterMap = {
							"lkpContainerType": "lkpContainerType.rid",
							"lkpTestingMethod": "lkpTestingMethod.rid",
							"section": "section.rid"
						};
						e.data = util.createFilterablePageRequest($scope.testDefinitionGridOptions.dataSource, filterMap);
						//here we add the quick search filters to the grid filters
						for (var i = 0; i < quickSearchFilters.length; i++) {
							e.data.filters.push(quickSearchFilters[i]);
						}
						testDefinitionManagementService.getTestDefinitionPage(e.data)
							.then(function successCallback(response) {
								e.success(response.data);
								if (quickSearchFilters.length > 0 && quickSearchFilters[0].field === "rid") {
									var grid = $("#testDefinitionGrid").data("kendoGrid");
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
							version: { type: "number" },
							additionalTestingRequirements: { type: "string" },
							advisoryInformation: { type: "string" },
							aliases: { type: "string" },
							analyticTime: { type: "string" },
							lkpContainerType: { type: "lov" },
							cptCode: { type: "string" },
							cptUnits: { type: "string" },
							daysTimesPerformed: { type: "string" },
							description: { type: "string" },
							maximumLabTime: { type: "string" },
							standardCode: { type: "string" },
							secondaryCode: { type: "string" },
							lkpTestingMethod: { type: "lov" },
							necessaryInformation: { type: "string" },
							orderableSeparately: { type: "string" },
							isPanel: { type: "boolean" },
							isActive: { type: "boolean" },
							isSeparatePage: { type: "boolean" },
							normalRangeText: { type: "string" },
							reportingDescription: { type: "string" },
							shippingInstructions: { type: "string" },
							urinePreservativeCollectionOptions: { type: "string" },
							usefulFor: { type: "string" },
							testingAlgorithm: { type: "string" },
							clinicalInformation: { type: "string" },
							interpretation: { type: "string" },
							cautions: { type: "string" },
							clinicalReference: { type: "string" },
							specialInstructions: { type: "string" },
							supportiveData: { type: "string" },
							geneticsTestInformation: { type: "string" },
							loincCode: { type: "string" },
							rank: { type: "number" },
							testQuestions: { type: "object" },
							testResults: { type: "object" },
							normalRanges: { type: "object" },
							extraTests: { type: "object" },
							loincAttributes: { type: "string" },
							disclaimer: { type: "string" },
							lkpReportType: { type: "lov" },
							section: { type: "lov" }
						}
					}
				}
			});

			$scope.testDefinitionGridOptions = {
				columns: [
					{
						hidden: false,
						field: "standardCode",
						title: "{{ 'standardCode' | translate }}"
					},
					{
						hidden: false,
						field: "secondaryCode",
						title: "{{ 'secondaryCode' | translate }}"
					},
					{
						hidden: false,
						field: "description",
						title: "{{ 'description' | translate }}"
					},
					{
						hidden: true,
						field: "additionalTestingRequirements",
						title: "{{ 'additionalTestingRequirements' | translate }}"
					},
					{
						hidden: true,
						field: "advisoryInformation",
						title: "{{ 'advisoryInformation' | translate }}"
					},
					{
						hidden: true,
						field: "aliases",
						title: "{{ 'aliases' | translate }}"
					},
					{
						hidden: true,
						field: "analyticTime",
						title: "{{ 'analyticTime' | translate }}"
					},
					{
						hidden: true,
						field: "lkpContainerType",
						title: "{{ 'containerType' | translate }}",
						filterable: {
							ui: function (element) {
								util.createLovFilter(element, { className: "LkpContainerType" }, lovService.getLkpByClass);
							}
						}
					},
					{
						hidden: true,
						field: "lkpReportType",
						title: "{{ 'reportType' | translate }}",
						filterable: {
							ui: function (element) {
								util.createLovFilter(element, { className: "LkpReportType" }, lovService.getLkpByClass);
							}
						}
					},
					{
						hidden: true,
						field: "cptCode",
						title: "{{ 'cptCode' | translate }}"
					},
					{
						hidden: true,
						field: "cptUnits",
						title: "{{ 'cptUnits' | translate }}"
					},
					{
						hidden: true,
						field: "daysTimesPerformed",
						title: "{{ 'daysTimesPerformed' | translate }}"
					},
					{
						hidden: true,
						field: "maximumLabTime",
						title: "{{ 'maximumLabTime' | translate }}"
					},
					{
						hidden: false,
						field: "section",
						title: "{{ 'section' | translate }}",
						template: function (dataItem) {
							return dataItem.section.name[util.userLocale];
						},
						filterable: {
							ui: function (element) {
								util.createLovFilter(element, null, testSelectionService.getAllSections);
							}
						}
					},
					{
						hidden: true,
						field: "lkpTestingMethod",
						title: "{{ 'testingMethod' | translate }}",
						filterable: {
							ui: function (element) {
								util.createLovFilter(element, { className: "LkpTestingMethod" }, lovService.getLkpByClass);
							}
						}
					},
					{
						hidden: true,
						field: "necessaryInformation",
						title: "{{ 'necessaryInformation' | translate }}"
					},
					{
						hidden: true,
						field: "orderableSeparately",
						title: "{{ 'orderableSeparately' | translate }}"
					},
					{
						hidden: false,
						field: "isPanel",
						title: "{{ 'panel' | translate }}",
						template: "{{ #: isPanel # ? 'yes' : 'no' | translate }}"
					},
					{
						hidden: true,
						field: "normalRangeText",
						title: "{{ 'normalRangeText' | translate }}"
					},
					{
						hidden: true,
						field: "reportingDescription",
						title: "{{ 'reportingDescription' | translate }}"
					},
					{
						hidden: true,
						field: "shippingInstructions",
						title: "{{ 'shippingInstructions' | translate }}"
					},
					{
						hidden: true,
						field: "urinePreservativeCollectionOptions",
						title: "{{ 'urinePreservativeCollectionOptions' | translate }}"
					},
					{
						hidden: true,
						field: "usefulFor",
						title: "{{ 'usefulFor' | translate }}"
					},
					{
						hidden: true,
						field: "testingAlgorithm",
						title: "{{ 'testingAlgorithm' | translate }}"
					},
					{
						hidden: true,
						field: "clinicalInformation",
						title: "{{ 'clinicalInformation' | translate }}"
					},
					{
						hidden: true,
						field: "interpretation",
						title: "{{ 'interpretation' | translate }}"
					},
					{
						hidden: true,
						field: "cautions",
						title: "{{ 'cautions' | translate }}"
					},
					{
						hidden: true,
						field: "clinicalReference",
						title: "{{ 'clinicalReference' | translate }}"
					},
					{
						hidden: true,
						field: "specialInstructions",
						title: "{{ 'specialInstructions' | translate }}"
					},
					{
						hidden: true,
						field: "supportiveData",
						title: "{{ 'supportiveData' | translate }}"
					},
					{
						hidden: true,
						field: "geneticsTestInformation",
						title: "{{ 'geneticsTestInformation' | translate }}"
					},
					{
						hidden: false,
						field: "loincCode",
						title: "{{ 'loincCode' | translate }}"
					},
					{
						hidden: false,
						field: "rank",
						title: "{{ 'ranking' | translate }}"
					},
					{
						hidden: true,
						field: "testQuestions",
						title: "{{ 'questions' | translate }}"
					},
					{
						hidden: true,
						field: "testResults",
						title: "{{ 'results' | translate }}"
					},
					{
						hidden: true,
						field: "normalRanges",
						title: "{{ 'normalRanges' | translate }}"
					},
					{
						hidden: true,
						field: "extraTests",
						title: "{{ 'extraTests' | translate }}"
					},
					{
						hidden: true,
						field: "loincAttributes",
						title: "{{ 'loincAttributes' | translate }}"
					},
					{
						hidden: false,
						field: "isActive",
						title: "{{ 'active' | translate }}",
						template: "{{ #: isActive # ? 'yes' : 'no' | translate }}"
					},
					{
						hidden: true,
						field: "isSeparatePage",
						title: "{{ 'separatePage' | translate }}",
						template: "{{ #: isSeparatePage # ? 'yes' : 'no' | translate }}"
					},
					{
						hidden: true,
						field: "disclaimer",
						title: "{{ 'disclaimer' | translate }}"
					}
				],
				dataSource: testDefinitionDataSource,
				dataBinding: function () {
					$scope.selectedCount = 0;
				},
				change: function (e) {
					var selectedRows = this.select();
					if (selectedRows.length === 1) {
						$scope.selectedTestIsActive = this.dataItem(selectedRows[0]).isActive;
					}
					$scope.selectedCount = selectedRows.length;
				}
			};

			//#endregion

			//#region reflexTestGrid

			var reflexQuickSearchFilters = [];

			$scope.runOnChangeReflexTest = { value: true };

			var reflexTestDataSource = new kendo.data.DataSource({
				pageSize: config.gridPageSizes[0],
				page: 1,
				transport: {
					read: function (e) {
						e.data = util.createFilterablePageRequest($scope.reflexTestGridOptions.dataSource);
						var nonSelfFilter = {
							field: "rid",
							value: $scope.selectedTestDefinition.rid,
							operator: "neq"
						};
						var addNonSelfFilter = true;
						for (var i = 0; i < e.data.filters.length; i++) {
							var filter = e.data.filters[i];
							if (filter.field === nonSelfFilter.field
								&& filter.operator === nonSelfFilter.operator
								&& filter.value === nonSelfFilter.value) {
								addNonSelfFilter = false;
								break;
							}
						}
						if (addNonSelfFilter) {
							e.data.filters.push(nonSelfFilter);
						}
						for (var i = 0; i < reflexQuickSearchFilters.length; i++) {
							e.data.filters.push(reflexQuickSearchFilters[i]);
						}
						testDefinitionManagementService.getTestDefinitionPage(e.data)
							.then(function successCallback(response) {
								e.success(response.data);
								if (reflexQuickSearchFilters.length > 0 && reflexQuickSearchFilters[0].field === "rid") {
									var grid = $("#reflexTestGrid").data("kendoGrid");
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
							rid: { type: "number", editable: false },
							description: { type: "string", editable: false },
							standardCode: { type: "string", editable: false },
							isReflex: { type: "boolean" },
							alwaysPerformed: { type: "boolean" }
						}
					},
					parse: function (response) {
						for (var i = 0; i < response.content.length; i++) {
							response.content[i].isReflex = true;
							response.content[i].alwaysPerformed = false;
						}
						return response;
					}
				}
			});

			function reflexAutocompleteCallback(filters) {
				reflexQuickSearchFilters = filters;
				reflexTestDataSource.page(0);
			}

			$scope.reflexTestSearchOptions = {
				service: testDefinitionManagementService.getTestDefinitionLookup,
				callback: reflexAutocompleteCallback,
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

			$scope.reflexTestGridOptions = {
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
				selectable: false,
				dataSource: reflexTestDataSource,
				autoBind: false,
				dataBound: function (e) {
					util.gridSelectionDataBound(e.sender, $scope.tempReflexTests, $scope.runOnChangeReflexTest);
				},
				change: function (e) {
					util.gridSelectionChange(e.sender, $scope.tempReflexTests, $scope.runOnChangeReflexTest);
				}
			}

			//#endregion

			//#region componentGrid

			$scope.runOnChangeComponent = { value: true };

			var componentQuickSearchFilters = [];

			var componentDataSource = new kendo.data.DataSource({
				pageSize: config.gridPageSizes[0],
				page: 1,
				transport: {
					read: function (e) {
						e.data = util.createFilterablePageRequest($scope.componentGridOptions.dataSource);
						var nonSelfFilter = {
							field: "rid",
							value: $scope.selectedTestDefinition.rid,
							operator: "neq"
						};
						var addNonSelfFilter = true;
						for (var i = 0; i < e.data.filters.length; i++) {
							var filter = e.data.filters[i];
							if (filter.field === nonSelfFilter.field
								&& filter.operator === nonSelfFilter.operator
								&& filter.value === nonSelfFilter.value) {
								addNonSelfFilter = false;
								break;
							}
						}
						if (addNonSelfFilter) {
							e.data.filters.push(nonSelfFilter);
						}
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
							rid: { type: "number", editable: false },
							description: { type: "string", editable: false },
							standardCode: { type: "string", editable: false },
							isReflex: { type: "boolean" },
							alwaysPerformed: { type: "boolean" }
						}
					},
					parse: function (response) {
						for (var i = 0; i < response.content.length; i++) {
							response.content[i].isReflex = true;
							response.content[i].alwaysPerformed = false;
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
				selectable: false,
				dataSource: componentDataSource,
				autoBind: false,
				dataBound: function (e) {
					util.gridSelectionDataBound(e.sender, $scope.tempComponents, $scope.runOnChangeComponent);
				},
				change: function (e) {
					util.gridSelectionChange(e.sender, $scope.tempComponents, $scope.runOnChangeComponent);
				}
			}

			//#endregion

			//#region sharedEditingCode

			function dropDownListEditor(container, options, className) {
				var dropDownListDataSource = new kendo.data.DataSource({
					schema: {
						model: {
							id: "rid"
						}
					},
					transport: {
						read: function (e) {
							if (className === "LabUnit") {
								labUnitService.getLabUnitList()
									.then(function (response) {
										e.success(response.data);
									}).catch(function (error) {
										e.error(error);
									});
							} else {
								lovService.getLkpByClass({ className: className })
									.then(function successCallback(response) {
										switch (className) {
											case "LkpSignum":
											case "LkpAgeUnit":
											case "LkpRangeUnit":
											case "LkpGender":
												// case "LkpQuestionStage":
												var noneObject = {
													rid: -1,
													name: {}
												};
												noneObject.name[util.userLocale] = $filter('translate')('none');
												response.unshift(noneObject);
												break;
										}
										e.success(response);
									}).catch(function (response) {
										e.error(response);
									});
							}
						}
					}
				});

				$('<input required name="' + options.field + '"/>')
					.appendTo(container)
					.kendoDropDownList({
						dataValueField: "rid",
						valueTemplate: function (dataItem) {
							if (className === "LabUnit") {
								return dataItem.unitOfMeasure;
							}
							return dataItem.name[util.userLocale];
						},
						template: function (dataItem) {
							if (className === "LabUnit") {
								return dataItem.unitOfMeasure;
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

			//#endregion

			//#region testQuestions

			var testQuestionDataSource = new kendo.data.DataSource({
				page: 1,
				data: [],
				schema: {
					model: {
						id: "rid",
						fields: {
							standardCode: { nullable: false, editable: true, validation: { required: true } },
							description: { editable: true, validation: { required: true } },
							lkpQuestionStage: { editable: true, validation: { required: true } },
							lkpQuestionType: { editable: true, validation: { required: true } },
							testQuestionOptions: { editable: true }
						}
					}
				},
				change: function (e) {
					$scope.testQuestionsChanged = true;
				},
				sync: function (e) {
					$scope.testQuestionsChanged = false;
					$scope.selectedTestQuestion = null;
				}
			});

			$scope.testQuestionsChanged = false;

			$scope.saveTestQuestionChanges = function () {
				testQuestionDataSource.sync();
			}

			$scope.selectedtestQuestion = null;

			$scope.addTestQuestion = function () {
				$scope.selectedTestQuestion = null;
				var newTestQuestion = {
					standardCode: null,
					description: null,
					lkpQuestionStage: null,
					lkpQuestionType: null,
					testQuestionOptions: []
				};
				testQuestionDataSource.add(newTestQuestion);
				var data = testQuestionDataSource.data();
				var dataItem = data[data.length - 1];
				$scope.editTestQuestion(dataItem);
			}

			$scope.editTestQuestion = function (dataItem) {
				$("#testQuestionGrid").data("kendoGrid").editRow(dataItem);
				$("#testQuestionGrid").data("kendoGrid").select("tr[data-uid=" + dataItem.uid + "]");
			}

			$scope.deleteTestQuestion = function () {
				testQuestionDataSource.remove($scope.selectedTestQuestion);
				testQuestionDataSource.sync();
			}

			$scope.addQuestionOptionChip = function (chip) {
				return { description: chip };
			}

			$scope.testQuestionGridOptions = {
				columns: [
					{
						field: "standardCode",
						title: "{{ 'standardCode' | translate }}"
					},
					{
						field: "description",
						title: "{{ 'description' | translate }}"
					},
					{
						field: "lkpQuestionStage",
						title: "{{ 'questionStage' | translate }}",
						editor: function (container, options) {
							dropDownListEditor(container, options, "LkpQuestionStage");
						},
						template: function (dataItem) {
							if (dataItem.lkpQuestionStage == null) {
								return "{{ 'none' | translate }}";
							}
							return dataItem.lkpQuestionStage.name[util.userLocale];
						}
					},
					{
						field: "lkpQuestionType",
						title: "{{ 'questionType' | translate }}",
						editor: function (container, options) {
							dropDownListEditor(container, options, "LkpQuestionType");
						},
						template: function (dataItem) {
							if (dataItem.lkpQuestionType == null) {
								return "{{ 'none' | translate }}";
							}
							return dataItem.lkpQuestionType.name[util.userLocale];
						}
					},
					{
						field: "testQuestionOptions",
						title: "{{ 'questionOptions' | translate }}",
						editor: function (container, options) {
							$scope.questionOptionChipData = options.model.testQuestionOptions;
							var placeholder = $filter('translate')('questionOptions');
							var template = '<md-chips ng-class="questionOptionChipData.length > 0 ? \'\' : \'hide-chips\' " placeholder="' + placeholder + '"' +
								'md-transform-chip="addQuestionOptionChip($chip)" ng-model="questionOptionChipData">' +
								'<md-chip-template>' +
								'<span class="bold">{{$chip.description}}</span>' +
								'</md-chip-template>' +
								'</md-chips>';
							angular.element(container).append(template);
						},
						template: function (dataItem) {
							var options = "";
							for (var i = 0; i < dataItem.testQuestionOptions.length; i++) {
								options += dataItem.testQuestionOptions[i].description;
								if (i < dataItem.testQuestionOptions.length - 1) {
									options += ", ";
								}
							}
							return options;
						}
					}
				],
				editable: "inline",
				dataSource: testQuestionDataSource,
				change: function (e) {
					var selectedRows = this.select();
					if (selectedRows.length > 0) {
						$scope.selectedTestQuestion = this.dataItem(selectedRows[0]);
					} else {
						$scope.selectedTestQuestion = null;
					}
				}
			}

			//#endregion

			//#region testResultSetup

			$scope.resultSetupOptions = {
				testDefinition: $scope.selectedTestDefinition,
				mode: $scope.viewMode
			};

			//#endregion

			//#region testQuestionSetup

			$scope.questionSetupOptions = {
				testDefinition: $scope.selectedTestDefinition
			};

			//#endregion

			//#region normalRangeGrid

			var normalRangesDataSource = new kendo.data.DataSource({
				pageSize: config.gridPageSizes[0],
				page: 1,
				transport: {
					read: function (e) {
						e.data = util.createFilterablePageRequest($scope.normalRangeGridOptions.dataSource);
						if ($scope.selectedTestResult !== null) {
							var resultFilter = {
								field: "testResult",
								value: $scope.selectedTestResult.rid,
								operator: "eq"
							};
							var addResultFilter = true;
							for (var i = 0; i < e.data.filters.length; i++) {
								var filter = e.data.filters[i];
								if (filter.field === resultFilter.field
									&& filter.operator === resultFilter.operator
									&& filter.value === resultFilter.value) {
									addResultFilter = false;
									break;
								}
							}
							if (addResultFilter) {
								e.data.filters.push(resultFilter);
							}
						}
						testDefinitionManagementService.getNormalRangeList(e.data)
							.then(function (response) {
								e.success(response.data);
							}).catch(function (error) {
								e.error(error);
							});
					},
					create: function (e) {
						$scope.selectedNormalRange.testResult = $scope.selectedTestResult;
						testDefinitionManagementService.addNormalRange($scope.selectedNormalRange)
							.then(function (response) {
								util.createToast(util.systemMessages.success, "success");
								e.success(response.data);
							}).catch(function (error) {
								e.error(error);
							});
					},
					update: function (e) {
						$scope.selectedNormalRange.testResult = $scope.selectedTestResult;
						testDefinitionManagementService.editNormalRange($scope.selectedNormalRange)
							.then(function (response) {
								util.createToast(util.systemMessages.success, "success");
								e.success(response.data);
							}).catch(function (error) {
								e.error(error);
							});
					},
					destroy: function (e) {
						testDefinitionManagementService.deleteNormalRange(e.data)
							.then(function (response) {
								util.createToast(util.systemMessages.success, "success");
								e.success(response.data);
							}).catch(function (error) {
								e.error(error);
							});
					}
				},
				sort: { field: "rid", dir: "desc" },
				schema: {
					total: "totalElements",
					data: "content",
					model: {
						id: "rid",
						fields: {
							rid: { type: "number", defaultValue: null },
							ageFrom: { type: "number", defaultValue: null },
							ageTo: { type: "number", defaultValue: null },
							age: { type: "number", defaultValue: null },
							sex: { validation: { required: false }, defaultValue: { name: {} } }, //dropdown
							minDetectableValue: { type: "number", defaultValue: null },
							minPanicValue: { type: "number", defaultValue: null },
							minNormalValue: { type: "number", defaultValue: null },
							maxDetectableValue: { type: "number", defaultValue: null },
							maxPanicValue: { type: "number", defaultValue: null },
							maxNormalValue: { type: "number", defaultValue: null },
							meanNormalValue: { type: "number", defaultValue: null },
							notes: {},
							unit: { validation: { required: false }, defaultValue: { name: {} } }, //dropdown
							signum: { validation: { required: false }, defaultValue: { name: {} } }, //dropdown
							ageFromUnit: { validation: { required: false }, defaultValue: { name: {} } }, //dropdown
							ageToUnit: { validation: { required: false }, defaultValue: { name: {} } }, //dropdown
							ageUnit: { validation: { required: false }, defaultValue: { name: {} } }, //dropdown
							criterionName: {},
							criterionValue: {}
						}
					}
				}
			});

			$scope.selectedNormalRange = null;

			$scope.normalRangeGridOptions = {
				autoBind: false,
				dataSource: normalRangesDataSource,
				columns: [
					{
						hidden: true,
						field: "ageFrom",
						title: "{{ 'ageFrom' | translate }}"
					},
					{
						hidden: true,
						field: "ageTo",
						title: "{{ 'ageTo' | translate }}"
					},
					{
						hidden: true,
						field: "age",
						title: "{{ 'age' | translate }}"
					},
					{
						field: "sex",
						title: "{{ 'sex' | translate }}",
						template: function (dataItem) {
							if (dataItem.sex === null) {
								return $filter('translate')('none');
							}
							return dataItem.sex.name[util.userLocale];
						},
						editor: function (container, options) {
							dropDownListEditor(container, options, "LkpGender");
						}
					},
					{
						hidden: true,
						field: "minDetectableValue",
						title: "{{ 'minDetectableValue' | translate }}"
					},
					{
						hidden: true,
						field: "minPanicValue",
						title: "{{ 'minPanicValue' | translate }}"
					},
					{
						hidden: true,
						field: "minNormalValue",
						title: "{{ 'minNormalValue' | translate }}"
					},
					{
						hidden: true,
						field: "maxDetectableValue",
						title: "{{ 'maxDetectableValue' | translate }}"
					},
					{
						hidden: true,
						field: "maxPanicValue",
						title: "{{ 'maxPanicValue' | translate }}"
					},
					{
						hidden: true,
						field: "maxNormalValue",
						title: "{{ 'maxNormalValue' | translate }}"
					},
					{
						hidden: true,
						field: "meanNormalValue",
						title: "{{ 'meanNormalValue' | translate }}"
					},
					{
						hidden: true,
						field: "notes",
						title: "{{ 'notes' | translate }}"
					},
					{
						field: "unit",
						title: "{{ 'unit' | translate }}",
						template: function (dataItem) {
							if (dataItem.unit === null) {
								return $filter('translate')('none');
							}
							return dataItem.unit.name[util.userLocale];
						},
						editor: function (container, options) {
							dropDownListEditor(container, options, "LkpRangeUnit");
						}
					},
					{
						field: "signum",
						title: "{{ 'signum' | translate }}",
						template: function (dataItem) {
							if (dataItem.signum === null) {
								return $filter('translate')('none');
							}
							return dataItem.signum.name[util.userLocale];
						},
						editor: function (container, options) {
							dropDownListEditor(container, options, "LkpSignum");
						}
					},
					{
						field: "ageFromUnit",
						title: "{{ 'ageFromUnit' | translate }}",
						template: function (dataItem) {
							if (dataItem.ageFromUnit === null) {
								return $filter('translate')('none');
							}
							return dataItem.ageFromUnit.name[util.userLocale];
						},
						editor: function (container, options) {
							dropDownListEditor(container, options, "LkpAgeUnit");
						}
					},
					{
						field: "ageToUnit",
						title: "{{ 'ageToUnit' | translate }}",
						template: function (dataItem) {
							if (dataItem.ageToUnit === null) {
								return $filter('translate')('none');
							}
							return dataItem.ageToUnit.name[util.userLocale];
						},
						editor: function (container, options) {
							dropDownListEditor(container, options, "LkpAgeUnit");
						}
					},
					{
						field: "ageUnit",
						title: "{{ 'ageUnit' | translate }}",
						template: function (dataItem) {
							if (dataItem.ageUnit === null) {
								return $filter('translate')('none');
							}
							return dataItem.ageUnit.name[util.userLocale];
						},
						editor: function (container, options) {
							dropDownListEditor(container, options, "LkpAgeUnit");
						}
					},
					{
						hidden: true,
						field: "criterionName",
						title: "{{ 'criterionName' | translate }}"
					},
					{
						hidden: true,
						field: "criterionValue",
						title: "{{ 'criterionValue' | translate }}"
					}
				],
				editable: "inline",
				edit: function (e) {
					e.sender.select($("tr[data-uid=" + e.model.uid + "]"));
					$scope.normalRangeChanged = true;
				},
				dataBinding: function () {
					$scope.selectedNormalRange = null;
				},
				change: function (e) {
					var selectedRows = e.sender.select();
					if (selectedRows.length > 0) {
						$scope.selectedNormalRange = this.dataItem(selectedRows[0]);
					} else {
						$scope.selectedNormalRange = null;
					}
				}
			};

			function checkNullReferences(dataItem) {
				var keysToCheck = ["sex", "unit", "signum", "ageFromUnit", "ageToUnit", "ageUnit"];
				for (var i in keysToCheck) {
					var key = keysToCheck[i];
					if (dataItem.hasOwnProperty(key)) {
						if (dataItem[key].rid === -1) {
							dataItem[key] = null;
						}
					}
				}
			}

			$scope.addNormalRange = function () {
				var grid = $("#normalRangeGrid").data("kendoGrid");
				grid.addRow();
			}

			$scope.editNormalRange = function (dataItem) {
				var grid = $("#normalRangeGrid").data("kendoGrid");
				grid.editRow(dataItem);
			};

			$scope.deleteNormalRange = function () {
				util.deleteGridRow($scope.selectedNormalRange, normalRangesDataSource);
			};

			$scope.saveNormalRangeChanges = function () {
				checkNullReferences($scope.selectedNormalRange);
				var grid = $("#normalRangeGrid").data("kendoGrid");
				grid.saveChanges();
				$scope.normalRangeChanged = false;
			};

			$scope.cancelNormalRangeChanges = function () {
				var grid = $("#normalRangeGrid").data("kendoGrid");
				grid.cancelChanges();
				$scope.normalRangeChanged = false;
			};

			//#endregion

			//#region fixMultipleGridSelectors

			//capture the grid creation event and change the ids of the checkbox and its label to 
			//make them unique across multiple grids
			$scope.$on("kendoWidgetCreated", function (event, widget) {
				var gridsToFix = ["componentGrid", "reflexTestGrid"];
				var index = gridsToFix.indexOf(widget.wrapper[0].id);
				if (index >= 0) {
					var checkboxInput = $("#" + gridsToFix[index] + " .k-grid-header thead th.k-header input.k-checkbox");
					var checkboxLabel = $("#" + gridsToFix[index] + " .k-grid-header thead th.k-header label.k-checkbox-label");

					checkboxInput.attr("id", checkboxInput.attr("id") + index);
					checkboxLabel.attr("for", checkboxInput.attr("id"));
				}
			});

			//#endregion

			$scope.removeChip = function (chip, gridId) {
				util.removeGridChip(chip, $("#" + gridId).data("kendoGrid"));
			}
		}
	]);
});
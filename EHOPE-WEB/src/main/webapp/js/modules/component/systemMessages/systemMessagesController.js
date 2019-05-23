define(['app', 'config', 'util', 'commonData'], function (app, config, util, commonData) {
	'use strict';
	app.controller('systemMessagesCtrl', [
		'$scope', 'systemMessagesService', 'commonMethods', 'lovService', '$filter', '$translate',
		function ($scope, systemMessagesService, commonMethods, lovService, $filter, $translate) {
			$scope.tenantMessageCode = null;
			$scope.tenantMessage = {};
			$scope.metaData = {};
			$scope.messageTypeLkp = null;
			var allMessageTypes = [];
			$scope.transFields = {};
			$scope.isAppAdmin = commonData.appAdminRid == util.user.rid;// to disable enable code input
			var originalTenantMessage = null;//to be used if user changed code so we can create a new label.
			commonMethods.retrieveMetaData("ComTenantMessage").then(function (response) {
				$scope.metaData = response.data;
				lovService.getLkpByClass({ className: "LkpMessagesType" }).then(function (data) {
					allMessageTypes = angular.copy(data);
					$scope.messageTypeLkp = {
						className: "LkpMessagesType",
						name: $scope.metaData.lkpMessagesType.name,
						labelText: "messageType",
						valueField: "name." + util.userLocale,
						selectedValue: null,
						required: $scope.metaData.lkpMessagesType.notNull,
						data: data
					};
				});

				$scope.transFields = {
					description: util.getTransFieldLanguages("description", "description", null, $scope.metaData.description.notNull)
				};
			});

			$scope.submitTenantMessage = function (isValid) {
				if (!isValid) {
					return;
				}
				$scope.tenantMessage.code = $scope.tenantMessageCode;
				$scope.tenantMessage[$scope.messageTypeLkp.name] = $scope.messageTypeLkp.selectedValue;
				systemMessagesService.createTenantMessage($scope.tenantMessage).then(function () {
					util.createToast(util.systemMessages.success, "success");
					$scope.clearFields();
					$scope.refreshGrid();
				});
			};

			$scope.refreshGrid = function () {
				$scope.tenantMessagesGrid.dataSource.read();
			};

			$scope.updateTenantMessage = function (isValid) {
				if (!isValid) {
					return;
				}
				$scope.tenantMessage.code = $scope.tenantMessageCode;
				$scope.tenantMessage[$scope.messageTypeLkp.name] = $scope.messageTypeLkp.selectedValue;
				systemMessagesService.updateTenantMessage($scope.tenantMessage)
					.then(function () {
						util.createToast(util.systemMessages.success, "success");
						$scope.clearFields();
						$scope.refreshGrid();
					}).catch(function () {
						$scope.refreshGrid();
					})

			};
			$scope.clearFields = function () {
				$scope.tenantMessagesForm.$setPristine();
				$scope.tenantMessagesForm.$setUntouched();
				$scope.tenantMessage = {};
				$scope.tenantMessageCode = null;
				$scope.messageTypeLkp.clearLkps([$scope.messageTypeLkp]);
				for (var i in util.languages) {
					var descriptionLang = $scope.transFields.description[i];
					descriptionLang.value = null;
				}
			};

			$scope.deleteTenantMessage = function () {
				return systemMessagesService.deleteTenantMessage($scope.tenantMessage).then(function () {
					util.createToast(util.systemMessages.success, "success");
					$scope.clearFields();
					$scope.refreshGrid();
				});
			}

			var tenantMessagesColumns = [
				{
					field: "code",
					title: util.systemMessages.code,
					width: "15%"
				},
				{
					field: "lkpMessagesType",
					title: util.systemMessages.messageType,
					width: "15%",
					template: function (dataItem) {
						return dataItem.lkpMessagesType ? dataItem.lkpMessagesType.name[util.userLocale] : "";
					},
					filterable: {
						ui: function (element) {
							util.createListFilter(element, allMessageTypes, ("name." + $scope.userLocale));
						}
					}
				}
			];
			var tenantMessagesModels = {
				id: "rid",
				fields: {
					"code": {
						type: "string"
					},
					"description": {
						defaultValue: {}
					},
					"lkpMessagesType": {
						type: "lov"
					}
				}
			};

			var languages = angular.copy(util.user.tenantLanguages);
			languages.sort(function (a, b) { return (a.isPrimary === b.isPrimary) ? 0 : a.isPrimary ? -1 : 1 });//primary first
			for (var objKey in languages) {
				var obj = languages[objKey];
				var column =
				{
					field: ("description_" + obj.comLanguage.locale),
					title: $filter("translator")("description", obj.comLanguage.locale)
				};

				tenantMessagesModels.fields[("description_" + obj.comLanguage.locale)] = {
					from: "description." + obj.comLanguage.locale,
					type: "string"
				};
				tenantMessagesColumns.splice(1 + parseInt(objKey), 0, column);
			}

			var dataSource = new kendo.data.DataSource({
				pageSize: config.gridPageSizes[0],
				page: 1,
				serverPaging: true,
				serverFiltering: true,
				transport: {
					read: function (e) {
						e.data = util.createFilterablePageRequest(dataSource, { "lkpMessagesType": "lkpMessagesType.rid" });
						if (e.data.sortList != null && e.data.sortList.length > 0) {
							for (var idx = 0; idx < e.data.sortList.length; idx++) {
								if (e.data.sortList[idx].property.startsWith("description")) {
									e.data.sortList[idx].property = "description";
								}
							}
						}
						// createFilterablePageRequest(...) filterMap, do not support startsWith
						if (e.data.filters != null && e.data.filters.length > 0) {
							for (var idx = 0; idx < e.data.filters.length; idx++) {
								if (e.data.filters[idx].field.startsWith("description")) {
									e.data.filters[idx].field = "description";
								}
							}
						}
						systemMessagesService.getTenantMessagesList(e.data)
							.then(function (response) {
								e.success(response.data);
								util.prepareSystemMessages(response.data);
							});
					}
				},
				sort: { field: "code", dir: "asc" },
				schema: {
					parse: function (response) {
						var descriptionKeys = [];
						for (var idx = 1; idx < tenantMessagesColumns.length - 1; idx++) {
							descriptionKeys.push(tenantMessagesColumns[idx].field);
						}
						for (var i = 0; i < response.content.length; i++) {
							var obj = response.content[i];
							for (var j = 0; j < descriptionKeys.length; j++) {
								var languageKey = descriptionKeys[j].substring(descriptionKeys[j].indexOf("_") + 1);
								if (obj.description[languageKey] == null) {
									obj.description[languageKey] = "";
								}
								obj[descriptionKeys[j]] = obj.description[languageKey];
							}
						}
						return response;
					},
					data: "content",
					total: "totalElements",
					model: tenantMessagesModels
				}
			});

			$scope.tenantMessagesGridOptions = {
				columns: tenantMessagesColumns,
				dataSource: dataSource,
				change: function () {
					$scope.tenantMessage = $scope.tenantMessagesGrid.dataItem($scope.tenantMessagesGrid.select());
					$scope.tenantMessageCode = $scope.tenantMessage.code;
					$scope.messageTypeLkp.selectedValue = $scope.tenantMessage[$scope.messageTypeLkp.name];
					originalTenantMessage = angular.copy($scope.tenantMessage);
				}
			};

			$scope.tenantMessageCodeListener = function () {
				//If user changed code then this is a new label.
				//If user set code back to its original state then set back the rid to update it.
				if (originalTenantMessage == null || originalTenantMessage.rid == null) {
					return;
				}
				if (originalTenantMessage.code != $scope.tenantMessageCode) {
					delete $scope.tenantMessage.rid;
				} else {
					$scope.tenantMessage["rid"] = originalTenantMessage.rid;
				}
			};
		}
	]);
});

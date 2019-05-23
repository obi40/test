define(['app', 'util'], function (app, util) {
	'use strict';
	app.controller('tenantManagementCtrl', ['$scope', 'tenantFormService', 'commonMethods', 'lovService', 'configService',
		function ($scope, tenantFormService, commonMethods, lovService, configService) {
			$scope.tenant = null;
			$scope.tenantFormOptions = {};
			$scope.tenantSmsConfigurations = null;
			$scope.smsConfigurationMetaData = null;
			$scope.smsTest = {
				output: null,
				mobileNumber: null,
				message: null
			};
			var smsKeyList = [];
			var smsDummyRid = -1;
			getTenant();

			commonMethods.retrieveMetaData("ConfTenantSMS").then(function (response) {
				$scope.smsConfigurationMetaData = response.data;
				lovService.getAnyLkpByClass({ className: "LkpSMSKey" }).then(function (response) {
					smsKeyList = response.data;
					getSMSConfigurations();
				});
			});
			function getTenant() {
				tenantFormService.getTenantData(util.user.tenantId).then(function (response) {
					$scope.tenant = response.data;
					//incase it didnt get populated yet
					if ($scope.tenantFormOptions.setTenantLkps) {
						$scope.tenantFormOptions.setTenantLkps($scope.tenant);
					}
				});
			}
			$scope.submitTenant = function () {
				var tenant = $scope.tenantFormOptions.getTenant();
				var data = {
					logo: $scope.tenantFormOptions.fileDataWrapper.fileModel,
					headerImage: $scope.tenantFormOptions.headerFileDataWrapper.fileModel,
					footerImage: $scope.tenantFormOptions.footerFileDataWrapper.fileModel,
					tenant: tenant,
					tenantLanguages: $scope.tenantFormOptions.tenantLanguages
				};
				if (tenant.rid != null) {
					tenantFormService.updateTenant(data).then(function () {
						util.createToast(util.systemMessages.success, "success");
						getTenant();
					});
				} else {
					tenantFormService.createTenant(data).then(function () {
						util.createToast(util.systemMessages.success, "success");
						getTenant();
					});
				}
			};
			function prepareTenantSmsConfigurations(data) {
				for (var idx = 0; idx < data.length; idx++) {
					var obj = data[idx];
					obj["smsKeyLkp"] = {
						className: "LkpSMSKey",
						name: "smsKey",
						labelText: "smsKey",
						valueField: "name." + util.userLocale,
						selectedValue: obj["smsKey"],
						required: obj["smsKey"] != null ? true : false,
						data: smsKeyList
					};
				}
			}
			function getSMSConfigurations() {
				configService.getSMSConfig().then(function (response) {
					$scope.tenantSmsConfigurations = response.data;
					if ($scope.tenantSmsConfigurations.length != 0) {
						prepareTenantSmsConfigurations($scope.tenantSmsConfigurations);
					}
				});
			}
			$scope.addDummySMS = function () {
				$scope.tenantSmsConfigurations.push({
					rid: smsDummyRid--,
					isEncodeKey: false,
					isEncodeValue: false
				});
				prepareTenantSmsConfigurations([$scope.tenantSmsConfigurations[$scope.tenantSmsConfigurations.length - 1]]);
			};
			$scope.deleteSMS = function (smsConfig) {
				for (var idx = 0; idx < $scope.tenantSmsConfigurations.length; idx++) {
					if ($scope.tenantSmsConfigurations[idx].rid == smsConfig.rid) {
						$scope.tenantSmsConfigurations.splice(idx, 1);
						break;
					}
				}
			};
			$scope.setSmsConfigurations = function (callback) {
				//to set the current configurations then fire the callback
				var data = angular.copy($scope.tenantSmsConfigurations);
				for (var idx = 0; idx < data.length; idx++) {
					if (data[idx].rid < 0) {//incase a dummy object
						delete data[idx].rid;
					}
					data[idx].smsKey = data[idx].smsKeyLkp.selectedValue;
				}
				configService.setSMSConfig(data).then(function () {
					callback();
				});
			}
			$scope.submitSmsConfigurations = function () {
				util.createToast(util.systemMessages.success, "success");
				getSMSConfigurations();
			};


			$scope.onSmsValueChange = function (smsConfig) {
				// if value is set then remove lkp value
				if (!smsConfig.smsKeyLkp.required && (smsConfig.value == null || smsConfig.value == "")) {
					smsConfig.smsKeyLkp.required = true;
				} else if (smsConfig.smsKeyLkp.required) {
					smsConfig.smsKeyLkp.required = false;
					smsConfig.smsKeyLkp.selectedValue = null;
					smsConfig.smsKey = null;
				}
			};

			$scope.testSMSConfigurations = function () {
				var data = {
					mobile: $scope.smsTest.mobileNumber,
					message: $scope.smsTest.message
				};
				configService.testSMSConfig(data).then(function () { });
			};

			//the full output of all sms configurations
			$scope.smsTestOutputGenerator = function () {
				$scope.smsTest.output = "";
				if ($scope.tenantSmsConfigurations == null || $scope.tenantSmsConfigurations.length == 0) {
					return;
				}
				var url = "";
				for (var idx = 0; idx < $scope.tenantSmsConfigurations.length; idx++) {
					var obj = $scope.tenantSmsConfigurations[idx];
					var key = obj.key;
					var value = obj.value;
					if (obj.smsKey != null) {
						if (obj.smsKey.code == "BASE_URL") {
							url = key + "?";
							continue;
						} else if (obj.smsKey.code == "MOBILE") {
							value = $scope.smsTest.mobileNumber;
						} else if (obj.smsKey.code == "MESSAGE") {
							value = $scope.smsTest.message;
						}
					}
					if (obj.isEncodeKey) {
						key = encodeURI(key);
					}
					if (obj.isEncodeValue) {
						value = encodeURI(value);
					}
					$scope.smsTest.output += key + "=" + value;
					if (idx + 1 != $scope.tenantSmsConfigurations.length) {
						$scope.smsTest.output += "&";
					}
				}
				$scope.smsTest.output = url + $scope.smsTest.output;
			};
			$scope.$watch("tenantSmsConfigurations", function () {
				$scope.smsTestOutputGenerator();
			}, true);


		}]);
});

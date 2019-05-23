define(['app', 'util'], function (app, util) {
	'use strict';
	app.service('APIInterceptor', ['$rootScope', '$state', function ($rootScope, $state) {
		var serverRequests = [];
		this.getServerRequests = function () { return serverRequests };
		this.insertServerRequest = function (request) { serverRequests.push(request) };

		function httpEnableClick(responseId) {
			if (!responseId) {
				return;
			}
			$rootScope.$broadcast('httpEnableClick', responseId);
		}

		this.request = function (config) {
			if (config.method === "POST" && util.token) {
				config.headers['Authorization'] = util.token;
			}
			return config;
		};

		this.requestError = function (config) {
			httpEnableClick(config.id);
			return config;
		};

		this.response = function (response) {
			httpEnableClick(response.config.id);
			return response;
		};

		this.responseError = function (responseError) {
			if (responseError.config.method !== "POST") {
				return;
			}
			httpEnableClick(responseError.config.id);
			if (responseError.status === 401) {
				$state.go('login');
			}
			errorHandler(responseError.data);
			throw responseError;
		};
		function errorHandler(data) {
			if (data == null) { // fallback
				util.createToast(util.systemMessages.somethingWrong, "error");
				return;
			}
			if (util.isJsonString(data)) {
				data = JSON.parse(data);
			}
			//handle token filtration  errors
			if (data.code === "invalidToken") {
				util.clearUtilData();
				$state.go("login");
				return;
			}

			function createToast(data) {
				var errorCode = data.code;
				var parameters = data.parameters;
				var translatedError;
				if (util.systemMessages.hasOwnProperty(errorCode)) { // systemMessages are set
					translatedError = util.systemMessages[errorCode];
					if (parameters) {
						for (var key in parameters) {
							translatedError = translatedError.replace(key, parameters[key]);
						}
					}
				} else {
					translatedError = util.systemMessages.somethingWrong;
				}
				util.createToast(translatedError, data.severity || "error");
			}
			if (data.size && data.size > 0 && data.type && data.type === "application/json") {//in case throwing an exception from the uploading process
				var b = new Blob([data], { type: "application/json" });
				var fr = new FileReader();
				fr.onload = function () {
					createToast(JSON.parse(this.result));
				};
				fr.readAsText(b);
			} else {
				createToast(data);
			}
		}

	}]);

});
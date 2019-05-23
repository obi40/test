define(['app', 'config'], function (app, config) {
	'use strict';
	app.service('testDefinitionInfoService', function ($http) {
		this.getTestDefinition = function (id) {
			return $http({
				data: id,
				method: "POST",
				url: config.server + config.api_path + "getTestDefinition.srvc"
			}).then(function successCallback(response) {
				console.log(response.data);
			}, function errorCallback(response) {
				console.log(response);
			});
		}
	});
});

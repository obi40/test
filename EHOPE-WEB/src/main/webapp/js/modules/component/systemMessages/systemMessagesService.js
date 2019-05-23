define(['app', 'util'], function (app, util) {
	'use strict';
	app.service('systemMessagesService', function () {

		this.getTenantMessagesList = function (data) {
			return util.createApiRequest("getTenantMessagesList.srvc", JSON.stringify(data));
		};
		this.getLabels = function () {
			return util.createApiRequest("getLabels.srvc");
		};

		this.createTenantMessage = function (data) {
			return util.createApiRequest("createTenantMessage.srvc", JSON.stringify(data));
		};

		this.updateTenantMessage = function (data) {
			return util.createApiRequest("updateTenantMessage.srvc", JSON.stringify(data));
		};

		this.deleteTenantMessage = function (data) {
			return util.createApiRequest("deleteTenantMessage.srvc", JSON.stringify(data));
		};

		//languages
		this.getSupportedLanguages = function () {
			return util.createApiRequest("getSupportedLanguages.pub.srvc");
		};
		this.getTenantLanguages = function () {
			return util.createApiRequest("getTenantLanguages.srvc");
		};
		this.setTenantLanguages = function (data) {
			return util.createApiRequest("setTenantLanguages.srvc", JSON.stringify(data));
		};

	});
});

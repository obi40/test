define(['app', 'util'], function (app, util) {
	'use strict';
	app.service('testDefinitionManagementService', function () {

		this.getTestDefinitionPage = function (data) {
			return util.createApiRequest("getTestDefinitionPage.srvc", JSON.stringify(data));
		};
		this.getSelectableTestDefinitionPage = function (data) {
			return util.createApiRequest("getSelectableTestDefinitionPage.srvc", JSON.stringify(data));
		};
		this.getTestDefinitionLookup = function (data) {
			return util.createApiRequest("getTestDefinitionLookup.srvc", JSON.stringify(data));
		};
		this.getTestDefinitionLookupWithDestinations = function (data) {
			return util.createApiRequest("getTestDefinitionLookupWithDestinations.srvc", JSON.stringify(data));
		};
		this.getTestDefinition = function (map) {
			return util.createApiRequest("getTestDefinition.srvc", JSON.stringify(map));
		};
		this.getQuickTestDefinition = function (testRid) {
			return util.createApiRequest("getQuickTestDefinition.srvc", JSON.stringify(testRid));
		};
		this.addTestDefinition = function (testDefinition) {
			return util.createApiRequest("addTestDefinition.srvc", JSON.stringify(testDefinition));
		};
		this.editTestDefinition = function (testDefinition) {
			return util.createApiRequest("editTestDefinition.srvc", JSON.stringify(testDefinition));
		};
		this.isTestDefinitionEditable = function (testDefinition) {
			return util.createApiRequest("isTestDefinitionEditable.srvc", JSON.stringify(testDefinition));
		};
		this.activateTestDefinition = function (testDefinitionFetch) {
			return util.createApiRequest("activateTestDefinition.srvc", JSON.stringify(testDefinitionFetch));
		}
		this.deactivateTestDefinition = function (testDefinitionFetch) {
			return util.createApiRequest("deactivateTestDefinition.srvc", JSON.stringify(testDefinitionFetch));
		};
		this.getNormalRangeList = function (data) {
			return util.createApiRequest("getNormalRangeList.srvc", JSON.stringify(data));
		};
		this.addNormalRange = function (data) {
			return util.createApiRequest("addNormalRange.srvc", JSON.stringify(data));
		};
		this.editNormalRange = function (data) {
			return util.createApiRequest("editNormalRange.srvc", JSON.stringify(data));
		};
		this.deleteNormalRange = function (data) {
			return util.createApiRequest("deleteNormalRange.srvc", JSON.stringify(data));
		};
		this.getTestsDefaultPricingPage = function (data) {
			return util.createApiRequest("getTestsDefaultPricingPage.srvc", JSON.stringify(data));
		};
	});
});

define(['app', 'util'], function (app, util) {
    'use strict';
    app.service('testSelectionService', function () {
        this.getAllSections = function () {
            return util.createApiRequest("getAllSections.srvc");
        };
        this.getSectionsWithType = function () {
            return util.createApiRequest("getSectionsWithType.srvc");
        };
        this.getFilteredSectionList = function (searchQuery) {
            return util.createApiRequest("getFilteredSectionList.srvc", JSON.stringify(searchQuery));
        };
        this.getMostRequestedTests = function (count) {
            return util.createApiRequest("getMostRequestedTests.srvc", count);
        };
        this.createActualTests = function (data) {
            return util.createApiRequest("createActualTests.srvc", JSON.stringify(data));
        };
        this.deleteActualTest = function (data) {
            return util.createApiRequest("deleteActualTest.srvc", JSON.stringify(data));
        };
    });
});
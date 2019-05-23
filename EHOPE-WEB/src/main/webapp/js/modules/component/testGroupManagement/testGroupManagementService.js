define(['app', 'util'], function (app, util) {
    'use strict';
    app.service('testGroupManagementService', function () {
        this.getGroupsPage = function (data) {
            return util.createApiRequest("getTestGroupsPage.srvc", JSON.stringify(data));
        };
        this.getGroups = function (data) {
            return util.createApiRequest("getTestGroups.srvc", JSON.stringify(data));
        };
        this.getTestGroupsWithDestinations = function (data) {
            return util.createApiRequest("getTestGroupsWithDestinations.srvc", JSON.stringify(data));
        };
        this.createTestGroup = function (data) {
            return util.createApiRequest("createTestGroup.srvc", JSON.stringify(data));
        };
        this.updateTestGroup = function (data) {
            return util.createApiRequest("updateTestGroup.srvc", JSON.stringify(data));
        };
        this.deleteTestGroup = function (data) {
            return util.createApiRequest("deleteTestGroup.srvc", JSON.stringify(data));
        };

        this.getTestGroupDetails = function (data) {
            return util.createApiRequest("getTestGroupDetails.srvc", JSON.stringify(data));
        };

    });
});

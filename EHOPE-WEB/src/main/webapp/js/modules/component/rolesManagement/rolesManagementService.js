define(['app', 'util'], function (app, util) {
    'use strict';
    app.service('rolesManagementService', function () {

        //this service is used when displaying roles in another managements pages i.e. group management
        this.getSecRoleList = function () {
            return util.createApiRequest("getSecRoleList.srvc");
        };

        //to display the roles with the rights they has
        this.getSecRoleWithRightsGroupsList = function () {
            return util.createApiRequest("getSecRoleWithRightsGroupsList.srvc");
        };

        this.getSecRightList = function () {
            return util.createApiRequest("getSecRightList.srvc");
        };

        this.createSecRole = function (data) {
            return util.createApiRequest("createSecRole.srvc", JSON.stringify(data));
        };

        this.updateSecRole = function (data) {
            return util.createApiRequest("updateSecRole.srvc", JSON.stringify(data));
        };

        this.deleteSecRole = function (data) {
            return util.createApiRequest("deleteSecRole.srvc", JSON.stringify(data));
        };

        this.getSecUsersListByRole = function (data) {
            return util.createApiRequest("getSecUsersListByRole.srvc", JSON.stringify(data));
        };

    });
});

define(['app', 'util'], function (app, util) {
    'use strict';
    app.service('usersManagementService', function () {

        this.getSecUserPage = function (data) {
            return util.createApiRequest("getSecUserPage.srvc", JSON.stringify(data));
        };

        //this service is used when displaying users in another managements pages i.e. group management
        this.getSecUserPageWithGroups = function (data) {
            return util.createApiRequest("getSecUserPageWithGroups.srvc", JSON.stringify(data));
        };
        //this service is used when displaying users in another managements pages i.e. group management
        this.getSecUserPageWithRolesGroups = function (data) {
            return util.createApiRequest("getSecUserPageWithRolesGroups.srvc", JSON.stringify(data));
        };

        this.deactivateSecUser = function (data) {
            return util.createApiRequest("deactivateSecUser.srvc", JSON.stringify(data));
        };

        this.activateSecUser = function (data) {
            return util.createApiRequest("activateSecUser.srvc", JSON.stringify(data));
        };

        this.createSecUser = function (data) {
            return util.createApiRequest("createSecUser.srvc", JSON.stringify(data));
        };

        this.updateSecUser = function (data) {
            return util.createApiRequest("updateSecUser.srvc", JSON.stringify(data));
        };

        this.resetSecUserPassword = function (data) {
            return util.createApiRequest("resetSecUserPassword.srvc", JSON.stringify(data));
        };

        this.getSecUserGroups = function (data) {
            return util.createApiRequest("getSecGroupUserBySecUser.srvc", JSON.stringify(data));
        };

        this.getSecUserRoles = function (data) {
            return util.createApiRequest("getSecUserRoleBySecUser.srvc", JSON.stringify(data));
        };
    });
});

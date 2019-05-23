define(['app', 'util'], function (app, util) {
    'use strict';
    app.service('groupsManagementService', function () {

        //this service is used when displaying groups in another managements pages i.e. role management
        this.getSecGroupList = function () {
            return util.createApiRequest("getSecGroupList.srvc");
        };
        //to display the groups with the roles they has
        this.getSecGroupWithRolesList = function () {
            return util.createApiRequest("getSecGroupWithRolesList.srvc");
        };

        this.createSecGroup = function (data) {
            return util.createApiRequest("createSecGroup.srvc", JSON.stringify(data));
        };

        this.updateSecGroup = function (data) {
            return util.createApiRequest("updateSecGroup.srvc", JSON.stringify(data));
        };

        this.deleteSecGroup = function (data) {
            return util.createApiRequest("deleteSecGroup.srvc", JSON.stringify(data));
        };

        this.getSecUsersListByGroup = function (data) {
            return util.createApiRequest("getSecUsersListByGroup.srvc", JSON.stringify(data));
        };
    });
});

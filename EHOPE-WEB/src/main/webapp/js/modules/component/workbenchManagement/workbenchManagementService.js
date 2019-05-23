define(['app', 'util'], function (app, util) {
    'use strict';
    app.service('workbenchManagementService', function () {

        this.getWorkbenchList = function (data) {
            return util.createApiRequest("getWorkbenchList.srvc", JSON.stringify(data));
        };
        this.getWorkbenchPage = function (data) {
            return util.createApiRequest("getWorkbenchPage.srvc", JSON.stringify(data));
        };
        this.addWorkbench = function (data) {
            return util.createApiRequest("addWorkbench.srvc", JSON.stringify(data));
        };
        this.editWorkbench = function (data) {
            return util.createApiRequest("editWorkbench.srvc", JSON.stringify(data));
        };
        this.deleteWorkbench = function (data) {
            return util.createApiRequest("deleteWorkbench.srvc", data);
        };

    });
});

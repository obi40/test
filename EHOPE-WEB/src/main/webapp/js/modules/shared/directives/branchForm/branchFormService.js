define(['app', 'util'], function (app, util) {
    'use strict';
    app.service('branchFormService', function () {

        //for lovs
        this.getLabBranchList = function (data) {
            return util.createApiRequest("getLabBranchList.srvc", JSON.stringify(data));
        };
        this.getLabBranchListExcluded = function (data) {
            return util.createApiRequest("getLabBranchListExcluded.srvc", JSON.stringify(data));
        };
        this.getBranches = function () {
            return util.createApiRequest("getBranches.srvc");
        };
        this.createBranch = function (data) {
            return util.createApiRequest("createBranch.srvc", JSON.stringify(data));
        };
        this.updateBranch = function (data) {
            return util.createApiRequest("updateBranch.srvc", JSON.stringify(data));
        };
        this.deleteBranch = function (data) {
            return util.createApiRequest("deleteBranch.srvc", JSON.stringify(data));
        };
        this.activateBranch = function (data) {
            return util.createApiRequest("activateBranch.srvc", JSON.stringify(data));
        };
        this.deactivateBranch = function (data) {
            return util.createApiRequest("deactivateBranch.srvc", JSON.stringify(data));
        };

    });
});
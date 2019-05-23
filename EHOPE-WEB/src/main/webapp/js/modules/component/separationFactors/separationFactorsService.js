define(['app', 'util'], function (app, util) {
    'use strict';
    app.service('separationFactorsService', function () {
        this.getSepFactorList = function () {
            return util.createApiRequest("getSepFactorList.srvc");
        };
        this.updateBranchSepFactor = function (factorsList) {
            return util.createApiRequest("updateBranchSepFactor.srvc", JSON.stringify(factorsList));
        };
        this.getActiveFactorsByBranch = function () {
            return util.createApiRequest("getActiveFactorsByBranch.srvc");
        };
    });
});

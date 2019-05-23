define(['app', 'util'], function (app, util) {
    'use strict';
    app.service('lkpManagementService', function () {

        this.getLkpMasterList = function () {
            return util.createApiRequest("getLkpMasterList.srvc");
        };

    });
});

define(['app', 'util'], function (app, util) {
    'use strict';
    app.service('testResultEntryService', function () {

        this.getTestActualListWithResultsByVisit = function (data) {
            return util.createApiRequest("getTestActualListWithResultsByVisit.srvc", JSON.stringify(data));
        };

        this.getTestCodedResultMappingsByResult = function (data) {
            return util.createApiRequest("getTestCodedResultMappingsByResult.srvc", JSON.stringify(data));
        };

        this.reorderActualTests = function (data) {
            return util.createApiRequest("reorderActualTests.srvc", JSON.stringify(data));
        };
    });
});
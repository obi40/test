define(['app', 'util'], function (app, util) {
    'use strict';
    app.service('destinationEntryService', function () {
        this.getDestinationEntryData = function (data) {
            return util.createApiRequest("getDestinationEntryData.srvc", JSON.stringify(data));
        };
        this.updateActualTestsDestinations = function (data) {
            return util.createApiRequest("updateActualTestsDestinations.srvc", JSON.stringify(data));
        };
    });
});
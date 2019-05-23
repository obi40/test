define(['app', 'util'], function (app, util) {
    'use strict';
    app.service('quickTestDefinitionService', function () {

        this.quickTestDefinition = function (data) {
            return util.createApiRequest("quickTestDefinition.srvc", JSON.stringify(data));
        };

    });
});
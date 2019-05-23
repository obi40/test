define(['app', 'util'], function (app, util) {
    'use strict';
    app.service('sectionService', function () {

        this.getSectionPage = function (data) {
            return util.createApiRequest("getSectionPage.srvc", JSON.stringify(data));
        };

        this.addSection = function (data) {
            return util.createApiRequest("createSection.srvc", JSON.stringify(data));
        };

        this.updateSection = function (data) {
            return util.createApiRequest("updateSection.srvc", JSON.stringify(data));
        };

        this.deleteSection = function (data) {
            return util.createApiRequest("deleteSection.srvc", JSON.stringify(data));
        };
    });
});


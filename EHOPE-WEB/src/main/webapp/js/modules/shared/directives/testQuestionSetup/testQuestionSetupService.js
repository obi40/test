define(['app', 'util'], function (app, util) {
    'use strict';
    app.service('testQuestionSetupService', function () {

        this.getQuestions = function (data) {
            return util.createApiRequest("getQuestions.srvc", JSON.stringify(data));
        };

    });
});
define(['app', 'util'], function (app, util) {
    'use strict';
    app.service('testQuestionEntryService', function () {
        this.getTestQuestionEntryData = function (data) {
            return util.createApiRequest("getTestQuestionEntryData.srvc", JSON.stringify(data));
        };
        this.answerQuestions = function (data) {
            return util.createApiRequest("answerQuestions.srvc", JSON.stringify(data));
        };
    });
});
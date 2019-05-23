define(['app', 'util'], function (app, util) {
    'use strict';
    app.service('userProfileService', function () {
        this.updateUserProfile = function (data) {
            return util.createApiRequest("updateSecUserProfile.srvc", JSON.stringify(data));
        };

        this.updateEmailPassword = function (data) {
            return util.createApiRequest("updateEmailPassword.srvc", JSON.stringify(data));
        };

        this.generateBranchedToken = function (data) {
            return util.createApiRequest("generateBranchedToken.srvc", JSON.stringify(data));
        };

    });
});

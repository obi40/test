define(['app', 'util'], function (app, util) {
    'use strict';
    app.service('passwordResetService', function () {
        this.changeForgottenPassword = function (data) {
            return util.createApiRequest("changeForgottenPassword.pub.srvc", JSON.stringify(data));
        };
    });
});

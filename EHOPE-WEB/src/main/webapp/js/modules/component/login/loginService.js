define(['app', 'util'], function (app, util) {
  'use strict';
  app.service('loginService', function () {

    this.loginUser = function (user) {
      return util.createApiRequest("login.pub.srvc", JSON.stringify(user));
    };

    this.forgotPassword = function (email) {
      return util.createApiRequest("forgotPassword.pub.srvc", JSON.stringify(email));
    };

    this.generateDummyToken = function () {
      return util.createApiRequest("generateDummyToken.pub.srvc");
    };

    this.generateTenantOnboardingToken = function (data) {
      return util.createApiRequest("generateTenantOnboardingToken.pub.srvc", JSON.stringify(data));
    };


  });
});

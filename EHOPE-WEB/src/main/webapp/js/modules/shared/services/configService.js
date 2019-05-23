define(['app', 'util'], function (app, util) {
  'use strict';
  app.service('configService', function () {
    this.getSMSConfig = function () {
      return util.createApiRequest("getSMSConfig.srvc");
    };
    this.setSMSConfig = function (data) {
      return util.createApiRequest("setSMSConfig.srvc", JSON.stringify(data));
    };
    this.testSMSConfig = function (data) {
      return util.createApiRequest("testSMSConfig.srvc", JSON.stringify(data));
    };
  });
});
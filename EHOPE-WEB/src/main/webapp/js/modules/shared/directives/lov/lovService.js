define(['app', 'config', 'util'], function (app, config, util) {
  'use strict';
  app.service('lovService', ["$http", function ($http) {

    this.getLkpByClass = function (lkpWrapper) {
      return $http({
        method: "POST",
        url: config.server + config.api_path + "getLkpByClass.srvc",
        data: JSON.stringify(lkpWrapper)
      }).then(function (response) {
        return response.data;
      });
    };

    this.getAnyLkpByClass = function (lkpWrapper) {
      return util.createApiRequest("getAnyLkpByClass.srvc", JSON.stringify(lkpWrapper));
    };

    this.getOneLkpByClass = function (lkpWrapper) {
      return util.createApiRequest("getOneLkpByClass.srvc", JSON.stringify(lkpWrapper));
    };

    this.createLkp = function (map) {
      return util.createApiRequest("createLkp.srvc", JSON.stringify(map));
    };

    this.updateLkp = function (map) {
      return util.createApiRequest("updateLkp.srvc", JSON.stringify(map));
    };

    this.deleteLkp = function (map) {
      return util.createApiRequest("deleteLkp.srvc", JSON.stringify(map));
    };

  }]);
});

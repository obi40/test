define(['app', 'config'], function (app, config) {
    'use strict';
    app.service('nameService', ["$http", function ($http) {
        this.getTransliteration = function (data) {
            return $http({
                method: "POST",
                url: config.server + config.api_path + "getTransliteration.srvc",
                data: JSON.stringify(data)
            });
        }

        this.getLocalTransliteration = function (data) {
            return $http({
                method: "POST",
                url: config.server + config.api_path + "getLocalTransliteration.srvc",
                data: JSON.stringify(data)
            });
        }
    }]);
});
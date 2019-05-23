define(['app', 'config'], function (app, config) {
    'use strict';
    app.service('testResultSetupService', ["$http", function ($http) {
        this.getNarrativeResultTemplates = function (resultRid) {
            return $http({
                data: resultRid,
                method: "POST",
                url: config.server + config.api_path + "getNarrativeResultTemplates.srvc"
            });
        }

        this.getTestResults = function (testRid) {
            return $http({
                data: testRid,
                method: "POST",
                url: config.server + config.api_path + "getTestResults.srvc"
            });
        }

        this.saveTestResults = function (results) {
            return $http({
                data: JSON.stringify(results),
                method: "POST",
                url: config.server + config.api_path + "saveTestResults.srvc"
            });
        }

        this.getTestCodedResults = function (data) {
            return $http({
                data: JSON.stringify(data),
                method: "POST",
                url: config.server + config.api_path + "getTestCodedResults.srvc"
            });
        }

        this.addTestCodedResult = function (data) {
            return $http({
                data: JSON.stringify(data),
                method: "POST",
                url: config.server + config.api_path + "addTestCodedResult.srvc"
            });
        }

        this.editTestCodedResult = function (data) {
            return $http({
                data: JSON.stringify(data),
                method: "POST",
                url: config.server + config.api_path + "editTestCodedResult.srvc"
            });
        }

        this.getTestCodedResultMappingsByResult = function (testResult) {
            return $http({
                data: JSON.stringify(testResult),
                method: "POST",
                url: config.server + config.api_path + "getTestCodedResultMappingsByResult.srvc"
            });
        }

        this.saveTestCodedResultMappings = function (mappings) {
            return $http({
                data: JSON.stringify(mappings),
                method: "POST",
                url: config.server + config.api_path + "saveTestCodedResultMappings.srvc"
            });
        };

    }]);
});
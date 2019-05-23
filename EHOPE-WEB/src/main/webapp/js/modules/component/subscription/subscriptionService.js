define(['app', 'util'], function (app, util) {
    'use strict';
    app.service('subscriptionService', function () {
        this.getPlanList = function (data) {
            return util.createApiRequest("getPlanList.srvc", JSON.stringify(data));
        };
        this.createTenantSubscription = function (data) {
            var payLoad = new FormData();
            if (data.logo !== undefined) {
                payLoad.append("logo", data.logo);
            }
            payLoad.append("subscriptionWrapper", JSON.stringify(data.subscriptionWrapper));
            var custom = {
                transformRequest: angular.identity,
                headers: {
                    'Content-Type': undefined
                }
            };
            return util.createApiRequest("createTenantSubscription.srvc", payLoad, custom);
        };
        this.executeTenantSubscription = function (data) {
            return util.createApiRequest("executeTenantSubscription.srvc", JSON.stringify(data));
        };

    });
});

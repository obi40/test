define(['app', 'util'], function (app, util) {
    'use strict';
    app.service('priceListDetailsService', function () {
        this.getMasterItemPriceListByTest = function (data) {
            return util.createApiRequest("getMasterItemPriceListByTest.srvc", JSON.stringify(data));
        };
    });
});

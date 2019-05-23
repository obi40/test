define(['app', 'util'], function (app, util) {
    'use strict';
    app.service('priceListService', function () {

        this.getBillPriceLists = function () {
            // because of createLovFilter
            return util.createApiRequest("getBillPriceLists.srvc").then(function (response) {
                return response.data;
            });
        };

        this.getDefaultBillPriceList = function () {
            return util.createApiRequest("getDefaultBillPriceList.srvc");
        };

        this.createBillPriceList = function (data) {
            return util.createApiRequest("createBillPriceList.srvc", JSON.stringify(data));
        };

        this.updateBillPriceList = function (data) {
            return util.createApiRequest("updateBillPriceList.srvc", JSON.stringify(data));
        };

        this.deleteBillPriceList = function (data) {
            return util.createApiRequest("deleteBillPriceList.srvc", JSON.stringify(data));
        };
    });
});

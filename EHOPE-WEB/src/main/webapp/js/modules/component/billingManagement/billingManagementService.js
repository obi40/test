define(['app', 'config'], function (app, config) {
    'use strict';
    app.service('billingManagementService', ["$http", function ($http) {
        //#region billClassification controllers
        this.getBillClassificationList = function (e) {
            return $http({
                data: JSON.stringify(e.data),
                method: "POST",
                url: config.server + config.api_path + "getBillClassificationList.srvc"
            });
        }

        this.getParentBillClassifications = function (rid) {
            return $http({
                data: JSON.stringify(rid),
                method: "POST",
                url: config.server + config.api_path + "getParentBillClassifications.srvc"
            });
        }

        this.addBillClassification = function (e) {
            return $http({
                data: JSON.stringify(e.data),
                method: "POST",
                url: config.server + config.api_path + "addBillClassification.srvc"
            });
        }

        this.editBillClassification = function (e) {
            return $http({
                data: JSON.stringify(e.data),
                method: "POST",
                url: config.server + config.api_path + "editBillClassification.srvc"
            });
        }

        this.activateBillClassification = function (rid) {
            return $http({
                data: JSON.stringify(rid),
                method: "POST",
                url: config.server + config.api_path + "activateBillClassification.srvc"
            });
        }

        this.deactivateBillClassification = function (rid) {
            return $http({
                data: JSON.stringify(rid),
                method: "POST",
                url: config.server + config.api_path + "deactivateBillClassification.srvc"
            });
        }

        // for insurance provider page
        this.filterBillClassificationList = function (searchValue) {
            return $http({
                data: JSON.stringify(searchValue),
                method: "POST",
                url: config.server + config.api_path + "filterBillClassificationList.srvc"
            });
        }

        //#endregion

        //#region billMasterItem controllers
        this.getBillMasterItemList = function (e) {
            return $http({
                data: JSON.stringify(e.data),
                method: "POST",
                url: config.server + config.api_path + "getBillMasterItemList.srvc"
            });
        }

        this.addBillMasterItem = function (e) {
            return $http({
                data: JSON.stringify(e.data),
                method: "POST",
                url: config.server + config.api_path + "addBillMasterItem.srvc"
            });
        }

        this.editBillMasterItem = function (e) {
            return $http({
                data: JSON.stringify(e.data),
                method: "POST",
                url: config.server + config.api_path + "editBillMasterItem.srvc"
            });
        }

        this.activateBillMasterItem = function (rid) {
            return $http({
                data: JSON.stringify(rid),
                method: "POST",
                url: config.server + config.api_path + "activateBillMasterItem.srvc"
            });
        }

        this.deactivateBillMasterItem = function (rid) {
            return $http({
                data: JSON.stringify(rid),
                method: "POST",
                url: config.server + config.api_path + "deactivateBillMasterItem.srvc"
            });
        }

        this.saveBillTestItems = function (masterItem) {
            return $http({
                data: JSON.stringify(masterItem),
                method: "POST",
                url: config.server + config.api_path + "saveBillTestItems.srvc"
            });
        }

        // for insurance provider page
        this.filterBillMasterItemList = function (searchValue) {
            return $http({
                data: JSON.stringify(searchValue),
                method: "POST",
                url: config.server + config.api_path + "filterBillMasterItemList.srvc"
            });
        }

        //#endregion


        //#region billPricing controllers
        this.getBillPricingList = function (e) {
            return $http({
                data: JSON.stringify(e.data),
                method: "POST",
                url: config.server + config.api_path + "getBillPricingList.srvc"
            });
        }

        this.addBillPricing = function (e) {
            return $http({
                data: JSON.stringify(e.data),
                method: "POST",
                url: config.server + config.api_path + "addBillPricing.srvc"
            });
        }
        this.addBillPricings = function (billPricings) {
            return $http({
                data: JSON.stringify(billPricings),
                method: "POST",
                url: config.server + config.api_path + "addBillPricings.srvc"
            });
        }

        this.editBillPricing = function (e) {
            return $http({
                data: JSON.stringify(e.data),
                method: "POST",
                url: config.server + config.api_path + "editBillPricing.srvc"
            });
        }
        //#endregion

    }]);
});

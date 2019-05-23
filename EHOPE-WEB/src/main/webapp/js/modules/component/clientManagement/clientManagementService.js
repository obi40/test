define(['app', 'config', 'util', 'commonData'], function (app, config, util, commonData) {
    'use strict';
    app.service('clientManagementService', ["$http", function ($http) {

        //provider
        this.getInsParentProviderList = function () {
            return util.createApiRequest("getInsParentProviderList.srvc");
        };

        this.getAllClientsList = function () {
            return util.createApiRequest("getAllClientsList.srvc");

        };

        this.getInsProviderList = function (onlyParent) {
            return util.createApiRequest("getInsProviderList.srvc").then(function (response) {
                var data = response.data;
                var result = [];
                for (var idx = 0; idx < data.length; idx++) {
                    var ins = data[idx];
                    var insuranceTypeCode = ins.insuranceType.code;
                    var customLabel = ins.name[util.userLocale];
                    if (ins.parentProvider != null) {
                        customLabel = ins.parentProvider.name[util.userLocale] + commonData.arrow + customLabel;
                    }
                    ins["customLabel"] = customLabel;

                    if (onlyParent) {
                        if (ins.parentProvider == null &&
                            (insuranceTypeCode == "TPA" || insuranceTypeCode == "LAB_NETWORK" || insuranceTypeCode == "SELF_FUNDED")) {
                            result.push(ins);
                        }
                    } else {
                        if (ins.parentProvider == null &&
                            (insuranceTypeCode == "TPA" || insuranceTypeCode == "LAB_NETWORK" || insuranceTypeCode == "SELF_FUNDED")) {
                            continue
                        }
                        result.push(ins);
                    }

                }
                return result;
            });
        };

        this.getProviderWithPlanListByPatient = function (patient) {
            return util.createApiRequest("getProviderWithPlanListByPatient.srvc", JSON.stringify(patient));
        };
        this.createInsProvider = function (data) {
            return util.createApiRequest("createInsProvider.srvc", JSON.stringify(data));
        };

        this.updateInsProvider = function (data) {
            return util.createApiRequest("updateInsProvider.srvc", JSON.stringify(data));
        };

        this.activateInsProvider = function (data) {
            return util.createApiRequest("activateInsProvider.srvc", JSON.stringify(data));
        };
        this.deactivateInsProvider = function (data) {
            return util.createApiRequest("deactivateInsProvider.srvc", JSON.stringify(data));
        };

        this.getInsProviderPlanListByProvider = function (insProvider) {
            return util.createApiRequest("getInsProviderPlanListByProvider.srvc", JSON.stringify(insProvider));
        };

        this.getInsProviderPlanList = function () {
            return $http({
                method: "POST",
                url: config.server + config.api_path + "getInsProviderPlanList.srvc"
            }).then(function (response) {
                return response.data;
            });
        };

        //plan
        this.createInsProviderPlan = function (data) {
            return util.createApiRequest("createInsProviderPlan.srvc", JSON.stringify(data));
        };

        this.updateInsProviderPlan = function (data) {
            return util.createApiRequest("updateInsProviderPlan.srvc", JSON.stringify(data));
        };

        this.deleteInsProviderPlan = function (data) {
            return util.createApiRequest("deleteInsProviderPlan.srvc", JSON.stringify(data));
        };

        //coverage
        this.getInsCoverageDetailListByPlan = function (data) {
            return util.createApiRequest("getInsCoverageDetailListByPlan.srvc", JSON.stringify(data));
        };

        this.createInsCoverageDetail = function (data) {
            return util.createApiRequest("createInsCoverageDetail.srvc", JSON.stringify(data));
        };

        this.updateInsCoverageDetail = function (data) {
            return util.createApiRequest("updateInsCoverageDetail.srvc", JSON.stringify(data));
        };

        this.deleteInsCoverageDetail = function (data) {
            return util.createApiRequest("deleteInsCoverageDetail.srvc", JSON.stringify(data));
        };

        //clients (for referral)
        this.getClientList = function (data) {
            return util.createApiRequest("getClientList.srvc", JSON.stringify(data));
        }

    }]);
});

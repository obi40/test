define(['app', 'util', 'commonData'], function (app, util, commonData) {
    'use strict';
    app.controller('subscriptionCtrl', [
        '$scope',
        'subscriptionService',
        'commonMethods',
        'loginService',
        'branchFormService',
        'serialFormService',
        'systemMessagesService',
        '$window',
        '$location',
        'WizardHandler',
        '$state',
        function (
            $scope,
            subscriptionService,
            commonMethods,
            loginService,
            branchFormService,
            serialFormService,
            systemMessagesService,
            $window,
            $location,
            WizardHandler,
            $state
        ) {
            //NEED A REVISIT
            util.fullWebsiteView($scope);
            $scope.langSwitcherOptions = {};
            $scope.planList = [];
            $scope.newTenant = {};
            $scope.tenantMetaData = null;
            $scope.lkpCountryOptions = null;
            $scope.lkpCityOptions = null;
            $scope.selectedPlan = null;
            $scope.createdPayment = null;
            $scope.planTotal = 0;
            $scope.countryCityOptions = null;
            $scope.supportedLanguages = null;
            $scope.tenantLanguages = [];
            $scope.branches = [];
            $scope.serials = [];
            $scope.serialsFormOptions = {
                valid: false,
                serials: $scope.serials,
                tenantRid: null,
                singleSubmit: false
            };
            $scope.fileDataWrapper = {
                oldFile: null,
                fileModel: null, // must be null
                types: ["jpg", "jpeg", "png"],
                labelCode: "logo"
            };
            $scope.branchFormOptions = {};
            $scope.userLocale = util.userLocale;
            $scope.$watch(function () { return util.userLocale }, function (newVal, oldVal) {
                if (newVal != null) {
                    $scope.userLocale = newVal;
                }
            });
            var branchesDummyRid = -1;
            var tenant = util.getStorageProperty("locale", "tenant");
            var plan = util.getStorageProperty("locale", "tenantPlan");
            var paypalToken = $location.search().token;
            if (paypalToken != null && tenant != null && plan != null) {
                $scope.serialsFormOptions.tenantRid = tenant.rid;
                loginService.generateTenantOnboardingToken(tenant.rid).then(function (response) {
                    util.token = response.data;
                    var wrapper = {
                        token: paypalToken,
                        tenant: tenant,
                        plan: plan
                    };
                    subscriptionService.executeTenantSubscription(wrapper).then(function (response) {
                        if (response.data != null) {
                            $location.search("token", null);//remove token from url
                            util.clearStorageProperty("locale", "tenant");
                            util.clearStorageProperty("locale", "tenantPlan");
                            $scope.newTenant = response.data.tenant;
                            $scope.agreement = JSON.stringify(response.data.agreement);
                            subscriptionService.getTenantData($scope.newTenant.rid).then(function (response) {
                                $scope.newTenant = response.data;
                                $scope.fileDataWrapper.oldFile = $scope.newTenant.logo;
                                prepareBranches();
                                WizardHandler.wizard().goTo(2);//Setup step
                            });
                        }
                    }).catch(function () {
                        WizardHandler.wizard().goTo(1);
                    });

                    prepareController();
                    systemMessagesService.getTenantLanguages().then(function (response) {
                        $scope.tenantLanguages = response.data;
                        util.prepareLanguages(response.data);
                    });

                });

            } else {
                util.clearUtilData();
                loginService.generateDummyToken().then(function (response) {
                    util.token = response.data;
                    prepareController();
                    var wrapper = {
                        filters: []
                    };
                    subscriptionService.getPlanList(wrapper).then(function (response) {
                        $scope.planList = response.data;
                        for (var idx = 0; idx < $scope.planList.length; idx++) {
                            //sort by name
                            $scope.planList[idx].planFieldList.sort(function (a, b) {
                                var codeA = a.lkpPlanFieldType.code.toUpperCase(); // ignore upper and lowercase
                                var codeB = b.lkpPlanFieldType.code.toUpperCase(); // ignore upper and lowercase
                                if (codeA < codeB) {
                                    return -1;
                                }
                                if (codeA > codeB) {
                                    return 1;
                                }
                                return 0;
                            });
                        }
                    });
                });
            }
            function prepareController() {
                commonMethods.retrieveMetaData("SecTenant").then(function (response) {
                    $scope.tenantMetaData = response.data;
                    $scope.countryCityOptions = {
                        country: {
                            name: $scope.tenantMetaData.country.name,
                            required: $scope.tenantMetaData.country.notNull
                        },
                        city: {
                            name: $scope.tenantMetaData.city.name,
                            required: $scope.tenantMetaData.city.notNull
                        }
                    };
                });
                systemMessagesService.getSupportedLanguages().then(function (response) {
                    $scope.supportedLanguages = [];
                    for (var idx = 0; idx < response.data.length; idx++) {
                        var obj = {
                            isPrimary: false,
                            comLanguage: response.data[idx]
                        }
                        $scope.supportedLanguages.push(obj);
                    }
                });
            }
            $scope.customPlanListener = function () {
                $scope.planTotal = 0;
                for (var idx = 0; idx < $scope.selectedPlan.planFieldList.length; idx++) {
                    if ($scope.selectedPlan.planFieldList[idx].amount == null) {
                        continue;
                    }
                    var total = $scope.selectedPlan.planFieldList[idx].price * $scope.selectedPlan.planFieldList[idx].amount;
                    $scope.planTotal += total;
                }
            };
            $scope.planListener = function (plan) {
                $scope.selectedPlan = plan;
            };
            $scope.createTenantSubscription = function (invalid) {

                if (invalid) {
                    return;
                }
                // already created a tenant
                if ($scope.newTenant != null && $scope.newTenant.rid != null) {
                    systemMessagesService.setTenantLanguages($scope.tenantLanguages).then(function () {
                        systemMessagesService.getTenantLanguages().then(function (response) {
                            $scope.tenantLanguages = response.data;
                            util.prepareLanguages(response.data);
                            subscriptionService.updateTenant($scope.newTenant).then(function () {
                                subscriptionService.getTenantData($scope.newTenant.rid).then(function (response) {
                                    $scope.newTenant = response.data;
                                });
                            });
                        });
                    });
                } else {
                    util.setStorageProperty("locale", "tenantPlan", $scope.selectedPlan);
                    var payload = {
                        logo: $scope.fileDataWrapper.fileModel,
                        subscriptionWrapper: {
                            tenant: $scope.newTenant,
                            plan: $scope.selectedPlan,
                            planFieldList: $scope.selectedPlan.planFieldList,
                            tenantLangauges: $scope.tenantLanguages
                        }
                    }
                    subscriptionService.createTenantSubscription(payload).then(function (response) {
                        $scope.fileDataWrapper.fileModel = undefined;
                        util.setStorageProperty("locale", "tenant", response.data.tenant);
                        if (response.data.approvalUrl != null) {
                            $window.location.href = response.data.approvalUrl;
                        }
                    }).catch(function (response) {
                        util.createToast(response.data.code, "error");
                        WizardHandler.wizard().goTo(1);//Registration step
                    });
                }


            };
            $scope.tenantLanguagesListener = function (chip) {
                for (var idx = 0; idx < $scope.tenantLanguages.length; idx++) {
                    if (chip.comLanguage.locale != $scope.tenantLanguages[idx].comLanguage.locale) {
                        $scope.tenantLanguages[idx].isPrimary = false;
                    }
                }
            };
            $scope.transferTenantLanguages = function (chip) {
                for (var idx = 0; idx < $scope.tenantLanguages.length; idx++) {
                    if (chip.comLanguage.locale == $scope.tenantLanguages[idx].comLanguage.locale) {
                        return;
                    }
                }
                chip.isPrimary = false;
                if ($scope.tenantLanguages.length == 0) {
                    chip.isPrimary = true;
                }
                $scope.tenantLanguages.push(chip);
            };
            $scope.onTenantLanguagesRemove = function () {
                for (var idx = 0; idx < $scope.tenantLanguages.length; idx++) {
                    if ($scope.tenantLanguages[idx].isPrimary) {
                        return;
                    }
                }
                if ($scope.tenantLanguages.length > 0) {
                    $scope.tenantLanguages[0].isPrimary = true;
                }
            };
            function updateTenantBranchesCount() {
                subscriptionService.updateTenant($scope.newTenant).then(function () {
                    subscriptionService.getTenantData($scope.newTenant.rid).then(function (response) {
                        $scope.newTenant = response.data;
                    });
                });
            }
            $scope.submitBranches = function () {
                var tenantBranches = angular.copy($scope.branches);
                var toUpdateBranches = [];
                for (var idx = tenantBranches.length - 1; idx >= 0; idx--) {
                    if (tenantBranches[idx].rid < 0) {
                        delete tenantBranches[idx].rid;
                    } else {
                        toUpdateBranches.push(tenantBranches[idx]);
                        tenantBranches.splice(idx, 1);
                    }
                }
                updateTenantBranchesCount();
                branchFormService.createBranch(tenantBranches).then(function () {
                    $scope.branches = [];
                    if (toUpdateBranches.length > 0) {
                        branchFormService.updateBranch(toUpdateBranches).then(function () {
                            prepareBranches();
                        });
                    } else {
                        prepareBranches();
                    }
                }).catch(function () {
                    WizardHandler.wizard().goTo(2);//Branch step
                });
            };
            $scope.addDummyBranch = function () {
                $scope.branches.push({
                    rid: branchesDummyRid,
                    isActive: true,
                    country: null,
                    city: null
                });
                --branchesDummyRid;
            };
            $scope.removeBranch = function (branch) {
                for (var idx = 0; idx < $scope.branches.length; idx++) {
                    if ($scope.branches[idx].rid == branch.rid) {
                        $scope.branches.splice(idx, 1);
                        break;
                    }
                }
                if (branch.rid > 0) {
                    updateTenantBranchesCount();
                    return branchFormService.deleteBranch(branch.rid).then(function () {
                    });
                }

            };
            function prepareBranches() {
                branchFormService.getBranches().then(function (response) {
                    if (response.data.length > 0) {
                        $scope.branches = response.data;
                    }
                });
            }

            $scope.submitSerials = function () {
                if ($scope.serialsFormOptions.isInvalid()) {
                    return;
                }

                var serials = angular.copy($scope.serialsFormOptions.serials);
                for (var idx = 0; idx < serials.length; idx++) {
                    delete serials[idx].rid;
                    delete serials[idx].tenantId;
                    delete serials[idx].version;
                }
                serialFormService.createSerials(serials).then(function () {
                    util.clearUtilData();
                    util.createToast(util.systemMessages.success, "success");
                    $state.go("login");
                }).catch(function () {
                    WizardHandler.wizard().goTo(3);//Branch step
                });
            };
        }
    ]);
});

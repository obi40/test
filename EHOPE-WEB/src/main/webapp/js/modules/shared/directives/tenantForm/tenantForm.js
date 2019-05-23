define(['app', 'config', 'util'], function (app, config, util) {
    'use strict';
    app.directive('tenantForm', function () {
        return {
            restrict: 'E',
            replace: false,
            scope: {
                tenant: "=tenant",
                options: "=options"
            },
            templateUrl: "./" + config.lisDir + "/modules/shared/directives/tenantForm/tenant-form-view.html",
            controller: ['$scope', 'commonMethods', 'systemMessagesService', 'lovService',
                function ($scope, commonMethods, systemMessagesService, lovService) {
                    $scope.userLocale = util.userLocale;
                    $scope.metaData = null;
                    $scope.countryCityOptions = null;
                    $scope.availableLanguages = null;
                    $scope.options.tenantLanguages = null;
                    $scope.dummyMobileNumber = null;
                    $scope.printFormatLkp = null;
                    $scope.options.fileDataWrapper = { //logo
                        oldFile: $scope.tenant.logo != null ? $scope.tenant.logo : null,
                        fileModel: $scope.tenant.logo != null ? $scope.tenant.logo : null,
                        types: ["jpg", "jpeg", "png"],
                        labelCode: "logo"
                    };

                    $scope.options.headerFileDataWrapper = {
                        oldFile: $scope.tenant.headerImage != null ? $scope.tenant.headerImage : null,
                        fileModel: $scope.tenant.headerImage != null ? $scope.tenant.headerImage : null,
                        types: ["jpg", "jpeg", "png"],
                        labelCode: "header"
                    };

                    $scope.options.footerFileDataWrapper = {
                        oldFile: $scope.tenant.footerImage != null ? $scope.tenant.footerImage : null,
                        fileModel: $scope.tenant.footerImage != null ? $scope.tenant.footerImage : null,
                        types: ["jpg", "jpeg", "png"],
                        labelCode: "footer"
                    };

                    systemMessagesService.getTenantLanguages()
                        .then(function (response) {
                            $scope.options.tenantLanguages = response.data;
                            systemMessagesService.getSupportedLanguages()
                                .then(function (response) {
                                    $scope.availableLanguages = [];
                                    OUTER: for (var idx = 0; idx < response.data.length; idx++) {
                                        for (var i = 0; i < $scope.options.tenantLanguages.length; i++) {
                                            if ($scope.options.tenantLanguages[i].comLanguage.rid == response.data[idx].rid) {
                                                continue OUTER;
                                            }
                                        }
                                        var obj = {
                                            isPrimary: false,
                                            comLanguage: response.data[idx]
                                        };
                                        $scope.availableLanguages.push(obj);
                                    }
                                });
                        });
                    $scope.onMobilePatternChange = function () {
                        $scope.dummyMobileNumber = null;
                    };
                    function onCountryChange(selectedCountry) {
                        if (selectedCountry == null || $scope.tenant == null) {
                            return;
                        }
                        if ($scope.tenant.mobilePattern == null || $scope.tenant.mobilePattern == "") {
                            //appending a hash so the ui-mask can work otherwise it wont recognize the phoneCode alone as a regex
                            $scope.tenant.mobilePattern = selectedCountry.phoneCode + "#";
                        }
                    }
                    commonMethods.retrieveMetaData("SecTenant").then(function (response) {
                        $scope.metaData = response.data;
                        $scope.countryCityOptions = {
                            country: {
                                name: $scope.metaData.country.name,
                                required: $scope.metaData.country.notNull,
                                onChange: onCountryChange
                            },
                            city: {
                                name: $scope.metaData.city.name,
                                required: $scope.metaData.city.notNull
                            }
                        };
                        $scope.printFormatLkp = {
                            className: "LkpPrintFormat",
                            name: $scope.metaData.printFormat.name,
                            labelText: "printFormat",
                            valueField: "name." + util.userLocale,
                            selectedValue: $scope.tenant.printFormat,
                            required: $scope.metaData.printFormat.notNull
                        };
                    });

                    $scope.tenantLanguagesListener = function (chip, field) {
                        for (var idx = 0; idx < $scope.options.tenantLanguages.length; idx++) {
                            if (chip.comLanguage.locale !== $scope.options.tenantLanguages[idx].comLanguage.locale) {
                                $scope.options.tenantLanguages[idx][field] = false;
                            }
                        }
                    };

                    $scope.transferTenantLanguages = function (chip) {
                        chip.isPrimary = false;
                        chip.isNamePrimary = false;
                        if ($scope.options.tenantLanguages.length === 0) {
                            chip.isPrimary = true;
                            chip.isNamePrimary = true;
                        }
                        $scope.options.tenantLanguages.push(chip);
                        for (var idx = 0; idx < $scope.availableLanguages.length; idx++) {
                            if (chip.comLanguage.rid == $scope.availableLanguages[idx].comLanguage.rid) {
                                $scope.availableLanguages.splice(idx, 1);
                                break;
                            }
                        }
                    };
                    $scope.onTenantLanguagesRemove = function (chip) {
                        $scope.availableLanguages.push(chip);
                        if ($scope.options.tenantLanguages.length > 0) {
                            if (chip.isPrimary) {
                                $scope.options.tenantLanguages[0].isPrimary = true;
                            }
                            if (chip.isNamePrimary) {
                                $scope.options.tenantLanguages[0].isNamePrimary = true;
                            }
                        }
                    };
                    $scope.options["clear"] = function () {
                        $scope.tenant = null;
                        $scope.options.tenantLanguages = [];
                        $scope.options.tenantForm.$setPristine();
                        $scope.options.tenantForm.$setUntouched();
                    };
                    $scope.options["isFormInvalid"] = function () {
                        return $scope.options.tenantForm.$invalid ||
                            (!$scope.options.tenantLanguages || $scope.options.tenantLanguages.length === 0);
                    };


                    $scope.options["getTenant"] = function () {
                        //country and city's directive sets the selected value automatically in the tenant object
                        $scope.printFormatLkp.assignValues($scope.tenant, [$scope.printFormatLkp]);
                        return $scope.tenant;
                    };

                    $scope.options["setTenantLkps"] = function (tenant) {
                        //country and city's directive sets the selected value automatically in the tenant object
                        $scope.printFormatLkp.selectedValue = tenant.printFormat;
                    };

                }]
        }
    });
});
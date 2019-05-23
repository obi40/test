define(['app', 'util', 'config', 'commonData'], function (app, util, config, commonData) {
    'use strict';
    /**
     * Tenant serial configurations. Call prepareSerials() when done creating or updating.
     * 1-options:
     *  a.serials: initial empty array.
     */
    app.directive('serialForm', function () {
        return {
            restrict: 'E',
            replace: false,
            templateUrl: "./" + config.lisDir + "/modules/shared/directives/serialForm/serial-form-view.html",
            scope: {
                options: "=options"
            },
            controller: ['$scope', 'commonMethods', 'serialFormService', 'lovService', 'branchFormService', '$q',
                function ($scope, commonMethods, serialFormService, lovService, branchFormService, $q) {
                    var serialFormatLkpData = [];
                    var allSerials = [];
                    $scope.branchLkp = null;
                    $scope.options.serials = [];
                    $scope.options["isInvalid"] = function () {
                        return $scope.masterForm.$invalid;
                    };
                    $scope.populateExamples = function (serialFormat, serial) {
                        if (serial.currentValue == null || serial.currentValue < 0) {
                            return;
                        }
                        serial.serialFormat = serialFormat;//set value
                        serial.examples = [];//reset
                        var year = new Date();
                        year = year.getFullYear().toString();
                        year = year.slice(-2);//2019->19
                        var dummyId = 0;
                        for (var i = 0; i < 3; i++) {
                            var obj = { id: --dummyId, label: "" };
                            var currentValue = (i + serial.currentValue).toString();
                            var delimiter = serial.delimiter != null && serial.delimiter.length > 0 ? serial.delimiter.toString() : "";
                            if (serial.filler != null && serial.filler > 0) {
                                while (currentValue.length < serial.filler) {
                                    currentValue = "0" + currentValue;
                                }
                            }
                            if (serialFormat.code == "SERIAL") {
                                obj.label = "" + currentValue;
                            } else if (serialFormat.code == "ANNUAL") {
                                obj.label = year + delimiter + currentValue;
                            } else if (serialFormat.code == "LOCATION") {
                                obj.label = util.user.tenant.code + delimiter;
                                if (serial.labBranch) {
                                    obj.label += serial.labBranch.code + delimiter;
                                }
                                obj.label += currentValue;
                            }
                            serial.examples.push(obj);
                        }
                    };
                    $scope.options["prepareSerials"] = function () {
                        serialFormService.getSerialsData().then(function (response) {
                            allSerials = response.data;
                            $scope.onBranchLkpChange($scope.branchLkp.selectedValue);
                        });
                    };
                    $scope.onBranchLkpChange = function (selectedBranch) {
                        if (!allSerials || allSerials.length === 0 || !selectedBranch) {
                            return;
                        }
                        $scope.options.serials = angular.copy(allSerials[selectedBranch.rid]);
                        for (var idx = 0; idx < $scope.options.serials.length; idx++) {
                            var lkpData = angular.copy(serialFormatLkpData);
                            $scope.options.serials[idx].label = $scope.options.serials[idx].serialType.name[util.userLocale];
                            $scope.options.serials[idx].serialFormatLov = {// each serial has its own copy of the lov
                                className: "LkpSerialFormat",
                                name: $scope.serialMetaData.serialFormat.name,
                                labelText: "serialFormat",
                                valueField: "name." + util.userLocale,
                                selectedValue: $scope.options.serials[idx].serialFormat,
                                required: $scope.serialMetaData.serialFormat.notNull,
                                data: lkpData
                            };
                            $scope.populateExamples($scope.options.serials[idx].serialFormat, $scope.options.serials[idx]);
                        }
                    };

                    $q.all([
                        lovService.getLkpByClass({ className: "LkpSerialFormat" }),
                        commonMethods.retrieveMetaData("SysSerial"),
                        branchFormService.getLabBranchList({ filters: [] })
                    ]).then(function (response) {
                        serialFormatLkpData = response[0];
                        $scope.serialMetaData = response[1].data;
                        var branches = response[2].data;
                        var tenant = { rid: -1, name: {} };
                        tenant.name[util.userLocale] = util.user.tenant.name;
                        branches.splice(0, 0, tenant);
                        $scope.branchLkp = {
                            className: "Branch",
                            name: "branch",
                            labelText: util.systemMessages.scope,
                            valueField: "name." + util.userLocale,
                            selectedValue: tenant,
                            required: true,
                            data: branches
                        };
                        $scope.options.prepareSerials();
                    });
                }]
        }
    });
});




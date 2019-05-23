define(['app', 'util', 'config', 'commonData'], function (app, util, config, commonData) {
    'use strict';
    app.service('patientProfileService', ["$mdDialog", function ($mdDialog) {
        //#region patient visit
        this.getPatientInfo = function (rid) {
            return util.createApiRequest("getPatientInfo.srvc", JSON.stringify(rid));
        };

        this.getVisitPageByPatient = function (data) {
            return util.createApiRequest("getVisitPageByPatient.srvc", JSON.stringify(data));
        };

        this.getVisitPage = function (data) {
            return util.createApiRequest("getVisitPage.srvc", JSON.stringify(data));
        };

        this.getActualTestResultsByVisit = function (visit) {
            return util.createApiRequest("getActualTestResultsByVisit.srvc", JSON.stringify(visit));
        };

        this.editActualTestResults = function (data) {
            return util.createApiRequest("editActualTestResults.srvc", JSON.stringify(data));
        };

        //#endregion

        //#region patient insurance
        this.getPatientInsuranceList = function (e) {
            return util.createApiRequest("getPatientInsuranceList.srvc", JSON.stringify(e.data));
        };

        this.activatePatientInsurance = function (rid) {
            return util.createApiRequest("activatePatientInsurance.srvc", rid);
        };

        this.deactivatePatientInsurance = function (rid) {
            return util.createApiRequest("deactivatePatientInsurance.srvc", rid);
        };
        this.triggerDefaultInsurance = function (data) {
            return util.createApiRequest("triggerDefaultPatientInsurance.srvc", JSON.stringify(data));
        }
        //#endregion        

        //#region historical data

        this.getHistoricalOrderPage = function (data) {
            return util.createApiRequest("getHistoricalOrderPage.srvc", JSON.stringify(data));
        };

        this.getHistoricalTestPage = function (data) {
            return util.createApiRequest("getHistoricalTestPage.srvc", JSON.stringify(data));
        };

        this.getHistoricalResultPage = function (data) {
            return util.createApiRequest("getHistoricalResultPage.srvc", JSON.stringify(data));
        };

        //#endregion

        this.printInvoiceReport = function (data) {
            return util.createApiRequest("generateInvoiceReport.srvc", JSON.stringify(data), { responseType: "blob" });
        };

        this.printInsuranceInvoiceReport = function (data) {
            return util.createApiRequest("generateInsuranceInvoiceReport.srvc", JSON.stringify(data), { responseType: "blob" });
        };



        this.resultsReportDialog = function (visitRid) {
            util.createApiRequest("getResultReportDialogData.srvc", JSON.stringify(visitRid)).then(function (response) {
                var fetchedVisit = response.data;
                $mdDialog.show({
                    controller: ["$scope", "$mdDialog", "patientProfileService", function ($scope, $mdDialog, patientProfileService) {
                        $scope.visit = fetchedVisit;
                        $scope.smsUrl = null;
                        $scope.tenantEmailHistory = null;
                        var isNewTenantHistoryEmail = false;
                        $scope.userLocale = util.userLocale;
                        $scope.selectedTests = [];
                        $scope.isSendEmailDisabled = true;
                        $scope.visit.emrPatientInfo["isEmail"] = false;
                        $scope.visit.emrPatientInfo["isSMS"] = false;
                        $scope.visit.emrPatientInfo["type"] = "PATIENT";
                        $scope.patDoc = [$scope.visit.emrPatientInfo];
                        if ($scope.visit.doctor) {
                            $scope.visit.doctor["isEmail"] = false;
                            $scope.visit.doctor["isSMS"] = false;
                            $scope.visit.doctor["type"] = "DOCTOR";
                            $scope.patDoc.push($scope.visit.doctor);
                        }

                        $scope.tenantEmailHistoryOptions = {
                            service: function (filterablePageRequest) {
                                return util.createApiRequest("getTenantEmailHistoryPage.srvc", JSON.stringify(filterablePageRequest));
                            },
                            callback: function (filters) {
                                if (!filters || filters.length === 0) {
                                    $scope.tenantEmailHistory = null;
                                    isNewTenantHistoryEmail = false;
                                    $scope.isEmailSMSListener();
                                    return;
                                }
                                $scope.tenantEmailHistory = $scope.tenantEmailHistoryOptions.searchText.toLowerCase();
                                if (filters[0]["field"] === "rid") {
                                    isNewTenantHistoryEmail = false;
                                } else {
                                    isNewTenantHistoryEmail = true;
                                }
                                $scope.isEmailSMSListener();
                            },
                            skeleton: { code: "email", description: "email" },
                            filterList: ["email"],
                            sortList: [{ direction: "ASC", property: "email" }],
                            label: "customEmail"
                        };
                        // $scope.$watch("tenantEmailHistory", function (newVal, oldVal) {
                        //     console.log($scope.tenantEmailHistory);
                        //     console.log(isNewTenantHistoryEmail);
                        // });
                        //toggle isNewTenantHistoryEmail in case user kept writing and didnt click on any record from search results
                        $scope.$watch("tenantEmailHistoryOptions.searchText", function (newVal, oldVal) {
                            if ($scope.tenantEmailHistoryOptions.searchText) {
                                $scope.tenantEmailHistory = $scope.tenantEmailHistoryOptions.searchText;
                                if ($scope.tenantEmailHistoryOptions.selectedItem &&
                                    $scope.tenantEmailHistoryOptions.selectedItem.rid &&
                                    $scope.tenantEmailHistoryOptions.selectedItem.email === $scope.tenantEmailHistory) {
                                    isNewTenantHistoryEmail = false;
                                } else {
                                    isNewTenantHistoryEmail = true;
                                }
                            } else {
                                $scope.tenantEmailHistory = null;
                                isNewTenantHistoryEmail = false;
                            }
                            $scope.isEmailSMSListener();
                        });
                        $scope.testsGridOptions = {
                            columns: [
                                {
                                    selectable: true,
                                    width: "50px"
                                },
                                {
                                    field: "standardCode",
                                    title: util.systemMessages.standardCode,
                                    template: function (dataItem) {
                                        return dataItem.testDefinition.standardCode;
                                    }
                                },
                                {
                                    field: "description",
                                    title: util.systemMessages.description,
                                    template: function (dataItem) {
                                        return dataItem.testDefinition.description;
                                    }
                                },
                                {
                                    field: "isPrintPrevious",
                                    title: util.systemMessages.printPreviousResults,
                                    template: function (dataItem) {
                                        var name = "isPrintPrevious_" + dataItem.rid;
                                        $scope[name] = dataItem.isPrintPrevious;
                                        return '<div id="' + name + '" class="text-center" >' +
                                            '<md-checkbox ng-model="' + name + '" ng-change="printPreviousListener(' + name + ',' + dataItem.rid + ')" aria-label="util.systemMessages.isApproved"></md-checkbox>'
                                            + '</div>';

                                    }
                                }
                            ],
                            dataSource: new kendo.data.DataSource({
                                pageSize: config.gridPageSizes[0],
                                page: 1,
                                transport: {
                                    read: function (e) {
                                        var tests = [];
                                        for (var idx = 0; idx < $scope.visit.labSamples.length; idx++) {
                                            for (var i = 0; i < $scope.visit.labSamples[idx].labTestActualSet.length; i++) {
                                                var t = $scope.visit.labSamples[idx].labTestActualSet[i];
                                                t["isPrintPrevious"] = true;
                                                tests.push(t);
                                            }
                                        }
                                        e.success(tests);
                                    }
                                },
                                serverPaging: false,
                                serverFiltering: false,
                                schema: {
                                    model: {
                                        id: "rid",
                                        fields: {
                                            "rid": { type: "number", editable: false },
                                            "standardCode": { type: "string", editable: false },
                                            "description": { type: "string", editable: false },
                                            "isPrintPrevious": { type: "boolean", editable: false }
                                        }
                                    }
                                }
                            }),
                            editable: false,
                            selectable: false,
                            persistSelection: true,
                            dataBound: function (e) {
                                //select all rows
                                var gridData = e.sender.dataSource.data();
                                for (var idx = 0; idx < gridData.length; idx++) {
                                    var item = gridData[idx];
                                    var row = e.sender.element.find("tr[data-uid='" + item.uid + "']");
                                    var checkBox = row.find("input[type=checkbox]");
                                    checkBox.trigger("click");
                                }
                            },
                            change: function () {
                                $scope.selectedTests = this.selectedKeyNames().map(Number);
                                $scope.isEmailSMSListener();
                            }
                        };
                        $scope.printPreviousListener = function (value, rid) {
                            var gridData = $scope.testsGridOptions.dataSource.data();
                            for (var idx = 0; idx < gridData.length; idx++) {
                                if (gridData[idx].rid === rid) {
                                    gridData[idx].isPrintPrevious = value;
                                    break;
                                }
                            }
                        };
                        $scope.print = function () {
                            var wrapper = getReportWrapper();
                            util.createApiRequest("generateVisitResults.srvc", JSON.stringify(wrapper), { responseType: "blob" }).then(function (response) {
                                //use above $scope.visit.rid
                                var fileName = $scope.visit.emrPatientInfo.fullName[util.userNamePrimary] + "-" + $scope.visit.admissionNumber; //commonData.reportNames.results
                                util.fileHandler(response.data, { name: fileName });
                            });
                        };
                        $scope.isEmailSMSListener = function () {
                            $scope.isSendEmailDisabled = true;
                            for (var idx = 0; idx < $scope.patDoc.length; idx++) {
                                if ($scope.patDoc[idx].isEmail || $scope.patDoc[idx].isSMS) {
                                    $scope.isSendEmailDisabled = false;
                                    break;
                                }
                            }
                            if ($scope.isSendEmailDisabled) {
                                $scope.isSendEmailDisabled = !$scope.tenantEmailHistory ? true : false;
                            }
                            if ($scope.selectedTests == null || $scope.selectedTests.length === 0) {
                                $scope.isSendEmailDisabled = true;
                            }
                        };
                        function getReportWrapper() {
                            var wrapper = {
                                visitRid: $scope.visit.rid,
                                testsMap: {},
                                timezoneOffset: new Date().getTimezoneOffset(),
                                timezoneId: new Date().toString().split('(')[1].slice(0, -1).replace('Daylight', 'Standard')
                            };
                            var gridData = $scope.testsGridOptions.dataSource.data();
                            for (var idx = 0; idx < gridData.length; idx++) {
                                var test = gridData[idx];
                                if ($scope.selectedTests.includes(test.rid)) {
                                    wrapper.testsMap[test.rid] = test.isPrintPrevious;
                                }
                            }
                            return wrapper;
                        }
                        $scope.sendEmail = function () {
                            for (var idx = 0; idx < $scope.patDoc.length; idx++) {
                                var obj = $scope.patDoc[idx];
                                var wrapper = {
                                    target: obj.type,
                                    errorSeverity: null
                                };

                                var reportWrapper = getReportWrapper();
                                reportWrapper["emailMap"] = wrapper;
                                if (obj.isEmail) {
                                    patientProfileService.generateVisitResultsEmail(reportWrapper).then(function () {
                                        util.createToast(util.systemMessages.success, "success");
                                    });
                                }
                                if (obj.isSMS) {
                                    util.createApiRequest("generateVisitResultsSMS.srvc", JSON.stringify(wrapper)).then(function () {
                                        util.createToast(util.systemMessages.success, "success");
                                    });
                                }
                            }
                            if ($scope.tenantEmailHistory) {
                                var wrapper = {
                                    target: "CUSTOM",
                                    customEmail: $scope.tenantEmailHistory
                                };

                                var reportWrapper = getReportWrapper();
                                reportWrapper["emailMap"] = wrapper;

                                patientProfileService.generateVisitResultsEmail(reportWrapper).then(function () {
                                    util.createToast(util.systemMessages.success, "success");
                                });

                                if (isNewTenantHistoryEmail) {
                                    util.createApiRequest("createTenantEmailHistory.srvc", JSON.stringify($scope.tenantEmailHistory)).then(function () {
                                        isNewTenantHistoryEmail = false;
                                    });
                                }
                            }

                        };

                        $scope.cancel = function () {
                            $mdDialog.cancel();
                        };
                    }],
                    templateUrl: './' + config.lisDir + '/modules/dialogs/results-report-sender.html',
                    parent: angular.element(document.body),
                    clickOutsideToClose: true
                }).then(function () { }, function () { });
            });
        };

        this.generateVisitResultsEmail = function (data) {
            return util.createApiRequest("generateVisitResultsEmail.srvc", JSON.stringify(data));
        };

    }]);
});


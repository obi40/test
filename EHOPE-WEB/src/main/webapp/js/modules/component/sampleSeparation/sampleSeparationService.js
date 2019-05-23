define(['app', 'util', 'commonData', 'config'], function (app, util, commonData, config) {
    'use strict';
    app.service('sampleSeparationService', ['$mdDialog', function ($mdDialog) {
        this.getSamplePage = function (data) {
            return util.createApiRequest("getSamplePage.srvc", JSON.stringify(data));
        };
        this.validateSample = function (data) {
            return util.createApiRequest("validateSample.srvc", JSON.stringify(data));
        };
        this.setSamples = function (data) {
            return util.createApiRequest("setSamples.srvc", JSON.stringify(data));
        };
        this.deleteSample = function (data) {
            return util.createApiRequest("deleteSample.srvc", JSON.stringify(data));
        };
        this.sendToMachine = function (visitRid, samplesRid, finishCallback) {
            var wrapper = {
                visitRid: visitRid,
                samplesTests: samplesRid
            };
            return util.createApiRequest("sendToMachine.srvc", JSON.stringify(wrapper));
            $mdDialog.show({
                controller: ["$scope", "$mdDialog", "sampleSeparationService",
                    function ($scope, $mdDialog, sampleSeparationService) {
                        //TODO add filtering by sample/test
                        //TODO only show non sent to machine
                        $scope.noTestsSelected = false;
                        var isCheckedAll = true;
                        $scope.checkUncheckAll = function () {
                            isCheckedAll = !isCheckedAll;
                            var grid = $("#samplesGrid").data("kendoTreeList");
                            var gridData = grid.dataSource.data();
                            for (var idx = 0; idx < gridData.length; idx++) {
                                var obj = gridData[idx];
                                obj.checked = isCheckedAll;
                            }
                            grid.refresh();
                            if (isCheckedAll == true) {
                                $scope.noTestsSelected = false;
                            } else {
                                $scope.noTestsSelected = true;
                            }
                        };
                        $scope.checkListener = function (rid) {
                            var grid = $("#samplesGrid").data("kendoTreeList");
                            var gridData = grid.dataSource.data();
                            var checkedObj = null;
                            var checkObjParent = null;
                            for (var idx = 0; idx < gridData.length; idx++) {
                                var obj = gridData[idx];
                                if (obj.rid === rid) {
                                    checkedObj = obj;
                                } else if (checkedObj != null && obj.rid === checkedObj.parentRid) {
                                    checkObjParent = obj;
                                }

                            }
                            checkedObj.checked = !checkedObj.checked;//set the actual value because this listener runs before changing the value
                            if (checkedObj.parentRid == null) {
                                //propagate check to tests for this sample
                                for (var idx = 0; idx < gridData.length; idx++) {
                                    var obj = gridData[idx];
                                    if (obj.parentRid !== checkedObj.rid) {
                                        continue;
                                    }
                                    obj.checked = checkedObj.checked;
                                }
                                grid.refresh();
                            } else {
                                //check/uncheck sample if it has a test checked/unchecked
                                var nonChecked = true;
                                for (var idx = 0; idx < gridData.length; idx++) {
                                    var obj = gridData[idx];
                                    if (obj.parentRid !== checkedObj.parentRid) {
                                        continue;
                                    }
                                    if (obj.checked) {
                                        nonChecked = false;
                                        break;
                                    }
                                }
                                if (nonChecked) {
                                    checkObjParent.checked = false;
                                } else {
                                    checkObjParent.checked = true;
                                }
                                grid.refresh();
                            }
                            for (var idx = 0; idx < gridData.length; idx++) {
                                var obj = gridData[idx];
                                if (obj.checked) {
                                    $scope.noTestsSelected = false;
                                    return;
                                }
                            }
                            $scope.noTestsSelected = true;
                        };
                        $scope.refreshGrid = function () {
                            dataSource.read();
                        };
                        var dataSource = new kendo.data.TreeListDataSource({
                            transport: {
                                read: function (e) {
                                    sampleSeparationService.getVisitSampleSeparation(visitRid).then(function (response) {
                                        e.success(response.data.labSamples);
                                    });
                                }
                            },
                            schema: {
                                parse: function (data) {
                                    if (data.length < 1) {
                                        return data;
                                    }
                                    var result = [];
                                    for (var idx = 0; idx < data.length; idx++) {
                                        var sample = data[idx];
                                        var sampleStatusCode = sample.lkpOperationStatus.code;
                                        if (sampleStatusCode !== commonData.operationStatus.REQUESTED &&
                                            sampleStatusCode !== commonData.operationStatus.VALIDATED &&
                                            sampleStatusCode !== commonData.operationStatus.COLLECTED) {
                                            continue;
                                        }
                                        sample["parentRid"] = null;
                                        sample["standardCode"] = "";
                                        sample["checked"] = true;
                                        sample.barcode = util.systemMessages.sample + " : " + sample.barcode;
                                        for (var i = 0; i < sample.labTestActualSet.length; i++) {
                                            var testActual = sample.labTestActualSet[i];
                                            var testStatusCode = testActual.lkpOperationStatus.code;
                                            if (testStatusCode !== commonData.operationStatus.REQUESTED &&
                                                testStatusCode !== commonData.operationStatus.VALIDATED &&
                                                testStatusCode !== commonData.operationStatus.COLLECTED) {
                                                continue;
                                            }
                                            testActual["parentRid"] = sample.rid;
                                            testActual["barcode"] = util.systemMessages.test;
                                            testActual["standardCode"] = testActual.testDefinition.standardCode;
                                            testActual["checked"] = true;
                                            result.push(testActual);
                                        }
                                        result.push(sample);
                                    }
                                    return result;
                                },
                                model: {
                                    expanded: true,
                                    id: "rid",
                                    parentId: "parentRid",
                                    fields: {
                                        rid: { type: "number" },
                                        parentRid: { type: "number", nullable: true },
                                        barcode: { type: "string" },
                                        standardCode: { type: "string" },
                                        checked: { type: "boolean" }
                                    }
                                }
                            }
                        });
                        $scope.samplesGridOptions = {
                            columns: [
                                {
                                    field: "barcode",
                                    title: util.systemMessages.barcode,
                                    expandable: true
                                },
                                {
                                    field: "standardCode",
                                    title: util.systemMessages.standardCode
                                },
                                {
                                    field: "checked",
                                    title: util.systemMessages.selected,
                                    template: function (dataItem) {
                                        var name = "checked" + dataItem.rid;
                                        $scope[name] = dataItem.checked;
                                        return '<div id="' + name + '" class="text-center">' +
                                            '<md-checkbox ng-model="' + name + '" ng-change="checkListener(' + dataItem.rid + ')" aria-label="' + util.systemMessages.selected + '" ></md-checkbox>'
                                            + '</div>';
                                    }
                                },
                            ],
                            dataSource: dataSource
                        };

                        $scope.submit = function () {
                            var wrapper = {
                                visitRid: visitRid,
                                samplesTests: []
                            };
                            var gridData = $("#samplesGrid").data("kendoTreeList").dataSource.data();
                            for (var idx = 0; idx < gridData.length; idx++) {

                            }
                            util.createApiRequest("sendToMachine.srvc", JSON.stringify(wrapper)).then(function () {
                                util.createToast(util.systemMessages.success, "success");
                                $scope.cancel();
                            });
                        };
                        $scope.cancel = function () {
                            $mdDialog.cancel();
                        };
                    }],
                templateUrl: './' + config.lisDir + '/modules/dialogs/collect-samples.html',
                parent: angular.element(document.body),
                clickOutsideToClose: true,
                fullscreen: false
            }).then(function () {
            }, function () {
                finishCallback();
            });
        };
        this.getVisitSampleSeparation = function (data) {
            return util.createApiRequest("getVisitSampleSeparation.srvc", JSON.stringify(data));
        };

        this.printSample = function (data) {
            return util.createApiRequest("printSample.srvc", JSON.stringify(data), { responseType: "blob" });
        };
        this.printAllSamples = function (data) {
            return util.createApiRequest("printAllSamples.srvc", JSON.stringify(data), { responseType: "blob" });
        };
        this.printSampleWorksheet = function (data) {
            return util.createApiRequest("printSampleWorksheet.srvc", JSON.stringify(data), { responseType: "blob" });
        };
        this.printAllWorksheets = function (data) {
            return util.createApiRequest("printAllSampleWorksheets.srvc", JSON.stringify(data), { responseType: "blob" });
        };

        this.generateAppointmentCard = function (data) {
            return util.createApiRequest("generateAppointmentCard.srvc", JSON.stringify(data), { responseType: "blob" });
        };
    }]);
});

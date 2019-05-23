define(['app', 'config', 'util', 'commonData'], function (app, config, util, commonData) {
    'use strict';
    app.controller('importDataCtrl', [
        '$scope', 'importDataService', '$rootScope', function (
            $scope, importDataService, $rootScope) {

            $scope.importDataList = [];
            $scope.baseFileDataWrapper = {
                oldFile: null,
                fileModel: null, // must be null
                types: ["xls", "xlsx"],
                labelCode: ""
            };
            prepareImportList();
            $scope.uploadTemplate = function (event, importObj) {
                if (importObj.fileDataWrapper.fileModel == null) {
                    return;
                }

                event.target.disabled = true;//to disable the button till request is back, so we can keep the uploaded file if request failed
                importObj.upload(importObj.fileDataWrapper.fileModel).then(function (response) {
                    util.createToast(util.systemMessages.success, "success");
                    importObj.fileDataWrapper.reset();
                    if (response.data != null && response.data.size > 0) {
                        util.fileHandler(response.data, { type: commonData.fileTypes.excel, name: "Failed.xls" }, true);
                    }
                }).catch(function (error) {
                    event.target.disabled = false;//to disable the button till request is back, so we can keep the uploaded file if request failed
                });
            };
            function prepareImportList() {
                $scope.importDataList = [
                    {
                        download: function () {
                            importDataService.downloadPatientsTemplate().then(function (response) {
                                util.fileHandler(response.data, { type: commonData.fileTypes.excel, name: "Patients' Template.xls" }, true);
                            });
                        },
                        upload: function (file) {
                            return importDataService.uploadPatientsTemplate(file);
                        },
                        label: "patient",
                        icon: "fas fa-user-alt"
                    },
                    {
                        download: function () {
                            importDataService.downloadDoctorsTemplate().then(function (response) {
                                util.fileHandler(response.data, { type: commonData.fileTypes.excel, name: "Doctors' Template.xls" }, true);
                            });
                        },
                        upload: function (file) {
                            return importDataService.uploadDoctorsTemplate(file);
                        },
                        label: "doctor",
                        icon: "fas fa-user-md"
                    },
                    {
                        download: function () {
                            importDataService.downloadTestDefinitionsTemplate().then(function (response) {
                                util.fileHandler(response.data, { type: commonData.fileTypes.excel, name: "Test Template.xls" }, true);
                            });
                        },
                        upload: function (file) {
                            return importDataService.uploadTestDefinitionsTemplate(file);
                        },
                        label: "test",
                        icon: "fas fa-vial"
                    },
                    {
                        download: function () {
                            importDataService.downloadTestResultsTemplate().then(function (response) {
                                util.fileHandler(response.data, { type: commonData.fileTypes.excel, name: "Result Template.xls" }, true);
                            });
                        },
                        upload: function (file) {
                            //return importDataService.uploadDoctorsTemplate(file);
                        },
                        label: "result",
                        icon: "fas fa-file-contract"
                    },
                    {
                        download: function () {
                            importDataService.downloadTestQuestionsTemplate().then(function (response) {
                                util.fileHandler(response.data, { type: commonData.fileTypes.excel, name: "Question Template.xls" }, true);
                            });
                        },
                        upload: function (file) {
                            //return importDataService.uploadDoctorsTemplate(file);
                        },
                        label: "question",
                        icon: "fas fa-question-circle"
                    },
                    {
                        download: function () {
                            importDataService.downloadTestPricingTemplate().then(function (response) {
                                util.fileHandler(response.data, { type: commonData.fileTypes.excel, name: "Pricing Template.xls" }, true);
                            });
                        },
                        upload: function (file) {
                            //return importDataService.uploadDoctorsTemplate(file);
                        },
                        label: "pricing",
                        icon: "fas fa-dollar-sign"
                    },
                    {
                        download: function () {
                            importDataService.downloadHistoricalOrdersTemplate().then(function (response) {
                                util.fileHandler(response.data, { type: commonData.fileTypes.excel, name: "Historical Orders' Template.xls" }, true);
                            });
                        },
                        upload: function (file) {
                            return importDataService.uploadHistoricalOrdersTemplate(file);
                        },
                        label: "historicalOrder",
                        icon: "fas fa-user-alt"
                    }
                ];
                for (var idx = 0; idx < $scope.importDataList.length; idx++) {
                    var obj = $scope.importDataList[idx];
                    obj["fileDataWrapper"] = angular.copy($scope.baseFileDataWrapper);
                    obj["download"]["callback"] = obj["download"];
                    obj["download"]["isActive"] = false;
                    obj["upload"]["callback"] = obj["upload"];
                    obj["upload"]["isActive"] = false;
                }
            }

        }
    ]);
});

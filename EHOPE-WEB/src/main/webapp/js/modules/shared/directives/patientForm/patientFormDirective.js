define(['app', 'util', 'commonData', 'config'], function (app, util, commonData, config) {
    'use strict';
    app.directive('patientForm', function () {
        return {
            restrict: 'E',
            replace: false,
            scope: {
                patient: "=patient",
                options: "=options"
            },
            templateUrl: "./" + config.lisDir + "/modules/shared/directives/patientForm/patient-form-view.html",
            controller: ['$rootScope', '$scope', 'commonMethods', 'patientFormService', '$mdDialog', '$state', 'lovService',
                function ($rootScope, $scope, commonMethods, patientFormService, $mdDialog, $state, lovService) {

                    //#region socket
                    $scope.fingerprintReadingState = "none";
                    $scope.fingerprintBase64 = null;
                    var sgSocket = null;

                    $scope.readFingerprint = function () {
                        $scope.fingerprintReadingState = "waiting";
                        if (!sgSocket || sgSocket.readyState === 3) {
                            initWebSocket();
                            sgSocket.onopen = function (e) {
                                sgSocket.send(JSON.stringify({ type: "Fingerprint", functionName: "ACQUIRE_IMAGE" }));
                            }
                        } else {
                            sgSocket.send(JSON.stringify({ type: "Fingerprint", functionName: "ACQUIRE_IMAGE" }));
                        }
                    }

                    function initWebSocket() {
                        if (sgSocket) {
                            sgSocket.close();
                        }
                        sgSocket = new WebSocket("ws://127.0.0.1:1234/ScanGatePlus");
                        sgSocket.onmessage = function (e) {
                            var response = JSON.parse(e.data);
                            if (response.status === "ERROR") {
                                util.createToast(response.error.message, "error");
                                $scope.fingerprintReadingState = "none";
                            } else {
                                $scope.fingerprintBase64 = response.data;
                                $scope.fingerprintReadingState = "read";
                            }
                        }

                        sgSocket.onerror = function (e) {
                            $scope.fingerprintReadingState = "none";
                            util.createToast(util.systemMessages.failedToConnectToFingerprintReader, "error");
                        }
                        sgSocket.onclose = function (e) {
                            //console.log(e);
                        };
                    }
                    //#endregion

                    var imageBlob = null;
                    commonData.events.patientFormReady = true;
                    $scope.patternPercent = config.regexpPercent;
                    $scope.patternNum = config.regexpNum;
                    $scope.patternMobile = config.mobilePattern;
                    $scope.today = new Date();
                    $scope.showArtifacts = true;
                    $scope.activateDirective = false;
                    $scope.userLocale = util.userLocale;
                    //#region lkps
                    $scope.genders = null;
                    $scope.femaleRid = -1;
                    lovService.getLkpByClass({ "className": "LkpGender" })
                        .then(function (data) {
                            $scope.genders = data;
                            //match using the code because object equality is weird
                            var idx = $scope.genders.map(function (g) {
                                return g.code;
                            }).indexOf("FEMALE");
                            $scope.femaleRid = $scope.genders[idx].rid;
                        });

                    var DAY_MS = 86400000;
                    $scope.dateOfBirthListener = function () {
                        if (!$scope.patient || !$scope.patient.dateOfBirth) {
                            $scope.patientAge = "";
                            return;
                        }
                        var todayMs = $scope.today.getTime();
                        var dobMs = $scope.patient.dateOfBirth.getTime();
                        var totalDays = (todayMs - dobMs) / DAY_MS;
                        if (totalDays < 7) {
                            $scope.patientAge = "(" + Math.floor(totalDays) + "D)";
                        } else if (totalDays < 30) {
                            $scope.patientAge = "(" + Math.floor((totalDays / 7)) + "W)";
                        } else if (totalDays < 365) {
                            $scope.patientAge = "(" + Math.floor((totalDays / 30)) + "M)";
                        } else {
                            $scope.patientAge = "(" + Math.floor((totalDays / 365)) + "Y)";
                        }
                    };

                    $scope.lkpCountry = {
                        className: "LkpCountry",
                        valueField: "name." + util.userLocale,
                        labelText: "country",
                        selectedValue: null
                    };
                    $scope.lkpMaritalStatus = {
                        className: "LkpMaritalStatus",
                        valueField: "name." + util.userLocale,
                        labelText: "maritalStatus",
                        selectedValue: null
                    };
                    $scope.lkpPatientStatus = {
                        className: "LkpPatientStatus",
                        valueField: "name." + util.userLocale,
                        labelText: "patientStatus",
                        selectedValue: null
                    };
                    $scope.lkpBloodType = {
                        className: "LkpBloodType",
                        valueField: "name." + util.userLocale,
                        labelText: "bloodType",
                        selectedValue: null
                    };

                    $scope.lkps = [
                        $scope.lkpCountry,
                        $scope.lkpMaritalStatus,
                        // $scope.lkpPatientStatus,
                        $scope.lkpBloodType
                    ];

                    //#endregion
                    $scope.$on(commonData.events.activatePatientForm, function (event, params) {
                        activateDirective();
                    });
                    //use this event to bind the LOVs
                    $scope.$on(commonData.events.enterPatientForm, function (event, params) {
                        $scope.patient = params;
                        if (!$scope.activateDirective) {
                            activateDirective();
                        }

                        $scope.lkpCountry.selectedValue = $scope.patient.country;

                        $scope.lkpMaritalStatus.selectedValue = $scope.patient.maritalStatus;

                        $scope.lkpPatientStatus.selectedValue = $scope.patient.patientStatus;

                        $scope.lkpBloodType.selectedValue = $scope.patient.bloodType;
                        $scope.fileDataWrapper.oldFile = $scope.patient.image;
                        $scope.fileDataWrapper.fileModel = $scope.patient.image;
                    });

                    function activateDirective() {
                        $scope.activateDirective = true;
                        $scope.locale = util.userLocale;
                        $scope.metaData = null;
                        $scope.fileDataWrapper = {
                            oldFile: $scope.patient.image,
                            fileModel: null,
                            types: ["jpg", "jpeg", "png"],
                            labelCode: "patientImage"
                        };

                        commonMethods.retrieveMetaData("EmrPatientInfo")
                            .then(function (response) {
                                $scope.metaData = response.data;

                                $scope.lkpCountry.name = $scope.metaData.country.name;
                                $scope.lkpCountry.required = $scope.metaData.country.notNull;

                                $scope.lkpMaritalStatus.name = $scope.metaData.maritalStatus.name;
                                $scope.lkpMaritalStatus.required = $scope.metaData.maritalStatus.notNull;

                                $scope.lkpPatientStatus.name = $scope.metaData.patientStatus.name;
                                $scope.lkpPatientStatus.required = $scope.metaData.patientStatus.notNull;

                                $scope.lkpBloodType.name = $scope.metaData.bloodType.name;
                                $scope.lkpBloodType.required = $scope.metaData.bloodType.notNull;

                                $scope.transFields = {
                                    remarks: util.getTransFieldLanguages("remarks", "remarks", $scope.patient, $scope.metaData.remarks.notNull),
                                    address: util.getTransFieldLanguages("address", "address", $scope.patient, $scope.metaData.address.notNull)
                                };

                            });

                        //#endregion

                        $scope.clearPatient = function (form) {
                            $scope.patient = {
                                isBloodSamplePhobia: false,
                                isAllergies: false,
                                isDiabetes: false,
                                isDifficultyFindingVeins: false,
                                isHypertension: false,
                                isPregnancy: false,
                                inInsurer: false,
                                isHeartDisease: false,
                                isHardTemperedPatient: false,
                                isBlackListed: false,
                                isVip: false,
                                isVerified: false,
                                isWhatsappNotification: false,
                                isSmsNotification: false,
                                isEmailNotification: false,
                                dateOfBirth: null,
                                firstName: {},
                                secondName: {},
                                thirdName: {},
                                lastName: {}
                            };

                            $scope.lkpCountry.selectedValue = $scope.patient.country;

                            $scope.lkpMaritalStatus.selectedValue = $scope.patient.maritalStatus;

                            $scope.lkpPatientStatus.selectedValue = $scope.patient.patientStatus;

                            $scope.lkpBloodType.selectedValue = $scope.patient.bloodType;

                            $scope.dateOfBirthListener();

                            util.clearForm(form);
                        }

                        $scope.submitPatient = function (valid) {
                            if (!valid) {
                                return;
                            }
                            $scope.patient.emrVisits = null;

                            $scope.patient.country = $scope.lkpCountry.selectedValue;

                            $scope.patient.maritalStatus = $scope.lkpMaritalStatus.selectedValue;

                            $scope.patient.patientStatus = $scope.lkpPatientStatus.selectedValue;

                            $scope.patient.bloodType = $scope.lkpBloodType.selectedValue;

                            var artifactsToUpload = $scope.patient.getArtifactsToUpload();
                            var artifactIdsToDelete = $scope.patient.getArtifactIdsToDelete();

                            //match using the rid because object equality is weird
                            var idx = $scope.genders.map(function (g) {
                                return g.rid;
                            }).indexOf($scope.patient.gender.rid);
                            $scope.patient.gender = $scope.genders[idx];

                            if ($scope.patient.gender.code !== 'FEMALE') {
                                $scope.patient.isPregnancy = false;
                            }
                            var patientCopy = angular.copy($scope.patient);
                            delete patientCopy.artifacts;

                            if ($scope.options.formMode === 'edit') {
                                $scope.showArtifacts = false;
                                patientFormService.editPatient($scope.fileDataWrapper, artifactsToUpload, artifactIdsToDelete, patientCopy, $scope.fingerprintBase64)
                                    .then(function (resp) {
                                        var data = resp.data;
                                        for (var key in data) {
                                            if (data.hasOwnProperty(key)) {
                                                $rootScope.currentPatient[key] = $scope.patient[key] = data[key];
                                            }
                                        }
                                        $scope.showArtifacts = true;
                                        util.createToast(util.systemMessages.success, "success");
                                        if ($scope.options.onEditCallback) {
                                            $scope.options.onEditCallback();
                                        }
                                    });
                            } else if ($scope.options.formMode === 'add') {
                                $scope.patient.isActive = true;
                                patientFormService.getSimilarPatientList(patientCopy)
                                    .then(function (resp) {
                                        var similarPatients = resp.data;
                                        for (var i = 0; i < similarPatients.length; i++) {
                                            similarPatients[i].dateOfBirth = new Date(similarPatients[i].dateOfBirth);
                                        }
                                        if (similarPatients.length > 0) {
                                            $mdDialog.show({
                                                controller: ["$scope", "$mdDialog", "similarPatients", "patientCopy", "addNewPatient",
                                                    function ($scope, $mdDialog, similarPatients, patientCopy, addNewPatient) {
                                                        $scope.similarPatientsGridOptions = {
                                                            columns: [{
                                                                field: "firstName",
                                                                title: util.systemMessages.name,
                                                                template: function (dataItem) {
                                                                    var name = dataItem.firstName[util.userLocale] + " " + dataItem.lastName[util.userLocale];
                                                                    return name;
                                                                }
                                                            }, {
                                                                field: "fileNo",
                                                                title: util.systemMessages.fileNumber
                                                            }, {
                                                                field: "age",
                                                                title: util.systemMessages.age
                                                            }, {
                                                                field: "dateOfBirth",
                                                                title: util.systemMessages.dob,
                                                                format: "{0:" + config.dateFormat + "}"
                                                            }, {
                                                                field: "gender",
                                                                title: util.systemMessages.sex,
                                                                template: function (dataItem) {
                                                                    return dataItem.gender.name[util.userLocale];
                                                                }
                                                            }],
                                                            dataSource: similarPatients,
                                                            pageable: false,
                                                            change: function (e) {
                                                                var selectedRows = this.select();
                                                                if (selectedRows.length > 0) {
                                                                    $scope.selectedPatient = this.dataItem(selectedRows[0]);
                                                                } else {
                                                                    $scope.selectedPatient = null;
                                                                }
                                                            }
                                                        };

                                                        $scope.chooseAndProgress = function (selectedPatient) {
                                                            $rootScope.currentPatient = selectedPatient;
                                                            if (patientCopy.isInsurer) {
                                                                $rootScope.currentPatient.isInsurer = true;
                                                            }
                                                            $scope.cancel();
                                                            $state.go("patient-profile-wizard");
                                                        };

                                                        $scope.createNew = function () {
                                                            addNewPatient(patientCopy);
                                                            $scope.cancel();
                                                        };

                                                        $scope.cancel = function () {
                                                            $mdDialog.cancel();
                                                        };
                                                    }],
                                                locals: {
                                                    similarPatients: similarPatients,
                                                    patientCopy: patientCopy,
                                                    addNewPatient: addNewPatient
                                                },
                                                templateUrl: './' + config.lisDir + '/modules/dialogs/registration-confirmation.html',
                                                parent: angular.element(document.body),
                                                clickOutsideToClose: true,
                                                fullscreen: false
                                            });
                                        } else {
                                            addNewPatient(patientCopy);
                                        }
                                    });
                            }
                        };

                        function addNewPatient(patientCopy) {
                            var artifactsToUpload = $scope.patient.getArtifactsToUpload();
                            patientFormService.addPatient($scope.fileDataWrapper.fileModel, artifactsToUpload, patientCopy, $scope.fingerprintBase64)
                                .then(function (resp) {
                                    $rootScope.currentPatient = resp.data;
                                    if ($scope.patient.isInsurer) {
                                        $rootScope.currentPatient.isInsurer = true;
                                    }
                                    $state.go("patient-profile-wizard");
                                });
                        }

                        function fillPatientData(data) {
                            var patient = $scope.patient;
                            patient.firstName.en_us = data.DemographicData.EnglishName1;
                            patient.firstName.ar_jo = data.DemographicData.ArabicName1;

                            patient.secondName.en_us = data.DemographicData.EnglishName2;
                            patient.secondName.ar_jo = data.DemographicData.ArabicName2;

                            patient.thirdName.en_us = data.DemographicData.EnglishName3;
                            patient.thirdName.ar_jo = data.DemographicData.ArabicName3;

                            patient.lastName.en_us = data.DemographicData.EnglishName4;
                            patient.lastName.ar_jo = data.DemographicData.ArabicName4;

                            var dateParts = data.DemographicData.BirthDate.split("/");

                            patient.dateOfBirth = new Date(dateParts[2], dateParts[1] - 1, dateParts[0]);
                            patient.mobileNo = data.ContactInformationData.MobileNumber.replace("00", "+");
                            patient.secondaryMobileNo = data.ContactInformationData.MobileNumber.replace("00", "+");

                            if (!patient.address) {
                                patient.address = {};
                            }
                            patient.address.en_us = data.DemographicData.Address;
                            patient.address.ar_jo = data.DemographicData.Address;
                            for (var i = 0; i < $scope.genders.length; i++) {
                                if ($scope.genders[i].code.startsWith(data.DemographicData.Gender)) {
                                    $scope.patient.gender = $scope.genders[i];
                                    break;
                                }
                            }
                            patient.nationalId = parseInt(data.DemographicData.NationalNumber);
                            imageBlob = util.base64ToBlob(data.Portrait, "image/jpeg");

                            $scope.fileDataWrapper.oldFile = data.Portrait;
                            $scope.fileDataWrapper.fileModel = imageBlob;
                            $scope.dateOfBirthListener();
                        }

                        $scope.idCardDialog = function () {
                            $mdDialog.show({
                                controller: ["$scope", "$mdDialog", "patient",
                                    function ($scope, $mdDialog, patient) {
                                        var sgSocket = null;

                                        $scope.idCardReadingStatus = "none";

                                        getReaders();

                                        function getReaders() {
                                            var payload = {
                                                type: "JO_ID_CARD",
                                                functionName: "GET_READERS"
                                            };
                                            if (!sgSocket || sgSocket.readyState === 3) {
                                                initWebSocket();
                                                sgSocket.onopen = function (e) {
                                                    sgSocket.send(JSON.stringify(payload));
                                                }
                                            } else {
                                                sgSocket.send(JSON.stringify(payload));
                                            }
                                        }

                                        $scope.readIdCard = function (valid) {
                                            if (!valid) {
                                                return;
                                            }
                                            $scope.idCardReadingStatus = "waiting";
                                            var payload = {
                                                type: "JO_ID_CARD",
                                                functionName: "READ_CARD_DATA",
                                                payload: {
                                                    can: $scope.can.toUpperCase(),
                                                    reader: $scope.idCardReaderOptions.selectedValue.name,
                                                    demographic: true,
                                                    contactInformation: true,
                                                    portrait: true,
                                                    signature: false
                                                }
                                            };
                                            if (!sgSocket || sgSocket.readyState === 3) {
                                                initWebSocket();
                                                sgSocket.onopen = function (e) {
                                                    sgSocket.send(JSON.stringify(payload));
                                                }
                                            } else {
                                                sgSocket.send(JSON.stringify(payload));
                                            }
                                        }

                                        function initWebSocket() {
                                            if (sgSocket) {
                                                sgSocket.close();
                                            }
                                            sgSocket = new WebSocket("ws://127.0.0.1:1234/ScanGatePlus");
                                            sgSocket.onmessage = function (e) {
                                                var response = JSON.parse(e.data);
                                                if (response.status === "ERROR") {
                                                    util.createToast(response.error.message, "error");
                                                    $scope.idCardReadingStatus = "none";
                                                } else {
                                                    switch (response.functionName) {
                                                        case "GET_READERS":
                                                            var readerNames = [];
                                                            for (var i = 0; i < response.data.length; i++) {
                                                                readerNames.unshift({
                                                                    rid: i,
                                                                    name: response.data[i]
                                                                });
                                                            }
                                                            $scope.idCardReaderOptions = {
                                                                valueField: "name",
                                                                labelText: "idCardReader",
                                                                selectedValue: readerNames[0],
                                                                data: readerNames,
                                                                required: true
                                                            };
                                                            break;
                                                        case "READ_CARD_DATA":
                                                            $scope.idCardReadingStatus = "none";
                                                            fillPatientData(response.data);
                                                            $mdDialog.hide();
                                                            break;
                                                    }
                                                }
                                            }

                                            sgSocket.onerror = function (e) {
                                                util.createToast(util.systemMessages.error, "error");
                                                $scope.idCardReadingStatus = "none";
                                            }
                                            sgSocket.onclose = function (e) {
                                                //console.log(e);
                                            };
                                        }

                                        $scope.cancel = function () {
                                            $mdDialog.cancel();
                                        };
                                    }],
                                locals: {
                                    patient: $scope.patient
                                },
                                templateUrl: './' + config.lisDir + '/modules/dialogs/id-card-dialog.html',
                                parent: angular.element(document.body),
                                clickOutsideToClose: false,
                                fullscreen: false
                            });
                        }

                    }
                }]
        }
    });
});
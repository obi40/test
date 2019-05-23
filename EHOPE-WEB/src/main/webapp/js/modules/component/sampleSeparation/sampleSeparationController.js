define(['app', 'util', 'config', 'commonData'], function (app, util, config, commonData) {
    'use strict';
    app.controller('sampleSeparationController', [
        '$scope',
        'sampleSeparationService',
        '$rootScope',
        '$state',
        '$timeout',
        '$mdDialog',
        'orderFormService',
        'separationFactorsService',
        function ($scope,
            sampleSeparationService,
            $rootScope,
            $state,
            $timeout,
            $mdDialog,
            orderFormService,
            separationFactorsService) {
            //exit if no samples or patient
            if ($rootScope.sampleSeparationVisitRid == null) {
                util.exitPage();
                return;
            }
            $scope.isWizard = $rootScope.sampleSeparationIsWizard ? true : false;//to show/hide the finish button, will be reseted on destroy
            $scope.testsWithoutSamples = [];// these are the tests that are not in any sample
            var ridCounter = -1;// dummy rid, must be removed when saving all samples
            $scope.areSamplesInvalid = false; // to enable/disable the save button if any sample is invalid and to color the samples
            var sampleCardPrefix = "sample_";//the prefix we add for samples when the dom element is created
            var testChipPrefix = "test_";//the prefix we add for tests when the dom element is created
            var testSampleRidKey = "sampleRid";//store the sample rid inside the test, so it can be used for manipulations.didnt use the actual field incase of infinite reference.
            $scope.validSample = "valid";
            $scope.indefiniteSample = "indefinite";
            $scope.invalidSample = "invalid";
            $scope.visit = null;
            $scope.patient = null;
            $scope.samples = null;
            //$scope.labFactors = {};
            var timezoneOffset = new Date().getTimezoneOffset();
            var timezoneId = new Date().toString().split('(')[1].slice(0, -1).replace('Daylight', 'Standard');

            $scope.patientInfoOptions = {
                patientRid: null
            };
            angular.element(function () {
                //fetch the required data
                $scope.refresh();
            });
            function prepareSamples() {
                $scope.testsWithoutSamples = [];//reset
                ridCounter = -1//reset
                $scope.areSamplesInvalid = false;//reset
                for (var idx = 0; idx < $scope.samples.length; idx++) {
                    var sample = $scope.samples[idx];
                    sampleValidation(sample, false);
                    isSampleDisabled(sample);
                    for (var i = 0; i < sample.labTestActualSet.length; i++) {
                        sample.labTestActualSet[i][testSampleRidKey] = sample.rid;
                    }
                }
                colorContainers();
                createDroppables();
            }
            // coloring of the samples
            function colorContainers() {
                $("#sample-separation-container-style").remove();
                var colorRules = "";
                for (var index in $scope.samples) {
                    var sample = $scope.samples[index];
                    var color = "#000000";//default
                    if (sample.state === $scope.validSample && sample.labTestActualSet.length > 0 && sample.lkpContainerType != null) {
                        color = sample.lkpContainerType.color;
                        colorRules += "#" + sampleCardPrefix + sample.rid + " .cap { fill: " + color + " !important; }";
                    } else {
                        colorRules += "#" + sampleCardPrefix + sample.rid + " .cap { fill: " + color + " !important; }";
                    }
                }
                $("head").append("<style id='sample-separation-container-style'>" + colorRules + "</style>");
            }
            function createDraggables() {
                // to create a draggable for tests
                $timeout(function () {
                    $('div[id^="' + testChipPrefix + '"]').each(function () {
                        //dont create draggable if this test's sample is disabled
                        for (var idx = 0; idx < $scope.samples.length; idx++) {
                            var sample = $scope.samples[idx];
                            for (var i = 0; i < sample.labTestActualSet.length; i++) {
                                var testRid = parseInt($(this).attr("id").substring($(this).attr("id").indexOf("_") + 1));
                                if (testRid === sample.labTestActualSet[i].rid && sample.isDisabled) {
                                    return;
                                }
                            }
                        }
                        $(this).draggable({
                            revert: true,
                            containment: ".sample-separation-wrapper"//dragging limit
                        });
                    });
                });
            }
            function createDroppables() {
                //wait to render
                $timeout(function () {
                    //Make samples a droppable
                    $('md-card[id^="' + sampleCardPrefix + '"]').each(function () {
                        //dont create droppable if this sample is disabled
                        for (var idx = 0; idx < $scope.samples.length; idx++) {
                            var sampleRid = parseInt($(this).attr("id").substring($(this).attr("id").indexOf("_") + 1));
                            if (sampleRid === $scope.samples[idx].rid && $scope.samples[idx].isDisabled) {
                                return;
                            }
                        }
                        $(this).droppable({
                            drop: function (event, ui) {
                                var draggedTestRid = ui.draggable.attr('id');
                                draggedTestRid = parseInt(draggedTestRid.substring(draggedTestRid.indexOf("_") + 1));
                                var droppedSampleRid = parseInt(event.target.id.substring(event.target.id.indexOf("_") + 1));
                                $scope.sampleDropListener(droppedSampleRid, draggedTestRid);
                            }
                        });
                    });
                    createDraggables();
                });
            }
            function toggleDropping(rid, state) {
                $("#" + sampleCardPrefix + rid).droppable(state);
                for (var idx = 0; idx < $scope.samples.length; idx++) {
                    var sample = $scope.samples[idx];
                    if (rid != sample.rid) {
                        continue;
                    }
                    for (var i = 0; i < sample.labTestActualSet.length; i++) {
                        var test = sample.labTestActualSet[i];
                        $("#" + testChipPrefix + test.rid).draggable(state);
                    }
                }
            }
            $scope.sampleDropListener = function (droppedSampleRid, testRid) {
                if (droppedSampleRid == null || testRid == null) {
                    return;
                }
                var test = null;
                //fetch from tests in samples
                OUTER: for (var sampleKey in $scope.samples) {
                    var labTestActualSet = $scope.samples[sampleKey].labTestActualSet;
                    for (var testKey in labTestActualSet) {
                        if (labTestActualSet[testKey].rid == testRid) {
                            test = labTestActualSet[testKey];
                            break OUTER;
                        }
                    }
                }
                //fetch from removed tests from samples
                if (test == null) {
                    for (var testKey in $scope.testsWithoutSamples) {
                        if ($scope.testsWithoutSamples[testKey].rid == testRid) {
                            test = $scope.testsWithoutSamples[testKey];
                            break;
                        }
                    }
                }
                if (test == null) {
                    return;
                }

                // dont do anything if we dropped the test in the same sample it is on
                for (var idx = 0; idx < $scope.samples.length; idx++) {
                    for (var i = 0; i < $scope.samples[idx].labTestActualSet.length; i++) {
                        if ($scope.samples[idx].labTestActualSet[i].rid == test.rid &&
                            $scope.samples[idx].rid == droppedSampleRid) {
                            return;
                        }
                    }
                }

                // Remove the test if it is in testsWithoutSamples
                for (var idx = 0; idx < $scope.testsWithoutSamples.length; idx++) {
                    if ($scope.testsWithoutSamples[idx].rid == test.rid) {
                        $scope.testsWithoutSamples.splice(idx, 1);
                        break;
                    }
                }

                // Remove the test from the sample that it belongs to
                OUTER: for (var idx = 0; idx < $scope.samples.length; idx++) {
                    for (var i = 0; i < $scope.samples[idx].labTestActualSet.length; i++) {
                        if ($scope.samples[idx].labTestActualSet[i].rid == test.rid) {
                            $scope.samples[idx].labTestActualSet.splice(i, 1);
                            //Revalidate after removing a test from this sample
                            sampleValidation($scope.samples[idx], true);
                            break OUTER;
                        }
                    }
                }

                // Push the test in the sample
                for (var sampleKey in $scope.samples) {
                    if ($scope.samples[sampleKey].rid == droppedSampleRid) {
                        test[testSampleRidKey] = $scope.samples[sampleKey].rid;
                        $scope.samples[sampleKey].labTestActualSet.push(test);
                        break;
                    }
                }

                //verify the sample we dropped the test in
                sampleValidation($scope.samples[sampleKey], true);
            };
            $scope.finish = function () {
                util.exitPage();
            };
            function isSampleDisabled(sample) {
                sample.isDisabled = sample.lkpOperationStatus.code !== commonData.operationStatus.REQUESTED
                    && sample.lkpOperationStatus.code !== commonData.operationStatus.VALIDATED
                    && sample.lkpOperationStatus.code !== commonData.operationStatus.COLLECTED;
            };
            $scope.refresh = function () {
                // separationFactorsService.getActiveFactorsByBranch().then(function (response) {
                //     var data = response.data;
                //     for (var idx = 0; idx < data.length; idx++) {
                //         $scope.labFactors[data[idx].rid] = data[idx].description[util.userLocale];
                //     }
                // });
                refetchVisit().then(function (visit) {
                    $scope.visit = visit;
                    $scope.patient = $scope.visit.emrPatientInfo;
                    $scope.patientInfoOptions.patientRid = $scope.patient.rid;
                    $scope.patientInfoOptions.refresh();
                    $scope.samples = $scope.visit.labSamples;
                    prepareSamples();
                });
            };
            $scope.printSample = function (sample) {
                if (sample.rid < 0) {
                    return;
                }

                $scope.sampleInformation = {
                    sampleRid: sample.rid,
                    timezoneOffset: timezoneOffset,
                    timezoneId: timezoneId,
                };

                if ($scope.visit.appointmentCardDate == null) {
                    $scope.sampleCustomConfirmClick(false, sample);
                } else {
                    sampleSeparationService.printSample($scope.sampleInformation).then(function (response) {
                        util.fileHandler(response.data, { type: "application/pdf", name: commonData.reportNames.sample });
                    });
                }
            };
            $scope.printAllSamples = function () {
                $scope.visitInformation = {
                    visitRid: $scope.visit.rid,
                    timezoneOffset: timezoneOffset,
                    timezoneId: timezoneId,
                };

                if ($scope.visit.appointmentCardDate == null) {
                    $scope.sampleCustomConfirmClick(true, null);
                } else {
                    sampleSeparationService.printAllSamples($scope.visitInformation).then(function (response) {
                        util.fileHandler(response.data, { type: "application/pdf", name: commonData.reportNames.sample });
                    });
                }
            };

            //////////////////////////////////////////////////
            $scope.sampleCustomConfirmClick = function (isAll, sample) {
                $mdDialog.show({
                    controller: ["$scope", "$mdDialog", "timezoneOffset", "timezoneId", "visitRid", "appointmentCard",
                        function ($scope, $mdDialog, timezoneOffset, timezoneId, visitRid, appointmentCard) {
                            $scope.printSampleBarcode = function () {
                                if (isAll) {
                                    $scope.visitInformation = {
                                        visitRid: visitRid,
                                        timezoneOffset: timezoneOffset,
                                        timezoneId: timezoneId,
                                    };

                                    sampleSeparationService.printAllSamples($scope.visitInformation).then(function (response) {
                                        util.fileHandler(response.data, { type: "application/pdf", name: commonData.reportNames.sample });
                                    });
                                } else {
                                    $scope.sampleInformation = {
                                        sampleRid: sample.rid,
                                        timezoneOffset: timezoneOffset,
                                        timezoneId: timezoneId,
                                    };

                                    sampleSeparationService.printSample($scope.sampleInformation).then(function (response) {
                                        util.fileHandler(response.data, { type: "application/pdf", name: commonData.reportNames.sample });
                                    });
                                }
                            };

                            $scope.appointmentCard = function () {
                                appointmentCard();
                            }
                            $scope.cancel = function () {
                                $mdDialog.cancel();
                            };
                        }],
                    templateUrl: './' + config.lisDir + '/modules/dialogs/sample-custom-confirm-click.html',
                    parent: angular.element(document.body),
                    //targetEvent: ev,
                    clickOutsideToClose: true,
                    locals: {
                        timezoneOffset: timezoneOffset,
                        timezoneId: timezoneId,
                        visitRid: $scope.visit.rid,
                        printAllSamples: $scope.printAllSamples,
                        appointmentCard: $scope.appointmentCardDialog,
                    }
                }).then(function () { }, function () { });
            };
            //////////////////////////////////////////////////


            // add new sample so tests can be added to it
            $scope.addNewSample = function () {
                $scope.samples.push({
                    rid: (ridCounter--),// so it can be tracked for drag and drop
                    valid: false,
                    validating: false,
                    labTestActualSet: []
                });
                colorContainers();
                areSamplesInvalid();
                createDroppables();
            };
            function refetchVisit() {
                return sampleSeparationService.getVisitSampleSeparation($rootScope.sampleSeparationVisitRid).then(function (response) {
                    return response.data;
                });
            }
            function setSample(sample) {
                var wrapper = getUpdateSamplesWrapper(sample);
                sampleSeparationService.setSamples(wrapper).then(function () {
                    refetchVisit().then(function (visit) {
                        var oldSampleTestsRid = wrapper.samplesTests[sample.rid];
                        //incase of remove a test from the sample the fetched visit will have the sample and it tests
                        //even the one in the testsWithoutSamples, because we didnt actually remove the test from the sample
                        //so we get their rids so we can compare to the fetched sample.
                        var testsWithoutSamplesRid = [];
                        if ($scope.testsWithoutSamples != null && $scope.testsWithoutSamples.length > 0) {
                            for (var idx = 0; idx < $scope.testsWithoutSamples.length; idx++) {
                                if ($scope.testsWithoutSamples[idx][testSampleRidKey] == sample.rid) {
                                    testsWithoutSamplesRid.push($scope.testsWithoutSamples[idx].rid);
                                }
                            }
                        }
                        oldSampleTestsRid = oldSampleTestsRid.concat(testsWithoutSamplesRid);
                        oldSampleTestsRid = oldSampleTestsRid.sort();
                        var newSample = null;
                        // get the new updated sample from the re-fetched visit
                        // by comparing the tests in both the parameter sample and the updated sample
                        OUTER: for (var idx = 0; idx < visit.labSamples.length; idx++) {
                            var obj = visit.labSamples[idx];
                            var testsRidInSample = [];
                            for (var i = obj.labTestActualSet.length - 1; i >= 0; i--) {
                                testsRidInSample.push(obj.labTestActualSet[i].rid);
                                obj.labTestActualSet[i][testSampleRidKey] = obj.rid;
                                //if the test is in testsWithoutSamples then remove it from the sample we fetched.
                                //so we dont reinsert it to the card which will result in the same test to be in the 
                                //testsWithoutSamples and the card
                                if (testsWithoutSamplesRid.length > 0) {
                                    for (var y = 0; y < testsWithoutSamplesRid.length; y++) {
                                        if (obj.labTestActualSet[i].rid == testsWithoutSamplesRid[y]) {
                                            obj.labTestActualSet.splice(i, 1);
                                            break;
                                        }
                                    }
                                }
                            }
                            testsRidInSample = testsRidInSample.sort();
                            if (angular.equals(testsRidInSample, oldSampleTestsRid)) {
                                newSample = obj;
                                break OUTER;
                            }
                        }
                        changeSampleState(newSample, sample.state);//get the old sample flag if it is valid or not
                        newSample["validating"] = false;
                        //replace with the new sample
                        for (var idx = 0; idx < $scope.samples.length; idx++) {
                            if ($scope.samples[idx].rid == sample.rid) {
                                $scope.samples.splice(idx, 1, newSample);
                                break;
                            }
                        }
                        colorContainers();
                        areSamplesInvalid();
                        createDroppables();
                    });
                });
            }

            function changeSampleState(sample, state) {
                sample.state = state;
            }

            function generateFactors(sample, factorValues) {
                var groups = {};
                for (var idx = 0; idx < sample.labTestActualSet.length; idx++) {
                    var test = sample.labTestActualSet[idx];
                    var testFactorValues = factorValues[test.rid];
                    test["factorValues"] = "";
                    for (var key in testFactorValues) {
                        var value = null;
                        if (testFactorValues[key] == null) {
                            value = commonData.na;
                        } else {
                            value = "" + (testFactorValues[key].rid ? testFactorValues[key].rid : testFactorValues[key]);
                        }
                        test["factorValues"] += ($scope.labFactors[key] + commonData.arrow + value);
                        test["factorValues"] += "|";
                    }

                    if (!groups[test.factorValues]) {
                        groups[test.factorValues] = [];
                    }
                    groups[test.factorValues].push(test.testDefinition.standardCode);
                }
                console.log(groups);
                var result = "";
                for (var key in groups) {
                    for (var idx = 0; idx < groups[key].length; idx++) {
                        result += groups[key][idx];
                        result += ",";
                    }
                    result = result.substring(0, result.length - 1);
                    result += "<br>";
                }
                console.log(result);
            };
            // add tests and validate if it can be added to the sample
            // isUpdate wither to call setSample(...) or not
            function sampleValidation(sample, isUpdate) {
                // validating is for the loading indicator
                sample.validating = true;
                if (sample.labTestActualSet.length == 0) {
                    changeSampleState(sample, $scope.invalidSample);
                    sample.validating = false;
                    colorContainers();
                    areSamplesInvalid();
                    return;
                } else if (sample.rid < 0) {//if this is a dummy sample then its valid so create it 
                    changeSampleState(sample, $scope.validSample);
                    if (isUpdate) {
                        setSample(sample);
                    } else {
                        sample.validating = false;
                        colorContainers();
                        areSamplesInvalid();
                    }
                    return;
                }
                changeSampleState(sample, $scope.invalidSample);
                var wrapper = getUpdateSamplesWrapper(sample);
                sampleSeparationService.validateSample(wrapper).then(function (response) {
                    sample.validating = false;
                    var valid = response.data.valid;
                    if (valid) {
                        changeSampleState(sample, $scope.validSample);
                    } else {
                        changeSampleState(sample, $scope.indefiniteSample);
                        //generateFactors(sample, response.data.factorValues);
                    }
                    if (isUpdate) {
                        setSample(sample);
                    } else {
                        colorContainers();
                        areSamplesInvalid();
                    }
                }).catch(function (error) {
                    sample.validating = false;
                    if (error.config) {//only if backend error
                        $scope.refresh();
                    }
                });
            };
            function areSamplesInvalid() {
                for (var sampleKey in $scope.samples) {
                    if ($scope.samples[sampleKey].rid > 0 && ($scope.samples[sampleKey].labTestActualSet.length == 0
                        || $scope.samples[sampleKey].state === $scope.invalidSample)) {
                        $scope.areSamplesInvalid = true;
                        return;
                    }
                }
                if ($scope.testsWithoutSamples && $scope.testsWithoutSamples.length > 0) {
                    $scope.areSamplesInvalid = true;
                    return;
                }
                $scope.areSamplesInvalid = false;
            }
            $scope.deleteSample = function (sample) {
                if (sample.rid < 0) {//dummy sample
                    // delete the sample from ui
                    for (var idx = 0; idx < $scope.samples.length; idx++) {
                        if ($scope.samples[idx].rid == sample.rid) {
                            $scope.samples.splice(idx, 1);
                            return;
                        }
                    }
                }
                sampleSeparationService.deleteSample(sample.rid).then(function () {
                    util.createToast(util.systemMessages.success, "success");
                    // delete the sample from ui
                    for (var idx = 0; idx < $scope.samples.length; idx++) {
                        if ($scope.samples[idx].rid == sample.rid) {
                            $scope.samples.splice(idx, 1);
                            break;
                        }
                    }
                    // return the tests inside the sample into somewhere for manual sample creation
                    for (var key in sample.labTestActualSet) {
                        $scope.testsWithoutSamples.push(sample.labTestActualSet[key]);
                    }
                    colorContainers();
                    areSamplesInvalid();
                    createDroppables();
                });

            };

            $scope.removeTest = function (sample, test) {
                if (sample.validating) {
                    return;
                }
                //After removing a test from the sample , revalidate the whole sample
                for (var idx = 0; idx < sample.labTestActualSet.length; idx++) {
                    if (sample.labTestActualSet[idx].rid == test.rid) {
                        sample.labTestActualSet.splice(idx, 1);
                        break;
                    }
                }
                $scope.testsWithoutSamples.push(test);
                sampleValidation(sample, true);
                createDraggables();
            };

            function getUpdateSamplesWrapper(sample) {
                var wrapper = {
                    samplesTests: {},
                    visitRid: $scope.visit.rid
                };
                for (var idx = 0; idx < $scope.samples.length; idx++) {
                    var obj = $scope.samples[idx];
                    // if sample is defined then only create samplesTests for it only.
                    if (sample && obj.rid != sample.rid) {
                        continue;
                    }
                    wrapper.samplesTests[obj.rid] = [];
                    for (var i = 0; i < obj.labTestActualSet.length; i++) {
                        wrapper.samplesTests[obj.rid].push(obj.labTestActualSet[i].rid);
                    }
                }
                return wrapper;
            }

            $scope.resultEntry = function () {
                //RECHECK WHEN RE ENABLING THE BUTTON
                var wrapper = getUpdateSamplesWrapper();
                return;
                sampleSeparationService.setSamples(wrapper)
                    .then(function (response) {
                        util.createToast(util.systemMessages.success, "success");
                        // refresh the current samples with the returned ones
                        $rootScope.currentSamples = response.data;
                        $scope.samples = $rootScope.currentSamples;
                        prepareSamples();
                        //then navigate to result entry page
                        $rootScope.patientProfileMode = "edit";
                        $rootScope.patientTabToSelect = 2; //2 is orders tab
                        $rootScope.currentPatient = $scope.patient;
                        $rootScope.orderToSelect = $scope.visit;
                        $state.go("patient-profile");
                    });
            };

            $scope.sendToMachine = function () {
                var allSamplesWrapper = getUpdateSamplesWrapper();
                sampleSeparationService.setSamples(allSamplesWrapper).then(function () {
                    sampleSeparationService.sendToMachine(allSamplesWrapper.visitRid, allSamplesWrapper.samplesTests).then(function () {
                        util.createToast(util.systemMessages.success, "success");
                        util.exitPage();
                    });
                });

            };
            $scope.printWorksheet = function (sample) {
                var fileName = $scope.visit.emrPatientInfo.fullName[util.userNamePrimary] + "-" + $scope.visit.admissionNumber; //commonData.reportNames.results

                if (sample.rid < 0) {
                    return;
                }

                $scope.sampleInformation = {
                    sampleRid: sample.rid,
                    timezoneOffset: timezoneOffset,
                    timezoneId: timezoneId,
                };

                sampleSeparationService.printSampleWorksheet($scope.sampleInformation).then(function (response) {
                    util.fileHandler(response.data, { type: "application/pdf", name: fileName });
                });
            };

            $scope.printAllWorksheets = function () {
                var fileName = $scope.visit.emrPatientInfo.fullName[util.userNamePrimary] + "-" + $scope.visit.admissionNumber; //commonData.reportNames.results

                $scope.visitInformation = {
                    visitRid: $scope.visit.rid,
                    timezoneOffset: timezoneOffset,
                    timezoneId: timezoneId,
                };

                sampleSeparationService.printAllWorksheets($scope.visitInformation).then(function (response) {
                    util.fileHandler(response.data, { type: "application/pdf", name: fileName });
                });
            };

            //reset
            $scope.$on('$destroy', function () {
                delete $rootScope.sampleSeparationVisitRid;
                delete $rootScope.sampleSeparationIsWizard;
            });

            function refreshAfterPrint(visit) {
                $scope.visit = visit;
            }



            $scope.appointmentCardDialog = function (ev) {
                $mdDialog.show({
                    controller: ["$scope", "$mdDialog", "visit", "refreshFunction", "refreshAfterPrint",
                        function ($scope, $mdDialog, visit, refreshFunction, refreshAfterPrint) {
                            $scope.visitRid = visit.rid;
                            $scope.visit = visit;
                            $scope.visitDate = new Date($scope.visit.visitDate);

                            $scope.info = {
                                visit: $scope.visit,
                                visitRid: $scope.visit.rid,
                                resultDate: null,
                                resultTime: $scope.visit.appointmentCardTime,
                                notes: $scope.visit.appointmentCardNotes,
                                timezoneOffset: new Date().getTimezoneOffset(),
                                timezoneId: new Date().toString().split('(')[1].slice(0, -1).replace('Daylight', 'Standard'),
                            };

                            if ($scope.visit.appointmentCardDate != null) {
                                $scope.info.resultDate = new Date($scope.visit.appointmentCardDate);
                            }

                            $scope.cancel = function () {
                                $mdDialog.cancel();

                            };
                            $scope.submit = function (invalid) {
                                if (invalid) {
                                    return;
                                }
                                else {
                                    util.setKendoTimeInDate($scope.info.resultDate, $scope.info.resultTime);
                                    //$scope.info.resultDate.setTime($scope.info.resultDate.getTime() - $scope.info.resultDate.getTimezoneOffset() * 60 * 1000);
                                    var wrapper = {
                                        visitRid: ($scope.info.visit.rid + ""),
                                        appointmentCardDate: $scope.info.resultDate,
                                        appointmentCardNotes: $scope.info.notes,
                                        appointmentCardTime: $scope.info.resultTime
                                    };
                                    orderFormService.updateVisitAppointment(wrapper).then(function () {
                                        refreshFunction().then(function (responseFromRefresh) {
                                            $scope.info.visit = responseFromRefresh;
                                            refreshAfterPrint($scope.info.visit);
                                            sampleSeparationService.generateAppointmentCard($scope.info).then(function (response) {
                                                util.fileHandler(response.data, { type: "application/pdf", name: commonData.reportNames.appointmentCard });
                                            });
                                        });

                                    });
                                }

                            };
                        }],
                    templateUrl: './' + config.lisDir + '/modules/dialogs/patient-card-dialog.html',
                    parent: angular.element(document.body),
                    targetEvent: ev,
                    clickOutsideToClose: true,
                    locals: {
                        visit: $scope.visit,
                        refreshFunction: refetchVisit,
                        refreshAfterPrint: refreshAfterPrint,
                    }
                }).then(function () { }, function () { });
            };
        }
    ]);
});
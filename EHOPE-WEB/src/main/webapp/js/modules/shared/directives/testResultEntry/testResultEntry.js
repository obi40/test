define(['app', 'util', 'config'], function (app, util, config) {
    'use strict';
    app.directive('testResultEntry', function () {
        return {
            restrict: 'E', // This means that it will be used as an element.
            replace: true,
            scope: {
                visit: "=visit",
                noEdit: "=?noEdit",
                filter: "=?filter",
                dialogAction: "=?dialogAction" //use this when in dialog { dialog: <the dialog object>, submit: will be bound below to the save function}
            },
            templateUrl: "./" + config.lisDir + "/modules/shared/directives/testResultEntry/test-result-entry-view.html",
            controller: ['$scope', '$timeout', '$q', 'testResultEntryService', 'patientProfileService', 'orderManagementService', 'lovService', 'antiMicrobialService', 'organismService',
                function ($scope, $timeout, $q, testResultEntryService, patientProfileService, orderManagementService, lovService, antiMicrobialService, organismService) {

                    var prerequisitesReady = false;
                    $scope.actualTests = [];
                    $scope.comprehensiveResults = {}; //ex: { "testCode_resultCode": { comprehensiveResult: actualCompResult, differentials: [array of actualDiffResults] } }
                    $scope.finalizeAll = {
                        value: false,
                        disabled: true
                    };
                    $scope.ratioPattern = /^(<|>)?\s*[0-9]+\s*:\s*[0-9]+$/;
                    $scope.$on('getActualResults', function (event, data) {
                        getTestActualListWithResultsByVisit(data.rid);
                    });

                    function getTestActualListWithResultsByVisit(visitRid) {
                        $scope.finalizeAll.value = false;//reset
                        var wrapper = {
                            visitRid: visitRid,
                            fetchedStatus: $scope.filter ? $scope.filter.fetchedStatus : null//custom status fetch,nullable
                        };

                        callAPI();
                        function callAPI() {
                            if (prerequisitesReady) {
                                testResultEntryService.getTestActualListWithResultsByVisit(wrapper)
                                    .then(function (response) {
                                        $scope.totalResults = 0;
                                        $scope.actualTests = [];
                                        var data = response.data;
                                        for (var i = 0; i < data.length; i++) {
                                            var testActual = data[i];
                                            if ($scope.filter && (($scope.filter.type === "sample" && testActual.labSample.rid !== $scope.filter.rid)
                                                || ($scope.filter.type === "test" && testActual.rid !== $scope.filter.rid))) {
                                                continue;
                                            }
                                            for (var j = 0; j < testActual.testDefinition.interpretations.length; j++) {
                                                testActual.testDefinition.interpretations[j].description = generateInterpretationDescription(testActual.testDefinition.interpretations[j]);
                                            }
                                            for (var j = 0; j < testActual.labTestActualResults.length; j++) {
                                                var currentActualResult = testActual.labTestActualResults[j];
                                                $scope.totalResults++;
                                                switch (currentActualResult.labResult.resultValueType.code) {
                                                    case "ORG":
                                                        getResultOrganismAndSensitivityDetails(currentActualResult);
                                                        break;
                                                    case "CE":
                                                        getTestCodedResultMappingsByResult(currentActualResult);
                                                        break;
                                                    case "QN_QL":
                                                        if (!testActual.allergyResultPattern) {
                                                            testActual.allergyResultPattern = generateDecimalValidator(testActual.testDefinition.allergyDecimals);
                                                        }
                                                        determineClassFromConcentration(currentActualResult, testActual.testDefinition.interpretations, testActual.testDefinition.allergyDecimals);
                                                        break;
                                                    case "QN":
                                                        currentActualResult.primaryResultPattern = generateDecimalValidator(currentActualResult.labResult.primaryDecimals);
                                                        if (currentActualResult.labResult.isComprehensive) {
                                                            var compCode = testActual.testDefinition.standardCode + "_" + currentActualResult.labResult.standardCode;
                                                            var tempComprehensiveResult = $scope.comprehensiveResults[compCode];
                                                            if (!tempComprehensiveResult) {
                                                                $scope.comprehensiveResults[compCode] = { comprehensiveResult: currentActualResult, differentials: [] };
                                                            } else {
                                                                tempComprehensiveResult.comprehensiveResult = currentActualResult;
                                                            }
                                                            currentActualResult.compCode = compCode;
                                                        } else if (currentActualResult.labResult.isDifferential) {
                                                            var compCode = testActual.testDefinition.standardCode + "_" + currentActualResult.labResult.comprehensiveResult.standardCode;
                                                            var tempComprehensiveResult = $scope.comprehensiveResults[compCode];
                                                            if (!tempComprehensiveResult) {
                                                                tempComprehensiveResult = $scope.comprehensiveResults[compCode] = { differentials: [] };
                                                            }
                                                            tempComprehensiveResult.differentials.push(currentActualResult);
                                                            currentActualResult.compCode = compCode;
                                                        }
                                                        break;
                                                    case "QN_SC":
                                                        currentActualResult.primaryResultPattern = generateDecimalValidator(currentActualResult.labResult.primaryDecimals);
                                                        break;
                                                }
                                            }
                                            $scope.actualTests.push(testActual);
                                        }
                                        if ($scope.dialogAction && $scope.totalResults === 0) {
                                            $scope.dialogAction.dialog.hide();
                                        }
                                    });
                            } else {
                                $timeout(function () {
                                    callAPI();
                                }, 1);
                            }
                        }
                    }

                    function generateInterpretationDescription(interpretation) {
                        var description = "";
                        if (interpretation.minConcentrationComparator) {
                            description += interpretation.minConcentrationComparator;
                        }
                        if (typeof interpretation.minConcentrationValue === "number") {
                            description += interpretation.minConcentrationValue;
                        }
                        if (typeof interpretation.minConcentrationValue === "number" &&
                            typeof interpretation.maxConcentrationValue === "number") {
                            description += " - ";
                        }
                        if (interpretation.maxConcentrationComparator) {
                            description += interpretation.maxConcentrationComparator;
                        }
                        if (typeof interpretation.maxConcentrationValue === "number") {
                            description += interpretation.maxConcentrationValue;
                        }
                        return description;
                    }

                    $q.all([
                        lovService.getLkpByClass({ "className": "LkpOrganismDetection" }),
                        lovService.getLkpByClass({ "className": "LkpOrganismSensitivity" }),
                        organismService.getOrganismList(),
                        antiMicrobialService.getAntiMicrobialList()
                    ])
                        .then(function (responses) {
                            $scope.organismDetectionData = responses[0];
                            breakDownSensitivityOptions(responses[1]);
                            $scope.organismList = responses[2].data;
                            $scope.antiMicrobialList = responses[3].data;
                            prerequisitesReady = true;
                        });

                    function breakDownSensitivityOptions(data) {
                        for (var i = 0; i < data.length; i++) {
                            switch (data[i].code) {
                                case "S":
                                    $scope.sensitivityS = data[i];
                                    break;
                                case "I":
                                    $scope.sensitivityI = data[i];
                                    break;
                                case "R":
                                    $scope.sensitivityR = data[i];
                                    break;
                            }
                        }
                    }

                    function findSensitivityUsingCode(code) {
                        switch (code) {
                            case "S":
                                return $scope.sensitivityS;
                            case "I":
                                return $scope.sensitivityI;
                            case "R":
                                return $scope.sensitivityR;
                        }
                        return null;
                    }

                    function generateDecimalValidator(primaryDecimals) {
                        var starterRegex = "((<=?)|(>=?)|(-))?";
                        var decimalRegex = '[0-9]+([.](?=[0-9])[0-9]{0,' + primaryDecimals + '})?';
                        var numberRegex = "[0-9]+";
                        var regex1 = decimalRegex + "(-" + decimalRegex + ")?";
                        var regex2 = starterRegex + decimalRegex;
                        var regex3 = numberRegex + ":" + numberRegex;
                        var regex4 = decimalRegex + "%";
                        var regex = new RegExp("^(" +
                            regex1 + "|" +
                            regex2 + "|" +
                            regex3 + "|" +
                            regex4 +
                            ")$");
                        return regex;
                    }

                    function getTestCodedResultMappingsByResult(actualResult) {
                        testResultEntryService.getTestCodedResultMappingsByResult(actualResult.labResult)
                            .then(function (response) {
                                var data = [];
                                for (var i = 0; i < response.data.length; i++) {
                                    var testCodedResult = response.data[i].testCodedResult;
                                    testCodedResult.textToView = testCodedResult.code + " | " + testCodedResult.value;
                                    data.push(testCodedResult);
                                }
                                actualResult.codedResultOptions = {
                                    className: "TestCodedResult",
                                    name: "testCodedResult",
                                    labelText: "codedResult",
                                    valueField: "textToView",
                                    required: !$scope.noEdit,
                                    selectedValue: actualResult.testCodedResult,
                                    data: data
                                };
                            });
                    }

                    $scope.organismListSearch = function (searchText) {
                        var filteredList = [];
                        for (var i = 0; i < $scope.organismList.length; i++) {
                            if ($scope.organismList[i].name.toLowerCase().indexOf(searchText.toLowerCase()) >= 0) {
                                filteredList.push($scope.organismList[i]);
                            }
                        }
                        return filteredList;
                    }

                    function getResultOrganismAndSensitivityDetails(actualResult) {
                        createOrganismDetectionLov(actualResult);
                        actualResult.actualAntiMicrobialList = angular.copy(actualResult.actualAntiMicrobials);
                        actualResult.actualOrganismList = angular.copy(actualResult.actualOrganisms);
                        for (var i = 0; i < actualResult.actualAntiMicrobialList.length; i++) {
                            actualResult.actualAntiMicrobialList[i].organismSensitivity = findSensitivityUsingCode(actualResult.actualAntiMicrobialList[i].organismSensitivity.code);
                        }
                        var len = actualResult.actualAntiMicrobials.length;
                        for (var i = 0; i < $scope.antiMicrobialList.length; i++) {
                            if (!doesResultContainAntiMicrobial($scope.antiMicrobialList[i], actualResult.actualAntiMicrobialList, len)) {
                                actualResult.actualAntiMicrobialList.push({
                                    antiMicrobial: $scope.antiMicrobialList[i],
                                    organismSensitivity: null
                                });
                            }
                        }
                        actualResult.actualAntiMicrobialList.sort(function (a, b) {
                            if (a.antiMicrobial.code < b.antiMicrobial.code) { return -1; }
                            if (a.antiMicrobial.code > b.antiMicrobial.code) { return 1; }
                            return 0;
                        })
                    }

                    function doesResultContainAntiMicrobial(antiMicrobial, actualAntiMicrobialList, len) {
                        for (var i = 0; i < len; i++) {
                            if (actualAntiMicrobialList[i].antiMicrobial.rid === antiMicrobial.rid) {
                                return true;
                            }
                        }
                        return false;
                    }

                    function createOrganismDetectionLov(actualResult) {
                        actualResult.organismDetectionOptions = {
                            className: "LkpOrganismDetection",
                            name: "organismDetection",
                            labelText: "organismDetection",
                            valueField: "name." + util.userLocale,
                            required: !$scope.noEdit,
                            selectedValue: actualResult.organismDetection,
                            data: $scope.organismDetectionData
                        };
                    }

                    $scope.addOrganism = function (actualResult) {
                        actualResult.actualOrganismList.push({
                            organism: "",
                            colonyCount: ""
                        });
                    }

                    $scope.deleteOrganism = function (actualOrganism, testForm, resultIndex) {
                        actualOrganism.markedForDeletion = true;
                        testForm.$$controls[resultIndex].$setDirty();
                    }

                    $scope.copyNarrativeTemplate = function (result, narrativeTemplate, resultForm) {
                        resultForm.narrativeText.$setDirty();
                        result.narrativeText = narrativeTemplate.text;
                    }

                    $scope.toggleNarrativeTemplates = function (result) {
                        result.showNarrativeTemplates = !result.showNarrativeTemplates;
                    }

                    getTestActualListWithResultsByVisit($scope.visit.rid);

                    $scope.saveResults = function () {
                        var actualResults = [];

                        for (var j = 0; j < $scope.mainResultForm.$$controls.length; j++) {
                            var testForm = $scope.mainResultForm.$$controls[j];
                            for (var i = 0; i < testForm.$$controls.length; i++) {
                                var childForm = testForm.$$controls[i];
                                if (childForm.$name === "toFinalize") {
                                    continue;
                                }
                                var element = childForm.$$element[0];
                                if (childForm.$dirty && childForm.$valid) {
                                    var parentIndex = element.getAttribute("parent-index");
                                    var childIndex = element.getAttribute("index");
                                    var actualTestCopy = angular.copy($scope.actualTests[parentIndex]);
                                    delete actualTestCopy.labTestActualResults;
                                    var actualResult = $scope.actualTests[parentIndex].labTestActualResults[childIndex];
                                    actualResult.labTestActual = actualTestCopy;
                                    if (actualResult.labResult.resultValueType.code === 'CE') {
                                        actualResult.testCodedResult = actualResult.codedResultOptions.selectedValue;
                                    } else if (actualResult.labResult.resultValueType.code === 'ORG') {
                                        actualResult.organismDetection = actualResult.organismDetectionOptions.selectedValue;
                                        for (var k = 0; k < actualResult.actualAntiMicrobialList.length; k++) {
                                            delete actualResult.actualAntiMicrobialList[k].actualResult;
                                        }
                                    }
                                    actualResults.push($scope.actualTests[parentIndex].labTestActualResults[childIndex]);
                                }
                            }
                        }

                        if (actualResults.length > 0) {
                            patientProfileService.editActualTestResults({ actualResults: actualResults, order: $scope.visit })
                                .then(function () {
                                    util.createToast(util.systemMessages.success, "success");
                                    getTestActualListWithResultsByVisit($scope.visit.rid);
                                    if ($scope.dialogAction) {
                                        $scope.dialogAction.dialog.hide();
                                    }
                                });
                        } else {
                            util.createToast(util.systemMessages.noValidValuesFound, "warning");
                        }
                    };

                    $scope.commentChanged = function (testCode, result, testForm) {
                        if (result.labResult.isDifferential || result.labResult.isComprehensive) {
                            var resultCode = result.labResult.comprehensiveResult.standardCode;
                            $scope.differentialPercentageChange(testCode, resultCode, testForm);
                        }
                    }

                    $scope.differentialValidatorMessages = { invalid: util.systemMessages.differentialValidation };

                    $scope.differentialPercentageChange = function (testCode, resultCode, testForm) {
                        var compObj = $scope.comprehensiveResults[testCode + "_" + resultCode];
                        compObj.totalPercentages = 0;
                        var totalValue = compObj.comprehensiveResult.primaryResultParsed;
                        compObj.comprehensiveResult.isValid = false;
                        var differentialResults = compObj.differentials;
                        for (var i = 0; i < differentialResults.length; i++) {
                            var diffResult = differentialResults[i];
                            if (typeof diffResult.percentage === "number") {
                                var primaryParsed = +parseFloat(diffResult.percentage / 100 * totalValue).toFixed(diffResult.labResult.primaryDecimals);
                                if (typeof totalValue !== "number") {
                                    primaryParsed = null;
                                } else {
                                    compObj.totalPercentages += diffResult.percentage;
                                }
                                diffResult.primaryResultValue = primaryParsed;
                                diffResult.primaryResultParsed = primaryParsed;
                                //make all differentials dirty because they need to be treated as a set
                                findFormByResultStandardCode(testForm.$$controls, diffResult.labResult.standardCode).$setDirty();
                            } else {
                                compObj.totalPercentages = 0;
                            }
                        }
                        findFormByResultStandardCode(testForm.$$controls, resultCode).$setDirty();
                        if (compObj.totalPercentages !== 100) {
                            testForm.differentialValidator.$setValidity("invalid", false); //invalid
                        } else {
                            testForm.differentialValidator.$setValidity("invalid", true); //valid
                        }
                    }

                    $scope.isPercentageInvalid = function (percentage) {
                        if (typeof percentage === 'number' && percentage !== 100) {
                            return true;
                        }
                        return false;
                    }

                    $scope.isComprehensiveInvalid = function (result) {
                        var totalPercentages = $scope.comprehensiveResults[result.compCode].totalPercentages;
                        var primaryResult = result.primaryResultParsed;
                        if (typeof primaryResult !== "number" && typeof totalPercentages === "number") {
                            return true;
                        }
                        return false;
                    }

                    function findFormByResultStandardCode(controls, standardCode) {
                        for (var i = 0; i < controls.length; i++) {
                            var control = controls[i];
                            if (control.$name === "differentialValidator") {
                                continue;
                            }
                            if (control.standardCode.$$lastCommittedViewValue === standardCode) {
                                return control;
                            }
                        }
                    }

                    $scope.enteringPrimaryResult = function (test, result, primaryDecimals, secondaryDecimals, testForm) {
                        try {
                            var extractedNumber = result.primaryResultValue.match(/-?\d+[.]?\d*/)[0];
                            var primaryParsed = +parseFloat(extractedNumber).toFixed(primaryDecimals);
                            result.primaryResultParsed = primaryParsed;
                            if (typeof secondaryDecimals === "number") {
                                var secondaryParsed = (primaryParsed * result.labResult.factor).toFixed(secondaryDecimals);
                                result.secondaryResultParsed = secondaryParsed;
                            }
                            if (test.interpretations.length > 0) {
                                determineClassFromConcentration(result, test.interpretations, primaryDecimals);
                            }
                        } catch (e) {
                            result.primaryResultParsed = null;
                            result.secondaryResultParsed = null;
                        }
                        if (result.labResult.isComprehensive) {
                            $scope.differentialPercentageChange(test.standardCode, result.labResult.standardCode, testForm);
                        }
                    };

                    function determineClassFromConcentration(result, interpretations, decimals) {
                        result.class = null;
                        var smallestAmount = decimals > 0 ? 1 : 0;
                        for (var i = 0; i < decimals; i++) {
                            smallestAmount /= 10;
                        }
                        var value = result.primaryResultParsed;
                        for (var i = 0; i < interpretations.length; i++) {
                            var minValue = interpretations[i].minConcentrationValue;
                            if (interpretations[i].minConcentrationComparator === ">") {
                                minValue = +(minValue + smallestAmount).toFixed(decimals);
                            }
                            var maxValue = interpretations[i].maxConcentrationValue;
                            if (interpretations[i].maxConcentrationComparator === "<") {
                                maxValue = +(maxValue - smallestAmount).toFixed(decimals);
                            }
                            if (typeof minValue === "number" && typeof maxValue === "number") {
                                if (value >= minValue && value <= maxValue) {
                                    result.class = interpretations[i].interpretationClass;
                                    break;
                                }
                            } else if (typeof minValue === "number") {
                                if (value >= minValue) {
                                    result.class = interpretations[i].interpretationClass;
                                    break;
                                }
                            } else if (typeof maxValue === "number") {
                                if (value <= maxValue) {
                                    result.class = interpretations[i].interpretationClass;
                                    break;
                                }
                            }
                        }
                    }

                    $scope.toggleInterpretationTable = function (test) {
                        test.showInterpretationTable = !test.showInterpretationTable;
                    }

                    $scope.toggleCheckAll = function () {
                        for (var idx = 0; idx < $scope.actualTests.length; idx++) {
                            $scope.actualTests[idx].toFinalize = $scope.finalizeAll.value;
                        }
                        $scope.finalizeAll.disabled = !$scope.finalizeAll.value;
                    };
                    $scope.checkFinalize = function () {
                        $scope.finalizeAll.disabled = true;
                        var checkedAll = true;
                        for (var idx = 0; idx < $scope.actualTests.length; idx++) {
                            if ($scope.actualTests[idx].toFinalize) {
                                if ($scope.finalizeAll.disabled) {
                                    $scope.finalizeAll.disabled = false;
                                }
                            } else {
                                checkedAll = false;
                            }
                        }
                        $scope.finalizeAll.value = checkedAll;
                    };

                    $scope.finalizeTests = function () {
                        var wrapper = {
                            propagateRids: [],
                            operationStatus: "FINALIZED",
                            type: "LabTestActual",
                            visitRid: $scope.visit.rid
                        };
                        for (var idx = 0; idx < $scope.actualTests.length; idx++) {
                            if ($scope.actualTests[idx].toFinalize) {
                                wrapper.propagateRids.push($scope.actualTests[idx].rid);
                            }
                        }
                        orderManagementService.changeSampleTestListStatus(wrapper)
                            .then(function () {
                                util.createToast(util.systemMessages.success, "success");
                                getTestActualListWithResultsByVisit($scope.visit.rid);
                                sendEmailResults();
                            });
                    };

                    function sendEmailResults() {
                        if ($scope.visit.paidAmount < $scope.visit.totalAmount) {
                            return;
                        }
                        var wrapper = {
                            visitRid: $scope.visit.rid,
                            testsMap: {},
                            emailMap: {
                                target: "PATIENT",
                                errorSeverity: "WARNING"
                            }
                        };
                        for (var idx = 0; idx < $scope.actualTests.length; idx++) {
                            wrapper.testsMap[$scope.actualTests[idx].rid] = true;
                        }
                        patientProfileService.generateVisitResultsEmail(wrapper);
                    }

                    if ($scope.dialogAction) {
                        if ($scope.dialogAction.type === "finalize") {
                            $scope.dialogAction.submit = $scope.finalizeTests;
                        } else {
                            $scope.dialogAction.submit = $scope.saveResults;
                        }
                    }
                }]
        }
    });
});
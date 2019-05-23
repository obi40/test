define(['app', 'util'], function (app, util) {
    'use strict';
    app.controller('patientLookupCtrl', ['$scope', '$state', '$rootScope', 'patientLookupService',
        function ($scope, $state, $rootScope, patientLookupService) {

            //#region socket
            $scope.fingerprintReadingState = "none";
            var sgSocket = null;

            $scope.findPatientRidByFingerprint = function () {
                $scope.fingerprintReadingState = "waiting";
                if (!sgSocket || sgSocket.readyState === 3) {
                    initWebSocket();
                    sgSocket.onopen = function (e) {
                        sgSocket.send(JSON.stringify({ type: "FINGERPRINT", functionName: "ACQUIRE_IMAGE" }));
                    }
                } else {
                    sgSocket.send(JSON.stringify({ type: "FINGERPRINT", functionName: "ACQUIRE_IMAGE" }));
                }
            }

            function initWebSocket() {
                if (sgSocket) {
                    sgSocket.close();
                }
                sgSocket = new WebSocket("ws://127.0.0.1:1234/ScanGatePlus");
                sgSocket.onmessage = function (e) {
                    $scope.fingerprintReadingState = "none";
                    var response = JSON.parse(e.data);
                    if (response.status === "ERROR") {
                        util.createToast(response.error.message, "error");
                    } else {
                        patientLookupService.findPatientRidByFingerprint(response.data)
                            .then(function (response) {
                                var filters = [{
                                    field: "rid",
                                    operator: "eq",
                                    value: response.data,
                                    junctionOperator: "And"
                                }];
                                patientAutocompleteCallback(filters);
                            });
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

            $scope.clearSearch = function () {
                patientAutocompleteCallback([]);
            }

            $scope.addPatient = function () {
                $state.go("patient-registration");
            };
            $scope.customRadioSearch = "currentWeek";
            $scope.patients = [];
            var fetchSize = 20;
            $scope.initialSearchObject = null;
            function populateInitSearch() {
                $scope.initialSearchObject = {
                    filters: [
                        {
                            field: "lastOrderDate",
                            value: null,// will be populated from server
                            operator: "gte",
                            junctionOperator: "And"
                        }
                    ],
                    page: 0,
                    size: fetchSize,
                    sortList: [{
                        direction: "DESC",
                        property: "lastOrderDate"
                    }]
                };
            }

            //#region quickSearch
            function generateQuickSearchObject(filters) {
                return {
                    filters: filters,
                    page: 0,
                    size: fetchSize,
                    sortList: [{
                        direction: "ASC",
                        property: "rid"
                    }]
                };
            }

            function patientAutocompleteCallback(filters) {
                resetRootScopeState();
                $rootScope.quickPatientSearch = { filters: filters, selectedItem: $scope.patientSearchOptions.selectedItem };
                if (filters.length === 0) {
                    $scope.radioFilterListener();
                } else {
                    $scope.patients = [];
                    var searchObject = {
                        filters: filters,
                        page: 0,
                        size: fetchSize,
                        sortList: [{
                            direction: "ASC",
                            property: "rid"
                        }]
                    };
                    $scope.loadOnScrollOptions.searchObject = searchObject;
                    $scope.loadOnScrollOptions.refillScreen();
                }
            }

            $scope.patientSearchOptions = {
                service: patientLookupService.getPatientLookupPage,
                callback: patientAutocompleteCallback,
                skeleton: {
                    code: "fullName",
                    description: "fullName",
                    image: "image"
                },
                dynamicLang: { code: true, description: true },
                disabled: $scope.advancedSearchBool,
                filterList: ["firstName", "secondName", "thirdName", "lastName", "fullName", "nationalId", "mobileNo", "secondaryMobileNo", "fileNo"]
            };
            //#endregion

            function resetRootScopeState() {
                $rootScope.quickPatientSearch = null;
                $rootScope.advancedPatientSearch = null;
                $rootScope.radioPatientSearch = null;
            }
            //this resets everything, disabling the preserve-search-state
            resetRootScopeState();

            //#region advancedSearch
            $scope.advancedSearchBool = false;
            $scope.showAdvancedSearch = function () {
                $scope.advancedSearchBool = !$scope.advancedSearchBool;
                $scope.patientSearchOptions.disabled = !$scope.patientSearchOptions.disabled;
            }
            $scope.advancedSearchQuery = {
                firstName: "",
                secondName: "",
                thirdName: "",
                lastName: "",
                nationalId: "",
                mobileNo: "",
                fileNo: ""
            };

            $scope.clearAdvancedSearch = function () {
                for (var key in $scope.advancedSearchQuery) {
                    if ($scope.advancedSearchQuery.hasOwnProperty(key)) {
                        $scope.advancedSearchQuery[key] = "";
                    }
                }
                $scope.advancedSearch();
            }

            function generateAdvancedSearchObject() {
                var filters = [];
                $rootScope.advancedPatientSearch = {};
                for (var key in $scope.advancedSearchQuery) {
                    if ($scope.advancedSearchQuery.hasOwnProperty(key)) {
                        if ($scope.advancedSearchQuery[key] != "") {
                            filters.push({
                                field: key,
                                value: $scope.advancedSearchQuery[key],
                                operator: "contains",
                                junctionOperator: "And"
                            });
                            $rootScope.advancedPatientSearch[key] = $scope.advancedSearchQuery[key];
                        }
                    }
                }
                if (filters.length === 0) {
                    $rootScope.advancedPatientSearch = null;
                }
                return {
                    filters: filters,
                    page: 0,
                    size: fetchSize,
                    sortList: [{
                        direction: "ASC",
                        property: "rid"
                    }]
                };
            }
            $scope.advancedSearch = function () {
                $scope.customRadioSearch = "all";
                resetRootScopeState();
                $scope.advancedSearchObject = generateAdvancedSearchObject();
                $scope.patients = [];
                $scope.loadOnScrollOptions.searchObject = $scope.advancedSearchObject;
                $scope.loadOnScrollOptions.refillScreen();
            }
            //#endregion



            $scope.showPager = false;
            var loadMore = function (searchObject) {
                $scope.showPager = false;
                return patientLookupService.getPatientLookupPage(searchObject)
                    .then(function (response) {
                        $scope.patients = $scope.patients.concat(response.data.content);
                        $scope.totalPatients = response.data.totalElements;
                        $scope.showPager = true;
                        $scope.loadOnScrollOptions.updateScrolling($scope.patients.length, response.data.totalElements, searchObject);
                    });
            }

            function generateRadioSearchObject() {
                var radioSearchObject = null;
                populateInitSearch();//reset
                if ($scope.customRadioSearch == "all") {
                    radioSearchObject = {
                        filters: [],
                        page: 0,
                        size: fetchSize,
                        sortList: [{
                            direction: "ASC",
                            property: "rid"
                        }]
                    };
                }
                else if ($scope.customRadioSearch == "currentWeek") {
                    radioSearchObject = angular.copy($scope.initialSearchObject);
                }
                else if ($scope.customRadioSearch == "earliestRegistrations") {
                    radioSearchObject = {
                        filters: [],
                        page: 0,
                        size: fetchSize,
                        sortList: [{
                            direction: "ASC",
                            property: "creationDate"
                        }]
                    };
                } else if ($scope.customRadioSearch == "latestRegistrations") {
                    radioSearchObject = {
                        filters: [],
                        page: 0,
                        size: fetchSize,
                        sortList: [{
                            direction: "DESC",
                            property: "creationDate"
                        }]
                    };
                }
                return radioSearchObject;
            }

            $scope.radioFilterListener = function () {
                resetRootScopeState();
                var radioSearchObject = generateRadioSearchObject();

                $rootScope.radioPatientSearch = $scope.customRadioSearch;

                $scope.patients = [];
                $scope.loadOnScrollOptions.searchObject = radioSearchObject;
                $scope.loadOnScrollOptions.refillScreen();
            }

            //#region preserve search state
            if ($rootScope.quickPatientSearch) {
                var filters = $rootScope.quickPatientSearch.filters;
                $scope.patientSearchOptions.selectedItem = $rootScope.quickPatientSearch.selectedItem;
                $scope.initialSearchObject = generateQuickSearchObject(filters);
            } else if ($rootScope.advancedPatientSearch) {
                $scope.customRadioSearch = "all";
                $scope.advancedSearchBool = true;
                $scope.patientSearchOptions.disabled = true;
                for (var key in $rootScope.advancedPatientSearch) {
                    if ($rootScope.advancedPatientSearch.hasOwnProperty(key)) {
                        $scope.advancedSearchQuery[key] = $rootScope.advancedPatientSearch[key];
                    }
                }
                $scope.advancedSearchObject = generateAdvancedSearchObject();
                $scope.initialSearchObject = $scope.advancedSearchObject;
            } else if ($rootScope.radioPatientSearch) {
                $scope.customRadioSearch = $rootScope.radioPatientSearch;
                $scope.initialSearchObject = generateRadioSearchObject();
            } else {
                populateInitSearch();
            }
            //#endregion

            $scope.loadOnScrollOptions = {
                callbackFn: loadMore,
                searchObject: $scope.initialSearchObject
            };
        }
    ])
});
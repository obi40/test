define(['app', 'util', 'config'], function (app, util, config) {
    //options.service: The API promise
    //options.callback: The callback function to call with the filters as parameter callback(filters)
    //                  Use this 'filters' to perform your API call
    //options.filterList: List of field names to filter [ "fieldName1", "fieldName2" ]
    //options.skeleton: The object keys to use. Ex: { code: "standardCode", description: "description", image: "image" }
    //options.disabled: A boolean or an object of type boolean
    //options.label: A custom label otherwise use default [OPTIONAL]
    //options.pageSize: Default is config.gridPageSizes[0] [OPTIONAL]
    //options.sortList: No sort is added by default. Ex: [{ direction: "ASC", property: "rid" }] [OPTIONAL]
    //options.staticFilters: List of SearchCriterion objects. Ex: [{ field: "", operator: "", value: "", junctionOperator: "And"}] [OPTIONAL]
    //options.dynamicLang: { code: true, description: true } Send an object with keys "code" and "description" 
    //                     Set each key value to true or false to dynamically determine the language for that key
    //                     WARNING: DO NOT SEND THE OBJECT FOR NON-TRANSLATABLE FIELDS
    //                     A suffix will be added to each key in the form "code.ar_jo"
    'use strict';
    app.directive('autocompleteSearch', ["$timeout", function ($timeout) {
        return {
            restrict: 'E',
            replace: true,
            templateUrl: "./" + config.lisDir + "/modules/shared/directives/autocompleteSearch/autocomplete-search.html",
            scope: {
                options: "=options"
            },
            link: function ($scope, $element) {
                var scrollerElement = $($element[0]).find(".md-virtual-repeat-scroller");
                var scrollTimer = null;
                angular.element(scrollerElement).bind('scroll', function () {
                    if (scrollerElement[0].scrollTop + scrollerElement[0].offsetHeight >= scrollerElement[0].scrollHeight
                        && $scope.autocompleteData.length < $scope.totalElements) {
                        if (scrollTimer) {
                            $timeout.cancel(scrollTimer);
                        }
                        scrollTimer = $timeout(function () {
                            $scope.pageNumber++;
                            $scope.autocompleteSearch($scope.options.searchText, true);
                        }, $scope.delayTime);
                    }
                });
            },
            controller: ['$scope', '$filter', function ($scope, $filter) {
                var languageSuffix = "";
                var quickSearchFilters = [];
                var pageSize = $scope.options.pageSize ? $scope.options.pageSize : 10;
                var lastQuickSearchText = "";
                $scope.autocompleteData = [];
                $scope.pageNumber = 0;
                $scope.totalElements = 0;
                $scope.delayTime = 1000;
                $scope.label = $scope.options.label ? $scope.options.label : "search";
                var quickSearchFiltersTemplate = function (query) {
                    if ($scope.options.dynamicLang) {
                        var arabicRegex = /[\u0600-\u06FF]/;
                        if (arabicRegex.test(query)) {
                            languageSuffix = "ar_jo";
                        } else {
                            languageSuffix = util.userLocale;
                        }
                    }
                    var filters = [];
                    if (query) {
                        for (var i = 0; i < $scope.options.filterList.length; i++) {
                            filters.push({
                                field: $scope.options.filterList[i],
                                operator: "contains",
                                value: query,
                                junctionOperator: "Or"
                            });
                        }
                        if ($scope.options.staticFilters) {
                            for (var i = 0; i < $scope.options.staticFilters.length; i++) {
                                filters.push($scope.options.staticFilters[i]);
                            }
                        }
                    }
                    return filters;
                }
                $scope.selectedItemChange = function (item) {
                    if (item) {
                        if (item.rid === -1) {
                            quickSearchFilters = quickSearchFiltersTemplate(item.autocompleteCode);
                        } else {
                            quickSearchFilters = [{
                                field: "rid",
                                operator: "eq",
                                value: item.rid,
                                junctionOperator: "And"
                            }];
                        }
                    } else {
                        quickSearchFilters = [];
                    }
                    // send the filters to the request to fetch data
                    $scope.options.callback(quickSearchFilters);
                }
                $scope.autocompleteSearch = function (searchText, isBottom) {
                    //isBottom = true means we called it from the scrolling event
                    if (!isBottom) {
                        //use this to prevent extra calls when clicking on an item [or search]
                        if (searchText === lastQuickSearchText) {
                            return new Promise(function (resolve, reject) {
                                resolve($scope.autocompleteData);
                            });
                        }
                        $scope.pageNumber = 0;//reset
                        $scope.totalElements = 0;//reset
                        $scope.autocompleteData = [];//reset
                        lastQuickSearchText = searchText;//set the new searchText 
                    } else {
                        lastQuickSearchText = searchText;//set the new searchText 
                    }
                    var filterablePageRequest = {
                        filters: [],
                        page: $scope.pageNumber,
                        size: pageSize,
                        sortList: []
                    };
                    var filters = quickSearchFiltersTemplate(searchText);
                    for (var i = 0; i < filters.length; i++) {
                        filterablePageRequest.filters.push(filters[i]);
                    }
                    if ($scope.options.sortList) {
                        for (var i = 0; i < $scope.options.sortList.length; i++) {
                            filterablePageRequest.sortList.push($scope.options.sortList[i]);
                        }
                    }
                    return $scope.options.service(filterablePageRequest)
                        .then(function (response) {
                            $scope.totalElements = response.data.totalElements;
                            if ($scope.autocompleteData.length == 0) {
                                var searchObj = { rid: -1 };
                                searchObj.searchLabel = $filter('translate')('search');
                                searchObj.autocompleteCode = searchText;
                                searchObj.autocompleteDescription = searchText;
                                $scope.autocompleteData.push(searchObj);
                            }
                            // must add them manually because using concat(...) will not work
                            for (var idx = 0; idx < response.data.content.length; idx++) {
                                $scope.autocompleteData.push(response.data.content[idx]);
                            }

                            //prepare keys for data access
                            var codeKey = $scope.options.skeleton.code;
                            var descKey = $scope.options.skeleton.description;
                            if ($scope.options.dynamicLang) {
                                if ($scope.options.dynamicLang.code) {
                                    codeKey += "." + languageSuffix;
                                }
                                if ($scope.options.dynamicLang.description) {
                                    descKey += "." + languageSuffix;
                                }
                            }

                            //start at 1 to skip modifying the searchObj
                            for (var i = 1; i < $scope.autocompleteData.length; i++) {
                                //flatten the data
                                var item = $scope.autocompleteData[i];
                                item.autocompleteCode = util.getDeepValueInObj(item, codeKey);
                                item.autocompleteDescription = util.getDeepValueInObj(item, descKey);
                                if (typeof $scope.options.isItemSelectable === "function") {
                                    item.isItemDisabled = !$scope.options.isItemSelectable(item);
                                }
                            }
                            return $scope.autocompleteData;
                        }).catch(function (response) {
                            return response;
                        });
                };
                //this will clear the search input box also it will call the callback fn with no filters (selectedItemChange(null))
                $scope.options["reset"] = function () {
                    $scope.options.searchText = null;
                    $scope.pageNumber = 0;
                    $scope.totalElements = 0;
                    $scope.autocompleteData = [];
                };
            }]
        }
    }]);
});
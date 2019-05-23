define(['app', 'util', 'config', 'commonData'], function (app, util, config, commonData) {
    'use strict';
    app.directive('testSelection', function () {
        return {
            restrict: 'E',
            replace: false,
            scope: {
                order: "=order",
                testActualList: "=testActualList",
                wiz: "=wiz" //named wiz to avoid conflict with the "wizard" directive [optional]
            },
            templateUrl: "./" + config.lisDir + "/modules/shared/directives/testSelection/test-selection-view.html",
            controller: ['$scope', 'testSelectionService', 'testDefinitionManagementService',
                'clientManagementService', '$mdDialog', '$filter', 'requestFormManagementService', 'testGroupManagementService',
                'paymentFormService',
                function ($scope, testSelectionService, testDefinitionManagementService,
                    clientManagementService, $mdDialog, $filter, requestFormManagementService, testGroupManagementService, paymentFormService) {

                    commonData.events.testSelectionReady = true;
                    $scope.activateDirective = false;
                    $scope.wizardProviderPlan = null;
                    $scope.patternPercent = config.regexpPercent;
                    $scope.patternNum = config.regexpNum;
                    $scope.primaryLang = util.userLocale;
                    $scope.sections = [];
                    $scope.requestForms = [];
                    $scope.mostRequestedTests = [];
                    $scope.packages = [];
                    $scope.profiles = [];
                    $scope.calcualtedPriceDetails = { total: 0 };
                    $scope.currency = util.userCurrency;
                    $scope.insurancePlanList = null;
                    $scope.allProviders = null;
                    $scope.originalTotalPrice = 0;
                    $scope.discountAmount = { value: null };
                    $scope.discountPercentage = { value: null };
                    $scope.isPercentage = true;
                    $scope.isAmount = true;
                    $scope.showSectionPager = false;
                    $scope.editOrderTests = null;
                    $scope.editOrderGroups = null;
                    var autocompleteFilters = [];
                    $scope.packagesSearch = { value: null };
                    $scope.profilesSearch = { value: null };
                    $scope.wrapper = {
                        selectedParentTab: 0,
                        selectedRequestForm: 0,
                        selectedSection: 0,
                        selectedRadio: "sections"
                    };
                    $scope.selectedTestChipsOptions = {
                        data: [],
                        label: "standardCode",
                        tooltip: "description"
                    };
                    $scope.$on(commonData.events.activateTestSelection, function (event, params) {
                        if (params != null) {
                            $scope.wizardProviderPlan = params.insProviderPlan;
                            $scope.editOrderTests = params.editOrderTests;
                            $scope.editOrderGroups = params.editOrderGroups;
                        }
                        if (!$scope.activateDirective) {
                            activateDirective();
                        }
                    });
                    function activateDirective() {
                        $scope.activateDirective = true;
                        $scope.testSearchOptions = {
                            service: testDefinitionManagementService.getTestDefinitionLookupWithDestinations,
                            callback: function (filters) {
                                var searchText = "";
                                if ($scope.testSearchOptions.selectedItem) {
                                    if ($scope.testSearchOptions.selectedItem.rid === -1) {
                                        autocompleteFilters = filters;
                                        searchText = $scope.testSearchOptions.selectedItem.autocompleteCode;
                                        getFilteredSectionList(searchText);
                                    } else {
                                        var testToSelect = angular.copy($scope.testSearchOptions.selectedItem);
                                        $scope.testSearchOptions.selectedItem = null;
                                        autocompleteFilters = [];
                                        singleTestSelectability(testToSelect);
                                        if (!testToSelect.isSelectable) {
                                            util.createToast(testToSelect.isSelectableCause, "warning");
                                            return;
                                        }
                                        $scope.toggleTestSelection(testToSelect, true);
                                        checkTabsTests();
                                    }
                                } else {
                                    if (autocompleteFilters.length !== filters.length) {
                                        getFilteredSectionList(searchText);
                                    }
                                }
                            },
                            isItemSelectable: singleTestSelectability,
                            skeleton: {
                                code: "standardCode",
                                description: "description"
                            },
                            filterList: ["description", "standardCode", "aliases", "secondaryCode"],
                            staticFilters: [
                                {
                                    field: "isActive",
                                    value: true,
                                    operator: "eq"
                                }
                            ]
                        };
                        if (!$scope.wiz) {
                            clientManagementService.getInsProviderList(false).then(function (data) {
                                $scope.allProviders = {
                                    className: "InsProvider",
                                    valueField: "customLabel",
                                    labelText: "insProvider",
                                    selectedValue: null,
                                    required: false,
                                    data: data
                                };
                            });
                            $scope.insurancePlanList = {
                                className: "InsProviderPlan",
                                name: "insProviderPlan",
                                labelText: "insProviderPlan",
                                valueField: ("name." + util.userLocale),
                                selectedValue: null,
                                required: false,
                                data: [],
                                onParentChange: function (selectedProvider) {
                                    $scope.insurancePlanList.selectedValue = null;
                                    if (selectedProvider == null) {
                                        $scope.calculatePrice();
                                        return;
                                    }
                                    var filterablePageRequest = {
                                        filters: [
                                            { field: "insProvider.rid", value: selectedProvider.rid, operator: "eq" }
                                        ]
                                    };
                                    return clientManagementService.getInsProviderPlanListByProvider(filterablePageRequest)
                                        .then(function (response) {
                                            $scope.insurancePlanList.data = response.data;
                                            if (selectedProvider.isSimple) {
                                                $scope.insurancePlanList.selectedValue = response.data[0];
                                                $scope.calculatePrice();
                                            }
                                        });
                                }
                            };
                        }
                        getFilteredSectionList("");
                        requestFormManagementService.getRequestForms([{ field: "isActive", value: true, operator: "eq" }]).then(function (response) {
                            var requestForms = response.data;
                            for (var i = 0; i < requestForms.length; i++) {
                                requestForms[i].sections = [];
                            }
                            $scope.requestForms = requestForms;
                        });
                        function resetDiscountValues() {
                            $scope.discountAmount = { value: null };
                            $scope.discountPercentage = { value: null };
                            $scope.isPercentage = true;
                            $scope.isAmount = true;
                        }
                        $scope.showPricingDetails = function (event) {
                            $mdDialog.show({
                                controller: ["$scope", "$mdDialog", "calcualtedPriceDetails", "discount",
                                    function ($scope, $mdDialog, calcualtedPriceDetails, discount) {
                                        $scope.calcualtedPriceDetails = calcualtedPriceDetails;
                                        $scope.total = $scope.calcualtedPriceDetails.total;
                                        $scope.testCount = $scope.calcualtedPriceDetails.result.length;
                                        $scope.flatDetails = [];
                                        $scope.printAll = true;
                                        $scope.nonPicked = false;
                                        $scope.discount = discount;
                                        $scope.userCurrency = util.userCurrency;
                                        var dummyId = 0;
                                        //formatting data
                                        for (var idx = 0; idx < $scope.calcualtedPriceDetails.result.length; idx++) {
                                            var pw = $scope.calcualtedPriceDetails.result[idx];
                                            pw["dummyId"] = --dummyId;
                                            pw["isPrint"] = $scope.printAll;
                                            for (var i = 0; i < pw.paymentInformationList.length; i++) {
                                                var pi = pw.paymentInformationList[i];
                                                var obj = {};
                                                if (i === 0) {
                                                    obj["piLength"] = pw.paymentInformationList.length;
                                                    obj["tdDesc"] = pw.testDefinition.description;
                                                    obj["tdStandardCode"] = "(" + pw.testDefinition.standardCode + ") ";
                                                    obj["isPrint"] = $scope.printAll;
                                                    obj["dummyId"] = dummyId;
                                                }
                                                obj["cptCode"] = pi.billMasterItem.cptCode;
                                                obj["pricing"] = pi.billPricing ? pi.billPricing.price : '';
                                                obj["charge"] = pi.charge ? pi.charge : '';
                                                if (pi.percentage) {
                                                    obj["percentage"] = (100 - pi.percentage) + "%";
                                                } else if (pi.billPricing && pi.billPricing.price) {
                                                    obj["percentage"] = commonData.na;
                                                }
                                                if (pi.groupPercentage) {
                                                    obj["groupPercentage"] = (100 - pi.groupPercentage) + "%";
                                                } else if (pi.billPricing && pi.billPricing.price) {
                                                    obj["groupPercentage"] = commonData.na;
                                                }
                                                obj["needAuthorization"] = util.systemMessages.no;
                                                if (pi.insCoverageDetail) {
                                                    obj["needAuthorization"] = pi.insCoverageDetail.needAuthorization ?
                                                        util.systemMessages.yes : util.systemMessages.no;
                                                }
                                                if (pi.billPricing == null) {
                                                    obj["pricing"] = commonData.na;
                                                    obj["needAuthorization"] = commonData.na;
                                                    obj["percentage"] = commonData.na;
                                                    obj["charge"] = commonData.na;
                                                }
                                                $scope.flatDetails.push(obj);
                                            }
                                        }
                                        $scope.printAllListener = function () {

                                            for (var idx = 0; idx < $scope.flatDetails.length; idx++) {
                                                $scope.flatDetails[idx].isPrint = $scope.printAll;
                                                // updating the list where we print from 
                                                for (var i = 0; i < $scope.calcualtedPriceDetails.result.length; i++) {
                                                    if ($scope.flatDetails[idx].dummyId &&
                                                        $scope.flatDetails[idx].dummyId === $scope.calcualtedPriceDetails.result[i].dummyId) {
                                                        $scope.calcualtedPriceDetails.result[i].isPrint = $scope.printAll;
                                                        break;
                                                    }
                                                }
                                            }
                                            updateUIValues();
                                        };
                                        $scope.printListener = function (obj) {
                                            // updating the list where we print from 
                                            for (var idx = 0; idx < $scope.calcualtedPriceDetails.result.length; idx++) {
                                                if (obj.dummyId === $scope.calcualtedPriceDetails.result[idx].dummyId) {
                                                    $scope.calcualtedPriceDetails.result[idx].isPrint = obj.isPrint;
                                                    break;
                                                }
                                            }
                                            updateUIValues();
                                            //change the print all value depending on if there any selection left
                                            $scope.printAll = !$scope.nonPicked;
                                        };
                                        function updateUIValues() {
                                            var total = 0;
                                            $scope.nonPicked = true;
                                            for (var idx = 0; idx < $scope.calcualtedPriceDetails.result.length; idx++) {
                                                if ($scope.calcualtedPriceDetails.result[idx].isPrint) {
                                                    total += $scope.calcualtedPriceDetails.result[idx].paymentInfoTotal;
                                                    $scope.nonPicked = false;
                                                }
                                            }
                                            $scope.total = total;
                                            var count = 0;
                                            for (var idx = 0; idx < $scope.calcualtedPriceDetails.result.length; idx++) {
                                                if ($scope.calcualtedPriceDetails.result[idx].isPrint) {
                                                    count += 1;
                                                }
                                            }
                                            $scope.testCount = count;
                                        }

                                        $scope.cancel = function () {
                                            $mdDialog.cancel();
                                        };
                                        $scope.printPayment = function () {
                                            var mywindow = window.open('', '_blank', 'height=1000px,width=1000px');
                                            function pricingResultHtml() {
                                                var result = "<tbody>";
                                                for (var paymentWrapperKey in $scope.calcualtedPriceDetails.result) {
                                                    var wrapperObj = $scope.calcualtedPriceDetails.result[paymentWrapperKey];
                                                    if (!wrapperObj.isPrint) {
                                                        continue;
                                                    }
                                                    for (var payKey in wrapperObj.paymentInformationList) {
                                                        var payObj = wrapperObj.paymentInformationList[payKey];
                                                        var innerHtml = "<tr>";
                                                        if (payKey == 0) {
                                                            innerHtml = "<tr><td rowspan='" + wrapperObj.paymentInformationList.length + "'>" + wrapperObj.testDefinition.description + "</td>";
                                                        }
                                                        innerHtml = innerHtml.concat("<td>" + payObj.billMasterItem.cptCode + "</td>");
                                                        innerHtml = innerHtml.concat("<td>" + (payObj.billPricing ? payObj.billPricing.price : '') + "</td>");
                                                        if (payObj.percentage) {
                                                            innerHtml = innerHtml.concat("<td>" + (100 - payObj.percentage) + "%</td>");
                                                        } else if (payObj.billPricing && payObj.billPricing.price) {
                                                            innerHtml = innerHtml.concat("<td>" + commonData.na + "</td>");
                                                        }
                                                        if (payObj.groupPercentage) {
                                                            innerHtml = innerHtml.concat("<td>" + (100 - util.round(payObj.groupPercentage)) + "%</td>");
                                                        } else if (payObj.billPricing && payObj.billPricing.price) {
                                                            innerHtml = innerHtml.concat("<td>" + commonData.na + "</td>");
                                                        }

                                                        innerHtml = innerHtml.concat("<td>" + (payObj.charge ? payObj.charge : '') + "</td></tr>");
                                                        result = result.concat(innerHtml);
                                                    }
                                                }
                                                result = result.concat("</tbody>");
                                                return result;
                                            }
                                            var tableBody = pricingResultHtml();
                                            var part1 = "<!DOCTYPE html>" +
                                                "<html>" +
                                                "<head>" +
                                                "    <meta charset=\"UTF-8\">" +
                                                "    <title>Title</title>" +
                                                "    <style>" +
                                                "       @page {size:  auto;margin: 0mm;}" +
                                                "        body {" +
                                                "            height: 100%;" +
                                                "            width: 100%;" +
                                                "            padding: 0px;" +
                                                "            margin: 0px;" +
                                                "        }" +
                                                "        .left-panel {" +
                                                "            float: left;" +
                                                "            padding-left: 10px;" +
                                                "            padding-top: 5px;" +
                                                "            padding-bottom: 10px;" +
                                                "            font-weight: bold;" +
                                                "        }" +
                                                "        .right-panel {" +
                                                "            float: right;" +
                                                "            padding-right: 10px;" +
                                                "            padding-top: 5px;" +
                                                "            font-weight: bold;" +
                                                "        }" +
                                                "        .patient-name div {" +
                                                "            font-weight: bold;" +
                                                "        }" +
                                                "        .notes {" +
                                                "            font-weight: bold;" +
                                                "            padding-left: 10px;" +
                                                "        }" +
                                                "        .dots {" +
                                                "            margin-left: 10px;" +
                                                "            margin-right: 10px;" +
                                                "            border-bottom: 2px dotted black;" +
                                                "        }" +
                                                "        img {" +
                                                "            display: block;" +
                                                "            margin-right: auto;" +
                                                "            margin-left: auto;" +
                                                "            padding-top: 5px;" +
                                                "        }" +
                                                "        table," +
                                                "        td," +
                                                "        th {" +
                                                "            border: 1px solid black;" +
                                                "            text-align: center;" +
                                                "        }" +
                                                "        table {" +
                                                "            border-collapse: collapse;" +
                                                "            width: calc(100% - 20px);" +
                                                "            margin: auto;" +
                                                "        }" +
                                                "        th {" +
                                                "            background-color: white;" +
                                                "            color: black;" +
                                                "        }" +
                                                "        tfoot tr td {" +
                                                "            font-weight: bold;" +
                                                "        }" +
                                                "        th," +
                                                "        td {" +
                                                "            padding: 5px;" +
                                                "            width: 20%;" +
                                                "        }" +
                                                "    </style>" +
                                                "</head>" +
                                                "<body>" +
                                                "    <div style='padding-top:10px'>" +
                                                "        <div>" +
                                                "            <div class=\"left-panel\">" +
                                                "                <div>" +
                                                "                    " + $filter('translate')('lab') + ": The Lab" +
                                                "                </div>" +
                                                "                <div>" +
                                                "                    " + $filter('translate')('address') + ": 6th circle" +
                                                "                </div>" +
                                                "                <div>" +
                                                "                    " + $filter('translate')('city') + ": Amman" +
                                                "                </div>" +
                                                "                <div>" +
                                                "                    " + $filter('translate')('phone') + ": 0796641555" +
                                                "                </div>" +
                                                "            </div>" +
                                                "            <div class=\"right-panel\">" +
                                                "                <div>" +
                                                "                </div>" +
                                                "                <div>" +
                                                "                </div>" +
                                                "                <br>" +
                                                "            </div>" +
                                                "        </div>" +
                                                "        <div class=\"left-panel\">" +
                                                "            <div></div>" +
                                                "            <br>" +
                                                "            <div></div>" +
                                                "            <div></div>" +
                                                "        </div>" +
                                                "                <div class=\"right-panel\">" +
                                                "                    <div class=\"patient-name\">" +
                                                "                        <div></div>" +
                                                "                        <div></div>" +
                                                "                        <div></div>" +
                                                "                    </div>" +
                                                "                </div>" +
                                                "        <div>" +
                                                "            <table>" +
                                                "                <thead>" +
                                                "                    <tr>" +
                                                "                        <th>" + $filter('translate')('description') + "</th>" +
                                                "                        <th>" + $filter('translate')('cptCode') + "</th>" +
                                                "                        <th>" + $filter('translate')('originalPrice') + "</th>" +
                                                "                        <th>" + $filter('translate')('coInsurance') + "</th>" +
                                                "                        <th>" + $filter('translate')('coGroupPercentage') + "</th>" +
                                                "                        <th>" + $filter('translate')('fees') + "</th>" +
                                                "                    </tr>" +
                                                "                </thead>";
                                            var part2 =
                                                "                <tfoot>" +
                                                "                    <tr>" +
                                                "                        <th colspan=\"5\">" + $filter('translate')('total') + "</th>" +
                                                "                        <td>" + $scope.total + "</td>" +
                                                "                    </tr>" +
                                                "                </tfoot>" +
                                                "            </table>" +
                                                "        </div>" +
                                                "        <br/>" +
                                                "        <div class=\"notes\">Notes:</div>" +
                                                "        <br/>" +
                                                "        <div class=\"dots\"></div>" +
                                                "        <br/>" +
                                                "        <div class=\"dots\"></div>" +
                                                "        <br/>" +
                                                "        <div class=\"dots\"></div>" +
                                                "        <br/>" +
                                                "        <div class=\"dots\"></div>" +
                                                "        <br/>" +
                                                "        <div class=\"dots\"></div>" +
                                                "        <br/>" +
                                                "        <div class='left-panel'>" +
                                                "        <div style='font-size: 12px;'>" + $filter('translate')('printedBy') + ": " + util.user.firstName[util.userLocale] + " " + util.user.lastName[util.userLocale] + "</div>" +
                                                "        </div>" +
                                                "        <div class='right-panel'>" +
                                                "        <div style='font-size: 12px;'>" + $filter('translate')('printDate') + ": " + $filter('dateFormat')(new Date()) + "</div>" +
                                                "        </div>" +
                                                "</body>" +
                                                "<script type='text/javascript'>" +
                                                "window.onload = function() { window.print();window.close(); }" +
                                                "</script>" +
                                                "</html>";
                                            var result = part1 + tableBody + part2;

                                            mywindow.document.write(result);

                                            mywindow.document.close();
                                        };
                                    }],
                                templateUrl: './' + config.lisDir + '/modules/dialogs/pricing-details.html',
                                parent: angular.element(document.body),
                                targetEvent: event,
                                clickOutsideToClose: true,
                                locals: {
                                    calcualtedPriceDetails: angular.copy($scope.calcualtedPriceDetails),
                                    discount: $scope.isAmount ? $scope.discountAmount.value : $scope.discountPercentage.value + "%"
                                }
                            }).then(function () {

                            }, function () {

                            });
                        };
                        $scope.discountPercentDeduct = function (discountErrors, discountValue, isPercentage) {
                            if (Object.keys(discountErrors).length > 1 || !discountValue || $scope.originalTotalPrice == 0) {
                                $scope.calcualtedPriceDetails.total = $scope.originalTotalPrice;
                                resetDiscountValues();
                                return;
                            }
                            //to know if it is a percent or amount
                            if (isPercentage) {
                                $scope.calcualtedPriceDetails.total = ((100.00 - discountValue) / 100.00) * $scope.originalTotalPrice;
                                $scope.discountAmount["value"] = util.round($scope.originalTotalPrice - $scope.calcualtedPriceDetails.total);
                                $scope.isPercentage = true;
                                $scope.isAmount = false;
                            } else {
                                var discountAmount = util.round(discountValue / $scope.originalTotalPrice, 10);//in percentage
                                $scope.discountPercentage["value"] = util.round(discountAmount * 100);
                                $scope.calcualtedPriceDetails.total = $scope.originalTotalPrice - (discountAmount * $scope.originalTotalPrice);
                                $scope.isPercentage = false;
                                $scope.isAmount = true;
                            }
                            $scope.calcualtedPriceDetails.total = util.round($scope.calcualtedPriceDetails.total);
                            if (isNaN($scope.calcualtedPriceDetails.total)) {
                                $scope.calcualtedPriceDetails.total = $scope.originalTotalPrice;
                            }

                        };
                        function getSelectedInsPlan() {
                            if ($scope.wiz) {
                                return $scope.wizardProviderPlan;
                            } else {
                                return $scope.allProviders.selectedValue != null && $scope.insurancePlanList != null ?
                                    $scope.insurancePlanList.selectedValue : null
                            }
                        }
                        $scope.calculatePrice = function () {
                            if ($scope.wiz) {
                                return;
                            }
                            if ($scope.selectedTestChipsOptions.data.length < 1) {
                                $scope.calcualtedPriceDetails.total = 0;
                                resetDiscountValues();
                                return;
                            }

                            var testPricingWrapper = {
                                testDefinitionList: $scope.selectedTestChipsOptions.data,
                                insProviderPlan: getSelectedInsPlan(),
                                testGroupList: getSelectedGroups()
                            };
                            paymentFormService.getTestsPricingNoVisit(testPricingWrapper).then(function (response) {
                                $scope.originalTotalPrice = response.data.total;
                                $scope.calcualtedPriceDetails = response.data;
                                resetDiscountValues();
                                checkTestsInsPricing();
                            });
                        };
                        function getFilteredSectionList(searchQuery) {
                            for (var i = 0; i < $scope.sections.length; i++) {
                                var section = $scope.sections[i];
                                section.testDataSource = null;
                            }
                            testSelectionService.getFilteredSectionList(searchQuery).then(function (response) {
                                $scope.sections = response.data;
                                for (var i = 0; i < $scope.sections.length; i++) {
                                    var section = $scope.sections[i];
                                    section.initialRequest = false;
                                    addDataSourceToSection(section);
                                    if (i === 0) {
                                        section.initialRequest = true;
                                        section.testDataSource.read();
                                    }
                                }
                            });
                        }
                        function addDataSourceToSection(section) {
                            section.testDataSource = new kendo.data.DataSource({
                                pageSize: config.testSelectionPageSize,
                                page: 1,
                                transport: {
                                    read: function (e) {
                                        e.data = util.createFilterablePageRequest(section.testDataSource);
                                        var sectionFilter = {
                                            field: "section",
                                            value: section.rid,
                                            operator: "eq",
                                            junctionOperator: "And"
                                        };
                                        e.data.filters = [sectionFilter];
                                        if ($scope.testSearchOptions.selectedItem) {
                                            if ($scope.testSearchOptions.selectedItem.rid === -1) {
                                                for (var k = 0; k < autocompleteFilters.length; k++) {
                                                    e.data.filters.push(autocompleteFilters[k]);
                                                }
                                            }
                                        } else {
                                            e.data.filters.push({
                                                field: "isActive",
                                                value: true,
                                                operator: "eq"
                                            });
                                        }
                                        $scope.showSectionPager = false;
                                        testDefinitionManagementService.getSelectableTestDefinitionPage(e.data)
                                            .then(function (response) {
                                                e.success(response.data);
                                                if (section.testDefinitionList === null) {
                                                    section.testDefinitionList = [];
                                                }
                                                section.testDefinitionList = section.testDefinitionList.concat(determineTestListSelectability(response.data.content));
                                                checkSelectedTests(section.testDefinitionList);
                                                $scope.showSectionPager = true;
                                            }, function (response) {
                                                e.error(response);
                                            });
                                    }
                                },
                                serverPaging: true,
                                serverFiltering: true,
                                serverSorting: true,
                                sort: { field: "rank", dir: "asc" },
                                schema: {
                                    total: "totalElements",
                                    data: "content",
                                    model: {
                                        id: "rid",
                                        fields: {
                                            rid: { type: "number" }
                                        }
                                    }
                                }
                            });
                        }
                        function getRequestFormTestsWithDestinations(requestForm) {
                            requestFormManagementService.getRequestFormTestsWithDestinations(requestForm.rid)
                                .then(function (response) {
                                    for (var i = 0; i < response.data.length; i++) {
                                        determineTestListSelectability(response.data[i].testDefinitionList);
                                    }
                                    requestForm.sections = response.data;
                                    for (var i = 0; i < requestForm.sections.length; i++) {
                                        checkSelectedTests(requestForm.sections[i].testDefinitionList);
                                    }
                                });
                        }
                        function determineTestListSelectability(tests, isGroup) {
                            var groupSelectable = true;
                            for (var i = 0; i < tests.length; i++) {
                                var testDefinition = tests[i];
                                if (isGroup) {
                                    testDefinition = testDefinition.testDefinition;
                                }
                                singleTestSelectability(testDefinition);
                                if (isGroup && !testDefinition.isSelectable) {
                                    groupSelectable = false;
                                }
                            }
                            if (isGroup) {
                                return groupSelectable;
                            }
                            return tests;
                        }
                        function singleTestSelectability(testDefinition) {
                            var canSelect = false;//determine if this test is selectable or not
                            var cause = [];//the message to display depending on the error,array bcz we can have multiple messages
                            //is destination valid
                            var validDestination = false;
                            for (var j = 0; j < testDefinition.destinations.length; j++) {
                                if (testDefinition.destinations[j].isActive && testDefinition.destinations[j].source.insuranceBranch.rid === util.user.branchId) {
                                    validDestination = true;
                                    break;
                                }
                            }
                            if (validDestination) {
                                canSelect = true;
                            } else {
                                canSelect = false;
                                cause.push($filter("translate")("noValidDestination"));
                            }
                            //is this test already in the order
                            if ($scope.editOrderTests != null && $scope.editOrderTests.length > 0) {
                                for (var idx = 0; idx < $scope.editOrderTests.length; idx++) {
                                    if (!$scope.editOrderTests[idx].isCancelled && $scope.editOrderTests[idx].rid === testDefinition.rid) {
                                        canSelect = false;
                                        cause.push($filter("translate")("selected"));
                                        break;
                                    }
                                }
                            }
                            var isSelectableCause = "";
                            for (var idx = 0; idx < cause.length; idx++) {
                                isSelectableCause += (cause[idx] + (idx + 1 !== cause.length ? "/" : ""));
                            }
                            testDefinition.isSelectable = canSelect;
                            testDefinition.isSelectableCause = isSelectableCause;
                            return testDefinition.isSelectable;//for autocomplete search callback
                        }
                        $scope.radioSelectionListener = function () {
                            switch ($scope.wrapper.selectedRadio) {
                                case "sections":
                                    break;
                                case "requestForms":
                                    break;
                                case "mostRequested":
                                    onMostRequestedTests();
                                    break;
                                case "packages":
                                case "profiles":
                                    getTestgroups();
                                    break;
                            }
                        };
                        $scope.onSectionTabSelect = function (section) {
                            if (!section.initialRequest && section.testDataSource !== null) {
                                section.testDataSource.read();
                                section.initialRequest = true;
                            }
                            if (section.testDefinitionList !== null) {
                                checkSelectedTests(section.testDefinitionList);
                            }
                        };
                        $scope.onRequestFormTab = function (requestForm) {
                            if (requestForm.sections.length === 0) {
                                getRequestFormTestsWithDestinations(requestForm);
                            } else {
                                for (var i = 0; i < requestForm.sections.length; i++) {
                                    checkSelectedTests(requestForm.sections[i].testDefinitionList);
                                }
                            }
                        };
                        $scope.searchGroups = function (searchObj, group) {
                            if (!searchObj || !searchObj.value) {
                                return true;
                            }
                            if (group.name.toLowerCase().indexOf(searchObj.value.toLowerCase()) != -1) {
                                return true;
                            } else {
                                return false;
                            }
                        };
                        function getTestgroups() {
                            if ($scope.packages.length === 0 && $scope.profiles.length === 0) {
                                testGroupManagementService.getTestGroupsWithDestinations().then(function (response) {
                                    var data = response.data;
                                    for (var i = 0; i < data.length; i++) {
                                        data[i].isSelectable = !!determineTestListSelectability(data[i].groupDefinitions, true);
                                        // if ($scope.editOrderGroups != null && $scope.editOrderGroups.length > 0) {
                                        //     for (var idx = 0; idx < $scope.editOrderGroups.length; idx++) {
                                        //         if ($scope.editOrderGroups[idx].rid === data[i].rid) {
                                        //             data[i].isSelectable = false;
                                        //             console.log("Already Selected Group: " + data[i].name);
                                        //             break;
                                        //         }
                                        //     }
                                        // }
                                        if (data[i].isProfile) {
                                            $scope.profiles.push(data[i]);
                                        } else {
                                            $scope.packages.push(data[i]);
                                        }
                                    }
                                });
                            }
                        }
                        function onMostRequestedTests() {
                            if ($scope.mostRequestedTests.length === 0) {
                                testSelectionService.getMostRequestedTests(20)
                                    .then(function (response) {
                                        $scope.mostRequestedTests = determineTestListSelectability(response.data);
                                        checkSelectedTests($scope.mostRequestedTests);
                                    });
                            } else {
                                checkSelectedTests($scope.mostRequestedTests);
                            }
                        }
                        function checkSelectedTests(testList) {
                            for (var k = 0; k < testList.length; k++) {
                                var test = testList[k];
                                var idx = $scope.selectedTestChipsOptions.data.map(function (t) {
                                    return t.rid;
                                }).indexOf(test.rid);
                                if (idx > -1) {
                                    test.isChecked = true;
                                } else {
                                    test.isChecked = false;
                                }
                            }
                        }
                        $scope.isRemovable = function (test) {
                            var selectedGroups = getSelectedGroups();
                            if (selectedGroups.length === 0) {
                                return true;
                            } else {
                                var canRemove = true;
                                OUTER: for (var idx = 0; idx < selectedGroups.length; idx++) {
                                    var group = selectedGroups[idx];
                                    if (!group.isProfile && group.isChecked) {
                                        for (var i = 0; i < group.groupDefinitions.length; i++) {
                                            if (group.groupDefinitions[i].testDefinition.rid === test.rid) {
                                                canRemove = false;
                                                break OUTER;
                                            }
                                        }
                                    }
                                }
                                return canRemove;
                            }
                        };
                        function checkSelectedGroups() {
                            var testGroups = $scope.packages.concat($scope.profiles);
                            for (var idx = 0; idx < testGroups.length; idx++) {
                                var allTestsExists = true;
                                for (var i = 0; i < testGroups[idx].groupDefinitions.length; i++) {
                                    var testIndex = $scope.selectedTestChipsOptions.data.map(function (t) {
                                        return t.rid;
                                    }).indexOf(testGroups[idx].groupDefinitions[i].testDefinition.rid);
                                    if (testIndex == -1) {
                                        allTestsExists = false;
                                        break;
                                    }
                                }
                                if (!allTestsExists) {
                                    testGroups[idx].isChecked = false;
                                }
                            }

                        }
                        function getSelectedGroups() {
                            var testGroups = $scope.packages.concat($scope.profiles);
                            var selectedGroups = [];
                            for (var idx = 0; idx < testGroups.length; idx++) {
                                if (testGroups[idx].isChecked) {
                                    selectedGroups.push(testGroups[idx]);
                                }
                            }
                            if ($scope.editOrderGroups != null && $scope.editOrderGroups.length > 0) {
                                selectedGroups = selectedGroups.concat($scope.editOrderGroups);
                            }
                            return selectedGroups;
                        }
                        $scope.toggleTestSelection = function (test, checkFlag) {
                            //accepts single test or array of tests in case of groups
                            var toggledTests = [];

                            //its a profile or package
                            if (test.hasOwnProperty('groupDefinitions') && Array.isArray(test.groupDefinitions)) {
                                if (test.isSelectable) {
                                    for (var i = 0; i < test.groupDefinitions.length; i++) {
                                        toggledTests.push(test.groupDefinitions[i].testDefinition);
                                    }
                                }
                            } else {
                                toggledTests.push(test);
                            }
                            for (var i = 0; i < toggledTests.length; i++) {
                                var idx = $scope.selectedTestChipsOptions.data.map(function (t) {
                                    return t.rid;
                                }).indexOf(toggledTests[i].rid);
                                if (idx > -1) {
                                    //deselect
                                    if (!checkFlag) {
                                        //if checkFlag is true, do not deselect
                                        removeFromTestActualList(toggledTests[i]);
                                        $scope.selectedTestChipsOptions.data.splice(idx, 1);
                                        toggledTests[i].isChecked = false;
                                    }
                                } else {
                                    //select
                                    toggledTests[i].isChecked = true;
                                    $scope.selectedTestChipsOptions.data.push(toggledTests[i]);
                                }
                            }
                            if (!checkFlag) {
                                checkSelectedGroups();
                            }
                            $scope.calculatePrice();
                        };

                        function removeFromTestActualList(test) {
                            if ($scope.testActualList == null || $scope.testActualList.length == 0) {
                                return;
                            }
                            var testActualIdx = $scope.testActualList.map(function (t) {
                                return t.testDefinition.rid;
                            }).indexOf(test.rid);
                            if (testActualIdx > -1) {
                                testSelectionService.deleteActualTest($scope.testActualList[testActualIdx].rid).then(function () {
                                    $scope.testActualList.splice(testActualIdx, 1);
                                });
                            }
                        }
                        function checkTabsTests() {
                            // check or uncheck tests that are in the md-tabs depending on the radio selection
                            switch ($scope.wrapper.selectedRadio) {
                                case "sections":
                                    var section = $scope.sections[$scope.wrapper.selectedSection];
                                    checkSelectedTests(section.testDefinitionList);
                                    break;
                                case "requestForms":
                                    var requestForm = $scope.requestForms[$scope.wrapper.selectedRequestForm];
                                    for (var i = 0; i < requestForm.sections.length; i++) {
                                        var section = requestForm.sections[i];
                                        checkSelectedTests(section.testDefinitionList);
                                    }
                                    break;
                                case "mostRequested":
                                    checkSelectedTests($scope.mostRequestedTests);
                                    break;
                                case "packages":
                                case "profiles":
                                    break;
                            }
                        }
                        $scope.removeChip = function (chip) {
                            chip.isChecked = false;
                            for (var idx = 0; idx < $scope.selectedTestChipsOptions.data.length; idx++) {
                                if ($scope.selectedTestChipsOptions.data[idx].rid === chip.rid) {
                                    $scope.selectedTestChipsOptions.data.splice(idx, 1);
                                    break;
                                }
                            }
                            checkTabsTests();
                            //checkSelectedGroups();
                            $scope.calculatePrice();
                            removeFromTestActualList(chip);
                        };
                        $scope.loadMore = function (section) {
                            section.testDataSource.page(section.testDataSource.page() + 1);
                        };
                        $scope.submitSelectedTests = function () {
                            //incase of edit order
                            if ($scope.selectedTestChipsOptions.data == 0) {
                                checkTestsInsPricing();
                                $scope.$emit(commonData.events.exitTestSelection,
                                    {
                                        testActualList: [],
                                        selectedTests: [],
                                        selectedGroups: getSelectedGroups(),
                                    });
                                return;
                            }
                            var wrapper = {
                                visit: $scope.order,
                                testDefinitionList: angular.copy($scope.selectedTestChipsOptions.data),
                                testGroupList: getSelectedGroups()
                            };

                            var lastTestActualList = angular.copy($scope.testActualList);

                            //if we reselected a test that already has a testActual then remove it
                            for (var i = wrapper.testDefinitionList.length - 1; i >= 0; i--) {
                                var testObj = wrapper.testDefinitionList[i];
                                var idx = $scope.testActualList.map(function (testActual) {
                                    return testActual.testDefinition.rid;
                                }).indexOf(testObj.rid);
                                if (idx > -1) {
                                    wrapper.testDefinitionList.splice(idx, 1);
                                }
                            }
                            testSelectionService.createActualTests(wrapper).then(function (response) {
                                checkTestsInsPricing();
                                $scope.testActualList = $scope.testActualList.concat(response.data);
                                //remove duplicates from the selected tests
                                for (var testActualKey in lastTestActualList) {
                                    var testActualObj = lastTestActualList[testActualKey];
                                    var idx = $scope.testActualList.map(function (testActual) {
                                        return testActual.testDefinition.rid;
                                    }).indexOf(testActualObj.testDefinition.rid);
                                    if (idx < 0) {
                                        $scope.testActualList.push(testActualObj);
                                    }
                                }
                                $scope.$emit(commonData.events.exitTestSelection,
                                    {
                                        testActualList: $scope.testActualList,
                                        selectedTests: $scope.selectedTestChipsOptions.data,
                                        selectedGroups: getSelectedGroups(),
                                    });
                            });
                        };

                        function checkTestsInsPricing() {
                            var selectedPlan = getSelectedInsPlan();
                            var tests = [];
                            if ($scope.selectedTestChipsOptions.data != null) {
                                tests = tests.concat($scope.selectedTestChipsOptions.data);
                            }
                            if ($scope.editOrderTests != null) {
                                tests = tests.concat($scope.editOrderTests);
                            }
                            if (tests.length === 0 || selectedPlan == null) {
                                return;
                            }
                            var testsRid = [];
                            for (var idx = 0; idx < tests.length; idx++) {
                                testsRid.push(tests[idx].rid);
                            }
                            paymentFormService.areTestsWithoutInsPricing({ "testsRid": testsRid, "providerPlanRid": selectedPlan.rid + "" });
                        }

                        // $scope.check = function ($event, test) {
                        //     $scope.showDescription($event, test.reportingDescription);
                        // }


                        // $scope.expandGroup = function (event) {
                        //     // console.log(event.currentTarget.outerHTML.md-svg-src);
                        //     $scope.expandFlag = !$scope.expandFlag;
                        //     if ($scope.expandFlag) {
                        //         $scope.expandSource = "/assets/images/svg/plus.svg";
                        //     } else {
                        //         $scope.expandSource = "/assets/images/svg/minus.svg"
                        //     }
                        // }




                        // $scope.showDescription = function ($event, description) {

                        //     $scope.description = description;
                        //     var position = $mdPanel.newPanelPosition();
                        //     position.relativeTo($event.target).addPanelPosition('align-start', 'below');
                        //     // position.addPanelPosition($mdPanel.xPosition.CENTER);

                        //     var animation = $mdPanel.newPanelAnimation();
                        //     animation.duration(300);
                        //     animation.openFrom($event.currentTarget.parentElement);
                        //     animation.closeTo($event.currentTarget.parentElement);
                        //     animation.withAnimation($mdPanel.animation.SCALE);

                        //     var config = {
                        //         attachTo: angular.element(document.body),
                        //         templateUrl: "./" + config.lisDir + "/modules/component/tests/test-description-panel.html",
                        //         openFrom: angular.element($event.currentTarget.parentElement),
                        //         closeTo: angular.element($event.currentTarget.parentElement),
                        //         position: position,
                        //         controller: function ($scope, description) {

                        //             $scope.description = description;
                        //         },
                        //         locals: {
                        //             description: $scope.description
                        //         },
                        //         animation: animation,
                        //         clickOutsideToClose: true,
                        //         escapeToClose: true,
                        //         panelClass: 'trans-field'
                        //     }
                        //     $mdPanel.open(config);
                        // };
                    }
                }]
        }
    });
});




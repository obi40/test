define("util", ["config", "commonData"], function (config, commonData) {
    'use strict';
    var util = {
        systemMessages: {},
        token: null,
        originalToken: null,
        user: {},
        userLocale: null,
        userPrimary: null,
        userNamePrimary: null,
        userCurrency: null,
        authorities: [],
        languages: [],//tenant languages
        direction: null,
        $rootScope: null,
        $http: null,
        $mdToast: null,
        $window: null,
        $timeout: null,
        $state: null,
        prepareAppDirection: function () {
            /**
             * Set application direction to rtl or ltr(default).
             * 
             */
            var minVar = config.lisDir === "js" ? "" : ".min";
            var pathVar = config.lisDir === "js" ? "./assets/css/" : config.lisDir + "/styles/";
            if (util.user.comLanguage !== undefined) {
                util.$rootScope.direction = util.user.comLanguage.direction;
            } else {
                util.$rootScope.direction = 'ltr';
            }
            util.$rootScope.directionCss = pathVar + util.$rootScope.direction + '_app' + minVar + '.css';
            util.direction = util.$rootScope.direction;
        },
        prepareSystemMessages: function (data) {
            /**
             * Take the tenant messages and reformat them, also update page title.
             * data: the tenant messages array.
             */
            var messages = data;
            // store all the tenant messages objects
            if (data.totalElements != null) {
                messages = data.content;
                for (var idx = 0; idx < messages.length; idx++) {
                    for (var i = 0; i < commonData.tenantMessages.length; i++) {
                        if (commonData.tenantMessages[i].code == messages[idx].code) {
                            commonData.tenantMessages[i] = messages[idx];
                            break;
                        }
                    }
                }
            } else {
                commonData.tenantMessages = messages;
            }
            for (var key in messages) {
                // get the value depending on user language
                util.systemMessages[messages[key].code] = messages[key].description[util.userLocale];
            }
            util.updatePageTitle(null);
        },
        gridSelectionDataBound: function (grid, selectedItems, runOnChange, preSelectFunc) {
            /**
             * Get the current page data in the grid and check/uncheck items if the item is in the chip array or not.
             * (Use inside Kendo's dataBound event)
             * grid : kendo grid.
             * selectedItems : the array which the chips read from, also it will contian all the selections.
             * runOnChange : an object that has "value" key which is a flag, to stop the onChange from running while we triggering the checkboxes.
             * preSelectFunc: a function to pre-select an item if it is in the chips array.(i dont think this is needed anymore, since if not pagination then the grid will persist selection )
             */
            runOnChange.value = false;// so we dont trigger the change event of the grid
            var gridData = grid.dataSource.data();
            for (var i = 0; i < gridData.length; i++) {
                var item = gridData[i];
                var row = grid.element.find("tr[data-uid='" + item.uid + "']");
                var checkBox = row.find("input[type=checkbox]");
                var notExist = true;// notExist as in " user didnt select it"

                if (preSelectFunc) {
                    preSelectFunc(item);// pass parameters to pre-select
                }

                for (var idx = 0; idx < selectedItems.length; idx++) {
                    if (selectedItems[idx].rid == item.rid) {
                        notExist = false;
                        if (checkBox.attr("aria-checked") == "false") {
                            checkBox.trigger("click");
                        }
                    }
                }
                if (checkBox.attr("aria-checked") == "true" && notExist) {
                    checkBox.trigger("click");
                }

                if (i + 1 == gridData.length) {
                    runOnChange.value = true;
                }
            }
        },
        gridSelectionChange: function (grid, selectedItems, runOnChange) {
            /**
             * Get the current page data in the grid remove all the current page records from the selectedItems then insert the selected.
             * (Use inside Kendo's dataBound event)
             * grid : kendo grid.
             * selectedItems : the array which the chips read from, also it will contian all the selections.
             * runOnChange : an object that has "value" key which is a flag, to stop the onChange from running while we triggering the checkboxes.
            */
            if (!runOnChange.value) {
                return;
            }
            // remove all current page records from the selected items then add the selected ones in the same page
            var currentPageRows = grid.dataSource.data();// current page records
            for (var idx = 0; idx < currentPageRows.length; idx++) {
                for (var i = selectedItems.length - 1; i >= 0; i--) {
                    if (selectedItems[i].rid == currentPageRows[idx].rid) {
                        selectedItems.splice(i, 1);
                    }
                }
            }
            // get the selected rows and put them in the selected items
            var selectedRows = grid.select();
            for (var i = 0; i < selectedRows.length; i++) {
                var dataItem = grid.dataItem(selectedRows[i]);
                if (dataItem.rid) {
                    selectedItems.push(dataItem);
                }
            }
        },
        removeGridChip: function (chip, grid) {
            /**
             * To trigger the checkbox of the item that we removed from the chip
             * (called from the function that listen's to md-on-remove's event of the chip)
             * chip: angular chip
             * grid: kendo grid
             */
            var gridData = grid.dataSource.data();
            for (var i = 0; i < gridData.length; i++) {
                var item = gridData[i];
                if (item.rid == chip.rid) {
                    var row = grid.element.find("tr[data-uid='" + item.uid + "']");
                    var checkBox = row.find("input[type=checkbox]");
                    checkBox.trigger("click");
                }
            }
        },
        prepareLanguages: function (languages) {
            /**
             * Modify the tenant languages so it can be used for the transfield directive
             * languages : the languages array, since we will use it when we dont have a user then we need
             * to inject the data to the function
             */
            util.languages = [];
            var langs = languages != null && languages.length > 0 ? languages : util.user.tenantLanguages;
            for (var objKey in langs) {
                var obj = langs[objKey];
                if (obj.isPrimary) {
                    util.userPrimary = obj.comLanguage.locale;
                }
                if (obj.isNamePrimary) {
                    util.userNamePrimary = obj.comLanguage.locale;
                }
                var language = {
                    language: obj.comLanguage.locale,
                    placeholder: "(" + obj.comLanguage.shortcutName + ")",
                    primary: obj.isPrimary,
                    namePrimary: obj.isNamePrimary,
                    value: null,
                    direction: obj.comLanguage.direction
                }
                util.languages.push(language);
            }
        },
        getTransFieldLanguages: function (key, labelCode, object, required) {
            /**
             * A prepare function to be called when declaring trans-fields so they can be used in the trans-field directive.
             * key: trans-field name
             * labelCode
             * object: the object which has a key to pre set the transfield by language(on transfield creation)
             * required
             */
            var langArray = angular.copy(util.languages);
            for (var i in langArray) {
                if (object != null && object[key] != null) {
                    langArray[i].value = object[key][langArray[i].language];
                }
                langArray[i].labelCode = labelCode;
                langArray[i].placeholder = langArray[i].placeholder;
                langArray[i].required = required;
                langArray[i].fieldName = key;
            }
            return langArray;
        },
        createLovFilter: function (element, payload, api, options) {
            /**
             * A function to be called when declaring the filter for LOVs in grids
             * element: The element passed from column.filterable.ui function(element)
             * payload: [nullable] The data to send to the API, for LKPs: { className: "LkpDependencyType" }
             * api: The API function which returns a promise, for LKPs: lovService.getLkpByClass
             * options: Additional options object:
             * {
             *      displayField: fieldNameToDisplay
             * }
             * 
             * Extra per grid settings:
             * 1. Must pass a filterMap to createFilterablePageRequest, ex: { "lkpDependencyTypeRid": "lkpDependencyType.rid" }
             * 2. Must pass specific fields to the schema.model.fields, ex:
             *    lkpDependencyTypeRid: { from: "lkpDependencyType.rid", type: "lov" },
             *    lkpDependencyType: { defaultValue: {} },
             * 3. Filter should be under new column name, ex: "lkpDependencyTypeRid"
             * 4. Column should contain the filterable.ui function, ex:
             *      filterable: {
                        ui: function (element) {
                            util.createLovFilter(element, { className: "LkpDependencyType" }, lovService.getLkpByClass);
                        }
                    },
             */
            var lovDataSource = new kendo.data.DataSource({
                transport: {
                    read: function (e) {
                        api(payload)
                            .then(function (response) {
                                var data = response;
                                if (response.hasOwnProperty("data")) {
                                    data = response.data;
                                }
                                e.success(data);
                            }).catch(function (error) {
                                e.error(error);
                            });
                    },
                },
                schema: {
                    model: {
                        id: "rid",
                        fields: {
                            rid: { type: "number" },
                            name: { type: "object" }
                        }
                    }
                }
            });

            var primaryLanguage = "en_us";
            for (var i = 0; i < util.languages.length; i++) {
                if (util.languages[i].primary) {
                    primaryLanguage = util.languages[i].language;
                    break;
                }
            }

            element.kendoDropDownList({
                dataSource: lovDataSource,
                dataValueField: "rid",
                template: function (dataItem) {
                    if (options && options.displayField) {
                        return dataItem[options.displayField];
                    } else if (dataItem.name[util.userLocale] != null) {
                        return dataItem.name[util.userLocale];
                    } else if (dataItem.name[primaryLanguage] != null) {
                        return dataItem.name[primaryLanguage];
                    } else {
                        return "";
                    }
                },
                valueTemplate: function (dataItem) {
                    if (options && options.displayField) {
                        return dataItem[options.displayField];
                    } else if (dataItem.name[util.userLocale] != null) {
                        return dataItem.name[util.userLocale];
                    } else if (dataItem.name[primaryLanguage] != null) {
                        return dataItem.name[primaryLanguage];
                    } else {
                        return "";
                    }
                },
                optionLabel: { name: util.systemMessages.selectValue },
                optionLabelTemplate: function (item) {
                    return item.name;
                }
            });
        },
        createListFilterHandler: function (e, fieldNames) {
            var currentFilters = [];
            if (e.sender.dataSource.filter() != null) {
                //get current filters
                currentFilters = e.sender.dataSource.filter().filters;
            }
            // we filtered this col
            if (e.filter != null) {
                var filteredCol = e.filter.filters[0];
                if (fieldNames[filteredCol.field]) {
                    e.preventDefault();
                    for (var idx = currentFilters.length - 1; idx >= 0; idx--) {
                        if (currentFilters[idx].field.indexOf(filteredCol.field) != -1) {
                            currentFilters.splice(idx, 1);
                            break;
                        }
                    }
                    filteredCol.field = fieldNames[filteredCol.field];
                    currentFilters.push(filteredCol);
                    e.sender.dataSource.filter(currentFilters);
                }
            } else if (e.sender.dataSource.filter() != null && fieldNames[e.field] && e.filter == null) {
                // we have filters and we cleared the col
                e.preventDefault();
                for (var idx = currentFilters.length - 1; idx >= 0; idx--) {
                    if (currentFilters[idx].field.indexOf(fieldNames[e.field]) != -1) {
                        currentFilters.splice(idx, 1);
                        break;
                    }
                }
                e.sender.dataSource.filter(currentFilters);
            }
        },
        isObjectEmpty: function (obj) {
            return !obj || (typeof obj === "object" && Object.keys(obj).length === 0);
        },
        createListFilter: function (element, data, label, dataSourceOptions) {
            /**
             * Create a list filter for any objects.
             * element: The element passed from column.filterable.ui function(element)
             * data: datasource's data
             * dataSourceOptions: to add options to datasource [optional]
             */
            var dataSource = new kendo.data.DataSource({
                schema: {
                    model: {
                        id: "rid",
                        fields: {
                            rid: {
                                type: "number"
                            }
                        }
                    }
                },
                data: data
            });
            for (var key in dataSourceOptions) {
                if (dataSourceOptions.hasOwnProperty(key)) {
                    dataSource[key] = dataSourceOptions[key];
                }
            }
            element.kendoDropDownList({
                dataSource: dataSource,
                dataValueField: "rid",
                valueTemplate: function (dataItem) {
                    return util.getDeepValueInObj(dataItem, label) || "";
                },
                template: function (dataItem) {
                    return util.getDeepValueInObj(dataItem, label) || "";
                },
                optionLabel: { name: util.systemMessages.selectValue },
                optionLabelTemplate: function (item) {
                    return item.name;
                }
            });
        },
        createListEditor: function (container, options, data, label, listOptions, dataSourceOptions) {
            /**
             * Create a List of values for editing.
             * container : kendo's container
             * options : kendo's container
             * data : datasource's data
             * label : label to display, accepts inner values e.g. user.name.en_us
             * dataSourceOptions: to add options to datasource [optional]
             */
            var dataSource = new kendo.data.DataSource({
                schema: {
                    model: {
                        id: "rid",
                        fields: {
                            rid: {
                                type: "number",
                                nullable: true
                            }
                        }
                    }
                },
                data: data
            });
            var listObj = {
                dataValueField: "rid",
                valueTemplate: function (dataItem) {
                    return util.getDeepValueInObj(dataItem, label) || "";
                },
                template: function (dataItem) {
                    return util.getDeepValueInObj(dataItem, label) || "";
                },
                dataBound: function (e) {
                    var selectedIndex = e.sender.select();
                    e.sender.select(selectedIndex >= 0 ? selectedIndex : 0);
                    e.sender.trigger("change");
                }
            };
            for (var key in listOptions) {
                if (listOptions.hasOwnProperty(key)) {
                    listObj[key] = listOptions[key];
                }
            }
            for (var key in dataSourceOptions) {
                if (dataSourceOptions.hasOwnProperty(key)) {
                    dataSource[key] = dataSourceOptions[key];
                }
            }
            listObj["dataSource"] = dataSource;
            $('<input name="' + options.field + '"/>').appendTo(container).kendoDropDownList(listObj);
        },
        createTransFieldEditor: function (container, options, fieldName) {
            /**
             * Create a custom component to handle trans-field column.
             * container: editor's container
             * options: editor's options
             * fieldName: the trans-field key in the object
            */
            var dataItem = options.model;
            if (dataItem[fieldName] == null) {// in case the object does not have the field name populated
                dataItem[fieldName] = {};
            }
            var uk = fieldName + "_" + dataItem.rid;
            var languages = angular.copy(util.languages);
            languages.sort(function (a, b) { return (a.primary === b.primary) ? 0 : a.primary ? -1 : 1 });//primary first
            var selectedLanguage = languages[0];//default value
            $(container).addClass("inline-trans-editor");
            $(container).attr("flex", "");
            // $(container).attr("layout", "row");
            $(container).attr("layout-wrap", "");
            $('<input class="lang-list" />')
                .appendTo(container)
                .kendoDropDownList({
                    dataTextField: "placeholder",
                    dataValueField: "language",
                    change: function (e) {
                        var selectedRows = this.select();
                        selectedLanguage = this.dataItem(selectedRows[0]);
                        $("#" + uk).val(dataItem[fieldName][selectedLanguage.language]);//put the value in the input
                    },
                    dataSource: languages,
                    value: selectedLanguage.language
                });
            $('<input flex-offset-gt-md="5" dynamic-flex="100|100|100|75|75" type="text" class="k-input k-textbox lang-list-value" id="' + uk + '" />')
                .appendTo(container);
            $("#" + uk).val(dataItem[fieldName][selectedLanguage.language]);//default value

            $(container).find("span.lang-list").attr("dynamic-flex", "100|100|100|20|20");

            $("#" + uk).bind("change paste keyup", function (e) {
                var value = $(this).val();
                dataItem[fieldName][selectedLanguage.language] = value;
                dataItem.dirty = true;
            });
            //'td[aria-describedby="' + $(container).attr("aria-describedby") + '"]'
            // $(container).kendoTooltip({
            //     filter: uk,
            //     position: "bottom",
            //     content: function (e) {
            //         return util.systemMessages.requiredField;
            //     }
            // });
        },
        createFilterablePageRequest: function (dataSource, filterMap, junctionMap) {
            /**
             * For pagination, parameters:
             * -dataSource: kendo dataSource
             * -filterMap: if you want to change the field that you are filtering on. i.e. ("lkpUserStatus", "lkpUserStatus.name")
             *  so if the filters from the grid has "lkpUserStatus" then change it to "lkpUserStatus.name" [optional]
             * -junctionMap: if you want to change the junctionOperator of each field, default is "And" [optional]
             */

            // Pageable object starts counting from 1, however the first page for the Pageable is index = 0
            // and since Kendo Grid sends the first page in the map as 1 then we decreased by 1
            var filterablePageRequest = {
                filters: [],
                page: dataSource._page - 1,
                size: dataSource._pageSize,
                sortList: []
            };

            if (dataSource.filter() != null) {
                var gridFilters = dataSource.filter().filters;
                for (var idx = 0; idx < gridFilters.length; idx++) {
                    var obj = {
                        field: gridFilters[idx].field,
                        operator: gridFilters[idx].operator,
                        value: gridFilters[idx].value,
                        junctionOperator: "And"
                    };
                    if (filterMap !== undefined) {
                        var value = filterMap[gridFilters[idx].field];
                        if (value !== undefined) {
                            obj.field = value;
                        }
                    }
                    if (junctionMap !== undefined) {
                        obj.junctionOperator = junctionMap[gridFilters[idx].field];
                    }
                    filterablePageRequest.filters.push(obj);
                }
            }
            if (dataSource.sort() != null) {
                //filterablePageRequest.sortList = data.sort();
                var gridSortList = dataSource.sort();
                for (var idx = 0; idx < gridSortList.length; idx++) {
                    var fixedEnum = String(gridSortList[idx].dir).toUpperCase();
                    filterablePageRequest.sortList[idx] =
                        {
                            direction: fixedEnum,
                            property: gridSortList[idx].field
                        };
                }
            }
            return filterablePageRequest;
        },
        createToast: function (message, type, duration) {
            /**
             * Create a custom toast depending on the type.
             * message : the message to be displayed
             * type : success,info,warning,error,errorfatal
             * duration : display time of the toast[optional]
             */
            if (message == null || type == null) {
                return;
            }

            var toastDir = util.direction == "ltr" ? "right" : "left";
            type = type.toLowerCase();
            if (type == "errorfatal") {
                type = "error-fatal";
            }
            var toastColor = type + "-toast";
            var toastType = util.systemMessages[type];
            var toastIcon;
            switch (type) {
                case "success":
                    toastIcon = "fas fa-check-circle";
                    toastType = util.systemMessages["successToast"];// success toast msg is different than the normal success
                    break;
                case "info":
                    toastIcon = "fas fa-info-circle";
                    break;
                case "warning":
                    toastIcon = "fas fa-exclamation-triangle";
                    break;
                case "error":
                    toastIcon = "fas fa-exclamation-circle";
                    break;
                case "error-fatal":
                    toastIcon = "lis-access-denied";
                    break;
            }
            var toastData = { color: toastColor, message: message, type: toastType, icon: toastIcon };
            if (duration === undefined) {
                duration = 10000;
            }
            util.$mdToast.show({
                hideDelay: duration,
                position: ('top ' + toastDir),
                controller: ["$scope", "toastData", function ($scope, toastData) {
                    $scope.content = toastData;
                    $scope.closeToast = function () { util.$mdToast.hide(); };
                }],
                templateUrl: "./" + config.lisDir + "/modules/dialogs/toast.html",
                locals: {
                    toastData: toastData
                }
            });

        },
        gridErrorTooltip: function (responseError, ignoreList) {
            if (!ignoreList) {
                ignoreList = [];
            }
            var causeMap = {
                "may not be null": "gridRequired"
            };
            switch (responseError.code) {
                case "dataIntegrityViolation":
                    var invalidFields = responseError.cause.replace(/\[|\]/g, "").split(",");
                    for (var i = 0; i < invalidFields.length; i++) {
                        var invalidFieldParts = invalidFields[i].split(":");
                        var fieldName = invalidFieldParts[0].trim();
                        var cause = invalidFieldParts[1];
                        cause = causeMap[cause];
                        if (!cause) {
                            cause = "invalidField";
                        }
                        var cell = $('[data-container-for="' + fieldName + '"]');
                        if (ignoreList.indexOf(fieldName) > -1 || cell.length < 1) {
                            continue;
                        }

                        var tooltip = $(cell).kendoTooltip({
                            content: function () {
                                return "<span class='red'>" + util.systemMessages[cause] + "</span>"
                            }
                        }).data("kendoTooltip");
                        tooltip.show(cell);
                    }
                    $(".k-tooltip").addClass("error-tooltip");
                    break;
            }
        },
        kendoInputEditorOnChange: function (fieldName, callback) {
            /**
             * Listener when an input changes after editing a field in kendo. MUST be used in "edit" event.
             * fieldName : the input field name.
             * callback : callback to be called after value changes.
             */
            var inputSelector = "td[data-container-for='" + fieldName + "'] input[data-bind='value:" + fieldName + "']";
            $(inputSelector).bind("change paste keyup click", function (e) {
                callback($(this).val());
            });
            // the up and down arrows if input was a number
            $("td[data-container-for='" + fieldName + "'] span.k-select").bind("click", function (e) {
                callback($(inputSelector).val());
            });
        },
        exitPage: function () {
            /**
             * To be used when redirecting user if no data found to be used for the page to function correctly.
             */
            util.$state.go(commonData.internalHomepage);
        },
        setKendoTimeInDate: function (date, timeTxt) {
            /**
             * Get the kendo-timepicker and set it inside the date.
             * date: the date that will be modified
             * timeTxt: kendo-timepicker ng-model (e.g. 10:25 AM)
             */
            var hours = parseInt(timeTxt.substring(0, timeTxt.indexOf(":")));
            var minutes = parseInt(timeTxt.substring(1 + timeTxt.indexOf(":"), timeTxt.indexOf(" ")));
            var timeOfDay = timeTxt.substring(1 + timeTxt.indexOf(" "), timeTxt.length);
            if (timeOfDay === "PM" && hours < 12) {
                hours = hours + 12;
            } else if (timeOfDay === "AM" && hours === 12) {
                hours = 0;
            }
            date.setHours(hours, minutes, 0);
        },
        isJsonString: function (str) {
            /**
             * Check whether the str can be parsed into an oject
             * str: the string object
             */
            if (typeof str != "string") {
                return false;
            }
            try {
                JSON.parse(str);
            } catch (e) {
                return false;
            }
            return true;
        },
        round: function (number, scale) {
            if (!scale) {
                scale = commonData.numberFraction;
            }
            return +(Math.round(number + ("e+" + scale)) + ("e-" + scale));
        },
        setUtilData: function (user, token, authorities) {
            util.token = token;
            util.originalToken = token;
            util.user = user;
            util.userLocale = user.comLanguage.locale;
            util.authorities = authorities;
            if (user.branch) {
                config.mobilePattern = user.branch.mobilePattern;
                util.userCurrency = user.branch.country.currency.code;
            } else {
                config.mobilePattern = user.tenant.mobilePattern;
                util.userCurrency = user.tenant.country.currency.code;
            }
            config.kendoMobilePattern = config.mobilePattern.replace(/#/g, "0").replace(/9/g, "\\9");//kendo uses 0 for numbers, then remove 9 from mask rules of kendo so it can be treated as static text
        },
        getUserData: function () {
            /**
             * Get the user data from the Session Storage or from Local Storage
             */
            if (util.getItemFromStorage("session", "token") && util.getItemFromStorage("session", "user")) {
                var user = util.getItemFromStorage("session", "user");
                var token = util.getItemFromStorage("session", "token");
                var authorities = util.getItemFromStorage("session", "authorities");
                util.setUtilData(user, token, authorities);
                util.prepareLanguages();
            } else if (util.getItemFromStorage("local", "token") && util.getItemFromStorage("local", "user")) {
                var user = util.getItemFromStorage("local", "user");
                var token = util.getItemFromStorage("local", "token");
                var authorities = util.getItemFromStorage("local", "authorities");
                util.setUtilData(user, token, authorities);
                util.prepareLanguages();
            } else {
                //defaults when no user is logged in
                util.userLocale = commonData.defaultLocale;
            }
        },
        setUserData: function (loginResponse, rememberMe) {
            /**
             * Set/Update user's data.
             * loginResponse: the data from the server containing user,token,tenant languages.
             * rememberMe: store in locale storage or not
             */
            util.clearUtilData();
            var token = loginResponse.token;
            var user = loginResponse.user;
            var authorities = [];
            for (var idx = 0; idx < loginResponse.authorities.length; idx++) {
                authorities.push(loginResponse.authorities[idx].authority);
            }
            var storageToUse = "session";
            if (rememberMe) {
                storageToUse = "local";
            }
            util.setItemInStorage(storageToUse, "token", token);
            util.setItemInStorage(storageToUse, "user", user);
            util.setItemInStorage(storageToUse, "authorities", authorities);
            util.setUtilData(user, token, authorities);
            util.prepareLanguages();
            util.prepareAppDirection();
        },
        updatePageTitle: function (toState) {
            /**
             * Update the page title in header file and in the window tab, except login we are handling it inisde the loginController
             * toState: ui-router $transition.to() [optional]
             */
            if (toState != null && toState.views != null) {// in case we called this outside the route listener 
                util.$rootScope.pageTitleName = toState.views.main.data ? toState.views.main.data.pageName : toState.name;
            }
            if (util.systemMessages.hasOwnProperty(util.$rootScope.pageTitleName)) {
                util.$rootScope.pageTitleName = util.systemMessages[util.$rootScope.pageTitleName];
                util.$window.document.title = "AccuLab - " + util.$rootScope.pageTitleName;
            } else {
                //in case the route has a pageTitleName of nothing i.e. ""
                util.$window.document.title = "AccuLab";
            }

        },
        addGridRow: function (data, dataSource) {
            /**
             * Add a new row at the top of curent page.
             * 
             * data: object to insert
             * dataSource
             * 
             * Returns the updated data with kendo's id.
             */
            var currentPage = dataSource.view();
            var index = dataSource.indexOf(currentPage[0]);
            data = dataSource.insert(index, data);
            return data;
        },
        editGridRow: function (dataItem, gridId) {
            var grid = $("#" + gridId);
            grid.data("kendoGrid").editRow(dataItem);
            grid.data("kendoGrid").select("tr[data-uid=" + dataItem.uid + "]");
        },
        deleteGridRow: function (dataItem, dataSource) {
            dataSource.remove(dataItem);
            dataSource.sync();
        },
        fullWebsiteView: function ($scope) {
            /**
             * Hide Header, stretch body to full view. And back to default when destroying the controller
             * $scope: the controller's scope
             */
            $("#header").hasClass("ng-hide") ? null : $("#header").addClass("ng-hide");
            $("#navMenu").hasClass("ng-hide") ? null : $("#navMenu").addClass("ng-hide");
            $("div.main").addClass("no-padding");
            $("#mainContent").removeClass("content");
            $scope.$on("$destroy", function () {
                $("#header").removeClass("ng-hide");
                $("#navMenu").removeClass("ng-hide");
                $("div.main").removeClass("no-padding");
                $("#mainContent").addClass("content");
            });
        },
        hideNavMenu: function ($scope) {
            $("#navMenu").hasClass("ng-hide") ? null : $("#navMenu").addClass("ng-hide");
            $scope.$on("$destroy", function () {
                $("#navMenu").removeClass("ng-hide");
            });
        },
        getDeepValueInObj: function (obj, nestedKeys) {
            /**
             * Get the deep value in the object even in case of list keys.
             * obj: the object to search for value in it.
             * nestedKeys: the key inside of the object to be fetched. i.e. obj.groups[3].name.en_us,obj.age.DoB
             */
            nestedKeys = nestedKeys.replace(/\[(\w+)\]/g, '.$1'); // convert indexes to properties
            nestedKeys = nestedKeys.replace(/^\./, '');// strip a leading dot
            var a = nestedKeys.split('.');
            for (var i = 0, n = a.length; i < n; ++i) {
                var k = a[i];
                if (k in obj) {
                    obj = obj[k];
                } else {
                    return;
                }
            }
            return obj;
        },
        fileHandler: function (data, file, download) {
            /**
             * data: the blob data.
             * file:
             *  1- type: type of file i.e. "application/pdf" 
             *           this is no longer needed since the blob should be of the correct type
             *  2- name: if this value is specified then the file will be downloaded with the given name (with the extension)
             * download: this parameter overrides the parameter "isDocumentAutoDownload" from tenant settings
             */

            //https://stackoverflow.com/a/13652829
            var popupBlockerChecker = {
                check: function (popup_window) {
                    var _scope = this;
                    if (popup_window) {
                        if (/chrome/.test(navigator.userAgent.toLowerCase())) {
                            setTimeout(function () {
                                _scope._is_popup_blocked(_scope, popup_window);
                            }, 200);
                        } else {
                            popup_window.onload = function () {
                                _scope._is_popup_blocked(_scope, popup_window);
                            };
                        }
                    } else {
                        _scope._displayError();
                    }
                },
                _is_popup_blocked: function (scope, popup_window) {
                    if ((popup_window.innerHeight > 0) == false) {
                        scope._displayError();
                    }
                },
                _displayError: function () {
                    util.createToast("popupBlocked", "warning");
                }
            };

            var supportedTypes = ["application/pdf", "image/bmp", "image/gif", "image/jpeg", "image/jpg", "image/png"];

            var blob;
            if (data instanceof Blob) {
                blob = data;
            } else {
                blob = new Blob([data], { type: file.type });
            }
            var url = util.$window.URL.createObjectURL(blob);
            var shouldDownload = util.user.tenant.isDocumentAutoDownload;
            if (download !== undefined) {
                shouldDownload = download;
            }
            if (supportedTypes.indexOf(blob.type) < 0) {
                shouldDownload = true;
            }
            if (shouldDownload) {
                var link = document.createElement('a');
                link.href = url;
                if (blob.type === "application/rtf") {
                    file.name = file.name + ".rtf";
                }
                link.download = file.name;

                document.body.appendChild(link);
                link.click();
                util.$window.URL.revokeObjectURL(url);
                link.remove();
            } else {
                var popup = util.$window.open(url, '_blank');
                // popup.document.write('<iframe src="' + url + '" style="width: 100vw; height: 100vh;" frameborder="0" allowfullscreen></iframe>');
                // popup.document.title = file.name;
                // popup.document.body.style.margin = 0;
                popupBlockerChecker.check(popup);
            }
        },
        getIconFromExtension: function (extension) {
            var iconClass = extension.toLowerCase().replace('.', '');

            var archiveFormats = ['rar', 'zip', 'zipx', '7z', 's7z', 'ace', 'afa', 'alz', 'apk', 'arc', 'arj', 'b1', 'ba', 'bh', 'cab', 'car', 'cfs', 'cpt', 'dar', 'dd', 'dgc', 'dmg', 'ear', 'gca', 'ha', 'hki', 'ice', 'jar', 'kgb', 'izh', 'iha', 'izx', 'pak', 'partimg', 'paq6', 'paq7', 'paq8', 'pea', 'pim', 'pit', 'qda', 'rk', 'sda', 'sea', 'sen', 'sfx', 'shk', 'sit', 'sitx', 'sqx', 'tar.gz', 'tgz', 'tar.z', 'tar.bz2', 'tbz2', 'tar.izma', 'tlz', 'uc', 'uc0', 'uc2', 'ucn', 'ur2', 'ue2', 'uca', 'uha', 'war', 'wim', 'xar', 'xp3', 'yz1', 'zoo', 'zpaq', 'zz'];
            var codeFormats = ['code'];
            var excelFormats = ['xlsx', 'xls', 'xlsm', 'xlsb', 'xltx', 'xltm', 'xlt', 'xml', 'xlam', 'xla', 'xlw'];
            var imageFormats = ['ani', 'jpg', 'jpeg', 'png', 'bmp', 'cal', 'fax', 'gif', 'img', 'jbg', 'jpe', 'mac', 'pbm', 'pcd', 'pcx', 'pct', 'pgm', 'ppm', 'psd', 'ras', 'tga', 'wmf'];
            var videoFormats = ['mp4', 'm4p', 'm4v', 'avi', 'webm', 'mkv', 'flv', 'drc', 'vob', 'ogv', 'ogg', 'gif', 'mng', 'mov', 'qt', 'wmv', 'yuv', 'rm', 'rmvb', 'asf', 'amv', 'mpg', 'mp2', 'mpeg', 'mpe', 'mpv', 'm2v', 'm4v', 'svi', '3gp', '3g2', 'mxf', 'roq', 'nsv', 'f4v', 'f4p', 'f4a', 'f4a'];
            var pdfFormats = ['pdf'];
            var powerPointFormats = ['powerpoint', 'ppt', 'pptx', 'pptm', 'potx', 'potm', 'pot', 'pps', 'ppsx', 'ppsm', 'ppam', 'odp'];
            var audioFormats = ['mp3', 'aa', 'aac', 'aax', 'act', 'aiff', 'amr', 'ape', 'au', 'awb', 'dct', 'dss', 'dvf', 'flac', 'gsm', 'iklax', 'ivs', 'm4a', 'm4b', 'm4p', 'mmf', 'mpc', 'oga', 'mogg', 'opus', 'ra', 'raw', 'tta', 'vox', 'wav', 'wma', 'wv'];
            var textFormats = ['txt'];
            var tiffFormats = ['tif', 'tiff'];
            var wordFormats = ['doc', 'docx', 'docm', 'dot', 'dotm', 'dotx', 'odt', 'rtf'];

            if (archiveFormats.indexOf(iconClass) >= 0) {
                iconClass = '-archive';
            } else if (codeFormats.indexOf(iconClass) >= 0) {
                iconClass = '-code';
            } else if (excelFormats.indexOf(iconClass) >= 0) {
                iconClass = '-excel';
            } else if (imageFormats.indexOf(iconClass) >= 0) {
                iconClass = '-image';
            } else if (videoFormats.indexOf(iconClass) >= 0) {
                iconClass = '-video';
            } else if (pdfFormats.indexOf(iconClass) >= 0) {
                iconClass = '-pdf';
            } else if (powerPointFormats.indexOf(iconClass) >= 0) {
                iconClass = '-powerpoint';
            } else if (audioFormats.indexOf(iconClass) >= 0) {
                iconClass = '-audio';
            } else if (textFormats.indexOf(iconClass) >= 0) {
                iconClass = '-alt';
                // } else if (tiffFormats.indexOf(iconClass) >= 0) {
                //     iconClass = '-tiff';
            } else if (wordFormats.indexOf(iconClass) >= 0) {
                iconClass = '-word';
            } else {
                iconClass = '';
            }

            iconClass = 'fa-file' + iconClass;
            return iconClass;
        },
        bytesToSizes: function (x) {
            var units = ['bytes', 'KB', 'MB', 'GB', 'TB', 'PB', 'EB', 'ZB', 'YB'];

            var l = 0, n = parseInt(x, 10) || 0;
            while (n >= 1024 && ++l)
                n = n / 1024;

            //include a decimal point and a tenths-place digit if presenting 
            //less than ten of KB or greater units
            return (n.toFixed(n < 10 && l > 0 ? 1 : 0) + ' ' + units[l]);
        },
        createApiRequest: function (requestMapping, payload, options) {
            /**
             * Create a Request to the server.
             * requestMapping: the api mapping on the back-end
             * payload: the request body [optional]
             * options: any options to add/override the requestObj [optional]
             */
            var requestObj = {
                method: "POST",//default
                url: config.server + config.api_path + requestMapping//default
            }
            if (payload) {
                requestObj["data"] = payload;
            }

            for (var key in options) {
                if (options.hasOwnProperty(key)) {
                    requestObj[key] = options[key];
                }
            }

            return util.$http(requestObj).then(function (response) {
                return response;
            });

        },
        waitForDirective: function (readyKey, fireEvent, scope, payload) {
            //readyKey: name of key in commonData.events object
            //fireEvent: the event to fire [String]
            //scope: scope of the original controller
            //payload: data to send to the directive

            var count = 0;
            waitForDir();
            function waitForDir() {
                if (commonData.events[readyKey]) {
                    scope.$broadcast(fireEvent, payload);
                } else if (count < 200) {
                    //try 200 times with 1 ms delay between trials
                    util.$timeout(function () {
                        waitForDir();
                    }, 1);
                }
            }
        },
        clearForm: function (form) {
            form.$setPristine();
            form.$setUntouched();
            form.$$element.find("div[ng-messages] div[ng-message]").removeAttr("style");
        },
        clearUtilData: function () {
            /**
             * Clear some util data.
             */
            util.clearStorageByName("local");
            util.clearStorageByName("session");
            util.token = null;
            util.originalToken = null;
            util.user = {};
            util.userPrimary = null;
            util.userNamePrimary = null;
            util.userCurrency = null;
            util.authorities = [];
            util.languages = [];
            util.direction = null;
        },
        getStorageByName: function (name) {
            /**
             * "local" for localStorage otherwise sessionStorage
             */
            return name === "local" ? util.$window.localStorage : util.$window.sessionStorage;
        },
        clearStorageByName: function (storageName) {
            var storage = util.getStorageByName(storageName);
            storage.clear();
        },
        removeItemFromStorage: function (storageName, prop) {
            var storage = util.getStorageByName(storageName);
            storage.removeItem(prop);
        },
        getAllItemsFromStorage: function (storageName) {
            var storage = util.getStorageByName(storageName);
            var data = {};
            for (var i = 0; i < storage.length; ++i) {
                data[storage.key(i)] = storage.getItem(storage.key(i));
            }
            return data;
        },
        getItemFromStorage: function (storageName, prop) {
            var storage = util.getStorageByName(storageName);
            var item = storage.getItem(prop);
            try {
                return JSON.parse(item); //If able to parse then it is a stringified object
            } catch (e) {
                return item; //If not return plain text value
            }
        },
        setItemInStorage: function (storageName, prop, value) {
            try {
                var storage = util.getStorageByName(storageName);
                if (typeof value === 'object') {
                    value = JSON.stringify(value);
                }
                storage.setItem(prop, value);
            } catch (e) { // storage might be full
                console.warn(e);
            }
        },
        base64ToBlob: function (base64, mime) {
            mime = mime || '';
            var sliceSize = 1024;
            var byteChars = window.atob(base64);
            var byteArrays = [];

            for (var offset = 0, len = byteChars.length; offset < len; offset += sliceSize) {
                var slice = byteChars.slice(offset, offset + sliceSize);

                var byteNumbers = new Array(slice.length);
                for (var i = 0; i < slice.length; i++) {
                    byteNumbers[i] = slice.charCodeAt(i);
                }

                var byteArray = new Uint8Array(byteNumbers);

                byteArrays.push(byteArray);
            }

            return new Blob(byteArrays, { type: mime });
        }
    };
    return util;
});

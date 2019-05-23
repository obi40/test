define(["config", "util"], function (config, util) {
    'use strict';

    //#region DataSource
    // this setting affects all datasources globally and should be avoided!!!!!
    // kendo.data.DataSource.prototype.options =
    //     $.extend(true, kendo.data.DataSource.prototype.options, {
    //         pageSize: config.gridPageSizes[0],
    //         page: 1
    //     });
    //#endregion

    //Dev purposes
    kendo.data.DataSource.prototype.options.error = function (e) {
        console.error("Kendo Exception Handler-> ", e);
    };

    //#region Grid
    if (kendo.ui.Grid) {
        kendo.ui.Grid.prototype.options.messages =
            $.extend(true, kendo.ui.Grid.prototype.options.messages, {
                "commands": {
                    "cancel": util.systemMessages.cancelChanges,
                    "canceledit": util.systemMessages.cancel,
                    "create": "Add new record",
                    "destroy": "Delete",
                    "edit": "Edit",
                    "excel": "Export to Excel",
                    "pdf": "Export to PDF",
                    "save": "Save changes",
                    "select": "Select",
                    "update": "Update"
                },
                "editable": {
                    "cancelDelete": util.systemMessages.cancel,
                    "confirmation": "Are you sure you want to delete this record?",
                    "confirmDelete": "Delete"
                },
                "noRecords": util.systemMessages.noData
            });

        kendo.ui.Grid.prototype.options =
            $.extend(true, kendo.ui.Grid.prototype.options, {
                noRecords: true,
                pageable: {
                    pageSizes: config.gridPageSizes,
                    buttonCount: config.gridPageButtonCount
                },
                navigatable: true,
                columnMenu: true,
                resizable: true,
                reorderable: true,
                sortable: true,
                filterable: {
                    extra: false,
                    operators: {
                        lov: {
                            eq: util.systemMessages.eq,
                            neq: util.systemMessages.neq,
                            isnull: util.systemMessages.isNull,
                            isnotnull: util.systemMessages.isNotNull
                        },
                        trans: {
                            contains: util.systemMessages.contains,
                            doesnotcontain: util.systemMessages.doesnotcontain,
                        },
                        object: {
                            //to be checked later
                            contains: util.systemMessages.contains,
                            doesnotcontain: util.systemMessages.doesnotcontain,
                        }
                    }
                },
                selectable: "row"
            });
    }
    //#endregion

    //#region TreeList
    kendo.ui.TreeList.prototype.options =
        $.extend(true, kendo.ui.TreeList.prototype.options, {
            columnMenu: true,
            sortable: true,
            resizable: true,
            filterable: {
                extra: false,
                messages: {
                    filter: util.systemMessages.filter,
                    clear: util.systemMessages.clear,
                    isFalse: util.systemMessages.no,
                    isTrue: util.systemMessages.yes
                },
                operators: {
                    lov: {
                        eq: util.systemMessages.eq,
                        neq: util.systemMessages.neq,
                        isnull: util.systemMessages.isNull,
                        isnotnull: util.systemMessages.isNotNull
                    }
                }
            },
            selectable: "row",
            messages: {
                noRows: util.systemMessages.noData
            }
        });
    //#endregion

    //#region DropDownList

    kendo.ui.DropDownList.prototype.options =
        $.extend(true, kendo.ui.DropDownList.prototype.options, {
            // optionLabel: { name: util.systemMessages.selectValue },
            // optionLabelTemplate: function (item) {
            //     return item.name;
            // }
        });

    //#endregion

    //#region ColumnMenu messages
    if (kendo.ui.ColumnMenu) {
        kendo.ui.ColumnMenu.prototype.options.messages =
            $.extend(true, kendo.ui.ColumnMenu.prototype.options.messages, {
                "sortAscending": util.systemMessages.sortAscending,
                "sortDescending": util.systemMessages.sortDescending,
                "filter": util.systemMessages.filter,
                "columns": util.systemMessages.columns,
                "done": util.systemMessages.finish,
                "settings": util.systemMessages.settings,
                "lock": util.systemMessages.lock,
                "unlock": util.systemMessages.unlock
            });
    }
    //#endregion

    //#region FilterMenu messages 
    if (kendo.ui.FilterMenu) {
        kendo.ui.FilterMenu.prototype.options.messages =
            $.extend(true, kendo.ui.FilterMenu.prototype.options.messages, {
                "info": "",
                "filter": util.systemMessages.filter,
                "clear": util.systemMessages.clear,
                "isFalse": util.systemMessages.no,
                "isTrue": util.systemMessages.yes,
                "and": "And",
                "or": "Or",
                "selectValue": util.systemMessages.selectValue,
                "operator": util.systemMessages.operator,
                "value": util.systemMessages.value,
                "cancel": util.systemMessages.cancel
            });
    }
    //#endregion

    //#region FilterMenu operator messages
    if (kendo.ui.FilterMenu) {
        kendo.ui.FilterMenu.prototype.options.operators =
            $.extend(true, kendo.ui.FilterMenu.prototype.options.operators, {
                "string": {
                    "eq": util.systemMessages.eq,
                    "neq": util.systemMessages.neq,
                    "startswith": util.systemMessages.startsWith,
                    "contains": util.systemMessages.contains,
                    "doesnotcontain": util.systemMessages.doesnotcontain,
                    "endswith": util.systemMessages.endsWith,
                    "isnull": util.systemMessages.isNull,
                    "isnotnull": util.systemMessages.isNotNull,
                    "isempty": util.systemMessages.isEmpty,
                    "isnotempty": util.systemMessages.isNotEmpty
                },
                "number": {
                    "eq": util.systemMessages.eq,
                    "neq": util.systemMessages.neq,
                    "gte": util.systemMessages.gte,
                    "gt": util.systemMessages.gt,
                    "lte": util.systemMessages.lte,
                    "lt": util.systemMessages.lt,
                    "isnull": util.systemMessages.isNull,
                    "isnotnull": util.systemMessages.isNotNull,
                },
                "date": {
                    "eq": util.systemMessages.eq,
                    "neq": util.systemMessages.neq,
                    "gte": util.systemMessages.gte,
                    "gt": util.systemMessages.gt,
                    "lte": util.systemMessages.lte,
                    "lt": util.systemMessages.lt,
                    "isnull": util.systemMessages.isNull,
                    "isnotnull": util.systemMessages.isNotNull,
                },
                "enums": {
                    "eq": util.systemMessages.eq,
                    "neq": util.systemMessages.neq,
                    "isnull": util.systemMessages.isNull,
                    "isnotnull": util.systemMessages.isNotNull,
                }
            });
    }
    //#endregion

    //#region NumericTextBox messages
    // if (kendo.ui.NumericTextBox) {
    //     kendo.ui.NumericTextBox.prototype.options =
    //         $.extend(true, kendo.ui.NumericTextBox.prototype.options, {
    //             "decimals": 2,
    //             "round": false,
    //             "restrictDecimals": true,
    //             "min": 0,
    //             "max": 100,
    //             "step": 10,
    //             "upArrowText": "",
    //             "downArrowText": ""
    //         });
    // }
    //#endregion

    //#region Pager messages
    if (kendo.ui.Pager) {
        kendo.ui.Pager.prototype.options.messages =
            $.extend(true, kendo.ui.Pager.prototype.options.messages, {
                "allPages": util.systemMessages.all,
                "display": util.systemMessages.gridDisplay,
                "empty": util.systemMessages.noData,
                "page": util.systemMessages.page,
                "of": util.systemMessages.gridOf,
                "itemsPerPage": util.systemMessages.recordsPerPage,
                "first": util.systemMessages.gridFirst,
                "previous": util.systemMessages.gridPrevious,
                "next": util.systemMessages.gridNext,
                "last": util.systemMessages.gridLast,
                "refresh": util.systemMessages.refresh,
                "morePages": util.systemMessages.showMore
            });
    }
    //#endregion

    //#region Upload messages 
    if (kendo.ui.Upload) {
        kendo.ui.Upload.prototype.options.localization =
            $.extend(true, kendo.ui.Upload.prototype.options.localization, {
                "select": util.systemMessages.selectFiles
            });
    }

    //#endregion

    //#region Validator messages 
    if (kendo.ui.Validator) {
        kendo.ui.Validator.prototype.options.messages =
            $.extend(true, kendo.ui.Validator.prototype.options.messages, {
                "required": util.systemMessages.gridRequired,
                "pattern": util.systemMessages.gridNotValid,
                "min": util.systemMessages.gridMin,
                "max": util.systemMessages.gridMax,
                "step": util.systemMessages.gridNotValid,
                "email": util.systemMessages.gridNotValid,
                "url": util.systemMessages.gridNotValid,
                "date": util.systemMessages.gridNotValid,
                "dateCompare": util.systemMessages.gridNotValid
            });
    }
    //#endregion

    //#region kendoCulture
    kendo.cultures["en-US"] = {
        //<language code>-<country/region code>
        name: "en-US",
        // "numberFormat" defines general number formatting rules
        numberFormat: {
            //numberFormat has only negative pattern unlike the percent and currency
            //negative pattern: one of (n)|-n|- n|n-|n -
            pattern: ["-n"],
            //number of decimal places
            decimals: 2,
            //string that separates the number groups (1,000,000)
            ",": ",",
            //string that separates a number from the fractional point
            ".": ".",
            //the length of each number group
            groupSize: [3],
            //formatting rules for percent number
            percent: {
                //[negative pattern, positive pattern]
                //negativePattern: one of -n %|-n%|-%n|%-n|%n-|n-%|n%-|-% n|n %-|% n-|% -n|n- %
                //positivePattern: one of n %|n%|%n|% n
                pattern: ["-n %", "n %"],
                //number of decimal places
                decimals: 2,
                //string that separates the number groups (1,000,000 %)
                ",": ",",
                //string that separates a number from the fractional point
                ".": ".",
                //the length of each number group
                groupSize: [3],
                //percent symbol
                symbol: "%"
            },
            currency: {
                //[negative pattern, positive pattern]
                //negativePattern: one of "($n)|-$n|$-n|$n-|(n$)|-n$|n-$|n$-|-n $|-$ n|n $-|$ n-|$ -n|n- $|($ n)|(n $)"
                //positivePattern: one of "$n|n$|$ n|n $"
                pattern: ["($n)", "$n"],
                //number of decimal places
                decimals: 2,
                //string that separates the number groups (1,000,000 $)
                ",": ",",
                //string that separates a number from the fractional point
                ".": ".",
                //the length of each number group
                groupSize: [3],
                //currency symbol
                symbol: "$"
            }
        },
        calendars: {
            standard: {
                days: {
                    // full day names
                    names: ["Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"],
                    // abbreviated day names
                    namesAbbr: ["Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"],
                    // shortest day names
                    namesShort: ["Su", "Mo", "Tu", "We", "Th", "Fr", "Sa"]
                },
                months: {
                    // full month names
                    names: ["January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"],
                    // abbreviated month names
                    namesAbbr: ["Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"]
                },
                // AM and PM designators
                // [standard,lowercase,uppercase]
                AM: ["AM", "am", "AM"],
                PM: ["PM", "pm", "PM"],
                // set of predefined date and time patterns used by the culture.
                patterns: {
                    d: "M/d/yyyy",
                    D: "dddd, MMMM dd, yyyy",
                    F: "dddd, MMMM dd, yyyy h:mm:ss tt",
                    g: "M/d/yyyy h:mm tt",
                    G: "M/d/yyyy h:mm:ss tt",
                    m: "MMMM dd",
                    M: "MMMM dd",
                    s: "yyyy'-'MM'-'ddTHH':'mm':'ss",
                    t: "h:mm tt",
                    T: "h:mm:ss tt",
                    u: "yyyy'-'MM'-'dd HH':'mm':'ss'Z'",
                    y: "MMMM, yyyy",
                    Y: "MMMM, yyyy"
                },
                // the first day of the week (0 = Sunday, 1 = Monday, etc)
                firstDay: 0
            }
        }
    };
    //#endregion
});
/* global define requirejs */
define(['config', 'routes', 'angular', 'util', 'commonData'], function (config, routes, angular, util, commonData) {
    'use strict';
    var app = angular.module('app', ['ngSanitize', 'pascalprecht.translate', 'ui.router', 'kendo.directives', 'ngMaterial',
        'mgo-angular-wizard', 'ngMessages', 'ngAnimate', 'angular-loading-bar', 'ngStomp', 'ngCookies', 'ui.mask']);
    app.config(['$mdThemingProvider', '$controllerProvider', '$compileProvider', '$filterProvider', '$provide',
        '$stateProvider', '$logProvider', '$httpProvider', '$translateProvider',
        '$urlRouterProvider', 'cfpLoadingBarProvider', '$mdDateLocaleProvider', 'uiMask.ConfigProvider', '$locationProvider',
        function ($mdThemingProvider, $controllerProvider, $compileProvider, $filterProvider, $provide,
            $stateProvider, $logProvider, $httpProvider, $translateProvider,
            $urlRouterProvider, cfpLoadingBarProvider, $mdDateLocaleProvider, uiMaskConfigProvider, $locationProvider
        ) {
            $locationProvider.hashPrefix(''); // by default '!'
            $locationProvider.html5Mode(true);
            app.controller = $controllerProvider.register;
            app.directive = $compileProvider.directive;
            app.filter = $filterProvider.register;
            app.factory = $provide.factory;
            app.service = $provide.service;
            $mdThemingProvider.definePalette('amazingPaletteName', {
                '50': 'e5eef3',
                '100': 'bdd4e1',
                '200': '92b8ce',
                '300': '669bba',
                '400': '4585ab',
                '500': '24709c',
                '600': '206894',
                '700': '1b5d8a',
                '800': '165380',
                '900': '0d416e',
                'A100': 'a1cfff',
                'A200': '6eb5ff',
                'A400': '3b9bff',
                'A700': '228eff',
                'contrastDefaultColor': 'light'
            });
            $mdThemingProvider.theme('default')
                .primaryPalette('amazingPaletteName', {
                    'default': '500'
                }).accentPalette('amazingPaletteName', {
                    'default': '200'
                }).backgroundPalette('grey', {
                    'default': 'A100'
                });

            $translateProvider.translations('translations', util.systemMessages);
            $translateProvider.preferredLanguage('translations');
            $translateProvider.useLoader('systemMessagesLoader');
            $translateProvider.useSanitizeValueStrategy('sanitizeParameters');
            $translateProvider.useMissingTranslationHandler("systemMessagesMissingLabelsHandler");
            // angular-loading-bar, the id of the div inside the header
            cfpLoadingBarProvider.parentSelector = '#loadingBarContainer';

            uiMaskConfigProvider.maskDefinitions({ '#': /\d/, '9': undefined });//remove 9 from the definitions
            uiMaskConfigProvider.addDefaultPlaceholder(false);//hide place holders for ui-mask
            // to apply a mask on the date pickers only if a date-mask attr were declared (if no mask value given then use the one in config)
            $provide.decorator('mdDatepickerDirective', ['$delegate', function ($delegate) {
                var directive = $delegate[0];
                var template = directive.template;
                var link = directive.link;
                directive.compile = function () {
                    return function (scope, element, attrs, ctrl) {
                        link.apply(this, arguments);
                        if (attrs.dateMask != null) {
                            var inputNgModelControl = scope.ctrl.ngInputElement.controller('ngModel')
                            // for the first initialization when there is a value in the binded model
                            inputNgModelControl.$formatters.push(function (value) {
                                var viewValue = scope.ctrl.ngInputElement.controller('mdDatepicker').ngModelCtrl.$viewValue;
                                if (!viewValue) {
                                    return undefined;
                                }
                                return scope.ctrl.$mdDateLocale.formatDate(viewValue);
                            });

                            scope.$watch(function () {
                                return scope.ctrl.ngInputElement[0].value;
                            }, function (newValue, oldValue) {

                                // checking the required property here because we cant know when the meta data will return
                                var isRequired = scope.ctrl.$element[0].attributes["required"] != undefined ? true : false;
                                // null means the value is invalid in format
                                if (kendo.parseDate(newValue, config.dateFormat) != null) {
                                    ctrl[0].$setValidity('pattern', true);
                                } else {
                                    ctrl[0].$setValidity('pattern', false);
                                }
                                //manually turning the required validation On and Off if the field is required
                                if (newValue == "") {
                                    if (isRequired) {
                                        ctrl[0].$setValidity('required', false);
                                    } else {
                                        ctrl[0].$setValidity('pattern', true);
                                        ctrl[0].$setValidity('required', true);
                                    }
                                    //if no value then set the model of the datepicker to null
                                    scope.ctrl.$element.controller('ngModel').$setViewValue(null);

                                } else {
                                    if (isRequired) {
                                        ctrl[0].$setValidity('required', true);
                                    }
                                }
                                //setting the value from datepicker to the input tag , for some reason it gets
                                //out of sync
                                scope.ctrl.ngInputElement.controller('ngModel').$setViewValue(newValue);
                                // console.log("Before Syncing")
                                // console.log("dp:", scope.ctrl.$element.controller('ngModel').$viewValue, "|", scope.ctrl.$element.controller('ngModel').$modelValue);
                                // console.log("el:", scope.ctrl.ngInputElement.controller('ngModel').$viewValue);
                                // console.log("After Syncing")
                                // console.log("dp:", scope.ctrl.$element.controller('ngModel').$viewValue, "|", scope.ctrl.$element.controller('ngModel').$modelValue);
                                // console.log("el:", scope.ctrl.ngInputElement.controller('ngModel').$viewValue);
                            });
                        }
                    };
                };
                directive.template = function (tElement, tAttrs) {
                    var originalTemplate = template.apply(this, arguments);
                    // adding mask and ng-model to mdDatepicker's input
                    if (tAttrs.dateMask != null) {
                        var element = angular.element(originalTemplate);
                        tAttrs.dateMask = tAttrs.dateMask == "" ? config.dateMask : tAttrs.dateMask;
                        element.find('input').attr('ui-mask', tAttrs.dateMask);
                        element.find('input').attr('ng-model', "ctrl.dateInput");
                        // new tempalte with mask and ng-model attributes
                        var newTemplate = '';
                        for (var i = 0; i < element.length; i++) {
                            newTemplate += element[i].outerHTML;
                        }
                        return newTemplate;
                    }
                    return originalTemplate;
                };
                return $delegate;
            }]);

            function addDirecitves(allDependencies, directives) {
                if (directives) {
                    for (var i = 0; i < directives.length; i++) {
                        var directiveName = directives[i];
                        var directive = routes.directives[directiveName];
                        allDependencies.push(directive.path);
                        if (directive.dependencies) {
                            allDependencies = allDependencies.concat(directive.dependencies);
                        }
                        if (directive.directives) {
                            allDependencies = addDirecitves(allDependencies, directive.directives);
                        }
                    }
                }
                return allDependencies;
            }

            if (routes.routes !== undefined) {
                angular.forEach(routes.routes, function (options, state) {
                    if (options.abstract) { // If state is abstract add an
                        // abstract state to the
                        // $stateProvider
                        $stateProvider.state(state, {
                            url: options.url,
                            abstract: true,
                            templateUrl: options.templateUrl,
                            views: options.views
                        });
                    } else { // Other non abstract states
                        //use either the minified-(prod) or non-minified-(dev) file depending on the lisDir variable
                        options.views.main.templateUrl = options.views.main.templateUrl.replace("js", config.lisDir);
                        $stateProvider.state(state, {
                            url: options.url,
                            resolve: {
                                deps: ['$q', '$log', '$rootScope', // Load each
                                    // state dependancy using requireJS
                                    function ($q, $log, $rootScope) {
                                        var deferred = $q.defer();
                                        var allDependencies = angular.copy(options.dependencies);
                                        allDependencies = addDirecitves(allDependencies, options.directives);
                                        for (var i = 0; i < allDependencies.length; i++) {
                                            //use either the minified-(prod) or non-minified-(dev) file depending on the lisDir variable
                                            allDependencies[i] = "../" + config.lisDir + "/" + allDependencies[i];
                                        }
                                        requirejs(allDependencies, function () {
                                            $rootScope.$apply(function () {
                                                deferred.resolve();
                                            });
                                        });

                                        return deferred.promise;
                                    }
                                ]
                            },
                            views: options.views,
                            onEnter: options.onEnter ? options.onEnter : null
                        });
                    }
                });
            }

            if (routes.otherwise !== undefined) {
                $urlRouterProvider.otherwise(routes.otherwise);
            }
            // Enable logs (could disable from config)
            $logProvider.debugEnabled(true); // to put in the config

            // $http requests interceptors
            $httpProvider.interceptors.push('APIInterceptor');
            $httpProvider.interceptors.push(["$q", "$rootScope", function ($q, $rootScope) {
                return {
                    'request': function (config) {
                        return config || $q.when(config);
                    },
                    'requestError': function (rejection) {
                        return $q.reject(rejection);
                    },
                    'response': function (response) {
                        return response || $q.when(response);
                    },
                    'responseError': function (rejection) {
                        return $q.reject(rejection);
                    }
                };
            }]);
            // global formatter for all md-datepickers
            $mdDateLocaleProvider.formatDate = function (date) {
                //sometimes the date get sent as a string
                if (typeof date == "string") {
                    date = new Date(date);
                    date = kendo.parseDate(date, config.dateFormat);
                }
                var formattedDate = kendo.toString(date, config.dateFormat);
                return formattedDate;
            };
            // global parser for all md-datepickers
            $mdDateLocaleProvider.parseDate = function (dateString) {
                var formattedDate = kendo.parseDate(dateString, config.dateFormat);
                return formattedDate;
            };

            //DEV purposes
            //to log promise errors  since sometimes they are silently thrown , to work properly there souldn't be any
            //catcher to the promise in the controller
            $provide.decorator("$exceptionHandler", ['$delegate', function ($delegate) {
                return function (exception, cause) {
                    $delegate(exception, cause);
                };
            }]);

        }
    ]).run(["$window", "$timeout", "$translate", "$rootScope", "$mdToast", "$transitions", "$http", "$stomp", "$log", "$q", "$cookies", "$state",
        function ($window, $timeout, $translate, $rootScope, $mdToast, $transitions, $http, $stomp, $log, $q, $cookies, $state) {

            $stomp.setDebug(function (args) {
                $log.debug(args);
            });

            // function sendChat() {
            //     $stomp.send(
            //         "/app/chat",
            //         {
            //             message: { from: "Murad", text: "Hello Peeps!" }
            //         },
            //         {
            //             priority: 9,
            //             custom: 42 // Custom Headers
            //         });
            // }

            // $stomp.connect('/lis/chat')
            //     // frame = CONNECTED headers
            //     .then(function (frame) {
            //         var subscription = $stomp.subscribe(
            //             '/topic/messages',
            //             function (payload, headers, res) {
            //                 console.log("Payload: ", payload);
            //                 console.log("Headers: ", headers);
            //                 console.log("Response: ", res);
            //                 util.createToast(util.systemMessages[payload.messageCode]
            //                     + " " + util.systemMessages["forSampleNo"] + " " +
            //                     payload.sampleNo, "info", 15000);
            //             },
            //             {
            //                 'headers': 'are awesome'
            //             });

            //         //     // Unsubscribe
            //         //     subscription.unsubscribe()

            //         //     // Disconnect
            //         //     $stomp.disconnect().then(function () {
            //         //         $log.info('disconnected');
            //         //     })
            //     });

            var cookies = $cookies.getAll();
            for (var key in cookies) {
                $cookies.remove(key);
            }

            util.$rootScope = $rootScope;
            util.$http = $http;
            util.$mdToast = $mdToast;
            util.$window = $window;
            util.$timeout = $timeout;
            util.$state = $state;
            util.getUserData();

            // so every view using a scope under rootScope can use these, inner directive templates must inject it manually 
            $rootScope.patternPercent = config.regexpPercent;
            $rootScope.patternNum = config.regexpNum;
            $rootScope.patternMobile = config.mobilePattern;

            //Route change listener
            $transitions.onBefore({}, function ($transition) {

                //#region waitForDirective
                //this resets the behavior of certain directives upon loading a new page
                //the directive sets its state to ready upon load and in turn the calling controller interacts with it properly
                for (var key in commonData.events) {
                    if (typeof commonData.events[key] === "boolean") {
                        commonData.events[key] = false;
                    }
                }
                //#endregion

                $rootScope.fromState = $transition.from();
                var toState = $transition.to();
                util.updatePageTitle(toState);

                //If navigating to other than login,password-reset pages and no token then go to login page
                if (toState.name != 'password-reset' && toState.name != 'subscription' && toState.name != 'home' && toState.name != 'sms' && toState.name != 'login' && !util.token) {
                    return $transition.router.stateService.target('login');
                } else if (toState.name == 'login' && util.token) {
                    return $transition.router.stateService.target(commonData.internalHomepage);
                }

                // To call the System_Messages_Loader for pushing data, call only if we have the token and we didnt load the messages
                if (Object.keys(util.systemMessages).length === 0) {
                    return $q(function (resolve, reject) {
                        $translate.refresh('translations').then(function () {
                            resolve();
                            document.getElementById("loadingWebsiteContainer").style.display = "none";
                            document.getElementsByTagName("html")[0].style.overflow = "visible";
                        }).catch(function () {
                            reject();
                        });
                    });
                } else {// hide when in login page
                    document.getElementById("loadingWebsiteContainer").style.display = "none";
                    document.getElementsByTagName("html")[0].style.overflow = "visible";
                }
                $window.scrollTo(0, 0);
            });

            util.prepareAppDirection();

        }]);
    app.filter('translator', function () {
        //this is also used in inputMessages directive
        return function (messageCode, language, messageValue) {
            var value = util.systemMessages[messageCode];

            for (var idx = 0; idx < commonData.tenantMessages.length; idx++) {
                var code = commonData.tenantMessages[idx].code;
                var obj = commonData.tenantMessages[idx];
                if (messageCode === code) {
                    value = obj.description[language];
                    break;
                }
            }
            if (messageValue) {
                for (var key in messageValue) {
                    value = value.replace("{" + key + "}", messageValue[key]);
                }
            }
            return value;
        }
    });
    app.filter('dateFormat', ["$filter", function ($filter) {
        var angularDateFilter = $filter('date');
        return function (theDate) {
            return angularDateFilter(theDate, config.dateFormat);
        }
    }]);
    app.filter('dateTimeFormat', ["$filter", function ($filter) {
        var angularDateFilter = $filter('date');
        return function (theDate) {
            return angularDateFilter(theDate, config.dateTimeFormat);
        }
    }]);
    app.filter('numberFormat', ["$filter", function ($filter) {
        var angularNumberFilter = $filter('number');
        return function (number) {
            return angularNumberFilter(number, commonData.numberFraction);
        }
    }]);
    //filter to render html strings to html
    app.filter('renderHtml', ["$sce", function ($sce) { return $sce.trustAsHtml; }]);
    app.factory('systemMessagesLoader', ["$http", "$q", function ($http, $q) {
        /**
         * Load labels so it can be used in angular-translation
         */

        return function (options) {
            var deferred = $q.defer();
            //fetch labels if required
            if (Object.keys(util.systemMessages).length === 0) {
                $http.post(config.server + config.api_path + "getLabels.pub.srvc").then(function (response) {
                    util.prepareSystemMessages(response.data);
                    return require(['kendoOptions'], function () {
                        return deferred.resolve(util.systemMessages);
                    });
                });
            } else {//reuse labels (most likely user not signed in, using language switcher)
                util.prepareSystemMessages(commonData.tenantMessages);
                deferred.resolve(util.systemMessages);
            }

            return deferred.promise;
        };
    }]);

    app.factory('systemMessagesMissingLabelsHandler', [function () {
        /**
         * Fall back when a label couldn't be found when translating
         */
        return function (translationID) {
            for (var objKey in commonData.tenantMessages) {
                if (commonData.tenantMessages[objKey].code == translationID) {
                    if (!commonData.tenantMessages[objKey].description[util.userPrimary]) {
                        // fail to load the value using tenant primary language
                        return translationID;
                    } else {
                        // success to load the value using tenant primary language
                        return commonData.tenantMessages[objKey].description[util.userPrimary];
                    }
                }
            }
            // this label does not exist on the database
            return translationID;

        };
    }]);

    var inputDefinition = function () {
        return {
            restrict: 'E',
            require: '?ngModel',
            link: function (scope, element, attr, ngModel) {
                if (ngModel) {
                    var convertToModel = function (value) {
                        return value === '' ? null : value;
                    };
                    ngModel.$parsers.push(convertToModel);
                }
            }
        };
    };
    /**
    * Overwrite default input element.
    */
    app.directive('input', inputDefinition);

    return app;
});
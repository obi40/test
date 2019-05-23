/* global window, requirejs */

(function (document) {
    'use strict';
    requirejs.config({
        baseUrl: lisDir,
        waitSeconds: 60,
        paths: {
            //angular derivatives
            'angular': '../libs/angular/angular.min',
            'angularMessages': '../libs/angular-messages/angular-messages.min',
            'angularWizard': '../libs/angular-wizard/dist/angular-wizard.min',
            'angularAnimate': '../libs/angular-animate/angular-animate.min',
            'angularAria': '../libs/angular-aria/angular-aria.min',
            'angularSanitize': '../libs/angular-sanitize/angular-sanitize.min',
            'angularTranslate': '../libs/angular-translate/dist/angular-translate.min',
            'angularTranslateLoaderStaticFiles': '../libs/angular-translate-loader-static-files/angular-translate-loader-static-files.min',
            'angularMaterial': '../libs/angular-material/angular-material.min',
            'angularCookies': '../libs/angular-cookies/angular-cookies.min',
            'angularLoadingBar': '../libs/angular-loading-bar/build/loading-bar.min',
            'angularUiMask': '../libs/angular-ui-mask/dist/mask.min',
            //sockJS
            // 'sockjs': '../libs/sockjs-client/dist/sockjs.min',
            //stomp
            'ngStomp': '../libs/ng-stomp/dist/ng-stomp.standalone.min',
            //ui-router
            'uiRouter': '../libs/@uirouter/angularjs/release/angular-ui-router.min',
            //gridstack
            'uigrid': '../libs/angular-ui-grid/ui-grid',
            'lodash': '../libs/lodash/core.min',
            'gridstack': '../libs/gridstack/dist/gridstack.min',
            'gridstackang': '../libs/gridstack-angular/dist/gridstack-angular.min',
            'gridstackui': '../libs/gridstack/dist/gridstack.jQueryUI.min',
            //jquery
            'jquery': '../libs/jquery/dist/jquery.min',
            'jqueryui': '../libs/jquery-ui/jquery-ui.min',
            'jquerytouchpunch': '../libs/jquery-ui-touch-punch/jquery.ui.touch-punch.min',
            //kendo (NOTE: do not change the naming style of kendo.all.min as it will break loading internal modules)
            'kendo.all.min': '../libs/kendoui/js/kendo.all.min',
            'kendoOptions': 'kendoOptions',
            'jszip': '../libs/jszip/dist/jszip.min',
            //particles (this file should be loaded by requireJS as dep for login only)
            'particles': '../libs/particles.js/particles.min',
            //modules
            'APIInterceptor': 'modules/shared/services/APIInterceptorService',
            'footer': 'modules/shared/directives/footer/footer',
            'header': 'modules/shared/directives/header/header',
            'navigationMenu': 'modules/shared/directives/navigationMenu/navigationMenu',
            'commonMethods': "modules/shared/services/commonMethods",
            'dynamicFlex': 'modules/shared/directives/dynamicFlex/dynamicFlex',
            'inputMessages': 'modules/shared/directives/inputMessages/inputMessages',
            'authorityChecker': 'modules/shared/directives/authorityChecker',
            'httpDisableClick': 'modules/shared/directives/httpDisableClick'
        },
        shim: {
            'angular': {
                exports: 'angular',
                deps: ['jquery']
            },
            'gridstackang': {
                deps: ['angular']
            },
            'gridstack': {
                deps: ['lodash']
            },
            'gridstackui': {
                deps: ['jqueryui', 'gridstack']
            },
            'angularWizard': {
                deps: ['angular']
            },
            'angularAria': {
                deps: ['angular']
            },
            'uibootstrap': {
                deps: ['angular']
            },
            'jquery': {
                exports: 'jquery'
            },
            'jqueryui': {
                exports: 'jqueryui',
                deps: ['jquery']
            },
            'jquerytouchpunch': {
                deps: ['jquery', 'jqueryui']
            },
            'uigrid': {
                deps: ['angular']
            },
            'angularMaterial': {
                deps: ['angularAnimate', 'angularAria']
            },
            'uiRouter': {
                deps: ['angular']
            },
            'angularSanitize': {
                deps: ['angular']
            },
            'angularAnimate': {
                deps: ['angular']
            },
            'angularTranslate': {
                deps: ['angular']
            },
            'angularTranslateLoaderStaticFiles': {
                deps: ['angularTranslate']
            },
            'angularMessages': {
                deps: ['angular']
            },
            'angularCookies': {
                deps: ['angular']
            },
            'angularLoadingBar': {
                deps: ['angular']
            },
            'angularUiMask': {
                deps: ['angular']
            },
            'ngStomp': {
                deps: ['angular']
            },
            'lodash': {
                deps: ['angular']
            },
            'app': {
                //removed resources 'd3', 'c3', 'c3-angular', 'angularTranslateLoaderStaticFiles', ,
                deps: ['angularUiMask', 'angularLoadingBar', 'ngStomp', 'angularMessages', 'angularSanitize',
                    'angularTranslate', 'angularMaterial', 'angularWizard', 'jquery', 'jqueryui',
                    'jquerytouchpunch', 'uiRouter', 'kendo.all.min', 'lodash', 'angularCookies']
            }
        },
        onNodeCreated: function (node, config, moduleName, url) {
            // if(moduleName.indexOf("kendo") >= 0 || moduleName =="angular"){
            // 	console.log('module ' + moduleName + ' is about to be loaded');
            // }

            node.addEventListener('load', function () {
                //  	if(moduleName.indexOf("kendo") >= 0 || moduleName == "angular"){
                // console.log('module ' + moduleName + ' has been loaded');
                //  	}
                if (moduleName === "angular") {
                    //The rest of the app starts here
                    requirejs(['angular', 'app', 'APIInterceptor', 'particles', 'header', 'footer', 'navigationMenu', 'commonMethods', 'dynamicFlex', 'inputMessages', 'authorityChecker', 'httpDisableClick'], function (angular, app) {
                        angular.bootstrap(document, [app.name]);
                    });
                }
            });

            node.addEventListener('error', function () {
                console.log('module ' + moduleName + ' could not be loaded');
            });
        }
    });
    // Angular starts here!
    requirejs(['angular'], function (angular) {

    });

}(window.document));

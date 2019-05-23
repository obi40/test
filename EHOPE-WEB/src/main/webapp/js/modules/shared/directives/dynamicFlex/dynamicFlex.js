define(['app'], function (app) {
    'use strict';
    /**
     * To apply dynamic flex on child elements.
     * 
     * 1. Parent must have the following directives [layout="row", layout-wrap, flex]
     * 2. dynamic-flex can be passed the sizes as a string in order [xs|sm|md|lg|xl] [Optional]
     * 3. Default values are ["100", "100", "50", "33", "25"]
     * 
     */
    app.directive('dynamicFlex', ["$compile", function ($compile) {
        return {
            restrict: 'A',
            replace: false,
            terminal: true, //to disable other directivs until this is compiled
            priority: 1000, //to run before other directives
            compile: function compile(element, attrs) {
                var sizes = attrs.dynamicFlex ? attrs.dynamicFlex : attrs.dataDynamicFlex;
                var sizeArray = sizes ? sizes.split("|") : ["100", "100", "50", "33", "25"];
                element.attr('flex-xs', sizeArray[0]);
                element.attr('flex-sm', sizeArray[1]);
                element.attr('flex-md', sizeArray[2]);
                element.attr('flex-lg', sizeArray[3]);
                element.attr('flex-xl', sizeArray[4]);
                element.removeAttr("dynamic-flex"); //remove the attribute to avoid indefinite loop
                element.removeAttr("data-dynamic-flex"); //also remove the same attribute with data- prefix

                return {
                    pre: function preLink(scope, iElement, iAttrs, controller) {

                    },
                    post: function postLink(scope, iElement, iAttrs, controller) {
                        $compile(iElement)(scope);
                    }
                };
            }
        };
    }]);
});
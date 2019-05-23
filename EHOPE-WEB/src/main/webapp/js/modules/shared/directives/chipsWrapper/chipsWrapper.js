define(['app', 'util', 'config'], function (app, util, config) {
    'use strict';
    /**
     * Wrapper for md-chips with show more/show less button
     * 
     * 1- options ->
     * 	a.
     * 
     * 
     */
    app.directive('chipsWrapper', function () {
        return {
            restrict: 'E',
            replace: false,
            templateUrl: "./" + config.lisDir + "/modules/shared/directives/chipsWrapper/chips-wrapper.html",
            scope: {
                options: "=options"
            },
            controller: ["$scope", "$element", function ($scope, $element) {

                $scope.options.readonly = $scope.options != null && $scope.options.readonly != null ? $scope.options.readonly : true;
                $scope.options.removable = $scope.options != null && $scope.options.removable != null ? $scope.options.removable : true;
                $scope.exceededMaxHeight = false;
                $scope.showToggleBtn = false;
                $scope.toggleIcon = "fas fa-angle-double-down";
                $scope.marginFixToggle = true;
                var maxChipsHeight = 250;//same height as the class "chips-wrapper"
                $scope.showMoreBtn = false;
                //var prevDataLength = $scope.options != null && $scope.options.data ? $scope.options.data.length : -1;

                // watching the height of the chips till it get fully populated by angular then removing this watcher
                var heightListener = $scope.$watch(function () { return $element.find(".chips-wrapper-container").height(); }, function (newValue, oldValue) {
                    var elementHeight = $element.find(".chips-wrapper-container").height();
                    //console.log(elementHeight);
                    if (newValue != oldValue && newValue > maxChipsHeight && $scope.exceededMaxHeight == false) {
                        $scope.exceededMaxHeight = true;
                        $scope.showToggleBtn = true;
                        $scope.showMoreBtn = true;
                        heightListener();// remove watcher
                    }
                    $scope.toggleIcon = $scope.exceededMaxHeight ? "fas fa-angle-double-down" : "fas fa-angle-double-up";
                });

                $scope.$watch(function () { return $element.find(".chips-wrapper-container").height(); }, function (newValue, oldValue) {
                    if (newValue != oldValue) {
                        var elementHeight = $element.find(".chips-wrapper-container").height();
                        //console.log(elementHeight);
                        if (elementHeight > maxChipsHeight) {
                            if ($scope.showMoreBtn == true) {// dont run if the up icon is showing
                                $scope.exceededMaxHeight = true;
                                $scope.showToggleBtn = true;
                            }
                        } else if (elementHeight < maxChipsHeight) {
                            $scope.exceededMaxHeight = false;
                            $scope.showToggleBtn = false;
                            $scope.marginFixToggle = true;
                            $scope.showMoreBtn = true;//return the state to "show more", so if we added more data we can run the first if
                        }
                    }

                    $scope.toggleIcon = $scope.exceededMaxHeight ? "fas fa-angle-double-down" : "fas fa-angle-double-up";

                });

                $scope.$watchCollection("options.data", function (newValue, oldValue) {
                    if (!oldValue || !newValue) {
                        return;
                    }
                    OUTER: for (var idx = 0; idx < newValue.length; idx++) {
                        for (var i = 0; i < oldValue.length; i++) {
                            if (newValue[idx].rid && oldValue[i].rid && newValue[idx].rid === oldValue[i].rid) {
                                continue OUTER;
                            }
                        }
                        $scope.onChipAdd(newValue[idx]);
                    }
                });

                $scope.toggle = function () {
                    $scope.exceededMaxHeight = !$scope.exceededMaxHeight;
                    $scope.showMoreBtn = $scope.exceededMaxHeight;
                    $scope.toggleIcon = $scope.exceededMaxHeight ? "fas fa-angle-double-down" : "fas fa-angle-double-up";
                    if ($scope.exceededMaxHeight == false) {//remove the class
                        $scope.marginFixToggle = false;
                    }

                };

                $scope.label = function ($chip) {
                    return util.getDeepValueInObj($chip, $scope.options.label);
                };

                $scope.tooltip = function ($chip) {
                    if ($scope.options.tooltip !== undefined) {
                        return util.getDeepValueInObj($chip, $scope.options.tooltip);
                    } else {
                        return null;
                    }
                };

                $scope.onChipAdd = function ($chip) {
                    if ($scope.options.onAdd) {
                        $scope.options.onAdd($chip);
                    }
                };
                $scope.onChipRemove = function ($chip) {
                    if ($scope.options.onRemove) {
                        $scope.options.onRemove($chip);
                    }
                };

            }]
        }
    });
});
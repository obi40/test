define(['app', 'util'], function (app, util) {
    'use strict';
    /**
     * Directive to load more data when reaching the bottom of the page.
     * 
     * 1- loadOnScrollOptions ->
     * 	a. callbackFn: this function will be called when the scrolling has reached the threshold.
     *  NOTE: The function MUST RETURN THE HTTP REQUEST i.e. return service.getData(...).then(...);
     *  b. searchObject -> data to send to the CallbackFn [optional]
     *  c. updateScrolling -> call this method after retrieving data and inject (length of current data, total length , the filter object) 
     *  d. refillScreen -> If the searchObject's criteria is changed then empty the data and call this.
     * 
     */
    app.directive("loadOnScroll", ["$window", "$compile", function ($window, $compile) {
        return {
            restrict: 'A',
            scope: {
                loadOnScrollOptions: "=loadOnScroll"
            },
            link: function ($scope, $element) {
                var callbackFn = $scope.loadOnScrollOptions.callbackFn;
                var currentElements = -1;
                var totalElements = 0;
                var fetching = false;
                var filledScreen = false;
                $window = angular.element($window);
                $element.append('<div id="scrollBottom" class="scroll-bottom"><span></span><span></span><span></span><span></span><span></span></div>');
                var scrollBottom = $("#scrollBottom")[0];
                scrollBottom.style.display = "none";
                function fillScreen() {
                    if (!filledScreen && $(document.body).innerHeight() >= $element.innerHeight() && currentElements < totalElements) {
                        //console.log("Filling Screen: Start");
                        callbackFn($scope.loadOnScrollOptions.searchObject).then(function () {
                            var heightWatcher = $scope.$watch(function () { return $element.innerHeight(); }, function (newVal, oldVal) {
                                //console.log(oldVal, newVal);
                                if (oldVal != newVal) {
                                    fillScreen();
                                    heightWatcher();//remove watcher
                                }
                            });
                        });
                    } else {
                        scrollBottom.style.display = "";//reset
                        //console.log("Filling Screen: Finish");
                        filledScreen = true;
                        //Binding the event AFTER we fill the screen because if user was scrolling in another page and went back to this page
                        //the scroll event will fire, so we set scrolling to 0
                        angular.element($window).bind('scroll', loadOnScroll);
                    }
                }
                fillScreen();
                function loadOnScroll() {
                    if (fetching == true || !isScrollBottomVisible() || currentElements >= totalElements) {
                        return;
                    }
                    fetching = true;
                    callbackFn($scope.loadOnScrollOptions.searchObject).then(function () {
                        fetching = false;
                    }).catch(function () {
                        fetching = false;
                    });
                }
                function isScrollBottomVisible() {
                    var rect = scrollBottom.getBoundingClientRect();
                    var rectTop = rect.top - 300;//threshold
                    var viewHeight = Math.max(document.documentElement.clientHeight, $window.innerHeight());
                    return !(rect.bottom < 0 || rectTop - viewHeight >= 0);
                }
                $scope.$on('$destroy', function () {
                    angular.element($window).unbind('scroll');
                    if ($scope.$$watchers != null) {
                        $scope.$$watchers.length = 0;
                        $scope.$$watchers.digestWatchIndex = -1;
                    }
                });
                $scope.loadOnScrollOptions["updateScrolling"] = function (currentData, totalData, filterObj) {
                    currentElements = currentData;
                    totalElements = totalData;
                    filterObj.page++;// increase page size
                };
                // called after changing the search object, and emptying the data
                $scope.loadOnScrollOptions["refillScreen"] = function () {
                    scrollBottom.style.display = "none";//to know when the $element has removed all children
                    angular.element($window).unbind('scroll');
                    currentElements = -1;
                    totalElements = 0;
                    fetching = false;
                    filledScreen = false;
                    //wait till the elements are gone from the view
                    var heightWatcher = $scope.$watch(function () { return $element.innerHeight(); }, function (newVal, oldVal) {
                        if (newVal == 0) {// the view removed all the elements
                            fillScreen();
                            heightWatcher();//remove watcher
                        }
                    });
                };
            }
        };
    }]);

});

                // angular.element($window).bind('resize', function () {
                //     // if the user zoomed out then fill the screen again
                //     if (screen.width <= window.innerWidth) {
                //         filledScreen = false;
                //         console.log("resizing");
                //         angular.element($window).unbind('resize');
                //         $q.when(fillScreen()).then(function (success) {
                //         }).catch(function (error) {

                //         });
                //     }
                //     // manual $digest required as resize event
                //     // is outside of angular
                //     $scope.$digest();
                // });
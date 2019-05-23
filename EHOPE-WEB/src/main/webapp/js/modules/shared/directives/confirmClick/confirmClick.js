define(['app', 'config'], function (app, config) {
    'use strict';
    /**
     * To show a confirmation popup to the user after clicking a button.
     * 
     * 1. confirm-click : Put your "ok" function call here, support a promise and non-promise functions
     * 2. confirm-click-arg : confirmFn parameters [optional]
     * 3. confirm-click-msg : Put the message code to show for users [optional], or use the default 'Are you sure?'
     * 4. cancel-click : Put your "cancel" function call here [optional]
     * 5. cancel-click-arg : cancelFn parameters [optional]
     * 
     */
    app.directive('confirmClick', ["$mdDialog", "$q", function ($mdDialog, $q) {
        return {
            restrict: 'A',
            scope: {
                confirmClick: "=confirmClick",
                confirmClickArg: "=?confirmClickArg",
                confirmClickMsg: "@?confirmClickMsg",
                cancelClick: "=?cancelClick",
                cancelClickArg: "=?cancelClickArg",
            },
            link: function ($scope, $element) {

                var confirmClick = $scope.confirmClick;
                var confirmClickArg = $scope.confirmClickArg;
                var confirmClickMsg = $scope.confirmClickMsg;
                var cancelClick = $scope.cancelClick;
                var cancelClickArg = $scope.cancelClickArg;

                //since inside the $element.bind('click',..) we cant access $scope
                // making confirmClickMsg: "=?confirmClickMsg" : does not work
                $scope.$watch("confirmClickMsg", function (newValue, oldValue) {
                    confirmClickMsg = newValue;
                });
                $scope.$watch("cancelClickArg", function (newValue, oldValue) {
                    cancelClickArg = newValue;
                });

                $element.bind('click', function (event) {
                    $mdDialog.show({
                        controller: ["$scope", function ($scope) {
                            $scope.message = confirmClickMsg || "areYouSure";
                            $scope.ok = function () {

                                $q.when(confirmClick(confirmClickArg)).then(function () {
                                    $mdDialog.cancel();
                                }).catch(function () {
                                    $mdDialog.cancel();
                                });

                            };
                            $scope.cancel = function () {
                                if (cancelClick) {
                                    $q.when(cancelClick(cancelClickArg)).then(function () {
                                        $mdDialog.cancel();
                                    }).catch(function () {
                                        $mdDialog.cancel();
                                    });
                                } else {
                                    $mdDialog.cancel();
                                }

                            };
                        }],
                        templateUrl: './' + config.lisDir + '/modules/shared/directives/confirmClick/confirm-click-view.html',
                        parent: angular.element(document.body),
                        targetEvent: event,
                        clickOutsideToClose: true,
                        fullscreen: true
                    }).then(function (e) {

                    }, function (e) {

                    });

                });
            }
        };
    }]);
});
define(['app', 'util', 'config'], function (app, util, config) {
	'use strict';
	app.controller('loginCtrl', ['$scope', 'loginService', '$state', '$rootScope', '$mdDialog',
		function ($scope, loginService, $state, $rootScope, $mdDialog) {

			util.fullWebsiteView($scope);
			util.clearUtilData();
			$scope.rememberMe = false;
			$scope.user = {};
			$scope.capslock = false;
			$scope.isPassVisible = false;
			$scope.passwordVisibilityIcon = $scope.isPassVisible ? "fas fa-eye" : "fas fa-eye-slash";
			$scope.langSwitcherOptions = {};
			$scope.triggerPasswordVisibility = function () {
				$scope.isPassVisible = !$scope.isPassVisible;
				$scope.passwordVisibilityIcon = $scope.isPassVisible ? "fas fa-eye" : "fas fa-eye-slash";
			};

			$scope.keydownCaps = function (e) {
				if (e.keyCode == 20) {//if he pressed capslock then toggle the message,without the need to input data
					$scope.capslock = !$scope.capslock;
				}
			};

			$scope.keypressCaps = function (e) {
				e = (e) ? e : window.event;
				var charCode = false;
				if (e.which) {
					charCode = e.which;
				} else if (e.keyCode) {
					charCode = e.keyCode;
				}
				var shifton = false;
				if (e.shiftKey) {
					shifton = e.shiftKey;
				} else if (e.modifiers) {
					shifton = !!(e.modifiers & 4);
				}
				if (charCode >= 97 && charCode <= 122 && shifton) {
					$scope.capslock = true;
				} else if (charCode >= 65 && charCode <= 90 && !shifton) {
					$scope.capslock = true;
				} else {
					$scope.capslock = false;
				}
			};

			$scope.login = function (invalid) {
				if (invalid) {
					return;
				}
				loginService.loginUser($scope.user).then(function (response) {
					util.systemMessages = {};//reset to get the actual tenant labels if the user, we will fetch new labels on route change
					util.setUserData(response.data, $scope.rememberMe);
					$rootScope.$broadcast("recompileHeader");
					$rootScope.$broadcast("recompileNavigation");
					$rootScope.$broadcast("reauthorize");
					$state.go("landing-page");
				});
			};

			$scope.forgotPassword = function (ev) {
				$mdDialog.show({
					controller: ["$scope", "$mdDialog", function ($scope, $mdDialog) {
						$scope.cancel = function () {
							$mdDialog.cancel();
						};
						$scope.submit = function (invalid) {
							if (invalid) {
								return;
							}
							loginService.forgotPassword($scope.username).then(function () {
								util.createToast(util.systemMessages.success, "success");
							});
						};
					}],
					templateUrl: './' + config.lisDir + '/modules/dialogs/forgot-password.html',
					parent: angular.element(document.body),
					targetEvent: ev,
					clickOutsideToClose: true,
					locals: {
					}
				}).then(function () { }, function () { });
			};
		}
	]);
});

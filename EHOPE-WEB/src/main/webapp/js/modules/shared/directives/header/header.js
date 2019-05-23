define(['app', 'util', 'config', 'commonData'], function (app, util, config, commonData) {
	'use strict';
	app.directive('lisHeader', function () {
		return {
			restrict: 'E',
			replace: true,
			templateUrl: "./" + config.lisDir + "/modules/shared/directives/header/header.html",
			controller: ['$scope', '$interval', '$state', '$rootScope',
				function ($scope, $interval, $state, $rootScope) {
					$scope.tooltipDirection = util.direction === 'ltr' ? "left" : "right";

					$scope.userLocale = util.userLocale;
					$scope.internalHomepage = commonData.internalHomepage;
					var originatorEv;
					$scope.userGroups = "";
					$scope.fullName = "";
					if (util.token == null) {// user is not logged, this class will be removed after login is successful
						$("#header").addClass("ng-hide");
					}

					// This code, despite it ticks the clock for the timer ui, it also makes Angular $apply which will $apply for
					// every place that is outside of angular context.
					var tick = function () {
						$scope.clock = Date.now();
					}
					tick();
					$interval(tick, 500);

					$scope.toggleLeftMenu = function () {
						$rootScope.$broadcast('toggleNavMenu', null);
					};

					function headerInfo() {
						if (util.user == null || Object.keys(util.user).length == 0) {
							$scope.userGroups = "";
							$scope.fullName = "";
							return;
						}
						if (util.user.fullName != null) {
							if (util.user.fullName.hasOwnProperty(util.userLocale)) {
								$scope.fullName = util.user.fullName[util.userLocale];
							} else {
								$scope.fullName = util.user.fullName[Object.keys(util.user.fullName)[0]];
							}
						} else {
							$scope.fullName = "";
						}
						if (util.user.userGroups != null && util.user.userGroups.length > 0) {
							$scope.userGroups = util.user.userGroups.map(function (group) {
								return group.name[util.userLocale];
							}).join(",");
						} else {
							util.user.userGroups = [];
						}

						if (util.user.branch) {
							$scope.branch = util.user.branch;
						} else {
							$scope.branch = null;
						}

					}
					headerInfo();

					$scope.$on("recompileHeader", function (event, data) {
						headerInfo();
					});

					$scope.logout = function () {
						util.clearUtilData();
						$state.go("login");
					};

					$rootScope.$watch('pageTitleName', function () {
						$scope.pageTitle = $rootScope.pageTitleName;
					});

					$scope.userSettings = function ($mdMenu, ev) {
						originatorEv = ev;
						$mdMenu.open(ev);
					};

				}]
		}
	});
});

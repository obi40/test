define(['app', 'util', 'config'], function (app, util, config) {
	'use strict';
	app.directive('lisNavMenu', function () {
		return {
			restrict: 'E',
			replace: true,
			templateUrl: "./" + config.lisDir + "/modules/shared/directives/navigationMenu/navigation-menu.html",
			controller: ['$scope', '$mdSidenav', 'commonMethods',
				function ($scope, $mdSidenav, commonMethods) {
					if (util.token === null) {// user is not logged in, this class will be removed after login is successful
						$("#navMenu").addClass("ng-hide");
					}
					//$scope.navSearch = null;
					$scope.isMenuToggled = false;
					//$scope.isMenuExpanded = false;
					//$scope.expandMenuIcon = "fas fa-angle-double-down";
					$scope.navMenuItems = commonMethods.getNavMenuItems();

					$scope.$on("recompileNavigation", function (event, data) {
						$scope.navMenuItems = commonMethods.getNavMenuItems();
					});

					// $scope.expandNav = function () {
					// 	$scope.isMenuExpanded = !$scope.isMenuExpanded;
					// 	$scope.expandMenuIcon = $scope.expandMenuIcon === "fas fa-angle-double-down" ? "fas fa-angle-double-up" : "fas fa-angle-double-down";
					// };
					$scope.expandSubList = function (item) {
						// dont change value of it does not have sub list to expand or the nav menu is not exapnded
						if (item.isSubItemToggled == null || $scope.isMenuToggled == false) {
							return;
						}
						item.isSubItemToggled = !item.isSubItemToggled;
					};

					$scope.$on("toggleNavMenu", function (event, data) {
						$scope.isMenuToggled = !$scope.isMenuToggled;
						for (var idx = 0; idx < $scope.navMenuItems.length; idx++) {
							if ($scope.navMenuItems[idx].isSubItemToggled != null) {
								$scope.navMenuItems[idx].isSubItemToggled = false;
							}
						}
					});


				}]
		}
	});
});

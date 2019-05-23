define(['app', 'util', 'config'], function (app, util, config) {
	'use strict';
	/**
	 * Show a breadcrumb.
	 */
	app.directive('lisBreadcrumb', function () {
		return {
			restrict: 'E',
			replace: true,
			templateUrl: "./" + config.lisDir + "/modules/shared/directives/breadcrumb/breadcrumb-view.html",
			scope: {
				options: '=options'
			},
			controller: ['$scope', function ($scope) {
				$scope.arrowIcon = util.direction == "ltr" ? "fa fa-angle-right" : "fa fa-angle-left";
				if ($scope.options.crumbs == null) {
					$scope.options.crumbs = [];
				}
				var base = {
					callback: $scope.options.baseCallback
				};
				$scope.options.crumbs.splice(0, 0, base);
				function updatePaths() {
					if ($scope.options.crumbs != null && $scope.options.crumbs.length < 1) {
						return;
					}
					for (var idx = 0; idx < $scope.options.crumbs.length; idx++) {
						var obj = $scope.options.crumbs[idx];
						obj["level"] = idx;
						obj["isLast"] = false;
						if (idx + 1 == $scope.options.crumbs.length) {
							obj["isLast"] = true;
						}
					}
				}
				updatePaths();
				$scope.callback = function (crumb) {
					if (crumb.callback == null || crumb.isLast) {
						return;
					}
					$scope.options.eatCrumbs(crumb);
					crumb.callback(crumb);
					updatePaths();
				};
				$scope.options["crumb"] = function (newPath) {
					if ($scope.options.crumbs == null) {
						return;
					}
					var isAdded = false;
					if (newPath.level != null) {
						for (var idx = 0; idx < $scope.options.crumbs.length; idx++) {
							var obj = $scope.options.crumbs[idx];
							if (obj.level == newPath.level) {
								$scope.options.replace(idx, null, newPath);
								isAdded = true;
								break;
							}
						}
					}
					if (isAdded == false) {
						$scope.options.crumbs.push(newPath);
					}
					updatePaths();
				};
				$scope.options["eatCrumbs"] = function (crumb) {
					if ($scope.options.crumbs == null || $scope.options.crumbs.length < 1) {
						return;
					}
					for (var idx = $scope.options.crumbs.length - 1; idx >= 0; idx--) {
						var obj = $scope.options.crumbs[idx];
						if (obj.level == crumb.level) {
							break;
						}
						$scope.options.crumbs.pop();
					}
				};
				$scope.options["replace"] = function (index, oldCrumb, newCrumb) {
					if ($scope.options.crumbs == null || $scope.options.crumbs.length < 1) {
						return;
					}
					if (index != null) {
						$scope.options.crumbs.splice(index, 1, newCrumb);
					} else if (oldCrumb != null) {
						for (var idx = 0; idx < $scope.options.crumbs.length; idx++) {
							if (oldCrumb.rid == $scope.options.crumbs[idx].rid) {
								$scope.options.crumbs.splice(idx, 1, newCrumb);
								break;
							}
						}
					}

					updatePaths();
				};
				$scope.options["clear"] = function () {
					if ($scope.options.crumbs == null) {
						return;
					}
					$scope.options.crumbs = [];
					updatePaths();
				};


			}]
		}
	});
});
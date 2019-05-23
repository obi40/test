define(['app', 'config'], function (app, config) {
	'use strict';

	app.directive('transField', ["$timeout", function ($timeout) {
		return {
			restrict: 'E',
			replace: true,
			templateUrl: "./" + config.lisDir + "/modules/shared/directives/transField/trans-field.html",
			scope: {
				languages: "=languages",
				form: "=form",
				model: "=model"
			},
			controller: ['$scope', '$element', function ($scope, $element) {

				$scope.primaryLanguage = null;
				// since most likely the languages will wait till we get the metadata 
				var languagesWatcher = $scope.$watch("languages", function (newValue, oldValue) {
					if (newValue != null && newValue.length > 0) {
						var result = angular.copy($scope.languages);
						$scope.languages = [];
						for (var idx = 0; idx < result.length; idx++) {
							var obj = result[idx];
							if (obj.primary) {
								$scope.primaryLanguage = obj;
							}
							$scope.languages.push(obj);
						}
						languagesWatcher();//remove watcher
					}
				});

				$scope.showSecondary = false;
				$scope.showAllLangs = function () {
					$timeout(function () {
						$element.find(".secondary-languages md-input-container:first-child input").focus();
					}, 301);
					$scope.showSecondary = true;
				};

				$scope.hideAllLangs = function (e) {
					// if e is defined(not called by backdrop) and the element who got focused is ours then dont hide
					if (e && $element.find(".secondary-languages")[0].contains(e.relatedTarget)) {
						return;
					}
					$scope.showSecondary = false;
				};

				//assign model value into the $scope.languages
				$scope.onChange = function (lang) {
					if ($scope.model == null) {
						return;
					}
					if ($scope.model[lang.fieldName] == null) {
						$scope.model[lang.fieldName] = {};
					}
					lang.value = $scope.model[lang.fieldName][lang.language];
				};

			}]
		}
	}]);
});

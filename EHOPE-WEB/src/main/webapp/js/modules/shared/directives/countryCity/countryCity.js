define(['app', 'config'], function (app, config) {
	'use strict';
	/**
	 * Directive to display city lov depending on the selected country lov.
	 * 
	 * If model changes it reflect the changes to the data in the directive
	 */
	app.directive('countryCity', function () {
		return {
			restrict: 'E',
			replace: false,
			templateUrl: "./" + config.lisDir + "/modules/shared/directives/countryCity/country-city-view.html",
			scope: {
				model: '=model',
				form: '=form',
				options: '=options'
			},
			controller: ['$scope', 'lovService',
				function ($scope, lovService) {
					$scope.lkpCountryOptions = null;
					$scope.lkpCityOptions = null;
					function prepareDir() {
						lovService.getAnyLkpByClass({ className: "LkpCountry" }).then(function (response) {
							$scope.lkpCountryOptions = {
								className: "LkpCountry",
								name: $scope.options.country.name,
								labelText: "country",
								valueField: "name.en_us",
								selectedValue: $scope.model == null ? null : $scope.model[$scope.options.country.name],
								required: $scope.options.country.required,
								data: response.data
							};
							$scope.lkpCityOptions = {
								className: "LkpCity",
								name: $scope.options.city.name,
								labelText: "city",
								valueField: "name.en_us",
								selectedValue: $scope.model == null ? null : $scope.model[$scope.options.city.name],
								required: $scope.options.city.required,
								onParentChange: getCitiesByCountry,
								data: []
							};
						});

					}

					var optionsWatcher = $scope.$watch("options", function (newVal, oldVal) {
						if (newVal != null) {
							prepareDir();
							optionsWatcher();//remove watcher
						}
					});

					function getCitiesByCountry(selectedCountry) {
						var wrapper = {
							className: "LkpCity",
							filterablePageRequest: {
								filters: [
									{
										field: "lkpCountry.rid",
										value: selectedCountry.rid,
										operator: "eq"
									}
								]
							},
							joins: ["lkpCountry"]
						}
						return lovService.getAnyLkpByClass(wrapper).then(function (response) {
							$scope.lkpCityOptions.data = response.data;
						});
					};

					$scope.assignCountry = function (selectedValue) {
						if ($scope.model == null) {
							return;
						}
						$scope.model[$scope.options.country.name] = selectedValue;
						if ($scope.options.country.onChange) {
							$scope.options.country.onChange(selectedValue);
						}
					};

					$scope.assignCity = function (selectedValue) {
						if ($scope.model == null) {
							return;
						}
						$scope.model[$scope.options.city.name] = selectedValue;
					};

					//auto assign values
					$scope.$watch("model", function (newVal, oldVal) {
						if ($scope.lkpCountryOptions == null || $scope.lkpCityOptions == null) {
							return;
						}
						if (newVal == null) {
							$scope.lkpCountryOptions.clearLkps([$scope.lkpCountryOptions, $scope.lkpCityOptions]);
						} else if ($scope.options != null) {
							$scope.lkpCountryOptions.setValues($scope.model, [$scope.lkpCountryOptions, $scope.lkpCityOptions]);
						}
					});

				}]
		}
	});
});

define(['app'], function(app) {
	'use strict';

	app.directive("capitalizeFirst", [function() {
		return {
			require: 'ngModel',
			link: function(scope, element, attrs, modelCtrl) {
				var capitalize = function(inputValue) {
					if (inputValue === undefined) {
						inputValue = '';
					}
					var capitalized = inputValue.toLowerCase().replace(/\b[a-z](?=[a-z]{0})/g, function(letter) {
						return letter.toUpperCase();
					});
					if (capitalized !== inputValue) {
						modelCtrl.$setViewValue(capitalized);
						modelCtrl.$render();
					}
					return capitalized;
				}
				modelCtrl.$parsers.push(capitalize);
				// capitalize($parse(attrs.ngModel)(scope)); // capitalize initial value
			}
		}
	}]);
});
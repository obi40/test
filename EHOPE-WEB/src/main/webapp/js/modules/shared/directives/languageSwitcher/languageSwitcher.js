define(['app', 'config', 'util', 'commonData'], function (app, config, util, commonData) {
	'use strict';
	/**
	 * Switch language when there are no tenant. After using this directive we must clear the util data 
	 * so we can fetch the actual tenant labels.
	 */
	app.directive('languageSwitcher', function () {
		return {
			restrict: 'E',
			replace: false,
			templateUrl: "./" + config.lisDir + "/modules/shared/directives/languageSwitcher/language-switcher-view.html",
			scope: {
				options: '=options'
			},
			controller: ['$scope', 'systemMessagesService', '$translate', function ($scope, systemMessagesService, $translate) {
				$scope.languages = [];
				systemMessagesService.getSupportedLanguages().then(function (response) {
					$scope.languages = response.data;
					for (var idx = 0; idx < $scope.languages.length; idx++) {
						if ($scope.languages[idx].locale == commonData.defaultLocale) {
							$scope.options.language = $scope.languages[idx];
						}
					}
				});

				$scope.onChange = function () {
					util.user.comLanguage = $scope.options.language;//so we can change direction using prepareAppDirection
					util.userLocale = $scope.options.language.locale;
					util.prepareAppDirection();
					$translate.refresh("translations");
				};
			}]
		}
	});
});

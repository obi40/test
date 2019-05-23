define(['app', 'util', 'config'], function (app, util, config) {
  'use strict';
  app.directive('name', function () {
    return {
      require: '^form',
      replace: true,
      restrict: 'E',
      scope: {
        nameOption: '=nameOption',
        metaData: '=metaData',
        form: '=form'
      },
      templateUrl: "./" + config.lisDir + "/modules/shared/directives/name/name-view.html",
      controller: ['$scope', 'nameService', function ($scope, nameService) {

        //wait for meta data
        var metaDataWatcher = $scope.$watch("metaData", function (newValue, oldValue) {
          if (newValue && Object.keys(newValue).length > 0) {
            prepareDirective();
            metaDataWatcher();
          }
        });

        function prepareDirective() {
          var languages = angular.copy(util.languages);
          for (var idx = 0; idx < languages.length; idx++) {
            var lang = languages[idx];
            lang["names"] =
              [
                { type: "firstName" },
                { type: "secondName" },
                { type: "thirdName" },
                { type: "lastName" }
              ];
          }

          $scope.languages = languages;
        }

        function getNames(transliterationName, originLang) {
          var names = {
            firstName: transliterationName.firstName && transliterationName.firstName[originLang] ?
              transliterationName.firstName[originLang] : null,
            secondName: transliterationName.secondName && transliterationName.secondName[originLang] ?
              transliterationName.secondName[originLang] : null,
            thirdName: transliterationName.thirdName && transliterationName.thirdName[originLang] ?
              transliterationName.thirdName[originLang] : null,
            lastName: transliterationName.lastName && transliterationName.lastName[originLang] ?
              transliterationName.lastName[originLang] : null
          };
          return names;
        }
        $scope.nameTransliteration = function (transliterationName, languageObj) {
          var originLang = languageObj.language;
          var targetLang = null;
          for (var i = 0; i < util.languages.length; i++) {
            if (util.languages[i].language != languageObj.language) {
              targetLang = util.languages[i].language;
              break;
            }
          }
          var names = getNames(transliterationName, originLang);

          var firstName = names.firstName ? names.firstName : "";
          var secondName = names.secondName ? names.secondName : "";
          var thirdName = names.thirdName ? names.thirdName : "";
          var lastName = names.lastName ? names.lastName : "";
          var toTranslate = firstName + "||" + secondName + "||" + thirdName + "||" + lastName;
          var transliterationObj = {
            name: toTranslate,
            entityType: "PERSON",
            sourceLanguageOfOrigin: originLang.substr(0, 2),
            sourceLanguageOfUse: originLang.substr(0, 2),
            targetLanguage: targetLang.substr(0, 2)
          };
          nameService.getLocalTransliteration(transliterationObj)
            .then(function (response) {
              var arraySplit = response.data["translation"].split("||");
              transliterationName.firstName[targetLang] = arraySplit[0];
              transliterationName.secondName[targetLang] = arraySplit[1];
              transliterationName.thirdName[targetLang] = arraySplit[2];
              transliterationName.lastName[targetLang] = arraySplit[3];
            });
        };
        // disable or enable the translation button, fields are required depending on metaData whether 
        // its a primary language or not
        $scope.disableTranslate = function (transliterationName, languageObj) {
          if (!transliterationName || !languageObj || !$scope.metaData || Object.keys($scope.metaData).length < 1) {
            return true;
          }
          var originLang = languageObj.language;
          var names = getNames(transliterationName, originLang);
          var isFirstNameRequired = $scope.metaData["firstName"].notNull;
          var isSecondNameRequired = $scope.metaData["secondName"].notNull;
          var isThirdNameRequired = $scope.metaData["thirdName"].notNull;
          var isLastNameRequired = $scope.metaData["lastName"].notNull;

          return (!names.firstName && isFirstNameRequired) || (!names.secondName && isSecondNameRequired)
            || (!names.thirdName && isThirdNameRequired) || (!names.lastName && isLastNameRequired);
        }

      }]
    }
  });
});
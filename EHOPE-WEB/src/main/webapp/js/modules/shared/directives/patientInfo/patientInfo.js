define(['app', 'util', 'config'], function (app, util, config) {
  'use strict';
  app.directive('patientInfo', function () {
    return {
      restrict: 'E', // This means that it will be used as an element.
      replace: true,
      scope: {
        options: "=options"
      },
      templateUrl: "./" + config.lisDir + "/modules/shared/directives/patientInfo/patientInfo.html",
      controller: ["$scope", "patientProfileService", function ($scope, patientProfileService) {
        $scope.patient = {};
        $scope.userLocale = util.userLocale;
        $scope.nameLanguage = util.userNamePrimary;
        function prepare() {
          if ($scope.options == null || $scope.options.patientRid == null) {
            return;
          }
          patientProfileService.getPatientInfo($scope.options.patientRid).then(function (response) {
            $scope.patient = response.data;
          });
        }
        prepare();
        $scope.isObjectEmpty = function (obj) {
          return util.isObjectEmpty(obj);
        };
        $scope.options["refresh"] = function () {
          prepare();
        };

      }]
    }
  });
});
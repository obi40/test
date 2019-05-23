define(['app', 'util', 'config'], function (app, util, config) {
  'use strict';
  app.directive('lisCard', function () {
    return {
      restrict: 'E', // This means that it will be used as an element.
      scope: {
        patient: "=patient"
      },
      replace: true,
      templateUrl: "./" + config.lisDir + "/modules/shared/directives/cardLayout/cardLayout.html",
      controller: ['$rootScope', '$scope', '$state', 'patientLookupService',
        function ($rootScope, $scope, $state, patientLookupService) {
          $scope.languages = util.languages;
          $scope.userLocale = util.userLocale;
          $scope.viewPatientProfile = function () {
            $scope.patientProfile('view');
          };
          $scope.editPatientProfile = function () {
            $scope.patientProfile('edit');
          };
          $scope.patientProfile = function (mode) {
            $rootScope.currentPatient = $scope.patient;
            $rootScope.patientProfileMode = mode;
            $state.go("patient-profile");
          };
          $scope.payOutstandingBalance = function () {
            $rootScope.outStandingPatient = $scope.patient;
            $state.go("outstanding-balances");
          };
          function toggleButtonsState(isActive) {
            activateButton.disable = isActive;
            deactivateButton.disable = !isActive;
            createOrderButton.disable = !isActive;
            editButton.disable = !isActive;
            mergePatientButton.disable = !isActive;
          }
          $scope.deactivatePatient = function () {
            return patientLookupService.deactivatePatient($scope.patient.rid)
              .then(function () {
                $scope.patient.isActive = false;
                toggleButtonsState(false);
                util.createToast(util.systemMessages.success, "success");
              });
          };
          $scope.activatePatient = function () {
            return patientLookupService.activatePatient($scope.patient.rid)
              .then(function () {
                $scope.patient.isActive = true;
                toggleButtonsState(true);
                util.createToast(util.systemMessages.success, "success");
              });
          };
          $scope.mergePatients = function () {
            patientLookupService.mergePatientsDialog($scope.patient.rid, function (toPatient) {
              $scope.patient.isActive = false;
              $scope.patient.mergedToPatientInfo = toPatient;
              toggleButtonsState(false);
              activateButton.disable = true;//override
            });
          };
          $scope.createOrder = function () {
            $scope.patient.isInsurer = false;
            $rootScope.currentPatient = $scope.patient;
            $state.go("patient-profile-wizard");
          };
          var activateButton = {
            label: "activate",
            iconClass: 'fas fa-check',
            action: $scope.activatePatient,
            authorityChecker: "ACTIVATE_PATIENT_PROFILE",
            disable: $scope.patient.isActive || $scope.patient.mergedToPatientInfo,
            confirmClick: true
          };
          var deactivateButton = {
            label: "deactivate",
            iconClass: 'fas fa-times',
            action: $scope.deactivatePatient,
            authorityChecker: "DEACTIVATE_PATIENT_PROFILE",
            disable: !$scope.patient.isActive,
            confirmClick: true
          };
          var createOrderButton = {
            label: "createOrder",
            iconClass: 'fas fa-clipboard',
            action: $scope.createOrder,
            authorityChecker: "ADD_ORDER",
            disable: !$scope.patient.isActive,
            confirmClick: $scope.patient.balance < 0//ask for confirmation only if in debit
          };
          var mergePatientButton = {
            label: "mergePatient",
            iconClass: 'fas fa-dolly',
            action: $scope.mergePatients,
            authorityChecker: "MERGE_PATIENT_PROFILE",
            disable: !$scope.patient.isActive || $scope.patient.mergedToPatientInfo
          };
          //if we need to show a confirmation dialog then use a custom message
          if (createOrderButton.confirmClick) {
            createOrderButton["confirmClickMsg"] = "patientOutstandingBalance";
          }
          var editButton = {
            label: "edit",
            iconClass: 'fas fa-edit',
            action: $scope.editPatientProfile,
            authorityChecker: "UPD_PATIENT_PROFILE",
            disable: !$scope.patient.isActive
          };
          $scope.icons = [
            {
              label: "view",
              iconClass: 'fas fa-eye',
              action: $scope.viewPatientProfile,
              authorityChecker: "VIEW_PATIENT_PROFILE || VIEW_PATIENT_INSURANCE || VIEW_ORDERS"
            },
            editButton,
            createOrderButton,
            activateButton,
            deactivateButton,
            mergePatientButton
          ];
        }]
    }
  });
});
define(['app', 'util', 'config'], function (app, util, config) {
    'use strict';
    app.service('artifactService', ["$mdDialog", function ($mdDialog) {

        var artifactService = this;

        this.getPatientArtifact = function (data) {
            return util.createApiRequest("getPatientArtifact.srvc", JSON.stringify(data), { responseType: "blob" });
        };

        this.getOrderArtifact = function (data) {
            return util.createApiRequest("getOrderArtifact.srvc", JSON.stringify(data), { responseType: "blob" });
        };

        this.getActualTestArtifact = function (data) {
            return util.createApiRequest("getActualTestArtifact.srvc", JSON.stringify(data), { responseType: "blob" });
        };

        this.getGeneralArtifactDescriptions = function (data, type) {
            var url;
            switch (type) {
                case "order":
                    url = "getOrderArtifactDescriptions.srvc";
                    break;
                case "actualTest":
                    url = "getActualTestArtifactDescriptions.srvc";

                    break;
            }
            return util.createApiRequest(url, JSON.stringify(data));
        };

        this.saveGeneralArtifacts = function (artifactsToUpload, artifactIdsToDelete, artifactParent, type) {
            var payLoad = new FormData();
            if (artifactsToUpload && artifactsToUpload.length) {
                for (var i = 0; i < artifactsToUpload.length; i++) {
                    payLoad.append("artifacts", artifactsToUpload[i].rawFile);
                }
            }
            payLoad.append("artifactsToDelete", artifactIdsToDelete);
            payLoad.append(type, JSON.stringify(artifactParent));
            var url;
            switch (type) {
                case "order":
                    url = "saveOrderArtifacts.srvc";
                    break;
                case "actualTest":
                    url = "saveActualTestArtifacts.srvc";
                    break;
            }
            return util.createApiRequest(url, payLoad, {
                transformRequest: angular.identity,
                headers: {
                    'Content-Type': undefined
                }
            });
        }

        this.showArtifactDialog = function (artifactParent, artifactType) {
            artifactService.getGeneralArtifactDescriptions(artifactParent.rid, artifactType)
                .then(function (response) {
                    artifactParent.artifactDescriptions = response.data;
                    showArtifactDialog(artifactParent, artifactType);
                });

            function showArtifactDialog() {
                $mdDialog.show({
                    controller: ["$scope", "$mdDialog",
                        function ($scope, $mdDialog) {
                            $scope.artifactParent = artifactParent;
                            $scope.artifactType = artifactType;
                            switch ($scope.artifactType) {
                                case "order":
                                    $scope.dialogTitle = $scope.artifactParent.admissionNumber;
                                    break;
                                case "actualTest":
                                    $scope.dialogTitle = $scope.artifactParent.testDefinition.standardCode;
                                    break;
                            }

                            $scope.submit = function () {
                                var artifactsToUpload = $scope.artifactParent.getArtifactsToUpload();
                                var artifactIdsToDelete = $scope.artifactParent.getArtifactIdsToDelete();
                                if (artifactIdsToDelete.length === 0 && artifactsToUpload.length === 0) {
                                    $mdDialog.cancel();
                                } else {
                                    artifactService.saveGeneralArtifacts(artifactsToUpload, artifactIdsToDelete, artifactParent, $scope.artifactType)
                                        .then(function (response) {
                                            util.createToast(util.systemMessages.success, "success");
                                            $mdDialog.cancel();
                                        });
                                }
                            }

                            $scope.cancel = function () {
                                $mdDialog.cancel();
                            };
                        }],
                    locals: {
                        artifactParent: artifactParent,
                        artifactType: artifactType
                    },
                    templateUrl: './' + config.lisDir + '/modules/dialogs/artifact-dialog.html',
                    parent: angular.element(document.body),
                    clickOutsideToClose: true,
                    fullscreen: false
                });
            }
        }

    }]);
});

define(['app', 'util', 'config', 'commonData'], function (app, util, config, commonData) {
    'use strict';
    app.directive('artifact', function () {
        return {
            restrict: 'E',
            replace: true,
            scope: {
                parent: "=parent",
                type: "=type",
                viewOnly: "=?viewOnly"
            },
            templateUrl: "./" + config.lisDir + "/modules/shared/directives/artifact/artifact.html",
            controller: ['$scope', '$element', 'artifactService', function ($scope, $element, artifactService) {
                //NOTE: The directive should be disabled using ng-if before saving the files, 
                //and re-enabled after saving to view the newly saved files

                var previousArtifacts = [];
                if ($scope.parent.artifactDescriptions) {
                    for (var i = 0; i < $scope.parent.artifactDescriptions.length; i++) {
                        var artifact = $scope.parent.artifactDescriptions[i];
                        //the order of the data in the array is determined by the named query columns
                        previousArtifacts.push({
                            rid: artifact[0],
                            name: artifact[1],
                            size: artifact[2],
                            extension: artifact[3],
                            type: artifact[4]
                        });
                    }
                }

                var options = {
                    async: {
                        saveUrl: "save",
                        removeUrl: "remove",
                        batch: false,
                        autoUpload: false
                    },
                    files: previousArtifacts,
                    template: function (e) {
                        var file = e.files[0];
                        var classToAdd = "";
                        var downloadButton = "";
                        if (file.rid) {
                            classToAdd = "green-text";
                            downloadButton = '<button type="button" title="' + util.systemMessages.download + '" class="md-button md-icon-button download-button" ng-click="downloadFile()">'
                                + '<i class="fas fa-file-download"></i>'
                                + '</button>';
                        }
                        var iconClass = util.getIconFromExtension(file.extension);
                        var template = '<div class="file-wrapper">'
                            + '<div class="file-details-wrapper">'
                            + '<div class="file-icon-wrapper"><i class="fas fa-2x ' + iconClass + '"></i></div>'
                            + '<div class="text-wrapper">'
                            + '<div class="name-wrapper ' + classToAdd + '" title="' + file.name + '">' + file.name + '</div>'
                            + '<div class="size-wrapper">' + util.bytesToSizes(file.size) + '</div>'
                            + '</div>'
                            + '</div>'
                            + '<div class="button-wrapper">'
                            + downloadButton
                            + '<button type="button" title="' + util.systemMessages.delete + '" class="md-button md-icon-button delete-button" ng-click="downloadFile">'
                            + '<i class="fas fa-times"></i>'
                            + '</button>'
                            + '</div>'
                            + '</div>';
                        return template;
                    }
                };
                var artifactWrapper = $($element[0]);
                artifactWrapper.empty();
                artifactWrapper.append("<input type='file' class='artifact-upload'/>");
                artifactWrapper.find(".artifact-upload").kendoUpload(options);
                var artifactUploadWidget = artifactWrapper.find(".artifact-upload").data("kendoUpload");
                artifactWrapper.find(".k-upload").click(function (event) {
                    var target = $(event.target);
                    if (target.hasClass("download-button") || target.parent().hasClass("download-button")) {
                        downloadFile(target.closest("li.k-file").attr("data-uid"));
                    }
                    if (target.hasClass("delete-button") || target.parent().hasClass("delete-button")) {
                        removeFile(target.closest("li.k-file").attr("data-uid"));
                    }
                });

                function removeFile(uid) {
                    var files = artifactUploadWidget.getFiles();
                    var file;
                    for (var i = 0; i < files.length; i++) {
                        if (files[i].uid === uid) {
                            file = files[i];
                            if (file.rid) {
                                setMarkedForDeletion(file.rid);
                                artifactUploadWidget.clearFileByUid(file.uid);
                            } else {
                                artifactUploadWidget.removeFileByUid(file.uid);
                            }
                            break;
                        }
                    }
                }

                function downloadFile(uid) {
                    var files = artifactUploadWidget.getFiles();
                    var file;
                    for (var i = 0; i < files.length; i++) {
                        if (files[i].uid === uid) {
                            file = files[i];
                            break;
                        }
                    }
                    switch ($scope.type) {
                        case "patient":
                            artifactService.getPatientArtifact(file.rid)
                                .then(function (response) {
                                    util.fileHandler(response.data, { type: file.type, name: file.name });
                                });
                            break;
                        case "order":
                            artifactService.getOrderArtifact(file.rid)
                                .then(function (response) {
                                    util.fileHandler(response.data, { type: file.type, name: file.name });
                                });
                            break;
                        case "actualTest":
                            artifactService.getActualTestArtifact(file.rid)
                                .then(function (response) {
                                    util.fileHandler(response.data, { type: file.type, name: file.name });
                                });
                            break;
                    }
                }

                function setMarkedForDeletion(rid) {
                    for (var i = 0; i < $scope.parent.artifactDescriptions.length; i++) {
                        if ($scope.parent.artifactDescriptions[i][0] === rid) {
                            $scope.parent.artifactDescriptions[i].push(true)
                            break;
                        }
                    }
                }

                $scope.parent.getArtifactIdsToDelete = function () {
                    var artifactIdsToDelete = [];
                    if ($scope.parent.artifactDescriptions) {
                        for (var i = 0; i < $scope.parent.artifactDescriptions.length; i++) {
                            if ($scope.parent.artifactDescriptions[i][5]) { //index 5: boolean for deletion
                                artifactIdsToDelete.push($scope.parent.artifactDescriptions[i][0]); //index 0: rid
                            }
                        }
                    }
                    return artifactIdsToDelete;
                }

                $scope.parent.getArtifactsToUpload = function () {
                    var artifacts = artifactUploadWidget.getFiles();
                    var artifactsToUpload = [];
                    for (var i = 0; i < artifacts.length; i++) {
                        if (!artifacts[i].rid) {
                            artifactsToUpload.push(artifacts[i]);
                        }
                    }
                    return artifactsToUpload;
                }
            }]
        }
    });
});
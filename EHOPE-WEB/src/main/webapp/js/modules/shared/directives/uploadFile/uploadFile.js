define(['app', 'config'], function (app, config) {
    /**
     * This directive is to upload any 'single' type of files.
     * 
     * 1-fileDataWrapper:
     * a. types: the accepted types without dots.
     * b. oldFile :  
     * c. fileModel : 
     * d. labelCode : labelCode to appear on the upload button.
     */
    //TODO: General Preview of any type of files ? currently viewing images only
    'use strict';
    app.directive('uploadFile', function () {
        return {
            restrict: 'E',
            replace: true,
            templateUrl: "./" + config.lisDir + "/modules/shared/directives/uploadFile/uploadFile.html",
            scope: {
                fileDataWrapper: "=fileDataWrapper"
            },
            link: function ($scope, $element, attributes) {
                $($element).find('.file-input').bind("change", function (changeEvent) {
                    $scope.fileDataWrapper.fileModel = undefined;
                    $scope.newFile = undefined;
                    if (changeEvent.target.files.length > 0) {
                        $scope.fileDataWrapper.fileModel = changeEvent.target.files[0];
                        var reader = new FileReader();
                        reader.onload = function (loadEvent) {
                            $scope.$apply(function () {
                                $scope.fileDataWrapper.oldFile = undefined;
                                $scope.newFile = loadEvent.target.result; //base64
                            });
                        }
                        reader.readAsDataURL(changeEvent.target.files[0]);
                    }
                });
            },
            controller: ['$scope', '$element', function ($scope, $element) {
                $scope.isImage = false;
                var typeStringsArray = [];
                $scope.fileDataWrapper.types.forEach(function (item) {
                    typeStringsArray.push("." + item);
                });
                $scope.typeStrings = typeStringsArray.join(", ");
                if ($scope.fileDataWrapper.types.indexOf("jpg") != -1 ||
                    $scope.fileDataWrapper.types.indexOf("jpeg") != -1 ||
                    $scope.fileDataWrapper.types.indexOf("png") != -1 ||
                    $scope.fileDataWrapper.types.indexOf("gif") != -1) {
                    $scope.isImage = true;
                }
                $scope.openFileDialog = function () {
                    $($element).find('.file-input').trigger('click');
                };
                $scope.fileDataWrapper["reset"] = function () {
                    $scope.fileDataWrapper.oldFile = undefined;
                    $scope.fileDataWrapper.fileModel = undefined;
                    $scope.newFile = undefined;
                    $($element).find('.file-input').val('');
                };

            }]
        }
    });
});
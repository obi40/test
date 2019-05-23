define(['app', 'util'], function (app, util) {
    'use strict';
    app.controller('separationFactorsCtrl', [
        '$scope',
        'separationFactorsService',
        function (
            $scope,
            separationFactorsService
        ) {
            $scope.allFactors = [];
            $scope.labFactors = [];
            $scope.compareTypeList = null;
            $scope.userLocale = util.userLocale;
            function prepareFactors() {
                for (var allFactorKey in $scope.allFactors) {
                    var factorObj = $scope.allFactors[allFactorKey];
                    factorObj["isActive"] = factorObj.isFixed;// if it is fixed then set isActive to null, for ui
                    for (var labFactorKey in $scope.labFactors) {
                        var labBranchFactorObj = $scope.labFactors[labFactorKey];
                        if (factorObj.rid == labBranchFactorObj.labSeparationFactor.rid) {
                            factorObj["isActive"] = labBranchFactorObj.isActive;
                        }
                    }
                }
            }
            separationFactorsService.getSepFactorList()
                .then(function (response) {
                    $scope.allFactors = response.data.allFactors;
                    $scope.labFactors = response.data.labFactors;
                    prepareFactors();
                });

            $scope.updateFactors = function (isValid) {
                if (!isValid) {
                    return;
                }
                var branchSeparationFactorList = [];
                for (var allFactorKey in $scope.allFactors) {
                    var factorObj = $scope.allFactors[allFactorKey];
                    branchSeparationFactorList.push({
                        branch_id: util.user.branchId,
                        labSeparationFactor: factorObj,
                        isActive: factorObj.isActive
                    });
                }

                separationFactorsService.updateBranchSepFactor(branchSeparationFactorList)
                    .then(function (response) {
                        util.createToast(util.systemMessages.success, "success");
                        $scope.labFactors = response.data;
                        prepareFactors();
                    });
            };
        }
    ]);
});

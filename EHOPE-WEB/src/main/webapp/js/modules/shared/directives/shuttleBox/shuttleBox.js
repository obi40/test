define(['app', 'util', 'config'], function (app, util, config) {
	'use strict';
	/**
	 * Directive to disaply data as a shuttle boxes.
	 * 
	 * 1- options ->
	 * 	a. getData: the api request to fetch the data, the function MUST return the request + the data. 
	 * 	b. getDataPayload: a payload to getData request. [optional]
	 * 	c. dataField: the field in the object to display in the shuttle.
	 * 
	 *  API:
	 *  a.clearSelection: clear shuttle selection and reset search input.
	 *  b.getSelectedData: get the selected data by user.
 	 */
	app.directive('shuttleBox', function () {
		return {
			restrict: 'E',
			replace: true,
			templateUrl: "./" + config.lisDir + "/modules/shared/directives/shuttleBox/shuttle-box-view.html",
			scope: {
				options: '=options'
			},
			controller: ['$scope', function ($scope) {
				var originalData = [];
				$scope.originalDataLength = -1;
				$scope.data = [];
				$scope.selectedData = [];
				$scope.quickSearchValue = "";

				$scope.options.getData($scope.options.getDataPayload).then(function (response) {
					originalData = response.data;
					$scope.originalDataLength = originalData.length;
					$scope.data = angular.copy(response.data);
				});

				$scope.objectLabel = function (obj) {
					return util.getDeepValueInObj(obj, $scope.options.dataField);
				};

				$scope.quickSearchListener = function () {
					if ($scope.quickSearchValue != null && $scope.quickSearchValue.length > 0) {
						$scope.data = [];
						for (var idx = 0; idx < originalData.length; idx++) {
							var label = $scope.objectLabel(originalData[idx]).toString().toLowerCase();
							if (label.indexOf($scope.quickSearchValue.toString().toLowerCase()) != -1) {
								$scope.data.push(originalData[idx]);
							}
						}
					} else {
						$scope.data = angular.copy(originalData);
					}

					for (var idx = 0; idx < $scope.selectedData.length; idx++) {
						for (var i = 0; i < $scope.data.length; i++) {
							if ($scope.data[i].rid == $scope.selectedData[idx].rid) {
								$scope.data.splice(i, 1);
								break;
							}
						}
					}
				};

				$scope.options.moveData = function (data, isSelect) {
					if (isSelect) {
						for (var idx = 0; idx < $scope.data.length; idx++) {
							if (data.rid == $scope.data[idx].rid) {
								$scope.data.splice(idx, 1);
								break;
							}
						}
						$scope.selectedData.push(data);
					} else {
						for (var idx = 0; idx < $scope.selectedData.length; idx++) {
							if (data.rid == $scope.selectedData[idx].rid) {
								$scope.selectedData.splice(idx, 1);
								break;
							}
						}
						$scope.data.push(data);
					}
				};

				$scope.selectAllData = function () {
					$scope.selectedData = angular.copy(originalData);
					$scope.data = [];
				};

				$scope.options.clearSelection = function () {
					$scope.data = angular.copy(originalData);
					$scope.selectedData = [];
					$scope.quickSearchValue = "";
					$scope.quickSearchListener();
				};

				$scope.options.getSelectedData = function () {
					return angular.copy($scope.selectedData);
				};



			}]
		}
	});
});
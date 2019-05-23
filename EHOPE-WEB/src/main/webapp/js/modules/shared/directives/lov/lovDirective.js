define(['app', 'util', 'config'], function (app, util, config) {
	'use strict';
	/**
	 * Directive to display any listed data (mainly for Lookups, but can be used to display NON Lookup classes).
	 * 
	 * 1- options ->
	 * 	a. className: the fully Lkp class name.
	 * 	b. valueField: the key to be used to know what to display in the list,also used in the search functionality.
	 * 	c. name: name of the Lkp, will be used by the developer to put the name key inside the object to send to server.
	 * 	d. labelText: the label of the list.
	 * 	e. selectedValue: the user selected value.
	 * 	f. required: is the list required?
	 * 	g. joins: list of joins for the Lkp [optional].
	 *  h. filterablePageRequest: that has-> [optional].
	 * 		1. filters: list of SearchCriterion [optional].
	 *  	2. operator: a JunctionOperator enum [optional].
	 *  	3. sortList: list of OrderObject [optional].
	 * 	i. data: use this when the directive will display a NON Lkp class, so it will skip calling the getLkpByClass() [optional].
	 *  j. onParentChange: a callback function to fetch new data depending on the parent value [optional], must RETURN the $http object and the new options of the lov.
	 *  k. noneLabel: custom string to display instead of 'none' [optional].
	 * 
	 * 2- select -> selected value.
	 * 3- form -> the current form.
	 * 4- disableSelect -> to disable the selection [optional]
	 * 5- onChange -> a callback function to be executed on value change [optional]
	 * 6- parent -> the parent value of this lov [optional]
	 * 7- onChangeParams -> params to send with the onChange callback [optional]
	 */
	app.directive('lov', ["$timeout", function ($timeout) {
		return {
			restrict: 'E',
			replace: true,
			templateUrl: "./" + config.lisDir + "/modules/shared/directives/lov/lov-view.html",
			scope: {
				options: '=options',
				select: '=select',
				form: '=form',
				disableSelect: '=disableSelect',
				onChange: '=onChange',
				onChangeParams: '=onChangeParams',
				parent: '=parent'
			},
			link: function ($scope, $element) {
				var scrollerElement = $($element[0]).find('md-select-menu._md md-content._md');
				angular.element(scrollerElement).bind('scroll', function () {
					if (scrollerElement[0].scrollTop + scrollerElement[0].offsetHeight >= (scrollerElement[0].scrollHeight - 200)) {
						$scope.showMore();
					}
				});
			},
			controller: ['$scope', '$element', 'lovService', '$filter',
				function ($scope, $element, lovService, $filter) {
					var maximumDataLength = 100;
					var searchTimer = null;
					var valueField = $scope.options.valueField;
					$scope.noneLabel = $scope.options.noneLabel;
					$scope.isOpened = false;//used to toggle html classes
					$scope.allValues = [];
					$scope.options.searchValue = null;
					if (!$scope.noneLabel) {
						$scope.noneLabel = $filter('translate')('none');
					}
					$scope.searchValues = function ($event) {
						//$event is not null when it is a user's interaction
						if ($scope.options.searchValue == null) {
							return;
						} else if ($scope.options.searchValue === "") {
							$scope.clear();
							return;
						}
						var searchValue = ($scope.options.searchValue).toLowerCase();
						if (searchTimer) {
							$timeout.cancel(searchTimer);
						}
						searchTimer = $timeout(function () {
							if ($event) {
								$scope.select = null;
								$scope.options.selectedValue = null;
							}
							$scope.options.data = [];
							for (var idx = 0; idx < $scope.allValues.length; idx++) {
								//dont add more than maximumDataLength
								if ($scope.options.data.length > maximumDataLength) {
									break;
								}
								var lkpValue = (getDeepValueInObj($scope.allValues[idx], valueField));
								if (lkpValue.toLowerCase().indexOf(searchValue) != -1) {
									$scope.options.data.push($scope.allValues[idx]);
								}
							}
						}, 250);
					};
					$scope.options["populateData"] = function () {
						$scope.options.data = [];
						for (var idx = 0; idx < maximumDataLength; idx++) {
							if (idx < $scope.allValues.length) {
								$scope.options.data.push($scope.allValues[idx]);
							}
						}
						if ($scope.options.selectedValue != null) {
							$scope.options.searchValue = (getDeepValueInObj($scope.options.selectedValue, valueField));
							$scope.searchValues(null);// means it is not a user interaction
						}
					};
					function prepareLov() {
						$scope.allValues = [];
						if ($scope.options.required === undefined) {
							$scope.options.required = false;
						}
						//default field for Lkp classes, in this case the $scope.options.valueField will be the locale for Lkps(backward compatibility)
						valueField = $scope.options.valueField;
						if ($scope.options.data) {
							$scope.allValues = angular.copy($scope.options.data);
							$scope.options.populateData();
						} else {
							var lkpWrapper = {
								"className": $scope.options.className,
								"joins": $scope.options.joins, //optional
								"filterablePageRequest": $scope.options.filterablePageRequest//optional
							};
							lovService.getLkpByClass(lkpWrapper).then(function (data) {
								$scope.allValues = angular.copy(data);
								// if ($scope.allValues.length == 1) {
								// 	$scope.select = $scope.allValues[0];
								// 	$scope.options.selectedValue = $scope.allValues[0];
								// }
								$scope.options.populateData();
							});
						}
					}
					prepareLov();
					$scope.showMore = function () {
						if (!$scope.options.data || $scope.options.data.length == 0) {
							return;
						}
						var dataLastRid = $scope.options.data[$scope.options.data.length - 1].rid;
						var lastIndexByData = $scope.allValues.map(function (d) { return d.rid }).indexOf(dataLastRid);
						lastIndexByData++;//starting index for the new objects
						if ($scope.options.searchValue != null) {
							var searchValue = $scope.options.searchValue.toLowerCase();
							for (var idx = 0; idx < maximumDataLength; idx++) {
								for (var i = lastIndexByData; i < $scope.allValues.length; i++) {
									var lkpValue = (getDeepValueInObj($scope.allValues[i], valueField));
									if (lkpValue.toLowerCase().indexOf(searchValue) != -1) {
										lastIndexByData = i + 1;//to start the next search from the last obj we found
										$scope.options.data.push($scope.allValues[i]);
										break;
									}
								}
							}
						} else {
							for (var idx = 0; idx < maximumDataLength; idx++) {
								// check if we reached allValues total length
								if ((idx + lastIndexByData) < $scope.allValues.length) {
									$scope.options.data.push($scope.allValues[(idx + lastIndexByData)]);
								}
							}
						}

					};
					$scope.onOpen = function () {
						$scope.isOpened = true;
					};
					$scope.onClose = function () {
						$scope.isOpened = false;
					};
					var warningBool = false;
					function logWarning(msg) {
						if (!warningBool) {
							warningBool = true;
							console.warn(msg);
						}
					}

					$scope.highlightSearched = function (lkp) {
						var lkpValue = (getDeepValueInObj(lkp, valueField));
						if (lkpValue == null) {
							logWarning("Can't find-> " + valueField + " in Lov entity");
							return;
						}
						if ($scope.options.searchValue == null || $scope.options.searchValue == "") {
							return lkpValue;
						}

						var searchedIndex = lkpValue.toLowerCase().indexOf(($scope.options.searchValue).toLowerCase());
						if (searchedIndex == -1) {
							return lkpValue;
						}
						var classes = $scope.isOpened ? "lov-text-search-highlight" : "";
						var result = "<span>" + lkpValue.substr(0, searchedIndex) +
							"&zwj;<span class='" + classes + "'>" + lkpValue.substr(searchedIndex, $scope.options.searchValue.length) + "&zwj;</span>" +
							lkpValue.substr(searchedIndex + $scope.options.searchValue.length) + "</span>";

						// to avoid zwj (zero-width-joiner) in the end of the text
						if ((searchedIndex + $scope.options.searchValue.length) == lkpValue.length || result.charAt(result.indexOf("</span>") + 7) == " ") {
							result = result.replace("&zwj;</span>", "</span>");
						}

						return result;
					};

					$element.find('input').on('keydown', function (ev) {
						ev.stopPropagation();
					});

					$scope.change = function (selectedValue) {
						//>>>>>>>>>
						//this makes the form pristine when the same value is selected automatically (solves the result-amendment dirty issue)
						var firstRid = -1;
						var secondRid = -2;
						if ($scope.options.selectedValue && $scope.options.selectedValue.hasOwnProperty("rid")) {
							firstRid = $scope.options.selectedValue.rid;
						}
						if (selectedValue && selectedValue.hasOwnProperty("rid")) {
							secondRid = selectedValue.rid;
						}
						if (firstRid == secondRid) {
							var controls = $scope.form.$$controls;
							$scope.form.$setPristine();
							for (var i = 0; i < controls.length; i++) {
								var control = controls[i];
								if (control.$name === $scope.options.className) {
									control.$setPristine();
								}
							}
						}
						//<<<<<<<<<
						if (!$scope.onChange) {
							return;
						}
						$scope.options.selectedValue = selectedValue;//the change event happens before applying the value in the model
						$scope.onChange(selectedValue, $scope.onChangeParams);
					};

					$scope.$watch("parent", function () {
						if (!$scope.parent || !$scope.options.onParentChange) {
							return;
						}
						$scope.options.onParentChange($scope.parent).then(function () {
							prepareLov();
						});

					});

					$scope.options["clearLkps"] = function (lkpList) {
						for (var idx = 0; idx < lkpList.length; idx++) {
							var lkp = lkpList[idx];
							$scope.select = null;
							lkp.selectedValue = null;
							lkp.searchValue = null;
							lkp.populateData();
						}
					};
					// clear button inside search input
					$scope.clear = function () {
						$scope.select = null;
						$scope.options.selectedValue = null;
						$scope.options.searchValue = null;
						$scope.options.populateData();
					};
					$scope.options["setValues"] = function (obj, lkpList, fieldNameMap) {
						/**
						 * Assign values from the obj to lkp.
						 * obj: the object to inject the value in.
						 * lkpList: all the lkps
						 * fieldNameMap: a map to assign a custom value from the lkp. the mapping should be className:customValue [optional]
							*/
						for (var idx = 0; idx < lkpList.length; idx++) {
							var lkp = lkpList[idx];
							lkp.selectedValue = null;
							var fieldName = fieldNameMap ? fieldNameMap[lkp.className] : null;
							if (fieldName == null) {
								lkp.selectedValue = obj[lkp.name];
								lkp.populateData();
							} else {
								for (var i = 0; i < lkp.data.length; i++) {
									if (lkp.data[i][fieldName] == obj[lkp.name]) {
										lkp.selectedValue = lkp.data[i];
										lkp.populateData();
										break;
									}
								}
							}
						}
					};
					$scope.options["assignValues"] = function (obj, lkpList, fieldNameMap) {
						/**
						 * Assign values from the lkps to the obj.
						 * obj: the object to inject the value in.
						 * lkpList: all the lkps
						 * fieldNameMap: a map to assign a custom value from the lkp. the mapping should be className:customValue [optional]
						 */
						for (var idx = 0; idx < lkpList.length; idx++) {
							var lkpValue = lkpList[idx];
							if (lkpValue.selectedValue == null) {
								obj[lkpValue.name] = null;
								continue;
							}
							var fieldName = fieldNameMap ? fieldNameMap[lkpValue.className] : null;
							if (fieldName == null) {
								obj[lkpValue.name] = lkpValue.selectedValue;
							} else {
								obj[lkpValue.name] = lkpValue.selectedValue[fieldName];
							}
						}
					};
					function getDeepValueInObj(obj, nestedKeys) {
						/**
						 * Get the deep value in the object even in case of list keys.
						 * obj: the object to search for value in it.
						 * nestedKeys: the key inside of the object to be fetched. i.e. obj.groups[3].name.en_us,obj.age.DoB
						 */
						nestedKeys = nestedKeys.replace(/\[(\w+)\]/g, '.$1'); // convert indexes to properties
						nestedKeys = nestedKeys.replace(/^\./, '');// strip a leading dot
						var a = nestedKeys.split('.');
						for (var i = 0, n = a.length; i < n; ++i) {
							var k = a[i];
							if (k in obj) {
								obj = obj[k];
							} else {
								if (obj[util.userPrimary] != null) {
									obj[util.userPrimary] = obj[util.userPrimary].toString();
								}
								return obj[util.userPrimary];
							}
						}
						if (obj != null) {
							obj = obj.toString();
						}
						return obj;
					}
					//To insert Lkp data.
					//Also to fix an issue where if the lkp has no data and then it got injected with some data.
					$scope.options["updateData"] = function (newData) {
						$scope.options.data = newData;
						prepareLov();
					};
				}]
		}
	}]);
});
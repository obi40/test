define(['app', 'config', 'util', 'commonData'], function (app, config, util, commonData) {
    'use strict';
    app.controller('rolesManagementCtrl', [
        '$scope',
        '$timeout',
        'rolesManagementService',
        'usersManagementService',
        'groupsManagementService',
        'commonMethods',
        function (
            $scope,
            $timeout,
            rolesManagementService,
            usersManagementService,
            groupsManagementService,
            commonMethods
        ) {
            $scope.editSecRoleTemplate = config.lisDir + "/modules/component/rolesManagement/roles-management-edit.html";
            $scope.mainTemplate = true;
            var disableBtnDelay = 1000;
            $scope.refreshGridActive = false;// to enable/disable the refresh button after time
            $scope.newSecRole = {};
            $scope.selectedSecRole = null;
            $scope.secRoleMetaData = {};
            $scope.secRoleLKPs = [];
            $scope.secRoleTransFields = {};
            $scope.quickSearchRight = {
                right: ""
            };
            $scope.rightsByParent = [];
            $scope.selectedRights = [];
            $scope.allRights = [];
            $scope.userLocale = util.userLocale;
            commonMethods.retrieveMetaData("SecRole").then(function (response) {
                $scope.secRoleMetaData = response.data;
                $scope.secRoleTransFields = {
                    name: util.getTransFieldLanguages("name", "name", null, $scope.secRoleMetaData.name.notNull)
                };
            });

            $scope.groupShuttleBoxOptions = {
                getData: groupsManagementService.getSecGroupList,
                dataField: "name." + util.userLocale,
                shuttleLabel: util.systemMessages.groups
            };

            rolesManagementService.getSecRightList().then(function (response) {
                $scope.allRights = response.data;
                $scope.rightsByParent = populateRightsTree(angular.copy($scope.allRights));
            });

            $scope.removeChip = function (chip) {
                var grid = $("#userViewGrid").data("kendoGrid");
                util.removeGridChip(chip, grid);
            };
            $scope.userChipsOptions = {
                data: [],
                label: "username",
                onRemove: $scope.removeChip
            };



            $scope.shuttleExpandParent = function (parent) {
                parent.expanded = !parent.expanded;
            };

            function movingItems(item) {
                if (item.checked) {
                    $scope.selectedRights.push(item);
                } else {
                    var idx = $scope.selectedRights.map(function (i) {
                        return i.rid;
                    }).indexOf(item.rid);
                    if (idx > -1) {
                        $scope.selectedRights.splice(idx, 1);
                    }
                }
            }

            $scope.shuttleItemListener = function (item, toDeselect) {
                $timeout(function () {// waiting the new checked value
                    if (item.isParent) {
                        item = getParent(item.rid);
                        for (var idx = 0; idx < item.children.length; idx++) {
                            // skip already checked
                            if (item.children[idx].checked == item.checked) {
                                continue;
                            }
                            item.children[idx].checked = item.checked;
                            movingItems(item.children[idx]);
                        }
                    } else {
                        item = getRightInParent(item.rid);
                        if (toDeselect) {
                            item.checked = false;
                            movingItems(item);
                        } else {
                            movingItems(item);
                        }
                        triggerParentCheck();
                    }
                });
            };

            function triggerParentCheck() {
                // check/un-check parent if there are no children are checked
                for (var idx = 0; idx < $scope.rightsByParent.length; idx++) {
                    var checkValue = false;
                    for (var i = 0; i < $scope.rightsByParent[idx].children.length; i++) {
                        if ($scope.rightsByParent[idx].children[i].checked) {
                            checkValue = true;
                            break;
                        }
                    }
                    $scope.rightsByParent[idx].checked = checkValue;
                }
            }

            $scope.moveAllRights = function (value) {
                for (var idx = 0; idx < $scope.rightsByParent.length; idx++) {
                    $scope.rightsByParent[idx].checked = value;
                    for (var i = 0; i < $scope.rightsByParent[idx].children.length; i++) {
                        if ($scope.rightsByParent[idx].children[i].checked == value) {
                            continue;
                        }
                        $scope.rightsByParent[idx].children[i].checked = value;
                        movingItems($scope.rightsByParent[idx].children[i]);
                    }
                }
            };

            function getRightInParent(rid) {
                for (var i = 0; i < $scope.rightsByParent.length; i++) {
                    for (var y = 0; y < $scope.rightsByParent[i].children.length; y++) {
                        if ($scope.rightsByParent[i].children[y].rid == rid) {
                            return $scope.rightsByParent[i].children[y];
                        }
                    }
                }
                return null;
            }
            function getParent(rid) {
                for (var i = 0; i < $scope.rightsByParent.length; i++) {
                    if ($scope.rightsByParent[i].rid == rid) {
                        return $scope.rightsByParent[i];
                    }
                }
                return null;
            }

            $scope.quickSearchRightListener = function () {
                $scope.rightsByParent = [];
                if ($scope.quickSearchRight.right != null && $scope.quickSearchRight.right.length > 0) {
                    $scope.quickSearchRight.right = $scope.quickSearchRight.right.toLowerCase();
                    var rights = [];
                    for (var idx = 0; idx < $scope.allRights.length; idx++) {
                        var right = $scope.allRights[idx];
                        if (right.name[util.userLocale].toLowerCase().indexOf($scope.quickSearchRight.right) != -1
                            || right.sysPage.name[util.userLocale].toLowerCase().indexOf($scope.quickSearchRight.right) != -1) {
                            rights.push(right);
                        }
                    }
                    $scope.rightsByParent = populateRightsTree(angular.copy(rights));
                } else {
                    $scope.rightsByParent = populateRightsTree(angular.copy($scope.allRights));
                }
                for (var idx = 0; idx < $scope.selectedRights.length; idx++) {
                    var right = getRightInParent($scope.selectedRights[idx].rid);
                    if (right != null) {
                        right.checked = true;
                    }
                }
                triggerParentCheck();

            };

            function clearGroupsRightsShuttles() {
                //reset role shuttles
                $scope.moveAllRights(false);
                $scope.quickSearchRight = {
                    right: ""
                };
                $scope.quickSearchRightListener();
                //reset group shuttle
                $scope.groupShuttleBoxOptions.clearSelection();
            }

            function populateRightsTree(rightsArray) {
                var groupedRightsByPages = [];
                var rightsWithNoPages = [];
                for (var idx = 0; idx < rightsArray.length; idx++) {
                    var right = rightsArray[idx];
                    var page = right.sysPage;
                    var selectedLabel = page != null ? page.name[util.userLocale] + commonData.arrow + right.name[util.userLocale] : right.name[util.userLocale];
                    var rightTreeObj =
                    {
                        rid: right.rid,
                        data: right,
                        label: right.name[util.userLocale],
                        selectedLabel: selectedLabel,
                        checked: false,
                        isParent: false
                    };
                    if (page != null) { //this right belong to a page
                        var toAdd = true;
                        var index = -1;
                        for (var i = 0; i < groupedRightsByPages.length; i++) {
                            if (groupedRightsByPages[i].rid == page.rid) {
                                toAdd = false;
                                index = i;
                                break;
                            }
                        }
                        if (toAdd) { // add new parent along with the current right
                            var pageTreeObj =
                            {
                                rid: page.rid,
                                data: page,
                                label: page.name[util.userLocale],
                                children: [],
                                checked: false,
                                expanded: false,
                                isParent: true
                            };
                            pageTreeObj.children.push(rightTreeObj);
                            groupedRightsByPages.push(pageTreeObj);
                        } else {// this page exists so add the right to it
                            groupedRightsByPages[index].children.push(rightTreeObj);
                        }
                    } else {// this right does not belong to any page
                        rightsWithNoPages.push(rightTreeObj);
                    }
                }

                for (var idx = 0; idx < rightsWithNoPages.length; idx++) {
                    groupedRightsByPages.push(rightsWithNoPages[idx]);
                }
                groupedRightsByPages.sort(function (a, b) {
                    return (a.label > b.label) ? 1 : ((b.label > a.label) ? -1 : 0);
                });
                return groupedRightsByPages;
            }

            $scope.toggleView = function () {
                $scope.mainTemplate = !$scope.mainTemplate;
            };

            $scope.submitForm = function (isValid) {
                if (isValid) {
                    if ($scope.selectedSecRole == null) {
                        createSecRole();
                    } else {
                        updateSecRole();
                    }
                }
            };

            $scope.createMode = function () {
                $scope.selectedSecRole = null;
                $scope.newSecRole = {};
                for (var i in util.languages) {
                    var nameLang = $scope.secRoleTransFields.name[i];
                    nameLang.value = null;
                }
                clearGroupsRightsShuttles();
                clearUserViewGridSelection();
                $scope.toggleView();
            };

            $scope.editMode = function () {

                clearGroupsRightsShuttles();
                clearUserViewGridSelection();
                $scope.newSecRole = angular.copy($scope.selectedSecRole);
                rolesManagementService.getSecUsersListByRole($scope.newSecRole)
                    .then(function (response) {
                        refreshUserViewGrid();
                        $scope.userChipsOptions.data = response.data;
                        for (var idx = 0; idx < $scope.newSecRole.roleRights.length; idx++) {
                            var right = getRightInParent($scope.newSecRole.roleRights[idx].rid);
                            if (right != null) {
                                right.checked = true;
                                movingItems(right);
                            }
                        }
                        triggerParentCheck();
                        for (var idx = 0; idx < $scope.newSecRole.roleGroups.length; idx++) {
                            $scope.groupShuttleBoxOptions.moveData($scope.newSecRole.roleGroups[idx], true);
                        }
                        $scope.toggleView();

                    });
            };

            //To get the information about users,rights and groups to add
            function assignRoleToGroupRight() {
                var result = {
                    rolesUsersList: [],
                    rolesGroupsList: [],
                    rolesRightsList: []
                }

                for (var idx = 0; idx < $scope.userChipsOptions.data.length; idx++) {
                    var obj = {
                        secUser: $scope.userChipsOptions.data[idx],
                        secRole: null
                    }
                    result.rolesUsersList.push(obj);
                }
                var selectedGroups = $scope.groupShuttleBoxOptions.getSelectedData();
                for (var idx = 0; idx < selectedGroups.length; idx++) {
                    var obj = {
                        secGroup: selectedGroups[idx],
                        secRole: null
                    }
                    result.rolesGroupsList.push(obj);
                }

                for (var idx = 0; idx < $scope.selectedRights.length; idx++) {
                    var obj = {
                        secRight: $scope.selectedRights[idx].data,
                        secRole: null
                    }
                    result.rolesRightsList.push(obj);
                }
                return result;
            }

            function createSecRole() {

                var groupsUsers = assignRoleToGroupRight();

                var data = {
                    master: $scope.newSecRole,
                    relationTableOne: groupsUsers.rolesUsersList,
                    relationTableTwo: groupsUsers.rolesGroupsList,
                    relationTableThree: groupsUsers.rolesRightsList
                }

                rolesManagementService.createSecRole(data)
                    .then(function () {
                        util.createToast(util.systemMessages.success, "success");
                        refreshGrid();
                        refreshUserViewGrid();
                        $scope.toggleView();
                    });


            }

            function updateSecRole() {

                var groupsUsers = assignRoleToGroupRight();

                var data = {
                    master: $scope.newSecRole,
                    relationTableOne: groupsUsers.rolesUsersList,
                    relationTableTwo: groupsUsers.rolesGroupsList,
                    relationTableThree: groupsUsers.rolesRightsList
                }

                rolesManagementService.updateSecRole(data)
                    .then(function () {
                        util.createToast(util.systemMessages.success, "success");
                        refreshGrid();
                        refreshUserViewGrid();
                        $scope.toggleView();
                    });

            }

            $scope.deleteSecRole = function () {
                return rolesManagementService.deleteSecRole($scope.selectedSecRole)
                    .then(function () {
                        util.createToast(util.systemMessages.success, "success");
                        refreshGrid();
                    });
            }

            $scope.duplicateSecRole = function () {
                $scope.editMode();
                $scope.selectedSecRole = null;
                delete $scope.newSecRole.rid;
                delete $scope.newSecRole.version;
                delete $scope.newSecRole.createdBy;
                delete $scope.newSecRole.creationDate;
                delete $scope.newSecRole.updatedBy;
                delete $scope.newSecRole.updateDate;
            };

            $scope.clearForm = function (form) {
                $scope.newSecRole = {};
                $scope.selectedSecRole = null;
                for (var i in util.languages) {
                    var nameLang = $scope.secRoleTransFields.name[i];
                    nameLang.value = null;
                }
                form.$setPristine();
                form.$setUntouched();
                clearGroupsRightsShuttles();
                clearUserViewGridSelection();
            };

            $scope.refreshBtn = function () {
                $scope.refreshGridActive = true;
                refreshGrid();
                $timeout(function () {
                    $scope.refreshGridActive = false;
                }, disableBtnDelay);

            };
            function refreshGrid() {
                $scope.roleViewGrid.dataSource.read();
                $scope.roleViewGrid.refresh();
            }

            var dataSource = new kendo.data.DataSource({
                pageSize: config.gridPageSizes[0],
                page: 1,
                transport: {
                    read: function (e) {
                        rolesManagementService.getSecRoleWithRightsGroupsList().then(function (response) {
                            e.success(response.data);
                        });
                    }
                },
                schema: {
                    parse: function (data) {
                        for (var idx = 0; idx < data.length; idx++) {
                            var obj = data[idx];
                            obj["nameLocale"] = obj.name[util.userLocale];
                            obj["roleRightsLocale"] = null;
                            if (obj.roleRights && obj.roleRights.length > 0) {
                                obj["roleRightsLocale"] = obj.roleRights[0].name[util.userLocale];
                                for (var i = 1; i < obj.roleRights.length; i++) {
                                    obj["roleRightsLocale"] = obj["roleRightsLocale"].concat("," + obj.roleRights[i].name[util.userLocale]);
                                }
                            }

                        }
                        return data;
                    },
                    model: {
                        id: "rid",
                        fields: {
                            "nameLocale": {
                                type: "string"
                            },
                            "roleRightsLocale": {
                                type: "string"
                            }
                        }
                    }
                }
            });

            $scope.roleViewGridOptions = {
                columns:
                    [{
                        field: "nameLocale",
                        title: util.systemMessages.name,
                        width: "10%"
                    }, {
                        field: "roleRightsLocale",
                        title: util.systemMessages.rights
                    }],
                dataSource: dataSource,
                change: function () {
                    $scope.selectedSecRole = $scope.roleViewGrid.dataItem($scope.roleViewGrid.select());
                }
            };

            function clearUserViewGridSelection() {
                var userViewGrid = $("#userViewGrid").data("kendoGrid");
                userViewGrid._selectedIds = {};
                userViewGrid.clearSelection();
                $scope.userChipsOptions.data = [];
            }

            function refreshUserViewGrid() {
                clearUserViewGridSelection();
                var grid = $("#userViewGrid").data("kendoGrid");
                grid.dataSource.read();
                grid.refresh();
            }

            var dataSourceSecUsers = new kendo.data.DataSource({
                pageSize: config.gridPageSizes[0],
                page: 1,
                serverPaging: true,
                serverFiltering: true,
                transport: {
                    read: function (e) {
                        e.data = util.createFilterablePageRequest($scope.userViewGridOptions.dataSource, undefined, "And");
                        usersManagementService.getSecUserPageWithRolesGroups(e.data).then(function (response) {
                            e.success(response.data);
                        });
                    }
                },
                schema: {
                    data: "content",
                    total: "totalElements",
                    model: {
                        id: "rid",
                        fields: {
                            username: {
                                type: "string"
                            },
                            userRoles: {
                                type: "object"
                            }
                        }
                    }
                }
            });
            var runOnChange = {
                value: true
            };

            $scope.userViewGridOptions = {
                columns: [{
                    selectable: true,
                    width: "10px"
                }, {
                    field: "username",
                    title: util.systemMessages.username,
                    width: "10%",
                    template: function (item) {
                        return item.username;
                    }
                }, {
                    field: "userRoles",
                    title: util.systemMessages.roles,
                    width: "10%",
                    filterable: false,
                    template: function (item) {
                        var array = [];
                        for (var idx = 0; idx < item.userRoles.length; idx++) {
                            array.push(item.userRoles[idx].name[util.userLocale]);
                        }
                        return array.join();
                    }
                }],
                selectable: false,
                dataSource: dataSourceSecUsers,
                dataBound: function (e) {
                    util.gridSelectionDataBound(e.sender, $scope.userChipsOptions.data, runOnChange, undefined);
                },
                change: function onChange(e) {
                    util.gridSelectionChange(e.sender, $scope.userChipsOptions.data, runOnChange);
                }
            };
        }
    ]);
});

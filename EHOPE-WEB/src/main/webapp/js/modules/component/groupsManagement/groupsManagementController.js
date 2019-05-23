define(['app', 'config', 'util'], function (app, config, util) {
    'use strict';
    app.controller('groupsManagementCtrl', [
        '$scope', 'groupsManagementService', 'usersManagementService', 'rolesManagementService',
        'commonMethods',
        function ($scope, groupsManagementService, usersManagementService, rolesManagementService,
            commonMethods
        ) {
            $scope.editSecGroupTemplate = config.lisDir + "/modules/component/groupsManagement/groups-management-edit.html";
            $scope.mainTemplate = true;
            $scope.selectedSecGroup = null;
            $scope.secGroupMetaData = {};
            $scope.newSecGroup = {};
            $scope.removedChips = [];
            $scope.userLocale = util.userLocale;
            $scope.removeChip = function (chip) {
                var grid = $("#userViewGrid").data("kendoGrid");
                util.removeGridChip(chip, grid);
            };

            $scope.userChipsOptions = {
                data: [],
                label: "username",
                onRemove: $scope.removeChip
            };

            $scope.roleShuttleBoxOptions = {
                getData: rolesManagementService.getSecRoleList,
                dataField: "name." + util.userLocale,
                shuttleLabel: util.systemMessages.roles
            };

            commonMethods.retrieveMetaData("SecGroup").then(function (response) {
                $scope.secGroupMetaData = response.data;
                $scope.secGroupTransFields = {
                    name: util.getTransFieldLanguages("name", "name", null, $scope.secGroupMetaData.name.notNull)
                };
            });

            $scope.toggleView = function () {
                $scope.mainTemplate = !$scope.mainTemplate;
            };

            $scope.clearForm = function (form) {
                $scope.selectedSecGroup = null;
                $scope.newSecGroup = {};
                for (var i in util.languages) {
                    var nameLang = $scope.secGroupTransFields.name[i];
                    nameLang.value = null;
                }
                $scope.roleShuttleBoxOptions.clearSelection();
                form.$setPristine();
                form.$setUntouched();
                clearUserViewGridSelection();
            };

            $scope.saveSecGroup = function (isValid) {
                if (isValid) {
                    if ($scope.selectedSecGroup == null) {// to know if we are duplicating 
                        submitSecGroup();
                    } else {
                        updateSecGroup();
                    }
                }
            };

            //To get the information about users and roles to add
            function assignGroupToUserRoles() {
                var result = {
                    groupUsersList: [],
                    groupRolesList: []
                }

                for (var idx = 0; idx < $scope.userChipsOptions.data.length; idx++) {
                    var obj = {
                        secUser: $scope.userChipsOptions.data[idx],
                        secGroup: null
                    }
                    result.groupUsersList.push(obj);
                }

                var selectedRoles = $scope.roleShuttleBoxOptions.getSelectedData();
                for (var idx = 0; idx < selectedRoles.length; idx++) {
                    var obj = {
                        secRole: selectedRoles[idx],
                        secGroup: null
                    }
                    result.groupRolesList.push(obj);
                }

                return result;
            }

            function submitSecGroup() {
                var usersRoles = assignGroupToUserRoles();

                var data = {
                    master: $scope.newSecGroup,
                    relationTableOne: usersRoles.groupRolesList,
                    relationTableTwo: usersRoles.groupUsersList
                }

                groupsManagementService.createSecGroup(data)
                    .then(function () {
                        util.createToast(util.systemMessages.success, "success");
                        $scope.refreshGrid();
                        refreshUserViewGrid();
                        $scope.toggleView();
                    });
            }

            function updateSecGroup() {
                var usersRoles = assignGroupToUserRoles();

                var data = {
                    master: $scope.newSecGroup,
                    relationTableOne: usersRoles.groupRolesList,
                    relationTableTwo: usersRoles.groupUsersList
                }

                groupsManagementService.updateSecGroup(data)
                    .then(function () {
                        util.createToast(util.systemMessages.success, "success");
                        $scope.refreshGrid();
                        refreshUserViewGrid();
                        $scope.toggleView();
                    });
            }

            $scope.createMode = function () {
                $scope.selectedSecGroup = null;
                $scope.newSecGroup = {};

                for (var i in util.languages) {
                    var nameLang = $scope.secGroupTransFields.name[i];
                    nameLang.value = null;
                }
                $scope.roleShuttleBoxOptions.clearSelection();
                clearUserViewGridSelection();
                refreshUserViewGrid();
                $scope.toggleView();
            };

            $scope.editMode = function () {
                $scope.roleShuttleBoxOptions.clearSelection();
                clearUserViewGridSelection();
                $scope.newSecGroup = angular.copy($scope.selectedSecGroup);
                groupsManagementService.getSecUsersListByGroup($scope.newSecGroup)
                    .then(function (response) {
                        refreshUserViewGrid();
                        $scope.userChipsOptions.data = response.data;
                        for (var i = 0; i < $scope.newSecGroup.groupRoles.length; i++) {
                            $scope.roleShuttleBoxOptions.moveData($scope.newSecGroup.groupRoles[i], true);
                        }
                        $scope.toggleView();
                    });
            };

            $scope.duplicateSecGroup = function () {
                $scope.editMode();
                $scope.selectedSecGroup = null;
                delete $scope.newSecGroup.rid;
                delete $scope.newSecGroup.version;
                delete $scope.newSecGroup.createdBy;
                delete $scope.newSecGroup.creationDate;
                delete $scope.newSecGroup.updatedBy;
                delete $scope.newSecGroup.updateDate;
            };

            $scope.deleteSecGroup = function () {
                return groupsManagementService.deleteSecGroup($scope.selectedSecGroup).then(function () {
                    util.createToast(util.systemMessages.success, "success");
                    $scope.refreshGrid();
                });
            };

            $scope.refreshGrid = function () {
                $scope.groupViewGrid.dataSource.read();
                $scope.groupViewGrid.refresh();
            }

            var dataSource = new kendo.data.DataSource({
                pageSize: config.gridPageSizes[0],
                page: 1,
                transport: {
                    read: function (e) {
                        groupsManagementService.getSecGroupWithRolesList().then(function (response) {
                            e.success(response.data);
                        });
                    }
                },
                schema: {
                    parse: function (data) {
                        for (var idx = 0; idx < data.length; idx++) {
                            var obj = data[idx];
                            obj["nameLocale"] = obj.name[util.userLocale];
                            obj["groupRolesLocale"] = null;
                            if (obj.groupRoles && obj.groupRoles.length > 0) {
                                obj["groupRolesLocale"] = obj.groupRoles[0].name[util.userLocale];
                                for (var i = 1; i < obj.groupRoles.length; i++) {
                                    obj["groupRolesLocale"] = obj["groupRolesLocale"].concat(", " + obj.groupRoles[i].name[util.userLocale]);
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
                            "groupRolesLocale": {
                                type: "string"
                            }
                        }
                    }
                }
            });

            $scope.groupViewGridOptions = {
                columns: [{
                    field: "nameLocale",
                    title: util.systemMessages.name,
                    width: "10%"
                }, {
                    field: "groupRolesLocale",
                    title: util.systemMessages.roles
                }],
                dataSource: dataSource,
                change: function () {
                    $scope.selectedSecGroup = $scope.groupViewGrid.dataItem($scope.groupViewGrid.select());
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
                        var map = {
                            "userGroups": "secGroupUsers.secGroup.name"
                        }
                        e.data = util.createFilterablePageRequest($scope.userViewGridOptions.dataSource, map, "And");
                        usersManagementService.getSecUserPageWithGroups(e.data).then(function (response) {
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
                            "username": {
                                type: "string"
                            },
                            "userGroups": {
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
                    width: "10px",
                }, {
                    field: "username",
                    title: util.systemMessages.username,
                    width: "10%",
                    template: function (item) {
                        return item.username;
                    }
                }, {
                    field: "userGroups",
                    title: util.systemMessages.groups,
                    width: "10%",
                    filterable: false,
                    sortable: false,
                    template: function (item) {
                        var array = [];
                        for (var idx = 0; idx < item.userGroups.length; idx++) {
                            array.push(item.userGroups[idx].name[util.userLocale]);
                        }
                        return array.join();
                    }
                }],
                selectable: false,
                persistSelection: false,
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

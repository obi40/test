define(['app', 'config', 'util'], function (app, config, util) {
    'use strict';
    app.controller('usersManagementCtrl', [
        '$scope',
        'usersManagementService',
        'groupsManagementService',
        'rolesManagementService',
        'branchFormService',
        'commonMethods',
        'lovService',
        '$filter',
        function (
            $scope,
            usersManagementService,
            groupsManagementService,
            rolesManagementService,
            branchFormService,
            commonMethods,
            lovService,
            $filter
        ) {
            $scope.mainTemplate = true;
            $scope.editSecUserTemplate = config.lisDir + "/modules/component/usersManagement/users-management-edit.html";
            $scope.newSecUser = {};
            $scope.selectedSecUser = null;
            $scope.secUserMetaData = {};
            $scope.secUserLKPs = [];
            $scope.secUserTransFields = {};
            $scope.comTenantLanguages = [];
            var allGenders = [];
            var allUserStatuses = [];
            var primaryLanguage = {};
            var autocompleteFilters = [];
            for (var idx in util.user.tenantLanguages) {
                $scope.comTenantLanguages.push(util.user.tenantLanguages[idx].comLanguage);
                if (util.user.tenantLanguages[idx].isPrimary) {
                    primaryLanguage = util.user.tenantLanguages[idx].comLanguage;
                }
            }

            $scope.groupShuttleBoxOptions = {
                getData: groupsManagementService.getSecGroupList,
                dataField: "name." + util.userLocale,
                shuttleLabel: util.systemMessages.groups
            };
            $scope.roleShuttleBoxOptions = {
                getData: rolesManagementService.getSecRoleList,
                dataField: "name." + util.userLocale,
                shuttleLabel: util.systemMessages.roles
            };
            function autocompleteCallback(filters) {
                autocompleteFilters = filters;
                $scope.refreshGrid();
            }
            $scope.userSearchOptions = {
                service: usersManagementService.getSecUserPage,
                callback: autocompleteCallback,
                skeleton: { code: "username", description: "fullName." + util.userLocale },
                filterList: ["firstName", "secondName", "thirdName", "lastName", "username", "email", "address", "mobileNo", "nationalId"]
            };

            commonMethods.retrieveMetaData("SecUser").then(function (response) {
                $scope.secUserMetaData = response.data;
                $scope.secUserLKPs = [
                    {
                        className: "ComLanguage",
                        name: $scope.secUserMetaData.comLanguage.name,
                        labelText: "language",
                        valueField: "name",
                        selectedValue: null,
                        required: $scope.secUserMetaData.comLanguage.notNull,
                        data: $scope.comTenantLanguages
                    }];
                lovService.getLkpByClass({ className: "LkpUserStatus" }).then(function (data) {
                    allUserStatuses = angular.copy(data);
                    $scope.secUserLKPs.push({
                        className: "LkpUserStatus",
                        name: $scope.secUserMetaData.lkpUserStatus.name,
                        labelText: "userStatus",
                        valueField: "name." + util.userLocale,
                        selectedValue: null,
                        required: $scope.secUserMetaData.lkpUserStatus.notNull,
                        data: data
                    });
                });
                lovService.getLkpByClass({ className: "LkpGender" }).then(function (data) {
                    allGenders = angular.copy(data);
                    $scope.secUserLKPs.push({
                        className: "LkpGender",
                        name: $scope.secUserMetaData.lkpGender.name,
                        labelText: "sex",
                        valueField: "name." + util.userLocale,
                        selectedValue: null,
                        required: $scope.secUserMetaData.lkpGender.notNull,
                        data: data
                    });
                });
                var filters = [
                    {
                        "field": "isActive",
                        "value": true,
                        "operator": "eq"
                    }
                ];
                branchFormService.getLabBranchList({ filters: filters }).then(function (response) {
                    $scope.secUserLKPs.push({
                        className: "LabBranch",
                        name: $scope.secUserMetaData.branchId.name,
                        labelText: "branch",
                        valueField: "name." + util.userLocale,
                        selectedValue: null,
                        required: $scope.secUserMetaData.branchId.notNull,
                        data: response.data
                    });
                });

                $scope.secUserTransFields = {
                    address: util.getTransFieldLanguages("address", "address", null, $scope.secUserMetaData.address.notNull)
                };

            });

            $scope.toggleView = function () {
                $scope.mainTemplate = !$scope.mainTemplate;
            };

            $scope.createMode = function () {
                $scope.selectedSecUser = null;
                $scope.newSecUser = {
                    isActive: true,
                    comLanguage: primaryLanguage
                };
                clearLkpsTransField();
                $scope.groupShuttleBoxOptions.clearSelection();
                $scope.roleShuttleBoxOptions.clearSelection();
                $scope.toggleView();
            };

            $scope.editMode = function () {
                $scope.groupShuttleBoxOptions.clearSelection();
                $scope.roleShuttleBoxOptions.clearSelection();
                $scope.secUserLKPs[0].setValues($scope.selectedSecUser, $scope.secUserLKPs, { "LabBranch": "rid" });
                $scope.newSecUser = angular.copy($scope.selectedSecUser);
                getSecUserGroupsRoles($scope.newSecUser);
                $scope.toggleView();
            };

            //To know what groups or roles this user has
            function getSecUserGroupsRoles(secUser) {
                usersManagementService.getSecUserGroups(secUser).then(function (response) {
                    for (var idx = 0; idx < response.data.length; idx++) {
                        $scope.groupShuttleBoxOptions.moveData(response.data[idx].secGroup, true);
                    }
                });
                usersManagementService.getSecUserRoles(secUser).then(function (response) {
                    for (var idx = 0; idx < response.data.length; idx++) {
                        $scope.roleShuttleBoxOptions.moveData(response.data[idx].secRole, true);
                    }
                });
            }

            $scope.submitForm = function (isValid) {
                if (isValid) {
                    if ($scope.selectedSecUser == null) {
                        $scope.createSecUser();
                    } else {
                        $scope.editSecUser();
                    }
                }
            };

            //To get the information about groups and roles to add
            function assignUserToGroupsRoles() {
                var result = {
                    groupUsersList: [],
                    roleUsersList: []
                }
                var selectedGroups = $scope.groupShuttleBoxOptions.getSelectedData();
                for (var idx = 0; idx < selectedGroups.length; idx++) {
                    var obj = {
                        secGroup: selectedGroups[idx],
                        secUser: null
                    }
                    result.groupUsersList.push(obj);
                }
                var selectedRoles = $scope.roleShuttleBoxOptions.getSelectedData();
                for (var idx = 0; idx < selectedRoles.length; idx++) {
                    var obj = {
                        secUser: null,
                        secRole: selectedRoles[idx]
                    }
                    result.roleUsersList.push(obj);
                }

                return result;
            }

            $scope.createSecUser = function () {

                $scope.newSecUser = assignLkpsTransField($scope.newSecUser);

                var groupsRoles = assignUserToGroupsRoles();

                var data = {
                    master: $scope.newSecUser,
                    relationTableOne: groupsRoles.groupUsersList,
                    relationTableTwo: groupsRoles.roleUsersList
                }

                usersManagementService.createSecUser(data).then(function () {
                    util.createToast(util.systemMessages.success, "success");
                    $scope.refreshGrid();
                    $scope.toggleView();
                });
            };

            $scope.editSecUser = function () {
                $scope.newSecUser = assignLkpsTransField($scope.newSecUser);
                var groupsRoles = assignUserToGroupsRoles();
                var data = {
                    master: $scope.newSecUser,
                    relationTableOne: groupsRoles.groupUsersList,
                    relationTableTwo: groupsRoles.roleUsersList
                };

                usersManagementService.updateSecUser(data).then(function () {
                    util.createToast(util.systemMessages.success, "success");
                    $scope.refreshGrid();
                    $scope.selectedSecUser = null;
                    $scope.toggleView();
                });
            };

            $scope.deactivateSecUser = function () {
                return usersManagementService.deactivateSecUser($scope.selectedSecUser).then(function () {
                    $scope.selectedSecUser = null;
                    util.createToast(util.systemMessages.success, "success");
                    $scope.refreshGrid();
                });

            };

            $scope.activateSecUser = function () {
                return usersManagementService.activateSecUser($scope.selectedSecUser).then(function () {
                    $scope.selectedSecUser = null;
                    util.createToast(util.systemMessages.success, "success");
                    $scope.refreshGrid();
                });

            };
            $scope.resetSecUserPassword = function () {

                return usersManagementService.resetSecUserPassword($scope.selectedSecUser).then(function () {
                    util.createToast(util.systemMessages.success, "success");
                    $scope.refreshGrid();
                });

            };
            $scope.duplicateSecUser = function () {
                $scope.editMode();
                $scope.selectedSecUser = null;

                //we dont need these in the duplicated user
                delete $scope.newSecUser.rid;
                delete $scope.newSecUser.email;
                delete $scope.newSecUser.username;
                delete $scope.newSecUser.lastLoginTime;
                delete $scope.newSecUser.version;
                delete $scope.newSecUser.createdBy;
                delete $scope.newSecUser.creationDate;
                delete $scope.newSecUser.updatedBy;
                delete $scope.newSecUser.updateDate;
            };



            $scope.clearForm = function (form) {
                $scope.newSecUser = {
                    isActive: true,
                    comLanguage: primaryLanguage
                };
                $scope.selectedSecUser = null;
                clearLkpsTransField();
                $scope.groupShuttleBoxOptions.clearSelection();
                $scope.roleShuttleBoxOptions.clearSelection();
                form.$setPristine();
                form.$setUntouched();
            };

            function clearLkpsTransField() {

                $scope.secUserLKPs[0].clearLkps($scope.secUserLKPs);

                for (var i in util.languages) {
                    var addressLang = $scope.secUserTransFields.address[i];
                    addressLang.value = null;
                }
            }

            function assignLkpsTransField(object) {
                $scope.secUserLKPs[0].assignValues(object, $scope.secUserLKPs, { "LabBranch": "rid" });
                return object;
            }

            $scope.refreshGrid = function () {
                $scope.userViewGrid.dataSource.read();
                $scope.selectedSecUser = null;
            }

            $scope.transFieldFormatting = function (value) {
                return value != null && value.hasOwnProperty(util.userLocale) ? value[util.userLocale] : "";
            };

            var dataSource = new kendo.data.DataSource({
                pageSize: config.gridPageSizes[0],
                page: 1,
                serverPaging: true,
                serverFiltering: true,
                transport: {
                    read: function (e) {

                        if (autocompleteFilters != null && autocompleteFilters.length > 0) {
                            e.data = {};
                            e.data["filters"] = autocompleteFilters;
                            e.data["page"] = $scope.userViewGrid.dataSource.page() - 1;
                            e.data["size"] = $scope.userViewGrid.dataSource.pageSize();
                        } else {
                            var filterMap = {
                                "lkpUserStatus": "lkpUserStatus.rid",
                                "comLanguage": "comLanguage.name",
                                "lkpGender": "lkpGender.rid"
                            }
                            e.data = util.createFilterablePageRequest($scope.userViewGridOptions.dataSource, filterMap);
                        }

                        usersManagementService.getSecUserPage(e.data).then(function (response) {
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
                            firstName: {
                                type: "trans"
                            },
                            username: {
                                type: "string"
                            },
                            lkpUserStatus: {
                                type: "lov"
                            },
                            isActive: {
                                type: "boolean"
                            },
                            address: {
                                type: "trans"
                            },
                            comLanguage: {
                                type: "object"
                            },
                            email: {
                                type: "string"
                            },
                            secondName: {
                                type: "trans"
                            },
                            thirdName: {
                                type: "trans"
                            },
                            lastName: {
                                type: "trans"
                            },
                            lkpGender: {
                                type: "lov"
                            },
                            lastLoginTime: {
                                type: "date"
                            },
                            mobileNo: {
                                type: "string"
                            },
                            nationalId: {
                                type: "number"
                            }

                        }
                    }
                }
            });

            $scope.userViewGridOptions = {
                columns: [
                    {
                        field: "username",
                        title: util.systemMessages.username,
                        template: function (item) {
                            return item.username;
                        }
                    },
                    {
                        field: "firstName",
                        title: util.systemMessages.firstName,
                        template: function (item) {
                            return item.firstName != null && item.firstName.hasOwnProperty(util.userLocale) ? item.firstName[util.userLocale] : "";
                        }
                    },
                    {
                        field: "email",
                        title: util.systemMessages.email
                    },
                    {
                        field: "isActive",
                        title: util.systemMessages.active,
                        template: function (dataItem) {
                            return dataItem.isActive ? util.systemMessages.yes : util.systemMessages.no;
                        }
                    },
                    {
                        field: "lkpUserStatus",
                        title: util.systemMessages.userStatus,
                        hidden: true,
                        template: function (item) {
                            return item.lkpUserStatus != null && item.lkpUserStatus.name != null &&
                                item.lkpUserStatus.name.hasOwnProperty(util.userLocale) ? item.lkpUserStatus.name[util.userLocale] : "";
                        },
                        filterable: {
                            ui: function (element) {
                                util.createListFilter(element, allUserStatuses, "name." + util.userLocale);
                            }
                        }
                    },
                    {
                        field: "address",
                        title: util.systemMessages.address,
                        template: function (item) {
                            return item.address != null && item.address.hasOwnProperty(util.userLocale) ? item.address[util.userLocale] : "";
                        }
                    },
                    {
                        field: "comLanguage",
                        title: util.systemMessages.language,
                        template: function (item) {
                            return item.comLanguage != null ? item.comLanguage.name : "";
                        }
                    },
                    {
                        field: "secondName",
                        title: util.systemMessages.secondName,
                        hidden: true,
                        template: function (item) {
                            return item.secondName != null && item.secondName.hasOwnProperty(util.userLocale) ? item.secondName[util.userLocale] : "";
                        }
                    },
                    {
                        field: "thirdName",
                        title: util.systemMessages.thirdName,
                        hidden: true,
                        template: function (item) {
                            return item.thirdName != null && item.thirdName.hasOwnProperty(util.userLocale) ? item.thirdName[util.userLocale] : "";
                        }
                    },
                    {
                        field: "lastName",
                        title: util.systemMessages.lastName,
                        hidden: true,
                        template: function (item) {
                            return item.lastName != null && item.lastName.hasOwnProperty(util.userLocale) ? item.lastName[util.userLocale] : "";
                        }
                    },
                    {
                        field: "lkpGender",
                        title: util.systemMessages.sex,
                        hidden: true,
                        template: function (item) {
                            return item.lkpGender != null && item.lkpGender.name != null &&
                                item.lkpGender.name.hasOwnProperty(util.userLocale) ? item.lkpGender.name[util.userLocale] : "";
                        },
                        filterable: {
                            ui: function (element) {
                                util.createListFilter(element, allGenders, "name." + util.userLocale);
                            }
                        }
                    },
                    {
                        field: "lastLoginTime",
                        title: util.systemMessages.lastLoginTime,
                        hidden: true,
                        template: function (item) {
                            return item.lastLoginTime != null ? $filter("dateTimeFormat")(item.lastLoginTime) : "";
                        }
                    },
                    {
                        field: "mobileNo",
                        title: util.systemMessages.mobileNumber,
                        hidden: true
                    },
                    {
                        field: "nationalId",
                        title: util.systemMessages.nationalNumber,
                        hidden: true
                    }],
                dataSource: dataSource,
                columnMenuInit: function (e) {
                    // hook a clear event listener on the clear buttons for each filter
                    e.container.on("click", "[type='reset']", function () {
                        var filtersList = $scope.userViewGrid.dataSource.filter();
                        if (filtersList != null) {
                            for (var i in filtersList.filters) {
                                var fieldName = e.field;
                                if (filtersList.filters[i].field == fieldName) {
                                    $scope.userViewGrid.dataSource.filter().filters.splice(i, 1);
                                    break;
                                }
                            }

                        }
                    });
                },
                change: function () {
                    $scope.selectedSecUser = $scope.userViewGrid.dataItem($scope.userViewGrid.select());
                }
            };
        }
    ]);
});
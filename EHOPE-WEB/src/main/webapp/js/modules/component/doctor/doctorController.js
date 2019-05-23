define(['app', 'config', 'util', 'commonData'], function (app, config, util, commonData) {
    'use strict';
    app.controller('doctorCtrl', [
        '$scope', 'doctorService',
        function ($scope, doctorService) {
            $scope.selectedDoctor = null;

            var doctorDataSource = new kendo.data.DataSource({
                pageSize: config.gridPageSizes[0],
                page: 1,
                transport: {
                    read: function (e) {
                        e.data = util.createFilterablePageRequest(doctorDataSource);
                        doctorService.getDoctorPage(e.data)
                            .then(function (response) {
                                e.success(response.data);
                            }).catch(function (error) {
                                e.error(error);
                            });
                    },
                    create: function (e) {
                        doctorService.addDoctor($scope.selectedDoctor)
                            .then(function (response) {
                                util.createToast(util.systemMessages.success, "success");
                                e.success({ content: response.data });
                            }).catch(function (error) {
                                e.error(error);
                            });
                    },
                    update: function (e) {
                        doctorService.editDoctor($scope.selectedDoctor)
                            .then(function (response) {
                                util.createToast(util.systemMessages.success, "success");
                                e.success({ content: response.data });
                            }).catch(function (error) {
                                e.error(error);
                            });
                    },
                    destroy: function (e) {
                        doctorService.deleteDoctor(e.data.rid)
                            .then(function (response) {
                                util.createToast(util.systemMessages.success, "success");
                                e.success(response.data);
                            }).catch(function (error) {
                                e.error(error);
                                $("#doctorGrid").data("kendoGrid").cancelChanges();
                            });
                    }
                },
                sync: function () {
                    $scope.doctorChanged = false;
                },
                serverPaging: true,
                serverFiltering: true,
                sort: { field: "rid", dir: "desc" },
                schema: {
                    total: "totalElements",
                    data: "content",
                    model: {
                        id: "rid",
                        fields: {
                            name: { type: "trans" },
                            description: { type: "trans" },
                            email: { type: "string" },
                            mobileNo: { type: "string" },
                            phoneNo: { type: "string" }
                        }
                    }
                }
            });

            $scope.doctorGridOptions = {
                dataSource: doctorDataSource,
                editable: "inline",
                columns: [
                    {
                        field: "name",
                        title: "{{ 'name' | translate}}",
                        template: function (dataItem) {
                            if (dataItem.name && dataItem.name[util.userLocale]) {
                                return dataItem.name[util.userLocale];
                            }
                            return "";
                        },
                        editor: function (container, options) {
                            util.createTransFieldEditor(container, options, "name");
                        }
                    },
                    {
                        field: "description",
                        title: "{{ 'description' | translate }}",
                        template: function (dataItem) {
                            if (dataItem.description && dataItem.description[util.userLocale]) {
                                return dataItem.description[util.userLocale];
                            }
                            return "";
                        },
                        editor: function (container, options) {
                            util.createTransFieldEditor(container, options, "description");
                        }
                    },
                    {
                        field: "email",
                        title: "{{ 'email' | translate}}"
                    },
                    {
                        field: "mobileNo",
                        title: "{{ 'mobileNumber' | translate}}",
                        editor: function (container, options) {
                            $('<input name="' + options.field + '"/>')
                                .appendTo(container)
                                .kendoMaskedTextBox({ mask: config.kendoMobilePattern });
                        },
                    },
                    {
                        field: "phoneNo",
                        title: "{{ 'phone' | translate}}"
                    }
                ],
                dataBinding: function () {
                    $scope.selectedDoctor = null;
                },
                edit: function (e) {
                    e.sender.select($("tr[data-uid=" + e.model.uid + "]"));
                    $scope.doctorChanged = true;
                },
                change: function (e) {
                    var selectedRows = e.sender.select();
                    if (selectedRows.length > 0) {
                        $scope.selectedDoctor = this.dataItem(selectedRows[0]);
                    } else {
                        $scope.selectedDoctor = null;
                    }
                }
            };

            $scope.addDoctor = function () {
                var grid = $("#doctorGrid").data("kendoGrid");
                grid.addRow();
            };

            $scope.editDoctor = function (dataItem) {
                if ($scope.doctorChanged) {
                    return;
                }
                var grid = $("#doctorGrid").data("kendoGrid");
                grid.editRow(dataItem);
            };

            $scope.saveChanges = function () {
                var grid = $("#doctorGrid").data("kendoGrid");
                grid.saveChanges();
            };

            $scope.cancelChanges = function () {
                var requestFormGrid = $("#doctorGrid").data("kendoGrid");
                requestFormGrid.cancelChanges();
                $scope.doctorChanged = false;
            };

            $scope.deleteDoctor = function () {
                util.deleteGridRow($scope.selectedDoctor, doctorDataSource);
            };

            $scope.refreshGrid = function () {
                doctorDataSource.read();
            };

        }
    ]);
});

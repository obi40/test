/*
 * Routes configrations Define all routes and there options/dependencies here
 * Dependencies will be loading in the system using requireJS
 */
define(["commonData"],
    function (commonData) {
        "use strict";
        var prefix = "/";
        var componentPath = "modules/component/";
        var directivePath = "modules/shared/directives/";
        return {
            directives: {
                loadOnScroll: {
                    path: directivePath + "loadOnScroll"
                },
                confirmClick: {
                    path: directivePath + "confirmClick/confirmClick",
                    directives: []
                },
                chipsWrapper: {
                    path: directivePath + "chipsWrapper/chipsWrapper"
                },
                cardLayout: {
                    path: directivePath + "cardLayout/cardLayout",
                    directives: [
                        "circularMenu"
                    ]
                },
                lov: {
                    path: directivePath + "lov/lovDirective",
                    dependencies: [
                        directivePath + "lov/lovService"
                    ]
                },
                name: {
                    path: directivePath + "name/nameDirective",
                    dependencies: [
                        directivePath + "name/nameService"
                    ]
                },
                passwordVerify: {
                    path: directivePath + "passwordVerify"
                },
                shuttleBox: {
                    path: directivePath + "shuttleBox/shuttleBox"
                },
                patientInfo: {
                    path: directivePath + "patientInfo/patientInfo",
                    dependencies: [
                        componentPath + "patientProfile/patientProfileService"
                    ]
                },
                transField: {
                    path: directivePath + "transField/transField"
                },
                countryCity: {
                    path: directivePath + "countryCity/countryCity",
                    directives: [
                        "lov"
                    ]
                },
                languageSwitcher: {
                    path: directivePath + "languageSwitcher/languageSwitcher",
                    dependencies: [
                        componentPath + "systemMessages/systemMessagesService"
                    ]
                },
                breadcrumb: {
                    path: directivePath + "breadcrumb/breadcrumb"
                },
                particles: {
                    path: directivePath + "particles/particles"
                },
                uploadFile: {
                    path: directivePath + "uploadFile/uploadFile"
                },
                artifact: {
                    path: directivePath + "artifact/artifactDirective",
                    dependencies: [
                        directivePath + "artifact/artifactService"
                    ]
                },
                patientForm: {
                    path: directivePath + "patientForm/patientFormDirective",
                    directives: [
                        "name",
                        "transField",
                        "lov",
                        "uploadFile",
                        "artifact"
                    ],
                    dependencies: [
                        directivePath + "patientForm/patientFormService",
                        componentPath + "patientRegistration/patientRegistrationService"
                    ]
                },
                patientInsuranceForm: {
                    path: directivePath + "patientInsuranceForm/patientInsuranceFormDirective",
                    directives: [
                        "lov"
                    ],
                    dependencies: [
                        directivePath + "patientInsuranceForm/patientInsuranceFormService",
                        componentPath + "clientManagement/clientManagementService"
                    ]
                },
                orderForm: {
                    path: directivePath + "orderForm/orderFormDirective",
                    directives: [
                        "lov", "transField"
                    ],
                    dependencies: [
                        directivePath + "orderForm/orderFormService",
                        componentPath + "clientManagement/clientManagementService",
                        directivePath + "patientInsuranceForm/patientInsuranceFormService",
                        componentPath + "doctor/doctorService"
                    ]
                },
                testSelection: {
                    path: directivePath + "testSelection/testSelectionDirective",
                    directives: [
                        "lov", "chipsWrapper", "autocompleteSearch", "paymentForm"
                    ],
                    dependencies: [
                        directivePath + "testSelection/testSelectionService",
                        componentPath + "testDefinitionManagement/testDefinitionManagementService",
                        componentPath + "clientManagement/clientManagementService",
                        componentPath + "requestFormManagement/requestFormManagementService",
                        "modules/component/testGroupManagement/testGroupManagementService",
                        componentPath + "patientProfile/patientProfileService"
                    ]
                },
                testQuestionEntry: {
                    path: directivePath + "testQuestionEntry/testQuestionEntry",
                    directives: [],
                    dependencies: [
                        directivePath + "testQuestionEntry/testQuestionEntryService"
                    ]
                },
                testQuestionSetup: {
                    path: directivePath + "testQuestionSetup/testQuestionSetup",
                    directives: ["lov"],
                    dependencies: [
                        directivePath + "testQuestionSetup/testQuestionSetupService",
                        directivePath + "lov/lovService"
                    ]
                },



                testDisclaimerSetup: {
                    path: directivePath + "testDisclaimerSetup/testDisclaimerSetup",
                    directives: [],
                    dependencies: [
                        directivePath + "testDisclaimerSetup/testDisclaimerSetupService"
                    ]
                },
                paymentForm: {
                    path: directivePath + "paymentForm/paymentFormDirective",
                    directives: ["lov"],
                    dependencies: [
                        directivePath + "paymentForm/paymentFormService",
                        componentPath + "billingManagement/billingManagementService",
                        componentPath + "priceList/priceListService",
                        componentPath + "sampleSeparation/sampleSeparationService",
                        directivePath + "orderForm/orderFormService",
                        componentPath + "patientProfile/patientProfileService"
                    ]
                },
                circularMenu: {
                    path: directivePath + "circularMenu/circularMenuDirective",
                    directives: [
                        "confirmClick"
                    ]
                },
                autocompleteSearch: {
                    path: directivePath + "autocompleteSearch/autocompleteSearch"
                },
                testResultSetup: {
                    path: directivePath + "testResultSetup/testResultSetup",
                    dependencies: [
                        directivePath + "testResultSetup/testResultSetupService",
                        componentPath + "labUnit/labUnitService"
                    ],
                    directives: ["lov", "confirmClick", "chipsWrapper"]
                },
                testResultEntry: {
                    path: directivePath + "testResultEntry/testResultEntry",
                    dependencies: [
                        directivePath + "lov/lovService",
                        componentPath + "antiMicrobial/antiMicrobialService",
                        componentPath + "organism/organismService",
                        componentPath + "patientProfile/patientProfileService",
                        directivePath + "testResultEntry/testResultEntryService",
                        componentPath + "orderManagement/orderManagementService"
                    ],
                    directives: ["lov", "confirmClick"]
                },
                quickTestDefinition: {
                    path: directivePath + "quickTestDefinition/quickTestDefinitionDirective",
                    dependencies: [
                        directivePath + "quickTestDefinition/quickTestDefinitionService",
                        directivePath + "testSelection/testSelectionService"
                    ],
                    directives: [
                        "lov",
                        "testSpecimenSetup",
                        "testInterpretationSetup",
                        "testResultSetup",
                        // "testComponentSetup",
                        "feesSetup",
                        "destinationSetup",
                        "testQuestionSetup",
                        "testDisclaimerSetup"
                    ]
                },
                testSpecimenSetup: {
                    path: directivePath + "testSpecimenSetup/testSpecimenSetupDirective",
                    dependencies: [
                        directivePath + "testSpecimenSetup/testSpecimenSetupService"
                    ],
                    directives: ["lov"]
                },
                testInterpretationSetup: {
                    path: directivePath + "testInterpretationSetup/testInterpretationSetup",
                    dependencies: [
                        componentPath + "labUnit/labUnitService"
                    ],
                    directives: ["lov"]
                },
                testComponentSetup: {
                    path: directivePath + "testComponentSetup/testComponentSetup",
                    dependencies: [
                        componentPath + "testDefinitionManagement/testDefinitionManagementService",
                        directivePath + "lov/lovService"
                    ]
                },
                feesSetup: {
                    path: directivePath + "feesSetup/feesSetupDirective",
                    dependencies: [
                        componentPath + "priceList/priceListService"
                    ]
                },
                destinationSetup: {
                    path: directivePath + "destinationSetup/destinationSetupDirective",
                    dependencies: [
                        directivePath + "lov/lovService",
                        componentPath + "clientManagement/clientManagementService",
                        componentPath + "workbenchManagement/workbenchManagementService"
                    ]
                },
                destinationEntry: {
                    path: directivePath + "destinationEntry/destinationEntry",
                    dependencies: [
                        directivePath + "destinationEntry/destinationEntryService"
                    ]
                },
                tenantForm: {
                    path: directivePath + "tenantForm/tenantForm",
                    dependencies: [
                        directivePath + "tenantForm/tenantFormService",
                        componentPath + "systemMessages/systemMessagesService"
                    ],
                    directives: [
                        "countryCity",
                        "transField",
                        "uploadFile"
                    ]
                },
                branchForm: {
                    path: directivePath + "branchForm/branchForm",
                    dependencies: [],
                    directives: [
                        "lov",
                        "countryCity",
                        "transField"
                    ]
                },
                serialForm: {
                    path: directivePath + "serialForm/serialForm",
                    dependencies: [
                        directivePath + "serialForm/serialFormService",
                        directivePath + "lov/lovService",
                        directivePath + "tenantForm/tenantFormService",
                        directivePath + "branchForm/branchFormService"
                    ],
                    directives: [
                        "lov"
                    ]
                }
            },
            routes: {
                home: {
                    url: prefix,
                    directives: ["particles", "languageSwitcher"],
                    dependencies: [
                        componentPath + "home/homeController",
                        componentPath + "home/homeService"
                    ],
                    views: {
                        main: {
                            templateUrl: "js/" + componentPath + "home/homeView.html",
                            controller: "homeCtrl",
                            data: {
                                pageName: "home"
                            }
                        }
                    }
                },
                "billing-management": {
                    url: prefix + "billing-management",
                    directives: ["lov", "chipsWrapper", "confirmClick", "autocompleteSearch"],
                    dependencies: [
                        componentPath + "billingManagement/billingManagementController",
                        componentPath + "billingManagement/billingManagementService",
                        componentPath + "testDefinitionManagement/testDefinitionManagementService",
                        componentPath + "priceList/priceListService"
                    ],
                    views: {
                        main: {
                            templateUrl: "js/" + componentPath + "billingManagement/billing-management-view.html",
                            controller: "billingManagementCtrl",
                            data: {
                                pageName: "billingManagement"
                            }
                        }
                    }
                },
                "insurance-network": {
                    url: prefix + "insurance-network",
                    directives: ["transField", "confirmClick"],
                    dependencies: [
                        componentPath + "insuranceNetwork/insuranceNetworkController",
                        componentPath + "insuranceNetwork/insuranceNetworkService"
                    ],
                    views: {
                        main: {
                            templateUrl: "js/" + componentPath + "insuranceNetwork/insurance-network-view.html",
                            controller: "insuranceNetworkCtrl",
                            data: {
                                pageName: "insNetwork"
                            }
                        }
                    }
                },
                "client-management": {
                    url: prefix + "client-management",
                    directives: ["lov", "confirmClick", "breadcrumb"],
                    dependencies: [
                        componentPath + "clientManagement/clientManagementController",
                        componentPath + "clientManagement/clientManagementService",
                        componentPath + "insuranceNetwork/insuranceNetworkService",
                        componentPath + "priceList/priceListService",
                        componentPath + "billingManagement/billingManagementService",
                        directivePath + "tenantForm/tenantFormService",
                        directivePath + "branchForm/branchFormService"
                    ],
                    views: {
                        main: {
                            templateUrl: "js/" + componentPath + "clientManagement/client-management-view.html",
                            controller: "clientManagementCtrl",
                            data: {
                                pageName: "clientManagement"
                            }
                        }
                    }
                },
                "list-view": {
                    url: prefix + "list-view",
                    dependencies: [
                        componentPath + "listView/listViewController",
                        componentPath + "listView/listViewService"
                    ],
                    views: {
                        main: {
                            templateUrl: "js/" + componentPath + "listView/list-view-view.html",
                            controller: "listViewCtrl"
                        }
                    }
                },
                "lkp-management": {
                    url: prefix + "lkp-management",
                    directives: ["lov", "transField", "confirmClick"],
                    dependencies: [
                        componentPath + "lkpManagement/lkpManagementController",
                        componentPath + "lkpManagement/lkpManagementService"
                    ],
                    views: {
                        main: {
                            templateUrl: "js/" + componentPath + "lkpManagement/lkp-management-view.html",
                            controller: "lkpManagementCtrl",
                            data: {
                                pageName: "lkpManagement"
                            }
                        }
                    }
                },
                "login": {
                    url: prefix + "login",
                    directives: ["languageSwitcher", "particles"],
                    dependencies: [
                        componentPath + "login/loginController",
                        componentPath + "login/loginService"
                    ],
                    views: {
                        main: {
                            templateUrl: "js/" + componentPath + "login/loginView.html",
                            controller: "loginCtrl",
                            data: {
                                pageName: "login"
                            }
                        }
                    }
                },
                "master-detail": {
                    url: prefix + "master-detail",
                    dependencies: [
                        componentPath + "masterDetail/masterDetailController",
                        componentPath + "masterDetail/masterDetailService"
                    ],
                    views: {
                        main: {
                            templateUrl: "js/" + componentPath + "masterDetail/master-detail-view.html",
                            controller: "masterDetailCtrl"
                        }
                    }
                },
                "password-reset": {
                    url: prefix + "password-reset",
                    directives: [
                        "passwordVerify",
                        "languageSwitcher"
                    ],
                    dependencies: [
                        componentPath + "passwordReset/passwordResetController",
                        componentPath + "passwordReset/passwordResetService"
                    ],
                    views: {
                        main: {
                            templateUrl: "js/" + componentPath + "passwordReset/password-reset-view.html",
                            controller: "passwordResetCtrl",
                            data: {
                                pageName: "passwordReset"
                            }
                        }
                    }
                },
                "patient-lookup": {
                    url: prefix + "patient-lookup",
                    directives: [
                        "cardLayout",
                        "loadOnScroll",
                        "autocompleteSearch",
                        "patientInfo"
                    ],
                    dependencies: [
                        componentPath + "patientLookup/patientLookupController",
                        componentPath + "patientLookup/patientLookupService"
                    ],
                    views: {
                        main: {
                            templateUrl: "js/" + componentPath + "patientLookup/patient-lookup-view.html",
                            controller: "patientLookupCtrl",
                            data: {
                                pageName: "patientLookup"
                            }
                        }
                    }
                },
                "patient-profile": {
                    url: prefix + "patient-profile",
                    directives: [
                        "lov",
                        "patientInfo",
                        "transField",
                        "confirmClick",
                        "patientForm",
                        "patientInsuranceForm",
                        "orderForm",
                        "testResultEntry",
                        "artifact",
                        "autocompleteSearch"
                    ],
                    dependencies: [
                        componentPath + "patientProfile/patientProfileController",
                        componentPath + "patientProfile/patientProfileService",
                        directivePath + "lov/lovService"
                    ],
                    views: {
                        main: {
                            templateUrl: "js/" + componentPath + "patientProfile/patientProfileView.html",
                            controller: "patientProfileCtrl",
                            data: {
                                pageName: "patientProfile"
                            }
                        }
                    }
                },
                "patient-profile-wizard": {
                    url: prefix + "patient-profile-wizard",
                    directives: [
                        "lov",
                        "patientInfo",
                        "patientInsuranceForm",
                        "orderForm",
                        "testSelection",
                        "destinationEntry",
                        "testQuestionEntry",
                        "paymentForm"
                    ],
                    dependencies: [
                        componentPath + "patientProfileWizard/patientProfileWizardController",
                        componentPath + "patientProfileWizard/patientProfileWizardService",
                        directivePath + "paymentForm/paymentFormService",
                        directivePath + "orderForm/orderFormService",
                        directivePath + "patientInsuranceForm/patientInsuranceFormService"
                    ],
                    views: {
                        main: {
                            templateUrl: "js/" + componentPath + "patientProfileWizard/patient-profile-wizard-view.html",
                            controller: "patientProfileWizardCtrl",
                            data: {
                                pageName: "patientProfileWizard"
                            }
                        }
                    }
                },
                "patient-registration": {
                    url: prefix + "patient-registration",
                    directives: [
                        "patientForm"
                    ],
                    dependencies: [
                        componentPath + "patientRegistration/patientRegistrationController",
                        componentPath + "patientRegistration/patientRegistrationService"
                    ],
                    views: {
                        main: {
                            templateUrl: "js/" + componentPath + "patientRegistration/patient-registration-view.html",
                            controller: "patientRegistrationCtrl",
                            data: {
                                pageName: "patientRegistration"
                            }
                        }
                    }
                },
                "price-list": {
                    url: prefix + "price-list",
                    directives: [
                        "lov",
                        "transField",
                        "confirmClick"
                    ],
                    dependencies: [
                        componentPath + "priceList/priceListController",
                        componentPath + "priceList/priceListService"
                    ],
                    views: {
                        main: {
                            templateUrl: "js/" + componentPath + "priceList/price-list-view.html",
                            controller: "priceListCtrl",
                            data: {
                                pageName: "priceList"
                            }
                        }
                    }
                },
                "sample-separation": {
                    url: prefix + "sample-separation",
                    directives: [
                        "patientInfo", "confirmClick"
                    ],
                    dependencies: [
                        componentPath + "sampleSeparation/sampleSeparationController",
                        componentPath + "sampleSeparation/sampleSeparationService",
                        directivePath + "orderForm/orderFormService",
                        componentPath + "separationFactors/separationFactorsService"
                    ],
                    views: {
                        main: {
                            templateUrl: "js/" + componentPath + "sampleSeparation/sample-separation-view.html",
                            controller: "sampleSeparationController",
                            data: {
                                pageName: "sampleSeparation"
                            }
                        }
                    }
                },
                "tenant-management": {
                    url: prefix + "tenant-management",
                    directives: [
                        "lov",
                        "transField",
                        "tenantForm",
                        "confirmClick"
                    ],
                    dependencies: [
                        componentPath + "tenantManagement/tenantManagementController",
                        componentPath + "tenantManagement/tenantManagementService",
                        directivePath + "tenantForm/tenantFormService",
                        directivePath + "lov/lovService",
                        "modules/shared/services/configService"
                    ],
                    views: {
                        main: {
                            templateUrl: "js/" + componentPath + "tenantManagement/tenant-management-view.html",
                            controller: "tenantManagementCtrl",
                            data: {
                                pageName: "tenantManagement"
                            }
                        }
                    }
                },
                "system-messages": {
                    url: prefix + "system-messages",
                    directives: [
                        "lov",
                        "transField",
                        "confirmClick"
                    ],
                    dependencies: [
                        componentPath + "systemMessages/systemMessagesController",
                        componentPath + "systemMessages/systemMessagesService"
                    ],
                    views: {
                        main: {
                            templateUrl: "js/" + componentPath + "systemMessages/system-messages-view.html",
                            controller: "systemMessagesCtrl",
                            data: {
                                pageName: "systemMessages"
                            }
                        }
                    }
                },
                "test-definition-management": {
                    url: prefix + "test-definition-management",
                    directives: [
                        "lov",
                        "confirmClick",
                        "autocompleteSearch",
                        "testResultSetup",
                        "quickTestDefinition"
                    ],
                    dependencies: [
                        componentPath + "testDefinitionManagement/testDefinitionManagementController",
                        componentPath + "testDefinitionManagement/testDefinitionManagementService",
                        componentPath + "labUnit/labUnitService",
                        directivePath + "testSelection/testSelectionService"
                    ],
                    views: {
                        main: {
                            templateUrl: "js/" + componentPath + "testDefinitionManagement/test-definition-management-view.html",
                            controller: "testDefinitionManagementCtrl",
                            data: {
                                pageName: "testDefinitionManagement"
                            }
                        }
                    }
                },
                "test-directory-view": {
                    url: prefix + "test-directory-view",
                    directives: [
                        "testSelection"
                    ],
                    dependencies: [
                        componentPath + "tests/testDirectoryController",
                        componentPath + "tests/testDirectoryService"
                    ],
                    views: {
                        main: {
                            templateUrl: "js/" + componentPath + "tests/test-directory-view.html",
                            controller: "testsCtrl",
                            data: {
                                pageName: "testDirectory"
                            }
                        }
                    }
                },
                "groups-management": {
                    url: prefix + "groups-management",
                    directives: [
                        "lov",
                        "transField",
                        "shuttleBox",
                        "confirmClick",
                        "chipsWrapper"
                    ],
                    dependencies: [
                        componentPath + "groupsManagement/groupsManagementController",
                        componentPath + "groupsManagement/groupsManagementService",
                        componentPath + "usersManagement/usersManagementService",
                        componentPath + "rolesManagement/rolesManagementService"
                    ],
                    views: {
                        main: {
                            templateUrl: "js/" + componentPath + "groupsManagement/groups-management-view.html",
                            controller: "groupsManagementCtrl",
                            data: {
                                pageName: "groupManagement"
                            }
                        }
                    }
                },
                "roles-management": {
                    url: prefix + "roles-management",
                    directives: [
                        "transField",
                        "shuttleBox",
                        "confirmClick",
                        "chipsWrapper"
                    ],
                    dependencies: [
                        componentPath + "rolesManagement/rolesManagementController",
                        componentPath + "rolesManagement/rolesManagementService",
                        componentPath + "usersManagement/usersManagementService",
                        componentPath + "groupsManagement/groupsManagementService"
                    ],
                    views: {
                        main: {
                            templateUrl: "js/" + componentPath + "rolesManagement/roles-management-view.html",
                            controller: "rolesManagementCtrl",
                            data: {
                                pageName: "roleManagement"
                            }
                        }
                    }
                },
                "users-management": {
                    url: prefix + "users-management",
                    directives: [
                        "lov",
                        "shuttleBox",
                        "transField",
                        "confirmClick",
                        "name",
                        "autocompleteSearch"
                    ],
                    dependencies: [
                        componentPath + "usersManagement/usersManagementController",
                        componentPath + "usersManagement/usersManagementService",
                        componentPath + "groupsManagement/groupsManagementService",
                        componentPath + "rolesManagement/rolesManagementService",
                        directivePath + "branchForm/branchFormService"
                    ],
                    views: {
                        main: {
                            templateUrl: "js/" + componentPath + "usersManagement/users-management-view.html",
                            controller: "usersManagementCtrl",
                            data: {
                                pageName: "userManagement"
                            }
                        }
                    }
                },
                "user-profile": {
                    url: prefix + "user-profile",
                    directives: [
                        "lov",
                        "passwordVerify",
                        "transField",
                        "name"
                    ],
                    dependencies: [
                        componentPath + "userProfile/userProfileController",
                        componentPath + "userProfile/userProfileService",
                        componentPath + "systemMessages/systemMessagesService",
                        directivePath + "branchForm/branchFormService"
                    ],
                    views: {
                        main: {
                            templateUrl: "js/" + componentPath + "userProfile/user-profile-view.html",
                            controller: "userProfileCtrl",
                            data: {
                                pageName: "userProfile"
                            }
                        }
                    }
                },
                "separation-factors": {
                    url: prefix + "separation-factors",
                    directives: ["lov", "transField"],
                    dependencies: [componentPath + "separationFactors/separationFactorsController",
                    componentPath + "separationFactors/separationFactorsService"],
                    views: {
                        "main": {
                            templateUrl: "js/" + componentPath + "separationFactors/separation-factors-view.html",
                            controller: "separationFactorsCtrl",
                            data: {
                                pageName: "separationFactors"
                            }
                        }
                    }
                },
                "request-form-management": {
                    "url": prefix + "request-form-management",
                    directives: ["chipsWrapper", "lov", "autocompleteSearch"],
                    "dependencies": [componentPath + "requestFormManagement/requestFormManagementController",
                    componentPath + "requestFormManagement/requestFormManagementService",
                    componentPath + "testDefinitionManagement/testDefinitionManagementService",
                    directivePath + "testSelection/testSelectionService"],
                    "views": {
                        "main": {
                            "templateUrl": "js/" + componentPath + "requestFormManagement/request-form-management-view.html",
                            "controller": "requestFormManagementCtrl",
                            "data": {
                                "pageName": "requestFormManagement"
                            }
                        }
                    }
                },
                "order-management": {
                    "url": prefix + "order-management",
                    "dependencies": [
                        componentPath + "orderManagement/orderManagementController",
                        componentPath + "orderManagement/orderManagementService",
                        directivePath + "lov/lovService",
                        componentPath + "sampleSeparation/sampleSeparationService",
                        componentPath + "patientProfile/patientProfileService",
                        componentPath + "patientLookup/patientLookupService",
                        componentPath + "testDefinitionManagement/testDefinitionManagementService",
                        directivePath + "paymentForm/paymentFormService",
                        directivePath + "testResultEntry/testResultEntryService",
                        directivePath + "orderForm/orderFormService"
                    ],
                    "directives": [
                        "autocompleteSearch", "confirmClick", "lov", "testResultEntry", "artifact"
                    ],
                    "views": {
                        "main": {
                            "templateUrl": "js/" + componentPath + "orderManagement/order-management-view.html",
                            "controller": "orderManagementCtrl",
                            "data": {
                                "pageName": "orderManagement"
                            }
                        }
                    }
                },
                "subscription": {
                    url: prefix + "subscription",
                    dependencies: [
                        componentPath + "subscription/subscriptionController",
                        componentPath + "subscription/subscriptionService",
                        directivePath + "lov/lovService",
                        componentPath + "login/loginService",
                        directivePath + "branchForm/branchFormService",
                        directivePath + "serialForm/serialFormService",
                        componentPath + "systemMessages/systemMessagesService"
                    ],
                    directives: ["lov", "countryCity", "branchForm", "confirmClick", "serialForm", "languageSwitcher", "particles"],
                    views: {
                        main: {
                            templateUrl: "js/" + componentPath + "subscription/subscription-view.html",
                            controller: "subscriptionCtrl",
                            data: {
                                pageName: "subscription"
                            }
                        }
                    }
                },
                "outstanding-balances": {
                    url: prefix + "outstanding-balances",
                    dependencies: [componentPath + "outstandingBalances/outstandingBalancesController",
                    componentPath + "patientProfile/patientProfileService",
                    componentPath + "outstandingBalances/outstandingBalancesService",
                    componentPath + "patientLookup/patientLookupService"],
                    directives: ["lov", "autocompleteSearch", "paymentForm"],
                    views: {
                        main: {
                            templateUrl: "js/" + componentPath + "outstandingBalances/outstanding-balances-view.html",
                            controller: "outstandingBalancesCtrl",
                            data: {
                                pageName: "outstandingBalances"
                            }
                        }
                    }
                },
                "doctors": {
                    url: prefix + "doctors",
                    dependencies: [componentPath + "doctor/doctorController", componentPath + "doctor/doctorService"],
                    directives: ["confirmClick"],
                    views: {
                        main: {
                            templateUrl: "js/" + componentPath + "doctor/doctor-view.html",
                            controller: "doctorCtrl",
                            data: {
                                pageName: "doctors"
                            }
                        }
                    }
                },
                "branches": {
                    url: prefix + "branches",
                    dependencies: [
                        componentPath + "branch/branchController",
                        componentPath + "branch/branchService",
                        directivePath + "branchForm/branchFormService"],
                    directives: ["branchForm", "lov", "countryCity", "transField", "confirmClick"],
                    views: {
                        main: {
                            templateUrl: "js/" + componentPath + "branch/branch-view.html",
                            controller: "branchCtrl",
                            data: {
                                pageName: "branches"
                            }
                        }
                    }
                },
                "serial": {
                    url: prefix + "serial",
                    dependencies: [componentPath + "serial/serialController",
                    componentPath + "serial/serialService", directivePath + "serialForm/serialFormService"],
                    directives: ["serialForm"],
                    views: {
                        main: {
                            templateUrl: "js/" + componentPath + "serial/serial-view.html",
                            controller: "serialCtrl",
                            data: {
                                pageName: "serials"
                            }
                        }
                    }
                },
                "price-list-details": {
                    url: prefix + "price-list-details",
                    dependencies: [componentPath + "priceListDetails/priceListDetailsController",
                    componentPath + "priceListDetails/priceListDetailsService",
                    componentPath + "priceList/priceListService",
                    componentPath + "testDefinitionManagement/testDefinitionManagementService",
                    directivePath + "lov/lovService"],
                    directives: ["lov", "confirmClick", "autocompleteSearch"],
                    views: {
                        main: {
                            templateUrl: "js/" + componentPath + "priceListDetails/price-list-details-view.html",
                            controller: "priceListDetailsCtrl",
                            data: {
                                pageName: "priceListDetails"
                            }
                        }
                    }
                },
                "workbench-management": {
                    url: prefix + "workbench-management",
                    dependencies: [componentPath + "workbenchManagement/workbenchManagementController",
                    componentPath + "workbenchManagement/workbenchManagementService"],
                    directives: ["confirmClick"],
                    views: {
                        main: {
                            templateUrl: "js/" + componentPath + "workbenchManagement/workbench-management-view.html",
                            controller: "workbenchManagementCtrl",
                            data: {
                                pageName: "workbenchManagement"
                            }
                        }
                    }
                },
                "import-data": {
                    url: prefix + "import-data",
                    dependencies: [componentPath + "importData/importDataController", componentPath + "importData/importDataService"],
                    directives: ["uploadFile"],
                    views: {
                        main: {
                            templateUrl: "js/" + componentPath + "importData/import-data-view.html",
                            controller: "importDataCtrl",
                            data: {
                                pageName: "importData"
                            }
                        }
                    }
                },
                "dashboard": {
                    url: prefix + "dashboard",
                    dependencies: [componentPath + "dashboard/dashboardController",
                    componentPath + "dashboard/dashboardService"],
                    directives: [],
                    views: {
                        main: {
                            templateUrl: "js/" + componentPath + "dashboard/dashboard-view.html",
                            controller: "dashboardCtrl",
                            data: {
                                pageName: "dashboard"
                            }
                        }
                    }
                },
                "daily-reports": {
                    url: prefix + "daily-reports",
                    dependencies: [componentPath + "dailyReports/dailyReportsController",
                    componentPath + "dailyReports/dailyReportsService",
                    directivePath + "branchForm/branchFormService",
                    componentPath + "clientManagement/clientManagementService",
                    directivePath + "lov/lovService"],

                    directives: ["lov"],
                    views: {
                        main: {
                            templateUrl: "js/" + componentPath + "dailyReports/daily-reports-view.html",
                            controller: "dailyReportsCtrl",
                            data: {
                                pageName: "dailyReports"
                            }
                        }
                    }
                },
                "lab-units": {
                    url: prefix + "lab-units",
                    dependencies: [componentPath + "labUnit/labUnitController", componentPath + "labUnit/labUnitService"],
                    directives: ['confirmClick'],
                    views: {
                        main: {
                            templateUrl: "js/" + componentPath + "labUnit/lab-unit-view.html",
                            controller: "labUnitCtrl",
                            data: {
                                pageName: "labUnits"
                            }
                        }
                    }
                },
                "sections": {
                    url: prefix + "sections",
                    dependencies: [
                        componentPath + "section/sectionController",
                        componentPath + "section/sectionService",
                        componentPath + "billingManagement/billingManagementService",
                        directivePath + "lov/lovService"
                    ],
                    directives: ['confirmClick'],
                    views: {
                        main: {
                            templateUrl: "js/" + componentPath + "section/section-view.html",
                            controller: "sectionCtrl",
                            data: {
                                pageName: "sections"
                            }
                        }
                    }
                },
                "organisms": {
                    url: prefix + "organisms",
                    dependencies: [
                        componentPath + "organism/organismController",
                        componentPath + "organism/organismService",
                        directivePath + "lov/lovService"
                    ],
                    directives: ['confirmClick', "uploadFile"],
                    views: {
                        main: {
                            templateUrl: "js/" + componentPath + "organism/organism-view.html",
                            controller: "organismCtrl",
                            data: {
                                pageName: "organisms"
                            }
                        }
                    }
                },
                "anti-microbials": {
                    url: prefix + "anti-microbials",
                    dependencies: [
                        componentPath + "antiMicrobial/antiMicrobialController",
                        componentPath + "antiMicrobial/antiMicrobialService",
                        directivePath + "lov/lovService"
                    ],
                    directives: ['confirmClick'],
                    views: {
                        main: {
                            templateUrl: "js/" + componentPath + "antiMicrobial/anti-microbial-view.html",
                            controller: "antiMicrobialCtrl",
                            data: {
                                pageName: "antiMicrobials"
                            }
                        }
                    }
                },
                "sms": {
                    url: prefix + "sms",
                    dependencies: [
                        componentPath + "sms/smsController",
                        componentPath + "sms/smsService"
                    ],
                    directives: [],
                    views: {
                        main: {
                            templateUrl: "js/" + componentPath + "sms/sms-view.html",
                            controller: "smsCtrl",
                            data: {
                                pageName: "sms"
                            }
                        }
                    }
                },
                'packages': {
                    url: prefix + 'packages',
                    dependencies: [
                        'modules/component/testGroupManagement/testGroupManagementController',
                        'modules/component/testGroupManagement/testGroupManagementService',
                        componentPath + "testDefinitionManagement/testDefinitionManagementService",
                        componentPath + "priceList/priceListService"
                    ],
                    directives: ["confirmClick", "chipsWrapper", "autocompleteSearch"],
                    views: {
                        main: {
                            templateUrl: 'js/modules/component/testGroupManagement/test-group-management-view.html',
                            controller: 'testGroupManagementCtrl',
                            data: {
                                pageName: "packagesProfiles"
                            }
                        }
                    }
                },
                'landing-page': {
                    url: prefix + 'landing-page',
                    dependencies: [
                        'modules/component/landingPage/landingPageController',
                        'modules/component/landingPage/landingPageService'
                    ],
                    directives: [],
                    views: {
                        main: {
                            templateUrl: 'js/modules/component/landingPage/landing-page-view.html',
                            controller: 'landingPageCtrl',
                            data: {
                                pageName: ""
                            }
                        }
                    }
                }//Don't add anything under this line, add your routes before it and Don't REMOVE
            },
            otherwise: prefix + commonData.internalHomepage
        };
    })
define("commonData", [], function () {
    'use strict';
    var commonData = {
        tenantMessages: [],//unmodified tenant messages
        na: "N/A",
        arrow: "\u2192",
        defaultTenantRid: 0,
        appAdminRid: 0,
        defaultLocale: "en_us",
        selectedTokenBranch: null,
        numberFraction: '2',
        fileTypes: {
            pdf: "application/pdf",
            excel: "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
        },
        internalHomepage: "landing-page",
        reportNames: {
            dailyCash: "Daily cash payments",
            dailyIncome: "Daily income",
            dailyCredit: "Daily credit",
            claimSummarized: "Claim summarized",
            claimDetailed: "Claim detailed",
            referralOut: "Referral-Out",
            sample: "Sample",
            patientInvoice: "Patient invoice",
            insuranceInvoice: "Insurance invoice",
            worksheet: "Worksheet",
            outstandingBalance: "Outstanding balance",
            results: "Results",
            appointmentCard: "Appointment card"
        },
        events: {
            //patientInsuranceForm
            patientInsuranceFormReady: false,
            activatePatientInsuranceForm: "activatePatientInsuranceForm",
            enterPatientInsuranceForm: "enterPatientInsuranceForm",
            exitPatientInsuranceForm: "exitPatientInsuranceForm",
            //orderForm
            orderFormReady: false,
            activateOrderForm: "activateOrderForm",
            exitOrderForm: "exitOrderForm",
            //patientForm
            patientFormReady: false,
            activatePatientForm: "activatePatientForm",
            enterPatientForm: "enterPatientForm",
            //testSelection
            testSelectionReady: false,
            activateTestSelection: "activateTestSelection",
            exitTestSelection: "exitTestSelection",
            //destinationEntry
            activateDestinationEntry: "activateDestinationEntry",
            exitDestinationEntry: "exitDestinationEntry",
            //testQuestionForm
            activateTestQuestionForm: "activateTestQuestionForm",
            exitTestQuestionForm: "exitTestQuestionForm",
            //paymentForm
            activatePaymentForm: "activatePaymentForm",
            exitPaymentForm: "exitPaymentForm",
            //branchForm
            branchFormReady: false,
            activateBranchForm: "activateBranchForm",
            exitBranchForm: "exitBranchForm"
        },
        operationStatus: {
            REQUESTED: "REQUESTED",
            VALIDATED: "VALIDATED",
            COLLECTED: "COLLECTED",
            IN_PROGRESS: "IN_PROGRESS",
            RESULTS_ENTERED: "RESULTS_ENTERED",
            FINALIZED: "FINALIZED",
            CANCELLED: "CANCELLED",
            CLOSED: "CLOSED",
            ABORTED: "ABORTED"
        },
        navMenuItems: [
            {
                authority: null,
                route: null,
                icon: "fas fa-lg fa-users",
                label: "patientManagement",
                isSubItemToggled: false,
                show: true,
                subItems: [
                    {
                        authority: "VIEW_PATIENT_LOOKUP",
                        route: "patient-lookup",
                        icon: "fas fa-lg fa-search",
                        label: "patientLookup"
                    },
                    {
                        authority: "VIEW_PATIENT_REGISTRATION",
                        route: "patient-registration",
                        icon: "fa-lg lis-patient-registration",
                        label: "patientRegistration"
                    }
                ]
            },
            {
                authority: null,
                route: null,
                icon: "fas fa-lg fa-vials",
                label: "testManagement",
                isSubItemToggled: false,
                show: true,
                subItems: [
                    {
                        authority: "VIEW_TEST_DIRECTORY",
                        route: "test-directory-view",
                        icon: "fas fa-lg fa-vial",
                        label: "testDirectory"
                    },
                    {
                        authority: "VIEW_TEST_DEFINITION",
                        route: "test-definition-management",
                        icon: "fa-lg lis-test-definition-management",
                        label: "testDefinitionManagement"
                    },
                    {
                        authority: "VIEW_TEST_GROUP",
                        route: "packages",
                        icon: "fas fa-lg fa-boxes",
                        label: "packagesProfiles"
                    }
                ]
            },
            {
                authority: "VIEW_ORDER_MANAGEMENT",
                route: "order-management",
                icon: "fas fa-lg fa-newspaper",
                label: "orderManagement",
                show: true
            },
            {
                authority: "VIEW_DASHBOARD",
                route: "dashboard",
                icon: "fas fa-lg fa-chart-bar",
                label: "dashboard",
                show: true
            },
            {
                authority: null,
                route: null,
                icon: "fas fa-lg fa-university",
                label: "accounting",
                isSubItemToggled: false,
                show: true,
                subItems: [
                    {
                        authority: "VIEW_BILL_CLASSIFICATION",
                        route: "billing-management",
                        icon: "fas fa-lg fa-dollar-sign",
                        label: "billingManagement"
                    },
                    {
                        authority: "VIEW_OUTSTANDING_BALANCES",
                        route: "outstanding-balances",
                        icon: "fas fa-lg fa-file-invoice-dollar",
                        label: "outstandingBalances"
                    },
                    {
                        authority: "VIEW_BILL_PRICE_LIST",
                        route: "price-list",
                        icon: "fa-lg lis-price-list",
                        label: "priceList"
                    },
                    {
                        authority: "VIEW_BILL_PRICE_LIST_DETAILS",
                        route: "price-list-details",
                        icon: "fas fa-lg fa-money-check-alt",
                        label: "priceListDetails"
                    },
                    {
                        authority: "VIEW_DAILY_REPORTS",
                        route: "daily-reports",
                        icon: "fas fa-lg fa-receipt",
                        label: "dailyReports"
                    }
                ]
            },
            {
                authority: "VIEW_INSURANCE_PROVIDER",
                route: "client-management",
                icon: "fa-lg lis-insurance-management",
                label: "clientManagement",
                show: true
            },
            {
                authority: null,
                route: null,
                icon: "fas fa-lg fa-shield-alt",
                label: "securityControl",
                isSubItemToggled: false,
                show: true,
                subItems: [
                    {
                        authority: "VIEW_USERS_MANAGEMENT",
                        route: "users-management",
                        icon: "fas fa-lg fa-user",
                        label: "userManagement"
                    },
                    {
                        authority: "VIEW_GROUPS_MANAGEMENT",
                        route: "groups-management",
                        icon: "fas fa-lg fa-users",
                        label: "groupManagement"
                    },
                    {
                        authority: "VIEW_ROLES_MANAGEMENT",
                        route: "roles-management",
                        icon: "fa-lg lis-role",
                        label: "roleManagement"
                    }
                ]
            },
            {
                authority: "VIEW_TENANT_MANAGEMENT",
                route: "tenant-management",
                icon: "fas fa-lg fa-user-tie",
                label: "tenantManagement",
                show: true
            },
            {
                authority: null,
                route: null,
                icon: "fas fa-lg fa-briefcase-medical",
                label: "medicalSettings",
                isSubItemToggled: false,
                show: true,
                subItems: [
                    {
                        authority: "VIEW_LAB_FACTORS",
                        route: "separation-factors",
                        icon: "fa-lg lis-separation-factors",
                        label: "separationFactors"
                    },
                    {
                        authority: "VIEW_REQUEST_FORM",
                        route: "request-form-management",
                        icon: "fas fa-lg fa-clipboard-list",
                        label: "requestFormManagement"
                    },
                    {
                        authority: "VIEW_DOCTOR",
                        route: "doctors",
                        icon: "fas fa-lg fa-user-md",
                        label: "doctors"
                    },
                    {
                        authority: "VIEW_SECTION",
                        route: "sections",
                        icon: "fas fa-archway",
                        label: "sections"
                    },
                    {
                        authority: "VIEW_WORKBENCH",
                        route: "workbench-management",
                        icon: "fas fa-keyboard",
                        label: "workbenches"
                    },
                    {
                        authority: "VIEW_LAB_UNIT",
                        route: "lab-units",
                        icon: "fas fa-vials",
                        label: "labUnits"
                    },
                    {
                        authority: "VIEW_ORGANISM",
                        route: "organisms",
                        icon: "fas fa-paw",
                        label: "organisms"
                    },
                    {
                        authority: "VIEW_ANTI_MICROBIAL",
                        route: "anti-microbials",
                        icon: "fas fa-pills",
                        label: "antiMicrobials"
                    }
                ]
            },
            {
                authority: null,
                route: null,
                icon: "fas fa-lg fa-cog",
                label: "settings",
                isSubItemToggled: false,
                show: true,
                subItems: [
                    {
                        authority: "VIEW_TENANT_MESSAGES",
                        route: "system-messages",
                        icon: "fas fa-lg fa-language",
                        label: "systemMessages"
                    },
                    {
                        authority: "VIEW_LKP_MANAGEMENT",
                        route: "lkp-management",
                        icon: "fa-lg lis-lkp-management-alt",
                        label: "lkpManagement"
                    },
                    {
                        authority: "VIEW_BRANCH",
                        route: "branches",
                        icon: "fas fa-lg fa-code-branch",
                        label: "branches"
                    },
                    {
                        authority: "VIEW_SERIAL",
                        route: "serial",
                        icon: "fas fa-lg fa-sort-numeric-down",
                        label: "serials"
                    },
                    {
                        authority: "VIEW_DATA_IMPORT",
                        route: "import-data",
                        icon: "fas fa-lg fa-exchange-alt",
                        label: "importData"
                    }
                    //,
                    //{<i class="fas fa-vials"></i>
                    //	//authority: "VIEW_LAB_UNIT",
                    //	route: "lab-unit",
                    //	icon: "fas fa-archway",
                    //	label: "labUnit"
                    //}
                ]
            }
        ]
    }
    return commonData;
});

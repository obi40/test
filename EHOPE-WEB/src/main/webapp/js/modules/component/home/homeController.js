define(['app', 'util'], function (app, util) {
    'use strict';
    app.controller('homeCtrl', ['$scope', '$anchorScroll', '$window', function ($scope, $anchorScroll, $window) {
        util.fullWebsiteView($scope);
        $scope.langSwitcherOptions = {};
        $scope.scrollTo = function (id) {
            $anchorScroll(id);
        };
        function isScrolledIntoView(el) {
            var rect = el.getBoundingClientRect();
            var elemTop = rect.top;
            var elemBottom = rect.bottom;
            return elemTop < window.innerHeight && elemBottom >= 0;
        }

        function fade() {
            $('.img-container').each(function () {
                if (isScrolledIntoView(this)) {
                    $(this).animate({ 'opacity': '1.0' }, 1000);
                }
            });
        }
        setTimeout(function () {
            fade();
        });
        angular.element($window).bind('scroll', function () {
            fade();
        });
        $scope.systemPoints = [
            {
                icon: "fas fa-stroopwafel",
                label: "Most Modern LIS",
                info: "Reduce maintenance costs and increase scalability by using the latest, industry standard technologies."
            },
            {
                icon: "fas fa-check-circle",
                label: "Right LIS for You",
                info: "Pick from our prepackaged or pre-validated editions to accelerate implementation and go-live faster."
            },
            {
                icon: "fas fa-cog",
                label: "Range of Services",
                info: "Our implementation, validation and managed services make adoption and maintenance easier."
            },
            {
                icon: "fas fa-plus-square",
                label: "Innovating Faster",
                info: "We are innovating faster, adding features that will maximize the value of your LIS over time."
            },
            {
                icon: "fas fa-check-circle",
                label: "Get Information Quickly",
                info: "It is important that you can access your information at the touch of a button. As AccuLab LIS centralizes your data, either in your own data center or in the cloud, you can easily retrieve the information you need. You can enter and retrieve data through your web browser."
            },
            {
                icon: "fas fa-cog",
                label: "Make Better, More Informed Decisions",
                info: "AccuLab LIS can help make better, more informed decisions thanks to its sophisticated real-time reporting. This helps you analyze data more quickly, allowing laboratory managers to get access to the right information at each stage of the testing process."
            },
            {
                icon: "fas fa-plus-square",
                label: "Grow Profitability",
                info: "Our LIS uses automation to improve data handling and help detect irregularities. What's more, they enhance data integrity, increase your laboratory capacity and improve efficiency all-round, helping make your laboratory more streamlined, more effective, and more profitable."
            }
        ];

        $scope.features = [
            {
                icon: "fas fa-newspaper",
                label: "Sample Management",
                info: "Register and track samples through the laboratory with ease."
            },
            {
                icon: "fas fa-vial",
                label: "Test Directory",
                info: "Mayo Clinic tests catalog is embedded with system and ability to edit any test or upload your own tests catalog."
            },
            {
                icon: "fas fa-check",
                label: "Instrumentation",
                info: "Hook-up to hundreds of instruments via open API."
            },
            {
                icon: "fas fa-check-circle",
                label: "Invoicing",
                info: "Invoice customers instantly to keep cash-flow going."
            },
            {
                icon: "fas fa-keyboard",
                label: "Result Entry",
                info: "Build in test limits and automate calculations to simplify entry of test results."
            },
            {
                icon: "fas fa-barcode",
                label: "Barcode",
                info: "Print appropriately formatted barcodes."
            },
            {
                icon: "fas fa-chart-line",
                label: "Dashboard",
                info: "View all your key performance indicators (KPIs) at a glance."
            },
            {
                icon: "fas fa-angle-double-right",
                label: "Workflow Management",
                info: "Enforce a standard operating procedure every time the same."
            },
            {
                icon: "fas fa-box-open",
                label: "Out-of-box",
                info: "Responsive menu and toolbar easily guide users through initial set-up and daily management."
            },
            {
                icon: "fas fa-bars",
                label: "Clear Navigation",
                info: "Provides the ability to track actions allowing for easier and faster retrieval."
            }
        ];

    }]);
});
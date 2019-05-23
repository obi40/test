define(['app', 'util', 'commonData'], function (app, util, commonData) {
    'use strict';
    /**
     * Disable button,tabs,etc if the user does not have an authority for it.
     * Use APPLICATION_ADMIN to hide/show for admin user.But check in back end that the user is actually an admin by using authorizeApplicationAdmin(..)
     * 
     */
    app.directive('authorityChecker', function () {
        return {
            restrict: 'A',
            link: function ($scope, $element, attr) {

                var gridTileClickHandler = function (evt) {
                    // the only working way to remove the ng-click from the <md-grid-tile>
                    evt.preventDefault();
                    evt.stopPropagation();
                };
                function authorize() {
                    if (!attr.authorityChecker) {
                        return;
                    }
                    //if admin user then show it else hide it
                    if (attr.authorityChecker === "APPLICATION_ADMIN") {
                        if (util.user.rid !== commonData.appAdminRid) {
                            $element.addClass("no-display");
                        }
                        return;
                    }
                    var auths = attr.authorityChecker.split("||").map(function (item) {
                        return item.trim();
                    });

                    var authorized = false;
                    for (var i = 0; i < auths.length; i++) {
                        if (util.authorities.indexOf(auths[i]) >= 0) {
                            authorized = true;
                            break;
                        }
                    }

                    if (!authorized) {
                        // to know if we modified the element so we can undo our modifications
                        if (!attr.hasOwnProperty("authorityCheckerModification")) {
                            attr.$set("authorityCheckerModification", "true");
                        }

                        $element.attr("title", util.systemMessages.noAuthority);
                        if (attr.hasOwnProperty("ngDisabled")) {
                            attr.$set("ngDisabled", "true");
                        } else {
                            $element.attr("disabled", "true");
                        }

                        switch ($element[0].nodeName.toLowerCase()) {
                            case "button":
                                break;
                            case "md-list-item":
                                $element.addClass("no-display");
                                break;
                            case "md-grid-tile":
                                $element.addClass("disabled-cursor");
                                $element.addClass("grey");
                                $element[0].addEventListener('click', gridTileClickHandler, true);
                                break;
                            case "md-tab":
                                $scope[attr.authorityCheckerVar] = true;
                                break;
                            case "md-toolbar":
                                $element.addClass("no-display");
                                break;
                            default:
                                $element.addClass("disabled-cursor");
                                //$element.addClass("disable-click-event");
                                break;
                        }
                        angular.element($element).unbind('click');//remove the click event, to keep the title

                    } else if (attr.hasOwnProperty("authorityCheckerModification")) {
                        $element.removeAttr("title");
                        if (attr.hasOwnProperty("ngDisabled")) {
                            attr.$set("ngDisabled", "false");
                        } else {
                            $element.attr("disabled", "false");
                        }
                        $element.removeClass("ng-hide");
                        $element.removeClass("disabled-cursor");
                        $element.removeClass("grey");
                        $element[0].removeEventListener('click', gridTileClickHandler, true);
                        $element.removeClass("no-display");
                        $element.removeClass("disable-click-event");
                    }
                }
                authorize();
                $scope.$on("reauthorize", function (event, data) {
                    authorize();
                });
            }
        }
    });
});
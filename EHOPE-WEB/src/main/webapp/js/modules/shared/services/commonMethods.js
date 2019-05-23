define(['app', 'util', 'commonData'], function (app, util, commonData) {
  'use strict';
  app.service('commonMethods', function () {
    this.retrieveMetaData = function (data) {
      return util.createApiRequest("getClassMetaData.srvc", JSON.stringify(data));
    };
    this.getCustomTokenData = function (data) {
      return util.createApiRequest("getCustomTokenData.pub.srvc", JSON.stringify(data));
    };
    this.getNavMenuItems = function () {
      var navMenuItems = angular.copy(commonData.navMenuItems);
      //Since the parent items do not have an authority then hide them if user does not have any authorities for children
      for (var idx = 0; idx < navMenuItems.length; idx++) {
        var parentMenuObj = navMenuItems[idx];
        if (parentMenuObj.authority) {
          continue;
        }
        var show = false;
        if (!parentMenuObj.authority) {
          for (var i = 0; i < parentMenuObj.subItems.length; i++) {
            var childMenuObj = parentMenuObj.subItems[i];
            if (util.authorities.indexOf(childMenuObj.authority) >= 0) {
              show = true;
              break;
            }
          }
        }
        parentMenuObj.show = show;
      }
      return navMenuItems;
    }
  });
});
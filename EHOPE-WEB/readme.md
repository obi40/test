Readme data to add later...

Adding new modules inside our application: gulp module --moduleName
Adding new libraries from NPM: perform "npm install --save <name>@<version>" to add to the package.json file and then copy the downloaded folder into the libs folder

How to build ehope-lis kendo theme:
1- Go to http://demos.telerik.com/kendo-ui/themebuilder/
2- Choose material as the theme to customize
3- Change "accent" color to #1565c0 (same as angular material ehope-lis theme primary color)
4- Change "selected background" to #039be5 (same as angular material ehope-lis theme secondary color)
5- Download the theme
6- Rename the css file from the zip file to kendo.ehope-lis.css
7- Place the css file in "styles" folder under kendoui
8- Change the path "https://kendo.cdn.telerik.com/2017.3.1026/styles/Material/" to "ehope-lis/" in the css file, this is used to reference images for the widgets
9- Change the .k-grid-filter.k-state-active background-color to #039be5, same as "selected background" in step 4 above
10- Create a new folder in "styles" under kendoui directory and name it "ehope-lis"
11- Copy the images from "material" folder to "ehope-lis" folder
12- Reference the new css file in index.html with the kendo.common-material.min.css for sizing

--Gridstack-angular wasn't found in NPM repository, download latest from: https://github.com/kdietrich/gridstack-angular
--KendoUI can be installed manually since it is acquired using a license, download from the vendor website or from our repository "\\consultant-svr\Telerik\Kendo UI Professional", current version: 2018.2.620. Note: you may also download a raw version (non-minified and not-built) using the command: npm install --save @progress/kendo-ui
--requirejs.min v2.3.5 has been added manually from https://requirejs.org/docs/download.html and renamed to require.min.js
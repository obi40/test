define("config", [], function () {
	'use strict';
	var config = {
		gridPageSizes: [10, 25, 50, 100],
		gridPageButtonCount: 1,
		testSelectionPageSize: 20,
		dateTimeFormat: 'dd/MM/yyyy HH:mm',
		dateFormat: 'dd/MM/yyyy',
		dateMask: '##/##/####',
		mobilePattern: null,
		kendoMobilePattern: null,
		kendoPercentageFormat: { decimals: 3, round: false, restrictDecimals: true, min: 0, max: 100, step: 10 },
		regexpPercent: new RegExp("^[0-9]*([.][0-9]+)?$"),
		regexpNum: new RegExp("^[0-9]*$"),
		api_path: '/lis/services/',
		lisDir: lisDir, //'dist' for prod, 'js' for dev. This is read from index.html lisDir variable
		versionNo: versionNo,
		server: 'http://localhost:8080'
	};
	return config;
});
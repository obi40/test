package com.optimiza.ehope.web.historicaldata.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.optimiza.core.common.helper.FilterablePageRequest;
import com.optimiza.ehope.lis.model.HistoricalOrder;
import com.optimiza.ehope.lis.model.HistoricalResult;
import com.optimiza.ehope.lis.model.HistoricalTest;
import com.optimiza.ehope.lis.service.HistoricalOrderService;
import com.optimiza.ehope.lis.service.HistoricalResultService;
import com.optimiza.ehope.lis.service.HistoricalTestService;

@RestController
@RequestMapping("/services")
public class HistoricalDataController {

	@Autowired
	private HistoricalOrderService historicalOrderService;

	@Autowired
	private HistoricalTestService historicalTestService;

	@Autowired
	private HistoricalResultService historicalResultService;

	@RequestMapping(value = "/getHistoricalOrderPage.srvc", method = RequestMethod.POST)
	public ResponseEntity<Page<HistoricalOrder>> getHistoricalOrderPage(@RequestBody FilterablePageRequest filterablePageRequest) {

		Page<HistoricalOrder> historicalOrderPage = historicalOrderService.find(filterablePageRequest.getFilters(),
				filterablePageRequest.getPageRequest(), HistoricalOrder.class);

		return new ResponseEntity<Page<HistoricalOrder>>(historicalOrderPage, HttpStatus.OK);
	}

	@RequestMapping(value = "/getHistoricalTestPage.srvc", method = RequestMethod.POST)
	public ResponseEntity<Page<HistoricalTest>> getHistoricalTestPage(@RequestBody FilterablePageRequest filterablePageRequest) {

		Page<HistoricalTest> historicalTestPage = historicalTestService.find(filterablePageRequest.getFilters(),
				filterablePageRequest.getPageRequest(), HistoricalTest.class);

		return new ResponseEntity<Page<HistoricalTest>>(historicalTestPage, HttpStatus.OK);
	}

	@RequestMapping(value = "/getHistoricalResultPage.srvc", method = RequestMethod.POST)
	public ResponseEntity<Page<HistoricalResult>> getHistoricalResultPage(@RequestBody FilterablePageRequest filterablePageRequest) {

		Page<HistoricalResult> historicalResultPage = historicalResultService.find(filterablePageRequest.getFilters(),
				filterablePageRequest.getPageRequest(), HistoricalResult.class);

		return new ResponseEntity<Page<HistoricalResult>>(historicalResultPage, HttpStatus.OK);
	}

}

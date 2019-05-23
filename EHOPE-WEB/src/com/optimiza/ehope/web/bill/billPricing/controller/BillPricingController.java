package com.optimiza.ehope.web.bill.billPricing.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.optimiza.core.common.helper.FilterablePageRequest;
import com.optimiza.ehope.lis.model.BillPricing;
import com.optimiza.ehope.lis.service.BillPricingService;

@RestController
@RequestMapping("/services")
public class BillPricingController {

	@Autowired
	private BillPricingService billPricingService;

	@RequestMapping(value = "/getBillPricingList.srvc", method = RequestMethod.POST)
	public ResponseEntity<Page<BillPricing>> getBillPricingList(@RequestBody FilterablePageRequest filterablePageRequest) {
		Page<BillPricing> all = billPricingService.getBillPricingList(filterablePageRequest);
		return new ResponseEntity<Page<BillPricing>>(all, HttpStatus.OK);
	}

	@RequestMapping(value = "/addBillPricing.srvc", method = RequestMethod.POST)
	public ResponseEntity<BillPricing> addBillPricing(@RequestBody BillPricing billPricing) {
		BillPricing saved = billPricingService.addBillPricing(billPricing);
		return new ResponseEntity<BillPricing>(saved, HttpStatus.OK);
	}

	@RequestMapping(value = "/addBillPricings.srvc", method = RequestMethod.POST)
	public ResponseEntity<List<BillPricing>> addBillPricings(@RequestBody List<BillPricing> billPricings) {
		List<BillPricing> saved = billPricingService.saveBillPricings(billPricings);
		return new ResponseEntity<List<BillPricing>>(saved, HttpStatus.OK);
	}

	@RequestMapping(value = "/editBillPricing.srvc", method = RequestMethod.POST)
	public ResponseEntity<BillPricing> editBillPricing(@RequestBody BillPricing billPricing) {
		BillPricing saved = billPricingService.editBillPricing(billPricing);
		return new ResponseEntity<BillPricing>(saved, HttpStatus.OK);
	}

}

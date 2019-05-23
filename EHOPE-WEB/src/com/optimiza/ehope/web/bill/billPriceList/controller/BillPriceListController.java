package com.optimiza.ehope.web.bill.billPriceList.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.optimiza.ehope.lis.model.BillPriceList;
import com.optimiza.ehope.lis.service.BillPriceListService;

@RestController
@RequestMapping("/services")
public class BillPriceListController {

	@Autowired
	private BillPriceListService billPriceListService;

	@RequestMapping(value = "/getBillPriceLists.srvc", method = RequestMethod.POST)
	public ResponseEntity<List<BillPriceList>> getBillPriceLists() {
		return new ResponseEntity<List<BillPriceList>>(billPriceListService.findAllBillPriceList(), HttpStatus.OK);
	}

	@RequestMapping(value = "/createBillPriceList.srvc", method = RequestMethod.POST)
	public ResponseEntity<BillPriceList> createBillPriceList(@RequestBody BillPriceList billPriceList) {
		return new ResponseEntity<BillPriceList>(billPriceListService.addBillPriceList(billPriceList), HttpStatus.OK);
	}

	@RequestMapping(value = "/updateBillPriceList.srvc", method = RequestMethod.POST)
	public ResponseEntity<BillPriceList> updateBillPriceList(@RequestBody BillPriceList billPriceList) {
		return new ResponseEntity<BillPriceList>(billPriceListService.editBillPriceList(billPriceList), HttpStatus.OK);
	}

	@RequestMapping(value = "/deleteBillPriceList.srvc", method = RequestMethod.POST)
	public ResponseEntity<Object> deleteBillPriceList(@RequestBody BillPriceList billPriceList) {
		billPriceListService.deleteBillPriceList(billPriceList);
		return new ResponseEntity<Object>("", HttpStatus.OK);
	}

	@RequestMapping(value = "/getDefaultBillPriceList.srvc", method = RequestMethod.POST)
	public ResponseEntity<BillPriceList> getDefaultBillPriceList() {
		return new ResponseEntity<BillPriceList>(billPriceListService.getDefault(), HttpStatus.OK);
	}

}

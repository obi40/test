package com.optimiza.ehope.web.bill.billClassification.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.optimiza.core.common.helper.FilterablePageRequest;
import com.optimiza.ehope.lis.model.BillClassification;
import com.optimiza.ehope.lis.service.BillClassificationService;

@RestController
@RequestMapping("/services")
public class BillClassificationController {

	@Autowired
	private BillClassificationService billClassificationService;

	@RequestMapping(value = "/getParentBillClassifications.srvc", method = RequestMethod.POST)
	public ResponseEntity<List<BillClassification>> getParentBillClassifications(@RequestBody(required = false) Long rid) {
		List<BillClassification> all = billClassificationService.getParentBillClassifications(rid);
		return new ResponseEntity<List<BillClassification>>(all, HttpStatus.OK);
	}

	@RequestMapping(value = "/getBillClassificationList.srvc", method = RequestMethod.POST)
	public ResponseEntity<List<BillClassification>> getBillClassificationList(@RequestBody FilterablePageRequest filterablePageRequest) {
		List<BillClassification> newAll = billClassificationService.getBillClassificationList(filterablePageRequest);
		return new ResponseEntity<List<BillClassification>>(newAll, HttpStatus.OK);
	}

	@RequestMapping(value = "/addBillClassification.srvc", method = RequestMethod.POST)
	public ResponseEntity<BillClassification> addBillClassification(@RequestBody BillClassification billClassification) {
		BillClassification saved = billClassificationService.addBillClassification(billClassification);
		return new ResponseEntity<BillClassification>(saved, HttpStatus.OK);
	}

	@RequestMapping(value = "/editBillClassification.srvc", method = RequestMethod.POST)
	public ResponseEntity<BillClassification> editBillClassification(@RequestBody BillClassification billClassification) {
		BillClassification saved = billClassificationService.editBillClassification(billClassification);
		return new ResponseEntity<BillClassification>(saved, HttpStatus.OK);
	}

	@RequestMapping(value = "/activateBillClassification.srvc", method = RequestMethod.POST)
	public ResponseEntity<BillClassification> activateBillClassification(@RequestBody Long rid) {
		BillClassification saved = billClassificationService.activateBillClassification(rid);
		return new ResponseEntity<BillClassification>(saved, HttpStatus.OK);
	}

	@RequestMapping(value = "/deactivateBillClassification.srvc", method = RequestMethod.POST)
	public ResponseEntity<BillClassification> deactivateBillClassification(@RequestBody Long rid) {
		BillClassification saved = billClassificationService.deactivateBillClassification(rid);
		return new ResponseEntity<BillClassification>(saved, HttpStatus.OK);
	}

	@RequestMapping(value = "/filterBillClassificationList.srvc", method = RequestMethod.POST)
	public ResponseEntity<List<BillClassification>> filterBillClassificationList(@RequestBody String searchValue) {
		return new ResponseEntity<List<BillClassification>>(billClassificationService.filterBillClassificationList(searchValue),
				HttpStatus.OK);
	}

}

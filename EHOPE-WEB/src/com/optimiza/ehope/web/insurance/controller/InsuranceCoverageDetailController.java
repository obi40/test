package com.optimiza.ehope.web.insurance.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.optimiza.ehope.lis.model.InsCoverageDetail;
import com.optimiza.ehope.lis.model.InsProviderPlan;
import com.optimiza.ehope.lis.service.InsCoverageDetailService;

@RestController
@RequestMapping("/services")
public class InsuranceCoverageDetailController {

	@Autowired
	private InsCoverageDetailService insCoverageDetailService;

	@RequestMapping(value = "/getInsCoverageDetailListByPlan.srvc", method = RequestMethod.POST)
	public ResponseEntity<List<InsCoverageDetail>> getInsCoverageDetailListByPlan(@RequestBody InsProviderPlan insProviderPlan) {
		return new ResponseEntity<List<InsCoverageDetail>>(insCoverageDetailService.findInsCoverageDetailListByPlan(insProviderPlan),
				HttpStatus.OK);
	}

	@RequestMapping(value = "/createInsCoverageDetail.srvc", method = RequestMethod.POST)
	public ResponseEntity<InsCoverageDetail> createInsCoverageDetail(@RequestBody InsCoverageDetail insCoverageDetail) {
		return new ResponseEntity<InsCoverageDetail>(insCoverageDetailService.createInsCoverageDetail(insCoverageDetail), HttpStatus.OK);
	}

	@RequestMapping(value = "/updateInsCoverageDetail.srvc", method = RequestMethod.POST)
	public ResponseEntity<InsCoverageDetail> updateInsCoverageDetail(@RequestBody InsCoverageDetail insCoverageDetail) {
		return new ResponseEntity<InsCoverageDetail>(insCoverageDetailService.updateInsCoverageDetail(insCoverageDetail), HttpStatus.OK);
	}

	@RequestMapping(value = "/deleteInsCoverageDetail.srvc", method = RequestMethod.POST)
	public ResponseEntity<Object> deleteInsCoverageDetail(@RequestBody InsCoverageDetail insCoverageDetail) {
		insCoverageDetailService.deleteInsCoverageDetail(insCoverageDetail);
		return new ResponseEntity<Object>("", HttpStatus.OK);
	}

}

package com.optimiza.ehope.web.insurance.controller;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.optimiza.core.base.helper.SearchCriterion;
import com.optimiza.core.base.helper.SearchCriterion.FilterOperator;
import com.optimiza.core.common.helper.FilterablePageRequest;
import com.optimiza.ehope.lis.model.InsProviderPlan;
import com.optimiza.ehope.lis.service.InsProviderPlanService;

@RestController
@RequestMapping("/services")
public class InsuranceProviderPlanController {

	@Autowired
	private InsProviderPlanService insProviderPlanService;

	@RequestMapping(value = "/getInsProviderPlanListByProvider.srvc", method = RequestMethod.POST)
	public ResponseEntity<List<InsProviderPlan>> getInsProviderPlanListByProvider(
			@RequestBody FilterablePageRequest filterablePageRequest) {
		return new ResponseEntity<List<InsProviderPlan>>(insProviderPlanService.findInsProviderPlansByProvider(filterablePageRequest),
				HttpStatus.OK);
	}

	@RequestMapping(value = "/getInsProviderPlanList.srvc", method = RequestMethod.POST)
	public ResponseEntity<List<InsProviderPlan>> getInsProviderPlanList() {
		return new ResponseEntity<List<InsProviderPlan>>(insProviderPlanService.find(
				Arrays.asList(new SearchCriterion("isSimple", Boolean.FALSE, FilterOperator.eq)), InsProviderPlan.class), HttpStatus.OK);
	}

	@RequestMapping(value = "/createInsProviderPlan.srvc", method = RequestMethod.POST)
	public ResponseEntity<InsProviderPlan> createInsProviderPlan(@RequestBody InsProviderPlan insProviderPlan) {
		return new ResponseEntity<InsProviderPlan>(insProviderPlanService.createInsProviderPlan(insProviderPlan), HttpStatus.OK);
	}

	@RequestMapping(value = "/updateInsProviderPlan.srvc", method = RequestMethod.POST)
	public ResponseEntity<InsProviderPlan> updateInsProviderPlan(@RequestBody InsProviderPlan insProviderPlan) {
		return new ResponseEntity<InsProviderPlan>(insProviderPlanService.updateInsProviderPlan(insProviderPlan), HttpStatus.OK);
	}

	@RequestMapping(value = "/deleteInsProviderPlan.srvc", method = RequestMethod.POST)
	public ResponseEntity<Object> deleteInsProviderPlan(@RequestBody InsProviderPlan insProviderPlan) {
		insProviderPlanService.deleteInsProviderPlan(insProviderPlan);
		return new ResponseEntity<Object>("", HttpStatus.OK);
	}

}

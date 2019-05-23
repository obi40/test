package com.optimiza.ehope.web.test.testDisclaimer.controller;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.optimiza.core.base.helper.SearchCriterion;
import com.optimiza.core.base.helper.SearchCriterion.FilterOperator;
import com.optimiza.ehope.lis.model.TestDisclaimer;
import com.optimiza.ehope.lis.service.TestDisclaimerService;

@RestController
@RequestMapping("/services")
public class TestDisclaimerController {

	@Autowired
	private TestDisclaimerService testDisclaimerService;

	@RequestMapping(value = "/getDisclaimers.srvc", method = RequestMethod.POST)
	public ResponseEntity<Set<TestDisclaimer>> getDisclaimers(@RequestBody Long testDefinitionRid) {
		Set<TestDisclaimer> disclaimers = new HashSet<>(
				testDisclaimerService.find(Arrays.asList(new SearchCriterion("testDefinition.rid", testDefinitionRid, FilterOperator.eq)),
						TestDisclaimer.class, "testDefinition"));
		return new ResponseEntity<Set<TestDisclaimer>>(disclaimers, HttpStatus.OK);
	}

}

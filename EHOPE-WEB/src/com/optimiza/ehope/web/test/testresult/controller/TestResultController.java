package com.optimiza.ehope.web.test.testresult.controller;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
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
import com.optimiza.ehope.lis.model.NarrativeResultTemplate;
import com.optimiza.ehope.lis.model.TestResult;
import com.optimiza.ehope.lis.service.NarrativeResultTemplateService;
import com.optimiza.ehope.lis.service.TestResultService;

@RestController
@RequestMapping("/services")
public class TestResultController {

	@Autowired
	private TestResultService resultService;

	@Autowired
	private NarrativeResultTemplateService narrativeResultTemplateService;

	@RequestMapping(value = "/getTestResults.srvc", method = RequestMethod.POST)
	public ResponseEntity<Set<TestResult>> getTestResults(@RequestBody Long testRid) {
		List<SearchCriterion> filters = new ArrayList<SearchCriterion>();
		filters.add(new SearchCriterion("testDefinition.rid", testRid, FilterOperator.eq));
		List<TestResult> resultList = resultService.find(filters, TestResult.class,
				"primaryUnit", "secondaryUnit", "resultValueType", "primaryUnitType",
				"testCodedResultMappings.testCodedResult", "narrativeTemplates",
				"normalRanges.ageFromUnit", "normalRanges.ageToUnit", "normalRanges.codedResult", "normalRanges.sex");
		Set<TestResult> resultSet = new HashSet<TestResult>(resultList);

		return new ResponseEntity<Set<TestResult>>(resultSet, HttpStatus.OK);
	}

	@RequestMapping(value = "/saveTestResults.srvc", method = RequestMethod.POST)
	public ResponseEntity<List<TestResult>> saveTestResults(@RequestBody List<TestResult> testResults) {
		//		testResults = resultService.saveTestResults(testResults);
		return new ResponseEntity<List<TestResult>>(testResults, HttpStatus.OK);
	}

	@RequestMapping(value = "/getNarrativeResultTemplates.srvc", method = RequestMethod.POST)
	public ResponseEntity<List<NarrativeResultTemplate>> getNormalRangeList(@RequestBody Long resultRid) {
		List<SearchCriterion> filters = new ArrayList<SearchCriterion>();
		filters.add(new SearchCriterion("result.rid", resultRid, FilterOperator.eq));
		List<NarrativeResultTemplate> templates = narrativeResultTemplateService.find(filters,
				NarrativeResultTemplate.class);

		return new ResponseEntity<List<NarrativeResultTemplate>>(templates, HttpStatus.OK);
	}

}

package com.optimiza.ehope.web.test.testDefinition.controller;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.optimiza.core.base.helper.SearchCriterion;
import com.optimiza.core.base.helper.SearchCriterion.FilterOperator;
import com.optimiza.core.common.helper.FilterablePageRequest;
import com.optimiza.ehope.lis.helper.TestEditability;
import com.optimiza.ehope.lis.model.TestDefinition;
import com.optimiza.ehope.lis.model.TestQuestion;
import com.optimiza.ehope.lis.service.TestDefinitionService;
import com.optimiza.ehope.lis.service.TestQuestionService;
import com.optimiza.ehope.web.test.wrapper.TestDefinitionFetch;

@RestController
@RequestMapping("/services")
public class TestDefinitionController {

	@Autowired
	private TestDefinitionService testDefinitionService;

	@Autowired
	private TestQuestionService questionService;

	@RequestMapping(value = "/getTestDefinitionPage.srvc", method = RequestMethod.POST)
	public ResponseEntity<Page<TestDefinition>> getTestDefinitionPage(@RequestBody FilterablePageRequest filterablePageRequest) {
		return new ResponseEntity<Page<TestDefinition>>(testDefinitionService.getTestDefinitionPage(filterablePageRequest), HttpStatus.OK);
	}

	@RequestMapping(value = "/getSelectableTestDefinitionPage.srvc", method = RequestMethod.POST)
	public ResponseEntity<Page<TestDefinition>> getSelectableTestDefinitionPage(@RequestBody FilterablePageRequest filterablePageRequest) {
		return new ResponseEntity<Page<TestDefinition>>(testDefinitionService.getSelectableTestDefinitionPage(filterablePageRequest),
				HttpStatus.OK);
	}

	@RequestMapping(value = "/getTestDefinitionLookup.srvc", method = RequestMethod.POST)
	public ResponseEntity<Page<TestDefinition>> getTestDefinitionLookup(@RequestBody FilterablePageRequest filterablePageRequest) {
		return new ResponseEntity<Page<TestDefinition>>(testDefinitionService.getTestDefinitionLookup(filterablePageRequest, Boolean.FALSE),
				HttpStatus.OK);
	}

	@RequestMapping(value = "/getTestDefinitionLookupWithDestinations.srvc", method = RequestMethod.POST)
	public ResponseEntity<Page<TestDefinition>> getTestDefinitionLookupWithDestinations(
			@RequestBody FilterablePageRequest filterablePageRequest) {
		return new ResponseEntity<Page<TestDefinition>>(testDefinitionService.getTestDefinitionLookup(filterablePageRequest, Boolean.TRUE),
				HttpStatus.OK);
	}

	@RequestMapping(value = "/getMostRequestedTests.srvc", method = RequestMethod.POST)
	public ResponseEntity<List<TestDefinition>> getMostRequestedTests(@RequestBody Integer count) {

		List<TestDefinition> result = testDefinitionService.getMostRequestedTests(count);

		return new ResponseEntity<List<TestDefinition>>(result, HttpStatus.OK);
	}

	@RequestMapping(value = "/isTestDefinitionEditable.srvc", method = RequestMethod.POST)
	public ResponseEntity<TestEditability> isTestDefinitionEditable(@RequestBody Long testDefinitionRid) {
		return new ResponseEntity<TestEditability>(testDefinitionService.testEditability(testDefinitionRid), HttpStatus.OK);
	}

	@RequestMapping(value = "/getTestDefinition.srvc", method = RequestMethod.POST)
	public ResponseEntity<TestDefinition> getTestDefinition(@RequestBody TestDefinitionFetch testDefinitionFetch) {

		SearchCriterion filterById = new SearchCriterion("rid", testDefinitionFetch.getRid(), FilterOperator.eq);
		List<SearchCriterion> filters = new ArrayList<SearchCriterion>();
		filters.add(filterById);

		ArrayList<String> joins = new ArrayList<String>();
		joins.add("section");
		//		joins.add("lkpContainerType");
		joins.add("lkpTestingMethod");
		joins.add("loincAttributes");
		joins.add("specimenType");
		joins.add("testSpecimens.specimenTemperature");
		joins.add("testSpecimens.containerType");
		joins.add("testSpecimens.stabilityUnit");
		joins.add("extraTests.extraTest");
		joins.add("extraTests.entryType");

		List<TestQuestion> questions = null;
		SearchCriterion filterQuestionByTestId = new SearchCriterion();
		filterQuestionByTestId.setField("testDefinition");
		filterQuestionByTestId.setValue(testDefinitionFetch.getRid());
		filterQuestionByTestId.setOperator(FilterOperator.eq);
		List<SearchCriterion> questionFilters = new ArrayList<SearchCriterion>();
		questionFilters.add(filterQuestionByTestId);

		String[] questionJoins = { "lkpQuestionType", "testQuestionOptions" };
		questions = questionService.find(questionFilters, TestQuestion.class, questionJoins);

		if (testDefinitionFetch.getMode().equals(TestDefinitionFetch.FetchMode.viewSingle)) {
			joins.add("tests.test");
			joins.add("testResults.primaryUnit");
			joins.add("testResults.resultValueType");
		}

		String[] joinsArray = new String[joins.size()];

		joins.toArray(joinsArray);

		TestDefinition result = testDefinitionService.findOne(filters, TestDefinition.class, joinsArray);
		if (questions == null) {
			questions = new ArrayList<TestQuestion>();
		}
		result.setTestQuestions(new HashSet<TestQuestion>(questions));

		return new ResponseEntity<TestDefinition>(result, HttpStatus.OK);
	}

	@RequestMapping(value = "/addTestDefinition.srvc", method = RequestMethod.POST)
	public ResponseEntity<String> addTestDefinition(@RequestBody TestDefinition testDefinition) {

		testDefinitionService.addTestDefinition(testDefinition);

		return new ResponseEntity<String>("Success", HttpStatus.OK);
	}

	@RequestMapping(value = "/editTestDefinition.srvc", method = RequestMethod.POST)
	public ResponseEntity<String> editTestDefinition(@RequestBody TestDefinition testDefinition) {

		testDefinitionService.editTestDefinition(testDefinition);

		return new ResponseEntity<String>("Success", HttpStatus.OK);
	}

	@RequestMapping(value = "/activateTestDefinition.srvc", method = RequestMethod.POST)
	public ResponseEntity<Boolean> activateTestDefinition(@RequestBody TestDefinitionFetch testDefinitionFetch) {

		TestDefinition savedTestDefinition = testDefinitionService.activateTestDefinition(testDefinitionFetch.getRid());

		return new ResponseEntity<Boolean>(savedTestDefinition.getIsActive(), HttpStatus.OK);
	}

	@RequestMapping(value = "/deactivateTestDefinition.srvc", method = RequestMethod.POST)
	public ResponseEntity<Boolean> deactivateTestDefinition(@RequestBody TestDefinitionFetch testDefinitionFetch) {

		TestDefinition savedTestDefinition = testDefinitionService.deactivateTestDefinition(testDefinitionFetch.getRid());

		return new ResponseEntity<Boolean>(savedTestDefinition.getIsActive(), HttpStatus.OK);
	}

	@RequestMapping(value = "/quickTestDefinition.srvc", method = RequestMethod.POST)
	public ResponseEntity<String> quickTestDefinition(@RequestBody TestDefinition testDefinition) {

		testDefinitionService.quickSaveTestDefinition(testDefinition);

		return new ResponseEntity<String>("Success", HttpStatus.OK);
	}

	@RequestMapping(value = "/getQuickTestDefinition.srvc", method = RequestMethod.POST)
	public ResponseEntity<TestDefinition> getQuickTestDefinition(@RequestBody Long testRid) {
		TestDefinition fetchedTest = testDefinitionService.getQuickTestDefintion(testRid);
		return new ResponseEntity<TestDefinition>(fetchedTest, HttpStatus.OK);
	}

	@RequestMapping(value = "/getTestsDefaultPricingPage.srvc", method = RequestMethod.POST)
	public ResponseEntity<Page<TestDefinition>> getTestsDefaultPricingPage(@RequestBody FilterablePageRequest filterablePageRequest) {
		return new ResponseEntity<Page<TestDefinition>>(testDefinitionService.getTestsDefaultPricingPage(filterablePageRequest),
				HttpStatus.OK);
	}

}

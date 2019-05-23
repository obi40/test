package com.optimiza.ehope.web.test.testQuestion.controller;

import java.util.Arrays;
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
import com.optimiza.ehope.lis.model.LabTestAnswer;
import com.optimiza.ehope.lis.model.TestQuestion;
import com.optimiza.ehope.lis.service.LabTestAnswerService;
import com.optimiza.ehope.lis.service.TestQuestionService;

@RestController
@RequestMapping("/services")
public class TestQuestionController {

	@Autowired
	private LabTestAnswerService testAnswerService;
	@Autowired
	private TestQuestionService testQuestionService;

	@RequestMapping(value = "/getTestQuestionEntryData.srvc", method = RequestMethod.POST)
	public ResponseEntity<List<LabTestAnswer>> getTestQuestionEntryData(@RequestBody Long visitRid) {
		return new ResponseEntity<List<LabTestAnswer>>(testAnswerService.getTestQuestionEntryData(visitRid), HttpStatus.OK);
	}

	@RequestMapping(value = "/answerQuestions.srvc", method = RequestMethod.POST)
	public ResponseEntity<List<LabTestAnswer>> answerQuestions(@RequestBody List<LabTestAnswer> answers) {
		return new ResponseEntity<List<LabTestAnswer>>(testAnswerService.createTestAnswer(answers), HttpStatus.OK);
	}

	@RequestMapping(value = "/getQuestions.srvc", method = RequestMethod.POST)
	public ResponseEntity<Set<TestQuestion>> getQuestions(@RequestBody Long testDefinitionRid) {
		Set<TestQuestion> questions = new HashSet<>(
				testQuestionService.find(Arrays.asList(new SearchCriterion("testDefinition.rid", testDefinitionRid, FilterOperator.eq)),
						TestQuestion.class, "testDefinition", "lkpQuestionType"));
		return new ResponseEntity<Set<TestQuestion>>(questions, HttpStatus.OK);
	}

}

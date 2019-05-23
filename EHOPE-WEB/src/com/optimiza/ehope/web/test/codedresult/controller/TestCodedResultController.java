package com.optimiza.ehope.web.test.codedresult.controller;

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
import com.optimiza.ehope.lis.model.TestCodedResult;
import com.optimiza.ehope.lis.model.TestCodedResultMapping;
import com.optimiza.ehope.lis.model.TestResult;
import com.optimiza.ehope.lis.service.TestCodedResultMappingService;
import com.optimiza.ehope.lis.service.TestCodedResultService;
import com.optimiza.ehope.web.test.wrapper.CodedResultMappingsWrapper;

@RestController
@RequestMapping("/services")
public class TestCodedResultController {

	@Autowired
	private TestCodedResultService codedResultService;

	@Autowired
	private TestCodedResultMappingService codedResultMappingService;

	@RequestMapping(value = "/getTestCodedResults.srvc", method = RequestMethod.POST)
	public ResponseEntity<Page<TestCodedResult>> getTestCodedResults(@RequestBody FilterablePageRequest filterablePageRequest) {
		Page<TestCodedResult> codedResults = codedResultService.find(filterablePageRequest.getFilters(),
				filterablePageRequest.getPageRequest(), TestCodedResult.class);
		return new ResponseEntity<Page<TestCodedResult>>(codedResults, HttpStatus.OK);
	}

	@RequestMapping(value = "/addTestCodedResult.srvc", method = RequestMethod.POST)
	public ResponseEntity<TestCodedResult> addTestCodedResult(@RequestBody TestCodedResult testCodedResult) {
		TestCodedResult codedResult = codedResultService.addTestCodedResult(testCodedResult);
		return new ResponseEntity<TestCodedResult>(codedResult, HttpStatus.OK);
	}

	@RequestMapping(value = "/editTestCodedResult.srvc", method = RequestMethod.POST)
	public ResponseEntity<TestCodedResult> editTestCodedResult(@RequestBody TestCodedResult testCodedResult) {
		TestCodedResult codedResult = codedResultService.editTestCodedResult(testCodedResult);
		return new ResponseEntity<TestCodedResult>(codedResult, HttpStatus.OK);
	}

	@RequestMapping(value = "/getTestCodedResultMappingsByResult.srvc", method = RequestMethod.POST)
	public ResponseEntity<List<TestCodedResultMapping>> getTestCodedResultMappingsByResult(@RequestBody TestResult testResult) {
		List<TestCodedResultMapping> codedResultMappings = codedResultMappingService.getTestCodedResultMappingsByResult(testResult);
		return new ResponseEntity<List<TestCodedResultMapping>>(codedResultMappings, HttpStatus.OK);
	}

	@RequestMapping(value = "/saveTestCodedResultMappings.srvc", method = RequestMethod.POST)
	public ResponseEntity<List<TestCodedResultMapping>> saveTestCodedResultMappings(
			@RequestBody CodedResultMappingsWrapper mappingsWrapper) {
		List<TestCodedResultMapping> codedResultMappings = codedResultMappingService.saveTestCodedResultMappings(
				mappingsWrapper.getMappings(), mappingsWrapper.getTestResult());
		return new ResponseEntity<List<TestCodedResultMapping>>(codedResultMappings, HttpStatus.OK);
	}

}

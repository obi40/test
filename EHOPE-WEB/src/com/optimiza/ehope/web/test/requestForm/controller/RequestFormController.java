package com.optimiza.ehope.web.test.requestForm.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.optimiza.core.base.helper.SearchCriterion;
import com.optimiza.ehope.lis.model.LabSection;
import com.optimiza.ehope.lis.model.TestRequestForm;
import com.optimiza.ehope.lis.service.TestRequestFormService;

@RestController
@RequestMapping("/services")
public class RequestFormController {

	@Autowired
	private TestRequestFormService testRequestFormService;

	@RequestMapping(value = "/getRequestForms.srvc", method = RequestMethod.POST)
	public ResponseEntity<List<TestRequestForm>> getRequestForms(@RequestBody List<SearchCriterion> filters) {
		List<TestRequestForm> results = testRequestFormService.getTestRequestForms(filters);
		return new ResponseEntity<List<TestRequestForm>>(results, HttpStatus.OK);
	}

	@RequestMapping(value = "/saveRequestFormTests.srvc", method = RequestMethod.POST)
	public ResponseEntity<String> saveRequestFormTests(@RequestBody TestRequestForm requestForm) {
		testRequestFormService.saveRequestFormTests(requestForm);
		return new ResponseEntity<String>("success", HttpStatus.OK);
	}

	@RequestMapping(value = "/addRequestForm.srvc", method = RequestMethod.POST)
	public ResponseEntity<TestRequestForm> addRequestForm(@RequestBody TestRequestForm testRequestForm) {
		testRequestFormService.addTestRequestForm(testRequestForm);
		return new ResponseEntity<TestRequestForm>(testRequestForm, HttpStatus.OK);
	}

	@RequestMapping(value = "/editRequestForm.srvc", method = RequestMethod.POST)
	public ResponseEntity<TestRequestForm> editRequestForm(@RequestBody TestRequestForm testRequestForm) {
		testRequestForm = testRequestFormService.editTestRequestForm(testRequestForm);
		return new ResponseEntity<TestRequestForm>(testRequestForm, HttpStatus.OK);
	}

	@RequestMapping(value = "/getRequestFormTests.srvc", method = RequestMethod.POST)
	public ResponseEntity<List<LabSection>> getRequestFormTests(@RequestBody Long testRequestFormRid) {
		List<LabSection> results = testRequestFormService.getRequestFormTests(testRequestFormRid);
		return new ResponseEntity<List<LabSection>>(results, HttpStatus.OK);
	}

	@RequestMapping(value = "/getRequestFormTestsWithDestinations.srvc", method = RequestMethod.POST)
	public ResponseEntity<List<LabSection>> getRequestFormTestsWithDestinations(@RequestBody Long testRequestFormRid) {
		List<LabSection> results = testRequestFormService.getRequestFormTestsWithDestinations(testRequestFormRid);
		return new ResponseEntity<List<LabSection>>(results, HttpStatus.OK);
	}

	@RequestMapping(value = "/activateRequestForm.srvc", method = RequestMethod.POST)
	public ResponseEntity<TestRequestForm> activateRequestForm(@RequestBody Long rid) {
		TestRequestForm saved = testRequestFormService.activateTestRequestForm(rid);
		return new ResponseEntity<TestRequestForm>(saved, HttpStatus.OK);
	}

	@RequestMapping(value = "/deactivateRequestForm.srvc", method = RequestMethod.POST)
	public ResponseEntity<TestRequestForm> deactivateRequestForm(@RequestBody Long rid) {
		TestRequestForm saved = testRequestFormService.deactivateTestRequestForm(rid);
		return new ResponseEntity<TestRequestForm>(saved, HttpStatus.OK);
	}
}

package com.optimiza.ehope.web.lab.labTestActualResult.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.optimiza.ehope.lis.model.LabTestActualResult;
import com.optimiza.ehope.lis.service.LabTestActualResultService;
import com.optimiza.ehope.web.visit.controller.ActualResultsWrapper;

@RestController
@RequestMapping("/services")
public class LabTestActualResultController {

	@Autowired
	private LabTestActualResultService labTestActualResultService;

	@RequestMapping(value = "/getActualTestResultsByVisit.srvc", method = RequestMethod.POST)
	public ResponseEntity<List<LabTestActualResult>> getActualTestResultsByVisit(@RequestBody Long visitRid) {
		return new ResponseEntity<List<LabTestActualResult>>(labTestActualResultService.getActualTestResultsByVisit(visitRid),
				HttpStatus.OK);
	}

	@RequestMapping(value = "/editActualTestResults.srvc", method = RequestMethod.POST)
	public ResponseEntity<Object> editActualTestResults(@RequestBody ActualResultsWrapper actualResultsWrapper) {
		labTestActualResultService.editTestActualResults(
				actualResultsWrapper.getActualResults(), actualResultsWrapper.getOrder().getRid(), false);
		return new ResponseEntity<Object>("Success", HttpStatus.OK);
	}
}

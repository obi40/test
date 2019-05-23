package com.optimiza.ehope.web.lab.separationFactors.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.optimiza.ehope.lis.model.LabBranchSeparationFactor;
import com.optimiza.ehope.lis.model.LabSeparationFactor;
import com.optimiza.ehope.lis.service.LabBranchSeparationFactorService;
import com.optimiza.ehope.lis.service.LabSeparationFactorService;

@RestController
@RequestMapping("/services")
public class LabSeparationFactorsController {

	@Autowired
	private LabBranchSeparationFactorService labBranchSeparationFactorService;

	@Autowired
	private LabSeparationFactorService labSeparationFactorService;

	@RequestMapping(value = "/getSepFactorList.srvc", method = RequestMethod.POST)
	public ResponseEntity<Map<String, Object>> getSepFactorList() {

		Map<String, Object> result = new HashMap<>();
		result.put("allFactors", labSeparationFactorService.findLabSeparationFactorList());
		result.put("labFactors", labBranchSeparationFactorService.findSeparationFactorByBranch());
		return new ResponseEntity<Map<String, Object>>(result, HttpStatus.OK);
	}

	@RequestMapping(value = "/getActiveFactorsByBranch.srvc", method = RequestMethod.POST)
	public ResponseEntity<List<LabSeparationFactor>> getActiveFactorsByBranch() {
		return new ResponseEntity<List<LabSeparationFactor>>(labSeparationFactorService.findActiveFactorsByBranch(), HttpStatus.OK);
	}

	@RequestMapping(value = "/updateBranchSepFactor.srvc", method = RequestMethod.POST)
	public ResponseEntity<List<LabBranchSeparationFactor>> updateBranchSepFactor(
			@RequestBody List<LabBranchSeparationFactor> branchSeparationFactorList) {

		labBranchSeparationFactorService.deleteAll();
		return new ResponseEntity<List<LabBranchSeparationFactor>>(
				labBranchSeparationFactorService.updateBranchSeparationFactor(branchSeparationFactorList),
				HttpStatus.OK);
	}
}

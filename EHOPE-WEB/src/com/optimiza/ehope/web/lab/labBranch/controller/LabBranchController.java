package com.optimiza.ehope.web.lab.labBranch.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.optimiza.core.common.helper.FilterablePageRequest;
import com.optimiza.ehope.lis.model.LabBranch;
import com.optimiza.ehope.lis.service.LabBranchService;

@RestController
@RequestMapping("/services")
public class LabBranchController {

	@Autowired
	private LabBranchService branchService;

	//lov
	@RequestMapping(value = "/getLabBranchList.srvc", method = RequestMethod.POST)
	public ResponseEntity<List<LabBranch>> getLabBranchList(@RequestBody FilterablePageRequest filterablePageRequest) {
		return new ResponseEntity<List<LabBranch>>(
				branchService.findBranchList(filterablePageRequest.getFilters(), filterablePageRequest.getSortObject()), HttpStatus.OK);
	}

	@RequestMapping(value = "/getLabBranchListExcluded.srvc", method = RequestMethod.POST)
	public ResponseEntity<List<LabBranch>> getLabBranchListExcluded(@RequestBody FilterablePageRequest filterablePageRequest) {
		return new ResponseEntity<List<LabBranch>>(
				branchService.findBranchesExcluded(filterablePageRequest.getFilters(), filterablePageRequest.getSortObject()),
				HttpStatus.OK);
	}

	@RequestMapping(value = "/getBranches.srvc", method = RequestMethod.POST)
	public ResponseEntity<List<LabBranch>> getBranches() {
		return new ResponseEntity<List<LabBranch>>(branchService.getBranches(), HttpStatus.OK);
	}

	// the api accepts arrays because somewhere else we are using lists , no need to duplicate api
	@RequestMapping(value = "/createBranch.srvc", method = RequestMethod.POST)
	public ResponseEntity<List<LabBranch>> createBranch(@RequestBody List<LabBranch> branch) {
		return new ResponseEntity<List<LabBranch>>(branchService.createBranch(branch), HttpStatus.OK);
	}

	@RequestMapping(value = "/updateBranch.srvc", method = RequestMethod.POST)
	public ResponseEntity<List<LabBranch>> updateBranch(@RequestBody List<LabBranch> branch) {
		return new ResponseEntity<List<LabBranch>>(branchService.updateBranch(branch), HttpStatus.OK);
	}

	@RequestMapping(value = "/activateBranch.srvc", method = RequestMethod.POST)
	public ResponseEntity<LabBranch> activateBranch(@RequestBody LabBranch branch) {
		return new ResponseEntity<LabBranch>(branchService.activateBranch(branch), HttpStatus.OK);
	}

	@RequestMapping(value = "/deactivateBranch.srvc", method = RequestMethod.POST)
	public ResponseEntity<LabBranch> deactivateBranch(@RequestBody LabBranch branch) {
		return new ResponseEntity<LabBranch>(branchService.deactivateBranch(branch), HttpStatus.OK);
	}

}

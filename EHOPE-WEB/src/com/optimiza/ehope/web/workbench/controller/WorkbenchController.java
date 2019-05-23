package com.optimiza.ehope.web.workbench.controller;

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
import com.optimiza.ehope.lis.model.Workbench;
import com.optimiza.ehope.lis.service.WorkbenchService;

@RestController
@RequestMapping("/services")
public class WorkbenchController {

	@Autowired
	private WorkbenchService workbenchService;

	@RequestMapping(value = "/getWorkbenchList.srvc", method = RequestMethod.POST)
	public ResponseEntity<List<Workbench>> getWorkbenchList(@RequestBody Long sourceBranchRid) {
		return new ResponseEntity<List<Workbench>>(workbenchService.getWorkbenchList(sourceBranchRid), HttpStatus.OK);
	}

	@RequestMapping(value = "/getWorkbenchPage.srvc", method = RequestMethod.POST)
	public ResponseEntity<Page<Workbench>> getWorkbenchPage(@RequestBody FilterablePageRequest filterablePageRequest) {
		return new ResponseEntity<Page<Workbench>>(workbenchService.getWorkbenchPage(filterablePageRequest), HttpStatus.OK);
	}

	@RequestMapping(value = "/addWorkbench.srvc", method = RequestMethod.POST)
	public ResponseEntity<Workbench> addWorkbench(@RequestBody Workbench workbench) {
		Workbench saved = workbenchService.createWorkbench(workbench);
		return new ResponseEntity<Workbench>(saved, HttpStatus.OK);
	}

	@RequestMapping(value = "/editWorkbench.srvc", method = RequestMethod.POST)
	public ResponseEntity<Workbench> editWorkbench(@RequestBody Workbench workbench) {
		Workbench saved = workbenchService.updateWorkbench(workbench);
		return new ResponseEntity<Workbench>(saved, HttpStatus.OK);
	}

	@RequestMapping(value = "/deleteWorkbench.srvc", method = RequestMethod.POST)
	public ResponseEntity<String> deleteWorkbench(@RequestBody Long workbenchRid) {
		workbenchService.deleteWorkbench(workbenchRid);
		return new ResponseEntity<String>("Success", HttpStatus.OK);
	}

}

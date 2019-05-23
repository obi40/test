package com.optimiza.ehope.web.lab.labunit.controller;

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
import com.optimiza.ehope.lis.model.LabUnit;
import com.optimiza.ehope.lis.service.LabUnitService;

@RestController
@RequestMapping("/services")
public class LabUnitController {

	@Autowired
	private LabUnitService labUnitService;

	@RequestMapping(value = "/getLabUnitList.srvc", method = RequestMethod.POST)
	public ResponseEntity<List<LabUnit>> getLabUnitList() {
		return new ResponseEntity<List<LabUnit>>(labUnitService.findAll(), HttpStatus.OK);
	}

	@RequestMapping(value = "/getLabUnitPage.srvc", method = RequestMethod.POST)
	public ResponseEntity<Page<LabUnit>> getLabUnitPage(@RequestBody FilterablePageRequest filterablePageRequest) {
		return new ResponseEntity<Page<LabUnit>>(labUnitService.findLabUnitList(filterablePageRequest), HttpStatus.OK);
	}

	@RequestMapping(value = "/createLabUnit.srvc", method = RequestMethod.POST)
	public ResponseEntity<LabUnit> createLabUnit(@RequestBody LabUnit labUnit) {
		return new ResponseEntity<LabUnit>(labUnitService.createUnit(labUnit), HttpStatus.OK);
	}

	@RequestMapping(value = "/updateLabUnit.srvc", method = RequestMethod.POST)
	public ResponseEntity<LabUnit> updateLabUnit(@RequestBody LabUnit labUnit) {
		return new ResponseEntity<LabUnit>(labUnitService.editLabUnit(labUnit), HttpStatus.OK);
	}

	@RequestMapping(value = "/deleteLabUnit.srvc", method = RequestMethod.POST)
	public ResponseEntity<Object> deleteLabUnit(@RequestBody Long labUnitId) {
		labUnitService.deleteLabUnit(labUnitId);
		return new ResponseEntity<Object>("", HttpStatus.OK);
	}

}

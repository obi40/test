package com.optimiza.ehope.web.test.destination.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.optimiza.ehope.lis.service.TestDestinationService;

@RestController
@RequestMapping("/services")
public class TestDestinationController {

	@Autowired
	private TestDestinationService destinationService;

	@RequestMapping(value = "/getDestinationEntryData.srvc", method = RequestMethod.POST)
	public ResponseEntity<Map<String, Object>> getDestinationEntryData(@RequestBody Long visitRid) {
		return new ResponseEntity<Map<String, Object>>(destinationService.getDestinationEntryData(visitRid), HttpStatus.OK);
	}

	@RequestMapping(value = "/createDestinationsForPriceLists.srvc", method = RequestMethod.POST)
	public ResponseEntity<String> createDestinationsForPriceLists() {
		destinationService.createDestinationsForPriceLists();
		return new ResponseEntity<String>("Success", HttpStatus.OK);
	}

}

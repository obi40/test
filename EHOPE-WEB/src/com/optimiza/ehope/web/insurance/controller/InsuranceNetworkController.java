package com.optimiza.ehope.web.insurance.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.optimiza.ehope.lis.model.InsNetwork;
import com.optimiza.ehope.lis.service.InsNetworkService;

@RestController
@RequestMapping("/services")
public class InsuranceNetworkController {

	@Autowired
	private InsNetworkService insNetworkService;

	@RequestMapping(value = "/getInsNetworkList.srvc", method = RequestMethod.POST)
	public ResponseEntity<List<InsNetwork>> getInsNetworkList() {
		return new ResponseEntity<List<InsNetwork>>(insNetworkService.findInsNetworks(), HttpStatus.OK);
	}

	@RequestMapping(value = "/createInsNetwork.srvc", method = RequestMethod.POST)
	public ResponseEntity<InsNetwork> createInsNetwork(@RequestBody InsNetwork insNetwork) {
		return new ResponseEntity<InsNetwork>(insNetworkService.createInsNetwork(insNetwork), HttpStatus.OK);
	}

	@RequestMapping(value = "/updateInsNetwork.srvc", method = RequestMethod.POST)
	public ResponseEntity<InsNetwork> updateInsNetwork(@RequestBody InsNetwork insNetwork) {
		return new ResponseEntity<InsNetwork>(insNetworkService.updateInsNetwork(insNetwork), HttpStatus.OK);
	}

	@RequestMapping(value = "/deleteInsNetwork.srvc", method = RequestMethod.POST)
	public ResponseEntity<Object> deleteInsNetwork(@RequestBody InsNetwork insNetwork) {
		insNetworkService.deleteInsNetwork(insNetwork);
		return new ResponseEntity<Object>("", HttpStatus.OK);
	}

}

package com.optimiza.ehope.web;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.optimiza.ehope.lis.service.EmrPatientInfoService;
import com.optimiza.ehope.lis.service.EmrVisitService;

@RestController
@RequestMapping("/services")
public class DashBoardController {

	@Autowired
	private EmrVisitService visitService;
	@Autowired
	private EmrPatientInfoService patientInfoService;

	@RequestMapping(value = "/getTotalVisits.srvc", method = RequestMethod.POST)
	public ResponseEntity<Long> getTotalVisits() {
		return new ResponseEntity<Long>(visitService.count(), HttpStatus.OK);
	}

	@RequestMapping(value = "/getTotalActivePatients.srvc", method = RequestMethod.POST)
	public ResponseEntity<Map<String, Long>> getTotalActivePatients() {
		Map<String, Long> map = new HashMap<>();
		map.put("active", patientInfoService.getActivePatientCount(Boolean.TRUE));
		map.put("inactive", patientInfoService.getActivePatientCount(Boolean.FALSE));
		return new ResponseEntity<Map<String, Long>>(map, HttpStatus.OK);
	}

	@RequestMapping(value = "/getTotalNewPatients.srvc", method = RequestMethod.POST)
	public ResponseEntity<Long> getTotalNewPatients() {
		return new ResponseEntity<Long>(patientInfoService.getTotalNewPatients(), HttpStatus.OK);
	}

}

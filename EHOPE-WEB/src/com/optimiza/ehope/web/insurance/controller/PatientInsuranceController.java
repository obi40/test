package com.optimiza.ehope.web.insurance.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.optimiza.core.common.helper.FilterablePageRequest;
import com.optimiza.ehope.lis.model.EmrPatientInsuranceInfo;
import com.optimiza.ehope.lis.service.EmrPatientInsuranceInfoService;

@RestController
@RequestMapping("/services")
public class PatientInsuranceController {

	@Autowired
	private EmrPatientInsuranceInfoService patientInsuranceService;

	@RequestMapping(value = "/getPatientInsuranceList.srvc", method = RequestMethod.POST)
	public ResponseEntity<Page<EmrPatientInsuranceInfo>> getPatientInsuranceList(
			@RequestBody FilterablePageRequest filterablePageRequest) {
		Page<EmrPatientInsuranceInfo> result = patientInsuranceService.getPatientInsuranceList(filterablePageRequest);
		return new ResponseEntity<Page<EmrPatientInsuranceInfo>>(result, HttpStatus.OK);
	}

	@RequestMapping(value = "/addPatientInsurance.srvc", method = RequestMethod.POST)
	public ResponseEntity<EmrPatientInsuranceInfo> addPatientInsurance(@RequestBody EmrPatientInsuranceInfo patientInsurance) {
		EmrPatientInsuranceInfo newPatientInsurance = patientInsuranceService.addPatientInsuranceInfo(patientInsurance);
		return new ResponseEntity<EmrPatientInsuranceInfo>(newPatientInsurance, HttpStatus.OK);
	}

	@RequestMapping(value = "/editPatientInsurance.srvc", method = RequestMethod.POST)
	public ResponseEntity<EmrPatientInsuranceInfo> editPatientInsurance(@RequestBody EmrPatientInsuranceInfo patientInsurance) {
		EmrPatientInsuranceInfo savedPatientInsurance = patientInsuranceService.updatePatientInsuranceInfo(patientInsurance);
		return new ResponseEntity<EmrPatientInsuranceInfo>(savedPatientInsurance, HttpStatus.OK);
	}

	@RequestMapping(value = "/activatePatientInsurance.srvc", method = RequestMethod.POST)
	public ResponseEntity<EmrPatientInsuranceInfo> activatePatientInsurance(@RequestBody Long rid) {
		EmrPatientInsuranceInfo saved = patientInsuranceService.activatePatientInsurance(rid);
		return new ResponseEntity<EmrPatientInsuranceInfo>(saved, HttpStatus.OK);
	}

	@RequestMapping(value = "/deactivatePatientInsurance.srvc", method = RequestMethod.POST)
	public ResponseEntity<EmrPatientInsuranceInfo> deactivatePatientInsurance(@RequestBody Long rid) {
		EmrPatientInsuranceInfo saved = patientInsuranceService.deactivatePatientInsurance(rid);
		return new ResponseEntity<EmrPatientInsuranceInfo>(saved, HttpStatus.OK);
	}

	@RequestMapping(value = "/triggerDefaultPatientInsurance.srvc", method = RequestMethod.POST)
	public ResponseEntity<EmrPatientInsuranceInfo> triggerDefaultPatientInsurance(@RequestBody EmrPatientInsuranceInfo patientInsurance) {
		return new ResponseEntity<EmrPatientInsuranceInfo>(patientInsuranceService.changeDefaultInsurance(patientInsurance),
				HttpStatus.OK);
	}

}

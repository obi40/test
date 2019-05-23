package com.optimiza.ehope.web.insurance.controller;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.optimiza.ehope.lis.lkp.helper.ClientPurpose;
import com.optimiza.ehope.lis.lkp.helper.TestDestinationType;
import com.optimiza.ehope.lis.model.EmrPatientInfo;
import com.optimiza.ehope.lis.model.EmrPatientInsuranceInfo;
import com.optimiza.ehope.lis.model.InsProvider;
import com.optimiza.ehope.lis.service.EmrPatientInsuranceInfoService;
import com.optimiza.ehope.lis.service.InsProviderService;
import com.optimiza.ehope.lis.wrapper.InsParentProviderWrapper;
import com.optimiza.ehope.web.insurance.wrapper.InsProviderPlanWrapper;

@RestController
@RequestMapping("/services")
public class InsuranceProviderController {

	@Autowired
	private InsProviderService insProviderService;

	@Autowired
	private EmrPatientInsuranceInfoService patientInsuranceInfoService;

	@RequestMapping(value = "/getInsParentProviderList.srvc", method = RequestMethod.POST)
	public ResponseEntity<List<InsParentProviderWrapper>> getInsParentProviderList() {
		return new ResponseEntity<List<InsParentProviderWrapper>>(insProviderService.fetchInsProviders(), HttpStatus.OK);
	}

	@RequestMapping(value = "/getInsProviderList.srvc", method = RequestMethod.POST)
	public ResponseEntity<List<InsProvider>> getInsProviderList() {
		return new ResponseEntity<List<InsProvider>>(insProviderService.findInsProviders(), HttpStatus.OK);
	}

	@RequestMapping(value = "/getProviderWithPlanListByPatient.srvc", method = RequestMethod.POST)
	public ResponseEntity<Set<InsProviderPlanWrapper>> getProviderListByPatient(@RequestBody EmrPatientInfo patient) {
		List<EmrPatientInsuranceInfo> emrPatientInsuranceInfoList = patientInsuranceInfoService.findPatientInsuranceListByPatient(
				patient, Boolean.TRUE);
		Set<InsProviderPlanWrapper> providerPlanWrapperSet = new HashSet<>();
		for (EmrPatientInsuranceInfo epii : emrPatientInsuranceInfoList) {
			providerPlanWrapperSet.add(new InsProviderPlanWrapper(epii, epii.getInsProvider(), epii.getInsProviderPlan()));
		}
		return new ResponseEntity<Set<InsProviderPlanWrapper>>(providerPlanWrapperSet, HttpStatus.OK);
	}

	@RequestMapping(value = "/createInsProvider.srvc", method = RequestMethod.POST)
	public ResponseEntity<InsProvider> createInsProvider(@RequestBody InsProvider insProvider) throws Exception {
		return new ResponseEntity<InsProvider>(insProviderService.createInsProvider(insProvider), HttpStatus.OK);
	}

	@RequestMapping(value = "/updateInsProvider.srvc", method = RequestMethod.POST)
	public ResponseEntity<InsProvider> updateInsProvider(@RequestBody InsProvider insProvider) throws Exception {
		return new ResponseEntity<InsProvider>(insProviderService.updateInsProvider(insProvider), HttpStatus.OK);
	}

	@RequestMapping(value = "/activateInsProvider.srvc", method = RequestMethod.POST)
	public ResponseEntity<String> activateInsProvider(@RequestBody InsProvider insProvider) {
		insProviderService.activateInsProvider(insProvider);
		return new ResponseEntity<String>("", HttpStatus.OK);
	}

	@RequestMapping(value = "/deactivateInsProvider.srvc", method = RequestMethod.POST)
	public ResponseEntity<String> deactivateInsProvider(@RequestBody InsProvider insProvider) {
		insProviderService.deactivateInsProvider(insProvider);
		return new ResponseEntity<String>("", HttpStatus.OK);
	}

	@RequestMapping(value = "/getClientList.srvc", method = RequestMethod.POST)
	public ResponseEntity<List<InsProvider>> getClientList(@RequestBody Map<String, Object> data) {
		Long branchRidToExclude = data.get("branchRidToExclude") == null ? null : Long.valueOf(data.get("branchRidToExclude").toString());
		ClientPurpose purpose = data.get("purpose") == null ? null : ClientPurpose.valueOf(data.get("purpose").toString());
		TestDestinationType type = TestDestinationType.valueOf(data.get("type").toString());

		List<InsProvider> clients = insProviderService.getClients(type, purpose, branchRidToExclude);

		return new ResponseEntity<List<InsProvider>>(clients, HttpStatus.OK);
	}

	@RequestMapping(value = "/getAllClientsList.srvc", method = RequestMethod.POST)
	public ResponseEntity<List<InsProvider>> getAllClients() {
		List<InsProvider> clients = insProviderService.getAllClients(null);
		return new ResponseEntity<List<InsProvider>>(clients, HttpStatus.OK);
	}

	//	@RequestMapping(value = "/getAcculabClientList.srvc", method = RequestMethod.POST)
	//	public ResponseEntity<List<InsProvider>> getAcculabClients() {
	//		List<InsProvider> clients = insProviderService.getClients(Boolean.FALSE, -1L);
	//		return new ResponseEntity<List<InsProvider>>(clients, HttpStatus.OK);
	//	}
	//
	//	@RequestMapping(value = "/getExternalClientList.srvc", method = RequestMethod.POST)
	//	public ResponseEntity<List<InsProvider>> getExternalClientList() {
	//		List<InsProvider> clients = insProviderService.getExternalClients();
	//		return new ResponseEntity<List<InsProvider>>(clients, HttpStatus.OK);
	//	}

}

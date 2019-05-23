package com.optimiza.ehope.web.lab.labTestActual.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.optimiza.core.common.helper.FilterablePageRequest;
import com.optimiza.core.common.util.StringUtil;
import com.optimiza.ehope.lis.lkp.helper.OperationStatus;
import com.optimiza.ehope.lis.model.LabTestActual;
import com.optimiza.ehope.lis.model.TestDestination;
import com.optimiza.ehope.lis.service.ActualTestArtifactService;
import com.optimiza.ehope.lis.service.LabTestActualService;
import com.optimiza.ehope.web.patient.wrapper.VisitTestsWrapper;

@RestController
@RequestMapping("/services")
public class LabTestActualController {

	@Autowired
	private LabTestActualService labTestActualService;

	@Autowired
	private ActualTestArtifactService actualTestArtifactService;

	@RequestMapping(value = "/getTestActualListWithResultsByVisit.srvc", method = RequestMethod.POST)
	public ResponseEntity<Set<LabTestActual>> findTestActualListWithResultsByVisit(@RequestBody Map<String, String> map) {

		OperationStatus fetchedStatus = StringUtil.isEmpty(map.get("fetchedStatus")) ? null
				: OperationStatus.valueOf(map.get("fetchedStatus"));

		return new ResponseEntity<Set<LabTestActual>>(
				labTestActualService.findTestActualListWithResultsByVisit(Long.valueOf(map.get("visitRid")), fetchedStatus),
				HttpStatus.OK);
	}

	@RequestMapping(value = "/getTestOrderManagementData.srvc", method = RequestMethod.POST)
	public ResponseEntity<Page<LabTestActual>> getTestOrderManagementData(@RequestBody FilterablePageRequest filterablePageRequest) {
		return new ResponseEntity<Page<LabTestActual>>(labTestActualService.getTestOrderManagementData(filterablePageRequest),
				HttpStatus.OK);
	}

	@RequestMapping(value = "/getTestsBySampleOrderManagement.srvc", method = RequestMethod.POST)
	public ResponseEntity<Set<LabTestActual>> getTestsBySampleOrderManagement(@RequestBody Long sampleRid) {
		return new ResponseEntity<Set<LabTestActual>>(labTestActualService.getTestsBySampleOrderManagement(sampleRid), HttpStatus.OK);
	}

	@RequestMapping(value = "/saveActualTestArtifacts.srvc", method = RequestMethod.POST, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<String> saveActualTestArtifacts(
			@RequestParam(value = "artifacts", required = false) MultipartFile[] artifacts,
			@RequestParam(value = "artifactsToDelete", required = false) String artifactsToDelete,
			@RequestParam(value = "actualTest") String actualTest) throws IOException {

		ObjectMapper mapper = new ObjectMapper();

		LabTestActual actualTestObject = mapper.readValue(actualTest, LabTestActual.class);

		actualTestArtifactService.saveArtifacts(artifacts, actualTestObject);
		List<Long> artifactIdsToDelete = new ArrayList<Long>();
		if (!StringUtil.isEmpty(artifactsToDelete)) {
			String[] tempIds = artifactsToDelete.split(",");
			for (String ridToDelete : tempIds) {
				artifactIdsToDelete.add(Long.valueOf(ridToDelete));
			}
		}
		actualTestArtifactService.deleteArtifacts(artifactIdsToDelete);

		return new ResponseEntity<String>("Success", HttpStatus.OK);
	}

	@RequestMapping(value = "/createActualTests.srvc", method = RequestMethod.POST)
	public ResponseEntity<List<LabTestActual>> createActualTests(@RequestBody VisitTestsWrapper visitTests) {
		List<LabTestActual> labTestActualList = labTestActualService.createTestActual(visitTests.getVisit(),
				visitTests.getTestDefinitionList(), visitTests.getTestGroupList());
		return new ResponseEntity<List<LabTestActual>>(labTestActualList, HttpStatus.OK);
	}

	@RequestMapping(value = "/deleteActualTest.srvc", method = RequestMethod.POST)
	public ResponseEntity<Object> deleteActualTest(@RequestBody Long testActualRid) {
		labTestActualService.deleteTestActualList(Arrays.asList(labTestActualService.findById(testActualRid)));
		return new ResponseEntity<Object>("", HttpStatus.OK);
	}

	@RequestMapping(value = "/updateActualTestsDestinations.srvc", method = RequestMethod.POST)
	public ResponseEntity<List<LabTestActual>> updateActualTestsDestinations(
			@RequestBody Map<Long, TestDestination> testActualsDestinations) {
		return new ResponseEntity<List<LabTestActual>>(labTestActualService.updateActualTestsDestinations(testActualsDestinations),
				HttpStatus.OK);
	}

}

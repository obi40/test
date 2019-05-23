package com.optimiza.ehope.web.visit.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
import com.optimiza.core.common.util.DateUtil;
import com.optimiza.core.common.util.StringUtil;
import com.optimiza.core.lkp.service.ComTenantLanguageService;
import com.optimiza.ehope.lis.model.EmrVisit;
import com.optimiza.ehope.lis.service.EmrVisitOperationHistoryService;
import com.optimiza.ehope.lis.service.EmrVisitService;
import com.optimiza.ehope.lis.service.LabSampleOperationHistoryService;
import com.optimiza.ehope.lis.service.LabSampleService;
import com.optimiza.ehope.lis.service.LabTestActualOperationHistoryService;
import com.optimiza.ehope.lis.service.LabTestActualService;
import com.optimiza.ehope.lis.service.OrderArtifactService;
import com.optimiza.ehope.web.visit.wrapper.OperationStatusWrapper;

@RestController
@RequestMapping("/services")
public class EmrVisitController {

	@Autowired
	private ComTenantLanguageService comTenantLanguageService;
	@Autowired
	private EmrVisitService visitService;
	@Autowired
	private OrderArtifactService orderArtifactService;
	@Autowired
	private EmrVisitOperationHistoryService visitOperationHistoryService;
	@Autowired
	private LabSampleOperationHistoryService sampleOperationHistoryService;
	@Autowired
	private LabTestActualOperationHistoryService testActualOperationHistoryService;
	@Autowired
	private LabSampleService sampleService;
	@Autowired
	private LabTestActualService testActualService;

	@Value("${system.website.url}")
	private String serverUrl;

	//wizard
	@RequestMapping(value = "/createVisit.srvc", method = RequestMethod.POST)
	public ResponseEntity<EmrVisit> createVisit(@RequestBody EmrVisit visit) {
		return new ResponseEntity<EmrVisit>(visitService.createVisit(visit), HttpStatus.OK);
	}

	@RequestMapping(value = "/updateVisitWizard.srvc", method = RequestMethod.POST)
	public ResponseEntity<EmrVisit> updateVisitWizard(@RequestBody EmrVisit visit) {
		return new ResponseEntity<EmrVisit>(visitService.updateVisit(visit, null), HttpStatus.OK);
	}

	@RequestMapping(value = "/updateVisitAppointment.srvc", method = RequestMethod.POST)
	public ResponseEntity<EmrVisit> updateVisitAppointment(@RequestBody Map<String, String> map) {
		EmrVisit visit = visitService.findById(Long.parseLong(map.get("visitRid")));
		visit.setAppointmentCardDate(DateUtil.parseUTCDate(map.get("appointmentCardDate")));
		visit.setAppointmentCardNotes(map.get("appointmentCardNotes"));
		visit.setAppointmentCardTime(map.get("appointmentCardTime"));
		visit = visitService.updateVisit(visit, null);
		return new ResponseEntity<EmrVisit>(visit, HttpStatus.OK);
	}

	@RequestMapping(value = "/updateVisitPayment.srvc", method = RequestMethod.POST)
	public ResponseEntity<EmrVisit> updateVisitPayment(@RequestBody Map<String, String> map) {
		EmrVisit visit = visitService.findById(Long.parseLong(map.get("visitRid")));
		visit.setIsEmailNotification(Boolean.valueOf(map.get("isEmailNotification")));
		visit.setIsSmsNotification(Boolean.valueOf(map.get("isSmsNotification")));
		visit = visitService.updateVisit(visit, null);
		return new ResponseEntity<EmrVisit>(visit, HttpStatus.OK);
	}

	//wizard
	@RequestMapping(value = "/fetchVisit.srvc", method = RequestMethod.POST)
	public ResponseEntity<EmrVisit> fetchVisit(@RequestBody Long visitRid) {
		return new ResponseEntity<EmrVisit>(visitService.fetchEditVisit(visitRid), HttpStatus.OK);
	}

	@RequestMapping(value = "/saveOrderArtifacts.srvc", method = RequestMethod.POST, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<String> saveOrderArtifacts(
			@RequestParam(value = "artifacts", required = false) MultipartFile[] artifacts,
			@RequestParam(value = "artifactsToDelete", required = false) String artifactsToDelete,
			@RequestParam(value = "order") String order) throws IOException {

		ObjectMapper mapper = new ObjectMapper();

		EmrVisit orderObject = mapper.readValue(order, EmrVisit.class);

		orderArtifactService.saveArtifacts(artifacts, orderObject);
		List<Long> artifactIdsToDelete = new ArrayList<Long>();
		if (!StringUtil.isEmpty(artifactsToDelete)) {
			String[] tempIds = artifactsToDelete.split(",");
			for (String ridToDelete : tempIds) {
				artifactIdsToDelete.add(Long.valueOf(ridToDelete));
			}
		}
		orderArtifactService.deleteArtifacts(artifactIdsToDelete);

		return new ResponseEntity<String>("Success", HttpStatus.OK);
	}

	@RequestMapping(value = "/getVisitPageByPatient.srvc", method = RequestMethod.POST)
	public ResponseEntity<Page<EmrVisit>> getVisitPageByPatient(@RequestBody FilterablePageRequest filterablePageRequest) {
		Page<EmrVisit> visits = visitService.getVisitPageByPatient(filterablePageRequest);
		return new ResponseEntity<Page<EmrVisit>>(visits, HttpStatus.OK);
	}

	@RequestMapping(value = "/getVisitPage.srvc", method = RequestMethod.POST)
	public ResponseEntity<Page<EmrVisit>> getVisitPage(@RequestBody FilterablePageRequest filterablePageRequest) {
		Page<EmrVisit> visits = visitService.getVisitPage(filterablePageRequest);
		return new ResponseEntity<Page<EmrVisit>>(visits, HttpStatus.OK);
	}

	@RequestMapping(value = "/getVisitOrderManagementData.srvc", method = RequestMethod.POST)
	public ResponseEntity<Page<EmrVisit>> getVisitOrderManagementData(@RequestBody FilterablePageRequest filterablePageRequest) {
		return new ResponseEntity<Page<EmrVisit>>(visitService.getVisitOrderManagementData(filterablePageRequest), HttpStatus.OK);
	}

	@RequestMapping(value = "/getVisitSampleTestHistory.srvc", method = RequestMethod.POST)
	public ResponseEntity<Object> getVisitSampleTestHistory(@RequestBody OperationStatusWrapper operationStatusWrapper) {

		String[] joins = { "oldOperationStatus", "newOperationStatus", "" };
		Object historyList = null;
		if (operationStatusWrapper.getType().equals("EmrVisit")) {
			joins[2] = "emrVisit";
			historyList = visitOperationHistoryService.findVisitOperationHistoryList(
					operationStatusWrapper.getFilterablePageRequest().getFilters(),
					operationStatusWrapper.getFilterablePageRequest().getSortObject(), joins);

		} else if (operationStatusWrapper.getType().equals("LabSample")) {
			joins[2] = "labSample";
			historyList = sampleOperationHistoryService.findSampleOperationHistoryList(
					operationStatusWrapper.getFilterablePageRequest().getFilters(),
					operationStatusWrapper.getFilterablePageRequest().getSortObject(), joins);
		} else {
			joins[2] = "labTestActual";
			historyList = testActualOperationHistoryService.findTestActualOperationHistoryList(
					operationStatusWrapper.getFilterablePageRequest().getFilters(),
					operationStatusWrapper.getFilterablePageRequest().getSortObject(), joins);
		}
		return new ResponseEntity<Object>(historyList, HttpStatus.OK);

	}

	@RequestMapping(value = "/changeVisitSampleTestStatus.srvc", method = RequestMethod.POST)
	public ResponseEntity<Object> changeVisitSampleTestStatus(@RequestBody OperationStatusWrapper operationStatusWrapper) {

		if (operationStatusWrapper.getType().equals("EmrVisit")) {
			visitService.propagateVisitStatus(operationStatusWrapper.getRid(),
					operationStatusWrapper.getOperationStatus(), operationStatusWrapper.getComment());
		} else if (operationStatusWrapper.getType().equals("LabSample")) {
			sampleService.propagateSampleStatus(operationStatusWrapper.getRid(),
					operationStatusWrapper.getOperationStatus(), operationStatusWrapper.getComment());
		} else {
			testActualService.propegateTestsStatuses(operationStatusWrapper.getVisitRid(),
					Arrays.asList(operationStatusWrapper.getRid()),
					operationStatusWrapper.getOperationStatus(), operationStatusWrapper.getComment());
		}

		return new ResponseEntity<Object>("", HttpStatus.OK);
	}

	@RequestMapping(value = "/changeSampleTestListStatus.srvc", method = RequestMethod.POST)
	public ResponseEntity<Object> changeSampleTestListStatus(@RequestBody OperationStatusWrapper operationStatusWrapper) {
		if (operationStatusWrapper.getType().equals("LabTestActual")) {
			testActualService.propegateTestsStatusesNoAuth(operationStatusWrapper.getVisitRid(),
					operationStatusWrapper.getPropagateRids(),
					operationStatusWrapper.getOperationStatus(), operationStatusWrapper.getComment());
		}

		return new ResponseEntity<Object>("", HttpStatus.OK);
	}

	@RequestMapping(value = "/getOutstandingBalanceVisits.srvc", method = RequestMethod.POST)
	public ResponseEntity<Page<EmrVisit>> getOutstandingBalanceVisits(@RequestBody FilterablePageRequest filterablePageRequest) {
		Page<EmrVisit> visits = visitService.getOutstandingBalanceVisits(filterablePageRequest);
		return new ResponseEntity<Page<EmrVisit>>(visits, HttpStatus.OK);
	}

	@RequestMapping(value = "/getVisitSampleSeparation.srvc", method = RequestMethod.POST)
	public ResponseEntity<EmrVisit> getVisitSampleSeparation(@RequestBody Long visitRid) {
		return new ResponseEntity<EmrVisit>(visitService.findVisitSampleSeparation(visitRid, Boolean.FALSE), HttpStatus.OK);
	}

	@RequestMapping(value = "/getResultReportDialogData.srvc", method = RequestMethod.POST)
	public ResponseEntity<EmrVisit> getResultReportDialogData(@RequestBody Long visitRid) {
		return new ResponseEntity<EmrVisit>(visitService.getResultReportDialogData(visitRid), HttpStatus.OK);

	}
}

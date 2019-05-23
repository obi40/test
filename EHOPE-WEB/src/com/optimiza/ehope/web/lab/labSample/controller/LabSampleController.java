package com.optimiza.ehope.web.lab.labSample.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.optimiza.core.base.helper.SearchCriterion;
import com.optimiza.core.base.helper.SearchCriterion.FilterOperator;
import com.optimiza.core.common.helper.FilterablePageRequest;
import com.optimiza.core.lkp.helper.PrintFormat;
import com.optimiza.core.lkp.service.ComTenantLanguageService;
import com.optimiza.ehope.lis.lkp.helper.OperationStatus;
import com.optimiza.ehope.lis.model.LabSample;
import com.optimiza.ehope.lis.service.LabSampleService;
import com.optimiza.ehope.web.actualTest.wrapper.UpdateSamplesWrapper;
import com.optimiza.ehope.web.util.ReportUtil;

import net.sf.jasperreports.engine.JRException;

@RestController
@RequestMapping("/services")
public class LabSampleController {

	@Autowired
	private ComTenantLanguageService comTenantLanguageService;
	@Autowired
	private LabSampleService labSampleService;

	@RequestMapping(value = "/validateSample.srvc", method = RequestMethod.POST)
	public ResponseEntity<Map<String, Object>> validateSample(@RequestBody UpdateSamplesWrapper updateSamplesWrapper) {
		return new ResponseEntity<Map<String, Object>>(labSampleService.validateSample(updateSamplesWrapper.getVisitRid(),
				updateSamplesWrapper.getSamplesTests()), HttpStatus.OK);
	}

	@RequestMapping(value = "/setSamples.srvc", method = RequestMethod.POST)
	public ResponseEntity<Object> setSamples(@RequestBody UpdateSamplesWrapper updateSamplesWrapper) {
		labSampleService.setSamples(updateSamplesWrapper.getVisitRid(),
				updateSamplesWrapper.getSamplesTests());
		return new ResponseEntity<Object>("", HttpStatus.OK);
	}

	@RequestMapping(value = "/deleteSample.srvc", method = RequestMethod.POST)
	public ResponseEntity<Object> deleteSample(@RequestBody Long sampleRid) {
		labSampleService.propagateSampleStatusNoAuth(sampleRid, OperationStatus.ABORTED, null);
		return new ResponseEntity<Object>("", HttpStatus.OK);
	}

	//TODO this should distinguish between actually sending to the machine or not
	@RequestMapping(value = "/sendToMachine.srvc", method = RequestMethod.POST)
	public ResponseEntity<Set<LabSample>> sendToMachine(@RequestBody UpdateSamplesWrapper updateSamplesWrapper) {

		Set<LabSample> labSamples = labSampleService.sendToMachine(updateSamplesWrapper.getVisitRid(),
				new ArrayList<>(updateSamplesWrapper.getSamplesTests().keySet()));

		return new ResponseEntity<Set<LabSample>>(labSamples, HttpStatus.OK);
	}

	@RequestMapping(value = "/getSamplesByVisitOrderManagement.srvc", method = RequestMethod.POST)
	public ResponseEntity<List<LabSample>> getSamplesByVisitOrderManagement(@RequestBody Long visitRid) {
		String[] joins = { "emrVisit", "lkpOperationStatus", "labTestActualSet", "lkpContainerType" };
		List<SearchCriterion> filters = new ArrayList<>();
		filters.add(new SearchCriterion("emrVisit.rid", visitRid, FilterOperator.eq));
		filters.add(new SearchCriterion("lkpOperationStatus.code", OperationStatus.ABORTED.getValue(), FilterOperator.neq));
		return new ResponseEntity<List<LabSample>>(labSampleService.findSamples(filters, null, joins), HttpStatus.OK);
	}

	@RequestMapping(value = "/getSamplePage.srvc", method = RequestMethod.POST)
	public ResponseEntity<Page<LabSample>> getSamplePage(@RequestBody FilterablePageRequest filterablePageRequest) {
		return new ResponseEntity<Page<LabSample>>(labSampleService.getSamplePage(filterablePageRequest), HttpStatus.OK);
	}

	@RequestMapping(value = "/printSample.srvc", method = RequestMethod.POST)
	public void printSample(HttpServletResponse response, @RequestBody Map<String, Object> sampleInformation) {
		ReportUtil.createMultipleJasperViews(response, "sample", labSampleService.getSampleReport(sampleInformation),
				PrintFormat.PDF.getValue());
	}

	@RequestMapping(value = "/printAllSamples.srvc", method = RequestMethod.POST)
	public void printAllSampleReport(HttpServletResponse response, @RequestBody Map<String, Object> visitInformation)
			throws JRException, IOException {
		ReportUtil.createMultipleJasperViews(response, "sample", labSampleService.getAllSampleReport(visitInformation),
				PrintFormat.PDF.getValue());
	}

	@RequestMapping(value = "/printSampleWorksheet.srvc", method = RequestMethod.POST)
	public void printSampleWorksheet(HttpServletResponse response, @RequestBody Map<String, Object> sampleInformation)
			throws JRException, IOException {

		Long sampleRid = Long.valueOf(sampleInformation.get("sampleRid").toString());
		Integer timezoneOffset = Integer.valueOf(sampleInformation.get("timezoneOffset").toString());
		String timezoneId = (String) sampleInformation.get("timezoneId");

		//ReportUtil.createMultipleJasperViews(response, labSampleService.getSampleWorksheet(sampleInformation));
		ReportUtil.createMultipleJasperViews(response, labSampleService.getSampleWorksheetReport(sampleRid),
				comTenantLanguageService.getTenantNamePrimary(), PrintFormat.PDF.getValue());
	}

	@RequestMapping(value = "/printAllSampleWorksheets.srvc", method = RequestMethod.POST)
	public void printAllSampleWorksheets(HttpServletResponse response, @RequestBody Map<String, Object> visitInformation)
			throws JRException, IOException {
		Long visitRid = Long.valueOf(visitInformation.get("visitRid").toString());
		Integer timezoneOffset = Integer.valueOf(visitInformation.get("timezoneOffset").toString());
		String timezoneId = (String) visitInformation.get("timezoneId");

		ReportUtil.createMultipleJasperViews(response, labSampleService.getAllSampleWorksheetReport(visitRid),
				comTenantLanguageService.getTenantNamePrimary(), PrintFormat.PDF.getValue());
		//ReportUtil.createMultipleJasperViews(response, labSampleService.getAllSampleWorksheets(visitInformation));
	}

}

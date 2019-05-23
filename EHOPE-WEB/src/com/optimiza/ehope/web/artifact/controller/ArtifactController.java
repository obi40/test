package com.optimiza.ehope.web.artifact.controller;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.optimiza.core.base.helper.SearchCriterion;
import com.optimiza.core.base.helper.SearchCriterion.FilterOperator;
import com.optimiza.ehope.lis.lkp.helper.OperationStatus;
import com.optimiza.ehope.lis.model.ActualTestArtifact;
import com.optimiza.ehope.lis.model.EmrVisit;
import com.optimiza.ehope.lis.model.LabTestActual;
import com.optimiza.ehope.lis.model.OrderArtifact;
import com.optimiza.ehope.lis.model.PatientArtifact;
import com.optimiza.ehope.lis.service.ActualTestArtifactService;
import com.optimiza.ehope.lis.service.EmrVisitService;
import com.optimiza.ehope.lis.service.LabTestActualService;
import com.optimiza.ehope.lis.service.OrderArtifactService;
import com.optimiza.ehope.lis.service.PatientArtifactService;

@RestController
@RequestMapping("/services")
public class ArtifactController {

	@Autowired
	private PatientArtifactService patientArtifactService;
	@Autowired
	private OrderArtifactService orderArtifactService;
	@Autowired
	private ActualTestArtifactService actualTestArtifactService;
	@Autowired
	private EmrVisitService visitService;
	@Autowired
	private LabTestActualService testActualService;

	@RequestMapping(value = "/getPatientArtifact.srvc", method = RequestMethod.POST)
	public void getPatientArtifact(HttpServletResponse response, @RequestBody Long rid) throws IOException {
		PatientArtifact artifact = patientArtifactService.findById(rid);
		InputStream inputStream = new ByteArrayInputStream(artifact.getContent());
		response.setContentType(artifact.getContentType());
		response.setHeader("Content-Disposition", "attachment; filename=" + artifact.getFileName());
		StreamUtils.copy(inputStream, response.getOutputStream());
		response.flushBuffer();
	}

	@RequestMapping(value = "/getOrderArtifact.srvc", method = RequestMethod.POST)
	public void getOrderArtifact(HttpServletResponse response, @RequestBody Long rid) throws IOException {
		OrderArtifact artifact = orderArtifactService.findById(rid);
		InputStream inputStream = new ByteArrayInputStream(artifact.getContent());
		response.setContentType(artifact.getContentType());
		response.setHeader("Content-Disposition", "attachment; filename=" + artifact.getFileName());
		StreamUtils.copy(inputStream, response.getOutputStream());
		response.flushBuffer();
	}

	@RequestMapping(value = "/getOrderArtifactDescriptions.srvc", method = RequestMethod.POST)
	public ResponseEntity<List<Object>> getOrderArtifactDescriptions(@RequestBody Long orderRid) {
		OperationStatus status = OperationStatus.valueOf(
				visitService.findOne(SearchCriterion.generateRidFilter(orderRid, FilterOperator.eq), EmrVisit.class,
						"lkpOperationStatus").getLkpOperationStatus().getCode());
		List<Object> objects = new ArrayList<>();
		if (status == OperationStatus.FINALIZED) {
			objects = orderArtifactService.getByOrderIdFinalized(orderRid);
		} else {
			objects = orderArtifactService.getByOrderIdNonFinalized(orderRid);
		}
		return new ResponseEntity<List<Object>>(objects, HttpStatus.OK);
	}

	@RequestMapping(value = "/getActualTestArtifactDescriptions.srvc", method = RequestMethod.POST)
	public ResponseEntity<List<Object>> getActualTestArtifactDescriptions(@RequestBody Long actualTestRid) {
		OperationStatus status = OperationStatus.valueOf(
				testActualService.findOne(SearchCriterion.generateRidFilter(actualTestRid, FilterOperator.eq), LabTestActual.class,
						"lkpOperationStatus").getLkpOperationStatus().getCode());
		List<Object> objects = new ArrayList<>();
		if (status == OperationStatus.FINALIZED) {
			objects = actualTestArtifactService.getByActualTestIdFinalized(actualTestRid);
		} else {
			objects = actualTestArtifactService.getByActualTestIdNonFinalized(actualTestRid);
		}
		return new ResponseEntity<List<Object>>(objects, HttpStatus.OK);
	}

	@RequestMapping(value = "/getActualTestArtifact.srvc", method = RequestMethod.POST)
	public void getActualTestArtifact(HttpServletResponse response, @RequestBody Long rid) throws IOException {
		ActualTestArtifact artifact = actualTestArtifactService.findById(rid);
		InputStream inputStream = new ByteArrayInputStream(artifact.getContent());
		response.setContentType(artifact.getContentType());
		response.setHeader("Content-Disposition", "attachment; filename=" + artifact.getFileName());
		StreamUtils.copy(inputStream, response.getOutputStream());
		response.flushBuffer();
	}

}

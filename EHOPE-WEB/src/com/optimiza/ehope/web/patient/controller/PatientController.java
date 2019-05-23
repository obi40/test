package com.optimiza.ehope.web.patient.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
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
import com.optimiza.ehope.lis.model.EmrPatientInfo;
import com.optimiza.ehope.lis.service.EmrPatientInfoService;
import com.optimiza.ehope.lis.service.PatientFingerprintService;
import com.optimiza.ehope.web.util.ImageUtil;

@RestController
@RequestMapping("/services")
public class PatientController {

	@Autowired
	private EmrPatientInfoService patientService;

	@Autowired
	private PatientFingerprintService patientFingerprintService;

	@Autowired
	ApplicationContext applicationContext;

	@RequestMapping(value = "/getPatientInfo.srvc", method = RequestMethod.POST)
	public ResponseEntity<EmrPatientInfo> getPatientInfo(@RequestBody Long rid) {
		return new ResponseEntity<EmrPatientInfo>(patientService.getPatientInfo(rid), HttpStatus.OK);
	}

	//method to retrieve all patients we are using this method in patient-lookup screen 
	@RequestMapping(value = "/getPatientList.srvc", method = RequestMethod.POST)
	public ResponseEntity<List<EmrPatientInfo>> getPatientList() {
		return new ResponseEntity<List<EmrPatientInfo>>(patientService.getPatientList(), HttpStatus.OK);
	}

	@RequestMapping(value = "/getPatientLookupPage.srvc", method = RequestMethod.POST)
	public ResponseEntity<Page<EmrPatientInfo>> getPatientLookupPage(@RequestBody FilterablePageRequest filterablePageRequest)
			throws InterruptedException, ExecutionException {

		Page<EmrPatientInfo> result = patientService.getPatientLookupPage(filterablePageRequest);
		return new ResponseEntity<Page<EmrPatientInfo>>(result, HttpStatus.OK);
	}

	@RequestMapping(value = "/findPatientRidByFingerprint.srvc", method = RequestMethod.POST)
	public ResponseEntity<Long> findPatientRidByFingerprint(@RequestBody String fingerprint) {
		Long rid = patientFingerprintService.findPatientRidByFingerprint(fingerprint);
		return new ResponseEntity<Long>(rid, HttpStatus.OK);
	}

	@RequestMapping(value = "/getPatientPage.srvc", method = RequestMethod.POST)
	public ResponseEntity<Page<EmrPatientInfo>> getPatientPage(@RequestBody FilterablePageRequest filterablePageRequest) {
		return new ResponseEntity<Page<EmrPatientInfo>>(patientService.getPatientPage(filterablePageRequest), HttpStatus.OK);
	}

	@RequestMapping(value = "/getSimilarPatientList.srvc", method = RequestMethod.POST)
	public ResponseEntity<List<EmrPatientInfo>> getSimilarPatientList(@RequestBody EmrPatientInfo patient) {
		List<EmrPatientInfo> similarPatients = patientService.getSimilarPatientList(patient);
		return new ResponseEntity<List<EmrPatientInfo>>(similarPatients, HttpStatus.OK);
	}

	@RequestMapping(value = "/addPatient.srvc", method = RequestMethod.POST, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<EmrPatientInfo> addPatient(
			@RequestParam(value = "image", required = false) MultipartFile image,
			@RequestParam(value = "artifacts", required = false) MultipartFile[] artifacts,
			@RequestParam(value = "fingerprint", required = false) String fingerprint,
			@RequestParam(value = "patient") String patient) throws IOException {
		ObjectMapper mapper = new ObjectMapper();

		EmrPatientInfo patientInfo = mapper.readValue(patient, EmrPatientInfo.class);
		if (image != null) {
			patientInfo.setImage(ImageUtil.reduceSize(image, ImageUtil.IMG_WIDTH, ImageUtil.IMG_HEIGHT));
		}
		EmrPatientInfo newPatient = patientService.createPatient(patientInfo, artifacts, fingerprint);
		return new ResponseEntity<EmrPatientInfo>(newPatient, HttpStatus.OK);
	}

	@RequestMapping(value = "/editPatient.srvc", method = RequestMethod.POST, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<EmrPatientInfo> editPatient(
			@RequestParam(value = "image", required = false) MultipartFile image,
			@RequestParam(value = "artifacts", required = false) MultipartFile[] artifacts,
			@RequestParam(value = "artifactsToDelete", required = false) String artifactsToDelete,
			@RequestParam(value = "fingerprint", required = false) String fingerprint,
			@RequestParam(value = "deleteImage") Boolean deleteImage,
			@RequestParam(value = "patient") String patient) throws IOException {
		ObjectMapper mapper = new ObjectMapper();

		EmrPatientInfo patientInfo = mapper.readValue(patient, EmrPatientInfo.class);
		if (deleteImage) {
			patientInfo.setImage(null);
		} else if (image != null) {
			patientInfo.setImage(ImageUtil.reduceSize(image, ImageUtil.IMG_WIDTH, ImageUtil.IMG_HEIGHT));
		}
		List<Long> artifactIdsToDelete = new ArrayList<Long>();
		if (!StringUtil.isEmpty(artifactsToDelete)) {
			String[] tempIds = artifactsToDelete.split(",");
			for (String ridToDelete : tempIds) {
				artifactIdsToDelete.add(Long.valueOf(ridToDelete));
			}
		}

		EmrPatientInfo savedPatient = patientService.editPatient(patientInfo, artifacts, artifactIdsToDelete, fingerprint);
		return new ResponseEntity<EmrPatientInfo>(savedPatient, HttpStatus.OK);
	}

	@RequestMapping(value = "/activatePatient.srvc", method = RequestMethod.POST)
	public ResponseEntity<EmrPatientInfo> activatePatient(@RequestBody Long patientRid) {
		EmrPatientInfo saved = patientService.activatePatient(patientRid);
		return new ResponseEntity<EmrPatientInfo>(saved, HttpStatus.OK);
	}

	@RequestMapping(value = "/deactivatePatient.srvc", method = RequestMethod.POST)
	public ResponseEntity<EmrPatientInfo> deactivatePatient(@RequestBody Long patientRid) {
		EmrPatientInfo saved = patientService.deactivatePatient(patientRid);
		return new ResponseEntity<EmrPatientInfo>(saved, HttpStatus.OK);
	}

	@RequestMapping(value = "/mergePatients.srvc", method = RequestMethod.POST)
	public ResponseEntity<Object> mergePatients(@RequestBody Map<String, String> map) {
		patientService.mergePatients(Long.parseLong(map.get("from")), Long.parseLong(map.get("to")));
		return new ResponseEntity<Object>("", HttpStatus.OK);
	}

}

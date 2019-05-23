package com.optimiza.ehope.web.doctor.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.optimiza.core.common.helper.FilterablePageRequest;
import com.optimiza.ehope.lis.model.Doctor;
import com.optimiza.ehope.lis.service.DoctorService;

@RestController
@RequestMapping("/services")
public class DoctorController {

	@Autowired
	private DoctorService doctorService;

	@RequestMapping(value = "/getDoctorPage.srvc", method = RequestMethod.POST)
	public ResponseEntity<Page<Doctor>> getDoctorPage(@RequestBody FilterablePageRequest filterablePageRequest) {
		return new ResponseEntity<Page<Doctor>>(doctorService.getDoctorsPage(filterablePageRequest), HttpStatus.OK);
	}

	@RequestMapping(value = "/getAllDoctors.srvc", method = RequestMethod.POST)
	public ResponseEntity<List<Doctor>> getAllDoctors() {
		List<Doctor> results = doctorService.findAll();
		return new ResponseEntity<List<Doctor>>(results, HttpStatus.OK);
	}

	@RequestMapping(value = "/addDoctor.srvc", method = RequestMethod.POST)
	public ResponseEntity<Doctor> addDoctor(@RequestBody Doctor doctor) {
		Doctor saved = doctorService.createDoctor(doctor);
		return new ResponseEntity<Doctor>(saved, HttpStatus.OK);
	}

	@RequestMapping(value = "/editDoctor.srvc", method = RequestMethod.POST)
	public ResponseEntity<Doctor> editDoctor(@RequestBody Doctor doctor) {
		Doctor saved = doctorService.updateDoctor(doctor);
		return new ResponseEntity<Doctor>(saved, HttpStatus.OK);
	}

	@RequestMapping(value = "/deleteDoctor.srvc", method = RequestMethod.POST)
	public ResponseEntity<String> deleteDoctor(@RequestBody Long doctorRid) {
		doctorService.deleteDoctor(doctorRid);
		return new ResponseEntity<String>("Success", HttpStatus.OK);
	}

}

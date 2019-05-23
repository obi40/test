package com.optimiza.ehope.web.antimicrobial.controller;

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
import com.optimiza.ehope.lis.model.AntiMicrobial;
import com.optimiza.ehope.lis.service.AntiMicrobialService;

@RestController
@RequestMapping("/services")
public class AntiMicrobialController {

	@Autowired
	private AntiMicrobialService antiMicrobialService;

	@RequestMapping(value = "/getAntiMicrobialList.srvc", method = RequestMethod.POST)
	public ResponseEntity<List<AntiMicrobial>> getAntiMicrobialList() {
		return new ResponseEntity<List<AntiMicrobial>>(antiMicrobialService.findAll(), HttpStatus.OK);
	}

	@RequestMapping(value = "/getAntiMicrobialPage.srvc", method = RequestMethod.POST)
	public ResponseEntity<Page<AntiMicrobial>> getAntiMicrobialPage(@RequestBody FilterablePageRequest filterablePageRequest) {
		return new ResponseEntity<Page<AntiMicrobial>>(antiMicrobialService.findAntiMicrobialPage(filterablePageRequest), HttpStatus.OK);
	}

	@RequestMapping(value = "/createAntiMicrobial.srvc", method = RequestMethod.POST)
	public ResponseEntity<AntiMicrobial> createAntiMicrobial(@RequestBody AntiMicrobial antiMicrobial) {
		return new ResponseEntity<AntiMicrobial>(
				antiMicrobialService.findOneAntiMicrobial(antiMicrobialService.addAntiMicrobial(antiMicrobial).getRid()),
				HttpStatus.OK);
	}

	@RequestMapping(value = "/updateAntiMicrobial.srvc", method = RequestMethod.POST)
	public ResponseEntity<AntiMicrobial> updateAntiMicrobial(@RequestBody AntiMicrobial antiMicrobial) {
		return new ResponseEntity<AntiMicrobial>(
				antiMicrobialService.findOneAntiMicrobial(antiMicrobialService.editAntiMicrobial(antiMicrobial).getRid()),
				HttpStatus.OK);
	}

	@RequestMapping(value = "/deleteAntiMicrobial.srvc", method = RequestMethod.POST)
	public ResponseEntity<String> deleteSection(@RequestBody Long antiMicrobialId) {
		antiMicrobialService.deleteAntiMicrobial(antiMicrobialId);
		return new ResponseEntity<String>("Success", HttpStatus.OK);
	}

}

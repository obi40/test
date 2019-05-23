package com.optimiza.ehope.web.organism.controller;

import java.io.IOException;
import java.util.List;

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

import com.optimiza.core.common.helper.FilterablePageRequest;
import com.optimiza.ehope.lis.model.Organism;
import com.optimiza.ehope.lis.service.OrganismService;

@RestController
@RequestMapping("/services")
public class OrganismController {

	@Autowired
	private OrganismService organismService;

	@RequestMapping(value = "/getOrganismList.srvc", method = RequestMethod.POST)
	public ResponseEntity<List<Organism>> getOrganismList() {
		return new ResponseEntity<List<Organism>>(organismService.findAll(), HttpStatus.OK);
	}

	@RequestMapping(value = "/getOrganismPage.srvc", method = RequestMethod.POST)
	public ResponseEntity<Page<Organism>> getOrganismPage(@RequestBody FilterablePageRequest filterablePageRequest) {
		return new ResponseEntity<Page<Organism>>(organismService.findOrganismPage(filterablePageRequest), HttpStatus.OK);
	}

	@RequestMapping(value = "/createOrganism.srvc", method = RequestMethod.POST)
	public ResponseEntity<Organism> createOrganism(@RequestBody Organism organism) {
		return new ResponseEntity<Organism>(organismService.findOneOrganism(organismService.addOrganism(organism).getRid()),
				HttpStatus.OK);
	}

	@RequestMapping(value = "/updateOrganism.srvc", method = RequestMethod.POST)
	public ResponseEntity<Organism> updateOrganism(@RequestBody Organism organism) {
		return new ResponseEntity<Organism>(organismService.findOneOrganism(organismService.editOrganism(organism).getRid()),
				HttpStatus.OK);
	}

	@RequestMapping(value = "/deleteOrganism.srvc", method = RequestMethod.POST)
	public ResponseEntity<String> deleteSection(@RequestBody Long organismId) {
		organismService.deleteOrganism(organismId);
		return new ResponseEntity<String>("Success", HttpStatus.OK);
	}

	@RequestMapping(value = "/importOrganisms.srvc", method = RequestMethod.POST, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<String> importOrganisms(@RequestParam(value = "organisms", required = true) MultipartFile excel)
			throws IOException {

		organismService.importOrganisms(excel);

		return new ResponseEntity<String>("Success", HttpStatus.OK);
	}

}

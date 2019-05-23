package com.optimiza.ehope.web.section.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.optimiza.core.base.helper.SearchCriterion;
import com.optimiza.core.common.helper.FilterablePageRequest;
import com.optimiza.ehope.lis.model.LabSection;
import com.optimiza.ehope.lis.service.LabSectionService;

@RestController
@RequestMapping("/services")
public class LabSectionController {

	@Autowired
	private LabSectionService sectionService;

	@RequestMapping(value = "/getAllSections.srvc", method = RequestMethod.POST)
	public ResponseEntity<List<LabSection>> getAllSections() {
		List<LabSection> sectionList = sectionService.find(new ArrayList<SearchCriterion>(), LabSection.class,
				new Sort(Direction.ASC, "rid"));
		return new ResponseEntity<List<LabSection>>(sectionList, HttpStatus.OK);
	}

	@RequestMapping(value = "/getSectionsWithType.srvc", method = RequestMethod.POST)
	public ResponseEntity<List<LabSection>> getSectionsWithType() {
		List<LabSection> sectionList = sectionService.find(new ArrayList<SearchCriterion>(), LabSection.class,
				new Sort(Direction.ASC, "rid"), "type");
		return new ResponseEntity<List<LabSection>>(sectionList, HttpStatus.OK);
	}

	@RequestMapping(value = "/getFilteredSectionList.srvc", method = RequestMethod.POST)
	public ResponseEntity<List<LabSection>> getFilteredSectionList(@RequestBody String searchQuery) {
		List<LabSection> sectionList = sectionService.getSectionList(searchQuery);
		return new ResponseEntity<List<LabSection>>(sectionList, HttpStatus.OK);
	}

	@RequestMapping(value = "/getSectionPage.srvc", method = RequestMethod.POST)
	public ResponseEntity<Page<LabSection>> getSectionPage(@RequestBody FilterablePageRequest filterablePageRequest) {
		return new ResponseEntity<Page<LabSection>>(sectionService.findSectionPage(filterablePageRequest), HttpStatus.OK);
	}

	@RequestMapping(value = "/createSection.srvc", method = RequestMethod.POST)
	public ResponseEntity<LabSection> createSection(@RequestBody LabSection section) {
		return new ResponseEntity<LabSection>(sectionService.addSection(section), HttpStatus.OK);
	}

	@RequestMapping(value = "/updateSection.srvc", method = RequestMethod.POST)
	public ResponseEntity<LabSection> updateSection(@RequestBody LabSection section) {
		return new ResponseEntity<LabSection>(sectionService.editSection(section), HttpStatus.OK);
	}

	@RequestMapping(value = "/deleteSection.srvc", method = RequestMethod.POST)
	public ResponseEntity<String> deleteSection(@RequestBody Long sectionId) {
		sectionService.deleteSection(sectionId);
		return new ResponseEntity<String>("Success", HttpStatus.OK);
	}

}

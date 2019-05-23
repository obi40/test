package com.optimiza.ehope.web.test.group.controller;

import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.optimiza.core.common.helper.FilterablePageRequest;
import com.optimiza.ehope.lis.model.TestGroup;
import com.optimiza.ehope.lis.model.TestGroupDetail;
import com.optimiza.ehope.lis.service.TestGroupDetailService;
import com.optimiza.ehope.lis.service.TestGroupService;

@RestController
@RequestMapping("/services")
public class TestGroupController {

	@Autowired
	private TestGroupService groupService;
	@Autowired
	private TestGroupDetailService groupDetailsService;

	@RequestMapping(value = "/getTestGroupsPage.srvc", method = RequestMethod.POST)
	public ResponseEntity<Page<TestGroup>> getTestGroupsPage(@RequestBody FilterablePageRequest filterablePageRequest) {
		return new ResponseEntity<Page<TestGroup>>(groupService.getTestGroupsPage(filterablePageRequest), HttpStatus.OK);
	}

	@RequestMapping(value = "/getTestGroups.srvc", method = RequestMethod.POST)
	public ResponseEntity<Set<TestGroup>> getTestGroups() {
		return new ResponseEntity<Set<TestGroup>>(groupService.getTestGroups(), HttpStatus.OK);
	}

	@RequestMapping(value = "/getTestGroupsWithDestinations.srvc", method = RequestMethod.POST)
	public ResponseEntity<Set<TestGroup>> getTestGroupsWithDestinations() {
		return new ResponseEntity<Set<TestGroup>>(groupService.getTestGroupsWithDestinations(), HttpStatus.OK);
	}

	@RequestMapping(value = "/createTestGroup.srvc", method = RequestMethod.POST)
	public ResponseEntity<TestGroup> createTestGroup(@RequestBody TestGroup testGroup) {
		return new ResponseEntity<TestGroup>(groupService.createTestGroup(testGroup), HttpStatus.OK);
	}

	@RequestMapping(value = "/updateTestGroup.srvc", method = RequestMethod.POST)
	public ResponseEntity<TestGroup> updateTestGroup(@RequestBody TestGroup testGroup) {
		return new ResponseEntity<TestGroup>(groupService.updateTestGroup(testGroup), HttpStatus.OK);
	}

	@RequestMapping(value = "/deleteTestGroup.srvc", method = RequestMethod.POST)
	public ResponseEntity<Object> deleteTestGroup(@RequestBody TestGroup testGroup) {
		groupService.deleteTestGroup(testGroup);
		return new ResponseEntity<Object>("", HttpStatus.OK);
	}

	@RequestMapping(value = "/getTestGroupDetails.srvc", method = RequestMethod.POST)
	public ResponseEntity<List<TestGroupDetail>> getTestGroupDetails(@RequestBody FilterablePageRequest filterablePageRequest) {
		return new ResponseEntity<List<TestGroupDetail>>(groupDetailsService.getTestGroupDetails(filterablePageRequest), HttpStatus.OK);
	}

}

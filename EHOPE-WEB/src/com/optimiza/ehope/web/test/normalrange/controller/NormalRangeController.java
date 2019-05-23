package com.optimiza.ehope.web.test.normalrange.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.optimiza.core.common.helper.FilterablePageRequest;
import com.optimiza.ehope.lis.model.TestNormalRange;
import com.optimiza.ehope.lis.service.TestNormalRangeService;

@RestController
@RequestMapping("/services")
public class NormalRangeController {

	@Autowired
	private TestNormalRangeService normalRangeService;

	@RequestMapping(value = "/getNormalRangeList.srvc", method = RequestMethod.POST)
	public ResponseEntity<Page<TestNormalRange>> getNormalRangeList(@RequestBody FilterablePageRequest filterablePageRequest) {
		Page<TestNormalRange> normalRanges = normalRangeService.find(filterablePageRequest.getFilters(),
				filterablePageRequest.getPageRequest(), TestNormalRange.class,
				"ageFromUnit", "ageToUnit", "ageUnit", "sex", "signum", "unit");

		return new ResponseEntity<Page<TestNormalRange>>(normalRanges, HttpStatus.OK);
	}

	@RequestMapping(value = "/addNormalRange.srvc", method = RequestMethod.POST)
	public ResponseEntity<TestNormalRange> addNormalRange(@RequestBody TestNormalRange normalRange) {
		normalRange = normalRangeService.addNormalRange(normalRange);

		return new ResponseEntity<TestNormalRange>(normalRange, HttpStatus.OK);
	}

	@RequestMapping(value = "/editNormalRange.srvc", method = RequestMethod.POST)
	public ResponseEntity<TestNormalRange> editNormalRange(@RequestBody TestNormalRange normalRange) {
		normalRange = normalRangeService.editNormalRange(normalRange);

		return new ResponseEntity<TestNormalRange>(normalRange, HttpStatus.OK);
	}

	@RequestMapping(value = "/deleteNormalRange.srvc", method = RequestMethod.POST)
	public ResponseEntity<TestNormalRange> deleteNormalRange(@RequestBody TestNormalRange normalRange) {
		normalRangeService.deleteNormalRange(normalRange);

		return new ResponseEntity<TestNormalRange>(normalRange, HttpStatus.OK);
	}

}

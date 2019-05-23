package com.optimiza.ehope.lis.service;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.optimiza.core.base.helper.SearchCriterion;
import com.optimiza.core.base.helper.SearchCriterion.FilterOperator;
import com.optimiza.core.base.service.GenericService;
import com.optimiza.ehope.lis.model.ActualResultNormalRange;
import com.optimiza.ehope.lis.model.LabTestActual;
import com.optimiza.ehope.lis.repo.ActualResultNormalRangeRepo;

/**
 * ActualResultNormalRangeService.java
 * 
 * @author Murad Poladian <mpoladian@optimizasolutions.com>
 * @since Apr/14/2019
 * 
 */
@Service("ActualResultNormalRangeService")
public class ActualResultNormalRangeService extends GenericService<ActualResultNormalRange, ActualResultNormalRangeRepo> {

	@Autowired
	private ActualResultNormalRangeRepo repo;

	@Override
	protected ActualResultNormalRangeRepo getRepository() {
		return repo;
	}

	public void deleteAllByActualTest(LabTestActual labTestActual) {
		List<ActualResultNormalRange> actualResultNormalRanges = repo.find(
				Arrays.asList(new SearchCriterion("actualResult.labTestActual", labTestActual, FilterOperator.eq)),
				ActualResultNormalRange.class);
		repo.delete(actualResultNormalRanges);
	}

	public ActualResultNormalRange saveActualResultNormalRange(ActualResultNormalRange actualResultNormalRange) {
		return repo.save(actualResultNormalRange);
	}

}

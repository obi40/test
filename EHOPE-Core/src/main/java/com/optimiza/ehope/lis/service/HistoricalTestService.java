package com.optimiza.ehope.lis.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.optimiza.core.base.service.GenericService;
import com.optimiza.ehope.lis.model.HistoricalTest;
import com.optimiza.ehope.lis.repo.HistoricalTestRepo;

/**
 * HistoricalTestService.java
 * 
 * @author Murad Poladian <mpoladian@optimizasolutions.com>
 * @since Nov/15/2018
 * 
 */
@Service("HistoricalTestService")
public class HistoricalTestService extends GenericService<HistoricalTest, HistoricalTestRepo> {

	@Autowired
	private HistoricalTestRepo repo;

	@Override
	protected HistoricalTestRepo getRepository() {
		return repo;
	}

	public HistoricalTest createHistoricalTest(HistoricalTest historicalTest) {
		return repo.save(historicalTest);
	}

	public HistoricalTest updateHistoricalTest(HistoricalTest historicalTest) {
		return repo.save(historicalTest);
	}

}

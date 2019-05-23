package com.optimiza.ehope.lis.repo;

import org.springframework.stereotype.Repository;

import com.optimiza.core.base.repo.GenericRepository;
import com.optimiza.ehope.lis.model.HistoricalTest;

/**
 * HistoricalTestRepo.java
 * 
 * @author Murad Poladian <mpoladian@optimizasolutions.com>
 * @since Nov/15/2018
 * 
 */
@Repository("HistoricalTestRepo")
public interface HistoricalTestRepo extends GenericRepository<HistoricalTest> {

}

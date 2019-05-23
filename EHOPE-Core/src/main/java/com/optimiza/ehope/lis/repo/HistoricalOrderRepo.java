package com.optimiza.ehope.lis.repo;

import org.springframework.stereotype.Repository;

import com.optimiza.core.base.repo.GenericRepository;
import com.optimiza.ehope.lis.model.HistoricalOrder;

/**
 * HistoricalOrderRepo.java
 * 
 * @author Murad Poladian <mpoladian@optimizasolutions.com>
 * @since Nov/11/2018
 * 
 */
@Repository("HistoricalOrderRepo")
public interface HistoricalOrderRepo extends GenericRepository<HistoricalOrder> {

}

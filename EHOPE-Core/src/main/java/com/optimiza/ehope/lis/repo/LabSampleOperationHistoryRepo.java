package com.optimiza.ehope.lis.repo;

import org.springframework.stereotype.Repository;

import com.optimiza.core.base.repo.GenericRepository;
import com.optimiza.ehope.lis.model.LabSample;
import com.optimiza.ehope.lis.model.LabSampleOperationHistory;

/**
 * LabSampleOperationHistoryRepo.java
 * 
 * @author Abdullah Imran <aImran@optimizasolutions.com>
 * @since May/07/2018
 **/

@Repository("LabSampleOperationHistoryRepo")
public interface LabSampleOperationHistoryRepo extends GenericRepository<LabSampleOperationHistory> {

	void deleteAllByLabSample(LabSample labSample);
}

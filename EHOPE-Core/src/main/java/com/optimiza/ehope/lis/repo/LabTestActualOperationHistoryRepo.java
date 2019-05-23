package com.optimiza.ehope.lis.repo;

import org.springframework.stereotype.Repository;

import com.optimiza.core.base.repo.GenericRepository;
import com.optimiza.ehope.lis.model.LabTestActual;
import com.optimiza.ehope.lis.model.LabTestActualOperationHistory;

/**
 * LabTestActualOperationHistoryRepo.java
 * 
 * @author Abdullah Imran <aImran@optimizasolutions.com>
 * @since May/07/2018
 **/

@Repository("LabTestActualOperationHistoryRepo")
public interface LabTestActualOperationHistoryRepo extends GenericRepository<LabTestActualOperationHistory> {

	void deleteAllByLabTestActual(LabTestActual labTestActual);
}

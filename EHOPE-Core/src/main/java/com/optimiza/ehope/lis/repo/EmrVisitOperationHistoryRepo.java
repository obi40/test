package com.optimiza.ehope.lis.repo;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.optimiza.core.base.repo.GenericRepository;
import com.optimiza.ehope.lis.model.EmrVisit;
import com.optimiza.ehope.lis.model.EmrVisitOperationHistory;

/**
 * EmrVisitOperationHistoryRepo.java
 * 
 * @author Abdullah Imran <aImran@optimizasolutions.com>
 * @since Apr/29/2018
 **/

@Repository("EmrVisitOperationHistoryRepo")
public interface EmrVisitOperationHistoryRepo extends GenericRepository<EmrVisitOperationHistory> {

	List<EmrVisitOperationHistory> findByEmrVisit(EmrVisit emrVisit);

	void deleteAllByEmrVisit(EmrVisit visit);
}

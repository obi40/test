package com.optimiza.ehope.lis.repo;

import org.springframework.stereotype.Repository;

import com.optimiza.core.base.repo.GenericRepository;
import com.optimiza.ehope.lis.model.EmrVisit;
import com.optimiza.ehope.lis.model.EmrVisitGroup;
import com.optimiza.ehope.lis.model.TestGroup;

/**
 * EmrVisitGroupRepo.java
 * 
 * @author Abdullah Imran <aImran@optimizasolutions.com>
 * @since Feb/19/2019
 **/

@Repository("EmrVisitGroupRepo")
public interface EmrVisitGroupRepo extends GenericRepository<EmrVisitGroup> {

	void deleteAllByTestGroup(TestGroup testGroup);

	void deleteAllByEmrVisit(EmrVisit emrVisit);

}

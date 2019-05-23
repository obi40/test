package com.optimiza.ehope.lis.repo;

import org.springframework.stereotype.Repository;

import com.optimiza.core.base.repo.GenericRepository;
import com.optimiza.ehope.lis.model.LabBranchSeparationFactor;

/**
 * LabBranchSeparationFactorRepo.java
 * 
 * @author Abdullah Imran <aImran@optimizasolutions.com>
 * @since Jan/07/2018
 **/

@Repository("LabBranchSeparationFactorRepo")
public interface LabBranchSeparationFactorRepo extends GenericRepository<LabBranchSeparationFactor> {

	void deleteAllByBranchId(Long branchRid);
}

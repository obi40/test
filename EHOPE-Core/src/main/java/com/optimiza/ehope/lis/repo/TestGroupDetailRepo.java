package com.optimiza.ehope.lis.repo;

import org.springframework.stereotype.Repository;

import com.optimiza.core.base.repo.GenericRepository;
import com.optimiza.ehope.lis.model.TestGroup;
import com.optimiza.ehope.lis.model.TestGroupDetail;

/**
 * TestGroupDetailRepo.java
 * 
 * @author Abdullah Imran <aImran@optimizasolutions.com>
 * @since Jan/24/2019
 **/

@Repository("TestGroupDetailRepo")
public interface TestGroupDetailRepo extends GenericRepository<TestGroupDetail> {

	void deleteAllByGroup(TestGroup group);

}

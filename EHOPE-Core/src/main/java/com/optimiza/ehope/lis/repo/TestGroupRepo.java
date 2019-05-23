package com.optimiza.ehope.lis.repo;

import org.springframework.stereotype.Repository;

import com.optimiza.core.base.repo.GenericRepository;
import com.optimiza.ehope.lis.model.TestGroup;

/**
 * TestGroupRepo.java
 * 
 * @author Abdullah Imran <aImran@optimizasolutions.com>
 * @since Dec/11/2018
 **/

@Repository("TestGroupRepo")
public interface TestGroupRepo extends GenericRepository<TestGroup> {

}

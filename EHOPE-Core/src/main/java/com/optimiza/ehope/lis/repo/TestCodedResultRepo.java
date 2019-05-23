package com.optimiza.ehope.lis.repo;

import org.springframework.stereotype.Repository;

import com.optimiza.core.base.repo.GenericRepository;
import com.optimiza.ehope.lis.model.TestCodedResult;

/**
 * TestCodedResultRepo.java
 * 
 * @author Murad Poladian <mpoladian@optimizasolutions.com>
 * @since Jul/11/2018
 * 
 */
@Repository("TestCodedResultRepo")
public interface TestCodedResultRepo extends GenericRepository<TestCodedResult> {

}

package com.optimiza.ehope.lis.repo;

import org.springframework.stereotype.Repository;

import com.optimiza.core.base.repo.GenericRepository;
import com.optimiza.ehope.lis.model.Workbench;

/**
 * WorkbenchRepo.java
 * 
 * @author Murad Poladian <mpoladian@optimizasolutions.com>
 * @since Sep/18/2018
 **/
@Repository("WorkbenchRepo")
public interface WorkbenchRepo extends GenericRepository<Workbench> {

}

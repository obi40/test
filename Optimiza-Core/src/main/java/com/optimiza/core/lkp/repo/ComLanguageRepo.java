package com.optimiza.core.lkp.repo;

import org.springframework.stereotype.Repository;

import com.optimiza.core.base.repo.GenericRepository;
import com.optimiza.core.lkp.model.ComLanguage;

/**
 * ComLanguageRepo.java
 * 
 * @author Abdullah Imran <aImran@optimizasolutions.com>
 * @since Sep/27/2017
 **/

@Repository("ComLanguageRepo")
public interface ComLanguageRepo extends GenericRepository<ComLanguage> {

}

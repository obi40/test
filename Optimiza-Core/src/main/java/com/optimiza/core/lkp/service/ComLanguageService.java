package com.optimiza.core.lkp.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.optimiza.core.base.service.GenericService;
import com.optimiza.core.lkp.model.ComLanguage;
import com.optimiza.core.lkp.repo.ComLanguageRepo;

/**
 * ComLanguageService.java
 * 
 * @author Abdullah Imran <aImran@optimizasolutions.com>
 * @since Sep/27/2017
 **/

@Service("ComLanguageService")
public class ComLanguageService extends GenericService<ComLanguage, ComLanguageRepo> {

	@Autowired
	private ComLanguageRepo comLanguageRepo;

	@Override
	protected ComLanguageRepo getRepository() {
		return this.comLanguageRepo;
	}

}

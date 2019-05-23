package com.optimiza.core.system.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.optimiza.core.base.service.GenericService;
import com.optimiza.core.system.model.SysModule;
import com.optimiza.core.system.repo.SysModuleRepo;

/**
 * SysModuleService.java
 * 
 * @author Abdullah Imran <aImran@optimizasolutions.com>
 * @since Dec/05/2017
 **/

@Service("SysModuleService")
public class SysModuleService extends GenericService<SysModule, SysModuleRepo> {

	@Autowired
	private SysModuleRepo repo;

	@Override
	protected SysModuleRepo getRepository() {
		return repo;
	}

	public SysModule createSysModule(SysModule sysModule) {
		return getRepository().save(sysModule);
	}

	public void deleteSysModule(SysModule sysModule) {
		getRepository().delete(sysModule);
	}

	public SysModule findSysModuleById(Long id) {
		return getRepository().findOne(id);
	}

	public SysModule updateSysModule(SysModule sysModule) {
		return getRepository().save(sysModule);
	}

}

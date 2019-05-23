package com.optimiza.core.system.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.optimiza.core.base.service.GenericService;
import com.optimiza.core.system.model.SysPage;
import com.optimiza.core.system.repo.SysPageRepo;

/**
 * SysPageService.java
 * 
 * @author Abdullah Imran <aImran@optimizasolutions.com>
 * @since Dec/05/2017
 **/

@Service("SysPageService")
public class SysPageService extends GenericService<SysPage, SysPageRepo> {

	@Autowired
	private SysPageRepo repo;

	@Override
	protected SysPageRepo getRepository() {
		return repo;
	}

	public SysPage createSysPage(SysPage sysPage) {
		return getRepository().save(sysPage);
	}

	public void deleteSysPage(SysPage sysPage) {
		getRepository().delete(sysPage);
	}

	public SysPage findSysPageById(Long id) {
		return getRepository().findOne(id);
	}

	public SysPage updateSysPage(SysPage sysPage) {
		return getRepository().save(sysPage);
	}

}

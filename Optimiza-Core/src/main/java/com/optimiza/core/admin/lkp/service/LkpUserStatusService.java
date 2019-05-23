package com.optimiza.core.admin.lkp.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.optimiza.core.admin.lkp.model.LkpUserStatus;
import com.optimiza.core.admin.lkp.repo.LkpUserStatusRepo;
import com.optimiza.core.base.service.GenericService;

/**
 * LkpUserStatusService.java
 * 
 * @author Abdullah Imran <aImran@optimizasolutions.com>
 * @since Sep/27/2017
 **/

@Service("LkpUserStatusService")
public class LkpUserStatusService extends GenericService<LkpUserStatus, LkpUserStatusRepo> {

	@Autowired
	private LkpUserStatusRepo lkpUserStatusRepo;

	public LkpUserStatus createLkpUserStatus(LkpUserStatus lkpUserStatus) {
		return getRepository().save(lkpUserStatus);
	}

	public void deleteLkpUserStatus(LkpUserStatus lkpUserStatus) {
		getRepository().delete(lkpUserStatus);
	}

	public LkpUserStatus findLkpUserStatusById(Long id) {
		return getRepository().findOne(id);
	}

	public LkpUserStatus updateLkpUserStatus(LkpUserStatus lkpUserStatus) {
		return getRepository().save(lkpUserStatus);
	}

	@Override
	protected LkpUserStatusRepo getRepository() {
		return getLkpUserStatusRepo();
	}

	public LkpUserStatusRepo getLkpUserStatusRepo() {
		return lkpUserStatusRepo;
	}

	public void setLkpUserStatusRepo(LkpUserStatusRepo lkpUserStatusRepo) {
		this.lkpUserStatusRepo = lkpUserStatusRepo;
	}

}

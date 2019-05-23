package com.optimiza.core.admin.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.optimiza.core.admin.model.SecRight;
import com.optimiza.core.admin.repo.SecRightRepo;
import com.optimiza.core.base.service.GenericService;

/**
 * SecRightService.java
 * 
 * @author Abdullah Imran <aImran@optimizasolutions.com>
 * @since Sep/27/2017
 **/

@Service("SecRightService")
public class SecRightService extends GenericService<SecRight, SecRightRepo> {

	@Autowired
	private SecRightRepo rightRepo;

	@Override
	protected SecRightRepo getRepository() {
		return rightRepo;
	}

	public SecRight createRight(SecRight secRight) {
		return getRepository().save(secRight);
	}

	public List<SecRight> findRights() {
		return getRepository().find(new ArrayList<>(), SecRight.class, "sysPage");
	}

}

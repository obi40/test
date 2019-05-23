package com.optimiza.ehope.lis.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.optimiza.core.base.helper.SearchCriterion;
import com.optimiza.core.base.service.GenericService;
import com.optimiza.ehope.lis.helper.SMSKey;
import com.optimiza.ehope.lis.model.ConfTenantSMS;
import com.optimiza.ehope.lis.repo.ConfTenantSMSRepo;
import com.optimiza.ehope.lis.util.SMSUtil;

/**
 * ConfTenantSMSService.java
 * 
 * @author Abdullah Imran <aImran@optimizasolutions.com>
 * @since Nov/27/2018
 **/

@Service("ConfTenantSMSService")
public class ConfTenantSMSService extends GenericService<ConfTenantSMS, ConfTenantSMSRepo> {

	@Autowired
	private ConfTenantSMSRepo confTenantSMSRepo;
	@Autowired
	private EntityManager entityManager;
	@Autowired
	private SMSUtil smsUtil;

	//TODO: RIGHTS?
	public List<ConfTenantSMS> getSMSConfig(List<SearchCriterion> filters, Sort sort, String... joins) {
		return getRepository().find(filters, ConfTenantSMS.class, sort, joins);
	}

	//TODO: RIGHTS?
	public List<ConfTenantSMS> setSMSConfig(List<ConfTenantSMS> confTenantSMSList) {
		getRepository().deleteAll();
		entityManager.flush();
		return getRepository().save(confTenantSMSList);
	}

	public void testSMSConfig(String message, String mobileNumber) {
		Map<SMSKey, String> smsValues = new HashMap<>();
		smsValues.put(SMSKey.MOBILE, mobileNumber);
		smsValues.put(SMSKey.MESSAGE, message);
		smsUtil.send(smsValues);
	}

	@Override
	protected ConfTenantSMSRepo getRepository() {
		return this.confTenantSMSRepo;
	}

}

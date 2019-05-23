package com.optimiza.ehope.lis.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import com.optimiza.core.base.service.GenericService;
import com.optimiza.ehope.lis.helper.EhopeRights;
import com.optimiza.ehope.lis.model.InsNetwork;
import com.optimiza.ehope.lis.repo.InsNetworkRepo;

@Service("InsNetworkService")
public class InsNetworkService extends GenericService<InsNetwork, InsNetworkRepo> {

	@Autowired
	private InsNetworkRepo repo;

	@Override
	protected InsNetworkRepo getRepository() {
		return repo;
	}

	@PreAuthorize("hasAuthority('" + EhopeRights.VIEW_INSURANCE_NETWORK + "')")
	public List<InsNetwork> findInsNetworks() {
		return getRepository().findAll();
	}

	@PreAuthorize("hasAuthority('" + EhopeRights.ADD_INS_NETWORK + "')")
	public InsNetwork createInsNetwork(InsNetwork insNetwork) {
		return getRepository().save(insNetwork);
	}

	@PreAuthorize("hasAuthority('" + EhopeRights.DEL_INS_NETWORK + "')")
	public void deleteInsNetwork(InsNetwork insNetwork) {
		getRepository().delete(insNetwork);
	}

	@PreAuthorize("hasAuthority('" + EhopeRights.UPD_INS_NETWORK + "')")
	public InsNetwork updateInsNetwork(InsNetwork insNetwork) {
		return getRepository().save(insNetwork);
	}

}

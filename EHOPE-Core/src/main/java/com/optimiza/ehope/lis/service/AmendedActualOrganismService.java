package com.optimiza.ehope.lis.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.optimiza.core.base.service.GenericService;
import com.optimiza.ehope.lis.model.AmendedActualOrganism;
import com.optimiza.ehope.lis.repo.AmendedActualOrganismRepo;

@Service("AmendedActualOrganismService")
public class AmendedActualOrganismService extends GenericService<AmendedActualOrganism, AmendedActualOrganismRepo> {

	@Autowired
	private AmendedActualOrganismRepo repo;

	@Override
	protected AmendedActualOrganismRepo getRepository() {
		return repo;
	}

	public AmendedActualOrganism createAmendedActualOrganism(AmendedActualOrganism amendedActualOrganism) {
		return repo.save(amendedActualOrganism);
	}

}

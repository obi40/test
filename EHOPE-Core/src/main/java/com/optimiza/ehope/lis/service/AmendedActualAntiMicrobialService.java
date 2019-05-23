package com.optimiza.ehope.lis.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.optimiza.core.base.service.GenericService;
import com.optimiza.ehope.lis.model.AmendedActualAntiMicrobial;
import com.optimiza.ehope.lis.repo.AmendedActualAntiMicrobialRepo;

@Service("AmendedActualAntiMicrobialService")
public class AmendedActualAntiMicrobialService extends GenericService<AmendedActualAntiMicrobial, AmendedActualAntiMicrobialRepo> {

	@Autowired
	private AmendedActualAntiMicrobialRepo repo;

	@Override
	protected AmendedActualAntiMicrobialRepo getRepository() {
		return repo;
	}

	public AmendedActualAntiMicrobial createAmendedActualAntiMicrobial(AmendedActualAntiMicrobial amendedActualAntiMicrobial) {
		return repo.save(amendedActualAntiMicrobial);
	}

}

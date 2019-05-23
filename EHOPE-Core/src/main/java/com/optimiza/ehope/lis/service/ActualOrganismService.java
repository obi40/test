package com.optimiza.ehope.lis.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.optimiza.core.base.service.GenericService;
import com.optimiza.ehope.lis.model.ActualOrganism;
import com.optimiza.ehope.lis.model.LabTestActualResult;
import com.optimiza.ehope.lis.repo.ActualOrganismRepo;

/**
 * ActualOrganismService.java
 * 
 * @author Murad Poladian <mpoladian@optimizasolutions.com>
 * @since Dec/10/2018
 * 
 */
@Service("ActualOrganismService")
public class ActualOrganismService extends GenericService<ActualOrganism, ActualOrganismRepo> {

	@Autowired
	private ActualOrganismRepo repo;

	@Override
	protected ActualOrganismRepo getRepository() {
		return repo;
	}

	public List<ActualOrganism> saveActualOrganisms(List<ActualOrganism> actualOrganisms, LabTestActualResult actualResult) {
		List<ActualOrganism> savedActualOrganisms = new ArrayList<ActualOrganism>();
		for (ActualOrganism actualOrganism : actualOrganisms) {
			if (actualOrganism.getMarkedForDeletion()) {
				if (actualOrganism.getRid() != null) {
					repo.delete(actualOrganism);
				}
			} else {
				actualOrganism.setActualResult(actualResult);
				savedActualOrganisms.add(repo.save(actualOrganism));
			}
		}
		actualResult.setActualOrganisms(null);
		return savedActualOrganisms;
	}

	public void deleteActualOrganisms(List<ActualOrganism> actualOrganisms, LabTestActualResult actualResult) {
		for (ActualOrganism actualOrganism : actualOrganisms) {
			if (actualOrganism.getRid() != null) {
				repo.delete(actualOrganism);
			}
		}
		actualResult.setActualOrganisms(null);
	}

}
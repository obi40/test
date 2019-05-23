package com.optimiza.ehope.lis.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.optimiza.core.base.service.GenericService;
import com.optimiza.ehope.lis.model.ActualAntiMicrobial;
import com.optimiza.ehope.lis.model.LabTestActualResult;
import com.optimiza.ehope.lis.repo.ActualAntiMicrobialRepo;

/**
 * ActualAntiMicrobialService.java
 * 
 * @author Murad Poladian <mpoladian@optimizasolutions.com>
 * @since Dec/10/2018
 * 
 */
@Service("ActualAntiMicrobialService")
public class ActualAntiMicrobialService extends GenericService<ActualAntiMicrobial, ActualAntiMicrobialRepo> {

	@Autowired
	private ActualAntiMicrobialRepo repo;

	@Override
	protected ActualAntiMicrobialRepo getRepository() {
		return repo;
	}

	public void saveActualAntiMicrobials(List<ActualAntiMicrobial> actualAntiMicrobialList, LabTestActualResult actualResult) {
		for (ActualAntiMicrobial actualAntiMicrobial : actualAntiMicrobialList) {
			if (actualAntiMicrobial.getOrganismSensitivity() == null) {
				if (actualAntiMicrobial.getRid() != null) {
					repo.delete(actualAntiMicrobial);
				}
			} else {
				actualAntiMicrobial.setActualResult(actualResult);
				repo.save(actualAntiMicrobial);
			}
		}
		actualResult.setActualAntiMicrobials(null);
	}

	public void deleteActualAntiMicrobials(List<ActualAntiMicrobial> actualAntiMicrobialList, LabTestActualResult actualResult) {
		for (ActualAntiMicrobial actualAntiMicrobial : actualAntiMicrobialList) {
			if (actualAntiMicrobial.getRid() != null) {
				repo.delete(actualAntiMicrobial);
			}
		}
		actualResult.setActualAntiMicrobials(null);
	}

}
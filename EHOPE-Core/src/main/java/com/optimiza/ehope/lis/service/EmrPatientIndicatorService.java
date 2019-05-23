package com.optimiza.ehope.lis.service;

import org.springframework.beans.factory.annotation.Autowired;

import com.optimiza.core.base.service.GenericService;
import com.optimiza.ehope.lis.model.EmrPatientIndicator;
import com.optimiza.ehope.lis.repo.EmrPatientIndicatorRepo;

public class EmrPatientIndicatorService extends GenericService<EmrPatientIndicator, EmrPatientIndicatorRepo> {

	@Autowired
	private EmrPatientIndicatorRepo repo;

	@Override
	protected EmrPatientIndicatorRepo getRepository() {
		return repo;
	}

	private EmrPatientIndicator addEmrPatientIndicator(EmrPatientIndicator patientIndicator) {
		return repo.save(patientIndicator);
	}

	private EmrPatientIndicator editEmrPatientIndicator(EmrPatientIndicator patientIndicator) {
		return repo.save(patientIndicator);
	}

	private void deleteEmrPatientIndicator(Long id) {
		repo.delete(id);
	}

}

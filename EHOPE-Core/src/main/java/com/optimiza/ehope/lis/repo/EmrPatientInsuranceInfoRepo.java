package com.optimiza.ehope.lis.repo;

import org.springframework.stereotype.Repository;

import com.optimiza.core.base.repo.GenericRepository;
import com.optimiza.ehope.lis.model.EmrPatientInfo;
import com.optimiza.ehope.lis.model.EmrPatientInsuranceInfo;

@Repository("EmrPatientInsuranceInfoRepo")
public interface EmrPatientInsuranceInfoRepo extends GenericRepository<EmrPatientInsuranceInfo> {

	void deleteAllByPatient(EmrPatientInfo emrPaitentInfo);
}

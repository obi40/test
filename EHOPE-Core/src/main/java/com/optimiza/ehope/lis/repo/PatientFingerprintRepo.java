package com.optimiza.ehope.lis.repo;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.optimiza.core.base.repo.GenericRepository;
import com.optimiza.ehope.lis.model.PatientFingerprint;

/**
 * PatientFingerprintRepo.java
 * 
 * @author Murad Poladian <mpoladian@optimizasolutions.com>
 * @since Mar/24/2019
 * 
 */
@Repository("PatientFingerprintRepo")
public interface PatientFingerprintRepo extends GenericRepository<PatientFingerprint> {

	@Query("select new com.optimiza.ehope.lis.model.PatientFingerprint(pf.patient.rid, pf.template) from PatientFingerprint pf")
	List<PatientFingerprint> getAllFingerprints();

}

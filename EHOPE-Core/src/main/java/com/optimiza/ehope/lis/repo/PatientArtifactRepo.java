package com.optimiza.ehope.lis.repo;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.optimiza.core.base.repo.GenericRepository;
import com.optimiza.ehope.lis.model.PatientArtifact;

/**
 * PatientArtifactRepo.java
 * 
 * @author Murad Poladian <mpoladian@optimizasolutions.com>
 * @since Jan/31/2019
 * 
 */
@Repository("PatientArtifactRepo")
public interface PatientArtifactRepo extends GenericRepository<PatientArtifact> {

	@Query("select a.rid, a.fileName, a.size, a.extension, a.contentType from PatientArtifact a where a.patient.rid = :patientId")
	List<Object> getByPatientId(@Param("patientId") Long patientId);

}

package com.optimiza.ehope.lis.repo;

import java.util.Date;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.optimiza.core.base.repo.GenericRepository;
import com.optimiza.ehope.lis.model.EmrPatientInfo;;

@Repository("EmrPatientInfoRepo")
public interface EmrPatientInfoRepo extends GenericRepository<EmrPatientInfo> {

	EmrPatientInfo findByEmailIgnoreCase(String email);

	@Modifying
	@Query("UPDATE EmrPatientInfo patient SET patient.lastOrderDate=?2 WHERE patient.rid=?1")
	void updateLastOrderDate(Long rid, Date visitDate);

	@Query("SELECT COUNT(*) FROM EmrPatientInfo pat WHERE pat.isActive = :isActive")
	Long countActivePatients(@Param("isActive") Boolean isActive);

	@Query("SELECT COUNT(*) FROM EmrPatientInfo pat WHERE pat.creationDate BETWEEN :from AND :to")
	Long countNewPatients(@Param("from") Date from, @Param("to") Date to);

}
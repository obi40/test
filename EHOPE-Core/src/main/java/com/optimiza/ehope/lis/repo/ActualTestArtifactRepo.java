package com.optimiza.ehope.lis.repo;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.optimiza.core.base.repo.GenericRepository;
import com.optimiza.ehope.lis.model.ActualTestArtifact;
import com.optimiza.ehope.lis.model.LabTestActual;

/**
 * ActualTestArtifactRepo.java
 * 
 * @author Murad Poladian <mpoladian@optimizasolutions.com>
 * @since Jan/31/2019
 * 
 */
@Repository("ActualTestArtifactRepo")
public interface ActualTestArtifactRepo extends GenericRepository<ActualTestArtifact> {

	@Query("select a.rid, a.fileName, a.size, a.extension, a.contentType from ActualTestArtifact a where a.actualTest.rid = :actualTestId")
	List<Object> getByActualTestId(@Param("actualTestId") Long actualTestId);

	void deleteAllByActualTest(LabTestActual testActual);

}

package com.optimiza.ehope.lis.repo;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.optimiza.core.base.repo.GenericRepository;
import com.optimiza.ehope.lis.model.LabTestActual;
import com.optimiza.ehope.lis.model.LabTestAnswer;

@Repository("LabTestAnswerRepo")
public interface LabTestAnswerRepo extends GenericRepository<LabTestAnswer> {

	void deleteAllByLabTestActual(LabTestActual labTestActual);

	@Query("SELECT answer FROM LabTestAnswer answer "
			+ "LEFT JOIN FETCH answer.testQuestion "
			+ "LEFT JOIN FETCH answer.labTestActual lta "
			+ "LEFT JOIN lta.labSample ls "
			+ "LEFT JOIN ls.emrVisit visit "
			+ "WHERE "
			+ "visit.rid = :visitRid")
	List<LabTestAnswer> getByVisit(@Param("visitRid") Long visitRid);
}

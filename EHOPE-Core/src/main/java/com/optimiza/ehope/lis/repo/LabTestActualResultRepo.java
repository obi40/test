package com.optimiza.ehope.lis.repo;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.optimiza.core.base.repo.GenericRepository;
import com.optimiza.ehope.lis.model.LabTestActual;
import com.optimiza.ehope.lis.model.LabTestActualResult;
import com.optimiza.ehope.lis.model.TestResult;

@Repository("LabTestActualResultRepo")
public interface LabTestActualResultRepo extends GenericRepository<LabTestActualResult> {

	//TODO update with new units //or remove?
	@Query("SELECT tar FROM LabTestActualResult tar "
			+ "LEFT OUTER JOIN FETCH tar.labResult lr "
			+ "LEFT OUTER JOIN FETCH lr.primaryUnit "
			+ "LEFT OUTER JOIN FETCH lr.secondaryUnit "
			+ "LEFT OUTER JOIN FETCH lr.resultValueType "
			+ "LEFT OUTER JOIN FETCH tar.labTestActual lta "
			+ "LEFT OUTER JOIN FETCH lta.testDefinition "
			+ "LEFT OUTER JOIN FETCH lta.labSample "
			+ "WHERE "
			+ "tar.labTestActual IN "
			+ "(SELECT lta FROM LabTestActual lta "
			+ "WHERE lta.labSample IN "
			+ "(SELECT ls FROM LabSample ls WHERE ls.emrVisit.rid = :visitRid))")
	List<LabTestActualResult> getActualTestResultsByVisit(@Param("visitRid") Long visitRid);

	@Query("select tar from LabTestActualResult tar "
			+ "left outer join fetch tar.labResult lr "
			+ "left outer join fetch lr.comprehensiveResult "
			+ "left outer join fetch lr.resultValueType "
			+ "left outer join tar.labTestActual lta "
			+ "left outer join lta.labSample ls "
			+ "where lr.standardCode = :resultCode "
			+ "AND "
			+ "ls.barcode = :barcode")
	LabTestActualResult findByResultCodeAndBarcode(@Param("resultCode") String resultCode, @Param("barcode") String barcode);

	@Query("select count(r) from LabTestActualResult r "
			+ "where r.labResult.comprehensiveResult = :comprehensiveResult "
			+ "and "
			+ "r.labTestActual.rid = :actualTestRid "
			+ "and "
			+ "r.labTestActual.labSample.emrVisit.rid = :orderRid")
	Integer findNumberOfDifferentialsByComprehensive(@Param("comprehensiveResult") TestResult comprehensiveResult,
			@Param("actualTestRid") Long actualTestRid, @Param("orderRid") Long orderRid);

	void deleteAllByLabTestActual(LabTestActual labTestActual);
}

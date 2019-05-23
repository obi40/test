package com.optimiza.ehope.lis.repo;

import java.util.List;
import java.util.Set;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.optimiza.core.base.repo.GenericRepository;
import com.optimiza.ehope.lis.lkp.model.LkpOperationStatus;
import com.optimiza.ehope.lis.model.EmrVisit;
import com.optimiza.ehope.lis.model.LabSample;

@Repository("LabSampleRepo")
public interface LabSampleRepo extends GenericRepository<LabSample> {

	@Query("SELECT ls FROM LabSample ls "
			+ "LEFT JOIN FETCH ls.emrVisit ev "
			+ "LEFT JOIN ls.lkpOperationStatus los "
			+ "LEFT JOIN FETCH ls.labTestActualSet lta "
			+ "LEFT JOIN FETCH lta.testDefinition "
			+ "WHERE los.code = :operationStatus AND ev in :visits")
	Set<LabSample> findByStatusAndVisits(@Param("operationStatus") String operationStatus, @Param("visits") List<EmrVisit> visits);

	@Query("SELECT ls FROM LabSample ls "
			+ "LEFT JOIN FETCH ls.emrVisit emr "
			+ "LEFT JOIN FETCH emr.emrPatientInfo epi "
			+ "LEFT JOIN FETCH epi.gender "
			+ "LEFT JOIN FETCH emr.doctor "
			+ "LEFT JOIN FETCH ls.labTestActualSet lta "
			+ "LEFT JOIN FETCH lta.labTestActualResults ltar "
			+ "LEFT JOIN FETCH lta.testDefinition td "
			+ "LEFT JOIN FETCH td.specimenType "
			+ "LEFT JOIN FETCH td.testResults trs "
			+ "LEFT JOIN FETCH trs.normalRanges nr "
			+ "LEFT JOIN FETCH ltar.labTestActual ltarlta "
			+ "LEFT JOIN FETCH ltarlta.labTestAnswerSet ltal "
			+ "LEFT JOIN FETCH ltal.testQuestion tq "
			+ "LEFT JOIN FETCH nr.sex "
			+ "WHERE ls.rid IN :sampleRids "
			+ "AND lta.lkpOperationStatus.code NOT IN :unwantedSamples "
			+ "ORDER BY nr.printOrder")
	LabSample getSampleData(@Param("sampleRids") List<Long> sampleRids,
			@Param("unwantedSamples") List<String> unwantedSamples);

	//TODO delete later
	@Query("SELECT ls FROM LabSample ls "
			+ "LEFT JOIN FETCH ls.emrVisit emr "
			+ "LEFT JOIN FETCH emr.emrPatientInfo epi "
			+ "LEFT JOIN FETCH epi.gender "
			+ "LEFT JOIN FETCH emr.doctor "
			+ "LEFT JOIN FETCH ls.labTestActualSet lta "
			+ "LEFT JOIN FETCH lta.labTestActualResults ltar "
			/*
			 * + "LEFT JOIN FETCH lta.testDefinition td "
			 * + "LEFT JOIN FETCH ltar.labResult lr "
			 * + "LEFT JOIN FETCH lr.normalRanges nr "
			 * + "LEFT JOIN FETCH nr.sex "
			 */
			+ "WHERE ls.rid = :sampleRid")
	LabSample getSampleResults(@Param("sampleRid") Long sampleRid);

	@Modifying
	@Query("UPDATE LabSample ls SET ls.lkpOperationStatus = :lkpOperationStatus WHERE ls.rid IN :samplesRid")
	void updateOperationStatus(@Param("samplesRid") List<Long> samplesRid,
			@Param("lkpOperationStatus") LkpOperationStatus lkpOperationStatus);
}

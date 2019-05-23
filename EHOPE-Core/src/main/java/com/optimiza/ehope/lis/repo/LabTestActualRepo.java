package com.optimiza.ehope.lis.repo;

import java.util.Date;
import java.util.List;
import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.optimiza.core.base.repo.GenericRepository;
import com.optimiza.ehope.lis.lkp.model.LkpOperationStatus;
import com.optimiza.ehope.lis.model.LabTestActual;

@Repository("LabTestActualRepo")
public interface LabTestActualRepo extends GenericRepository<LabTestActual> {

	@Query("SELECT lta FROM LabTestActual lta "
			+ "LEFT JOIN FETCH lta.testDefinition "
			+ "LEFT JOIN FETCH lta.testDestination "
			+ "LEFT JOIN FETCH lta.lkpOperationStatus status "
			+ "WHERE "
			+ "status.code NOT IN :excludedStatuses AND "
			+ "lta.labSample in (SELECT ls FROM LabSample ls WHERE ls.emrVisit.rid = :visitRid)")
	Set<LabTestActual> findActualsDestinations(@Param("visitRid") Long visitRid, @Param("excludedStatuses") List<String> excludedStatuses);

	@Query("SELECT lta FROM LabTestActual lta "
			+ "LEFT OUTER JOIN FETCH lta.testDefinition "
			+ "LEFT OUTER JOIN FETCH lta.lkpOperationStatus los "
			+ "LEFT OUTER JOIN FETCH lta.labSample "
			+ "LEFT OUTER JOIN FETCH lta.testDestination td "
			+ "LEFT OUTER JOIN FETCH td.workbench "
			+ "LEFT OUTER JOIN FETCH td.type "
			+ "LEFT OUTER JOIN FETCH td.destinationBranch db "
			+ "LEFT OUTER JOIN FETCH db.priceList "
			+ "WHERE "
			+ "los.code != :abortedStatus AND "
			+ "lta.labSample in (SELECT ls FROM LabSample ls WHERE ls.emrVisit.rid = :visitRid)")
	Set<LabTestActual> findActualsChargeSlip(@Param("visitRid") Long visitRid, @Param("abortedStatus") String abortedStatus);

	@Query("select lta FROM LabTestActual lta "
			+ "LEFT OUTER JOIN FETCH lta.sourceActualTest "
			+ "LEFT OUTER JOIN FETCH lta.testDefinition td "
			+ "LEFT OUTER JOIN FETCH td.allergyUnit "
			+ "LEFT OUTER JOIN FETCH td.interpretations "
			+ "LEFT OUTER JOIN FETCH lta.labSample "
			+ "LEFT OUTER JOIN FETCH lta.lkpOperationStatus los "
			+ "LEFT OUTER JOIN FETCH lta.labTestActualResults tar "
			+ "LEFT OUTER JOIN FETCH tar.testCodedResult "
			+ "LEFT OUTER JOIN FETCH tar.organismDetection "
			+ "LEFT OUTER JOIN FETCH tar.actualAntiMicrobials aam "
			+ "LEFT OUTER JOIN FETCH aam.antiMicrobial "
			+ "LEFT OUTER JOIN FETCH aam.organismSensitivity "
			+ "LEFT OUTER JOIN FETCH tar.actualOrganisms ao "
			+ "LEFT OUTER JOIN FETCH tar.labResult lr "
			+ "LEFT OUTER JOIN FETCH lr.comprehensiveResult "
			+ "LEFT OUTER JOIN FETCH lr.resultValueType "
			+ "LEFT OUTER JOIN FETCH lr.primaryUnit "
			+ "LEFT OUTER JOIN FETCH lr.secondaryUnit "
			+ "LEFT OUTER JOIN FETCH lr.primaryUnitType "
			+ "LEFT OUTER JOIN FETCH lr.narrativeTemplates "
			+ "WHERE "
			+ "los.code NOT IN :excludedStatuses AND "
			+ "(:includedStatus IS NULL OR los.code = :includedStatus) AND "
			+ "lta.labSample in "
			+ "(select ls FROM LabSample ls WHERE ls.emrVisit.rid = :visitRid) "
			+ "order by lta.rid")
	Set<LabTestActual> findTestActualListWithResultsByVisit(@Param("visitRid") Long visitRid,
			@Param("excludedStatuses") List<String> excludedStatuses, @Param("includedStatus") String includedStatus);

	@Query(value = "SELECT DISTINCT lta FROM LabTestActual lta "
			+ "LEFT JOIN lta.lkpOperationStatus los "
			+ "LEFT JOIN lta.testDefinition td "
			+ "LEFT JOIN lta.labSample sample "
			+ "LEFT JOIN sample.emrVisit visit "
			+ "LEFT JOIN visit.visitType vt "
			+ "LEFT JOIN visit.emrPatientInfo pat "
			+ "WHERE "
			+ "los.code != :operationStatus AND "
			+ "(:visitTypeCode IS NULL OR LOWER(vt.code) = :visitTypeCode) AND "
			+ "(:admissionNumber IS NULL OR LOWER(visit.admissionNumber) LIKE CONCAT('%',CAST(:admissionNumber AS java.lang.String),'%')) AND "
			+ "((CAST(:visitDateFrom AS java.util.Date) IS NULL AND CAST(:visitDateTo AS java.util.Date) IS NULL) OR (visit.visitDate BETWEEN :visitDateFrom AND :visitDateTo)) AND "
			+ "(:barcode IS NULL OR LOWER(sample.barcode) LIKE CONCAT('%',CAST(:barcode AS java.lang.String),'%')) AND "
			+ "(:standardCode IS NULL OR LOWER(td.standardCode) LIKE CONCAT('%',CAST(:standardCode AS java.lang.String),'%')) AND "
			+ "(:aliases IS NULL OR LOWER(td.aliases) LIKE CONCAT('%',CAST(:aliases AS java.lang.String),'%')) AND "
			+ "(:description IS NULL OR LOWER(CAST(td.description AS org.hibernate.type.TextType)) LIKE CONCAT('%',CAST(:description AS org.hibernate.type.TextType),'%')) AND "
			+ "(:secondaryCode IS NULL OR LOWER(td.secondaryCode) LIKE CONCAT('%',CAST(:secondaryCode AS java.lang.String),'%')) AND "
			+ "(:nationalId IS NULL OR pat.nationalId = :nationalId) AND "
			+ "(:fileNo IS NULL OR LOWER(pat.fileNo) LIKE CONCAT('%',CAST(:fileNo AS java.lang.String),'%')) AND "
			+ "(:mobileNo IS NULL OR LOWER(pat.mobileNo) LIKE CONCAT('%',CAST(:mobileNo AS java.lang.String),'%')) AND "
			+ "(:firstName IS NULL OR LOWER(CAST(pat.firstName AS org.hibernate.type.TextType)) LIKE CONCAT('%',CAST(:firstName AS org.hibernate.type.TextType),'%')) AND "
			+ "(:lastName IS NULL OR LOWER(CAST(pat.lastName AS org.hibernate.type.TextType)) LIKE CONCAT('%',CAST(:lastName AS org.hibernate.type.TextType),'%'))", countQuery = "SELECT COUNT(DISTINCT lta) FROM LabTestActual lta "
					+ "LEFT JOIN lta.lkpOperationStatus los "
					+ "LEFT JOIN lta.testDefinition td "
					+ "LEFT JOIN lta.labSample sample "
					+ "LEFT JOIN sample.emrVisit visit "
					+ "LEFT JOIN visit.visitType vt "
					+ "LEFT JOIN visit.emrPatientInfo pat "
					+ "WHERE "
					+ "los.code != :operationStatus AND "
					+ "(:visitTypeCode IS NULL OR LOWER(vt.code) = :visitTypeCode) AND "
					+ "(:admissionNumber IS NULL OR LOWER(visit.admissionNumber) LIKE CONCAT('%',CAST(:admissionNumber AS java.lang.String),'%')) AND "
					+ "((CAST(:visitDateFrom AS java.util.Date) IS NULL AND CAST(:visitDateTo AS java.util.Date) IS NULL) OR (visit.visitDate BETWEEN :visitDateFrom AND :visitDateTo)) AND "
					+ "(:barcode IS NULL OR LOWER(sample.barcode) LIKE CONCAT('%',CAST(:barcode AS java.lang.String),'%')) AND "
					+ "(:standardCode IS NULL OR LOWER(td.standardCode) LIKE CONCAT('%',CAST(:standardCode AS java.lang.String),'%')) AND "
					+ "(:aliases IS NULL OR LOWER(td.aliases) LIKE CONCAT('%',CAST(:aliases AS java.lang.String),'%')) AND "
					+ "(:description IS NULL OR LOWER(CAST(td.description AS org.hibernate.type.TextType)) LIKE CONCAT('%',CAST(:description AS org.hibernate.type.TextType),'%')) AND "
					+ "(:secondaryCode IS NULL OR LOWER(td.secondaryCode) LIKE CONCAT('%',CAST(:secondaryCode AS java.lang.String),'%')) AND "
					+ "(:nationalId IS NULL OR pat.nationalId = :nationalId) AND "
					+ "(:fileNo IS NULL OR LOWER(pat.fileNo) LIKE CONCAT('%',CAST(:fileNo AS java.lang.String),'%')) AND "
					+ "(:mobileNo IS NULL OR LOWER(pat.mobileNo) LIKE CONCAT('%',CAST(:mobileNo AS java.lang.String),'%')) AND "
					+ "(:firstName IS NULL OR LOWER(CAST(pat.firstName AS org.hibernate.type.TextType)) LIKE CONCAT('%',CAST(:firstName AS org.hibernate.type.TextType),'%')) AND "
					+ "(:lastName IS NULL OR LOWER(CAST(pat.lastName AS org.hibernate.type.TextType)) LIKE CONCAT('%',CAST(:lastName AS org.hibernate.type.TextType),'%'))")
	Page<LabTestActual> findOrderManagementDataGeneral(@Param("operationStatus") String operationStatus,
			@Param("visitTypeCode") String visitTypeCode, @Param("admissionNumber") String admissionNumber,
			@Param("barcode") String barcode, @Param("nationalId") Long nationalId, @Param("fileNo") String fileNo,
			@Param("mobileNo") String mobileNo, @Param("firstName") String firstName, @Param("lastName") String lastName,
			@Param("standardCode") String standardCode, @Param("description") String description, @Param("aliases") String aliases,
			@Param("secondaryCode") String secondaryCode, @Param("visitDateFrom") Date visitDateFrom,
			@Param("visitDateTo") Date visitDateTo, Pageable pageable);

	@Query(value = "SELECT DISTINCT lta FROM LabTestActual lta "
			+ "LEFT JOIN lta.lkpOperationStatus los "
			+ "LEFT JOIN lta.testDefinition td "
			+ "LEFT JOIN lta.labSample sample "
			+ "LEFT JOIN sample.emrVisit visit "
			+ "LEFT JOIN visit.visitType vt "
			+ "LEFT JOIN visit.emrPatientInfo pat "
			+ "WHERE "
			+ "los.code != :operationStatus AND "
			+ "(:visitTypeCode IS NULL OR LOWER(vt.code) = :visitTypeCode) AND "
			+ "((CAST(:visitDateFrom AS java.util.Date) IS NULL AND CAST(:visitDateTo AS java.util.Date) IS NULL) OR (visit.visitDate BETWEEN :visitDateFrom AND :visitDateTo)) AND "
			+ "(:visitRid IS NULL OR visit.rid = :visitRid) AND "
			+ "(:patientRid IS NULL OR pat.rid = :patientRid) AND "
			+ "(:sampleRid IS NULL OR sample.rid = :sampleRid) AND "
			+ "(:testRid IS NULL OR td.rid = :testRid)", countQuery = "SELECT COUNT(DISTINCT lta) FROM LabTestActual lta "
					+ "LEFT JOIN lta.lkpOperationStatus los "
					+ "LEFT JOIN lta.testDefinition td "
					+ "LEFT JOIN lta.labSample sample "
					+ "LEFT JOIN sample.emrVisit visit "
					+ "LEFT JOIN visit.visitType vt "
					+ "LEFT JOIN visit.emrPatientInfo pat "
					+ "WHERE "
					+ "los.code != :operationStatus AND "
					+ "(:visitTypeCode IS NULL OR LOWER(vt.code) = :visitTypeCode) AND "
					+ "((CAST(:visitDateFrom AS java.util.Date) IS NULL AND CAST(:visitDateTo AS java.util.Date) IS NULL) OR (visit.visitDate BETWEEN :visitDateFrom AND :visitDateTo)) AND "
					+ "(:visitRid IS NULL OR visit.rid = :visitRid) AND "
					+ "(:patientRid IS NULL OR pat.rid = :patientRid) AND "
					+ "(:sampleRid IS NULL OR sample.rid = :sampleRid) AND "
					+ "(:testRid IS NULL OR td.rid = :testRid)")
	Page<LabTestActual> findOrderManagementDataRid(@Param("operationStatus") String operationStatus,
			@Param("visitTypeCode") String visitTypeCode, @Param("visitDateFrom") Date visitDateFrom,
			@Param("visitDateTo") Date visitDateTo, @Param("visitRid") Long visitRid, @Param("patientRid") Long patientRid,
			@Param("sampleRid") Long sampleRid, @Param("testRid") Long testRid, Pageable pageable);

	@Query(value = "SELECT DISTINCT lta FROM LabTestActual lta "
			+ "LEFT JOIN lta.lkpOperationStatus los "
			+ "LEFT JOIN lta.labSample sample "
			+ "LEFT JOIN sample.emrVisit visit "
			+ "LEFT JOIN visit.emrPatientInfo pat "
			+ "WHERE "
			+ "los.code != :abortedStatus AND "
			+ "((CAST(:visitDateFrom AS java.util.Date) IS NULL AND CAST(:visitDateTo AS java.util.Date) IS NULL) OR (visit.visitDate BETWEEN :visitDateFrom AND :visitDateTo)) AND "
			+ "((:nationalId IS NULL OR pat.nationalId = :nationalId) OR "
			+ "(:fileNo IS NULL OR LOWER(pat.fileNo) LIKE CONCAT('%',CAST(:fileNo AS java.lang.String),'%')) OR "
			+ "(:mobileNo IS NULL OR LOWER(pat.mobileNo) LIKE CONCAT('%',CAST(:mobileNo AS java.lang.String),'%')) OR "
			+ "(:firstName IS NULL OR LOWER(CAST(pat.firstName AS org.hibernate.type.TextType)) LIKE CONCAT('%',CAST(:firstName AS org.hibernate.type.TextType),'%')) OR "
			+ "(:lastName IS NULL OR LOWER(CAST(pat.lastName AS org.hibernate.type.TextType)) LIKE CONCAT('%',CAST(:lastName AS org.hibernate.type.TextType),'%')))", countQuery = "SELECT COUNT(DISTINCT lta) FROM LabTestActual lta "
					+ "LEFT JOIN lta.lkpOperationStatus los "
					+ "LEFT JOIN lta.labSample sample "
					+ "LEFT JOIN sample.emrVisit visit "
					+ "LEFT JOIN visit.emrPatientInfo pat "
					+ "WHERE "
					+ "los.code != :abortedStatus AND "
					+ "((CAST(:visitDateFrom AS java.util.Date) IS NULL AND CAST(:visitDateTo AS java.util.Date) IS NULL) OR (visit.visitDate BETWEEN :visitDateFrom AND :visitDateTo)) AND "
					+ "((:nationalId IS NULL OR pat.nationalId = :nationalId) OR "
					+ "(:fileNo IS NULL OR LOWER(pat.fileNo) LIKE CONCAT('%',CAST(:fileNo AS java.lang.String),'%')) OR "
					+ "(:mobileNo IS NULL OR LOWER(pat.mobileNo) LIKE CONCAT('%',CAST(:mobileNo AS java.lang.String),'%')) OR "
					+ "(:firstName IS NULL OR LOWER(CAST(pat.firstName AS org.hibernate.type.TextType)) LIKE CONCAT('%',CAST(:firstName AS org.hibernate.type.TextType),'%')) OR "
					+ "(:lastName IS NULL OR LOWER(CAST(pat.lastName AS org.hibernate.type.TextType)) LIKE CONCAT('%',CAST(:lastName AS org.hibernate.type.TextType),'%')))")
	Page<LabTestActual> findOrderManagementDataPatient(@Param("abortedStatus") String abortedStatus,
			@Param("nationalId") Long nationalId, @Param("fileNo") String fileNo,
			@Param("mobileNo") String mobileNo, @Param("firstName") String firstName, @Param("lastName") String lastName,
			@Param("visitDateFrom") Date visitDateFrom, @Param("visitDateTo") Date visitDateTo, Pageable pageable);

	@Query(value = "SELECT DISTINCT lta FROM LabTestActual lta "
			+ "LEFT JOIN lta.lkpOperationStatus los "
			+ "LEFT JOIN lta.labSample sample "
			+ "LEFT JOIN sample.emrVisit visit "
			+ "WHERE "
			+ "los.code != :abortedStatus AND "
			+ "((CAST(:visitDateFrom AS java.util.Date) IS NULL AND CAST(:visitDateTo AS java.util.Date) IS NULL) OR (visit.visitDate BETWEEN :visitDateFrom AND :visitDateTo)) AND "
			+ "(LOWER(visit.admissionNumber) LIKE CONCAT('%',CAST(:admissionNumber AS java.lang.String),'%'))", countQuery = "SELECT COUNT(DISTINCT lta) FROM LabTestActual lta "
					+ "LEFT JOIN lta.lkpOperationStatus los "
					+ "LEFT JOIN lta.labSample sample "
					+ "LEFT JOIN sample.emrVisit visit "
					+ "WHERE "
					+ "los.code != :abortedStatus AND "
					+ "((CAST(:visitDateFrom AS java.util.Date) IS NULL AND CAST(:visitDateTo AS java.util.Date) IS NULL) OR (visit.visitDate BETWEEN :visitDateFrom AND :visitDateTo)) AND "
					+ "(LOWER(visit.admissionNumber) LIKE CONCAT('%',CAST(:admissionNumber AS java.lang.String),'%'))")
	Page<LabTestActual> findOrderManagementDataVisit(@Param("abortedStatus") String abortedStatus,
			@Param("admissionNumber") String admissionNumber,
			@Param("visitDateFrom") Date visitDateFrom, @Param("visitDateTo") Date visitDateTo, Pageable pageable);

	@Query(value = "SELECT DISTINCT lta FROM LabTestActual lta "
			+ "LEFT JOIN lta.lkpOperationStatus los "
			+ "LEFT JOIN lta.labSample sample "
			+ "LEFT JOIN sample.emrVisit visit "
			+ "WHERE "
			+ "los.code != :abortedStatus AND "
			+ "((CAST(:visitDateFrom AS java.util.Date) IS NULL AND CAST(:visitDateTo AS java.util.Date) IS NULL) OR (visit.visitDate BETWEEN :visitDateFrom AND :visitDateTo)) AND "
			+ "(LOWER(sample.barcode) LIKE CONCAT('%',CAST(:barcode AS java.lang.String),'%'))", countQuery = "SELECT COUNT(DISTINCT lta) FROM LabTestActual lta "
					+ "LEFT JOIN lta.lkpOperationStatus los "
					+ "LEFT JOIN lta.labSample sample "
					+ "LEFT JOIN sample.emrVisit visit "
					+ "WHERE "
					+ "los.code != :abortedStatus AND "
					+ "((CAST(:visitDateFrom AS java.util.Date) IS NULL AND CAST(:visitDateTo AS java.util.Date) IS NULL) OR (visit.visitDate BETWEEN :visitDateFrom AND :visitDateTo)) AND "
					+ "(LOWER(sample.barcode) LIKE CONCAT('%',CAST(:barcode AS java.lang.String),'%'))")
	Page<LabTestActual> findOrderManagementDataSample(@Param("abortedStatus") String abortedStatus, @Param("barcode") String barcode,
			@Param("visitDateFrom") Date visitDateFrom, @Param("visitDateTo") Date visitDateTo, Pageable pageable);

	@Query(value = "SELECT DISTINCT lta FROM LabTestActual lta "
			+ "LEFT JOIN lta.lkpOperationStatus los "
			+ "LEFT JOIN lta.testDefinition td "
			+ "LEFT JOIN lta.labSample sample "
			+ "LEFT JOIN sample.emrVisit visit "
			+ "WHERE "
			+ "los.code != :abortedStatus AND "
			+ "((CAST(:visitDateFrom AS java.util.Date) IS NULL AND CAST(:visitDateTo AS java.util.Date) IS NULL) OR (visit.visitDate BETWEEN :visitDateFrom AND :visitDateTo)) AND "
			+ "((LOWER(td.standardCode) LIKE CONCAT('%',CAST(:standardCode AS java.lang.String),'%')) OR "
			+ "(LOWER(td.aliases) LIKE CONCAT('%',CAST(:aliases AS java.lang.String),'%')) OR "
			+ "(LOWER(td.secondaryCode) LIKE CONCAT('%',CAST(:secondaryCode AS java.lang.String),'%')) OR "
			+ "(LOWER(CAST(td.description AS org.hibernate.type.TextType)) LIKE CONCAT('%',CAST(:description AS org.hibernate.type.TextType),'%')))", countQuery = "SELECT COUNT(DISTINCT lta) FROM LabTestActual lta "
					+ "LEFT JOIN lta.lkpOperationStatus los "
					+ "LEFT JOIN lta.testDefinition td "
					+ "LEFT JOIN lta.labSample sample "
					+ "LEFT JOIN sample.emrVisit visit "
					+ "WHERE "
					+ "los.code != :abortedStatus AND "
					+ "((CAST(:visitDateFrom AS java.util.Date) IS NULL AND CAST(:visitDateTo AS java.util.Date) IS NULL) OR (visit.visitDate BETWEEN :visitDateFrom AND :visitDateTo)) AND "
					+ "((LOWER(td.standardCode) LIKE CONCAT('%',CAST(:standardCode AS java.lang.String),'%')) OR "
					+ "(LOWER(td.aliases) LIKE CONCAT('%',CAST(:aliases AS java.lang.String),'%')) OR "
					+ "(LOWER(td.secondaryCode) LIKE CONCAT('%',CAST(:secondaryCode AS java.lang.String),'%')) OR "
					+ "(LOWER(CAST(td.description AS org.hibernate.type.TextType)) LIKE CONCAT('%',CAST(:description AS org.hibernate.type.TextType),'%')))")
	Page<LabTestActual> findOrderManagementDataTest(@Param("abortedStatus") String abortedStatus,
			@Param("standardCode") String standardCode,
			@Param("description") String description,
			@Param("aliases") String aliases, @Param("secondaryCode") String secondaryCode,
			@Param("visitDateFrom") Date visitDateFrom,
			@Param("visitDateTo") Date visitDateTo, Pageable pageable);

	@Query(value = "SELECT lta from LabTestActual lta "
			+ "LEFT JOIN FETCH lta.labTestActualResults "
			+ "LEFT JOIN lta.testDefinition td "
			+ "LEFT JOIN lta.labSample ls "
			+ "LEFT JOIN ls.emrVisit ev "
			+ "LEFT JOIN ev.emrPatientInfo p "
			+ "WHERE td.standardCode = :testCode "
			+ "AND p.rid = :patientRid "
			+ "AND lta.rid < :currentLabTestActualRid "
			+ "AND lta.lkpOperationStatus.code IN :filters "
			+ "ORDER BY ev.rid DESC", countQuery = "SELECT count(lta) from LabTestActual lta "
					+ "LEFT JOIN lta.testDefinition td "
					+ "LEFT JOIN lta.labSample ls "
					+ "LEFT JOIN ls.emrVisit ev "
					+ "LEFT JOIN ev.emrPatientInfo p "
					+ "WHERE td.standardCode = :testCode "
					+ "AND p.rid = :patientRid "
					+ "AND lta.lkpOperationStatus.code IN :filters "
					+ "AND lta.rid < :currentLabTestActualRid")
	Page<LabTestActual> findPreviousTestActual(@Param("patientRid") Long patientRid, @Param("testCode") String testCode,
			@Param("currentLabTestActualRid") Long currentLabTestActualRid, Pageable pageable, @Param("filters") List<String> filters);

	@Modifying
	@Query("UPDATE LabTestActual lta SET lta.lkpOperationStatus = :lkpOperationStatus WHERE lta.rid IN :testsRid")
	void updateOperationStatus(@Param("testsRid") List<Long> testsRid,
			@Param("lkpOperationStatus") LkpOperationStatus lkpOperationStatus);

}

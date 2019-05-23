package com.optimiza.ehope.lis.repo;

import java.util.Date;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.optimiza.core.base.repo.GenericRepository;
import com.optimiza.ehope.lis.lkp.model.LkpOperationStatus;
import com.optimiza.ehope.lis.model.EmrVisit;

@Repository("EmrVisitRepo")
public interface EmrVisitRepo extends GenericRepository<EmrVisit> {

	@Query(value = "SELECT DISTINCT visit FROM EmrVisit visit "
			+ "LEFT JOIN visit.lkpOperationStatus visitOperationStatus "
			+ "LEFT JOIN visit.labSamples sample "
			+ "LEFT JOIN sample.labTestActualSet testActual "
			+ "LEFT JOIN testActual.testDefinition td "
			+ "WHERE "
			+ "visitOperationStatus.code != :abortedStatus AND "
			+ "((CAST(:visitDateFrom AS java.util.Date) IS NULL AND CAST(:visitDateTo AS java.util.Date) IS NULL) OR (visit.visitDate BETWEEN :visitDateFrom AND :visitDateTo)) AND "
			+ "((LOWER(td.standardCode) LIKE CONCAT('%',CAST(:standardCode AS java.lang.String),'%')) OR "
			+ "(LOWER(td.aliases) LIKE CONCAT('%',CAST(:aliases AS java.lang.String),'%')) OR "
			+ "(LOWER(td.secondaryCode) LIKE CONCAT('%',CAST(:secondaryCode AS java.lang.String),'%')) OR "
			+ "(LOWER(CAST(td.description AS org.hibernate.type.TextType)) LIKE CONCAT('%',CAST(:description AS org.hibernate.type.TextType),'%')))", countQuery = "SELECT COUNT(DISTINCT visit) FROM EmrVisit visit "
					+ "LEFT JOIN visit.lkpOperationStatus visitOperationStatus "
					+ "LEFT JOIN visit.labSamples sample "
					+ "LEFT JOIN sample.labTestActualSet testActual "
					+ "LEFT JOIN testActual.testDefinition td "
					+ "WHERE "
					+ "visitOperationStatus.code != :abortedStatus AND "
					+ "((CAST(:visitDateFrom AS java.util.Date) IS NULL AND CAST(:visitDateTo AS java.util.Date) IS NULL) OR (visit.visitDate BETWEEN :visitDateFrom AND :visitDateTo)) AND "
					+ "((LOWER(td.standardCode) LIKE CONCAT('%',CAST(:standardCode AS java.lang.String),'%')) OR "
					+ "(LOWER(td.aliases) LIKE CONCAT('%',CAST(:aliases AS java.lang.String),'%')) OR "
					+ "(LOWER(td.secondaryCode) LIKE CONCAT('%',CAST(:secondaryCode AS java.lang.String),'%')) OR "
					+ "(LOWER(CAST(td.description AS org.hibernate.type.TextType)) LIKE CONCAT('%',CAST(:description AS org.hibernate.type.TextType),'%')))")
	Page<EmrVisit> findOrderManagementDataTest(@Param("abortedStatus") String abortedStatus, @Param("standardCode") String standardCode,
			@Param("description") String description,
			@Param("aliases") String aliases, @Param("secondaryCode") String secondaryCode,
			@Param("visitDateFrom") Date visitDateFrom,
			@Param("visitDateTo") Date visitDateTo, Pageable pageable);

	@Query(value = "SELECT DISTINCT visit FROM EmrVisit visit "
			+ "LEFT JOIN visit.lkpOperationStatus visitOperationStatus "
			+ "LEFT JOIN visit.labSamples sample "
			+ "WHERE "
			+ "visitOperationStatus.code != :abortedStatus AND "
			+ "((CAST(:visitDateFrom AS java.util.Date) IS NULL AND CAST(:visitDateTo AS java.util.Date) IS NULL) OR (visit.visitDate BETWEEN :visitDateFrom AND :visitDateTo)) AND "
			+ "(LOWER(sample.barcode) LIKE CONCAT('%',CAST(:barcode AS java.lang.String),'%'))", countQuery = "SELECT COUNT(DISTINCT visit) FROM EmrVisit visit "
					+ "LEFT JOIN visit.lkpOperationStatus visitOperationStatus "
					+ "LEFT JOIN visit.labSamples sample "
					+ "WHERE "
					+ "visitOperationStatus.code != :abortedStatus AND "
					+ "((CAST(:visitDateFrom AS java.util.Date) IS NULL AND CAST(:visitDateTo AS java.util.Date) IS NULL) OR (visit.visitDate BETWEEN :visitDateFrom AND :visitDateTo)) AND "
					+ "(LOWER(sample.barcode) LIKE CONCAT('%',CAST(:barcode AS java.lang.String),'%'))")
	Page<EmrVisit> findOrderManagementDataSample(@Param("abortedStatus") String abortedStatus, @Param("barcode") String barcode,
			@Param("visitDateFrom") Date visitDateFrom,
			@Param("visitDateTo") Date visitDateTo, Pageable pageable);

	// findTestsSampleVisitPage & overloaded one should have the save fetches in both the query and count query
	@Query(value = "SELECT DISTINCT visit FROM EmrVisit visit "
			+ "LEFT JOIN visit.visitType vt "
			+ "LEFT JOIN visit.lkpOperationStatus visitOperationStatus "
			+ "LEFT JOIN visit.emrPatientInfo pat "
			+ "LEFT JOIN pat.gender "
			+ "LEFT JOIN visit.labSamples sample "
			+ "LEFT JOIN sample.labTestActualSet testActual "
			+ "LEFT JOIN testActual.testDefinition td "
			+ "WHERE "
			+ "visitOperationStatus.code != :abortedStatus AND "
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
			+ "(:lastName IS NULL OR LOWER(CAST(pat.lastName AS org.hibernate.type.TextType)) LIKE CONCAT('%',CAST(:lastName AS org.hibernate.type.TextType),'%'))", countQuery = "SELECT COUNT(DISTINCT visit) FROM EmrVisit visit "
					+ "LEFT JOIN visit.visitType vt "
					+ "LEFT JOIN visit.lkpOperationStatus visitOperationStatus "
					+ "LEFT JOIN visit.emrPatientInfo pat "
					+ "LEFT JOIN pat.gender "
					+ "LEFT JOIN visit.labSamples sample "
					+ "LEFT JOIN sample.labTestActualSet testActual "
					+ "LEFT JOIN testActual.testDefinition td "
					+ "WHERE "
					+ "visitOperationStatus.code != :abortedStatus AND "
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
	Page<EmrVisit> findOrderManagementDataGeneral(@Param("abortedStatus") String abortedStatus,
			@Param("visitTypeCode") String visitTypeCode, @Param("admissionNumber") String admissionNumber,
			@Param("barcode") String barcode, @Param("nationalId") Long nationalId, @Param("fileNo") String fileNo,
			@Param("mobileNo") String mobileNo,
			@Param("firstName") String firstName, @Param("lastName") String lastName, @Param("standardCode") String standardCode,
			@Param("description") String description, @Param("aliases") String aliases, @Param("secondaryCode") String secondaryCode,
			@Param("visitDateFrom") Date visitDateFrom,
			@Param("visitDateTo") Date visitDateTo, Pageable pageable);

	//RID Only
	@Query(value = "SELECT DISTINCT visit FROM EmrVisit visit "
			+ "LEFT JOIN visit.lkpOperationStatus los "
			+ "LEFT JOIN visit.emrPatientInfo pat "
			+ "LEFT JOIN pat.gender "
			+ "LEFT JOIN visit.labSamples sample "
			+ "LEFT JOIN sample.labTestActualSet testActual "
			+ "LEFT JOIN testActual.testDefinition td "
			+ "WHERE "
			+ "los.code != :operationStatus AND "
			+ "((CAST(:visitDateFrom AS java.util.Date) IS NULL AND CAST(:visitDateTo AS java.util.Date) IS NULL) "
			+ "OR (visit.visitDate BETWEEN :visitDateFrom AND :visitDateTo)) AND "
			+ "(:visitRid IS NULL OR visit.rid = :visitRid) AND "
			+ "(:patientRid IS NULL OR pat.rid = :patientRid) AND "
			+ "(:sampleRid IS NULL OR sample.rid = :sampleRid) AND "
			+ "(:testRid IS NULL OR td.rid = :testRid)", countQuery = "SELECT COUNT(DISTINCT visit) FROM EmrVisit visit "
					+ "LEFT JOIN visit.lkpOperationStatus los "
					+ "LEFT JOIN visit.emrPatientInfo pat "
					+ "LEFT JOIN pat.gender "
					+ "LEFT JOIN visit.labSamples sample "
					+ "LEFT JOIN sample.labTestActualSet testActual "
					+ "LEFT JOIN testActual.testDefinition td "
					+ "WHERE "
					+ "los.code != :operationStatus AND "
					+ "((CAST(:visitDateFrom AS java.util.Date) IS NULL AND CAST(:visitDateTo AS java.util.Date) IS NULL) "
					+ "OR (visit.visitDate BETWEEN :visitDateFrom AND :visitDateTo)) AND "
					+ "(:visitRid IS NULL OR visit.rid = :visitRid) AND "
					+ "(:patientRid IS NULL OR pat.rid = :patientRid) AND "
					+ "(:sampleRid IS NULL OR sample.rid = :sampleRid) AND "
					+ "(:testRid IS NULL OR td.rid = :testRid)")
	Page<EmrVisit> findOrderManagementDataRid(@Param("operationStatus") String operationStatus, @Param("visitDateFrom") Date visitDateFrom,
			@Param("visitDateTo") Date visitDateTo, @Param("visitRid") Long visitRid, @Param("patientRid") Long patientRid,
			@Param("sampleRid") Long sampleRid, @Param("testRid") Long testRid, Pageable pageable);

	//"sampleStatus IS NULL,testActualStatus IS NULL" in case this order do not have samples or tests
	@Query(value = "SELECT DISTINCT visit FROM EmrVisit visit "
			+ "LEFT JOIN FETCH visit.lkpOperationStatus "
			+ "LEFT JOIN FETCH visit.labSamples sample "
			+ "LEFT JOIN FETCH sample.lkpOperationStatus sampleStatus "
			+ "LEFT JOIN FETCH sample.labTestActualSet testActual "
			+ "LEFT JOIN FETCH testActual.lkpOperationStatus testActualStatus "
			+ "LEFT JOIN FETCH testActual.testDefinition "
			+ "WHERE "
			+ "(sampleStatus IS NULL OR sampleStatus.code NOT IN :excludedStatuses) AND "
			+ "(testActualStatus IS NULL OR testActualStatus.code NOT IN :excludedStatuses) AND "
			+ "visit.rid = :visitRid")
	EmrVisit findOneOrderSampleTestStatus(@Param("visitRid") Long visitRid, @Param("excludedStatuses") List<String> excludedStatuses);

	@Query(value = "SELECT DISTINCT visit FROM EmrVisit visit "
			+ "LEFT JOIN FETCH visit.visitType "
			+ "LEFT JOIN FETCH visit.emrPatientInfo "
			+ "LEFT JOIN FETCH visit.lkpOperationStatus "
			+ "LEFT JOIN FETCH visit.labSamples sample "
			+ "LEFT JOIN FETCH sample.lkpContainerType "
			+ "LEFT JOIN FETCH sample.lkpOperationStatus "
			+ "LEFT JOIN FETCH sample.labTestActualSet lta "
			+ "LEFT JOIN FETCH lta.lkpOperationStatus "
			+ "LEFT JOIN FETCH lta.testDestination td "
			+ "LEFT JOIN FETCH td.workbench "
			+ "LEFT JOIN FETCH td.type "
			+ "LEFT JOIN FETCH td.destinationBranch "
			+ "LEFT JOIN FETCH lta.testDefinition td "
			+ "LEFT JOIN FETCH td.lkpTestingMethod "
			+ "LEFT JOIN FETCH td.specimenType "
			+ "LEFT JOIN FETCH td.section "
			+ "LEFT JOIN FETCH td.testSpecimens ts "
			+ "LEFT JOIN FETCH ts.containerType "
			+ "WHERE "
			+ "visit.rid = :visitRid "
			+ "ORDER BY "
			+ "sample.barcode")
	EmrVisit findVisitSampleSeparation(@Param("visitRid") Long visitRid);

	@Query(value = "SELECT visit FROM EmrVisit visit "
			+ "LEFT JOIN FETCH visit.emrPatientInfo pat "
			+ "LEFT JOIN FETCH visit.visitType "
			+ "WHERE "
			+ "((CAST(:visitDateFrom AS java.util.Date) IS NULL AND CAST(:visitDateTo AS java.util.Date) IS NULL) "
			+ "OR (visit.visitDate BETWEEN :visitDateFrom AND :visitDateTo)) AND "
			+ "visit.totalAmount > visit.paidAmount AND "
			+ "(:patientRid IS NULL OR pat.rid = :patientRid)",

			countQuery = "SELECT COUNT(visit) FROM EmrVisit visit "
					+ "LEFT JOIN visit.emrPatientInfo pat "
					+ "WHERE "
					+ "((CAST(:visitDateFrom AS java.util.Date) IS NULL AND CAST(:visitDateTo AS java.util.Date) IS NULL) "
					+ "OR (visit.visitDate BETWEEN :visitDateFrom AND :visitDateTo)) AND "
					+ "visit.totalAmount > visit.paidAmount AND "
					+ "(:patientRid IS NULL OR pat.rid = :patientRid)")
	Page<EmrVisit> getOutstandingBalanceVisits(@Param("visitDateFrom") Date visitDateFrom,
			@Param("visitDateTo") Date visitDateTo, @Param("patientRid") Long patientRid, Pageable pageable);

	@Query(value = "SELECT visit FROM EmrVisit visit "
			+ "LEFT JOIN FETCH visit.emrPatientInfo pat "
			+ "WHERE "
			+ "((CAST(:visitDateFrom AS java.util.Date) IS NULL AND CAST(:visitDateTo AS java.util.Date) IS NULL) "
			+ "OR (visit.visitDate BETWEEN :visitDateFrom AND :visitDateTo)) AND "
			+ "visit.totalAmount > visit.paidAmount "
			+ "AND (:patientRid IS NULL OR pat.rid = :patientRid)")
	List<EmrVisit> getAllOutstandingBalances(@Param("visitDateFrom") Date visitDateFrom,
			@Param("visitDateTo") Date visitDateTo, @Param("patientRid") Long patientRid);

	@Query(value = "SELECT visit FROM EmrVisit visit "
			+ "LEFT JOIN FETCH visit.emrPatientInfo pat "
			+ "WHERE "
			+ "((CAST(:visitDateFrom AS java.util.Date) IS NULL AND CAST(:visitDateTo AS java.util.Date) IS NULL) "
			+ "OR (visit.visitDate BETWEEN :visitDateFrom AND :visitDateTo)) AND "
			+ "visit.totalAmount > visit.paidAmount AND "
			+ "visit.rid = :visitRid")
	List<EmrVisit> getPatientOutstandingBalances(@Param("visitDateFrom") Date visitDateFrom,
			@Param("visitDateTo") Date visitDateTo,
			@Param("visitRid") Long visitRid);

	@Modifying
	@Query("UPDATE EmrVisit visit SET visit.lkpOperationStatus = :lkpOperationStatus WHERE visit.rid IN :visitsRid")
	void updateOperationStatus(@Param("visitsRid") List<Long> visitsRid,
			@Param("lkpOperationStatus") LkpOperationStatus lkpOperationStatus);

	@Query("SELECT DISTINCT emr FROM EmrVisit emr "
			+ "LEFT JOIN FETCH emr.emrPatientInfo epi "
			+ "LEFT JOIN FETCH epi.gender "
			+ "LEFT JOIN FETCH emr.patientInsuranceInfo "
			+ "LEFT JOIN FETCH emr.providerPlan pp "
			+ "LEFT JOIN FETCH pp.insProvider ppip "
			+ "LEFT JOIN FETCH ppip.parentProvider "
			+ "LEFT JOIN FETCH emr.doctor "
			+ "LEFT JOIN FETCH emr.labSamples ls "
			+ "LEFT JOIN FETCH ls.labTestActualSet lta "
			+ "LEFT JOIN FETCH lta.lkpOperationStatus "
			+ "LEFT JOIN FETCH lta.testDefinition "
			+ "LEFT JOIN FETCH lta.billChargeSlipList bcs "
			+ "LEFT JOIN FETCH bcs.billPatientTransactionList bptl "
			+ "LEFT JOIN FETCH bptl.lkpTransactionType ltt "
			+ "LEFT JOIN FETCH bptl.lkpPaymentMethod lpm "
			+ "WHERE emr.rid = :visitRid "
			+ "AND emr.lkpOperationStatus.code NOT IN :unwantedVisits")
	EmrVisit getInvoiceData(@Param("visitRid") Long visitRid, @Param("unwantedVisits") List<String> unwantedVisits);

	@Query("SELECT emr FROM EmrVisit emr "
			+ "LEFT JOIN FETCH emr.labSamples ls "
			+ "LEFT JOIN FETCH emr.emrPatientInfo epi "
			+ "LEFT JOIN FETCH epi.gender "
			+ "LEFT JOIN FETCH emr.doctor "
			+ "LEFT JOIN FETCH ls.labTestActualSet lta "
			+ "LEFT JOIN FETCH lta.labTestActualResults ltar "
			+ "LEFT JOIN FETCH ltar.labResult lr "
			+ "LEFT JOIN FETCH lr.testDefinition td "
			+ "LEFT JOIN FETCH td.section "
			+ "LEFT JOIN FETCH lr.normalRanges nr "
			+ "LEFT JOIN FETCH ltar.labTestActual ltarlta "
			+ "LEFT JOIN FETCH ltarlta.labTestAnswerSet ltal "
			+ "LEFT JOIN FETCH ltal.testQuestion tq "
			+ "WHERE emr.rid = :visitRid "
			+ "AND lta.lkpOperationStatus.code IN (:operationStatus) "
			+ "ORDER BY td.rank, nr.printOrder, td.section.rank")
	EmrVisit getVisitResults(@Param("visitRid") Long visitRid,
			@Param("operationStatus") List<String> operationStatus);

	@Query("SELECT emr FROM EmrVisit emr "
			+ "LEFT JOIN FETCH emr.labSamples ls "
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
			+ "WHERE emr.rid = :visitRid "
			+ "AND lta.lkpOperationStatus.code NOT IN :operationStatus")
	EmrVisit getVisitWorksheets(@Param("visitRid") Long visitRid,
			@Param("operationStatus") List<String> operationStatus);

	@Query("SELECT emr FROM EmrVisit emr "
			+ "LEFT JOIN FETCH emr.labSamples ls "
			+ "LEFT JOIN FETCH emr.emrPatientInfo epi "
			+ "LEFT JOIN FETCH epi.gender "
			+ "LEFT JOIN FETCH emr.doctor "
			+ "LEFT JOIN FETCH ls.labTestActualSet lta "
			+ "LEFT JOIN FETCH lta.testDestination tdest "
			+ "LEFT JOIN FETCH tdest.type tdestt "
			+ "LEFT JOIN FETCH lta.testDefinition td "
			+ "LEFT JOIN FETCH td.testResults trs "
			+ "LEFT JOIN FETCH trs.normalRanges nr "
			+ "LEFT JOIN FETCH nr.sex "
			+ "WHERE emr.rid = :visitRid "
			+ "AND ls.lkpOperationStatus.code NOT IN (:unwantedSamples)")
	EmrVisit getVisitSamples(@Param("visitRid") Long visitRid,
			@Param("unwantedSamples") List<String> unwantedSamples);

	//"labTestActualSet.testDestination.type"

	//Get all visits fetched from the query getDailyCashPaymentsVisits(...), 
	//used because that query will not fetch any charge slips with no patient transaction, 
	//which is needed to calculate the right amount
	@Query("SELECT DISTINCT emr FROM EmrVisit emr "
			+ "LEFT JOIN FETCH emr.lkpOperationStatus "
			+ "LEFT JOIN FETCH emr.emrPatientInfo epi "
			+ "LEFT JOIN FETCH emr.labSamples ls "
			+ "LEFT JOIN FETCH ls.labTestActualSet lta "
			+ "LEFT JOIN FETCH lta.billChargeSlipList bcs "
			+ "LEFT JOIN FETCH bcs.billPatientTransactionList bptl "
			+ "LEFT JOIN FETCH bptl.lkpTransactionType ltt "
			+ "LEFT JOIN FETCH bptl.lkpPaymentMethod lpm "
			+ "WHERE emr.rid IN (:visitsRids)")
	List<EmrVisit> getDailyCashPayments(@Param("visitsRids") List<Long> visitsRids);

	//Get all visits rids with bill patient transactions in the selected period
	@Query("SELECT DISTINCT emr.rid FROM EmrVisit emr "
			+ "LEFT JOIN emr.lkpOperationStatus "
			+ "LEFT JOIN emr.emrPatientInfo epi "
			+ "LEFT JOIN emr.labSamples ls "
			+ "LEFT JOIN ls.labTestActualSet lta "
			+ "LEFT JOIN lta.billChargeSlipList bcs "
			+ "LEFT JOIN bcs.billPatientTransactionList bptl "
			+ "LEFT JOIN bptl.lkpTransactionType ltt "
			+ "LEFT JOIN bptl.lkpPaymentMethod lpm "
			+ "WHERE emr.branchId IN :branches "
			+ "AND emr.lkpOperationStatus.code NOT IN :cancelledVisits "
			+ "AND bptl.creationDate BETWEEN :dateFrom AND :dateTo "
			+ "AND (:paymentMethod IS NULL OR bptl.lkpPaymentMethod.code = :paymentMethod)")
	List<Long> getDailyCashPaymentsVisits(@Param("dateFrom") Date dateFrom,
			@Param("dateTo") Date dateTo,
			@Param("cancelledVisits") List<String> cancelledVisits,
			@Param("branches") List<Long> branches,
			@Param("paymentMethod") String paymentMethod);

	@Query("SELECT DISTINCT emr.rid FROM EmrVisit emr "
			+ "LEFT JOIN emr.lkpOperationStatus "
			+ "LEFT JOIN emr.providerPlan pp "
			+ "LEFT JOIN pp.insProvider ip "
			+ "LEFT JOIN emr.emrPatientInfo "
			+ "LEFT JOIN emr.labSamples ls "
			+ "LEFT JOIN ls.labTestActualSet lta "
			+ "LEFT JOIN lta.billChargeSlipList bcs "
			//+ "LEFT JOIN bcs.billPatientTransactionList bptl "
			+ "WHERE bcs.creationDate BETWEEN :fromDate AND :toDate "
			+ "AND emr.lkpOperationStatus.code NOT IN (:unwantedVisits) "
			+ "AND emr.branchId IN :branches")
	List<Long> getDailyIncomeFromChargeSlips(@Param("fromDate") Date fromDate,
			@Param("toDate") Date toDate,
			@Param("unwantedVisits") List<String> unwantedVisits,
			@Param("branches") List<Long> branches);

	@Query("SELECT DISTINCT emr.rid FROM EmrVisit emr "
			+ "LEFT JOIN emr.lkpOperationStatus "
			+ "LEFT JOIN emr.providerPlan pp "
			+ "LEFT JOIN pp.insProvider ip "
			+ "LEFT JOIN emr.emrPatientInfo "
			+ "LEFT JOIN emr.labSamples ls "
			+ "LEFT JOIN ls.labTestActualSet lta "
			+ "LEFT JOIN lta.billChargeSlipList bcs "
			+ "LEFT JOIN bcs.billPatientTransactionList bptl "
			+ "WHERE bptl.creationDate BETWEEN :fromDate AND :toDate "
			+ "AND emr.lkpOperationStatus.code NOT IN (:unwantedVisits) "
			+ "AND emr.branchId IN :branches")
	List<Long> getDailyIncomeFromPatientTransactions(@Param("fromDate") Date fromDate,
			@Param("toDate") Date toDate,
			@Param("unwantedVisits") List<String> unwantedVisits,
			@Param("branches") List<Long> branches);

	@Query("SELECT DISTINCT emr FROM EmrVisit emr "
			+ "LEFT JOIN FETCH emr.providerPlan pp "
			+ "LEFT JOIN FETCH pp.insProvider ip "
			+ "LEFT JOIN FETCH emr.emrPatientInfo "
			+ "LEFT JOIN FETCH emr.labSamples ls "
			+ "LEFT JOIN FETCH ls.labTestActualSet lta "
			+ "LEFT JOIN FETCH lta.billChargeSlipList bcs "
			+ "LEFT JOIN FETCH bcs.billPatientTransactionList bptl "
			+ "LEFT JOIN FETCH bptl.lkpTransactionType ltt "
			+ "WHERE emr.rid IN (:visitsRids)")
	List<EmrVisit> getDailyIncomeFromRids(@Param("visitsRids") List<Long> visitsRids);

	@Query("SELECT DISTINCT emr FROM EmrVisit emr "
			+ "LEFT JOIN FETCH emr.providerPlan pp "
			+ "LEFT JOIN FETCH pp.insProvider hcp "
			+ "LEFT JOIN FETCH emr.emrPatientInfo "
			+ "LEFT JOIN FETCH emr.labSamples ls "
			+ "LEFT JOIN FETCH ls.labTestActualSet lta "
			+ "LEFT JOIN FETCH lta.billChargeSlipList bcs "
			//+ "LEFT JOIN FETCH bcs.billPatientTransactionList bptl "
			//+ "LEFT JOIN FETCH bptl.lkpTransactionType ltt "
			+ "WHERE emr.visitDate BETWEEN :fromDate AND :toDate "
			+ "AND emr.providerPlan <> null "
			+ "AND emr.lkpOperationStatus.code NOT IN (:unwantedVisits) "
			+ "AND hcp.rid IN :insurances "
			+ "AND emr.branchId IN :branches")
	//+ "AND bcs.isAuthorized = true")
	List<EmrVisit> getClaimSummarized(@Param("fromDate") Date fromDate,
			@Param("toDate") Date toDate,
			@Param("unwantedVisits") List<String> unwantedVisits,
			@Param("insurances") List<Long> insurances,
			@Param("branches") List<Long> branches);

	@Query("SELECT DISTINCT emr FROM EmrVisit emr "
			+ "LEFT JOIN FETCH emr.providerPlan pp "
			+ "LEFT JOIN FETCH pp.insProvider hcp "
			+ "LEFT JOIN FETCH emr.patientInsuranceInfo "
			+ "LEFT JOIN FETCH emr.emrPatientInfo "
			+ "LEFT JOIN FETCH emr.labSamples ls "
			+ "LEFT JOIN FETCH ls.labTestActualSet lta "
			+ "LEFT JOIN FETCH lta.billChargeSlipList bcs "
			//+ "LEFT JOIN FETCH bcs.billPatientTransactionList bptl "
			//+ "LEFT JOIN FETCH bptl.lkpTransactionType ltt "
			+ "WHERE emr.visitDate BETWEEN :fromDate AND :toDate "
			+ "AND emr.providerPlan <> null "
			+ "AND emr.lkpOperationStatus.code NOT IN (:unwantedVisits) "
			+ "AND hcp.rid IN :insurances "
			+ "AND emr.branchId IN :branches")
	//+ "AND bcs.isAuthorized = true")
	List<EmrVisit> getClaimDetailed(@Param("fromDate") Date fromDate,
			@Param("toDate") Date toDate,
			@Param("unwantedVisits") List<String> unwantedVisits,
			@Param("insurances") List<Long> insurances,
			@Param("branches") List<Long> branches);

	@Query("SELECT DISTINCT emr.rid FROM EmrVisit emr "
			+ "LEFT JOIN emr.lkpOperationStatus "
			+ "LEFT JOIN emr.providerPlan pp "
			+ "LEFT JOIN pp.insProvider "
			+ "LEFT JOIN emr.emrPatientInfo "
			+ "LEFT JOIN emr.labSamples ls "
			+ "LEFT JOIN ls.labTestActualSet lta "
			+ "LEFT JOIN lta.billChargeSlipList bcs "
			//+ "LEFT JOIN bcs.billPatientTransactionList bptl "
			//+ "LEFT JOIN bptl.lkpTransactionType ltt "
			+ "WHERE bcs.creationDate BETWEEN :fromDate AND :toDate "
			+ "AND emr.lkpOperationStatus.code NOT IN (:unwantedVisits) "
			+ "AND emr.providerPlan <> null "
			//+ "AND ltt.code = :paymentType "
			+ "AND emr.branchId IN :branches "
			+ "AND bcs.isAuthorized = true")
	List<Long> getDailyCreditPaymentVisits(@Param("fromDate") Date fromDate,
			@Param("toDate") Date toDate,
			@Param("unwantedVisits") List<String> unwantedVisits,
			@Param("branches") List<Long> branches);

	@Query("SELECT DISTINCT emr FROM EmrVisit emr "
			+ "LEFT JOIN FETCH emr.lkpOperationStatus "
			+ "LEFT JOIN FETCH emr.providerPlan pp "
			+ "LEFT JOIN FETCH pp.insProvider "
			+ "LEFT JOIN FETCH emr.emrPatientInfo "
			+ "LEFT JOIN FETCH emr.labSamples ls "
			+ "LEFT JOIN FETCH ls.labTestActualSet lta "
			+ "LEFT JOIN FETCH lta.billChargeSlipList bcs "
			+ "LEFT JOIN FETCH bcs.billPatientTransactionList bptl "
			+ "LEFT JOIN FETCH bptl.lkpTransactionType ltt "
			+ "WHERE emr.rid IN (:visitsRids)")
	List<EmrVisit> getDailyCreditPayment(@Param("visitsRids") List<Long> visitsRids);

	@Query("SELECT DISTINCT emr.rid FROM EmrVisit emr "
			+ "LEFT JOIN emr.lkpOperationStatus los "
			+ "LEFT JOIN emr.emrPatientInfo epi "
			+ "WHERE "
			+ "emr.rid <> :currentVisitRid AND "
			+ "emr.visitDate >= :previousDate AND "
			+ "emr.visitDate <= :currentDate AND "
			+ "epi.rid = :patientRid AND "
			+ "los.code = :requestedStatus")
	List<Long> getWithinPreviousVisits(@Param("currentVisitRid") Long currentVisitRid, @Param("patientRid") Long patientRid,
			@Param("requestedStatus") String requestedStatus, @Param("previousDate") Date previousDate,
			@Param("currentDate") Date currentDate);

	@Query("SELECT DISTINCT emr FROM EmrVisit emr "
			+ "LEFT JOIN FETCH emr.lkpOperationStatus "
			+ "LEFT JOIN FETCH emr.emrPatientInfo "
			+ "LEFT JOIN FETCH emr.labSamples ls "
			+ "LEFT JOIN FETCH ls.labTestActualSet lta "
			+ "LEFT JOIN FETCH lta.lkpOperationStatus "
			+ "LEFT JOIN FETCH lta.testDefinition ltatd "
			+ "LEFT JOIN FETCH lta.testDestination td "
			+ "LEFT JOIN FETCH td.type tdt "
			+ "LEFT JOIN FETCH td.destinationBranch tddb "
			+ "LEFT JOIN FETCH lta.billChargeSlipList bcs "
			+ "WHERE emr.visitDate BETWEEN :fromDate AND :toDate "
			+ "AND tddb.rid IN (:insurances) "
			+ "AND emr.lkpOperationStatus.code NOT IN (:unwantedVisits) "
			+ "AND lta.lkpOperationStatus.code NOT IN (:unwantedTests) "
			+ "AND tdt.code <> :workbenchVisit")
	List<EmrVisit> getReferralOutVisits(@Param("fromDate") Date fromDate,
			@Param("toDate") Date toDate,
			@Param("insurances") List<Long> insurances,
			@Param("unwantedVisits") List<String> unwantedVisits,
			@Param("unwantedTests") List<String> unwantedTests,
			@Param("workbenchVisit") String workbenchVisit);

	@Query("SELECT DISTINCT emr FROM EmrVisit emr "
			+ "LEFT JOIN FETCH emr.providerPlan pp "
			+ "LEFT JOIN FETCH pp.insProvider ip "
			+ "WHERE "
			+ "ip.rid = :providerRid")
	List<EmrVisit> getVisitsByProvider(@Param("providerRid") Long providerRid);
}

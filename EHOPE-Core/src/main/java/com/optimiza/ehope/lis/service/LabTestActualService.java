package com.optimiza.ehope.lis.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import com.optimiza.core.base.helper.SearchCriterion;
import com.optimiza.core.base.helper.SearchCriterion.FilterOperator;
import com.optimiza.core.base.helper.SearchCriterion.JunctionOperator;
import com.optimiza.core.base.service.GenericService;
import com.optimiza.core.common.annotation.InterceptorFree;
import com.optimiza.core.common.business.exception.BusinessException;
import com.optimiza.core.common.business.exception.BusinessException.ErrorSeverity;
import com.optimiza.core.common.helper.FilterablePageRequest;
import com.optimiza.core.common.util.CollectionUtil;
import com.optimiza.core.common.util.SecurityUtil;
import com.optimiza.core.common.util.StringUtil;
import com.optimiza.core.lkp.service.LkpService;
import com.optimiza.ehope.lis.helper.EhopeRights;
import com.optimiza.ehope.lis.helper.SeparationFactorType;
import com.optimiza.ehope.lis.lkp.helper.OperationStatus;
import com.optimiza.ehope.lis.lkp.helper.TestDestinationType;
import com.optimiza.ehope.lis.lkp.model.LkpOperationStatus;
import com.optimiza.ehope.lis.lkp.service.LkpOperationStatusService;
import com.optimiza.ehope.lis.model.ActualResultNormalRange;
import com.optimiza.ehope.lis.model.EmrVisit;
import com.optimiza.ehope.lis.model.EmrVisitGroup;
import com.optimiza.ehope.lis.model.LabSample;
import com.optimiza.ehope.lis.model.LabTestActual;
import com.optimiza.ehope.lis.model.LabTestActualOperationHistory;
import com.optimiza.ehope.lis.model.LabTestActualResult;
import com.optimiza.ehope.lis.model.LabTestAnswer;
import com.optimiza.ehope.lis.model.TestDefinition;
import com.optimiza.ehope.lis.model.TestDestination;
import com.optimiza.ehope.lis.model.TestGroup;
import com.optimiza.ehope.lis.model.TestNormalRange;
import com.optimiza.ehope.lis.repo.LabTestActualRepo;

@Service("LabTestActualService")
public class LabTestActualService extends GenericService<LabTestActual, LabTestActualRepo> {

	@Autowired
	private LabTestActualRepo repo;
	@Autowired
	private LabSampleService labSampleService;
	@Autowired
	private LabTestActualResultService testActualResultService;
	@Autowired
	private LabTestAnswerService testAnswerService;
	@Autowired
	private LkpService lkpService;
	@Autowired
	private LabTestActualOperationHistoryService testActualOperationHistoryService;
	@Autowired
	private EmrVisitService emrVisitService;
	@Autowired
	private LkpOperationStatusService operationStatusService;
	@Autowired
	private LabBranchService branchService;
	@Autowired
	private BillPatientTransactionService patientTransactionService;
	@Autowired
	private TestDefinitionService testDefinitionService;
	@Autowired
	private TestDestinationService testDestinationService;
	@Autowired
	private EmrVisitGroupService visitGroupService;
	@Autowired
	private ActualResultNormalRangeService actualResultNormalRangeService;
	@Autowired
	private ActualTestArtifactService actualTestArtifactService;
	@Autowired
	private BillChargeSlipService billChargeSlipService;
	@Autowired
	private LabSeparationFactorService separationFactorService;
	@Autowired
	private EntityManager entityManager;

	@Override
	protected LabTestActualRepo getRepository() {
		return repo;
	}

	public List<LabTestActual> findActualsChargeSlip(Long visitRid) {
		return new ArrayList<LabTestActual>(getRepository().findActualsChargeSlip(visitRid, OperationStatus.ABORTED.getValue()));
	}

	public Set<LabTestActual> getTestActualsDestinations(Long visitRid) {
		List<String> excludedStatuses = Arrays.asList(OperationStatus.ABORTED.getValue(), OperationStatus.CANCELLED.getValue());
		return getRepository().findActualsDestinations(visitRid, excludedStatuses);
	}

	/**
	 * 
	 * @param visitRid
	 * @param fetchedStatus : nullable
	 * @return List
	 */
	public Set<LabTestActual> findTestActualListWithResultsByVisit(Long visitRid, OperationStatus fetchedStatus) {
		List<String> excludedStatuses = Arrays.asList(OperationStatus.CANCELLED.getValue(), OperationStatus.ABORTED.getValue());
		TreeSet<LabTestActual> output = new TreeSet<LabTestActual>(
				getRepository().findTestActualListWithResultsByVisit(visitRid, excludedStatuses,
						fetchedStatus != null ? fetchedStatus.getValue() : null));
		output.forEach(lta -> lta.setLabTestActualResults(new TreeSet<LabTestActualResult>(lta.getLabTestActualResults())));
		entityManager.clear();//this function updates the LabTestActual
		return output;
	}

	/**
	 * check max amount if we will merge requested visits together
	 * 
	 * @param visitRid
	 * @param patientRid
	 * @param selectedTests
	 */
	private void checkPrevVisitsDiffSampleAmount(Long visitRid, Long patientRid, List<TestDefinition> selectedTests) {
		if (!separationFactorService.isFactorActive(SeparationFactorType.MINUTES_30)) {
			return;
		}
		List<Long> openVisitsRid = emrVisitService.getOpenVisits(visitRid, patientRid);
		if (CollectionUtil.isCollectionEmpty(openVisitsRid)) {
			return;
		}
		List<LabTestActual> dummyActuals = new ArrayList<>();
		long dummyRid = -1;
		for (TestDefinition td : selectedTests) {
			LabTestActual lta = new LabTestActual();
			lta.setTestDefinition(td);
			lta.setRid(dummyRid--);
			dummyActuals.add(lta);
		}
		List<LabTestActual> testActuals = new ArrayList<>(dummyActuals);
		Set<EmrVisit> openVisits = emrVisitService	.find(Arrays.asList(new SearchCriterion("rid", openVisitsRid, FilterOperator.in)),
				EmrVisit.class, "labSamples.labTestActualSet.testDefinition", "labSamples.labTestActualSet.sourceActualTest").stream()
													.collect(Collectors.toSet());
		for (EmrVisit prevVisit : openVisits) {
			if (CollectionUtil.isCollectionEmpty(prevVisit.getLabSamples())) {
				continue;
			}
			for (LabSample sample : prevVisit.getLabSamples()) {
				if (CollectionUtil.isCollectionEmpty(sample.getLabTestActualSet())) {
					continue;
				}
				testActuals.addAll(sample.getLabTestActualSet());
			}

		}
		verifyTestsMaxAmount(testActuals);
	}

	/**
	 * This method is used after test selection
	 * 
	 * @param currentVisit
	 * @param currentSelectedTestsList
	 * @return List LabTestActual
	 */
	@PreAuthorize("hasAuthority('" + EhopeRights.ADD_TEST_ACTUAL + "')")
	public List<LabTestActual> createTestActual(Long visitRid, List<TestDefinition> selectedTestsList, List<TestGroup> selectedGroups) {
		if (CollectionUtil.isCollectionEmpty(selectedTestsList)) {
			return new ArrayList<>();
		}
		EmrVisit visit = emrVisitService.findOne(SearchCriterion.generateRidFilter(visitRid, FilterOperator.eq), EmrVisit.class,
				"emrPatientInfo");
		checkPrevVisitsDiffSampleAmount(visitRid, visit.getEmrPatientInfo().getRid(), selectedTestsList);
		visitGroupService.deleteAllByEmrVisit(visit);
		entityManager.flush();
		if (!CollectionUtil.isCollectionEmpty(selectedGroups)) {
			List<EmrVisitGroup> visitGroups = new ArrayList<>();
			for (TestGroup tg : selectedGroups) {
				EmrVisitGroup evg = new EmrVisitGroup();
				evg.setEmrVisit(visit);
				evg.setTestGroup(tg);
				visitGroups.add(evg);
			}
			visitGroupService.createVisitGroups(visitGroups);
		}
		LabSample dummySample = labSampleService.findOne(
				Arrays.asList(new SearchCriterion("emrVisit.rid", visit.getRid(), FilterOperator.eq),
						new SearchCriterion("isDummy", Boolean.TRUE, FilterOperator.eq)),
				LabSample.class, "emrVisit");
		if (dummySample == null) {
			dummySample = labSampleService.generateSample(visit);
			dummySample.setIsDummy(Boolean.TRUE);
			labSampleService.createLabSample(dummySample);
		}
		LkpOperationStatus requestedOperationStatus = lkpService.findOneAnyLkp(
				Arrays.asList(new SearchCriterion("code", OperationStatus.REQUESTED.getValue(), FilterOperator.eq)),
				LkpOperationStatus.class);
		List<TestDestination> testDestinations = testDestinationService.getTestDestinationsByTestDefs(
				selectedTestsList.stream().map(TestDefinition::getRid).collect(Collectors.toList()));
		List<LabTestActual> patientActualTestList = new ArrayList<LabTestActual>();
		for (TestDefinition patientTest : selectedTestsList) {
			if (!testDefinitionService.determineTestDefinitionSelectability(patientTest)) {
				throw new BusinessException("Can't select an invalid test", "invalidTest", ErrorSeverity.ERROR);
			}
			LabTestActual newActual = new LabTestActual();
			newActual.setLabSample(dummySample);
			newActual.setTestDefinition(patientTest);
			newActual.setLkpOperationStatus(requestedOperationStatus);
			setDummyDestination(newActual, patientTest, testDestinations);
			newActual = getRepository().save(newActual);
			patientActualTestList.add(newActual);
			testActualResultService.addTestActualResult(patientTest, newActual);
		}

		testActualOperationHistoryService.createTestActualOperationHistory(patientActualTestList, null);
		return patientActualTestList;
	}

	/**
	 * Set a destination inside the testActual, default value is workbench destination otherwise random destination
	 * 
	 * @param testActual
	 * @param test
	 * @param testDestinations
	 */
	private void setDummyDestination(LabTestActual testActual, TestDefinition test, List<TestDestination> testDestinations) {
		TestDestination destination = null;
		for (TestDestination td : testDestinations) {
			if (!td.getTestDefinition().equals(test)) {
				continue;
			}
			destination = td;
			TestDestinationType type = TestDestinationType.valueOf(td.getType().getCode());
			if (type.equals(TestDestinationType.WORKBENCH)) {
				break;
			}

		}
		destination.setNormalRanges(new HashSet<>());//self reference overflow
		testActual.setTestDestination(destination);
	}

	public List<LabTestActual> updateActualTestsDestinations(Map<Long, TestDestination> testActualsDestinations) {
		List<LabTestActual> testActuals = getRepository().find(
				Arrays.asList(new SearchCriterion("rid", testActualsDestinations.keySet().stream().collect(Collectors.toList()),
						FilterOperator.in)),
				LabTestActual.class, "labSample.labTestActualSet", "labSample.emrVisit",
				"lkpOperationStatus", "testDefinition.testSpecimens.containerType");
		EmrVisit visit = testActuals.get(0).getLabSample().getEmrVisit();
		Boolean revalidateStatus = Boolean.FALSE;
		for (LabTestActual lta : testActuals) {
			lta.setTestDestination(testActualsDestinations.get(lta.getRid()));
			//means we didn't separate patient tests into samples yet, which means this code only will work in EditOrder
			//also if this test is the only one in the sample then no need for a new sample
			if (lta.getLabSample().getLabTestActualSet().size() > 1 && !lta.getLabSample().getIsDummy()
					&& !labSampleService.isTestValidInSample(visit.getRid(), lta.getRid())) {
				revalidateStatus = Boolean.TRUE;
				LabSample ls = labSampleService.generateSample(visit);
				ls.setLkpOperationStatus(lta.getLkpOperationStatus());
				ls.setLkpContainerType(lta	.getTestDefinition()
											.getTestSpecimens().iterator().next().getContainerType());
				ls = labSampleService.updateLabSample(ls, null);//not using create to avoid setting REQUESTED if we used createLabSample(...)
				lta.setLabSample(ls);
				ls.addToLabTestActualSet(lta);
			}
		}
		testActuals = getRepository().save(testActuals);
		for (LabTestActual lta : testActuals) {
			List<LabTestActualResult> actualResults = testActualResultService.find(
					Arrays.asList(new SearchCriterion("labTestActual.rid", lta.getRid(), FilterOperator.eq)),
					LabTestActualResult.class, "labResult", "labTestActual");
			actualResultNormalRangeService.deleteAllByActualTest(lta);
			for (LabTestActualResult ltar : actualResults) {

				Set<TestNormalRange> normalRanges = testActualResultService.getNormalRangesForGeneration(lta.getRid(),
						ltar.getLabResult().getRid(),
						lta.getTestDestination().getRid());
				testActualResultService.saveActualResultNormalRanges(ltar, normalRanges);
				ltar.setNormalRangeText(testActualResultService.generateNormalRangeTxt(ltar.getLabResult().getRid(), ltar,
						Boolean.FALSE, normalRanges));
			}
			lta.getTestDestination().setNormalRanges(new HashSet<>());//stack over flow
		}
		if (revalidateStatus) {
			//flushing here because revalidateVisitStatus(...) wont see the new created samples 
			entityManager.flush();
			entityManager.clear();
			visit = emrVisitService.revalidateVisitStatus(visit.getRid());
		}
		return testActuals;
	}

	public List<LabTestActual> updateTestActual(List<LabTestActual> testActualList, String comment) {
		testActualList = getRepository().save(testActualList);
		testActualOperationHistoryService.createTestActualOperationHistory(testActualList, comment);
		return testActualList;
	}

	public LabTestActual updateTestActual(LabTestActual newActualTest, String comment) {
		newActualTest = getRepository().save(newActualTest);
		testActualOperationHistoryService.createTestActualOperationHistory(Arrays.asList(newActualTest), comment);
		return newActualTest;
	}

	public void deleteTestActualsByRid(List<Long> testActualsRid) {
		if (CollectionUtil.isCollectionEmpty(testActualsRid)) {
			return;
		}
		List<LabTestActual> testActualList = getRepository().find(
				Arrays.asList(new SearchCriterion("rid", testActualsRid, FilterOperator.in)), LabTestActual.class);
		for (LabTestActual testActual : testActualList) {
			actualTestArtifactService.deleteAllByTestActual(testActual);
			actualResultNormalRangeService.deleteAllByActualTest(testActual);
			testActualResultService.deleteAllByLabTestActual(testActual);
			testAnswerService.deleteAllByTestActual(testActual);
			testActualOperationHistoryService.deleteAllByTestActual(testActual);
		}
		getRepository().delete(testActualList);
	}

	public void reorderActualTests(List<Long> toReorderActualRids) {
		for (Long toReorderActualRid : toReorderActualRids) {
			reorderActualTest(toReorderActualRid);
		}
	}

	private LabTestActual reorderActualTest(Long toReorderActualRid) {
		//Fetch old actual-test
		LabTestActual oldActualTest = getRepository().findOne(SearchCriterion.generateRidFilter(toReorderActualRid, FilterOperator.eq),
				LabTestActual.class,
				"labTestActualResults.actualResultNormalRanges", "labTestAnswerSet", "billChargeSlipList",
				"testDefinition", "labSample.emrVisit");
		List<LabTestActual> reorderedActualTests = getRepository().find(Arrays.asList(
				new SearchCriterion("rid", toReorderActualRid, FilterOperator.neq),
				new SearchCriterion("labSample", oldActualTest.getLabSample(), FilterOperator.eq),
				new SearchCriterion("testDefinition.standardCode", oldActualTest.getTestDefinition().getStandardCode(), FilterOperator.eq),
				new SearchCriterion("lkpOperationStatus.code", OperationStatus.CANCELLED.getValue(), FilterOperator.neq),
				new SearchCriterion("lkpOperationStatus.code", OperationStatus.ABORTED.getValue(), FilterOperator.neq)),
				LabTestActual.class);
		EmrVisit visit = oldActualTest.getLabSample().getEmrVisit();

		if (!oldActualTest.getTestDefinition().getIsAllowRepetitionSameSample()) {
			throw new BusinessException("Reordering this test on the same sample is not allowed!",
					"sameSampleRepetitionNotAllowed", ErrorSeverity.ERROR);
		}
		if (!CollectionUtil.isCollectionEmpty(reorderedActualTests)
				&& reorderedActualTests.size() >= oldActualTest.getTestDefinition().getSameSampleMaxAmount()) {
			throw new BusinessException("Reached maximum number of reordered tests ["
					+ oldActualTest.getTestDefinition().getStandardCode()
					+ "] for the sample with barcode [" + oldActualTest.getLabSample().getBarcode() + "]!",
					"reachedMaxReorderedTestsForSameSample", ErrorSeverity.ERROR,
					Arrays.asList(oldActualTest.getTestDefinition().getStandardCode(), oldActualTest.getLabSample().getBarcode()));
		}

		//statuses required for test and its history creations
		List<LkpOperationStatus> operationStatuses = lkpService.findAnyLkp(
				Arrays.asList(new SearchCriterion("code", OperationStatus.REQUESTED.getValue(), FilterOperator.eq, JunctionOperator.Or),
						new SearchCriterion("code", OperationStatus.VALIDATED.getValue(), FilterOperator.eq, JunctionOperator.Or),
						new SearchCriterion("code", OperationStatus.COLLECTED.getValue(), FilterOperator.eq, JunctionOperator.Or)),
				LkpOperationStatus.class, null);

		LkpOperationStatus requestedStatus = operationStatuses.stream().filter(
				o -> OperationStatus.valueOf(o.getCode()) == OperationStatus.REQUESTED).findFirst().get();
		LkpOperationStatus validatedStatus = operationStatuses.stream().filter(
				o -> OperationStatus.valueOf(o.getCode()) == OperationStatus.VALIDATED).findFirst().get();
		LkpOperationStatus collectedStatus = operationStatuses.stream().filter(
				o -> OperationStatus.valueOf(o.getCode()) == OperationStatus.COLLECTED).findFirst().get();

		//Copy actual-test
		LabTestActual newActualTest = new LabTestActual();
		BeanUtils.copyProperties(oldActualTest, newActualTest, "rid", "version", "creationDate", "createdBy", "updatedDate", "updatedBy",
				"artifacts", "labTestActualResults", "labTestAnswerSet", "billChargeSlipList", "reorderedActualTests");
		newActualTest.setLkpOperationStatus(collectedStatus);
		newActualTest.setSourceActualTest(oldActualTest);
		newActualTest = getRepository().save(newActualTest);

		//Copy results
		Set<LabTestActualResult> oldResults = oldActualTest.getLabTestActualResults();
		for (LabTestActualResult oldResult : oldResults) {
			LabTestActualResult newResult = new LabTestActualResult();
			BeanUtils.copyProperties(oldResult, newResult, "rid", "version", "creationDate", "createdBy", "updatedDate", "updatedBy",
					"isConfirmed", "normality", "primaryResultValue", "normalRangeText", "primaryResultParsed", "percentage",
					"resultSourceId", "secondaryResultParsed", "ratio", "comments", "narrativeText", "labTestActual", "testCodedResult",
					"isAmended", "amendmentReason", "organismDetection", "actualAntiMicrobials", "actualOrganisms",
					"actualResultNormalRanges");
			newResult.setLabTestActual(newActualTest);
			newResult = testActualResultService.save(newResult);
			//Copy actual-result-normal-ranges
			Set<ActualResultNormalRange> oldActualResultNormalRanges = oldResult.getActualResultNormalRanges();
			for (ActualResultNormalRange oldActualResultNormalRange : oldActualResultNormalRanges) {
				ActualResultNormalRange newActualResultNormalRange = new ActualResultNormalRange();
				BeanUtils.copyProperties(oldActualResultNormalRange, newActualResultNormalRange, "rid", "version", "creationDate",
						"createdBy", "updatedDate", "updatedBy", "actualResult");
				newActualResultNormalRange.setActualResult(newResult);
				actualResultNormalRangeService.saveActualResultNormalRange(newActualResultNormalRange);
			}
		}

		//Copy answers
		Set<LabTestAnswer> oldAnswers = oldActualTest.getLabTestAnswerSet();
		for (LabTestAnswer oldAnswer : oldAnswers) {
			LabTestAnswer newAnswer = new LabTestAnswer();
			BeanUtils.copyProperties(oldAnswer, newAnswer, "rid", "version", "creationDate", "createdBy", "updatedDate", "updatedBy",
					"labTestActual");
			newAnswer.setLabTestActual(newActualTest);
			testAnswerService.saveTestAnswer(newAnswer);
		}
		//add charge slip
		if (oldActualTest.getTestDefinition().getIsSameSampleChargeable()) {
			billChargeSlipService.duplicateTestChargeSlips(newActualTest.getTestDefinition().getRid(), newActualTest, visit.getRid());
		}
		//create operation history
		List<LabTestActualOperationHistory> newActualTestOperationHistories = new ArrayList<>();
		LabTestActualOperationHistory reqTestOperationHistory = new LabTestActualOperationHistory();
		reqTestOperationHistory.setOldOperationStatus(requestedStatus);
		reqTestOperationHistory.setNewOperationStatus(requestedStatus);
		newActualTestOperationHistories.add(reqTestOperationHistory);
		LabTestActualOperationHistory valTestOperationHistory = new LabTestActualOperationHistory();
		valTestOperationHistory.setOldOperationStatus(requestedStatus);
		valTestOperationHistory.setNewOperationStatus(validatedStatus);
		newActualTestOperationHistories.add(valTestOperationHistory);
		LabTestActualOperationHistory colTestOperationHistory = new LabTestActualOperationHistory();
		colTestOperationHistory.setOldOperationStatus(validatedStatus);
		colTestOperationHistory.setNewOperationStatus(collectedStatus);
		newActualTestOperationHistories.add(colTestOperationHistory);
		for (LabTestActualOperationHistory ltaoh : newActualTestOperationHistories) {
			ltaoh.setLabTestActual(newActualTest);
			ltaoh.setOperationDate(new Date());
			ltaoh.setOperationBy(SecurityUtil.getCurrentUser().getRid());
		}
		testActualOperationHistoryService.createTestActualOperationHistory(newActualTestOperationHistories);

		//TODO send to machine, if we sent to machine then no need for revalidateVisitStatus(...)
		entityManager.flush();
		entityManager.clear();
		emrVisitService.revalidateVisitStatus(visit.getRid());
		return newActualTest;
	}

	public Set<LabTestActual> getTestsBySampleOrderManagement(Long sampleRid) {
		List<SearchCriterion> filters = new ArrayList<>();
		filters.add(new SearchCriterion("labSample.rid", sampleRid, FilterOperator.eq));
		filters.add(new SearchCriterion("lkpOperationStatus.code", OperationStatus.ABORTED.getValue(), FilterOperator.neq));
		return new HashSet<>(
				getRepository().find(filters, LabTestActual.class, "labSample", "lkpOperationStatus", "testDefinition.section"));
	}

	@PreAuthorize("hasAuthority(#newOperationStatus.getValue().concat('" + EhopeRights._OPERATION_STATUS + "'))")
	public Set<LabTestActual> propegateTestsStatuses(Long visitRid, List<Long> testsToPropegate, OperationStatus newOperationStatus,
			String comment) {
		EmrVisit visit = emrVisitService.findOneOrderSampleTestStatus(visitRid);
		Set<LabSample> samplesToChange = new HashSet<>();
		Set<LabTestActual> testActualsToChange = new HashSet<>();
		LkpOperationStatus newOperationStatusLkp = lkpService.findOneAnyLkp(
				Arrays.asList(new SearchCriterion("code", newOperationStatus.getValue(), FilterOperator.eq)), LkpOperationStatus.class);
		for (LabSample sample : visit.getLabSamples()) {
			for (LabTestActual testActual : sample.getLabTestActualSet()) {
				if (!testsToPropegate.contains(testActual.getRid())) {
					continue;
				}
				OperationStatus currentOperationStatus = OperationStatus.valueOf(testActual.getLkpOperationStatus().getCode());
				if (newOperationStatus == currentOperationStatus) {
					continue;
				}
				operationStatusService.validations(currentOperationStatus, newOperationStatus, visitRid);

				//Check if results were entered, we don't need to check other tests in sample because sample is least child status
				if (newOperationStatus == OperationStatus.RESULTS_ENTERED) {
					List<LabTestActual> testActualsResults = testActualResultService.getTestActualListWithRequiredResults(visitRid);
					testActualsResults.removeIf(t -> !t.getRid().equals(testActual.getRid()));
					if (CollectionUtil.isCollectionEmpty(testActualsResults)) {
						throw new BusinessException("Not All Tests has results", "testNoResults",
								ErrorSeverity.ERROR, Arrays.asList(testActual.getTestDefinition().getStandardCode()));
					}
				}
				testActual.setLkpOperationStatus(newOperationStatusLkp);
				testActualsToChange.add(testActual);
				samplesToChange.add(testActual.getLabSample());
			}
		}
		//Nothing to change
		if (CollectionUtil.isCollectionEmpty(testActualsToChange)) {
			return new HashSet<>();
		}
		for (LabSample sample : samplesToChange) {

			LkpOperationStatus newSampleLkpStatus = operationStatusService.getLkpBySmallestOperationStatuses(
					sample	.getLabTestActualSet().stream().map(lta -> OperationStatus.valueOf(lta.getLkpOperationStatus().getCode()))
							.collect(Collectors.toList()));
			sample.setLkpOperationStatus(newSampleLkpStatus);
		}
		LkpOperationStatus newVisitLkpStatus = operationStatusService.getLkpBySmallestOperationStatuses(
				visit	.getLabSamples().stream().map(lta -> OperationStatus.valueOf(lta.getLkpOperationStatus().getCode()))
						.collect(Collectors.toList()));
		visit.setLkpOperationStatus(newVisitLkpStatus);

		visit = emrVisitService.updateVisit(visit, comment);
		labSampleService.updateLabSample(new ArrayList<>(samplesToChange), comment);
		testActualsToChange = new HashSet<>(updateTestActual(new ArrayList<>(testActualsToChange), comment));

		if (newOperationStatus == OperationStatus.CANCELLED) {
			patientTransactionService.cancelPayment(visit.getRid(), comment,
					testActualsToChange.stream().map(LabTestActual::getRid).collect(Collectors.toList()));
		}
		visit = emrVisitService.closeVisit(visit, OperationStatus.valueOf(newVisitLkpStatus.getCode()));
		return testActualsToChange;
	}

	public Set<LabTestActual> propegateTestsStatusesNoAuth(Long visitRid, List<Long> testsToPropegate, OperationStatus newOperationStatus,
			String comment) {
		return propegateTestsStatuses(visitRid, testsToPropegate, newOperationStatus, comment);
	}

	@PreAuthorize("hasAuthority('" + EhopeRights.VIEW_ORDER_MANAGEMENT + "')")
	public Page<LabTestActual> getTestOrderManagementData(FilterablePageRequest filterablePageRequest) {

		String type = filterablePageRequest.getStringFilter("type");//to know what type we are filtering
		filterablePageRequest.getFilters().removeIf(sc -> sc.getField().equals("type"));//remove this filter
		//pull the filters to use in @query 
		String admissionNumber = filterablePageRequest.getStringFilter("admissionNumber");
		String visitTypeCode = filterablePageRequest.getStringFilter("visitTypeCode");
		String barcode = filterablePageRequest.getStringFilter("barcode");

		String fileNo = filterablePageRequest.getStringFilter("fileNo");
		String mobileNo = filterablePageRequest.getStringFilter("mobileNo");
		String firstName = filterablePageRequest.getStringFilter("firstName");
		String lastName = filterablePageRequest.getStringFilter("lastName");
		Long nationalId = filterablePageRequest.getLongFilter("nationalId");

		String standardCode = filterablePageRequest.getStringFilter("standardCode");
		String description = filterablePageRequest.getStringFilter("description");
		String aliases = filterablePageRequest.getStringFilter("aliases");
		String secondaryCode = filterablePageRequest.getStringFilter("secondaryCode");

		Long patientRid = filterablePageRequest.getLongFilter("patientRid");
		Long visitRid = filterablePageRequest.getLongFilter("visitRid");
		Long sampleRid = filterablePageRequest.getLongFilter("sampleRid");
		Long testRid = filterablePageRequest.getLongFilter("testRid");

		Date visitDateFrom = emrVisitService.getDateFromToFilter(filterablePageRequest, "visitDateFrom");
		Date visitDateTo = emrVisitService.getDateFromToFilter(filterablePageRequest, "visitDateTo");
		Page<LabTestActual> page = null;
		//if type is empty then autocomplete was not used and we used advanced search
		if (StringUtil.isEmpty(type)) {
			page = repo.findOrderManagementDataGeneral(OperationStatus.ABORTED.getValue(), visitTypeCode, admissionNumber, barcode,
					nationalId,
					fileNo,
					mobileNo, firstName, lastName,
					standardCode, description, aliases, secondaryCode, visitDateFrom, visitDateTo, filterablePageRequest.getPageRequest());
		} else {
			//means here the user clicked on a specific row in auto complete
			if (patientRid != null || visitRid != null || sampleRid != null || testRid != null) {
				page = repo.findOrderManagementDataRid(OperationStatus.ABORTED.getValue(), visitTypeCode, visitDateFrom, visitDateTo,
						visitRid,
						patientRid,
						sampleRid, testRid,
						filterablePageRequest.getPageRequest());
			} else {
				//we clicked "search" in auto complete
				if (type.equals("PATIENT")) {
					page = getRepository().findOrderManagementDataPatient(OperationStatus.ABORTED.getValue(), nationalId, fileNo, mobileNo,
							firstName, lastName, visitDateFrom, visitDateTo, filterablePageRequest.getPageRequest());
				} else if (type.equals("VISIT")) {
					page = getRepository().findOrderManagementDataVisit(OperationStatus.ABORTED.getValue(), admissionNumber, visitDateFrom,
							visitDateTo, filterablePageRequest.getPageRequest());
				} else if (type.equals("SAMPLE")) {
					page = getRepository().findOrderManagementDataSample(OperationStatus.ABORTED.getValue(), barcode, visitDateFrom,
							visitDateTo, filterablePageRequest.getPageRequest());
				} else if (type.equals("TEST")) {
					page = getRepository().findOrderManagementDataTest(OperationStatus.ABORTED.getValue(), standardCode, description,
							aliases, secondaryCode, visitDateFrom, visitDateTo, filterablePageRequest.getPageRequest());
				}
			}
		}
		if (page.getNumberOfElements() == 0) {
			return page;
		}

		//fetch the required data
		List<LabTestActual> visits = getRepository().find(Arrays.asList(new SearchCriterion("rid",
				page.getContent().stream().map(LabTestActual::getRid).collect(Collectors.toList()), FilterOperator.in)),
				LabTestActual.class, filterablePageRequest.getSortObject(),
				"lkpOperationStatus", "testDefinition.section", "labSample.lkpOperationStatus", "labSample.lkpContainerType",
				"labSample.emrVisit.lkpOperationStatus", "labSample.emrVisit.doctor", "labSample.emrVisit.providerPlan",
				"labSample.emrVisit.visitType", "labSample.emrVisit.emrPatientInfo.gender").stream()
													.distinct().collect(Collectors.toList());

		for (LabTestActual lta : page.getContent()) {
			lta.getLabSample().getEmrVisit().setBranch(branchService.findById(lta.getLabSample().getEmrVisit().getBranchId()));
		}
		Page<LabTestActual> fetchedPage = new PageImpl<LabTestActual>(visits, filterablePageRequest.getPageRequest(),
				page.getTotalElements());
		return fetchedPage;

	}

	@InterceptorFree
	public List<LabTestActual> findPreviousTestActual(Long patientRid, String testCode, Long currentLabTestActualRid,
			Integer neededPreviousResults) {
		try {
			List<String> filterString = Arrays.asList(OperationStatus.FINALIZED.getValue(), OperationStatus.RESULTS_ENTERED.getValue(),
					OperationStatus.CLOSED.getValue());

			List<LabTestActual> labTestActual = new ArrayList<LabTestActual>();
			labTestActual.addAll(repo
										.findPreviousTestActual(patientRid, testCode, currentLabTestActualRid,
												new PageRequest(0, neededPreviousResults), filterString)
										.getContent());
			return labTestActual;
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * To verify max amounts for repetition tests and normal tests with repetition off
	 * 
	 * @param testActuals : must has test definition and source actual test fetched
	 */
	public void verifyTestsMaxAmount(List<LabTestActual> testActuals) {
		//check tests with repetition different sample is on
		Map<Long, Integer> maxAmountTests = new HashMap<>();
		for (LabTestActual lta : testActuals) {
			TestDefinition td = lta.getTestDefinition();
			if (!td.getIsAllowRepetitionDifferentSample() || lta.getSourceActualTest() != null) {
				continue;
			}
			if (maxAmountTests.containsKey(td.getRid())) {
				if ((maxAmountTests.get(td.getRid()) + 1) > td.getDifferentSampleMaxAmount()) {
					throw new BusinessException("Picked more tests than allowed amount", "testRepetitionMaxAmount", ErrorSeverity.ERROR);
				} else {
					maxAmountTests.put(td.getRid(), maxAmountTests.get(td.getRid()) + 1);
				}
			} else {
				maxAmountTests.put(td.getRid(), new Integer(0));
			}
		}
		//check normal tests that has all repetition off
		for (LabTestActual lta : testActuals) {
			TestDefinition td = lta.getTestDefinition();
			if (td.getIsAllowRepetitionDifferentSample() || td.getIsAllowRepetitionSameSample()) {
				continue;
			}
			for (LabTestActual testActual : testActuals) {
				if (!testActual.getRid().equals(lta.getRid())
						&& testActual.getTestDefinition().getRid().equals(lta.getTestDefinition().getRid())) {
					throw new BusinessException("Picked more tests than allowed amount", "testRepetitionMaxAmount", ErrorSeverity.ERROR);
				}
			}
		}
	}

}

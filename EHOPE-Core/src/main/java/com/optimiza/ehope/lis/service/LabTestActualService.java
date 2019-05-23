package com.optimiza.ehope.lis.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import com.optimiza.core.base.helper.SearchCriterion;
import com.optimiza.core.base.helper.SearchCriterion.FilterOperator;
import com.optimiza.core.base.service.GenericService;
import com.optimiza.core.common.annotation.InterceptorFree;
import com.optimiza.core.common.business.exception.BusinessException;
import com.optimiza.core.common.business.exception.BusinessException.ErrorSeverity;
import com.optimiza.core.common.helper.FilterablePageRequest;
import com.optimiza.core.common.util.CollectionUtil;
import com.optimiza.core.common.util.StringUtil;
import com.optimiza.core.lkp.service.LkpService;
import com.optimiza.ehope.lis.helper.EhopeRights;
import com.optimiza.ehope.lis.lkp.helper.OperationStatus;
import com.optimiza.ehope.lis.lkp.helper.TestDestinationType;
import com.optimiza.ehope.lis.lkp.model.LkpOperationStatus;
import com.optimiza.ehope.lis.lkp.service.LkpOperationStatusService;
import com.optimiza.ehope.lis.model.EmrVisit;
import com.optimiza.ehope.lis.model.EmrVisitGroup;
import com.optimiza.ehope.lis.model.LabSample;
import com.optimiza.ehope.lis.model.LabTestActual;
import com.optimiza.ehope.lis.model.LabTestActualResult;
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
	private EntityManager entityManager;

	@Override
	protected LabTestActualRepo getRepository() {
		return repo;
	}

	public List<LabTestActual> findActualsChargeSlip(Long visitRid) {
		return new ArrayList<LabTestActual>(repo.findActualsChargeSlip(visitRid, OperationStatus.ABORTED.getValue()));
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
				repo.findTestActualListWithResultsByVisit(visitRid, excludedStatuses,
						fetchedStatus != null ? fetchedStatus.getValue() : null));
		output.forEach(lta -> lta.setLabTestActualResults(new TreeSet<LabTestActualResult>(lta.getLabTestActualResults())));
		entityManager.clear();//this function updates the LabTestActual
		return output;
	}

	/**
	 * This method is used after test selection
	 * 
	 * @param currentVisit
	 * @param currentSelectedTestsList
	 * @return List LabTestActual
	 */
	@PreAuthorize("hasAuthority('" + EhopeRights.ADD_TEST_ACTUAL + "')")
	public List<LabTestActual> createTestActual(EmrVisit visit, List<TestDefinition> selectedTestsList, List<TestGroup> selectedGroups) {
		if (CollectionUtil.isCollectionEmpty(selectedTestsList)) {
			return new ArrayList<>();
		}
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
			newActual = repo.save(newActual);
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

	public void deleteTestActual(Long id) {
		repo.delete(id);
	}

	public void deleteTestActualList(Collection<LabTestActual> testActualList) {
		if (CollectionUtil.isCollectionEmpty(testActualList)) {
			return;
		}
		for (LabTestActual testActual : testActualList) {
			actualResultNormalRangeService.deleteAllByActualTest(testActual);
			testActualResultService.deleteAllByLabTestActual(testActual);
			testAnswerService.deleteAllByTestActual(testActual);
			testActualOperationHistoryService.deleteAllByTestActual(testActual);
		}
		getRepository().delete(testActualList);
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
		//entityManager.unwrap(Session.class).disableFilter(BaseAuditableBranchedEntity.BRANCH_FILTER);
		try {
			List<String> filterString = Arrays.asList(OperationStatus.FINALIZED.getValue(), OperationStatus.RESULTS_ENTERED.getValue(),
					OperationStatus.CLOSED.getValue());

			List<LabTestActual> labTestActual = new ArrayList<LabTestActual>();
			labTestActual.addAll(repo
										.findPreviousTestActual(patientRid, testCode, currentLabTestActualRid,
												new PageRequest(0, neededPreviousResults), filterString)
										.getContent());
			//			Filter branchFilter = entityManager.unwrap(Session.class).enableFilter(BaseAuditableBranchedEntity.BRANCH_FILTER);
			//			branchFilter.setParameter("branchId", SecurityUtil.getCurrentUser().getBranchId());
			//			branchFilter.validate();
			return labTestActual;
		} catch (Exception e) {
			// TODO: handle exception
			//			Filter branchFilter = entityManager.unwrap(Session.class).enableFilter(BaseAuditableBranchedEntity.BRANCH_FILTER);
			//			branchFilter.setParameter("branchId", SecurityUtil.getCurrentUser().getBranchId());
			//			branchFilter.validate();
			return null;
		}
	}

}

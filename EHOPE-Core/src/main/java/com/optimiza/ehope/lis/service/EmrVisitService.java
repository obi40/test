package com.optimiza.ehope.lis.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.optimiza.core.admin.model.SecTenant;
import com.optimiza.core.admin.service.SecTenantService;
import com.optimiza.core.admin.service.SecUserService;
import com.optimiza.core.base.helper.SearchCriterion;
import com.optimiza.core.base.helper.SearchCriterion.FilterOperator;
import com.optimiza.core.base.service.GenericService;
import com.optimiza.core.common.annotation.InterceptorFree;
import com.optimiza.core.common.business.exception.BusinessException;
import com.optimiza.core.common.business.exception.BusinessException.ErrorSeverity;
import com.optimiza.core.common.helper.Email;
import com.optimiza.core.common.helper.FilterablePageRequest;
import com.optimiza.core.common.util.CollectionUtil;
import com.optimiza.core.common.util.DateUtil;
import com.optimiza.core.common.util.EmailUtil;
import com.optimiza.core.common.util.JSONUtil;
import com.optimiza.core.common.util.ReflectionUtil;
import com.optimiza.core.common.util.SecurityUtil;
import com.optimiza.core.common.util.StringUtil;
import com.optimiza.core.lkp.service.ComTenantLanguageService;
import com.optimiza.core.lkp.service.LkpService;
import com.optimiza.ehope.lis.helper.Comparator;
import com.optimiza.ehope.lis.helper.EhopeRights;
import com.optimiza.ehope.lis.lkp.helper.OperationStatus;
import com.optimiza.ehope.lis.lkp.helper.ReportType;
import com.optimiza.ehope.lis.lkp.helper.SectionType;
import com.optimiza.ehope.lis.lkp.helper.SerialType;
import com.optimiza.ehope.lis.lkp.model.LkpOperationStatus;
import com.optimiza.ehope.lis.lkp.service.LkpOperationStatusService;
import com.optimiza.ehope.lis.model.EmrPatientInfo;
import com.optimiza.ehope.lis.model.EmrVisit;
import com.optimiza.ehope.lis.model.HistoricalResult;
import com.optimiza.ehope.lis.model.Interpretation;
import com.optimiza.ehope.lis.model.LabSample;
import com.optimiza.ehope.lis.model.LabTestActual;
import com.optimiza.ehope.lis.model.LabTestActualResult;
import com.optimiza.ehope.lis.model.TestNormalRange;
import com.optimiza.ehope.lis.onboarding.helper.PlanFieldType;
import com.optimiza.ehope.lis.onboarding.service.BrdTenantPlanDetailService;
import com.optimiza.ehope.lis.repo.EmrVisitRepo;
import com.optimiza.ehope.lis.util.NumberUtil;
import com.optimiza.ehope.lis.wrapper.VisitResultsWrapper;

@Service("EmrVisitService")
public class EmrVisitService extends GenericService<EmrVisit, EmrVisitRepo> {

	@Autowired
	private EntityManager entityManager;

	@Autowired
	private ComTenantLanguageService comTenantLanguageService;

	@Autowired
	private LabBranchService labBranchService;
	@Autowired
	private EmrVisitRepo repo;
	@Autowired
	private LkpService lkpService;
	@Autowired
	private EmrPatientInfoService emrPatientInfoService;
	@Autowired
	private SysSerialService serialSerivce;
	@Value("${system.timeBetweenVisits}")
	public String timeBetweenVisits;
	@Autowired
	private EmrVisitOperationHistoryService visitOperationHistoryService;
	@Autowired
	private LkpOperationStatusService operationStatusService;
	@Autowired
	private LabSampleService labSampleService;
	@Autowired
	private LabTestActualService labTestActualService;
	@Autowired
	private BrdTenantPlanDetailService tenantPlanDetailService;
	@Autowired
	private LabBranchService branchService;
	@Autowired
	private SecTenantService tenantService;
	@Autowired
	private BillPatientTransactionService patientTransactionService;
	@Autowired
	private SecUserService secUserService;
	@Autowired
	private LabTestActualResultService testActualResultService;
	@Autowired
	private SecTenantService secTenantService;
	@Autowired
	private EmailUtil emailUtil;
	@Autowired
	private InsProviderService insProviderService;

	@Autowired
	private HistoricalResultService historicalResultService;

	@Value("${special.tests}")
	private String SPECIAL_TESTS;

	@Value("${special.tests.stool}")
	private String SPECIAL_TESTS_STOOL;

	@Value("${special.tests.cbc}")
	private String SPECIAL_TESTS_CBC;

	@Override
	protected EmrVisitRepo getRepository() {
		return repo;
	}

	public void delete(EmrVisit visit) {
		getRepository().delete(visit);
	}

	public EmrVisit getResultReportDialogData(Long visitRid) {
		EmrVisit visit = getRepository().findOne(SearchCriterion.generateRidFilter(visitRid, FilterOperator.eq),
				EmrVisit.class, "emrPatientInfo", "doctor", "labSamples.labTestActualSet.lkpOperationStatus",
				"labSamples.labTestActualSet.testDefinition");
		List<String> filterString = Arrays.asList(OperationStatus.FINALIZED.getValue(), OperationStatus.RESULTS_ENTERED.getValue(),
				OperationStatus.CLOSED.getValue());
		for (LabSample sample : visit.getLabSamples()) {
			sample.getLabTestActualSet().removeIf(lta -> !filterString.contains(lta.getLkpOperationStatus().getCode()));
		}
		visit.getLabSamples().removeIf(s -> CollectionUtil.isCollectionEmpty(s.getLabTestActualSet()));
		if (CollectionUtil.isCollectionEmpty(visit.getLabSamples())) {
			throw new BusinessException("No Result To be printed", "noResultToBePrinted", ErrorSeverity.ERROR);
		}
		return visit;
	}

	public boolean isVisitCovered(EmrVisit visit) {
		return visit.getPaidAmount().compareTo(visit.getTotalAmount()) >= 0;
	}

	/**
	 * Create a patient Visit.
	 * 
	 * @param visit
	 * @return EmrVisit
	 */
	@PreAuthorize("hasAuthority('" + EhopeRights.ADD_ORDER + "')")
	public EmrVisit createVisit(EmrVisit visit) {
		EmrPatientInfo mergedToPatientInfo = emrPatientInfoService.findOne(
				SearchCriterion.generateRidFilter(visit.getEmrPatientInfo().getRid(),
						FilterOperator.eq),
				EmrPatientInfo.class, "mergedToPatientInfo").getMergedToPatientInfo();
		if (mergedToPatientInfo != null) {
			throw new BusinessException("This patient was merged", "mergedPatientVisit", ErrorSeverity.ERROR);
		}
		tenantPlanDetailService.counterChecker(PlanFieldType.ORDERS, 1);
		LkpOperationStatus requestedOperationStatus = lkpService.findOneAnyLkp(
				Arrays.asList(new SearchCriterion("code", OperationStatus.REQUESTED.getValue(), FilterOperator.eq)),
				LkpOperationStatus.class);
		String admissionNo = serialSerivce.sequenceGeneration(SerialType.VISIT_ADMISSION_NO);
		String invoiceNo = serialSerivce.sequenceGeneration(SerialType.INVOICE_NO);
		Date currentDate = new Date();
		visit.setLkpOperationStatus(requestedOperationStatus);
		visit.setAdmissionNumber(admissionNo);
		visit.setInvoiceNumber(invoiceNo);
		visit.getEmrPatientInfo().setLastOrderDate(currentDate);
		visit.setPaidAmount(BigDecimal.ZERO);
		visit.setTotalAmount(BigDecimal.ZERO);
		//Using @modifying here so we can continue the wizard order without re-fetching patient
		emrPatientInfoService.updateLastOrderDate(visit.getEmrPatientInfo(), visit.getEmrPatientInfo().getLastOrderDate());
		visit = repo.save(visit);
		visitOperationHistoryService.createVisitOperationHistory(visit.getRid(), null);
		return visit;
	}

	/**
	 * Update an EmrVisit,general use.
	 * 
	 * @param visit
	 * @param comment : for operation history,nullable
	 * @return EmrVisit
	 */
	public EmrVisit updateVisit(EmrVisit visit, String comment) {
		visit = getRepository().save(visit);
		visitOperationHistoryService.createVisitOperationHistory(visit.getRid(), comment);
		return visit;
	}

	/**
	 * Update visits
	 * 
	 * @param visits
	 * @return list of updated visits
	 */
	public List<EmrVisit> updateVisit(Collection<EmrVisit> visits) {
		return getRepository().save(visits);
	}

	@PreAuthorize("hasAuthority('" + EhopeRights.UPD_ORDER + "')")
	public EmrVisit fetchEditVisit(Long visitRid) {
		EmrVisit visit = getRepository().findOne(SearchCriterion.generateRidFilter(visitRid, FilterOperator.eq), EmrVisit.class,
				"providerPlan.insProvider.insuranceType", "emrPatientInfo.mergedToPatientInfo", "doctor", "visitType", "lkpOperationStatus",
				"patientInsuranceInfo.lkpDependencyType", "visitGroups.testGroup.groupDefinitions.testDefinition",
				"labSamples.labTestActualSet.lkpOperationStatus", "labSamples.labTestActualSet.testDefinition",
				"labSamples.labTestActualSet.sourceActualTest");
		OperationStatus visitStatus = OperationStatus.valueOf(visit.getLkpOperationStatus().getCode());
		if (OperationStatus.CANCELLED == visitStatus || OperationStatus.FINALIZED == visitStatus || OperationStatus.ABORTED == visitStatus
				|| OperationStatus.CLOSED == visitStatus) {
			throw new BusinessException("This Visit can't be edited", "cantEditOrder", ErrorSeverity.ERROR);
		}
		if (visit.getEmrPatientInfo().getMergedToPatientInfo() != null) {
			throw new BusinessException("This patient was merged", "mergedPatientVisit", ErrorSeverity.ERROR);
		}
		if (!CollectionUtil.isCollectionEmpty(visit.getLabSamples())) {
			visit.getLabSamples().removeIf(s -> OperationStatus.ABORTED.getValue().equals(s.getLkpOperationStatus().getCode()));
			if (!CollectionUtil.isCollectionEmpty(visit.getLabSamples())) {
				for (LabSample sample : visit.getLabSamples()) {
					if (!CollectionUtil.isCollectionEmpty(sample.getLabTestActualSet())) {
						sample	.getLabTestActualSet()
								.removeIf(lta -> OperationStatus.ABORTED.getValue().equals(lta.getLkpOperationStatus().getCode()));
					}
				}
			}
		}
		return visit;
	}

	/**
	 * To find a visit with its status along with its samples , statuses and its tests and statuses
	 * 
	 * @param visitRid
	 * @return EmrVisit
	 */
	public EmrVisit findOneOrderSampleTestStatus(Long visitRid) {
		List<String> excludedStatuses = Arrays.asList(OperationStatus.ABORTED.getValue(), OperationStatus.CANCELLED.getValue());
		return repo.findOneOrderSampleTestStatus(visitRid, excludedStatuses);
	}

	@PreAuthorize("hasAuthority('" + EhopeRights.VIEW_ORDERS + "')")
	public Page<EmrVisit> getVisitPageByPatient(FilterablePageRequest filterablePageRequest) {
		return repo.find(filterablePageRequest.getFilters(), filterablePageRequest.getPageRequest(),
				EmrVisit.class, "providerPlan.insProvider", "lkpOperationStatus", "doctor");
	}

	public Page<EmrVisit> getVisitPage(FilterablePageRequest filterablePageRequest) {
		return repo.find(filterablePageRequest.getFilters(), filterablePageRequest.getPageRequest(), EmrVisit.class);
	}

	public List<EmrVisit> getVisitsByProvider(Long providerRid) {
		return repo.getVisitsByProvider(providerRid);
	}

	/**
	 * Fetch Visit -> Sample -> Test.
	 * Two modes: run the query with the normal parameters or the query with the rid parameters.
	 * 
	 * @param filterablePageRequest
	 * @return Page
	 */
	@PreAuthorize("hasAuthority('" + EhopeRights.VIEW_ORDER_MANAGEMENT + "')")
	public Page<EmrVisit> getVisitOrderManagementData(FilterablePageRequest filterablePageRequest) {

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

		Date visitDateFrom = getDateFromToFilter(filterablePageRequest, "visitDateFrom");
		Date visitDateTo = getDateFromToFilter(filterablePageRequest, "visitDateTo");
		Page<EmrVisit> page = null;

		//if type is empty then autocomplete was not used and we used advanced search
		if (StringUtil.isEmpty(type)) {
			page = getRepository().findOrderManagementDataGeneral(OperationStatus.ABORTED.getValue(), visitTypeCode, admissionNumber,
					barcode,
					nationalId,
					fileNo, mobileNo, firstName, lastName, standardCode, description, aliases, secondaryCode, visitDateFrom, visitDateTo,
					filterablePageRequest.getPageRequest());
		} else {
			//means here the user clicked on a specific row in auto complete
			if (patientRid != null || visitRid != null || sampleRid != null || testRid != null) {
				page = getRepository().findOrderManagementDataRid(OperationStatus.ABORTED.getValue(), visitDateFrom, visitDateTo, visitRid,
						patientRid,
						sampleRid, testRid,
						filterablePageRequest.getPageRequest());
			} else {
				//we clicked "search" in auto complete
				if (type.equals("PATIENT") || type.equals("VISIT")) {
					SearchCriterion abortedFilter = new SearchCriterion("lkpOperationStatus.code", OperationStatus.ABORTED.getValue(),
							FilterOperator.neq);
					filterablePageRequest.getFilters().add(abortedFilter);
					for (SearchCriterion sc : filterablePageRequest.getFilters()) {
						if (sc.getField().equals("visitDateFrom") && visitDateFrom != null) {
							sc.setField("visitDate");
							sc.setValue(visitDateFrom);
						} else if (sc.getField().equals("visitDateTo") && visitDateTo != null) {
							sc.setField("visitDate");
							sc.setValue(visitDateTo);
						}
					}
				}
				if (type.equals("PATIENT")) {
					page = getRepository().find(filterablePageRequest.getFilters(), filterablePageRequest.getPageRequest(), EmrVisit.class,
							"emrPatientInfo", "lkpOperationStatus");
				} else if (type.equals("VISIT")) {
					page = getRepository().find(filterablePageRequest.getFilters(), filterablePageRequest.getPageRequest(), EmrVisit.class,
							"lkpOperationStatus");
				} else if (type.equals("SAMPLE")) {
					page = getRepository().findOrderManagementDataSample(OperationStatus.ABORTED.getValue(), barcode, visitDateFrom,
							visitDateTo,
							filterablePageRequest.getPageRequest());
				} else if (type.equals("TEST")) {
					page = getRepository().findOrderManagementDataTest(OperationStatus.ABORTED.getValue(), standardCode, description,
							aliases,
							secondaryCode,
							visitDateFrom, visitDateTo,
							filterablePageRequest.getPageRequest());
				}

			}
		}
		if (page.getNumberOfElements() == 0) {
			return page;
		}
		//fetch the required data
		List<EmrVisit> visits = getRepository()	.find(Arrays.asList(new SearchCriterion("rid",
				page.getContent().stream().map(EmrVisit::getRid).collect(Collectors.toList()), FilterOperator.in)),
				EmrVisit.class, filterablePageRequest.getSortObject(),
				"doctor", "visitType", "lkpOperationStatus", "emrPatientInfo.gender", "providerPlan", "labSamples")
												.stream()
												.distinct().collect(Collectors.toList());
		for (EmrVisit visit : visits) {
			visit.setBranch(branchService.findById(visit.getBranchId()));
		}
		Page<EmrVisit> fetchedPage = new PageImpl<EmrVisit>(visits, filterablePageRequest.getPageRequest(),
				page.getTotalElements());

		return fetchedPage;

	}

	/**
	 * To generate a date from and date to objects, date to will be added extra day
	 * 
	 * @param filterablePageRequest
	 * @param filterName
	 * @return Date
	 */
	public Date getDateFromToFilter(FilterablePageRequest filterablePageRequest, String filterName) {
		Date value = null;
		//only from date is required
		Date visitDateFrom = filterablePageRequest.getDateFilter("visitDateFrom");
		Date visitDateTo = filterablePageRequest.getDateFilter("visitDateTo");
		if (visitDateFrom != null) {
			if (filterName.equals("visitDateFrom")) {
				value = visitDateFrom;
			} else if (filterName.equals("visitDateTo")) {
				//use current date if user didn't choose a date
				if (visitDateTo == null) {
					value = new Date();
				} else {
					value = visitDateTo;
				}
				// so we also get the same day visit 23:59
				value = DateUtil.addDays(value, 1);
				value = DateUtil.addSeconds(value, -1);
			}
		}
		return value;
	}

	public List<Long> getOpenVisits(Long currentVisitRid, Long patientRid) {
		Date negativeDate = DateUtil.addMinutes(new Date(), -1 * Integer.parseInt(timeBetweenVisits));// negative
		return getRepository().getWithinPreviousVisits(currentVisitRid, patientRid, OperationStatus.REQUESTED.getValue(), negativeDate,
				new Date());
	}

	/**
	 * Propagate visit status to newOperationStatus.
	 * 
	 * @param visitRid
	 * @param newOperationStatus
	 * @param comment : nullable
	 * @return EmrVisit
	 */
	@PreAuthorize("hasAuthority(#newOperationStatus.getValue().concat('" + EhopeRights._OPERATION_STATUS + "'))")
	public EmrVisit propagateVisitStatus(Long visitRid, OperationStatus newOperationStatus, String comment) {
		EmrVisit visit = findOneOrderSampleTestStatus(visitRid);
		OperationStatus currentOperationStatus = OperationStatus.valueOf(visit.getLkpOperationStatus().getCode());
		if (newOperationStatus == currentOperationStatus) {
			return visit;
		}
		operationStatusService.validations(currentOperationStatus, newOperationStatus, visitRid);

		Set<LabSample> labSampleSet = CollectionUtil.isCollectionEmpty(visit.getLabSamples()) ? new HashSet<>()
				: visit.getLabSamples();
		Set<LabTestActual> testActualInSamplesSet = labSampleSet.stream().flatMap(s -> s.getLabTestActualSet().stream())
																.collect(Collectors.toSet());
		//remove any test that surpassed order status, means order was edited
		labSampleSet.removeIf(ls ->
			{
				return OperationStatus.valueOf(ls.getLkpOperationStatus().getCode()).getOrder() > newOperationStatus.getOrder();
			});
		//remove any test that surpassed sample status, means order was edited
		testActualInSamplesSet.removeIf(lta ->
			{
				return OperationStatus.valueOf(lta.getLkpOperationStatus().getCode()).getOrder() > newOperationStatus.getOrder();
			});
		for (LabSample sample : labSampleSet) {
			operationStatusService.validations(OperationStatus.valueOf(sample.getLkpOperationStatus().getCode()), newOperationStatus,
					visitRid);
		}

		for (LabTestActual test : testActualInSamplesSet) {
			operationStatusService.validations(OperationStatus.valueOf(test.getLkpOperationStatus().getCode()), newOperationStatus,
					visitRid);
		}
		//Check if results were entered for all tests in the visit
		if (newOperationStatus == OperationStatus.RESULTS_ENTERED && !CollectionUtil.isCollectionEmpty(testActualInSamplesSet)) {
			List<LabTestActual> testActualsResults = testActualResultService.getTestActualListWithRequiredResults(visitRid);
			if (CollectionUtil.isCollectionEmpty(testActualsResults) || testActualsResults.size() != testActualInSamplesSet.size()) {
				//get only the non filled tests
				StringBuilder testsStandardCodes = new StringBuilder();
				OUTER: for (LabTestActual lta : testActualInSamplesSet) {
					for (LabTestActual ltar : testActualsResults) {
						if (lta.equals(ltar)) {//this test has a result so dont add it
							continue OUTER;
						}
					}
					testsStandardCodes.append(lta.getTestDefinition().getStandardCode() + ",");
				}
				testsStandardCodes.deleteCharAt(testsStandardCodes.length() - 1);
				throw new BusinessException("Not All Tests has results", "testNoResults", ErrorSeverity.ERROR,
						Arrays.asList(testsStandardCodes.toString()));
			}
		}
		LkpOperationStatus lkpOperationStatus = lkpService.findOneAnyLkp(
				Arrays.asList(new SearchCriterion("code", newOperationStatus.getValue(), FilterOperator.eq)), LkpOperationStatus.class);
		visit.setLkpOperationStatus(lkpOperationStatus);
		for (LabSample sample : labSampleSet) {
			sample.setLkpOperationStatus(lkpOperationStatus);
		}
		for (LabTestActual lta : testActualInSamplesSet) {
			lta.setLkpOperationStatus(lkpOperationStatus);
		}

		visit = updateVisit(visit, comment);
		labSampleService.updateLabSample(new ArrayList<>(labSampleSet), comment);
		labTestActualService.updateTestActual(new ArrayList<>(testActualInSamplesSet), comment);
		if (newOperationStatus == OperationStatus.CANCELLED) {
			patientTransactionService.cancelPayment(visit.getRid(), comment,
					testActualInSamplesSet.stream().map(LabTestActual::getRid).collect(Collectors.toList()));
		}

		visit = closeVisit(visit, newOperationStatus);
		return visit;
	}

	public EmrVisit propagateVisitStatusNoAuth(Long visitRid, OperationStatus newOperationStatus, String comment) {
		return propagateVisitStatus(visitRid, newOperationStatus, comment);
	}

	/**
	 * Re-validate (parent is smallest of children) Statuses of Samples and visit
	 * 
	 * @param visitRid
	 * @return visit
	 */
	public EmrVisit revalidateVisitStatus(Long visitRid) {
		EmrVisit visit = findOneOrderSampleTestStatus(visitRid);
		for (LabSample sample : visit.getLabSamples()) {
			//sometimes this method is called when there are some empty samples so getLkpBySmallestOperationStatuses(...) will return null 
			if (CollectionUtil.isCollectionEmpty(sample.getLabTestActualSet())) {
				continue;
			}
			LkpOperationStatus smallestTestStatus = operationStatusService.getLkpBySmallestOperationStatuses(
					sample	.getLabTestActualSet().stream().map(lta -> OperationStatus.valueOf(lta.getLkpOperationStatus().getCode()))
							.collect(Collectors.toList()));
			sample.setLkpOperationStatus(smallestTestStatus);
		}
		//filtering non empty samples
		LkpOperationStatus smallestSampleStatus = operationStatusService.getLkpBySmallestOperationStatuses(
				visit	.getLabSamples().stream().filter(s -> !CollectionUtil.isCollectionEmpty(s.getLabTestActualSet()))
						.map(s -> OperationStatus.valueOf(s.getLkpOperationStatus().getCode()))
						.collect(Collectors.toList()));
		visit.setLkpOperationStatus(smallestSampleStatus);
		visit = updateVisit(visit, null);
		labSampleService.updateLabSample(new ArrayList<>(visit.getLabSamples()), null);
		return visit;
	}

	public EmrVisit closeVisit(EmrVisit visit, OperationStatus visitOperationStatus) {
		if (visitOperationStatus != OperationStatus.FINALIZED || !isVisitCovered(visit)) {
			return visit;
		}
		return propagateVisitStatus(visit.getRid(), OperationStatus.CLOSED, null);
	}

	@Transactional(noRollbackFor = BusinessException.class)
	public EmrVisit findVisitSampleSeparationNoRollBack(Long visitRid, Boolean isAll) {
		return findVisitSampleSeparation(visitRid, isAll);
	}

	/**
	 * To get the required data for the sample separation functionalities.
	 * 
	 * @param visitRid
	 * @param isAll : wither to fetch all samples inside the visit regarding the status
	 * @return EmrVisit
	 */
	public EmrVisit findVisitSampleSeparation(Long visitRid, Boolean isAll) {
		List<String> excludedStatuses = Arrays.asList(OperationStatus.ABORTED.getValue(),
				OperationStatus.CANCELLED.getValue());
		EmrVisit visit = getRepository().findVisitSampleSeparation(visitRid);
		if (excludedStatuses.contains(visit.getLkpOperationStatus().getCode())) {
			throw new BusinessException("Can't view sample separation", "operationStatusSampleSeparation", ErrorSeverity.ERROR);
		}
		Set<LabSample> samples = !CollectionUtil.isCollectionEmpty(visit.getLabSamples()) ? visit.getLabSamples() : null;
		if (samples == null) {
			throw new BusinessException("No Samples Found", "noTestInOrder", ErrorSeverity.ERROR);
		} else {
			Set<LabTestActual> tests = visit.getLabSamples().stream().flatMap(s -> s.getLabTestActualSet().stream())
											.collect(Collectors.toSet());
			if (CollectionUtil.isCollectionEmpty(tests)) {
				throw new BusinessException("No Samples Found", "noTestInOrder", ErrorSeverity.ERROR);
			}
		}
		if (!isAll) {
			samples.removeIf(s ->
				{
					if (excludedStatuses.contains(s.getLkpOperationStatus().getCode())) {
						return true;
					} else {
						s.getLabTestActualSet().removeIf(lta -> excludedStatuses.contains(lta.getLkpOperationStatus().getCode()));
						return false;
					}
				});
		}
		return visit;
	}

	@PreAuthorize("hasAuthority('" + EhopeRights.VIEW_OUTSTANDING_BALANCES + "')")
	public Page<EmrVisit> getOutstandingBalanceVisits(FilterablePageRequest filterablePageRequest) {
		Date visitDateFrom = getDateFromToFilter(filterablePageRequest, "visitDateFrom");
		Date visitDateTo = getDateFromToFilter(filterablePageRequest, "visitDateTo");
		Long patientRid = filterablePageRequest.getLongFilter("patientRid");
		Page<EmrVisit> dataPage = repo.getOutstandingBalanceVisits(visitDateFrom, visitDateTo, patientRid,
				filterablePageRequest.getPageRequest());

		return dataPage;
	}

	public List<EmrVisit> findVisits(List<SearchCriterion> filters, Sort sort, String... joins) {
		return getRepository().find(filters, EmrVisit.class, sort, joins);
	}

	@InterceptorFree
	public List<EmrVisit> findExcluded(List<SearchCriterion> filters, Sort sort, String... joins) {
		return getRepository().find(filters, EmrVisit.class, sort, joins).stream().distinct().collect(Collectors.toList());
	}

	public void sendVisitResults(String name, String targetEmail, List<byte[]> reportBytesList) {
		Map<String, String> templateValues = new HashMap<>();
		templateValues.put("info", "Mail Body");
		Email email = new Email("email-results", name, targetEmail, templateValues);
		Map<String, byte[]> map = new HashMap<>();

		int counter = 1;
		for (byte[] reportBytes : reportBytesList) {
			map.put("results" + counter + ".pdf", reportBytes);
			counter++;
			email.setAttachmentBytes(map);
		}

		emailUtil.sendMailTemplate(email);
	}

	public EmrVisit getVisitWorksheets(Long visitRid, List<String> operationStatus) {
		return getRepository().getVisitWorksheets(visitRid, operationStatus);
	}

	public EmrVisit getVisitSamples(Long visitRid, List<String> unwantedSamples) {
		return getRepository().getVisitSamples(visitRid, unwantedSamples);
	}

	/**
	 * Retrieve list of branches Ids for reports
	 * 
	 * @param branchId
	 * @return branchesList
	 */
	public List<Long> retrieveBranchesList(Long branchId) {
		List<Long> branchesList = new ArrayList<Long>();

		if (branchId.equals(-1L)) {
			branchesList = labBranchService.getBranches().stream().map(lb -> lb.getRid()).collect(Collectors.toList());
		} else {
			branchesList.add(branchId);
		}
		return branchesList;
	}

	/**
	 * Retrieve list of insurance Ids for reports
	 * 
	 * @param insuranceId
	 * @return insuranceList
	 */
	public List<Long> retrieveInsurancesList(Long insuranceId, Boolean isReferralOut, List<String> destinationTypesList) {
		List<Long> insurancesList = new ArrayList<Long>();

		if (!isReferralOut) {
			if (insuranceId.equals(-1L)) {
				insurancesList = insProviderService	.getRepository().findAll().stream().map(ins -> ins.getRid())
													.collect(Collectors.toList());
			} else {
				insurancesList.add(insuranceId);
			}
		} else {
			if (insuranceId.equals(-1L)) {
				insurancesList.addAll(
						insProviderService	.getAllClients(destinationTypesList).stream().map(ins -> ins.getRid())
											.collect(Collectors.toList()));
			} else {
				insurancesList.add(insuranceId);
			}
		}
		return insurancesList;
	}

	/**
	 * Retrieve result interpretation class
	 * 
	 * @param testActualResult
	 * @return interpretationClass
	 */
	public String retrieveResultInterpretationClass(BigDecimal testResultValue, Set<Interpretation> interpretationSet,
			Integer primaryValueDecimals) {
		String interpretationClass = "";
		BigDecimal smallestDecimal = NumberUtil.getSmallestDecimal(primaryValueDecimals);

		for (Interpretation interpretation : interpretationSet) {
			BigDecimal minValue = interpretation.getMinConcentrationValue();
			BigDecimal maxValue = interpretation.getMaxConcentrationValue();

			if (interpretation.getMinConcentrationComparator() != null
					&& interpretation.getMinConcentrationComparator().equals(Comparator.gt.getValue())) {
				minValue = minValue.add(smallestDecimal);
			}

			if (interpretation.getMaxConcentrationComparator() != null
					&& interpretation.getMaxConcentrationComparator().equals(Comparator.lt.getValue())) {
				maxValue = maxValue.subtract(smallestDecimal);
			}

			if (maxValue != null && minValue != null) {
				if (testResultValue.compareTo(minValue) >= 0 && testResultValue.compareTo(maxValue) <= 0) {
					interpretationClass = interpretation.getInterpretationClass();
					break;
				}
			} else if (minValue != null) {
				if (testResultValue.compareTo(minValue) >= 0) {
					interpretationClass = interpretation.getInterpretationClass();
					break;
				}
			} else if (maxValue != null) {
				if (testResultValue.compareTo(maxValue) <= 0) {
					interpretationClass = interpretation.getInterpretationClass();
					break;
				}
			}
		}

		return interpretationClass;
	}

	public List<TestNormalRange> getTestNormalRangeList(LabTestActualResult labTestActualResult) {
		String[] normalRangeSpilttedMap;
		String normalRangeText = labTestActualResult.getNormalRangeText();
		List<TestNormalRange> normalRangeList = new ArrayList<TestNormalRange>();
		if (!StringUtil.isEmpty(normalRangeText)) {
			normalRangeSpilttedMap = normalRangeText.split("\\|\\|\\|");

			for (String normalRange : normalRangeSpilttedMap) {
				Map<String, String> normalRangeMap = JSONUtil.convertJSONToMap(normalRange, String.class, String.class);
				TestNormalRange newRange = new TestNormalRange();
				newRange.setCriterionName(normalRangeMap.get("Criterion").toString());
				newRange.setPrimaryValueDescription(normalRangeMap.get("Primary").toString());
				newRange.setSecondaryValueDescription(normalRangeMap.get("Secondary").toString());
				newRange.setTestResult(labTestActualResult.getLabResult());
				normalRangeList.add(newRange);
			}
		}
		return normalRangeList;
	}

	public Set<LabTestActualResult> getPreviousLabTestActualResults(EmrVisit emrVisit, LabTestActual labTestActual,
			LabTestActualResult labTestActualResult, Integer neededPreviousResults) {

		Integer previousCounter = 0;
		List<LabTestActual> previousLabTestActualList = ReflectionUtil.disableIntercepterFilters(entityManager, true, false,
				() -> labTestActualService.findPreviousTestActual(
						emrVisit.getEmrPatientInfo().getRid(),
						labTestActual.getTestDefinition().getStandardCode(), labTestActual.getRid(), neededPreviousResults));

		Set<LabTestActualResult> previousResultsSet = new TreeSet<LabTestActualResult>();

		if (!CollectionUtil.isCollectionEmpty(previousLabTestActualList)) {
			List<LabTestActualResult> previousActualResultList = null;

			for (LabTestActual previousLabTestActual : previousLabTestActualList) {
				previousActualResultList = new ArrayList<>(
						previousLabTestActual.getLabTestActualResults());
				if (!CollectionUtil.isCollectionEmpty(previousActualResultList)) {
					previousCounter = 0;

					for (LabTestActualResult previousLabTestActualResult : previousActualResultList) {
						if (previousLabTestActualResult	.getLabResult().getStandardCode()
														.equals(labTestActualResult	.getLabResult()
																					.getStandardCode())) {
							previousResultsSet.add(previousLabTestActualResult);
							previousCounter += 1;
						}
						if (previousCounter >= neededPreviousResults) {
							break;
						}
					}
				}
			}
		}

		Iterator<LabTestActualResult> iterator = previousResultsSet.iterator();

		LabTestActualResult prev = null;
		while (iterator.hasNext()) {
			LabTestActualResult temp = iterator.next();
			if (labTestActualResult.getNormalRangeText() != null
					&& !labTestActualResult.getNormalRangeText().equals(temp.getNormalRangeText())) {
				if (prev != null) {
					if (prev.getNormalRangeText() != null && temp.getNormalRangeText() != null
							&& !prev.getNormalRangeText().equals(temp.getNormalRangeText())) {
						temp.setNormalRangeList(getTestNormalRangeList(temp));
					}
				} else {
					temp.setNormalRangeList(getTestNormalRangeList(temp));
				}

			}
			prev = temp;
		}

		return previousResultsSet;
	}

	public Set<HistoricalResult> getLatestHistoricalResults(EmrVisit emrVisit, LabTestActual labTestActual,
			LabTestActualResult labTestActualResult, Integer neededPreviousResults, Integer previousCounter) {

		List<HistoricalResult> historicalResultList = historicalResultService.getLatestHistoricalResults(
				emrVisit.getEmrPatientInfo().getFileNo(), labTestActual.getTestDefinition().getStandardCode(),
				labTestActualResult.getLabResult().getStandardCode(), neededPreviousResults);
		Set<HistoricalResult> historicalResultsSet = new HashSet<HistoricalResult>();

		if (!CollectionUtil.isCollectionEmpty(historicalResultList)) {
			for (HistoricalResult historicalResult : historicalResultList) {
				if (previousCounter >= neededPreviousResults) {
					break;
				} else {
					historicalResultsSet.add(historicalResult);
					previousCounter += 1;
				}
			}
		}
		return historicalResultsSet;
	}

	public List<VisitResultsWrapper> getResultsListForReports(EmrVisit emrVisit, List<LabTestActual> usedLabTestActualList) {
		Set<VisitResultsWrapper> wrappersList = new TreeSet<VisitResultsWrapper>();
		VisitResultsWrapper notSeparateWrapper = new VisitResultsWrapper();
		String reportName = ReportType.DEFAULT.getValue();

		SecTenant tenant = secTenantService.findById(SecurityUtil.getCurrentUser().getTenantId());
		notSeparateWrapper.setTenant(tenant);

		notSeparateWrapper.setEmrVisit(emrVisit);
		Set<LabTestActual> labTestActualSet = new TreeSet<LabTestActual>();
		labTestActualSet.addAll(usedLabTestActualList);

		Set<LabTestActual> tempLabTestActualSet = new TreeSet<LabTestActual>();
		Set<LabTestActual> filteredLabTestActualSet = new TreeSet<LabTestActual>();

		StringBuilder disclaimers = new StringBuilder();

		for (LabTestActual labTestActual : labTestActualSet) {

			disclaimers.append(labTestActual.getTestDefinition().getDisclaimer());
			disclaimers.append("\n");

			Boolean isSeparate = labTestActual.getTestDefinition().getIsSeparatePage();
			int previousCounter = 0;

			List<LabTestActualResult> labTestActualResultList = new ArrayList<LabTestActualResult>(labTestActual.getLabTestActualResults());

			labTestActualResultList.removeIf((LabTestActualResult ltar) ->
				{
					return testActualResultService.removeResultIfEmptyAndNotRequired(ltar);
				});

			Collections.sort(labTestActualResultList);

			for (LabTestActualResult labTestActualResult : labTestActualResultList) {
				labTestActualResult.setNormalRangeList(getTestNormalRangeList(labTestActualResult));

				int neededPreviousResults = 2;

				labTestActualResult.setPreviousResult(
						getPreviousLabTestActualResults(emrVisit, labTestActual, labTestActualResult, neededPreviousResults));

				neededPreviousResults = neededPreviousResults - labTestActualResult.getPreviousResult().size();
				if (neededPreviousResults > 0) {
					labTestActualResult.setHistoricalResult(getLatestHistoricalResults(emrVisit, labTestActual, labTestActualResult,
							neededPreviousResults, previousCounter));
				}

				//Checking if test is ALLERGY, so we can define result interpretation class
				//might be used for the IF below
				//labTestActual.get(i).getTestDefinition().getLkpReportType().getCode().equals(ReportType.ALLERGY.getValue())
				if (labTestActual.getTestDefinition().getSection().getType() != null
						&& labTestActual.getTestDefinition().getSection().getType().getCode()
										.equals(SectionType.ALLERGY.getValue())) {
					String interpretationClass = retrieveResultInterpretationClass(
							labTestActualResult.getPrimaryResultParsed(),
							labTestActual.getTestDefinition().getInterpretations(),
							labTestActual.getTestDefinition().getAllergyDecimals());
					labTestActualResult.setInterpretationClass(interpretationClass);

					//Checking if test is MICROBIOLOGY
					//						if (labTestActual	.get(i).getTestDefinition().getSection().getType().getCode()
					//											.equals(SectionType.MICROBIOLOGY.getValue())) {
					//						}
				}

			}

			if (isSeparate) {
				VisitResultsWrapper tempWrapper = new VisitResultsWrapper();
				LabSample labSampleTemp = new LabSample();
				Set<LabTestActualResult> labTestActualResultSetTemp = new TreeSet<LabTestActualResult>();
				labTestActualResultSetTemp.addAll(labTestActualResultList);

				////////////////////////
				//CULTURE TEST//////////
				////////////////////////
				if (labTestActual.getTestDefinition().getSection().getType() != null
						&& labTestActual.getTestDefinition().getSection().getType().getCode().equals(SectionType.MICROBIOLOGY.getValue())) {
					Iterator<LabTestActualResult> iterator = labTestActualResultSetTemp.iterator();
					Set<LabTestActualResult> tempLabTestActualResultSet = new TreeSet<LabTestActualResult>();

					while (iterator.hasNext()) {
						LabTestActualResult temp = iterator.next();
						if (temp.getOrganismDetection() == null) {
							tempLabTestActualResultSet.add(temp);
							iterator.remove();
						}
					}
					tempWrapper.setOtherCultureTestActualSet(tempLabTestActualResultSet);
				}
				////////////////////////
				//CULTURE TEST//////////
				////////////////////////

				labTestActual.setLabTestActualResults(labTestActualResultSetTemp);
				labTestActual.setLabTestActualResultList(new ArrayList<LabTestActualResult>(labTestActualResultSetTemp));

				Set<LabTestActual> labTestActualFilteredTemp = new TreeSet<LabTestActual>();
				labTestActualFilteredTemp.add(labTestActual);
				Set<LabTestActual> labTestActualIntoSample = new TreeSet<LabTestActual>();
				labTestActualIntoSample.addAll(labTestActualFilteredTemp);
				labSampleTemp.setLabTestActualSet(labTestActualIntoSample);

				Set<LabSample> labSampleFilteredTemp = new HashSet<LabSample>();
				labSampleFilteredTemp.add(labSampleTemp);

				/////////////////////////
				//STOOL FIXED PART///////
				/////////////////////////
				if (labTestActual.getTestDefinition().getLkpReportType().getCode().equals(ReportType.STOOL.getValue())) {
					Iterator<LabTestActual> iterator = tempLabTestActualSet.iterator();
					Set<LabTestActual> stoolOtherTests = new TreeSet<LabTestActual>();
					while (iterator.hasNext()) {
						LabTestActual tempActual = iterator.next();
						if (tempActual.getTestDefinition().getLkpReportType().getCode().equals(ReportType.STOOL.getValue())) {
							tempWrapper.setStoolMainTest(new TreeSet<LabTestActual>(Arrays.asList(tempActual)));
						} else {
							stoolOtherTests.add(tempActual);
						}
					}
					tempWrapper.setStoolOtherTests(stoolOtherTests);
				}
				/////////////////////////
				//STOOL FIXED PART///////
				/////////////////////////

				/////////////////////////
				////CBC FIXED PART///////
				/////////////////////////
				if (labTestActual.getTestDefinition().getLkpReportType().getCode().equals(ReportType.CBC.getValue())) {
					Iterator<LabTestActual> iterator = tempLabTestActualSet.iterator();
					Set<LabTestActual> cbcOtherTests = new TreeSet<LabTestActual>();
					while (iterator.hasNext()) {
						LabTestActual tempActual = iterator.next();
						if (tempActual.getTestDefinition().getLkpReportType().getCode().equals(ReportType.CBC.getValue())) {
							tempWrapper.setCbcMainTest(new TreeSet<LabTestActual>(Arrays.asList(tempActual)));
						} else {
							cbcOtherTests.add(tempActual);
						}
					}
					tempWrapper.setCbcOtherTests(cbcOtherTests);
				}
				/////////////////////////
				////CBC FIXED PART///////
				/////////////////////////

				tempWrapper.setReportName(labTestActual.getTestDefinition().getLkpReportType().getCode());
				tempWrapper.setUser(SecurityUtil.getCurrentUser());
				tempWrapper.setEmrVisit(emrVisit);
				tempWrapper.setLabSample(new ArrayList<LabSample>(labSampleFilteredTemp));

				Set<LabTestActual> separateTempLabTestActual = new TreeSet<LabTestActual>();
				for (LabSample labSample : labSampleFilteredTemp) {
					separateTempLabTestActual.addAll(labSample.getLabTestActualSet());
				}
				tempWrapper.setLabTestActual(separateTempLabTestActual);
				tempWrapper.setReportingType("RESULT");
				tempWrapper.setTenant(tenant);
				tempWrapper.setDisclaimers(labTestActual.getTestDefinition().getDisclaimer());
				tempWrapper.setTestDefinition(labTestActual.getTestDefinition());
				tempWrapper.setNeonatalResultsCount(labTestActual.getLabTestActualResults().size());
				wrappersList.add(tempWrapper);

			} else {
				ReportType reportType = ReportType.getByValue(labTestActual.getTestDefinition().getLkpReportType().getCode());

				switch (reportType) {
					case STOOL:
						reportName = ReportType.STOOL.getValue();
						break;
					case URINE:
						reportName = ReportType.URINE.getValue();
						break;
					case CULTURE:
						reportName = ReportType.CULTURE.getValue();
						break;
					case PROTEIN_ELECTRO:
						reportName = ReportType.PROTEIN_ELECTRO.getValue();
						break;
					case CBC:
						reportName = ReportType.CBC.getValue();
						break;
					case ALLERGY:
						reportName = ReportType.ALLERGY.getValue();
						break;
					case NEONATAL:
						reportName = ReportType.NEONATAL.getValue();
					default:
						break;
				}

				Set<LabTestActualResult> labTesActualResultSet = new TreeSet<LabTestActualResult>();
				labTesActualResultSet.addAll(labTestActualResultList);

				labTestActual.setLabTestActualResults(labTesActualResultSet);
				labTestActual.setLabTestActualResultList(new ArrayList<LabTestActualResult>(labTesActualResultSet));

				tempLabTestActualSet.add(labTestActual);
			}
		}

		filteredLabTestActualSet.addAll(tempLabTestActualSet);

		if (!CollectionUtil.isCollectionEmpty(filteredLabTestActualSet)) {
			notSeparateWrapper.setLabTestActual(filteredLabTestActualSet);
			notSeparateWrapper.setUser(SecurityUtil.getCurrentUser());
			notSeparateWrapper.setReportName(reportName);

			/////////////////////////
			//STOOL FIXED PART///////
			/////////////////////////
			if (reportName.equals(ReportType.STOOL.getValue())) {
				Iterator<LabTestActual> iterator = filteredLabTestActualSet.iterator();
				Set<LabTestActual> stoolOtherTests = new TreeSet<LabTestActual>();
				while (iterator.hasNext()) {
					LabTestActual tempActual = iterator.next();
					if (tempActual.getTestDefinition().getLkpReportType().getCode().equals(ReportType.STOOL.getValue())) {
						notSeparateWrapper.setStoolMainTest(new TreeSet<LabTestActual>(Arrays.asList(tempActual)));
					} else {
						stoolOtherTests.add(tempActual);
					}
				}
				notSeparateWrapper.setStoolOtherTests(stoolOtherTests);
			}
			/////////////////////////
			//STOOL FIXED PART///////
			/////////////////////////

			/////////////////////////
			////CBC FIXED PART///////
			/////////////////////////
			if (reportName.equals(ReportType.CBC.getValue())) {
				Iterator<LabTestActual> iterator = filteredLabTestActualSet.iterator();
				Set<LabTestActual> cbcOtherTests = new TreeSet<LabTestActual>();
				while (iterator.hasNext()) {
					LabTestActual tempActual = iterator.next();
					if (tempActual.getTestDefinition().getLkpReportType().getCode().equals(ReportType.CBC.getValue())) {
						notSeparateWrapper.setCbcMainTest(new TreeSet<LabTestActual>(Arrays.asList(tempActual)));
					} else {
						cbcOtherTests.add(tempActual);
					}
				}
				notSeparateWrapper.setCbcOtherTests(cbcOtherTests);
			}
			/////////////////////////
			////CBC FIXED PART///////
			/////////////////////////

			notSeparateWrapper.setReportingType("RESULT");
			notSeparateWrapper.setDisclaimers(disclaimers.toString());
			;
			wrappersList.add(notSeparateWrapper);
		}

		return new ArrayList<VisitResultsWrapper>(wrappersList);
	}

	public List<LabTestActual> findRelatedTests(EmrVisit emrVisit, List<String> neededTestsList, List<LabTestActual> normalTests) {
		List<LabTestActual> labTestActualList = new ArrayList<LabTestActual>();

		for (String testCode : neededTestsList) {
			labTestActualList.addAll(emrVisit	.getLabSamples().stream().flatMap(
					ls -> ls.getLabTestActualSet().stream()
							.filter(lta -> lta.getTestDefinition().getStandardCode().equals(testCode) && normalTests.contains(lta)))
												.collect(Collectors.toList()));
		}

		return labTestActualList;
	}

	public List<EmrVisit> getReferralOutVisits(Date fromDate, Date toDate, List<Long> insurances, List<String> unwantedVisits,
			List<String> unwantedTests, String workbenchVisit) {
		return getRepository().getReferralOutVisits(fromDate, toDate, insurances, unwantedVisits, unwantedTests, workbenchVisit);
	}

	public List<Long> getDailyCreditPaymentVisits(Date fromDate, Date toDate, List<String> unwantedVisits, List<Long> branches) {
		return getRepository().getDailyCreditPaymentVisits(fromDate, toDate, unwantedVisits, branches);
	}

	public List<EmrVisit> getDailyCreditPayment(List<Long> visitsRids) {
		return getRepository().getDailyCreditPayment(visitsRids);
	}

	public List<EmrVisit> getClaimDetailed(Date fromDate, Date toDate, List<String> unwantedVisits, List<Long> insurances,
			List<Long> branches) {
		return getRepository().getClaimDetailed(fromDate, toDate, unwantedVisits, insurances, branches);
	}

	public List<EmrVisit> getClaimSummarized(Date fromDate, Date toDate, List<String> unwantedVisits, List<Long> insurances,
			List<Long> branches) {
		return getRepository().getClaimSummarized(fromDate, toDate, unwantedVisits, insurances, branches);
	}

	public List<Long> getDailyIncomeFromChargeSlips(Date fromDate, Date toDate, List<String> unwantedVisits, List<Long> branches) {
		return getRepository().getDailyIncomeFromChargeSlips(fromDate, toDate, unwantedVisits, branches);
	}

	public List<Long> getDailyIncomeFromPatientTransactions(Date fromDate, Date toDate, List<String> unwantedVisits, List<Long> branches) {
		return getRepository().getDailyIncomeFromPatientTransactions(fromDate, toDate, unwantedVisits, branches);
	}

	public List<EmrVisit> getDailyIncomeFromRids(ArrayList<Long> visitsRids) {
		return getRepository().getDailyIncomeFromRids(visitsRids);
	}

	public List<Long> getDailyCashPaymentsVisits(Date dateFrom, Date dateTo, List<String> cancelledVisits, List<Long> branches,
			String paymentMethod) {
		return getRepository().getDailyCashPaymentsVisits(dateFrom, dateTo, cancelledVisits, branches, paymentMethod);
	}

	public List<EmrVisit> getDailyCashPayments(List<Long> visitsRids) {
		return getRepository().getDailyCashPayments(visitsRids);
	}

	public List<EmrVisit> getAllOutstandingBalances(Date visitDateFrom, Date visitDateTo, Long patientRid) {
		return getRepository().getAllOutstandingBalances(visitDateFrom, visitDateTo, patientRid);
	}

	public List<EmrVisit> getPatientOutstandingBalances(Date visitDateFrom, Date visitDateTo, Long visitRid) {
		return getRepository().getPatientOutstandingBalances(visitDateFrom, visitDateTo, visitRid);
	}

	public EmrVisit getInvoiceData(Long visitRid, List<String> unwantedVisits) {
		return getRepository().getInvoiceData(visitRid, unwantedVisits);
	}

	public EmrVisit getVisitResults(Long visitRid, List<String> operationStatus) {
		return getRepository().getVisitResults(visitRid, operationStatus);
	}

	public EmrVisit getCancelData(Long visitRid) {
		return getRepository().getCancelData(visitRid);
	}

}
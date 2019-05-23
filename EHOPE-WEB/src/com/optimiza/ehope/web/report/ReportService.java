package com.optimiza.ehope.web.report;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import com.optimiza.core.admin.model.SecTenant;
import com.optimiza.core.admin.model.SecUser;
import com.optimiza.core.admin.service.SecTenantService;
import com.optimiza.core.admin.service.SecUserService;
import com.optimiza.core.base.helper.SearchCriterion;
import com.optimiza.core.base.helper.SearchCriterion.FilterOperator;
import com.optimiza.core.common.annotation.InterceptorFree;
import com.optimiza.core.common.business.exception.BusinessException;
import com.optimiza.core.common.business.exception.BusinessException.ErrorSeverity;
import com.optimiza.core.common.util.CollectionUtil;
import com.optimiza.core.common.util.DateUtil;
import com.optimiza.core.common.util.EmailUtil;
import com.optimiza.core.common.util.SecurityUtil;
import com.optimiza.core.lkp.service.ComTenantLanguageService;
import com.optimiza.core.lkp.service.LkpService;
import com.optimiza.ehope.lis.helper.EhopeRights;
import com.optimiza.ehope.lis.lkp.helper.OperationStatus;
import com.optimiza.ehope.lis.lkp.helper.PaymentMethod;
import com.optimiza.ehope.lis.lkp.helper.ReportType;
import com.optimiza.ehope.lis.lkp.helper.TestDestinationType;
import com.optimiza.ehope.lis.lkp.helper.TransactionType;
import com.optimiza.ehope.lis.lkp.helper.VisitType;
import com.optimiza.ehope.lis.lkp.model.LkpPaymentMethod;
import com.optimiza.ehope.lis.lkp.model.LkpTestDestinationType;
import com.optimiza.ehope.lis.lkp.service.LkpOperationStatusService;
import com.optimiza.ehope.lis.model.BillChargeSlip;
import com.optimiza.ehope.lis.model.BillPatientTransaction;
import com.optimiza.ehope.lis.model.EmrVisit;
import com.optimiza.ehope.lis.model.InsProvider;
import com.optimiza.ehope.lis.model.LabBranch;
import com.optimiza.ehope.lis.model.LabTestActual;
import com.optimiza.ehope.lis.onboarding.service.BrdTenantPlanDetailService;
import com.optimiza.ehope.lis.service.BillPatientTransactionService;
import com.optimiza.ehope.lis.service.EmrPatientInfoService;
import com.optimiza.ehope.lis.service.EmrVisitOperationHistoryService;
import com.optimiza.ehope.lis.service.EmrVisitService;
import com.optimiza.ehope.lis.service.HistoricalResultService;
import com.optimiza.ehope.lis.service.InsProviderService;
import com.optimiza.ehope.lis.service.LabBranchService;
import com.optimiza.ehope.lis.service.LabSampleService;
import com.optimiza.ehope.lis.service.LabTestActualResultService;
import com.optimiza.ehope.lis.service.LabTestActualService;
import com.optimiza.ehope.lis.service.SysSerialService;
import com.optimiza.ehope.lis.util.NumberUtil;
import com.optimiza.ehope.lis.wrapper.ClaimDetailedWrapper;
import com.optimiza.ehope.lis.wrapper.ClaimSummarizedWrapper;
import com.optimiza.ehope.lis.wrapper.DailyCashPaymentWrapper;
import com.optimiza.ehope.lis.wrapper.DailyCashWrapper;
import com.optimiza.ehope.lis.wrapper.DailyIncomeWrapper;
import com.optimiza.ehope.lis.wrapper.InsuranceInvoiceWrapper;
import com.optimiza.ehope.lis.wrapper.InvoiceWrapper;
import com.optimiza.ehope.lis.wrapper.ReferralOutWrapper;
import com.optimiza.ehope.lis.wrapper.VisitResultsWrapper;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JRParameter;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

@Service("ReportService")
public class ReportService {

	@Autowired
	private EmrVisitService emrVisitService;
	@Autowired
	private EntityManager entityManager;
	@Autowired
	private ComTenantLanguageService comTenantLanguageService;
	@Autowired
	private LabBranchService labBranchService;

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

	public List<VisitResultsWrapper> getAllResultsReports(Long visitRid, Map<Long, Boolean> testsMap) {
		List<String> filterString = Arrays.asList(OperationStatus.FINALIZED.getValue(), OperationStatus.RESULTS_ENTERED.getValue(),
				OperationStatus.CLOSED.getValue());

		EmrVisit emrVisit = emrVisitService.getVisitResults(visitRid, filterString);

		if (emrVisit == null || CollectionUtil.isCollectionEmpty(emrVisit.getLabSamples())) {
			throw new BusinessException("No Result To be printed", "noResultToBePrinted", ErrorSeverity.ERROR);
		}

		Set<VisitResultsWrapper> wrappersSet = new TreeSet<VisitResultsWrapper>();
		List<String> specialTests = Arrays.asList(SPECIAL_TESTS.split(","));

		//These values are taken from application.props
		List<String> specialTestsStool = Arrays.asList(SPECIAL_TESTS_STOOL.split(","));
		List<String> specialTestsCBC = Arrays.asList(SPECIAL_TESTS_CBC.split(","));
		Boolean containsStool = false;
		Boolean containsCBC = false;

		List<LabTestActual> normalTests = emrVisit	.getLabSamples().stream().flatMap(ls -> ls.getLabTestActualSet().stream())
													.collect(Collectors.toList());

		//Remove
		normalTests.removeIf(lta ->
			{

				return testsMap.get(lta.getRid()) == null;
			});

		//Add isPrintPrevious to Lists
		for (LabTestActual labTestActual : normalTests) {
			labTestActual.setIsPrintPrevious(testsMap.get(labTestActual.getRid()));
		}

		List<LabTestActual> labTestActualList = null;
		List<LabTestActual> filteredLabTestActualList = null;

		for (String testCode : specialTests) {
			labTestActualList = emrVisit.getLabSamples().stream().flatMap(
					ls -> ls.getLabTestActualSet().stream()
							.filter(lta -> lta.getTestDefinition().getStandardCode().equals(testCode) && normalTests.contains(lta)))
										.collect(Collectors.toList());

			if (!CollectionUtil.isCollectionEmpty(labTestActualList)) {
				if (testCode.equals("STA")) {
					//Get stool tests
					containsStool = true;
					filteredLabTestActualList = emrVisitService.findRelatedTests(emrVisit, specialTestsStool, normalTests);
				} else if (testCode.startsWith("CBC")) {
					//Get CBC tests
					containsCBC = true;
					List<String> tempList = new ArrayList<String>();
					tempList.add(testCode);
					tempList.addAll(specialTestsCBC);
					filteredLabTestActualList = emrVisitService.findRelatedTests(emrVisit, tempList, normalTests);
				}
				wrappersSet.addAll(emrVisitService.getResultsListForReports(emrVisit, filteredLabTestActualList));
			}
		}

		//Remove Stool tests from normal tests. (If found)
		if (containsStool) {
			List<String> StoolTestsCodes = wrappersSet	.stream().filter(rws -> rws.getReportName().equals("STOOL")).flatMap(
					rws -> rws.getLabTestActual().stream().map(lta -> lta.getTestDefinition().getStandardCode()))
														.collect(Collectors.toList());
			for (String testCode : StoolTestsCodes) {
				List<LabTestActual> tempLabTestActual = normalTests	.stream().filter(
						lta -> lta.getTestDefinition().getStandardCode().equals(testCode))
																	.collect(Collectors.toList());
				if (!CollectionUtil.isCollectionEmpty(tempLabTestActual)) {
					normalTests.remove(tempLabTestActual.get(0));
				}
			}
		}

		//Remove CBC tests from normal tests. (If found)
		if (containsCBC) {
			List<String> CBCTestsCodes = wrappersSet.stream().filter(rws -> rws.getReportName().equals("CBC")).flatMap(
					rws -> rws.getLabTestActual().stream().map(lta -> lta.getTestDefinition().getStandardCode()))
													.collect(Collectors.toList());

			for (String testCode : CBCTestsCodes) {
				List<LabTestActual> tempLabTestActual = normalTests	.stream().filter(
						lta -> lta.getTestDefinition().getStandardCode().equals(testCode))
																	.collect(Collectors.toList());
				if (!CollectionUtil.isCollectionEmpty(tempLabTestActual)) {
					normalTests.remove(tempLabTestActual.get(0));
				}
			}
		}

		if (!CollectionUtil.isCollectionEmpty(normalTests)) {
			labTestActualList = normalTests	.stream().filter(
					lta -> lta.getTestDefinition().getLkpReportType().getCode().equals(ReportType.DEFAULT.getValue()) != true)
											.collect(Collectors.toList());
			wrappersSet.addAll(emrVisitService.getResultsListForReports(emrVisit, labTestActualList));

			labTestActualList = normalTests	.stream().filter(
					lta -> lta.getTestDefinition().getLkpReportType().getCode().equals(ReportType.DEFAULT.getValue()) == true)
											.collect(Collectors.toList());
			wrappersSet.addAll(emrVisitService.getResultsListForReports(emrVisit, labTestActualList));
		}
		return new ArrayList<VisitResultsWrapper>(wrappersSet);
	}

	public Map<JRDataSource, Map<String, Object>> generateInsuranceInvoiceReport(Map<String, Object> visitInformation) {

		Long visitRid = Long.valueOf(visitInformation.get("visitRid").toString());
		Integer timezoneOffset = Integer.valueOf(visitInformation.get("timezoneOffset").toString());
		String timezoneId = (String) visitInformation.get("timezoneId");

		EmrVisit emrVisit = emrVisitService.getInvoiceData(visitRid,
				Arrays.asList(OperationStatus.CANCELLED.getValue(), OperationStatus.ABORTED.getValue()));

		if (emrVisit == null || CollectionUtil.isCollectionEmpty(emrVisit.getLabSamples())) {
			throw new BusinessException("No Data Found", "noData", ErrorSeverity.ERROR);
		}

		Map<JRDataSource, Map<String, Object>> allReports = new HashMap<>();
		InvoiceWrapper invoiceWrapper = new InvoiceWrapper();
		List<InsuranceInvoiceWrapper> insuranceInvoiceWrapperList = new ArrayList<InsuranceInvoiceWrapper>();
		Long branchId = emrVisit.getBranchId();
		LabBranch labBranch = labBranchService.findById(branchId);
		SecTenant tenant = secTenantService.findById(emrVisit.getTenantId());
		invoiceWrapper.setTenant(tenant);
		invoiceWrapper.setLabBranch(labBranch);

		///////////////////////////////////////////////////////////
		BigDecimal claimNetAmountTotal = BigDecimal.ZERO;
		BigDecimal insuranceDiscountTotal = BigDecimal.ZERO;
		BigDecimal coPaymentTotal = BigDecimal.ZERO;
		BigDecimal fullChargeTotal = BigDecimal.ZERO;
		///////////////////////////////////////////////////////////

		List<LabTestActual> lta = emrVisit	.getLabSamples().stream().flatMap(ls -> ls.getLabTestActualSet().stream())
											.collect(Collectors.toList());

		for (LabTestActual labTestActual : lta) {
			InsuranceInvoiceWrapper insuranceInvoiceWrapper = new InsuranceInvoiceWrapper();
			BigDecimal fullCharge = BigDecimal.ZERO;
			BigDecimal coPayment = BigDecimal.ZERO;
			BigDecimal coPaymentGeneralDiscount = BigDecimal.ZERO;
			BigDecimal coPaymentExcluded = BigDecimal.ZERO;
			BigDecimal insuranceDiscount = BigDecimal.ZERO;
			BigDecimal claimNetAmount = BigDecimal.ZERO;

			insuranceInvoiceWrapper.setLabTestActual(labTestActual);

			List<BillChargeSlip> bcs = labTestActual.getBillChargeSlipList().stream().collect(Collectors.toList());
			for (BillChargeSlip billChargeSlip : bcs) {
				if (!billChargeSlip.getIsCancelled()) {
					//fullCharge = fullCharge.add(billChargeSlip.getOriginalPrice());
					//coPayment = coPayment.add(billChargeSlip.getAmount());
					BigDecimal currentDiscountAmount = billChargeSlip.getOriginalPrice();

					//contains discount
					if (billChargeSlip.getPercentage() != null) {
						BigDecimal currentNetAmount = BigDecimal.ZERO;
						fullCharge = fullCharge.add(billChargeSlip.getOriginalPrice());
						coPayment = coPayment.add(billChargeSlip.getAmount());

						if (billChargeSlip.getPercentage().compareTo(BigDecimal.ZERO) == 0) {
							currentNetAmount = billChargeSlip.getOriginalPrice();
						} else {
							currentNetAmount = billChargeSlip	.getOriginalPrice()
																.multiply(billChargeSlip.getPercentage().divide(NumberUtil.MAX_PERCENTAGE));
						}

						if (emrVisit.getProviderPlan() != null && billChargeSlip.getInsDeductionPercentage() != null) {
							BigDecimal healthDiscount = billChargeSlip.getInsDeductionPercentage().divide(NumberUtil.MAX_PERCENTAGE);
							if (emrVisit.getProviderPlan().getInsProvider().getIsNetAmount()) {
								healthDiscount = healthDiscount.multiply(currentNetAmount);
							} else {
								healthDiscount = healthDiscount.multiply(billChargeSlip.getOriginalPrice());
							}

							currentNetAmount = currentNetAmount.subtract(healthDiscount);
							insuranceDiscount = insuranceDiscount.add(healthDiscount);
							currentDiscountAmount = currentDiscountAmount.subtract(healthDiscount);
						}

						claimNetAmount = claimNetAmount.add(currentNetAmount);
						currentDiscountAmount = currentDiscountAmount.subtract(currentNetAmount);

						if (billChargeSlip.getGeneralDiscountPercentage() != null) {
							currentDiscountAmount = currentDiscountAmount.multiply(
									billChargeSlip.getGeneralDiscountPercentage().divide(NumberUtil.MAX_PERCENTAGE));
							coPaymentGeneralDiscount = coPaymentGeneralDiscount.add(currentDiscountAmount);
						}
					} else {
						//coPayment = coPayment.add(billChargeSlip.getAmount());
						//fullCharge = fullCharge.add(billChargeSlip.getAmount());
						coPaymentExcluded = coPaymentExcluded.add(billChargeSlip.getAmount());
					}
				}
			} //end of charge slip loop

			//if (emrVisit.getVisitType().getCode().equals(VisitType.REFERRAL.getValue())) {
			//	coPayment = BigDecimal.ZERO;
			//} else {
			//	coPayment = emrVisit.getTotalAmount();
			//	coPayment = coPayment.add(coPaymentGeneralDiscount);
			//	coPayment = coPayment.subtract(coPaymentExcluded);
			//}

			if (coPayment.compareTo(BigDecimal.ZERO) != 0) {
				coPayment = coPayment.add(coPaymentGeneralDiscount);
				insuranceInvoiceWrapper.setCoPayment(coPayment);
				insuranceInvoiceWrapper.setClaimedNetAmount(claimNetAmount);
				insuranceInvoiceWrapper.setContractDiscount(insuranceDiscount);
				insuranceInvoiceWrapper.setFullCharge(fullCharge);

				fullChargeTotal = fullChargeTotal.add(fullCharge);
				coPaymentTotal = coPaymentTotal.add(coPayment);
				claimNetAmountTotal = claimNetAmountTotal.add(claimNetAmount);
				insuranceDiscountTotal = insuranceDiscountTotal.add(insuranceDiscount);

				insuranceInvoiceWrapperList.add(insuranceInvoiceWrapper);
			}
		}

		if (CollectionUtil.isCollectionEmpty(insuranceInvoiceWrapperList)) {
			throw new BusinessException("No Data Found", "noData", ErrorSeverity.ERROR);
		}

		invoiceWrapper.setInsuranceTestsList(insuranceInvoiceWrapperList);
		invoiceWrapper.setEmrVisit(emrVisit);
		invoiceWrapper.setEmrPatientInfo(emrVisit.getEmrPatientInfo());
		invoiceWrapper.setDoctor(emrVisit.getDoctor());

		JRDataSource resultsDataSource = new JRBeanCollectionDataSource(Arrays.asList(invoiceWrapper));
		Map<String, Object> parameterMap = new HashMap<String, Object>();
		parameterMap.put("datasource", resultsDataSource);
		parameterMap.put(JRParameter.REPORT_TIME_ZONE, DateUtil.returnClientTimeZone(timezoneId, timezoneOffset));

		parameterMap.put("claimNetAmountTotal", claimNetAmountTotal);
		parameterMap.put("insuranceDiscountTotal", insuranceDiscountTotal);
		parameterMap.put("coPaymentTotal", coPaymentTotal);
		parameterMap.put("fullChargeTotal", fullChargeTotal);
		parameterMap.put("namePrimary", comTenantLanguageService.getTenantNamePrimary());

		allReports.put(resultsDataSource, parameterMap);
		return allReports;
	}

	public Map<JRDataSource, Map<String, Object>> generateInvoiceReport(Map<String, Object> visitInformation) {

		Long visitRid = Long.valueOf(visitInformation.get("visitRid").toString());
		Integer timezoneOffset = Integer.valueOf(visitInformation.get("timezoneOffset").toString());
		String timezoneId = (String) visitInformation.get("timezoneId");

		EmrVisit emrVisit = emrVisitService.getInvoiceData(visitRid,
				Arrays.asList(OperationStatus.CANCELLED.getValue(), OperationStatus.ABORTED.getValue()));

		if (emrVisit == null || CollectionUtil.isCollectionEmpty(emrVisit.getLabSamples())) {
			throw new BusinessException("No Data Found", "noData", ErrorSeverity.ERROR);
		}

		Map<JRDataSource, Map<String, Object>> allReports = new HashMap<>();
		InvoiceWrapper invoice = new InvoiceWrapper();
		BigDecimal generalDiscountPercentage = BigDecimal.ZERO;
		BigDecimal groupDiscountAmount = BigDecimal.ZERO;
		BigDecimal totalAmount = BigDecimal.ZERO;

		List<BillPatientTransaction> payments = new ArrayList<>();
		List<BillPatientTransaction> filteredPayments = new ArrayList<>();

		List<BillPatientTransaction> cancels = new ArrayList<>();
		List<BillPatientTransaction> recalculates = new ArrayList<>();
		List<BillPatientTransaction> refunds = new ArrayList<>();

		List<BillChargeSlip> billChargeSlipList = emrVisit	.getLabSamples().stream().flatMap(s -> s.getLabTestActualSet().stream().filter(
				lta -> !lta.getLkpOperationStatus().getCode().equals(OperationStatus.CANCELLED.getValue())))
															.flatMap(lta -> lta.getBillChargeSlipList().stream())
															.collect(Collectors.toList());

		groupDiscountAmount = billChargeSlipList.stream().filter(bsc -> bsc.getGroupCoverageResult() != null)
												.map(bcs -> bcs.getGroupCoverageResult()).reduce(BigDecimal.ZERO, BigDecimal::add);

		//Get BillCharge Slip Transactions
		for (BillChargeSlip slip : billChargeSlipList) {
			BigDecimal totalAmountTemp = slip.getOriginalPrice();

			if (slip.getGeneralDiscountPercentage() != null || slip.getGeneralDiscountAmount() != null) {
				generalDiscountPercentage = generalDiscountPercentage.add(slip.getAmountAfterCoverage().subtract(slip.getAmount()));
			}

			else if (slip.getPercentage() != null) {
				totalAmountTemp = totalAmountTemp
													.subtract(slip	.getPercentage().multiply(slip.getOriginalPrice())
																	.divide(NumberUtil.MAX_PERCENTAGE));
			}
			for (BillPatientTransaction trans : slip.getBillPatientTransactionList()) {
				SecUser receivedBy = secUserService.findById(trans.getCreatedBy());
				trans.setReceivedBy(receivedBy);

				//why did i use this before if (trans.getAmount().compareTo(BigDecimal.ZERO) > 0)

				TransactionType transactionType = TransactionType.getByValue(trans.getLkpTransactionType().getCode());

				switch (transactionType) {
					case PAYMENT:
						payments.add(trans);
						break;
					case RECALCULATE:
						recalculates.add(trans);
						break;
					case CANCEL:
						cancels.add(trans);
						break;
					case REFUND:
						refunds.add(trans);
						break;
				}
			}

			slip.setTempAmount(patientTransactionService.amountRounding(totalAmountTemp));
			totalAmount = totalAmount.add(totalAmountTemp);
		}
		Long branchId = emrVisit.getBranchId();
		LabBranch labBranch = labBranchService.findById(branchId);

		////////////////////////////////////////
		Map<String, BillPatientTransaction> filteredTransMap = new HashMap<>();
		for (BillPatientTransaction payment : payments) {
			if (payment.getAmount().compareTo(BigDecimal.ZERO) != 0) {
				Calendar calendar = new GregorianCalendar();
				calendar.setTime(payment.getCreationDate());
				String filterString = Integer.toString(calendar.get(Calendar.YEAR));
				filterString = filterString.concat(Integer.toString(calendar.get(Calendar.MONTH)));
				filterString = filterString.concat(Integer.toString(calendar.get(Calendar.DAY_OF_MONTH)));
				filterString = filterString.concat(Integer.toString(calendar.get(Calendar.HOUR_OF_DAY)));
				filterString = filterString.concat(Integer.toString(calendar.get(Calendar.MINUTE)));
				filterString = filterString.concat(payment.getLkpPaymentMethod().getCode());

				if (filteredTransMap.containsKey(filterString)) {
					filteredTransMap.get(filterString)
									.setAmount(filteredTransMap.get(filterString).getAmount().add(payment.getAmount()));
				} else {
					BillPatientTransaction tempPayment = new BillPatientTransaction();
					tempPayment.setAmount(payment.getAmount());
					tempPayment.setCreationDate(payment.getCreationDate());
					tempPayment.setLkpPaymentMethod(payment.getLkpPaymentMethod());
					tempPayment.setReceivedBy(payment.getReceivedBy());
					tempPayment.setDescription(payment.getDescription());
					filteredTransMap.put(filterString, tempPayment);
				}
			}
		}

		if (!filteredTransMap.isEmpty()) {
			filteredPayments.addAll(filteredTransMap.values());
		}
		////////////////////////////////////////

		invoice.setEmrVisit(emrVisit);
		invoice.setEmrPatientInfo(emrVisit.getEmrPatientInfo());
		invoice.setLabBranch(labBranch);
		invoice.setDoctor(emrVisit.getDoctor());
		invoice.setBills(billChargeSlipList);
		invoice.setPayments(filteredPayments);
		invoice.setCancels(cancels);
		invoice.setRecalculates(recalculates);
		invoice.setRefunds(refunds);

		BigDecimal discountResult = BigDecimal.ZERO;
		discountResult = generalDiscountPercentage;
		if (groupDiscountAmount.compareTo(BigDecimal.ZERO) > 0) {
			discountResult = discountResult.add(groupDiscountAmount);
		}

		invoice.setGeneralDiscountAmount(discountResult);
		SecTenant tenant = secTenantService.findById(emrVisit.getTenantId());
		invoice.setTenant(tenant);

		JRDataSource resultsDataSource = new JRBeanCollectionDataSource(Arrays.asList(invoice));
		Map<String, Object> parameterMap = new HashMap<String, Object>();
		parameterMap.put("datasource", resultsDataSource);
		parameterMap.put(JRParameter.REPORT_TIME_ZONE, DateUtil.returnClientTimeZone(timezoneId, timezoneOffset));
		parameterMap.put("isInsured", (emrVisit.getProviderPlan() != null) ? true : false);
		parameterMap.put("namePrimary", comTenantLanguageService.getTenantNamePrimary());

		allReports.put(resultsDataSource, parameterMap);
		return allReports;
	}

	@PreAuthorize("hasAuthority('" + EhopeRights.VIEW_OUTSTANDING_BALANCES + "')")
	public Map<JRDataSource, Map<String, Object>> generatePatientOutstandingBalancesReport(Map<String, Object> patientInformation) {

		Date visitDateFrom = DateUtil.parseUTCDate(patientInformation.get("visitDateFrom").toString());
		Date visitDateTo = DateUtil.parseUTCDate(patientInformation.get("visitDateTo").toString());
		Long visitRid = Long.valueOf(patientInformation.get("visitRid").toString());
		//Long patientRid = getRepository().getOne(visitRid).getEmrPatientInfo().getRid();

		Integer timezoneOffset = Integer.valueOf(patientInformation.get("timezoneOffset").toString());
		String timezoneId = (String) patientInformation.get("timezoneId");
		visitDateTo = DateUtil.addDays(visitDateTo, 1);
		visitDateTo = DateUtil.addSeconds(visitDateTo, -1);

		List<EmrVisit> results = emrVisitService.getPatientOutstandingBalances(visitDateFrom, visitDateTo, visitRid);

		JRDataSource resultsDataSource = new JRBeanCollectionDataSource(results);

		SecUser user = SecurityUtil.getCurrentUser();
		SecTenant tenant = tenantService.findOne(
				Arrays.asList(new SearchCriterion("rid", user.getTenantId(), FilterOperator.eq)), SecTenant.class,
				"country.currency");

		Map<JRDataSource, Map<String, Object>> allReports = new HashMap<>();
		Map<String, Object> parameterMap = new HashMap<String, Object>();
		parameterMap.put(JRParameter.REPORT_TIME_ZONE, DateUtil.returnClientTimeZone(timezoneId, timezoneOffset));
		parameterMap.put("currency", tenant.getCountry().getCurrency().getCode());
		parameterMap.put("visitDateFrom", visitDateFrom);
		parameterMap.put("visitDateTo", visitDateTo);
		parameterMap.put("datasource", resultsDataSource);
		parameterMap.put("namePrimary", comTenantLanguageService.getTenantNamePrimary());

		allReports.put(resultsDataSource, parameterMap);
		return allReports;
	}

	@PreAuthorize("hasAuthority('" + EhopeRights.VIEW_OUTSTANDING_BALANCES + "')")
	public Map<JRDataSource, Map<String, Object>> generateOutstandingBalancesReport(Map<String, Object> filtersInformation) {
		Date visitDateFrom = DateUtil.parseUTCDate(filtersInformation.get("visitDateFrom").toString());
		Date visitDateTo = DateUtil.parseUTCDate(filtersInformation.get("visitDateTo").toString());
		Integer timezoneOffset = Integer.valueOf(filtersInformation.get("timezoneOffset").toString());
		String timezoneId = (String) filtersInformation.get("timezoneId");
		visitDateTo = DateUtil.addDays(visitDateTo, 1);
		visitDateTo = DateUtil.addSeconds(visitDateTo, -1);

		Long patientRid = null;
		if (filtersInformation.get("patientRid") != null) {
			patientRid = Long.valueOf(filtersInformation.get("patientRid").toString());
		}

		List<EmrVisit> results = emrVisitService.getAllOutstandingBalances(visitDateFrom, visitDateTo, patientRid);

		JRDataSource resultsDataSource = new JRBeanCollectionDataSource(results);

		SecUser user = SecurityUtil.getCurrentUser();
		SecTenant tenant = tenantService.findOne(
				Arrays.asList(new SearchCriterion("rid", user.getTenantId(), FilterOperator.eq)), SecTenant.class,
				"country.currency");

		Map<JRDataSource, Map<String, Object>> allReports = new HashMap<>();

		Map<String, Object> parameterMap = new HashMap<String, Object>();
		parameterMap.put(JRParameter.REPORT_TIME_ZONE, DateUtil.returnClientTimeZone(timezoneId, timezoneOffset));
		parameterMap.put("currency", tenant.getCountry().getCurrency().getCode());
		parameterMap.put("visitDateFrom", visitDateFrom);
		parameterMap.put("visitDateTo", visitDateTo);
		parameterMap.put("datasource", resultsDataSource);
		parameterMap.put("namePrimary", comTenantLanguageService.getTenantNamePrimary());

		allReports.put(resultsDataSource, parameterMap);
		return allReports;
	}

	@InterceptorFree
	@PreAuthorize("hasAuthority('" + EhopeRights.VIEW_DAILY_CASH_REPORT + "')")
	public Map<JRDataSource, Map<String, Object>> getDailyCashPayments(Map<String, Object> cashPaymentsInformation) {

		Long paymentMethodRid = null;
		LkpPaymentMethod paymentMethod = new LkpPaymentMethod();
		String paymentMethodCode = null;

		if (cashPaymentsInformation.get("paymentTypeRid") != null) {
			paymentMethodRid = Long.valueOf(cashPaymentsInformation.get("paymentTypeRid").toString());

			paymentMethod = lkpService.findOneAnyLkp(
					Arrays.asList(new SearchCriterion("rid", paymentMethodRid, FilterOperator.eq)), LkpPaymentMethod.class);
			paymentMethodCode = paymentMethod.getCode();
		}

		Date dateFrom = DateUtil.parseUTCDate(cashPaymentsInformation.get("dailyCashDateFrom").toString());
		Date dateTo = DateUtil.parseUTCDate(cashPaymentsInformation.get("dailyCashDateTo").toString());
		Integer timezoneOffset = Integer.valueOf(cashPaymentsInformation.get("timezoneOffset").toString());
		String timezoneId = (String) cashPaymentsInformation.get("timezoneId");

		dateTo = DateUtil.addDays(dateTo, 1);
		dateTo = DateUtil.addSeconds(dateTo, -1);

		Long branchId = Long.valueOf(cashPaymentsInformation.get("branchRid").toString());

		if (!SecurityUtil.isBranchIdAllowed(branchId)) {
			throw new BusinessException("Branch is not the same as the user branch!", "branchIsNotUserBranch",
					ErrorSeverity.ERROR);
		}

		List<Long> branches = emrVisitService.retrieveBranchesList(branchId);

		List<Long> visitsRids = emrVisitService.getDailyCashPaymentsVisits(dateFrom, dateTo,
				Arrays.asList(OperationStatus.CANCELLED.getValue()), branches,
				paymentMethodCode);

		if (CollectionUtil.isCollectionEmpty(visitsRids)) {
			throw new BusinessException("No Visits In The Selected Period", "noOrdersInSelectedPeriod", ErrorSeverity.ERROR);
		}

		Map<JRDataSource, Map<String, Object>> allReports = new HashMap<>();

		List<DailyCashWrapper> wrapper = new ArrayList<DailyCashWrapper>();
		List<EmrVisit> visitsList = new ArrayList<EmrVisit>();
		visitsList = emrVisitService.getDailyCashPayments(visitsRids);

		BigDecimal paidPrevious = BigDecimal.ZERO;
		BigDecimal cashTotal = BigDecimal.ZERO;
		BigDecimal creditCardTotal = BigDecimal.ZERO;
		BigDecimal chequeTotal = BigDecimal.ZERO;

		BigDecimal cashRefundTotal = BigDecimal.ZERO;
		BigDecimal creditCardRefundTotal = BigDecimal.ZERO;
		BigDecimal chequeRefundTotal = BigDecimal.ZERO;

		BigDecimal refundsTotal = BigDecimal.ZERO;
		BigDecimal discountsTotal = BigDecimal.ZERO;
		BigDecimal amountsTotal = BigDecimal.ZERO;

		PaymentMethod cashPayment = PaymentMethod.CASH;
		PaymentMethod creditCardPayment = PaymentMethod.CREDIT_CARD;
		PaymentMethod chequePayment = PaymentMethod.CHEQUE;

		for (EmrVisit emrVisit : visitsList) {
			BigDecimal cashRefund = BigDecimal.ZERO;
			BigDecimal creditCardRefund = BigDecimal.ZERO;
			BigDecimal chequeRefund = BigDecimal.ZERO;

			BigDecimal cashAmount = BigDecimal.ZERO;
			BigDecimal creditCardAmount = BigDecimal.ZERO;
			BigDecimal chequeAmount = BigDecimal.ZERO;

			BigDecimal allDiscouts = BigDecimal.ZERO;

			BigDecimal paidPreviousTempCash = BigDecimal.ZERO;
			BigDecimal paidPreviousTempCreditCard = BigDecimal.ZERO;
			BigDecimal paidPreviousTempCheque = BigDecimal.ZERO;

			String firstChargeSlip = "";

			Calendar visitCal = Calendar.getInstance();
			visitCal.setTime(emrVisit.getVisitDate());
			visitCal.set(Calendar.MINUTE, 0);
			visitCal.set(Calendar.SECOND, 0);
			visitCal.set(Calendar.MILLISECOND, 0);

			//List<BillChargeSlip> bcs = emrVisit	.getLabSamples().stream().flatMap(s -> s.getLabTestActualSet().stream())
			//									.flatMap(lta -> lta.getBillChargeSlipList().stream()).collect(Collectors.toList());

			List<BillChargeSlip> bcs = emrVisit	.getLabSamples().stream().flatMap(s -> s.getLabTestActualSet().stream().filter(
					lta -> !lta.getLkpOperationStatus().getCode().equals(OperationStatus.CANCELLED.getValue())))
												.flatMap(lta -> lta.getBillChargeSlipList().stream())
												.collect(Collectors.toList());

			if (!CollectionUtil.isCollectionEmpty(bcs)) {
				for (BillChargeSlip billChargeSlip : bcs) {
					allDiscouts = allDiscouts.add(billChargeSlip.getAmountAfterCoverage().subtract(billChargeSlip.getAmount()));

					List<BillPatientTransaction> billPatientTransactions = new ArrayList<BillPatientTransaction>(
							billChargeSlip.getBillPatientTransactionList());

					if (!CollectionUtil.isCollectionEmpty(billPatientTransactions)) {
						if (billPatientTransactions.get(0).getLkpPaymentMethod() != null) {
							firstChargeSlip = billPatientTransactions.get(0).getLkpPaymentMethod().getCode();
						}

						for (BillPatientTransaction patientTrans : billPatientTransactions) {
							Calendar patientTransCal = Calendar.getInstance();
							patientTransCal.setTime(patientTrans.getCreationDate());
							patientTransCal.set(Calendar.MINUTE, 0);
							patientTransCal.set(Calendar.SECOND, 0);
							patientTransCal.set(Calendar.MILLISECOND, 0);

							if (patientTrans.getLkpPaymentMethod() != null) {
								if (patientTrans.getLkpTransactionType().getCode()
												.equals(TransactionType.PAYMENT.getValue())) {

									PaymentMethod switchPayment = PaymentMethod.valueOf(
											patientTrans.getLkpPaymentMethod().getCode());

									switch (switchPayment) {
										case CASH:
											cashAmount = cashAmount.add(patientTrans.getAmount());
											cashTotal = cashTotal.add(patientTrans.getAmount());
											if (visitCal.before(patientTransCal)) {
												paidPreviousTempCash = paidPreviousTempCash.add(patientTrans.getAmount());
											}
											break;

										case CHEQUE:
											chequeAmount = chequeAmount.add(patientTrans.getAmount());
											chequeTotal = chequeTotal.add(patientTrans.getAmount());
											if (visitCal.before(patientTransCal)) {
												paidPreviousTempCheque = paidPreviousTempCheque.add(patientTrans.getAmount());
											}
											break;

										case CREDIT_CARD:
											creditCardAmount = creditCardAmount.add(patientTrans.getAmount());
											creditCardTotal = creditCardTotal.add(patientTrans.getAmount());
											if (visitCal.before(patientTransCal)) {
												paidPreviousTempCreditCard = paidPreviousTempCreditCard.add(patientTrans.getAmount());
											}
											break;
									}
								} else if (patientTrans	.getLkpTransactionType().getCode()
														.equals(TransactionType.REFUND.getValue())) {

									PaymentMethod switchPayment = PaymentMethod.valueOf(
											patientTrans.getLkpPaymentMethod().getCode());

									switch (switchPayment) {
										case CASH:
											cashRefund = cashRefund.add(patientTrans.getAmount());
											cashRefundTotal = cashRefundTotal.add(patientTrans.getAmount());
											if (visitCal.before(patientTransCal)) {
												paidPreviousTempCash = paidPreviousTempCash.subtract(patientTrans.getAmount());
											}
											break;

										case CHEQUE:
											chequeRefund = chequeRefund.add(patientTrans.getAmount());
											chequeRefundTotal = chequeRefundTotal.add(patientTrans.getAmount());
											if (visitCal.before(patientTransCal)) {
												paidPreviousTempCheque = paidPreviousTempCheque.subtract(patientTrans.getAmount());
											}
											break;

										case CREDIT_CARD:
											creditCardRefund = creditCardRefund.add(patientTrans.getAmount());
											creditCardRefundTotal = creditCardRefundTotal.add(patientTrans.getAmount());
											if (visitCal.before(patientTransCal)) {
												paidPreviousTempCreditCard = paidPreviousTempCreditCard.subtract(patientTrans.getAmount());
											}
											break;
									}
								}
							}
						}
					}
				} //end of chargeSlip loop
			}

			discountsTotal = discountsTotal.add(allDiscouts);

			if (paymentMethodCode != null) {
				firstChargeSlip = paymentMethodCode;
			}

			if ((paymentMethodCode == null || paymentMethodCode.equals(PaymentMethod.CASH.getValue()))
					&& !cashAmount.equals(BigDecimal.ZERO)) {

				paidPrevious = paidPrevious.add(paidPreviousTempCash);
				refundsTotal = refundsTotal.add(cashRefund);
				amountsTotal = amountsTotal.add(cashAmount);

				DailyCashWrapper cashWrapper;
				if (firstChargeSlip.equals("CASH")) {
					cashWrapper = new DailyCashWrapper(emrVisit, (lkpService.findOneAnyLkp(
							Arrays.asList(new SearchCriterion("code", cashPayment.getValue(), FilterOperator.eq)),
							LkpPaymentMethod.class)),
							cashRefund,
							allDiscouts,
							cashAmount);
				} else {
					cashWrapper = new DailyCashWrapper(emrVisit, (lkpService.findOneAnyLkp(
							Arrays.asList(new SearchCriterion("code", cashPayment.getValue(), FilterOperator.eq)),
							LkpPaymentMethod.class)),
							cashRefund,
							BigDecimal.ZERO,
							cashAmount);
				}
				wrapper.add(cashWrapper);
			}

			if ((paymentMethodCode == null || paymentMethodCode.equals(PaymentMethod.CREDIT_CARD.getValue()))
					&& !creditCardAmount.equals(BigDecimal.ZERO)) {

				paidPrevious = paidPrevious.add(paidPreviousTempCreditCard);
				refundsTotal = refundsTotal.add(creditCardRefund);
				amountsTotal = amountsTotal.add(creditCardAmount);

				DailyCashWrapper creditCardWrapper;
				if (firstChargeSlip.equals("CREDIT_CARD")) {
					creditCardWrapper = new DailyCashWrapper(emrVisit, (lkpService.findOneAnyLkp(
							Arrays.asList(new SearchCriterion("code", creditCardPayment.getValue(), FilterOperator.eq)),
							LkpPaymentMethod.class)),
							creditCardRefund,
							allDiscouts,
							creditCardAmount);
				} else {
					creditCardWrapper = new DailyCashWrapper(emrVisit, (lkpService.findOneAnyLkp(
							Arrays.asList(new SearchCriterion("code", creditCardPayment.getValue(), FilterOperator.eq)),
							LkpPaymentMethod.class)),
							creditCardRefund,
							BigDecimal.ZERO,
							creditCardAmount);
				}
				wrapper.add(creditCardWrapper);
			}

			if ((paymentMethodCode == null || paymentMethodCode.equals(PaymentMethod.CHEQUE.getValue()))
					&& !chequeAmount.equals(BigDecimal.ZERO)) {

				paidPrevious = paidPrevious.add(paidPreviousTempCheque);
				refundsTotal = refundsTotal.add(chequeRefund);
				amountsTotal = amountsTotal.add(chequeAmount);

				DailyCashWrapper chequeWrapper;
				if (firstChargeSlip.equals("CHEQUE")) {
					chequeWrapper = new DailyCashWrapper(emrVisit, (lkpService.findOneAnyLkp(
							Arrays.asList(new SearchCriterion("code", chequePayment.getValue(), FilterOperator.eq)),
							LkpPaymentMethod.class)),
							chequeRefund,
							allDiscouts,
							chequeAmount);
				} else {
					chequeWrapper = new DailyCashWrapper(emrVisit, (lkpService.findOneAnyLkp(
							Arrays.asList(new SearchCriterion("code", chequePayment.getValue(), FilterOperator.eq)),
							LkpPaymentMethod.class)),
							chequeRefund,
							BigDecimal.ZERO,
							chequeAmount);
				}
				wrapper.add(chequeWrapper);
			}
		}

		if (paymentMethodCode != null) {
			PaymentMethod switchPayment = PaymentMethod.valueOf(paymentMethodCode);

			switch (switchPayment) {
				case CASH:
					creditCardTotal = BigDecimal.ZERO;
					chequeTotal = BigDecimal.ZERO;
					break;
				case CREDIT_CARD:
					chequeTotal = BigDecimal.ZERO;
					cashTotal = BigDecimal.ZERO;
					break;
				case CHEQUE:
					creditCardTotal = BigDecimal.ZERO;
					cashTotal = BigDecimal.ZERO;
					break;
			}
		}

		String userName = secUserService.findById(SecurityUtil.getCurrentUser().getRid()).getUsername();
		String branchName = "";

		if (branches.size() == 1) {
			LabBranch labBranch = labBranchService.findOne(SearchCriterion.generateRidFilter(branchId, FilterOperator.eq), LabBranch.class);
			branchName = labBranch.getName().get("en_us");
		} else {
			branchName = "All Branches";
		}

		JRDataSource resultsDataSource = new JRBeanCollectionDataSource(wrapper);
		Map<String, Object> parameterMap = new HashMap<String, Object>();
		parameterMap.put("datasource", resultsDataSource);
		parameterMap.put(JRParameter.REPORT_TIME_ZONE, DateUtil.returnClientTimeZone(timezoneId, timezoneOffset));
		parameterMap.put("userName", userName);
		parameterMap.put("dateFrom", dateFrom);
		parameterMap.put("dateTo", dateTo);
		parameterMap.put("paidPrevious", paidPrevious);
		parameterMap.put("cashTotal", cashTotal.subtract(refundsTotal));
		parameterMap.put("creditCardTotal", creditCardTotal);
		parameterMap.put("chequeTotal", chequeTotal);
		parameterMap.put("refundsTotal", refundsTotal);
		parameterMap.put("discountsTotal", discountsTotal);
		parameterMap.put("amountsTotal", amountsTotal);
		parameterMap.put("branchName", branchName);
		parameterMap.put("paymentMethodCode", paymentMethodCode);
		parameterMap.put("namePrimary", comTenantLanguageService.getTenantNamePrimary());

		allReports.put(resultsDataSource, parameterMap);
		return allReports;
	}

	@InterceptorFree
	@PreAuthorize("hasAuthority('" + EhopeRights.VIEW_DAILY_INCOME_REPORT + "')")
	public Map<JRDataSource, Map<String, Object>> generateDailyIncomeReport(Map<String, Object> incomeInformation) {

		Date dateFrom = DateUtil.parseUTCDate(incomeInformation.get("dailyIncomeDateFrom").toString());
		Date dateTo = DateUtil.parseUTCDate(incomeInformation.get("dailyIncomeDateTo").toString());
		Integer timezoneOffset = Integer.valueOf(incomeInformation.get("timezoneOffset").toString());
		String timezoneId = (String) incomeInformation.get("timezoneId");

		dateTo = DateUtil.addDays(dateTo, 1);
		dateTo = DateUtil.addSeconds(dateTo, -1);

		Long branchId = Long.valueOf(incomeInformation.get("branchRid").toString());

		if (!SecurityUtil.isBranchIdAllowed(branchId)) {
			throw new BusinessException("Branch is not the same as the user branch!", "branchIsNotUserBranch",
					ErrorSeverity.ERROR);
		}

		Map<JRDataSource, Map<String, Object>> allReports = new HashMap<>();

		List<Long> branches = emrVisitService.retrieveBranchesList(branchId);

		List<DailyIncomeWrapper> wrapper = new ArrayList<DailyIncomeWrapper>();

		Set<Long> allVisits = new HashSet<Long>();

		List<Long> visitsRids = emrVisitService.getDailyIncomeFromChargeSlips(dateFrom, dateTo,
				Arrays.asList(OperationStatus.ABORTED.getValue(), OperationStatus.CANCELLED.getValue()),
				branches);

		allVisits.addAll(visitsRids);

		visitsRids = emrVisitService.getDailyIncomeFromPatientTransactions(dateFrom, dateTo,
				Arrays.asList(OperationStatus.ABORTED.getValue(), OperationStatus.CANCELLED.getValue()),
				branches);

		allVisits.addAll(visitsRids);

		//use everywhere
		if (CollectionUtil.isCollectionEmpty(allVisits)) {
			throw new BusinessException("No Visits In The Selected Period", "noOrdersInSelectedPeriod", ErrorSeverity.ERROR);
		}

		BigDecimal allCashTotal = BigDecimal.ZERO;
		BigDecimal allCreditTotal = BigDecimal.ZERO;
		BigDecimal allDiscountTotal = BigDecimal.ZERO;
		BigDecimal allNotPaidTotal = BigDecimal.ZERO;
		BigDecimal discountPerc = BigDecimal.ZERO;

		List<EmrVisit> visitsList = emrVisitService.getDailyIncomeFromRids(new ArrayList<Long>(allVisits));

		for (EmrVisit emrVisit : visitsList) {
			DailyIncomeWrapper tempWrapper = new DailyIncomeWrapper();
			tempWrapper.setEmrVisit(emrVisit);

			BigDecimal totalCash = emrVisit.getPaidAmount();
			BigDecimal totalCredit = BigDecimal.ZERO; //emrVisit.getPaidAmount().subtract(emrVisit.getTotalAmount());
			BigDecimal totalDiscount = BigDecimal.ZERO;
			BigDecimal totalNotPaid = BigDecimal.ZERO;

			totalNotPaid = totalNotPaid.add(emrVisit.getTotalAmount().subtract(emrVisit.getPaidAmount()));
			if (totalNotPaid.compareTo(BigDecimal.ZERO) < 0) {
				totalNotPaid = BigDecimal.ZERO;
			}
			tempWrapper.setTotalNotPaid(totalNotPaid);

			List<BillChargeSlip> billChargeSlipList = emrVisit	.getLabSamples().stream().flatMap(s -> s.getLabTestActualSet().stream())
																.flatMap(lta -> lta.getBillChargeSlipList().stream())
																.collect(Collectors.toList());

			if (!CollectionUtil.isCollectionEmpty(billChargeSlipList)) {
				for (BillChargeSlip bcs : billChargeSlipList) {
					if (bcs.getIsCancelled()) {
						continue;
					}

					totalDiscount = totalDiscount.add(bcs.getAmountAfterCoverage().subtract(bcs.getAmount()));

					if (bcs.getInsCoverageResult() != null) {
						totalCredit = totalCredit.add(
								bcs.getInsCoverageResult().subtract(bcs.getInsDeductionResult()));
					}
				}
			}

			if (totalCredit.compareTo(BigDecimal.ZERO) < 0) {
				totalCredit = BigDecimal.ZERO;
			}

			tempWrapper.setTotalCash(totalCash);
			tempWrapper.setTotalCredit(totalCredit);
			tempWrapper.setTotalDiscount(totalDiscount);

			wrapper.add(tempWrapper);

			allCashTotal = allCashTotal.add(totalCash);
			allCreditTotal = allCreditTotal.add(totalCredit);
			allDiscountTotal = allDiscountTotal.add(totalDiscount);
			allNotPaidTotal = allNotPaidTotal.add(totalNotPaid);

		}

		String userName = secUserService.findById(SecurityUtil.getCurrentUser().getRid()).getUsername();

		BigDecimal grandTotal = allCashTotal.add(allNotPaidTotal).subtract(allDiscountTotal);

		try {
			discountPerc = allDiscountTotal.divide(allCashTotal, RoundingMode.HALF_UP);
		} catch (ArithmeticException e) {
			System.out.println(e);
			discountPerc = BigDecimal.ZERO;
		}

		discountPerc = discountPerc.setScale(3, RoundingMode.CEILING);

		String branchName = "";

		if (branches.size() == 1) {
			LabBranch labBranch = labBranchService.findOne(SearchCriterion.generateRidFilter(branchId, FilterOperator.eq), LabBranch.class);
			branchName = labBranch.getName().get("en_us");
		} else {
			branchName = "All Branches";
		}

		JRDataSource resultsDataSource = new JRBeanCollectionDataSource(wrapper);
		Map<String, Object> parameterMap = new HashMap<String, Object>();
		parameterMap.put("datasource", resultsDataSource);
		parameterMap.put(JRParameter.REPORT_TIME_ZONE, DateUtil.returnClientTimeZone(timezoneId, timezoneOffset));

		parameterMap.put("userName", userName);
		parameterMap.put("dateFrom", dateFrom);
		parameterMap.put("dateTo", dateTo);

		parameterMap.put("allCashTotal", allCashTotal);
		parameterMap.put("allCreditTotal", allCreditTotal);
		parameterMap.put("allDiscountTotal", allDiscountTotal);
		parameterMap.put("allNotPaidTotal", allNotPaidTotal);
		parameterMap.put("grandTotal", grandTotal);
		parameterMap.put("discountPerc", discountPerc);
		parameterMap.put("branchName", branchName);
		parameterMap.put("namePrimary", comTenantLanguageService.getTenantNamePrimary());

		allReports.put(resultsDataSource, parameterMap);
		return allReports;
	}

	public Map<JRDataSource, Map<String, Object>> generateAppointmentCard(Map<String, Object> appointmentCardInfo) {

		Long visitRid = Long.valueOf(appointmentCardInfo.get("visitRid").toString());
		Integer timezoneOffset = Integer.valueOf(appointmentCardInfo.get("timezoneOffset").toString());
		String timezoneId = (String) appointmentCardInfo.get("timezoneId");

		EmrVisit emrVisit = emrVisitService.findOne(SearchCriterion.generateRidFilter(visitRid, FilterOperator.eq), EmrVisit.class);

		BigDecimal paid = emrVisit.getPaidAmount();
		BigDecimal notPaid = emrVisit.getTotalAmount().subtract(paid);

		Map<JRDataSource, Map<String, Object>> allReports = new HashMap<>();

		JRDataSource resultsDataSource = new JRBeanCollectionDataSource(Arrays.asList(emrVisit));
		Map<String, Object> parameterMap = new HashMap<String, Object>();
		parameterMap.put(JRParameter.REPORT_TIME_ZONE, DateUtil.returnClientTimeZone(timezoneId, timezoneOffset));
		parameterMap.put("datasource", resultsDataSource);
		parameterMap.put("paid", paid);
		parameterMap.put("notPaid", notPaid);
		parameterMap.put("namePrimary", comTenantLanguageService.getTenantNamePrimary());

		allReports.put(resultsDataSource, parameterMap);
		return allReports;
	}

	@InterceptorFree
	@PreAuthorize("hasAuthority('" + EhopeRights.VIEW_CLAIM_REPORT + "')")
	public Map<JRDataSource, Map<String, Object>> generateClaimSummarizedReport(Map<String, Object> claimInformation) {

		Date dateFrom = DateUtil.parseUTCDate(claimInformation.get("claimDateFrom").toString());
		Date dateTo = DateUtil.parseUTCDate(claimInformation.get("claimDateTo").toString());
		Integer timezoneOffset = Integer.valueOf(claimInformation.get("timezoneOffset").toString());
		String timezoneId = (String) claimInformation.get("timezoneId");

		dateTo = DateUtil.addDays(dateTo, 1);
		dateTo = DateUtil.addSeconds(dateTo, -1);

		Long branchId = Long.valueOf("0");
		Long insuranceId = Long.valueOf("0");

		try {
			branchId = Long.valueOf(claimInformation.get("branchRid").toString());
		} catch (NumberFormatException e) {
			System.out.println(e);
		}

		List<Long> branches = emrVisitService.retrieveBranchesList(branchId);

		try {
			insuranceId = Long.valueOf(claimInformation.get("providerRid").toString());
		} catch (NumberFormatException e) {
			System.out.println(e);
		}

		List<Long> insurances = emrVisitService.retrieveInsurancesList(insuranceId, false, Arrays.asList());

		List<EmrVisit> visitsList = emrVisitService.getClaimSummarized(dateFrom, dateTo,
				Arrays.asList(OperationStatus.ABORTED.getValue(), OperationStatus.CANCELLED.getValue()), insurances, branches);

		if (CollectionUtil.isCollectionEmpty(visitsList)) {
			throw new BusinessException("No Visits In The Selected Period", "noOrdersInSelectedPeriod", ErrorSeverity.ERROR);
		}

		Map<JRDataSource, Map<String, Object>> allReports = new HashMap<>();

		//List<ClaimSummarizedWrapper> myWrapper = new ArrayList<ClaimSummarizedWrapper>();
		Map<String, ClaimSummarizedWrapper> wrapperMap = new HashMap<>();

		BigDecimal claimNetAmountSum = BigDecimal.ZERO;
		BigDecimal coPaymentSum = BigDecimal.ZERO;
		BigDecimal discountSum = BigDecimal.ZERO;
		BigDecimal overallTotalSum = BigDecimal.ZERO;

		///////////////////////////////////////////////////////////////////////////////
		for (EmrVisit emrVisit : visitsList) {
			ClaimSummarizedWrapper tempVisitInfo = new ClaimSummarizedWrapper();

			tempVisitInfo.setCompanyName(emrVisit.getProviderPlan().getInsProvider().getName());

			BigDecimal overallTotal = BigDecimal.ZERO;
			BigDecimal insClaimNetAmount = BigDecimal.ZERO;
			BigDecimal insDiscountAmount = BigDecimal.ZERO;
			BigDecimal patientCoPayment = BigDecimal.ZERO;
			BigDecimal visitClaimAmount = BigDecimal.ZERO;

			List<BillChargeSlip> bcs = emrVisit	.getLabSamples().stream().flatMap(s -> s.getLabTestActualSet().stream())
												.flatMap(lta -> lta.getBillChargeSlipList().stream()).collect(Collectors.toList());

			if (!CollectionUtil.isCollectionEmpty(bcs)) {
				for (BillChargeSlip billChargeSlip : bcs) {
					if (billChargeSlip.getIsCancelled() || billChargeSlip.getInsCoverageResult() == null) {
						continue;
					}

					insClaimNetAmount = insClaimNetAmount.add(
							billChargeSlip.getInsCoverageResult().subtract(billChargeSlip.getInsDeductionResult()));
					insDiscountAmount = insDiscountAmount.add(billChargeSlip.getInsDeductionResult());
					patientCoPayment = patientCoPayment.add(billChargeSlip.getAmountAfterCoverage());
				} //end of charge slip loop
			}

			if (emrVisit.getVisitType().getCode().equals(VisitType.REFERRAL.getValue())) {
				patientCoPayment = BigDecimal.ZERO;
			}

			visitClaimAmount = patientCoPayment.add(insClaimNetAmount);
			overallTotal = visitClaimAmount.add(insDiscountAmount);

			tempVisitInfo.setClaimedNetAmount(insClaimNetAmount);
			tempVisitInfo.setContractDiscount(insDiscountAmount);
			tempVisitInfo.setCoPayment(patientCoPayment);
			tempVisitInfo.setFullCharge(overallTotal);

			if (insClaimNetAmount.compareTo(BigDecimal.ZERO) > 0) {
				claimNetAmountSum = claimNetAmountSum.add(insClaimNetAmount);
				coPaymentSum = coPaymentSum.add(patientCoPayment);
				discountSum = discountSum.add(insDiscountAmount);
				overallTotalSum = overallTotalSum.add(overallTotal);

				if (!wrapperMap.containsKey(emrVisit.getProviderPlan().getInsProvider().getCode())) {
					wrapperMap.put(emrVisit.getProviderPlan().getInsProvider().getCode(), tempVisitInfo);
				} else {
					ClaimSummarizedWrapper tempWrapper = wrapperMap.get(emrVisit.getProviderPlan().getInsProvider().getCode());
					tempWrapper.setContractDiscount(tempWrapper.getContractDiscount().add(tempVisitInfo.getContractDiscount()));
					tempWrapper.setCoPayment(tempWrapper.getCoPayment().add(tempVisitInfo.getCoPayment()));
					tempWrapper.setClaimedNetAmount(tempWrapper.getClaimedNetAmount().add(tempVisitInfo.getClaimedNetAmount()));
					tempWrapper.setFullCharge(tempWrapper.getFullCharge().add(tempVisitInfo.getFullCharge()));

					wrapperMap.put(emrVisit.getProviderPlan().getInsProvider().getCode(), tempWrapper);
				}
			}

			//myWrapper.add(tempVisitInfo);
		}
		///////////////////////////////////////////////////////////////////////////////

		List<ClaimSummarizedWrapper> wrapper = new ArrayList<ClaimSummarizedWrapper>();
		wrapper.addAll(wrapperMap.values());

		if (CollectionUtil.isCollectionEmpty(wrapper)) {
			throw new BusinessException("No Visits In The Selected Period", "noOrdersInSelectedPeriod", ErrorSeverity.ERROR);
		}

		String branchName = "";

		if (branches.size() == 1) {
			LabBranch labBranch = labBranchService.findOne(SearchCriterion.generateRidFilter(branchId, FilterOperator.eq), LabBranch.class);
			branchName = labBranch.getName().get("ar_jo");
		} else {
			branchName = "All Branches";
		}

		String insuranceName = "";

		if (insurances.size() == 1) {
			InsProvider insProvider = insProviderService.findOne(SearchCriterion.generateRidFilter(insuranceId, FilterOperator.eq),
					InsProvider.class);
			insuranceName = insProvider.getName().get("ar_jo");
			if (insuranceName.equals("")) {
				insuranceName = insProvider.getName().get("en_us");
			}
		} else {
			insuranceName = "All Insurances";
		}

		JRDataSource resultsDataSource = new JRBeanCollectionDataSource(wrapper);
		Map<String, Object> parameterMap = new HashMap<String, Object>();
		parameterMap.put("datasource", resultsDataSource);
		parameterMap.put(JRParameter.REPORT_TIME_ZONE, DateUtil.returnClientTimeZone(timezoneId, timezoneOffset));
		parameterMap.put("dateFrom", dateFrom);
		parameterMap.put("dateTo", dateTo);
		parameterMap.put("userNumber", secUserService.findById(SecurityUtil.getCurrentUser().getRid()).getMobileNo());
		parameterMap.put("userEmail", secUserService.findById(SecurityUtil.getCurrentUser().getRid()).getEmail());
		parameterMap.put("insuranceName", insuranceName);
		parameterMap.put("branchName", branchName);
		parameterMap.put("fullChargeTotal", overallTotalSum);
		parameterMap.put("coPaymentTotal", coPaymentSum);
		parameterMap.put("contractDiscountTotal", discountSum);
		parameterMap.put("claimNetAmountTotal", claimNetAmountSum);
		parameterMap.put("namePrimary", comTenantLanguageService.getTenantNamePrimary());

		allReports.put(resultsDataSource, parameterMap);
		return allReports;
	}

	@InterceptorFree
	@PreAuthorize("hasAuthority('" + EhopeRights.VIEW_CLAIM_REPORT + "')")
	public Map<JRDataSource, Map<String, Object>> generateClaimDetailedReport(Map<String, Object> claimInformation) {

		Date dateFrom = DateUtil.parseUTCDate(claimInformation.get("claimDateFrom").toString());
		Date dateTo = DateUtil.parseUTCDate(claimInformation.get("claimDateTo").toString());
		Integer timezoneOffset = Integer.valueOf(claimInformation.get("timezoneOffset").toString());
		String timezoneId = (String) claimInformation.get("timezoneId");

		dateTo = DateUtil.addDays(dateTo, 1);
		dateTo = DateUtil.addSeconds(dateTo, -1);

		Long branchId = Long.valueOf("0");
		Long insuranceId = Long.valueOf("0");

		try {
			branchId = Long.valueOf(claimInformation.get("branchRid").toString());
		} catch (NumberFormatException e) {
			System.out.println(e);
		}

		List<Long> branches = emrVisitService.retrieveBranchesList(branchId);

		try {
			insuranceId = Long.valueOf(claimInformation.get("providerRid").toString());
		} catch (NumberFormatException e) {
			System.out.println(e);
		}

		List<Long> insurances = emrVisitService.retrieveInsurancesList(insuranceId, false, Arrays.asList());

		List<EmrVisit> visitsList = emrVisitService.getClaimDetailed(dateFrom, dateTo,
				Arrays.asList(OperationStatus.ABORTED.getValue(), OperationStatus.CANCELLED.getValue()), insurances, branches);

		if (CollectionUtil.isCollectionEmpty(visitsList)) {
			throw new BusinessException("No Visits In The Selected Period", "noOrdersInSelectedPeriod", ErrorSeverity.ERROR);
		}

		Map<JRDataSource, Map<String, Object>> allReports = new HashMap<>();

		List<ClaimDetailedWrapper> myWrapper = new ArrayList<ClaimDetailedWrapper>();
		BigDecimal claimNetAmountSum = BigDecimal.ZERO;
		BigDecimal coPaymentSum = BigDecimal.ZERO;
		BigDecimal claimAmountSum = BigDecimal.ZERO;
		BigDecimal discountSum = BigDecimal.ZERO;
		BigDecimal overallTotalSum = BigDecimal.ZERO;

		///////////////////////////////////////////////////////////////////////////////
		for (EmrVisit emrVisit : visitsList) {
			ClaimDetailedWrapper tempVisitInfo = new ClaimDetailedWrapper();
			tempVisitInfo.setEmrVisit(emrVisit);

			BigDecimal overallTotal = BigDecimal.ZERO;
			BigDecimal insClaimNetAmount = BigDecimal.ZERO;
			BigDecimal insDiscountAmount = BigDecimal.ZERO;
			BigDecimal patientCoPayment = BigDecimal.ZERO;
			BigDecimal visitClaimAmount = BigDecimal.ZERO;

			List<BillChargeSlip> bcs = emrVisit	.getLabSamples().stream().flatMap(s -> s.getLabTestActualSet().stream())
												.flatMap(lta -> lta.getBillChargeSlipList().stream()).collect(Collectors.toList());

			if (!CollectionUtil.isCollectionEmpty(bcs)) {
				for (BillChargeSlip billChargeSlip : bcs) {
					if (billChargeSlip.getIsCancelled() || billChargeSlip.getInsCoverageResult() == null) {
						continue;
					}

					insClaimNetAmount = insClaimNetAmount.add(
							billChargeSlip.getInsCoverageResult().subtract(billChargeSlip.getInsDeductionResult()));
					insDiscountAmount = insDiscountAmount.add(billChargeSlip.getInsDeductionResult());
					patientCoPayment = patientCoPayment.add(billChargeSlip.getAmountAfterCoverage());
				} //end of charge slip loop

			}
			if (emrVisit.getVisitType().getCode().equals(VisitType.REFERRAL.getValue())) {
				patientCoPayment = BigDecimal.ZERO;
			}

			visitClaimAmount = patientCoPayment.add(insClaimNetAmount);
			overallTotal = visitClaimAmount.add(insDiscountAmount);

			tempVisitInfo.setClaimedNetAmount(insClaimNetAmount);
			tempVisitInfo.setContractDiscount(insDiscountAmount);
			tempVisitInfo.setCoPayment(patientCoPayment);
			tempVisitInfo.setClaimAmount(visitClaimAmount);
			tempVisitInfo.setFullCharge(overallTotal);

			if (insClaimNetAmount.compareTo(BigDecimal.ZERO) > 0) {
				claimNetAmountSum = claimNetAmountSum.add(insClaimNetAmount);
				coPaymentSum = coPaymentSum.add(patientCoPayment);
				discountSum = discountSum.add(insDiscountAmount);
				claimAmountSum = claimAmountSum.add(visitClaimAmount);
				overallTotalSum = overallTotalSum.add(overallTotal);

				myWrapper.add(tempVisitInfo);
			}
		}

		if (CollectionUtil.isCollectionEmpty(myWrapper)) {
			throw new BusinessException("No Visits In The Selected Period", "noOrdersInSelectedPeriod", ErrorSeverity.ERROR);
		}

		String branchName = "";

		if (branches.size() == 1) {
			LabBranch labBranch = labBranchService.findOne(SearchCriterion.generateRidFilter(branchId, FilterOperator.eq), LabBranch.class);
			branchName = labBranch.getName().get("ar_jo");
		} else {
			branchName = "All Branches";
		}

		String insuranceName = "";

		if (insurances.size() == 1) {
			InsProvider insProvider = insProviderService.findOne(SearchCriterion.generateRidFilter(insuranceId, FilterOperator.eq),
					InsProvider.class);
			insuranceName = insProvider.getName().get("ar_jo");

			if (insuranceName.equals("")) {
				insuranceName = insProvider.getName().get("en_us");
			}

		} else {
			insuranceName = "All Insurances";
		}

		JRDataSource resultsDataSource = new JRBeanCollectionDataSource(myWrapper);
		Map<String, Object> parameterMap = new HashMap<String, Object>();
		parameterMap.put("datasource", resultsDataSource);
		parameterMap.put(JRParameter.REPORT_TIME_ZONE, DateUtil.returnClientTimeZone(timezoneId, timezoneOffset));
		parameterMap.put("dateFrom", dateFrom);
		parameterMap.put("dateTo", dateTo);
		parameterMap.put("insuranceName", insuranceName);
		parameterMap.put("branchName", branchName);

		parameterMap.put("totalsTotal", overallTotalSum);
		parameterMap.put("discountTotal", discountSum);
		parameterMap.put("claimAmountTotal", claimAmountSum);
		parameterMap.put("coPaymentTotal", coPaymentSum);
		parameterMap.put("claimNetAmountTotal", claimNetAmountSum);
		parameterMap.put("namePrimary", comTenantLanguageService.getTenantNamePrimary());

		allReports.put(resultsDataSource, parameterMap);
		return allReports;
	}

	public Map<JRDataSource, Map<String, Object>> generateDailyCreditPaymentReport(Map<String, Object> dailyCreditInformation) {

		Date dateFrom = DateUtil.parseUTCDate(dailyCreditInformation.get("dailyCreditPaymentDateFrom").toString());
		Date dateTo = DateUtil.parseUTCDate(dailyCreditInformation.get("dailyCreditPaymentDateTo").toString());
		Integer timezoneOffset = Integer.valueOf(dailyCreditInformation.get("timezoneOffset").toString());
		String timezoneId = (String) dailyCreditInformation.get("timezoneId");

		dateTo = DateUtil.addDays(dateTo, 1);
		dateTo = DateUtil.addSeconds(dateTo, -1);

		Long branchId = Long.valueOf(dailyCreditInformation.get("branchRid").toString());

		if (!SecurityUtil.isBranchIdAllowed(branchId)) {
			throw new BusinessException("Branch is not the same as the user branch!", "branchIsNotUserBranch",
					ErrorSeverity.ERROR);
		}

		List<Long> branches = emrVisitService.retrieveBranchesList(branchId);

		List<Long> visitsRids = emrVisitService.getDailyCreditPaymentVisits(dateFrom, dateTo,
				Arrays.asList(OperationStatus.ABORTED.getValue(), OperationStatus.CANCELLED.getValue()), branches);

		if (CollectionUtil.isCollectionEmpty(visitsRids)) {
			throw new BusinessException("No Visits In The Selected Period", "noOrdersInSelectedPeriod", ErrorSeverity.ERROR);
		}

		Map<JRDataSource, Map<String, Object>> allReports = new HashMap<>();

		List<EmrVisit> visitsList = emrVisitService.getDailyCreditPayment(visitsRids);

		List<DailyIncomeWrapper> visitsInfo = new ArrayList<DailyIncomeWrapper>();
		BigDecimal creditTotal = BigDecimal.ZERO;

		///////////////////////////////////////////////////////////////////////////////
		for (EmrVisit emrVisit : visitsList) {
			DailyIncomeWrapper tempVisitInfo = new DailyIncomeWrapper();
			tempVisitInfo.setEmrVisit(emrVisit);

			BigDecimal totalCash = BigDecimal.ZERO;
			//BigDecimal totalDiscount = BigDecimal.ZERO;			

			List<BillChargeSlip> bcs = emrVisit	.getLabSamples().stream().flatMap(s -> s.getLabTestActualSet().stream())
												.flatMap(lta -> lta.getBillChargeSlipList().stream()).collect(Collectors.toList());

			if (!CollectionUtil.isCollectionEmpty(bcs)) {
				for (BillChargeSlip billChargeSlip : bcs) {
					if (billChargeSlip.getIsCancelled() || billChargeSlip.getInsCoverageResult() == null) {
						continue;
					}

					totalCash = totalCash.add(
							billChargeSlip.getInsCoverageResult().subtract(billChargeSlip.getInsDeductionResult()));
				}
			}
			tempVisitInfo.setTotalCash(totalCash);

			if (totalCash.compareTo(BigDecimal.ZERO) > 0) {
				visitsInfo.add(tempVisitInfo);
			}
		}
		///////////////////////////////////////////////////////////////////////////////

		///////////////////////////////////////////////////////////////////////////////
		List<DailyCashPaymentWrapper> wrapper = new ArrayList<DailyCashPaymentWrapper>();

		for (DailyIncomeWrapper info : visitsInfo) {
			DailyCashPaymentWrapper tempWrapper = new DailyCashPaymentWrapper();
			tempWrapper.setEmrVisit(info.getEmrVisit());
			tempWrapper.setTotalAmount(info.getTotalCash());
			creditTotal = creditTotal.add(info.getTotalCash());
			wrapper.add(tempWrapper);
		}
		///////////////////////////////////////////////////////////////////////////////
		String branchName = "";

		if (branches.size() == 1) {
			LabBranch labBranch = labBranchService.findOne(SearchCriterion.generateRidFilter(branchId, FilterOperator.eq), LabBranch.class);
			branchName = labBranch.getName().get("en_us");
		} else {
			branchName = "All Branches";
		}

		String userName = secUserService.findById(SecurityUtil.getCurrentUser().getRid()).getUsername();

		JRDataSource resultsDataSource = new JRBeanCollectionDataSource(wrapper);
		Map<String, Object> parameterMap = new HashMap<String, Object>();
		parameterMap.put("datasource", resultsDataSource);
		parameterMap.put(JRParameter.REPORT_TIME_ZONE, DateUtil.returnClientTimeZone(timezoneId, timezoneOffset));
		parameterMap.put("dateFrom", dateFrom);
		parameterMap.put("dateTo", dateTo);
		parameterMap.put("branchName", branchName);
		parameterMap.put("creditTotal", creditTotal);
		parameterMap.put("userName", userName);
		parameterMap.put("namePrimary", comTenantLanguageService.getTenantNamePrimary());

		allReports.put(resultsDataSource, parameterMap);
		return allReports;
	}

	public Map<JRDataSource, Map<String, Object>> generateReferralOutReport(Map<String, Object> referralFilters) {
		Date referralDateFrom = DateUtil.parseUTCDate(referralFilters.get("referralOutDateFrom").toString());
		Date referralDateTo = DateUtil.parseUTCDate(referralFilters.get("referralOutDateTo").toString());
		Integer timezoneOffset = Integer.valueOf(referralFilters.get("timezoneOffset").toString());
		String timezoneId = (String) referralFilters.get("timezoneId");

		referralDateTo = DateUtil.addDays(referralDateTo, 1);
		referralDateTo = DateUtil.addSeconds(referralDateTo, -1);

		List<Long> insurances = new ArrayList<Long>();

		Long destinationTypeRid = null;
		List<LkpTestDestinationType> destinationTypesList = new ArrayList<LkpTestDestinationType>();
		List<String> destinationTypeCodeList = new ArrayList<String>();

		if (referralFilters.get("destinationTypeRid") != null) {
			destinationTypeRid = Long.valueOf(referralFilters.get("destinationTypeRid").toString());
			if (destinationTypeRid != -1L) {
				destinationTypesList.add(lkpService.findOneAnyLkp(
						Arrays.asList(new SearchCriterion("rid", destinationTypeRid, FilterOperator.eq)), LkpTestDestinationType.class));

				destinationTypeCodeList.add(destinationTypesList.get(0).getCode());
			} else {
				destinationTypesList.addAll(lkpService.findAnyLkp(
						Arrays.asList(new SearchCriterion("code", TestDestinationType.WORKBENCH.getValue(), FilterOperator.neq)),
						LkpTestDestinationType.class, null));
				destinationTypeCodeList.addAll(destinationTypesList.stream().map(dtl -> dtl.getCode()).collect(Collectors.toList()));
			}
			insurances = emrVisitService.retrieveInsurancesList(Long.valueOf(referralFilters.get("insuranceRid").toString()), true,
					destinationTypeCodeList);
		} else {
			insurances = emrVisitService.retrieveInsurancesList(Long.valueOf(referralFilters.get("insuranceRid").toString()), false,
					Arrays.asList());
		}

		List<EmrVisit> referralVisits = emrVisitService.getReferralOutVisits(referralDateFrom, referralDateTo, insurances,
				Arrays.asList(OperationStatus.ABORTED.getValue(), OperationStatus.CANCELLED.getValue()),
				Arrays.asList(OperationStatus.ABORTED.getValue(), OperationStatus.CANCELLED.getValue()),
				TestDestinationType.WORKBENCH.getValue()); //destinationTypeCodeList

		if (CollectionUtil.isCollectionEmpty(referralVisits)) {
			throw new BusinessException("No Visits In The Selected Period", "noOrdersInSelectedPeriod", ErrorSeverity.ERROR);
		}

		Map<JRDataSource, Map<String, Object>> allReports = new HashMap<>();

		List<ReferralOutWrapper> wrapper = new ArrayList<ReferralOutWrapper>();
		BigDecimal originalPriceTotal = BigDecimal.ZERO;
		BigDecimal referralPriceTotal = BigDecimal.ZERO;

		for (EmrVisit emrVisit : referralVisits) {
			ReferralOutWrapper tempWrapper = new ReferralOutWrapper();
			tempWrapper.setEmrVisit(emrVisit);

			List<BillChargeSlip> bcs = emrVisit	.getLabSamples().stream().flatMap(s -> s.getLabTestActualSet().stream())
												.flatMap(lta -> lta.getBillChargeSlipList().stream()).collect(Collectors.toList());

			BigDecimal originalPrice = BigDecimal.ZERO;
			BigDecimal referralPrice = BigDecimal.ZERO;

			for (BillChargeSlip billChargeSlip : bcs) {
				if (billChargeSlip.getOriginalPrice() != null) {
					originalPrice = originalPrice.add(billChargeSlip.getOriginalPrice());
				}

				if (billChargeSlip.getReferralAmount() != null) {
					referralPrice = referralPrice.add(billChargeSlip.getReferralAmount());
				}
			}

			tempWrapper.setOriginalPrices(originalPrice);
			tempWrapper.setReferralPrices(referralPrice);

			originalPriceTotal = originalPriceTotal.add(originalPrice);
			referralPriceTotal = referralPriceTotal.add(referralPrice);

			wrapper.add(tempWrapper);
		}

		String branchName = labBranchService.findOne(
				SearchCriterion.generateRidFilter(SecurityUtil.getCurrentUser().getBranchId(), FilterOperator.eq), LabBranch.class)
											.getName().get("en_us");

		String tenantName = secTenantService.findById(SecurityUtil.getCurrentUser().getTenantId()).getName();

		JRDataSource resultsDataSource = new JRBeanCollectionDataSource(wrapper);
		Map<String, Object> parameterMap = new HashMap<String, Object>();
		parameterMap.put("datasource", resultsDataSource);
		parameterMap.put(JRParameter.REPORT_TIME_ZONE, DateUtil.returnClientTimeZone(timezoneId, timezoneOffset));
		parameterMap.put("dateFrom", referralDateFrom);
		parameterMap.put("dateTo", referralDateTo);
		parameterMap.put("branchName", branchName);
		parameterMap.put("tenantName", tenantName);
		parameterMap.put("originalPriceTotal", originalPriceTotal);
		parameterMap.put("referralPriceTotal", referralPriceTotal);
		parameterMap.put("namePrimary", comTenantLanguageService.getTenantNamePrimary());

		allReports.put(resultsDataSource, parameterMap);
		return allReports;
	}

}
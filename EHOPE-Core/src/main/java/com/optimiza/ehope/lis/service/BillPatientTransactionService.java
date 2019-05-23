package com.optimiza.ehope.lis.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import com.optimiza.core.admin.service.SecUserService;
import com.optimiza.core.base.helper.SearchCriterion;
import com.optimiza.core.base.helper.SearchCriterion.FilterOperator;
import com.optimiza.core.base.service.GenericService;
import com.optimiza.core.common.business.exception.BusinessException;
import com.optimiza.core.common.business.exception.BusinessException.ErrorSeverity;
import com.optimiza.core.common.util.CollectionUtil;
import com.optimiza.core.common.util.ReflectionUtil;
import com.optimiza.core.common.util.SecurityUtil;
import com.optimiza.core.common.util.StringUtil;
import com.optimiza.core.lkp.service.LkpService;
import com.optimiza.ehope.lis.helper.BalanceTransactionType;
import com.optimiza.ehope.lis.helper.CalculationData;
import com.optimiza.ehope.lis.helper.EhopeRights;
import com.optimiza.ehope.lis.lkp.helper.OperationStatus;
import com.optimiza.ehope.lis.lkp.helper.PaymentMethod;
import com.optimiza.ehope.lis.lkp.helper.SerialType;
import com.optimiza.ehope.lis.lkp.helper.TestDestinationType;
import com.optimiza.ehope.lis.lkp.helper.TransactionType;
import com.optimiza.ehope.lis.lkp.helper.VisitType;
import com.optimiza.ehope.lis.lkp.model.LkpPaymentMethod;
import com.optimiza.ehope.lis.lkp.model.LkpTransactionType;
import com.optimiza.ehope.lis.model.BillBalanceTransaction;
import com.optimiza.ehope.lis.model.BillChargeSlip;
import com.optimiza.ehope.lis.model.BillClassification;
import com.optimiza.ehope.lis.model.BillMasterItem;
import com.optimiza.ehope.lis.model.BillPatientTransaction;
import com.optimiza.ehope.lis.model.BillPriceList;
import com.optimiza.ehope.lis.model.BillPricing;
import com.optimiza.ehope.lis.model.BillTestItem;
import com.optimiza.ehope.lis.model.EmrPatientInfo;
import com.optimiza.ehope.lis.model.EmrPatientInsuranceInfo;
import com.optimiza.ehope.lis.model.EmrVisit;
import com.optimiza.ehope.lis.model.EmrVisitGroup;
import com.optimiza.ehope.lis.model.InsCoverageDetail;
import com.optimiza.ehope.lis.model.InsProvider;
import com.optimiza.ehope.lis.model.InsProviderPlan;
import com.optimiza.ehope.lis.model.LabBranch;
import com.optimiza.ehope.lis.model.LabTestActual;
import com.optimiza.ehope.lis.model.TestDefinition;
import com.optimiza.ehope.lis.model.TestGroup;
import com.optimiza.ehope.lis.model.TestGroupDefinition;
import com.optimiza.ehope.lis.repo.BillPatientTransactionRepo;
import com.optimiza.ehope.lis.util.NumberUtil;
import com.optimiza.ehope.lis.wrapper.PaymentInformation;
import com.optimiza.ehope.lis.wrapper.TestPricingWrapper;
import com.optimiza.ehope.lis.wrapper.TestPricingWrapper.TestPayment;

/**
 * BillPatientTransactionService.java
 * 
 * @author Abdullah Imran <aImran@optimizasolutions.com>
 * @since Feb/19/2018
 **/

@Service("BillPatientTransactionService")
public class BillPatientTransactionService extends GenericService<BillPatientTransaction, BillPatientTransactionRepo> {

	@Autowired
	private BillPatientTransactionRepo billPatientTransactionRepo;
	@Autowired
	private LkpService lkpService;
	@Autowired
	private SysSerialService serialSerivce;
	@Autowired
	private BillTestItemService billTestItemService;
	@Autowired
	private BillPriceListService billPriceListService;
	@Autowired
	private BillMasterItemService billMasterItemService;
	@Autowired
	private InsProviderPlanService insProviderPlanService;
	@Autowired
	private BillChargeSlipService billChargeSlipService;
	@Autowired
	private EmrVisitService visitService;
	@Autowired
	private BillBalanceTransactionService balanceTransactionService;
	@Autowired
	private LabBranchService branchService;
	@Autowired
	private TestGroupService testGroupService;
	@Autowired
	private SecUserService userService;
	@Autowired
	private TestDefinitionService testDefinitionService;
	@Autowired
	private EmrPatientInfoService patientInfoService;
	@Autowired
	private InsProviderService insProviderService;
	@Autowired
	private EntityManager entityManager;
	@Autowired
	private LabSampleService sampleService;
	@Autowired
	private LabTestActualService testActualService;

	public void deleteAllByBillChargeSlipIn(List<BillChargeSlip> chargeSlips) {
		getRepository().deleteAllByBillChargeSlipIn(chargeSlips);
	}

	/**
	 * Getting how much actually got paid in this visit by using the BillPatientTransaction, because using visit paid amount column sometimes is wrong
	 * in case of skipping because we set visit paid amount as fully paid but patient didn't 'actually' pay.
	 *
	 * @param visitRid
	 * @return actual paid amount
	 */
	public BigDecimal getVisitActualPaidAmount(Long visitRid) {
		BigDecimal previousPaidAmount = BigDecimal.ZERO;
		Set<BillPatientTransaction> transactions = getRepository().findByVisit(visitRid);
		for (BillPatientTransaction bpt : transactions) {
			BigDecimal amount = bpt.getAmount();
			TransactionType tt = TransactionType.valueOf(bpt.getLkpTransactionType().getCode());
			if (tt == TransactionType.CANCEL || tt == TransactionType.RECALCULATE) {
				continue;
			}
			switch (tt) {
				case PAYMENT:
					previousPaidAmount = previousPaidAmount.add(amount);
					break;
				case REFUND:
					previousPaidAmount = previousPaidAmount.subtract(amount);
					break;
			}
		}
		return previousPaidAmount;
	}

	/**
	 * In case of edit order to get the previous general discount and the total paid amount
	 * 
	 * @param visitRid
	 * @return Map
	 */
	public Map<String, Object> getPreviousPaymentDialogData(Long visitRid) {
		Map<String, Object> result = new HashMap<>();
		List<BillChargeSlip> previousChargeSlips = billChargeSlipService.findByVisit(visitRid);
		BigDecimal previousPaidAmount = BigDecimal.ZERO;
		BigDecimal prevGeneralDisPercentage = null;
		BigDecimal prevGeneralDisAmount = null;
		if (!CollectionUtil.isCollectionEmpty(previousChargeSlips)) {
			if (previousChargeSlips.get(0).getGeneralDiscountAmount() != null) {
				prevGeneralDisAmount = previousChargeSlips.get(0).getGeneralDiscountAmount();
			} else if (previousChargeSlips.get(0).getGeneralDiscountPercentage() != null) {
				prevGeneralDisPercentage = previousChargeSlips.get(0).getGeneralDiscountPercentage();
			}
			previousPaidAmount = getVisitActualPaidAmount(visitRid);
		}
		result.put("previousPaidAmount", previousPaidAmount);
		result.put("prevGeneralDisPercentage", prevGeneralDisPercentage);
		result.put("prevGeneralDisAmount", prevGeneralDisAmount);
		return result;
	}

	/**
	 * For refunding functionality on screen.
	 * 
	 * @param visitRid
	 * @return Map
	 */
	public Map<String, Object> getRefundInfo(Long visitRid) {
		EmrVisit visit = visitService.findOne(SearchCriterion.generateRidFilter(visitRid, FilterOperator.eq), EmrVisit.class,
				"lkpOperationStatus");
		Map<String, Object> map = new HashMap<>();
		List<TestPayment> payments = getRefundData(visitRid);
		List<BillChargeSlip> chargeSlips = billChargeSlipService.findByVisit(visitRid).stream().filter(BillChargeSlip::getIsCancelled)
																.collect(Collectors.toList());
		//if visit is cancelled and there no total amount left in visit , still patient has some money it then refund all paid amount
		if (OperationStatus.CANCELLED == OperationStatus.valueOf(visit.getLkpOperationStatus().getCode())
				&& visit.getTotalAmount().compareTo(BigDecimal.ZERO) == 0 && visit.getPaidAmount().compareTo(BigDecimal.ZERO) == 1) {
			map.put("cancelledAmounts", visit.getPaidAmount());
		} else if (CollectionUtil.isCollectionEmpty(chargeSlips) && visit.getPaidAmount().compareTo(visit.getTotalAmount()) == 1) {
			//if user edited visit and added insurance or whatever then allow to refund only if paid is more than total which means
			//patient has extra credit in this visit
			map.put("cancelledAmounts", visit.getPaidAmount().subtract(visit.getTotalAmount()));
		} else {
			BigDecimal cancelledAmounts = chargeSlips.stream().map(BillChargeSlip::getAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
			BigDecimal refundedAmounts = getRepository().findByVisit(visitRid).stream().filter(
					bpt -> TransactionType.REFUND == TransactionType.valueOf(bpt.getLkpTransactionType().getCode()))
														.map(BillPatientTransaction::getAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
			cancelledAmounts = cancelledAmounts.subtract(refundedAmounts);//remaining amount to refund
			//either no cancelled amount to return or we already refunded everything
			//or patient didn't pay at all
			if (cancelledAmounts.compareTo(BigDecimal.ZERO) <= 0 || visit.getPaidAmount().compareTo(BigDecimal.ZERO) <= 0) {
				throw new BusinessException("Can't refund", "refundFail", ErrorSeverity.ERROR);
			}
			cancelledAmounts = cancelledAmounts.min(visit.getPaidAmount());//take the least amount from paid amount and cancelledAmounts
			map.put("cancelledAmounts", cancelledAmounts);
		}
		map.put("payments", payments);
		return map;
	}

	/**
	 * Get the required data to refund for the patient
	 * 
	 * @param visitRid
	 * @return patient payments
	 */
	public List<TestPayment> getRefundData(Long visitRid) {
		List<LkpPaymentMethod> paymenthMethods = lkpService.findAnyLkp(new ArrayList<>(), LkpPaymentMethod.class, null);
		Set<BillPatientTransaction> transactions = getRepository().findByVisit(visitRid);
		Map<PaymentMethod, TestPayment> testPaymentsMap = new HashMap<>();
		for (LkpPaymentMethod lpm : paymenthMethods) {
			TestPayment tp = new TestPayment();
			tp.setAmount(BigDecimal.ZERO);
			tp.setLkpPaymentMethod(lpm);
			testPaymentsMap.put(PaymentMethod.valueOf(lpm.getCode()), tp);
		}

		for (BillPatientTransaction bpt : transactions) {
			BigDecimal amount = bpt.getAmount();
			TransactionType tt = TransactionType.valueOf(bpt.getLkpTransactionType().getCode());
			if (tt == TransactionType.CANCEL || tt == TransactionType.RECALCULATE) {
				continue;
			}
			PaymentMethod pm = PaymentMethod.valueOf(bpt.getLkpPaymentMethod().getCode());
			switch (tt) {
				case PAYMENT:
					testPaymentsMap.get(pm).setAmount(testPaymentsMap.get(pm).getAmount().add(amount));
					break;
				case REFUND:
					testPaymentsMap.get(pm).setAmount(testPaymentsMap.get(pm).getAmount().subtract(amount));
					break;
			}
		}
		return testPaymentsMap.values().stream().filter(tp -> tp.getAmount().compareTo(BigDecimal.ZERO) == 1).collect(Collectors.toList());
		// we reduce the payments in the map by the difference in visit paid amount.
		// if the amount is less than the total of the payments then it is ok.
		// otherwise we reduced because the reduction came from cancellation and when we cancel we don't specify a type like cash.
		//		testPaymentsMap.entrySet().removeIf(tpm -> tpm.getValue().getAmount().compareTo(BigDecimal.ZERO) <= 0);
		//		BigDecimal sumOfTestPayments = testPaymentsMap.values().stream().map(TestPayment::getAmount).reduce(BigDecimal.ZERO,
		//				BigDecimal::add);
		//		if (sumOfTestPayments.compareTo(visitPaidAmount) == 1) {
		//			BigDecimal difference = sumOfTestPayments.subtract(visitPaidAmount);
		//			for (TestPayment tp : testPaymentsMap.values()) {
		//				if (difference.compareTo(BigDecimal.ZERO) <= 0) {
		//					break;
		//				}
		//				BigDecimal tpAmount = tp.getAmount();
		//				if (tpAmount.compareTo(difference) >= 0) {
		//					tp.setAmount(tpAmount.subtract(difference));
		//				} else {
		//					tp.setAmount(BigDecimal.ZERO);
		//				}
		//				difference = difference.subtract(tpAmount);
		//			}
		//		}
		//		for (BillPatientTransaction bpt : transactions) {
		//			BigDecimal amount = bpt.getBillChargeSlip().getAmount();
		//			TransactionType tt = TransactionType.valueOf(bpt.getLkpTransactionType().getCode());
		//			if (!tt.equals(TransactionType.CANCEL)) {
		//				continue;
		//			}
		//			for (TestPayment tp : testPaymentsMap.values()) {
		//				if (amount.compareTo(BigDecimal.ZERO) == -1) {
		//					break;
		//				}
		//				BigDecimal tpAmount = tp.getAmount();
		//				if (tpAmount.compareTo(BigDecimal.ZERO) == 1) {
		//					if (tpAmount.compareTo(amount) >= 0) {
		//						tp.setAmount(tpAmount.subtract(amount));
		//					} else {
		//						tp.setAmount(BigDecimal.ZERO);
		//					}
		//					amount = amount.subtract(tpAmount);
		//				}
		//			}
		//		}

		//return testPaymentsMap.values().stream().collect(Collectors.toList());
	}

	/**
	 * Cancel a payment.
	 * 
	 * @param visitRid
	 * @param toCancelTestActualsRid : list of LabTestActual to cancel.
	 */
	public void cancelPayment(Long visitRid, String cancelReason, List<Long> toCancelTestActualsRid) {
		if (CollectionUtil.isCollectionEmpty(toCancelTestActualsRid)) {
			return;
		}
		List<OperationStatus> excludedStatuses = Arrays.asList(OperationStatus.ABORTED, OperationStatus.CANCELLED);
		LkpTransactionType cancelType = lkpService.findOneAnyLkp(
				Arrays.asList(new SearchCriterion("code", TransactionType.CANCEL.getValue(), FilterOperator.eq)), LkpTransactionType.class);
		EmrVisit visit = visitService.getCancelData(visitRid);
		List<LabTestActual> testActuals = visit	.getLabSamples().stream().flatMap(s -> s.getLabTestActualSet().stream())
												.collect(Collectors.toList());
		List<LabTestActual> otherActualTests = testActuals	.stream()
															.filter(lta -> !toCancelTestActualsRid.contains(lta.getRid())
																	&& !excludedStatuses.contains(
																			OperationStatus.valueOf(lta.getLkpOperationStatus().getCode())))
															.collect(Collectors.toList());
		List<BillChargeSlip> visitChargeSlips = testActuals	.stream().flatMap(lta -> lta.getBillChargeSlipList().stream())
															.collect(Collectors.toList());
		LabBranch labBranch = branchService.findOne(SearchCriterion.generateRidFilter(visit.getBranchId(), FilterOperator.eq),
				LabBranch.class);

		BigDecimal totalPatientShare = BigDecimal.ZERO;
		BigDecimal totalInsuranceShare = BigDecimal.ZERO;
		List<BillPatientTransaction> cancelledTransactions = new ArrayList<>();
		//to move payments and refunds to another ChargeSlip, since we assign these transactions to a random charge slip
		List<BillPatientTransaction> toReserveTransactions = new ArrayList<>();
		Map<InsProvider, BigDecimal> externalBranchDebit = new HashMap<>();
		for (BillChargeSlip bcs : visitChargeSlips) {
			LabTestActual lta = bcs.getLabTestActual();
			TestDefinition td = lta.getTestDefinition();
			if (!toCancelTestActualsRid.contains(lta.getRid()) || bcs.getIsCancelled()) {
				continue;
			}
			//in case canceling a test actual which holds the charge slips then move them to another test actual for same test def
			LabTestActual sameTestLTA = null;
			if ((td.getIsAllowRepetitionDifferentSample() && !td.getIsDifferentSampleChargeable())
					&& lta.getSourceActualTest() == null && !CollectionUtil.isCollectionEmpty(otherActualTests)) {
				sameTestLTA = otherActualTests	.stream().filter(l -> l.getTestDefinition().getRid().equals(td.getRid())
						&& l.getSourceActualTest() == null).findFirst()
												.orElse(null);
				if (sameTestLTA != null) {
					bcs.setLabTestActual(sameTestLTA);
					continue;
				}
			}
			TestDestinationType tdt = TestDestinationType.valueOf(lta.getTestDestination().getType().getCode());
			//bcs.getReferralAmount() != null because in edit order it may be null because we changed destination from default to some destination 
			//and the charge slips thinks that it got a referral amount
			if (!TestDestinationType.WORKBENCH.equals(tdt) && bcs.getReferralAmount() != null) {
				InsProvider insurance = lta.getTestDestination().getDestinationBranch();
				if (externalBranchDebit.containsKey(insurance)) {
					externalBranchDebit.put(insurance, externalBranchDebit.get(insurance).add(bcs.getReferralAmount()));
				} else {
					externalBranchDebit.put(insurance, bcs.getReferralAmount());
				}
			}
			String code = serialSerivce.sequenceGeneration(SerialType.CANCEL_NO);
			BillPatientTransaction cancelledTransaction = new BillPatientTransaction();
			cancelledTransaction.setLkpTransactionType(cancelType);
			cancelledTransaction.setBillChargeSlip(bcs);
			cancelledTransaction.setCode(code);
			cancelledTransaction.setDescription(cancelReason);
			cancelledTransactions.add(cancelledTransaction);
			BigDecimal insAmount = getInsuranceCoverageAmount(bcs);
			if (insAmount != null) {//not null means it has an insurance
				totalInsuranceShare = totalInsuranceShare.add(insAmount);
			}
			totalPatientShare = totalPatientShare.add(bcs.getAmount());
			bcs.setIsCancelled(Boolean.TRUE);
			if (!CollectionUtil.isCollectionEmpty(bcs.getBillPatientTransactionList())) {
				for (BillPatientTransaction bpt : bcs.getBillPatientTransactionList()) {
					TransactionType tt = TransactionType.valueOf(bpt.getLkpTransactionType().getCode());
					//only reserve payment and refund
					if (tt == TransactionType.PAYMENT || tt == TransactionType.REFUND) {
						toReserveTransactions.add(bpt);
					}
				}
			}
		}

		//Recalculate the total amount of visit
		BigDecimal visitTotal = visitChargeSlips.stream().filter(bcs -> !bcs.getIsCancelled()).map(BillChargeSlip::getAmount).reduce(
				BigDecimal.ZERO, BigDecimal::add);
		visit.setTotalAmount(visitTotal);

		if (visit.getProviderPlan() != null) {
			updateInsuranceBalance(visit, visit.getProviderPlan().getInsProvider(), totalInsuranceShare, null,
					totalInsuranceShare.add(totalPatientShare));
		}
		updatePatientBalance(visit, visit.getEmrPatientInfo(), totalPatientShare, null);
		balanceTransactionService.addBalanceTransaction(BalanceTransactionType.LAB_SALES, labBranch, visit, null,
				totalInsuranceShare.add(totalPatientShare));
		//if there any non workbench destinations then we should credit the branch and debit the destination
		billChargeSlipService.chargeBalanceDestinations(visit, labBranch, externalBranchDebit, Boolean.TRUE);
		visit = visitService.updateVisit(visit, null);
		getRepository().save(cancelledTransactions);
		visitChargeSlips = billChargeSlipService.updateBillChargeSlip(visitChargeSlips);
		if (!CollectionUtil.isCollectionEmpty(toReserveTransactions)) {
			//to get a non cancelled charge slip, initially it is any object in case we cancelled everything
			BillChargeSlip toReserveBCS = visitChargeSlips.get(0);
			for (BillChargeSlip bcs : visitChargeSlips) {
				if (bcs.getIsCancelled()) {
					continue;
				}
				toReserveBCS = bcs;
				break;
			}
			for (BillPatientTransaction bpt : toReserveTransactions) {
				bpt.setBillChargeSlip(toReserveBCS);
			}
			getRepository().save(toReserveTransactions);
		}
	}

	/**
	 * Get the amount that the insurance covered from insurance's percentage
	 * 
	 * @param bcs
	 * @return BigDecimal
	 */
	public BigDecimal getInsuranceCoverageAmount(BillChargeSlip bcs) {
		if (bcs.getPercentage() == null) {
			return null;
		}
		BigDecimal percentage = bcs.getPercentage();
		percentage = divideNumbers(percentage, NumberUtil.MAX_PERCENTAGE);
		return bcs.getOriginalPrice().multiply(percentage);
	}

	/**
	 * Undo all visit balance changes.
	 * 
	 * @param visitRid
	 * @return previous payments
	 */
	public Map<String, Object> undoVisitBalanceChanges(Long visitRid) {
		List<BillChargeSlip> chargeSlips = billChargeSlipService.findByVisit(visitRid);
		if (CollectionUtil.isCollectionEmpty(chargeSlips)) {
			return null;
		}
		EmrVisit visit = visitService.findOne(Arrays.asList(new SearchCriterion("rid", visitRid, FilterOperator.eq)),
				EmrVisit.class, "emrPatientInfo", "visitType");
		EmrPatientInfo patient = visit.getEmrPatientInfo();
		LabBranch labBranch = branchService.findById(visit.getBranchId());
		Map<Long, InsProvider> providersMap = new HashMap<>();
		List<SearchCriterion> filters = Arrays.asList(new SearchCriterion("isActive", Boolean.TRUE, FilterOperator.eq),
				new SearchCriterion("visit.rid", visit.getRid(), FilterOperator.eq));
		//Reverse balance changes
		List<BillBalanceTransaction> balanceTransactions = balanceTransactionService.find(filters, BillBalanceTransaction.class, "visit",
				"patientInfo", "branch", "provider", "balanceTransactionType");
		for (BillBalanceTransaction bbt : balanceTransactions) {
			bbt.setIsActive(Boolean.FALSE);
			BalanceTransactionType balanceTransactionType = BalanceTransactionType.valueOf(bbt.getBalanceTransactionType().getCode());
			BigDecimal absAmount = bbt.getCredit() != null ? bbt.getCredit().multiply(new BigDecimal("-1")) : bbt.getDebit();
			switch (balanceTransactionType) {
				case PATIENT:
					patient.setBalance(patient.getBalance().add(absAmount));
					break;
				case LAB_CASH_DRAWER:
				case LAB_SALES:
					labBranch.setBalance(labBranch.getBalance().add(absAmount));
					break;
				case INSURANCE:
					Long providerRid = bbt.getProvider().getRid();
					if (providersMap.containsKey(providerRid)) {
						InsProvider provider = providersMap.get(providerRid);
						provider.setBalance(provider.getBalance().add(absAmount));
					} else {
						bbt.getProvider().setBalance(bbt.getProvider().getBalance().add(absAmount));
						providersMap.put(providerRid, bbt.getProvider());
					}
					break;
			}
		}
		balanceTransactionService.updateBalanceTransaction(balanceTransactions);
		patientInfoService.updatePatient(patient);
		branchService.updateBranch(labBranch);
		insProviderService.update(providersMap.values());
		List<TestPayment> previousPayments = new ArrayList<>();
		BigDecimal previousPaidAmount = BigDecimal.ZERO;
		//only re add previous payments if visit is walk-in
		if (VisitType.WALK_IN == VisitType.valueOf(visit.getVisitType().getCode())) {
			Set<BillPatientTransaction> transactions = getRepository().findByVisit(visitRid);
			for (BillPatientTransaction bpt : transactions) {
				TransactionType tt = TransactionType.valueOf(bpt.getLkpTransactionType().getCode());
				if (tt == TransactionType.CANCEL || tt == TransactionType.RECALCULATE) {
					continue;
				}
				TestPayment tp = new TestPayment();
				BillPatientTransaction copyBPT = new BillPatientTransaction();
				BeanUtils.copyProperties(bpt, copyBPT, "rid", "billChargeSlip");
				tp.setPreviousPayment(copyBPT);
				previousPayments.add(tp);
			}
			previousPaidAmount = getVisitActualPaidAmount(visitRid);
		}

		billChargeSlipService.deleteChargeSlip(chargeSlips);
		Map<String, Object> result = new HashMap<>();
		result.put("previousPayments", previousPayments);
		result.put("previousPaidAmount", previousPaidAmount);
		result.put("prevGeneralDisPercentage", chargeSlips.get(0).getGeneralDiscountPercentage());
		result.put("prevGeneralDisAmount", chargeSlips.get(0).getGeneralDiscountAmount());
		return result;
	}

	/**
	 * Refund a payment.
	 * 
	 * @param testPricingWrapper
	 */
	@PreAuthorize("hasAuthority('" + EhopeRights.REFUND_PAYMENT + "')")
	public void refundPayment(Long visitRid, List<TestPayment> testPayments) {
		//USER CAN REFUND WRONG TYPES EVEN IF NOT PAID USING THESE AMOUNTS 
		//PAID CASH : 20$, USER CAN REFUND 20$ VOUCHER,ETC
		EmrVisit visit = visitService.findOne(
				Arrays.asList(new SearchCriterion("rid", visitRid, FilterOperator.eq)), EmrVisit.class, "emrPatientInfo", "visitType");
		BigDecimal total = BigDecimal.ZERO;
		for (TestPayment tp : testPayments) {
			total = total.add(tp.getAmount());
		}
		if (visit.getPaidAmount().compareTo(BigDecimal.ZERO) == 0 || total.compareTo(visit.getPaidAmount()) == 1) {
			throw new BusinessException("Can't refund a visit if it is not paid, wrong inputs", "invalidParameters", ErrorSeverity.ERROR);
		}

		EmrPatientInfo patient = visit.getEmrPatientInfo();
		LabBranch labBranch = branchService.findById(visit.getBranchId());
		LkpTransactionType refundType = lkpService.findOneAnyLkp(
				Arrays.asList(new SearchCriterion("code", TransactionType.REFUND.getValue(), FilterOperator.eq)), LkpTransactionType.class);
		List<BillChargeSlip> chargeSlips = billChargeSlipService.findByVisit(visit.getRid());
		//attach it to a non cancelled charge slip otherwise get any
		BillChargeSlip randomChargeSlip = chargeSlips.stream().filter(bcs -> !bcs.getIsCancelled()).findFirst().orElse(chargeSlips.get(0));
		List<BillPatientTransaction> refundTransactions = new ArrayList<>();
		for (TestPayment tp : testPayments) {
			String code = serialSerivce.sequenceGeneration(SerialType.REFUND_NO);
			BillPatientTransaction refundTransaction = new BillPatientTransaction();
			refundTransaction.setLkpTransactionType(refundType);
			refundTransaction.setBillChargeSlip(randomChargeSlip);
			refundTransaction.setCode(code);
			refundTransaction.setAmount(tp.getAmount());
			refundTransaction.setLkpPaymentMethod(tp.getLkpPaymentMethod());
			refundTransactions.add(refundTransaction);
		}

		getRepository().save(refundTransactions);
		visit.setPaidAmount(visit.getPaidAmount().subtract(total));
		visitService.updateVisit(visit, null);
		updatePatientBalance(visit, patient, null, total);
		balanceTransactionService.addBalanceTransaction(BalanceTransactionType.LAB_CASH_DRAWER, labBranch, visit, total, null);
	}

	/**
	 * Create the the payments in a new patient transactions that are related to this random charge slip.
	 * 
	 * @param testPricingWrapper
	 * @param chargeSlip: random charge slip; we don't care about what charge slip got paid, we only care about how much paid.
	 * @param totalPaidAmount: the total paid amount ,not using what actually got sent in testPricingWrapper because we are doing some custom behavior in case of edit order
	 */
	public void createPaymentPatientTransactions(TestPricingWrapper testPricingWrapper, BillChargeSlip chargeSlip,
			BigDecimal totalPaidAmount) {

		EmrVisit visit = visitService.findOne(
				Arrays.asList(new SearchCriterion("rid", testPricingWrapper.getPatientVisit().getRid(), FilterOperator.eq)),
				EmrVisit.class, "emrPatientInfo", "visitType");
		EmrPatientInfo patient = visit.getEmrPatientInfo();
		LkpTransactionType paymentTransactionType = lkpService.findOneAnyLkp(
				Arrays.asList(new SearchCriterion("code", TransactionType.PAYMENT.getValue(), FilterOperator.eq)),
				LkpTransactionType.class);
		LabBranch labBranch = branchService.findById(visit.getBranchId());
		List<TestPayment> testPaymentList = testPricingWrapper.getTestPaymentList();
		List<BillPatientTransaction> patientTransactions = new ArrayList<>();
		for (TestPayment tp : testPaymentList) {
			BillPatientTransaction patientTransaction = null;
			//if it is an old payment keep it, otherwise create a new one
			if (tp.getPreviousPayment() != null) {
				patientTransaction = tp.getPreviousPayment();
			} else {
				patientTransaction = new BillPatientTransaction();
				String code = serialSerivce.sequenceGeneration(SerialType.PAYMENT_NO);
				patientTransaction.setLkpTransactionType(paymentTransactionType);
				patientTransaction.setCode(code);
				patientTransaction.setLkpPaymentMethod(tp.getLkpPaymentMethod());
				patientTransaction.setChangeRate(tp.getChangeRate());
				patientTransaction.setLkpPaymentCurrency(tp.getLkpPaymentCurrency());
				patientTransaction.setAmount(tp.getAmount());
			}
			patientTransaction.setBillChargeSlip(chargeSlip);
			patientTransactions.add(patientTransaction);
		}
		updatePatientBalance(visit, patient, totalPaidAmount, null);
		balanceTransactionService.addBalanceTransaction(BalanceTransactionType.LAB_CASH_DRAWER, labBranch, visit, null, totalPaidAmount);
		getRepository().save(patientTransactions);
	}

	/**
	 * Partial Payment.
	 * Currently we are only getting any random charge slip and inserting a patient transaction related to it.
	 * 
	 * @param testPricingWrapper
	 */
	@PreAuthorize("hasAuthority('" + EhopeRights.ADD_PAYMENT + "')")
	public void partialPayment(TestPricingWrapper testPricingWrapper) {
		EmrVisit visit = visitService.findOne(
				SearchCriterion.generateRidFilter(testPricingWrapper.getPatientVisit().getRid(), FilterOperator.eq),
				EmrVisit.class, "lkpOperationStatus", "visitType");
		BillChargeSlip randomChargeSlip = billChargeSlipService.findByVisit(visit.getRid()).get(0);
		BigDecimal totalPaidAmount = BigDecimal.ZERO;
		for (TestPayment testPayment : testPricingWrapper.getTestPaymentList()) {
			totalPaidAmount = totalPaidAmount.add(testPayment.getAmount());
		}
		createPaymentPatientTransactions(testPricingWrapper, randomChargeSlip, totalPaidAmount);
		visit.setPaidAmount(visit.getPaidAmount().add(totalPaidAmount));
		visit = visitService.updateVisit(visit, null);
		if (visitService.isVisitCovered(visit)
				&& OperationStatus.valueOf(visit.getLkpOperationStatus().getCode()) == OperationStatus.FINALIZED) {
			visit = visitService.propagateVisitStatusNoAuth(visit.getRid(), OperationStatus.CLOSED, null);
		}

	}

	/**
	 * Payment in the create order for patient wizard wither it covered the visit or not.
	 * Get the raw input with the authorities again and re-calculate it to make sure that the request body wasn't modified.
	 * 
	 * @param testPricingWrapper
	 * 
	 */
	@SuppressWarnings("unchecked")
	@PreAuthorize("hasAuthority('" + EhopeRights.ADD_PAYMENT + "')")
	public void payment(TestPricingWrapper testPricingWrapper) {
		Map<String, Object> previous = undoVisitBalanceChanges(testPricingWrapper.getPatientVisit().getRid());
		BigDecimal previousPaidAmount = null;
		if (!CollectionUtil.isMapEmpty(previous)
				&& !CollectionUtil.isCollectionEmpty((List<TestPayment>) previous.get("previousPayments"))) {
			testPricingWrapper.getTestPaymentList().addAll((List<TestPayment>) previous.get("previousPayments"));
			previousPaidAmount = (BigDecimal) previous.get("previousPaidAmount");
		}
		Map<String, Object> map = getTestsPricing(testPricingWrapper.getPatientVisit().getRid(), testPricingWrapper);
		List<PaymentInformation> paymentInformations = (List<PaymentInformation>) map.get("result");
		EmrVisit visit = (EmrVisit) map.get("visit");
		visit.setApprovalNumber(testPricingWrapper.getPatientVisit().getApprovalNumber());
		BigDecimal totalPaidAmount = previousPaidAmount == null ? BigDecimal.ZERO : previousPaidAmount;
		for (TestPayment testPayment : testPricingWrapper.getTestPaymentList()) {
			if (testPayment.getPreviousPayment() != null) {
				continue;
			}
			totalPaidAmount = totalPaidAmount.add(testPayment.getAmount());
		}
		List<BillChargeSlip> chargeSlips = billChargeSlipService.createVisitChargeSlips(visit, paymentInformations,
				testPricingWrapper);
		createPaymentPatientTransactions(testPricingWrapper, chargeSlips.get(0), totalPaidAmount);
		visit.setPaidAmount(totalPaidAmount);
		visit.setTotalAmount((BigDecimal) map.get("total"));
		visit = visitService.updateVisit(visit, null);
		entityManager.flush();
		entityManager.clear();
		sampleService.separatePatientTests(testPricingWrapper.getPatientVisit().getRid());
	}

	/**
	 * Skipping payment, just updating status of visit,samples,tests
	 * 
	 * @param testPricingWrapper
	 */
	@SuppressWarnings("unchecked")
	@PreAuthorize("hasAuthority('" + EhopeRights.SKIP_PAYMENT + "')")
	public void skipPayment(TestPricingWrapper testPricingWrapper) {
		Map<String, Object> previous = undoVisitBalanceChanges(testPricingWrapper.getPatientVisit().getRid());
		if (!CollectionUtil.isMapEmpty(previous) && (((BigDecimal) previous.get("prevGeneralDisPercentage")) != null
				|| ((BigDecimal) previous.get("prevGeneralDisAmount")) != null)) {

			if ((BigDecimal) previous.get("prevGeneralDisAmount") != null) {
				testPricingWrapper.setGeneralDiscountAmount((BigDecimal) previous.get("prevGeneralDisAmount"));
			} else if ((BigDecimal) previous.get("prevGeneralDisPercentage") != null) {
				testPricingWrapper.setGeneralDiscountPercentage((BigDecimal) previous.get("prevGeneralDisPercentage"));
			}

		}
		Map<String, Object> map = getTestsPricing(testPricingWrapper.getPatientVisit().getRid(), testPricingWrapper);
		EmrVisit visit = (EmrVisit) map.get("visit");
		visit.setApprovalNumber(testPricingWrapper.getPatientVisit().getApprovalNumber());
		List<PaymentInformation> paymentInformations = (List<PaymentInformation>) map.get("result");
		List<BillChargeSlip> chargeSlips = billChargeSlipService.createVisitChargeSlips(visit, paymentInformations,
				testPricingWrapper);
		BigDecimal previousPaidAmount = BigDecimal.ZERO;
		//in-case skipping an edited order that was already paid 
		if (!CollectionUtil.isMapEmpty(previous)
				&& !CollectionUtil.isCollectionEmpty((List<TestPayment>) previous.get("previousPayments"))) {
			List<TestPayment> previousPayments = (List<TestPayment>) previous.get("previousPayments");
			testPricingWrapper.setTestPaymentList(new ArrayList<>(previousPayments));
			previousPaidAmount = (BigDecimal) previous.get("previousPaidAmount");
			createPaymentPatientTransactions(testPricingWrapper, chargeSlips.get(0), previousPaidAmount);
		}

		//Make it fully paid if visit type is referral
		if (visit.getVisitType().getCode().equals(VisitType.REFERRAL.getValue())) {
			visit.setTotalAmount((BigDecimal) map.get("total"));
			visit.setPaidAmount(visit.getTotalAmount());
		} else {
			visit.setTotalAmount((BigDecimal) map.get("total"));
			visit.setPaidAmount(previousPaidAmount);
		}
		visit = visitService.updateVisit(visit, null);
		entityManager.flush();
		entityManager.clear();
		sampleService.separatePatientTests(testPricingWrapper.getPatientVisit().getRid());
	}

	/**
	 * Throw an error if there are any test without any billing information
	 * 
	 * @param testDefinitionList: the requested tests
	 * @param billTestItemList: the fetched test item list
	 */
	public void areTestsWithoutBill(Collection<TestDefinition> testDefinitionList, List<BillTestItem> billTestItemList) {
		StringBuilder testsStandardCodes = new StringBuilder("");
		for (TestDefinition testDefinition : testDefinitionList) {
			boolean hasBilling = false;
			for (BillTestItem bti : billTestItemList) {
				if (bti.getTestDefinition().getRid().equals(testDefinition.getRid())) {
					hasBilling = true;
				}
			}
			if (!hasBilling) {
				testsStandardCodes.append(testDefinition.getStandardCode() + ",");
			}
		}
		if (!StringUtil.isEmpty(testsStandardCodes.toString())) {
			testsStandardCodes.deleteCharAt(testsStandardCodes.length() - 1);
			throw new BusinessException("Test(s) does not have any billing information:" + testsStandardCodes, "testNoBillingInfo",
					ErrorSeverity.ERROR, Arrays.asList(testsStandardCodes.toString()));
		}

	}

	/**
	 * The operation of calculating the pricing for the selected tests. Fetch the default price list and insurance price list then get all bill master items
	 * that intersect between the 2 bill price lists within the effective date(if a bill master item didn't have a pricing then user must input pricing, it will be in the output map).
	 * After that we wrap each test and its bill master items to calculate them.
	 * 
	 * @param testDefinitionList : the tests where we will charge patient for.
	 * @param userInsProviderPlan : user's insurance [nullable].
	 * @return CalculationData object that contains all necessary data to calculate.
	 */
	public CalculationData getCalculationData(List<LabTestActual> testActuals, List<TestGroup> selectedGroups,
			InsProviderPlan userInsProviderPlan) {
		testActualService.verifyTestsMaxAmount(testActuals);
		List<TestDefinition> selectedTests = testActuals.stream().map(LabTestActual::getTestDefinition).collect(Collectors.toList());
		//list to be used in the below query to fetch the records that only has a pricing that belong to one of the both priceLists default and ins if exists
		List<BillPriceList> billPriceLists = new ArrayList<>();
		BillPriceList defaultBillPriceList = billPriceListService.getDefaultNoAuth();
		billPriceLists.add(defaultBillPriceList);
		// get the insPlan selected by the user and fetch its cov details and scopes
		if (userInsProviderPlan != null) {
			userInsProviderPlan = insProviderPlanService.findOne(
					SearchCriterion.generateRidFilter(userInsProviderPlan.getRid(), FilterOperator.eq), InsProviderPlan.class,
					"insProvider", "billPriceList", "insCoverageDetailList.billClassification", "insCoverageDetailList.billMasterItem",
					"insCoverageDetailList.lkpCoverageDetailScope");
			if (!CollectionUtil.isCollectionEmpty(userInsProviderPlan.getInsCoverageDetailList())) {
				userInsProviderPlan.getInsCoverageDetailList().removeIf(icd -> !icd.getIsActive());
			}
			userInsProviderPlan.setBillPriceList(ReflectionUtil.unproxy(userInsProviderPlan.getBillPriceList()));
			billPriceLists.add(userInsProviderPlan.getBillPriceList());
		}

		Set<TestGroup> fetchedTestGroups = new HashSet<>();
		if (!CollectionUtil.isCollectionEmpty(selectedGroups)) {
			List<Long> groupsRid = selectedGroups.stream().map(TestGroup::getRid).collect(Collectors.toList());
			fetchedTestGroups = new HashSet<>(
					testGroupService.find(Arrays.asList(new SearchCriterion("rid", groupsRid, FilterOperator.in)), TestGroup.class,
							"groupDefinitions.testDefinition", "groupDetails.priceList"));
			for (TestGroup tg : fetchedTestGroups) {
				if (!tg.getIsProfile() && tg.getDiscountAmount() != null) {
					tg.setTotalPrice(testGroupService.getTestGroupPrice(tg.getRid(), defaultBillPriceList.getRid()));
				}
			}
		}

		List<BillTestItem> billTestItemList = billTestItemService.getByTestDefinitions(selectedTests);
		//this is Set in case that if there are a BillTestItems with the same bmt and test
		Set<BillMasterItem> billMasterItemList = billTestItemList.stream().map(bti -> bti.getBillMasterItem()).collect(Collectors.toSet());
		areTestsWithoutBill(selectedTests, billTestItemList);
		List<BillMasterItem> fetchedBmiList = billMasterItemService.getByMasterItemAndPriceList(new ArrayList<>(billMasterItemList),
				billPriceLists);
		List<BillMasterItem> bmiNoPriceList = new ArrayList<>();
		// If the bmi items we fetched does not equal to the bmi by the test items, then get them
		// it means that we are getting the BillMasterItems where they don't have either default or plan's price lists
		if (billMasterItemList.size() != fetchedBmiList.size()) {
			bmiNoPriceList = billMasterItemService.getByMasterItemAndNotPriceList(new ArrayList<>(billMasterItemList), billPriceLists);
			//remove in case we found the same bmi (in fetched) in another price lists
			//i.e. two pricings of same bmi in two different price lists, one is the intersect the other is not
			bmiNoPriceList.removeIf(bmi -> fetchedBmiList.contains(bmi));
		}

		List<PaymentInformation> paymentInformations = generatePaymentInformation(testActuals);
		// we are looping through all bill master items and un-proxying them then insert them into their payment wrappers, 
		// but we only calculate bill master items that met the fetching requirements.
		for (BillMasterItem bmi : billMasterItemList) {

			BillClassification bc = ReflectionUtil.unproxy(bmi.getBillClassification());
			if (bc != null) {
				BillClassification bcParent = ReflectionUtil.unproxy(bc.getParentClassification());
				bc.setParentClassification(bcParent);
			}

			bmi.setBillClassification(bc);

			for (PaymentInformation pi : paymentInformations) {
				for (BillTestItem bti : billTestItemList) {
					if (bti.getTestDefinition().getRid().equals(pi.getTestDefinition().getRid())
							&& bti.getBillMasterItem().getRid().equals(bmi.getRid())) {
						BillChargeSlip bcs = new BillChargeSlip();
						bcs.setBillMasterItem(bmi);
						pi.getChargeSlips().add(bcs);
					}
				}
			}
		}

		return new CalculationData(fetchedBmiList, bmiNoPriceList, defaultBillPriceList, userInsProviderPlan, paymentInformations,
				fetchedTestGroups);

	}

	/**
	 * 
	 * @param testDefsRid
	 * @param insProviderPlanRid
	 */
	public void areTestsWithoutInsPricing(List<Long> testDefsRid, Long insProviderPlanRid) {
		List<BillTestItem> billTestItemList = billTestItemService.getByTestDefinitions(testDefinitionService.find(
				Arrays.asList(new SearchCriterion("rid", testDefsRid, FilterOperator.in)), TestDefinition.class));
		InsProviderPlan userInsProviderPlan = insProviderPlanService.findOne(
				SearchCriterion.generateRidFilter(insProviderPlanRid, FilterOperator.eq), InsProviderPlan.class, "billPriceList");
		String userLocale = userService.getUserLocale(SecurityUtil.getCurrentUser().getRid());
		String priceListName = StringUtil.isEmpty(userInsProviderPlan.getBillPriceList().getName().get(userLocale))
				? userInsProviderPlan.getBillPriceList().getName().entrySet().iterator().next().getValue()
				: userInsProviderPlan.getBillPriceList().getName().get(userLocale);
		Set<TestDefinition> testsWithoutInsPricing = new HashSet<>();
		OUTER: for (BillTestItem bti : billTestItemList) {
			for (BillPricing bp : bti.getBillMasterItem().getBillPricings()) {
				if (bp.getBillPriceList().getRid().equals(userInsProviderPlan.getBillPriceList().getRid())) {
					continue OUTER;
				}
			}
			testsWithoutInsPricing.add(bti.getTestDefinition());
		}
		if (!CollectionUtil.isCollectionEmpty(testsWithoutInsPricing)) {
			String testsCodes = testsWithoutInsPricing.stream().map(TestDefinition::getStandardCode).collect(Collectors.joining(","));
			throw new BusinessException("Test(s) " + testsCodes + " does not have any price on price list:" + priceListName,
					"testNoPricing", ErrorSeverity.WARNING,
					Arrays.asList(testsCodes, priceListName));
		}
	}

	/**
	 * This method will return data about the payment for the fetched bill master items,
	 * data for non fetched bill master items(they don't belong to default or insurance's price list),
	 * defaultBillPriceList to be used from the front end to set it inside the bill pricing for the non fetched bill master items,
	 * total fees that patient must pay.
	 * Uses the private method calculate, to actually calculate the tests.
	 * 
	 * @param patientInsuranceInfo : patient's insurance [nullable].
	 * @param testDefinitionList : the tests where we will charge patient for.
	 * @param userInsProviderPlan : user's insurance [nullable].
	 * @param paymentAuthorities : mapping between bill master items and coverage details, to know if it was authorized or not
	 * @return map
	 */
	public Map<String, Object> getTestsPricing(Long visitRid, TestPricingWrapper testPricingWrapper) {
		List<String> excludedStatuses = Arrays.asList(OperationStatus.ABORTED.getValue(), OperationStatus.CANCELLED.getValue());
		EmrVisit visit = visitService.findOne(
				SearchCriterion.generateRidFilter(visitRid, FilterOperator.eq), EmrVisit.class,
				"providerPlan", "patientInsuranceInfo", "visitGroups.testGroup",
				"labSamples.labTestActualSet.testDefinition", "labSamples.labTestActualSet.lkpOperationStatus",
				"labSamples.labTestActualSet.sourceActualTest");
		List<LabTestActual> testActuals = visit	.getLabSamples().stream().flatMap(s -> s.getLabTestActualSet().stream())
												.filter(lta -> !excludedStatuses.contains(
														lta.getLkpOperationStatus().getCode()))
												.distinct()
												.collect(Collectors.toList());
		testPricingWrapper.setTestDefinitionList(new ArrayList<>(
				testActuals.stream().map(LabTestActual::getTestDefinition).collect(Collectors.toList())));
		testPricingWrapper.setInsProviderPlan(visit.getProviderPlan());
		testPricingWrapper.setPatientInsuranceInfo(visit.getPatientInsuranceInfo());
		EmrPatientInsuranceInfo patientInsuranceInfo = visit.getPatientInsuranceInfo();
		InsProviderPlan userInsProviderPlan = visit.getProviderPlan();
		List<BillChargeSlip> paymentAuthorities = new ArrayList<>();
		List<TestGroup> selectedGroups = new ArrayList<>();
		if (!CollectionUtil.isCollectionEmpty(visit.getVisitGroups())) {
			selectedGroups = visit.getVisitGroups().stream().map(EmrVisitGroup::getTestGroup).distinct().collect(Collectors.toList());
		}
		for (PaymentInformation pi : testPricingWrapper.getPaymentInformations()) {
			for (BillChargeSlip bcs : pi.getChargeSlips()) {
				paymentAuthorities.add(bcs);
			}
		}

		CalculationData calculationData = getCalculationData(testActuals, selectedGroups, userInsProviderPlan);

		List<PaymentInformation> paymentInformations = calculationData.getPaymentInformations();
		calculate(calculationData, paymentInformations, patientInsuranceInfo, paymentAuthorities);

		BigDecimal total = generalDiscount(paymentInformations, testPricingWrapper);
		total = total.setScale(2, RoundingMode.HALF_UP);
		return calculationResult(visit, paymentInformations, calculationData.getBmiNoPriceList(), total);
	}

	/**
	 * The general discount logic.
	 * 
	 * @param paymentWrapperList
	 * @param testPricingWrapper
	 * @return the total with or without deduction
	 */
	private BigDecimal generalDiscount(List<PaymentInformation> paymentInformations, TestPricingWrapper testPricingWrapper) {
		BigDecimal total = BigDecimal.ZERO;
		for (PaymentInformation pi : paymentInformations) {
			total = total.add(pi.getChargeSlipsTotal());
		}

		// general discount logic
		BigDecimal generalPercentage = null;
		// use the discount of %
		if (testPricingWrapper.getGeneralDiscountPercentage() != null) {
			generalPercentage = divideNumbers(NumberUtil.validatePercentage(testPricingWrapper.getGeneralDiscountPercentage()),
					NumberUtil.MAX_PERCENTAGE);
		} else if (testPricingWrapper.getGeneralDiscountAmount() != null) {// use the discount of a specific amount by converting it to a %
			// i.e. 15 amount, 48 total : 15/48 -> 0.3125 -> 31.25
			// we are using special rounding here to get an accurate value, thats why we didn't use divideNumbers(...)
			generalPercentage = testPricingWrapper.getGeneralDiscountAmount().divide(total, 10, RoundingMode.HALF_UP);
			generalPercentage = NumberUtil.validatePercentage(generalPercentage);
		}

		// re calculate new total after deductions
		if (generalPercentage != null) {
			total = BigDecimal.ZERO;//reset
			for (PaymentInformation pi : paymentInformations) {
				pi.setChargeSlipsTotal(BigDecimal.ZERO);//reset
				for (BillChargeSlip bcs : pi.getChargeSlips()) {
					BigDecimal charge = bcs.getAmount();
					charge = charge.subtract(charge.multiply(generalPercentage));
					charge = amountRounding(charge);
					bcs.setAmount(charge);
					pi.setChargeSlipsTotal(pi.getChargeSlipsTotal().add(charge));
				}
			}

			for (PaymentInformation pi : paymentInformations) {
				total = total.add(pi.getChargeSlipsTotal());
			}
		}

		return total;
	}

	/**
	 * This is almost the same as the getTestsPricing. The differences are:
	 * 1- No payment wrapper for authorizations.
	 * 2- Total is not affected by general discount.
	 * 
	 * @return pricing of selected tests
	 */
	public Map<String, Object> getTestsPricingNoVisit(TestPricingWrapper testPricingWrapper) {
		List<TestDefinition> testDefinitionList = testPricingWrapper.getTestDefinitionList();
		InsProviderPlan userInsProviderPlan = testPricingWrapper.getInsProviderPlan();
		List<LabTestActual> dummyActuals = new ArrayList<>();//using dummy actuals so getCalculationData(...) can work correctly
		long dummyRid = -1;
		for (TestDefinition td : testDefinitionList) {
			LabTestActual lta = new LabTestActual();
			lta.setRid(dummyRid--);
			lta.setTestDefinition(td);
			dummyActuals.add(lta);
		}
		CalculationData calculationData = getCalculationData(dummyActuals, testPricingWrapper.getTestGroupList(),
				userInsProviderPlan);

		List<PaymentInformation> paymentInformations = calculationData.getPaymentInformations();
		calculate(calculationData, paymentInformations, null, new ArrayList<>());

		//total that is returned is not affected by general discount
		BigDecimal total = BigDecimal.ZERO;
		for (PaymentInformation pi : paymentInformations) {
			total = total.add(pi.getChargeSlipsTotal());
		}
		total = total.setScale(2, RoundingMode.HALF_UP);
		return calculationResult(null, paymentInformations, calculationData.getBmiNoPriceList(), total);
	}

	private void calculateFromGroups(BillChargeSlip bcs, BillMasterItem bmi, List<TestGroup> testGroups,
			InsProviderPlan userInsProviderPlan, BillPricing defaultBillPricing, BillPricing insuranceBillPricing,
			EmrPatientInsuranceInfo patientInsInfo) {

		BillPricing chosenBillPricing = null;
		BigDecimal chosenPrice = null;
		BigDecimal chosenDiscount = null;
		TestGroup chosenGroup = null;
		BigDecimal insPercentage = getPatientInsuranceDiscount(patientInsInfo, userInsProviderPlan);//nullable
		Boolean isExcluded = Boolean.FALSE;//default
		if (userInsProviderPlan != null) {//nullable
			isExcluded = userInsProviderPlan.getInsCoverageDetailList().stream().anyMatch(
					icd -> icd.getBillMasterItem() != null && icd.getBillMasterItem().getRid().equals(bmi.getRid()) && !icd.getIsCovered());
		}
		for (TestGroup group : testGroups) {
			BillPricing pricing = null;
			BigDecimal price = null;
			BigDecimal discount = null;
			BigDecimal groupDiscountAmount = null;
			BigDecimal groupDiscountPercentage = null;
			BigDecimal groupTotalPrice = null;
			if (!group.getIsProfile()) {
				pricing = defaultBillPricing;
				price = defaultBillPricing.getPrice();
				if (group.getDiscountAmount() != null) {
					groupDiscountAmount = group.getDiscountAmount();
					groupTotalPrice = group.getTotalPrice();
				} else {
					groupDiscountPercentage = group.getDiscountPercentage();
				}
			} else {
				//this is not affected by discount amounts or percentages
				if (!isExcluded && insuranceBillPricing != null) {
					pricing = insuranceBillPricing;
					price = insuranceBillPricing.getPrice();
					groupDiscountPercentage = insPercentage;
					//pick the detail that has the same price list as the user
					//					for (TestGroupDetail tgd : group.getGroupDetails()) {
					//						//if we have a pricing on this price list then pick it
					//						if (tgd.getPriceList().getRid().equals(userInsProviderPlan.getBillPriceList().getRid())
					//								&& insuranceBillPricing != null) {
					//							pricing = insuranceBillPricing;
					//							price = insuranceBillPricing.getPrice();
					//							groupDiscountPercentage = insPercentage;
					//							break;
					//						}
					//					}
				}
				//picked a profile but there are no insurance or there are no detail for this insurance
				if (pricing == null) {
					pricing = defaultBillPricing;
					price = defaultBillPricing.getPrice();
					discount = null;
				}
			}

			if (groupDiscountAmount != null) {
				//in-case the prices change and now the discount amount is bigger than the total amount
				if (groupDiscountAmount.compareTo(groupTotalPrice) == 1) {
					groupDiscountAmount = groupTotalPrice;
				}
				discount = groupDiscountAmount.divide(groupTotalPrice, 10, RoundingMode.HALF_UP);
				discount = discount.multiply(NumberUtil.MAX_PERCENTAGE);
				BigDecimal discountAmount = divideNumbers((NumberUtil.MAX_PERCENTAGE.subtract(discount)), NumberUtil.MAX_PERCENTAGE);
				discount = amountRounding(discount);
				price = price.multiply(discountAmount);
			} else if (groupDiscountPercentage != null) {
				discount = groupDiscountPercentage;
				BigDecimal discountAmount = divideNumbers((NumberUtil.MAX_PERCENTAGE.subtract(groupDiscountPercentage)),
						NumberUtil.MAX_PERCENTAGE);
				price = price.multiply(discountAmount);
			}

			// no picked pricing yet
			if (chosenPrice == null) {
				chosenBillPricing = pricing;
				chosenPrice = price;
				chosenDiscount = discount;
				chosenGroup = group;
			} else if (price.compareTo(chosenPrice) == 1) {
				chosenBillPricing = pricing;
				chosenPrice = price;
				chosenDiscount = discount;
				chosenGroup = group;
			}

		}
		//fallback to default
		if (chosenBillPricing == null) {
			chosenBillPricing = defaultBillPricing;
			chosenPrice = defaultBillPricing.getPrice();
			chosenDiscount = null;
			chosenGroup = null;
		}
		bcs.setIsExceedMaxAmount(Boolean.FALSE);
		bcs.setAmount(chosenPrice);
		bcs.setIsAuthorized(Boolean.TRUE);
		bcs.setBillPricing(chosenBillPricing);
		if (chosenGroup != null) {
			//If the selected group was a profile and the pricing was the insured pricing then this paymentInformation is covered by insurance
			if (chosenGroup.getIsProfile() && insuranceBillPricing != null
					&& bcs.getBillPricing().equals(insuranceBillPricing)) {
				bcs.setPercentage(chosenDiscount);
				bcs.setInsCoverageResult(insuranceBillPricing.getPrice().subtract(chosenPrice));
				bcs.setInsDeductionPercentage(userInsProviderPlan.getInsProvider().getDiscount());
				bcs.setInsDeductionResult(
						getInsDeductionResult(userInsProviderPlan.getInsProvider(), bcs.getInsCoverageResult(),
								insuranceBillPricing.getPrice()));
				bcs.setAmountAfterCoverage(chosenPrice);
			} else {

				bcs.setGroupDiscountPercentage(chosenGroup.getDiscountPercentage());
				bcs.setGroupCoverageAmount(chosenGroup.getDiscountAmount());
				bcs.setGroupTotal(chosenGroup.getTotalPrice());
				bcs.setGroupCoverageResult(chosenBillPricing.getPrice().subtract(chosenPrice));
				bcs.setAmountAfterCoverage(chosenPrice);
			}
		}
	}

	private void calculateFromInsurance(BillChargeSlip bcs, InsProviderPlan userInsProviderPlan,
			BillPricing insuranceBillPricing, EmrPatientInsuranceInfo patientInsInfo, Map<InsCoverageDetail, BigDecimal> insCovMaxAmtMap) {

		BillMasterItem masterItem = bcs.getBillMasterItem();
		BigDecimal insPrice = insuranceBillPricing.getPrice();
		BigDecimal insPercentage = null;
		BigDecimal insDeductionPercentage = userInsProviderPlan.getInsProvider().getDiscount();
		Stack<InsCoverageDetail> insCovByPlanStack = getCovDetailStackByMasterItem(masterItem, userInsProviderPlan);
		Boolean exceededMaxAmount = Boolean.FALSE;

		InsCoverageDetail insCoverageDetail = null;
		// going by levels, insurance -> parent -> class -> item
		//(4th level)
		insPercentage = getPatientInsuranceDiscount(patientInsInfo, userInsProviderPlan);
		// going by 3 levels, parent -> class -> item
		if (!CollectionUtil.isCollectionEmpty(insCovByPlanStack)) {
			while (!insCovByPlanStack.isEmpty()) {
				InsCoverageDetail icd = insCovByPlanStack.pop();
				BigDecimal prevAmount = insCovMaxAmtMap.get(icd);
				BigDecimal differenceMaxAmount = icd.getMaxAmount().subtract(prevAmount);
				// if the amount didn't reach zero then we can deduct from price using this insurance coverage that is connected to the pricing
				if (differenceMaxAmount.compareTo(BigDecimal.ZERO) <= 0) {// should be == but using <= just to make sure we don't use it if less than zero
					exceededMaxAmount = Boolean.TRUE;
					continue;
				}

				// the bill master item does have an insCoverageDetail and didn't exceed the max amount
				insPercentage = icd.getPercentage();

				BigDecimal amountPercentage = divideNumbers((NumberUtil.MAX_PERCENTAGE.subtract(insPercentage)), NumberUtil.MAX_PERCENTAGE);

				// the minimum that will deduct from the price between the allowed max and the (price * percentage) 
				differenceMaxAmount = differenceMaxAmount.min(insPrice.multiply(amountPercentage));
				insPrice = differenceMaxAmount;

				//update the amount , previous + the used difference amount
				insCovMaxAmtMap.put(icd, differenceMaxAmount.add(prevAmount));

				insCoverageDetail = icd;
				exceededMaxAmount = Boolean.FALSE;
				break;
			}
		}
		// using the percentage in (4th level) if no percentage found in the other levels
		if (insCoverageDetail == null && insPercentage != null) {
			BigDecimal amountPercentage = divideNumbers((NumberUtil.MAX_PERCENTAGE.subtract(insPercentage)), NumberUtil.MAX_PERCENTAGE);
			insPrice = insPrice.multiply(amountPercentage);
		}
		bcs.setInsCoverageDetail(insCoverageDetail);
		bcs.setIsExceedMaxAmount(exceededMaxAmount);
		bcs.setBillPricing(insuranceBillPricing);
		bcs.setAmount(insPrice);
		bcs.setPercentage(insPercentage);
		bcs.setInsCoverageResult(insuranceBillPricing.getPrice().subtract(insPrice));
		bcs.setInsDeductionPercentage(insDeductionPercentage);
		bcs.setInsDeductionResult(getInsDeductionResult(userInsProviderPlan.getInsProvider(),
				bcs.getInsCoverageResult(), insuranceBillPricing.getPrice()));
		bcs.setAmountAfterCoverage(insPrice);
		bcs.setIsAuthorized(Boolean.TRUE);
	}

	/**
	 * get the insurance's percentage or the patient's percentage
	 * 
	 * @param patientInsInfo
	 * @param userInsProviderPlan
	 * @return BigDecimal
	 */
	private BigDecimal getPatientInsuranceDiscount(EmrPatientInsuranceInfo patientInsInfo, InsProviderPlan userInsProviderPlan) {
		if (userInsProviderPlan == null) {
			return null;
		}
		if (patientInsInfo != null && !userInsProviderPlan.getIsFixed()) {
			return patientInsInfo.getCoveragePercentage();
		} else {
			return userInsProviderPlan.getCoveragePercentage();
		}
	}

	/**
	 * This method will be called after fetching the BillMasterItems with their pricing and price lists.
	 * After calculating the price it will inject the data inside paymentWrapperList in fetchedBillMasterItemList.
	 * If there are any bill master items with no default or insurance price list then the output will leave NULLS in the object in paymentWrapperList that is connected
	 * to the bill master item (inside its test), i.e. charge=null,percentage=null, etc
	 * 
	 * @param calculationData : the "basic" information that is required to calculate pricings
	 * @param paymentWrapperList : the output of the whole operation of pricings
	 * @param patientInsInfo : the custom patient percentage in case the insurance is not fixed, otherwise it will be the same as the insurance's percentage.
	 * @param paymentAuthorityList : mapping between bill master items and coverage details, to know if it was authorized or not
	 * 
	 * @return modified paymentWrapperList that has a single test definition and one or multiple payment information
	 */
	public void calculate(CalculationData calculationData,
			List<PaymentInformation> paymentInformations, EmrPatientInsuranceInfo patientInsInfo,
			List<BillChargeSlip> chargeSlipAuthorities) {
		List<BillMasterItem> fetchedBmiList = calculationData.getFetchedBmiList();//the fetched bill master items
		InsProviderPlan userInsProviderPlan = calculationData.getUserInsProviderPlan();//user's insurance ,nullable
		BillPriceList defaultBillPriceList = calculationData.getDefaultBillPriceList();//insurance's default pricing list
		Set<TestGroup> testGroups = calculationData.getTestGroupsSet();//if this parameter is not null then we dont have any insurance,so we deduct from group itself,nullable
		Map<InsCoverageDetail, BigDecimal> insCovMaxAmtMap = new HashMap<>();// map to store the current used max amount for each ins coverage detail
		for (BillMasterItem bmi : fetchedBmiList) {
			//get if this bmi is authorized or not, orElse(null) bcz we can call this method without paymentAuthorityList
			BillChargeSlip chargeSlipAuthority = chargeSlipAuthorities	.stream()
																		.filter(bcs -> bcs	.getBillMasterItem().getRid()
																							.equals(bmi.getRid()))
																		.findFirst()
																		.orElse(null);
			//to keep the default pricing,throw exception if does not exit which is correct since there must be a price on the default list
			BillPricing defaultBillPricing = bmi.getBillPricings().stream()
												.filter(bp -> bp.getBillPriceList().getRid().equals(defaultBillPriceList.getRid()))
												.findFirst().get();

			BillPricing insuranceBillPricing = null;//the pricing that has the same price list as the user insurance
			Boolean hasInsurancePricing = Boolean.FALSE;//to know if we can use insurance calculation or not
			if (userInsProviderPlan != null) {
				insuranceBillPricing = bmi	.getBillPricings().stream().filter(
						bp -> bp.getBillPriceList().getRid().equals(userInsProviderPlan.getBillPriceList().getRid())).findFirst()
											.orElse(null);
				hasInsurancePricing = insuranceBillPricing != null;
				for (InsCoverageDetail icd : userInsProviderPlan.getInsCoverageDetailList()) {
					insCovMaxAmtMap.put(icd, BigDecimal.ZERO);
				}
			}

			for (PaymentInformation pi : paymentInformations) {
				//get the groups with this test in pw
				List<TestGroup> relatedGroups = testGroups.stream().filter(tg ->
					{
						for (TestGroupDefinition tgd : tg.getGroupDefinitions()) {
							if (tgd.getTestDefinition().getRid().equals(pi.getTestDefinition().getRid())) {
								return true;
							}
						}
						return false;
					}).collect(Collectors.toList());
				for (BillChargeSlip bcs : pi.getChargeSlips()) {
					if (!bcs.getBillMasterItem().getRid().equals(bmi.getRid())) {
						continue;
					}

					//pi.setIsFetched(Boolean.TRUE);

					//the order we use to charge items:
					//if it is authorized then we will only use the default pricing
					//then if we have relatedGroups we calculate from groups
					//then if we have insurance and an insurance pricing then calculate from insurance
					//otherwise use default
					if (chargeSlipAuthority != null && chargeSlipAuthority.getIsAuthorized() == Boolean.FALSE) {
						calculateUnauthorized(bcs, chargeSlipAuthority.getComment(), defaultBillPricing);
					} else if (!CollectionUtil.isCollectionEmpty(relatedGroups)) {
						calculateFromGroups(bcs, bmi, relatedGroups, userInsProviderPlan, defaultBillPricing, insuranceBillPricing,
								patientInsInfo);
					} else if (hasInsurancePricing) {
						calculateFromInsurance(bcs, userInsProviderPlan, insuranceBillPricing, patientInsInfo, insCovMaxAmtMap);
					} else {
						bcs.setIsExceedMaxAmount(Boolean.FALSE);
						bcs.setBillPricing(defaultBillPricing);
						bcs.setAmount(defaultBillPricing.getPrice());
						bcs.setAmountAfterCoverage(defaultBillPricing.getPrice());
						bcs.setIsAuthorized(Boolean.TRUE);
					}
					bcs.setOriginalPrice(bcs.getBillPricing().getPrice());
					bcs.setAmount(amountRounding(bcs.getAmount()));
					bcs.setAmountAfterCoverage(amountRounding(bcs.getAmountAfterCoverage()));
					if (bcs.getInsCoverageResult() != null) {
						bcs.setInsCoverageResult(amountRounding(bcs.getInsCoverageResult()));
					}
					if (bcs.getInsDeductionResult() != null) {
						bcs.setInsDeductionResult(amountRounding(bcs.getInsDeductionResult()));
					}
					if (bcs.getGroupCoverageResult() != null) {
						bcs.setGroupCoverageResult(amountRounding(bcs.getGroupCoverageResult()));
					}
					//set comment wither it was approved or not
					if (chargeSlipAuthority != null) {
						bcs.setComment(chargeSlipAuthority.getComment());
					}
					//total amount without general discount deduction
					pi.setChargeSlipsTotal(pi.getChargeSlipsTotal().add(bcs.getAmount()));

				}

			}
		}

	}

	private void calculateUnauthorized(BillChargeSlip bcs, String authorityComment, BillPricing defaultPricing) {
		bcs.setIsExceedMaxAmount(Boolean.FALSE);
		bcs.setIsAuthorized(Boolean.FALSE);
		bcs.setComment(authorityComment);
		bcs.setBillPricing(defaultPricing);
		bcs.setAmount(defaultPricing.getPrice());
		bcs.setAmountAfterCoverage(defaultPricing.getPrice());
	}

	/**
	 * Go through the 3 levels of coverage details for this bill master item
	 * 
	 * @param billMasterItem
	 * @param userInsProviderPlan
	 * @return Stack with coverage details from lowest at top to highest at bottom
	 */
	private Stack<InsCoverageDetail> getCovDetailStackByMasterItem(BillMasterItem billMasterItem, InsProviderPlan userInsProviderPlan) {
		if (userInsProviderPlan != null && CollectionUtil.isCollectionEmpty(userInsProviderPlan.getInsCoverageDetailList())) {
			return new Stack<>();
		}
		Stack<InsCoverageDetail> insCovByPlanStack = new Stack<>();

		for (InsCoverageDetail icd : userInsProviderPlan.getInsCoverageDetailList()) {
			if (!icd.getIsCovered()) {
				continue;
			}
			if (icd.getBillClassification() != null && billMasterItem.getBillClassification() != null
					&& billMasterItem.getBillClassification().getParentClassification() != null
					&& icd	.getBillClassification().getRid()
							.equals(billMasterItem.getBillClassification().getParentClassification().getRid())) {
				// (3rd level)

				insCovByPlanStack.push(icd);
			} else if (icd.getBillClassification() != null && billMasterItem.getBillClassification() != null
					&& icd.getBillClassification().getRid().equals(billMasterItem.getBillClassification().getRid())) {
				// (2nd level)

				insCovByPlanStack.push(icd);
			} else if (icd.getBillMasterItem() != null && icd.getBillMasterItem().getRid().equals(billMasterItem.getRid())) {
				// (1st level)

				insCovByPlanStack.push(icd);
			}
		}

		return insCovByPlanStack;
	}

	/**
	 * Get master items for each test , and get the top coverage detail for that item
	 * Used to show the user if the master item need an authorization or not.
	 * 
	 * @param testPricingWrapper
	 * 
	 * @return PaymentWrapper that only has bill master item and it's coverage detail, no calculations
	 */
	public List<PaymentInformation> getTestsCoverageDetail(Long visitRid) {
		List<String> excludedStatuses = Arrays.asList(OperationStatus.ABORTED.getValue(), OperationStatus.CANCELLED.getValue());
		EmrVisit visit = visitService.findOne(
				SearchCriterion.generateRidFilter(visitRid, FilterOperator.eq), EmrVisit.class,
				"providerPlan", "patientInsuranceInfo", "visitGroups.testGroup", "labSamples.labTestActualSet.testDefinition",
				"labSamples.labTestActualSet.lkpOperationStatus", "labSamples.labTestActualSet.billChargeSlipList.billMasterItem",
				"labSamples.labTestActualSet.sourceActualTest");
		List<LabTestActual> testActuals = visit	.getLabSamples().stream().flatMap(s -> s.getLabTestActualSet().stream())
												.filter(lta -> !excludedStatuses.contains(
														lta.getLkpOperationStatus().getCode()))
												.distinct()
												.collect(Collectors.toList());
		List<TestGroup> selectedGroups = new ArrayList<>();
		if (!CollectionUtil.isCollectionEmpty(visit.getVisitGroups())) {
			selectedGroups = visit.getVisitGroups().stream().map(EmrVisitGroup::getTestGroup).distinct().collect(Collectors.toList());
		}
		CalculationData calculationData = getCalculationData(testActuals, selectedGroups, visit.getProviderPlan());
		List<PaymentInformation> paymentInformations = calculationData.getPaymentInformations();
		//to get the old authority for the chargeslips
		List<BillChargeSlip> previousChargeSlips = visit.getLabSamples().stream().flatMap(s -> s.getLabTestActualSet().stream())
														.filter(lta -> !CollectionUtil.isCollectionEmpty(
																lta.getBillChargeSlipList()))
														.flatMap(lta -> lta.getBillChargeSlipList().stream())
														.collect(Collectors.toList());
		Map<Long, Boolean> previousAuthorizations = new HashMap<>();
		if (!CollectionUtil.isCollectionEmpty(previousChargeSlips)) {
			for (BillChargeSlip bcs : previousChargeSlips) {
				if (previousAuthorizations.containsKey(bcs.getBillMasterItem().getRid())) {
					continue;
				}
				previousAuthorizations.put(bcs.getBillMasterItem().getRid(), bcs.getIsAuthorized());
			}

		}
		for (BillMasterItem billMasterItem : calculationData.getFetchedBmiList()) {
			Boolean previousAuth = null;
			if (!CollectionUtil.isMapEmpty(previousAuthorizations) && previousAuthorizations.containsKey(billMasterItem.getRid())) {
				previousAuth = previousAuthorizations.get(billMasterItem.getRid());
			}
			for (PaymentInformation pi : paymentInformations) {
				for (BillChargeSlip bcs : pi.getChargeSlips()) {
					if (bcs.getBillMasterItem().getRid().equals(billMasterItem.getRid())) {
						//pi.setIsFetched(Boolean.TRUE);
						InsCoverageDetail icd = null;
						if (calculationData.getUserInsProviderPlan() != null) {
							Stack<InsCoverageDetail> insCovByPlanStack = getCovDetailStackByMasterItem(billMasterItem,
									calculationData.getUserInsProviderPlan());
							if (!CollectionUtil.isCollectionEmpty(insCovByPlanStack)) {
								icd = insCovByPlanStack.pop();// get the top element of the stack
								bcs.setInsCoverageDetail(icd);
							}
						}
						if (previousAuth != null) {
							bcs.setIsAuthorized(previousAuth);
						} else {
							bcs.setIsAuthorized(Boolean.TRUE);
							if (icd != null) {
								bcs.setIsAuthorized(!icd.getNeedAuthorization());// if it need authorization then its not authorized, otherwise it is
							}
						}
						break;
					}
				}

			}
		}
		//		for (BillMasterItem billMasterItem : calculationData.getBmiNoPriceList()) {
		//			for (PaymentInformation pi : paymentInformations) {
		//				for (BillChargeSlip bcs : pi.getChargeSlips()) {
		//					if (bcs.getBillMasterItem().getRid().equals(billMasterItem.getRid())) {
		//						bcs.setIsFetched(Boolean.FALSE);
		//					}
		//				}
		//			}
		//		}

		return paymentInformations;
	}

	/**
	 * The global rounding for any calculations, HALF_UP mode
	 * 
	 * @param amount the amount to round
	 * @return rounded amount
	 */
	public BigDecimal amountRounding(BigDecimal amount) {
		return amount.setScale(3, RoundingMode.HALF_UP);
	}

	/**
	 * Divide number1 / number2
	 * 
	 * @param number1
	 * @param number2
	 * @return BigDecimal
	 */
	public BigDecimal divideNumbers(BigDecimal number1, BigDecimal number2) {
		//removed the rounding since this is used to divide percentages so we need the actual value
		//return number1.divide(number2, 3, RoundingMode.HALF_UP);
		return number1.divide(number2);
	}

	/**
	 * 
	 * @param paymentWrapperList
	 * @param bmiNoPriceList
	 * @param total
	 * @return
	 */
	private Map<String, Object> calculationResult(EmrVisit visit, List<PaymentInformation> paymentInformations,
			List<BillMasterItem> bmiNoPriceList,
			BigDecimal total) {
		Map<String, Object> result = new HashMap<>();
		result.put("visit", visit);
		result.put("result", paymentInformations);
		result.put("masterItemsNoPriceList", bmiNoPriceList);
		result.put("total", total);
		return result;
	}

	/**
	 * Don't update patient's balance for referral visits.
	 * 
	 * @param visit
	 * @param patient
	 * @param credit
	 * @param debit
	 */
	public void updatePatientBalance(EmrVisit visit, EmrPatientInfo patient, BigDecimal credit, BigDecimal debit) {
		if (VisitType.getByValue(visit.getVisitType().getCode()) == VisitType.REFERRAL) {
			return;
		}
		balanceTransactionService.addBalanceTransaction(BalanceTransactionType.PATIENT, patient, visit, credit, debit);

	}

	/**
	 * Update the insurance's balance either in the normal way or if referral then use full amount.
	 * 
	 * @param visit
	 * @param provider
	 * @param credit:nullable
	 * @param debit:nullable
	 * @param fullAmount : patient share + insurance share
	 */
	public void updateInsuranceBalance(EmrVisit visit, InsProvider provider, BigDecimal credit, BigDecimal debit, BigDecimal fullAmount) {
		if (provider == null) {//means patient didn't come with insurance
			return;
		}
		BigDecimal amount = BigDecimal.ZERO;//the actual amount to credit or debit depending on the input
		VisitType visitType = VisitType.getByValue(visit.getVisitType().getCode());
		if (visitType == VisitType.REFERRAL) {
			amount = fullAmount;
		} else {
			amount = credit != null ? credit : debit;
		}
		BigDecimal insDeductionAmount = getInsDeductionResult(provider, amount, fullAmount);
		amount = amount.subtract(insDeductionAmount);
		if (amount.compareTo(BigDecimal.ZERO) == -1) {
			return;
		}
		if (credit != null) {
			balanceTransactionService.addBalanceTransaction(BalanceTransactionType.INSURANCE, provider, visit, amount, null);
		} else {
			balanceTransactionService.addBalanceTransaction(BalanceTransactionType.INSURANCE, provider, visit, null, amount);
		}

	}

	/**
	 * How much deduction will the insurance have.
	 * 
	 * @param provider
	 * @param insuranceShare : or the total of the insurances share
	 * @param originalPrice : or the total of the original prices
	 * 
	 * @return amount of deduction
	 */
	public BigDecimal getInsDeductionResult(InsProvider provider, BigDecimal insuranceShare, BigDecimal originalPrice) {
		if (provider == null) {
			return null;
		}
		BigDecimal percentage = divideNumbers(provider.getDiscount(), NumberUtil.MAX_PERCENTAGE);
		BigDecimal insDeductionAmount = provider.getIsNetAmount() ? insuranceShare.multiply(percentage)
				: originalPrice.multiply(percentage);
		return amountRounding(insDeductionAmount);
	}

	/**
	 * Generate charging objects.
	 * depending on test definition flags
	 * 
	 * @param testActuals
	 * @return List<PaymentInformation>
	 */
	private List<PaymentInformation> generatePaymentInformation(List<LabTestActual> testActuals) {
		List<PaymentInformation> paymentInformations = new ArrayList<>();
		Set<Long> chargeableTestsRid = new HashSet<>();
		//for non re-ordered tests ,this only matters in test selection when creating order
		//we create an initial PaymentInformation for the test then we check where we gonna make more or not
		for (LabTestActual lta : testActuals) {
			if (lta.getSourceActualTest() != null) {
				continue;
			}
			TestDefinition td = lta.getTestDefinition();
			if (td.getIsAllowRepetitionDifferentSample() && !td.getIsDifferentSampleChargeable()) {
				if (chargeableTestsRid.contains(td.getRid())) {
					continue;
				} else {
					chargeableTestsRid.add(td.getRid());
				}
			}
			PaymentInformation pi = new PaymentInformation();
			pi.setTestDefinition(td);
			paymentInformations.add(pi);
		}
		//for re-ordered tests only this is affective when editing an order
		for (LabTestActual lta : testActuals) {
			if (lta.getSourceActualTest() == null) {
				continue;
			}
			TestDefinition td = lta.getTestDefinition();
			if (td.getIsAllowRepetitionSameSample() && !td.getIsSameSampleChargeable()) {
				continue;
			}
			PaymentInformation pi = new PaymentInformation();
			pi.setTestDefinition(td);
			paymentInformations.add(pi);
		}
		return paymentInformations;
	}

	@Override
	protected BillPatientTransactionRepo getRepository() {
		return billPatientTransactionRepo;
	}

}

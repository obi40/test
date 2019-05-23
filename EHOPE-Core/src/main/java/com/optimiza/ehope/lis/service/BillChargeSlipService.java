package com.optimiza.ehope.lis.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.optimiza.core.base.helper.SearchCriterion;
import com.optimiza.core.base.helper.SearchCriterion.FilterOperator;
import com.optimiza.core.base.service.GenericService;
import com.optimiza.core.common.business.exception.BusinessException;
import com.optimiza.core.common.business.exception.BusinessException.ErrorSeverity;
import com.optimiza.core.common.util.CollectionUtil;
import com.optimiza.core.common.util.StringUtil;
import com.optimiza.ehope.lis.helper.BalanceTransactionType;
import com.optimiza.ehope.lis.lkp.helper.OperationStatus;
import com.optimiza.ehope.lis.lkp.helper.SerialType;
import com.optimiza.ehope.lis.lkp.helper.TestDestinationType;
import com.optimiza.ehope.lis.model.BillChargeSlip;
import com.optimiza.ehope.lis.model.EmrVisit;
import com.optimiza.ehope.lis.model.InsProvider;
import com.optimiza.ehope.lis.model.LabBranch;
import com.optimiza.ehope.lis.model.LabTestActual;
import com.optimiza.ehope.lis.repo.BillChargeSlipRepo;
import com.optimiza.ehope.lis.wrapper.PaymentInformation;
import com.optimiza.ehope.lis.wrapper.PaymentWrapper;
import com.optimiza.ehope.lis.wrapper.TestPricingWrapper;

/**
 * BillChargeSlipService.java
 * 
 * @author Abdullah Imran <aImran@optimizasolutions.com>
 * @since Feb/19/2018
 **/

@Service("BillChargeSlipService")
public class BillChargeSlipService extends GenericService<BillChargeSlip, BillChargeSlipRepo> {

	@Autowired
	private BillChargeSlipRepo billChargeSlipRepo;
	@Autowired
	private SysSerialService serialSerivce;
	@Autowired
	private LabTestActualService labTestActualService;
	@Autowired
	private EmrVisitService visitService;
	@Autowired
	private BillBalanceTransactionService balanceService;
	@Autowired
	private BillPatientTransactionService patientTransactionService;
	@Autowired
	private LabBranchService branchService;
	@Autowired
	private BillPricingService pricingService;

	public List<BillChargeSlip> chargeBillMasterItems(EmrVisit patientVisit, List<PaymentWrapper> paymentWrapperList,
			TestPricingWrapper testPricingWrapper) {
		// fetch visit with patient for updating balances
		EmrVisit visit = visitService.findOne(Arrays.asList(new SearchCriterion("rid", patientVisit.getRid(), FilterOperator.eq)),
				EmrVisit.class, "emrPatientInfo", "visitType", "providerPlan.insProvider");
		LabBranch labBranch = branchService.findById(visit.getBranchId());
		InsProvider provider = visit.getProviderPlan() != null ? visit.getProviderPlan().getInsProvider() : null;
		Map<InsProvider, BigDecimal> externalBranchCredit = new HashMap<>();
		List<LabTestActual> labTestActualList = labTestActualService.findActualsChargeSlip(visit.getRid());
		List<BillChargeSlip> billChargeSlips = new ArrayList<>();
		for (PaymentWrapper pw : paymentWrapperList) {
			LabTestActual labTestActual = null;
			BigDecimal referralAmount = null;
			for (LabTestActual lta : labTestActualList) {
				if (lta.getTestDefinition().equals(pw.getTestDefinition())) {
					labTestActual = lta;
					TestDestinationType tdt = TestDestinationType.valueOf(labTestActual.getTestDestination().getType().getCode());
					if (!TestDestinationType.WORKBENCH.equals(tdt)) {
						referralAmount = pricingService.getTestPrice(labTestActual.getTestDefinition(),
								labTestActual.getTestDestination().getDestinationBranch().getPriceList());
						InsProvider insurance = labTestActual.getTestDestination().getDestinationBranch();
						if (externalBranchCredit.containsKey(insurance)) {
							externalBranchCredit.put(insurance, externalBranchCredit.get(insurance).add(referralAmount));
						} else {
							externalBranchCredit.put(insurance, referralAmount);
						}
					}
					break;
				}
			}
			for (PaymentInformation pi : pw.getPaymentInformationList()) {
				if (pi.getInsCoverageDetail() != null && pi.getInsCoverageDetail().getNeedAuthorization() && pi.getIsAuthorized()
						&& StringUtil.isEmpty(visit.getApprovalNumber())) {
					throw new BusinessException("Bill Master Item was approved without an approval number", "billMasterItemNoAprvNum",
							ErrorSeverity.ERROR);
				}
				String code = serialSerivce.sequenceGeneration(SerialType.CHARGE_SLIP_NO);
				BillChargeSlip billChargeSlip = new BillChargeSlip();
				billChargeSlip.setIsCancelled(Boolean.FALSE);
				billChargeSlip.setCode(code);
				billChargeSlip.setLabTestActual(labTestActual);
				billChargeSlip.setBillMasterItem(pi.getBillMasterItem());
				billChargeSlip.setBillClassification(pi.getBillMasterItem().getBillClassification());
				if (billChargeSlip.getBillClassification() != null) {
					billChargeSlip.setParentClassification(pi.getBillMasterItem().getBillClassification().getParentClassification());
				}
				billChargeSlip.setInsCoverageDetail(pi.getInsCoverageDetail());
				billChargeSlip.setBillPricing(pi.getBillPricing());
				billChargeSlip.setIsAuthorized(pi.getIsAuthorized());
				billChargeSlip.setReferralAmount(referralAmount);
				billChargeSlip.setGeneralDiscountAmount(testPricingWrapper.getGeneralDiscountAmount());
				billChargeSlip.setGeneralDiscountPercentage(testPricingWrapper.getGeneralDiscountPercentage());

				billChargeSlip.setOriginalPrice(pi.getBillPricing().getPrice());

				billChargeSlip.setPercentage(pi.getPercentage());
				billChargeSlip.setInsCoverageResult(pi.getInsCoverageResult());
				billChargeSlip.setInsDeductionPercentage(pi.getInsDeductionPercentage());
				billChargeSlip.setInsDeductionResult(pi.getInsDeductionResult());

				billChargeSlip.setGroupDiscountPercentage(pi.getGroupPercentage());
				billChargeSlip.setGroupCoverageAmount(pi.getGroupAmount());
				billChargeSlip.setGroupCoverageResult(pi.getGroupResult());

				billChargeSlip.setAmountAfterCoverage(pi.getChargeAfterCoverage());

				billChargeSlip.setAmount(pi.getCharge());

				billChargeSlips.add(billChargeSlip);
			}
		}

		//Update balances
		BigDecimal patientShare = billChargeSlips.stream().map(BillChargeSlip::getAmount).reduce(BigDecimal.ZERO, BigDecimal::add);

		BigDecimal insuranceShare = billChargeSlips	.stream()
													.filter(bcs -> patientTransactionService.getInsuranceCoverageAmount(bcs) != null)
													.map(bcs ->
														{
															return patientTransactionService.getInsuranceCoverageAmount(bcs);
														})
													.reduce(BigDecimal.ZERO, BigDecimal::add);
		BigDecimal fullAmount = insuranceShare.add(patientShare);
		patientTransactionService.updateInsuranceBalance(visit, provider, null, insuranceShare, fullAmount);
		patientTransactionService.updatePatientBalance(visit, visit.getEmrPatientInfo(), null, patientShare);
		balanceService.addBalanceTransaction(BalanceTransactionType.LAB_SALES, labBranch, visit, fullAmount, null);

		//if there any non workbench destinations then we should debit the branch and credit the destination
		chargeBalanceDestinations(visit, labBranch, externalBranchCredit, Boolean.FALSE);
		//Abort any testactual that is not in the paymentWrapperList which means it got deleted from the front end
		List<Long> toAbortTests = labTestActualList.stream().filter(lta ->
			{
				for (PaymentWrapper pw : paymentWrapperList) {
					if (pw.getTestDefinition().getRid().equals(lta.getTestDefinition().getRid())) {
						return false;
					}
				}
				return true;
			}).map(LabTestActual::getRid).collect(Collectors.toList());
		labTestActualService.propegateTestsStatusesNoAuth(visit.getRid(), toAbortTests, OperationStatus.ABORTED, null);

		billChargeSlips = getRepository().save(billChargeSlips);
		return billChargeSlips;
	}

	/**
	 * 
	 * @param labBranch
	 * 
	 * @param externalBranch
	 * 
	 * @param isReverse
	 */
	public void chargeBalanceDestinations(EmrVisit visit, LabBranch labBranch, Map<InsProvider, BigDecimal> externalBranch,
			Boolean isReverse) {
		if (CollectionUtil.isMapEmpty(externalBranch)) {
			return;
		}

		BigDecimal referralsTotal = BigDecimal.ZERO;
		for (Map.Entry<InsProvider, BigDecimal> entry : externalBranch.entrySet()) {
			if (isReverse) {
				balanceService.addBalanceTransaction(BalanceTransactionType.INSURANCE, entry.getKey(), visit, null, entry.getValue());
			} else {
				balanceService.addBalanceTransaction(BalanceTransactionType.INSURANCE, entry.getKey(), visit, entry.getValue(), null);
			}
			referralsTotal = referralsTotal.add(entry.getValue());
		}
		if (isReverse) {
			balanceService.addBalanceTransaction(BalanceTransactionType.LAB_SALES, labBranch, visit, referralsTotal, null);
		} else {
			balanceService.addBalanceTransaction(BalanceTransactionType.LAB_SALES, labBranch, visit, null, referralsTotal);
		}

	}

	public void deleteChargeSlip(List<BillChargeSlip> chargeSlips) {
		patientTransactionService.deleteAllByBillChargeSlipIn(chargeSlips);
		getRepository().delete(chargeSlips);
	}

	public List<BillChargeSlip> getRecalculateData(Long visitRid) {
		return new ArrayList<>(getRepository().getRecalculateData(visitRid));
	}

	public List<BillChargeSlip> findByVisit(Long visitRid) {
		return new ArrayList<>(getRepository().findByVisit(visitRid));
	}

	public List<BillChargeSlip> getCancelData(Long visitRid) {
		return new ArrayList<>(getRepository().getCancelData(visitRid));
	}

	/**
	 * Does this visit has charge slips?
	 * 
	 * @param visitRid
	 * @return Boolean
	 */
	public Boolean isVisitCharged(Long visitRid) {
		return !CollectionUtil.isCollectionEmpty(getRepository().findByVisit(visitRid));
	}

	public BillChargeSlip updateBillChargeSlip(BillChargeSlip billChargeSlip) {
		return getRepository().save(billChargeSlip);
	}

	public List<BillChargeSlip> updateBillChargeSlip(Collection<BillChargeSlip> billChargeSlips) {
		return getRepository().save(billChargeSlips);
	}

	@Override
	protected BillChargeSlipRepo getRepository() {
		return billChargeSlipRepo;
	}

}

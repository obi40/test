package com.optimiza.ehope.lis.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.BeanUtils;
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
import com.optimiza.ehope.lis.lkp.helper.SerialType;
import com.optimiza.ehope.lis.lkp.helper.TestDestinationType;
import com.optimiza.ehope.lis.model.BillChargeSlip;
import com.optimiza.ehope.lis.model.EmrPatientInfo;
import com.optimiza.ehope.lis.model.EmrVisit;
import com.optimiza.ehope.lis.model.InsProvider;
import com.optimiza.ehope.lis.model.LabBranch;
import com.optimiza.ehope.lis.model.LabTestActual;
import com.optimiza.ehope.lis.repo.BillChargeSlipRepo;
import com.optimiza.ehope.lis.wrapper.PaymentInformation;
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
	private SysSerialService serialService;
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

	public List<BillChargeSlip> createVisitChargeSlips(EmrVisit patientVisit, List<PaymentInformation> paymentInformations,
			TestPricingWrapper testPricingWrapper) {
		// fetch visit with patient for updating balances
		EmrVisit visit = visitService.findOne(SearchCriterion.generateRidFilter(patientVisit.getRid(), FilterOperator.eq),
				EmrVisit.class, "emrPatientInfo", "visitType", "providerPlan.insProvider");
		LabBranch labBranch = branchService.findById(visit.getBranchId());
		InsProvider provider = visit.getProviderPlan() != null ? visit.getProviderPlan().getInsProvider() : null;
		Map<InsProvider, BigDecimal> externalBranchCredit = new HashMap<>();
		List<LabTestActual> labTestActualList = labTestActualService.findActualsChargeSlip(visit.getRid());
		List<BillChargeSlip> billChargeSlips = new ArrayList<>();
		for (PaymentInformation pi : paymentInformations) {
			LabTestActual labTestActual = null;
			BigDecimal referralAmount = null;
			for (LabTestActual lta : labTestActualList) {
				if (lta.getTestDefinition().getRid().equals(pi.getTestDefinition().getRid())) {
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
			for (BillChargeSlip bcs : pi.getChargeSlips()) {
				if (bcs.getInsCoverageDetail() != null && bcs.getInsCoverageDetail().getNeedAuthorization() && bcs.getIsAuthorized()
						&& StringUtil.isEmpty(visit.getApprovalNumber())) {
					throw new BusinessException("Bill Master Item was approved without an approval number", "billMasterItemNoAprvNum",
							ErrorSeverity.ERROR);
				}
				String code = serialService.sequenceGeneration(SerialType.CHARGE_SLIP_NO);
				bcs.setIsCancelled(Boolean.FALSE);
				bcs.setCode(code);
				bcs.setLabTestActual(labTestActual);
				bcs.setBillClassification(bcs.getBillMasterItem().getBillClassification());
				if (bcs.getBillClassification() != null) {
					bcs.setParentClassification(bcs.getBillMasterItem().getBillClassification().getParentClassification());
				}
				bcs.setReferralAmount(referralAmount);
				bcs.setGeneralDiscountAmount(testPricingWrapper.getGeneralDiscountAmount());
				bcs.setGeneralDiscountPercentage(testPricingWrapper.getGeneralDiscountPercentage());
				billChargeSlips.add(bcs);
			}

			labTestActualList.remove(labTestActual);
		}
		chargeVisit(billChargeSlips, visit, provider, visit.getEmrPatientInfo(), labBranch, externalBranchCredit);
		billChargeSlips = getRepository().save(billChargeSlips);
		return billChargeSlips;
	}

	/**
	 * Duplicate Test's charge slips and charge the visit
	 * 
	 * @param sourceTestActualRid
	 * @param newTestActual
	 * @param visitRid
	 * @return
	 */
	public List<BillChargeSlip> duplicateTestChargeSlips(Long sourceTestRid, LabTestActual newTestActual, Long visitRid) {
		EmrVisit visit = visitService.findOne(SearchCriterion.generateRidFilter(visitRid, FilterOperator.eq),
				EmrVisit.class, "emrPatientInfo", "visitType", "providerPlan.insProvider", "labSamples.labTestActualSet.billChargeSlipList",
				"labSamples.labTestActualSet.testDefinition");
		LabBranch labBranch = branchService.findById(visit.getBranchId());
		InsProvider provider = visit.getProviderPlan() != null ? visit.getProviderPlan().getInsProvider() : null;
		//get any test actual that has the same test def and has charge slips
		LabTestActual sourceTestActual = visit	.getLabSamples().stream().flatMap(s -> s.getLabTestActualSet().stream())
												.filter(lta -> lta.getTestDefinition().getRid().equals(sourceTestRid)
														&& !CollectionUtil.isCollectionEmpty(lta.getBillChargeSlipList()))
												.findFirst().get();
		Set<BillChargeSlip> sourceChargeSlips = sourceTestActual.getBillChargeSlipList();
		InsProvider sourceDestinationInsurance = sourceTestActual.getTestDestination().getDestinationBranch();
		List<BillChargeSlip> newChargeSlips = new ArrayList<>();
		Map<InsProvider, BigDecimal> externalBranchCredit = new HashMap<>();
		BigDecimal patientShare = BigDecimal.ZERO;
		for (BillChargeSlip oldSlip : sourceChargeSlips) {
			BillChargeSlip newSlip = new BillChargeSlip();
			BeanUtils.copyProperties(oldSlip, newSlip, "rid", "version", "creationDate", "createdBy", "updatedDate", "updatedBy",
					"labTestActual", "code", "billPatientTransactionList");
			String code = serialService.sequenceGeneration(SerialType.CHARGE_SLIP_NO);
			newSlip.setCode(code);
			newSlip.setLabTestActual(newTestActual);
			if (newSlip.getReferralAmount() != null && sourceDestinationInsurance != null) {
				if (externalBranchCredit.containsKey(sourceDestinationInsurance)) {
					externalBranchCredit.put(sourceDestinationInsurance,
							externalBranchCredit.get(sourceDestinationInsurance).add(newSlip.getReferralAmount()));
				} else {
					externalBranchCredit.put(sourceDestinationInsurance, newSlip.getReferralAmount());
				}
			}
			patientShare = patientShare.add(newSlip.getAmount());
			newChargeSlips.add(newSlip);
		}
		chargeVisit(newChargeSlips, visit, provider, visit.getEmrPatientInfo(), labBranch, externalBranchCredit);
		visit.setTotalAmount(visit.getTotalAmount().add(patientShare));
		visit = visitService.updateVisit(visit, null);
		newChargeSlips = getRepository().save(newChargeSlips);
		return newChargeSlips;
	}

	/**
	 * The operation where we credit/debit entities.
	 * Internal uses.
	 * 
	 * @param chargeSlips
	 * @param visit
	 * @param provider
	 * @param patientInfo
	 * @param branch
	 * @param externalBranchCredit
	 */
	private void chargeVisit(List<BillChargeSlip> chargeSlips, EmrVisit visit, InsProvider provider, EmrPatientInfo patientInfo,
			LabBranch branch, Map<InsProvider, BigDecimal> externalBranchCredit) {

		BigDecimal patientShare = chargeSlips.stream().map(BillChargeSlip::getAmount).reduce(BigDecimal.ZERO, BigDecimal::add);

		BigDecimal insuranceShare = chargeSlips	.stream()
												.filter(bcs -> patientTransactionService.getInsuranceCoverageAmount(bcs) != null)
												.map(bcs ->
													{
														return patientTransactionService.getInsuranceCoverageAmount(bcs);
													})
												.reduce(BigDecimal.ZERO, BigDecimal::add);
		BigDecimal fullAmount = insuranceShare.add(patientShare);
		patientTransactionService.updateInsuranceBalance(visit, provider, null, insuranceShare, fullAmount);
		patientTransactionService.updatePatientBalance(visit, patientInfo, null, patientShare);
		balanceService.addBalanceTransaction(BalanceTransactionType.LAB_SALES, branch, visit, fullAmount, null);
		//if there any non workbench destinations then we should debit the branch and credit the destination
		chargeBalanceDestinations(visit, branch, externalBranchCredit, Boolean.FALSE);
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

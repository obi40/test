package com.optimiza.ehope.lis.service;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.optimiza.core.base.entity.BaseEntity;
import com.optimiza.core.base.helper.SearchCriterion;
import com.optimiza.core.base.helper.SearchCriterion.FilterOperator;
import com.optimiza.core.base.service.GenericService;
import com.optimiza.core.common.annotation.InterceptorFree;
import com.optimiza.core.common.business.exception.BusinessException;
import com.optimiza.core.common.business.exception.BusinessException.ErrorSeverity;
import com.optimiza.core.lkp.service.LkpService;
import com.optimiza.ehope.lis.helper.BalanceTransactionType;
import com.optimiza.ehope.lis.lkp.model.LkpBalanceTransactionType;
import com.optimiza.ehope.lis.model.BillBalanceTransaction;
import com.optimiza.ehope.lis.model.EmrPatientInfo;
import com.optimiza.ehope.lis.model.EmrVisit;
import com.optimiza.ehope.lis.model.InsProvider;
import com.optimiza.ehope.lis.model.LabBranch;
import com.optimiza.ehope.lis.repo.BillBalanceTransactionRepo;

/**
 * BillBalanceTransactionService.java
 * 
 * @author Abdullah Imran <aImran@optimizasolutions.com>
 * @since Jul/09/2018
 **/

@Service("BillBalanceTransactionService")
public class BillBalanceTransactionService extends GenericService<BillBalanceTransaction, BillBalanceTransactionRepo> {

	@Autowired
	private BillBalanceTransactionRepo billBalanceRepo;
	@Autowired
	private LkpService lkpService;
	@Autowired
	private BillPatientTransactionService patientTransactionService;

	public void addBalanceTransaction(BalanceTransactionType balanceTransactionType, BaseEntity balanceHolder, EmrVisit visit,
			BigDecimal credit, BigDecimal debit) {

		BigDecimal amount = credit != null ? credit : debit;
		amount = patientTransactionService.amountRounding(amount);
		if (amount.compareTo(BigDecimal.ZERO) == -1) {
			throw new BusinessException("Amount cant be less than zero", "invalidParameters", ErrorSeverity.ERROR);
		} else if (amount.compareTo(BigDecimal.ZERO) == 0) {
			return;
		}
		LkpBalanceTransactionType lkpBalanceTransactionType = lkpService.findOneAnyLkp(
				Arrays.asList(new SearchCriterion("code", balanceTransactionType.getValue(), FilterOperator.eq)),
				LkpBalanceTransactionType.class);
		BillBalanceTransaction balanceTransaction = new BillBalanceTransaction();
		balanceTransaction.setBalanceTransactionType(lkpBalanceTransactionType);
		balanceTransaction.setIsActive(Boolean.TRUE);
		balanceTransaction.setVisit(visit);
		BigDecimal absAmount = credit != null ? amount : amount.multiply(new BigDecimal("-1"));
		if (credit != null) {
			balanceTransaction.setCredit(amount);
		} else if (debit != null) {
			balanceTransaction.setDebit(amount);
		}
		switch (balanceTransactionType) {
			case PATIENT:
				EmrPatientInfo patient = EmrPatientInfo.class.cast(balanceHolder);
				patient.setBalance(patient.getBalance().add(absAmount));
				balanceTransaction.setPatientInfo(patient);
				break;
			case LAB_CASH_DRAWER:
			case LAB_SALES:
				LabBranch branch = LabBranch.class.cast(balanceHolder);
				branch.setBalance(branch.getBalance().add(absAmount));
				balanceTransaction.setBranch(branch);
				break;
			case INSURANCE:
				InsProvider insurance = InsProvider.class.cast(balanceHolder);
				insurance.setBalance(insurance.getBalance().add(absAmount));
				balanceTransaction.setProvider(insurance);
				break;
		}
		balanceTransaction = getRepository().save(balanceTransaction);
	}

	public List<BillBalanceTransaction> updateBalanceTransaction(List<BillBalanceTransaction> balanceTransaction) {
		return getRepository().save(balanceTransaction);
	}

	@InterceptorFree
	public List<BillBalanceTransaction> findExcluded(List<SearchCriterion> filters, Sort sort, String... joins) {
		return getRepository().find(filters, BillBalanceTransaction.class, sort, joins).stream().distinct().collect(Collectors.toList());
	}

	@Override
	protected BillBalanceTransactionRepo getRepository() {
		return billBalanceRepo;
	}

}

package com.optimiza.ehope.lis.lkp.service;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.optimiza.core.base.helper.SearchCriterion;
import com.optimiza.core.base.helper.SearchCriterion.FilterOperator;
import com.optimiza.core.base.service.GenericService;
import com.optimiza.core.common.business.exception.BusinessException;
import com.optimiza.core.common.business.exception.BusinessException.ErrorSeverity;
import com.optimiza.core.common.util.SecurityUtil;
import com.optimiza.core.lkp.service.LkpService;
import com.optimiza.ehope.lis.helper.EhopeRights;
import com.optimiza.ehope.lis.lkp.helper.OperationStatus;
import com.optimiza.ehope.lis.lkp.model.LkpOperationStatus;
import com.optimiza.ehope.lis.lkp.repo.LkpOperationStatusRepo;
import com.optimiza.ehope.lis.service.BillChargeSlipService;

/**
 * LkpOperationStatusService.java
 * 
 * @author Abdullah Imran <aImran@optimizasolutions.com>
 * @since Mar/14/2018
 **/

@Service("LkpOperationStatusService")
public class LkpOperationStatusService extends GenericService<LkpOperationStatus, LkpOperationStatusRepo> {

	@Autowired
	private LkpOperationStatusRepo lkpOperationStatusRepo;
	@Autowired
	private BillChargeSlipService chargeSlipService;
	@Autowired
	private LkpService lkpService;

	/**
	 * Some business validations.
	 * 
	 * @param currentOperationStatus
	 * @param newOperationStatus
	 * 
	 */
	public void validations(OperationStatus currentOperationStatus, OperationStatus newOperationStatus, Long visitRid) {

		if (newOperationStatus == currentOperationStatus) {
			return;
		}
		if (!SecurityUtil.isUserAuthorized(EhopeRights.CANCEL_ANY_OPERATION_STATUS) && newOperationStatus == OperationStatus.CANCELLED
				&& (currentOperationStatus == OperationStatus.RESULTS_ENTERED || currentOperationStatus == OperationStatus.FINALIZED
						|| currentOperationStatus == OperationStatus.CLOSED)) {
			throw new BusinessException("Finalized/Closed/Results Enteredcan not be cancelled", "operationStatusCancelFail",
					ErrorSeverity.ERROR);
		}
		//Excluding the jumping over statuses
		if (newOperationStatus != OperationStatus.CANCELLED && newOperationStatus != OperationStatus.CLOSED
				&& newOperationStatus != OperationStatus.ABORTED) {
			if (newOperationStatus.getOrder().compareTo(currentOperationStatus.getOrder()) < 0) {
				throw new BusinessException("Can't go backwards from: " + currentOperationStatus + " -> " + newOperationStatus,
						"operationStatusBackwards", ErrorSeverity.ERROR);
			}
			if (!(currentOperationStatus == OperationStatus.COLLECTED && newOperationStatus == OperationStatus.RESULTS_ENTERED)
					&& (currentOperationStatus.getOrder() + 1) != newOperationStatus.getOrder()) {

				throw new BusinessException("Can't skip from: " + currentOperationStatus + " -> " + newOperationStatus,
						"operationStatusSkip",
						ErrorSeverity.ERROR);

			}

		}

		if (newOperationStatus == OperationStatus.VALIDATED && !chargeSlipService.isVisitCharged(visitRid)) {
			throw new BusinessException("Visit does not have any charge slips", "noChargeSlipInOrder", ErrorSeverity.ERROR);
		}

	}

	public LkpOperationStatus getLkpBySmallestOperationStatuses(List<OperationStatus> operationStatuses) {
		OperationStatus smallestTestStatus = OperationStatus.getSmallestStatus(operationStatuses);
		if (smallestTestStatus == null) {
			return null;
		}
		return lkpService.findOneAnyLkp(
				Arrays.asList(new SearchCriterion("code", smallestTestStatus.getValue(), FilterOperator.eq)), LkpOperationStatus.class);
	}

	@Override
	protected LkpOperationStatusRepo getRepository() {
		return lkpOperationStatusRepo;
	}

}

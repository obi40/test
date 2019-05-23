package com.optimiza.ehope.lis.onboarding.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.optimiza.core.admin.model.SecTenant;
import com.optimiza.core.base.helper.SearchCriterion;
import com.optimiza.core.base.service.GenericService;
import com.optimiza.core.common.business.exception.BusinessException;
import com.optimiza.core.common.business.exception.BusinessException.ErrorSeverity;
import com.optimiza.core.common.util.SecurityUtil;
import com.optimiza.ehope.lis.onboarding.helper.PlanFieldType;
import com.optimiza.ehope.lis.onboarding.model.BrdPlanField;
import com.optimiza.ehope.lis.onboarding.model.BrdTenantPlanDetail;
import com.optimiza.ehope.lis.onboarding.repo.BrdTenantPlanDetailRepo;

/**
 * TenantPlanDetailService.java
 * 
 * @author Abdullah Imran <aImran@optimizasolutions.com>
 * @since May/20/2018
 **/

@Service("BrdTenantPlanDetailService")
public class BrdTenantPlanDetailService extends GenericService<BrdTenantPlanDetail, BrdTenantPlanDetailRepo> {

	@Autowired
	private BrdTenantPlanDetailRepo tenantPlanDetailRepo;

	public List<BrdTenantPlanDetail> createTenantPlanDetails(SecTenant tenant, List<BrdPlanField> planFields) {

		//Delete if exists
		tenantPlanDetailRepo.deleteAllByTenantRid(tenant.getRid());

		List<BrdTenantPlanDetail> planDetails = new ArrayList<>();
		for (BrdPlanField planField : planFields) {
			BrdTenantPlanDetail tpd = new BrdTenantPlanDetail();
			tpd.setAmount(new BigDecimal(planField.getAmount()));
			tpd.setCurrent(BigDecimal.ZERO);
			tpd.setPlanField(planField);
			tpd.setTenant(tenant);
			planDetails.add(tpd);
		}

		return getRepository().save(planDetails);
	}

	public List<BrdTenantPlanDetail> createTenantPlanDetails(List<BrdTenantPlanDetail> tenantPlanDetails) {
		return getRepository().save(tenantPlanDetails);
	}

	/**
	 * To throw an exception if the new amount exceeds the max amount
	 * 
	 * @param planFieldType
	 * @param amount : amount of X to be created
	 * @return BrdTenantPlanDetail
	 */
	public BrdTenantPlanDetail counterChecker(PlanFieldType planFieldType, int amount) {
		BrdTenantPlanDetail currentPlanDetail = getRepository().findByPlanFieldType(SecurityUtil.getCurrentUser().getTenantId(),
				planFieldType.getValue());
		BigDecimal newCurrent = currentPlanDetail.getCurrent().add(new BigDecimal(amount));
		if (newCurrent.compareTo(currentPlanDetail.getAmount()) == 1) {
			String exceptionCode = "";
			switch (planFieldType) {
				case ORDERS:
					exceptionCode = "counterOrders";
					break;
				case USERS:
					exceptionCode = "counterUsers";
					break;

			}
			throw new BusinessException("Exceeded " + planFieldType.getValue() + " max amount of " + currentPlanDetail.getAmount(),
					exceptionCode,
					ErrorSeverity.ERROR);
		}
		currentPlanDetail.setCurrent(newCurrent);
		currentPlanDetail = getRepository().save(currentPlanDetail);
		return currentPlanDetail;
	}

	public List<BrdTenantPlanDetail> findTenantPlanDetails(List<SearchCriterion> filters, Sort sort, String... joins) {
		return getRepository().find(filters, BrdTenantPlanDetail.class, sort, joins);
	}

	@Override
	protected BrdTenantPlanDetailRepo getRepository() {
		return this.tenantPlanDetailRepo;
	}

}

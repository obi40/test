package com.optimiza.ehope.lis.service;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.optimiza.core.base.helper.SearchCriterion;
import com.optimiza.core.base.helper.SearchCriterion.FilterOperator;
import com.optimiza.core.base.service.GenericService;
import com.optimiza.ehope.lis.model.InsCoverageDetail;
import com.optimiza.ehope.lis.model.InsProviderPlan;
import com.optimiza.ehope.lis.repo.InsCoverageDetailRepo;
import com.optimiza.ehope.lis.util.NumberUtil;

/**
 * InsCoverageDetailService.java
 * 
 * @author Abdullah Imran <aImran@optimizasolutions.com>
 * @since Jan/23/2018
 **/

@Service("InsCoverageDetailService")
public class InsCoverageDetailService extends GenericService<InsCoverageDetail, InsCoverageDetailRepo> {

	@Autowired
	private InsCoverageDetailRepo insCoverageDetailRepo;

	public InsCoverageDetail createInsCoverageDetail(InsCoverageDetail insCoverageDetail) {
		BigDecimal maxPercentage = new BigDecimal("100.00");
		if (insCoverageDetail.getPercentage().compareTo(maxPercentage) == 1) {
			insCoverageDetail.setPercentage(maxPercentage);
		}
		return getRepository().save(insCoverageDetail);
	}

	public void deleteInsCoverageDetail(InsCoverageDetail insCoverageDetail) {
		getRepository().delete(insCoverageDetail);
	}

	public void deleteAllByInsProviderPlan(InsProviderPlan providerPlan) {
		getRepository().deleteAllByInsProviderPlan(providerPlan);
	}

	public InsCoverageDetail findInsCoverageDetailById(Long id) {
		return getRepository().findOne(id);
	}

	public List<InsCoverageDetail> updateInsCoverageDetail(List<InsCoverageDetail> coverageDetails) {
		for (InsCoverageDetail isd : coverageDetails) {
			BigDecimal percentage = NumberUtil.validatePercentage(isd.getPercentage());
			isd.setPercentage(percentage);
		}
		return getRepository().save(coverageDetails);
	}

	public InsCoverageDetail updateInsCoverageDetail(InsCoverageDetail coverageDetail) {
		BigDecimal percentage = NumberUtil.validatePercentage(coverageDetail.getPercentage());
		coverageDetail.setPercentage(percentage);
		return getRepository().save(coverageDetail);
	}

	public List<InsCoverageDetail> findInsCoverageDetailList(List<SearchCriterion> filters, Sort sort, String... joins) {
		return getRepository().find(filters, InsCoverageDetail.class, sort, joins);
	}

	public List<InsCoverageDetail> findInsCoverageDetailListByPlan(InsProviderPlan insProviderPlan) {
		return getRepository().find(Arrays.asList(new SearchCriterion("insProviderPlan.rid", insProviderPlan.getRid(), FilterOperator.eq)),
				InsCoverageDetail.class, "lkpCoverageDetailScope",
				"insProviderPlan", "billClassification", "billMasterItem");
	}

	@Override
	protected InsCoverageDetailRepo getRepository() {
		return insCoverageDetailRepo;
	}

}

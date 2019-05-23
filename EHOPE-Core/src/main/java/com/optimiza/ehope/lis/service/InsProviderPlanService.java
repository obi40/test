package com.optimiza.ehope.lis.service;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.optimiza.core.base.helper.SearchCriterion;
import com.optimiza.core.base.helper.SearchCriterion.FilterOperator;
import com.optimiza.core.base.service.GenericService;
import com.optimiza.core.common.data.model.TransField;
import com.optimiza.core.common.helper.FilterablePageRequest;
import com.optimiza.ehope.lis.model.InsCoverageDetail;
import com.optimiza.ehope.lis.model.InsProvider;
import com.optimiza.ehope.lis.model.InsProviderPlan;
import com.optimiza.ehope.lis.repo.InsProviderPlanRepo;
import com.optimiza.ehope.lis.util.NumberUtil;

@Service("InsProviderPlanService")
public class InsProviderPlanService extends GenericService<InsProviderPlan, InsProviderPlanRepo> {

	@Autowired
	private InsProviderPlanRepo repo;
	@Autowired
	private InsCoverageDetailService coverageDetailService;

	@Override
	protected InsProviderPlanRepo getRepository() {
		return repo;
	}

	public List<InsProviderPlan> findProviderPlans(List<SearchCriterion> filters, Sort sort, String... joins) {
		return getRepository().find(filters, InsProviderPlan.class, joins);
	}

	// for the main view of the ins provider
	public List<InsProviderPlan> findInsProviderPlansByProvider(FilterablePageRequest filterablePageRequest) {
		return getRepository().find(filterablePageRequest.getFilters(), InsProviderPlan.class, "insProvider",
				"billPriceList");
	}

	private void propagateIsActive(Long providerPlanRid, Boolean newIsActive) {
		List<InsCoverageDetail> coverageDetails = coverageDetailService.findInsCoverageDetailList(
				Arrays.asList(new SearchCriterion("insProviderPlan.rid", providerPlanRid, FilterOperator.eq)), null, "insProviderPlan");
		for (InsCoverageDetail icd : coverageDetails) {
			icd.setIsActive(newIsActive);
		}
		coverageDetailService.updateInsCoverageDetail(coverageDetails);
	}

	public InsProviderPlan createInsProviderPlan(InsProviderPlan insProviderPlan) {
		BigDecimal percentage = NumberUtil.validatePercentage(insProviderPlan.getCoveragePercentage());
		insProviderPlan.setCoveragePercentage(percentage);
		return getRepository().save(insProviderPlan);
	}

	public void deleteInsProviderPlan(InsProviderPlan insProviderPlan) {
		getRepository().delete(insProviderPlan);
	}

	public void deleteInsProviderPlanByInsProvider(InsProvider insProvider) {
		List<InsProviderPlan> providerPlans = getRepository().find(
				Arrays.asList(new SearchCriterion("insProvider.rid", insProvider.getRid(), FilterOperator.eq)),
				InsProviderPlan.class, "insProvider");
		for (InsProviderPlan ipp : providerPlans) {
			coverageDetailService.deleteAllByInsProviderPlan(ipp);
		}
		getRepository().deleteAllByInsProvider(insProvider);
	}

	public InsProviderPlan updateInsProviderPlan(InsProviderPlan insProviderPlan) {
		BigDecimal percentage = NumberUtil.validatePercentage(insProviderPlan.getCoveragePercentage());
		insProviderPlan.setCoveragePercentage(percentage);
		propagateIsActive(insProviderPlan.getRid(), insProviderPlan.getIsActive());
		return getRepository().save(insProviderPlan);
	}

	public List<InsProviderPlan> updateInsProviderPlan(List<InsProviderPlan> providerPlans) {
		for (InsProviderPlan ipp : providerPlans) {
			BigDecimal percentage = NumberUtil.validatePercentage(ipp.getCoveragePercentage());
			ipp.setCoveragePercentage(percentage);
		}
		return getRepository().save(providerPlans);
	}

	public InsProviderPlan findSimplePlanByProvider(Long providerRid) {
		SearchCriterion providerFilter = new SearchCriterion("insProvider.rid", providerRid, FilterOperator.eq);
		SearchCriterion isSimpleFilter = new SearchCriterion("isSimple", Boolean.TRUE, FilterOperator.eq);
		return getRepository().findOne(Arrays.asList(providerFilter, isSimpleFilter), InsProviderPlan.class,
				"insProvider");
	}

	/**
	 * Reflect the provider data to create a plan with these data.
	 * 
	 * @param provider
	 * @param providerPlan
	 * @param isSimple
	 * @return InsProviderPlan
	 */
	public InsProviderPlan reflectProviderData(InsProvider provider, InsProviderPlan providerPlan, Boolean isSimple) {
		providerPlan.setInsProvider(provider);
		providerPlan.setIsSimple(isSimple);
		providerPlan.setIsActive(provider.getIsActive());
		providerPlan.setCoveragePercentage(provider.getCoveragePercentage());
		providerPlan.setBillPriceList(provider.getPriceList());
		providerPlan.setIsFixed(Boolean.FALSE);
		String suffix = "";
		if (isSimple) {
			suffix = "-Simple";
		}
		providerPlan.setCode(provider.getCode() + suffix);
		TransField tf = new TransField();
		for (Map.Entry<String, String> entry : provider.getName().entrySet()) {
			tf.put(entry.getKey(), entry.getValue() + suffix);
		}
		providerPlan.setName(tf);
		providerPlan.setDescription(tf);
		return providerPlan;
	}

	/**
	 * Create simple plan only if this provider does not have one.
	 * 
	 * @param insProvider
	 */
	public void createSimplePlan(InsProvider provider) {
		if (provider.getIsSimple().equals(Boolean.FALSE)) {
			return;
		}
		InsProviderPlan simplePlan = findSimplePlanByProvider(provider.getRid());
		//already has a simple plan
		if (simplePlan != null) {
			return;
		}
		InsProviderPlan ipp = reflectProviderData(provider, new InsProviderPlan(), Boolean.TRUE);
		createInsProviderPlan(ipp);
	}

	public void updateSimplePlan(InsProvider provider) {
		if (provider.getIsSimple().equals(Boolean.FALSE)) {
			return;
		}
		InsProviderPlan simplePlan = findSimplePlanByProvider(provider.getRid());
		if (simplePlan == null) {
			return;
		}
		simplePlan = reflectProviderData(provider, simplePlan, Boolean.TRUE);
		updateInsProviderPlan(simplePlan);
	}

}

package com.optimiza.ehope.lis.service;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.optimiza.core.base.helper.SearchCriterion;
import com.optimiza.core.base.helper.SearchCriterion.FilterOperator;
import com.optimiza.core.base.service.GenericService;
import com.optimiza.core.common.business.exception.BusinessException;
import com.optimiza.core.common.business.exception.BusinessException.ErrorSeverity;
import com.optimiza.core.common.helper.FilterablePageRequest;
import com.optimiza.core.common.helper.FilterablePageRequest.OrderObject;
import com.optimiza.core.common.util.CollectionUtil;
import com.optimiza.core.common.util.DateUtil;
import com.optimiza.core.lkp.service.LkpService;
import com.optimiza.ehope.lis.helper.BillMasterItemType;
import com.optimiza.ehope.lis.helper.EhopeRights;
import com.optimiza.ehope.lis.lkp.model.LkpBillItemType;
import com.optimiza.ehope.lis.model.BillClassification;
import com.optimiza.ehope.lis.model.BillMasterItem;
import com.optimiza.ehope.lis.model.BillPriceList;
import com.optimiza.ehope.lis.model.BillPricing;
import com.optimiza.ehope.lis.model.BillTestItem;
import com.optimiza.ehope.lis.model.TestDefinition;
import com.optimiza.ehope.lis.repo.BillMasterItemRepo;

@Service("BillMasterItemService")
public class BillMasterItemService extends GenericService<BillMasterItem, BillMasterItemRepo> {

	@Autowired
	private BillMasterItemRepo repo;

	@Autowired
	private TestDefinitionService testDefinitionService;

	@Autowired
	private BillClassificationService billClassificationService;

	@Autowired
	private LkpService lkpService;

	@Autowired
	private BillTestItemService billTestItemService;

	@Autowired
	private BillPricingService billPricingService;

	@Autowired
	private BillPriceListService billPriceListService;

	@Override
	protected BillMasterItemRepo getRepository() {
		return repo;
	}

	@PreAuthorize("hasAuthority('" + EhopeRights.VIEW_BILL_MASTER_ITEM + "')")
	public Page<BillMasterItem> getBillMasterItemList(FilterablePageRequest filterablePageRequest) {
		List<OrderObject> sortList = filterablePageRequest.getSortList();
		OrderObject sortByRid = new OrderObject();
		sortByRid.setDirection(Direction.ASC);
		sortByRid.setProperty("rid");
		sortList.add(sortByRid);
		filterablePageRequest.setSortList(sortList);
		Page<BillMasterItem> all = repo.find(filterablePageRequest.getFilters(),
				filterablePageRequest.getPageRequest(), BillMasterItem.class,
				"type", "billClassification", "billPricings", "billTestItems.testDefinition");
		return all;
	}

	@PreAuthorize("hasAuthority('" + EhopeRights.VIEW_BILL_PRICE_LIST_DETAILS + "')")
	public Page<BillMasterItem> getMasterItemPriceListByTest(FilterablePageRequest filterablePageRequest) {
		Page<BillMasterItem> bmiPage = getRepository().getByMasterItemAndPriceList(filterablePageRequest.getStringFilter("standardCode"),
				filterablePageRequest.getStringFilter("description"), filterablePageRequest.getStringFilter("secondaryCode"),
				filterablePageRequest.getStringFilter("aliases"), filterablePageRequest.getLongFilter("testRid"),
				filterablePageRequest.getPageRequest());
		if (bmiPage.getTotalElements() == 0) {
			return bmiPage;
		}
		List<BillMasterItem> bmiList = getRepository().find(
				Arrays.asList(new SearchCriterion("rid",
						bmiPage.getContent().stream().map(BillMasterItem::getRid).collect(Collectors.toList()), FilterOperator.in)),
				BillMasterItem.class, filterablePageRequest.getPageRequest().getSort(), "billPricings.billPriceList",
				"billTestItems.testDefinition").stream().distinct().collect(Collectors.toList());
		for (BillMasterItem bmi : bmiList) {
			//we only care about the first one,removing the rest because the response size got so big
			if (!CollectionUtil.isCollectionEmpty(bmi.getBillTestItems()) && bmi.getBillTestItems().size() > 0) {
				BillTestItem bti = bmi.getBillTestItems().iterator().next();
				bmi.getBillTestItems().clear();
				bmi.getBillTestItems().add(bti);
			}
		}
		return new PageImpl<>(bmiList, filterablePageRequest.getPageRequest(), bmiPage.getTotalElements());
	}

	@PreAuthorize("hasAuthority('" + EhopeRights.ADD_BILL_MASTER_ITEM + "')")
	public BillMasterItem addBillMasterItem(BillMasterItem billMasterItem) {
		return repo.save(billMasterItem);
	}

	@PreAuthorize("hasAuthority('" + EhopeRights.UPD_BILL_MASTER_ITEM + "')")
	public BillMasterItem editBillMasterItem(BillMasterItem billMasterItem) {
		return repo.save(billMasterItem);
	}

	@PreAuthorize("hasAuthority('" + EhopeRights.DEACTIVATE_BILL_MASTER_ITEM + "')")
	public BillMasterItem deactivateBillMasterItem(Long rid) throws BusinessException {
		BillMasterItem fetchedBillMasterItem = repo.getOne(rid);
		if (!fetchedBillMasterItem.getIsActive()) {
			throw new BusinessException("This billing master item is already inactive!", "billingMasterItemAlreadyInactive",
					ErrorSeverity.ERROR);
		}

		fetchedBillMasterItem.setIsActive(false);

		return repo.save(fetchedBillMasterItem);
	}

	@PreAuthorize("hasAuthority('" + EhopeRights.ACTIVATE_BILL_MASTER_ITEM + "')")
	public BillMasterItem activateBillMasterItem(Long rid) throws BusinessException {
		BillMasterItem fetchedBillMasterItem = repo.getOne(rid);
		if (fetchedBillMasterItem.getIsActive()) {
			throw new BusinessException("This billing master item is already active!", "billingMasterItemAlreadyActive",
					ErrorSeverity.ERROR);
		}

		fetchedBillMasterItem.setIsActive(true);

		return repo.save(fetchedBillMasterItem);
	}

	@PreAuthorize("hasAuthority('" + EhopeRights.UPD_BILL_MASTER_ITEM + "')")
	public void saveBillTestItems(BillMasterItem billMasterItem) {
		List<BillTestItem> billTestItemList = billMasterItem.getBillTestItemList();
		List<BillTestItem> oldBillTestItemList = billTestItemService.getByBillMaster(billMasterItem);
		oldBillTestItemList.forEach(oldBillTestItem ->
			{
				if (!billTestItemList.contains(oldBillTestItem)) {
					billTestItemService.deleteBillTestItem(oldBillTestItem.getRid());
				}
			});
		billTestItemList.forEach(billTestItem ->
			{
				if (!oldBillTestItemList.contains(billTestItem)) {
					billTestItem.setBillMasterItem(billMasterItem);
					billTestItemService.addBillTestItem(billTestItem);
				}
			});
	}

	public void importBillMasterItems() {
		List<TestDefinition> tests = testDefinitionService.find(new ArrayList<SearchCriterion>(), TestDefinition.class, "section");

		List<SearchCriterion> lkpFilters = new ArrayList<SearchCriterion>();
		SearchCriterion filterByLkpCode = new SearchCriterion();
		filterByLkpCode.setField("code");
		filterByLkpCode.setValue(BillMasterItemType.TEST.toString());
		filterByLkpCode.setOperator(FilterOperator.eq);
		lkpFilters.add(filterByLkpCode);
		LkpBillItemType testType = lkpService.findOneAnyLkp(lkpFilters, LkpBillItemType.class);
		for (TestDefinition test : tests) {
			BillMasterItem masterItem = repo.getByCptCode(test.getCptCode());
			if (masterItem == null) {
				List<SearchCriterion> classificationFilters = new ArrayList<SearchCriterion>();
				SearchCriterion filterByCode = new SearchCriterion();
				filterByCode.setField("code");
				filterByCode.setValue(test.getSection().getName().get("en_us"));
				filterByCode.setOperator(FilterOperator.eq);
				classificationFilters.add(filterByCode);
				BillClassification billClassification = billClassificationService.findOne(classificationFilters, BillClassification.class);
				masterItem = new BillMasterItem();
				masterItem.setBillClassification(billClassification);
				masterItem.setCptCode(test.getCptCode());
				masterItem.setCode(test.getCptCode());
				masterItem.setIsActive(true);
				masterItem.setType(testType);
				masterItem = repo.save(masterItem);
			}
			BillTestItem billTestItem = new BillTestItem();
			billTestItem.setBillMasterItem(masterItem);
			billTestItem.setTestDefinition(test);
			billTestItemService.addBillTestItem(billTestItem);
		}
	}

	public void importBillMasterItemPricings() {
		Random rand = new Random();
		BillPriceList defaultPriceList = billPriceListService.getDefaultNoAuth();

		List<BillMasterItem> masterItems = repo.findAll();
		for (BillMasterItem masterItem : masterItems) {
			BillPricing billPricing = new BillPricing();
			billPricing.setBillMasterItem(masterItem);
			billPricing.setBillPriceList(defaultPriceList);
			billPricing.setPrice(BigDecimal.valueOf(rand.nextInt(10) + 1));
			billPricing.setStartDate(new Timestamp(System.currentTimeMillis()));
			billPricingService.addBillPricing(billPricing);
		}
	}

	/**
	 * For insurnace provider page
	 * 
	 * @param searchValue
	 * @return
	 */
	//@PreAuthorize("hasAuthority('" + EhopeRights.VIEW_BILL_MASTER_ITEM + "')")
	public List<BillMasterItem> filterBillMasterItemList(String searchValue) {
		if (StringUtils.isEmpty(searchValue)) {
			return getRepository().find(new ArrayList<>(), BillMasterItem.class);
		}
		return getRepository().find(Arrays.asList(new SearchCriterion("code", searchValue, FilterOperator.contains)), BillMasterItem.class);
	}

	/**
	 * Used in pricing tests
	 * 
	 * @param billMasterItemList
	 * @param billPriceLists
	 * @return
	 */
	public List<BillMasterItem> getByMasterItemAndPriceList(List<BillMasterItem> billMasterItemList, List<BillPriceList> billPriceLists) {
		return getRepository().getByMasterItemAndPriceList(billMasterItemList, billPriceLists, DateUtil.getCurrentDateWithoutTime());
	}

	/**
	 * Used in pricing tests
	 * 
	 * @param billMasterItemList
	 * @param billPriceLists
	 * @return
	 */
	public List<BillMasterItem> getByMasterItemAndNotPriceList(List<BillMasterItem> billMasterItemList,
			List<BillPriceList> billPriceLists) {
		return getRepository().getByMasterItemAndNotPriceList(billMasterItemList, billPriceLists);
	}
}

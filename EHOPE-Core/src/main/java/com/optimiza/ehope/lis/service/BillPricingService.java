package com.optimiza.ehope.lis.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import com.optimiza.core.admin.service.SecUserService;
import com.optimiza.core.base.helper.SearchCriterion;
import com.optimiza.core.base.helper.SearchCriterion.FilterOperator;
import com.optimiza.core.base.service.GenericService;
import com.optimiza.core.common.business.exception.BusinessException;
import com.optimiza.core.common.business.exception.BusinessException.ErrorSeverity;
import com.optimiza.core.common.helper.FilterablePageRequest;
import com.optimiza.core.common.helper.FilterablePageRequest.OrderObject;
import com.optimiza.core.common.util.CollectionUtil;
import com.optimiza.core.common.util.DateUtil;
import com.optimiza.core.common.util.SecurityUtil;
import com.optimiza.core.common.util.StringUtil;
import com.optimiza.ehope.lis.helper.EhopeRights;
import com.optimiza.ehope.lis.model.BillPriceList;
import com.optimiza.ehope.lis.model.BillPricing;
import com.optimiza.ehope.lis.model.BillTestItem;
import com.optimiza.ehope.lis.model.TestDefinition;
import com.optimiza.ehope.lis.repo.BillPricingRepo;

@Service("BillPricingService")
public class BillPricingService extends GenericService<BillPricing, BillPricingRepo> {

	@Autowired
	private BillPricingRepo repo;

	@Autowired
	private BillTestItemService testItemService;
	@Autowired
	private BillPatientTransactionService patientTransactionService;
	@Autowired
	private SecUserService userService;

	@Override
	protected BillPricingRepo getRepository() {
		return repo;
	}

	@PreAuthorize("hasAuthority('" + EhopeRights.VIEW_BILL_PRICING + "')")
	public Page<BillPricing> getBillPricingList(FilterablePageRequest filterablePageRequest) {
		List<OrderObject> sortList = filterablePageRequest.getSortList();
		OrderObject sortByRid = new OrderObject();
		sortByRid.setDirection(Direction.ASC);
		sortByRid.setProperty("rid");
		sortList.add(sortByRid);
		filterablePageRequest.setSortList(sortList);
		Page<BillPricing> all = repo.find(filterablePageRequest.getFilters(), filterablePageRequest.getPageRequest(),
				BillPricing.class, "billMasterItem", "billPriceList");
		return all;
	}

	@PreAuthorize("hasAuthority('" + EhopeRights.ADD_BILL_PRICING + "')")
	public BillPricing addBillPricing(BillPricing billPricing) {
		checkBillPricingValidity(billPricing, fetchOldPricingsExceptSelf(billPricing));
		return repo.save(billPricing);
	}

	@PreAuthorize("hasAuthority('" + EhopeRights.ADD_BILL_PRICING + "')")
	public List<BillPricing> saveBillPricings(List<BillPricing> billPricings) {
		for (int i = 0; i < billPricings.size(); i++) {
			BillPricing billPricing = billPricings.get(i);
			List<BillPricing> tempPricings = new ArrayList<BillPricing>(billPricings);
			tempPricings.remove(i);
			checkBillPricingValidity(billPricing, tempPricings);
		}
		return repo.save(billPricings);
	}

	@PreAuthorize("hasAuthority('" + EhopeRights.UPD_BILL_PRICING + "')")
	public BillPricing editBillPricing(BillPricing billPricing) {
		checkBillPricingValidity(billPricing, fetchOldPricingsExceptSelf(billPricing));
		return repo.save(billPricing);
	}

	private List<BillPricing> fetchOldPricingsExceptSelf(BillPricing billPricing) {
		List<SearchCriterion> filters = new ArrayList<SearchCriterion>();
		SearchCriterion filterByMasterItem = new SearchCriterion("billMasterItem", billPricing.getBillMasterItem(), FilterOperator.eq);
		SearchCriterion filterByPriceList = new SearchCriterion("billPriceList", billPricing.getBillPriceList(), FilterOperator.eq);
		if (billPricing.getRid() != null) {
			filters.add(new SearchCriterion("rid", billPricing.getRid(), FilterOperator.neq));
		}
		filters.add(filterByMasterItem);
		filters.add(filterByPriceList);
		return repo.find(filters, BillPricing.class, "billMasterItem", "billPriceList");
	}

	public List<BillPricing> getPricingsByPriceList(Long priceListRid) {
		return getRepository().find(Arrays.asList(new SearchCriterion("billPriceList.rid", priceListRid, FilterOperator.eq)),
				BillPricing.class,
				"billPriceList");
	}

	//This accepts an item and a list that doesn't contain the first argument in it
	private void checkBillPricingValidity(BillPricing billPricing, List<BillPricing> otherBillPricings) {
		if (billPricing.getEndDate() != null) {
			if (DateUtil.isBefore(billPricing.getEndDate(), billPricing.getStartDate())) {
				throw new BusinessException("End-date is before start-date", "endDateBeforeStartDate", ErrorSeverity.ERROR);
			}
			if (DateUtil.isBefore(billPricing.getEndDate(), DateUtil.getCurrentDateWithoutTime())) {
				throw new BusinessException("BillPricing is expired!", "billPricingExpired", ErrorSeverity.ERROR);
			}
		}

		otherBillPricings.forEach(otherBillPricing ->
			{
				if (billPricing.getBillPriceList().equals(otherBillPricing.getBillPriceList())
						&& DateUtil.isIntersected(billPricing.getStartDate(), billPricing.getEndDate(),
								otherBillPricing.getStartDate(), otherBillPricing.getEndDate())) {
					throw new BusinessException("Active period intersects with another billPricing!", "billPricingActivePeriodIntersection",
							ErrorSeverity.ERROR);
				}
			});
	}

	/**
	 * Is pricing within this current period.
	 * 
	 * @param pricing
	 * @return
	 */
	public boolean isPricingActive(BillPricing pricing) {
		Date currentDate = DateUtil.getCurrentDateWithoutTime();
		if (pricing.getEndDate() != null && DateUtil.isBetween(currentDate, pricing.getStartDate(), pricing.getEndDate())) {
			return true;
		} else if (pricing.getEndDate() == null && DateUtil.isAfterOrEqual(currentDate, pricing.getStartDate())) {
			return true;
		}
		return false;
	}

	/**
	 * Get the active price for this test.
	 * 
	 * @param testDefinition
	 * @param priceList
	 * @return price
	 */
	public BigDecimal getTestPrice(TestDefinition testDefinition, BillPriceList priceList) {
		Set<TestDefinition> tests = new HashSet<>();
		tests.add(testDefinition);
		List<BillTestItem> billTestItemList = testItemService.getByTestDefinitions(tests);
		patientTransactionService.areTestsWithoutBill(tests, billTestItemList);
		List<Long> billMasterItems = billTestItemList.stream().map(bti -> bti.getBillMasterItem().getRid()).collect(Collectors.toList());
		List<SearchCriterion> filters = new ArrayList<>();
		filters.add(new SearchCriterion("billMasterItem.rid", billMasterItems, FilterOperator.in));
		filters.add(new SearchCriterion("billPriceList.rid", priceList.getRid(), FilterOperator.eq));
		Set<BillPricing> pricings = new HashSet<>(getRepository().find(filters, BillPricing.class, "billMasterItem", "billPriceList"));
		pricings.removeIf(bp -> !isPricingActive(bp));
		if (CollectionUtil.isCollectionEmpty(pricings)) {
			String userLocale = userService.getUserLocale(SecurityUtil.getCurrentUser().getRid());
			String priceListName = StringUtil.isEmpty(priceList.getName().get(userLocale))
					? priceList.getName().entrySet().iterator().next().getValue()
					: priceList.getName().get(userLocale);
			throw new BusinessException(
					"Test " + testDefinition.getStandardCode() + " doesn't have a pricing on pricelist: " + priceList,
					"testNoPricing", ErrorSeverity.ERROR, Arrays.asList(testDefinition.getStandardCode(), priceListName));
		}
		BigDecimal price = pricings.stream().filter(bp -> isPricingActive(bp)).map(BillPricing::getPrice).reduce(BigDecimal.ZERO,
				BigDecimal::add);
		return price;
	}

}

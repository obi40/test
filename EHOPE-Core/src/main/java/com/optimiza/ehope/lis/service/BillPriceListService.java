package com.optimiza.ehope.lis.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import com.optimiza.core.base.helper.SearchCriterion;
import com.optimiza.core.base.helper.SearchCriterion.FilterOperator;
import com.optimiza.core.base.service.GenericService;
import com.optimiza.core.common.business.exception.BusinessException;
import com.optimiza.core.common.business.exception.BusinessException.ErrorSeverity;
import com.optimiza.ehope.lis.helper.EhopeRights;
import com.optimiza.ehope.lis.model.BillMasterItem;
import com.optimiza.ehope.lis.model.BillPriceList;
import com.optimiza.ehope.lis.model.BillPricing;
import com.optimiza.ehope.lis.repo.BillPriceListRepo;

@Service("BillPriceListService")
public class BillPriceListService extends GenericService<BillPriceList, BillPriceListRepo> {

	@Autowired
	private BillPriceListRepo repo;
	@Autowired
	private BillMasterItemService masterItemService;
	@Autowired
	private BillPricingService pricingService;

	@Override
	protected BillPriceListRepo getRepository() {
		return repo;
	}

	@PreAuthorize("hasAuthority('" + EhopeRights.ADD_BILL_PRICE_LIST + "')")
	public BillPriceList addBillPriceList(BillPriceList billPriceList) {
		triggerIsDefault(billPriceList);
		BillPriceList newBillPriceList = repo.save(billPriceList);
		return newBillPriceList;
	}

	@PreAuthorize("hasAuthority('" + EhopeRights.UPD_BILL_PRICE_LIST + "')")
	public BillPriceList editBillPriceList(BillPriceList billPriceList) {
		triggerIsDefault(billPriceList);
		return repo.save(billPriceList);
	}

	@PreAuthorize("hasAuthority('" + EhopeRights.DEL_BILL_PRICE_LIST + "')")
	public void deleteBillPriceList(BillPriceList billPriceList) {
		repo.delete(billPriceList);
	}

	@PreAuthorize("hasAuthority('" + EhopeRights.VIEW_BILL_PRICE_LIST + "')")
	public List<BillPriceList> findAllBillPriceList() {
		return getRepository().find(new ArrayList<>(), BillPriceList.class);
	}

	/**
	 * If this bill price list has isDefault = true , then make all other records false
	 * 
	 * @param billPriceList
	 */
	public void triggerIsDefault(BillPriceList priceList) {
		if (priceList.getIsDefault() == Boolean.TRUE) {
			BillPriceList defaultPriceList = getDefaultNoAuth();
			//incase if it is first price list
			if (defaultPriceList == null) {
				return;
			}
			hasPricingsInPriceList(priceList);
			defaultPriceList.setIsDefault(Boolean.FALSE);
			repo.save(defaultPriceList);
		}
	}

	/**
	 * Does this price list has an active pricing for all master items?
	 * 
	 * @param priceList
	 */
	private void hasPricingsInPriceList(BillPriceList priceList) {
		Set<BillMasterItem> masterItems = new HashSet<>(
				masterItemService.find(new ArrayList<>(), BillMasterItem.class, "billPricings.billPriceList"));
		StringBuilder sb = new StringBuilder();
		for (BillMasterItem bmi : masterItems) {
			boolean noActivePricing = true;
			for (BillPricing bp : bmi.getBillPricings()) {
				if (bp.getBillPriceList().equals(priceList) && pricingService.isPricingActive(bp)) {
					noActivePricing = false;
					break;
				}
			}
			if (noActivePricing) {
				sb.append(bmi.getCode() + ", ");
			}
		}
		if (sb.length() > 0) {
			sb.deleteCharAt(sb.length() - 1);
			throw new BusinessException(
					"Cant change default price list because some master items do not have active pricing or any pricing at all:"
							+ sb.toString(),
					"masterItemNoPricing",
					ErrorSeverity.ERROR, Arrays.asList(sb.toString()));
		}
	}

	@PreAuthorize("hasAuthority('" + EhopeRights.VIEW_BILL_PRICE_LIST + "')")
	public BillPriceList getDefault() {
		return repo.findOne(Arrays.asList(new SearchCriterion("isDefault", true, FilterOperator.eq)), BillPriceList.class);
	}

	public BillPriceList getDefaultNoAuth() {
		return getDefault();
	}

}

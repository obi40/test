package com.optimiza.ehope.lis.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.domain.Sort.Order;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.optimiza.core.base.helper.SearchCriterion;
import com.optimiza.core.base.helper.SearchCriterion.FilterOperator;
import com.optimiza.core.base.service.GenericService;
import com.optimiza.core.common.annotation.InterceptorFree;
import com.optimiza.core.common.business.exception.BusinessException;
import com.optimiza.core.common.business.exception.BusinessException.ErrorSeverity;
import com.optimiza.core.common.helper.FilterablePageRequest;
import com.optimiza.ehope.lis.helper.EhopeRights;
import com.optimiza.ehope.lis.model.BillClassification;
import com.optimiza.ehope.lis.model.LabSection;
import com.optimiza.ehope.lis.repo.BillClassificationRepo;

@Service("BillClassificationService")
public class BillClassificationService extends GenericService<BillClassification, BillClassificationRepo> {

	@Autowired
	private BillClassificationRepo repo;

	@Autowired
	private LabSectionService sectionService;

	@Override
	protected BillClassificationRepo getRepository() {
		return repo;
	}

	@PreAuthorize("hasAuthority('" + EhopeRights.VIEW_BILL_CLASSIFICATION + "')")
	public List<BillClassification> getParentBillClassifications(Long rid) {
		List<SearchCriterion> filters = new ArrayList<SearchCriterion>();
		SearchCriterion parentFilter = new SearchCriterion();
		parentFilter.setField("parentClassification");
		parentFilter.setOperator(FilterOperator.isnull);
		filters.add(parentFilter);
		if (rid != null) {
			SearchCriterion ridFilter = new SearchCriterion();
			ridFilter.setField("rid");
			ridFilter.setOperator(FilterOperator.neq);
			ridFilter.setValue(rid);
			filters.add(ridFilter);
		}
		List<Order> sortList = new ArrayList<Order>();
		Order sortByRid = new Order(Direction.ASC, "rid");
		sortList.add(sortByRid);
		Sort sort = new Sort(sortList);

		return repo.find(filters, BillClassification.class, sort, "parentClassification");
	}

	@PreAuthorize("hasAuthority('" + EhopeRights.VIEW_BILL_CLASSIFICATION + "')")
	public List<BillClassification> getBillClassificationList(FilterablePageRequest filterablePageRequest) {
		Sort sort = filterablePageRequest.getSortObject();

		List<BillClassification> all = repo.find(filterablePageRequest.getFilters(),
				BillClassification.class, sort, "parentClassification");
		List<BillClassification> newAll = new ArrayList<BillClassification>(all);
		all.forEach(billClassification ->
			{
				if (billClassification.getParentClassification() != null
						&& !newAll.contains(billClassification.getParentClassification())) {
					newAll.add(billClassification.getParentClassification());
				}
			});
		return newAll;
	}

	@InterceptorFree
	public List<BillClassification> getClassificationsExcluded(List<SearchCriterion> filters, Sort sort, String... joins) {
		return getRepository().find(filters, BillClassification.class, sort, joins);
	}

	@PreAuthorize("hasAuthority('" + EhopeRights.ADD_BILL_CLASSIFICATION + "')")
	public BillClassification addBillClassification(BillClassification billClassification) {
		checkValidity(billClassification);
		return repo.save(billClassification);
	}

	@PreAuthorize("hasAuthority('" + EhopeRights.UPD_BILL_CLASSIFICATION + "')")
	public BillClassification editBillClassification(BillClassification billClassification) {
		checkValidity(billClassification);
		return repo.save(billClassification);
	}

	private void checkValidity(BillClassification billClassification) {
		if (billClassification.getParentClassification() == null) {
			return;
		}
		BillClassification parentWithParent = repo.getByRidWithParent(billClassification.getParentClassification().getRid());
		if (parentWithParent.getParentClassification() != null) {
			throw new BusinessException("Only 2 levels of Billing Classification is allowed!", "billClassificationLevelsExceeded",
					ErrorSeverity.ERROR);
		}
	}

	@PreAuthorize("hasAuthority('" + EhopeRights.DEACTIVATE_BILL_CLASSIFICATION + "')")
	public BillClassification deactivateBillClassification(Long rid) throws BusinessException {
		BillClassification fetchedBillClassification = repo.getOne(rid);
		if (!fetchedBillClassification.getIsActive()) {
			throw new BusinessException("This billing classification is already inactive!", "billingClassificationAlreadyInactive",
					ErrorSeverity.ERROR);
		}

		fetchedBillClassification.setIsActive(false);

		return repo.save(fetchedBillClassification);
	}

	@PreAuthorize("hasAuthority('" + EhopeRights.ACTIVATE_BILL_CLASSIFICATION + "')")
	public BillClassification activateBillClassification(Long rid) throws BusinessException {
		BillClassification fetchedBillClassification = repo.getOne(rid);
		if (fetchedBillClassification.getIsActive()) {
			throw new BusinessException("This billing classification is already active!", "billingClassificationAlreadyActive",
					ErrorSeverity.ERROR);
		}

		fetchedBillClassification.setIsActive(true);

		return repo.save(fetchedBillClassification);
	}

	public void importClassifications() {
		BillClassification parentLabBillClassification = new BillClassification();
		parentLabBillClassification.setCode("lab");
		parentLabBillClassification.setName("lab");
		parentLabBillClassification.setIsActive(true);
		parentLabBillClassification = repo.save(parentLabBillClassification);
		List<LabSection> allSections = sectionService.findAll();
		for (LabSection section : allSections) {
			BillClassification billClass = new BillClassification();
			billClass.setCode(section.getName().get("en_us"));
			billClass.setName(section.getName().get("en_us"));
			billClass.setIsActive(true);
			billClass.setParentClassification(parentLabBillClassification);
			repo.save(billClass);
		}
	}

	public void connectClassificationToSection() {
		List<LabSection> allSections = sectionService.findAll();
		for (LabSection section : allSections) {
			String sectionName = section.getName().get("en_us");
			BillClassification classification = repo.findOne(
					Arrays.asList(new SearchCriterion("code", sectionName, FilterOperator.eq)),
					BillClassification.class);
			if (classification != null) {
				classification.setSection(section);
				repo.save(classification);
			}
		}
	}

	/**
	 * For insurnace provider page
	 * 
	 * @param searchValue
	 * @return
	 */
	//@PreAuthorize("hasAuthority('" + EhopeRights.VIEW_BILL_CLASSIFICATION + "')")
	public List<BillClassification> filterBillClassificationList(String searchValue) {
		if (StringUtils.isEmpty(searchValue)) {
			return getRepository().find(new ArrayList<>(), BillClassification.class);
		}
		return getRepository().find(Arrays.asList(new SearchCriterion("code", searchValue, FilterOperator.contains)),
				BillClassification.class);
	}

}

package com.optimiza.ehope.lis.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import com.optimiza.core.base.helper.SearchCriterion;
import com.optimiza.core.base.helper.SearchCriterion.FilterOperator;
import com.optimiza.core.base.service.GenericService;
import com.optimiza.core.common.business.exception.BusinessException;
import com.optimiza.core.common.business.exception.BusinessException.ErrorSeverity;
import com.optimiza.core.common.helper.FilterablePageRequest;
import com.optimiza.core.common.util.CollectionUtil;
import com.optimiza.ehope.lis.helper.EhopeRights;
import com.optimiza.ehope.lis.model.BillPriceList;
import com.optimiza.ehope.lis.model.BillPricing;
import com.optimiza.ehope.lis.model.BillTestItem;
import com.optimiza.ehope.lis.model.TestDefinition;
import com.optimiza.ehope.lis.model.TestGroup;
import com.optimiza.ehope.lis.model.TestGroupDefinition;
import com.optimiza.ehope.lis.model.TestGroupDetail;
import com.optimiza.ehope.lis.repo.TestGroupRepo;
import com.optimiza.ehope.lis.util.NumberUtil;

/**
 * TestGroupService.java
 * 
 * @author Abdullah Imran <aImran@optimizasolutions.com>
 * @since Dec/11/2018
 **/

@Service("TestGroupService")
public class TestGroupService extends GenericService<TestGroup, TestGroupRepo> {

	@Autowired
	private TestGroupRepo testGroupRepo;
	@Autowired
	private TestGroupDefinitionService groupDefinitionService;
	@Autowired
	private TestGroupDetailService groupDetailService;
	@Autowired
	private TestDefinitionService testDefinitionService;
	@Autowired
	private BillPriceListService priceListService;
	@Autowired
	private EmrVisitGroupService visitGroupService;
	@Autowired
	private EntityManager entityManager;

	@PreAuthorize("hasAuthority('" + EhopeRights.VIEW_TEST_GROUP + "')")
	public Page<TestGroup> getTestGroupsPage(FilterablePageRequest filterablePageRequest) {
		Page<TestGroup> groupsPage = getRepository().find(filterablePageRequest.getFilters(), filterablePageRequest.getPageRequest(),
				TestGroup.class);
		if (groupsPage.getNumberOfElements() == 0) {
			return groupsPage;
		}
		List<TestGroup> fetchedGroups = getRepository()	.find(Arrays.asList(new SearchCriterion("rid",
				groupsPage.getContent().stream().map(TestGroup::getRid).collect(Collectors.toList()), FilterOperator.in)), TestGroup.class,
				filterablePageRequest.getSortObject(), "groupDefinitions.testDefinition", "groupDetails.priceList").stream().distinct()
														.collect(Collectors.toList());
		List<TestDefinition> tests = testDefinitionService.getAllTestsPricings(Arrays.asList(new SearchCriterion("rid",
				fetchedGroups	.stream().flatMap(tg -> tg.getGroupDefinitions().stream()).map(tgd -> tgd.getTestDefinition().getRid())
								.distinct().collect(Collectors.toList()),
				FilterOperator.in)), null);
		for (TestDefinition td : tests) {
			for (TestGroup tg : fetchedGroups) {
				for (TestGroupDefinition tgd : tg.getGroupDefinitions()) {
					if (tgd.getTestDefinition().equals(td)) {
						tgd.setTestDefinition(td);
						break;
					}
				}
			}
		}

		Page<TestGroup> page = new PageImpl<>(fetchedGroups, filterablePageRequest.getPageRequest(), groupsPage.getTotalElements());
		return page;
	}

	/**
	 * Get the test Group total price by the group and the price list of the group detail
	 * 
	 * @param testGroupRid
	 * @param priceList
	 * 
	 * @return total
	 */
	public BigDecimal getTestGroupPrice(Long testGroupRid, Long priceList) {
		BillPriceList defaultPriceList = priceListService.getDefault();
		List<Long> testsRid = getRepository()	.findOne(SearchCriterion.generateRidFilter(testGroupRid, FilterOperator.eq), TestGroup.class,
				"groupDefinitions.testDefinition").getGroupDefinitions().stream().map(tgd -> tgd.getTestDefinition().getRid())
												.collect(Collectors.toList());
		List<TestDefinition> tests = testDefinitionService.getAllTestsPricings(
				Arrays.asList(new SearchCriterion("rid", testsRid, FilterOperator.in)), null);
		return caluclateGroupTotalPrice(tests, defaultPriceList.getRid(), priceList);
	}

	/**
	 * Calculates the sum of this group by the tests and the group detail price list pricings.
	 * If a test does not have a price on that price list then it will use the default.
	 * 
	 * @param tests
	 * @param defaultPriceList
	 * @param priceList
	 * @return total
	 */
	private BigDecimal caluclateGroupTotalPrice(List<TestDefinition> tests, Long defaultPriceList, Long priceList) {
		BigDecimal totalGroupPrice = BigDecimal.ZERO;
		for (TestDefinition td : tests) {
			for (BillTestItem bti : td.getBillTestItems()) {
				BillPricing defaultPricing = null;
				BigDecimal price = null;
				for (BillPricing bp : bti.getBillMasterItem().getBillPricings()) {
					if (bp.getBillPriceList().getRid().equals(defaultPriceList)) {
						defaultPricing = bp;
					} else if (bp.getBillPriceList().getRid().equals(priceList)) {
						price = bp.getPrice();
						break;
					}
				}
				if (price == null) {
					price = defaultPricing.getPrice();
				}
				totalGroupPrice = totalGroupPrice.add(price);
			}
		}

		return totalGroupPrice;
	}

	public Set<TestGroup> getTestGroups() {
		return new HashSet<>(getRepository().find(new ArrayList<>(), TestGroup.class, "groupDefinitions.testDefinition"));
	}

	public Set<TestGroup> getTestGroupsWithDestinations() {

		return new HashSet<>(
				getRepository().find(Arrays.asList(new SearchCriterion("isActive", Boolean.TRUE, FilterOperator.eq)), TestGroup.class,
						"groupDefinitions.testDefinition.destinations.source.insuranceBranch"));
	}

	/**
	 * Business validation
	 * 
	 * @param testGroup
	 */
	private void validations(TestGroup testGroup) {
		if (CollectionUtil.isCollectionEmpty(testGroup.getGroupDefinitions())) {
			throw new BusinessException("Package must have atleast one or more tests", "packageTestEmpty", ErrorSeverity.ERROR);
		}

		BillPriceList defaultPriceList = priceListService.getDefault();

		//if it is a profile it must
		//have a details
		//a detail with default price list
		//no duplicate details with same price list
		//		if (testGroup.getIsProfile()) {
		//			if (CollectionUtil.isCollectionEmpty(testGroup.getGroupDetails())) {
		//				throw new BusinessException("Package must have atleast one or more tests", "packageDetailEmpty", ErrorSeverity.ERROR);
		//			}
		//			Boolean hasDefaultDetail = Boolean.FALSE;
		//			for (TestGroupDetail tgd : testGroup.getGroupDetails()) {
		//				for (TestGroupDetail otherTgd : testGroup.getGroupDetails()) {
		//					if (!tgd.equals(otherTgd) && tgd.getPriceList().equals(otherTgd.getPriceList())) {
		//						throw new BusinessException("Cant have details with same price list:" + tgd.getRid() + "," + otherTgd.getRid(),
		//								"packageDuplicateDetails", ErrorSeverity.ERROR);
		//					}
		//				}
		//				if (tgd.getPriceList().equals(defaultPriceList)) {
		//					hasDefaultDetail = Boolean.TRUE;
		//				}
		//			}
		//			if (!hasDefaultDetail) {
		//				throw new BusinessException("Must have a detail with a default price list", "packageDefaultDetail",
		//						ErrorSeverity.ERROR);
		//			}
		//		}
		List<Long> testsRid = testGroup	.getGroupDefinitions().stream().map(tgd -> tgd.getTestDefinition().getRid())
										.sorted((o1, o2) -> o1.compareTo(o2)).collect(Collectors.toList());
		List<SearchCriterion> filters = testGroup.getRid() == null ? new ArrayList<>()
				: SearchCriterion.generateRidFilter(testGroup.getRid(), FilterOperator.neq);
		Set<TestGroup> groups = new HashSet<>(getRepository().find(filters, TestGroup.class, "groupDefinitions.testDefinition"));
		//check if there is any other group has the same tests
		for (TestGroup tg : groups) {
			if (tg.getGroupDefinitions().size() != testGroup.getGroupDefinitions().size()
					|| !testGroup.getIsProfile().equals(tg.getIsProfile())) {
				continue;
			}
			List<Long> existedGroupTestsRid = tg.getGroupDefinitions().stream().map(tgd -> tgd.getTestDefinition().getRid())
												.sorted((o1, o2) -> o1.compareTo(o2)).collect(Collectors.toList());
			if (testsRid.equals(existedGroupTestsRid)) {
				throw new BusinessException("Cant have two groups with same tests", "groupDuplicated", ErrorSeverity.ERROR,
						Arrays.asList(tg.getName()));
			}

		}
		//Validate TestGroup discounts
		List<TestDefinition> tests = testDefinitionService.getAllTestsPricings(
				Arrays.asList(new SearchCriterion("rid", testsRid, FilterOperator.in)), null);
		if (!testGroup.getIsProfile()) {
			if ((testGroup.getDiscountAmount() == null && testGroup.getDiscountPercentage() == null) ||
					(testGroup.getDiscountAmount() != null && testGroup.getDiscountPercentage() != null)) {
				throw new BusinessException("Must have neither Percentage or Amount", "cantMultipleDiscounts", ErrorSeverity.ERROR);
			}
			if (testGroup.getDiscountAmount() != null) {
				testGroup.setDiscountAmount(
						testGroup.getDiscountAmount().compareTo(BigDecimal.ZERO) == -1 ? BigDecimal.ZERO : testGroup.getDiscountAmount());
			} else {
				testGroup.setDiscountPercentage(NumberUtil.validatePercentage(testGroup.getDiscountPercentage()));
			}
			BigDecimal totalDefaultPrice = caluclateGroupTotalPrice(tests, defaultPriceList.getRid(), defaultPriceList.getRid());
			if (testGroup.getDiscountAmount() != null && testGroup.getDiscountAmount().compareTo(totalDefaultPrice) == 1) {
				throw new BusinessException("Discount amount cant exceed the group total price", "discountExceedTotal",
						ErrorSeverity.ERROR);
			}
		}
	}

	@PreAuthorize("hasAuthority('" + EhopeRights.ADD_TEST_GROUP + "')")
	public TestGroup createTestGroup(TestGroup testGroup) {
		validations(testGroup);
		Set<TestGroupDefinition> groupDefinitions = testGroup.getGroupDefinitions();
		Set<TestGroupDetail> groupDetails = testGroup.getGroupDetails();
		testGroup.setGroupDefinitions(new HashSet<>());
		testGroup.setGroupDetails(new HashSet<>());
		testGroup = getRepository().save(testGroup);
		if (!CollectionUtil.isCollectionEmpty(groupDefinitions)) {
			for (TestGroupDefinition tgd : groupDefinitions) {
				tgd.setRid(null);//remove dummy rid
				tgd.setTestGroup(testGroup);
			}
			testGroup.setGroupDefinitions(new HashSet<>(groupDefinitionService.createGroupDefinitions(groupDefinitions)));
		}
		if (!CollectionUtil.isCollectionEmpty(groupDetails)) {
			for (TestGroupDetail gd : groupDetails) {
				gd.setRid(null);//remove dummy rid
				gd.setGroup(testGroup);
			}
			testGroup.setGroupDetails(new HashSet<>(groupDetailService.createGroupDetails(groupDetails)));
		}
		return testGroup;

	}

	@PreAuthorize("hasAuthority('" + EhopeRights.UPD_TEST_GROUP + "')")
	public TestGroup updateTestGroup(TestGroup testGroup) {
		validations(testGroup);
		Set<TestGroupDefinition> groupDefinitions = testGroup.getGroupDefinitions();
		Set<TestGroupDetail> groupDetails = testGroup.getGroupDetails();

		testGroup.setGroupDefinitions(new HashSet<>());
		testGroup.setGroupDetails(new HashSet<>());
		testGroup = getRepository().save(testGroup);
		groupDefinitionService.deleteAllByTestGroup(testGroup);
		List<SearchCriterion> filters = groupDetails.stream().filter(tgd -> tgd.getRid() != null && tgd.getRid() >= 0)
													.map(tgd -> new SearchCriterion("rid", tgd.getRid(), FilterOperator.neq))
													.collect(Collectors.toList());
		//delete all details
		if (CollectionUtil.isCollectionEmpty(filters) || !testGroup.getIsProfile()) {
			groupDetailService.deleteAllByGroup(testGroup);
		} else {
			// get details that are not in groupDetails 
			filters.add(new SearchCriterion("group.rid", testGroup.getRid(), FilterOperator.eq));
			List<TestGroupDetail> toDeleteGroupDetails = groupDetailService.find(filters, TestGroupDetail.class, "group");
			groupDetailService.deleteGroupDetails(toDeleteGroupDetails);
		}
		entityManager.flush();

		if (!CollectionUtil.isCollectionEmpty(groupDefinitions)) {
			for (TestGroupDefinition tgd : groupDefinitions) {
				tgd.setRid(null);//remove dummy rid
				tgd.setTestGroup(testGroup);
			}
			testGroup.setGroupDefinitions(new HashSet<>(groupDefinitionService.updateGroupDefinitions(groupDefinitions)));
		}
		if (!CollectionUtil.isCollectionEmpty(groupDetails)) {
			for (TestGroupDetail gd : groupDetails) {
				if (gd.getRid() < 0) {//remove dummy rid only if it is a new one
					gd.setRid(null);
					gd.setGroup(testGroup);
				}
			}
			testGroup.setGroupDetails(new HashSet<>(groupDetailService.updateGroupDetails(groupDetails)));
		}
		return testGroup;

	}

	public TestGroup updateTestGroupNoAuth(TestGroup testGroup) {
		return getRepository().save(testGroup);

	}

	@PreAuthorize("hasAuthority('" + EhopeRights.DEL_TEST_GROUP + "')")
	public void deleteTestGroup(TestGroup testGroup) {
		groupDefinitionService.deleteAllByTestGroup(testGroup);
		groupDetailService.deleteAllByGroup(testGroup);
		visitGroupService.deleteAllByTestGroup(testGroup);
		getRepository().delete(testGroup);
	}

	@Override
	protected TestGroupRepo getRepository() {
		return this.testGroupRepo;
	}

}

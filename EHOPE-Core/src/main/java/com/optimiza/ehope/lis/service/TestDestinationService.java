package com.optimiza.ehope.lis.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.domain.Sort.Order;
import org.springframework.stereotype.Service;

import com.optimiza.core.admin.model.SecUser;
import com.optimiza.core.base.helper.SearchCriterion;
import com.optimiza.core.base.helper.SearchCriterion.FilterOperator;
import com.optimiza.core.base.service.GenericService;
import com.optimiza.core.common.business.exception.BusinessException;
import com.optimiza.core.common.business.exception.BusinessException.ErrorSeverity;
import com.optimiza.core.common.data.model.TransField;
import com.optimiza.core.common.util.SecurityUtil;
import com.optimiza.core.lkp.model.ComTenantLanguage;
import com.optimiza.core.lkp.service.ComTenantLanguageService;
import com.optimiza.core.lkp.service.LkpService;
import com.optimiza.ehope.lis.lkp.helper.ClientPurpose;
import com.optimiza.ehope.lis.lkp.helper.InsuranceType;
import com.optimiza.ehope.lis.lkp.helper.TestDestinationType;
import com.optimiza.ehope.lis.lkp.model.LkpTestDestinationType;
import com.optimiza.ehope.lis.model.BillPriceList;
import com.optimiza.ehope.lis.model.BillPricing;
import com.optimiza.ehope.lis.model.BillTestItem;
import com.optimiza.ehope.lis.model.InsProvider;
import com.optimiza.ehope.lis.model.LabTestActual;
import com.optimiza.ehope.lis.model.TestDefinition;
import com.optimiza.ehope.lis.model.TestDestination;
import com.optimiza.ehope.lis.model.Workbench;
import com.optimiza.ehope.lis.repo.TestDestinationRepo;

/**
 * TestDestinationService.java
 * 
 * @author Murad Poladian <mpoladian@optimizasolutions.com>
 * @since Sep/18/2018
 **/
@Service("TestDestinationService")
public class TestDestinationService extends GenericService<TestDestination, TestDestinationRepo> {

	@Autowired
	private WorkbenchService workbenchService;
	@Autowired
	private ComTenantLanguageService languageService;
	@Autowired
	private TestDefinitionService testDefinitionService;
	@Autowired
	private InsProviderService insProviderService;
	@Autowired
	private LkpService lkpService;
	@Autowired
	private LabTestActualService testActualService;
	@Autowired
	private TestDestinationRepo repo;

	public void createDestinationsForPriceLists() {
		//Step 0: Prepare prerequisites
		LkpTestDestinationType typeExternal = lkpService.findOneAnyLkp(
				Arrays.asList(new SearchCriterion("code", TestDestinationType.EXTERNAL.toString(), FilterOperator.eq)),
				LkpTestDestinationType.class);
		LkpTestDestinationType typeLocal = lkpService.findOneAnyLkp(
				Arrays.asList(new SearchCriterion("code", TestDestinationType.LOCAL.toString(), FilterOperator.eq)),
				LkpTestDestinationType.class);
		InsProvider selfClient = insProviderService.getClients(TestDestinationType.LOCAL, ClientPurpose.SOURCE, null).get(0);

		//Step 1: Loop over all test-definitions
		Set<TestDefinition> testDefinitions = new HashSet<TestDefinition>(testDefinitionService.find(
				Arrays.asList(), TestDefinition.class,
				"billTestItems.billMasterItem.billPricings.billPriceList"));
		for (TestDefinition testDefinition : testDefinitions) {
			System.out.println(testDefinition.getStandardCode());
			for (BillTestItem bti : testDefinition.getBillTestItems()) {
				for (BillPricing bp : bti.getBillMasterItem().getBillPricings()) {
					BillPriceList bpl = bp.getBillPriceList();
					String name = bpl.getName().get("en_us");
					System.out.println(name);
					switch (name) {
						case "MOH H 2008":
						case "MOH L 2008":
						case "MOH H 1995":
						case "MOH L 1995":
							break;
						default:
							InsProvider client = insProviderService.findOne(Arrays.asList(
									new SearchCriterion("priceList", bpl, FilterOperator.eq),
									new SearchCriterion("name", name, FilterOperator.contains)),
									InsProvider.class);
							if (client != null) {
								TestDestination testDestination = new TestDestination();
								testDestination.setSource(selfClient);
								testDestination.setTestDefinition(testDefinition);
								testDestination.setDestinationBranch(client);
								testDestination.setIsActive(Boolean.TRUE);
								String code = client.getCode();
								switch (code) {
									case "0":
									case "10":
									case "11":
									case "14":
										testDestination.setType(typeLocal);
										if (!testDestination.getSource().equals(testDestination.getDestinationBranch())) {
											repo.save(testDestination);
										}
										break;
									case "1":
									case "2":
									case "3":
									case "4":
									case "5":
									case "6":
									case "7":
									case "8":
									case "9":
									case "12":
									case "13":
									case "15":
									case "16":
									case "17":
										testDestination.setType(typeExternal);
										if (!testDestination.getSource().equals(testDestination.getDestinationBranch())) {
											repo.save(testDestination);
										}
										break;
								}
							}
							break;
					}
				}
			}
		}
	}

	public void createDummyDestinations() {
		//Step 0: Prepare prerequisites
		SecUser user = SecurityUtil.getCurrentUser();
		List<ComTenantLanguage> languages = languageService.findAll();
		LkpTestDestinationType typeWorkbench = lkpService.findOneAnyLkp(
				Arrays.asList(new SearchCriterion("code", TestDestinationType.WORKBENCH.toString(), FilterOperator.eq)),
				LkpTestDestinationType.class);
		TransField workbenchName = new TransField();
		for (ComTenantLanguage lang : languages) {
			workbenchName.put(lang.getComLanguage().getLocale(), "Default Workbench");
		}

		//Step 1: Delete all destinations for this branch
		repo.deleteAllInBatch();

		//Step 2: Delete all workbenches for this branch
		workbenchService.deleteAllInBatch();

		//Step 3: Create default workbench
		Workbench workbench = new Workbench();
		workbench.setName(workbenchName);
		workbench = workbenchService.createWorkbench(workbench);

		//Step 4: Find the source-client associated with this branch
		List<SearchCriterion> sourceFilters = new ArrayList<SearchCriterion>();
		InsProvider localTenant = insProviderService.findOne(
				Arrays.asList(
						new SearchCriterion("insuranceTenant.rid", user.getTenantId(), FilterOperator.eq),
						new SearchCriterion("insuranceType.code", InsuranceType.LAB_NETWORK.getValue(), FilterOperator.eq)),
				InsProvider.class);
		sourceFilters.add(new SearchCriterion("parentProvider.insuranceTenant", null, FilterOperator.isnotnull));
		sourceFilters.add(new SearchCriterion("insuranceType.code", InsuranceType.LAB_NETWORK.getValue(), FilterOperator.eq));
		sourceFilters.add(new SearchCriterion("parentProvider.rid", localTenant.getRid(), FilterOperator.eq));
		sourceFilters.add(new SearchCriterion("insuranceBranch.rid", user.getBranchId(), FilterOperator.eq));
		InsProvider source = insProviderService.findOne(sourceFilters, InsProvider.class);

		//Step 5: Loop over all test-definitions and create a destination for each
		List<TestDefinition> testDefinitions = testDefinitionService.findAll();
		for (TestDefinition testDefinition : testDefinitions) {
			TestDestination testDestination = new TestDestination();
			testDestination.setType(typeWorkbench);
			testDestination.setWorkbench(workbench);
			testDestination.setSource(source);
			testDestination.setTestDefinition(testDefinition);
			testDestination.setIsActive(Boolean.TRUE);
			repo.save(testDestination);
		}
	}

	public void saveTestDestinations(List<TestDestination> testDestinations, TestDefinition testDefinition,
			List<BillPriceList> priceLists) {
		Long branchRid = SecurityUtil.getCurrentUser().getBranchId();
		Set<String> connections = new HashSet<String>();
		for (TestDestination testDestination : testDestinations) {
			if (branchRid != null && testDestination.getSource().getInsuranceBranch().getRid() != branchRid) {
				throw new BusinessException("Source branch is not the same as the user branch!", "sourceBranchIsNotUserBranch",
						ErrorSeverity.ERROR);
			}
			testDestination.setTestDefinition(testDefinition);
			switch (TestDestinationType.valueOf(testDestination.getType().getCode())) {
				case ACCULAB:
				case EXTERNAL:
				case LOCAL:
					testDestination.setWorkbench(null);
					if (testDestination.getSource().equals(testDestination.getDestinationBranch())) {
						throw new BusinessException("Same source and destination!", "sameSourceAndDestination", ErrorSeverity.ERROR);
					}
					if (testDestination.getDestinationBranch() == null) {
						throw new BusinessException("Destination branch cannot be null!", "destinationCannotBeNull", ErrorSeverity.ERROR);
					}
					if (!connections.add(testDestination.getSource().getCode() + "|" + testDestination.getDestinationBranch().getCode())) {
						throw new BusinessException("Source & destination already exist!", "sourceAndDestinationAlreadyExist",
								ErrorSeverity.ERROR);
					}
					InsProvider destinationBranch = insProviderService.findOne(
							Arrays.asList(new SearchCriterion("rid", testDestination.getDestinationBranch().getRid(), FilterOperator.eq)),
							InsProvider.class, "priceList");
					if (!priceLists.contains(destinationBranch.getPriceList())) {
						throw new BusinessException("Destination must have a pricelist!", "destinationMustHaveAPriceList",
								ErrorSeverity.ERROR,
								Arrays.asList(
										testDestination.getType().getCode() + "|" + testDestination.getDestinationBranch().getCode(),
										testDefinition.getStandardCode()));
					}
					break;
				case WORKBENCH:
					testDestination.setDestinationBranch(null);
					if (!connections.add(testDestination.getSource().getCode() + "|" + testDestination.getWorkbench().getRid())) {
						throw new BusinessException("Source & destination already exist!", "sourceAndDestinationAlreadyExist",
								ErrorSeverity.ERROR);
					}
					if (testDestination.getWorkbench() == null) {
						throw new BusinessException("Workbench cannot be null!", "workbenchCannotBeNull", ErrorSeverity.ERROR);
					}
					if (!testDestination.getSource().getInsuranceBranch().getRid().equals(testDestination.getWorkbench().getBranchId())) {
						//Workbench and source should belong to same branch
						throw new BusinessException("Workbench does not belong to source!", "workbenchDoesNotBelongToSource",
								ErrorSeverity.ERROR);
					}
					break;
			}
		}
		repo.save(testDestinations);
	}

	public List<TestDestination> getTestDestinationsByTestDefs(List<Long> testDefinitions) {

		Long branchRid = SecurityUtil.getCurrentUser().getBranchId();
		List<SearchCriterion> filters = new ArrayList<>();
		filters.add(new SearchCriterion("testDefinition.rid", testDefinitions, FilterOperator.in));
		filters.add(new SearchCriterion("isActive", Boolean.TRUE, FilterOperator.eq));
		List<TestDestination> destinations = getRepository().find(filters, TestDestination.class,
				new Sort(new Order(Direction.ASC, "workbench")),
				"source.insuranceBranch", "workbench", "destinationBranch", "type", "testDefinition", "normalRanges");
		return destinations	.stream().filter(td -> td.getSource().getInsuranceBranch().getRid().equals(branchRid)).distinct()
							.collect(Collectors.toList());
	}

	public Map<String, Object> getDestinationEntryData(Long visitRid) {
		Set<LabTestActual> testActuals = testActualService.getTestActualsDestinations(visitRid);
		List<TestDestination> testDestinations = getTestDestinationsByTestDefs(
				testActuals.stream().map(lta -> lta.getTestDefinition().getRid()).collect(Collectors.toList()));
		Map<String, Object> map = new HashMap<>();
		map.put("testsActuals", testActuals);
		map.put("destinations", testDestinations);
		return map;
	}

	@Override
	protected TestDestinationRepo getRepository() {
		return repo;
	}

}

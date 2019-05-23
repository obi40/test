package com.optimiza.ehope.lis.service;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.optimiza.core.base.helper.SearchCriterion;
import com.optimiza.core.base.helper.SearchCriterion.FilterOperator;
import com.optimiza.core.base.service.GenericService;
import com.optimiza.core.common.business.exception.BusinessException;
import com.optimiza.core.common.business.exception.BusinessException.ErrorSeverity;
import com.optimiza.core.common.data.model.TransField;
import com.optimiza.core.common.helper.FilterablePageRequest;
import com.optimiza.core.common.util.DateUtil;
import com.optimiza.core.common.util.SecurityUtil;
import com.optimiza.core.common.util.StringUtil;
import com.optimiza.core.lkp.helper.FieldType;
import com.optimiza.core.lkp.model.ComTenantLanguage;
import com.optimiza.core.lkp.model.LkpGender;
import com.optimiza.core.lkp.service.ComTenantLanguageService;
import com.optimiza.core.lkp.service.LkpService;
import com.optimiza.ehope.lis.helper.BillMasterItemType;
import com.optimiza.ehope.lis.helper.EhopeRights;
import com.optimiza.ehope.lis.helper.ExcelColumn;
import com.optimiza.ehope.lis.helper.ExcelSheet;
import com.optimiza.ehope.lis.helper.TestEditability;
import com.optimiza.ehope.lis.lkp.helper.ClientPurpose;
import com.optimiza.ehope.lis.lkp.helper.ResultValueType;
import com.optimiza.ehope.lis.lkp.helper.SectionType;
import com.optimiza.ehope.lis.lkp.helper.TestDestinationType;
import com.optimiza.ehope.lis.lkp.helper.UnitType;
import com.optimiza.ehope.lis.lkp.model.LkpAgeUnit;
import com.optimiza.ehope.lis.lkp.model.LkpBillItemType;
import com.optimiza.ehope.lis.lkp.model.LkpContainerType;
import com.optimiza.ehope.lis.lkp.model.LkpResultValueType;
import com.optimiza.ehope.lis.lkp.model.LkpSpecimenType;
import com.optimiza.ehope.lis.lkp.model.LkpTestingMethod;
import com.optimiza.ehope.lis.lkp.model.LkpUnitType;
import com.optimiza.ehope.lis.model.BillClassification;
import com.optimiza.ehope.lis.model.BillMasterItem;
import com.optimiza.ehope.lis.model.BillPriceList;
import com.optimiza.ehope.lis.model.BillPricing;
import com.optimiza.ehope.lis.model.BillTestItem;
import com.optimiza.ehope.lis.model.ExtraTest;
import com.optimiza.ehope.lis.model.InsProvider;
import com.optimiza.ehope.lis.model.Interpretation;
import com.optimiza.ehope.lis.model.LabSection;
import com.optimiza.ehope.lis.model.LabUnit;
import com.optimiza.ehope.lis.model.TestCodedResult;
import com.optimiza.ehope.lis.model.TestCodedResultMapping;
import com.optimiza.ehope.lis.model.TestDefinition;
import com.optimiza.ehope.lis.model.TestDestination;
import com.optimiza.ehope.lis.model.TestDisclaimer;
import com.optimiza.ehope.lis.model.TestNormalRange;
import com.optimiza.ehope.lis.model.TestQuestion;
import com.optimiza.ehope.lis.model.TestQuestionOption;
import com.optimiza.ehope.lis.model.TestResult;
import com.optimiza.ehope.lis.model.TestSpecimen;
import com.optimiza.ehope.lis.repo.TestDefinitionRepo;
import com.optimiza.ehope.lis.util.ExcelUtil;
import com.optimiza.ehope.lis.util.NumberUtil;

@Service("TestDefinitionService")
public class TestDefinitionService extends GenericService<TestDefinition, TestDefinitionRepo> {

	@Autowired
	private TestDefinitionRepo repo;

	@Autowired
	private EntityManagerFactory entityManagerFactory;

	@Autowired
	private EntityManager entityManager;

	@Value("${system.batchSize}")
	public String batchSize;

	@Autowired
	private ComTenantLanguageService tenantLanguageService;

	@Autowired
	private LabTestActualService labTestActualService;

	@Autowired
	private ExtraTestService extraTestService;

	@Autowired
	private TestSpecimenService specimenService;

	@Autowired
	private TestQuestionService testQuestionService;

	@Autowired
	private TestQuestionOptionService testQuestionOptionService;

	@Autowired
	private TestResultService testResultService;

	@Autowired
	private BillTestItemService testItemService;

	@Autowired
	private BillMasterItemService masterItemService;

	@Autowired
	private BillClassificationService classificationService;

	@Autowired
	private BillPricingService pricingService;

	@Autowired
	private TestDestinationService destinationService;

	@Autowired
	private LabSectionService sectionService;

	@Autowired
	private BillPriceListService priceListService;

	@Autowired
	private LabUnitService labUnitService;

	@Autowired
	private TestNormalRangeService normalRangeService;

	@Autowired
	private TestCodedResultService codedResultService;

	@Autowired
	private TestCodedResultMappingService codedResultMappingService;

	@Autowired
	private LkpService lkpService;

	@Autowired
	private TestQuestionService questionService;

	@Autowired
	private TestGroupService testGroupService;

	@Autowired
	private InsProviderService insProviderService;

	@Autowired
	private InterpretationService intepretationService;

	@Autowired
	private BillPatientTransactionService patientTransactionService;

	@Autowired
	private TestDisclaimerService testDisclaimerService;

	@Override
	protected TestDefinitionRepo getRepository() {
		return repo;
	}

	public TestDefinition getQuickTestDefintion(Long testRid) {
		Long branchRid = SecurityUtil.getCurrentUser().getBranchId();
		List<SearchCriterion> filters = new ArrayList<SearchCriterion>();
		filters.add(new SearchCriterion("rid", testRid, FilterOperator.eq));
		TestDefinition fetchedTest = repo.findOne(filters, TestDefinition.class,
				"section.type",
				"specimenType",
				"lkpReportType",
				"billTestItems.billMasterItem.billPricings.billPriceList", "billTestItems.billMasterItem.type",
				"destinations.source.insuranceBranch", "destinations.type",
				"destinations.workbench", "destinations.destinationBranch",
				"interpretations",
				"allergyUnit",
				//				"extraTests",
				"testSpecimens.containerType", "testSpecimens.stabilityUnit", "testSpecimens.specimenTemperature",
				"testResults.primaryUnit", "testResults.secondaryUnit",
				"testResults.resultValueType", "testResults.primaryUnitType", "testResults.comprehensiveResult",
				"testResults.testCodedResultMappings.testCodedResult", "testResults.narrativeTemplates",
				"testResults.normalRanges.ageFromUnit", "testResults.normalRanges.ageToUnit",
				"testResults.normalRanges.codedResult", "testResults.normalRanges.sex", "testResults.normalRanges.testDestination",
				"testDisclaimerSet");

		if (branchRid != null && fetchedTest.getDestinations() != null) {
			fetchedTest.setDestinations(fetchedTest	.getDestinations().stream()
													.filter(destination -> destination	.getSource().getInsuranceBranch()
																						.getRid() == branchRid)
													.collect(Collectors.toSet()));
			fetchedTest.getTestResults().forEach(testResult ->
				{
					testResult.setNormalRanges(
							testResult	.getNormalRanges().stream()
										.filter(normalRange -> normalRange.getTestDestination() == null
												|| normalRange.getTestDestination().getSource().getInsuranceBranch().getRid() == branchRid)
										.collect(Collectors.toSet()));
				});
		}
		entityManager.clear();
		return fetchedTest;
	}

	public TestDefinition quickSaveTestDefinition(TestDefinition testDefinition) {
		if (testDefinition.getRid() != null) {
			switch (testEditability(testDefinition.getRid())) {
				case INACTIVE:
					throw new BusinessException("Only active tests can be edited!", "inactiveTestUneditable", ErrorSeverity.ERROR);
				case USED:
					throw new BusinessException("Only unused tests can be edited!", "usedTestUneditable", ErrorSeverity.ERROR);
				default:
					break;
			}
		}
		if (testDefinition.getRank().compareTo(1L) < 0) {
			throw new BusinessException("Minimum acceptable rank is 1!", "minimumRankIs1", ErrorSeverity.ERROR);
		}
		List<SearchCriterion> codeExistsFilters = new ArrayList<SearchCriterion>();
		codeExistsFilters.add((new SearchCriterion("standardCode", testDefinition.getStandardCode(), FilterOperator.eq)));
		if (testDefinition.getRid() != null) {
			codeExistsFilters.add(new SearchCriterion("rid", testDefinition.getRid(), FilterOperator.neq));
		}
		if (repo.findOne(codeExistsFilters, TestDefinition.class) != null) {
			throw new BusinessException("Test Standard Code already exists!", "testStandardCodeExists", ErrorSeverity.ERROR,
					Arrays.asList(testDefinition.getStandardCode()));
		}

		if (!testDefinition.getIsAllowRepetition()) {
			testDefinition.setIsRepetitionChargeable(Boolean.FALSE);
			testDefinition.setIsRepetitionSeparateSample(Boolean.FALSE);
		}

		//get temps before saving the TestDefinition to prevent data loss
		List<TestSpecimen> tempSpecimenList = testDefinition.getTestSpecimenList();
		List<Interpretation> tempInterpretationList = testDefinition.getInterpretationList();
		List<TestResult> tempResultList = testDefinition.getTestResultList();
		List<BillPricing> tempPrices = testDefinition.getPrices();
		List<TestDestination> tempDestinationList = testDefinition.getDestinationList();
		List<TestQuestion> tempQuestionList = testDefinition.getTestQuestionList();
		List<TestDisclaimer> tempDisclaimerList = testDefinition.getTestDisclaimerList();
		//		List<ExtraTest> tempExtraTestList = testDefinition.getExtraTestList();

		SectionType sectionType = null;
		if (testDefinition.getSection().getType() != null) {
			sectionType = SectionType.valueOf(testDefinition.getSection().getType().getCode());
		}

		if (SectionType.ALLERGY.equals(sectionType)
				&& (testDefinition.getAllergyDecimals() == null || testDefinition.getAllergyUnit() == null)) {
			throw new BusinessException("Test Defininition of type ALLERGY is missing allergy unit and decimals!",
					"missingAllergyDetails",
					ErrorSeverity.ERROR);
		}

		testDefinition = repo.save(testDefinition);

		//save the temp specimens
		for (TestSpecimen testSpecimen : tempSpecimenList) {
			testSpecimen.setTestDefinition(testDefinition);
		}
		specimenService.saveTestSpecimens(tempSpecimenList);

		if (SectionType.ALLERGY.equals(sectionType)) {
			//save the temp interpretations
			intepretationService.saveInterpretationList(tempInterpretationList, testDefinition);
		}

		//save the temp results
		for (TestResult testResult : tempResultList) {
			testResult.setTestDefinition(testDefinition);
		}
		testResultService.saveTestResults(tempResultList, sectionType);

		//save the temp results
		for (TestQuestion testQuestion : tempQuestionList) {
			testQuestion.setTestDefinition(testDefinition);
		}
		questionService.createTestQuestions(tempQuestionList);

		//save the temp disclaimers
		for (TestDisclaimer testDisclaimer : tempDisclaimerList) {
			testDisclaimer.setTestDefinition(testDefinition);
		}

		testDisclaimerService.createTestDisclaimers(tempDisclaimerList);

		//save the extraTests
		//		extraTestService.saveExtraTests(tempExtraTestList, testDefinition);

		//save the prices
		BillMasterItem masterItemTypeTest = null; //only one masterItem of type "TEST" must exist per testDefinition

		//try to find masterItemTypeTest
		List<BillTestItem> existingTestItems = testItemService.find(
				Arrays.asList(new SearchCriterion("testDefinition", testDefinition, FilterOperator.eq)),
				BillTestItem.class, "billMasterItem.type");
		for (BillTestItem testItem : existingTestItems) {
			if (testItem.getBillMasterItem().getType().getCode().equals(BillMasterItemType.TEST.toString())) {
				masterItemTypeTest = testItem.getBillMasterItem();
				break;
			}
		}

		//if masterItemTypeTest doesn't exist, create it
		if (masterItemTypeTest == null) {
			LkpBillItemType typeTest = lkpService.findOneAnyLkp(
					Arrays.asList(new SearchCriterion("code", BillMasterItemType.TEST.toString(), FilterOperator.eq)),
					LkpBillItemType.class);

			BillClassification classificiation = classificationService.findOne(
					Arrays.asList(new SearchCriterion("section", testDefinition.getSection(), FilterOperator.eq)),
					BillClassification.class);

			if (classificiation != null) {
				masterItemTypeTest = new BillMasterItem();
				masterItemTypeTest.setCode(testDefinition.getStandardCode());
				masterItemTypeTest.setType(typeTest);
				masterItemTypeTest.setIsActive(true);
				masterItemTypeTest.setBillClassification(classificiation);
				masterItemTypeTest = masterItemService.addBillMasterItem(masterItemTypeTest);

				//connect the master item with the test
				BillTestItem billTestItem = new BillTestItem();
				billTestItem.setBillMasterItem(masterItemTypeTest);
				billTestItem.setTestDefinition(testDefinition);
				testItemService.addBillTestItem(billTestItem);
			} else {
				throw new BusinessException("Section is not connected to classification!", "sectionNoClassification", ErrorSeverity.ERROR);
			}
		}

		List<BillPriceList> priceLists = new ArrayList<BillPriceList>();

		Date today = DateUtil.getCurrentDateWithoutTime();
		if (masterItemTypeTest != null) {
			boolean activePricingExists = false;
			for (BillPricing billPricing : tempPrices) {
				billPricing.setStartDate(DateUtil.trimTime(billPricing.getStartDate()));
				if (billPricing.getBillMasterItem() == null) {
					billPricing.setBillMasterItem(masterItemTypeTest);
				}
				if (billPricing.getEndDate() != null) {
					billPricing.setEndDate(DateUtil.trimTime(billPricing.getEndDate()));
				}
				if (billPricing.getBillPriceList().getIsDefault()) {
					if (billPricing.getEndDate() != null) {
						if (DateUtil.isBetween(today, billPricing.getStartDate(), billPricing.getEndDate())) {
							activePricingExists = true;
						}
					} else {
						if (DateUtil.isAfterOrEqual(today, billPricing.getStartDate())) {
							activePricingExists = true;
						}
					}
				}
				priceLists.add(billPricing.getBillPriceList());
			}
			pricingService.saveBillPricings(tempPrices);
			if (!activePricingExists) {
				throw new BusinessException("Test must have a default active pricing!", "testDefaultActivePricing", ErrorSeverity.ERROR);
			}
		}

		//save the destinations
		destinationService.saveTestDestinations(tempDestinationList, testDefinition, priceLists);

		return testDefinition;
	}

	@PreAuthorize("hasAuthority('" + EhopeRights.ADD_TEST_DEFINITION + "')")
	public TestDefinition addTestDefinition(TestDefinition newTestDefinition) {
		List<TestResult> tempResultList = newTestDefinition.getTestResultList();
		TestDefinition savedTestDefinition = repo.save(newTestDefinition);

		//		if (newTestDefinition.getSpecimens != null) {
		//			specimenService.addTestSpecimen(newTestDefinition.getSpecimen());
		//		}
		//TODO check this later (replaced specimen with a list of specimens)
		for (TestSpecimen specimen : newTestDefinition.getTestSpecimenList()) {
			specimenService.addTestSpecimen(specimen);
		}

		List<ExtraTest> extraTests = newTestDefinition.getExtraTestList();
		extraTests.forEach(extraTest ->
			{
				extraTest.setTest(savedTestDefinition);
				extraTestService.addExtraTest(extraTest);
			});

		List<TestQuestion> testQuestions = newTestDefinition.getTestQuestionList();
		testQuestions.forEach(testQuestion ->
			{
				testQuestion.setTestDefinition(savedTestDefinition);
				testQuestionService.addTestQuestion(testQuestion);
				testQuestion.getTestQuestionOptionList().forEach(testQuestionOption ->
					{
						testQuestionOption.setTestQuestion(testQuestion);
						testQuestionOptionService.addTestQuestionOption(testQuestionOption);
					});
			});

		//		List<TestResult> testResults = newTestDefinition.getTestResultList();
		//		testResults.forEach(testResult ->
		//			{
		//				testResult.setTestDefinition(savedTestDefinition);
		//				testResultService.addTestResult(testResult);
		//			});

		for (TestResult testResult : tempResultList) {
			testResult.setTestDefinition(newTestDefinition);
		}

		//testResultService.saveTestResults(tempResultList);

		return savedTestDefinition;
	}

	@PreAuthorize("hasAuthority('" + EhopeRights.UPD_TEST_DEFINITION + "')")
	public TestDefinition editTestDefinition(TestDefinition testDefinition) {
		switch (testEditability(testDefinition.getRid())) {
			case INACTIVE:
				throw new BusinessException("Only active tests can be edited!", "inactiveTestUneditable", ErrorSeverity.ERROR);
			case USED:
				throw new BusinessException("Only unused tests can be edited!", "usedTestUneditable", ErrorSeverity.ERROR);
			default:
				break;
		}

		//TODO check this later (replaced specimen with a list of specimens)
		//		TestSpecimen newSpecimen = testDefinition.getSpecimen();
		//		TestSpecimen oldSpecimen = specimenService.getByTestId(testDefinition);
		//		if (newSpecimen != null) {
		//			if (newSpecimen.getRid() != null && oldSpecimen != null && oldSpecimen.getRid().equals(newSpecimen.getRid())) {
		//				specimenService.editTestSpecimen(newSpecimen);
		//			} else {
		//				newSpecimen.setTestDefinition(testDefinition);
		//				specimenService.addTestSpecimen(testDefinition.getSpecimen());
		//			}
		//		} else if (oldSpecimen != null) {
		//			specimenService.deleteTestSpecimen(oldSpecimen.getRid());
		//		}

		List<TestResult> tempResultList = testDefinition.getTestResultList();
		TestDefinition savedTestDefinition = repo.save(testDefinition);

		List<ExtraTest> oldExtraTests = extraTestService.getByTestDefinition(testDefinition);
		List<ExtraTest> extraTests = testDefinition.getExtraTestList();
		oldExtraTests.forEach(oldExtraTest ->
			{
				if (!extraTests.contains(oldExtraTest)) {
					extraTestService.deleteExtraTest(oldExtraTest.getRid());
				}
			});
		extraTests.forEach(extraTest ->
			{
				if (extraTest.getExtraTest().getRid() == savedTestDefinition.getRid()) {
					throw new BusinessException("A test cannot be its own extra-test!", "selfExtraTestError", ErrorSeverity.ERROR);
				}
				extraTest.setTest(savedTestDefinition);
				extraTestService.addExtraTest(extraTest);
			});

		Set<TestQuestion> oldTestQuestions = testQuestionService.getByTestId(testDefinition);
		List<TestQuestion> testQuestions = testDefinition.getTestQuestionList();
		for (TestQuestion oldTestQuestion : oldTestQuestions) {
			if (!testQuestions.contains(oldTestQuestion)) {
				testQuestionService.deleteTestQuestion(oldTestQuestion.getRid());
				Set<TestQuestionOption> oldQuestionOptions = testQuestionOptionService.getByQuestionId(oldTestQuestion);
				for (TestQuestionOption oldQuestionOption : oldQuestionOptions) {
					testQuestionOptionService.deleteTestQuestionOption(oldQuestionOption.getRid());
				}
			}
		}
		for (TestQuestion testQuestion : testQuestions) {
			testQuestion.setTestDefinition(savedTestDefinition);
			TestQuestion savedTestQuestion = testQuestionService.addTestQuestion(testQuestion);

			Set<TestQuestionOption> oldQuestionOptions = testQuestionOptionService.getByQuestionId(savedTestQuestion);
			List<TestQuestionOption> questionOptions = testQuestion.getTestQuestionOptionList();
			for (TestQuestionOption oldQuestionOption : oldQuestionOptions) {
				if (!questionOptions.contains(oldQuestionOption)) {
					testQuestionOptionService.deleteTestQuestionOption(oldQuestionOption.getRid());
				}
			}
			for (TestQuestionOption questionOption : questionOptions) {
				questionOption.setTestQuestion(savedTestQuestion);
				testQuestionOptionService.addTestQuestionOption(questionOption);
			}
		}

		//		Set<TestResult> oldTestResults = testResultService.getByTestId(testDefinition);
		//		List<TestResult> testResults = testDefinition.getTestResultList();
		//		oldTestResults.forEach(oldTestResult ->
		//			{
		//				if (!testResults.contains(oldTestResult)) {
		//					testResultService.deleteTestResult(oldTestResult.getRid());
		//				}
		//			});
		//		testResults.forEach(testResult ->
		//			{
		//				testResult.setTestDefinition(savedTestDefinition);
		//				testResultService.addTestResult(testResult);
		//			});

		for (TestResult testResult : tempResultList) {
			testResult.setTestDefinition(savedTestDefinition);
		}

		//testResultService.saveTestResults(tempResultList);

		return savedTestDefinition;
	}

	@PreAuthorize("hasAuthority('" + EhopeRights.ACTIVATE_TEST_DEFINITION + "')")
	public TestDefinition activateTestDefinition(Long rid) throws BusinessException {
		TestDefinition fetchedTestDefinition = repo.getOne(rid);
		if (fetchedTestDefinition.getIsActive()) {
			throw new BusinessException("This test is already active!", "testAlreadyActive", ErrorSeverity.ERROR);
		}

		List<TestDefinition> activeTestDefinitionList = repo.getActiveTestsByStandardCode(fetchedTestDefinition.getStandardCode());
		if (activeTestDefinitionList.size() > 1 || fetchedTestDefinition.getDeactivationDate() != null) {
			throw new BusinessException("Cannot have more than one active test with the same code!", "activeTestExists",
					ErrorSeverity.ERROR);
		} else {
			fetchedTestDefinition.setIsActive(true);
		}
		return repo.save(fetchedTestDefinition);
	}

	@PreAuthorize("hasAuthority('" + EhopeRights.DEACTIVATE_TEST_DEFINITION + "')")
	public TestDefinition deactivateTestDefinition(Long rid) throws BusinessException {
		TestDefinition fetchedTestDefinition = repo.getOne(rid);
		if (!fetchedTestDefinition.getIsActive()) {
			throw new BusinessException("This test is already inactive!", "testAlreadyInactive", ErrorSeverity.ERROR);
		}

		fetchedTestDefinition.setIsActive(false);

		return repo.save(fetchedTestDefinition);
	}

	//This function checks the business rules for the test-definition during order-step
	//A test is selectable if it is:
	//1- Active
	//2- Has a destination whose source is the branch of this user and it active
	public Boolean determineTestDefinitionSelectability(TestDefinition testDefinition) {
		if (!testDefinition.getIsActive()) {
			return false;
		}
		Long branchRid = SecurityUtil.getCurrentUser().getBranchId();
		for (TestDestination testDestination : testDefinition.getDestinations()) {
			if (testDestination.getIsActive() && testDestination.getSource().getInsuranceBranch().getRid().equals(branchRid)) {
				return true;
			}
		}
		return false;
	}

	public TestEditability testEditability(Long testDefinitionRid) {
		if (!repo.getOne(testDefinitionRid).getIsActive()) {
			return TestEditability.INACTIVE;
		}

		//TODO check later (date effective solution?)
		//		SearchCriterion filterByTestDefinition = new SearchCriterion();
		//		filterByTestDefinition.setField("testDefinition");
		//		filterByTestDefinition.setValue(testDefinitionRid);
		//		filterByTestDefinition.setOperator(FilterOperator.eq);
		//		List<SearchCriterion> filters = new ArrayList<SearchCriterion>();
		//		filters.add(filterByTestDefinition);
		//
		//		List<LabTestActual> result = labTestActualService.find(filters, LabTestActual.class);
		//		if (result.size() > 0) {
		//			return TestEditability.USED;
		//		}

		return TestEditability.EDITABLE;
	}

	public List<TestDefinition> getMostRequestedTests(Integer count) {
		return repo.getMostRequestedTests(new PageRequest(0, count));
	}

	public TestDefinition testResultsFetch(Long rid) {
		return repo.testResultsFetch(rid);
	}

	/**
	 * General use for front end to fetch Test Definition pages
	 * 
	 * @param filterablePageRequest
	 * @return Page
	 */
	@PreAuthorize("hasAuthority('" + EhopeRights.VIEW_TEST_DIRECTORY + "')")
	public Page<TestDefinition> getTestDefinitionPage(FilterablePageRequest filterablePageRequest) {
		//		List<OrderObject> sortList = filterablePageRequest.getSortList();
		//		OrderObject ridOrderObject = new OrderObject();
		//		ridOrderObject.setDirection(Direction.ASC);
		//		ridOrderObject.setProperty("standardCode");
		//		sortList.add(ridOrderObject);
		//		filterablePageRequest.setSortList(sortList);

		return repo.find(filterablePageRequest.getFilters(), filterablePageRequest.getPageRequest(),
				TestDefinition.class, "section");
	}

	@PreAuthorize("hasAuthority('" + EhopeRights.VIEW_TEST_DIRECTORY + "')")
	public Page<TestDefinition> getSelectableTestDefinitionPage(FilterablePageRequest filterablePageRequest) {
		return repo.find(filterablePageRequest.getFilters(), filterablePageRequest.getPageRequest(),
				TestDefinition.class, "destinations.source.insuranceBranch");
	}

	@PreAuthorize("hasAuthority('" + EhopeRights.VIEW_TEST_DIRECTORY + "')")
	public Page<TestDefinition> getTestDefinitionLookup(FilterablePageRequest filterablePageRequest, Boolean fetchDestinations) {
		String standardCode = filterablePageRequest.getStringFilter("standardCode").toLowerCase();
		String secondaryCode = filterablePageRequest.getStringFilter("secondaryCode").toLowerCase();
		String description = filterablePageRequest.getStringFilter("description").toLowerCase();
		String aliases = filterablePageRequest.getStringFilter("aliases").toLowerCase();
		Boolean isActive = filterablePageRequest.getBooleanFilter("isActive");
		if (fetchDestinations) {
			return getRepository().getTestDefinitionLookupWithDestinations(isActive, standardCode, description, secondaryCode, aliases,
					filterablePageRequest.getPageRequest());
		}
		return getRepository().getTestDefinitionLookup(isActive, standardCode, description, secondaryCode, aliases,
				filterablePageRequest.getPageRequest());
	}

	/**
	 * Get the columns that are generated for the download/upload process.
	 * 
	 * @return ExcelSheet
	 */
	public ExcelSheet getTestDefinitionSheet() {
		List<ExcelColumn> rootColumns = new ArrayList<ExcelColumn>();

		rootColumns.add(new ExcelColumn("Standard Code", FieldType.STRING, "standardCode"));
		rootColumns.add(new ExcelColumn("Secondary Code", FieldType.STRING, "secondaryCode"));
		rootColumns.add(new ExcelColumn("Description", FieldType.STRING, "description"));
		rootColumns.add(new ExcelColumn("Reporting Description", FieldType.STRING, "reportingDescription"));
		rootColumns.add(new ExcelColumn("Additional Testing Requirements", FieldType.STRING, "additionalTestingRequirements"));
		rootColumns.add(new ExcelColumn("Advisory Information", FieldType.STRING, "advisoryInformation"));
		rootColumns.add(new ExcelColumn("Aliases", FieldType.STRING, "aliases"));
		rootColumns.add(new ExcelColumn("Analytic Time", FieldType.STRING, "analyticTime"));
		//		rootColumns.add(new ExcelColumn("Container Type", FieldType.STRING, "lkpContainerType", LkpContainerType.class, "code"));
		rootColumns.add(new ExcelColumn("CPT Code", FieldType.STRING, "cptCode"));
		rootColumns.add(new ExcelColumn("CPT Units", FieldType.STRING, "cptUnits"));
		rootColumns.add(new ExcelColumn("Days/Times Performed", FieldType.STRING, "daysTimesPerformed"));
		rootColumns.add(new ExcelColumn("Maximum Lab Time", FieldType.STRING, "maximumLabTime"));
		rootColumns.add(new ExcelColumn("Testing Method", FieldType.STRING, "lkpTestingMethod", LkpTestingMethod.class, "code"));
		rootColumns.add(new ExcelColumn("Necessary Information", FieldType.STRING, "necessaryInformation"));
		rootColumns.add(new ExcelColumn("Orderable Separately", FieldType.BOOLEAN, "orderableSeparately"));
		rootColumns.add(new ExcelColumn("Is Panel", FieldType.BOOLEAN, "isPanel"));
		rootColumns.add(new ExcelColumn("Shipping Instructions", FieldType.STRING, "shippingInstructions"));
		rootColumns.add(new ExcelColumn("Useful For", FieldType.STRING, "usefulFor"));
		rootColumns.add(new ExcelColumn("Testing Algorithm", FieldType.STRING, "testingAlgorithm"));
		rootColumns.add(new ExcelColumn("Clinical Information", FieldType.STRING, "clinicalInformation"));
		rootColumns.add(new ExcelColumn("Interpretation", FieldType.STRING, "interpretation"));
		rootColumns.add(new ExcelColumn("Cautions", FieldType.STRING, "cautions"));
		rootColumns.add(new ExcelColumn("Clinical Reference", FieldType.STRING, "clinicalReference"));
		rootColumns.add(new ExcelColumn("Special Instructions", FieldType.STRING, "specialInstructions"));
		rootColumns.add(new ExcelColumn("Supportive Data", FieldType.STRING, "supportiveData"));
		rootColumns.add(new ExcelColumn("Genetics Test Information", FieldType.STRING, "geneticsTestInformation"));
		rootColumns.add(new ExcelColumn("LOINC Code", FieldType.STRING, "loincCode"));
		rootColumns.add(new ExcelColumn("Rank", FieldType.STRING, "rank"));
		rootColumns.add(new ExcelColumn("Section", FieldType.STRING, "section", LabSection.class, "name"));

		ExcelSheet rootSheet = new ExcelSheet("Test Definitions", TestDefinition.class, rootColumns);

		return rootSheet;
	}

	public ExcelSheet getTestResultSheet() {
		List<ExcelColumn> resultColumns = new ArrayList<ExcelColumn>();

		resultColumns.add(new ExcelColumn("Test Definition", FieldType.STRING, "testDefinition", TestDefinition.class, "standardCode"));
		resultColumns.add(new ExcelColumn("Standard Code", FieldType.STRING, "standardCode"));
		resultColumns.add(new ExcelColumn("Description", FieldType.STRING, "description"));
		resultColumns.add(new ExcelColumn("Reporting Description", FieldType.STRING, "reportingDescription"));
		resultColumns.add(new ExcelColumn("Result Value Type", FieldType.STRING, "resultValueType", LkpResultValueType.class, "code"));
		resultColumns.add(new ExcelColumn("Primary Unit Type", FieldType.STRING, "primaryUnitType", LkpUnitType.class, "code"));
		resultColumns.add(new ExcelColumn("Primary Unit", FieldType.STRING, "primaryUnit", LabUnit.class, "unitOfMeasure"));
		resultColumns.add(new ExcelColumn("Primary Decimals", FieldType.STRING, "primaryDecimals"));
		resultColumns.add(new ExcelColumn("Factor", FieldType.STRING, "factor"));
		resultColumns.add(new ExcelColumn("Secondary Unit", FieldType.STRING, "secondaryUnit", LabUnit.class, "unitOfMeasure"));
		resultColumns.add(new ExcelColumn("Secondary Decimals", FieldType.STRING, "secondaryDecimals"));
		resultColumns.add(new ExcelColumn("Coded Results", FieldType.STRING, "testCodedResultList"));
		resultColumns.add(new ExcelColumn("Print Order", FieldType.STRING, "printOrder"));

		List<ExcelColumn> normalRangeColumns = new ArrayList<ExcelColumn>();

		normalRangeColumns.add(new ExcelColumn("Sex", FieldType.STRING, "sex", LkpGender.class, "code"));
		normalRangeColumns.add(new ExcelColumn("Age From", FieldType.STRING, "ageFromData"));
		normalRangeColumns.add(new ExcelColumn("Age To", FieldType.STRING, "ageToData"));
		normalRangeColumns.add(new ExcelColumn("Criterion Name", FieldType.STRING, "criterionName"));
		normalRangeColumns.add(new ExcelColumn("Criterion Value", FieldType.STRING, "criterionValue"));
		normalRangeColumns.add(new ExcelColumn("Min Panic Value", FieldType.STRING, "minPanicValue"));
		normalRangeColumns.add(new ExcelColumn("Min Rerun Value", FieldType.STRING, "minRerunValue"));
		normalRangeColumns.add(new ExcelColumn("Min Normal Value", FieldType.STRING, "minValue"));
		normalRangeColumns.add(new ExcelColumn("Max Normal Value", FieldType.STRING, "maxValue"));
		normalRangeColumns.add(new ExcelColumn("Max Rerun Value", FieldType.STRING, "maxRerunValue"));
		normalRangeColumns.add(new ExcelColumn("Max Panic Value", FieldType.STRING, "maxPanicValue"));
		normalRangeColumns.add(new ExcelColumn("Coded Result", FieldType.STRING, "codedResult"));

		ExcelSheet normalRangeSheet = new ExcelSheet("Normal_Ranges", TestNormalRange.class, normalRangeColumns,
				"normalRangeList", "standardCode", "Result Standard Code");

		ExcelSheet resultSheet = new ExcelSheet("Test_Results", TestResult.class, resultColumns,
				Arrays.asList(normalRangeSheet));

		return resultSheet;
	}

	//	public ExcelSheet getTestQuestionSheet() {
	//		List<ExcelColumn> questionColumns = new ArrayList<ExcelColumn>();
	//
	//		questionColumns.add(new ExcelColumn("Standard Code", FieldType.STRING, "standardCode"));
	//		questionColumns.add(new ExcelColumn("Description", FieldType.STRING, "description"));
	//		questionColumns.add(new ExcelColumn("Question Type", FieldType.STRING, "lkpQuestionType", LkpQuestionType.class, "code"));
	//		questionColumns.add(new ExcelColumn("Question Stage", FieldType.STRING, "lkpQuestionStage", LkpQuestionStage.class, "code"));
	//
	//		List<ExcelColumn> questionOptionColumns = new ArrayList<ExcelColumn>();
	//
	//		questionOptionColumns.add(new ExcelColumn("Standard Code", FieldType.STRING, "standardCode"));
	//		questionOptionColumns.add(new ExcelColumn("Description", FieldType.STRING, "description"));
	//
	//		ExcelSheet questionOptionSheet = new ExcelSheet("Question Options", TestQuestionOption.class, questionOptionColumns,
	//				"testQuestionOptionList", "standardCode", "Question Standard Code");
	//
	//		ExcelSheet questionSheet = new ExcelSheet("Questions", TestQuestion.class, questionColumns,
	//				Arrays.asList(questionOptionSheet));
	//
	//		return questionSheet;
	//	}

	public ExcelSheet getTestPricingSheet() {
		List<ExcelColumn> pricingColumns = new ArrayList<ExcelColumn>();

		//pricingColumns.add(new ExcelColumn("Master Item", FieldType.STRING, "billMasterItem", BillMasterItem.class, "code"));
		pricingColumns.add(new ExcelColumn("Billing Price List", FieldType.STRING, "billPriceList", BillPriceList.class, "name"));
		pricingColumns.add(new ExcelColumn("Price", FieldType.STRING, "price"));
		pricingColumns.add(new ExcelColumn("Start Date", FieldType.STRING, "startDate"));
		pricingColumns.add(new ExcelColumn("End Date", FieldType.STRING, "endDate"));

		ExcelSheet pricingSheet = new ExcelSheet("Fees and Coding", BillPricing.class, pricingColumns);

		return pricingSheet;
	}

	//	public ExcelSheet getTestDefinitionSheet() {
	//		List<ExcelColumn> rootColumns = new ArrayList<ExcelColumn>();
	//
	//		rootColumns.add(new ExcelColumn("Standard Code", FieldType.STRING, "standardCode"));
	//		rootColumns.add(new ExcelColumn("Secondary Code", FieldType.STRING, "secondaryCode"));
	//		rootColumns.add(new ExcelColumn("Description", FieldType.STRING, "description"));
	//		rootColumns.add(new ExcelColumn("Reporting Description", FieldType.STRING, "reportingDescription"));
	//		rootColumns.add(new ExcelColumn("Additional Testing Requirements", FieldType.STRING, "additionalTestingRequirements"));
	//		rootColumns.add(new ExcelColumn("Advisory Information", FieldType.STRING, "advisoryInformation"));
	//		rootColumns.add(new ExcelColumn("Aliases", FieldType.STRING, "aliases"));
	//		rootColumns.add(new ExcelColumn("Analytic Time", FieldType.STRING, "analyticTime"));
	//		rootColumns.add(new ExcelColumn("Container Type", FieldType.STRING, "lkpContainerType", LkpContainerType.class, "code"));
	//		rootColumns.add(new ExcelColumn("CPT Code", FieldType.STRING, "cptCode"));
	//		rootColumns.add(new ExcelColumn("CPT Units", FieldType.STRING, "cptUnits"));
	//		rootColumns.add(new ExcelColumn("Days/Times Performed", FieldType.STRING, "daysTimesPerformed"));
	//		rootColumns.add(new ExcelColumn("Maximum Lab Time", FieldType.STRING, "maximumLabTime"));
	//		rootColumns.add(new ExcelColumn("Testing Method", FieldType.STRING, "lkpTestingMethod", LkpTestingMethod.class, "code"));
	//		rootColumns.add(new ExcelColumn("Necessary Information", FieldType.STRING, "necessaryInformation"));
	//		rootColumns.add(new ExcelColumn("Orderable Separately", FieldType.BOOLEAN, "orderableSeparately"));
	//		rootColumns.add(new ExcelColumn("Is Panel", FieldType.BOOLEAN, "isPanel"));
	//		rootColumns.add(new ExcelColumn("Shipping Instructions", FieldType.STRING, "shippingInstructions"));
	//		rootColumns.add(new ExcelColumn("Useful For", FieldType.STRING, "usefulFor"));
	//		rootColumns.add(new ExcelColumn("Testing Algorithm", FieldType.STRING, "testingAlgorithm"));
	//		rootColumns.add(new ExcelColumn("Clinical Information", FieldType.STRING, "clinicalInformation"));
	//		rootColumns.add(new ExcelColumn("Interpretation", FieldType.STRING, "interpretation"));
	//		rootColumns.add(new ExcelColumn("Cautions", FieldType.STRING, "cautions"));
	//		rootColumns.add(new ExcelColumn("Clinical Reference", FieldType.STRING, "clinicalReference"));
	//		rootColumns.add(new ExcelColumn("Special Instructions", FieldType.STRING, "specialInstructions"));
	//		rootColumns.add(new ExcelColumn("Supportive Data", FieldType.STRING, "supportiveData"));
	//		rootColumns.add(new ExcelColumn("Genetics Test Information", FieldType.STRING, "geneticsTestInformation"));
	//		rootColumns.add(new ExcelColumn("LOINC Code", FieldType.STRING, "loincCode"));
	//		rootColumns.add(new ExcelColumn("Rank", FieldType.STRING, "rank"));
	//		rootColumns.add(new ExcelColumn("Section", FieldType.STRING, "section", LabSection.class, "name"));
	//
	//		List<ExcelColumn> resultColumns = new ArrayList<ExcelColumn>();
	//
	//		resultColumns.add(new ExcelColumn("Standard Code", FieldType.STRING, "standardCode"));
	//		resultColumns.add(new ExcelColumn("Description", FieldType.STRING, "description"));
	//		resultColumns.add(new ExcelColumn("Reporting Description", FieldType.STRING, "reportingDescription"));
	//		resultColumns.add(new ExcelColumn("Result Value Type", FieldType.STRING, "resultValueType", LkpResultValueType.class, "code"));
	//		resultColumns.add(new ExcelColumn("Primary Unit Type", FieldType.STRING, "primaryUnitType", LkpUnitType.class, "code"));
	//		resultColumns.add(new ExcelColumn("Primary Unit", FieldType.STRING, "primaryUnit", LabUnit.class, "unitOfMeasure"));
	//		resultColumns.add(new ExcelColumn("Primary Decimals", FieldType.STRING, "primaryDecimals"));
	//		resultColumns.add(new ExcelColumn("Factor", FieldType.STRING, "factor"));
	//		resultColumns.add(new ExcelColumn("Secondary Unit", FieldType.STRING, "secondaryUnit", LabUnit.class, "unitOfMeasure"));
	//		resultColumns.add(new ExcelColumn("Secondary Decimals", FieldType.STRING, "secondaryDecimals"));
	//		resultColumns.add(new ExcelColumn("Coded Results", FieldType.STRING, "testCodedResultList"));
	//		resultColumns.add(new ExcelColumn("Print Order", FieldType.STRING, "printOrder"));
	//
	//		List<ExcelColumn> normalRangeColumns = new ArrayList<ExcelColumn>();
	//
	//		normalRangeColumns.add(new ExcelColumn("Sex", FieldType.STRING, "sex", LkpGender.class, "code"));
	//		normalRangeColumns.add(new ExcelColumn("Age From", FieldType.STRING, "ageFromData"));
	//		normalRangeColumns.add(new ExcelColumn("Age To", FieldType.STRING, "ageToData"));
	//		normalRangeColumns.add(new ExcelColumn("Criterion Name", FieldType.STRING, "criterionName"));
	//		normalRangeColumns.add(new ExcelColumn("Criterion Value", FieldType.STRING, "criterionValue"));
	//		normalRangeColumns.add(new ExcelColumn("Min Panic Value", FieldType.STRING, "minPanicValue"));
	//		normalRangeColumns.add(new ExcelColumn("Min Rerun Value", FieldType.STRING, "minRerunValue"));
	//		normalRangeColumns.add(new ExcelColumn("Min Normal Value", FieldType.STRING, "minValue"));
	//		normalRangeColumns.add(new ExcelColumn("Max Normal Value", FieldType.STRING, "maxValue"));
	//		normalRangeColumns.add(new ExcelColumn("Max Rerun Value", FieldType.STRING, "maxRerunValue"));
	//		normalRangeColumns.add(new ExcelColumn("Max Panic Value", FieldType.STRING, "maxPanicValue"));
	//		normalRangeColumns.add(new ExcelColumn("Coded Result", FieldType.STRING, "codedResult"));
	//
	//		ExcelSheet normalRangeSheet = new ExcelSheet("Normal Ranges", TestNormalRange.class, normalRangeColumns,
	//				"normalRangeList", "standardCode", "Result Standard Code");
	//
	//		ExcelSheet resultSheet = new ExcelSheet("Test Results", TestResult.class, resultColumns,
	//				Arrays.asList(normalRangeSheet), "testResultList", "standardCode", "Test Standard Code");
	//
	//		List<ExcelColumn> pricingColumns = new ArrayList<ExcelColumn>();
	//
	//		//pricingColumns.add(new ExcelColumn("Master Item", FieldType.STRING, "billMasterItem", BillMasterItem.class, "code"));
	//		pricingColumns.add(new ExcelColumn("Billing Price List", FieldType.STRING, "billPriceList", BillPriceList.class, "name"));
	//		pricingColumns.add(new ExcelColumn("Price", FieldType.STRING, "price"));
	//		pricingColumns.add(new ExcelColumn("Start Date", FieldType.STRING, "startDate"));
	//		pricingColumns.add(new ExcelColumn("End Date", FieldType.STRING, "endDate"));
	//
	//		ExcelSheet pricingSheet = new ExcelSheet("Fees and Coding", BillPricing.class, pricingColumns,
	//				"prices", "standardCode", "Test Standard Code");
	//
	//		List<ExcelColumn> questionColumns = new ArrayList<ExcelColumn>();
	//
	//		questionColumns.add(new ExcelColumn("Standard Code", FieldType.STRING, "standardCode"));
	//		questionColumns.add(new ExcelColumn("Description", FieldType.STRING, "description"));
	//		questionColumns.add(new ExcelColumn("Question Type", FieldType.STRING, "lkpQuestionType", LkpQuestionType.class, "code"));
	//		questionColumns.add(new ExcelColumn("Question Stage", FieldType.STRING, "lkpQuestionStage", LkpQuestionStage.class, "code"));
	//
	//		List<ExcelColumn> questionOptionColumns = new ArrayList<ExcelColumn>();
	//
	//		questionOptionColumns.add(new ExcelColumn("Standard Code", FieldType.STRING, "standardCode"));
	//		questionOptionColumns.add(new ExcelColumn("Description", FieldType.STRING, "description"));
	//
	//		ExcelSheet questionOptionSheet = new ExcelSheet("Question Options", TestQuestionOption.class, questionOptionColumns,
	//				"testQuestionOptionList", "standardCode", "Question Standard Code");
	//
	//		ExcelSheet questionSheet = new ExcelSheet("Questions", TestQuestion.class, questionColumns,
	//				Arrays.asList(questionOptionSheet), "testQuestionList", "standardCode", "Test Standard Code");
	//
	//		ExcelSheet rootSheet = new ExcelSheet("Test Definitions", TestDefinition.class, rootColumns,
	//				Arrays.asList(resultSheet, pricingSheet, questionSheet));
	//
	//		return rootSheet;
	//	}

	//TODO: Lock adding
	//TODO incomplete
	public ExcelSheet uploadTestData(MultipartFile excel) {
		SessionFactory sessionFactory = entityManagerFactory.unwrap(SessionFactory.class);
		Session session = null;
		Transaction tx = null;
		Workbook workbook = ExcelUtil.getWorkbookFromExcel(excel);
		Sheet sheet = workbook.getSheetAt(0);
		Iterator<Row> rowIterator = sheet.iterator();
		rowIterator.next();//skip
		rowIterator.next();//skip
		session = sessionFactory.openSession();
		tx = session.beginTransaction();
		ExcelSheet es = getTestDefinitionSheet();
		List<ExcelColumn> columns = es.getColumns();
		List<ComTenantLanguage> languages = tenantLanguageService.findTenantExcelLanguages();
		Row row = null;
		try {
			while (rowIterator.hasNext()) {
				row = rowIterator.next();
				if (ExcelUtil.isRowEmpty(row)) {
					continue;
				}
				try {
					TestDefinition testDef = ExcelUtil.createObjectFromRow(row, TestDefinition.class, columns, languages);
					testDef.setIsActive(Boolean.FALSE);
					session.persist(testDef);
					session.flush();
					session.clear();
				} catch (Exception e) {
					//es.addToFailedRows(row);
				}
			}
			//don't continue if we have any failed row,otherwise session.flush(...) will throw exception
			//if (!CollectionUtils.isListEmpty(es.getFailedRows())) {
			//return es;
			//}

			workbook.close();
			tx.commit();
			session.close();
		} catch (IOException ioe) {//workbook.close() exception
			ioe.printStackTrace();
			throw new BusinessException("Fail To Close Excel Workbook", "importDataFail", ErrorSeverity.ERROR);
		} catch (Exception e) {//if any random exception happened then roll back
			e.printStackTrace();
			tx.rollback();
			throw new BusinessException("Importing Persisting Exception", "importDataFail", ErrorSeverity.ERROR);
		} finally {
			if (session.isOpen()) {
				session.close();
			}
		}
		return es;
	}

	//TODO: Lock adding
	//TODO incomplete
	public ExcelSheet uploadResultData(MultipartFile excel) {
		Workbook workbook = ExcelUtil.getWorkbookFromExcel(excel);
		Sheet resultSheet = workbook.getSheet("Test_Results");
		Iterator<Row> resultRowIterator = resultSheet.iterator();
		resultRowIterator.next();//skip
		resultRowIterator.next();//skip

		Sheet normalRangeSheet = workbook.getSheet("Normal_Ranges");
		Iterator<Row> normalRangeRowIterator = normalRangeSheet.iterator();
		normalRangeRowIterator.next();//skip
		normalRangeRowIterator.next();//skip

		ExcelSheet resultExcelSheet = getTestResultSheet();
		ExcelSheet normalRangeExcelSheet = resultExcelSheet.getSubSheets().get(0);
		List<ExcelColumn> columns = resultExcelSheet.getColumns();
		List<ComTenantLanguage> languages = tenantLanguageService.findTenantExcelLanguages();
		Row resultRow = null;
		Row normalRangeRow = null;
		Map<String, List<TestNormalRange>> resultMap = new HashMap<String, List<TestNormalRange>>();
		try {
			while (normalRangeRowIterator.hasNext()) {
				normalRangeRow = normalRangeRowIterator.next();
				if (ExcelUtil.isRowEmpty(normalRangeRow)) {
					continue;
				}
				try {
					TestNormalRange normalRange = ExcelUtil.createObjectFromRow(normalRangeRow, TestNormalRange.class, columns, languages);

				} catch (Exception e) {
					//normalRangeExcelSheet.addToFailedRows(normalRangeRow);
				}
			}
			while (resultRowIterator.hasNext()) {
				resultRow = resultRowIterator.next();
				if (ExcelUtil.isRowEmpty(resultRow)) {
					continue;
				}
				try {
					TestResult result = ExcelUtil.createObjectFromRow(resultRow, TestResult.class, columns, languages);

				} catch (Exception e) {
					//resultExcelSheet.addToFailedRows(resultRow);
				}
			}
			workbook.close();
		} catch (IOException ioe) {//workbook.close() exception
			ioe.printStackTrace();
			throw new BusinessException("Fail To Close Excel Workbook", "importDataFail", ErrorSeverity.ERROR);
		} catch (Exception e) {//if any random exception happened then roll back
			e.printStackTrace();
			throw new BusinessException("Importing Persisting Exception", "importDataFail", ErrorSeverity.ERROR);
		}
		return resultExcelSheet;
	}

	private <T> Map<String, List<T>> addToMap(Map<String, List<T>> map, String key, T child) {
		List<T> childList = map.get(key);
		if (childList == null) {
			childList = new ArrayList<T>();
			map.put(key, childList);
		}
		childList.add(child);
		return map;
	}

	public void importFailedNormalRanges(MultipartFile excel) throws IOException {
		String fileName = excel.getOriginalFilename();
		InputStream inputStream = excel.getInputStream();

		String extension = fileName.substring(fileName.lastIndexOf('.') + 1);
		Workbook workbook;
		if (extension.equals("xls")) {
			workbook = new HSSFWorkbook(inputStream);
		} else {
			workbook = new XSSFWorkbook(inputStream);
		}

		Sheet normalRangeSheet = workbook.getSheetAt(1);

		List<LkpGender> genderList = lkpService.findAnyLkp(new ArrayList<SearchCriterion>(), LkpGender.class, null);
		Map<String, LkpGender> genders = new HashMap<String, LkpGender>();
		for (LkpGender gender : genderList) {
			genders.put(gender.getCode(), gender);
		}

		List<LkpAgeUnit> ageUnitList = lkpService.findAnyLkp(new ArrayList<SearchCriterion>(), LkpAgeUnit.class, null);
		Map<String, LkpAgeUnit> ageUnits = new HashMap<String, LkpAgeUnit>();
		for (LkpAgeUnit ageUnit : ageUnitList) {
			ageUnits.put(ageUnit.getCode().substring(0, 1), ageUnit);
		}

		List<TestCodedResult> codedResultList = codedResultService.findAll();
		Map<String, TestCodedResult> globalCodedResults = new HashMap<String, TestCodedResult>();
		for (TestCodedResult codedResult : codedResultList) {
			globalCodedResults.put(codedResult.getCode(), codedResult);
		}

		List<LkpResultValueType> resultValueTypeList = lkpService.findAnyLkp(new ArrayList<>(), LkpResultValueType.class, null);
		Map<ResultValueType, LkpResultValueType> resultValueTypes = new HashMap<ResultValueType, LkpResultValueType>();
		for (LkpResultValueType resultValueType : resultValueTypeList) {
			resultValueTypes.put(ResultValueType.valueOf(resultValueType.getCode()), resultValueType);
		}

		Iterator<Row> normalRangeIterator = normalRangeSheet.iterator();
		normalRangeIterator.next();
		normalRangeIterator.next();

		Pattern fromPattern = Pattern.compile("(>|>=)?(\\d+)(d|w|m|y)");
		Pattern toPattern = Pattern.compile("(<|<=)?(\\d+)(d|w|m|y)");
		Pattern valuePattern = Pattern.compile("(>|>=|<|<=)?(\\d+[.\\d+]?)");

		String groupResultCode = null;
		TestResult testResult = null;
		List<TestNormalRange> groupNormalRanges = new ArrayList<TestNormalRange>();
		while (normalRangeIterator.hasNext()) {
			Row row = normalRangeIterator.next();
			if (ExcelUtil.isRowEmpty(row)) {
				continue;
			}
			int column = 0;
			column++;
			String resultCode = ExcelUtil.getStringFromCell(row.getCell(column++));
			String sexCode = ExcelUtil.getStringFromCell(row.getCell(column++));
			String ageFrom = ExcelUtil.getStringFromCell(row.getCell(column++)).toLowerCase();
			String ageTo = ExcelUtil.getStringFromCell(row.getCell(column++)).toLowerCase();
			String resultValueType = ExcelUtil.getStringFromCell(row.getCell(column++));
			String criterionName = ExcelUtil.getStringFromCell(row.getCell(column++));
			String criterionValue = ExcelUtil.getStringFromCell(row.getCell(column++));
			String minPanicValue = ExcelUtil.getStringFromCell(row.getCell(column++)).replaceAll("\\s+", "");
			String minRerunValue = ExcelUtil.getStringFromCell(row.getCell(column++)).replaceAll("\\s+", "");
			String minNormalValue = ExcelUtil.getStringFromCell(row.getCell(column++)).replaceAll("\\s+", "");
			String maxNormalValue = ExcelUtil.getStringFromCell(row.getCell(column++)).replaceAll("\\s+", "");
			String maxRerunValue = ExcelUtil.getStringFromCell(row.getCell(column++)).replaceAll("\\s+", "");
			String maxPanicValue = ExcelUtil.getStringFromCell(row.getCell(column++)).replaceAll("\\s+", "");
			String codedResults = ExcelUtil.getStringFromCell(row.getCell(column++));
			String codedResult = ExcelUtil.getStringFromCell(row.getCell(column++));

			if (groupResultCode == null || !resultCode.equals(groupResultCode)) {
				if (groupResultCode == null) {
					groupResultCode = resultCode;
				} else {
					normalRangeService.saveNormalRanges(groupNormalRanges, testResult);
					groupResultCode = resultCode;
					groupNormalRanges.clear();
				}
				testResult = testResultService.findOne(
						Arrays.asList(new SearchCriterion("standardCode", resultCode, FilterOperator.eq)),
						TestResult.class, "resultValueType");
				if (!testResult.getResultValueType().getCode().equals(resultValueType)) {
					LkpResultValueType typeToSet = resultValueTypes.get(ResultValueType.valueOf(resultValueType));
					testResult.setResultValueType(typeToSet);
					testResult = testResultService.editTestResult(testResult);
					testResult.setResultValueType(typeToSet);
				}
			}

			TestNormalRange normalRange = new TestNormalRange();
			if (!StringUtil.isEmpty(sexCode)) {
				sexCode = sexCode.toLowerCase().startsWith("m") ? "MALE" : "FEMALE";
				normalRange.setSex(genders.get(sexCode));
			}
			Matcher ageFromMatcher = fromPattern.matcher(ageFrom);
			if (ageFromMatcher.find()) {
				String ageFromComparator = ">=";
				if (!StringUtil.isEmpty(ageFromMatcher.group(1))) {
					ageFromComparator = ageFromMatcher.group(1);
				}
				normalRange.setAgeFromComparator(ageFromComparator);
				normalRange.setAgeFrom(Integer.parseInt(ageFromMatcher.group(2)));
				normalRange.setAgeFromUnit(ageUnits.get(ageFromMatcher.group(3)));
			}

			Matcher ageToMatcher = toPattern.matcher(ageTo);
			if (ageToMatcher.find()) {
				String ageToComparator = "<=";
				if (!StringUtil.isEmpty(ageToMatcher.group(1))) {
					ageToComparator = ageToMatcher.group(1);
				}
				normalRange.setAgeToComparator(ageToComparator);
				normalRange.setAgeTo(Integer.parseInt(ageToMatcher.group(2)));
				normalRange.setAgeToUnit(ageUnits.get(ageToMatcher.group(3)));
			}
			normalRange.setCriterionName(criterionName);
			normalRange.setCriterionValue(criterionValue);
			Matcher valueMatcher;
			valueMatcher = valuePattern.matcher(minPanicValue);
			if (valueMatcher.find()) {
				normalRange.setMinPanicValue(new BigDecimal(valueMatcher.group(2)));
			}
			valueMatcher = valuePattern.matcher(minRerunValue);
			if (valueMatcher.find()) {
				normalRange.setMinRerunValue(new BigDecimal(valueMatcher.group(2)));
			}
			valueMatcher = valuePattern.matcher(minNormalValue);
			if (valueMatcher.find()) {
				normalRange.setMinValue(new BigDecimal(valueMatcher.group(2)));
			}
			valueMatcher = valuePattern.matcher(maxNormalValue);
			if (valueMatcher.find()) {
				normalRange.setMaxValue(new BigDecimal(valueMatcher.group(2)));
			}
			valueMatcher = valuePattern.matcher(maxRerunValue);
			if (valueMatcher.find()) {
				normalRange.setMaxRerunValue(new BigDecimal(valueMatcher.group(2)));
			}
			valueMatcher = valuePattern.matcher(maxPanicValue);
			if (valueMatcher.find()) {
				normalRange.setMaxPanicValue(new BigDecimal(valueMatcher.group(2)));
			}

			if (resultValueType.equals(ResultValueType.CE.toString())) {
				String[] codedResultArray = codedResults.split("[|]");
				for (String coded : codedResultArray) {
					TestCodedResultMapping mapping = new TestCodedResultMapping();
					TestCodedResult testCodedResult = globalCodedResults.get(coded);
					if (testCodedResult == null) {
						testCodedResult = new TestCodedResult();
						testCodedResult.setCode(coded);
						testCodedResult.setValue(coded);
						testCodedResult = codedResultService.addTestCodedResult(testCodedResult);
						globalCodedResults.put(coded, testCodedResult);
					}
					mapping.setTestCodedResult(testCodedResult);
					mapping.setTestResult(testResult);
				}
				if (!StringUtil.isEmpty(codedResult)) {
					normalRange.setCodedResult(globalCodedResults.get(codedResult));
				}
			}

			normalRange.setPrintOrder(groupNormalRanges.size());
			groupNormalRanges.add(normalRange);
		}
		normalRangeService.saveNormalRanges(groupNormalRanges, testResult);

		workbook.close();
	}

	/**
	 * Get the parsed normal range values from a string
	 * 
	 * @return BigDecimal array of length 2, index 0 is min, index 1 is max
	 */
	public Boolean[] getValuesFromNormalRangeString(String input, Integer decimals) {
		input = input.trim().replaceAll("\\s+", "").toLowerCase();
		BigDecimal[] parsedValues = new BigDecimal[2];
		Boolean[] changedValues = { Boolean.FALSE, Boolean.FALSE };
		String numberRegex = "\\d+(\\.\\d+)?";
		String closedRangePattern = "^(>|>=)?\\s*(" + numberRegex + ")\\s*-\\s*(<|<=)?\\s*(" + numberRegex + ")$";
		Pattern pattern = Pattern.compile(closedRangePattern);
		Matcher matcher = pattern.matcher(input);
		String min;
		String max;
		if (matcher.find()) {
			min = matcher.group(2);
			max = matcher.group(5);
			BigDecimal minDecimal = new BigDecimal(min).setScale(decimals, RoundingMode.HALF_UP);
			BigDecimal maxDecimal = new BigDecimal(max).setScale(decimals, RoundingMode.HALF_UP);
			if (matcher.group(1) != null && matcher.group(1).equals(">")) {
				minDecimal = minDecimal.add(NumberUtil.getSmallestDecimal(decimals));
				changedValues[0] = Boolean.TRUE;
			}
			if (matcher.group(4) != null && matcher.group(4).equals("<")) {
				maxDecimal = maxDecimal.subtract(NumberUtil.getSmallestDecimal(decimals));
				changedValues[1] = Boolean.TRUE;
			}
			parsedValues[0] = minDecimal;
			parsedValues[1] = maxDecimal;
			return changedValues;
		}
		String openRangeRegex = "^(<|<=|>|>=|up\\s*to)\\s*(" + numberRegex + ")$";
		pattern = Pattern.compile(openRangeRegex);
		matcher = pattern.matcher(input);
		if (matcher.find()) {
			String operator = matcher.group(1).replaceAll("\\s+", "");
			switch (operator) {
				case "<":
					max = matcher.group(2);
					parsedValues[1] = new BigDecimal(max)	.setScale(decimals, RoundingMode.HALF_UP)
															.subtract(NumberUtil.getSmallestDecimal(decimals));
					changedValues[1] = Boolean.TRUE;
					break;
				case "upto":
				case "<=":
					max = matcher.group(2);
					parsedValues[1] = new BigDecimal(max).setScale(decimals, RoundingMode.HALF_UP);
					break;
				case ">":
					min = matcher.group(2);
					parsedValues[0] = new BigDecimal(min)	.setScale(decimals, RoundingMode.HALF_UP)
															.add(NumberUtil.getSmallestDecimal(decimals));
					changedValues[0] = Boolean.TRUE;
					break;
				case ">=":
					min = matcher.group(2);
					parsedValues[0] = new BigDecimal(min).setScale(decimals, RoundingMode.HALF_UP);
					break;
			}
			return changedValues;
		}
		throw new BusinessException("Unrecognized pattern! No values parsed!", "noValuesParsed", ErrorSeverity.ERROR);
	}

	public Boolean[] setNormalRangeValues(TestResult testResult, TestNormalRange normalRange, String si, String conv) {
		switch (testResult.getResultValueType().getCode()) {
			case "QN":
			case "QN_SC":
				switch (testResult.getPrimaryUnitType().getCode()) {
					case "SI":
						return setNormalRangeValuesHelper(testResult, normalRange, si, conv);
					case "CONV":
						return setNormalRangeValuesHelper(testResult, normalRange, conv, si);
				}
				break;
		}
		return null;
	}

	public void setNormalRangeAge(TestNormalRange normalRange, Integer ageFrom, Integer ageTo, String ageFromUnit, String ageToUnit,
			List<LkpAgeUnit> ageUnits) {
		if (!StringUtil.isEmpty(ageFromUnit) && !StringUtil.isEmpty(ageToUnit)) {
			for (LkpAgeUnit ageUnit : ageUnits) {
				if (ageUnit.getCode().startsWith(ageFromUnit.toLowerCase())) {
					normalRange.setAgeFromUnit(ageUnit);
				}
				if (ageUnit.getCode().startsWith(ageToUnit.toLowerCase())) {
					normalRange.setAgeToUnit(ageUnit);
				}
			}
			normalRange.setAgeFrom(ageFrom);
			normalRange.setAgeFromComparator(">=");
			normalRange.setAgeTo(ageTo);
			normalRange.setAgeToComparator("<=");
		}
	}

	public Boolean[] setNormalRangeValuesHelper(TestResult testResult, TestNormalRange normalRange, String primaryString,
			String secondaryString) {
		BigDecimal[] parsedValues = null;
		if (StringUtil.isEmpty(primaryString) && StringUtil.isEmpty(secondaryString)) {
			throw new BusinessException("Empty line values (SI + Conv.)!", "emptySiAndConvLine", ErrorSeverity.ERROR);
		} else if (!StringUtil.isEmpty(primaryString)) {
			return getValuesFromNormalRangeString(primaryString, testResult.getPrimaryDecimals());
			//			parsedValues = getValuesFromNormalRangeString(primaryString, testResult.getPrimaryDecimals());
			//			normalRange.setMinValue(parsedValues[0]);
			//			normalRange.setMaxValue(parsedValues[1]);
		} else if (!StringUtil.isEmpty(secondaryString)) {
			return getValuesFromNormalRangeString(secondaryString, testResult.getPrimaryDecimals());
			//			parsedValues = getValuesFromNormalRangeString(secondaryString, testResult.getPrimaryDecimals());
			//			normalRange.setMinValue(parsedValues[0].divide(testResult.getFactor()));
			//			normalRange.setMaxValue(parsedValues[1].divide(testResult.getFactor()));
		}
		return null;
	}

	public void importMultiResultTests(MultipartFile excel) throws IOException {
		String fileName = excel.getOriginalFilename();
		InputStream inputStream = excel.getInputStream();

		String extension = fileName.substring(fileName.lastIndexOf('.') + 1);
		Workbook workbook;
		if (extension.equals("xls")) {
			workbook = new HSSFWorkbook(inputStream);
		} else {
			workbook = new XSSFWorkbook(inputStream);
		}

		Sheet testSheet = workbook.getSheet("Tests");
		Map<String, TestDefinition> savedTests = new HashMap<String, TestDefinition>();
		Sheet resultSheet = workbook.getSheet("Results");
		Map<String, TestResult> savedResults = new HashMap<String, TestResult>();
		Sheet normalRangeSheet = workbook.getSheet("Normal ranges");

		List<ComTenantLanguage> languages = tenantLanguageService.findTenantExcelLanguages();
		Map<String, LkpContainerType> containerTypes = new HashMap<String, LkpContainerType>();
		Map<String, LkpSpecimenType> specimenTypes = new HashMap<String, LkpSpecimenType>();
		Map<String, LabSection> sections = new HashMap<String, LabSection>();
		Map<String, BillClassification> classifications = new HashMap<String, BillClassification>();
		List<LkpContainerType> containerTypeList = lkpService.findAnyLkp(new ArrayList<SearchCriterion>(), LkpContainerType.class, null);
		for (LkpContainerType containerType : containerTypeList) {
			if (!containerTypes.containsKey(containerType.getCode())) {
				containerTypes.put(containerType.getCode(), containerType);
			}
		}
		List<LkpSpecimenType> specimenTypeList = lkpService.findAnyLkp(new ArrayList<>(), LkpSpecimenType.class, null);
		for (LkpSpecimenType specimenType : specimenTypeList) {
			if (!specimenTypes.containsKey(specimenType.getCode())) {
				specimenTypes.put(specimenType.getCode(), specimenType);
			}
		}
		List<LabSection> sectionList = sectionService.findAll();
		for (LabSection section : sectionList) {
			String name = section.getName().get(languages.get(0).getComLanguage().getLocale());
			sections.put(name.toLowerCase(), section);
		}
		List<BillClassification> classificationList = classificationService.findAll();
		for (BillClassification classification : classificationList) {
			String code = classification.getCode();
			classifications.put(code.toLowerCase(), classification);
		}
		BillClassification parentClassification = classificationService.findOne(
				Arrays.asList(new SearchCriterion("code", "TEAM_LAB", FilterOperator.eq)), BillClassification.class);
		if (parentClassification == null) {
			parentClassification = new BillClassification();
			parentClassification.setIsActive(Boolean.TRUE);
			parentClassification.setCode("TEAM_LAB");
			parentClassification.setName("TEAM LAB");
			parentClassification = classificationService.addBillClassification(parentClassification);
		}

		List<SearchCriterion> billItemTypeFilters = new ArrayList<>();
		billItemTypeFilters.add(new SearchCriterion("code", "TEST", FilterOperator.eq));
		LkpBillItemType masterItemTypeTest = lkpService.findOneAnyLkp(billItemTypeFilters, LkpBillItemType.class);

		BillPriceList mohH2008List = priceListService.findOne(
				Arrays.asList(new SearchCriterion("name", "MOH H 2008", FilterOperator.contains)), BillPriceList.class);
		BillPriceList mohL2008List = priceListService.findOne(
				Arrays.asList(new SearchCriterion("name", "MOH L 2008", FilterOperator.contains)), BillPriceList.class);
		BillPriceList mohH1995List = priceListService.findOne(
				Arrays.asList(new SearchCriterion("name", "MOH H 1995", FilterOperator.contains)), BillPriceList.class);
		BillPriceList mohL1995List = priceListService.findOne(
				Arrays.asList(new SearchCriterion("name", "MOH L 1995", FilterOperator.contains)), BillPriceList.class);
		BillPriceList refPriceList = priceListService.findOne(
				Arrays.asList(new SearchCriterion("name", "Ref. Price", FilterOperator.contains)), BillPriceList.class);

		Iterator<Row> testIterator = testSheet.rowIterator();
		testIterator.next();
		while (testIterator.hasNext()) {
			int column = 0;
			Row row = testIterator.next();
			column++;
			String sectionName = row.getCell(column++).getStringCellValue().toLowerCase();
			String testCode = row.getCell(column++).getStringCellValue();
			Long rank = (long) row.getCell(column++).getNumericCellValue();
			String testName = row.getCell(column++).getStringCellValue();
			Double priceH2008 = row.getCell(column++).getNumericCellValue();
			Double priceL2008 = row.getCell(column++).getNumericCellValue();
			Double priceH1995 = row.getCell(column++).getNumericCellValue();
			Double priceL1995 = row.getCell(column++).getNumericCellValue();
			Double refPrice = row.getCell(column++).getNumericCellValue();
			column++;
			column++;
			column++;
			column++;
			column++;
			column++;
			column++;
			String containerTypeCode = (int) row.getCell(column++).getNumericCellValue() + "";

			LabSection section = sections.get(sectionName);
			BillClassification billClassification = classifications.get(sectionName);

			if (section == null && billClassification == null) {
				section = new LabSection();
				section.setIsActive(Boolean.TRUE);
				TransField nameField = new TransField();
				for (ComTenantLanguage lang : languages) {
					nameField.put(lang.getComLanguage().getLocale(), sectionName);
				}
				section.setName(nameField);
				section = sectionService.addSection(section);
				sections.put(sectionName, section);

				billClassification = new BillClassification();
				billClassification.setCode(sectionName.toUpperCase());
				billClassification.setIsActive(Boolean.TRUE);
				billClassification.setName(sectionName);
				billClassification.setSection(section);
				billClassification.setParentClassification(parentClassification);
				billClassification = classificationService.addBillClassification(billClassification);
				classifications.put(sectionName, billClassification);
			}
			LkpContainerType containerType = containerTypes.get(containerTypeCode);
			LkpSpecimenType specimenType = specimenTypes.get(containerTypeCode);
			TestDefinition test = new TestDefinition();
			test.setIsSeparatePage(Boolean.FALSE);
			test.setOrderableSeparately(Boolean.TRUE);
			test.setIsPanel(Boolean.FALSE);
			test.setIsActive(Boolean.FALSE);
			test.setStandardCode(testCode);
			test.setSecondaryCode(testCode);
			test.setRank(rank);
			test.setDescription(testName);
			test.setSection(section);
			test.setSpecimenType(specimenType);
			test = repo.save(test);
			savedTests.put(testCode, test);

			TestSpecimen specimen = new TestSpecimen();
			specimen.setTestDefinition(test);
			specimen.setContainerCount(1);
			specimen.setIsDefault(Boolean.TRUE);
			specimen.setContainerType(containerType);

			specimenService.saveTestSpecimen(specimen);

			BillMasterItem masterItem = new BillMasterItem();
			masterItem.setBillClassification(billClassification);
			masterItem.setIsActive(Boolean.TRUE);
			masterItem.setType(masterItemTypeTest);
			masterItem.setCode(testCode);
			masterItem = masterItemService.addBillMasterItem(masterItem);

			BillTestItem billTestItem = new BillTestItem();
			billTestItem.setBillMasterItem(masterItem);
			billTestItem.setTestDefinition(test);
			testItemService.addBillTestItem(billTestItem);

			createPricing(masterItem, mohH2008List, priceH2008);
			createPricing(masterItem, mohL2008List, priceL2008);
			createPricing(masterItem, mohH1995List, priceH1995);
			createPricing(masterItem, mohL1995List, priceL1995);
			createPricing(masterItem, refPriceList, refPrice);
		}

		List<LkpResultValueType> resultValueTypeList = lkpService.findAnyLkp(new ArrayList<>(), LkpResultValueType.class, null);
		Map<ResultValueType, LkpResultValueType> resultValueTypes = new HashMap<ResultValueType, LkpResultValueType>();
		for (LkpResultValueType resultValueType : resultValueTypeList) {
			resultValueTypes.put(ResultValueType.valueOf(resultValueType.getCode()), resultValueType);
		}
		List<TestCodedResult> codedResultList = codedResultService.findAll();
		Map<String, TestCodedResult> globalCodedResults = new HashMap<String, TestCodedResult>();
		for (TestCodedResult codedResult : codedResultList) {
			globalCodedResults.put(codedResult.getCode(), codedResult);
		}

		List<LkpUnitType> unitTypeList = lkpService.findAnyLkp(new ArrayList<>(), LkpUnitType.class, null);
		Map<UnitType, LkpUnitType> unitTypes = new HashMap<UnitType, LkpUnitType>();
		for (LkpUnitType unitType : unitTypeList) {
			unitTypes.put(UnitType.valueOf(unitType.getCode()), unitType);
		}

		List<LabUnit> labUnitList = labUnitService.findAll();
		Map<String, LabUnit> labUnits = new HashMap<String, LabUnit>();
		for (LabUnit labUnit : labUnitList) {
			labUnits.put(labUnit.getUnitOfMeasure(), labUnit);
		}

		Iterator<Row> resultIterator = resultSheet.iterator();
		resultIterator.next();
		resultIterator.next();
		while (resultIterator.hasNext()) {
			Row row = resultIterator.next();
			int column = 0;
			column++;
			column++;
			String testCode = ExcelUtil.getStringFromCell(row.getCell(column++));
			String resultCode = ExcelUtil.getStringFromCell(row.getCell(column++));
			String description = ExcelUtil.getStringFromCell(row.getCell(column++));
			String reportingDescription = ExcelUtil.getStringFromCell(row.getCell(column++));
			ResultValueType resultValueTypeCode = ResultValueType.valueOf(ExcelUtil.getStringFromCell(row.getCell(column++)));
			String primaryUnitTypeCode = ExcelUtil.getStringFromCell(row.getCell(column++));
			String primaryUnitCode = ExcelUtil.getStringFromCell(row.getCell(column++));
			String primaryDecimals = ExcelUtil.getStringFromCell(row.getCell(column++));
			String factor = ExcelUtil.getStringFromCell(row.getCell(column++));
			String secondaryUnitCode = ExcelUtil.getStringFromCell(row.getCell(column++));
			String secondaryDecimals = ExcelUtil.getStringFromCell(row.getCell(column++));
			String codedResults = ExcelUtil.getStringFromCell(row.getCell(column++));
			Integer printOrder = (int) row.getCell(column++).getNumericCellValue();
			TestResult testResult = new TestResult();
			testResult.setStandardCode(resultCode);
			testResult.setDescription(description);
			testResult.setReportingDescription(reportingDescription);
			testResult.setResultValueType(resultValueTypes.get(resultValueTypeCode));
			testResult.setPrintOrder(printOrder);
			testResult.setTestDefinition(savedTests.get(testCode));
			switch (resultValueTypeCode) {
				case CE:
					testResult = testResultService.addTestResult(testResult);
					savedResults.put(testCode + "|" + resultCode, testResult);
					String[] codedResultArray = codedResults.split("[|│]");
					for (String codedResult : codedResultArray) {
						codedResult = codedResult.trim();
						TestCodedResult testCodedResult = globalCodedResults.get(codedResult);
						if (testCodedResult == null) {
							testCodedResult = new TestCodedResult();
							testCodedResult.setCode(codedResult);
							testCodedResult.setValue(codedResult);
							testCodedResult = codedResultService.addTestCodedResult(testCodedResult);
							globalCodedResults.put(codedResult, testCodedResult);
						}
						TestCodedResultMapping testCodedResultMapping = new TestCodedResultMapping();
						testCodedResultMapping.setTestCodedResult(testCodedResult);
						testCodedResultMapping.setTestResult(testResult);
						codedResultMappingService.saveTestCodedResultMapping(testCodedResultMapping);
					}
					break;
				case NAR:
					testResult = testResultService.addTestResult(testResult);
					savedResults.put(testCode + "|" + resultCode, testResult);
					break;
				case QN:
				case QN_SC:
					testResult.setPrimaryUnitType(unitTypes.get(UnitType.valueOf(primaryUnitTypeCode)));
					LabUnit primaryUnit = labUnits.get(primaryUnitCode);
					if (primaryUnit == null) {
						primaryUnit = new LabUnit();
						primaryUnit.setUnitOfMeasure(primaryUnitCode);
						primaryUnit.setRecommendedReportAbbreviation(primaryUnitCode);
						primaryUnit = labUnitService.createUnit(primaryUnit);
						labUnits.put(primaryUnitCode, primaryUnit);
					}
					testResult.setPrimaryUnit(primaryUnit);
					testResult.setPrimaryDecimals(Integer.parseInt(primaryDecimals));
					if (resultValueTypeCode.equals(ResultValueType.QN_SC)) {
						testResult.setFactor(new BigDecimal(factor));
						LabUnit secondaryUnit = labUnits.get(secondaryUnitCode);
						if (secondaryUnit == null) {
							secondaryUnit = new LabUnit();
							secondaryUnit.setUnitOfMeasure(secondaryUnitCode);
							secondaryUnit.setRecommendedReportAbbreviation(secondaryUnitCode);
							secondaryUnit = labUnitService.createUnit(secondaryUnit);
							labUnits.put(secondaryUnitCode, secondaryUnit);
						}
						testResult.setSecondaryUnit(secondaryUnit);
						testResult.setSecondaryDecimals(Integer.parseInt(secondaryDecimals));
					}
					testResult = testResultService.addTestResult(testResult);
					savedResults.put(testCode + "|" + resultCode, testResult);
					break;
			}
		}

		List<LkpGender> genderList = lkpService.findAnyLkp(new ArrayList<SearchCriterion>(), LkpGender.class, null);
		Map<String, LkpGender> genders = new HashMap<String, LkpGender>();
		for (LkpGender gender : genderList) {
			genders.put(gender.getCode(), gender);
		}

		List<LkpAgeUnit> ageUnitList = lkpService.findAnyLkp(new ArrayList<SearchCriterion>(), LkpAgeUnit.class, null);
		Map<String, LkpAgeUnit> ageUnits = new HashMap<String, LkpAgeUnit>();
		for (LkpAgeUnit ageUnit : ageUnitList) {
			ageUnits.put(ageUnit.getCode().substring(0, 1), ageUnit);
		}

		Iterator<Row> normalRangeIterator = normalRangeSheet.iterator();
		normalRangeIterator.next();
		normalRangeIterator.next();

		Pattern fromPattern = Pattern.compile("(>|>=)?(\\d+)(d|w|m|y)");
		Pattern toPattern = Pattern.compile("(<|<=)?(\\d+)(d|w|m|y)");
		Pattern valuePattern = Pattern.compile("(>|>=|<|<=)?(\\d+[.\\d+]?)");

		String groupResultCode = null;
		List<TestNormalRange> groupNormalRanges = new ArrayList<TestNormalRange>();
		while (normalRangeIterator.hasNext()) {
			Row row = normalRangeIterator.next();
			if (ExcelUtil.isRowEmpty(row)) {
				continue;
			}
			int column = 0;
			String testCode = ExcelUtil.getStringFromCell(row.getCell(column++));
			String resultCode = ExcelUtil.getStringFromCell(row.getCell(column++));
			String sexCode = ExcelUtil.getStringFromCell(row.getCell(column++));
			String ageFrom = ExcelUtil.getStringFromCell(row.getCell(column++)).toLowerCase();
			String ageTo = ExcelUtil.getStringFromCell(row.getCell(column++)).toLowerCase();
			String criterionName = ExcelUtil.getStringFromCell(row.getCell(column++));
			String criterionValue = ExcelUtil.getStringFromCell(row.getCell(column++));
			String minPanicValue = ExcelUtil.getStringFromCell(row.getCell(column++)).replaceAll("\\s+", "");
			String minRerunValue = ExcelUtil.getStringFromCell(row.getCell(column++)).replaceAll("\\s+", "");
			String minNormalValue = ExcelUtil.getStringFromCell(row.getCell(column++)).replaceAll("\\s+", "");
			String maxNormalValue = ExcelUtil.getStringFromCell(row.getCell(column++)).replaceAll("\\s+", "");
			String maxRerunValue = ExcelUtil.getStringFromCell(row.getCell(column++)).replaceAll("\\s+", "");
			String maxPanicValue = ExcelUtil.getStringFromCell(row.getCell(column++)).replaceAll("\\s+", "");
			String codedResult = ExcelUtil.getStringFromCell(row.getCell(column++));

			if (groupResultCode == null) {
				groupResultCode = resultCode;
			}

			if (!resultCode.equals(groupResultCode)) {
				normalRangeService.saveNormalRanges(groupNormalRanges, groupNormalRanges.get(0).getTestResult());
				groupResultCode = resultCode;
				groupNormalRanges.clear();
			}

			TestNormalRange normalRange = new TestNormalRange();
			if (!StringUtil.isEmpty(sexCode)) {
				sexCode = sexCode.toLowerCase().startsWith("m") ? "MALE" : "FEMALE";
				normalRange.setSex(genders.get(sexCode));
			}
			Matcher ageFromMatcher = fromPattern.matcher(ageFrom);
			if (ageFromMatcher.find()) {
				String ageFromComparator = ">=";
				if (!StringUtil.isEmpty(ageFromMatcher.group(1))) {
					ageFromComparator = ageFromMatcher.group(1);
				}
				normalRange.setAgeFromComparator(ageFromComparator);
				normalRange.setAgeFrom(Integer.parseInt(ageFromMatcher.group(2)));
				normalRange.setAgeFromUnit(ageUnits.get(ageFromMatcher.group(3)));
			}

			Matcher ageToMatcher = toPattern.matcher(ageTo);
			if (ageToMatcher.find()) {
				String ageToComparator = "<=";
				if (!StringUtil.isEmpty(ageToMatcher.group(1))) {
					ageToComparator = ageToMatcher.group(1);
				}
				normalRange.setAgeToComparator(ageToComparator);
				normalRange.setAgeTo(Integer.parseInt(ageToMatcher.group(2)));
				normalRange.setAgeToUnit(ageUnits.get(ageToMatcher.group(3)));
			}
			normalRange.setCriterionName(criterionName);
			normalRange.setCriterionValue(criterionValue);
			Matcher valueMatcher;
			valueMatcher = valuePattern.matcher(minPanicValue);
			if (valueMatcher.find()) {
				normalRange.setMinPanicValue(new BigDecimal(valueMatcher.group(2)));
			}
			valueMatcher = valuePattern.matcher(minRerunValue);
			if (valueMatcher.find()) {
				normalRange.setMinRerunValue(new BigDecimal(valueMatcher.group(2)));
			}
			valueMatcher = valuePattern.matcher(minNormalValue);
			if (valueMatcher.find()) {
				normalRange.setMinValue(new BigDecimal(valueMatcher.group(2)));
			}
			valueMatcher = valuePattern.matcher(maxNormalValue);
			if (valueMatcher.find()) {
				normalRange.setMaxValue(new BigDecimal(valueMatcher.group(2)));
			}
			valueMatcher = valuePattern.matcher(maxRerunValue);
			if (valueMatcher.find()) {
				normalRange.setMaxRerunValue(new BigDecimal(valueMatcher.group(2)));
			}
			valueMatcher = valuePattern.matcher(maxPanicValue);
			if (valueMatcher.find()) {
				normalRange.setMaxPanicValue(new BigDecimal(valueMatcher.group(2)));
			}
			if (!StringUtil.isEmpty(codedResult)) {
				normalRange.setCodedResult(globalCodedResults.get(codedResult));
			}
			normalRange.setTestResult(savedResults.get(testCode + "|" + resultCode));
			normalRange.setPrintOrder(groupNormalRanges.size());
			groupNormalRanges.add(normalRange);
		}
		normalRangeService.saveNormalRanges(groupNormalRanges, groupNormalRanges.get(0).getTestResult());

		workbook.close();
	}

	public List<Integer> importTest(MultipartFile excel) throws IOException {
		String fileName = excel.getOriginalFilename();
		InputStream inputStream = excel.getInputStream();

		String extension = fileName.substring(fileName.lastIndexOf('.') + 1);
		Workbook workbook;
		if (extension.equals("xls")) {
			workbook = new HSSFWorkbook(inputStream);
		} else {
			workbook = new XSSFWorkbook(inputStream);
		}

		List<ComTenantLanguage> languages = tenantLanguageService.findTenantExcelLanguages();

		Map<Integer, LkpContainerType> containerTypes = new HashMap<Integer, LkpContainerType>();
		Map<Integer, LkpSpecimenType> specimenTypes = new HashMap<Integer, LkpSpecimenType>();

		LkpSpecimenType otherSpecimenType = new LkpSpecimenType();
		LkpContainerType otherContainerType = new LkpContainerType();
		otherSpecimenType.setCode("0");
		otherContainerType.setCode("0");
		TransField otherNameField = new TransField();
		for (ComTenantLanguage lang : languages) {
			otherNameField.put(lang.getComLanguage().getLocale(), "Other");
		}
		otherSpecimenType.setName(otherNameField);
		otherSpecimenType.setDescription(otherNameField);
		otherSpecimenType = (LkpSpecimenType) lkpService.createTenantedLkp(LkpSpecimenType.class, otherSpecimenType);
		specimenTypes.put(0, otherSpecimenType);
		otherContainerType.setName(otherNameField);
		otherContainerType.setDescription(otherNameField);
		otherContainerType.setColor("#000000");
		otherContainerType = (LkpContainerType) lkpService.createTenantedLkp(LkpContainerType.class, otherContainerType);
		containerTypes.put(0, otherContainerType);

		Sheet containerTypeSheet = workbook.getSheetAt(3);
		Iterator<Row> containerTypeIterator = containerTypeSheet.iterator();
		containerTypeIterator.next();//skip header
		while (containerTypeIterator.hasNext()) {
			Row row = containerTypeIterator.next();
			Integer code = (int) row.getCell(0).getNumericCellValue();
			String name = row.getCell(1).getStringCellValue();
			LkpContainerType containerType = new LkpContainerType();
			LkpSpecimenType specimenType = new LkpSpecimenType();
			specimenType.setCode(code.toString());
			containerType.setCode(code.toString());
			TransField nameField = new TransField();
			for (ComTenantLanguage lang : languages) {
				nameField.put(lang.getComLanguage().getLocale(), name);
			}
			specimenType.setName(nameField);
			specimenType.setDescription(nameField);
			specimenType = (LkpSpecimenType) lkpService.createTenantedLkp(LkpSpecimenType.class, specimenType);
			specimenTypes.put(code, specimenType);

			containerType.setName(nameField);
			containerType.setDescription(nameField);
			containerType.setColor("#000000");
			containerType = (LkpContainerType) lkpService.createTenantedLkp(LkpContainerType.class, containerType);
			containerTypes.put(code, containerType);
		}

		Sheet sheet = workbook.getSheetAt(0);

		Iterator<Row> rowIterator = sheet.iterator();
		rowIterator.next();//skip header

		BillClassification parentClassification = classificationService.findOne(
				Arrays.asList(new SearchCriterion("code", "TEAM_LAB", FilterOperator.eq)), BillClassification.class);
		if (parentClassification == null) {
			parentClassification = new BillClassification();
			parentClassification.setIsActive(Boolean.TRUE);
			parentClassification.setCode("TEAM_LAB");
			parentClassification.setName("TEAM LAB");
			parentClassification = classificationService.addBillClassification(parentClassification);
		}

		BillPriceList mohH2008List = createPriceList(languages, "MOH H 2008", Boolean.TRUE);
		BillPriceList mohL2008List = createPriceList(languages, "MOH L 2008", Boolean.FALSE);
		BillPriceList mohH1995List = createPriceList(languages, "MOH H 1995", Boolean.FALSE);
		BillPriceList mohL1995List = createPriceList(languages, "MOH L 1995", Boolean.FALSE);
		BillPriceList refPriceList = createPriceList(languages, "Ref. Price", Boolean.FALSE);

		List<SearchCriterion> billItemTypeFilters = new ArrayList<>();
		billItemTypeFilters.add(new SearchCriterion("code", "TEST", FilterOperator.eq));
		LkpBillItemType masterItemTypeTest = lkpService.findOneAnyLkp(billItemTypeFilters, LkpBillItemType.class);

		Map<String, LabSection> sections = new HashMap<String, LabSection>();
		Map<String, BillClassification> classifications = new HashMap<String, BillClassification>();

		Map<String, LabUnit> labUnits = new HashMap<String, LabUnit>();

		LkpResultValueType narrativeType = null;
		LkpResultValueType qnType = null;
		LkpResultValueType qnscType = null;
		List<LkpResultValueType> resultTypes = lkpService.findAnyLkp(new ArrayList<SearchCriterion>(), LkpResultValueType.class, null);
		for (LkpResultValueType resultType : resultTypes) {
			switch (ResultValueType.valueOf(resultType.getCode())) {
				case CE:
					break;
				case NAR:
					narrativeType = resultType;
					break;
				case QN:
					qnType = resultType;
					break;
				case QN_SC:
					qnscType = resultType;
					break;
			}
		}

		LkpUnitType siType = null;
		LkpUnitType convType = null;
		List<LkpUnitType> unitTypes = lkpService.findAnyLkp(new ArrayList<SearchCriterion>(), LkpUnitType.class, null);
		for (LkpUnitType unitType : unitTypes) {
			switch (UnitType.valueOf(unitType.getCode())) {
				case CONV:
					convType = unitType;
					break;
				case SI:
					siType = unitType;
					break;
			}
		}
		List<Integer> failed = new ArrayList<Integer>();
		int rowCount = 0;
		while (rowIterator.hasNext()) {
			rowCount++;
			int column = 0;
			Row row = rowIterator.next();
			column++;
			String sectionName = row.getCell(column++).getStringCellValue();
			String testCode = row.getCell(column++).getStringCellValue();
			Long rank = (long) row.getCell(column++).getNumericCellValue();
			String testName = row.getCell(column++).getStringCellValue();
			Double priceH2008 = row.getCell(column++).getNumericCellValue();
			Double priceL2008 = row.getCell(column++).getNumericCellValue();
			Double priceH1995 = row.getCell(column++).getNumericCellValue();
			Double priceL1995 = row.getCell(column++).getNumericCellValue();
			Double refPrice = row.getCell(column++).getNumericCellValue();
			column++;
			String convUnit = row.getCell(column++).getStringCellValue().trim().replace("^[.-]{1}$", "");
			String siUnit = row.getCell(column++).getStringCellValue().trim().replace("^[.-]{1}$", "");
			Double factor = row.getCell(column++).getNumericCellValue();
			column++;
			column++;
			column++;
			Integer containerTypeCode = (int) row.getCell(column++).getNumericCellValue();
			try {
				LkpContainerType containerType = containerTypes.get(containerTypeCode);
				LkpSpecimenType specimenType = specimenTypes.get(containerTypeCode);

				LabSection section = sections.get(sectionName);
				BillClassification billClassification = classifications.get(sectionName);

				if (section == null && billClassification == null) {
					section = new LabSection();
					section.setIsActive(Boolean.TRUE);
					TransField nameField = new TransField();
					for (ComTenantLanguage lang : languages) {
						nameField.put(lang.getComLanguage().getLocale(), sectionName);
					}
					section.setName(nameField);
					section = sectionService.addSection(section);
					sections.put(sectionName, section);

					billClassification = new BillClassification();
					billClassification.setCode(sectionName.toUpperCase());
					billClassification.setIsActive(Boolean.TRUE);
					billClassification.setName(sectionName);
					billClassification.setSection(section);
					billClassification.setParentClassification(parentClassification);
					billClassification = classificationService.addBillClassification(billClassification);
					classifications.put(sectionName, billClassification);
				}

				TestDefinition test = new TestDefinition();
				test.setIsSeparatePage(Boolean.FALSE);
				test.setOrderableSeparately(Boolean.TRUE);
				test.setIsPanel(Boolean.FALSE);
				test.setIsActive(Boolean.FALSE);
				test.setStandardCode(testCode);
				test.setSecondaryCode(testCode);
				test.setRank(rank);
				test.setDescription(testName);
				test.setSection(section);
				test.setSpecimenType(specimenType);
				test = repo.save(test);

				TestSpecimen specimen = new TestSpecimen();
				specimen.setTestDefinition(test);
				specimen.setContainerCount(1);
				specimen.setIsDefault(Boolean.TRUE);
				specimen.setContainerType(containerType);

				specimenService.saveTestSpecimen(specimen);

				BillMasterItem masterItem = new BillMasterItem();
				masterItem.setBillClassification(billClassification);
				masterItem.setIsActive(Boolean.TRUE);
				masterItem.setType(masterItemTypeTest);
				masterItem.setCode(testCode);
				masterItem = masterItemService.addBillMasterItem(masterItem);

				BillTestItem billTestItem = new BillTestItem();
				billTestItem.setBillMasterItem(masterItem);
				billTestItem.setTestDefinition(test);
				testItemService.addBillTestItem(billTestItem);

				createPricing(masterItem, mohH2008List, priceH2008);
				createPricing(masterItem, mohL2008List, priceL2008);
				createPricing(masterItem, mohH1995List, priceH1995);
				createPricing(masterItem, mohL1995List, priceL1995);
				createPricing(masterItem, refPriceList, refPrice);

				TestResult testResult = new TestResult();
				testResult.setTestDefinition(test);
				testResult.setStandardCode(testCode);
				testResult.setPrintOrder(1);
				if (StringUtil.isEmpty(convUnit) && StringUtil.isEmpty(siUnit)) {
					testResult.setResultValueType(narrativeType);
				} else if (StringUtil.isEmpty(convUnit)) {
					LabUnit siLabUnit = getUnit(siUnit, labUnits);
					testResult.setResultValueType(qnType);
					testResult.setPrimaryUnitType(siType);
					testResult.setPrimaryUnit(siLabUnit);
					Integer primaryDecimals = BigDecimal.valueOf(factor).scale();
					primaryDecimals = primaryDecimals == 0 ? 2 : primaryDecimals;
					testResult.setPrimaryDecimals(primaryDecimals);
				} else if (StringUtil.isEmpty(siUnit)) {
					LabUnit convLabUnit = getUnit(convUnit, labUnits);
					testResult.setResultValueType(qnType);
					testResult.setPrimaryUnitType(convType);
					testResult.setPrimaryUnit(convLabUnit);
					Integer primaryDecimals = BigDecimal.valueOf(factor).scale();
					primaryDecimals = primaryDecimals == 0 ? 2 : primaryDecimals;
					testResult.setPrimaryDecimals(primaryDecimals);
				} else {
					testResult.setResultValueType(qnscType);
					LabUnit siLabUnit = getUnit(siUnit, labUnits);
					LabUnit convLabUnit = getUnit(convUnit, labUnits);
					testResult.setPrimaryUnitType(convType);
					testResult.setPrimaryUnit(convLabUnit);
					testResult.setSecondaryUnit(siLabUnit);
					Integer decimals = BigDecimal.valueOf(factor).scale();
					decimals = decimals == 0 ? 2 : decimals;
					testResult.setPrimaryDecimals(decimals);
					testResult.setSecondaryDecimals(decimals);
					testResult.setFactor(BigDecimal.valueOf(factor));
				}
				testResultService.addTestResult(testResult);
			} catch (Exception e) {
				failed.add(rowCount);
				System.out.println("ERROR!");
				continue;
			}
		}
		workbook.close();
		return failed;
	}

	public List<Map<String, String>> fixNormalRanges(MultipartFile excel) throws IOException {
		List<Map<String, String>> amendedNormalRanges = new ArrayList<Map<String, String>>();
		String fileName = excel.getOriginalFilename();
		InputStream inputStream = excel.getInputStream();

		String extension = fileName.substring(fileName.lastIndexOf('.') + 1);
		Workbook workbook;
		if (extension.equals("xls")) {
			workbook = new HSSFWorkbook(inputStream);
		} else {
			workbook = new XSSFWorkbook(inputStream);
		}

		List<LkpGender> genderList = lkpService.findAnyLkp(new ArrayList<SearchCriterion>(), LkpGender.class, null);
		Map<String, LkpGender> genders = new HashMap<String, LkpGender>();
		for (LkpGender gender : genderList) {
			genders.put(gender.getCode(), gender);
		}

		List<LkpAgeUnit> ageUnitList = lkpService.findAnyLkp(new ArrayList<SearchCriterion>(), LkpAgeUnit.class, null);
		Map<String, LkpAgeUnit> ageUnits = new HashMap<String, LkpAgeUnit>();
		for (LkpAgeUnit ageUnit : ageUnitList) {
			ageUnits.put(ageUnit.getCode().substring(0, 1), ageUnit);
		}

		Pattern fromPattern = Pattern.compile("(>|>=)?(\\d+)(d|w|m|y)");
		Pattern toPattern = Pattern.compile("(<|<=)?(\\d+)(d|w|m|y)");

		Pattern incorrectValuePattern = Pattern.compile("(>|>=|<|<=)?(\\d+[.\\d+]?)");
		Pattern correctValuePattern = Pattern.compile("(>|>=|<|<=)?(\\d+([.][\\d]+)?)");

		Sheet sheet = workbook.getSheet("Sheet1");
		Iterator<Row> normalRangeIterator = sheet.iterator();
		normalRangeIterator.next();//skip header
		normalRangeIterator.next();//skip header

		while (normalRangeIterator.hasNext()) {
			Row row = normalRangeIterator.next();
			if (ExcelUtil.isRowEmpty(row)) {
				continue;
			}
			int column = 0;
			String testCode = ExcelUtil.getStringFromCell(row.getCell(column++));
			String resultCode = ExcelUtil.getStringFromCell(row.getCell(column++));
			String sexCode = ExcelUtil.getStringFromCell(row.getCell(column++));
			String ageFrom = ExcelUtil.getStringFromCell(row.getCell(column++)).toLowerCase();
			String ageTo = ExcelUtil.getStringFromCell(row.getCell(column++)).toLowerCase();
			String criterionName = ExcelUtil.getStringFromCell(row.getCell(column++));
			String criterionValue = ExcelUtil.getStringFromCell(row.getCell(column++));
			String minPanicValue = ExcelUtil.getStringFromCell(row.getCell(column++)).replaceAll("\\s+", "");
			String minRerunValue = ExcelUtil.getStringFromCell(row.getCell(column++)).replaceAll("\\s+", "");
			String minNormalValue = ExcelUtil.getStringFromCell(row.getCell(column++)).replaceAll("\\s+", "");
			String maxNormalValue = ExcelUtil.getStringFromCell(row.getCell(column++)).replaceAll("\\s+", "");
			String maxRerunValue = ExcelUtil.getStringFromCell(row.getCell(column++)).replaceAll("\\s+", "");
			String maxPanicValue = ExcelUtil.getStringFromCell(row.getCell(column++)).replaceAll("\\s+", "");
			String codedResult = ExcelUtil.getStringFromCell(row.getCell(column++));

			String ageFromComparator = null;
			Integer ageFromValue = null;
			LkpAgeUnit ageFromUnit = null;

			Matcher ageFromMatcher = fromPattern.matcher(ageFrom);
			if (ageFromMatcher.find()) {
				ageFromComparator = ">=";
				if (!StringUtil.isEmpty(ageFromMatcher.group(1))) {
					ageFromComparator = ageFromMatcher.group(1);
				}
				ageFromValue = Integer.parseInt(ageFromMatcher.group(2));
				ageFromUnit = ageUnits.get(ageFromMatcher.group(3));
			}

			String ageToComparator = null;
			Integer ageToValue = null;
			LkpAgeUnit ageToUnit = null;

			Matcher ageToMatcher = toPattern.matcher(ageTo);
			if (ageToMatcher.find()) {
				ageToComparator = "<=";
				if (!StringUtil.isEmpty(ageToMatcher.group(1))) {
					ageToComparator = ageToMatcher.group(1);
				}
				ageToValue = Integer.parseInt(ageToMatcher.group(2));
				ageToUnit = ageUnits.get(ageToMatcher.group(3));
			}

			BigDecimal incorrectMinValue = null;
			BigDecimal incorrectMaxValue = null;
			BigDecimal correctMinValue = null;
			String minComparator = null;
			BigDecimal correctMaxValue = null;
			String maxComparator = null;

			Matcher valueMatcher;
			valueMatcher = incorrectValuePattern.matcher(minNormalValue);
			if (valueMatcher.find()) {
				incorrectMinValue = new BigDecimal(valueMatcher.group(2));
			}
			valueMatcher = incorrectValuePattern.matcher(maxNormalValue);
			if (valueMatcher.find()) {
				incorrectMaxValue = new BigDecimal(valueMatcher.group(2));
			}
			valueMatcher = correctValuePattern.matcher(minNormalValue);
			if (valueMatcher.find()) {
				minComparator = valueMatcher.group(1);
				correctMinValue = new BigDecimal(valueMatcher.group(2));
			}
			valueMatcher = correctValuePattern.matcher(maxNormalValue);
			if (valueMatcher.find()) {
				maxComparator = valueMatcher.group(1);
				correctMaxValue = new BigDecimal(valueMatcher.group(2));
			}

			BigDecimal correctMinPanicValue = null;
			//			String minPanicComparator = null;
			BigDecimal correctMinRerunValue = null;
			//			String minRerunComparator = null;
			BigDecimal correctMaxPanicValue = null;
			//			String maxPanicComparator = null;
			BigDecimal correctMaxRerunValue = null;
			//			String maxRerunComparator = null;

			valueMatcher = correctValuePattern.matcher(minPanicValue);
			if (valueMatcher.find()) {
				//				minPanicComparator = valueMatcher.group(1);
				correctMinPanicValue = new BigDecimal(valueMatcher.group(2));
			}
			valueMatcher = correctValuePattern.matcher(minRerunValue);
			if (valueMatcher.find()) {
				//				minRerunComparator = valueMatcher.group(1);
				correctMinRerunValue = new BigDecimal(valueMatcher.group(2));
			}
			valueMatcher = correctValuePattern.matcher(maxPanicValue);
			if (valueMatcher.find()) {
				//				maxPanicComparator = valueMatcher.group(1);
				correctMaxPanicValue = new BigDecimal(valueMatcher.group(2));
			}
			valueMatcher = correctValuePattern.matcher(maxRerunValue);
			if (valueMatcher.find()) {
				//				maxRerunComparator = valueMatcher.group(1);
				correctMaxRerunValue = new BigDecimal(valueMatcher.group(2));
			}

			boolean notEqual = false;

			if (correctMinValue != null && incorrectMinValue != null) {
				if (correctMinValue.compareTo(incorrectMinValue) != 0) {
					System.out.println("Min is not equal: " + correctMinValue + ", " + incorrectMinValue);
					notEqual = true;
				}
			}

			if (correctMaxValue != null && incorrectMaxValue != null) {
				if (correctMaxValue.compareTo(incorrectMaxValue) != 0) {
					System.out.println("Max is not equal: " + correctMaxValue + ", " + incorrectMaxValue);
					notEqual = true;
				}
			}

			if (notEqual) {
				LkpGender gender = null;
				if (!StringUtil.isEmpty(sexCode)) {
					sexCode = sexCode.toLowerCase().startsWith("m") ? "MALE" : "FEMALE";
					gender = genders.get(sexCode);
				}
				TestNormalRange normalRange = normalRangeService.getIncorrectNormalRange(testCode, resultCode, incorrectMinValue,
						incorrectMaxValue, gender, ageFromComparator, ageFromValue, ageFromUnit, ageToComparator, ageToValue,
						ageToUnit);
				if (normalRange != null) {
					Map<String, String> dataMap = new HashMap<String, String>();
					dataMap.put("rid", normalRange.getRid().toString());
					dataMap.put("test", testCode);
					dataMap.put("result", resultCode);
					dataMap.put("incorrectMin", incorrectMinValue + "");
					dataMap.put("correctMin", correctMinValue + "");
					dataMap.put("incorrectMax", incorrectMaxValue + "");
					dataMap.put("correctMax", correctMaxValue + "");
					dataMap.put("minComparator", normalRange.getMinValueComparator() + "|" + minComparator);
					dataMap.put("maxComparator", normalRange.getMaxValueComparator() + "|" + maxComparator);
					dataMap.put("gender", gender != null ? gender.getCode() : null);
					dataMap.put("ageFrom", ageFromValue != null ? ageFromComparator + ageFromValue + ageFromUnit.getCode() : null);
					dataMap.put("ageTo", ageToValue != null ? ageToComparator + ageToValue + ageToUnit.getCode() : null);
					amendedNormalRanges.add(dataMap);

					normalRange.setMinValue(correctMinValue);
					normalRange.setMinValueComparator(minComparator);
					normalRange.setMaxValue(correctMaxValue);
					normalRange.setMaxValueComparator(maxComparator);
					normalRange.setMinPanicValue(correctMinPanicValue);
					normalRange.setMinRerunValue(correctMinRerunValue);
					normalRange.setMaxPanicValue(correctMaxPanicValue);
					normalRange.setMaxRerunValue(correctMaxRerunValue);
					normalRangeService.editNormalRange(normalRange);
				} else {
					if (testCode.contains("CBC")) {
						TestResult result = testResultService.findOne(
								Arrays.asList(new SearchCriterion("standardCode", resultCode, FilterOperator.eq),
										new SearchCriterion("testDefinition.standardCode", testCode, FilterOperator.eq)),
								TestResult.class);
						TestNormalRange newNormalRange = new TestNormalRange();
						newNormalRange.setTestResult(result);
						newNormalRange.setAgeFrom(ageFromValue);
						newNormalRange.setAgeFromComparator(ageFromComparator);
						newNormalRange.setAgeFromUnit(ageFromUnit);
						newNormalRange.setAgeTo(ageToValue);
						newNormalRange.setAgeToComparator(ageToComparator);
						newNormalRange.setAgeToUnit(ageToUnit);
						newNormalRange.setCriterionName(criterionName);
						newNormalRange.setCriterionValue(criterionValue);
						newNormalRange.setSex(gender);
						newNormalRange.setMinValue(correctMinValue);
						newNormalRange.setMinValueComparator(minComparator);
						newNormalRange.setMaxValue(correctMaxValue);
						newNormalRange.setMaxValueComparator(maxComparator);
						newNormalRange.setMinPanicValue(correctMinPanicValue);
						newNormalRange.setMinRerunValue(correctMinRerunValue);
						newNormalRange.setMaxPanicValue(correctMaxPanicValue);
						newNormalRange.setMaxRerunValue(correctMaxRerunValue);
						newNormalRange.setPrintOrder(1);
						newNormalRange = normalRangeService.addNormalRange(newNormalRange);

						//						Map<String, String> dataMap = new HashMap<String, String>();
						//						dataMap.put("rid", newNormalRange.getRid().toString());
						//						dataMap.put("test", testCode);
						//						dataMap.put("result", resultCode);
						//						dataMap.put("incorrectMin", incorrectMinValue + "");
						//						dataMap.put("correctMin", correctMinValue + "");
						//						dataMap.put("incorrectMax", incorrectMaxValue + "");
						//						dataMap.put("correctMax", correctMaxValue + "");
						//						dataMap.put("minComparator", minComparator);
						//						dataMap.put("maxComparator", maxComparator);
						//						dataMap.put("gender", gender != null ? gender.getCode() : null);
						//						dataMap.put("ageFrom", ageFromValue != null ? ageFromComparator + ageFromValue + ageFromUnit.getCode() : null);
						//						dataMap.put("ageTo", ageToValue != null ? ageToComparator + ageToValue + ageToUnit.getCode() : null);
						//						amendedNormalRanges.add(dataMap);
					}
				}
			}

		}
		workbook.close();
		return amendedNormalRanges;
	}

	private LabUnit getUnit(String unit, Map<String, LabUnit> labUnits) {
		LabUnit labUnit = labUnits.get(unit);
		if (labUnit == null) {
			labUnit = new LabUnit();
			labUnit.setUnitOfMeasure(unit);
			labUnit.setRecommendedReportAbbreviation(unit);
			labUnit = labUnitService.createUnit(labUnit);
			labUnits.put(unit, labUnit);
		}
		return labUnit;
	}

	private void createPricing(BillMasterItem masterItem, BillPriceList priceList, Double price) {
		BillPricing pricing = new BillPricing();
		pricing.setBillMasterItem(masterItem);
		pricing.setBillPriceList(priceList);
		pricing.setPrice(BigDecimal.valueOf(price));
		//TODO what are the start and end dates?
		pricing.setStartDate(new Date());
		try {
			pricingService.addBillPricing(pricing);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private BillPriceList createPriceList(List<ComTenantLanguage> languages, String name, Boolean isDefault) {
		BillPriceList priceList = new BillPriceList();
		priceList.setIsDefault(isDefault);
		TransField priceListName = new TransField();
		for (ComTenantLanguage lang : languages) {
			priceListName.put(lang.getComLanguage().getLocale(), name);
		}
		priceList.setName(priceListName);
		return priceListService.addBillPriceList(priceList);
	}

	@PreAuthorize("hasAuthority('" + EhopeRights.VIEW_TEST_GROUP + "')")
	public Page<TestDefinition> getTestsDefaultPricingPage(FilterablePageRequest filterablePageRequest) {
		Page<TestDefinition> page = getRepository().getTestsDefaultPricingPage(DateUtil.getCurrentDateWithoutTime(),
				filterablePageRequest.getLongFilter("rid"), filterablePageRequest.getStringFilter("standardCode"),
				filterablePageRequest.getStringFilter("description"), filterablePageRequest.getStringFilter("secondaryCode"),
				filterablePageRequest.getStringFilter("aliases"),
				filterablePageRequest.getPageRequest());

		if (page.getTotalElements() == 0) {
			return page;
		}
		List<TestDefinition> tests = getAllTestsPricings(Arrays.asList(new SearchCriterion("rid",
				page.getContent().stream().map(TestDefinition::getRid).collect(Collectors.toList()), FilterOperator.in)),
				filterablePageRequest.getSortObject());
		Page<TestDefinition> testsPage = new PageImpl<>(tests, filterablePageRequest.getPageRequest(), page.getTotalElements());
		return testsPage;
	}

	public List<TestDefinition> getAllTestsPricings(List<SearchCriterion> filters, Sort sort) {
		return getRepository()	.find(filters, TestDefinition.class, sort, "billTestItems.billMasterItem.billPricings.billPriceList")
								.stream().distinct()
								.map(t ->
									{
										//get the active pricing
										for (BillTestItem bti : t.getBillTestItems()) {
											bti	.getBillMasterItem().getBillPricings()
												.removeIf(bp -> !pricingService.isPricingActive(bp));
										}
										return t;
									})
								.collect(Collectors.toList());
	}

	public void importReferralPrices(MultipartFile excel) throws IOException {
		String fileName = excel.getOriginalFilename();
		InputStream inputStream = excel.getInputStream();

		String extension = fileName.substring(fileName.lastIndexOf('.') + 1);
		Workbook workbook;
		if (extension.equals("xls")) {
			workbook = new HSSFWorkbook(inputStream);
		} else {
			workbook = new XSSFWorkbook(inputStream);
		}

		List<InsProvider> localClients = insProviderService.getClients(TestDestinationType.LOCAL, ClientPurpose.DESTINATION, null);
		List<InsProvider> externalClients = insProviderService.getClients(TestDestinationType.EXTERNAL, null, null);
		localClients.addAll(externalClients);
		Map<String, InsProvider> destinations = localClients.stream().collect(Collectors.toMap(InsProvider::getCode, x -> x));

		Sheet priceSheet = workbook.getSheet("Prices");
		Iterator<Row> priceIterator = priceSheet.iterator();
		priceIterator.next();//skip header
		while (priceIterator.hasNext()) {
			Row row = priceIterator.next();
			String testCode = ExcelUtil.getStringFromCell(row.getCell(0));
			//String priceString = ExcelUtil.getStringFromCell(row.getCell(1));
			String locationCode = ExcelUtil.getStringFromCell(row.getCell(2));

			InsProvider client = destinations.get(locationCode);

			BillPriceList priceList = client.getPriceList();

			TestDefinition testDefinition = repo.findOne(Arrays.asList(new SearchCriterion("standardCode", testCode, FilterOperator.eq)),
					TestDefinition.class, "billTestItems.billMasterItem.type", "billTestItems.billMasterItem.billPricings");
			BillMasterItem billMasterItem = testDefinition	.getBillTestItems().stream().filter(
					bti -> BillMasterItemType	.valueOf(bti.getBillMasterItem().getType().getCode())
												.equals(BillMasterItemType.TEST))
															.collect(Collectors.toList()).get(0).getBillMasterItem();

			billMasterItem.getBillPricings().forEach(x -> System.out.println(x.getBillPriceList().getName().get("en_us")));

			BillPricing pricing = billMasterItem.getBillPricings().stream()
												.filter(bp -> bp.getBillPriceList().getName().get("en_us").equals("Ref. Price"))
												.collect(Collectors.toList()).get(0);

			pricing.setBillPriceList(priceList);
			pricingService.editBillPricing(pricing);
		}

		workbook.close();
	}
}

package com.optimiza.ehope.lis.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.optimiza.core.admin.model.SecUser;
import com.optimiza.core.base.helper.SearchCriterion;
import com.optimiza.core.base.helper.SearchCriterion.FilterOperator;
import com.optimiza.core.base.helper.SearchCriterion.JunctionOperator;
import com.optimiza.core.base.service.GenericService;
import com.optimiza.core.common.business.exception.BusinessException;
import com.optimiza.core.common.business.exception.BusinessException.ErrorSeverity;
import com.optimiza.core.common.util.CollectionUtil;
import com.optimiza.core.common.util.JSONUtil;
import com.optimiza.core.common.util.SecurityUtil;
import com.optimiza.core.common.util.StringUtil;
import com.optimiza.ehope.lis.helper.EhopeRights;
import com.optimiza.ehope.lis.lkp.helper.OperationStatus;
import com.optimiza.ehope.lis.lkp.helper.OrganismDetection;
import com.optimiza.ehope.lis.lkp.helper.ResultValueType;
import com.optimiza.ehope.lis.model.ActualResultNormalRange;
import com.optimiza.ehope.lis.model.EmrPatientInfo;
import com.optimiza.ehope.lis.model.EmrVisit;
import com.optimiza.ehope.lis.model.LabTestActual;
import com.optimiza.ehope.lis.model.LabTestActualResult;
import com.optimiza.ehope.lis.model.TestDefinition;
import com.optimiza.ehope.lis.model.TestDestination;
import com.optimiza.ehope.lis.model.TestNormalRange;
import com.optimiza.ehope.lis.model.TestResult;
import com.optimiza.ehope.lis.repo.LabTestActualResultRepo;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

@Service("LabTestActualResultService")
public class LabTestActualResultService extends GenericService<LabTestActualResult, LabTestActualResultRepo> {

	@Autowired
	private LabTestActualResultRepo repo;
	@Autowired
	private TestResultService testResultService;
	@Autowired
	private EmrVisitService visitService;
	@Autowired
	private LabTestActualService testActualService;
	@Autowired
	private ActualAntiMicrobialService actualAntiMicrobialService;
	@Autowired
	private ActualOrganismService actualOrganismService;
	@Autowired
	private TestDestinationService testDestinationService;
	@Autowired
	private TestNormalRangeService testNormalRangeService;
	@Autowired
	private AmendedActualResultService amendedActualResultService;
	@Autowired
	private EmrPatientInfoService patientService;
	@Autowired
	private ActualResultNormalRangeService actualResultNormalRangeService;
	@Autowired
	private EntityManager entityManager;

	@Override
	protected LabTestActualResultRepo getRepository() {
		return repo;
	}

	/**
	 * Get LabTestActualResult list by a list of LabTestActual
	 * 
	 * @param testActualList
	 * @return LabTestActualResult List
	 */
	public List<LabTestActualResult> getActualTestResultsByTestActual(List<LabTestActual> testActualList) {
		List<SearchCriterion> testActualsRidFilter = testActualList	.stream().map(
				lta -> new SearchCriterion("labTestActual.rid", lta.getRid(), FilterOperator.eq, JunctionOperator.Or))
																	.collect(Collectors.toList());
		return repo.find(testActualsRidFilter, LabTestActualResult.class, "labTestActual");
	}

	public List<LabTestActualResult> getActualTestResultsByVisit(Long visitRid) {
		return repo.getActualTestResultsByVisit(visitRid);
	}

	public List<LabTestActualResult> addTestActualResult(TestDefinition selectedTest, LabTestActual actualTest) {
		Set<TestResult> testResults = new HashSet<>(testResultService.find(
				Arrays.asList(new SearchCriterion("testDefinition", selectedTest, FilterOperator.eq)), TestResult.class));

		List<LabTestActualResult> testActualResultList = new ArrayList<>();
		if (testResults != null) {
			for (TestResult testResult : testResults) {
				LabTestActualResult actualTestResult = new LabTestActualResult();
				actualTestResult.setLabTestActual(actualTest);

				actualTestResult.setLabResult(testResult);
				Set<TestNormalRange> normalRanges = getNormalRangesForGeneration(actualTest.getRid(), testResult.getRid(),
						actualTest.getTestDestination().getRid());
				actualTestResult.setNormalRangeText(
						generateNormalRangeTxt(testResult.getRid(), actualTestResult, Boolean.FALSE, normalRanges));
				actualTestResult = repo.save(actualTestResult);
				testActualResultList.add(actualTestResult);
				saveActualResultNormalRanges(actualTestResult, normalRanges);
				//				testResult.setNormalRanges(new HashSet<>());//self reference overflow
			}
		}

		return testActualResultList;
	}

	/**
	 * Provides the needed normal-ranges to be used in the generation of normalRangeText value
	 * Also used in creating ActualResultNormalRange items
	 * 
	 * @param labTestActualRid rid of the actual-test
	 * @param testResultRid rid of the test-result
	 * @param testDestinationRid rid of the destination
	 */
	public Set<TestNormalRange> getNormalRangesForGeneration(Long labTestActualRid, Long testResultRid, Long testDestinationRid) {
		LabTestActual labTestActual = testActualService.getRepository().findOne(
				SearchCriterion.generateRidFilter(labTestActualRid, FilterOperator.eq), LabTestActual.class,
				"labSample.emrVisit.emrPatientInfo");
		EmrPatientInfo patient = patientService.findOne(
				SearchCriterion.generateRidFilter(labTestActual.getLabSample().getEmrVisit().getEmrPatientInfo().getRid(),
						FilterOperator.eq),
				EmrPatientInfo.class, "gender");
		TestResult testResult = testResultService.findOne(SearchCriterion.generateRidFilter(testResultRid, FilterOperator.eq),
				TestResult.class, "primaryUnit", "resultValueType", "normalRanges.codedResult");
		TestDestination testDestination = testDestinationService.findOne(
				SearchCriterion.generateRidFilter(testDestinationRid, FilterOperator.eq), TestDestination.class,
				"normalRanges.codedResult", "normalRanges.sex", "normalRanges.testResult");
		//only keep normal ranges that are connected to testResult
		testDestination.getNormalRanges().removeIf(nr -> !nr.getTestResult().getRid().equals(testResultRid));
		Set<TestNormalRange> normalRanges = !CollectionUtil.isCollectionEmpty(testDestination.getNormalRanges())
				? testDestination.getNormalRanges() : testResult.getNormalRanges();
		normalRanges.removeIf(nr -> !nr.getIsActive());//remove in-active normal ranges

		normalRanges = testNormalRangeService.filterNormalRange(patient, normalRanges);
		return normalRanges;
	}

	public void saveActualResultNormalRanges(LabTestActualResult actualResult, Set<TestNormalRange> normalRanges) {
		for (TestNormalRange tnr : normalRanges) {
			ActualResultNormalRange actualResultNormalRange = new ActualResultNormalRange();
			actualResultNormalRange.setActualResult(actualResult);
			actualResultNormalRange.setNormalRange(tnr);
			actualResultNormalRangeService.saveActualResultNormalRange(actualResultNormalRange);
		}
	}

	/**
	 * Set the normal range text using testDestination normal ranges if they exist, otherwise use the testResult's defaults.
	 * Also saves the ActualResultNormalRange set for reference.
	 * 
	 * @param testResultRid rid of the test-result
	 * @param actualTestResult the actual-result
	 * @param isWithUnit boolean indicating whether to print the unit or not
	 * @param normalRanges set of NormalRanges to be used
	 */
	public String generateNormalRangeTxt(Long testResultRid, LabTestActualResult actualTestResult, Boolean isWithUnit,
			Set<TestNormalRange> normalRanges) {
		TestResult testResult = testResultService.findOne(SearchCriterion.generateRidFilter(testResultRid, FilterOperator.eq),
				TestResult.class, "primaryUnit", "resultValueType", "normalRanges.codedResult");

		List<String> normalRangeJsonList = new ArrayList<>();
		for (TestNormalRange tnr : normalRanges) {
			Map<String, String> map = new HashMap<>();
			String value = testNormalRangeService.generateNormalRangeCriterionDescription(tnr);
			map.put("Criterion", value);
			value = testNormalRangeService.generateNormalRangeValueDescription(tnr, testResult, Boolean.TRUE, isWithUnit);
			map.put("Primary", value);
			if (ResultValueType.QN_SC.equals(ResultValueType.valueOf(testResult.getResultValueType().getCode()))) {
				value = testNormalRangeService.generateNormalRangeValueDescription(tnr, testResult, Boolean.FALSE, isWithUnit);
				map.put("Secondary", value);
			} else {
				map.put("Secondary", "");
			}
			String normalRangeJson = JSONUtil.convertMapToJSON(map);
			normalRangeJsonList.add(normalRangeJson);
		}

		if (CollectionUtil.isCollectionEmpty(normalRangeJsonList)) {
			return null;
		} else {
			return String.join("|||", normalRangeJsonList);
		}
	}

	public LabTestActualResult findByResultCodeAndBarcode(String resultCode, String barcode) {
		return repo.findByResultCodeAndBarcode(resultCode, barcode);
	}

	@SuppressWarnings("unchecked")
	@PreAuthorize("hasAuthority('" + EhopeRights.UPD_TEST_ACTUAL_RESULT + "')")
	public void editTestActualResults(List<LabTestActualResult> actualResults, Long visitRid, boolean isMachine) {
		OperationStatus visitOperationStatus = OperationStatus.valueOf(
				visitService.findOne(SearchCriterion.generateRidFilter(visitRid, FilterOperator.eq), EmrVisit.class, "lkpOperationStatus")
							.getLkpOperationStatus().getCode());
		if (visitOperationStatus == OperationStatus.ABORTED || visitOperationStatus == OperationStatus.CANCELLED) {
			throw new BusinessException("Cannot enter results for non-open visits!", "noResultEntryOnNonOpenVisit", ErrorSeverity.ERROR);
		}

		Map<String, Map<String, Object>> comprehensiveResults = new HashMap<String, Map<String, Object>>();
		for (LabTestActualResult actualResult : actualResults) {
			String resultCode = actualResult.getLabResult().getStandardCode();
			if (actualResult.getLabResult().getIsComprehensive()) {
				Map<String, Object> tempComprehensiveResult = comprehensiveResults.get(resultCode);
				if (tempComprehensiveResult == null) {
					tempComprehensiveResult = new HashMap<String, Object>();
					tempComprehensiveResult.put("comprehensiveResult", actualResult);
					tempComprehensiveResult.put("differentials", new ArrayList<LabTestActualResult>());
					comprehensiveResults.put(resultCode, tempComprehensiveResult);
				} else {
					tempComprehensiveResult.put("comprehensiveResult", actualResult);
				}
			} else if (actualResult.getLabResult().getIsDifferential()) {
				Map<String, Object> tempComprehensiveResult = comprehensiveResults.get(
						actualResult.getLabResult().getComprehensiveResult().getStandardCode());
				if (tempComprehensiveResult == null) {
					tempComprehensiveResult = new HashMap<String, Object>();
					tempComprehensiveResult.put("differentials", new ArrayList<LabTestActualResult>());
					comprehensiveResults.put(actualResult.getLabResult().getComprehensiveResult().getStandardCode(),
							tempComprehensiveResult);
				}
				((List<LabTestActualResult>) tempComprehensiveResult.get("differentials")).add(actualResult);
			}
		}

		comprehensiveResults.forEach((k, v) ->
			{
				LabTestActualResult comprehensiveResult = (LabTestActualResult) v.get("comprehensiveResult");
				Integer diffCountInDb = repo.findNumberOfDifferentialsByComprehensive(comprehensiveResult.getLabResult(), visitRid);
				BigDecimal totalValue = comprehensiveResult.getPrimaryResultParsed();
				Integer totalPercentages = 0;
				List<LabTestActualResult> differentials = (ArrayList<LabTestActualResult>) v.get("differentials");
				if (!diffCountInDb.equals(differentials.size())) {
					throw new BusinessException("All differential results must be sent!", "allDifferentialsMustBeSent",
							ErrorSeverity.ERROR);
				}
				for (LabTestActualResult actualResult : differentials) {
					Integer percentage = actualResult.getPercentage();
					totalPercentages += percentage;
					actualResult.setPrimaryResultValue(
							totalValue
										.multiply(new BigDecimal(percentage))
										.divide(new BigDecimal(100))
										.setScale(actualResult.getLabResult().getPrimaryDecimals(), RoundingMode.HALF_UP)
										.toString());
				}
				if (!isMachine && !totalPercentages.equals(100)) {
					throw new BusinessException("Sum of all differential result percentages should be 100!", "differentialValidation",
							ErrorSeverity.ERROR);
				}
			});

		for (LabTestActualResult actualResult : actualResults) {
			OperationStatus currentStatus = OperationStatus.valueOf(actualResult.getLabTestActual().getLkpOperationStatus().getCode());
			if (currentStatus == OperationStatus.FINALIZED || currentStatus == OperationStatus.CLOSED) {
				amendedActualResultService.createAmendedActualResult(actualResult);
				actualResult.setIsAmended(Boolean.TRUE);
				if (StringUtil.isEmpty(actualResult.getAmendmentReason())) {
					throw new BusinessException("Must enter amendment reason!", "mustEnterAmendmentReason", ErrorSeverity.ERROR);
				}
			}
			validateAndParseResult(actualResult, isMachine, comprehensiveResults);
			repo.save(actualResult);
		}
		//flushing the changes and clearing the persisted entities so we can re-fetch them again down below
		entityManager.flush();
		entityManager.clear();
		List<LabTestActual> labTestActualList = getTestActualListWithRequiredResults(visitRid);
		//Only propagate tests that we entered results for
		labTestActualList.removeIf(lta ->
			{
				OperationStatus ltaStatus = OperationStatus.valueOf(lta.getLkpOperationStatus().getCode());
				if (ltaStatus.getOrder() >= OperationStatus.RESULTS_ENTERED.getOrder()) {
					return true;
				}
				for (LabTestActualResult actualResult : actualResults) {
					if (actualResult.getLabTestActual().getRid().equals(lta.getRid())) {
						return false;
					}
				}
				return true;
			});
		testActualService.propegateTestsStatusesNoAuth(visitRid,
				labTestActualList.stream().map(LabTestActual::getRid).collect(Collectors.toList()),
				OperationStatus.RESULTS_ENTERED, null);
	}

	/**
	 * Get all filled results in tests for this visit
	 * 
	 * @param visitRid
	 * @return LabTestActual
	 */
	public List<LabTestActual> getTestActualListWithRequiredResults(Long visitRid) {
		List<LabTestActual> labTestActualList = new ArrayList<LabTestActual>(
				testActualService.findTestActualListWithResultsByVisit(visitRid, null));

		labTestActualList.removeIf(t ->
			{
				for (LabTestActualResult result : t.getLabTestActualResults()) {
					return removeResultIfEmptyAndNotRequired(result);
				}
				return false;
			});
		return labTestActualList;
	}

	public Boolean removeResultIfEmptyAndNotRequired(LabTestActualResult result) {
		ResultValueType resultValueType = ResultValueType.valueOf(result.getLabResult().getResultValueType().getCode());
		Boolean isResultRequired = result.getLabResult().getIsRequired();
		switch (resultValueType) {
			case NAR:
				if (StringUtils.isEmpty(result.getNarrativeText()) && isResultRequired) {
					return true;
				}
				break;
			case CE:
				if (result.getTestCodedResult() == null && isResultRequired) {
					return true;
				}
				break;
			case QN:
			case QN_SC:
			case QN_QL:
				if (StringUtils.isEmpty(result.getPrimaryResultValue()) && isResultRequired) {
					return true;
				}
				break;
			case ORG:
				if (result.getOrganismDetection() == null) {
					if (isResultRequired) {
						return true;
					}
				} else {
					OrganismDetection detection = OrganismDetection.valueOf(result.getOrganismDetection().getCode());
					switch (detection) {
						case GROWTH:
							return CollectionUtil.isCollectionEmpty(result.getActualAntiMicrobials()) && isResultRequired;
						case NO_GROWTH:
							return false;
					}
				}
				break;
			case RATIO:
				if (StringUtil.isEmpty(result.getRatio()) && isResultRequired) {
					return true;
				}
				break;
		}
		return false;
	}

	private void validateAndParseResult(LabTestActualResult actualResult, boolean isMachine,
			Map<String, Map<String, Object>> comprehensiveResults) {
		TestResult testResult = actualResult.getLabResult();
		ResultValueType resultValueType = ResultValueType.valueOf(testResult.getResultValueType().getCode());
		switch (resultValueType) {
			case ORG:
				OrganismDetection organismDetection = OrganismDetection.valueOf(actualResult.getOrganismDetection().getCode());
				switch (organismDetection) {
					case GROWTH:
						actualOrganismService.saveActualOrganisms(actualResult.getActualOrganismList(), actualResult);
						actualAntiMicrobialService.saveActualAntiMicrobials(actualResult.getActualAntiMicrobialList(), actualResult);

						//TODO is this a requirement?
						//						List<ActualOrganism> savedActualOrganisms = actualOrganismService.saveActualOrganisms(
						//								actualResult.getActualOrganismList(), actualResult);
						//						if (savedActualOrganisms.size() == 0) {
						//							actualAntiMicrobialService.deleteActualAntiMicrobials(actualResult.getActualAntiMicrobialList(), actualResult);
						//						} else {
						//							actualAntiMicrobialService.saveActualAntiMicrobials(actualResult.getActualAntiMicrobialList(), actualResult);
						//						}
						break;
					case NO_GROWTH:
						actualOrganismService.deleteActualOrganisms(actualResult.getActualOrganismList(), actualResult);
						actualAntiMicrobialService.deleteActualAntiMicrobials(actualResult.getActualAntiMicrobialList(), actualResult);
						break;
				}
				break;
			case NAR:
				if (StringUtils.isEmpty(actualResult.getNarrativeText())) {
					throw new BusinessException("Narrative type must have value!", "emptyNarrativeValue", ErrorSeverity.ERROR);
				}
				break;
			case CE:
				if (actualResult.getTestCodedResult() == null) {
					throw new BusinessException("Coded type must have value!", "emptyCodedValue", ErrorSeverity.ERROR);
				}
				break;
			case QN:
			case QN_SC:
			case QN_QL:
				Integer decimals = testResult.getPrimaryDecimals();
				if (resultValueType.equals(ResultValueType.QN_QL)) {
					decimals = testResult.getTestDefinition().getAllergyDecimals();
				}
				if (isMachine) {
					parseAndSetValues(testResult, decimals, actualResult, resultValueType);
					break;
				}
				String primaryResultValue = actualResult.getPrimaryResultValue();
				Pattern r = generateRegexPattern(decimals);
				Matcher m = r.matcher(primaryResultValue);
				if (m.find()) {
					parseAndSetValues(testResult, decimals, actualResult, resultValueType);
				} else {
					throw new BusinessException("Invalid quantitative value!", "invalidQuantitativeValue", ErrorSeverity.ERROR);
				}
				break;
			case RATIO:
				String regex = "(<|>)?\\s*[0-9]+\\s*:\\s*[0-9]+";
				Pattern pattern = Pattern.compile(regex);
				Matcher matcher = pattern.matcher(actualResult.getRatio());
				if (!matcher.find()) {
					throw new BusinessException("Invalid ratio pattern", "invalidRatioPattern", ErrorSeverity.ERROR);
				}
				break;
		}
	}

	private Pattern generateRegexPattern(Integer decimals) {
		String starterRegex = "((<=?)|(>=?)|(-))?";
		String decimalRegex = "[0-9]+([.](?=[0-9])[0-9]{0," + decimals + "})?";
		String numberRegex = "[0-9]+";
		String regex1 = decimalRegex + "(-" + decimalRegex + ")?";
		String regex2 = starterRegex + decimalRegex;
		String regex3 = numberRegex + ":" + numberRegex;
		String regex4 = decimalRegex + "%";
		String pattern = "^(" + regex1 + "|" + regex2 + "|" + regex3 + "|" + regex4 + ")$";
		return Pattern.compile(pattern);
	}

	private void parseAndSetValues(TestResult testResult, Integer primaryDecimals, LabTestActualResult actualResult,
			ResultValueType resultValueType) {
		Pattern pat = Pattern.compile("-?\\d+[.]?\\d*");
		Matcher matcher = pat.matcher(actualResult.getPrimaryResultValue());
		if (matcher.find()) {
			String parsed = matcher.group();
			BigDecimal primaryParsed = new BigDecimal(parsed).setScale(primaryDecimals, RoundingMode.HALF_UP);
			actualResult.setPrimaryResultParsed(primaryParsed);
			if (resultValueType.equals(ResultValueType.QN_SC)) {
				BigDecimal secondaryParsed = primaryParsed	.multiply(testResult.getFactor())
															.setScale(testResult.getSecondaryDecimals(),
																	RoundingMode.HALF_UP);
				actualResult.setSecondaryResultParsed(secondaryParsed);
			}
		}
	}

	public LabTestActualResult save(LabTestActualResult actualResult) {
		return repo.save(actualResult);
	}

	public void deleteAllByLabTestActual(LabTestActual testActual) {
		getRepository().deleteAllByLabTestActual(testActual);
	}

	public List<LabTestActualResult> findByTestActual(Long testActualRid) {
		return repo.find(Arrays.asList(new SearchCriterion("labTestActual.rid", testActualRid, FilterOperator.eq)),
				LabTestActualResult.class, "labTestActual");
	}

	@PreAuthorize("hasAuthority('" + EhopeRights.GENERATE_RESULTS_REPORT + "')")
	public Map<String, Object> getResultReportData(Long visitRid) {
		List<SearchCriterion> visitFilters = new ArrayList<SearchCriterion>();
		visitFilters.add(new SearchCriterion("rid", visitRid, FilterOperator.eq));
		EmrVisit visit = visitService.findOne(visitFilters, EmrVisit.class, "emrPatientInfo");

		EmrPatientInfo patient = visit.getEmrPatientInfo();
		List<LabTestActualResult> results = getRepository().getActualTestResultsByVisit(visitRid);

		//		List<LkpAgeUnit> ageUnits = lkpService.findAnyLkp(new ArrayList<SearchCriterion>(), LkpAgeUnit.class, null);
		//		LkpAgeUnit ageUnit = matchChronoUnitWithLkpAgeUnit(ageUnits, patient.getAgeWithUnit().getUnit());
		//		Long patientAge = patient.getAgeWithUnit().getAge();
		//		for (int i = 0; i < results.size(); i++) {
		//			LabTestActualResult result = results.get(i);
		//
		//			List<SearchCriterion> normalRangeFilters = new ArrayList<SearchCriterion>();
		//			normalRangeFilters.add(new SearchCriterion("testDefinition", result.getTestDefinitionId(), FilterOperator.eq));
		//			normalRangeFilters.add(new SearchCriterion("sex", patient.getGender(), FilterOperator.eq));
		//
		//			normalRangeFilters.add(new SearchCriterion("ageFromUnit", ageUnit, FilterOperator.eq, JunctionOperator.Or));
		//			normalRangeFilters.add(new SearchCriterion("ageFrom", patientAge, FilterOperator.gte, JunctionOperator.Or));
		//
		//			normalRangeFilters.add(new SearchCriterion("ageToUnit", ageUnit, FilterOperator.eq, JunctionOperator.Or));
		//			normalRangeFilters.add(new SearchCriterion("ageTo", patientAge, FilterOperator.lte, JunctionOperator.Or));
		//
		//			normalRangeFilters.add(new SearchCriterion("ageUnit", ageUnit, FilterOperator.eq, JunctionOperator.Or));
		//			normalRangeFilters.add(new SearchCriterion("age", patientAge, FilterOperator.eq, JunctionOperator.Or));
		//			List<TestNormalRange> normalRanges = normalRangeService.find(normalRangeFilters, TestNormalRange.class,
		//					"ageFromUnit", "ageToUnit", "ageUnit", "signum", "unit");
		//
		//			for (int j = 0; j < normalRanges.size(); j++) {
		//				System.out.println(normalRanges.get(j));
		//			}
		//
		//		}

		SecUser user = SecurityUtil.getCurrentUser();

		JRDataSource resultsDataSource = new JRBeanCollectionDataSource(results);

		Map<String, Object> parameterMap = new HashMap<String, Object>();
		parameterMap.put("user", user);
		parameterMap.put("visit", visit);
		parameterMap.put("patient", patient);
		parameterMap.put("datasource", resultsDataSource);

		return parameterMap;
	}

	//	private LkpAgeUnit matchChronoUnitWithLkpAgeUnit(List<LkpAgeUnit> ageUnits, ChronoUnit chronoUnit) {
	//		String chronoUnitStr = chronoUnit.toString().toLowerCase();
	//		for (int i = 0; i < ageUnits.size(); i++) {
	//			LkpAgeUnit ageUnit = ageUnits.get(i);
	//			if (ageUnit.getCode().equals(chronoUnitStr.substring(0, chronoUnitStr.length() - 1).toLowerCase())) {
	//				return ageUnit;
	//			}
	//		}
	//		return null;
	//	}
}

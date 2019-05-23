package com.optimiza.ehope.lis.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.optimiza.core.base.service.GenericService;
import com.optimiza.core.common.business.exception.BusinessException;
import com.optimiza.core.common.business.exception.BusinessException.ErrorSeverity;
import com.optimiza.ehope.lis.lkp.helper.ResultValueType;
import com.optimiza.ehope.lis.lkp.helper.SectionType;
import com.optimiza.ehope.lis.model.NarrativeResultTemplate;
import com.optimiza.ehope.lis.model.TestCodedResult;
import com.optimiza.ehope.lis.model.TestCodedResultMapping;
import com.optimiza.ehope.lis.model.TestDefinition;
import com.optimiza.ehope.lis.model.TestNormalRange;
import com.optimiza.ehope.lis.model.TestResult;
import com.optimiza.ehope.lis.repo.TestResultRepo;

@Service("TestResultService")
public class TestResultService extends GenericService<TestResult, TestResultRepo> {

	@Autowired
	private TestResultRepo repo;

	@Autowired
	private TestCodedResultMappingService codedResultMappingService;

	@Autowired
	private TestNormalRangeService normalRangeService;

	@Autowired
	private NarrativeResultTemplateService narrativeResultTemplateService;

	@Override
	protected TestResultRepo getRepository() {
		return repo;
	}

	public TestResult addTestResult(TestResult testResult) {
		return repo.save(testResult);
	}

	public TestResult editTestResult(TestResult testResult) {
		return repo.save(testResult);
	}

	public List<TestResult> saveTestResults(List<TestResult> testResults, SectionType sectionType) {
		Set<String> testResultCodeSet = new HashSet<String>();
		testResults.sort((o1, o2) -> o2.getIsComprehensive().compareTo(o1.getIsComprehensive()));
		for (int i = 0; i < testResults.size(); i++) {
			TestResult testResult = testResults.get(i);
			List<TestNormalRange> normalRanges;
			if (testResult.getMarkedForDeletion()) {
				if (testResult.getRid() != null) {
					if (ResultValueType.valueOf(testResult.getResultValueType().getCode()) == ResultValueType.CE) {
						//this deletes related mappings if type is CODED_ENTRY
						codedResultMappingService.saveTestCodedResultMappings(new ArrayList<TestCodedResultMapping>(), testResult);
						testResult.setTestCodedResultMappings(null);
					}
					normalRangeService.deleteNormalRangesByResultId(testResult.getRid());
					testResult.setNormalRanges(null);
					repo.delete(testResult);
				}
				testResults.remove(i);
				i--;
			} else {
				if (!testResultCodeSet.add(testResult.getStandardCode())) {
					throw new BusinessException("Duplicate Result Standard Code!", "duplicateResultStandardCode", ErrorSeverity.ERROR,
							Arrays.asList(testResult.getStandardCode()));
				}
				String typeCode = testResult.getResultValueType().getCode();
				ResultValueType resultType = ResultValueType.valueOf(typeCode);
				if (SectionType.ALLERGY.equals(sectionType) && !ResultValueType.QN_QL.equals(resultType)) {
					throw new BusinessException(
							"Tests which belong to ALLERGY must have results of type Quantitative/Qualitative",
							"allergyMustHaveQuantitativeQualitative", ErrorSeverity.ERROR);
				}
				if ((testResult.getIsComprehensive() || testResult.getIsDifferential())
						&& !ResultValueType.QN.equals(resultType)) {
					throw new BusinessException(
							"Only results of type QN can be used in the comprehensive/differential relationship",
							"comprehensiveDifferentialMustBeQN", ErrorSeverity.ERROR);
				}
				if (testResult.getIsDifferential() && testResult.getComprehensiveResult() == null) {
					throw new BusinessException(
							"Differential results must specify the comprehensive result",
							"differentialMustSpecifyComprehensive", ErrorSeverity.ERROR);
				}
				switch (resultType) {
					case ORG:
						if (!SectionType.MICROBIOLOGY.equals(sectionType)) {
							throw new BusinessException("Only tests which belong to MICROBIOLOGY can have results of type ORGANISM",
									"organismMustBelongToMicrobiology", ErrorSeverity.ERROR);
						}
						testResult.setPrimaryUnit(null);
						testResult.setSecondaryUnit(null);
						testResult.setPrimaryDecimals(null);
						testResult.setSecondaryDecimals(null);
						testResult.setFactor(null);
						testResult.setPrimaryUnitType(null);
						testResult = repo.save(testResult);
						break;
					case NAR:
						testResult.setPrimaryUnit(null);
						testResult.setSecondaryUnit(null);
						testResult.setPrimaryDecimals(null);
						testResult.setSecondaryDecimals(null);
						testResult.setFactor(null);
						testResult.setPrimaryUnitType(null);
						List<NarrativeResultTemplate> narrativeResultTemplates = testResult.getNarrativeTemplateList();
						testResult = repo.save(testResult);
						narrativeResultTemplateService.saveNarrativeResultTemplates(narrativeResultTemplates, testResult);
						break;
					case CE:
						testResult.setPrimaryUnit(null);
						testResult.setSecondaryUnit(null);
						testResult.setPrimaryDecimals(null);
						testResult.setSecondaryDecimals(null);
						testResult.setFactor(null);
						testResult.setPrimaryUnitType(null);
						List<TestCodedResultMapping> mappings = new ArrayList<TestCodedResultMapping>();
						List<TestCodedResult> testCodedResultList = testResult.getTestCodedResultList();
						for (TestCodedResult testCodedResult : testCodedResultList) {
							TestCodedResultMapping mapping = new TestCodedResultMapping();
							mapping.setTestCodedResult(testCodedResult);
							mappings.add(mapping);
						}
						normalRanges = testResult.getNormalRangeList();
						testResult = repo.save(testResult);
						testResult.setTestCodedResultList(testCodedResultList);
						normalRangeService.saveNormalRanges(normalRanges, testResult);
						codedResultMappingService.saveTestCodedResultMappings(mappings, testResult);
						break;
					case QN_QL:
						if (!SectionType.ALLERGY.equals(sectionType)) {
							throw new BusinessException(
									"Only tests which belong to ALLERGY can have results of type Quantitative/Qualitative",
									"quantitativeQualitativeMustBelongToAllergy", ErrorSeverity.ERROR);
						}
						testResult.setPrimaryUnit(null);
						testResult.setPrimaryDecimals(null);
						testResult.setPrimaryUnitType(null);
						testResult.setSecondaryUnit(null);
						testResult.setSecondaryDecimals(null);
						testResult.setFactor(null);
						testResult = repo.save(testResult);
						break;
					case QN:
						testResult.setSecondaryUnit(null);
						testResult.setSecondaryDecimals(null);
						testResult.setFactor(null);
						if (testResult.getPrimaryUnitType() == null ||
								testResult.getPrimaryUnit() == null ||
								testResult.getPrimaryDecimals() == null) {
							throw new BusinessException("Data Integrity Error", "dataIntegrityViolation", ErrorSeverity.ERROR);
						}
						normalRanges = testResult.getNormalRangeList();
						if (testResult.getIsDifferential() && testResult.getComprehensiveResult().getRid() == null) {
							final String compResultStandardCode = testResult.getComprehensiveResult().getStandardCode();
							TestResult comprehensiveResult = testResults.stream()
																		.filter(r -> compResultStandardCode.equals(r.getStandardCode()))
																		.findFirst().orElse(null);
							testResult.setComprehensiveResult(comprehensiveResult);
						}
						testResult = repo.save(testResult);
						normalRangeService.saveNormalRanges(normalRanges, testResult);
						break;
					case QN_SC:
						if (testResult.getPrimaryUnitType() == null ||
								testResult.getPrimaryUnit() == null ||
								testResult.getPrimaryDecimals() == null ||
								testResult.getSecondaryUnit() == null ||
								testResult.getSecondaryDecimals() == null ||
								testResult.getFactor() == null) {
							throw new BusinessException("Data Integrity Error", "dataIntegrityViolation", ErrorSeverity.ERROR);
						}
						normalRanges = testResult.getNormalRangeList();
						testResult = repo.save(testResult);
						normalRangeService.saveNormalRanges(normalRanges, testResult);
						break;
					case RATIO:
						testResult.setPrimaryUnit(null);
						testResult.setSecondaryUnit(null);
						testResult.setPrimaryDecimals(null);
						testResult.setSecondaryDecimals(null);
						testResult.setFactor(null);
						testResult.setPrimaryUnitType(null);
						normalRanges = testResult.getNormalRangeList();
						testResult = repo.save(testResult);
						normalRangeService.saveNormalRanges(normalRanges, testResult);
						break;
					default:
						break;
				}
			}
		}
		return testResults;
	}

	public void deleteTestResult(Long id) {
		repo.delete(id);
	}

	public List<TestResult> testsResultFetch(List<TestDefinition> testsDefinition) {
		return repo.testsResultFetch(testsDefinition);
	}

	public Set<TestResult> getByTestId(TestDefinition testDefinition) {
		return repo.getByTestId(testDefinition);
	}
}

package com.optimiza.ehope.lis.service;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.persistence.FlushModeType;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.optimiza.core.admin.model.SecTenant;
import com.optimiza.core.admin.service.SecTenantService;
import com.optimiza.core.admin.service.SecUserService;
import com.optimiza.core.base.entity.BaseEntity;
import com.optimiza.core.base.helper.SearchCriterion;
import com.optimiza.core.base.helper.SearchCriterion.FilterOperator;
import com.optimiza.core.base.helper.SearchCriterion.JunctionOperator;
import com.optimiza.core.base.service.GenericService;
import com.optimiza.core.common.business.exception.BusinessException;
import com.optimiza.core.common.business.exception.BusinessException.ErrorSeverity;
import com.optimiza.core.common.helper.FilterablePageRequest;
import com.optimiza.core.common.util.CollectionUtil;
import com.optimiza.core.common.util.DateUtil;
import com.optimiza.core.common.util.ReflectionUtil;
import com.optimiza.core.common.util.SecurityUtil;
import com.optimiza.core.common.util.StringUtil;
import com.optimiza.core.lkp.service.ComTenantLanguageService;
import com.optimiza.core.lkp.service.LkpService;
import com.optimiza.ehope.lis.helper.EhopeRights;
import com.optimiza.ehope.lis.helper.OrderPriority;
import com.optimiza.ehope.lis.helper.SeparationFactorType;
import com.optimiza.ehope.lis.lkp.helper.OperationStatus;
import com.optimiza.ehope.lis.lkp.helper.ReportType;
import com.optimiza.ehope.lis.lkp.helper.SectionType;
import com.optimiza.ehope.lis.lkp.helper.SerialType;
import com.optimiza.ehope.lis.lkp.helper.TestDestinationType;
import com.optimiza.ehope.lis.lkp.model.LkpContainerType;
import com.optimiza.ehope.lis.lkp.model.LkpOperationStatus;
import com.optimiza.ehope.lis.lkp.service.LkpOperationStatusService;
import com.optimiza.ehope.lis.model.Doctor;
import com.optimiza.ehope.lis.model.EmrPatientInfo;
import com.optimiza.ehope.lis.model.EmrVisit;
import com.optimiza.ehope.lis.model.InsProvider;
import com.optimiza.ehope.lis.model.LabBranch;
import com.optimiza.ehope.lis.model.LabSample;
import com.optimiza.ehope.lis.model.LabSeparationFactor;
import com.optimiza.ehope.lis.model.LabTestActual;
import com.optimiza.ehope.lis.model.LabTestActualResult;
import com.optimiza.ehope.lis.model.TestDefinition;
import com.optimiza.ehope.lis.model.TestDestination;
import com.optimiza.ehope.lis.model.TestResult;
import com.optimiza.ehope.lis.model.TestSpecimen;
import com.optimiza.ehope.lis.repo.LabSampleRepo;
import com.optimiza.ehope.lis.wrapper.VisitResultsWrapper;
import com.optimiza.ehope.lis.wrapper.WorksheetWrapper;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JRParameter;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

/**
 * LabSampleService.java
 * 
 * @author Abdullah Imran <aImran@optimizasolutions.com>
 * @since Jan/17/2018
 **/
@Service("LabSampleService")
public class LabSampleService extends GenericService<LabSample, LabSampleRepo> {

	@Autowired
	private SecTenantService secTenantService;
	@Autowired
	private LabSampleRepo repo;
	@Autowired
	private ComTenantLanguageService comTenantLanguageService;
	@Autowired
	private EmrVisitService emrVisitService;
	@Autowired
	private LabTestActualService labTestActualService;
	@Autowired
	private TestDefinitionService testDefinitionService;
	@Autowired
	private LkpService lkpService;
	@Autowired
	private SysSerialService serialSerivce;
	@Autowired
	private LabSeparationFactorService separationFactorService;
	@Autowired
	private LabSampleOperationHistoryService sampleOperationHistoryService;
	@Autowired
	private LkpOperationStatusService operationStatusService;
	@Autowired
	private LabBranchService branchService;
	@Autowired
	private BillPatientTransactionService patientTransactionService;
	@Autowired
	private LabTestActualResultService testActualResultService;
	@Autowired
	private SecUserService userService;
	@Autowired
	private EntityManager entityManager;
	@Autowired
	private HistoricalResultService historicalResultService;
	@Value("${special.tests}")
	private String SPECIAL_TESTS;
	@Value("${special.tests.stool}")
	private String SPECIAL_TESTS_STOOL;
	@Value("${special.tests.cbc}")
	private String SPECIAL_TESTS_CBC;

	@Override
	protected LabSampleRepo getRepository() {
		return repo;
	}

	public LabSample findOneSampleJoins(Long rid, String... joins) {

		return getRepository().findOne(Arrays.asList(new SearchCriterion("rid", rid, FilterOperator.eq)),
				LabSample.class, joins);
	}

	public LabSample createLabSample(LabSample sample) {
		LkpOperationStatus requestedOperationStatus = lkpService.findOneAnyLkp(
				Arrays.asList(new SearchCriterion("code", OperationStatus.REQUESTED.getValue(), FilterOperator.eq)),
				LkpOperationStatus.class);
		sample.setLkpOperationStatus(requestedOperationStatus);
		sample = getRepository().save(sample);
		sampleOperationHistoryService.createSampleOperationHistory(Arrays.asList(sample.getRid()), null);
		return sample;
	}

	public LabSample updateLabSample(LabSample newSample, String comment) {
		newSample = getRepository().save(newSample);
		sampleOperationHistoryService.createSampleOperationHistory(Arrays.asList(newSample.getRid()), comment);
		return newSample;
	}

	public List<LabSample> updateLabSample(List<LabSample> samplesList, String comment) {
		samplesList = getRepository().save(samplesList);
		sampleOperationHistoryService.createSampleOperationHistory(
				samplesList.stream().map(LabSample::getRid).collect(Collectors.toList()), comment);
		return samplesList;
	}

	public void deleteLabSamples(List<Long> samplesRid) {
		List<LabSample> samples = getRepository().find(Arrays.asList(new SearchCriterion("rid", samplesRid, FilterOperator.eq)),
				LabSample.class, "labTestActualSet");
		for (LabSample sample : samples) {
			sampleOperationHistoryService.deleteAllBySample(sample);
			labTestActualService.deleteTestActualsByRid(
					sample.getLabTestActualSet().stream().map(LabTestActual::getRid).collect(Collectors.toList()));
		}
		getRepository().delete(samples);
	}

	public Set<LabSample> findByEmrVisitAndStatus(OperationStatus operationStatus, List<EmrVisit> visit) {
		return getRepository().findByStatusAndVisits(operationStatus.getValue(), visit);
	}

	/**
	 * Patient tests sample separation.
	 * 
	 * @param visitRid
	 * 
	 * @return samples that contains tests in them
	 */
	@PreAuthorize("hasAuthority('" + EhopeRights.ADD_SAMPLE + "')")
	public void separatePatientTests(Long visitRid) {

		//used COMMIT because sometimes a flush will run randomly after many queries, so we only flush after we finish the whole function
		entityManager.setFlushMode(FlushModeType.COMMIT);

		List<LabSeparationFactor> labFactors = separationFactorService.findActiveFactorsByBranch();
		LkpOperationStatus validatedOperationStatus = lkpService.findOneAnyLkp(
				Arrays.asList(new SearchCriterion("code", OperationStatus.VALIDATED.getValue(), FilterOperator.eq)),
				LkpOperationStatus.class);
		LkpOperationStatus abortedOperationStatus = lkpService.findOneAnyLkp(
				Arrays.asList(new SearchCriterion("code", OperationStatus.ABORTED.getValue(), FilterOperator.eq)),
				LkpOperationStatus.class);
		EmrVisit visit = emrVisitService.findVisitSampleSeparation(visitRid, Boolean.FALSE);
		EmrPatientInfo currentPatient = visit.getEmrPatientInfo();
		Optional<LabSample> dummySampleOptional = visit.getLabSamples().stream().filter(LabSample::getIsDummy).findFirst();
		//this visit is edited without adding any new tests so we don't have anything to separate
		if (!dummySampleOptional.isPresent()) {
			return;
		}
		LabSample dummySample = dummySampleOptional.get();
		List<LabTestActual> patientActualTestList = new ArrayList<>(dummySample.getLabTestActualSet());
		//cleared the tests in the dummy sample because we are filling the openVisitsRequestedSamples by the old samples then the dummy sample then
		//we will loop over the old ones to check if we can insert test to them or not , otherwise we use the dummy sample
		dummySample.setLabTestActualSet(new HashSet<>());
		List<LabSample> openVisitsRequestedSamples = new ArrayList<>();
		List<EmrVisit> openEmrVisits = new ArrayList<>();
		//use previous requested visits only if this factor is active
		if (separationFactorService.isFactorActive(SeparationFactorType.MINUTES_30)) {
			List<Long> openVisitsRid = emrVisitService.getOpenVisits(visit.getRid(), currentPatient.getRid());
			if (!CollectionUtil.isCollectionEmpty(openVisitsRid)) {
				for (Long vrid : openVisitsRid) {
					EmrVisit prevVisit = null;
					//in-case this visit does not have any samples/tests, do not roll back the whole operation
					try {
						prevVisit = emrVisitService.findVisitSampleSeparationNoRollBack(vrid, Boolean.FALSE);
					} catch (BusinessException e) {
						continue;
					}
					openEmrVisits.add(prevVisit);
					for (LabSample sample : prevVisit.getLabSamples()) {
						patientActualTestList.addAll(sample.getLabTestActualSet());
						sample.setEmrVisit(visit);//set the previous sample's visit to the new visit
						sample.setLabTestActualSet(new HashSet<>());//empty this sample so we can add tests to it 
						openVisitsRequestedSamples.add(sample);
					}
				}
			}
		}
		//this visit has old samples(editing a visit),
		//only use samples that are not collected 
		List<LabSample> previousVisitSamples = visit.getLabSamples().stream().filter(ls ->
			{
				return (OperationStatus.valueOf(ls.getLkpOperationStatus().getCode()) == OperationStatus.REQUESTED ||
						OperationStatus.valueOf(ls.getLkpOperationStatus().getCode()) == OperationStatus.VALIDATED ||
						OperationStatus.valueOf(ls.getLkpOperationStatus().getCode()) == OperationStatus.COLLECTED)
						&& ls.getIsDummy() == Boolean.FALSE;
			}).collect(Collectors.toList());
		if (!CollectionUtil.isCollectionEmpty(previousVisitSamples)) {
			openVisitsRequestedSamples.addAll(previousVisitSamples);
		}
		openVisitsRequestedSamples.add(dummySample);// insert the dummy sample, make it last one if there are any previous samples
		Consumer<LabTestActual> makeNewSample = test ->
			{
				//use dummy sample if it is empty,otherwise create new sample
				LabSample ds = CollectionUtil.isCollectionEmpty(dummySample.getLabTestActualSet()) ? dummySample : generateSample(visit);
				test.setLabSample(ds);
				ds.setLkpOperationStatus(validatedOperationStatus);
				ds.addToLabTestActualSet(test);
				if (!ds.equals(dummySample)) {
					openVisitsRequestedSamples.add(ds);
				}
			};
		OUTER: for (int i = 0; i < patientActualTestList.size(); i++) {
			LabTestActual actualTest = patientActualTestList.get(i);
			actualTest.setLabSample(null);//null here as a flag to know if we put a test in a sample or not
			actualTest.setLkpOperationStatus(validatedOperationStatus);
			if (actualTest.getTestDefinition().getIsAllowRepetitionDifferentSample()) {
				makeNewSample.accept(actualTest);
				continue;
			}
			for (LabSample sample : openVisitsRequestedSamples) {

				LabTestActual testInSample = CollectionUtil.isCollectionEmpty(sample.getLabTestActualSet()) ? null
						: sample.getLabTestActualSet().iterator().next();
				//If testInSample is null then its valid, which means this is the dummy sample and there are no other previous tests
				if (testInSample == null || canInsertInSample(testInSample, actualTest, currentPatient.getAge(), labFactors)) {
					actualTest.setLabSample(sample);
					sample.addToLabTestActualSet(actualTest);
					sample.setLkpOperationStatus(validatedOperationStatus);//since this sample has a new test added to it then it is validated
					continue OUTER;
				}
			}
			//the test does not have a sample
			makeNewSample.accept(actualTest);
		}

		List<LabSample> emptySamples = new ArrayList<>();
		for (LabSample sample : openVisitsRequestedSamples) {
			sample.setIsDummy(Boolean.FALSE);//reset this flag for all samples
			if (CollectionUtil.isCollectionEmpty(sample.getLabTestActualSet())) {
				emptySamples.add(sample);
				continue;
			}
			Set<LabTestActual> testsInSamples = new HashSet<>();
			testsInSamples.addAll(sample.getLabTestActualSet());
			sample.setLkpContainerType(sample	.getLabTestActualSet().iterator().next().getTestDefinition().getTestSpecimens().iterator()
												.next().getContainerType());
			sample.setLabTestActualSet(new HashSet<>());//clearing because hibernate caches these objects
			LabSample savedSample = updateLabSample(sample, null);
			for (LabTestActual lta : testsInSamples) {
				lta.setLabSample(savedSample);
				labTestActualService.updateTestActual(lta, null);
			}
		}
		//changing statuses manually since propagate(...) leads to some weird behavior.
		visit.setLkpOperationStatus(validatedOperationStatus);
		emrVisitService.updateVisit(visit, null);
		if (!CollectionUtil.isCollectionEmpty(openEmrVisits)) {
			for (EmrVisit ev : openEmrVisits) {
				ev.setLkpOperationStatus(abortedOperationStatus);
				emrVisitService.updateVisit(ev, null);
			}
		}
		//Any empty samples will be aborted
		if (!CollectionUtil.isCollectionEmpty(emptySamples)) {
			for (LabSample ls : emptySamples) {
				ls.setLkpOperationStatus(abortedOperationStatus);
				updateLabSample(ls, null);

			}

		}

	}

	/**
	 * Test every LabTestActual with each other so we can determine if this sample is valid or not.
	 * Testing with every LabTestActual because we don't know which test will invalidate it.
	 * 
	 * @param visitRid
	 * @param sampleTests
	 * @return is valid,factors values for each test
	 */
	public Map<String, Object> validateSample(Long visitRid, Map<Long, List<Long>> sampleTests) {
		List<LabSeparationFactor> labFactors = separationFactorService.findActiveFactorsByBranch();
		EmrPatientInfo patient = emrVisitService.findOne(SearchCriterion.generateRidFilter(visitRid, FilterOperator.eq),
				EmrVisit.class, "emrPatientInfo").getEmrPatientInfo();
		Set<LabTestActual> testActuals = getSamplesTestsMapping(visitRid, sampleTests).get(0).getLabTestActualSet();
		Map<String, Object> result = new HashMap<>();
		Map<Long, Map<Long, Object>> allTestsfactorValues = new HashMap<>();
		Set<InsProvider> externalDestinations = new HashSet<>();
		Boolean valid = true;
		for (LabTestActual toTestActual : testActuals) {
			Map<Long, Object> factorMap = testFactorValues(toTestActual, patient.getAge(),
					labFactors);
			allTestsfactorValues.put(toTestActual.getRid(), factorMap);
			TestDestinationType tdt = TestDestinationType.valueOf(toTestActual.getTestDestination().getType().getCode());
			if (tdt.equals(TestDestinationType.EXTERNAL)) {
				externalDestinations.add(toTestActual.getTestDestination().getDestinationBranch());
			}
			if (valid) {//we didnt break the loop here so we can get all the factors for all tests
				for (LabTestActual ta : testActuals) {
					if (toTestActual.equals(ta)) {
						continue;
					}
					valid = canInsertInSample(ta, toTestActual,
							patient.getAge(), labFactors);
				}
			}

		}
		if (!CollectionUtil.isCollectionEmpty(externalDestinations) && externalDestinations.size() > 1) {
			String userLocale = userService.getUserLocale(SecurityUtil.getCurrentUser().getRid());
			StringBuilder value = new StringBuilder();
			for (InsProvider ip : externalDestinations) {
				value.append(!StringUtil.isEmpty(ip.getName().get(userLocale)) ? ip.getName().get(userLocale)
						: ip.getName().entrySet().iterator().next().getValue());
				value.append(", ");
			}
			value.setLength(value.length() - 2);

			throw new BusinessException("Tests in sample have more than one external destination", "sampleDifferentDestination",
					ErrorSeverity.ERROR, Arrays.asList(value.toString()));
		}
		result.put("valid", valid);
		result.put("factorValues", allTestsfactorValues);
		return result;
	}

	/**
	 * Testing a test inside the sample and a test to be inserted in the sample that if the toTest can be inserted or not.
	 * For internal usage only otherwise use isTestValidInSample(...)
	 * 
	 * @param testInSample
	 * @param toTest
	 * @param patientAge
	 * @param factors
	 * @return true if it can be inserted otherwise false
	 */
	private boolean canInsertInSample(LabTestActual testInSample, LabTestActual toTest, Long patientAge,
			List<LabSeparationFactor> factors) {
		String testInSampleValue = formatFactorValues(testFactorValues(testInSample, patientAge, factors));
		String toTestValue = formatFactorValues(testFactorValues(toTest, patientAge, factors));
		return testInSampleValue.equals(toTestValue);
	}

	/**
	 * Is the test still valid in its sample?
	 * 
	 * @param visitRid
	 * @param toTestRid
	 * @return true if this test still match factors with other tests in its sample
	 */
	public boolean isTestValidInSample(Long visitRid, Long toTestRid) {
		List<LabSeparationFactor> labFactors = separationFactorService.findActiveFactorsByBranch();
		EmrVisit visit = emrVisitService.findVisitSampleSeparation(visitRid, Boolean.FALSE);
		EmrPatientInfo patient = visit.getEmrPatientInfo();
		List<LabTestActual> testActuals = visit	.getLabSamples().stream().flatMap(s -> s.getLabTestActualSet().stream())
												.collect(Collectors.toList());
		LabTestActual toTest = testActuals.stream().filter(lta -> lta.getRid().equals(toTestRid)).findFirst().get();
		List<LabTestActual> otherTests = testActuals.stream()
													.filter(lta -> lta.getLabSample().getRid().equals(toTest.getLabSample().getRid())
															&& !lta.getRid().equals(toTestRid))
													.collect(Collectors.toList());

		String toTestValue = formatFactorValues(testFactorValues(toTest, patient.getAge(), labFactors));

		for (LabTestActual lta : otherTests) {
			String testInSampleValue = formatFactorValues(testFactorValues(lta, patient.getAge(), labFactors));
			if (!testInSampleValue.equals(toTestValue)) {
				return false;
			}
		}

		return true;
	}

	/**
	 * Format the test and its factor values to a string so it can be used in comparisons.
	 * 
	 * @param testFactorValues
	 * @return String
	 */
	private String formatFactorValues(Map<Long, Object> testFactorValues) {
		String value = "";
		for (Object obj : testFactorValues.values()) {
			value += " ";
			if (obj != null) {
				if (obj instanceof BaseEntity) {
					value += ((BaseEntity) obj).getRid();
				} else {
					value += obj.toString();
				}
			} else {
				value += "null";
			}
			value += " ";
			value = value.trim().replaceAll("\\s+", ",");
		}

		return value;
	}

	/**
	 * Get the test full factors values comma separated and get the factor values
	 * 
	 * @param testActual
	 * @param patientAge
	 * @param factors
	 * @return value: all factors separated by comma,factor actual value
	 */
	public Map<Long, Object> testFactorValues(LabTestActual testActual, Long patientAge, List<LabSeparationFactor> factors) {
		testActual.setTestDefinition(ReflectionUtil.unproxy(testActual.getTestDefinition()));
		Map<Long, Object> map = new HashMap<>();
		try {
			for (LabSeparationFactor factor : factors) {
				Object factorValue = null;
				if (factor.getFieldName().equals("specimenContainerType")) {
					factorValue = getContainerToUse(testActual.getTestDefinition(), patientAge);
				} else if (factor.getFieldName().equals("destination")) {
					TestDestinationType tdt = TestDestinationType.valueOf(testActual.getTestDestination().getType().getCode());
					if (tdt.equals(TestDestinationType.WORKBENCH)) {
						factorValue = testActual.getTestDestination().getWorkbench();
					} else {
						factorValue = testActual.getTestDestination().getDestinationBranch();
					}
				} else {
					Field field = TestDefinition.class.getDeclaredField(factor.getFieldName());
					field.setAccessible(true);
					factorValue = field.get(testActual.getTestDefinition());
				}
				if (factorValue != null) {
					map.put(factor.getRid(), factorValue);
				} else {
					map.put(factor.getRid(), null);
				}
			}
		} catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
			e.printStackTrace();
		}

		return map;
	}

	/**
	 * Get the container type from TestDefinition
	 * 
	 * @param testDefinition
	 * @param age : patient age
	 * @return LkpContainerType
	 */
	private LkpContainerType getContainerToUse(TestDefinition testDefinition, Long age) {
		TestSpecimen defaultSpecimen = null;
		TestSpecimen pediatricSpecimen = null;
		for (TestSpecimen tempSpecimen : testDefinition.getTestSpecimens()) {
			if (tempSpecimen.getIsDefault()) {
				defaultSpecimen = tempSpecimen;
			} else {
				pediatricSpecimen = tempSpecimen;
			}
		}
		//TODO use tenant preferences later instead of 2L, also replace age with ageWrapper and use getDuration functions
		if (age < 2L && pediatricSpecimen != null) {
			defaultSpecimen = pediatricSpecimen;
		}
		return defaultSpecimen.getContainerType();
	}

	/**
	 * Covert the samplesTests to actual mapping of LabSample -> Tests
	 * 
	 * @param visitRid
	 * @param samplesTests
	 * @return LabSample
	 */
	public List<LabSample> getSamplesTestsMapping(Long visitRid, Map<Long, List<Long>> samplesTests) {
		EmrVisit visit = emrVisitService.findVisitSampleSeparation(visitRid, Boolean.TRUE);//fetching all samples even the aborted so we can get their testActual since they may still connected to the aborted sample
		List<LabTestActual> testActuals = visit	.getLabSamples().stream().flatMap(s -> s.getLabTestActualSet().stream())
												.collect(Collectors.toList());
		List<LabSample> samples = new ArrayList<>();
		for (Map.Entry<Long, List<Long>> entry : samplesTests.entrySet()) {
			Long sampleRid = entry.getKey();
			LabSample sample = null;
			if (sampleRid > 0) {
				for (LabSample visitSample : visit.getLabSamples()) {
					if (visitSample.getRid().equals(sampleRid)) {
						sample = visitSample;
						break;
					}
				}
			} else {
				sample = generateSample(visit);
				sample.setRid(null);
			}
			sample.setLabTestActualSet(new HashSet<>());
			for (int i = 0; i < entry.getValue().size(); i++) {
				Long testRid = entry.getValue().get(i);
				for (LabTestActual lta : testActuals) {
					if (lta.getRid().equals(testRid)) {
						if (i == 0) {
							sample.setLkpContainerType(lta	.getTestDefinition()
															.getTestSpecimens().iterator().next().getContainerType());
						}
						lta.setLabSample(sample);
						sample.addToLabTestActualSet(lta);
						break;
					}
				}
			}
			samples.add(sample);
		}
		return samples;
	}

	/**
	 * Used to change test's sample reference.Also creating new samples.
	 * 
	 * @param visitRid
	 * @param samplesTests
	 */
	public void setSamples(Long visitRid, Map<Long, List<Long>> samplesTests) {

		LkpOperationStatus validatedOperationStatus = lkpService.findOneAnyLkp(
				Arrays.asList(new SearchCriterion("code", OperationStatus.VALIDATED.getValue(), FilterOperator.eq)),
				LkpOperationStatus.class);

		List<LabSample> toSaveSamples = getSamplesTestsMapping(visitRid, samplesTests);
		List<LabTestActual> testActuals = new ArrayList<>();
		for (LabSample sample : toSaveSamples) {
			if (sample.getRid() == null) {
				sample.setLkpOperationStatus(validatedOperationStatus);
			}
			testActuals.addAll(sample.getLabTestActualSet());
		}
		if (!CollectionUtil.isCollectionEmpty(toSaveSamples)) {
			toSaveSamples = updateLabSample(toSaveSamples, null);
		}
		if (!CollectionUtil.isCollectionEmpty(testActuals)) {
			testActuals = labTestActualService.updateTestActual(testActuals, null);
		}
		//flushing so revalidateVisitStatus(...) can see new samples
		//calling revalidateVisitStatus(...) in case of moving a test with low order operation status to a higher sample status
		entityManager.flush();
		entityManager.clear();
		emrVisitService.revalidateVisitStatus(visitRid);
	}

	public Set<LabSample> sendToMachine(Long currentVisitRid, List<Long> samplesRid) {

		Set<LabSample> labSamples = new HashSet<>(
				getRepository().find(Arrays.asList(new SearchCriterion("rid", samplesRid, FilterOperator.in)),
						LabSample.class, "emrVisit.emrPatientInfo", "labTestActualSet"));

		List<Map<String, Object>> requestMapList = new ArrayList<Map<String, Object>>();

		labSamples.forEach(labSample ->
			{
				EmrPatientInfo patient = labSample.getEmrVisit().getEmrPatientInfo();
				Doctor doctor = labSample.getEmrVisit().getDoctor();
				for (LabTestActual labTestActual : labSample.getLabTestActualSet()) {

					TestDefinition testDefinition = testDefinitionService.findOne(
							Arrays.asList(new SearchCriterion("rid", labTestActual.getTestDefinition().getRid(), FilterOperator.eq)),
							TestDefinition.class, "testResults", "specimenType");
					Set<TestResult> testResults = testDefinition.getTestResults();
					for (TestResult testResult : testResults) {
						Map<String, Object> requestMap = new HashMap<String, Object>();
						requestMap.put("barcode", labSample.getBarcode());

						requestMap.put("specimenDescriptor", testDefinition.getSpecimenType().getCode());

						//TODO is it correct to hardcode en_us?
						if (doctor != null) {
							requestMap.put("orderingPhysician", doctor.getName().get("en_us"));
						}

						requestMap.put("testCode", testResult.getStandardCode());
						if (testResults.size() > 1) {
							requestMap.put("panelCode", testDefinition.getStandardCode());
						}

						requestMap.put("patientFirstName", patient.getFirstName().get("en_us"));
						requestMap.put("patientLastName", patient.getLastName().get("en_us"));

						requestMap.put("patientId", patient.getFileNo());
						requestMap.put("dateOfBirth", patient.getDateOfBirth());
						String genderCode = patient.getGender().getCode();
						requestMap.put("gender", genderCode.substring(0, 1));
						LabBranch branch = branchService.findById(labSample.getBranchId());
						requestMap.put("senderName", branch.getCode());
						//TODO fix the collection date and time as some of them are null, they must be filled
						//requestMap.put("specimenCollectionDateAndTime", labSample.getCollectedDate());
						requestMap.put("specimenCollectionDateAndTime", new Date());
						requestMap.put("priority", labSample.getEmrVisit().getIsStat() ? OrderPriority.STAT : OrderPriority.ROUTINE);

						requestMapList.add(requestMap);
					}

				}
			});

		LabBranch branch = branchService.findById(SecurityUtil.getCurrentUser().getBranchId());
		String integrationUrl = branch.getIntegrationUrl();
		String integrationToken = branch.getIntegrationToken();
		performRequest(integrationUrl, requestMapList, Void.class, integrationToken);

		emrVisitService.propagateVisitStatusNoAuth(currentVisitRid, OperationStatus.COLLECTED, null);

		return labSamples;
	}

	private <T> T performRequest(String url, Object objectToSend, Class<T> responseType, String token) {
		HttpHeaders headers = new HttpHeaders();
		headers.set("Authorization", token);
		headers.setContentType(MediaType.APPLICATION_JSON);

		HttpEntity<Object> request = new HttpEntity<Object>(objectToSend, headers);

		RestTemplate restTemplate = new RestTemplate();
		return restTemplate.postForObject(url, request, responseType);
	}

	/**
	 * To be used when a patient order a visit
	 * 
	 * @param currentVisit
	 * @return
	 */
	public LabSample generateSample(EmrVisit visit) {
		LabSample dummySample = new LabSample();
		dummySample.setEmrVisit(visit);
		dummySample.setSampleNo(serialSerivce.sequenceGeneration(SerialType.SAMPLE_NO));
		dummySample.setBarcode(serialSerivce.sequenceGeneration(SerialType.SAMPLE_BARCODE));
		dummySample.setIsDummy(Boolean.FALSE);
		return dummySample;
	}

	private LabSample propegateSample(EmrVisit emrVisit, LabSample labSample, OperationStatus newOperationStatus, String comment) {
		LkpOperationStatus currentLkpOperationStatus = ReflectionUtil.unproxy(labSample.getLkpOperationStatus());
		OperationStatus currentOperationStatus = OperationStatus.valueOf(currentLkpOperationStatus.getCode());
		if (newOperationStatus == currentOperationStatus) {
			return labSample;
		}
		operationStatusService.validations(currentOperationStatus, newOperationStatus, emrVisit.getRid());
		Set<LabTestActual> testActualInSamplesSet = labSample.getLabTestActualSet().stream().collect(Collectors.toSet());
		//remove any test that surpassed sample status, means order was edited
		testActualInSamplesSet.removeIf(lta ->
			{
				return OperationStatus.valueOf(lta.getLkpOperationStatus().getCode()).getOrder() > newOperationStatus.getOrder();
			});
		for (LabTestActual test : testActualInSamplesSet) {
			operationStatusService.validations(OperationStatus.valueOf(test.getLkpOperationStatus().getCode()), newOperationStatus,
					emrVisit.getRid());
		}
		//Check if results were entered for this sample,checking if tests are empty in case the sample has no tests in it
		if (newOperationStatus == OperationStatus.RESULTS_ENTERED && !CollectionUtil.isCollectionEmpty(testActualInSamplesSet)) {
			List<LabTestActual> testActualsResults = testActualResultService.getTestActualListWithRequiredResults(emrVisit.getRid());
			testActualsResults.removeIf(t -> !t.getLabSample().getRid().equals(labSample.getRid()));
			if (CollectionUtil.isCollectionEmpty(testActualsResults)
					|| testActualsResults.size() != testActualInSamplesSet.size()) {
				//get only the non filled tests
				StringBuilder testsStandardCodes = new StringBuilder();
				OUTER: for (LabTestActual lta : testActualInSamplesSet) {
					for (LabTestActual ltar : testActualsResults) {
						if (lta.equals(ltar)) {//this test has a result so dont add it
							continue OUTER;
						}
					}
					testsStandardCodes.append(lta.getTestDefinition().getStandardCode() + ",");
				}
				testsStandardCodes.deleteCharAt(testsStandardCodes.length() - 1);
				throw new BusinessException("Not All Tests has results", "testNoResults", ErrorSeverity.ERROR,
						Arrays.asList(testsStandardCodes.toString()));
			}
		}
		OperationStatus smallestStatus = OperationStatus.getSmallestVisitSampleTestStatus(emrVisit.getLabSamples(), labSample.getRid(),
				newOperationStatus);
		List<SearchCriterion> filters = Arrays.asList(
				new SearchCriterion("code", smallestStatus.getValue(), FilterOperator.eq, JunctionOperator.Or),
				new SearchCriterion("code", newOperationStatus.getValue(), FilterOperator.eq, JunctionOperator.Or));
		List<LkpOperationStatus> operationStatusList = lkpService.findAnyLkp(filters, LkpOperationStatus.class, null);
		LkpOperationStatus smallestLkpSampleStatus = null;
		LkpOperationStatus lkpOperationStatus = null;
		for (LkpOperationStatus operationStatus : operationStatusList) {
			OperationStatus os = OperationStatus.valueOf(operationStatus.getCode());
			if (smallestStatus == os) {
				smallestLkpSampleStatus = operationStatus;
			}
			if (newOperationStatus == os) {
				lkpOperationStatus = operationStatus;
			}
		}

		emrVisit.setLkpOperationStatus(smallestLkpSampleStatus);
		labSample.setLkpOperationStatus(lkpOperationStatus);
		for (LabTestActual lta : testActualInSamplesSet) {
			lta.setLkpOperationStatus(lkpOperationStatus);
		}

		//only propagate comment if its new visit is cancelled
		if (OperationStatus.valueOf(emrVisit.getLkpOperationStatus().getCode()) == OperationStatus.CANCELLED) {
			emrVisit = emrVisitService.updateVisit(emrVisit, comment);
		} else {
			emrVisit = emrVisitService.updateVisit(emrVisit, null);
		}
		updateLabSample(labSample, comment);
		labTestActualService.updateTestActual(new ArrayList<>(testActualInSamplesSet), comment);
		if (newOperationStatus == OperationStatus.CANCELLED) {
			patientTransactionService.cancelPayment(emrVisit.getRid(), comment,
					testActualInSamplesSet.stream().map(LabTestActual::getRid).collect(Collectors.toList()));
		}

		emrVisit = emrVisitService.closeVisit(emrVisit, OperationStatus.valueOf(smallestLkpSampleStatus.getCode()));
		return labSample;
	}

	public LabSample propagateSampleStatusNoAuth(Long sampleRid, OperationStatus newOperationStatus, String comment) {
		return propagateSampleStatus(sampleRid, newOperationStatus, comment);
	}

	@PreAuthorize("hasAuthority(#newOperationStatus.getValue().concat('" + EhopeRights._OPERATION_STATUS + "'))")
	public LabSample propagateSampleStatus(Long sampleRid, OperationStatus newOperationStatus, String comment) {
		LabSample labSample = findOneSampleJoins(sampleRid, "emrVisit");
		EmrVisit emrVisit = emrVisitService.findOneOrderSampleTestStatus(labSample.getEmrVisit().getRid());
		for (LabSample sample : emrVisit.getLabSamples()) {
			if (sample.equals(labSample)) {
				labSample = sample;
				break;
			}
		}
		labSample = propegateSample(emrVisit, labSample, newOperationStatus, comment);
		return labSample;
	}

	public List<LabSample> findSamples(List<SearchCriterion> filters, Sort sort, String... joins) {
		//remove duplicates
		return getRepository().find(filters, LabSample.class, sort, joins).stream().distinct().collect(Collectors.toList());
	}

	public Page<LabSample> getSamplePage(FilterablePageRequest filterablePageRequest) {
		return getRepository().find(filterablePageRequest.getFilters(), filterablePageRequest.getPageRequest(), LabSample.class);
	}

	public Map<JRDataSource, Map<String, Object>> getSampleReport(Map<String, Object> sampleInformation) {
		Long sampleRid = Long.valueOf(sampleInformation.get("sampleRid").toString());
		Integer timezoneOffset = Integer.valueOf(sampleInformation.get("timezoneOffset").toString());
		String timezoneId = (String) sampleInformation.get("timezoneId");

		List<SearchCriterion> filters = new ArrayList<SearchCriterion>();
		filters.add(new SearchCriterion("rid", sampleRid, FilterOperator.eq));
		filters.add(new SearchCriterion("lkpOperationStatus.code", OperationStatus.CANCELLED.getValue(), FilterOperator.neq));
		filters.add(new SearchCriterion("lkpOperationStatus.code", OperationStatus.ABORTED.getValue(), FilterOperator.neq));

		LabSample sample = getRepository().findOne(filters, LabSample.class,
				"lkpContainerType", "emrVisit.emrPatientInfo", "labTestActualSet.testDestination.type");

		if (sample == null) {
			throw new BusinessException("No Samples Found", "noTestInOrder", ErrorSeverity.ERROR);
		}

		Map<JRDataSource, Map<String, Object>> allReports = new HashMap<>();

		LabBranch branch = branchService.findById(sample.getBranchId());

		TestDestination testDestination = sample.getLabTestActualSet().iterator().next().getTestDestination();
		String testDestData = "";

		if (testDestination.getType().getCode().equals(TestDestinationType.WORKBENCH.getValue())) {
			testDestData = testDestination.getWorkbench().getName().get("en_us");
		} else {
			testDestData = testDestination.getDestinationBranch().getName().get("en_us");
		}

		JRDataSource resultsDataSource = new JRBeanCollectionDataSource(Arrays.asList(sample));
		Map<String, Object> parameterMap = new HashMap<String, Object>();
		parameterMap.put("datasource", resultsDataSource);
		parameterMap.put(JRParameter.REPORT_TIME_ZONE, DateUtil.returnClientTimeZone(timezoneId, timezoneOffset));
		parameterMap.put("branch", branch);
		parameterMap.put("testDestination", testDestData);
		parameterMap.put("namePrimary", comTenantLanguageService.getTenantNamePrimary());

		allReports.put(resultsDataSource, parameterMap);
		return allReports;
	}

	public Map<JRDataSource, Map<String, Object>> getAllSampleReport(Map<String, Object> visitInformation) {
		Long visitRid = Long.valueOf(visitInformation.get("visitRid").toString());
		Integer timezoneOffset = Integer.valueOf(visitInformation.get("timezoneOffset").toString());
		String timezoneId = (String) visitInformation.get("timezoneId");

		Map<JRDataSource, Map<String, Object>> allReports = new HashMap<>();
		EmrVisit emrVisit = emrVisitService.getVisitSamples(visitRid,
				Arrays.asList(OperationStatus.ABORTED.getValue(), OperationStatus.CANCELLED.getValue()));

		if (CollectionUtil.isCollectionEmpty(emrVisit.getLabSamples())) {
			throw new BusinessException("No Samples Found", "noTestInOrder", ErrorSeverity.ERROR);
		}

		List<LabSample> labSamples = new ArrayList<LabSample>(emrVisit.getLabSamples());

		for (LabSample labSample : labSamples) {

			TestDestination testDestination = labSample.getLabTestActualSet().iterator().next().getTestDestination();
			String testDestData = "";

			if (testDestination.getType().getCode().equals(TestDestinationType.WORKBENCH.getValue())) {
				testDestData = testDestination.getWorkbench().getName().get("en_us");
			} else {
				testDestData = testDestination.getDestinationBranch().getName().get("en_us");
			}

			HashMap<String, Object> params = new HashMap<>();

			LabSample sample = getRepository().findOne(labSample.getRid());
			if (sample != null) {
				LabBranch branch = branchService.findById(sample.getBranchId());
				params.put(JRParameter.REPORT_TIME_ZONE, DateUtil.returnClientTimeZone(timezoneId, timezoneOffset));
				params.put("sample", sample);
				params.put("branch", branch);
				params.put("testDestination", testDestData);
				params.put("namePrimary", comTenantLanguageService.getTenantNamePrimary());
			}
			JRDataSource ds = new JRBeanCollectionDataSource(Arrays.asList(sample));
			allReports.put(ds, params);

		}
		return allReports;
	}

	public List<VisitResultsWrapper> getSampleWorksheetData(LabSample labSample, Set<LabTestActual> labTestActualSet) {

		Set<VisitResultsWrapper> wrappersList = new TreeSet<VisitResultsWrapper>();
		VisitResultsWrapper resultsWrapper = new VisitResultsWrapper();
		SecTenant tenant = secTenantService.findById(SecurityUtil.getCurrentUser().getTenantId());
		resultsWrapper.setTenant(tenant);

		TestDestination testDestination = labSample.getLabTestActualSet().iterator().next().getTestDestination();
		String testDestData = "";

		if (testDestination.getType().getCode().equals(TestDestinationType.WORKBENCH.getValue())) {
			testDestData = testDestination.getWorkbench().getName().get("en_us");
		} else {
			testDestData = testDestination.getDestinationBranch().getName().get("en_us");
		}

		String reportName = ReportType.DEFAULT.getValue();
		WorksheetWrapper wrapper = new WorksheetWrapper();
		Set<String> sectionsSet = new HashSet<String>();

		wrapper.setBarcode(labSample.getBarcode());
		wrapper.setEmrVisit(labSample.getEmrVisit());
		wrapper.setLkpContainerType(labSample.getLkpContainerType());
		wrapper.setTestDestination(testDestData);

		Set<LabTestActual> tempLabTestActual = new TreeSet<LabTestActual>();
		Set<LabTestActual> filteredLabTestActual = new TreeSet<LabTestActual>();

		for (LabTestActual labTestActual : labTestActualSet) {
			Boolean isSeparate = labTestActual.getTestDefinition().getIsSeparatePage();
			wrapper.setTestDefinition(labTestActual.getTestDefinition());
			int previousCounter = 0;

			if (!sectionsSet.contains(labTestActual.getTestDefinition().getSection().getName().get("en_us"))) {
				sectionsSet.add(labTestActual.getTestDefinition().getSection().getName().get("en_us"));
			}

			List<LabTestActualResult> labTestActualResultList = new ArrayList<>(labTestActual.getLabTestActualResults());
			Collections.sort(labTestActualResultList);

			for (LabTestActualResult labTestActualResult : labTestActualResultList) {

				labTestActualResult.setNormalRangeList(emrVisitService.getTestNormalRangeList(labTestActualResult));

				int neededPreviousResults = 2;

				labTestActualResult.setPreviousResult(
						emrVisitService.getPreviousLabTestActualResults(labSample.getEmrVisit(), labTestActual, labTestActualResult,
								neededPreviousResults));

				neededPreviousResults = neededPreviousResults - labTestActualResult.getPreviousResult().size();
				if (neededPreviousResults > 0) {
					labTestActualResult.setHistoricalResult(
							emrVisitService.getLatestHistoricalResults(labSample.getEmrVisit(), labTestActual, labTestActualResult,
									neededPreviousResults, previousCounter));
				}

				//Checking if test is ALLERGY, so we can define result interpretation class
				//might be used for the IF below
				//labTestActual.get(i).getTestDefinition().getLkpReportType().getCode().equals(ReportType.ALLERGY.getValue())
				if (labTestActual.getTestDefinition().getSection().getType() != null
						&& labTestActual.getTestDefinition().getSection().getType().getCode()
										.equals(SectionType.ALLERGY.getValue())) {
					if (labTestActualResult.getPrimaryResultParsed() != null) {
						String interpretationClass = emrVisitService.retrieveResultInterpretationClass(
								labTestActualResult.getPrimaryResultParsed(),
								labTestActual.getTestDefinition().getInterpretations(),
								labTestActual.getTestDefinition().getAllergyDecimals());
						labTestActualResult.setInterpretationClass(interpretationClass);
					}
				}
			}

			if (isSeparate) {
				VisitResultsWrapper tempResultsWrapper = new VisitResultsWrapper();
				WorksheetWrapper tempWrapper = new WorksheetWrapper();

				tempWrapper.setBarcode(labSample.getBarcode());
				tempWrapper.setEmrVisit(labSample.getEmrVisit());
				tempWrapper.setLkpContainerType(labSample.getLkpContainerType());
				tempWrapper.setTestDestination(testDestData);

				Set<LabTestActualResult> labTestActualResultSetTemp = new TreeSet<LabTestActualResult>();
				labTestActualResultSetTemp.addAll(labTestActualResultList);

				////////////////////////
				//CULTURE TEST//////////
				////////////////////////
				if (labTestActual.getTestDefinition().getSection().getType() != null
						&& labTestActual.getTestDefinition().getSection().getType().getCode().equals(SectionType.MICROBIOLOGY.getValue())) {
					Iterator<LabTestActualResult> iterator = labTestActualResultSetTemp.iterator();
					Set<LabTestActualResult> tempLabTestActualResultSet = new TreeSet<LabTestActualResult>();

					while (iterator.hasNext()) {
						LabTestActualResult temp = iterator.next();
						if (temp.getOrganismDetection() == null) {
							tempLabTestActualResultSet.add(temp);
							iterator.remove();
						}
					}
					tempResultsWrapper.setOtherCultureTestActualSet(tempLabTestActualResultSet);
				}
				////////////////////////
				//CULTURE TEST//////////
				////////////////////////

				labTestActual.setLabTestActualResults(labTestActualResultSetTemp);
				labTestActual.setLabTestActualResultList(new ArrayList<LabTestActualResult>(labTestActualResultSetTemp));

				Set<LabTestActual> labTestActualFilteredTemp = new TreeSet<LabTestActual>();
				labTestActualFilteredTemp.add(labTestActual);
				Set<LabTestActual> labTestActualIntoSample = new TreeSet<LabTestActual>();
				labTestActualIntoSample.addAll(labTestActualFilteredTemp);
				labSample.setLabTestActualSet(labTestActualIntoSample);

				/////////////////////////
				//STOOL FIXED PART///////
				/////////////////////////
				if (labTestActual.getTestDefinition().getLkpReportType().getCode().equals(ReportType.STOOL.getValue())) {
					Iterator<LabTestActual> iterator = tempLabTestActual.iterator();
					Set<LabTestActual> stoolOtherTests = new TreeSet<LabTestActual>();
					while (iterator.hasNext()) {
						LabTestActual tempActual = iterator.next();
						if (tempActual.getTestDefinition().getLkpReportType().getCode().equals(ReportType.STOOL.getValue())) {
							tempResultsWrapper.setStoolMainTest(new TreeSet<LabTestActual>(Arrays.asList(tempActual)));
						} else {
							stoolOtherTests.add(tempActual);
						}
					}
					tempResultsWrapper.setStoolOtherTests(stoolOtherTests);
				}
				/////////////////////////
				//STOOL FIXED PART///////
				/////////////////////////

				/////////////////////////
				////CBC FIXED PART///////
				/////////////////////////
				if (labTestActual.getTestDefinition().getLkpReportType().getCode().equals(ReportType.CBC.getValue())) {
					Iterator<LabTestActual> iterator = tempLabTestActual.iterator();
					Set<LabTestActual> cbcOtherTests = new TreeSet<LabTestActual>();
					while (iterator.hasNext()) {
						LabTestActual tempActual = iterator.next();
						if (tempActual.getTestDefinition().getLkpReportType().getCode().equals(ReportType.CBC.getValue())) {
							tempResultsWrapper.setCbcMainTest(new TreeSet<LabTestActual>(Arrays.asList(tempActual)));
						} else {
							cbcOtherTests.add(tempActual);
						}
					}
					tempResultsWrapper.setCbcOtherTests(cbcOtherTests);
				}
				/////////////////////////
				////CBC FIXED PART///////
				/////////////////////////

				tempResultsWrapper.setReportName(labTestActual.getTestDefinition().getLkpReportType().getCode());
				tempResultsWrapper.setUser(SecurityUtil.getCurrentUser());
				tempResultsWrapper.setEmrVisit(labSample.getEmrVisit());
				tempResultsWrapper.setTestDefinition(labTestActual.getTestDefinition());

				tempResultsWrapper.setNeonatalResultsCount(labTestActual.getLabTestActualResults().size());
				tempResultsWrapper.setReportingType("WORKSHEET");

				Set<LabTestActual> separateTempLabTestActual = new TreeSet<LabTestActual>();
				separateTempLabTestActual = labSample.getLabTestActualSet();

				tempWrapper.setLabTestActual(new ArrayList<LabTestActual>(separateTempLabTestActual));

				if (CollectionUtil.isCollectionEmpty(tempResultsWrapper.getLabTestActual())) {
					tempResultsWrapper.setLabTestActual(new TreeSet<LabTestActual>(tempWrapper.getLabTestActual()));
				}

				tempWrapper.setTestDefinition(labTestActual.getTestDefinition());
				tempResultsWrapper.setWorksheetWrapper(tempWrapper);
				tempResultsWrapper.setTestDefinition(labTestActual.getTestDefinition());
				tempResultsWrapper.setTenant(tenant);

				wrappersList.add(tempResultsWrapper);
			} else {
				ReportType reportType = ReportType.getByValue(labTestActual.getTestDefinition().getLkpReportType().getCode());

				switch (reportType) {
					case STOOL:
						reportName = ReportType.STOOL.getValue();
						break;
					case URINE:
						reportName = ReportType.URINE.getValue();
						break;
					case CULTURE:
						reportName = ReportType.CULTURE.getValue();
						break;
					case PROTEIN_ELECTRO:
						reportName = ReportType.PROTEIN_ELECTRO.getValue();
						break;
					case CBC:
						reportName = ReportType.CBC.getValue();
						break;
					case ALLERGY:
						reportName = ReportType.ALLERGY.getValue();
						break;
					case NEONATAL:
						reportName = ReportType.NEONATAL.getValue();
					default:
						break;
				}

				Set<LabTestActualResult> labTesActualResultSet = new TreeSet<LabTestActualResult>();
				labTesActualResultSet.addAll(labTestActualResultList);

				labTestActual.setLabTestActualResults(labTesActualResultSet);
				labTestActual.setLabTestActualResultList(new ArrayList<LabTestActualResult>(labTesActualResultSet));

				tempLabTestActual.add(labTestActual);
			}
		}

		filteredLabTestActual.addAll(tempLabTestActual);

		if (!CollectionUtil.isCollectionEmpty(filteredLabTestActual)) {
			resultsWrapper.setLabTestActual(filteredLabTestActual);
			resultsWrapper.setUser(SecurityUtil.getCurrentUser());
			resultsWrapper.setReportName(reportName);

			/////////////////////////
			//STOOL FIXED PART///////
			/////////////////////////
			if (reportName.equals(ReportType.STOOL.getValue())) {
				Iterator<LabTestActual> iterator = filteredLabTestActual.iterator();
				Set<LabTestActual> stoolOtherTests = new TreeSet<LabTestActual>();
				while (iterator.hasNext()) {
					LabTestActual tempActual = iterator.next();
					if (tempActual.getTestDefinition().getLkpReportType().getCode().equals(ReportType.STOOL.getValue())) {
						resultsWrapper.setStoolMainTest(new TreeSet<LabTestActual>(Arrays.asList(tempActual)));
					} else {
						stoolOtherTests.add(tempActual);
					}
				}
				resultsWrapper.setStoolOtherTests(stoolOtherTests);
			}
			/////////////////////////
			//STOOL FIXED PART///////
			/////////////////////////

			/////////////////////////
			////CBC FIXED PART///////
			/////////////////////////
			if (reportName.equals(ReportType.CBC.getValue())) {
				Iterator<LabTestActual> iterator = filteredLabTestActual.iterator();
				Set<LabTestActual> cbcOtherTests = new TreeSet<LabTestActual>();
				while (iterator.hasNext()) {
					LabTestActual tempActual = iterator.next();
					if (tempActual.getTestDefinition().getLkpReportType().getCode().equals(ReportType.CBC.getValue())) {
						resultsWrapper.setCbcMainTest(new TreeSet<LabTestActual>(Arrays.asList(tempActual)));
					} else {
						cbcOtherTests.add(tempActual);
					}
				}
				resultsWrapper.setCbcOtherTests(cbcOtherTests);
			}
			/////////////////////////
			////CBC FIXED PART///////
			/////////////////////////

			wrapper.setSection(String.join(",", sectionsSet));
			//wrapper.setLabTestActual(new ArrayList<LabTestActual>(labSample.getLabTestActualSet()));
			wrapper.setLabTestActual(new ArrayList<LabTestActual>(filteredLabTestActual));

			if (CollectionUtil.isCollectionEmpty(resultsWrapper.getLabTestActual())) {
				resultsWrapper.setLabTestActual(new TreeSet<LabTestActual>(new ArrayList<LabTestActual>(labSample.getLabTestActualSet())));
			}

			resultsWrapper.setReportName(reportName);
			resultsWrapper.setReportingType("WORKSHEET");
			resultsWrapper.setWorksheetWrapper(wrapper);

			if (labTestActualSet.iterator().hasNext()) {
				resultsWrapper.setTestDefinition(labTestActualSet.iterator().next().getTestDefinition());
			}

			wrappersList.add(resultsWrapper);
		}

		return new ArrayList<VisitResultsWrapper>(wrappersList);
	}

	public List<VisitResultsWrapper> getSampleWorksheetReport(Long sampleRid) {
		LabSample labSample = getRepository().getSampleData(Arrays.asList(sampleRid), Arrays.asList(OperationStatus.CANCELLED.getValue(),
				OperationStatus.ABORTED.getValue()));

		if (labSample == null) {
			throw new BusinessException("No Samples Found", "noTestInOrder", ErrorSeverity.ERROR);
		}

		List<VisitResultsWrapper> resultsWrapperSet = new ArrayList<VisitResultsWrapper>();
		Boolean containsStool = false;
		Boolean containsCBC = false;

		//These values are taken from application.props
		List<String> specialTests = Arrays.asList(SPECIAL_TESTS.split(","));
		List<String> specialTestsStool = Arrays.asList(SPECIAL_TESTS_STOOL.split(","));
		List<String> specialTestsCBC = Arrays.asList(SPECIAL_TESTS_CBC.split(","));

		List<LabTestActual> normalTests = labSample.getLabTestActualSet().stream().collect(Collectors.toList());

		List<LabTestActual> labTestActualList = null;
		List<LabTestActual> filteredLabTestActualList = null;

		for (String testCode : specialTests) {
			labTestActualList = labSample	.getLabTestActualSet().stream()
											.filter(lta -> lta.getTestDefinition().getStandardCode().equals(testCode))
											.collect(Collectors.toList());

			if (!CollectionUtil.isCollectionEmpty(labTestActualList)) {
				if (testCode.equals("STOOL")) {
					//Get stool tests
					containsStool = true;
					filteredLabTestActualList = emrVisitService.findRelatedTests(labSample.getEmrVisit(), specialTestsStool, normalTests);
				} else if (testCode.startsWith("CBC")) {
					//Get Other CBC tests 
					containsCBC = true;
					List<String> tempList = new ArrayList<String>();
					tempList.add(testCode);
					tempList.addAll(specialTestsCBC);

					filteredLabTestActualList = emrVisitService.findRelatedTests(labSample.getEmrVisit(), tempList, normalTests);
				}
				resultsWrapperSet.addAll(getSampleWorksheetData(labSample, new TreeSet<LabTestActual>(filteredLabTestActualList)));
			}
		}

		//Remove Stool tests from normal tests. (If found)
		if (containsStool) {
			List<String> StoolTestsCodes = resultsWrapperSet.stream().filter(rws -> rws.getReportName().equals("STOOL")).flatMap(
					rws -> rws.getLabTestActual().stream().map(lta -> lta.getTestDefinition().getStandardCode()))
															.collect(Collectors.toList());
			for (String testCode : StoolTestsCodes) {
				List<LabTestActual> tempLabTestActual = normalTests	.stream().filter(
						lta -> lta.getTestDefinition().getStandardCode().equals(testCode))
																	.collect(Collectors.toList());
				if (!CollectionUtil.isCollectionEmpty(tempLabTestActual)) {
					normalTests.remove(tempLabTestActual.get(0));
				}
			}
		}

		//Remove CBC tests from normal tests. (If found)
		if (containsCBC) {
			List<String> CBCTestsCodes = resultsWrapperSet	.stream().filter(rws -> rws.getReportName().equals("CBC")).flatMap(
					rws -> rws.getLabTestActual().stream().map(lta -> lta.getTestDefinition().getStandardCode()))
															.collect(Collectors.toList());

			for (String testCode : CBCTestsCodes) {
				List<LabTestActual> tempLabTestActual = normalTests	.stream().filter(
						lta -> lta.getTestDefinition().getStandardCode().equals(testCode))
																	.collect(Collectors.toList());
				if (!CollectionUtil.isCollectionEmpty(tempLabTestActual)) {
					normalTests.remove(tempLabTestActual.get(0));
				}
			}
		}

		//Operate special tests first		
		if (!CollectionUtil.isCollectionEmpty(normalTests)) {
			Set<LabTestActual> labTestActualSet = new TreeSet<LabTestActual>();
			labTestActualSet.addAll(normalTests	.stream().filter(
					lta -> lta.getTestDefinition().getLkpReportType().getCode().equals(ReportType.DEFAULT.getValue()) != true)
												.collect(Collectors.toList()));

			resultsWrapperSet.addAll(
					getSampleWorksheetData(labSample, labTestActualSet));

			//Remove special tests from normal list
			for (LabTestActual labTestActual : labTestActualSet) {
				normalTests.remove(labTestActual);
			}

			resultsWrapperSet.addAll(
					getSampleWorksheetData(labSample, new TreeSet<LabTestActual>(normalTests)));
		}

		return resultsWrapperSet;
		//return getSampleWorksheetData(labSample, labSample.getLabTestActualSet());
	}

	public List<VisitResultsWrapper> getAllSampleWorksheetReport(Long visitRid) {
		List<VisitResultsWrapper> resultsWrapperSet = new ArrayList<VisitResultsWrapper>();
		EmrVisit emrVisit = emrVisitService.getVisitWorksheets(visitRid,
				Arrays.asList(OperationStatus.ABORTED.getValue(), OperationStatus.CANCELLED.getValue()));

		if (emrVisit == null || CollectionUtil.isCollectionEmpty(emrVisit.getLabSamples())) {
			throw new BusinessException("No Samples Found", "noTestInOrder", ErrorSeverity.ERROR);
		}

		//These values are taken from application.props
		List<String> specialTests = Arrays.asList(SPECIAL_TESTS.split(","));
		List<String> specialTestsStool = Arrays.asList(SPECIAL_TESTS_STOOL.split(","));
		List<String> specialTestsCBC = Arrays.asList(SPECIAL_TESTS_CBC.split(","));
		Boolean containsStool = false;
		Boolean containsCBC = false;

		List<LabTestActual> normalTests = emrVisit	.getLabSamples().stream().flatMap(ls -> ls.getLabTestActualSet().stream())
													.collect(Collectors.toList());

		List<LabTestActual> labTestActualList = null;
		List<LabTestActual> filteredLabTestActualList = null;
		LabSample labSample = null;

		for (String testCode : specialTests) {
			labTestActualList = emrVisit.getLabSamples().stream().flatMap(
					ls -> ls.getLabTestActualSet().stream().filter(lta -> lta.getTestDefinition().getStandardCode().equals(testCode)))
										.collect(Collectors.toList());

			if (!CollectionUtil.isCollectionEmpty(labTestActualList)) {
				labSample = labTestActualList.iterator().next().getLabSample();
				if (testCode.equals("STA")) {
					//Get stool tests
					containsStool = true;
					filteredLabTestActualList = emrVisitService.findRelatedTests(emrVisit, specialTestsStool, normalTests);
				} else if (testCode.startsWith("CBC")) {
					//Get CBC tests
					containsCBC = true;
					List<String> tempList = new ArrayList<String>();
					tempList.add(testCode);
					tempList.addAll(specialTestsCBC);
					filteredLabTestActualList = emrVisitService.findRelatedTests(emrVisit, tempList, normalTests);

				}
				resultsWrapperSet.addAll(getSampleWorksheetData(labSample, new TreeSet<LabTestActual>(filteredLabTestActualList)));
			}
		}

		//Remove Stool tests from normal tests. (If found)
		//Here using STOOL instead of STA because report type is STOOL
		if (containsStool) {
			List<String> StoolTestsCodes = resultsWrapperSet.stream().filter(rws -> rws.getReportName().equals("STOOL")).flatMap(
					rws -> rws.getLabTestActual().stream().map(lta -> lta.getTestDefinition().getStandardCode()))
															.collect(Collectors.toList());
			for (String testCode : StoolTestsCodes) {
				List<LabTestActual> tempLabTestActual = normalTests	.stream().filter(
						lta -> lta.getTestDefinition().getStandardCode().equals(testCode))
																	.collect(Collectors.toList());
				if (!CollectionUtil.isCollectionEmpty(tempLabTestActual)) {
					normalTests.remove(tempLabTestActual.get(0));
				}
			}
		}

		//Remove CBC tests from normal tests. (If found)
		if (containsCBC) {
			List<String> CBCTestsCodes = resultsWrapperSet	.stream().filter(rws -> rws.getReportName().equals("CBC")).flatMap(
					rws -> rws.getLabTestActual().stream().map(lta -> lta.getTestDefinition().getStandardCode()))
															.collect(Collectors.toList());

			for (String testCode : CBCTestsCodes) {
				List<LabTestActual> tempLabTestActual = normalTests	.stream().filter(
						lta -> lta.getTestDefinition().getStandardCode().equals(testCode))
																	.collect(Collectors.toList());
				if (!CollectionUtil.isCollectionEmpty(tempLabTestActual)) {
					normalTests.remove(tempLabTestActual.get(0));
				}
			}
		}

		//Operate special tests first
		if (!CollectionUtil.isCollectionEmpty(normalTests)) {
			List<LabSample> labSampleList = normalTests.stream().map(lta -> lta.getLabSample()).distinct().collect(Collectors.toList());
			for (LabSample labSampleNormal : labSampleList) {
				Set<LabTestActual> labTestActualSet = new TreeSet<LabTestActual>();
				labTestActualSet.addAll(normalTests	.stream().filter(
						lta -> lta.getTestDefinition().getLkpReportType().getCode().equals(ReportType.DEFAULT.getValue()) != true
								&& lta.getLabSample().equals(labSampleNormal))
													.collect(Collectors.toList()));

				resultsWrapperSet.addAll(
						getSampleWorksheetData(labSampleNormal, labTestActualSet));

				//Remove special tests from normal list
				//for (LabTestActual labTestActual : labTestActualSet) {
				//	normalTests.remove(labTestActual);
				//}

				labTestActualSet.clear();
				labTestActualSet.addAll(normalTests	.stream().filter(
						lta -> lta.getTestDefinition().getLkpReportType().getCode().equals(ReportType.DEFAULT.getValue()) == true
								&& lta.getLabSample().equals(labSampleNormal))
													.collect(Collectors.toList()));

				resultsWrapperSet.addAll(
						getSampleWorksheetData(labSampleNormal, labTestActualSet));
			}
		}
		return resultsWrapperSet;
	}
}

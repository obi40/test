package com.optimiza.ehope.lis.model;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.envers.Audited;
import org.hibernate.envers.RelationTargetAuditMode;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.optimiza.core.base.entity.BaseAuditableTenantedEntity;
import com.optimiza.core.common.util.BooleanIntegerConverter;
import com.optimiza.ehope.lis.lkp.model.LkpReportType;
import com.optimiza.ehope.lis.lkp.model.LkpSpecimenType;
import com.optimiza.ehope.lis.lkp.model.LkpTestingMethod;

/**
 * TestDefinition.java
 * 
 * @author Murad Poladian <mpoladian@optimizasolutions.com>
 * @since Aug/3/2017
 * 
 */
@Entity
@Table(name = "test_definition")
@Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
public class TestDefinition extends BaseAuditableTenantedEntity implements Serializable, Comparable<TestDefinition> {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Basic(optional = false)
	@Column(name = "rid")
	private Long rid;

	@Column(name = "deactivation_date")
	@Temporal(TemporalType.TIMESTAMP)
	private Date deactivationDate;

	@Column(name = "additional_testing_requirements")
	private String additionalTestingRequirements;

	@Column(name = "advisory_information")
	private String advisoryInformation;

	@Column(name = "aliases")
	private String aliases;

	@Column(name = "analytic_time")
	private String analyticTime;

	@Size(min = 0, max = 4000)
	@Column(name = "disclaimer")
	private String disclaimer;

	@NotNull
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "specimen_type_id")
	private LkpSpecimenType specimenType;

	@Column(name = "cpt_code")
	private String cptCode;

	@Column(name = "cpt_units")
	private String cptUnits;

	@Column(name = "days_times_performed")
	private String daysTimesPerformed;

	@NotNull
	@Size(min = 1, max = 4000)
	@Column(name = "description")
	private String description;

	@Column(name = "maximum_lab_time")
	private String maximumLabTime;

	@NotNull
	@Column(name = "standard_code")
	private String standardCode;

	@Column(name = "secondary_code")
	private String secondaryCode;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "testing_method_id")
	private LkpTestingMethod lkpTestingMethod;

	@Column(name = "necessary_information")
	private String necessaryInformation;

	@Column(name = "orderable_separately")
	@Convert(converter = BooleanIntegerConverter.class)
	private Boolean orderableSeparately;

	@NotNull
	@Column(name = "is_active")
	@Convert(converter = BooleanIntegerConverter.class)
	private Boolean isActive;

	@NotNull
	@Column(name = "is_allow_repetition")
	@Convert(converter = BooleanIntegerConverter.class)
	private Boolean isAllowRepetition;

	@NotNull
	@Column(name = "is_repetition_separate_sample")
	@Convert(converter = BooleanIntegerConverter.class)
	//This flag is used for repeated tests inside same order
	//True means all actuals of this test-def will have THEIR OWN SAMPLE
	//False means all actuals of this test-def will have THE SAME SAMPLE
	private Boolean isRepetitionSeparateSample;

	@NotNull
	@Column(name = "is_repetition_chargeable")
	@Convert(converter = BooleanIntegerConverter.class)
	//This flag is used for repeated tests inside same order
	//True means each actual will be charged AS MANY TIMES AS REQUESTED
	//False means all actuals will be charged ONCE
	private Boolean isRepetitionChargeable;

	@NotNull
	@Column(name = "is_panel")
	@Convert(converter = BooleanIntegerConverter.class)
	private Boolean isPanel;

	@NotNull
	@Column(name = "is_separate_page")
	@Convert(converter = BooleanIntegerConverter.class)
	private Boolean isSeparatePage;

	@NotNull
	@Column(name = "is_separate_sample")
	@Convert(converter = BooleanIntegerConverter.class)
	private Boolean isSeparateSample;

	//TODO hide from excel import sheet
	@Column(name = "normal_range_text")
	private String normalRangeText;

	@Column(name = "reporting_description")
	private String reportingDescription;

	@Column(name = "shipping_instructions")
	private String shippingInstructions;

	//TODO to be revisited and hide from excel sheet
	@Column(name = "urine_preservative_collection_options")
	private String urinePreservativeCollectionOptions;

	@Column(name = "useful_for")
	private String usefulFor;

	@Column(name = "testing_algorithm")
	private String testingAlgorithm;

	@Column(name = "clinical_information")
	private String clinicalInformation;

	@Column(name = "interpretation")
	private String interpretation;

	@Column(name = "cautions")
	private String cautions;

	@Column(name = "clinical_reference")
	private String clinicalReference;

	@Column(name = "special_instructions")
	private String specialInstructions;

	@Column(name = "supportive_data")
	private String supportiveData;

	@Column(name = "genetics_test_information")
	private String geneticsTestInformation;

	@Column(name = "loinc_code")
	private String loincCode;

	@NotNull
	@Column(name = "rank")
	private Long rank;

	//TODO this has to be imported at an earlier stage
	@NotNull
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "section_id")
	private LabSection section;

	//TODO HIDE temporarily 
	//TODO figure this out / special instrutions?
	//bi-directional many-to-one association to TestForm
	@OneToMany(mappedBy = "testDefinition", fetch = FetchType.LAZY)
	@JsonIgnoreProperties(value = { "testDefinition", "extraTest" }, allowSetters = true)
	private Set<TestForm> testForms;

	//TODO add separate sheet
	//bi-directional many-to-one association to TestQuestion
	@OneToMany(mappedBy = "testDefinition", fetch = FetchType.LAZY)
	@JsonIgnoreProperties(value = "testDefinition", allowSetters = true)
	private Set<TestQuestion> testQuestions;

	@Transient
	@JsonDeserialize
	private List<TestQuestion> testQuestionList;

	//TODO add separate sheet
	//bi-directional many-to-one association to TestResult
	@OneToMany(mappedBy = "testDefinition", fetch = FetchType.LAZY)
	@JsonIgnoreProperties(value = { "testDefinition" }, allowSetters = true)
	private Set<TestResult> testResults;

	@Transient
	@JsonDeserialize
	private List<TestResult> testResultList;

	//TODO add separate sheet
	@Transient
	@JsonDeserialize
	private List<BillPricing> prices;

	//TODO sub sheet in the import sheet
	//bi-directional many-to-one association to TestSpecimen
	@OneToMany(mappedBy = "testDefinition", fetch = FetchType.LAZY)
	@JsonIgnoreProperties(value = { "testDefinition" }, allowSetters = true)
	private Set<TestSpecimen> testSpecimens;

	@Transient
	@JsonDeserialize
	private List<TestSpecimen> testSpecimenList;

	//TODO hide from import excel sheet
	@OneToMany(mappedBy = "test", fetch = FetchType.LAZY)
	private Set<ExtraTest> extraTests;

	@Transient
	@JsonDeserialize
	private List<ExtraTest> extraTestList;

	@OneToMany(mappedBy = "extraTest", fetch = FetchType.LAZY)
	private Set<ExtraTest> tests;

	//bi-directional many-to-one association to TestRequestFormTestDefinition
	@OneToMany(mappedBy = "testDefinition", fetch = FetchType.LAZY)
	private Set<TestRequestFormTestDefinition> testRequestFormTestDefinitions;

	//TODO hide from excel
	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "loinc_attributes_id")
	private LoincAttributes loincAttributes;

	//bi-directional many-to-one association to BillTestItem
	@OneToMany(mappedBy = "testDefinition", fetch = FetchType.LAZY)
	@JsonIgnoreProperties(value = "testDefinition", allowSetters = true)
	private Set<BillTestItem> billTestItems;

	//bi-directional many-to-one association to LabTestActual
	@OneToMany(mappedBy = "testDefinition", fetch = FetchType.LAZY)
	@JsonIgnoreProperties(value = "testDefinition", allowSetters = true)
	private Set<LabTestActual> testActualList;

	//bi-directional one-to-many association to TestGroupDefinition
	@OneToMany(mappedBy = "testDefinition", fetch = FetchType.LAZY)
	@JsonIgnoreProperties(value = "testDefinition", allowSetters = true)
	private List<TestGroupDefinition> groupDefinitions;

	//TODO hide from excel sheet
	//bi-directional many-to-one association to LabTestActual
	@OneToMany(mappedBy = "testDefinition", fetch = FetchType.LAZY)
	@JsonIgnoreProperties(value = "testDefinition", allowSetters = true)
	private Set<TestDestination> destinations;

	@Transient
	@JsonDeserialize
	private List<TestDestination> destinationList;

	//bi-directional many-to-one association to LabTestActual
	@OneToMany(mappedBy = "test", fetch = FetchType.LAZY)
	@JsonIgnoreProperties(value = "test", allowSetters = true)
	private Set<Interpretation> interpretations;

	@Transient
	@JsonDeserialize
	private List<Interpretation> interpretationList;

	@Column(name = "allergy_decimals")
	private Integer allergyDecimals;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "allergy_unit_id")
	private LabUnit allergyUnit;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "report_type_id")
	private LkpReportType lkpReportType;

	@OneToMany(mappedBy = "testDefinition", fetch = FetchType.LAZY)
	@JsonIgnoreProperties(value = "testDefinition", allowSetters = true)
	private Set<TestDisclaimer> testDisclaimerSet;

	@Transient
	@JsonDeserialize
	private List<TestDisclaimer> testDisclaimerList;

	public TestDefinition() {
	}

	@Override
	public Long getRid() {
		return this.rid;
	}

	public void setRid(Long rid) {
		this.rid = rid;
	}

	public Boolean getIsSeparateSample() {
		return isSeparateSample;
	}

	public void setIsSeparateSample(Boolean isSeparateSample) {
		this.isSeparateSample = isSeparateSample;
	}

	public String getDisclaimer() {
		return disclaimer;
	}

	public void setDisclaimer(String disclaimer) {
		this.disclaimer = disclaimer;
	}

	public Set<TestDisclaimer> getTestDisclaimerSet() {
		return testDisclaimerSet;
	}

	public void setTestDisclaimerSet(Set<TestDisclaimer> testDisclaimerSet) {
		this.testDisclaimerSet = testDisclaimerSet;
	}

	public Boolean getIsSeparatePage() {
		return isSeparatePage;
	}

	public void setIsSeparatePage(Boolean isSeparatePage) {
		this.isSeparatePage = isSeparatePage;
	}

	public Date getDeactivationDate() {
		return deactivationDate;
	}

	public void setDeactivationDate(Date deactivationDate) {
		this.deactivationDate = deactivationDate;
	}

	public String getAdditionalTestingRequirements() {
		return this.additionalTestingRequirements;
	}

	public void setAdditionalTestingRequirements(String additionalTestingRequirements) {
		this.additionalTestingRequirements = additionalTestingRequirements;
	}

	public String getAdvisoryInformation() {
		return this.advisoryInformation;
	}

	public void setAdvisoryInformation(String advisoryInformation) {
		this.advisoryInformation = advisoryInformation;
	}

	public String getAliases() {
		return aliases;
	}

	public void setAliases(String aliases) {
		this.aliases = aliases;
	}

	public String getAnalyticTime() {
		return this.analyticTime;
	}

	public void setAnalyticTime(String analyticTime) {
		this.analyticTime = analyticTime;
	}

	public String getCptCode() {
		return this.cptCode;
	}

	public void setCptCode(String cptCode) {
		this.cptCode = cptCode;
	}

	public String getCptUnits() {
		return this.cptUnits;
	}

	public void setCptUnits(String cptUnits) {
		this.cptUnits = cptUnits;
	}

	public String getDaysTimesPerformed() {
		return this.daysTimesPerformed;
	}

	public void setDaysTimesPerformed(String daysTimesPerformed) {
		this.daysTimesPerformed = daysTimesPerformed;
	}

	public String getDescription() {
		return this.description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getMaximumLabTime() {
		return this.maximumLabTime;
	}

	public void setMaximumLabTime(String maximumLabTime) {
		this.maximumLabTime = maximumLabTime;
	}

	public String getStandardCode() {
		return this.standardCode;
	}

	public void setStandardCode(String standardCode) {
		this.standardCode = standardCode;
	}

	public LkpTestingMethod getLkpTestingMethod() {
		return lkpTestingMethod;
	}

	public void setLkpTestingMethod(LkpTestingMethod lkpTestingMethod) {
		this.lkpTestingMethod = lkpTestingMethod;
	}

	public LkpSpecimenType getSpecimenType() {
		return specimenType;
	}

	public void setSpecimenType(LkpSpecimenType specimenType) {
		this.specimenType = specimenType;
	}

	public String getNecessaryInformation() {
		return this.necessaryInformation;
	}

	public void setNecessaryInformation(String necessaryInformation) {
		this.necessaryInformation = necessaryInformation;
	}

	public Boolean getOrderableSeparately() {
		return orderableSeparately;
	}

	public void setOrderableSeparately(Boolean orderableSeparately) {
		this.orderableSeparately = orderableSeparately;
	}

	public Boolean getIsActive() {
		return isActive;
	}

	public void setIsActive(Boolean isActive) {
		this.isActive = isActive;
	}

	public Boolean getIsAllowRepetition() {
		return isAllowRepetition;
	}

	public void setIsAllowRepetition(Boolean isAllowRepetition) {
		this.isAllowRepetition = isAllowRepetition;
	}

	public Boolean getIsRepetitionSeparateSample() {
		return isRepetitionSeparateSample;
	}

	public void setIsRepetitionSeparateSample(Boolean isRepetitionSeparateSample) {
		this.isRepetitionSeparateSample = isRepetitionSeparateSample;
	}

	public Boolean getIsRepetitionChargeable() {
		return isRepetitionChargeable;
	}

	public void setIsRepetitionChargeable(Boolean isRepetitionChargeable) {
		this.isRepetitionChargeable = isRepetitionChargeable;
	}

	public Boolean getIsPanel() {
		return isPanel;
	}

	public void setIsPanel(Boolean isPanel) {
		this.isPanel = isPanel;
	}

	public String getNormalRangeText() {
		return normalRangeText;
	}

	public void setNormalRangeText(String normalRangeText) {
		this.normalRangeText = normalRangeText;
	}

	public String getReportingDescription() {
		return this.reportingDescription;
	}

	public void setReportingDescription(String reportingDescription) {
		this.reportingDescription = reportingDescription;
	}

	public String getShippingInstructions() {
		return this.shippingInstructions;
	}

	public void setShippingInstructions(String shippingInstructions) {
		this.shippingInstructions = shippingInstructions;
	}

	public String getUrinePreservativeCollectionOptions() {
		return this.urinePreservativeCollectionOptions;
	}

	public void setUrinePreservativeCollectionOptions(String urinePreservativeCollectionOptions) {
		this.urinePreservativeCollectionOptions = urinePreservativeCollectionOptions;
	}

	public String getUsefulFor() {
		return usefulFor;
	}

	public void setUsefulFor(String usefulFor) {
		this.usefulFor = usefulFor;
	}

	public String getTestingAlgorithm() {
		return testingAlgorithm;
	}

	public void setTestingAlgorithm(String testingAlgorithm) {
		this.testingAlgorithm = testingAlgorithm;
	}

	public String getClinicalInformation() {
		return clinicalInformation;
	}

	public void setClinicalInformation(String clinicalInformation) {
		this.clinicalInformation = clinicalInformation;
	}

	public String getInterpretation() {
		return interpretation;
	}

	public void setInterpretation(String interpretation) {
		this.interpretation = interpretation;
	}

	public String getCautions() {
		return cautions;
	}

	public void setCautions(String cautions) {
		this.cautions = cautions;
	}

	public String getClinicalReference() {
		return clinicalReference;
	}

	public void setClinicalReference(String clinicalReference) {
		this.clinicalReference = clinicalReference;
	}

	public String getSpecialInstructions() {
		return specialInstructions;
	}

	public void setSpecialInstructions(String specialInstructions) {
		this.specialInstructions = specialInstructions;
	}

	public String getSupportiveData() {
		return supportiveData;
	}

	public void setSupportiveData(String supportiveData) {
		this.supportiveData = supportiveData;
	}

	public String getGeneticsTestInformation() {
		return geneticsTestInformation;
	}

	public void setGeneticsTestInformation(String geneticsTestInformation) {
		this.geneticsTestInformation = geneticsTestInformation;
	}

	public String getLoincCode() {
		return loincCode;
	}

	public void setLoincCode(String loincCode) {
		this.loincCode = loincCode;
	}

	public LabSection getSection() {
		return section;
	}

	public void setSection(LabSection section) {
		this.section = section;
	}

	public Long getRank() {
		return rank;
	}

	public void setRank(Long rank) {
		this.rank = rank;
	}

	public Set<TestForm> getTestForms() {
		return this.testForms;
	}

	public void setTestForms(Set<TestForm> testForms) {
		this.testForms = testForms;
	}

	public Set<TestQuestion> getTestQuestions() {
		return this.testQuestions;
	}

	public void setTestQuestions(Set<TestQuestion> testOrderQuestions) {
		this.testQuestions = testOrderQuestions;
	}

	public List<TestQuestion> getTestQuestionList() {
		return testQuestionList;
	}

	public void setTestQuestionList(List<TestQuestion> testQuestionList) {
		this.testQuestionList = testQuestionList;
	}

	public Set<TestResult> getTestResults() {
		return this.testResults;
	}

	public void setTestResults(Set<TestResult> testResults) {
		this.testResults = testResults;
	}

	public List<TestResult> getTestResultList() {
		return testResultList;
	}

	public void setTestResultList(List<TestResult> testResultList) {
		this.testResultList = testResultList;
	}

	public List<BillPricing> getPrices() {
		return prices;
	}

	public void setPrices(List<BillPricing> prices) {
		this.prices = prices;
	}

	public Set<ExtraTest> getExtraTests() {
		return extraTests;
	}

	public void setExtraTests(Set<ExtraTest> extraTests) {
		this.extraTests = extraTests;
	}

	public List<ExtraTest> getExtraTestList() {
		return extraTestList;
	}

	public void setExtraTestList(List<ExtraTest> extraTestList) {
		this.extraTestList = extraTestList;
	}

	public Set<ExtraTest> getTests() {
		return tests;
	}

	public void setTests(Set<ExtraTest> tests) {
		this.tests = tests;
	}

	public Set<TestSpecimen> getTestSpecimens() {
		return testSpecimens;
	}

	public void setTestSpecimens(Set<TestSpecimen> testSpecimens) {
		this.testSpecimens = testSpecimens;
	}

	public List<TestSpecimen> getTestSpecimenList() {
		return testSpecimenList;
	}

	public void setTestSpecimenList(List<TestSpecimen> testSpecimenList) {
		this.testSpecimenList = testSpecimenList;
	}

	public Set<TestRequestFormTestDefinition> getTestRequestFormTestDefinitions() {
		return testRequestFormTestDefinitions;
	}

	public void setTestRequestFormTestDefinitions(Set<TestRequestFormTestDefinition> testRequestFormTestDefinitions) {
		this.testRequestFormTestDefinitions = testRequestFormTestDefinitions;
	}

	public LoincAttributes getLoincAttributes() {
		return loincAttributes;
	}

	public void setLoincAttributes(LoincAttributes loincAttributes) {
		this.loincAttributes = loincAttributes;
	}

	public Set<BillTestItem> getBillTestItems() {
		return billTestItems;
	}

	public void setBillTestItems(Set<BillTestItem> billTestItems) {
		this.billTestItems = billTestItems;
	}

	public Set<LabTestActual> getTestActualList() {
		return testActualList;
	}

	public void setTestActualList(Set<LabTestActual> testActualList) {
		this.testActualList = testActualList;
	}

	public Set<TestDestination> getDestinations() {
		return destinations;
	}

	public void setDestinations(Set<TestDestination> destinations) {
		this.destinations = destinations;
	}

	public List<TestDestination> getDestinationList() {
		return destinationList;
	}

	public void setDestinationList(List<TestDestination> destinationList) {
		this.destinationList = destinationList;
	}

	public Set<Interpretation> getInterpretations() {
		return interpretations;
	}

	public void setInterpretations(Set<Interpretation> interpretations) {
		this.interpretations = interpretations;
	}

	public List<Interpretation> getInterpretationList() {
		return interpretationList;
	}

	public void setInterpretationList(List<Interpretation> interpretationList) {
		this.interpretationList = interpretationList;
	}

	public String getSecondaryCode() {
		return secondaryCode;
	}

	public void setSecondaryCode(String secondaryCode) {
		this.secondaryCode = secondaryCode;
	}

	public List<TestGroupDefinition> getGroupDefinitions() {
		return groupDefinitions;
	}

	public void setGroupDefinitions(List<TestGroupDefinition> groupDefinitions) {
		this.groupDefinitions = groupDefinitions;
	}

	public Integer getAllergyDecimals() {
		return allergyDecimals;
	}

	public void setAllergyDecimals(Integer allergyDecimals) {
		this.allergyDecimals = allergyDecimals;
	}

	public LabUnit getAllergyUnit() {
		return allergyUnit;
	}

	public void setAllergyUnit(LabUnit allergyUnit) {
		this.allergyUnit = allergyUnit;
	}

	public LkpReportType getLkpReportType() {
		return lkpReportType;
	}

	public void setLkpReportType(LkpReportType lkpReportType) {
		this.lkpReportType = lkpReportType;
	}

	public List<TestDisclaimer> getTestDisclaimerList() {
		return testDisclaimerList;
	}

	public void setTestDisclaimerList(List<TestDisclaimer> testDisclaimerList) {
		this.testDisclaimerList = testDisclaimerList;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((rid == null) ? 0 : rid.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		TestDefinition other = (TestDefinition) obj;
		if (rid == null) {
			if (other.rid != null)
				return false;
		} else if (!rid.equals(other.rid))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "TestDefinition [rid=" + rid + "]";
	}

	@Override
	public int compareTo(TestDefinition other) {
		int compareValue;
		if (section.getRank().equals(other.getSection().getRank())) {
			compareValue = rank.compareTo(other.getRank());
		} else {
			compareValue = section.compareTo(other.getSection());
		}
		if (compareValue == 0) {
			compareValue = 1;
		}
		return compareValue;
	}

}
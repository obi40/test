package com.optimiza.ehope.lis.model;

import java.io.Serializable;
import java.math.BigDecimal;
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
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;

import org.hibernate.envers.Audited;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.optimiza.core.base.entity.BaseAuditableTenantedEntity;
import com.optimiza.core.common.util.BooleanIntegerConverter;
import com.optimiza.ehope.lis.lkp.model.LkpResultValueType;
import com.optimiza.ehope.lis.lkp.model.LkpUnitType;

/**
 * TestResult.java
 * 
 * @author Murad Poladian <mpoladian@optimizasolutions.com>
 * @since Aug/3/2017
 * 
 */
@Entity
@Table(name = "test_result")
@Audited
public class TestResult extends BaseAuditableTenantedEntity implements Serializable, Comparable<TestResult> {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Basic(optional = false)
	@Column(name = "rid")
	private Long rid;

	@Column(name = "description")
	private String description;

	@Column(name = "loinc_code")
	private String loincCode;

	@NotNull
	@Column(name = "standard_code")
	private String standardCode;

	@NotNull
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "result_value_type_id")
	private LkpResultValueType resultValueType;

	@Column(name = "reporting_description")
	private String reportingDescription;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "primary_unit_id")
	private LabUnit primaryUnit;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "secondary_unit_id")
	private LabUnit secondaryUnit;

	//bi-directional many-to-one association to TestDefinition
	@NotNull
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "test_id")
	private TestDefinition testDefinition;

	//TODO what is this for?
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "loinc_attributes_id")
	private LoincAttributes loincAttributesObject;

	@Column(name = "factor", columnDefinition = "FLOAT(53)")
	private BigDecimal factor;

	@Column(name = "primary_decimals")
	private Integer primaryDecimals;

	@Column(name = "secondary_decimals")
	private Integer secondaryDecimals;

	@NotNull
	@Column(name = "print_order")
	private Integer printOrder;

	@NotNull
	@Column(name = "is_required")
	@Convert(converter = BooleanIntegerConverter.class)
	private Boolean isRequired;

	@NotNull
	@Column(name = "is_comprehensive")
	@Convert(converter = BooleanIntegerConverter.class)
	private Boolean isComprehensive;

	@NotNull
	@Column(name = "is_differential")
	@Convert(converter = BooleanIntegerConverter.class)
	private Boolean isDifferential;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "comprehensive_result_id")
	@JsonIgnoreProperties(value = { "comprehensiveResult", "testDefinition" }, allowSetters = true)
	private TestResult comprehensiveResult;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "primary_unit_type_id")
	private LkpUnitType primaryUnitType;

	//bi-directional many-to-one association to TestNormalRange
	@OneToMany(mappedBy = "testResult", fetch = FetchType.LAZY)
	@JsonIgnoreProperties(value = { "testResult" }, allowSetters = true)
	private Set<TestNormalRange> normalRanges;

	//bi-directional many-to-one association to NarrativeResultTemplate
	@OneToMany(mappedBy = "result", fetch = FetchType.LAZY)
	@JsonIgnoreProperties("result")
	private Set<NarrativeResultTemplate> narrativeTemplates;

	@Transient
	@JsonDeserialize
	private List<NarrativeResultTemplate> narrativeTemplateList;

	//bi-directional many-to-one association to TestCodedResultMapping
	@OneToMany(mappedBy = "testResult", fetch = FetchType.LAZY)
	@JsonIgnoreProperties("testResult")
	private Set<TestCodedResultMapping> testCodedResultMappings;

	@Transient
	@JsonDeserialize
	private List<TestNormalRange> normalRangeList;

	@Transient
	@JsonDeserialize
	private List<TestCodedResult> testCodedResultList;

	public TestResult() {
	}

	@Override
	public Long getRid() {
		return this.rid;
	}

	public BigDecimal getFactor() {
		return factor;
	}

	public void setFactor(BigDecimal factor) {
		this.factor = factor;
	}

	public void setRid(Long rid) {
		this.rid = rid;
	}

	public String getDescription() {
		return this.description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getLoincCode() {
		return this.loincCode;
	}

	public void setLoincCode(String loincCode) {
		this.loincCode = loincCode;
	}

	public String getStandardCode() {
		return this.standardCode;
	}

	public void setStandardCode(String standardCode) {
		this.standardCode = standardCode;
	}

	public String getReportingDescription() {
		return this.reportingDescription;
	}

	public void setReportingDescription(String reportingDescription) {
		this.reportingDescription = reportingDescription;
	}

	public TestDefinition getTestDefinition() {
		return this.testDefinition;
	}

	public void setTestDefinition(TestDefinition testDefinition) {
		this.testDefinition = testDefinition;
	}

	public LabUnit getPrimaryUnit() {
		return primaryUnit;
	}

	public void setPrimaryUnit(LabUnit primaryUnit) {
		this.primaryUnit = primaryUnit;
	}

	public LabUnit getSecondaryUnit() {
		return secondaryUnit;
	}

	public void setSecondaryUnit(LabUnit secondaryUnit) {
		this.secondaryUnit = secondaryUnit;
	}

	public Integer getPrimaryDecimals() {
		return primaryDecimals;
	}

	public void setPrimaryDecimals(Integer primaryDecimals) {
		this.primaryDecimals = primaryDecimals;
	}

	public Boolean getIsRequired() {
		return isRequired;
	}

	public void setIsRequired(Boolean isRequired) {
		this.isRequired = isRequired;
	}

	public Boolean getIsComprehensive() {
		return isComprehensive;
	}

	public void setIsComprehensive(Boolean isComprehensive) {
		this.isComprehensive = isComprehensive;
	}

	public Boolean getIsDifferential() {
		return isDifferential;
	}

	public void setIsDifferential(Boolean isDifferential) {
		this.isDifferential = isDifferential;
	}

	public TestResult getComprehensiveResult() {
		return comprehensiveResult;
	}

	public void setComprehensiveResult(TestResult comprehensiveResult) {
		this.comprehensiveResult = comprehensiveResult;
	}

	public Integer getSecondaryDecimals() {
		return secondaryDecimals;
	}

	public void setSecondaryDecimals(Integer secondaryDecimals) {
		this.secondaryDecimals = secondaryDecimals;
	}

	public Integer getPrintOrder() {
		return printOrder;
	}

	public void setPrintOrder(Integer printOrder) {
		this.printOrder = printOrder;
	}

	public LkpUnitType getPrimaryUnitType() {
		return primaryUnitType;
	}

	public void setPrimaryUnitType(LkpUnitType primaryUnitType) {
		this.primaryUnitType = primaryUnitType;
	}

	public LoincAttributes getLoincAttributesObject() {
		return loincAttributesObject;
	}

	public void setLoincAttributesObject(LoincAttributes loincAttributesObject) {
		this.loincAttributesObject = loincAttributesObject;
	}

	public LkpResultValueType getResultValueType() {
		return resultValueType;
	}

	public void setResultValueType(LkpResultValueType resultValueType) {
		this.resultValueType = resultValueType;
	}

	public Set<TestNormalRange> getNormalRanges() {
		return normalRanges;
	}

	public void setNormalRanges(Set<TestNormalRange> normalRanges) {
		this.normalRanges = normalRanges;
	}

	public Set<NarrativeResultTemplate> getNarrativeTemplates() {
		return narrativeTemplates;
	}

	public void setNarrativeTemplates(Set<NarrativeResultTemplate> narrativeTemplates) {
		this.narrativeTemplates = narrativeTemplates;
	}

	public List<NarrativeResultTemplate> getNarrativeTemplateList() {
		return narrativeTemplateList;
	}

	public void setNarrativeTemplateList(List<NarrativeResultTemplate> narrativeTemplateList) {
		this.narrativeTemplateList = narrativeTemplateList;
	}

	public Set<TestCodedResultMapping> getTestCodedResultMappings() {
		return testCodedResultMappings;
	}

	public void setTestCodedResultMappings(Set<TestCodedResultMapping> testCodedResultMappings) {
		this.testCodedResultMappings = testCodedResultMappings;
	}

	public List<TestNormalRange> getNormalRangeList() {
		return normalRangeList;
	}

	public void setNormalRangeList(List<TestNormalRange> normalRangeList) {
		this.normalRangeList = normalRangeList;
	}

	public List<TestCodedResult> getTestCodedResultList() {
		return testCodedResultList;
	}

	public void setTestCodedResultList(List<TestCodedResult> testCodedResultList) {
		this.testCodedResultList = testCodedResultList;
	}

	@Override
	public int compareTo(TestResult other) {
		int compareValue = printOrder.compareTo(other.getPrintOrder());
		if (compareValue == 0) {
			compareValue = 1;
		}
		return compareValue;
	}

}
package com.optimiza.ehope.lis.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.Set;

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
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.validation.constraints.Digits;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.envers.Audited;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.optimiza.core.base.entity.BaseAuditableTenantedEntity;
import com.optimiza.core.common.util.BooleanIntegerConverter;
import com.optimiza.core.lkp.model.LkpGender;
import com.optimiza.ehope.lis.lkp.model.LkpAgeUnit;

/**
 * TestNormalRange.java
 * 
 * @author Murad Poladian <mpoladian@optimizasolutions.com>
 * @since Nov/14/2017
 * 
 */
@Entity
@Table(name = "test_normal_ranges")
@Audited
public class TestNormalRange extends BaseAuditableTenantedEntity implements Serializable, Comparable<TestNormalRange> {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long rid;

	@Column(name = "age_from")
	private Integer ageFrom;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "age_from_unit_id")
	private LkpAgeUnit ageFromUnit;

	@Size(max = 2)
	@Column(name = "age_from_comparator")
	private String ageFromComparator;

	@Column(name = "age_to")
	private Integer ageTo;

	@Size(max = 2)
	@Column(name = "age_to_comparator")
	private String ageToComparator;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "age_to_unit_id")
	private LkpAgeUnit ageToUnit;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "coded_result_id")
	private TestCodedResult codedResult;

	@Column(name = "criterion_name")
	private String criterionName;

	@Column(name = "criterion_value")
	private String criterionValue;

	@Column(name = "max_value")
	@Digits(integer = 15, fraction = 5)
	private BigDecimal maxValue;

	@Size(max = 6)
	@Column(name = "max_value_comparator")
	private String maxValueComparator;

	@Column(name = "min_value")
	@Digits(integer = 15, fraction = 5)
	private BigDecimal minValue;

	@Size(max = 2)
	@Column(name = "min_value_comparator")
	private String minValueComparator;

	@Column(name = "max_rerun_value")
	@Digits(integer = 15, fraction = 5)
	private BigDecimal maxRerunValue;

	@Column(name = "min_rerun_value")
	@Digits(integer = 15, fraction = 5)
	private BigDecimal minRerunValue;

	@Column(name = "max_panic_value")
	@Digits(integer = 15, fraction = 5)
	private BigDecimal maxPanicValue;

	@Column(name = "min_panic_value")
	@Digits(integer = 15, fraction = 5)
	private BigDecimal minPanicValue;

	@Column(name = "ratio")
	private String ratio;

	@NotNull
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "result_id")
	private TestResult testResult;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "destination_id")
	@JsonIgnoreProperties(value = { "testDefinition" }, allowSetters = true)
	private TestDestination testDestination;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "sex_id")
	private LkpGender sex;

	private String description;

	@NotNull
	@Column(name = "print_order")
	private Integer printOrder;

	@NotNull
	@Column(name = "is_active")
	@Convert(converter = BooleanIntegerConverter.class)
	private Boolean isActive;

	@NotNull
	@Column(name = "state_change_date")
	@Temporal(TemporalType.TIMESTAMP)
	private Date stateChangeDate;

	//bi-directional many-to-one association to ActualResultNormalRange
	@OneToMany(mappedBy = "normalRange", fetch = FetchType.LAZY)
	@JsonIgnoreProperties("normalRange")
	private Set<ActualResultNormalRange> actualResultNormalRanges;

	@Transient
	@JsonSerialize
	private String primaryValueDescription;

	@Transient
	@JsonSerialize
	private String secondaryValueDescription;

	public TestNormalRange() {
	}

	@Override
	public Long getRid() {
		return this.rid;
	}

	public void setRid(Long rid) {
		this.rid = rid;
	}

	public Integer getAgeFrom() {
		return this.ageFrom;
	}

	public void setAgeFrom(Integer ageFrom) {
		this.ageFrom = ageFrom;
	}

	public LkpAgeUnit getAgeFromUnit() {
		return this.ageFromUnit;
	}

	public void setAgeFromUnit(LkpAgeUnit ageFromUnit) {
		this.ageFromUnit = ageFromUnit;
	}

	public String getAgeFromComparator() {
		return ageFromComparator;
	}

	public void setAgeFromComparator(String ageFromComparator) {
		this.ageFromComparator = ageFromComparator;
	}

	public Integer getAgeTo() {
		return this.ageTo;
	}

	public void setAgeTo(Integer ageTo) {
		this.ageTo = ageTo;
	}

	public LkpAgeUnit getAgeToUnit() {
		return this.ageToUnit;
	}

	public void setAgeToUnit(LkpAgeUnit ageToUnit) {
		this.ageToUnit = ageToUnit;
	}

	public String getAgeToComparator() {
		return ageToComparator;
	}

	public void setAgeToComparator(String ageToComparator) {
		this.ageToComparator = ageToComparator;
	}

	public TestCodedResult getCodedResult() {
		return this.codedResult;
	}

	public void setCodedResult(TestCodedResult codedResult) {
		this.codedResult = codedResult;
	}

	public String getCriterionName() {
		return this.criterionName;
	}

	public void setCriterionName(String criterionName) {
		this.criterionName = criterionName;
	}

	public String getCriterionValue() {
		return this.criterionValue;
	}

	public void setCriterionValue(String criterionValue) {
		this.criterionValue = criterionValue;
	}

	public BigDecimal getMaxValue() {
		return this.maxValue;
	}

	public void setMaxValue(BigDecimal maxValue) {
		this.maxValue = maxValue;
	}

	public BigDecimal getMinValue() {
		return this.minValue;
	}

	public void setMinValue(BigDecimal minValue) {
		this.minValue = minValue;
	}

	public BigDecimal getMaxRerunValue() {
		return maxRerunValue;
	}

	public void setMaxRerunValue(BigDecimal maxRerunValue) {
		this.maxRerunValue = maxRerunValue;
	}

	public BigDecimal getMinRerunValue() {
		return minRerunValue;
	}

	public void setMinRerunValue(BigDecimal minRerunValue) {
		this.minRerunValue = minRerunValue;
	}

	public BigDecimal getMaxPanicValue() {
		return maxPanicValue;
	}

	public void setMaxPanicValue(BigDecimal maxPanicValue) {
		this.maxPanicValue = maxPanicValue;
	}

	public BigDecimal getMinPanicValue() {
		return minPanicValue;
	}

	public void setMinPanicValue(BigDecimal minPanicValue) {
		this.minPanicValue = minPanicValue;
	}

	public String getRatio() {
		return ratio;
	}

	public void setRatio(String ratio) {
		this.ratio = ratio;
	}

	public TestResult getTestResult() {
		return this.testResult;
	}

	public void setTestResult(TestResult testResult) {
		this.testResult = testResult;
	}

	public TestDestination getTestDestination() {
		return testDestination;
	}

	public void setTestDestination(TestDestination testDestination) {
		this.testDestination = testDestination;
	}

	public LkpGender getSex() {
		return this.sex;
	}

	public void setSex(LkpGender sex) {
		this.sex = sex;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Integer getPrintOrder() {
		return printOrder;
	}

	public void setPrintOrder(Integer printOrder) {
		this.printOrder = printOrder;
	}

	public Boolean getIsActive() {
		return isActive;
	}

	public void setIsActive(Boolean isActive) {
		this.isActive = isActive;
	}

	public Date getStateChangeDate() {
		return stateChangeDate;
	}

	public void setStateChangeDate(Date stateChangeDate) {
		this.stateChangeDate = stateChangeDate;
	}

	public Set<ActualResultNormalRange> getActualResultNormalRanges() {
		return actualResultNormalRanges;
	}

	public void setActualResultNormalRanges(Set<ActualResultNormalRange> actualResultNormalRanges) {
		this.actualResultNormalRanges = actualResultNormalRanges;
	}

	public String getMaxValueComparator() {
		return maxValueComparator;
	}

	public void setMaxValueComparator(String maxValueComparator) {
		this.maxValueComparator = maxValueComparator;
	}

	public String getMinValueComparator() {
		return minValueComparator;
	}

	public void setMinValueComparator(String minValueComparator) {
		this.minValueComparator = minValueComparator;
	}

	public String getPrimaryValueDescription() {
		return primaryValueDescription;
	}

	public void setPrimaryValueDescription(String primaryValueDescription) {
		this.primaryValueDescription = primaryValueDescription;
	}

	public String getSecondaryValueDescription() {
		return secondaryValueDescription;
	}

	public void setSecondaryValueDescription(String secondaryValueDescription) {
		this.secondaryValueDescription = secondaryValueDescription;
	}

	@Override
	public int compareTo(TestNormalRange arg0) {
		return Integer.compare(printOrder, arg0.getPrintOrder());
	}

}
package com.optimiza.ehope.lis.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;
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
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.Digits;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import org.hibernate.envers.Audited;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.optimiza.core.base.entity.BaseAuditableBranchedEntity;
import com.optimiza.core.common.util.BooleanIntegerConverter;
import com.optimiza.ehope.lis.lkp.model.LkpOrganismDetection;

/**
 * The persistent class for the lab_test_actual database table.
 * 
 * @author Eshraq Albakri <ebakri@optimizasolutions.com>
 * @since Oct/12/2017
 * 
 */
@Entity
@Table(name = "lab_test_actual_result")
@Audited
public class LabTestActualResult extends BaseAuditableBranchedEntity implements Serializable, Comparable<LabTestActualResult> {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long rid;

	@Column(name = "is_confirmed")
	@Convert(converter = BooleanIntegerConverter.class)
	private Boolean isConfirmed;

	//TODO what is this for?
	private Long normality;

	@Column(name = "primary_result_value")
	private String primaryResultValue;

	@Column(name = "normal_range_text")
	private String normalRangeText;

	@Column(name = "primary_result_parsed")
	@Digits(integer = 15, fraction = 5)
	private BigDecimal primaryResultParsed;

	@Column(name = "percentage")
	@Max(value = 100)
	@Min(value = 0)
	private Integer percentage;

	@Column(name = "result_source_id")
	private Long resultSourceId;

	@Column(name = "secondary_result_parsed")
	@Digits(integer = 15, fraction = 5)
	private BigDecimal secondaryResultParsed;

	@Column(name = "ratio")
	private String ratio;

	@Column(name = "comments")
	private String comments;

	@Column(name = "narrative_text")
	private String narrativeText;

	@NotNull
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "actual_test_id")
	private LabTestActual labTestActual;

	@NotNull
	@JoinColumn(name = "test_result_id")
	@OneToOne(fetch = FetchType.LAZY)
	private TestResult labResult;

	//bi-directional many-to-one association to TestCodedResult
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "test_coded_result_id")
	private TestCodedResult testCodedResult;

	@Column(name = "is_amended")
	@Convert(converter = BooleanIntegerConverter.class)
	private Boolean isAmended;

	@Column(name = "amendment_reason")
	private String amendmentReason;

	//bi-directional many-to-one association to LkpOrganismDetection
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "organism_detection_id")
	private LkpOrganismDetection organismDetection;

	//bi-directional many-to-one association to ActualAntiMicrobial
	@OneToMany(mappedBy = "actualResult", fetch = FetchType.LAZY)
	@JsonIgnoreProperties("actualResult")
	private Set<ActualAntiMicrobial> actualAntiMicrobials;

	@Transient
	@JsonDeserialize
	private List<ActualAntiMicrobial> actualAntiMicrobialList;

	//bi-directional many-to-one association to ActualOrganism
	@OneToMany(mappedBy = "actualResult", fetch = FetchType.LAZY)
	@JsonIgnoreProperties("actualResult")
	private Set<ActualOrganism> actualOrganisms;

	@Transient
	@JsonDeserialize
	private List<ActualOrganism> actualOrganismList;

	//bi-directional many-to-one association to ActualResultNormalRange
	@OneToMany(mappedBy = "actualResult", fetch = FetchType.LAZY)
	@JsonIgnoreProperties("actualResult")
	private Set<ActualResultNormalRange> actualResultNormalRanges;

	@Transient
	@JsonSerialize
	private Long testDefinitionId;

	@Transient
	@JsonSerialize
	private String testDefinitionStandardCode;

	@Transient
	@JsonSerialize
	private Set<LabTestActualResult> previousResult;

	@Transient
	@JsonSerialize
	private Set<HistoricalResult> historicalResult;

	@Transient
	@JsonSerialize
	private String interpretationClass;

	@Transient
	@JsonSerialize
	private List<TestNormalRange> normalRangeList;

	public Set<HistoricalResult> getHistoricalResult() {
		return historicalResult;
	}

	public void setHistoricalResult(Set<HistoricalResult> historicalResult) {
		this.historicalResult = historicalResult;
	}

	public Set<LabTestActualResult> getPreviousResult() {
		return previousResult;
	}

	public void setPreviousResult(Set<LabTestActualResult> previousResult) {
		this.previousResult = previousResult;
	}

	public TestResult getLabResult() {
		return labResult;
	}

	public void setLabResult(TestResult labResult) {
		this.labResult = labResult;
	}

	public LabTestActualResult() {
	}

	@Override
	public Long getRid() {
		return this.rid;
	}

	public void setRid(Long rid) {
		this.rid = rid;
	}

	public Boolean getIsConfirmed() {
		return this.isConfirmed;
	}

	public void setIsConfirmed(Boolean isConfirmed) {
		this.isConfirmed = isConfirmed;
	}

	public Long getNormality() {
		return this.normality;
	}

	public void setNormality(Long normality) {
		this.normality = normality;
	}

	public String getPrimaryResultValue() {
		return primaryResultValue;
	}

	public void setPrimaryResultValue(String primaryResultValue) {
		this.primaryResultValue = primaryResultValue;
	}

	public BigDecimal getPrimaryResultParsed() {
		return primaryResultParsed;
	}

	public void setPrimaryResultParsed(BigDecimal primaryResultParsed) {
		this.primaryResultParsed = primaryResultParsed;
	}

	public Integer getPercentage() {
		return percentage;
	}

	public void setPercentage(Integer percentage) {
		this.percentage = percentage;
	}

	public BigDecimal getSecondaryResultParsed() {
		return secondaryResultParsed;
	}

	public void setSecondaryResultParsed(BigDecimal secondaryResultParsed) {
		this.secondaryResultParsed = secondaryResultParsed;
	}

	public String getRatio() {
		return ratio;
	}

	public void setRatio(String ratio) {
		this.ratio = ratio;
	}

	public String getComments() {
		return comments;
	}

	public void setComments(String comments) {
		this.comments = comments;
	}

	public String getNarrativeText() {
		return narrativeText;
	}

	public void setNarrativeText(String narrativeText) {
		this.narrativeText = narrativeText;
	}

	public String getNormalRangeText() {
		return normalRangeText;
	}

	public void setNormalRangeText(String normalRangeText) {
		this.normalRangeText = normalRangeText;
	}

	public Long getResultSourceId() {
		return this.resultSourceId;
	}

	public void setResultSourceId(Long resultSourceId) {
		this.resultSourceId = resultSourceId;
	}

	public LabTestActual getLabTestActual() {
		return labTestActual;
	}

	public void setLabTestActual(LabTestActual labTestActual) {
		this.labTestActual = labTestActual;
	}

	public TestCodedResult getTestCodedResult() {
		return testCodedResult;
	}

	public void setTestCodedResult(TestCodedResult testCodedResult) {
		this.testCodedResult = testCodedResult;
	}

	public Boolean getIsAmended() {
		return isAmended;
	}

	public void setIsAmended(Boolean isAmended) {
		this.isAmended = isAmended;
	}

	public String getAmendmentReason() {
		return amendmentReason;
	}

	public void setAmendmentReason(String amendmentReason) {
		this.amendmentReason = amendmentReason;
	}

	public LkpOrganismDetection getOrganismDetection() {
		return organismDetection;
	}

	public void setOrganismDetection(LkpOrganismDetection organismDetection) {
		this.organismDetection = organismDetection;
	}

	public Set<ActualAntiMicrobial> getActualAntiMicrobials() {
		return actualAntiMicrobials;
	}

	public void setActualAntiMicrobials(Set<ActualAntiMicrobial> actualAntiMicrobials) {
		this.actualAntiMicrobials = actualAntiMicrobials;
	}

	public List<ActualAntiMicrobial> getActualAntiMicrobialList() {
		return actualAntiMicrobialList;
	}

	public void setActualAntiMicrobialList(List<ActualAntiMicrobial> actualAntiMicrobialList) {
		this.actualAntiMicrobialList = actualAntiMicrobialList;
	}

	public Set<ActualOrganism> getActualOrganisms() {
		return actualOrganisms;
	}

	public void setActualOrganisms(Set<ActualOrganism> actualOrganisms) {
		this.actualOrganisms = actualOrganisms;
	}

	public List<ActualOrganism> getActualOrganismList() {
		return actualOrganismList;
	}

	public void setActualOrganismList(List<ActualOrganism> actualOrganismList) {
		this.actualOrganismList = actualOrganismList;
	}

	public Set<ActualResultNormalRange> getActualResultNormalRanges() {
		return actualResultNormalRanges;
	}

	public void setActualResultNormalRanges(Set<ActualResultNormalRange> actualResultNormalRanges) {
		this.actualResultNormalRanges = actualResultNormalRanges;
	}

	public Long getTestDefinitionId() {
		testDefinitionId = labTestActual.getTestDefinition().getRid();
		return testDefinitionId;
	}

	public String getTestDefinitionStandardCode() {
		testDefinitionStandardCode = labTestActual.getTestDefinition().getStandardCode();
		return testDefinitionStandardCode;
	}

	public String getInterpretationClass() {
		return interpretationClass;
	}

	public void setInterpretationClass(String interpretationClass) {
		this.interpretationClass = interpretationClass;
	}

	public List<TestNormalRange> getNormalRangeList() {
		return normalRangeList;
	}

	public void setNormalRangeList(List<TestNormalRange> normalRangeList) {
		this.normalRangeList = normalRangeList;
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
		LabTestActualResult other = (LabTestActualResult) obj;
		if (rid == null) {
			if (other.rid != null)
				return false;
		} else if (!rid.equals(other.rid))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "LabTestActualResult [rid=" + rid + "]";
	}

	@Override
	public int compareTo(LabTestActualResult other) {
		return labResult.compareTo(other.getLabResult());
	}

}
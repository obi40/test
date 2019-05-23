package com.optimiza.ehope.lis.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import javax.validation.constraints.Digits;
import javax.validation.constraints.NotNull;

import com.optimiza.core.admin.model.SecUser;
import com.optimiza.core.base.entity.BaseEntity;
import com.optimiza.core.common.util.BooleanIntegerConverter;
import com.optimiza.core.common.util.SecurityUtil;
import com.optimiza.ehope.lis.lkp.model.LkpOrganismDetection;

/**
 * AmendedActualResult.java
 * 
 * @author Murad Poladian <mpoladian@optimizasolutions.com>
 * @since Feb/6/2019
 * 
 */
//TODO: updatable = false
@Entity
@Table(name = "amended_actual_result")
public class AmendedActualResult extends BaseEntity implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long rid;

	@NotNull
	@Column(name = "tenant_id")
	private Long tenantId;

	@NotNull
	@Column(name = "created_by")
	private Long createdBy;

	@NotNull
	@Column(name = "creation_date")
	private Date creationDate;

	@NotNull
	@Column(name = "version")
	private Long version;

	@NotNull
	@JoinColumn(name = "actual_result_id")
	@ManyToOne(fetch = FetchType.LAZY)
	private LabTestActualResult actualResult;

	@Column(name = "amendment_reason")
	private String amendmentReason;

	private String comments;

	@Column(name = "is_amended")
	@Convert(converter = BooleanIntegerConverter.class)
	private Boolean isAmended;

	@Column(name = "is_confirmed")
	@Convert(converter = BooleanIntegerConverter.class)
	private Boolean isConfirmed;

	@Column(name = "narrative_text")
	private String narrativeText;

	@Column(name = "normal_range_text")
	private String normalRangeText;

	private Long normality;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "organism_detection_id")
	private LkpOrganismDetection organismDetection;

	@Column(name = "primary_result_parsed")
	@Digits(integer = 15, fraction = 5)
	private BigDecimal primaryResultParsed;

	@Column(name = "primary_result_value")
	private String primaryResultValue;

	@Column(name = "result_source_id")
	private Long resultSourceId;

	@Column(name = "secondary_result_parsed")
	@Digits(integer = 15, fraction = 5)
	private BigDecimal secondaryResultParsed;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "test_coded_result_id")
	private TestCodedResult testCodedResult;

	public AmendedActualResult() {
	}

	@PrePersist
	public void onPrePersist() {
		setCreationDate(new Date());
		SecUser user = SecurityUtil.getCurrentUser();
		setCreatedBy(user.getRid());
		setTenantId(user.getTenantId());
	}

	@Override
	public Long getRid() {
		return this.rid;
	}

	public void setRid(Long rid) {
		this.rid = rid;
	}

	public Long getTenantId() {
		return tenantId;
	}

	public void setTenantId(Long tenantId) {
		this.tenantId = tenantId;
	}

	public Long getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(Long createdBy) {
		this.createdBy = createdBy;
	}

	public Date getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}

	public Long getVersion() {
		return version;
	}

	public void setVersion(Long version) {
		this.version = version;
	}

	public LabTestActualResult getActualResult() {
		return actualResult;
	}

	public void setActualResult(LabTestActualResult actualResult) {
		this.actualResult = actualResult;
	}

	public String getAmendmentReason() {
		return amendmentReason;
	}

	public void setAmendmentReason(String amendmentReason) {
		this.amendmentReason = amendmentReason;
	}

	public String getComments() {
		return comments;
	}

	public void setComments(String comments) {
		this.comments = comments;
	}

	public Boolean getIsAmended() {
		return isAmended;
	}

	public void setIsAmended(Boolean isAmended) {
		this.isAmended = isAmended;
	}

	public Boolean getIsConfirmed() {
		return isConfirmed;
	}

	public void setIsConfirmed(Boolean isConfirmed) {
		this.isConfirmed = isConfirmed;
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

	public Long getNormality() {
		return normality;
	}

	public void setNormality(Long normality) {
		this.normality = normality;
	}

	public LkpOrganismDetection getOrganismDetection() {
		return organismDetection;
	}

	public void setOrganismDetection(LkpOrganismDetection organismDetection) {
		this.organismDetection = organismDetection;
	}

	public BigDecimal getPrimaryResultParsed() {
		return primaryResultParsed;
	}

	public void setPrimaryResultParsed(BigDecimal primaryResultParsed) {
		this.primaryResultParsed = primaryResultParsed;
	}

	public String getPrimaryResultValue() {
		return primaryResultValue;
	}

	public void setPrimaryResultValue(String primaryResultValue) {
		this.primaryResultValue = primaryResultValue;
	}

	public Long getResultSourceId() {
		return resultSourceId;
	}

	public void setResultSourceId(Long resultSourceId) {
		this.resultSourceId = resultSourceId;
	}

	public BigDecimal getSecondaryResultParsed() {
		return secondaryResultParsed;
	}

	public void setSecondaryResultParsed(BigDecimal secondaryResultParsed) {
		this.secondaryResultParsed = secondaryResultParsed;
	}

	public TestCodedResult getTestCodedResult() {
		return testCodedResult;
	}

	public void setTestCodedResult(TestCodedResult testCodedResult) {
		this.testCodedResult = testCodedResult;
	}

}
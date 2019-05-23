package com.optimiza.ehope.lis.model;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import com.optimiza.core.admin.model.SecUser;
import com.optimiza.core.base.entity.BaseEntity;
import com.optimiza.core.common.util.SecurityUtil;

/**
 * AmendedActualOrganism.java
 * 
 * @author Murad Poladian <mpoladian@optimizasolutions.com>
 * @since Feb/10/2019
 * 
 */
//TODO: updatable = false
@Entity
@Table(name = "amended_actual_organism")
public class AmendedActualOrganism extends BaseEntity implements Serializable {

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
	@JoinColumn(name = "amended_actual_result_id")
	@ManyToOne(fetch = FetchType.LAZY)
	private AmendedActualResult amendedActualResult;

	@Column(name = "colony_count")
	private String colonyCount;

	private String organism;

	public AmendedActualOrganism() {
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

	public AmendedActualResult getAmendedActualResult() {
		return amendedActualResult;
	}

	public void setAmendedActualResult(AmendedActualResult amendedActualResult) {
		this.amendedActualResult = amendedActualResult;
	}

	public String getColonyCount() {
		return colonyCount;
	}

	public void setColonyCount(String colonyCount) {
		this.colonyCount = colonyCount;
	}

	public String getOrganism() {
		return organism;
	}

	public void setOrganism(String organism) {
		this.organism = organism;
	}

}
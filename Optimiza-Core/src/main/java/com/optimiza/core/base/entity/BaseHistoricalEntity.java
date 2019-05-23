package com.optimiza.core.base.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.persistence.PrePersist;
import javax.persistence.PreRemove;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.Filter;
import org.hibernate.annotations.FilterDef;
import org.hibernate.annotations.ParamDef;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.optimiza.core.common.util.SecurityUtil;

@MappedSuperclass
@FilterDef(name = BaseHistoricalEntity.TENANT_FILTER, parameters = { @ParamDef(name = "tenantId", type = "long") })
@Filter(name = BaseHistoricalEntity.TENANT_FILTER, condition = "tenant_id = :tenantId")
@FilterDef(name = BaseHistoricalEntity.BRANCH_FILTER, parameters = { @ParamDef(name = "branchId", type = "long") })
@Filter(name = BaseHistoricalEntity.BRANCH_FILTER, condition = "branch_id = :branchId")
public abstract class BaseHistoricalEntity extends BaseEntity {

	private static final long serialVersionUID = 1L;

	@JsonIgnore
	public static final String BRANCH_FILTER = "branchFilter";

	@JsonIgnore
	public static final String TENANT_FILTER = "tenantFilter";

	@NotNull
	@Column(name = "CREATED_BY", updatable = false)
	private Long createdBy;

	@NotNull
	@Column(name = "CREATION_DATE", updatable = false)
	@Temporal(TemporalType.TIMESTAMP)
	private Date creationDate;

	@Column(name = "BRANCH_ID", updatable = false)
	@NotNull
	private Long branchId;

	@Column(name = "TENANT_ID")
	@NotNull
	private Long tenantId;

	@PrePersist
	private void prePersist() throws Exception {
		setCreationDate(new Date());
		setCreatedBy(SecurityUtil.getCurrentUser().getRid());
		setBranchId(SecurityUtil.getCurrentUser().getBranchId());
		setTenantId(SecurityUtil.getCurrentUser().getTenantId());
	}

	@PreRemove
	private void preRemove() throws Exception {
		throw new Exception("Remove not allowed on BaseHistoricalEntity");
	}

	/**
	 * @return ID of the user who created the record
	 */
	public final Long getCreatedBy() {
		return createdBy;
	}

	/**
	 * @param Set the ID of the user who created the record
	 */
	public final void setCreatedBy(Long createdBy) {
		this.createdBy = createdBy;
	}

	/**
	 * @return Date the record was created
	 */
	public final Date getCreationDate() {
		return creationDate;
	}

	/**
	 * @param Set the date the record was created
	 */
	public final void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}

	/**
	 * @return The branch ID of the entity
	 */
	public Long getBranchId() {
		return branchId;
	}

	/**
	 * @param branchId Sets the branch ID
	 */
	public void setBranchId(Long branchId) {
		this.branchId = branchId;
	}

	/**
	 * @return The tenant ID of the entity
	 */
	public Long getTenantId() {
		return tenantId;
	}

	/**
	 * @param tenantId Sets the tenant ID
	 */
	public void setTenantId(Long tenantId) {
		this.tenantId = tenantId;
	}

}
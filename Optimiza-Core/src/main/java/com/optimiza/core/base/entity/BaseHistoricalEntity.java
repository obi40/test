package com.optimiza.core.base.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.persistence.PrePersist;
import javax.persistence.PreRemove;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;

import com.optimiza.core.common.util.SecurityUtil;

@MappedSuperclass
public abstract class BaseHistoricalEntity extends BaseEntity {

	private static final long serialVersionUID = 1L;

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
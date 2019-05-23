package com.optimiza.ehope.lis.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import com.optimiza.core.base.entity.BaseAuditableTenantedEntity;

/**
 * BranchWorkbench.java
 * 
 * @author Murad Poladian <mpoladian@optimizasolutions.com>
 * @since Jan/10/2018
 * 
 */
@Entity
@Table(name = "branch_workbench")
//TODO: Add -> @Audited, extend BaseAuditableBranchedEntity
public class BranchWorkbench extends BaseAuditableTenantedEntity implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long rid;

	@Column(name = "branch_id")
	private Long branchId;

	private String code;

	private String name;

	public BranchWorkbench() {
	}

	@Override
	public Long getRid() {
		return this.rid;
	}

	public void setRid(Long rid) {
		this.rid = rid;
	}

	public Long getBranchId() {
		return this.branchId;
	}

	public void setBranchId(Long branchId) {
		this.branchId = branchId;
	}

	public String getCode() {
		return this.code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

}
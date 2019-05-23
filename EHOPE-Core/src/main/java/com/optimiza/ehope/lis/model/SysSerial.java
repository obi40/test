package com.optimiza.ehope.lis.model;

import java.io.Serializable;

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
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.envers.Audited;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.optimiza.core.admin.model.SecTenant;
import com.optimiza.core.base.entity.BaseAuditableTenantedEntity;
import com.optimiza.core.common.util.BooleanIntegerConverter;
import com.optimiza.ehope.lis.lkp.model.LkpSerialFormat;
import com.optimiza.ehope.lis.lkp.model.LkpSerialType;

/**
 * SysSerial.java
 * 
 * @author Murad Poladian <mpoladian@optimizasolutions.com>
 * @since Jan/15/2018
 **/
@Entity
@Table(name = "sys_serial")
@Audited
public class SysSerial extends BaseAuditableTenantedEntity implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Basic(optional = false)
	@Column(name = "rid")
	private Long rid;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "lab_branch_id")
	private LabBranch labBranch;

	@NotNull
	@Min(value = 0)
	@Column(name = "current_value")
	private Long currentValue;

	@Min(value = 1)
	@Column(name = "filler")
	private Long filler;

	@Size(min = 0, max = 1)
	@Column(name = "delimiter")
	private String delimiter;

	@Column(name = "last_serial")
	private String lastSerial;

	@NotNull
	@Column(name = "is_branch_level")
	@Convert(converter = BooleanIntegerConverter.class)
	private Boolean isBranchLevel;

	@NotNull
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "serial_type_id")
	private LkpSerialType serialType;

	@NotNull
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "serial_format_id")
	private LkpSerialFormat serialFormat;

	@Transient
	@JsonProperty
	private SecTenant tenant;

	public SysSerial() {
	}

	public LabBranch getLabBranch() {
		return labBranch;
	}

	public void setLabBranch(LabBranch labBranch) {
		this.labBranch = labBranch;
	}

	public Boolean getIsBranchLevel() {
		return isBranchLevel;
	}

	public void setIsBranchLevel(Boolean isBranchLevel) {
		this.isBranchLevel = isBranchLevel;
	}

	public String getLastSerial() {
		return lastSerial;
	}

	public void setLastSerial(String lastSerial) {
		this.lastSerial = lastSerial;
	}

	@Override
	public Long getRid() {
		return this.rid;
	}

	public void setRid(Long rid) {
		this.rid = rid;
	}

	public Long getFiller() {
		return filler;
	}

	public void setFiller(Long filler) {
		this.filler = filler;
	}

	public SecTenant getTenant() {
		return tenant;
	}

	public void setTenant(SecTenant tenant) {
		this.tenant = tenant;
	}

	public LkpSerialFormat getSerialFormat() {
		return serialFormat;
	}

	public void setSerialFormat(LkpSerialFormat serialFormat) {
		this.serialFormat = serialFormat;
	}

	public LkpSerialType getSerialType() {
		return serialType;
	}

	public void setSerialType(LkpSerialType serialType) {
		this.serialType = serialType;
	}

	public Long getCurrentValue() {
		return this.currentValue;
	}

	public void setCurrentValue(Long currentValue) {
		this.currentValue = currentValue;
	}

	public String getDelimiter() {
		return this.delimiter;
	}

	public void setDelimiter(String delimiter) {
		this.delimiter = delimiter;
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
		SysSerial other = (SysSerial) obj;
		if (rid == null) {
			if (other.rid != null)
				return false;
		} else if (!rid.equals(other.rid))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "SysSerial [rid=" + rid + "]";
	}

}
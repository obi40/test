package com.optimiza.core.lkp.model;

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
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.hibernate.envers.Audited;

import com.optimiza.core.base.entity.BaseAuditableTenantedEntity;
import com.optimiza.core.common.util.BooleanIntegerConverter;

/**
 * ComTenantLanguage.java
 * 
 * @author Abdullah Imran <aImran@optimizasolutions.com>
 * @since Nov/07/2017
 **/
@Entity
@Table(name = "com_tenant_languages")
@Audited
public class ComTenantLanguage extends BaseAuditableTenantedEntity implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Basic(optional = false)
	@Column(name = "rid")
	private Long rid;

	@NotNull
	@Column(name = "is_primary")
	@Convert(converter = BooleanIntegerConverter.class)
	private Boolean isPrimary;

	@NotNull
	@Column(name = "is_name_primary")
	@Convert(converter = BooleanIntegerConverter.class)
	private Boolean isNamePrimary;

	@NotNull
	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "language_id")
	private ComLanguage comLanguage;

	public ComTenantLanguage() {
	}

	public void setRid(Long rid) {
		this.rid = rid;
	}

	@Override
	public Long getRid() {
		return this.rid;
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
		ComTenantLanguage other = (ComTenantLanguage) obj;
		if (rid == null) {
			if (other.rid != null)
				return false;
		} else if (!rid.equals(other.rid))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "ComTenantLanguage [rid=" + rid + "]";
	}

	public Boolean getIsNamePrimary() {
		return isNamePrimary;
	}

	public void setIsNamePrimary(Boolean isNamePrimary) {
		this.isNamePrimary = isNamePrimary;
	}

	public Boolean getIsPrimary() {
		return isPrimary;
	}

	public void setIsPrimary(Boolean isPrimary) {
		this.isPrimary = isPrimary;
	}

	public ComLanguage getComLanguage() {
		return comLanguage;
	}

	public void setComLanguage(ComLanguage comLanguage) {
		this.comLanguage = comLanguage;
	}

}
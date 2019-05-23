package com.optimiza.core.admin.model;

import java.io.Serializable;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.hibernate.envers.Audited;

import com.optimiza.core.base.entity.BaseAuditableTenantedEntity;

/**
 * SecGroupRole.java
 * 
 * @author Abdullah Imran <aImran@optimizasolutions.com>
 * @since Sep/27/2017
 **/
@Entity
@Table(name = "sec_group_roles")
@Audited
public class SecGroupRole extends BaseAuditableTenantedEntity implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Basic(optional = false)
	@Column(name = "rid")
	private Long rid;

	//bi-directional many-to-one association to SecGroup
	@NotNull
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "group_id")
	private SecGroup secGroup;

	//bi-directional many-to-one association to SecRole
	@NotNull
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "role_id")
	private SecRole secRole;

	public SecGroupRole() {
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
		SecGroupRole other = (SecGroupRole) obj;
		if (rid == null) {
			if (other.rid != null)
				return false;
		} else if (!rid.equals(other.rid))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "SecGroupRole [rid=" + rid + "]";
	}

	@Override
	public Long getRid() {
		return this.rid;
	}

	public SecGroup getSecGroup() {
		return secGroup;
	}

	public void setSecGroup(SecGroup secGroup) {
		this.secGroup = secGroup;
	}

	public SecRole getSecRole() {
		return secRole;
	}

	public void setSecRole(SecRole secRole) {
		this.secRole = secRole;
	}

}
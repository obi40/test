package com.optimiza.core.admin.model;

import java.io.Serializable;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.envers.Audited;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.optimiza.core.base.entity.BaseAuditableTenantedEntity;
import com.optimiza.core.common.data.model.TransField;
import com.optimiza.core.common.data.model.converter.TransFieldAttConverter;

/**
 * SecGroup.java
 * 
 * @author Abdullah Imran <aImran@optimizasolutions.com>
 * @since Sep/27/2017
 **/
@Entity
@Table(name = "sec_groups")
@Audited
public class SecGroup extends BaseAuditableTenantedEntity implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Basic(optional = false)
	@Column(name = "rid")
	private Long rid;

	@NotNull
	@Size(min = 1, max = 4000)
	@Convert(converter = TransFieldAttConverter.class)
	private TransField name;

	//bi-directional many-to-one association to SecGroupRole
	@JsonIgnore
	@OneToMany(mappedBy = "secGroup", fetch = FetchType.LAZY)
	private List<SecGroupRole> secGroupRoles;

	//bi-directional many-to-one association to SecGroupUser
	@JsonIgnore
	@OneToMany(mappedBy = "secGroup", fetch = FetchType.LAZY)
	private List<SecGroupUser> secGroupUsers;

	@Transient
	@JsonProperty
	private Set<SecRole> groupRoles = new HashSet<>();// the assigned roles for this group,used when fetching groups with its roles

	public SecGroup() {
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
		SecGroup other = (SecGroup) obj;
		if (rid == null) {
			if (other.rid != null)
				return false;
		} else if (!rid.equals(other.rid))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "SecGroup [rid=" + rid + "]";
	}

	public TransField getName() {
		return name;
	}

	public void setName(TransField name) {
		this.name = name;
	}

	public List<SecGroupRole> getSecGroupRoles() {
		return this.secGroupRoles;
	}

	public void setSecGroupRoles(List<SecGroupRole> secGroupRoles) {
		this.secGroupRoles = secGroupRoles;
	}

	public List<SecGroupUser> getSecGroupUsers() {
		return this.secGroupUsers;
	}

	public void setSecGroupUsers(List<SecGroupUser> secGroupUsers) {
		this.secGroupUsers = secGroupUsers;
	}

	public Set<SecRole> getGroupRoles() {
		return groupRoles;
	}

	public void setGroupRoles(Set<SecRole> groupRoles) {
		this.groupRoles = groupRoles;
	}

}
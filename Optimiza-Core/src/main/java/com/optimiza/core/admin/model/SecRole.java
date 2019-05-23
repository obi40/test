package com.optimiza.core.admin.model;

import java.io.Serializable;
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
 * SecRole.java
 * 
 * @author Abdullah Imran <aImran@optimizasolutions.com>
 * @since Sep/27/2017
 **/
@Entity
@Table(name = "sec_roles")
@Audited
public class SecRole extends BaseAuditableTenantedEntity implements Serializable {

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
	@OneToMany(mappedBy = "secRole", fetch = FetchType.LAZY)
	private List<SecGroupRole> secGroupRoles;

	//bi-directional many-to-one association to SecRoleRight
	@JsonIgnore
	@OneToMany(mappedBy = "secRole", fetch = FetchType.LAZY)
	private Set<SecRoleRight> secRoleRights;

	//bi-directional many-to-one association to SecUserRole
	@JsonIgnore
	@OneToMany(mappedBy = "secRole", fetch = FetchType.LAZY)
	private List<SecUserRole> secUserRoles;

	//used when fetching a user with roles
	@Transient
	@JsonProperty
	private Set<SecRight> roleRights;

	//used when fetching a user with groups
	@Transient
	@JsonProperty
	private Set<SecGroup> roleGroups;

	public SecRole() {
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
		SecRole other = (SecRole) obj;
		if (rid == null) {
			if (other.rid != null)
				return false;
		} else if (!rid.equals(other.rid))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "SecRole [rid=" + rid + "]";
	}

	@Override
	public Long getRid() {
		return this.rid;
	}

	public List<SecGroupRole> getSecGroupRoles() {
		return this.secGroupRoles;
	}

	public void setSecGroupRoles(List<SecGroupRole> secGroupRoles) {
		this.secGroupRoles = secGroupRoles;
	}

	public SecGroupRole addSecGroupRole(SecGroupRole secGroupRole) {
		getSecGroupRoles().add(secGroupRole);
		secGroupRole.setSecRole(this);

		return secGroupRole;
	}

	public SecGroupRole removeSecGroupRole(SecGroupRole secGroupRole) {
		getSecGroupRoles().remove(secGroupRole);
		secGroupRole.setSecRole(null);

		return secGroupRole;
	}

	public Set<SecRoleRight> getSecRoleRights() {
		return this.secRoleRights;
	}

	public void setSecRoleRights(Set<SecRoleRight> secRoleRights) {
		this.secRoleRights = secRoleRights;
	}

	public SecRoleRight addSecRoleRight(SecRoleRight secRoleRight) {
		getSecRoleRights().add(secRoleRight);
		secRoleRight.setSecRole(this);

		return secRoleRight;
	}

	public SecRoleRight removeSecRoleRight(SecRoleRight secRoleRight) {
		getSecRoleRights().remove(secRoleRight);
		secRoleRight.setSecRole(null);

		return secRoleRight;
	}

	public List<SecUserRole> getSecUserRoles() {
		return this.secUserRoles;
	}

	public void setSecUserRoles(List<SecUserRole> secUserRoles) {
		this.secUserRoles = secUserRoles;
	}

	public SecUserRole addSecUserRole(SecUserRole secUserRole) {
		getSecUserRoles().add(secUserRole);
		secUserRole.setSecRole(this);

		return secUserRole;
	}

	public SecUserRole removeSecUserRole(SecUserRole secUserRole) {
		getSecUserRoles().remove(secUserRole);
		secUserRole.setSecRole(null);

		return secUserRole;
	}

	public TransField getName() {
		return name;
	}

	public void setName(TransField name) {
		this.name = name;
	}

	public Set<SecRight> getRoleRights() {
		return roleRights;
	}

	public void setRoleRights(Set<SecRight> roleRights) {
		this.roleRights = roleRights;
	}

	public Set<SecGroup> getRoleGroups() {
		return roleGroups;
	}

	public void setRoleGroups(Set<SecGroup> roleGroups) {
		this.roleGroups = roleGroups;
	}

	public void setRid(Long rid) {
		this.rid = rid;
	}

}
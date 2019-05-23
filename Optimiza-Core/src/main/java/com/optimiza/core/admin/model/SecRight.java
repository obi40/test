package com.optimiza.core.admin.model;

import java.io.Serializable;
import java.util.List;

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
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.envers.Audited;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.optimiza.core.base.entity.BaseAuditableEntity;
import com.optimiza.core.common.data.model.TransField;
import com.optimiza.core.common.data.model.converter.TransFieldAttConverter;
import com.optimiza.core.system.model.SysPage;

/**
 * SecRight.java
 * 
 * @author Abdullah Imran <aImran@optimizasolutions.com>
 * @since Sep/27/2017
 **/
@Entity
@Table(name = "sec_rights")
@Audited
public class SecRight extends BaseAuditableEntity implements Serializable {

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

	@Column(name = "description")
	@Size(min = 1, max = 4000)
	@Convert(converter = TransFieldAttConverter.class)
	private TransField description;

	@NotNull
	@Size(min = 1, max = 255)
	@Column(name = "code", unique = true)
	private String code;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "page_id")
	private SysPage sysPage;

	//bi-directional many-to-one association to SecRoleRight
	@OneToMany(mappedBy = "secRight", fetch = FetchType.LAZY)
	@JsonIgnore
	private List<SecRoleRight> secRoleRights;

	public SecRight() {
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
		SecRight other = (SecRight) obj;
		if (rid == null) {
			if (other.rid != null)
				return false;
		} else if (!rid.equals(other.rid))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "SecRight [rid=" + rid + "]";
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public List<SecRoleRight> getSecRoleRights() {
		return this.secRoleRights;
	}

	public void setSecRoleRights(List<SecRoleRight> secRoleRights) {
		this.secRoleRights = secRoleRights;
	}

	public SecRoleRight addSecRoleRight(SecRoleRight secRoleRight) {
		getSecRoleRights().add(secRoleRight);
		secRoleRight.setSecRight(this);

		return secRoleRight;
	}

	public SecRoleRight removeSecRoleRight(SecRoleRight secRoleRight) {
		getSecRoleRights().remove(secRoleRight);
		secRoleRight.setSecRight(null);

		return secRoleRight;
	}

	@Override
	public Long getRid() {
		return this.rid;
	}

	public TransField getName() {
		return name;
	}

	public void setName(TransField name) {
		this.name = name;
	}

	public TransField getDescription() {
		return description;
	}

	public void setDescription(TransField description) {
		this.description = description;
	}

	public SysPage getSysPage() {
		return sysPage;
	}

	public void setSysPage(SysPage sysPage) {
		this.sysPage = sysPage;
	}

}
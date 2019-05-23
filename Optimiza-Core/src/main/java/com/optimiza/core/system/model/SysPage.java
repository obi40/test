package com.optimiza.core.system.model;

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

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.optimiza.core.admin.model.SecRight;
import com.optimiza.core.base.entity.BaseAuditableEntity;
import com.optimiza.core.common.data.model.TransField;
import com.optimiza.core.common.data.model.converter.TransFieldAttConverter;

/**
 * SysPage.java
 * 
 * @author Abdullah Imran <aImran@optimizasolutions.com>
 * @since Dec/05/2017
 **/
@Entity
@Table(name = "sys_page")
@Audited
public class SysPage extends BaseAuditableEntity implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Basic(optional = false)
	@Column(name = "rid")
	private Long rid;

	@Column(name = "description")
	@Size(min = 1, max = 4000)
	@Convert(converter = TransFieldAttConverter.class)
	private TransField description;

	@Column(name = "name")
	@NotNull
	@Size(min = 1, max = 4000)
	@Convert(converter = TransFieldAttConverter.class)
	private TransField name;

	@NotNull
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "module_id")
	private SysModule sysModule;

	//bi-directional many-to-one association to SecRoleRight
	@OneToMany(mappedBy = "sysPage", fetch = FetchType.LAZY)
	@JsonIgnoreProperties("sysPage")
	private List<SecRight> secRights;

	public SysPage() {
	}

	@Override
	public Long getRid() {
		return rid;
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
		SysPage other = (SysPage) obj;
		if (rid == null) {
			if (other.rid != null)
				return false;
		} else if (!rid.equals(other.rid))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "SysPage [rid=" + rid + "]";
	}

	public TransField getDescription() {
		return description;
	}

	public void setDescription(TransField description) {
		this.description = description;
	}

	public TransField getName() {
		return name;
	}

	public void setName(TransField name) {
		this.name = name;
	}

	public SysModule getSysModule() {
		return sysModule;
	}

	public void setSysModule(SysModule sysModule) {
		this.sysModule = sysModule;
	}

	public void setRid(Long rid) {
		this.rid = rid;
	}

	public List<SecRight> getSecRights() {
		return secRights;
	}

	public void setSecRights(List<SecRight> secRights) {
		this.secRights = secRights;
	}

}
package com.optimiza.ehope.lis.model;

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
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.envers.Audited;

import com.optimiza.core.base.entity.BaseAuditableTenantedEntity;
import com.optimiza.core.common.data.model.TransField;
import com.optimiza.core.common.data.model.converter.TransFieldAttConverter;

/**
 * InsProviderNetwork.java
 * 
 * @author Eshraq Albakri <ebakri@optimizasolutions.com>
 * @since Aug/7/2017
 */
@Entity
@Table(name = "ins_network")
@Audited
public class InsNetwork extends BaseAuditableTenantedEntity implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Basic(optional = false)
	@Column(name = "rid")
	private Long rid;

	@NotNull
	@Size(min = 0, max = 50)
	@Column(name = "code")
	private String code;

	@NotNull
	@Convert(converter = TransFieldAttConverter.class)
	@Size(min = 1, max = 4000)
	@Column(name = "name")
	private TransField name;

	//bi-directional many-to-one association to InsProvider
	@OneToMany(mappedBy = "insNetwork", fetch = FetchType.LAZY)
	private List<InsProvider> insProviders;

	public InsNetwork() {
	}

	@Override
	public Long getRid() {
		return this.rid;
	}

	public void setRid(Long rid) {
		this.rid = rid;
	}

	public String getCode() {
		return this.code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public TransField getName() {
		return this.name;
	}

	public void setName(TransField name) {
		this.name = name;
	}

	public List<InsProvider> getInsProviders() {
		return this.insProviders;
	}

	public void setInsProviders(List<InsProvider> insProviders) {
		this.insProviders = insProviders;
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
		InsNetwork other = (InsNetwork) obj;
		if (rid == null) {
			if (other.rid != null)
				return false;
		} else if (!rid.equals(other.rid))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "InsNetwork [rid=" + rid + "]";
	}

}
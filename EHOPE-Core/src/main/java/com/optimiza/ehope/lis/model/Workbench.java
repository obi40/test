package com.optimiza.ehope.lis.model;

import java.io.Serializable;
import java.util.Set;

import javax.persistence.Basic;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.hibernate.envers.Audited;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.optimiza.core.base.entity.BaseAuditableBranchedEntity;
import com.optimiza.core.common.data.model.TransField;
import com.optimiza.core.common.data.model.converter.TransFieldAttConverter;

/**
 * Workbench.java
 * 
 * @author Murad Poladian <mpoladian@optimizasolutions.com>
 * @since Sep/18/2018
 * 
 */
@Entity
@Table(name = "workbench")
@Audited
public class Workbench extends BaseAuditableBranchedEntity implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Basic(optional = false)
	private Long rid;

	@NotNull
	@Convert(converter = TransFieldAttConverter.class)
	private TransField name;

	@OneToMany(mappedBy = "workbench", fetch = FetchType.LAZY)
	@JsonIgnoreProperties(value = { "destinations" }, allowSetters = true)
	private Set<TestDestination> destinations;

	public Workbench() {
	}

	@Override
	public Long getRid() {
		return this.rid;
	}

	public void setRid(Long rid) {
		this.rid = rid;
	}

	public TransField getName() {
		return this.name;
	}

	public void setName(TransField name) {
		this.name = name;
	}

	public Set<TestDestination> getDestinations() {
		return destinations;
	}

	public void setDestinations(Set<TestDestination> destinations) {
		this.destinations = destinations;
	}
}
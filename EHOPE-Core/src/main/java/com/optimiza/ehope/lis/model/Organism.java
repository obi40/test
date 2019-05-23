package com.optimiza.ehope.lis.model;

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

import org.hibernate.envers.Audited;

import com.optimiza.core.base.entity.BaseAuditableTenantedEntity;
import com.optimiza.ehope.lis.lkp.model.LkpOrganismType;

/**
 * Organism.java
 * 
 * @author Murad Poladian <mpoladian@optimizasolutions.com>
 * @since Dec/3/2018
 * 
 */
@Entity
@Table(name = "organism")
@Audited
public class Organism extends BaseAuditableTenantedEntity implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Basic(optional = false)
	@Column(name = "rid")
	private Long rid;

	@Column(name = "code")
	private String code;

	@Column(name = "name")
	private String name;

	//bi-directional many-to-one association to LkpOrganismType
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "type_id")
	private LkpOrganismType type;

	public Organism() {

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

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public LkpOrganismType getType() {
		return type;
	}

	public void setType(LkpOrganismType type) {
		this.type = type;
	}

}
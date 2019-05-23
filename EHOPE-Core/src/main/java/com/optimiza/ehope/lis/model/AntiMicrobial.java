package com.optimiza.ehope.lis.model;

import java.io.Serializable;
import java.util.Set;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.envers.Audited;

import com.optimiza.core.base.entity.BaseAuditableTenantedEntity;

/**
 * AntiMicrobial.java
 * 
 * @author Murad Poladian <mpoladian@optimizasolutions.com>
 * @since Dec/3/2018
 * 
 */
@Entity
@Table(name = "anti_microbial")
@Audited
public class AntiMicrobial extends BaseAuditableTenantedEntity implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Basic(optional = false)
	@Column(name = "rid")
	private Long rid;

	@NotNull
	@Size(min = 1, max = 50)
	@Column(name = "code")
	private String code;

	@NotNull
	@Size(min = 1, max = 255)
	@Column(name = "name")
	private String name;

	//bi-directional many-to-one association to AntiMicrobialTypeMapping
	@OneToMany(mappedBy = "antiMicrobial")
	private Set<AntiMicrobialTypeMapping> antiMicrobialTypeMappings;

	public AntiMicrobial() {
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

	public Set<AntiMicrobialTypeMapping> getAntiMicrobialTypeMappings() {
		return this.antiMicrobialTypeMappings;
	}

	public void setAntiMicrobialTypeMappings(Set<AntiMicrobialTypeMapping> antiMicrobialTypeMappings) {
		this.antiMicrobialTypeMappings = antiMicrobialTypeMappings;
	}

}
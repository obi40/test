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
import com.optimiza.ehope.lis.lkp.model.LkpAntiMicrobialType;

/**
 * AntiMicrobialTypeMapping.java
 * 
 * @author Murad Poladian <mpoladian@optimizasolutions.com>
 * @since Dec/3/2018
 * 
 */
@Entity
@Table(name = "anti_microbial_type_mapping")
@Audited
public class AntiMicrobialTypeMapping extends BaseAuditableTenantedEntity implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Basic(optional = false)
	@Column(name = "rid")
	private Long rid;

	//bi-directional many-to-one association to LkpAntiMicrobialType
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "type_id")
	private LkpAntiMicrobialType type;

	//bi-directional many-to-one association to AntiMicrobial
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "anti_microbial_id")
	private AntiMicrobial antiMicrobial;

	public AntiMicrobialTypeMapping() {
	}

	@Override
	public Long getRid() {
		return this.rid;
	}

	public void setRid(Long rid) {
		this.rid = rid;
	}

	public LkpAntiMicrobialType getType() {
		return type;
	}

	public void setType(LkpAntiMicrobialType type) {
		this.type = type;
	}

	public AntiMicrobial getAntiMicrobial() {
		return this.antiMicrobial;
	}

	public void setAntiMicrobial(AntiMicrobial antiMicrobial) {
		this.antiMicrobial = antiMicrobial;
	}

}
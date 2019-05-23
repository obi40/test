package com.optimiza.ehope.lis.lkp.model;

import java.io.Serializable;

import javax.persistence.Basic;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.envers.Audited;

import com.optimiza.core.base.entity.BaseAuditableEntity;
import com.optimiza.core.common.data.model.TransField;
import com.optimiza.core.common.data.model.converter.TransFieldAttConverter;

/**
 * LkpOrganismDetection.java
 * 
 * @author Murad Poladian <mpoladian@optimizasolutions.com>
 * @since Dec/10/2018
 * 
 */
@Entity
@Table(name = "lkp_organism_detection")
@Audited
public class LkpOrganismDetection extends BaseAuditableEntity implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Basic(optional = false)
	private Long rid;

	@NotNull
	@Size(min = 1, max = 50)
	private String code;

	@Size(max = 4000)
	@Convert(converter = TransFieldAttConverter.class)
	private TransField description;

	@NotNull
	@Size(min = 1, max = 4000)
	@Convert(converter = TransFieldAttConverter.class)
	private TransField name;

	public LkpOrganismDetection() {
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

	public TransField getDescription() {
		return this.description;
	}

	public void setDescription(TransField description) {
		this.description = description;
	}

	public TransField getName() {
		return this.name;
	}

	public void setName(TransField name) {
		this.name = name;
	}

}
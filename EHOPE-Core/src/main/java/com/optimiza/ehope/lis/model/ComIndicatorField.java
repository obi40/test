package com.optimiza.ehope.lis.model;

import java.io.Serializable;

import javax.persistence.Basic;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.hibernate.envers.Audited;

import com.optimiza.core.base.entity.BaseAuditableTenantedEntity;
import com.optimiza.core.common.data.model.TransField;
import com.optimiza.core.common.data.model.converter.TransFieldAttConverter;
import com.optimiza.core.lkp.model.LkpFieldType;

/**
 * The persistent class for the com_indicator_fields database table.
 * 
 */
@Entity
@Table(name = "com_indicator_fields")
@Audited
public class ComIndicatorField extends BaseAuditableTenantedEntity implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Basic(optional = false)
	private Long rid;

	@Convert(converter = TransFieldAttConverter.class)
	private TransField description;

	@NotNull
	@JoinColumn(name = "field_type_id")
	@ManyToOne(fetch = FetchType.LAZY)
	private LkpFieldType fieldType;

	@NotNull
	@Convert(converter = TransFieldAttConverter.class)
	private TransField name;

	//bi-directional many-to-one association to ComIndicator
	@NotNull
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "indicator_id")
	private ComIndicator comIndicator;

	public ComIndicatorField() {
	}

	@Override
	public Long getRid() {
		return this.rid;
	}

	public void setRid(Long rid) {
		this.rid = rid;
	}

	public TransField getDescription() {
		return this.description;
	}

	public void setDescription(TransField description) {
		this.description = description;
	}

	public LkpFieldType getFieldType() {
		return this.fieldType;
	}

	public void setFieldType(LkpFieldType fieldType) {
		this.fieldType = fieldType;
	}

	public TransField getName() {
		return this.name;
	}

	public void setName(TransField name) {
		this.name = name;
	}

	public ComIndicator getComIndicator() {
		return this.comIndicator;
	}

	public void setComIndicator(ComIndicator comIndicator) {
		this.comIndicator = comIndicator;
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
		ComIndicatorField other = (ComIndicatorField) obj;
		if (rid == null) {
			if (other.rid != null)
				return false;
		} else if (!rid.equals(other.rid))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "ComIndicatorField [rid=" + rid + "]";
	}

}
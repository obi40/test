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
 * The persistent class for the lkp_sample_priority database table.
 * 
 * @author Eshraq Albakri <ebakri@optimizasolutions.com>
 * @since 15/06/2017 ---- dd/mm/yyyy
 */
@Entity
@Table(name = "lkp_sample_priority")
@Audited
public class LkpSamplePriority extends BaseAuditableEntity implements Serializable {

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

	public LkpSamplePriority() {
	}

	@Override
	public Long getRid() {
		return this.rid;
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
		LkpSamplePriority other = (LkpSamplePriority) obj;
		if (rid == null) {
			if (other.rid != null)
				return false;
		} else if (!rid.equals(other.rid))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "LkpSamplePriority [rid=" + rid + "]";
	}
}
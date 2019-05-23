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

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.optimiza.core.base.entity.BaseAuditableTenantedEntity;
import com.optimiza.core.common.data.model.TransField;
import com.optimiza.core.common.data.model.converter.TransFieldAttConverter;
import com.optimiza.core.common.util.BooleanIntegerConverter;

/**
 * The persistent class for the com_indicators database table.
 * 
 */
@Entity
@Table(name = "com_indicators")
@Audited
public class ComIndicator extends BaseAuditableTenantedEntity implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Basic(optional = false)
	private Long rid;

	@Size(max = 4000)
	@Convert(converter = TransFieldAttConverter.class)
	private TransField description;

	private byte[] icon;

	@Column(name = "is_expiry_entry")
	@Convert(converter = BooleanIntegerConverter.class)
	private Boolean isExpiryEntry;

	@NotNull
	@Convert(converter = TransFieldAttConverter.class)
	private TransField name;

	//bi-directional many-to-one association to ComIndicatorField
	@OneToMany(mappedBy = "comIndicator", fetch = FetchType.LAZY)
	@JsonIgnoreProperties("comIndicator")
	private List<ComIndicatorField> comIndicatorFields;

	//bi-directional many-to-one association to EmrPatientIndicator
	@OneToMany(mappedBy = "comIndicator", fetch = FetchType.LAZY)
	@JsonIgnoreProperties("emrPatientIndicators")
	private List<EmrPatientIndicator> emrPatientIndicators;

	public ComIndicator() {
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

	public byte[] getIcon() {
		return this.icon;
	}

	public void setIcon(byte[] icon) {
		this.icon = icon;
	}

	public Boolean getIsExpiryEntry() {
		return this.isExpiryEntry;
	}

	public void setIsExpiryEntry(Boolean isExpiryEntry) {
		this.isExpiryEntry = isExpiryEntry;
	}

	public TransField getName() {
		return this.name;
	}

	public void setName(TransField name) {
		this.name = name;
	}

	public List<ComIndicatorField> getComIndicatorFields() {
		return this.comIndicatorFields;
	}

	public void setComIndicatorFields(List<ComIndicatorField> comIndicatorFields) {
		this.comIndicatorFields = comIndicatorFields;
	}

	public List<EmrPatientIndicator> getEmrPatientIndicators() {
		return this.emrPatientIndicators;
	}

	public void setEmrPatientIndicators(List<EmrPatientIndicator> emrPatientIndicators) {
		this.emrPatientIndicators = emrPatientIndicators;
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
		ComIndicator other = (ComIndicator) obj;
		if (rid == null) {
			if (other.rid != null)
				return false;
		} else if (!rid.equals(other.rid))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "ComIndicator [rid=" + rid + "]";
	}

}
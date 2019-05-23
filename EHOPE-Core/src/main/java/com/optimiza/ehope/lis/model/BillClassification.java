package com.optimiza.ehope.lis.model;

import java.io.Serializable;
import java.util.List;

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
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;

import org.hibernate.envers.Audited;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.optimiza.core.base.entity.BaseAuditableTenantedEntity;
import com.optimiza.core.common.util.BooleanIntegerConverter;

/**
 * BillClassification.java
 * 
 * @author Murad Poladian <mpoladian@optimizasolutions.com>
 * @since Jan/21/2018
 * 
 */
@Entity
@Table(name = "bill_classification")
@Audited
public class BillClassification extends BaseAuditableTenantedEntity implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long rid;

	@NotNull
	private String code;

	@NotNull
	private String name;

	@NotNull
	@Column(name = "is_active")
	@Convert(converter = BooleanIntegerConverter.class)
	private Boolean isActive;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "section_id")
	private LabSection section;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "parent_id")
	private BillClassification parentClassification;

	@Transient
	@JsonProperty
	private Long parentClassificationId;

	//bi-directional many-to-one association to BillMasterItem
	@OneToMany(mappedBy = "billClassification", fetch = FetchType.LAZY)
	private List<BillMasterItem> billMasterItems;

	public BillClassification() {
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

	public Boolean getIsActive() {
		return isActive;
	}

	public void setIsActive(Boolean isActive) {
		this.isActive = isActive;
	}

	public LabSection getSection() {
		return section;
	}

	public void setSection(LabSection section) {
		this.section = section;
	}

	public BillClassification getParentClassification() {
		return this.parentClassification;
	}

	public void setParentClassification(BillClassification parentClassification) {
		this.parentClassification = parentClassification;
	}

	public Long getParentClassificationId() {
		setParentClassificationId(parentClassification == null ? null : parentClassification.getRid());
		return parentClassificationId;
	}

	public void setParentClassificationId(Long parentClassificationId) {
		this.parentClassificationId = parentClassificationId;
	}

	public List<BillMasterItem> getBillMasterItems() {
		return this.billMasterItems;
	}

	public void setBillMasterItems(List<BillMasterItem> billMasterItems) {
		this.billMasterItems = billMasterItems;
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
		BillClassification other = (BillClassification) obj;
		if (rid == null) {
			if (other.rid != null)
				return false;
		} else if (!rid.equals(other.rid))
			return false;
		return true;
	}

}
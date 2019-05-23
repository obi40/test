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
 * BillPriceList.java
 * 
 * @author Murad Poladian <mpoladian@optimizasolutions.com>
 * @since Jan/21/2018
 * 
 */
@Entity
@Table(name = "bill_price_list")
@Audited
public class BillPriceList extends BaseAuditableTenantedEntity implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long rid;

	@Column(name = "is_default")
	@Convert(converter = BooleanIntegerConverter.class)
	private Boolean isDefault;

	@NotNull
	@Column(name = "name")
	@Size(min = 1, max = 4000)
	@Convert(converter = TransFieldAttConverter.class)
	private TransField name;

	@OneToMany(mappedBy = "billPriceList", fetch = FetchType.LAZY)
	@JsonIgnoreProperties("billPriceList")
	private List<BillPricing> billPricings;

	@OneToMany(mappedBy = "billPriceList", fetch = FetchType.LAZY)
	@JsonIgnoreProperties(value = "billPriceList", allowSetters = true)
	private List<InsProviderPlan> insProviderPlanList;

	@OneToMany(mappedBy = "priceList", fetch = FetchType.LAZY)
	@JsonIgnoreProperties(value = "priceList", allowSetters = true)
	private List<InsProvider> insProviderList;

	public BillPriceList() {
	}

	@Override
	public Long getRid() {
		return this.rid;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		BillPriceList other = (BillPriceList) obj;
		if (rid == null) {
			if (other.rid != null)
				return false;
		} else if (!rid.equals(other.rid))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "BillPriceList [rid=" + rid + "]";
	}

	public void setRid(Long rid) {
		this.rid = rid;
	}

	public List<InsProvider> getInsProviderList() {
		return insProviderList;
	}

	public void setInsProviderList(List<InsProvider> insProviderList) {
		this.insProviderList = insProviderList;
	}

	public Boolean getIsDefault() {
		return isDefault;
	}

	public void setIsDefault(Boolean isDefault) {
		this.isDefault = isDefault;
	}

	public TransField getName() {
		return name;
	}

	public void setName(TransField name) {
		this.name = name;
	}

	public List<BillPricing> getBillPricings() {
		return this.billPricings;
	}

	public void setBillPricings(List<BillPricing> billPricings) {
		this.billPricings = billPricings;
	}

	public List<InsProviderPlan> getInsProviderPlanList() {
		return insProviderPlanList;
	}

	public void setInsProviderPlanList(List<InsProviderPlan> insProviderPlanList) {
		this.insProviderPlanList = insProviderPlanList;
	}

}
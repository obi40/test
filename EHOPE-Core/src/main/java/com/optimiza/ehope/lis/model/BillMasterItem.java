package com.optimiza.ehope.lis.model;

import java.io.Serializable;
import java.util.List;
import java.util.Set;

import javax.persistence.Basic;
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
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;

import org.hibernate.envers.Audited;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.optimiza.core.base.entity.BaseAuditableTenantedEntity;
import com.optimiza.core.common.util.BooleanIntegerConverter;
import com.optimiza.ehope.lis.lkp.model.LkpBillItemType;

/**
 * BillMasterItem.java
 * 
 * @author Murad Poladian <mpoladian@optimizasolutions.com>
 * @since Jan/21/2018
 * 
 */
@Entity
@Table(name = "bill_master_item")
@Audited
public class BillMasterItem extends BaseAuditableTenantedEntity implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Basic(optional = false)
	@Column(name = "rid")
	private Long rid;

	@NotNull
	private String code;

	@Column(name = "cpt_code")
	private String cptCode;

	//bi-directional many-to-one association to LkpBillItemType
	@NotNull
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "type_id")
	private LkpBillItemType type;

	@NotNull
	@Column(name = "is_active")
	@Convert(converter = BooleanIntegerConverter.class)
	private Boolean isActive;

	//bi-directional many-to-one association to BillClassification
	@NotNull
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "class_id")
	private BillClassification billClassification;

	//bi-directional many-to-one association to BillPricing
	@OneToMany(mappedBy = "billMasterItem", fetch = FetchType.LAZY)
	@JsonIgnoreProperties(value = { "billMasterItem" }, allowSetters = true)
	private Set<BillPricing> billPricings;

	//bi-directional many-to-one association to BillTestItem
	@OneToMany(mappedBy = "billMasterItem", fetch = FetchType.LAZY)
	@JsonIgnoreProperties(value = { "billMasterItem" }, allowSetters = true)
	private Set<BillTestItem> billTestItems;

	@Transient
	@JsonDeserialize
	@JsonIgnoreProperties("billMasterItem")
	private List<BillTestItem> billTestItemList;

	public BillMasterItem() {
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

	public String getCptCode() {
		return this.cptCode;
	}

	public void setCptCode(String cptCode) {
		this.cptCode = cptCode;
	}

	public LkpBillItemType getType() {
		return this.type;
	}

	public void setType(LkpBillItemType type) {
		this.type = type;
	}

	public Boolean getIsActive() {
		return isActive;
	}

	public void setIsActive(Boolean isActive) {
		this.isActive = isActive;
	}

	public BillClassification getBillClassification() {
		return this.billClassification;
	}

	public void setBillClassification(BillClassification billClassification) {
		this.billClassification = billClassification;
	}

	public Set<BillPricing> getBillPricings() {
		return this.billPricings;
	}

	public void setBillPricings(Set<BillPricing> billPricings) {
		this.billPricings = billPricings;
	}

	public Set<BillTestItem> getBillTestItems() {
		return this.billTestItems;
	}

	public void setBillTestItems(Set<BillTestItem> billTestItems) {
		this.billTestItems = billTestItems;
	}

	public List<BillTestItem> getBillTestItemList() {
		return billTestItemList;
	}

	public void setBillTestItemList(List<BillTestItem> billTestItemList) {
		this.billTestItemList = billTestItemList;
	}

}
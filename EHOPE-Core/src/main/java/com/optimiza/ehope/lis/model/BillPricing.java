package com.optimiza.ehope.lis.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

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
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.Digits;
import javax.validation.constraints.NotNull;

import org.hibernate.envers.Audited;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.optimiza.core.base.entity.BaseAuditableTenantedEntity;

/**
 * BillPricing.java
 * 
 * @author Murad Poladian <mpoladian@optimizasolutions.com>
 * @since Jan/21/2018
 * 
 */
@Entity
@Table(name = "bill_pricing")
@Audited
public class BillPricing extends BaseAuditableTenantedEntity implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Basic(optional = false)
	@Column(name = "rid")
	private Long rid;

	@Column(name = "end_date")
	@Temporal(TemporalType.TIMESTAMP)
	private Date endDate;

	@NotNull
	@Digits(integer = 15, fraction = 2)
	private BigDecimal price;

	@NotNull
	@Column(name = "start_date")
	@Temporal(TemporalType.TIMESTAMP)
	private Date startDate;

	//bi-directional many-to-one association to BillMasterItem
	@NotNull
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "master_item_id")
	@JsonIgnoreProperties(value = "billPricings", allowSetters = true)
	private BillMasterItem billMasterItem;

	//bi-directional many-to-one association to BillPriceList
	@NotNull
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "price_list_id")
	private BillPriceList billPriceList;

	public BillPricing() {
	}

	@Override
	public Long getRid() {
		return this.rid;
	}

	public void setRid(Long rid) {
		this.rid = rid;
	}

	public Date getEndDate() {
		return this.endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public BigDecimal getPrice() {
		return this.price;
	}

	public void setPrice(BigDecimal price) {
		this.price = price;
	}

	public Date getStartDate() {
		return this.startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public BillMasterItem getBillMasterItem() {
		return this.billMasterItem;
	}

	public void setBillMasterItem(BillMasterItem billMasterItem) {
		this.billMasterItem = billMasterItem;
	}

	public BillPriceList getBillPriceList() {
		return this.billPriceList;
	}

	public void setBillPriceList(BillPriceList billPriceList) {
		this.billPriceList = billPriceList;
	}

}
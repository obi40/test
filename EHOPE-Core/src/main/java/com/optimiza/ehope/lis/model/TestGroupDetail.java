package com.optimiza.ehope.lis.model;

import java.io.Serializable;
import java.math.BigDecimal;

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
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;

import org.hibernate.envers.Audited;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.optimiza.core.base.entity.BaseAuditableTenantedEntity;

/**
 * TestGroupDetail.java
 * 
 * @author Abdullah Imran <aImran@optimizasolutions.com>
 * @since Jan/24/2019
 **/
@Entity
@Table(name = "test_group_detail")
@Audited
public class TestGroupDetail extends BaseAuditableTenantedEntity implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Basic(optional = false)
	@Column(name = "rid")
	private Long rid;

	@NotNull
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "group_id")
	private TestGroup group;

	@NotNull
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "price_list_id")
	private BillPriceList priceList;

	@Transient
	@JsonProperty
	private BigDecimal totalPrice;

	@Transient
	@JsonProperty
	private BigDecimal groupPrice;

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
		TestGroupDetail other = (TestGroupDetail) obj;
		if (rid == null) {
			if (other.rid != null)
				return false;
		} else if (!rid.equals(other.rid))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "TestGroupDetail [rid=" + rid + "]";
	}

	public void setRid(Long rid) {
		this.rid = rid;
	}

	public BillPriceList getPriceList() {
		return priceList;
	}

	public void setPriceList(BillPriceList priceList) {
		this.priceList = priceList;
	}

	public TestGroup getGroup() {
		return group;
	}

	public void setGroup(TestGroup group) {
		this.group = group;
	}

	public BigDecimal getTotalPrice() {
		return totalPrice;
	}

	public void setTotalPrice(BigDecimal totalPrice) {
		this.totalPrice = totalPrice;
	}

	public BigDecimal getGroupPrice() {
		return groupPrice;
	}

	public void setGroupPrice(BigDecimal groupPrice) {
		this.groupPrice = groupPrice;
	}

}
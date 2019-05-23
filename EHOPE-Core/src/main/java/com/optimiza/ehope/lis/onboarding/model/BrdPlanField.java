package com.optimiza.ehope.lis.onboarding.model;

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
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.hibernate.envers.Audited;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.optimiza.core.base.entity.BaseAuditableEntity;
import com.optimiza.core.common.util.BooleanIntegerConverter;
import com.optimiza.ehope.lis.lkp.model.LkpPlanFieldType;

/**
 * PlanField.java
 * 
 * @author Abdullah Imran <aImran@optimizasolutions.com>
 * @since May/20/2018
 **/
@Entity
@Table(name = "brd_plan_field")
@Audited
public class BrdPlanField extends BaseAuditableEntity implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Basic(optional = false)
	@Column(name = "rid")
	private Long rid;

	@Column(name = "price")
	private Long price;

	@NotNull
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "plan_field_type_id")
	private LkpPlanFieldType lkpPlanFieldType;

	@Column(name = "isFixed")
	@NotNull
	@Convert(converter = BooleanIntegerConverter.class)
	private Boolean isFixed;

	@Column(name = "amount")
	private Long amount;

	@NotNull
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "plan_id")
	private BrdPlan plan;

	//bi-directional many-to-one association to TenantPlanDetail
	@OneToMany(mappedBy = "planField", fetch = FetchType.LAZY)
	@JsonIgnoreProperties(value = "planField", allowSetters = true)
	private List<BrdTenantPlanDetail> tenantPlanDetailList;

	public BrdPlanField() {
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
		BrdPlanField other = (BrdPlanField) obj;
		if (rid == null) {
			if (other.rid != null)
				return false;
		} else if (!rid.equals(other.rid))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "BrdPlanField [rid=" + rid + "]";
	}

	@Override
	public Long getRid() {
		return this.rid;
	}

	public void setRid(Long rid) {
		this.rid = rid;
	}

	public Boolean getIsFixed() {
		return isFixed;
	}

	public void setIsFixed(Boolean isFixed) {
		this.isFixed = isFixed;
	}

	public Long getAmount() {
		return amount;
	}

	public void setAmount(Long amount) {
		this.amount = amount;
	}

	public LkpPlanFieldType getLkpPlanFieldType() {
		return lkpPlanFieldType;
	}

	public void setLkpPlanFieldType(LkpPlanFieldType lkpPlanFieldType) {
		this.lkpPlanFieldType = lkpPlanFieldType;
	}

	public Long getPrice() {
		return this.price;
	}

	public void setPrice(Long price) {
		this.price = price;
	}

	public BrdPlan getPlan() {
		return this.plan;
	}

	public void setPlan(BrdPlan plan) {
		this.plan = plan;
	}

	public List<BrdTenantPlanDetail> getTenantPlanDetailList() {
		return tenantPlanDetailList;
	}

	public void setTenantPlanDetailList(List<BrdTenantPlanDetail> tenantPlanDetailList) {
		this.tenantPlanDetailList = tenantPlanDetailList;
	}

}
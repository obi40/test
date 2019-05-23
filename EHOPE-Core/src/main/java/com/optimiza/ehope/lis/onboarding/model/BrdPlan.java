package com.optimiza.ehope.lis.onboarding.model;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.hibernate.envers.Audited;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.optimiza.core.base.entity.BaseAuditableEntity;

/**
 * Plan.java
 * 
 * @author Abdullah Imran <aImran@optimizasolutions.com>
 * @since May/20/2018
 **/
@Entity
@Table(name = "brd_plan")
@Audited
public class BrdPlan extends BaseAuditableEntity implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Basic(optional = false)
	@Column(name = "rid")
	private Long rid;

	@NotNull
	@Column(name = "description")
	private String description;

	@NotNull
	@Column(name = "name")
	private String name;

	@Column(name = "price")
	private Long price;

	@OneToMany(mappedBy = "plan", fetch = FetchType.LAZY)
	@JsonIgnoreProperties(value = "plan", allowSetters = true)
	private List<BrdPlanField> planFieldList;

	@OneToMany(mappedBy = "plan", fetch = FetchType.LAZY)
	@JsonIgnoreProperties(value = "plan", allowSetters = true)
	private List<BrdTenantSubscription> tenantSubscriptionList;

	public BrdPlan() {
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
		BrdPlan other = (BrdPlan) obj;
		if (rid == null) {
			if (other.rid != null)
				return false;
		} else if (!rid.equals(other.rid))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Plan [rid=" + rid + "]";
	}

	@Override
	public Long getRid() {
		return this.rid;
	}

	public void setRid(Long rid) {
		this.rid = rid;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return this.description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Long getPrice() {
		return this.price;
	}

	public void setPrice(Long price) {
		this.price = price;
	}

	public List<BrdPlanField> getPlanFieldList() {
		return planFieldList;
	}

	public void setPlanFieldList(List<BrdPlanField> planFieldList) {
		this.planFieldList = planFieldList;
	}

	public List<BrdTenantSubscription> getTenantSubscriptionList() {
		return tenantSubscriptionList;
	}

	public void setTenantSubscriptionList(List<BrdTenantSubscription> tenantSubscriptionList) {
		this.tenantSubscriptionList = tenantSubscriptionList;
	}

}
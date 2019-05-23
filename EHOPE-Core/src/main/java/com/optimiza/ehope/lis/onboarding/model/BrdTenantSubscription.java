package com.optimiza.ehope.lis.onboarding.model;

import java.io.Serializable;
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
import javax.validation.constraints.NotNull;

import org.hibernate.envers.Audited;

import com.optimiza.core.admin.model.SecTenant;
import com.optimiza.core.base.entity.BaseAuditableEntity;

/**
 * TenantSubscription.java
 * 
 * @author Abdullah Imran <aImran@optimizasolutions.com>
 * @since May/20/2018
 **/
@Entity
@Table(name = "brd_tenant_subscription")
@Audited
public class BrdTenantSubscription extends BaseAuditableEntity implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Basic(optional = false)
	@Column(name = "rid")
	private Long rid;

	@Column(name = "expiry_date")
	@Temporal(TemporalType.TIMESTAMP)
	private Date expiryDate;

	//TODO change column name
	@NotNull
	@JoinColumn(name = "tenant_id")
	@ManyToOne(fetch = FetchType.LAZY)
	private SecTenant tenant;

	@NotNull
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "plan_id")
	private BrdPlan plan;

	public BrdTenantSubscription() {
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
		BrdTenantSubscription other = (BrdTenantSubscription) obj;
		if (rid == null) {
			if (other.rid != null)
				return false;
		} else if (!rid.equals(other.rid))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "TenantSubscription [rid=" + rid + "]";
	}

	@Override
	public Long getRid() {
		return this.rid;
	}

	public void setRid(Long rid) {
		this.rid = rid;
	}

	public Date getExpiryDate() {
		return expiryDate;
	}

	public void setExpiryDate(Date expiryDate) {
		this.expiryDate = expiryDate;
	}

	public SecTenant getTenant() {
		return tenant;
	}

	public void setTenant(SecTenant tenant) {
		this.tenant = tenant;
	}

	public BrdPlan getPlan() {
		return this.plan;
	}

	public void setPlan(BrdPlan plan) {
		this.plan = plan;
	}

}
package com.optimiza.ehope.lis.model;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.envers.Audited;

import com.optimiza.core.base.entity.BaseAuditableTenantedEntity;

/**
 * BillTestItem.java
 * 
 * @author Murad Poladian <mpoladian@optimizasolutions.com>
 * @since Jan/21/2018
 * 
 */
@Entity
@Table(name = "bill_test_item")
@Audited
public class BillTestItem extends BaseAuditableTenantedEntity implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long rid;

	//bi-directional many-to-one association to TestDefinition
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "test_id")
	private TestDefinition testDefinition;

	//bi-directional many-to-one association to BillMasterItem
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "master_item_id")
	private BillMasterItem billMasterItem;

	public BillTestItem() {
	}

	@Override
	public Long getRid() {
		return this.rid;
	}

	public void setRid(Long rid) {
		this.rid = rid;
	}

	public TestDefinition getTestDefinition() {
		return this.testDefinition;
	}

	public void setTestDefinition(TestDefinition testDefinition) {
		this.testDefinition = testDefinition;
	}

	public BillMasterItem getBillMasterItem() {
		return this.billMasterItem;
	}

	public void setBillMasterItem(BillMasterItem billMasterItem) {
		this.billMasterItem = billMasterItem;
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
		BillTestItem other = (BillTestItem) obj;
		if (rid == null) {
			if (other.rid != null)
				return false;
		} else if (!rid.equals(other.rid))
			return false;
		return true;
	}

}
package com.optimiza.ehope.lis.model;

import java.io.Serializable;
import java.math.BigDecimal;

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
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.validation.constraints.Digits;
import javax.validation.constraints.NotNull;

import org.hibernate.envers.Audited;
import org.hibernate.envers.RelationTargetAuditMode;

import com.optimiza.core.base.entity.BaseAuditableTenantedEntity;
import com.optimiza.core.common.util.BooleanIntegerConverter;
import com.optimiza.ehope.lis.lkp.model.LkpBalanceTransactionType;

/**
 * BillBalanceTransaction.java
 * 
 * @author Abdullah Imran <aImran@optimizasolutions.com>
 * @since Jul/09/2018
 **/
@Entity
@Table(name = "bill_balance_transaction")
//TODO: MUST REMOVE AUDIT EXCLUSION AFTER IMPORT PROCESS ENDS (FOR ALL ENTITIES)
@Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
public class BillBalanceTransaction extends BaseAuditableTenantedEntity implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "rid")
	@Basic(optional = false)
	private Long rid;

	@Column(name = "credit")
	@Digits(integer = 15, fraction = 3)
	private BigDecimal credit;

	@Column(name = "debit")
	@Digits(integer = 15, fraction = 3)
	private BigDecimal debit;

	@NotNull
	@Column(name = "is_active")
	@Convert(converter = BooleanIntegerConverter.class)
	private Boolean isActive;

	@NotNull
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "balance_transaction_type_id")
	private LkpBalanceTransactionType balanceTransactionType;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "branch_id")
	private LabBranch branch;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "patient_id")
	private EmrPatientInfo patientInfo;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "provider_id")
	private InsProvider provider;

	@NotNull
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "visit_id")
	private EmrVisit visit;

	public BillBalanceTransaction() {

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
		BillBalanceTransaction other = (BillBalanceTransaction) obj;
		if (rid == null) {
			if (other.rid != null)
				return false;
		} else if (!rid.equals(other.rid))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "BillBalanceTransaction [rid=" + rid + "]";
	}

	public Boolean getIsActive() {
		return isActive;
	}

	public void setIsActive(Boolean isActive) {
		this.isActive = isActive;
	}

	public EmrVisit getVisit() {
		return visit;
	}

	public void setVisit(EmrVisit visit) {
		this.visit = visit;
	}

	@Override
	public Long getRid() {
		return rid;
	}

	public InsProvider getProvider() {
		return provider;
	}

	public void setProvider(InsProvider provider) {
		this.provider = provider;
	}

	public LabBranch getBranch() {
		return branch;
	}

	public void setBranch(LabBranch branch) {
		this.branch = branch;
	}

	public EmrPatientInfo getPatientInfo() {
		return patientInfo;
	}

	public void setPatientInfo(EmrPatientInfo patientInfo) {
		this.patientInfo = patientInfo;
	}

	public BigDecimal getCredit() {
		return credit;
	}

	public void setCredit(BigDecimal credit) {
		this.credit = credit;
	}

	public BigDecimal getDebit() {
		return debit;
	}

	public void setDebit(BigDecimal debit) {
		this.debit = debit;
	}

	public LkpBalanceTransactionType getBalanceTransactionType() {
		return balanceTransactionType;
	}

	public void setBalanceTransactionType(LkpBalanceTransactionType balanceTransactionType) {
		this.balanceTransactionType = balanceTransactionType;
	}

	public void setRid(Long rid) {
		this.rid = rid;
	}

}
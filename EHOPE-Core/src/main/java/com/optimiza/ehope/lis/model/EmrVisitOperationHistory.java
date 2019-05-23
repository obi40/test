package com.optimiza.ehope.lis.model;

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
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;

import org.hibernate.envers.Audited;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.optimiza.core.admin.model.SecUser;
import com.optimiza.core.base.entity.BaseAuditableTenantedEntity;
import com.optimiza.ehope.lis.lkp.model.LkpOperationStatus;

/**
 * EmrVisitOperationHistory.java
 * 
 * @author Abdullah Imran <aImran@optimizasolutions.com>
 * @since Apr/29/2018
 **/
@Entity
@Table(name = "emr_visits_operation_history")
@Audited
public class EmrVisitOperationHistory extends BaseAuditableTenantedEntity implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Basic(optional = false)
	@Column(name = "rid")
	private Long rid;

	//changing this value name will affect the generic api the retrieve them
	@NotNull
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "visit_id")
	private EmrVisit emrVisit;

	@NotNull
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "old_operation_status_id")
	private LkpOperationStatus oldOperationStatus;

	@NotNull
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "new_operation_status_id")
	private LkpOperationStatus newOperationStatus;

	@NotNull
	@Column(name = "operation_date")
	@Temporal(TemporalType.TIMESTAMP)
	private Date operationDate;

	@NotNull
	@Column(name = "operationBy")
	private Long operationBy;

	@Column(name = "comment", length = 4000)
	private String comment;

	@Transient
	@JsonProperty
	private SecUser operationByUser;

	public EmrVisitOperationHistory() {
	}

	@Override
	public Long getRid() {
		return this.rid;
	}

	public void setRid(Long rid) {
		this.rid = rid;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public EmrVisit getEmrVisit() {
		return emrVisit;
	}

	public void setEmrVisit(EmrVisit emrVisit) {
		this.emrVisit = emrVisit;
	}

	public LkpOperationStatus getOldOperationStatus() {
		return oldOperationStatus;
	}

	public void setOldOperationStatus(LkpOperationStatus oldOperationStatus) {
		this.oldOperationStatus = oldOperationStatus;
	}

	public LkpOperationStatus getNewOperationStatus() {
		return newOperationStatus;
	}

	public void setNewOperationStatus(LkpOperationStatus newOperationStatus) {
		this.newOperationStatus = newOperationStatus;
	}

	public Date getOperationDate() {
		return operationDate;
	}

	public void setOperationDate(Date operationDate) {
		this.operationDate = operationDate;
	}

	public Long getOperationBy() {
		return operationBy;
	}

	public void setOperationBy(Long operationBy) {
		this.operationBy = operationBy;
	}

	public SecUser getOperationByUser() {
		return operationByUser;
	}

	public void setOperationByUser(SecUser operationByUser) {
		this.operationByUser = operationByUser;
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
		EmrVisitOperationHistory other = (EmrVisitOperationHistory) obj;
		if (rid == null) {
			if (other.rid != null)
				return false;
		} else if (!rid.equals(other.rid))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "EmrVisitOperationHistory [rid=" + rid + "]";
	}
}
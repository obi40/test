package com.optimiza.ehope.lis.model;

import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
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
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.envers.Audited;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.optimiza.core.base.entity.BaseAuditableBranchedEntity;
import com.optimiza.core.common.util.BooleanIntegerConverter;
import com.optimiza.ehope.lis.lkp.model.LkpActionCode;
import com.optimiza.ehope.lis.lkp.model.LkpContainerType;
import com.optimiza.ehope.lis.lkp.model.LkpOperationStatus;
import com.optimiza.ehope.lis.lkp.model.LkpSamplePriority;

/**
 * 
 * The persistent class for the lab_sample database table.
 * 
 * @author Eshraq Albakri <ebakri@optimizasolutions.com>
 * @since 14/6/2017 --- dd/mm/yyyy
 * 
 */
@Entity
@Table(name = "lab_sample")
@Audited
public class LabSample extends BaseAuditableBranchedEntity implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Basic(optional = false)
	@Column(name = "rid")
	private Long rid;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "action_code_id")
	private LkpActionCode lkpActionCode;

	@Size(min = 1, max = 255)
	@Column(name = "barcode")
	private String barcode;

	@NotNull
	@Column(name = "is_dummy")
	@Convert(converter = BooleanIntegerConverter.class)
	private Boolean isDummy;

	@Column(name = "collected_by")
	@Size(min = 1, max = 255)
	private String collectedBy;

	@Column(name = "collected_date")
	private Date collectedDate;

	@Column(name = "collection_volume")
	private Long collectionVolume;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "container_id")
	private LkpContainerType lkpContainerType;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "priority_id")
	private LkpSamplePriority lkpSamplePriority;

	@Column(name = "recived_date")
	private Date recivedDate;

	@Column(name = "requested_date")
	private Date requestedDate;

	@Column(name = "sample_no")
	@NotNull
	private String sampleNo;

	@Column(name = "transferred_by")
	@Size(min = 1, max = 255)
	private String transferredBy;

	@Column(name = "transferred_date")
	private Date transferredDate;

	@Column(name = "work_sheet_id")
	private Long workSheetId;

	@NotNull
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "lab_visit_id")
	private EmrVisit emrVisit;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "operation_status_id")
	private LkpOperationStatus lkpOperationStatus;

	//bi-directional many-to-one association to LabTestActual
	@OneToMany(mappedBy = "labSample", fetch = FetchType.LAZY)
	@JsonIgnoreProperties(value = { "labSample" }, allowSetters = true)
	private Set<LabTestActual> labTestActualSet;

	public LabSample() {
	}

	@Override
	public Long getRid() {
		return this.rid;
	}

	public void setRid(Long rid) {
		this.rid = rid;
	}

	public Boolean getIsDummy() {
		return isDummy;
	}

	public void setIsDummy(Boolean isDummy) {
		this.isDummy = isDummy;
	}

	public LkpOperationStatus getLkpOperationStatus() {
		return lkpOperationStatus;
	}

	public void setLkpOperationStatus(LkpOperationStatus lkpOperationStatus) {
		this.lkpOperationStatus = lkpOperationStatus;
	}

	public LkpActionCode getLkpActionCode() {
		return this.lkpActionCode;
	}

	public void setLkpActionCode(LkpActionCode lkpActionCode) {
		this.lkpActionCode = lkpActionCode;
	}

	public String getBarcode() {
		return this.barcode;
	}

	public void setBarcode(String barcode) {
		this.barcode = barcode;
	}

	public String getCollectedBy() {
		return this.collectedBy;
	}

	public void setCollectedBy(String collectedBy) {
		this.collectedBy = collectedBy;
	}

	public Date getCollectedDate() {
		return this.collectedDate;
	}

	public void setCollectedDate(Date collectedDate) {
		this.collectedDate = collectedDate;
	}

	public Long getCollectionVolume() {
		return this.collectionVolume;
	}

	public void setCollectionVolume(Long collectionVolume) {
		this.collectionVolume = collectionVolume;
	}

	public LkpContainerType getLkpContainerType() {
		return lkpContainerType;
	}

	public void setLkpContainerType(LkpContainerType lkpContainerType) {
		this.lkpContainerType = lkpContainerType;
	}

	public LkpSamplePriority getLkpSamplePriority() {
		return this.lkpSamplePriority;
	}

	public void setLkpSamplePriority(LkpSamplePriority lkpSamplePriority) {
		this.lkpSamplePriority = lkpSamplePriority;
	}

	public Date getRecivedDate() {
		return this.recivedDate;
	}

	public void setRecivedDate(Date recivedDate) {
		this.recivedDate = recivedDate;
	}

	public Date getRequestedDate() {
		return this.requestedDate;
	}

	public void setRequestedDate(Date requestedDate) {
		this.requestedDate = requestedDate;
	}

	public String getSampleNo() {
		return this.sampleNo;
	}

	public void setSampleNo(String sampleNo) {
		this.sampleNo = sampleNo;
	}

	public String getTransferredBy() {
		return this.transferredBy;
	}

	public void setTransferredBy(String transferredBy) {
		this.transferredBy = transferredBy;
	}

	public Date getTransferredDate() {
		return this.transferredDate;
	}

	public void setTransferredDate(Date transferredDate) {
		this.transferredDate = transferredDate;
	}

	public Long getWorkSheetId() {
		return this.workSheetId;
	}

	public void setWorkSheetId(Long workSheetId) {
		this.workSheetId = workSheetId;
	}

	public EmrVisit getEmrVisit() {
		return this.emrVisit;
	}

	public void setEmrVisit(EmrVisit emrVisit) {
		this.emrVisit = emrVisit;
	}

	public void addToLabTestActualSet(LabTestActual labTestActual) {
		if (labTestActualSet == null) {
			labTestActualSet = new HashSet<>();
		}
		labTestActualSet.add(labTestActual);
	}

	public Set<LabTestActual> getLabTestActualSet() {
		return labTestActualSet;
	}

	public void setLabTestActualSet(Set<LabTestActual> labTestActualSet) {
		this.labTestActualSet = labTestActualSet;
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
		LabSample other = (LabSample) obj;
		if (rid == null) {
			if (other.rid != null)
				return false;
		} else if (!rid.equals(other.rid))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "LabSample [rid=" + rid + "]";
	}
}
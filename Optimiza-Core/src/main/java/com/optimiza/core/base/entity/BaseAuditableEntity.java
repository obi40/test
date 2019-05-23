package com.optimiza.core.base.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.persistence.PrePersist;
import javax.persistence.PreRemove;
import javax.persistence.PreUpdate;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Version;
import javax.validation.constraints.NotNull;

import org.hibernate.envers.Audited;

import com.optimiza.core.common.util.SecurityUtil;

/**
 * BaseAuditableTenantedEntity.java Super class for Audited entities, used to add auditing columns to child entity and set attribute values when needed, in addition this class will create an auditing
 * table
 * automatically
 *
 **/

@MappedSuperclass
@Audited
public abstract class BaseAuditableEntity extends BaseEntity {

	private static final long serialVersionUID = 1L;

	@NotNull
	@Column(name = "VERSION")
	@Version
	private Long version;

	@NotNull
	@Column(name = "CREATED_BY", updatable = false)
	private Long createdBy;

	@NotNull
	@Column(name = "CREATION_DATE", updatable = false)
	@Temporal(TemporalType.TIMESTAMP)
	private Date creationDate;

	@Column(name = "UPDATED_BY")
	private Long updatedBy;

	@Column(name = "UPDATE_DATE")
	@Temporal(TemporalType.TIMESTAMP)
	private Date updateDate;

	@PrePersist
	public void onPrePersist() {
		populateAudit();
	}

	@PreUpdate
	public void onPreUpdate() {
		populateAudit();
	}

	@PreRemove
	public void onPreRemove() {
		// call a procedure to log the delete operation
		// FIXME, Check commented code

		// UserData userData = (UserData) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		// List<Parameter> parameters = new ArrayList<Parameter>();
		// parameters.add(new Parameter("p_jpa_entity_name", getClass().getSimpleName(), String.class));
		// parameters.add(new Parameter("p_rid", getRid(), Long.class));
		// parameters.add(new Parameter("p_user_rid", userData.getUser().getRid(), Long.class));
		//
		// FunctionHandlerService functionHandlerService = (FunctionHandlerService) SpringUtil.getBean(FunctionHandlerService.class.getSimpleName());
		// functionHandlerService.callFunction("BASE_PKG", "log_record", parameters, Integer.class);

		// UserData userData = (UserData) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		// List<Parameter> parameters = new ArrayList<Parameter>();
		// parameters.add(new Parameter("p_attribute_name", "p_user_rid", String.class));
		// parameters.add(new Parameter("p_attribute_value", userData.getUser().getRid(), Long.class));
		//
		// ProcedureHandlerService procedureHandlerService = (ProcedureHandlerService) SpringUtil.getBean(ProcedureHandlerService.class.getSimpleName());
		// procedureHandlerService.callProcedure("CTX_PKG", "set_context_attribute", parameters);

	}

	/**
	 * populate timestamp on presist and update
	 */
	protected void populateAudit() {

		if (getCreationDate() == null) {
			setCreationDate(new Date());

			if (getCreatedBy() == null) {
				setCreatedBy(SecurityUtil.getCurrentUser().getRid());
			}
		} else {
			setUpdateDate(new Date());
			if (getUpdatedBy() == null && SecurityUtil.getCurrentUser() != null) {
				setUpdatedBy(SecurityUtil.getCurrentUser().getRid());
			}
		}
	}

	/**
	 * @return The current version number
	 */
	public Long getVersion() {
		return version;
	}

	/**
	 * @return ID of the user who created the record
	 */
	public final Long getCreatedBy() {
		return createdBy;
	}

	/**
	 * @param Set the ID of the user who created the record
	 */
	public final void setCreatedBy(Long createdBy) {
		this.createdBy = createdBy;
	}

	/**
	 * @return Date the record was created
	 */
	public final Date getCreationDate() {
		return creationDate;
	}

	/**
	 * @param Set the date the record was created
	 */
	public final void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}

	/**
	 * @return Get ID of user who last update the record
	 */
	public final Long getUpdatedBy() {
		return updatedBy;
	}

	/**
	 * @param updatedBy Get ID of user who last update the record
	 */
	public final void setUpdatedBy(Long updatedBy) {
		this.updatedBy = updatedBy;
	}

	/**
	 * @return Get last update date
	 */
	public final Date getUpdateDate() {
		return updateDate;
	}

	/**
	 * @param updatedDate Set last update date
	 */
	public final void setUpdateDate(Date updateDate) {
		this.updateDate = updateDate;
	}

}

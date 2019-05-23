package com.optimiza.core.base.entity;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.persistence.PrePersist;
import javax.persistence.PreRemove;
import javax.persistence.PreUpdate;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.Filter;
import org.hibernate.annotations.FilterDef;
import org.hibernate.annotations.ParamDef;
import org.hibernate.envers.Audited;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.optimiza.core.common.util.SecurityUtil;

/**
 * BaseAuditableBranchedEntity.java Super class for Audited entities, used to add auditing columns to child entity and set attribute values when needed, in addition this class will create an auditing
 * table
 * automatically
 *
 **/

@MappedSuperclass
@Audited
@FilterDef(name = BaseAuditableBranchedEntity.BRANCH_FILTER, parameters = { @ParamDef(name = "branchId", type = "long") })
@Filter(name = BaseAuditableBranchedEntity.BRANCH_FILTER, condition = "branch_id = :branchId")
public abstract class BaseAuditableBranchedEntity extends BaseAuditableTenantedEntity {

	private static final long serialVersionUID = 1L;

	@JsonIgnore
	public static final String BRANCH_FILTER = "branchFilter";

	@Column(name = "BRANCH_ID", updatable = false)
	@NotNull
	private Long branchId;

	@Override
	@PrePersist
	public void onPrePersist() {
		populateAudit();
	}

	@Override
	@PreUpdate
	public void onPreUpdate() {
		populateAudit();
	}

	@Override
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
	@Override
	protected void populateAudit() {
		super.populateAudit();
		if (getBranchId() == null && getRid() == null) {
			setBranchId(SecurityUtil.getCurrentUser().getBranchId());
		}

	}

	/**
	 * @return The branch ID of the entity
	 */
	public Long getBranchId() {
		return branchId;
	}

	/**
	 * @param branchId Sets the branch ID
	 */
	public void setBranchId(Long branchId) {
		this.branchId = branchId;
	}

}

package com.optimiza.core.base.entity;

import javax.persistence.MappedSuperclass;
import javax.persistence.PrePersist;
import javax.persistence.PreRemove;
import javax.persistence.PreUpdate;

@MappedSuperclass
public abstract class BaseView extends BaseEntity {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@PrePersist
	private void prePersist() throws Exception {
		throw new Exception("Persist not allowed on View");
	}

	@PreRemove
	private void preRemove() throws Exception {
		throw new Exception("Remove not allowed on View");
	}

	@PreUpdate
	private void preUpdate() throws Exception {
		throw new Exception("Update not allowed on View");
	}

}
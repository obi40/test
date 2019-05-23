package com.optimiza.ehope.lis.model;

import java.io.Serializable;
import java.util.Set;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.envers.Audited;
import org.hibernate.envers.RelationTargetAuditMode;
import org.hibernate.validator.constraints.Email;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.optimiza.core.base.entity.BaseAuditableTenantedEntity;
import com.optimiza.core.common.data.model.TransField;
import com.optimiza.core.common.data.model.converter.TransFieldAttConverter;

/**
 * Doctor.java
 * 
 * @author Murad Poladian <mpoladian@optimizasolutions.com>
 * @since Jul/26/2018
 * 
 */
@Entity
@Table(name = "doctor")
@Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
public class Doctor extends BaseAuditableTenantedEntity implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Basic(optional = false)
	@Column(name = "rid")
	private Long rid;

	@Column(name = "email")
	@Size(max = 255)
	@Email
	private String email;

	@Column(name = "mobile_no")
	@Size(max = 255)
	private String mobileNo;

	@Size(max = 4000)
	@Convert(converter = TransFieldAttConverter.class)
	@Column(name = "description")
	private TransField description;

	@NotNull
	@Size(min = 1, max = 4000)
	@Convert(converter = TransFieldAttConverter.class)
	@Column(name = "name")
	private TransField name;

	@Column(name = "phone_no")
	@Size(max = 255)
	private String phoneNo;

	@OneToMany(mappedBy = "doctor", fetch = FetchType.LAZY)
	@JsonIgnoreProperties("doctor")
	private Set<EmrVisit> visits;

	public Doctor() {
	}

	@Override
	public Long getRid() {
		return this.rid;
	}

	public void setRid(Long rid) {
		this.rid = rid;
	}

	public TransField getDescription() {
		return this.description;
	}

	public void setDescription(TransField description) {
		this.description = description;
	}

	public String getEmail() {
		return this.email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getMobileNo() {
		return this.mobileNo;
	}

	public void setMobileNo(String mobileNo) {
		this.mobileNo = mobileNo;
	}

	public TransField getName() {
		return this.name;
	}

	public void setName(TransField name) {
		this.name = name;
	}

	public String getPhoneNo() {
		return this.phoneNo;
	}

	public void setPhoneNo(String phoneNo) {
		this.phoneNo = phoneNo;
	}

	public Set<EmrVisit> getVisits() {
		return visits;
	}

	public void setVisits(Set<EmrVisit> visits) {
		this.visits = visits;
	}

}
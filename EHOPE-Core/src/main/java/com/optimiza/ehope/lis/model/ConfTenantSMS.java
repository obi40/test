package com.optimiza.ehope.lis.model;

import java.io.Serializable;

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
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.envers.Audited;

import com.optimiza.core.base.entity.BaseAuditableTenantedEntity;
import com.optimiza.core.common.util.BooleanIntegerConverter;
import com.optimiza.ehope.lis.lkp.model.LkpSMSKey;

/**
 * ConfTenantSMS.java
 * 
 * @author Abdullah Imran <aImran@optimizasolutions.com>
 * @since Nov/27/2018
 **/
@Entity
@Table(name = "conf_tenant_sms")
@Audited
public class ConfTenantSMS extends BaseAuditableTenantedEntity implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "rid")
	@Basic(optional = false)
	private Long rid;

	@NotNull
	@Column(name = "key")
	@Size(min = 1, max = 4000)
	private String key;

	@Column(name = "value")
	@Size(min = 0, max = 4000)
	private String value;

	@JoinColumn(name = "sms_key_id")
	@ManyToOne(fetch = FetchType.LAZY)
	private LkpSMSKey smsKey;

	@NotNull
	@Column(name = "is_encode_key")
	@Convert(converter = BooleanIntegerConverter.class)
	private Boolean isEncodeKey;

	@NotNull
	@Column(name = "is_encode_value")
	@Convert(converter = BooleanIntegerConverter.class)
	private Boolean isEncodeValue;

	public ConfTenantSMS() {

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
		ConfTenantSMS other = (ConfTenantSMS) obj;
		if (rid == null) {
			if (other.rid != null)
				return false;
		} else if (!rid.equals(other.rid))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "ConfTenantSMS [rid=" + rid + "]";
	}

	@Override
	public Long getRid() {
		return rid;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public LkpSMSKey getSmsKey() {
		return smsKey;
	}

	public void setSmsKey(LkpSMSKey smsKey) {
		this.smsKey = smsKey;
	}

	public Boolean getIsEncodeKey() {
		return isEncodeKey;
	}

	public void setIsEncodeKey(Boolean isEncodeKey) {
		this.isEncodeKey = isEncodeKey;
	}

	public Boolean getIsEncodeValue() {
		return isEncodeValue;
	}

	public void setIsEncodeValue(Boolean isEncodeValue) {
		this.isEncodeValue = isEncodeValue;
	}

	public void setRid(Long rid) {
		this.rid = rid;
	}

}
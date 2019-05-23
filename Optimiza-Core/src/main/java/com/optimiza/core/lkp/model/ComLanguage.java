package com.optimiza.core.lkp.model;

import java.io.Serializable;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.Size;

import org.hibernate.envers.Audited;

import com.optimiza.core.base.entity.BaseAuditableEntity;
import com.optimiza.core.common.util.BooleanIntegerConverter;

/**
 * ComLanguage.java
 * 
 * @author Abdullah Imran <aImran@optimizasolutions.com>
 * @since Sep/28/2017
 **/
@Entity
@Table(name = "com_languages")
@Audited
public class ComLanguage extends BaseAuditableEntity implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Basic(optional = false)
	@Column(name = "rid")
	private Long rid;

	@Column(name = "is_entry")
	@Convert(converter = BooleanIntegerConverter.class)
	private Boolean isEntry;

	@Column(name = "is_login")
	@Convert(converter = BooleanIntegerConverter.class)
	private Boolean isLogin;

	@Column(name = "locale", unique = true)
	@Size(min = 1, max = 255)
	private String locale;

	@Column(name = "name")
	@Size(min = 1, max = 255)
	private String name;

	@Column(name = "arrangement")
	private Long arrangement;

	@Column(name = "shortcut_name")
	@Size(min = 1, max = 10)
	private String shortcutName;

	@Column(name = "direction")
	@Size(min = 1, max = 3)
	private String direction;

	public ComLanguage() {
	}

	@Override
	public Long getRid() {
		return this.rid;
	}

	public Boolean getIsEntry() {
		return isEntry;
	}

	public void setIsEntry(Boolean isEntry) {
		this.isEntry = isEntry;
	}

	public Boolean getIsLogin() {
		return isLogin;
	}

	public void setIsLogin(Boolean isLogin) {
		this.isLogin = isLogin;
	}

	public String getLocale() {
		return locale;
	}

	public void setLocale(String locale) {
		this.locale = locale;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Long getArrangement() {
		return arrangement;
	}

	public void setArrangement(Long arrangement) {
		this.arrangement = arrangement;
	}

	public String getShortcutName() {
		return shortcutName;
	}

	public void setShortcutName(String shortcutName) {
		this.shortcutName = shortcutName;
	}

	public String getDirection() {
		return direction;
	}

	public void setDirection(String direction) {
		this.direction = direction;
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
		ComLanguage other = (ComLanguage) obj;
		if (rid == null) {
			if (other.rid != null)
				return false;
		} else if (!rid.equals(other.rid))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "ComLanguage [rid=" + rid + "]";
	}

}
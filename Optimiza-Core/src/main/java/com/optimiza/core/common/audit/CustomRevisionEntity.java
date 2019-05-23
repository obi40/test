package com.optimiza.core.common.audit;

import java.io.Serializable;
import java.text.DateFormat;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.envers.DefaultRevisionEntity;
import org.hibernate.envers.RevisionEntity;
import org.hibernate.envers.RevisionNumber;
import org.hibernate.envers.RevisionTimestamp;

@Entity
@Table(name = "REVINFO")
@RevisionEntity(CustomRevisionListener.class)
public class CustomRevisionEntity implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue
	@RevisionNumber
	@Column(name = "ID")
	private Integer id;

	@RevisionTimestamp
	@Column(name = "REVTSTMP")
	private long timestamp;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	@Transient
	public Date getRevisionDate() {
		return new Date(timestamp);
	}

	@Column(name = "USERNAME")
	private String username;

	@Column(name = "DATE_OPER")
	private Date dateOperation;

	public long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (!(o instanceof DefaultRevisionEntity))
			return false;

		DefaultRevisionEntity that = (DefaultRevisionEntity) o;

		if (id != that.getId())
			return false;
		if (timestamp != that.getTimestamp())
			return false;

		return true;
	}

	@Override
	public int hashCode() {
		int result;
		result = id;
		result = 31 * result + (int) (timestamp ^ (timestamp >>> 32));
		return result;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public Date getDateOperation() {
		return dateOperation;
	}

	public void setDateOperation(Date dateOperation) {
		this.dateOperation = dateOperation;
	}

	@Override
	public String toString() {
		return "DefaultRevisionEntity(id = " + id + ", revisionDate = " + DateFormat.getDateTimeInstance().format(getRevisionDate()) + ")";
	}
}

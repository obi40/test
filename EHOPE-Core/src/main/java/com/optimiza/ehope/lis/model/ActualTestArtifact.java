package com.optimiza.ehope.lis.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;

import com.optimiza.core.base.entity.BaseAuditableTenantedEntity;

/**
 * ActualTestArtifact.java
 * 
 * @author Murad Poladian <mpoladian@optimizasolutions.com>
 * @since Jan/31/2019
 * 
 */
@Entity
@Table(name = "actual_test_artifact")
@Audited
public class ActualTestArtifact extends BaseAuditableTenantedEntity implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long rid;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "actual_test_id")
	@NotNull
	private LabTestActual actualTest;

	@NotAudited
	@NotNull
	private byte[] content;

	@Column(name = "file_name")
	@NotNull
	private String fileName;

	@NotNull
	private String extension;

	@Column(name = "content_type")
	@NotNull
	private String contentType;

	@NotNull
	private Long size;

	public ActualTestArtifact() {
	}

	@Override
	public Long getRid() {
		return this.rid;
	}

	public void setRid(Long rid) {
		this.rid = rid;
	}

	public LabTestActual getActualTest() {
		return actualTest;
	}

	public void setActualTest(LabTestActual actualTest) {
		this.actualTest = actualTest;
	}

	public byte[] getContent() {
		return content;
	}

	public void setContent(byte[] content) {
		this.content = content;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getExtension() {
		return extension;
	}

	public void setExtension(String extension) {
		this.extension = extension;
	}

	public String getContentType() {
		return contentType;
	}

	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

	public Long getSize() {
		return size;
	}

	public void setSize(Long size) {
		this.size = size;
	}

}
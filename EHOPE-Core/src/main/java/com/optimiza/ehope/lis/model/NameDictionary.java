package com.optimiza.ehope.lis.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import com.optimiza.core.base.entity.BaseEntity;

/**
 * BillClassification.java
 * 
 * @author Murad Poladian <mpoladian@optimizasolutions.com>
 * @since Aug/20/2018
 * 
 */
@Entity
@Table(name = "name_dictionary")
public class NameDictionary extends BaseEntity implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long rid;

	@Column(name = "arabic_name")
	private String arabicName;

	@Column(name = "english_name")
	private String englishName;

	@Column(name = "arabic_normalized")
	private String arabicNormalized;

	@Column(name = "english_normalized")
	private String englishNormalized;

	@Column(name = "rec_count")
	private Integer recCount;

	public NameDictionary() {
	}

	@Override
	public Long getRid() {
		return this.rid;
	}

	public void setRid(Long rid) {
		this.rid = rid;
	}

	public String getArabicName() {
		return this.arabicName;
	}

	public void setArabicName(String arabicName) {
		this.arabicName = arabicName;
	}

	public String getEnglishName() {
		return this.englishName;
	}

	public void setEnglishName(String englishName) {
		this.englishName = englishName;
	}

	public String getArabicNormalized() {
		return arabicNormalized;
	}

	public void setArabicNormalized(String arabicNormalized) {
		this.arabicNormalized = arabicNormalized;
	}

	public String getEnglishNormalized() {
		return englishNormalized;
	}

	public void setEnglishNormalized(String englishNormalized) {
		this.englishNormalized = englishNormalized;
	}

	public Integer getRecCount() {
		return this.recCount;
	}

	public void setRecCount(Integer recCount) {
		this.recCount = recCount;
	}

}
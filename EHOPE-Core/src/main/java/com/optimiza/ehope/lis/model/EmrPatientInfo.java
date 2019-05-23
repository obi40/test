package com.optimiza.ehope.lis.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
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
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.validation.constraints.Digits;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.envers.Audited;
import org.hibernate.envers.RelationTargetAuditMode;
import org.hibernate.validator.constraints.Email;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.optimiza.core.base.entity.BaseAuditableTenantedEntity;
import com.optimiza.core.common.data.model.TransField;
import com.optimiza.core.common.data.model.converter.TransFieldAttConverter;
import com.optimiza.core.common.helper.AgeWrapper;
import com.optimiza.core.common.util.BooleanIntegerConverter;
import com.optimiza.core.common.util.DateUtil;
import com.optimiza.core.lkp.model.LkpCountry;
import com.optimiza.core.lkp.model.LkpGender;
import com.optimiza.ehope.lis.lkp.model.LkpBloodType;
import com.optimiza.ehope.lis.lkp.model.LkpMaritalStatus;
import com.optimiza.ehope.lis.lkp.model.LkpPatientStatus;
import com.optimiza.ehope.lis.lkp.model.LkpRace;
import com.optimiza.ehope.lis.lkp.model.LkpReligion;

/**
 * EmrPatientInfo.java
 * 
 * @author Eshraq Albakri <ebakri@optimizasolutions.com>
 * @since Jun/5/2017
 */
@Entity
@Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
@Table(name = "emr_patient_info")
public class EmrPatientInfo extends BaseAuditableTenantedEntity implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Basic(optional = false)
	@Column(name = "rid")
	private Long rid;

	@Size(max = 4000)
	@Convert(converter = TransFieldAttConverter.class)
	@Column(name = "address")
	private TransField address;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "blood_type_id")
	private LkpBloodType bloodType;

	@Column(name = "file_number", updatable = false)
	@NotNull
	@Size(min = 1, max = 255)
	private String fileNo;

	@Column(name = "date_of_birth")
	@NotNull
	@Temporal(TemporalType.TIMESTAMP)
	private Date dateOfBirth;

	@Column(name = "last_order_date")
	@Temporal(TemporalType.TIMESTAMP)
	private Date lastOrderDate;

	@Transient
	@JsonSerialize
	private Long age;

	@Transient
	@JsonSerialize
	private AgeWrapper ageWithUnit;

	@Column(name = "email")
	@Size(max = 255)
	@Email
	private String email;

	@Column(name = "last_name")
	@NotNull
	@Size(min = 1, max = 4000)
	@Convert(converter = TransFieldAttConverter.class)
	private TransField lastName;

	@Column(name = "first_name")
	@NotNull
	@Size(min = 1, max = 4000)
	@Convert(converter = TransFieldAttConverter.class)
	private TransField firstName;

	@Column(name = "full_name")
	@NotNull
	@Size(min = 1, max = 4000)
	@Convert(converter = TransFieldAttConverter.class)
	private TransField fullName;

	@NotNull
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "gender_id")
	private LkpGender gender;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "merged_to_patient_info_id")
	private EmrPatientInfo mergedToPatientInfo;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "marital_status_id")
	private LkpMaritalStatus maritalStatus;

	@Column(name = "marriage_date")
	@Temporal(TemporalType.TIMESTAMP)
	private Date marriageDate;

	@Column(name = "mobile_no")
	@NotNull
	@Size(min = 1, max = 255)
	private String mobileNo;

	@Column(name = "secondary_mobile_no")
	@NotNull
	@Size(min = 1, max = 255)
	private String secondaryMobileNo;

	@NotNull
	@Column(name = "balance")
	@Digits(integer = 15, fraction = 3)
	private BigDecimal balance;

	@Column(name = "mother_name")
	@Size(max = 4000)
	@Convert(converter = TransFieldAttConverter.class)
	private TransField motherName;

	@Column(name = "national_id")
	private Long nationalId;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "nationality_id")
	//TODO rename to nationality
	private LkpCountry country;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "patient_status_id")
	private LkpPatientStatus patientStatus;

	@Column(name = "phone_no")
	@Size(max = 255)
	private String phoneNo;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "race_id")
	private LkpRace race;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "religion_id")
	private LkpReligion religion;

	@Size(max = 4000)
	@Convert(converter = TransFieldAttConverter.class)
	@Column(name = "remarks")
	private TransField remarks;

	@NotNull
	@Column(name = "medical_information")
	@Size(min = 1, max = 4000)
	private String medicalInformation;

	@NotNull
	@Column(name = "is_active")
	@Convert(converter = BooleanIntegerConverter.class)
	private Boolean isActive;

	@NotNull
	@Column(name = "is_black_listed")
	@Convert(converter = BooleanIntegerConverter.class)
	private Boolean isBlackListed;

	@NotNull
	@Column(name = "is_verified")
	@Convert(converter = BooleanIntegerConverter.class)
	private Boolean isVerified;

	@NotNull
	@Column(name = "is_vip")
	@Convert(converter = BooleanIntegerConverter.class)
	private Boolean isVip;

	@NotNull
	@Column(name = "is_difficulty_finding_veins")
	@Convert(converter = BooleanIntegerConverter.class)
	private Boolean isDifficultyFindingVeins;

	@NotNull
	@Column(name = "is_blood_sample_phobia")
	@Convert(converter = BooleanIntegerConverter.class)
	private Boolean isBloodSamplePhobia;

	@NotNull
	@Column(name = "is_allergies")
	@Convert(converter = BooleanIntegerConverter.class)
	private Boolean isAllergies;

	@NotNull
	@Column(name = "is_hard_tempered_patient")
	@Convert(converter = BooleanIntegerConverter.class)
	private Boolean isHardTemperedPatient;

	@NotNull
	@Column(name = "is_pregnancy")
	@Convert(converter = BooleanIntegerConverter.class)
	private Boolean isPregnancy;

	@NotNull
	@Column(name = "is_hypertension")
	@Convert(converter = BooleanIntegerConverter.class)
	private Boolean isHypertension;

	@NotNull
	@Column(name = "is_diabetes")
	@Convert(converter = BooleanIntegerConverter.class)
	private Boolean isDiabetes;

	@NotNull
	@Column(name = "is_heart_disease")
	@Convert(converter = BooleanIntegerConverter.class)
	private Boolean isHeartDisease;

	@NotNull
	@Column(name = "is_sms_notification")
	@Convert(converter = BooleanIntegerConverter.class)
	private Boolean isSmsNotification;

	@NotNull
	@Column(name = "is_whatsapp_notification")
	@Convert(converter = BooleanIntegerConverter.class)
	private Boolean isWhatsappNotification;

	@NotNull
	@Column(name = "is_email_notification")
	@Convert(converter = BooleanIntegerConverter.class)
	private Boolean isEmailNotification;

	@Column(name = "second_name")
	@NotNull
	@Size(min = 1, max = 4000)
	@Convert(converter = TransFieldAttConverter.class)
	private TransField secondName;

	@Column(name = "third_name")
	@NotNull
	@Size(min = 1, max = 4000)
	@Convert(converter = TransFieldAttConverter.class)
	private TransField thirdName;

	@Column(name = "medical_history")
	@Size(max = 4000)
	private String medicalHistory;

	private byte[] image;

	@OneToMany(mappedBy = "patient", fetch = FetchType.LAZY)
	@JsonIgnoreProperties(value = { "patient" }, allowSetters = true)
	private Set<EmrPatientInsuranceInfo> emrPatientInsurance;

	@OneToMany(mappedBy = "emrPatientInfo", fetch = FetchType.LAZY)
	@JsonIgnoreProperties("emrPatientInfo")
	private Set<EmrVisit> emrVisits;

	@OneToMany(mappedBy = "patient", fetch = FetchType.LAZY)
	@JsonIgnoreProperties("patient")
	private Set<PatientArtifact> artifacts;

	@OneToMany(mappedBy = "patient", fetch = FetchType.LAZY)
	@JsonIgnoreProperties("patient")
	private Set<PatientFingerprint> fingerprints;

	@Transient
	@JsonSerialize
	private List<Object> artifactDescriptions;

	public EmrPatientInfo() {
	}

	public EmrPatientInfo getMergedToPatientInfo() {
		return mergedToPatientInfo;
	}

	public void setMergedToPatientInfo(EmrPatientInfo mergedToPatientInfo) {
		this.mergedToPatientInfo = mergedToPatientInfo;
	}

	@Override
	public Long getRid() {
		return this.rid;
	}

	public void setRid(Long rid) {
		this.rid = rid;
	}

	public String getMedicalInformation() {
		return medicalInformation;
	}

	public void setMedicalInformation(String medicalInformation) {
		this.medicalInformation = medicalInformation;
	}

	public BigDecimal getBalance() {
		return balance;
	}

	public void setBalance(BigDecimal balance) {
		this.balance = balance;
	}

	public Date getLastOrderDate() {
		return lastOrderDate;
	}

	public void setLastOrderDate(Date lastOrderDate) {
		this.lastOrderDate = lastOrderDate;
	}

	public TransField getAddress() {
		return address;
	}

	public void setAddress(TransField address) {
		this.address = address;
	}

	public LkpBloodType getBloodType() {
		return bloodType;
	}

	public void setBloodType(LkpBloodType bloodType) {
		this.bloodType = bloodType;
	}

	public String getFileNo() {
		return fileNo;
	}

	public void setFileNo(String fileNo) {
		this.fileNo = fileNo;
	}

	public Date getDateOfBirth() {
		return dateOfBirth;
	}

	public void setDateOfBirth(Date dateOfBirth) {
		this.dateOfBirth = dateOfBirth;
	}

	public Long getAge() {
		age = DateUtil.getAge(dateOfBirth);
		return age;
	}

	public AgeWrapper getAgeWithUnit() {
		ageWithUnit = DateUtil.getAgeWithUnit(dateOfBirth);
		return ageWithUnit;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public TransField getLastName() {
		return lastName;
	}

	public void setLastName(TransField lastName) {
		this.lastName = lastName;
	}

	public TransField getFirstName() {
		return firstName;
	}

	public void setFirstName(TransField firstName) {
		this.firstName = firstName;
	}

	public TransField getFullName() {
		return fullName;
	}

	public void setFullName(TransField fullName) {
		this.fullName = fullName;
	}

	public LkpGender getGender() {
		return gender;
	}

	public void setGender(LkpGender gender) {
		this.gender = gender;
	}

	public Boolean getIsActive() {
		return isActive;
	}

	public void setIsActive(Boolean isActive) {
		this.isActive = isActive;
	}

	public Boolean getIsBlackListed() {
		return isBlackListed;
	}

	public void setIsBlackListed(Boolean isBlackListed) {
		this.isBlackListed = isBlackListed;
	}

	public Boolean getIsVerified() {
		return isVerified;
	}

	public void setIsVerified(Boolean isVerified) {
		this.isVerified = isVerified;
	}

	public Boolean getIsVip() {
		return isVip;
	}

	public void setIsVip(Boolean isVip) {
		this.isVip = isVip;
	}

	public LkpMaritalStatus getMaritalStatus() {
		return maritalStatus;
	}

	public void setMaritalStatus(LkpMaritalStatus maritalStatus) {
		this.maritalStatus = maritalStatus;
	}

	public Date getMarriageDate() {
		return marriageDate;
	}

	public void setMarriageDate(Date marriageDate) {
		this.marriageDate = marriageDate;
	}

	public String getMobileNo() {
		return mobileNo;
	}

	public void setMobileNo(String mobileNo) {
		this.mobileNo = mobileNo;
	}

	public String getSecondaryMobileNo() {
		return secondaryMobileNo;
	}

	public void setSecondaryMobileNo(String secondaryMobileNo) {
		this.secondaryMobileNo = secondaryMobileNo;
	}

	public TransField getMotherName() {
		return motherName;
	}

	public void setMotherName(TransField motherName) {
		this.motherName = motherName;
	}

	public Long getNationalId() {
		return nationalId;
	}

	public void setNationalId(Long nationalId) {
		this.nationalId = nationalId;
	}

	public LkpCountry getCountry() {
		return country;
	}

	public void setCountry(LkpCountry country) {
		this.country = country;
	}

	public LkpPatientStatus getPatientStatus() {
		return patientStatus;
	}

	public void setPatientStatus(LkpPatientStatus patientStatus) {
		this.patientStatus = patientStatus;
	}

	public String getPhoneNo() {
		return phoneNo;
	}

	public void setPhoneNo(String phoneNo) {
		this.phoneNo = phoneNo;
	}

	public LkpRace getRace() {
		return race;
	}

	public void setRace(LkpRace race) {
		this.race = race;
	}

	public LkpReligion getReligion() {
		return religion;
	}

	public void setReligion(LkpReligion religion) {
		this.religion = religion;
	}

	public TransField getRemarks() {
		return remarks;
	}

	public void setRemarks(TransField remarks) {
		this.remarks = remarks;
	}

	public Boolean getIsDifficultyFindingVeins() {
		return isDifficultyFindingVeins;
	}

	public void setIsDifficultyFindingVeins(Boolean isDifficultyFindingVeins) {
		this.isDifficultyFindingVeins = isDifficultyFindingVeins;
	}

	public Boolean getIsBloodSamplePhobia() {
		return isBloodSamplePhobia;
	}

	public void setIsBloodSamplePhobia(Boolean isBloodSamplePhobia) {
		this.isBloodSamplePhobia = isBloodSamplePhobia;
	}

	public Boolean getIsAllergies() {
		return isAllergies;
	}

	public void setIsAllergies(Boolean isAllergies) {
		this.isAllergies = isAllergies;
	}

	public Boolean getIsHardTemperedPatient() {
		return isHardTemperedPatient;
	}

	public void setIsHardTemperedPatient(Boolean isHardTemperedPatient) {
		this.isHardTemperedPatient = isHardTemperedPatient;
	}

	public Boolean getIsPregnancy() {
		return isPregnancy;
	}

	public void setIsPregnancy(Boolean isPregnancy) {
		this.isPregnancy = isPregnancy;
	}

	public Boolean getIsHypertension() {
		return isHypertension;
	}

	public void setIsHypertension(Boolean isHypertension) {
		this.isHypertension = isHypertension;
	}

	public Boolean getIsDiabetes() {
		return isDiabetes;
	}

	public void setIsDiabetes(Boolean isDiabetes) {
		this.isDiabetes = isDiabetes;
	}

	public Boolean getIsHeartDisease() {
		return isHeartDisease;
	}

	public void setIsHeartDisease(Boolean isHeartDisease) {
		this.isHeartDisease = isHeartDisease;
	}

	public TransField getSecondName() {
		return secondName;
	}

	public void setSecondName(TransField secondName) {
		this.secondName = secondName;
	}

	public TransField getThirdName() {
		return thirdName;
	}

	public void setThirdName(TransField thirdName) {
		this.thirdName = thirdName;
	}

	public String getMedicalHistory() {
		return medicalHistory;
	}

	public void setMedicalHistory(String medicalHistory) {
		this.medicalHistory = medicalHistory;
	}

	public byte[] getImage() {
		return image;
	}

	public void setImage(byte[] image) {
		this.image = image;
	}

	public Boolean getIsSmsNotification() {
		return isSmsNotification;
	}

	public void setIsSmsNotification(Boolean isSmsNotification) {
		this.isSmsNotification = isSmsNotification;
	}

	public Boolean getIsWhatsappNotification() {
		return isWhatsappNotification;
	}

	public void setIsWhatsappNotification(Boolean isWhatsappNotification) {
		this.isWhatsappNotification = isWhatsappNotification;
	}

	public Boolean getIsEmailNotification() {
		return isEmailNotification;
	}

	public void setIsEmailNotification(Boolean isEmailNotification) {
		this.isEmailNotification = isEmailNotification;
	}

	public Set<EmrPatientInsuranceInfo> getEmrPatientInsurance() {
		return emrPatientInsurance;
	}

	public void setEmrPatientInsurance(Set<EmrPatientInsuranceInfo> emrPatientInsurance) {
		this.emrPatientInsurance = emrPatientInsurance;
	}

	public Set<EmrVisit> getEmrVisits() {
		return emrVisits;
	}

	public void setEmrVisits(Set<EmrVisit> emrVisits) {
		this.emrVisits = emrVisits;
	}

	//	@JsonProperty
	//	public Map<String, String> fullName() {
	//		Map<String, String> fullNameMap = new HashMap<>();
	//		for (Map.Entry<String, String> entry : getFirstName().entrySet()) {
	//			String key = entry.getKey();
	//			String result = StringUtil.isEmpty(entry.getValue()) ? "" : entry.getValue();
	//			if (getSecondName().containsKey(key)) {
	//				result += (" " + getSecondName().get(key));
	//			}
	//			if (getThirdName().containsKey(key)) {
	//				result += (" " + getThirdName().get(key));
	//			}
	//			if (getLastName().containsKey(key)) {
	//				result += (" " + getLastName().get(key));
	//			}
	//			result = result.replaceAll("\\s+", " ");
	//			fullNameMap.put(entry.getKey(), result);
	//			// && !StringUtils.isEmpty(getThirdName().get(entry.getKey()))
	//		}
	//		return fullNameMap;
	//	}

	public Set<PatientArtifact> getArtifacts() {
		return artifacts;
	}

	public void setArtifacts(Set<PatientArtifact> artifacts) {
		this.artifacts = artifacts;
	}

	public List<Object> getArtifactDescriptions() {
		return artifactDescriptions;
	}

	public void setArtifactDescriptions(List<Object> artifactDescriptions) {
		this.artifactDescriptions = artifactDescriptions;
	}

	public Set<PatientFingerprint> getFingerprints() {
		return fingerprints;
	}

	public void setFingerprints(Set<PatientFingerprint> fingerprints) {
		this.fingerprints = fingerprints;
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
		EmrPatientInfo other = (EmrPatientInfo) obj;
		if (rid == null) {
			if (other.rid != null)
				return false;
		} else if (!rid.equals(other.rid))
			return false;
		return true;
	}

}
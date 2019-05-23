package com.optimiza.core.admin.model;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
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
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.envers.Audited;
import org.hibernate.validator.constraints.Email;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.optimiza.core.admin.lkp.model.LkpUserStatus;
import com.optimiza.core.base.entity.BaseAuditableTenantedEntity;
import com.optimiza.core.common.data.model.TransField;
import com.optimiza.core.common.data.model.converter.TransFieldAttConverter;
import com.optimiza.core.common.util.BooleanIntegerConverter;
import com.optimiza.core.common.util.StringUtil;
import com.optimiza.core.lkp.model.ComLanguage;
import com.optimiza.core.lkp.model.ComTenantLanguage;
import com.optimiza.core.lkp.model.LkpCountry;
import com.optimiza.core.lkp.model.LkpGender;

/**
 * SecUser.java
 * 
 * @author Abdullah Imran <aImran@optimizasolutions.com>
 * @since Sep/27/2017
 **/
@Entity
@Table(name = "sec_users")
@Audited
//WHENEVER ADDING/REMOVING @NotNull , MUST ALSO CHANGE THE createTenantAdminUser(...) TO REFLECT @NotNull VALIDATIONS 
public class SecUser extends BaseAuditableTenantedEntity implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Basic(optional = false)
	@Column(name = "rid")
	private Long rid;

	@Column(name = "address")
	@Size(max = 4000)
	@Convert(converter = TransFieldAttConverter.class)
	private TransField address;

	@Column(name = "branch_id")
	private Long branchId;

	@NotNull
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "default_language_id")
	private ComLanguage comLanguage;

	@NotNull
	@Column(name = "email")
	@Size(max = 255)
	@Email
	private String email;

	@Column(name = "first_name")
	@NotNull
	@Size(min = 1, max = 4000)
	@Convert(converter = TransFieldAttConverter.class)
	private TransField firstName;

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

	@Column(name = "family_name")
	@NotNull
	@Size(min = 1, max = 4000)
	@Convert(converter = TransFieldAttConverter.class)
	private TransField lastName;

	@NotNull
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "gender_id")
	private LkpGender lkpGender;

	@Column(name = "last_login_time")
	@Temporal(TemporalType.TIMESTAMP)
	private Date lastLoginTime;

	@Column(name = "mobile_no")
	@NotNull
	@Size(min = 1, max = 255)
	private String mobileNo;

	@NotNull
	@Column(name = "national_id")
	private Long nationalId;

	@Column(name = "password")
	@NotNull
	@Size(min = 1, max = 255)
	@JsonIgnore
	private String password;

	@NotNull
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "status_id")
	private LkpUserStatus lkpUserStatus;

	@Column(name = "username", unique = true, updatable = false)
	@NotNull
	@Size(min = 1, max = 255)
	private String username;

	@Column(name = "is_active")
	@NotNull
	@Convert(converter = BooleanIntegerConverter.class)
	private Boolean isActive;

	//bi-directional many-to-one association to SecGroupUser
	@JsonIgnore
	@OneToMany(mappedBy = "secUser", fetch = FetchType.LAZY)
	private Set<SecGroupUser> secGroupUsers;

	//bi-directional many-to-one association to SecUserRole
	@JsonIgnore
	@OneToMany(mappedBy = "secUser", fetch = FetchType.LAZY)
	private Set<SecUserRole> secUserRoles;

	//used when fetching a user in SpringLoginService
	@Transient
	@JsonProperty
	private List<ComTenantLanguage> tenantLanguages;

	@Transient
	@JsonProperty
	private LkpCountry country;

	//used when fetching a user with groups
	@Transient
	@JsonProperty
	private Set<SecGroup> userGroups = new HashSet<>();

	//used when fetching a user with roles
	@Transient
	@JsonProperty
	private Set<SecRole> userRoles = new HashSet<>();

	@Transient
	@JsonProperty
	private SecTenant tenant;

	@Transient
	@JsonProperty
	private Object branch;

	public SecUser() {
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
		SecUser other = (SecUser) obj;
		if (rid == null) {
			if (other.rid != null)
				return false;
		} else if (!rid.equals(other.rid))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "SecUser [rid=" + rid + "]";
	}

	@Override
	public Long getRid() {
		return this.rid;
	}

	public SecTenant getTenant() {
		return tenant;
	}

	public void setTenant(SecTenant tenant) {
		this.tenant = tenant;
	}

	public Boolean getIsActive() {
		return isActive;
	}

	public void setIsActive(Boolean isActive) {
		this.isActive = isActive;
	}

	public TransField getAddress() {
		return address;
	}

	public void setAddress(TransField address) {
		this.address = address;
	}

	public Long getBranchId() {
		return branchId;
	}

	public void setBranchId(Long branchId) {
		this.branchId = branchId;
	}

	public ComLanguage getComLanguage() {
		return comLanguage;
	}

	public void setComLanguage(ComLanguage comLanguage) {
		this.comLanguage = comLanguage;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public TransField getFirstName() {
		return firstName;
	}

	public void setFirstName(TransField firstName) {
		this.firstName = firstName;
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

	public TransField getLastName() {
		return lastName;
	}

	public void setLastName(TransField lastName) {
		this.lastName = lastName;
	}

	public LkpGender getLkpGender() {
		return lkpGender;
	}

	public void setLkpGender(LkpGender lkpGender) {
		this.lkpGender = lkpGender;
	}

	public Date getLastLoginTime() {
		return lastLoginTime;
	}

	public void setLastLoginTime(Date lastLoginTime) {
		this.lastLoginTime = lastLoginTime;
	}

	public String getMobileNo() {
		return mobileNo;
	}

	public void setMobileNo(String mobileNo) {
		this.mobileNo = mobileNo;
	}

	public Long getNationalId() {
		return nationalId;
	}

	public void setNationalId(Long nationalId) {
		this.nationalId = nationalId;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public LkpUserStatus getLkpUserStatus() {
		return lkpUserStatus;
	}

	public void setLkpUserStatus(LkpUserStatus lkpUserStatus) {
		this.lkpUserStatus = lkpUserStatus;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public Set<SecGroupUser> getSecGroupUsers() {
		return secGroupUsers;
	}

	public void setSecGroupUsers(Set<SecGroupUser> secGroupUsers) {
		this.secGroupUsers = secGroupUsers;
	}

	public Set<SecUserRole> getSecUserRoles() {
		return secUserRoles;
	}

	public void setSecUserRoles(Set<SecUserRole> secUserRoles) {
		this.secUserRoles = secUserRoles;
	}

	public List<ComTenantLanguage> getTenantLanguages() {
		return tenantLanguages;
	}

	public void setTenantLanguages(List<ComTenantLanguage> tenantLanguages) {
		this.tenantLanguages = tenantLanguages;
	}

	public LkpCountry getCountry() {
		return country;
	}

	public void setCountry(LkpCountry country) {
		this.country = country;
	}

	public Set<SecGroup> getUserGroups() {
		return userGroups;
	}

	public void setUserGroups(Set<SecGroup> userGroups) {
		this.userGroups = userGroups;
	}

	public Set<SecRole> getUserRoles() {
		return userRoles;
	}

	public void setUserRoles(Set<SecRole> userRoles) {
		this.userRoles = userRoles;
	}

	public Object getBranch() {
		return branch;
	}

	public void setBranch(Object branch) {
		this.branch = branch;
	}

	public void setRid(Long rid) {
		this.rid = rid;
	}

	@JsonProperty
	public Map<String, String> fullName() {
		Map<String, String> fullNameMap = new HashMap<>();
		if (getFirstName() != null) {
			for (Map.Entry<String, String> entry : getFirstName().entrySet()) {
				String key = entry.getKey();
				String result = StringUtil.isEmpty(entry.getValue()) ? "" : entry.getValue();
				if (getSecondName().containsKey(key)) {
					result += (" " + getSecondName().get(key));
				}
				if (getThirdName().containsKey(key)) {
					result += (" " + getThirdName().get(key));
				}
				if (getLastName().containsKey(key)) {
					result += (" " + getLastName().get(key));
				}
				result = result.replaceAll("\\s+", " ");
				fullNameMap.put(entry.getKey(), result);
			}
		}

		return fullNameMap;
	}

}
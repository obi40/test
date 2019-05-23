package com.optimiza.ehope.lis.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import com.optimiza.core.base.helper.SearchCriterion;
import com.optimiza.core.base.helper.SearchCriterion.FilterOperator;
import com.optimiza.core.base.service.GenericService;
import com.optimiza.core.common.business.exception.BusinessException;
import com.optimiza.core.common.business.exception.BusinessException.ErrorSeverity;
import com.optimiza.core.common.helper.FilterablePageRequest;
import com.optimiza.core.common.util.DateUtil;
import com.optimiza.ehope.lis.helper.EhopeRights;
import com.optimiza.ehope.lis.model.EmrPatientInfo;
import com.optimiza.ehope.lis.model.EmrPatientInsuranceInfo;
import com.optimiza.ehope.lis.repo.EmrPatientInsuranceInfoRepo;

@Service("EmrPatientInsuranceInfoService")
public class EmrPatientInsuranceInfoService extends GenericService<EmrPatientInsuranceInfo, EmrPatientInsuranceInfoRepo> {

	@Autowired
	private EmrPatientInsuranceInfoRepo repo;

	@Override
	protected EmrPatientInsuranceInfoRepo getRepository() {
		return repo;
	}

	public void deleteAllByPatient(EmrPatientInfo patient) {
		getRepository().deleteAllByPatient(patient);
	}

	@PreAuthorize("hasAuthority('" + EhopeRights.VIEW_PATIENT_INSURANCE + "')")
	public Page<EmrPatientInsuranceInfo> getPatientInsuranceList(FilterablePageRequest filterablePageRequest) {
		Page<EmrPatientInsuranceInfo> result = repo.find(filterablePageRequest.getFilters(),
				filterablePageRequest.getPageRequest(), EmrPatientInsuranceInfo.class,
				"lkpDependencyType", "insProvider.parentProvider", "insProviderPlan");
		return result;
	}

	/**
	 * used in patient profile wizard, payment step
	 * 
	 * @param filterablePageRequest
	 * @return
	 */
	public EmrPatientInsuranceInfo getOnePatientInsuranceJoinPlan(EmrPatientInsuranceInfo patientInsuranceInfo) {
		return getRepository().findOne(Arrays.asList(new SearchCriterion("rid", patientInsuranceInfo.getRid(), FilterOperator.eq)),
				EmrPatientInsuranceInfo.class, "insProviderPlan");
	}

	/**
	 * Check if there is any insurance for the patient that intersected with patientInsuranceInfo
	 * 
	 * @param patientInsuranceInfo
	 * @return Boolean
	 */
	private Boolean isPatientInsDuplicated(EmrPatientInsuranceInfo patientInsuranceInfo) {
		List<EmrPatientInsuranceInfo> patientInsList = findPatientInsListByProviderAndPlan(patientInsuranceInfo, Boolean.TRUE);
		for (EmrPatientInsuranceInfo epii : patientInsList) {
			if (!patientInsuranceInfo.equals(epii)
					&& DateUtil.isIntersected(patientInsuranceInfo.getIssueDate(), patientInsuranceInfo.getExpiryDate(),
							epii.getIssueDate(), epii.getIssueDate())) {
				return Boolean.TRUE;
			}
		}
		return Boolean.FALSE;
	}

	/**
	 * Get all patient insurances by patient id,provider,plan,state
	 * 
	 * @param patientInsuranceInfo
	 * @param isActive
	 * @return List
	 */
	public List<EmrPatientInsuranceInfo> findPatientInsListByProviderAndPlan(EmrPatientInsuranceInfo patientInsuranceInfo,
			Boolean isActive) {
		List<SearchCriterion> filters = new ArrayList<>();
		filters.add(new SearchCriterion("isActive", isActive, FilterOperator.eq));
		filters.add(new SearchCriterion("insProvider.rid", patientInsuranceInfo.getInsProvider().getRid(),
				FilterOperator.eq));
		if (patientInsuranceInfo.getInsProviderPlan() != null) {
			filters.add(new SearchCriterion("insProviderPlan.rid", patientInsuranceInfo.getInsProviderPlan().getRid(),
					FilterOperator.eq));
		}

		filters.add(new SearchCriterion("patient.rid", patientInsuranceInfo.getPatient().getRid(),
				FilterOperator.eq));
		return getRepository().find(filters, EmrPatientInsuranceInfo.class, "insProvider", "insProviderPlan", "patient");

	}

	private void validations(EmrPatientInsuranceInfo patientInsuranceInfo) {
		if (isPatientInsDuplicated(patientInsuranceInfo)) {
			throw new BusinessException("Patient Insurance Duplicated", "patientInsuranceDuplicated", ErrorSeverity.ERROR);
		}
		if (DateUtil.isBefore(patientInsuranceInfo.getExpiryDate(), new Date())) {
			throw new BusinessException("Patient insurance is expired", "patientInsuranceIsExpired", ErrorSeverity.ERROR);
		}
		if (patientInsuranceInfo.getIssueDate() != null && DateUtil.isAfter(patientInsuranceInfo.getIssueDate(), new Date())) {
			throw new BusinessException("Patient insurance is not yet effective", "patientInsuranceNotYetEffective",
					ErrorSeverity.ERROR);
		}
	}

	@PreAuthorize("hasAuthority('" + EhopeRights.ADD_PATIENT_INSURANCE + "')")
	public EmrPatientInsuranceInfo addPatientInsuranceInfo(EmrPatientInsuranceInfo patientInsuranceInfo) {
		validations(patientInsuranceInfo);
		triggerDefaultInsurance(patientInsuranceInfo, patientInsuranceInfo.getPatient().getRid());
		return repo.save(patientInsuranceInfo);
	}

	@PreAuthorize("hasAuthority('" + EhopeRights.UPD_PATIENT_INSURANCE + "')")
	public EmrPatientInsuranceInfo updatePatientInsuranceInfo(EmrPatientInsuranceInfo patientInsuranceInfo) {
		validations(patientInsuranceInfo);
		triggerDefaultInsurance(patientInsuranceInfo, patientInsuranceInfo.getPatient().getRid());
		return repo.save(patientInsuranceInfo);
	}

	@PreAuthorize("hasAuthority('" + EhopeRights.DEACTIVATE_PATIENT_INSURANCE + "')")
	public EmrPatientInsuranceInfo deactivatePatientInsurance(Long rid) {
		EmrPatientInsuranceInfo fetchedPatientInsuranceInfo = repo.getOne(rid);
		fetchedPatientInsuranceInfo.setIsActive(false);
		return repo.save(fetchedPatientInsuranceInfo);
	}

	@PreAuthorize("hasAuthority('" + EhopeRights.ACTIVATE_PATIENT_INSURANCE + "')")
	public EmrPatientInsuranceInfo activatePatientInsurance(Long rid) {
		EmrPatientInsuranceInfo fetchedPatientInsuranceInfo = repo.getOne(rid);
		if (isPatientInsDuplicated(fetchedPatientInsuranceInfo)) {
			throw new BusinessException("Patient Insurance Duplicated", "patientInsuranceDuplicated", ErrorSeverity.ERROR);
		}
		fetchedPatientInsuranceInfo.setIsActive(true);
		return repo.save(fetchedPatientInsuranceInfo);
	}

	/**
	 * For wizard when getting provider -> plan
	 * 
	 * @param emrPatientInfo
	 * @param isActive
	 * @return
	 * @throws BusinessException
	 */
	public List<EmrPatientInsuranceInfo> findPatientInsuranceListByPatient(EmrPatientInfo emrPatientInfo, Boolean isActive)
			throws BusinessException {
		SearchCriterion patientFilter = new SearchCriterion("patient.rid", emrPatientInfo.getRid(), FilterOperator.eq);
		SearchCriterion activeFilter = new SearchCriterion("isActive", isActive, FilterOperator.eq);
		return getRepository().find(Arrays.asList(patientFilter, activeFilter),
				EmrPatientInsuranceInfo.class, "lkpDependencyType", "patient", "insProvider.parentProvider", "insProviderPlan");
	}

	@PreAuthorize("hasAuthority('" + EhopeRights.UPD_PATIENT_INSURANCE + "')")
	public EmrPatientInsuranceInfo changeDefaultInsurance(EmrPatientInsuranceInfo patientInsuranceInfo) {
		patientInsuranceInfo.setIsDefault(Boolean.TRUE);
		triggerDefaultInsurance(patientInsuranceInfo, patientInsuranceInfo.getPatient().getRid());
		return getRepository().save(patientInsuranceInfo);
	}

	private void triggerDefaultInsurance(EmrPatientInsuranceInfo patientInsuranceInfo, Long patientRid) {
		if (patientInsuranceInfo.getIsDefault().equals(Boolean.FALSE)) {
			return;
		}
		EmrPatientInsuranceInfo currentDefaultInsurance = getCurrentDefaultInusrance(patientRid);
		if (currentDefaultInsurance != null && !currentDefaultInsurance.equals(patientInsuranceInfo)) {
			currentDefaultInsurance.setIsDefault(Boolean.FALSE);
			currentDefaultInsurance = getRepository().save(currentDefaultInsurance);
		}
	}

	private EmrPatientInsuranceInfo getCurrentDefaultInusrance(Long patientRid) {
		SearchCriterion patientFilter = new SearchCriterion("patient", patientRid, FilterOperator.eq);
		SearchCriterion patientInsuranceFilter = new SearchCriterion("isDefault", Boolean.TRUE, FilterOperator.eq);
		return getRepository().findOne(Arrays.asList(patientFilter, patientInsuranceFilter),
				EmrPatientInsuranceInfo.class);
	}

}

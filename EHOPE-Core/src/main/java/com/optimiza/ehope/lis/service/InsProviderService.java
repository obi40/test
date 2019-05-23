package com.optimiza.ehope.lis.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.optimiza.core.base.helper.SearchCriterion;
import com.optimiza.core.base.helper.SearchCriterion.FilterOperator;
import com.optimiza.core.base.helper.SearchCriterion.JunctionOperator;
import com.optimiza.core.base.service.GenericService;
import com.optimiza.core.common.business.exception.BusinessException;
import com.optimiza.core.common.business.exception.BusinessException.ErrorSeverity;
import com.optimiza.core.common.data.model.TransField;
import com.optimiza.core.common.util.CollectionUtil;
import com.optimiza.core.common.util.ReflectionUtil;
import com.optimiza.core.common.util.SecurityUtil;
import com.optimiza.core.lkp.model.LkpCountry;
import com.optimiza.core.lkp.service.LkpService;
import com.optimiza.ehope.lis.helper.EhopeRights;
import com.optimiza.ehope.lis.lkp.helper.ClientPurpose;
import com.optimiza.ehope.lis.lkp.helper.InsuranceType;
import com.optimiza.ehope.lis.lkp.helper.TestDestinationType;
import com.optimiza.ehope.lis.lkp.model.LkpInsuranceType;
import com.optimiza.ehope.lis.lkp.model.LkpTestDestinationType;
import com.optimiza.ehope.lis.model.BillPriceList;
import com.optimiza.ehope.lis.model.EmrVisit;
import com.optimiza.ehope.lis.model.InsCoverageDetail;
import com.optimiza.ehope.lis.model.InsProvider;
import com.optimiza.ehope.lis.model.InsProviderPlan;
import com.optimiza.ehope.lis.repo.InsProviderRepo;
import com.optimiza.ehope.lis.util.ExcelUtil;
import com.optimiza.ehope.lis.wrapper.InsParentProviderWrapper;

@Service("InsProviderService")
public class InsProviderService extends GenericService<InsProvider, InsProviderRepo> {

	@Autowired
	private InsProviderRepo repo;
	@Autowired
	private InsProviderPlanService insProviderPlanService;
	@Autowired
	private EmrVisitService visitService;
	@Autowired
	private InsCoverageDetailService coverageDetailService;
	@Autowired
	private BillPriceListService priceListService;
	@Autowired
	private LkpService lkpService;

	@Override
	protected InsProviderRepo getRepository() {
		return repo;
	}

	public void validation(Long providerRid) {
		List<EmrVisit> visits = visitService.getVisitsByProvider(providerRid);
		if (!CollectionUtil.isCollectionEmpty(visits)) {
			throw new BusinessException("Insurance is used", "insuranceUsed", ErrorSeverity.ERROR);
		}
	}

	@PreAuthorize("hasAuthority('" + EhopeRights.ADD_INS_PROVIDER + "')")
	public InsProvider createInsProvider(InsProvider insProvider) {
		InsuranceType it = InsuranceType.getByValue(insProvider.getInsuranceType().getCode());
		if (it == InsuranceType.LAB || it == InsuranceType.PUBLIC_SECTOR || it == InsuranceType.INDIVIDUALS) {
			insProvider.setIsSimple(Boolean.TRUE);
		} else {
			insProvider.setIsSimple(Boolean.FALSE);
		}
		insProvider.setBalance(BigDecimal.ZERO);
		insProvider = getRepository().save(insProvider);
		//		create balance for all types except PARENT tpa,lab_network,self fund
		//		if ((it != InsuranceType.TPA && it != InsuranceType.LAB_NETWORK && it != InsuranceType.SELF_FUNDED)
		//				|| insProvider.getParentProvider() != null) {
		//			insProvider.setBalance(BigDecimal.ZERO);
		//		}
		//if simple then create simple plan otherwise create an initial plan
		if (it == InsuranceType.LAB || it == InsuranceType.PUBLIC_SECTOR || it == InsuranceType.INDIVIDUALS) {
			insProviderPlanService.createSimplePlan(insProvider);
		} else if (insProvider.getParentProvider() != null || it == InsuranceType.INSURANCE) {
			insProviderPlanService.createInsProviderPlan(
					insProviderPlanService.reflectProviderData(insProvider, new InsProviderPlan(), Boolean.FALSE));
		}

		return insProvider;
	}

	/**
	 * for LOVs
	 * 
	 * @return List
	 */
	public List<InsProvider> findInsProviders() {
		return getRepository().find(Arrays.asList(new SearchCriterion("isActive", true, FilterOperator.eq)),
				InsProvider.class, "insuranceType", "parentProvider");
	}

	public List<InsProvider> findInsProvidersList(List<SearchCriterion> filters, Sort sort, String... joins) {
		return getRepository().find(filters, InsProvider.class, sort, joins);
	}

	@PreAuthorize("hasAuthority('" + EhopeRights.VIEW_INSURANCE_PROVIDER + "')")
	public List<InsParentProviderWrapper> fetchInsProviders() {
		List<InsProvider> providers = getRepository().find(new ArrayList<>(), InsProvider.class, "insuranceType", "insNetwork",
				"lkpCountry", "priceList", "parentProvider", "insuranceTenant", "insuranceBranch");
		List<InsParentProviderWrapper> ippws = new ArrayList<>();
		for (InsProvider provider : providers) {
			if (provider.getParentProvider() == null) {
				BigDecimal balance = provider.getBalance();
				if (balance == null) {
					balance = BigDecimal.ZERO;
				}
				ippws.add(new InsParentProviderWrapper(balance, provider, new ArrayList<>()));
			}
		}
		for (InsProvider provider : providers) {
			if (provider.getParentProvider() != null) {
				for (InsParentProviderWrapper ippw : ippws) {
					if (ippw.getMasterProvider().equals(provider.getParentProvider())) {
						BigDecimal balance = provider.getBalance();
						if (balance == null) {
							balance = BigDecimal.ZERO;
						}
						ippw.setMasterBalance(ippw.getMasterBalance().add(balance));
						ippw.getProviders().add(provider);
					}
				}

			}
		}
		return ippws;
	}

	@PreAuthorize("hasAuthority('" + EhopeRights.UPD_INS_PROVIDER + "')")
	public InsProvider updateInsProvider(InsProvider insProvider) {

		LkpInsuranceType lit = getRepository().findOne(Arrays.asList(new SearchCriterion("rid", insProvider.getRid(), FilterOperator.eq)),
				InsProvider.class,
				"insuranceType").getInsuranceType();
		InsuranceType currentInsuranceType = InsuranceType.getByValue(lit.getCode());
		InsuranceType newInsuranceType = InsuranceType.getByValue(insProvider.getInsuranceType().getCode());
		if (currentInsuranceType != newInsuranceType) {
			validation(insProvider.getRid());
			//			if ((newInsuranceType != InsuranceType.TPA && newInsuranceType != InsuranceType.LAB_NETWORK
			//					&& newInsuranceType != InsuranceType.SELF_FUNDED)
			//					|| insProvider.getParentProvider() != null) {
			//				insProvider.setBalance(BigDecimal.ZERO);
			//			} else {
			//				insProvider.setBalance(null);
			//			}
		}
		insProviderPlanService.updateSimplePlan(insProvider);// we check if it is simple or not inside the function
		propagateIsActive(insProvider.getRid(), insProvider.getIsActive());
		return getRepository().save(insProvider);
	}

	public List<InsProvider> update(Collection<InsProvider> insProvider) {
		return getRepository().save(insProvider);
	}

	/**
	 * Propagate isActive to plans and coverage details
	 * Used providerRid so we dont override the changes that came from the front-end
	 * 
	 * @param providerRid
	 * @param newIsActive
	 */
	private void propagateIsActive(Long providerRid, Boolean newIsActive) {
		InsProvider provider = getRepository().findOne(Arrays.asList(new SearchCriterion("rid", providerRid, FilterOperator.eq)),
				InsProvider.class);
		List<SearchCriterion> filters = new ArrayList<>();
		filters.add(new SearchCriterion("insProvider.rid", provider.getRid(), FilterOperator.eq));
		List<InsProvider> childrenProviders = getRepository().find(
				Arrays.asList(new SearchCriterion("parentProvider.rid", providerRid, FilterOperator.eq)),
				InsProvider.class, "parentProvider");
		//if it has children then add them to the filters
		if (!CollectionUtil.isCollectionEmpty(childrenProviders)) {
			filters.get(0).setJunctionOperator(JunctionOperator.Or);
			for (InsProvider ip : childrenProviders) {
				ip.setIsActive(newIsActive);
				filters.add(new SearchCriterion("insProvider.rid", ip.getRid(), FilterOperator.eq, JunctionOperator.Or));
			}
		}
		Set<InsProviderPlan> plans = new HashSet<>(
				insProviderPlanService.findProviderPlans(filters, null, "insProvider", "insCoverageDetailList"));
		List<InsCoverageDetail> details = new ArrayList<>();
		for (InsProviderPlan ipp : plans) {
			ipp.setIsActive(newIsActive);
			details.addAll(ipp.getInsCoverageDetailList());
			for (InsCoverageDetail icd : ipp.getInsCoverageDetailList()) {
				icd.setIsActive(newIsActive);
			}
		}
		getRepository().save(childrenProviders);
		insProviderPlanService.updateInsProviderPlan(new ArrayList<>(plans));
		coverageDetailService.updateInsCoverageDetail(details);
	}

	@PreAuthorize("hasAuthority('" + EhopeRights.ACTIVATE_INS_PROVIDER + "')")
	public InsProvider activateInsProvider(InsProvider insProvider) {
		insProvider.setIsActive(Boolean.TRUE);
		return getRepository().save(insProvider);
	}

	@PreAuthorize("hasAuthority('" + EhopeRights.DEACTIVATE_INS_PROVIDER + "')")
	public InsProvider deactivateInsProvider(InsProvider insProvider) {
		insProvider.setIsActive(Boolean.FALSE);
		return getRepository().save(insProvider);
	}

	public List<InsProvider> getClients(TestDestinationType type, ClientPurpose purpose, Long branchRidToExclude) {
		InsProvider localTenant = getRepository().findOne(
				Arrays.asList(
						new SearchCriterion("insuranceTenant.rid", SecurityUtil.getCurrentUser().getTenantId(), FilterOperator.eq),
						new SearchCriterion("insuranceType.code", InsuranceType.LAB_NETWORK.getValue(), FilterOperator.eq)),
				InsProvider.class);
		if (localTenant == null) {
			throw new BusinessException("No client is associated with this tenant!", "noClientWithTenant", ErrorSeverity.ERROR);
		}

		Long branchRid = SecurityUtil.getCurrentUser().getBranchId();

		List<SearchCriterion> filters = new ArrayList<SearchCriterion>();
		List<InsProvider> clients = null;
		switch (type) {
			case ACCULAB:
				filters.add(new SearchCriterion("parentProvider.insuranceTenant", null, FilterOperator.isnotnull));
				filters.add(new SearchCriterion("insuranceType.code", InsuranceType.LAB_NETWORK.getValue(), FilterOperator.eq));
				filters.add(new SearchCriterion("parentProvider.rid", localTenant.getRid(), FilterOperator.neq));
				clients = getRepository().find(filters, InsProvider.class, "insuranceBranch");
				break;
			case EXTERNAL:
				clients = getExternalClients();
				break;
			case LOCAL:
				filters.add(new SearchCriterion("parentProvider.insuranceTenant", null, FilterOperator.isnotnull));
				filters.add(new SearchCriterion("insuranceType.code", InsuranceType.LAB_NETWORK.getValue(), FilterOperator.eq));
				filters.add(new SearchCriterion("parentProvider.rid", localTenant.getRid(), FilterOperator.eq));
				switch (purpose) {
					case DESTINATION:
						if (branchRidToExclude != null) {
							filters.add(new SearchCriterion("rid", branchRidToExclude, FilterOperator.neq));
						}
						break;
					case SOURCE:
						if (branchRid != null) {
							filters.add(new SearchCriterion("insuranceBranch.rid", branchRid, FilterOperator.eq));
						}
						break;
				}
				clients = getRepository().find(filters, InsProvider.class, "insuranceBranch");
				break;
			case WORKBENCH:
			default:
				break;
		}

		return clients;
	}

	public List<InsProvider> getAllClients(List<String> destinationTypeCodeList) {
		List<InsProvider> clients = new ArrayList<InsProvider>();
		List<LkpTestDestinationType> destinationTypesList = new ArrayList<LkpTestDestinationType>();
		List<String> tempStringList = new ArrayList<String>();

		if (CollectionUtil.isCollectionEmpty(destinationTypeCodeList)) {
			destinationTypesList.addAll(lkpService.findAnyLkp(
					Arrays.asList(new SearchCriterion("code", TestDestinationType.WORKBENCH.getValue(), FilterOperator.neq)),
					LkpTestDestinationType.class, null));

			tempStringList = destinationTypesList.stream().map(dtl -> dtl.getCode()).collect(Collectors.toList());
		} else {
			tempStringList.addAll(destinationTypeCodeList);
		}

		for (String destination : tempStringList) {
			clients.addAll(getClients(TestDestinationType.getByValue(destination), ClientPurpose.DESTINATION,
					SecurityUtil.getCurrentUser().getBranchId()).stream()
																.collect(Collectors.toList()));
		}
		return clients;
	}

	private List<InsProvider> getExternalClients() {
		List<SearchCriterion> filters = Arrays.asList(
				new SearchCriterion("insuranceType.code", InsuranceType.LAB.getValue(), FilterOperator.eq));
		List<InsProvider> labs = getRepository().find(filters, InsProvider.class);

		List<SearchCriterion> networkFilters = Arrays.asList(
				new SearchCriterion("parentProvider", null, FilterOperator.isnotnull),
				new SearchCriterion("parentProvider.insuranceTenant", null, FilterOperator.isnull),
				new SearchCriterion("insuranceType.code", InsuranceType.LAB_NETWORK.getValue(), FilterOperator.eq));
		List<InsProvider> clients = getRepository().find(networkFilters, InsProvider.class);

		clients.addAll(labs);

		return clients;
	}

	public void loadTeamLabInsurances(MultipartFile excel) {
		Workbook workbook = ExcelUtil.getWorkbookFromExcel(excel);
		InsProvider medNet = null;
		InsProvider natHealth = null;
		InsProvider omniCare = null;
		InsProvider medService = null;
		InsProvider globeMed = null;

		BillPriceList mohL2008 = priceListService.findOne(Arrays.asList(new SearchCriterion("name", "moh l 2008", FilterOperator.contains)),
				BillPriceList.class);
		LkpInsuranceType tpaType = lkpService.findOneAnyLkp(
				Arrays.asList(new SearchCriterion("code", InsuranceType.TPA.getValue(), FilterOperator.eq)), LkpInsuranceType.class);
		LkpInsuranceType insuranceType = lkpService.findOneAnyLkp(
				Arrays.asList(new SearchCriterion("code", InsuranceType.INSURANCE.getValue(), FilterOperator.eq)), LkpInsuranceType.class);
		LkpCountry jordan = (LkpCountry) ReflectionUtil.getRepository("LkpCountry").findOne(141L);
		Sheet sheet = null;
		Iterator<Row> rowIterator = null;
		Row row = null;
		try {
			for (int idx = 0; idx <= 6; idx++) {
				sheet = workbook.getSheetAt(idx);
				rowIterator = sheet.iterator();

				rowIterator.next();
				while (rowIterator.hasNext()) {
					row = rowIterator.next();
					if (ExcelUtil.isRowEmpty(row)) {
						continue;
					}
					InsProvider provider = new InsProvider();
					provider.setPriceList(mohL2008);
					provider.setCoveragePercentage(BigDecimal.ZERO);
					provider.setIsActive(Boolean.TRUE);
					provider.setIsSimple(Boolean.FALSE);
					provider.setIsAutoApprove(Boolean.TRUE);
					provider.setLkpCountry(jordan);
					TransField tf = new TransField();
					Cell nameCell = row.getCell(1);
					String value = (String) ExcelUtil.getDataFromCell(nameCell, String.class, null);
					String toReplace = null;
					if (sheet.getSheetName().equals("Mednet")) {
						provider.setParentProvider(medNet);
						toReplace = "Mednet";
					} else if (sheet.getSheetName().equals("Nathealth")) {
						provider.setParentProvider(natHealth);
						toReplace = "Nathealth";
					} else if (sheet.getSheetName().equals("Omni Care")) {
						provider.setParentProvider(omniCare);
						toReplace = "Omni Care";
					} else if (sheet.getSheetName().equals("MedSevice")) {
						provider.setParentProvider(medService);
						toReplace = "MedService";
					} else if (sheet.getSheetName().equals("GlobeMed")) {
						provider.setParentProvider(globeMed);
						toReplace = "Globe Med";
					}
					if (sheet.getSheetName().equals("Insurance")) {
						provider.setInsuranceType(insuranceType);
					} else {
						provider.setInsuranceType(tpaType);
					}
					if (toReplace != null) {
						value = value.replaceAll("(?i)" + toReplace, "").replaceAll("-", "").trim();
					}

					tf.put("ar_jo", value);
					tf.put("en_us", value);
					provider.setName(tf);
					Cell codeCell = row.getCell(0);
					provider.setCode((String) ExcelUtil.getDataFromCell(codeCell, String.class, null));

					Cell discountCell = row.getCell(4);
					provider.setDiscount(new BigDecimal((String) ExcelUtil.getDataFromCell(discountCell, String.class, null)));

					provider = createInsProvider(provider);
					if (nameCell.getStringCellValue().equals("Mednet")) {
						medNet = provider;
					} else if (nameCell.getStringCellValue().equals("NatHealth")) {
						natHealth = provider;
					} else if (nameCell.getStringCellValue().equals("Omni Care")) {
						omniCare = provider;
					} else if (nameCell.getStringCellValue().equals("MedService")) {
						medService = provider;
					} else if (nameCell.getStringCellValue().equals("GlobeMed")) {
						globeMed = provider;
					}
				}

			}
		} catch (Exception e) {
			System.out.println(e);
		}

	}
}

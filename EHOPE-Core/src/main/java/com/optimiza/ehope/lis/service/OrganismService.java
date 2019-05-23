package com.optimiza.ehope.lis.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.optimiza.core.base.helper.SearchCriterion;
import com.optimiza.core.base.helper.SearchCriterion.FilterOperator;
import com.optimiza.core.base.service.GenericService;
import com.optimiza.core.common.data.model.TransField;
import com.optimiza.core.common.helper.FilterablePageRequest;
import com.optimiza.core.lkp.model.ComTenantLanguage;
import com.optimiza.core.lkp.service.ComTenantLanguageService;
import com.optimiza.core.lkp.service.LkpService;
import com.optimiza.ehope.lis.helper.EhopeRights;
import com.optimiza.ehope.lis.lkp.model.LkpOrganismType;
import com.optimiza.ehope.lis.model.Organism;
import com.optimiza.ehope.lis.repo.OrganismRepo;
import com.optimiza.ehope.lis.util.ExcelUtil;

/**
 * OrganismService.java
 * 
 * @author Murad Poladian <mpoladian@optimizasolutions.com>
 * @since Dec/3/2018
 * 
 */
@Service("OrganismService")
public class OrganismService extends GenericService<Organism, OrganismRepo> {

	@Autowired
	private OrganismRepo repo;

	@Autowired
	private LkpService lkpService;

	@Autowired
	private ComTenantLanguageService tenantLanguageService;

	@Override
	protected OrganismRepo getRepository() {
		return repo;
	}

	@PreAuthorize("hasAuthority('" + EhopeRights.VIEW_ORGANISM + "')")
	public Page<Organism> findOrganismPage(FilterablePageRequest filterablePageRequest) {
		return repo.find(filterablePageRequest.getFilters(), filterablePageRequest.getPageRequest(), Organism.class, "type");
	}

	@PreAuthorize("hasAuthority('" + EhopeRights.ADD_ORGANISM + "')")
	public Organism addOrganism(Organism organism) {
		return repo.save(organism);
	}

	@PreAuthorize("hasAuthority('" + EhopeRights.UPD_ORGANISM + "')")
	public Organism editOrganism(Organism organism) {
		return repo.save(organism);
	}

	@PreAuthorize("hasAuthority('" + EhopeRights.DEL_ORGANISM + "')")
	public void deleteOrganism(Long organismId) {
		repo.delete(organismId);
	}

	@PreAuthorize("hasAuthority('" + EhopeRights.VIEW_ORGANISM + "')")
	public Organism findOneOrganism(Long organismRid) {
		return repo.findOne(Arrays.asList(new SearchCriterion("rid", organismRid, FilterOperator.eq)), Organism.class, "type");
	}

	public void importOrganisms(MultipartFile excel) {
		Workbook workbook = ExcelUtil.getWorkbookFromExcel(excel);
		Sheet sheet = workbook.getSheetAt(0);
		Iterator<Row> rowIterator = sheet.iterator();
		rowIterator.next();//skip header

		List<LkpOrganismType> organismTypes = lkpService.findAnyLkp(new ArrayList<>(), LkpOrganismType.class, null);
		Map<String, LkpOrganismType> organismTypeMap = new HashMap<String, LkpOrganismType>();
		for (LkpOrganismType organismType : organismTypes) {
			organismTypeMap.put(organismType.getCode(), organismType);
		}

		List<ComTenantLanguage> languages = tenantLanguageService.findTenantExcelLanguages();

		while (rowIterator.hasNext()) {
			Row row = rowIterator.next();
			String code = ExcelUtil.getStringFromCell(row.getCell(1));
			String name = ExcelUtil.getStringFromCell(row.getCell(2));
			String type = ExcelUtil.getStringFromCell(row.getCell(3));

			LkpOrganismType organismType = organismTypeMap.get(type);
			if (organismType == null) {
				TransField nameField = new TransField();
				for (ComTenantLanguage lang : languages) {
					nameField.put(lang.getComLanguage().getLocale(), type);
				}
				organismType = new LkpOrganismType();
				organismType.setCode(type);
				organismType.setName(nameField);
				organismType.setDescription(nameField);
				organismType = (LkpOrganismType) lkpService.createTenantedLkp(LkpOrganismType.class, organismType);
				organismTypeMap.put(type, organismType);
			}

			Organism organism = new Organism();
			organism.setCode(code);
			organism.setName(name);
			organism.setType(organismType);
			addOrganism(organism);
		}
	}

}
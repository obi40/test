package com.optimiza.ehope.lis.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import com.optimiza.core.base.helper.SearchCriterion;
import com.optimiza.core.base.helper.SearchCriterion.FilterOperator;
import com.optimiza.core.base.service.GenericService;
import com.optimiza.core.common.business.exception.BusinessException;
import com.optimiza.core.common.business.exception.BusinessException.ErrorSeverity;
import com.optimiza.core.common.helper.FilterablePageRequest;
import com.optimiza.ehope.lis.helper.EhopeRights;
import com.optimiza.ehope.lis.model.AntiMicrobial;
import com.optimiza.ehope.lis.repo.AntiMicrobialRepo;

/**
 * AntiMicrobialService.java
 * 
 * @author Murad Poladian <mpoladian@optimizasolutions.com>
 * @since Dec/3/2018
 * 
 */
//TODO check the functions below
@Service("AntiMicrobialService")
public class AntiMicrobialService extends GenericService<AntiMicrobial, AntiMicrobialRepo> {

	@Autowired
	private AntiMicrobialRepo repo;

	@Override
	protected AntiMicrobialRepo getRepository() {
		return repo;
	}

	@PreAuthorize("hasAuthority('" + EhopeRights.VIEW_ANTI_MICROBIAL + "')")
	public Page<AntiMicrobial> findAntiMicrobialPage(FilterablePageRequest filterablePageRequest) {
		Page<AntiMicrobial> page = repo.find(filterablePageRequest.getFilters(), filterablePageRequest.getPageRequest(),
				AntiMicrobial.class);
		if (page.getTotalElements() == 0) {
			return page;
		}
		List<AntiMicrobial> antiMicrobials = repo	.find(Arrays.asList(new SearchCriterion("rid",
				page.getContent().stream().map(AntiMicrobial::getRid).collect(Collectors.toList()), FilterOperator.in)),
				AntiMicrobial.class,
				filterablePageRequest.getSortObject(),
				"antiMicrobialTypeMappings.type").stream().distinct()
													.collect(Collectors.toList());
		Page<AntiMicrobial> antiMicrobialsPage = new PageImpl<>(antiMicrobials, filterablePageRequest.getPageRequest(),
				page.getTotalElements());
		return antiMicrobialsPage;
	}

	@PreAuthorize("hasAuthority('" + EhopeRights.ADD_ANTI_MICROBIAL + "')")
	public AntiMicrobial addAntiMicrobial(AntiMicrobial antiMicrobial) {
		checkCodeAndNameUnique(antiMicrobial);
		return repo.save(antiMicrobial);
	}

	@PreAuthorize("hasAuthority('" + EhopeRights.UPD_ANTI_MICROBIAL + "')")
	public AntiMicrobial editAntiMicrobial(AntiMicrobial antiMicrobial) {
		checkCodeAndNameUnique(antiMicrobial);
		return repo.save(antiMicrobial);
	}

	private void checkCodeAndNameUnique(AntiMicrobial antiMicrobial) {
		List<SearchCriterion> codeFilters = new ArrayList<SearchCriterion>();
		codeFilters.add(new SearchCriterion("code", antiMicrobial.getCode(), FilterOperator.eq));

		List<SearchCriterion> nameFilters = new ArrayList<SearchCriterion>();
		nameFilters.add(new SearchCriterion("name", antiMicrobial.getName(), FilterOperator.eq));

		if (antiMicrobial.getRid() != null) {
			codeFilters.add(new SearchCriterion("rid", antiMicrobial.getRid(), FilterOperator.neq));
			nameFilters.add(new SearchCriterion("rid", antiMicrobial.getRid(), FilterOperator.neq));
		}
		AntiMicrobial existingAntiMicrobial = repo.findOne(codeFilters, AntiMicrobial.class);
		if (existingAntiMicrobial != null) {
			throw new BusinessException("Anti-Microbial with same CODE exists!", "antiMicrobialCodeExists", ErrorSeverity.ERROR);
		}
		existingAntiMicrobial = repo.findOne(nameFilters, AntiMicrobial.class);
		if (existingAntiMicrobial != null) {
			throw new BusinessException("Anti-Microbial with same NAME exists!", "antiMicrobialNameExists", ErrorSeverity.ERROR);
		}
	}

	@PreAuthorize("hasAuthority('" + EhopeRights.DEL_ANTI_MICROBIAL + "')")
	public void deleteAntiMicrobial(Long antiMicrobialRid) {
		repo.delete(antiMicrobialRid);
	}

	@PreAuthorize("hasAuthority('" + EhopeRights.VIEW_ANTI_MICROBIAL + "')")
	public AntiMicrobial findOneAntiMicrobial(Long antiMicrobialRid) {
		return repo.findOne(Arrays.asList(new SearchCriterion("rid", antiMicrobialRid, FilterOperator.eq)), AntiMicrobial.class);
	}

}
package com.optimiza.ehope.lis.service;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.persistence.FlushModeType;

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
import com.optimiza.ehope.lis.model.BillClassification;
import com.optimiza.ehope.lis.model.LabSection;
import com.optimiza.ehope.lis.repo.LabSectionRepo;

@Service("LabSectionService")
public class LabSectionService extends GenericService<LabSection, LabSectionRepo> {

	@Autowired
	private LabSectionRepo repo;
	@Autowired
	private EntityManager entityManager;

	@Autowired
	BillClassificationService billClassificationService;

	@Override
	protected LabSectionRepo getRepository() {
		return repo;
	}

	/**
	 * To be used outside the section view page
	 * 
	 * @param searchQuery
	 * @return List
	 */
	public List<LabSection> getSectionList(String searchQuery) {
		List<LabSection> sections = repo.getSectionList(searchQuery);
		return sections;
	}

	@PreAuthorize("hasAuthority('" + EhopeRights.VIEW_SECTION + "')")
	public Page<LabSection> findSectionPage(FilterablePageRequest filterablePageRequest) {
		Page<LabSection> page = getRepository().find(filterablePageRequest.getFilters(), filterablePageRequest.getPageRequest(),
				LabSection.class);
		if (page.getTotalElements() == 0) {
			return page;
		}
		List<LabSection> sections = getRepository()	.find(
				Arrays.asList(new SearchCriterion("rid", page.getContent().stream().map(LabSection::getRid).collect(Collectors.toList()),
						FilterOperator.in)),
				LabSection.class, filterablePageRequest.getSortObject(), "type", "classification").stream().distinct()
													.collect(Collectors.toList());
		Page<LabSection> sectionsPage = new PageImpl<>(sections, filterablePageRequest.getPageRequest(), page.getTotalElements());
		return sectionsPage;
	}

	@PreAuthorize("hasAuthority('" + EhopeRights.ADD_SECTION + "')")
	public LabSection addSection(LabSection newSection) {
		entityManager.setFlushMode(FlushModeType.AUTO);

		Set<BillClassification> classificationList = newSection.getClassification();
		Iterator<BillClassification> classificationIterator = classificationList.iterator();
		BillClassification newClassification = classificationIterator.next();
		newClassification = billClassificationService.findOne(
				Arrays.asList(new SearchCriterion("rid", newClassification.getRid(), FilterOperator.eq)),
				BillClassification.class);

		if (newClassification.getSection() != null) {
			throw new BusinessException("Selected bill classification has a section!", "billClassificationHasASection",
					ErrorSeverity.ERROR);
		}

		Long newRank = newSection.getRank();
		Long maxRanking = repo.getMaxRank();
		if (maxRanking == null) {
			maxRanking = 1L;
		} else {
			maxRanking += 1L;
		}

		if (newRank == null) {
			newRank = maxRanking;
			newSection.setRank(maxRanking);
		}

		LabSection existingSection = repo.findOne(Arrays.asList(new SearchCriterion("rank", newRank, FilterOperator.eq)), LabSection.class);

		Long maxRank = repo.getMaxRank();
		maxRank = (maxRank == null) ? 1 : maxRank;

		if (existingSection != null) {

			existingSection.setRank(maxRank + 1L);
			existingSection = repo.save(existingSection);
			//Manual flush is used because of data integrity constraint on section ranking.
			entityManager.flush();

			newSection = repo.save(newSection);
		} else {
			newSection.setRank(maxRank + 1L);

			newSection = repo.save(newSection);
		}

		newClassification.setSection(newSection);
		billClassificationService.editBillClassification(newClassification);
		return newSection;
	}

	@PreAuthorize("hasAuthority('" + EhopeRights.UPD_SECTION + "')")
	public LabSection editSection(LabSection updatedSection) {
		entityManager.setFlushMode(FlushModeType.AUTO);

		Set<BillClassification> classificationList = updatedSection.getClassification();
		Iterator<BillClassification> classificationIterator = classificationList.iterator();
		BillClassification newClassification = classificationIterator.next();
		newClassification = billClassificationService.findOne(
				Arrays.asList(new SearchCriterion("rid", newClassification.getRid(), FilterOperator.eq)),
				BillClassification.class);

		if (newClassification.getSection() != null && !newClassification.getSection().equals(updatedSection)) {
			throw new BusinessException("Selected bill classification has a section!", "billClassificationHasASection",
					ErrorSeverity.ERROR);
		}

		Long oldRank = updatedSection.getOldRank();
		Long newRank = updatedSection.getRank();
		LabSection existingSection = repo.findOne(Arrays.asList(new SearchCriterion("rank", newRank, FilterOperator.eq)), LabSection.class);

		if (existingSection != null && !existingSection.equals(updatedSection)) {
			existingSection.setRank(-1L);
			existingSection = repo.save(existingSection);
			entityManager.flush();
			updatedSection = repo.save(updatedSection);
			//Manual flush is used because of data integrity constraint on section ranking.
			entityManager.flush();
			existingSection.setRank(oldRank);
			existingSection = repo.save(existingSection);
		} else {

			newClassification.setSection(updatedSection);
			billClassificationService.editBillClassification(newClassification);

			classificationList.clear();
			classificationList.add(newClassification);
			updatedSection.setClassification(classificationList);

			updatedSection = repo.save(updatedSection);
		}

		return updatedSection;

	}

	@PreAuthorize("hasAuthority('" + EhopeRights.DEL_SECTION + "')")
	public void deleteSection(Long sectionId) {

		Set<BillClassification> classificationList = repo.getOne(sectionId).getClassification();
		Iterator<BillClassification> classificationIterator = classificationList.iterator();
		BillClassification updatedClassification = classificationIterator.next();
		updatedClassification = billClassificationService.findOne(
				Arrays.asList(new SearchCriterion("rid", updatedClassification.getRid(), FilterOperator.eq)),
				BillClassification.class);
		billClassificationService.getRepository().delete(updatedClassification);
		repo.delete(sectionId);
	}

}

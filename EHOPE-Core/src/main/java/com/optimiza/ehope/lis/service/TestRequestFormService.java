package com.optimiza.ehope.lis.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import com.optimiza.core.base.helper.SearchCriterion;
import com.optimiza.core.base.helper.SearchCriterion.FilterOperator;
import com.optimiza.core.base.service.GenericService;
import com.optimiza.core.common.business.exception.BusinessException;
import com.optimiza.core.common.business.exception.BusinessException.ErrorSeverity;
import com.optimiza.ehope.lis.helper.EhopeRights;
import com.optimiza.ehope.lis.model.LabSection;
import com.optimiza.ehope.lis.model.TestDefinition;
import com.optimiza.ehope.lis.model.TestRequestForm;
import com.optimiza.ehope.lis.model.TestRequestFormTestDefinition;
import com.optimiza.ehope.lis.repo.TestRequestFormRepo;

@Service("TestRequestFormService")
public class TestRequestFormService extends GenericService<TestRequestForm, TestRequestFormRepo> {

	@Autowired
	private TestRequestFormTestDefinitionService testRequestFormTestDefinitionService;

	@Autowired
	private TestRequestFormRepo repo;

	@Override
	protected TestRequestFormRepo getRepository() {
		return repo;
	}

	@PreAuthorize("hasAuthority('" + EhopeRights.ADD_REQUEST_FORM + "')")
	public TestRequestForm addTestRequestForm(TestRequestForm testRequestForm) {
		return repo.save(testRequestForm);
	}

	@PreAuthorize("hasAuthority('" + EhopeRights.UPD_REQUEST_FORM + "')")
	public TestRequestForm editTestRequestForm(TestRequestForm testRequestForm) {
		return repo.save(testRequestForm);
	}

	@PreAuthorize("hasAuthority('" + EhopeRights.VIEW_REQUEST_FORM + "')")
	public List<TestRequestForm> getTestRequestForms(List<SearchCriterion> filters) {
		return repo.find(filters, TestRequestForm.class, new Sort(Direction.DESC, "rid"));
	}

	@PreAuthorize("hasAuthority('" + EhopeRights.UPD_REQUEST_FORM + "')")
	public void saveRequestFormTests(TestRequestForm requestForm) {
		List<TestRequestFormTestDefinition> requestFormTestList = requestForm.getTestRequestFormTestList();
		List<TestRequestFormTestDefinition> oldRequestFormTestList = testRequestFormTestDefinitionService.getByRequestForm(requestForm);
		oldRequestFormTestList.forEach(oldRequestFormTest ->
			{
				if (!requestFormTestList.contains(oldRequestFormTest)) {
					testRequestFormTestDefinitionService.deleteTestRequestFormTestDefinition(oldRequestFormTest);
				}
			});
		requestFormTestList.forEach(requestFormTest ->
			{
				if (!oldRequestFormTestList.contains(requestFormTest)) {
					requestFormTest.setTestRequestForm(requestForm);
					testRequestFormTestDefinitionService.addTestRequestFormTestDefinition(requestFormTest);
				}
			});
	}

	@PreAuthorize("hasAuthority('" + EhopeRights.DEACTIVATE_REQUEST_FORM + "')")
	public TestRequestForm deactivateTestRequestForm(Long rid) throws BusinessException {
		TestRequestForm fetchedTestRequestForm = repo.getOne(rid);

		if (!fetchedTestRequestForm.getIsActive()) {
			throw new BusinessException("This test request form is already inactive!", "testRequestFormAlreadyInactive",
					ErrorSeverity.ERROR);
		}

		fetchedTestRequestForm.setIsActive(false);

		return repo.save(fetchedTestRequestForm);
	}

	@PreAuthorize("hasAuthority('" + EhopeRights.ACTIVATE_REQUEST_FORM + "')")
	public TestRequestForm activateTestRequestForm(Long rid) throws BusinessException {
		TestRequestForm fetchedTestRequestForm = repo.getOne(rid);

		if (fetchedTestRequestForm.getIsActive()) {
			throw new BusinessException("This test request form is already inactive!", "testRequestFormAlreadyActive",
					ErrorSeverity.ERROR);
		}

		fetchedTestRequestForm.setIsActive(true);

		return repo.save(fetchedTestRequestForm);
	}

	@PreAuthorize("hasAuthority('" + EhopeRights.VIEW_REQUEST_FORM + "')")
	public List<LabSection> getRequestFormTests(Long testRequestFormRid) {
		return getRequestFormTestsHelper(testRequestFormRid, Boolean.FALSE);
	}

	@PreAuthorize("hasAuthority('" + EhopeRights.VIEW_REQUEST_FORM + "')")
	public List<LabSection> getRequestFormTestsWithDestinations(Long testRequestFormRid) {
		return getRequestFormTestsHelper(testRequestFormRid, Boolean.TRUE);
	}

	private List<LabSection> getRequestFormTestsHelper(Long testRequestFormRid, Boolean fetchDestinations) {
		List<SearchCriterion> filters = new ArrayList<SearchCriterion>();
		filters.add(new SearchCriterion("testRequestForm", testRequestFormRid, FilterOperator.eq));
		filters.add(new SearchCriterion("testDefinition.isActive", true, FilterOperator.eq));
		List<String> joinList = new ArrayList<String>();
		joinList.add("testDefinition.section");
		joinList.add("testRequestForm");
		if (fetchDestinations) {
			joinList.add("testDefinition.destinations.source.insuranceBranch");
		}
		String[] joinArray = new String[joinList.size()];
		joinArray = joinList.toArray(joinArray);
		List<TestRequestFormTestDefinition> requestFormTests = testRequestFormTestDefinitionService.find(filters,
				TestRequestFormTestDefinition.class, joinArray);
		List<LabSection> sections = new ArrayList<LabSection>();
		for (TestRequestFormTestDefinition requestFormTest : requestFormTests) {
			LabSection section = requestFormTest.getTestDefinition().getSection();
			if (!sections.contains(section)) {
				section.setTestDefinitionList(new ArrayList<TestDefinition>());
				sections.add(section);
			}
			List<TestDefinition> tests = section.getTestDefinitionList();
			tests.add(requestFormTest.getTestDefinition());
		}
		return sections;
	}

}

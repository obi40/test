package com.optimiza.ehope.lis.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.optimiza.core.base.helper.SearchCriterion;
import com.optimiza.core.base.helper.SearchCriterion.FilterOperator;
import com.optimiza.core.base.service.GenericService;
import com.optimiza.core.common.util.ReflectionUtil;
import com.optimiza.ehope.lis.lkp.helper.ResultValueType;
import com.optimiza.ehope.lis.model.ActualAntiMicrobial;
import com.optimiza.ehope.lis.model.ActualOrganism;
import com.optimiza.ehope.lis.model.AmendedActualAntiMicrobial;
import com.optimiza.ehope.lis.model.AmendedActualOrganism;
import com.optimiza.ehope.lis.model.AmendedActualResult;
import com.optimiza.ehope.lis.model.LabTestActualResult;
import com.optimiza.ehope.lis.repo.AmendedActualResultRepo;

@Service("AmendedActualResultService")
public class AmendedActualResultService extends GenericService<AmendedActualResult, AmendedActualResultRepo> {

	@Autowired
	private AmendedActualResultRepo repo;

	@Autowired
	private LabTestActualResultService actualResultService;

	@Autowired
	private AmendedActualAntiMicrobialService amendedActualAntiMicrobialService;

	@Autowired
	private AmendedActualOrganismService amendedActualOrganismService;

	@Override
	protected AmendedActualResultRepo getRepository() {
		return repo;
	}

	public AmendedActualResult createAmendedActualResult(LabTestActualResult actualResult) {
		List<String> joins = new ArrayList<>();
		ResultValueType resultType = ResultValueType.valueOf(actualResult.getLabResult().getResultValueType().getCode());
		switch (resultType) {
			default:
			case QN:
			case QN_QL:
			case QN_SC:
			case NAR:
				break;
			case CE:
				joins.add("testCodedResult");
				break;
			case ORG:
				joins.add("organismDetection");
				joins.add("actualAntiMicrobials");
				joins.add("actualOrganisms");
				break;
		}
		String[] joinArray = new String[joins.size()];
		LabTestActualResult oldActualResult = actualResultService.findOne(
				Arrays.asList(new SearchCriterion("rid", actualResult.getRid(), FilterOperator.eq)),
				LabTestActualResult.class, joins.toArray(joinArray));
		oldActualResult = ReflectionUtil.unproxy(oldActualResult);
		AmendedActualResult amendedActualResult = new AmendedActualResult();
		BeanUtils.copyProperties(oldActualResult, amendedActualResult, "rid", "tenantId", "creationDate", "createdBy");
		amendedActualResult.setActualResult(oldActualResult);
		amendedActualResult = repo.save(amendedActualResult);

		if (resultType.equals(ResultValueType.ORG)) {
			for (ActualAntiMicrobial actualAntiMicrobial : oldActualResult.getActualAntiMicrobials()) {
				AmendedActualAntiMicrobial amendedActualAntiMicrobial = new AmendedActualAntiMicrobial();
				BeanUtils.copyProperties(actualAntiMicrobial, amendedActualAntiMicrobial, "rid", "tenantId", "creationDate", "createdBy");
				amendedActualAntiMicrobial.setAmendedActualResult(amendedActualResult);
				amendedActualAntiMicrobialService.createAmendedActualAntiMicrobial(amendedActualAntiMicrobial);
			}
			for (ActualOrganism actualOrganism : oldActualResult.getActualOrganisms()) {
				AmendedActualOrganism amendedActualOrganism = new AmendedActualOrganism();
				BeanUtils.copyProperties(actualOrganism, amendedActualOrganism, "rid", "tenantId", "creationDate", "createdBy");
				amendedActualOrganism.setAmendedActualResult(amendedActualResult);
				amendedActualOrganismService.createAmendedActualOrganism(amendedActualOrganism);
			}
		}
		return amendedActualResult;
	}

}

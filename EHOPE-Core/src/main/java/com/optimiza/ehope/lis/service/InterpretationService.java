package com.optimiza.ehope.lis.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.optimiza.core.base.service.GenericService;
import com.optimiza.core.common.business.exception.BusinessException;
import com.optimiza.core.common.business.exception.BusinessException.ErrorSeverity;
import com.optimiza.ehope.lis.lkp.helper.SectionType;
import com.optimiza.ehope.lis.model.Interpretation;
import com.optimiza.ehope.lis.model.TestDefinition;
import com.optimiza.ehope.lis.repo.InterpretationRepo;
import com.optimiza.ehope.lis.util.NumberUtil;

@Service("InterpretationService")
public class InterpretationService extends GenericService<Interpretation, InterpretationRepo> {

	@Autowired
	private InterpretationRepo repo;

	@Override
	protected InterpretationRepo getRepository() {
		return repo;
	}

	public List<Interpretation> saveInterpretationList(List<Interpretation> interpretationList, TestDefinition testDefinition) {
		List<Interpretation> savedInterpretationList = new ArrayList<Interpretation>();

		SectionType sectionType = null;
		if (testDefinition.getSection().getType() != null) {
			sectionType = SectionType.valueOf(testDefinition.getSection().getType().getCode());
		}
		for (Interpretation interpretation : interpretationList) {
			if (!sectionType.equals(SectionType.ALLERGY)) {
				if (interpretation.getRid() != null) {
					repo.delete(interpretation);
				}
			} else {
				if (interpretation.getMarkedForDeletion()) {
					if (interpretation.getRid() != null) {
						repo.delete(interpretation);
					}
				} else {
					validateValues(interpretation, testDefinition.getAllergyDecimals());
					interpretation.setTest(testDefinition);
					savedInterpretationList.add(repo.save(interpretation));
				}
			}
		}

		return savedInterpretationList;
	}

	private void validateValues(Interpretation interpretation, Integer decimals) {
		if (interpretation.getMinConcentrationValue() == null && interpretation.getMaxConcentrationValue() == null) {
			throw new BusinessException("\"Min Concentration Value\" or \"Max Concentration Value\" should be filled",
					"minOrMaxConcentrationValueShouldBeFilled",
					ErrorSeverity.ERROR);
		} else if (interpretation.getMinConcentrationValue() != null && interpretation.getMaxConcentrationValue() != null) {
			BigDecimal minValue = NumberUtil.getValueRespectingComparator(interpretation.getMinConcentrationComparator(),
					interpretation.getMinConcentrationValue(), decimals);
			BigDecimal maxValue = NumberUtil.getValueRespectingComparator(interpretation.getMaxConcentrationComparator(),
					interpretation.getMaxConcentrationValue(), decimals);
			if (maxValue.compareTo(minValue) < 0) {
				throw new BusinessException("Min value should be less than max value", "minLessThanMax", ErrorSeverity.ERROR);
			}
		}
	}

}

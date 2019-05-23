package com.optimiza.ehope.lis.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.optimiza.core.base.helper.SearchCriterion;
import com.optimiza.core.base.helper.SearchCriterion.FilterOperator;
import com.optimiza.core.base.service.GenericService;
import com.optimiza.core.common.business.exception.BusinessException;
import com.optimiza.core.common.business.exception.BusinessException.ErrorSeverity;
import com.optimiza.core.common.helper.AgeWrapper;
import com.optimiza.core.common.util.StringUtil;
import com.optimiza.core.lkp.model.LkpGender;
import com.optimiza.ehope.lis.helper.Comparator;
import com.optimiza.ehope.lis.lkp.helper.ResultValueType;
import com.optimiza.ehope.lis.lkp.model.LkpAgeUnit;
import com.optimiza.ehope.lis.model.EmrPatientInfo;
import com.optimiza.ehope.lis.model.TestNormalRange;
import com.optimiza.ehope.lis.model.TestResult;
import com.optimiza.ehope.lis.repo.TestNormalRangeRepo;
import com.optimiza.ehope.lis.util.NumberUtil;

@Service("TestNormalRangeService")
public class TestNormalRangeService extends GenericService<TestNormalRange, TestNormalRangeRepo> {

	@Autowired
	private TestNormalRangeRepo repo;

	@Override
	protected TestNormalRangeRepo getRepository() {
		return repo;
	}

	public TestNormalRange addNormalRange(TestNormalRange normalRange) {
		return repo.save(normalRange);
	}

	public TestNormalRange editNormalRange(TestNormalRange normalRange) {
		return repo.save(normalRange);
	}

	public void deleteNormalRange(TestNormalRange normalRange) {
		repo.delete(normalRange);
	}

	public TestNormalRange getIncorrectNormalRange(String testCode, String resultCode, BigDecimal min, BigDecimal max, LkpGender gender,
			String ageFromComparator, Integer ageFromValue, LkpAgeUnit ageFromUnit,
			String ageToComparator, Integer ageToValue, LkpAgeUnit ageToUnit) {
		return repo.getIncorrectNormalRange(testCode, resultCode, min, max, gender,
				ageFromComparator, ageFromValue, ageFromUnit,
				ageToComparator, ageToValue, ageToUnit);
	}

	public void saveNormalRanges(List<TestNormalRange> normalRanges, TestResult testResult) {
		String code = testResult.getResultValueType().getCode();
		ResultValueType resultType = ResultValueType.valueOf(code);
		//narrative and organism types do not have normal ranges
		if (resultType.equals(ResultValueType.NAR) || resultType.equals(ResultValueType.ORG)) {
			deleteNormalRangesByResultId(testResult.getRid());
			return;
		}
		//		else if (CollectionUtil.isCollectionEmpty(normalRanges)) {
		//			throw new BusinessException("At least one normal range should exist!", "atLeastOneNormalRange", ErrorSeverity.ERROR);
		//		}
		for (int i = 0; i < normalRanges.size(); i++) {
			TestNormalRange normalRange = normalRanges.get(i);
			if (normalRange.getMarkedForDeletion()) {
				if (normalRange.getRid() != null) {
					deleteNormalRange(normalRange);
				}
				normalRanges.remove(i);
				i--;
			} else {
				if (normalRange.getRid() != null) {
					Boolean oldState = repo.getNormalRangeLastState(normalRange.getRid());
					if (!oldState.equals(normalRange.getIsActive())) {
						normalRange.setStateChangeDate(new Date());
					}
				} else {
					normalRange.setStateChangeDate(new Date());
				}
				validateAge(normalRange);
				normalRange.setTestResult(testResult);
				generateNormalRangeDescription(normalRange, testResult, Boolean.TRUE);
				switch (resultType) {
					case CE:
						if (!testResult.getTestCodedResultList().contains(normalRange.getCodedResult())) {
							throw new BusinessException("Normal range coded-value must be part of the coded-result-list",
									"codedValueNotFound", ErrorSeverity.ERROR);
						}
						normalRange.setMinValue(null);
						normalRange.setMaxValue(null);
						break;
					case QN:
					case QN_SC:
						validateQuantitativeValues(normalRange, testResult.getPrimaryDecimals());
						normalRange.setCodedResult(null);
						break;
					case RATIO:
						validateRatio(normalRange);
						normalRange.setCodedResult(null);
						normalRange.setMinValue(null);
						normalRange.setMaxValue(null);
						break;
					case NAR:
					case ORG:
					case QN_QL:
						//do nothing
						break;
				}
			}
		}

		repo.save(normalRanges);
	}

	private void generateNormalRangeDescription(TestNormalRange normalRange, TestResult result, Boolean isWithUnit) {
		List<String> descList = new ArrayList<String>();
		if (normalRange.getSex() != null) {
			descList.add(normalRange.getSex().getCode());
		}
		String ageFromComparator = "";
		if (normalRange.getAgeFromComparator() != null) {
			ageFromComparator = normalRange.getAgeFromComparator();
		}
		String ageToComparator = "";
		if (normalRange.getAgeToComparator() != null) {
			ageToComparator = normalRange.getAgeToComparator();
		}

		if (normalRange.getAgeFrom() != null && normalRange.getAgeTo() != null) {
			if (normalRange.getAgeToUnit().equals(normalRange.getAgeFromUnit())) {
				descList.add(ageFromComparator + normalRange.getAgeFrom() + "-"
						+ ageToComparator + normalRange.getAgeTo() + " "
						+ normalRange.getAgeToUnit().getCode());
			} else {
				descList.add(ageFromComparator + normalRange.getAgeFrom() + " " + normalRange.getAgeFromUnit().getCode()
						+ "-" + ageToComparator + normalRange.getAgeTo() + " " + normalRange.getAgeToUnit().getCode());
			}
		} else if (normalRange.getAgeFrom() != null) {
			descList.add(ageFromComparator + normalRange.getAgeFrom() + " " + normalRange.getAgeFromUnit().getCode());
		} else if (normalRange.getAgeTo() != null) {
			descList.add(ageToComparator + normalRange.getAgeTo() + " " + normalRange.getAgeToUnit().getCode());
		}

		String criterionData = generateNormalRangeCriterionDescription(normalRange);
		if (!StringUtil.isEmpty(criterionData)) {
			descList.add(criterionData);
		}

		descList.add(generateNormalRangeValueDescription(normalRange, result, Boolean.TRUE, isWithUnit));
		normalRange.setDescription(String.join("|", descList));
	}

	public String generateNormalRangeCriterionDescription(TestNormalRange normalRange) {
		String criterionData = "";

		if (!StringUtil.isEmpty(normalRange.getCriterionName()) && !StringUtil.isEmpty(normalRange.getCriterionValue())) {
			criterionData = normalRange.getCriterionName() + ": " + normalRange.getCriterionValue();
		} else if (!StringUtil.isEmpty(normalRange.getCriterionName())) {
			criterionData = normalRange.getCriterionName();
		} else if (!StringUtil.isEmpty(normalRange.getCriterionValue())) {
			criterionData = normalRange.getCriterionValue();
		}

		return criterionData;
	}

	public String generateNormalRangeValueDescription(TestNormalRange normalRange, TestResult result, Boolean isPrimary,
			Boolean isWithUnit) {
		Integer decimalsToUse = result.getPrimaryDecimals();
		if (!isPrimary) {
			decimalsToUse = result.getSecondaryDecimals();
		}
		String generatedValue = "";

		ResultValueType resultType = ResultValueType.valueOf(result.getResultValueType().getCode());
		switch (resultType) {
			case CE:
				generatedValue = normalRange.getCodedResult().getCode();
				break;
			case QN:
			case QN_SC:
				BigDecimal minValue = null;
				if (normalRange.getMinValue() != null) {
					minValue = normalRange.getMinValue().setScale(decimalsToUse, RoundingMode.HALF_UP);
					if (!isPrimary) {
						minValue = normalRange.getMinValue().multiply(result.getFactor()).setScale(decimalsToUse, RoundingMode.HALF_UP);
					}
				}
				BigDecimal maxValue = null;
				if (normalRange.getMaxValue() != null) {
					maxValue = normalRange.getMaxValue().setScale(decimalsToUse, RoundingMode.HALF_UP);
					if (!isPrimary) {
						maxValue = normalRange.getMaxValue().multiply(result.getFactor()).setScale(decimalsToUse, RoundingMode.HALF_UP);
					}
				}

				String minValueComparator = "";
				if (normalRange.getMinValueComparator() != null) {
					minValueComparator = normalRange.getMinValueComparator();
				}
				String maxValueComparator = "";
				if (normalRange.getMaxValueComparator() != null) {
					maxValueComparator = normalRange.getMaxValueComparator();
				}
				if (minValue != null && maxValue != null) {
					generatedValue = minValueComparator + minValue.toString() + " - " + maxValueComparator + maxValue.toString();
				} else if (minValue != null) {
					generatedValue = minValueComparator + minValue.toString();
				} else if (maxValue != null) {
					generatedValue = maxValueComparator + maxValue.toString();
				}
				if (isWithUnit) {
					String unit = result.getPrimaryUnit().getUnitOfMeasure();
					if (!isPrimary) {
						unit = result.getSecondaryUnit().getUnitOfMeasure();
					}
					generatedValue = generatedValue + " " + unit;
				}
				break;
			case RATIO:
				generatedValue = normalRange.getRatio();
				break;
			case NAR:
			case ORG:
			case QN_QL:
				break;
		}

		return generatedValue;
	}

	public void deleteNormalRangesByResultId(Long resultRid) {
		List<SearchCriterion> filters = new ArrayList<SearchCriterion>();
		filters.add(new SearchCriterion("testResult", resultRid, FilterOperator.eq));
		repo.delete(repo.find(filters, TestNormalRange.class));
	}

	private void validateQuantitativeValues(TestNormalRange normalRange, Integer decimals) {
		if (normalRange.getMinValue() == null && normalRange.getMaxValue() == null) {
			throw new BusinessException("\"Min Normal Value\" or \"Max Normal Value\" should be filled",
					"minOrMaxNormalValueShouldBeFilled", ErrorSeverity.ERROR);
		} else if (normalRange.getMinValue() != null && normalRange.getMaxValue() != null) {
			BigDecimal minValue = NumberUtil.getValueRespectingComparator(normalRange.getMinValueComparator(), normalRange.getMinValue(),
					decimals);
			BigDecimal maxValue = NumberUtil.getValueRespectingComparator(normalRange.getMaxValueComparator(), normalRange.getMaxValue(),
					decimals);
			if (maxValue.compareTo(minValue) < 0) {
				throw new BusinessException("Min value should be less than max value", "minLessThanMax", ErrorSeverity.ERROR);
			}
		}
	}

	private void validateRatio(TestNormalRange normalRange) {
		String regex = "(<|>)?\\s*[0-9]+\\s*:\\s*[0-9]+";
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(normalRange.getRatio());
		if (!matcher.find()) {
			throw new BusinessException("Invalid ratio pattern", "invalidRatioPattern", ErrorSeverity.ERROR);
		}
	}

	private void validateAge(TestNormalRange normalRange) {
		if (normalRange.getAgeFrom() != null && normalRange.getAgeTo() != null) {
			Long fromDuration = getDuration(normalRange.getAgeToComparator(), normalRange.getAgeFrom(),
					normalRange.getAgeFromUnit());
			Long toDuration = getDuration(normalRange.getAgeFromComparator(), normalRange.getAgeTo(),
					normalRange.getAgeToUnit());
			if (fromDuration.compareTo(toDuration) > 0) {
				throw new BusinessException("Age range should be chronological", "chronologicalAgeRange", ErrorSeverity.ERROR);
			}
		}
	}

	private Long getDuration(String comparator, Integer value, LkpAgeUnit ageUnit) {
		String unitCode = ageUnit.getCode() + "s";
		ChronoUnit unit = ChronoUnit.valueOf(unitCode.toUpperCase());
		Long duration = unit.getDuration().multipliedBy(value).toMillis();
		if (comparator == null) {
			return duration;
		}
		switch (Comparator.fromString(comparator)) {
			case gt:
				return duration++;
			case lt:
				return duration--;
			default:
			case gte:
			case lte:
				return duration;
		}
	}

	public TreeSet<TestNormalRange> filterNormalRange(EmrPatientInfo patient, Set<TestNormalRange> normalRanges) {
		TreeSet<TestNormalRange> filteredRanges = new TreeSet<TestNormalRange>();
		AgeWrapper ageWrapper = patient.getAgeWithUnit();
		Long patientAgeDuration = ageWrapper.getUnit().getDuration().multipliedBy(ageWrapper.getAge()).toMillis();

		for (TestNormalRange testRange : normalRanges) {
			if (testRange.getSex() != null && !testRange.getSex().getRid().equals(patient.getGender().getRid())) {
				continue;
			}

			if (testRange.getAgeFrom() != null && testRange.getAgeTo() != null) {
				Long ageFrom = getDuration(testRange.getAgeFromComparator(), testRange.getAgeFrom(), testRange.getAgeFromUnit());
				Long ageTo = getDuration(testRange.getAgeToComparator(), testRange.getAgeTo(), testRange.getAgeToUnit());

				int patientWithFrom = patientAgeDuration.compareTo(ageFrom);
				int patientWithTo = patientAgeDuration.compareTo(ageTo);

				if (patientWithFrom >= 0 && patientWithTo <= 0) {
					filteredRanges.add(testRange);
				}
			} else if (testRange.getAgeFrom() != null) {
				Long ageFrom = getDuration(testRange.getAgeFromComparator(), testRange.getAgeFrom(), testRange.getAgeFromUnit());
				int patientWithFrom = patientAgeDuration.compareTo(ageFrom);
				if (patientWithFrom >= 0) {
					filteredRanges.add(testRange);
				}
			} else if (testRange.getAgeTo() != null) {
				Long ageTo = getDuration(testRange.getAgeToComparator(), testRange.getAgeTo(), testRange.getAgeToUnit());
				int patientWithTo = patientAgeDuration.compareTo(ageTo);

				if (patientWithTo <= 0) {
					filteredRanges.add(testRange);
				}
			} else {
				filteredRanges.add(testRange);
			}
		}

		for (TestNormalRange normalRange : filteredRanges) {
			normalRange.setPrimaryValueDescription(
					generateNormalRangeValueDescription(normalRange, normalRange.getTestResult(), Boolean.TRUE, Boolean.FALSE));
		}

		return filteredRanges;
	}

}

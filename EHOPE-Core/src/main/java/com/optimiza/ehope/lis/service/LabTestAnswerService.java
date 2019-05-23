package com.optimiza.ehope.lis.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.optimiza.core.base.helper.SearchCriterion;
import com.optimiza.core.base.helper.SearchCriterion.FilterOperator;
import com.optimiza.core.base.service.GenericService;
import com.optimiza.core.common.util.DateUtil;
import com.optimiza.ehope.lis.lkp.helper.OperationStatus;
import com.optimiza.ehope.lis.lkp.helper.QuestionType;
import com.optimiza.ehope.lis.model.EmrVisit;
import com.optimiza.ehope.lis.model.LabTestActual;
import com.optimiza.ehope.lis.model.LabTestAnswer;
import com.optimiza.ehope.lis.model.TestQuestion;
import com.optimiza.ehope.lis.repo.LabTestAnswerRepo;

/**
 * LabTestAnswerService.java
 * 
 * @author Abdullah Imran <aImran@optimizasolutions.com>
 * @since Jan/14/2018
 **/
@Service("LabTestAnswerService")
public class LabTestAnswerService extends GenericService<LabTestAnswer, LabTestAnswerRepo> {

	@Autowired
	private LabTestAnswerRepo repo;
	@Autowired
	private EmrVisitService visitService;
	@Autowired
	private TestQuestionService testQuestionService;

	@Override
	protected LabTestAnswerRepo getRepository() {
		return repo;
	}

	public List<LabTestAnswer> createTestAnswer(List<LabTestAnswer> testAnswers) {
		return getRepository().save(testAnswers);
	}

	public void deleteAllByTestActual(LabTestActual testActual) {
		getRepository().deleteAllByLabTestActual(testActual);
	}

	public List<LabTestAnswer> getAnswersByVisit(Long visitRid) {
		return getRepository().getByVisit(visitRid);
	}

	/**
	 * Set the LabTestAnswer default value
	 * 
	 * @param answer
	 * @param qt
	 */
	public void setAnswerDefaultValue(LabTestAnswer answer, QuestionType qt) {
		if (qt == QuestionType.BOOLEAN && answer.getAnswerBoolean() == null) {
			answer.setAnswerBoolean(Boolean.FALSE);
		} else if (qt == QuestionType.NUMBER && answer.getAnswerNumber() == null) {
			answer.setAnswerNumber(BigDecimal.ZERO);
		} else if ((qt == QuestionType.DATE || qt == QuestionType.DATE_TIME) && answer.getAnswerDate() == null) {
			answer.setAnswerDate(DateUtil.getCurrentDateWithoutTime());
		} else if (qt == QuestionType.NARRATIVE && answer.getAnswerNarrative() == null) {
			answer.setAnswerNarrative("");
		}
	}

	/**
	 * Get the required data to enter question's answers by creating dummy answers or use old ones if exists
	 * 
	 * @param visitRid
	 * @return LabTestAnswer
	 */
	public List<LabTestAnswer> getTestQuestionEntryData(Long visitRid) {
		EmrVisit visit = visitService.findOne(Arrays.asList(new SearchCriterion("rid", visitRid, FilterOperator.eq)), EmrVisit.class,
				"labSamples.labTestActualSet.testDefinition");
		List<String> excludedStatuses = Arrays.asList(OperationStatus.ABORTED.getValue(), OperationStatus.CANCELLED.getValue());
		List<LabTestActual> testActuals = visit	.getLabSamples().stream().flatMap(s -> s.getLabTestActualSet().stream())
												.filter(lta -> !excludedStatuses.contains(lta.getLkpOperationStatus().getCode()))
												.collect(Collectors.toList());
		List<Long> testRids = testActuals.stream().map(lta -> lta.getTestDefinition().getRid()).collect(Collectors.toList());
		List<LabTestAnswer> previousAnswers = getAnswersByVisit(visitRid);
		List<LabTestAnswer> answers = new ArrayList<>();
		List<TestQuestion> selectedTestsQuestions = testQuestionService.find(
				Arrays.asList(new SearchCriterion("testDefinition.rid", testRids, FilterOperator.in)), TestQuestion.class,
				"lkpQuestionType", "testDefinition");
		for (LabTestActual test : testActuals) {
			for (TestQuestion question : selectedTestsQuestions) {
				if (question.getTestDefinition().equals(test.getTestDefinition())) {
					LabTestAnswer answer = new LabTestAnswer();
					Optional<LabTestAnswer> prevAnswer = previousAnswers.stream().filter(
							a -> a.getLabTestActual().getRid().equals(test.getRid())
									&& a.getTestQuestion().getRid().equals(question.getRid())).findFirst();
					if (prevAnswer.isPresent()) {
						answer = prevAnswer.get();//use previous answer
					} else { //create dummy answer
						answer.setLabTestActual(test);
						answer.setTestQuestion(question);
						QuestionType qt = QuestionType.valueOf(answer.getTestQuestion().getLkpQuestionType().getCode());
						setAnswerDefaultValue(answer, qt);
					}
					answers.add(answer);
				}
			}
		}
		return answers;
	}

}

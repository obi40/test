//package com.optimiza.ehope.lis.service;
//
//import java.util.List;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//
//import com.optimiza.core.base.service.GenericService;
//import com.optimiza.core.common.util.CollectionUtils;
//import com.optimiza.ehope.lis.model.LabTestQuestion;
//import com.optimiza.ehope.lis.model.LabTestSetupLine;
//import com.optimiza.ehope.lis.repo.LabTestQuestionRepo;
//
//@Service("LabTestQuestionService")
//public class LabTestQuestionService extends GenericService<LabTestQuestion, LabTestQuestionRepo> {
//
//	@Autowired
//	private LabTestQuestionRepo repo;
//
//	@Override
//	protected LabTestQuestionRepo getRepository() {
//		return repo;
//	}
//
//	public LabTestQuestion addTestQuestion(LabTestQuestion testQuestion) {
//		return repo.save(testQuestion);
//	}
//
//	public LabTestQuestion editTestQuestion(LabTestQuestion newTestQuestion) {
//		return repo.save(newTestQuestion);
//	}
//
//	public void deleteTestQuestion(LabTestQuestion testQuestion) {
//		repo.delete(testQuestion);
//	}
//
//	public List<LabTestQuestion> findLinesQuestions(List<LabTestSetupLine> testLines) {
//		if (CollectionUtils.isListEmpty(testLines)) {
//			return null;
//		}
//		return repo.findLinesQuestions(testLines);
//	}
//
//}

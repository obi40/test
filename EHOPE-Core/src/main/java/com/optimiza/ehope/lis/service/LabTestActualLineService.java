//package com.optimiza.ehope.lis.service;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//
//import com.optimiza.core.base.service.GenericService;
//import com.optimiza.ehope.lis.model.LabTestActualLine;
//import com.optimiza.ehope.lis.repo.LabTestActualLineRepo;
//
//@Service("LabTestActualLineService")
//public class LabTestActualLineService extends GenericService<LabTestActualLine, LabTestActualLineRepo> {
//
//	@Autowired
//	private LabTestActualLineRepo repo;
//
//	@Override
//	protected LabTestActualLineRepo getRepository() {
//		
//		return repo;
//	}
//
//	public LabTestActualLine addTestActualLine(LabTestActualLine actualLine) {
//		return repo.save(actualLine);
//	}
//
//	public void editTestActualLine(LabTestActualLine actualLine) {
//		repo.save(actualLine);
//	}
//
//	public void deleteTestActualLine(LabTestActualLine actualLine) {
//		repo.delete(actualLine);
//	}
//}

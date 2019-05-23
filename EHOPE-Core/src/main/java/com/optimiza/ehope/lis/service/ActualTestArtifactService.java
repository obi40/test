package com.optimiza.ehope.lis.service;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.optimiza.core.base.service.GenericService;
import com.optimiza.ehope.lis.helper.EhopeRights;
import com.optimiza.ehope.lis.model.ActualTestArtifact;
import com.optimiza.ehope.lis.model.LabTestActual;
import com.optimiza.ehope.lis.repo.ActualTestArtifactRepo;

/**
 * ActualTestArtifactService.java
 * 
 * @author Murad Poladian <mpoladian@optimizasolutions.com>
 * @since Jan/31/2019
 * 
 */
@Service("ActualTestArtifactService")
public class ActualTestArtifactService extends GenericService<ActualTestArtifact, ActualTestArtifactRepo> {

	@Autowired
	private ActualTestArtifactRepo repo;

	@Override
	protected ActualTestArtifactRepo getRepository() {
		return repo;
	}

	public ActualTestArtifact saveArtifact(ActualTestArtifact artifact) {
		return repo.save(artifact);
	}

	@PreAuthorize("hasAuthority('" + EhopeRights.VIEW_ARTIFACT_FINALIZED + "')")
	public List<Object> getByActualTestIdFinalized(Long actualTestId) {
		return getByActualTestId(actualTestId);
	}

	@PreAuthorize("hasAuthority('" + EhopeRights.VIEW_ARTIFACT + "')")
	public List<Object> getByActualTestIdNonFinalized(Long actualTestId) {
		return getByActualTestId(actualTestId);
	}

	public List<Object> getByActualTestId(Long actualTestId) {
		return getRepository().getByActualTestId(actualTestId);
	}

	public void deleteArtifactById(Long rid) {
		repo.delete(rid);
	}

	public void deleteAllByTestActual(LabTestActual testActual) {
		getRepository().deleteAllByActualTest(testActual);
	}

	public void saveArtifacts(MultipartFile[] artifacts, LabTestActual actualTest) throws IOException {
		for (int i = 0; i < artifacts.length; i++) {
			MultipartFile multipartFile = artifacts[i];
			ActualTestArtifact artifact = new ActualTestArtifact();
			artifact.setActualTest(actualTest);
			artifact.setContent(multipartFile.getBytes());
			artifact.setFileName(multipartFile.getOriginalFilename());
			artifact.setSize(multipartFile.getSize());
			artifact.setExtension(multipartFile.getOriginalFilename().substring(multipartFile.getOriginalFilename().lastIndexOf(".")));
			artifact.setContentType(multipartFile.getContentType());
			repo.save(artifact);
		}
	}

	public void deleteArtifacts(List<Long> artifactIdsToDelete) {
		for (Long rid : artifactIdsToDelete) {
			repo.delete(rid);
		}
	}

}

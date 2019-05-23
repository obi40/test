package com.optimiza.ehope.lis.service;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.optimiza.core.base.service.GenericService;
import com.optimiza.ehope.lis.model.EmrPatientInfo;
import com.optimiza.ehope.lis.model.PatientArtifact;
import com.optimiza.ehope.lis.repo.PatientArtifactRepo;

/**
 * PatientArtifactService.java
 * 
 * @author Murad Poladian <mpoladian@optimizasolutions.com>
 * @since Jan/31/2019
 * 
 */
@Service("PatientArtifactService")
public class PatientArtifactService extends GenericService<PatientArtifact, PatientArtifactRepo> {

	@Autowired
	private PatientArtifactRepo repo;

	@Override
	protected PatientArtifactRepo getRepository() {
		return repo;
	}

	public void saveArtifacts(MultipartFile[] multipartFiles, EmrPatientInfo patient) throws IOException {
		for (int i = 0; i < multipartFiles.length; i++) {
			MultipartFile multipartFile = multipartFiles[i];
			PatientArtifact artifact = new PatientArtifact();
			artifact.setPatient(patient);
			artifact.setContent(multipartFile.getBytes());
			artifact.setFileName(multipartFile.getOriginalFilename());
			artifact.setSize(multipartFile.getSize());
			artifact.setExtension(multipartFile.getOriginalFilename().substring(multipartFile.getOriginalFilename().lastIndexOf(".")));
			artifact.setContentType(multipartFile.getContentType());
			repo.save(artifact);
		}
	}

	public PatientArtifact saveArtifact(PatientArtifact artifact) {
		return repo.save(artifact);
	}

	public List<Object> getByPatientId(Long patientId) {
		return repo.getByPatientId(patientId);
	}

	public void deleteArtifactById(Long rid) {
		repo.delete(rid);
	}

}

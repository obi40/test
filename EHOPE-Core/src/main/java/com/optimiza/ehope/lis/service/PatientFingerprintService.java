package com.optimiza.ehope.lis.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.machinezoo.sourceafis.FingerprintMatcher;
import com.machinezoo.sourceafis.FingerprintTemplate;
import com.optimiza.core.base.helper.SearchCriterion;
import com.optimiza.core.base.helper.SearchCriterion.FilterOperator;
import com.optimiza.core.base.service.GenericService;
import com.optimiza.core.common.util.CollectionUtil;
import com.optimiza.ehope.lis.model.EmrPatientInfo;
import com.optimiza.ehope.lis.model.PatientFingerprint;
import com.optimiza.ehope.lis.repo.PatientFingerprintRepo;

/**
 * PatientFingerprintService.java
 * 
 * @author Murad Poladian <mpoladian@optimizasolutions.com>
 * @since Mar/24/2019
 * 
 */
@Service("PatientFingerprintService")
public class PatientFingerprintService extends GenericService<PatientFingerprint, PatientFingerprintRepo> {

	@Autowired
	private PatientFingerprintRepo repo;

	@Override
	protected PatientFingerprintRepo getRepository() {
		return repo;
	}

	public PatientFingerprint savePatientFingerprint(EmrPatientInfo patient, String base64Image) {
		PatientFingerprint fingerprint;
		List<PatientFingerprint> fingerprints = repo.find(Arrays.asList(new SearchCriterion("patient", patient, FilterOperator.eq)),
				PatientFingerprint.class);
		if (!CollectionUtil.isCollectionEmpty(fingerprints)) {
			fingerprint = fingerprints.get(0);
		} else {
			fingerprint = new PatientFingerprint();
			fingerprint.setPatient(patient);
		}

		byte[] bytes = Base64.getDecoder().decode(base64Image);
		String template = getTemplateStringFromImage(bytes);
		fingerprint.setTemplate(template);
		fingerprint.setImage(bytes);

		return repo.save(fingerprint);
	}

	public Long findPatientRidByFingerprint(String fingerprint) {
		byte[] bytes = Base64.getDecoder().decode(fingerprint);
		FingerprintTemplate candidate = getTemplateFromImage(bytes);
		FingerprintMatcher fingerprintMatcher = new FingerprintMatcher().index(candidate);
		List<PatientFingerprint> patientFingerprintsList = repo.getAllFingerprints();
		int count = 0;
		ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(10);
		Long matchPatientRid = null;
		List<Future<PatientFingerprint>> resultList = new ArrayList<>();
		FingerprintMatcherCallable fingerprintMatcherCallable = null;

		for (PatientFingerprint patientFingerPrint : patientFingerprintsList) {
			fingerprintMatcherCallable = new FingerprintMatcherCallable(patientFingerPrint, fingerprintMatcher);
			Future<PatientFingerprint> result = executor.submit(fingerprintMatcherCallable);
			resultList.add(result);
		}

		for (Future<PatientFingerprint> future : resultList) {
			try {
				++count;
				if (future.get().getScore() > 40) {
					matchPatientRid = future.get().getPatientId();
					System.out.println("##########  Match: Result is " + future.get().getScore() + "; And Task done is "
							+ future.isDone() + "; And patient RID is " + future.get().getPatientId() + "; After  " + count
							+ " Iterations");
					executor.shutdown();
					break;

				}
			} catch (InterruptedException | ExecutionException e) {
				e.printStackTrace();
			}
		}
		//shut down the executor service now
		executor.shutdown();

		return matchPatientRid;
	}

	private String getTemplateStringFromImage(byte[] bytes) {
		return getTemplateFromImage(bytes).serialize();
	}

	private FingerprintTemplate getTemplateFromImage(byte[] bytes) {
		return new FingerprintTemplate()
										.dpi(500)
										.create(bytes);
	}

}

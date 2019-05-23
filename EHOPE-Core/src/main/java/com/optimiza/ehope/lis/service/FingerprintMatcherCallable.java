package com.optimiza.ehope.lis.service;

import java.util.concurrent.Callable;

import com.machinezoo.sourceafis.FingerprintMatcher;
import com.machinezoo.sourceafis.FingerprintTemplate;
import com.optimiza.ehope.lis.model.PatientFingerprint;

public class FingerprintMatcherCallable implements Callable<PatientFingerprint>

{

	private PatientFingerprint patientFingerprint;

	private FingerprintMatcher matcher;

	public FingerprintMatcherCallable(PatientFingerprint patientFingerprint, FingerprintMatcher matcher) {
		this.patientFingerprint = patientFingerprint;
		this.matcher = matcher;
	}

	@Override
	public PatientFingerprint call() {
		FingerprintTemplate fingerprintTemplate = new FingerprintTemplate();
		fingerprintTemplate = fingerprintTemplate.deserialize(patientFingerprint.getTemplate());

		double score = matcher.match(fingerprintTemplate);
		//System.out.println("Result for number - " + patientFingerprint.getRid() + " -> " + score);
		patientFingerprint.setScore(score);
		return patientFingerprint;

	}

}

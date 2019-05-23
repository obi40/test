package com.optimiza.ehope.lis.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.optimiza.core.base.service.GenericService;
import com.optimiza.ehope.lis.model.NarrativeResultTemplate;
import com.optimiza.ehope.lis.model.TestResult;
import com.optimiza.ehope.lis.repo.NarrativeResultTemplateRepo;

/**
 * NarrativeResultTemplateService.java
 * 
 * @author Murad Poladian <mpoladian@optimizasolutions.com>
 * @since Jun/27/2018
 * 
 */
@Service("NarrativeResultTemplateService")
public class NarrativeResultTemplateService extends GenericService<NarrativeResultTemplate, NarrativeResultTemplateRepo> {

	@Autowired
	private NarrativeResultTemplateRepo repo;

	@Override
	protected NarrativeResultTemplateRepo getRepository() {
		return repo;
	}

	public void saveNarrativeResultTemplates(List<NarrativeResultTemplate> narrativeResultTemplateList, TestResult testResult) {
		for (NarrativeResultTemplate narrativeResultTemplate : narrativeResultTemplateList) {
			if (narrativeResultTemplate.getMarkedForDeletion()) {
				if (narrativeResultTemplate.getRid() != null) {
					repo.delete(narrativeResultTemplate);
				}
			} else {
				narrativeResultTemplate.setResult(testResult);
				repo.save(narrativeResultTemplate);
			}
		}
		testResult.setNarrativeTemplates(null);
	}

}

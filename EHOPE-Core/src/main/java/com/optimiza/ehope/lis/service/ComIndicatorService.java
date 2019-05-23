package com.optimiza.ehope.lis.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.optimiza.core.base.service.GenericService;
import com.optimiza.ehope.lis.model.ComIndicator;
import com.optimiza.ehope.lis.repo.ComIndicatorRepo;

@Service("ComIndicatorService")
public class ComIndicatorService extends GenericService<ComIndicator, ComIndicatorRepo> {

	@Autowired
	private ComIndicatorRepo repo;

	@Override
	protected ComIndicatorRepo getRepository() {
		return repo;
	}

	public ComIndicator addIndicator(ComIndicator indicator) {
		return repo.save(indicator);
	}

	public ComIndicator editIndicator(ComIndicator indicator) {
		return repo.save(indicator);
	}

	public void deleteIndicator(Long id) {
		repo.delete(id);
	}
}

package com.optimiza.ehope.lis.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.optimiza.core.base.service.GenericService;
import com.optimiza.ehope.lis.model.ComIndicatorField;
import com.optimiza.ehope.lis.repo.ComIndicatorFieldRepo;

@Service("ComIndicatorFieldService")
public class ComIndicatorFieldService extends GenericService<ComIndicatorField, ComIndicatorFieldRepo> {

	@Autowired
	private ComIndicatorFieldRepo repo;

	@Override
	protected ComIndicatorFieldRepo getRepository() {
		return repo;
	}

	public ComIndicatorField addIndicatorField(ComIndicatorField indicatorField) {
		return repo.save(indicatorField);
	}

	public ComIndicatorField editIndicatorField(ComIndicatorField indicatorField) {
		return repo.save(indicatorField);
	}

	public void deleteIndicatorField(Long id) {
		repo.delete(id);
	}

}

package com.optimiza.ehope.lis.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.optimiza.core.base.service.GenericService;
import com.optimiza.ehope.lis.model.LabSectionBranch;
import com.optimiza.ehope.lis.repo.LabSectionBranchRepo;

@Service("LabSectionBranchService")
public class LabSectionBranchService extends GenericService<LabSectionBranch, LabSectionBranchRepo> {

	@Autowired
	private LabSectionBranchRepo repo;

	@Override
	protected LabSectionBranchRepo getRepository() {
		return repo;
	}

	public void deleteAllByLabBranch(Long branchRid) {
		getRepository().deleteAllByLabBranchRid(branchRid);
	}
}

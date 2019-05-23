package com.optimiza.ehope.lis.repo;

import org.springframework.stereotype.Repository;

import com.optimiza.core.base.repo.GenericRepository;
import com.optimiza.ehope.lis.model.LabSectionBranch;

@Repository("LabSectionBranchRepo")
public interface LabSectionBranchRepo extends GenericRepository<LabSectionBranch> {

	void deleteAllByLabBranchRid(Long branchRid);
}

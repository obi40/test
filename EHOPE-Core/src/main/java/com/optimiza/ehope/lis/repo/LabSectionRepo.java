package com.optimiza.ehope.lis.repo;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.optimiza.core.base.repo.GenericRepository;
import com.optimiza.ehope.lis.model.LabSection;

@Repository("LabSectionRepo")
public interface LabSectionRepo extends GenericRepository<LabSection> {

	@Query("select distinct s from LabSection s, TestDefinition t "
			+ "where s.rid = t.section AND "
			+ "(LOWER(t.aliases) LIKE CONCAT('%',LOWER(:searchQuery),'%') OR "
			+ "LOWER(t.description) LIKE CONCAT('%',LOWER(:searchQuery),'%') OR "
			+ "LOWER(t.standardCode) LIKE CONCAT('%',LOWER(:searchQuery),'%')) "
			+ "order by s.rid")
	public List<LabSection> getSectionList(@Param("searchQuery") String searchQuery);

	@Query("select max(s.rank) from LabSection s")
	public Long getMaxRank();
	//	@Query("select s from LabSection s "
	//			+ "where s.rank = :newRank")
	//	public LabSection getExistingRankFromOld(@Param("newRank") Long newRank);

}

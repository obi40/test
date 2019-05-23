package com.optimiza.ehope.lis.repo;

import java.util.Date;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.optimiza.core.base.repo.GenericRepository;
import com.optimiza.ehope.lis.model.TestDefinition;

@Repository("TestDefinitionRepo")
public interface TestDefinitionRepo extends GenericRepository<TestDefinition> {

	@Query(value = "SELECT td FROM TestDefinition td WHERE "
			+ "(:isActive IS NULL OR td.isActive = :isActive) AND "
			+ "(LOWER(td.standardCode) LIKE CONCAT('%',:standardCode,'%') OR "
			+ "LOWER(td.secondaryCode) LIKE CONCAT('%',:secondaryCode,'%') OR "
			+ "LOWER(td.description) LIKE CONCAT('%',CAST(:description AS org.hibernate.type.TextType),'%') OR "
			+ "LOWER(td.aliases) LIKE CONCAT('%',CAST(:aliases AS org.hibernate.type.TextType),'%')) "
			+ "ORDER BY "
			+ "CASE "
			+ "WHEN LOWER(td.standardCode) LIKE CONCAT(:standardCode,'%') THEN 0 "
			+ "WHEN LOWER(td.description) LIKE CONCAT(CAST(:description AS org.hibernate.type.TextType),'%') THEN 1 "
			+ "WHEN LOWER(td.secondaryCode) LIKE CONCAT(:secondaryCode,'%') THEN 2 "
			+ "WHEN LOWER(td.aliases) LIKE CONCAT(CAST(:aliases AS org.hibernate.type.TextType),'%') THEN 2 "
			+ "ELSE 3 END,"
			+ "LENGTH (td.standardCode),"
			+ "td.standardCode", countQuery = "SELECT COUNT(td) FROM TestDefinition td WHERE "
					+ "td.isActive = :isActive AND "
					+ "LOWER(td.standardCode) LIKE CONCAT('%',:standardCode,'%') OR "
					+ "LOWER(td.secondaryCode) LIKE CONCAT('%',:secondaryCode,'%') OR "
					+ "LOWER(td.description) LIKE CONCAT('%',CAST(:description AS org.hibernate.type.TextType),'%') OR "
					+ "LOWER(td.aliases) LIKE CONCAT('%',CAST(:aliases AS org.hibernate.type.TextType),'%')")
	Page<TestDefinition> getTestDefinitionLookup(@Param("isActive") Boolean isActive, @Param("standardCode") String standardCode,
			@Param("description") String description,
			@Param("secondaryCode") String secondaryCode, @Param("aliases") String aliases, Pageable pageable);

	@Query(value = "SELECT td FROM TestDefinition td "
			+ "LEFT JOIN FETCH td.destinations d "
			+ "LEFT JOIN FETCH d.source s "
			+ "LEFT JOIN FETCH s.insuranceBranch WHERE "
			+ "(:isActive IS NULL OR td.isActive = :isActive) AND "
			+ "(LOWER(td.standardCode) LIKE CONCAT('%',:standardCode,'%') OR "
			+ "LOWER(td.secondaryCode) LIKE CONCAT('%',:secondaryCode,'%') OR "
			+ "LOWER(td.description) LIKE CONCAT('%',CAST(:description AS org.hibernate.type.TextType),'%') OR "
			+ "LOWER(td.aliases) LIKE CONCAT('%',CAST(:aliases AS org.hibernate.type.TextType),'%')) "
			+ "ORDER BY "
			+ "CASE "
			+ "WHEN LOWER(td.standardCode) LIKE CONCAT(:standardCode,'%') THEN 0 "
			+ "WHEN LOWER(td.description) LIKE CONCAT(CAST(:description AS org.hibernate.type.TextType),'%') THEN 1 "
			+ "WHEN LOWER(td.secondaryCode) LIKE CONCAT(:secondaryCode,'%') THEN 2 "
			+ "WHEN LOWER(td.aliases) LIKE CONCAT(CAST(:aliases AS org.hibernate.type.TextType),'%') THEN 2 "
			+ "ELSE 3 END,"
			+ "LENGTH (td.standardCode),"
			+ "td.standardCode", countQuery = "SELECT COUNT(td) FROM TestDefinition td WHERE "
					+ "td.isActive = :isActive AND "
					+ "LOWER(td.standardCode) LIKE CONCAT('%',:standardCode,'%') OR "
					+ "LOWER(td.secondaryCode) LIKE CONCAT('%',:secondaryCode,'%') OR "
					+ "LOWER(td.description) LIKE CONCAT('%',CAST(:description AS org.hibernate.type.TextType),'%') OR "
					+ "LOWER(td.aliases) LIKE CONCAT('%',CAST(:aliases AS org.hibernate.type.TextType),'%')")
	Page<TestDefinition> getTestDefinitionLookupWithDestinations(@Param("isActive") Boolean isActive,
			@Param("standardCode") String standardCode,
			@Param("description") String description,
			@Param("secondaryCode") String secondaryCode, @Param("aliases") String aliases, Pageable pageable);

	@Query("select t from TestDefinition t where t.standardCode = :standardCode")
	TestDefinition getByStandardCode(@Param("standardCode") String standardCode);

	@Query("select t from TestDefinition t where t.standardCode = :standardCode and t.isActive = true")
	List<TestDefinition> getActiveTestsByStandardCode(@Param("standardCode") String standardCode);

	//TODO must enhance performance
	@Query("select td from TestDefinition td "
			+ "LEFT JOIN td.testActualList lta "
			+ "LEFT JOIN FETCH td.destinations d "
			+ "LEFT JOIN FETCH d.source s "
			+ "LEFT JOIN FETCH s.insuranceBranch ib "
			+ "where td.isActive = true "
			+ "group by td, d, s, ib "
			+ "order by count(lta) desc")
	List<TestDefinition> getMostRequestedTests(Pageable pageable);

	@Query("select t.standardCode from TestDefinition t")
	List<String> getStandardCodes();

	@Query("select t from TestDefinition t where t.loincCode = :loincCode")
	List<TestDefinition> getByLoincCode(@Param("loincCode") String loincCode);

	@Query("select td from TestDefinition td left join fetch td.testResults where td.rid = :rid")
	TestDefinition testResultsFetch(@Param("rid") Long rid);

	@Query(value = "SELECT DISTINCT td FROM TestDefinition td "
			+ "LEFT JOIN td.billTestItems bti "
			+ "LEFT JOIN bti.billMasterItem bmt "
			+ "LEFT JOIN bmt.billPricings bp "
			+ "LEFT JOIN bp.billPriceList "
			+ "WHERE "
			+ "td.isActive = true AND "
			+ "(:testRid IS NULL OR td.rid = :testRid) AND "
			+ "(:standardCode IS NULL OR LOWER(td.standardCode) LIKE CONCAT('%',CAST(:standardCode AS java.lang.String),'%')) AND "
			+ "(:secondaryCode IS NULL OR LOWER(td.secondaryCode) LIKE CONCAT('%',CAST(:secondaryCode AS java.lang.String),'%')) AND "
			+ "(:aliases IS NULL OR LOWER(td.aliases) LIKE CONCAT('%',CAST(:aliases AS java.lang.String),'%')) AND "
			+ "(:description IS NULL OR LOWER(CAST(td.description AS org.hibernate.type.TextType)) LIKE CONCAT('%',CAST(:description AS org.hibernate.type.TextType),'%')) AND "
			+ "((:currentDate >= bp.startDate AND :currentDate <= bp.endDate) OR (:currentDate >= bp.startDate AND bp.endDate IS NULL))", countQuery = "SELECT COUNT(DISTINCT td) FROM TestDefinition td "
					+ "LEFT JOIN td.billTestItems bti "
					+ "LEFT JOIN bti.billMasterItem bmt "
					+ "LEFT JOIN bmt.billPricings bp "
					+ "LEFT JOIN bp.billPriceList "
					+ "WHERE "
					+ "td.isActive = true AND "
					+ "(:testRid IS NULL OR td.rid = :testRid) AND "
					+ "(:standardCode IS NULL OR LOWER(td.standardCode) LIKE CONCAT('%',CAST(:standardCode AS java.lang.String),'%')) AND "
					+ "(:secondaryCode IS NULL OR LOWER(td.secondaryCode) LIKE CONCAT('%',CAST(:secondaryCode AS java.lang.String),'%')) AND "
					+ "(:aliases IS NULL OR LOWER(td.aliases) LIKE CONCAT('%',CAST(:aliases AS java.lang.String),'%')) AND "
					+ "(:description IS NULL OR LOWER(CAST(td.description AS org.hibernate.type.TextType)) LIKE CONCAT('%',CAST(:description AS org.hibernate.type.TextType),'%')) AND "
					+ "((:currentDate >= bp.startDate AND :currentDate <= bp.endDate) OR (:currentDate >= bp.startDate AND bp.endDate IS NULL))")
	Page<TestDefinition> getTestsDefaultPricingPage(@Param("currentDate") Date currentDate,
			@Param("testRid") Long testRid, @Param("standardCode") String standardCode, @Param("description") String description,
			@Param("secondaryCode") String secondaryCode, @Param("aliases") String aliases,
			Pageable pageable);

}

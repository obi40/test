package com.optimiza.ehope.lis.repo;

import java.math.BigDecimal;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.optimiza.core.base.repo.GenericRepository;
import com.optimiza.core.lkp.model.LkpGender;
import com.optimiza.ehope.lis.lkp.model.LkpAgeUnit;
import com.optimiza.ehope.lis.model.TestNormalRange;

@Repository("TestNormalRangeRepo")
public interface TestNormalRangeRepo extends GenericRepository<TestNormalRange> {

	@Query("select nr from TestNormalRange nr "
			+ "LEFT JOIN nr.testResult tr "
			+ "LEFT JOIN tr.testDefinition td "
			+ "where "
			+ "td.standardCode = :testCode and "
			+ "tr.standardCode = :resultCode and "
			+ "((:min is null and nr.minValue is null) or nr.minValue = :min) and "
			+ "((:max is null and nr.maxValue is null) or nr.maxValue = :max) and "
			+ "((:gender is null and nr.sex is null) or nr.sex = :gender) and "
			+ "((:ageFromComparator is null and nr.ageFromComparator is null) or nr.ageFromComparator = :ageFromComparator) and "
			+ "((:ageFromValue is null and nr.ageFrom is null) or nr.ageFrom = :ageFromValue) and "
			+ "((:ageFromUnit is null and nr.ageFromUnit is null) or nr.ageFromUnit = :ageFromUnit) and "
			+ "((:ageToComparator is null and nr.ageToComparator is null) or nr.ageToComparator = :ageToComparator) and "
			+ "((:ageToValue is null and nr.ageTo is null) or nr.ageTo = :ageToValue) and "
			+ "((:ageToUnit is null and nr.ageToUnit is null) or nr.ageToUnit = :ageToUnit)")
	TestNormalRange getIncorrectNormalRange(@Param("testCode") String testCode, @Param("resultCode") String resultCode,
			@Param("min") BigDecimal min, @Param("max") BigDecimal max, @Param("gender") LkpGender gender,
			@Param("ageFromComparator") String ageFromComparator,
			@Param("ageFromValue") Integer ageFromValue,
			@Param("ageFromUnit") LkpAgeUnit ageFromUnit,
			@Param("ageToComparator") String ageToComparator,
			@Param("ageToValue") Integer ageToValue,
			@Param("ageToUnit") LkpAgeUnit ageToUnit);

	@Query("select nr.isActive from TestNormalRange nr where nr.rid = :rid")
	Boolean getNormalRangeLastState(@Param("rid") Long rid);

}

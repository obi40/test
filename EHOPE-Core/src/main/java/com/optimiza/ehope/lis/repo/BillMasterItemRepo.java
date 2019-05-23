package com.optimiza.ehope.lis.repo;

import java.util.Date;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.optimiza.core.base.repo.GenericRepository;
import com.optimiza.ehope.lis.model.BillMasterItem;
import com.optimiza.ehope.lis.model.BillPriceList;

@Repository("BillMasterItemRepo")
public interface BillMasterItemRepo extends GenericRepository<BillMasterItem> {

	@Query("select t from BillMasterItem t where t.cptCode = :cptCode")
	BillMasterItem getByCptCode(@Param("cptCode") String cptCode);

	@Query("SELECT DISTINCT bmt FROM BillMasterItem bmt "
			+ "LEFT JOIN FETCH bmt.billClassification bc "
			+ "LEFT JOIN FETCH bc.parentClassification pc "
			+ "LEFT JOIN FETCH bmt.billPricings bp "
			+ "LEFT JOIN FETCH bp.billPriceList bpl "
			+ "WHERE bmt IN :bmtList AND bpl IN :billPriceList "
			+ "AND ((:currentDate >= bp.startDate AND :currentDate <= bp.endDate) OR (:currentDate >= bp.startDate AND bp.endDate IS NULL)) ")
	List<BillMasterItem> getByMasterItemAndPriceList(@Param("bmtList") List<BillMasterItem> bmtList,
			@Param("billPriceList") List<BillPriceList> billPriceList, @Param("currentDate") Date currentDate);

	@Query("SELECT DISTINCT bmt FROM BillMasterItem bmt "
			+ "LEFT JOIN FETCH bmt.billPricings bp "
			+ "LEFT JOIN FETCH bp.billPriceList bpl "
			+ "WHERE bmt IN :bmtList AND bpl NOT IN :billPriceList ")
	List<BillMasterItem> getByMasterItemAndNotPriceList(@Param("bmtList") List<BillMasterItem> bmtList,
			@Param("billPriceList") List<BillPriceList> billPriceList);

	@Query(value = "SELECT DISTINCT bmt FROM BillMasterItem bmt "
			+ "LEFT JOIN bmt.billPricings bp "
			+ "LEFT JOIN bp.billPriceList "
			+ "LEFT JOIN bmt.billTestItems bti "
			+ "LEFT JOIN bti.testDefinition td "
			+ "WHERE "
			+ "(:standardCode IS NULL OR td.standardCode IS NULL OR LOWER(td.standardCode) LIKE CONCAT('%',CAST(:standardCode AS java.lang.String),'%')) AND "
			+ "(:description IS NULL OR td.description IS NULL OR LOWER(td.description) LIKE CONCAT('%',CAST(:description AS java.lang.String),'%')) AND "
			+ "(:secondaryCode IS NULL OR td.secondaryCode IS NULL OR LOWER(td.secondaryCode) LIKE CONCAT('%',CAST(:secondaryCode AS java.lang.String),'%')) AND "
			+ "(:aliases IS NULL OR td.aliases IS NULL OR LOWER(td.aliases) LIKE CONCAT('%',CAST(:aliases AS java.lang.String),'%')) AND "
			+ "(:testRid IS NULL OR td.rid = :testRid)", countQuery = "SELECT COUNT(DISTINCT bmt) FROM BillMasterItem bmt "
					+ "LEFT JOIN bmt.billPricings bp "
					+ "LEFT JOIN bp.billPriceList "
					+ "LEFT JOIN bmt.billTestItems bti "
					+ "LEFT JOIN bti.testDefinition td "
					+ "WHERE "
					+ "(:standardCode IS NULL OR td.standardCode IS NULL OR LOWER(td.standardCode) LIKE CONCAT('%',CAST(:standardCode AS java.lang.String),'%')) AND "
					+ "(:description IS NULL OR td.description IS NULL OR LOWER(td.description) LIKE CONCAT('%',CAST(:description AS java.lang.String),'%')) AND "
					+ "(:secondaryCode IS NULL OR td.secondaryCode IS NULL OR LOWER(td.secondaryCode) LIKE CONCAT('%',CAST(:secondaryCode AS java.lang.String),'%')) AND "
					+ "(:aliases IS NULL OR td.aliases IS NULL OR LOWER(td.aliases) LIKE CONCAT('%',CAST(:aliases AS java.lang.String),'%')) AND "
					+ "(:testRid IS NULL OR td.rid = :testRid)")
	Page<BillMasterItem> getByMasterItemAndPriceList(@Param("standardCode") String standardCode, @Param("description") String description,
			@Param("secondaryCode") String secondaryCode, @Param("aliases") String aliases, @Param("testRid") Long testRid,
			Pageable pageable);

}

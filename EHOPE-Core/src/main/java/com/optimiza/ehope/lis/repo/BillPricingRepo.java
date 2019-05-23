package com.optimiza.ehope.lis.repo;

import java.math.BigDecimal;
import java.util.Date;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.optimiza.core.base.repo.GenericRepository;
import com.optimiza.ehope.lis.model.BillPricing;

@Repository("BillPricingRepo")
public interface BillPricingRepo extends GenericRepository<BillPricing> {

	@Query("SELECT SUM(bp.price) FROM BillPricing bp "
			+ "LEFT JOIN bp.billPriceList bpl "
			+ "LEFT JOIN bp.billMasterItem bmt "
			+ "LEFT JOIN bmt.billTestItems bti "
			+ "LEFT JOIN bti.testDefinition td "
			+ "LEFT JOIN td.groupDefinitions groupDef "
			+ "LEFT JOIN groupDef.testGroup tg "
			+ "WHERE "
			+ "tg.rid = :groupId AND "
			+ "bpl.rid = :priceListRid AND "
			+ "((:currentDate >= bp.startDate AND :currentDate <= bp.endDate) OR (:currentDate >= bp.startDate AND bp.endDate IS NULL)) ")
	BigDecimal getTestGroupPrice(@Param("priceListRid") Long priceListRid, @Param("currentDate") Date currentDate,
			@Param("groupId") Long groupId);

}

package com.optimiza.ehope.lis.repo;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.optimiza.core.base.repo.GenericRepository;
import com.optimiza.ehope.lis.model.OrderArtifact;

/**
 * OrderArtifactRepo.java
 * 
 * @author Murad Poladian <mpoladian@optimizasolutions.com>
 * @since Jan/31/2019
 * 
 */
@Repository("OrderArtifactRepo")
public interface OrderArtifactRepo extends GenericRepository<OrderArtifact> {

	@Query("select a.rid, a.fileName, a.size, a.extension, a.contentType from OrderArtifact a where a.order.rid = :orderId")
	List<Object> getByOrderId(@Param("orderId") Long orderId);

}

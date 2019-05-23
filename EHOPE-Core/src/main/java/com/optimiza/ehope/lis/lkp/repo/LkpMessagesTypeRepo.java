package com.optimiza.ehope.lis.lkp.repo;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.optimiza.core.base.repo.GenericRepository;
import com.optimiza.ehope.lis.lkp.model.LkpMessagesType;

/**
 * LkpMessagesTypeRepo.java
 * 
 * @author Abdullah Imran <aImran@optimizasolutions.com>
 * @since Sep/12/2017
 **/

@Repository("LkpMessagesTypeRepo")
public interface LkpMessagesTypeRepo extends GenericRepository<LkpMessagesType> {

	@Query("select lmt from LkpMessagesType lmt")
	public List<LkpMessagesType> messagesTypeSearch();

}

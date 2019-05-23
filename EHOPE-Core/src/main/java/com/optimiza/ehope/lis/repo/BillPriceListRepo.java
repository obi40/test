package com.optimiza.ehope.lis.repo;

import org.springframework.stereotype.Repository;

import com.optimiza.core.base.repo.GenericRepository;
import com.optimiza.ehope.lis.model.BillPriceList;

@Repository("BillPriceListRepo")
public interface BillPriceListRepo extends GenericRepository<BillPriceList> {

}

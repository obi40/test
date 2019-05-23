package com.optimiza.ehope.lis.service;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.optimiza.core.base.service.GenericService;
import com.optimiza.ehope.lis.model.HistoricalResult;
import com.optimiza.ehope.lis.repo.HistoricalResultRepo;

/**
 * HistoricalResultService.java
 * 
 * @author Murad Poladian <mpoladian@optimizasolutions.com>
 * @since Nov/11/2018
 * 
 */
@Service("HistoricalResultService")
public class HistoricalResultService extends GenericService<HistoricalResult, HistoricalResultRepo> {

	@Autowired
	private HistoricalResultRepo repo;

	@Autowired
	private EntityManagerFactory entityManagerFactory;

	@Override
	protected HistoricalResultRepo getRepository() {
		return repo;
	}

	public HistoricalResult createHistoricalResult(HistoricalResult historicalResult) {
		return repo.save(historicalResult);
	}

	public List<HistoricalResult> getLatestHistoricalResults(String patientFileNo, String testCode, String resultCode,
			Integer neededPreviousResults) {
		try {
			return repo.getLatestHistoricalResults(patientFileNo, testCode, resultCode, new PageRequest(0, neededPreviousResults));
		} catch (IndexOutOfBoundsException e) {
			return null;
		}
	}

	public void createHistoricalResults(List<HistoricalResult> historicalResults) {
		EntityManager entityManager = entityManagerFactory.createEntityManager();
		EntityTransaction entityTransaction = entityManager.getTransaction();
		entityTransaction.begin();
		int size = historicalResults.size();
		try {
			for (int i = 0; i < size; i++) {
				entityManager.persist(historicalResults.get(i));
				if (i > 0 && i % 50 == 0) {
					entityManager.flush();
					entityManager.clear();
				}
			}
			entityManager.flush();
			entityManager.clear();
			entityTransaction.commit();
			entityManager.close();
		} catch (Exception e) {//if any random exception happened then roll back
			e.printStackTrace();
			entityTransaction.rollback();
		}
	}

}

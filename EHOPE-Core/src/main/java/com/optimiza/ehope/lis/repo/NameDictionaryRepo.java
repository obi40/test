package com.optimiza.ehope.lis.repo;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.optimiza.core.base.repo.GenericRepository;
import com.optimiza.ehope.lis.model.NameDictionary;

/**
 * NameDictionaryRepo.java
 * 
 * @author Murad Poladian <mpoladian@optimizasolutions.com>
 * @since Aug/20/2018
 * 
 */
@Repository("NameDictionaryRepo")
public interface NameDictionaryRepo extends GenericRepository<NameDictionary> {

	@Query("SELECT arabicName from NameDictionary "
			+ "where lower(englishNormalized) = :name "
			+ "ORDER BY recCount DESC")
	public Page<String> findMostCommonArabicName(@Param("name") String name, Pageable pageable);

	@Query("SELECT englishName from NameDictionary "
			+ "where lower(arabicNormalized) = :name "
			+ "ORDER BY recCount DESC")
	public Page<String> findMostCommonEnglishName(@Param("name") String name, Pageable pageable);

}

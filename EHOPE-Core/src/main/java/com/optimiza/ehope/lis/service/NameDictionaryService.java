package com.optimiza.ehope.lis.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.Normalizer;
import java.text.Normalizer.Form;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.optimiza.core.base.helper.SearchCriterion;
import com.optimiza.core.base.service.GenericService;
import com.optimiza.core.common.data.model.TransField;
import com.optimiza.core.common.util.StringUtil;
import com.optimiza.ehope.lis.model.NameDictionary;
import com.optimiza.ehope.lis.repo.NameDictionaryRepo;

@Service("NameDictionaryService")
public class NameDictionaryService extends GenericService<NameDictionary, NameDictionaryRepo> {

	@Autowired
	private NameDictionaryRepo repo;

	public static final String EN = "en";
	public static final String AR = "ar";

	@Override
	protected NameDictionaryRepo getRepository() {
		return repo;
	}

	public NameDictionary addNameDictionary(NameDictionary nameDictionary) {
		return repo.save(nameDictionary);
	}

	public NameDictionary editNameDictionary(NameDictionary nameDictionary) {
		return repo.save(nameDictionary);
	}

	public void deleteNameDictionary(NameDictionary nameDictionary) {
		repo.delete(nameDictionary);
	}

	public List<NameDictionary> findNameDictionaries(List<SearchCriterion> filters, Sort sort, String... joins) {
		return getRepository().find(filters, NameDictionary.class, sort, joins);
	}

	public void importTransliteration(InputStream is) {
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new InputStreamReader(is, "UTF8"));

			reader.readLine(); //skip the first line

			List<NameDictionary> nameDictionaries = new ArrayList<NameDictionary>();
			String line = null;
			while ((line = reader.readLine()) != null) {
				String[] values = line.split(",");
				NameDictionary nameDictionary = new NameDictionary();
				nameDictionary.setArabicName(values[0]);
				nameDictionary.setArabicNormalized(normalize(values[0]));
				nameDictionary.setEnglishName(values[1]);
				nameDictionary.setEnglishNormalized(normalize(values[1]));
				nameDictionary.setRecCount(Integer.parseInt(values[2]));
				nameDictionaries.add(nameDictionary);
				if (nameDictionaries.size() == 10) {
					repo.save(nameDictionaries);
					nameDictionaries.clear();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (reader != null) {
					reader.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public String transliterate(String[] names, String sourceLanguage) {
		for (int i = 0; i < names.length; i++) {
			if (!names[i].isEmpty()) {
				names[i] = transliterate(names[i], sourceLanguage);
			}
		}

		return String.join("||", names);
	}

	public String transliterate(String name, String sourceLanguage) {
		String output;
		try {
			if (sourceLanguage.startsWith(EN)) {
				output = repo.findMostCommonArabicName(normalize(name).toLowerCase(), new PageRequest(0, 1)).getContent().get(0);
			} else {
				output = repo.findMostCommonEnglishName(normalize(name).toLowerCase(), new PageRequest(0, 1)).getContent().get(0);
			}
		} catch (IndexOutOfBoundsException e) {
			output = "";
		}
		return output;
	}

	@Cacheable(cacheNames = "NameDictionary")
	public Map<String, Map<String, NameDictionary>> getAll() {
		Map<String, Map<String, NameDictionary>> result = new HashMap<String, Map<String, NameDictionary>>();
		List<NameDictionary> names = getRepository().find(new ArrayList<>(), NameDictionary.class);
		Map<String, NameDictionary> arabicMap = new HashMap<String, NameDictionary>();
		Map<String, NameDictionary> englishMap = new HashMap<String, NameDictionary>();
		for (NameDictionary name : names) {
			NameDictionary arabicName = arabicMap.get(name.getArabicNormalized());
			NameDictionary englishName = englishMap.get(name.getEnglishNormalized());
			if (arabicName == null) {
				arabicMap.put(name.getArabicNormalized(), name);
			} else if (arabicName.getRecCount() < name.getRecCount()) {
				arabicMap.put(name.getArabicNormalized(), name);
			}
			if (englishName == null) {
				englishMap.put(name.getEnglishNormalized(), name);
			} else if (englishName.getRecCount() < name.getRecCount()) {
				englishMap.put(name.getEnglishNormalized(), name);
			}
		}
		result.put(EN, englishMap);
		result.put(AR, arabicMap);
		return result;
	}

	private String normalize(String input) {
		String output = Normalizer
									.normalize(input, Form.NFD)
									//{Mn} Mark, Nonspacing
									//{P} Punctuation
									.replaceAll("\\p{Mn}*\\p{P}*", "")
									.replaceAll("\\s+", "");
		return output;
	}

	public TransField translateTransFields(TransField tf, Map<String, Map<String, NameDictionary>> transliteration) {
		if (tf == null) {
			return tf;
		}
		String firstNameAR = tf.get(NameDictionaryService.AR + "_jo");
		String firstNameEN = tf.get(NameDictionaryService.EN + "_us");
		NameDictionary nameDictionary = null;
		String target = null;
		if (!StringUtil.isEmpty(firstNameAR) && StringUtil.isEmpty(firstNameEN)) {
			nameDictionary = transliteration.get(NameDictionaryService.AR)
											.get(normalize(firstNameAR));
			target = NameDictionaryService.EN + "_us";

		} else if (StringUtil.isEmpty(firstNameAR) && !StringUtil.isEmpty(firstNameEN)) {
			nameDictionary = transliteration.get(NameDictionaryService.EN)
											.get(normalize(firstNameAR));
			target = NameDictionaryService.AR + "_jo";

		}
		if (nameDictionary != null && !StringUtil.isEmpty(target)) {
			tf.put(target, nameDictionary.getEnglishName());
		}
		return tf;
	}

	public String transliterate(String name, String sourceLanguage, Map<String, Map<String, NameDictionary>> transliteration) {
		if (StringUtil.isEmpty(name)) {
			return name;
		}
		String targetName;
		if (sourceLanguage.startsWith(EN)) {
			NameDictionary nameDictionary = transliteration	.get(EN)
															.get(normalize(name));
			if (nameDictionary == null) {
				return name;
			}
			targetName = nameDictionary.getArabicName();
		} else {
			NameDictionary nameDictionary = transliteration	.get(AR)
															.get(normalize(name));
			if (nameDictionary == null) {
				return name;
			}
			targetName = nameDictionary.getEnglishName();
		}
		return targetName;
	}
}

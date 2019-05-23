package com.optimiza.ehope.web.namedictionary.controller;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.basistech.rosette.api.HttpRosetteAPI;
import com.basistech.rosette.apimodel.NameTranslationRequest;
import com.basistech.rosette.apimodel.NameTranslationResponse;
import com.basistech.util.ISO15924;
import com.basistech.util.LanguageCode;
import com.basistech.util.TransliterationScheme;
import com.optimiza.ehope.lis.service.NameDictionaryService;
import com.optimiza.ehope.web.helper.TranslationObject;

@RestController
@RequestMapping("/services")
public class NameDictionaryController {

	@Autowired
	private NameDictionaryService nameDictionaryService;

	private final String ROSETTE_KEY = "2c7f4fed1d3d7ff1f5eef7a2a2d36fb6";

	@RequestMapping(value = "/getLocalTransliteration.srvc", method = RequestMethod.POST)
	public ResponseEntity<NameTranslationResponse> getLocalTransliteration(@RequestBody TranslationObject translationObject) {
		String[] names = translationObject.getName().split("\\|\\|");
		String sourceLanguage = translationObject.getSourceLanguageOfOrigin();

		String translation = nameDictionaryService.transliterate(names, sourceLanguage);

		NameTranslationResponse response = new NameTranslationResponse(null, null,
				null, translation, null, null, null, null);
		return new ResponseEntity<NameTranslationResponse>(response, HttpStatus.OK);
	}

	@RequestMapping(value = "/getTransliteration.srvc", method = RequestMethod.POST)
	public ResponseEntity<NameTranslationResponse> getRosetteTransliteration(@RequestBody TranslationObject translationObject)
			throws IOException {
		String name = translationObject.getName();
		LanguageCode targetLanguage = LanguageCode.lookupByISO639(translationObject.getTargetLanguage());

		String entityType = translationObject.getEntityType();

		LanguageCode sourceLanguageOfOrigin = translationObject.getSourceLanguageOfOrigin() != null
				? LanguageCode.lookupByISO639(translationObject.getSourceLanguageOfOrigin())
				: null;
		LanguageCode sourceLanguageOfUse = translationObject.getSourceLanguageOfUse() != null
				? LanguageCode.lookupByISO639(translationObject.getSourceLanguageOfUse())
				: null;
		ISO15924 sourceScript = translationObject.getSourceScript();
		ISO15924 targetScript = translationObject.getTargetScript();
		TransliterationScheme targetScheme = translationObject.getTargetScheme();

		NameTranslationRequest request = new NameTranslationRequest.Builder(name, targetLanguage)
																									.entityType(entityType)
																									.sourceScript(sourceScript)
																									.sourceLanguageOfOrigin(
																											sourceLanguageOfOrigin)
																									.sourceLanguageOfUse(
																											sourceLanguageOfUse)
																									.targetScript(targetScript)
																									.targetScheme(targetScheme)
																									.build();

		HttpRosetteAPI rosetteApi = new HttpRosetteAPI.Builder()
																.key(ROSETTE_KEY)
																.url(HttpRosetteAPI.DEFAULT_URL_BASE)
																.build();

		NameTranslationResponse response = rosetteApi.perform(HttpRosetteAPI.NAME_TRANSLATION_SERVICE_PATH, request,
				NameTranslationResponse.class);

		return new ResponseEntity<NameTranslationResponse>(response, HttpStatus.OK);
	}

}

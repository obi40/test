package com.optimiza.ehope.web.helper;

import com.basistech.util.ISO15924;
import com.basistech.util.TransliterationScheme;

public class TranslationObject {
	
	private String name;
	private String targetLanguage;
	
	//following fields are optional
	private String entityType;
	private ISO15924 sourceScript;
	private String sourceLanguageOfOrigin;
	private String sourceLanguageOfUse;
	private ISO15924 targetScript;
	private TransliterationScheme targetScheme;
	

	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getTargetLanguage() {
		return targetLanguage;
	}
	public void setTargetLanguage(String targetLanguage) {
		this.targetLanguage = targetLanguage;
	}
	public String getEntityType() {
		return entityType;
	}
	public void setEntityType(String entityType) {
		this.entityType = entityType;
	}
	public ISO15924 getSourceScript() {
		return sourceScript;
	}
	public void setSourceScript(ISO15924 sourceScript) {
		this.sourceScript = sourceScript;
	}
	public String getSourceLanguageOfOrigin() {
		return sourceLanguageOfOrigin;
	}
	public void setSourceLanguageOfOrigin(String sourceLanguageOfOrigin) {
		this.sourceLanguageOfOrigin = sourceLanguageOfOrigin;
	}
	public String getSourceLanguageOfUse() {
		return sourceLanguageOfUse;
	}
	public void setSourceLanguageOfUse(String sourceLanguageOfUse) {
		this.sourceLanguageOfUse = sourceLanguageOfUse;
	}
	public ISO15924 getTargetScript() {
		return targetScript;
	}
	public void setTargetScript(ISO15924 targetScript) {
		this.targetScript = targetScript;
	}
	public TransliterationScheme getTargetScheme() {
		return targetScheme;
	}
	public void setTargetScheme(TransliterationScheme targetScheme) {
		this.targetScheme = targetScheme;
	}
	
}

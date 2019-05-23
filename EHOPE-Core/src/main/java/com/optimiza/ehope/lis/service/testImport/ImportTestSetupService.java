package com.optimiza.ehope.lis.service.testImport;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Node;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;
import com.optimiza.core.base.service.GenericService;
import com.optimiza.core.common.data.model.TransField;
import com.optimiza.core.lkp.repo.LkpGenderRepo;
import com.optimiza.core.lkp.service.LkpService;
import com.optimiza.ehope.lis.lkp.model.LkpAgeUnit;
import com.optimiza.ehope.lis.lkp.model.LkpRangeUnit;
import com.optimiza.ehope.lis.lkp.model.LkpSignum;
import com.optimiza.ehope.lis.lkp.model.LkpSpecimenStabilityUnit;
import com.optimiza.ehope.lis.lkp.model.LkpSpecimenTemperature;
import com.optimiza.ehope.lis.lkp.model.LkpSpecimenType;
import com.optimiza.ehope.lis.lkp.model.LkpTestEntryType;
import com.optimiza.ehope.lis.lkp.repo.LkpAgeUnitRepo;
import com.optimiza.ehope.lis.lkp.repo.LkpRangeUnitRepo;
import com.optimiza.ehope.lis.lkp.repo.LkpSignumRepo;
import com.optimiza.ehope.lis.lkp.repo.LkpSpecimenStabilityUnitRepo;
import com.optimiza.ehope.lis.lkp.repo.LkpTestEntryTypeRepo;
import com.optimiza.ehope.lis.model.ExtraTest;
import com.optimiza.ehope.lis.model.LoincAttributes;
import com.optimiza.ehope.lis.model.TestDefinition;
import com.optimiza.ehope.lis.model.TestForm;
import com.optimiza.ehope.lis.model.TestQuestion;
import com.optimiza.ehope.lis.model.TestQuestionOption;
import com.optimiza.ehope.lis.model.TestResult;
import com.optimiza.ehope.lis.model.TestSpecimen;
import com.optimiza.ehope.lis.repo.ExtraTestRepo;
import com.optimiza.ehope.lis.repo.LoincAttributesRepo;
import com.optimiza.ehope.lis.repo.TestDefinitionRepo;
import com.optimiza.ehope.lis.repo.TestFormRepo;
import com.optimiza.ehope.lis.repo.TestNormalRangeRepo;
import com.optimiza.ehope.lis.repo.TestQuestionOptionRepo;
import com.optimiza.ehope.lis.repo.TestQuestionRepo;
import com.optimiza.ehope.lis.repo.TestResultRepo;
import com.optimiza.ehope.lis.repo.TestSpecimenRepo;

@Service("ImportTestSetupService")
public class ImportTestSetupService extends GenericService<TestDefinition, TestDefinitionRepo> {

	@Autowired
	private LkpTestEntryTypeRepo entryTypeRepo;

	@Autowired
	private TestDefinitionRepo testRepo;

	@Autowired
	private TestFormRepo formRepo;

	@Autowired
	private TestQuestionOptionRepo optionRepo;

	@Autowired
	private TestQuestionRepo questionRepo;

	@Autowired
	private TestResultRepo resultRepo;

	@Autowired
	private TestSpecimenRepo specimenRepo;

	@Autowired
	private ExtraTestRepo extraTestRepo;

	@Autowired
	private TestNormalRangeRepo normalRangeRepo;

	@Autowired
	private LoincAttributesRepo loincAttributesRepo;

	@Autowired
	private LkpGenderRepo genderRepo;

	@Autowired
	private LkpAgeUnitRepo ageUnitRepo;

	@Autowired
	private LkpRangeUnitRepo rangeUnitRepo;

	@Autowired
	private LkpSignumRepo signumRepo;

	@Autowired
	private LkpSpecimenStabilityUnitRepo specimenStabilityUnitRepo;

	@Autowired
	private LkpService lkpService;

	public void parseLoincAttributes() {
		try {
			File excelFile = new File("C:\\Users\\mpoladian\\Documents\\LIS-Codes\\LOINC\\LOINC_2.61\\LoincTable\\loinc.xlsx");
			FileInputStream inputStream = new FileInputStream(excelFile);
			Workbook workbook = new XSSFWorkbook(inputStream);
			Sheet firstSheet = workbook.getSheetAt(0);

			DataFormatter dataFormatter = new DataFormatter();

			Iterator<Row> iterator = firstSheet.iterator();
			//skip the first row
			iterator.next();

			Map<String, String> rowMap = new HashMap<String, String>();

			while (iterator.hasNext()) {
				Row nextRow = iterator.next();
				if (nextRow.getRowNum() > 10000) {
					break;
				}
				int lastColumn = Math.max(nextRow.getLastCellNum(), 46);

				for (int cn = 0; cn < lastColumn; cn++) {
					Cell cell = nextRow.getCell(cn, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);
					String cellValue = null;

					if (cell != null) {
						cellValue = dataFormatter.formatCellValue(cell);
					}
					getLoincAttributesMap(cn, rowMap, cellValue);
				}

				LoincAttributes loincAttributes = getLoincAttributesFromMap(rowMap);
				loincAttributes = loincAttributesRepo.save(loincAttributes);
				List<TestDefinition> tests = testRepo.getByLoincCode(rowMap.get("loincNum"));
				for (TestDefinition test : tests) {
					if (test != null) {
						test.setLoincAttributes(loincAttributes);
						testRepo.save(test);
					}
				}

				List<TestResult> testResults = resultRepo.getByLoincCode(rowMap.get("loincNum"));
				for (TestResult testResult : testResults) {
					if (testResult != null) {
						testResult.setLoincAttributesObject(loincAttributes);
						resultRepo.save(testResult);
					}
				}

			}
			workbook.close();
			inputStream.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private LoincAttributes getLoincAttributesFromMap(Map<String, String> rowMap) {
		LoincAttributes loincAttributes = new LoincAttributes();
		loincAttributes.setAskAtOrderEntry(rowMap.get("askAtOrderEntry"));
		loincAttributes.setAssociatedObservations(rowMap.get("associatedObservations"));
		loincAttributes.setCdiscCommonTests(rowMap.get("cdiscCommonTests"));
		loincAttributes.setChangeReasonPublic(rowMap.get("changeReasonPublic"));
		loincAttributes.setChangeType(rowMap.get("changeType"));
		loincAttributes.setClass_(rowMap.get("class"));
		loincAttributes.setClassType(rowMap.get("classType"));
		loincAttributes.setCommonOrderRank(rowMap.get("commonOrderRank"));
		loincAttributes.setCommonSiTestRank(rowMap.get("commonSiTestRank"));
		loincAttributes.setCommonTestRank(rowMap.get("commonTestRank"));
		loincAttributes.setComponent(rowMap.get("component"));
		loincAttributes.setConsumerName(rowMap.get("consumerName"));
		loincAttributes.setDefinitionDescription(rowMap.get("definitionDescription"));
		loincAttributes.setDocumentSection(rowMap.get("documentSection"));
		loincAttributes.setExampleAnswers(rowMap.get("exampleAnswers"));
		loincAttributes.setExampleSiUcumUnits(rowMap.get("exampleSiUcumUnits"));
		loincAttributes.setExampleUcumUnits(rowMap.get("exampleUcumUnits"));
		loincAttributes.setExampleUnits(rowMap.get("exampleUnits"));
		loincAttributes.setExternalCopyrightLink(rowMap.get("externalCopyrightLink"));
		loincAttributes.setExternalCopyrightNotice(rowMap.get("externalCopyrightNotice"));
		loincAttributes.setFormula(rowMap.get("formula"));
		loincAttributes.setHl7AttachmentStructure(rowMap.get("hl7AttachmentStructure"));
		loincAttributes.setHl7FieldSubfieldId(rowMap.get("hl7FieldSubfieldId"));
		loincAttributes.setLoincNum(rowMap.get("loincNum"));
		loincAttributes.setLongCommonName(rowMap.get("longCommonName"));
		loincAttributes.setMethodType(rowMap.get("methodType"));
		loincAttributes.setOrderObservation(rowMap.get("orderObservation"));
		loincAttributes.setPanelType(rowMap.get("panelType"));
		loincAttributes.setProperty(rowMap.get("property"));
		loincAttributes.setRelatedNames2(rowMap.get("relatedNames2"));
		loincAttributes.setScaleType(rowMap.get("scaleType"));
		loincAttributes.setShortName(rowMap.get("shortName"));
		loincAttributes.setSpecies(rowMap.get("species"));
		loincAttributes.setStatus(rowMap.get("status"));
		loincAttributes.setStatusReason(rowMap.get("statusReason"));
		loincAttributes.setStatusText(rowMap.get("statusText"));
		loincAttributes.setSubmittedUnits(rowMap.get("submittedUnits"));
		loincAttributes.setSurveyQuestionSource(rowMap.get("surveyQuestionSource"));
		loincAttributes.setSurveyQuestionText(rowMap.get("surveyQuestionText"));
		loincAttributes.setSystem(rowMap.get("system"));
		loincAttributes.setTimeAspect(rowMap.get("timeAspect"));
		loincAttributes.setUnitsAndRange(rowMap.get("unitsAndRange"));
		loincAttributes.setUnitsRequired(rowMap.get("unitsRequired"));
		loincAttributes.setValidHl7AttachmentRequest(rowMap.get("validHl7AttachmentRequest"));
		loincAttributes.setVersionFirstReleased(rowMap.get("versionFirstReleased"));
		loincAttributes.setVersionLastChanged(rowMap.get("versionLastChanged"));
		return loincAttributes;
	}

	private void getLoincAttributesMap(int j, Map<String, String> rowMap, String cellValue) {
		switch (j) {
			case 0:
				rowMap.put("loincNum", cellValue);
				break;
			case 1:
				rowMap.put("component", cellValue);
				break;
			case 2:
				rowMap.put("property", cellValue);
				break;
			case 3:
				rowMap.put("timeAspect", cellValue);
				break;
			case 4:
				rowMap.put("system", cellValue);
				break;
			case 5:
				rowMap.put("scaleType", cellValue);
				break;
			case 6:
				rowMap.put("methodType", cellValue);
				break;
			case 7:
				rowMap.put("class", cellValue);
				break;
			case 8:
				rowMap.put("versionLastChanged", cellValue);
				break;
			case 9:
				rowMap.put("changeType", cellValue);
				break;
			case 10:
				rowMap.put("definitionDescription", cellValue);
				break;
			case 11:
				rowMap.put("status", cellValue);
				break;
			case 12:
				rowMap.put("consumerName", cellValue);
				break;
			case 13:
				rowMap.put("classType", cellValue);
				break;
			case 14:
				rowMap.put("formula", cellValue);
				break;
			case 15:
				rowMap.put("species", cellValue);
				break;
			case 16:
				rowMap.put("exampleAnswers", cellValue);
				break;
			case 17:
				rowMap.put("surveyQuestionText", cellValue);
				break;
			case 18:
				rowMap.put("surveyQuestionSource", cellValue);
				break;
			case 19:
				rowMap.put("unitsRequired", cellValue);
				break;
			case 20:
				rowMap.put("submittedUnits", cellValue);
				break;
			case 21:
				rowMap.put("relatedNames2", cellValue);
				break;
			case 22:
				rowMap.put("shortName", cellValue);
				break;
			case 23:
				rowMap.put("orderObservation", cellValue);
				break;
			case 24:
				rowMap.put("cdiscCommonTests", cellValue);
				break;
			case 25:
				rowMap.put("hl7FieldSubfieldId", cellValue);
				break;
			case 26:
				rowMap.put("externalCopyrightNotice", cellValue);
				break;
			case 27:
				rowMap.put("exampleUnits", cellValue);
				break;
			case 28:
				rowMap.put("longCommonName", cellValue);
				break;
			case 29:
				rowMap.put("unitsAndRange", cellValue);
				break;
			case 30:
				rowMap.put("documentSection", cellValue);
				break;
			case 31:
				rowMap.put("exampleUcumUnits", cellValue);
				break;
			case 32:
				rowMap.put("exampleSiUcumUnits", cellValue);
				break;
			case 33:
				rowMap.put("statusReason", cellValue);
				break;
			case 34:
				rowMap.put("statusText", cellValue);
				break;
			case 35:
				rowMap.put("changeReasonPublic", cellValue);
				break;
			case 36:
				rowMap.put("commonTestRank", cellValue);
				break;
			case 37:
				rowMap.put("commonOrderRank", cellValue);
				break;
			case 38:
				rowMap.put("commonSiTestRank", cellValue);
				break;
			case 39:
				rowMap.put("hl7AttachmentStructure", cellValue);
				break;
			case 40:
				rowMap.put("externalCopyrightLink", cellValue);
				break;
			case 41:
				rowMap.put("panelType", cellValue);
				break;
			case 42:
				rowMap.put("askAtOrderEntry", cellValue);
				break;
			case 43:
				rowMap.put("associatedObservations", cellValue);
				break;
			case 44:
				rowMap.put("versionFirstReleased", cellValue);
				break;
			case 45:
				rowMap.put("validHl7AttachmentRequest", cellValue);
				break;
		}
	}

	//	public void parseAllRefVals() {
	//		try {
	//			//this sets the age and signum distinct units, the range units will be set during the parsing below
	//			extractDistinctUnits();
	//			File excelFile = new File("C:\\Users\\mpoladian\\Documents\\LIS-Codes\\Sep-8-2017\\MayoRepository-Official.xls");
	//			FileInputStream inputStream = new FileInputStream(excelFile);
	//			Workbook workbook = new HSSFWorkbook(inputStream);
	//			Sheet firstSheet = workbook.getSheetAt(0);
	//
	//			DataFormatter dataFormatter = new DataFormatter();
	//
	//			Iterator<Row> iterator = firstSheet.iterator();
	//			//skip the first 2 rows
	//			iterator.next();
	//			iterator.next();
	//
	//			Map<String, String> rowMap = new HashMap<String, String>();
	//
	//			while (iterator.hasNext()) {
	//				Row nextRow = iterator.next();
	//				Iterator<Cell> cellIterator = nextRow.cellIterator();
	//
	//				int j = 0;
	//
	//				while (cellIterator.hasNext()) {
	//					Cell cell = cellIterator.next();
	//					String cellValue = dataFormatter.formatCellValue(cell);
	//					insertRowMapCell(j, rowMap, cellValue);
	//					j++;
	//				}
	//				switch (rowMap.get("entryType")) {
	//					case "Test":
	//					case "Reflex Test":
	//					case "Component":
	//						List<TestNormalRange> normalRanges = parseRefVal(rowMap.get("referenceValues"));
	//						TestDefinition testDefinition = testRepo.getByStandardCode(rowMap.get("mayoId"));
	//						normalRanges.forEach(nr ->
	//							{
	//								nr.setTestDefinition(testDefinition);
	//								normalRangeRepo.save(nr);
	//							});
	//						break;
	//				}
	//			}
	//
	//			workbook.close();
	//			inputStream.close();
	//		} catch (FileNotFoundException e) {
	//			e.printStackTrace();
	//		} catch (IOException e) {
	//			e.printStackTrace();
	//		}
	//	}

	private LkpRangeUnit getRangeUnit(String unit) {
		if (unit.equals("")) {
			return null;
		}
		LkpRangeUnit lkpRangeUnit = rangeUnitRepo.findByCode(unit);
		if (lkpRangeUnit == null) {
			lkpRangeUnit = new LkpRangeUnit();
			lkpRangeUnit.setCode(unit);
			TransField name = new TransField();
			name.put("en_us", unit);
			lkpRangeUnit.setName(name);
			TransField desc = new TransField();
			desc.put("en_us", unit);
			lkpRangeUnit.setDescription(desc);
			lkpRangeUnit = rangeUnitRepo.save(lkpRangeUnit);
		}
		return lkpRangeUnit;
	}

	//	private TestNormalRange extractRangeValues(TestNormalRange normalRange, String[] groups, int index) {
	//		if (groups[index] != null) {
	//			normalRange.setMinNormalValue(new BigDecimal(groups[index].replaceAll(",", "")));
	//		}
	//		if (groups[index + 2] != null) {
	//			normalRange.setMaxNormalValue(new BigDecimal(groups[index + 2].replaceAll(",", "")));
	//		}
	//		if (groups[index + 4] != null) {
	//			normalRange.setUnit(getRangeUnit(groups[index + 4]));
	//		}
	//		return normalRange;
	//	}

	//	private TestNormalRange extractRangeValues2(TestNormalRange normalRange, String[] groups, int index) {
	//		if ((checkEquality(groups[index], ">") && checkEquality(groups[index + 2], "="))
	//				|| checkEquality(groups[index + 2], ">")) {
	//			normalRange.setMinNormalValue(new BigDecimal(groups[index + 3].replaceAll(",", "")));
	//		} else if ((checkEquality(groups[index], "<") && checkEquality(groups[index + 2], "="))
	//				|| checkEquality(groups[index + 2], "<")) {
	//			normalRange.setMaxNormalValue(new BigDecimal(groups[index + 3].replaceAll(",", "")));
	//		}
	//		if (groups[index + 5] != null) {
	//			normalRange.setUnit(getRangeUnit(groups[index + 5]));
	//		}
	//		return normalRange;
	//	}

	//	private TestNormalRange extractMeanValue(TestNormalRange normalRange, String[] groups, int index) {
	//		normalRange.setMeanNormalValue(new BigDecimal(groups[index].replaceAll(",", "")));
	//		normalRange.setUnit(getRangeUnit(groups[index + 2]));
	//		return normalRange;
	//	}

	//	private TestNormalRange extractAgeRange(TestNormalRange normalRange, String[] groups, int index) {
	//		if ((checkEquality(groups[index], ">") && checkEquality(groups[index + 2], "="))
	//				|| checkEquality(groups[index + 2], ">")) {
	//			normalRange.setAgeFrom(Long.parseLong(groups[3]));
	//		} else if ((checkEquality(groups[index], "<") && checkEquality(groups[index + 2], "="))
	//				|| checkEquality(groups[index + 2], "<")) {
	//			normalRange.setAgeTo(Long.parseLong(groups[index + 3]));
	//		}
	//		LkpAgeUnit lkpAgeUnit = ageUnitRepo.findByCode(groups[index + 4]);
	//		normalRange.setAgeFromUnit(lkpAgeUnit);
	//		normalRange.setAgeToUnit(lkpAgeUnit);
	//		return normalRange;
	//	}
	//
	//	private TestNormalRange extractAgeRange2(TestNormalRange normalRange, String[] groups, int index) {
	//		LkpAgeUnit lkpAgeUnit = ageUnitRepo.findByCode(groups[index + 2]);
	//		normalRange.setAgeFrom(Long.parseLong(groups[index]));
	//		normalRange.setAgeFromUnit(lkpAgeUnit);
	//		normalRange.setAgeTo(Long.parseLong(groups[index + 1]));
	//		normalRange.setAgeToUnit(lkpAgeUnit);
	//		return normalRange;
	//	}
	//
	//	private TestNormalRange extractAgeValue(TestNormalRange normalRange, String[] groups, int index) {
	//		normalRange.setAge(Long.parseLong(groups[index]));
	//		LkpAgeUnit lkpAgeUnit = ageUnitRepo.findByCode(groups[index + 1]);
	//		normalRange.setAgeUnit(lkpAgeUnit);
	//		return normalRange;
	//	}

	//	private List<TestNormalRange> parseRefVal(String refVal) {
	//
	//		//reusables
	//		String rangeRegex = "\\s*(\\d+(,\\d{3})*\\d*\\.?\\d*)-(\\d+(,\\d{3})*\\d*\\.?\\d*)\\s*(\\w*(\\/?\\w*)*).*"; //6 groups
	//		String rangeRegex2 = "\\s*(<|>)?\\s*(or)?\\s*(<|=|>)\\s*(\\d+(,\\d{3})*\\d*\\.?\\d*)\\s*(\\w*(\\/?\\w*)*).*"; //7 groups
	//		String meanRegex = "\\s*(\\d+(,\\d{3})*\\d*\\.?\\d*)\\s*(\\w*(\\/?\\w*)*).*";//4 groups
	//
	//		String baseAgeRegex = "(<|>)?\\s*(or)?\\s*(<|=|>)(\\d+)\\s*(year|month|week|day)s?:"; //5 groups
	//		String baseAgeRegex2 = "(\\d+)-(\\d+)\\s*(year|month|week|day)s?:"; //3 groups
	//		String baseAgeRegex3 = "(\\d+)\\s*(year|month|week|day)s?:"; //2 groups
	//
	//		//actual matches
	//		String ageRegex = baseAgeRegex + rangeRegex; //11 groups
	//		String ageRegex2 = baseAgeRegex2 + rangeRegex; //9 groups
	//		String ageRegex3 = baseAgeRegex + rangeRegex2; //12 groups
	//		String ageRegex4 = baseAgeRegex2 + rangeRegex2; //10 groups
	//		String ageRegex5 = baseAgeRegex3 + rangeRegex; //8 groups
	//		String ageRegex6 = baseAgeRegex3 + rangeRegex2; //9 groups
	//		String ageRegex7 = baseAgeRegex + meanRegex; //9 groups
	//		String ageRegex8 = baseAgeRegex2 + meanRegex; //7 groups
	//		String ageRegex9 = baseAgeRegex3 + meanRegex; //6 groups
	//
	//		String genderRegex = "(Male|Female)s?";
	//
	//		String signumRegex = "(Negative|Positive)";
	//
	//		//to be parsed properly
	//		//		String edgeAgeRegex = "(<|>)?(\\s)?(\\w)*(\\s)?(=)?(\\d)+(\\s)*(year|month|week|day)(s)?.*";
	//		//		String ratioRegex = "\\w*:?\\s*[<|>]\\d+:\\d+.*";
	//		//		String rangeRegex = "(<|>)?(\\s)*(or)?(\\s)*(<|=|>)\\d+\\.?\\d*.*";
	//		//		String rangeRegex2 = "(\\d+\\.?\\d*-\\d+\\.?\\d*.*)";
	//
	//		String[] lines = refVal.trim().split("\n");
	//
	//		List<TestNormalRange> normalRanges = new ArrayList<TestNormalRange>();
	//
	//		Pattern pattern;
	//		Matcher matcher;
	//		String detectedGender = "";
	//
	//		LkpGender maleGender = genderRepo.findByCode("MALE");
	//		LkpGender femaleGender = genderRepo.findByCode("FEMALE");
	//
	//		for (int i = 0; i < lines.length; i++) {
	//			String line = lines[i].trim();
	//			TestNormalRange normalRange = new TestNormalRange();
	//			if (line.matches(genderRegex)) {
	//				pattern = Pattern.compile(genderRegex);
	//				matcher = pattern.matcher(line);
	//				if (matcher.find()) {
	//					detectedGender = matcher.group(1);
	//				}
	//			} else if (line.matches(ageRegex)) {
	//				pattern = Pattern.compile(ageRegex);
	//				matcher = pattern.matcher(line);
	//				if (matcher.find()) {
	//					int groupLength = 10;
	//					String[] groups = new String[groupLength];
	//					for (int k = 1; k <= groupLength; k++) {
	//						groups[k - 1] = matcher.group(k);
	//					}
	//					extractAgeRange(normalRange, groups, 0);
	//					extractRangeValues(normalRange, groups, 5);
	//				}
	//			} else if (line.matches(ageRegex2)) {
	//				pattern = Pattern.compile(ageRegex2);
	//				matcher = pattern.matcher(line);
	//				if (matcher.find()) {
	//					int groupLength = 8;
	//					String[] groups = new String[groupLength];
	//					for (int k = 1; k <= groupLength; k++) {
	//						groups[k - 1] = matcher.group(k);
	//					}
	//					extractAgeRange2(normalRange, groups, 0);
	//					extractRangeValues(normalRange, groups, 3);
	//				}
	//			} else if (line.matches(ageRegex3)) {
	//				pattern = Pattern.compile(ageRegex3);
	//				matcher = pattern.matcher(line);
	//				if (matcher.find()) {
	//					int groupLength = 11;
	//					String[] groups = new String[groupLength];
	//					for (int k = 1; k <= groupLength; k++) {
	//						groups[k - 1] = matcher.group(k);
	//					}
	//					extractAgeRange(normalRange, groups, 0);
	//					extractRangeValues2(normalRange, groups, 5);
	//				}
	//			} else if (line.matches(ageRegex4)) {
	//				pattern = Pattern.compile(ageRegex4);
	//				matcher = pattern.matcher(line);
	//				if (matcher.find()) {
	//					int groupLength = 9;
	//					String[] groups = new String[groupLength];
	//					for (int k = 1; k <= groupLength; k++) {
	//						groups[k - 1] = matcher.group(k);
	//					}
	//					extractAgeRange2(normalRange, groups, 0);
	//					extractRangeValues2(normalRange, groups, 3);
	//				}
	//			} else if (line.matches(ageRegex5)) {
	//				pattern = Pattern.compile(ageRegex5);
	//				matcher = pattern.matcher(line);
	//				if (matcher.find()) {
	//					int groupLength = 8;
	//					String[] groups = new String[groupLength];
	//					for (int k = 1; k <= groupLength; k++) {
	//						groups[k - 1] = matcher.group(k);
	//					}
	//					extractAgeValue(normalRange, groups, 0);
	//					extractRangeValues(normalRange, groups, 2);
	//				}
	//			} else if (line.matches(ageRegex6)) {
	//				pattern = Pattern.compile(ageRegex6);
	//				matcher = pattern.matcher(line);
	//				if (matcher.find()) {
	//					int groupLength = 8;
	//					String[] groups = new String[groupLength];
	//					for (int k = 1; k <= groupLength; k++) {
	//						groups[k - 1] = matcher.group(k);
	//					}
	//					extractAgeValue(normalRange, groups, 0);
	//					extractRangeValues2(normalRange, groups, 2);
	//				}
	//			} else if (line.matches(ageRegex7)) {
	//				pattern = Pattern.compile(ageRegex7);
	//				matcher = pattern.matcher(line);
	//				if (matcher.find()) {
	//					int groupLength = 9;
	//					String[] groups = new String[groupLength];
	//					for (int k = 1; k <= groupLength; k++) {
	//						groups[k - 1] = matcher.group(k);
	//					}
	//					extractAgeRange(normalRange, groups, 0);
	//					extractMeanValue(normalRange, groups, 5);
	//				}
	//			} else if (line.matches(ageRegex8)) {
	//				pattern = Pattern.compile(ageRegex8);
	//				matcher = pattern.matcher(line);
	//				if (matcher.find()) {
	//					int groupLength = 7;
	//					String[] groups = new String[groupLength];
	//					for (int k = 1; k <= groupLength; k++) {
	//						groups[k - 1] = matcher.group(k);
	//					}
	//					extractAgeRange2(normalRange, groups, 0);
	//					extractMeanValue(normalRange, groups, 3);
	//				}
	//			} else if (line.matches(ageRegex9)) {
	//				pattern = Pattern.compile(ageRegex9);
	//				matcher = pattern.matcher(line);
	//				if (matcher.find()) {
	//					int groupLength = 6;
	//					String[] groups = new String[groupLength];
	//					for (int k = 1; k <= groupLength; k++) {
	//						groups[k - 1] = matcher.group(k);
	//					}
	//					extractAgeValue(normalRange, groups, 0);
	//					extractMeanValue(normalRange, groups, 2);
	//				}
	//			} else if (line.matches(signumRegex)) {
	//				pattern = Pattern.compile(signumRegex);
	//				matcher = pattern.matcher(line);
	//				if (matcher.find()) {
	//					normalRange.setSignum(signumRepo.findByCode(matcher.group(1)));
	//				}
	//			} else if (line.matches(rangeRegex)) {
	//				pattern = Pattern.compile(rangeRegex);
	//				matcher = pattern.matcher(line);
	//				if (matcher.find()) {
	//					int groupLength = 6;
	//					String[] groups = new String[groupLength];
	//					for (int k = 1; k <= groupLength; k++) {
	//						groups[k - 1] = matcher.group(k);
	//					}
	//					extractRangeValues(normalRange, groups, 0);
	//				}
	//			} else if (line.matches(rangeRegex2)) {
	//				pattern = Pattern.compile(rangeRegex2);
	//				matcher = pattern.matcher(line);
	//				if (matcher.find()) {
	//					int groupLength = 6;
	//					String[] groups = new String[groupLength];
	//					for (int k = 1; k <= groupLength; k++) {
	//						groups[k - 1] = matcher.group(k);
	//					}
	//					extractRangeValues2(normalRange, groups, 0);
	//				}
	//			} else {
	//
	//			}
	//			if (!line.equals("")) {
	//				if (detectedGender.equals("Male")) {
	//					normalRange.setSex(maleGender);
	//				} else if (detectedGender.equals("Female")) {
	//					normalRange.setSex(femaleGender);
	//				}
	//				normalRange.setNotes(line);
	//				normalRanges.add(normalRange);
	//			}
	//		}
	//		return normalRanges;
	//	}

	private boolean checkEquality(String str1, String str2) {
		return str1 != null && str1.equals(str2);
	}

	public void bindMayoToLoinc() {
		try {
			File excelFile = new File("C:\\Users\\mpoladian\\Documents\\LIS-Codes\\Sep-8-2017\\MayoClinicLOINC Mapping-Official.xlsx");
			FileInputStream inputStream = new FileInputStream(excelFile);

			Workbook workbook = new XSSFWorkbook(inputStream);
			Sheet firstSheet = workbook.getSheetAt(0);

			DataFormatter dataFormatter = new DataFormatter();

			Iterator<Row> iterator = firstSheet.iterator();

			//skip the first row
			iterator.next();

			Map<String, String> rowMap = new HashMap<String, String>();

			while (iterator.hasNext()) {
				Row nextRow = iterator.next();
				Iterator<Cell> cellIterator = nextRow.cellIterator();

				TestDefinition testDefinition = null;
				int j = 0;
				while (cellIterator.hasNext()) {
					Cell cell = cellIterator.next();
					switch (j) {
						case 0:
							String mayoId = dataFormatter.formatCellValue(cell);
							testDefinition = testRepo.getByStandardCode(mayoId);
							break;
						case 2:
							rowMap.put("loincCode", dataFormatter.formatCellValue(cell));
							break;
						case 3:
							rowMap.put("loincAttributes", dataFormatter.formatCellValue(cell));
							break;
						case 4:
							rowMap.put("methodName", dataFormatter.formatCellValue(cell));
							break;
						case 5:
							rowMap.put("cptCode", dataFormatter.formatCellValue(cell));
							break;
					}
					j++;
				}
				if (testDefinition != null) {
					if (rowMap.get("loincCode").matches("\\d+-\\d")) {
						testDefinition.setLoincCode(rowMap.get("loincCode"));
					}
					if (rowMap.get("cptCode").matches("\\d+")) {
						testDefinition.setCptCode(rowMap.get("cptCode"));
					}
					if (!rowMap.get("methodName").isEmpty()) {
						//testDefinition.setMethod(rowMap.get("methodName")); its a lkp now
					}
					testRepo.save(testDefinition);
				}
			}

			workbook.close();

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void extractFromHtml() {

		try {
			//reading the mayo ids from the db instead of the text file - there is no need to fetch the extra data
			//			String testCodesFile = "C:\\Users\\mpoladian\\Documents\\LIS-Codes\\ListOfMayClinicTestClinicalInformation\\"
			//					+ "ListOfMayoClinicTestCodes.txt";
			//
			//			BufferedReader br = new BufferedReader(new FileReader(testCodesFile));

			String outputFile = "C:\\Users\\mpoladian\\Documents\\LIS-Codes\\ListOfMayClinicTestClinicalInformation\\"
					+ "TestClinicalInfo3.json";
			BufferedWriter bw = new BufferedWriter(new FileWriter(outputFile));

			ObjectMapper mapper = new ObjectMapper();

			bw.write("[\n");

			boolean writeComa = false;

			List<String> mayoIds = testRepo.getStandardCodes();
			for (String mayoId : mayoIds) {
				Map<String, String> testData = extractJson(mayoId.trim());
				if (!testData.isEmpty()) {
					if (writeComa) {
						bw.write(",\n");
					} else {
						writeComa = true;
					}
					bw.write(mapper	.writeValueAsString(testData)
									.replaceAll("\",\"", "\",\n\"").replaceAll("\\{", "\\{\n").replaceAll("\\}", "\n\\}"));
				} else {
					System.out.println("Skipping empty test: " + mayoId);
				}
			}

			//			String line;
			//
			//			while ((line = br.readLine()) != null) {
			//				Map<String, String> testData = extractJson(line.trim());
			//				if (!testData.isEmpty()) {
			//					if (writeComa) {
			//						bw.write(",\n");
			//					} else {
			//						writeComa = true;
			//					}
			//					bw.write(mapper	.writeValueAsString(testData)
			//									.replaceAll("\",\"", "\",\n\"").replaceAll("\\{", "\\{\n").replaceAll("\\}", "\n\\}"));
			//				} else {
			//					System.out.println("Skipping empty test: " + line);
			//				}
			//			}
			bw.write("\n]");
			bw.close();
			//			br.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private Map<String, String> extractJson(String testId) {
		Map<String, String> testEntry = new HashMap<String, String>();
		try {
			Document doc = Jsoup.connect("http://www.mayomedicallaboratories.com/test-catalog/Clinical+and+Interpretive/" + testId).get();

			Elements headers = doc.select("#test_catalog > a");
			Elements dataFields = doc.select("#test_catalog > .data_field");

			for (int i = 0; i < headers.size(); i++) {
				String key = headers.get(i).attr("name");
				String value = dataFields.get(i).html().replaceAll("<p>&nbsp;</p>|\n", "").trim();
				testEntry.put(key, value);
			}

			if (!testEntry.isEmpty()) {
				testEntry.put("testId", testId);
				testEntry.put("loincCode", getLoincCode(testId));
			}
		} catch (IOException e) {
			//e.printStackTrace();
		}
		return testEntry;
	}

	private String getLoincCode(String testId) {
		try {
			Document doc = Jsoup.connect("http://www.mayomedicallaboratories.com/test-catalog/Fees+and+Coding/" + testId).get();
			Elements loincCodeWrapper = doc.select("div#test_catalog > a[name=loinc_codes]");

			Node table = loincCodeWrapper.first().nextSibling().nextSibling().nextSibling().childNode(0);

			List<Node> children = table.childNode(0).childNode(1).childNodes();
			Node loincCode = children.get(children.size() - 1);

			return loincCode.toString().replaceAll("<td>|</td>", "").trim();

		} catch (Exception e) {
			System.out.println("Skipping loinc code for test ID: " + testId);
		}
		return null;
	}

	public void importJson() {
		try {
			byte[] jsonData = Files.readAllBytes(
					Paths.get("C:\\Users\\mpoladian\\Documents\\LIS-Codes\\ListOfMayClinicTestClinicalInformation"
							+ "\\TestClinicalInfo3.json"));

			ObjectMapper objectMapper = new ObjectMapper();

			CollectionType javaType = objectMapper.getTypeFactory().constructCollectionType(List.class, TestInfo.class);

			List<TestInfo> testInfoList = objectMapper.readValue(jsonData, javaType);

			testInfoList.forEach((t) ->
				{
					TestDefinition testDefinition = testRepo.getByStandardCode(t.getTestId());
					//skipping reference values as they already exist

					if (testDefinition != null) {
						testDefinition.setUsefulFor(t.getUsefulFor());
						testDefinition.setTestingAlgorithm(t.getTestingAlgorithm());
						testDefinition.setClinicalInformation(t.getClinicalInformation());
						testDefinition.setInterpretation(t.getInterpretation());
						testDefinition.setCautions(t.getCautions());
						testDefinition.setClinicalReference(t.getClinicalReference());
						testDefinition.setSpecialInstructions(t.getSpecialInstructions());
						testDefinition.setSupportiveData(t.getSupportiveData());
						testDefinition.setGeneticsTestInformation(t.getGeneticsTestInformation());
						testDefinition.setAliases(t.getAliases());
						if ((testDefinition.getLoincCode() == null || testDefinition.getLoincCode().equals(""))
								&& t.getLoincCode() != null && t.getLoincCode().matches("\\d+-\\d")) {
							testDefinition.setLoincCode(t.getLoincCode());
						}
						testRepo.save(testDefinition);
					}

				});

		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public void importNormalRangeText() {
		try {
			File excelFile = new File("C:\\Users\\mpoladian\\Documents\\LIS-Codes\\Sep-8-2017\\MayoRepository-Official.xls");
			FileInputStream inputStream = new FileInputStream(excelFile);
			Workbook workbook = new HSSFWorkbook(inputStream);
			Sheet firstSheet = workbook.getSheetAt(0);

			DataFormatter dataFormatter = new DataFormatter();

			Iterator<Row> iterator = firstSheet.iterator();
			//skip the first 2 rows
			iterator.next();
			iterator.next();

			Map<String, String> rowMap = new HashMap<String, String>();

			TestDefinition testDefinition = null;

			while (iterator.hasNext()) {
				Row nextRow = iterator.next();
				Iterator<Cell> cellIterator = nextRow.cellIterator();

				int j = 0;

				while (cellIterator.hasNext()) {
					Cell cell = cellIterator.next();
					String cellValue = dataFormatter.formatCellValue(cell);
					insertRowMapCell(j, rowMap, cellValue);
					j++;
				}

				switch (rowMap.get("entryType")) {
					case "Test":
						testDefinition = testRepo.getByStandardCode(rowMap.get("mayoId"));
						testDefinition.setNormalRangeText(rowMap.get("referenceValues"));
						testRepo.save(testDefinition);
						break;
				}
			}
			workbook.close();
			inputStream.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void importTestsFromExcel() {
		try {
			File excelFile = new File("C:\\Users\\mpoladian\\Documents\\LIS-Codes\\Sep-8-2017\\MayoRepository-Official.xls");
			FileInputStream inputStream = new FileInputStream(excelFile);
			Workbook workbook = new HSSFWorkbook(inputStream);
			Sheet firstSheet = workbook.getSheetAt(0);

			DataFormatter dataFormatter = new DataFormatter();

			Iterator<Row> iterator = firstSheet.iterator();
			//skip the first 2 rows
			iterator.next();
			iterator.next();

			Map<String, String> rowMap = new HashMap<String, String>();

			TestDefinition testDefinition = null;
			TestQuestion testQuestion = null;

			while (iterator.hasNext()) {
				Row nextRow = iterator.next();
				Iterator<Cell> cellIterator = nextRow.cellIterator();

				int j = 0;

				while (cellIterator.hasNext()) {
					Cell cell = cellIterator.next();
					String cellValue = dataFormatter.formatCellValue(cell);
					insertRowMapCell(j, rowMap, cellValue);
					j++;
				}

				switch (rowMap.get("entryType")) {
					case "Test":
						testDefinition = saveTest(rowMap, "Yes");
						break;
					case "Specimen":
						if (rowMap.get("testReferenced").equals(testDefinition.getStandardCode())) {
							TestSpecimen specimen = getSpecimenFromRowMap(rowMap);
							//							specimen.setTestDefinition(testDefinition);
							specimenRepo.save(specimen);
						}
						break;
					case "Result":
						if (rowMap.get("testReferenced").equals(testDefinition.getStandardCode())) {
							TestResult result = getResultFromRowMap(rowMap);
							result.setTestDefinition(testDefinition);
							resultRepo.save(result);
						}
						break;
					case "Alt Specimen":
						if (rowMap.get("testReferenced").equals(testDefinition.getStandardCode())) {
							//							TestAltSpecimen altSpecimen = getAltSpecimenFromRowMap(rowMap);
							//							altSpecimen.setTestDefinition(testDefinition);
							//							altSpecimenRepo.save(altSpecimen);
						}
						break;
					case "Billing Component":
						if (rowMap.get("testReferenced").equals(testDefinition.getStandardCode())) {
							//							TestBillingComponent billingComponent = getBillingComponentFromRowMap(rowMap);
							//							billingComponent.setTestDefinition(testDefinition);
							//							billingComponentRepo.save(billingComponent);
						}
						break;
					case "Form":
						if (rowMap.get("testReferenced").equals(testDefinition.getStandardCode())) {
							TestForm form = getFormFromRowMap(rowMap);
							form.setTestDefinition(testDefinition);
							formRepo.save(form);
						}
						break;
					case "Order Question":
						if (rowMap.get("testReferenced").equals(testDefinition.getStandardCode())) {
							testQuestion = getOrderQuestionFromRowMap(rowMap);
							testQuestion.setTestDefinition(testDefinition);
							testQuestion = questionRepo.save(testQuestion);
						}
						break;
					case "Option":
						if (rowMap.get("testReferenced").equals(testDefinition.getStandardCode())) {
							TestQuestionOption option = getOptionFromRowMap(rowMap);
							option.setTestQuestion(testQuestion);
							optionRepo.save(option);
						}
						break;
				}

			}

			workbook.close();
			inputStream.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void importExtraTestsFromExcel() {
		try {
			File excelFile = new File("C:\\Users\\mpoladian\\Documents\\LIS-Codes\\Sep-8-2017\\MayoRepository-Official.xls");
			FileInputStream inputStream = new FileInputStream(excelFile);
			Workbook workbook = new HSSFWorkbook(inputStream);
			Sheet firstSheet = workbook.getSheetAt(0);

			DataFormatter dataFormatter = new DataFormatter();

			LkpTestEntryType entryTypeReflexTest = entryTypeRepo.getEntryTypeByCode("reflexTest");
			LkpTestEntryType entryTypeComponent = entryTypeRepo.getEntryTypeByCode("component");

			Iterator<Row> iterator = firstSheet.iterator();
			//skip the first 2 rows
			iterator.next();
			iterator.next();

			Map<String, String> rowMap = new HashMap<String, String>();

			TestDefinition testReferenced = null;
			ExtraTest extraTest = null;
			TestQuestion orderQuestion = null;

			while (iterator.hasNext()) {
				Row nextRow = iterator.next();
				Iterator<Cell> cellIterator = nextRow.cellIterator();

				int j = 0;

				while (cellIterator.hasNext()) {
					Cell cell = cellIterator.next();
					String cellValue = dataFormatter.formatCellValue(cell);
					insertRowMapCell(j, rowMap, cellValue);
					j++;
				}

				System.out.println(rowMap.get("testReferenced"));
				TestDefinition currentRowTest = testRepo.getByStandardCode(rowMap.get("testReferenced"));
				//				currentRowTest.setTestSpecimens(specimenRepo.getByTestId(currentRowTest));
				currentRowTest.setTestResults(resultRepo.getByTestId(currentRowTest));
				//				currentRowTest.setTestAltSpecimens(altSpecimenRepo.getByTestId(currentRowTest));
				//				currentRowTest.setTestBillingComponents(billingComponentRepo.getByTestId(currentRowTest));
				currentRowTest.setTestForms(formRepo.getByTestId(currentRowTest));
				currentRowTest.setTestQuestions(questionRepo.getByTestId(currentRowTest));
				switch (rowMap.get("entryType")) {
					case "Test":
						testReferenced = testRepo.getByStandardCode(rowMap.get("mayoId"));
						break;
					case "Specimen":
						if (!currentRowTest.getStandardCode().equals(testReferenced.getStandardCode())) {
							TestSpecimen specimen = getSpecimenFromRowMap(rowMap);
							//							if (!currentRowTest.getTestSpecimens().contains(specimen)) {
							//								specimen.setExtraTest(extraTest);
							//								specimen.setTestDefinition(currentRowTest);
							//								specimenRepo.save(specimen);
							//							}
						}
						break;
					case "Result":
						if (!currentRowTest.getStandardCode().equals(testReferenced.getStandardCode())) {
							TestResult result = getResultFromRowMap(rowMap);
							if (!currentRowTest.getTestResults().contains(result)) {
								//								result.setExtraTest(extraTest);
								result.setTestDefinition(currentRowTest);
								resultRepo.save(result);
							}
						}
						break;
					case "Alt Specimen":
						if (!currentRowTest.getStandardCode().equals(testReferenced.getStandardCode())) {
							//							TestAltSpecimen altSpecimen = getAltSpecimenFromRowMap(rowMap);
							//							if (!currentRowTest.getTestAltSpecimens().contains(altSpecimen)) {
							//								altSpecimen.setExtraTest(extraTest);
							//								altSpecimen.setTestDefinition(currentRowTest);
							//								altSpecimenRepo.save(altSpecimen);
							//							}
						}
						break;
					case "Billing Component":
						if (!currentRowTest.getStandardCode().equals(testReferenced.getStandardCode())) {
							//							TestBillingComponent billingComponent = getBillingComponentFromRowMap(rowMap);
							//							if (!currentRowTest.getTestBillingComponents().contains(billingComponent)) {
							//								billingComponent.setExtraTest(extraTest);
							//								billingComponent.setTestDefinition(currentRowTest);
							//								billingComponentRepo.save(billingComponent);
							//							}
						}
						break;
					case "Form":
						if (!currentRowTest.getStandardCode().equals(testReferenced.getStandardCode())) {
							TestForm form = getFormFromRowMap(rowMap);
							if (!currentRowTest.getTestForms().contains(form)) {
								form.setExtraTest(extraTest);
								form.setTestDefinition(currentRowTest);
								formRepo.save(form);
							}
						}
						break;
					case "Order Question":
						if (!currentRowTest.getStandardCode().equals(testReferenced.getStandardCode())) {
							orderQuestion = getOrderQuestionFromRowMap(rowMap);
							if (!currentRowTest.getTestQuestions().contains(orderQuestion)) {
								orderQuestion.setExtraTest(extraTest);
								orderQuestion.setTestDefinition(currentRowTest);
								orderQuestion = questionRepo.save(orderQuestion);
							} else {
								orderQuestion = null;
							}
						}
						break;
					case "Option":
						if (!currentRowTest.getStandardCode().equals(testReferenced.getStandardCode()) && orderQuestion != null) {
							TestQuestionOption option = getOptionFromRowMap(rowMap);
							option.setTestQuestion(orderQuestion);
							optionRepo.save(option);
						}
						break;
					case "Reflex Test":
						extraTest = saveExtraTest(rowMap, entryTypeReflexTest, testReferenced);
						break;
					case "Component":
						extraTest = saveExtraTest(rowMap, entryTypeComponent, testReferenced);
						break;
				}

			}

			workbook.close();
			inputStream.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private ExtraTest saveExtraTest(Map<String, String> rowMap, LkpTestEntryType entryType, TestDefinition testReferenced) {
		TestDefinition extraTestDefinition = testRepo.getByStandardCode(rowMap.get("mayoId"));
		if (//rowMap.get("orderableSeparately").startsWith("No") && 
		!rowMap.get("mayoId").equals(rowMap.get("testReferenced")) &&
				extraTestDefinition == null) {
			System.out.println("Test with ID:" + rowMap.get("mayoId") + " was not found, creating!");
			extraTestDefinition = saveTest(rowMap, rowMap.get("orderableSeparately"));
		}
		ExtraTest extraTest = new ExtraTest();
		//extraTest.setAlwaysPerformed(rowMap.get("alwaysPerformed"));
		extraTest.setAlwaysPerformed(rowMap.get("alwaysPerformed").equalsIgnoreCase("Yes") ? Boolean.TRUE : Boolean.FALSE);
		extraTest.setEntryType(entryType);
		extraTest.setTest(testReferenced);
		extraTest.setExtraTest(extraTestDefinition);
		return extraTestRepo.save(extraTest);
	}

	private void insertRowMapCell(int j, Map<String, String> rowMap, String cellValue) {
		switch (j) {
			case 0:
				rowMap.put("testReferenced", cellValue);
				break;
			case 1:
				rowMap.put("entryType", cellValue);
				break;
			case 2:
				rowMap.put("mayoId", cellValue);
				break;
			case 3:
				rowMap.put("description", cellValue);
				break;
			case 4:
				rowMap.put("reportingDescription", cellValue);
				break;
			case 5:
				rowMap.put("method", cellValue);
				break;
			case 6:
				rowMap.put("collectionContainer", cellValue);
				break;
			case 7:
				rowMap.put("temperature", cellValue);
				break;
			case 8:
				rowMap.put("stability", cellValue);
				break;
			case 9:
				rowMap.put("specimenVolume", cellValue);
				break;
			case 10:
				rowMap.put("unitOfMeasure", cellValue);
				break;
			case 11:
				rowMap.put("specimenRequirements", cellValue);
				break;
			case 12:
				rowMap.put("minimumVolume", cellValue);
				break;
			case 13:
				rowMap.put("rejectDueTo", cellValue);
				break;
			case 14:
				rowMap.put("referenceValues", cellValue);
				break;
			case 15:
				rowMap.put("daysTimesPerformed", cellValue);
				break;
			case 16:
				rowMap.put("analyticTime", cellValue);
				break;
			case 17:
				rowMap.put("maximumLabTime", cellValue);
				break;
			case 18:
				rowMap.put("cptUnits", cellValue);
				break;
			case 19:
				rowMap.put("cptCode", cellValue);
				break;
			case 20:
				rowMap.put("loincCode", cellValue);
				break;
			case 21:
				rowMap.put("loincAttributes", cellValue);
				break;
			case 22:
				rowMap.put("alwaysPerformed", cellValue);
				break;
			case 23:
				rowMap.put("orderableSeparately", cellValue);
				break;
			case 24:
				rowMap.put("performingLocation", cellValue);
				break;
			case 25:
				rowMap.put("advisoryInformation", cellValue);
				break;
			case 26:
				rowMap.put("additionalTestingRequirements", cellValue);
				break;
			case 27:
				rowMap.put("shippingInstructions", cellValue);
				break;
			case 28:
				rowMap.put("necessaryInformation", cellValue);
				break;
			case 29:
				rowMap.put("forms", cellValue);
				break;
			case 30:
				rowMap.put("urinePreservativeCollectionOptions", cellValue);
				break;
		}
	}

	private TestDefinition saveTest(Map<String, String> rowMap, String orderableSeparately) {
		TestDefinition testDefinition = new TestDefinition();
		//		testDefinition.setStandardCode(rowMap.get("mayoId"));
		//		testDefinition.setAdditionalTestingRequirements(rowMap.get("additionalTestingRequirements"));
		//		testDefinition.setAdvisoryInformation(rowMap.get("advisoryInformation"));
		//		testDefinition.setAnalyticTime(rowMap.get("analyticTime"));
		//		testDefinition.setClinicalInformation(rowMap.get("clinicalInformation"));
		//		//testDefinition.setLkpContainerTYpe(rowMap.get("collectionContainer")); its a lkp now
		//		testDefinition.setCptCode(rowMap.get("cptCode"));
		//		testDefinition.setCptUnits(rowMap.get("cptUnits"));
		//		testDefinition.setDaysTimesPerformed(rowMap.get("daysTimesPerformed"));
		//		testDefinition.setDescription(rowMap.get("description"));
		//		testDefinition.setForms(rowMap.get("forms"));
		//		testDefinition.setGeneticsTestInformation(rowMap.get("geneticsTestInformation"));
		//		testDefinition.setLoincCode(rowMap.get("loincCode"));
		//		testDefinition.setMaximumLabTime(rowMap.get("maximumLabTime"));
		//		//testDefinition.setMethod(rowMap.get("method")); its a lkp now
		//		testDefinition.setMinimumVolume(rowMap.get("minimumVolume"));
		//		testDefinition.setNecessaryInformation(rowMap.get("necessaryInformation"));
		//		testDefinition.setPerformingLocation(rowMap.get("performingLocation"));
		//		//		testDefinition.setReferenceValues(rowMap.get("referenceValues"));
		//		testDefinition.setNormalRangeText(rowMap.get("referenceValues"));
		//		testDefinition.setRejectDueTo(rowMap.get("rejectDueTo"));
		//		testDefinition.setReportingDescription(rowMap.get("reportingDescription"));
		//		testDefinition.setShippingInstructions(rowMap.get("shippingInstructions"));
		//		testDefinition.setSpecialInstructions(rowMap.get("specialInstructions"));
		//		testDefinition.setSpecimenRequirements(rowMap.get("specimenRequirements"));
		//		testDefinition.setSpecimenVolume(rowMap.get("specimenVolume"));
		//		testDefinition.setSupportiveData(rowMap.get("supportiveData"));
		//		//testDefinition.setUnitOfMeasure(rowMap.get("unitOfMeasure"));
		//		testDefinition.setUrinePreservativeCollectionOptions(rowMap.get("urinePreservativeCollectionOptions"));
		//		//testDefinition.setOrderableSeparately(orderableSeparately); its boolean now
		return testRepo.save(testDefinition);
	}

	private TestSpecimen getSpecimenFromRowMap(Map<String, String> rowMap) {
		TestSpecimen specimen = new TestSpecimen();
		//		specimen.setStandardCode(rowMap.get("mayoId"));
		specimen.setDescription(rowMap.get("description"));
		//		specimen.setTemperature(rowMap.get("temperature"));
		//		specimen.setStability(rowMap.get("stability"));
		return specimen;
	}

	private TestQuestion getOrderQuestionFromRowMap(Map<String, String> rowMap) {
		TestQuestion orderQuestion = new TestQuestion();
		orderQuestion.setStandardCode(rowMap.get("mayoId"));
		orderQuestion.setDescription(rowMap.get("description"));
		//orderQuestion.setLkpQuestionType(rowMap.get("method")); its a lkp now
		return orderQuestion;
	}

	//	private TestBillingComponent getBillingComponentFromRowMap(Map<String, String> rowMap) {
	//		TestBillingComponent billingComponent = new TestBillingComponent();
	//		billingComponent.setStandardCode(rowMap.get("mayoId"));
	//		billingComponent.setReportingDescription(rowMap.get("reportingDescription"));
	//		billingComponent.setCptUnits(rowMap.get("cptUnits"));
	//		billingComponent.setCptCode(rowMap.get("cptCode"));
	//		return billingComponent;
	//	}

	//	private TestAltSpecimen getAltSpecimenFromRowMap(Map<String, String> rowMap) {
	//		TestAltSpecimen altSpecimen = new TestAltSpecimen();
	//		altSpecimen.setStandardCode(rowMap.get("mayoId"));
	//		altSpecimen.setDescription(rowMap.get("description"));
	//		altSpecimen.setTemperature(rowMap.get("temperature"));
	//		altSpecimen.setStability(rowMap.get("stability"));
	//		return altSpecimen;
	//	}

	private TestForm getFormFromRowMap(Map<String, String> rowMap) {
		TestForm form = new TestForm();
		form.setStandardCode(rowMap.get("mayoId"));
		form.setDescription(rowMap.get("description"));
		form.setReportingDescription(rowMap.get("reportingDescription"));
		form.setMethod(rowMap.get("method"));
		return form;
	}

	private TestQuestionOption getOptionFromRowMap(Map<String, String> rowMap) {
		TestQuestionOption option = new TestQuestionOption();
		option.setStandardCode(rowMap.get("mayoId"));
		option.setDescription(rowMap.get("description"));
		return option;
	}

	private TestResult getResultFromRowMap(Map<String, String> rowMap) {
		TestResult result = new TestResult();
		result.setStandardCode(rowMap.get("mayoId"));
		result.setDescription(rowMap.get("description"));
		result.setReportingDescription(rowMap.get("reportingDescription"));
		//result.setMethod(rowMap.get("method"));
		//result.setLkpResultType(rowMap.get("method"));// its a lkp now
		//result.setUnitOfMeasure(rowMap.get("unitOfMeasure"));
		result.setLoincCode(rowMap.get("loincCode"));
		//result.setLoincAttributes(rowMap.get("loincAttributes"));
		return result;
	}

	private void extractDistinctUnits() {
		Map<String, String> distinctSignum = new HashMap<String, String>();
		distinctSignum.put("positive", "إيجابي");
		distinctSignum.put("negative", "سلبي");
		distinctSignum.forEach((k, v) ->
			{
				LkpSignum lkpSignum = new LkpSignum();
				lkpSignum.setCode(k);
				TransField name = new TransField();
				name.put("en_us", k);
				name.put("ar_jo", v);
				lkpSignum.setName(name);
				TransField desc = new TransField();
				desc.put("en_us", k);
				desc.put("ar_jo", v);
				lkpSignum.setDescription(desc);
				signumRepo.save(lkpSignum);
			});

		Map<String, String> distinctAgeUnits = new HashMap<String, String>();
		distinctAgeUnits.put("year", "سنة");
		distinctAgeUnits.put("month", "شهر");
		distinctAgeUnits.put("week", "اسبوع");
		distinctAgeUnits.put("day", "يوم");
		distinctAgeUnits.forEach((k, v) ->
			{
				LkpAgeUnit lkpAgeUnit = new LkpAgeUnit();
				lkpAgeUnit.setCode(k);
				TransField name = new TransField();
				name.put("en_us", k);
				name.put("ar_jo", v);
				lkpAgeUnit.setName(name);
				TransField desc = new TransField();
				desc.put("en_us", k);
				desc.put("ar_jo", v);
				lkpAgeUnit.setDescription(desc);
				ageUnitRepo.save(lkpAgeUnit);
			});
	}

	public void importSpecimenTypes() {
		try {

			File file = new File("C:\\Users\\mpoladian\\eclipse-workspace\\EHOPE-LIS\\trunk\\Lkp Generation Util\\specimen-types.tsv");

			BufferedReader b = new BufferedReader(new FileReader(file));

			String readLine = "";

			System.out.println("Reading file using Buffered Reader");

			while ((readLine = b.readLine()) != null) {
				String[] lineArray = readLine.split("\t");
				System.out.println(lineArray[0] + ": " + lineArray[1]);
				LkpSpecimenType specimenType = new LkpSpecimenType();
				specimenType.setCode(lineArray[0]);
				TransField name = new TransField();
				name.put("en_us", lineArray[1]);
				specimenType.setName(name);
				TransField description = new TransField();
				description.put("en_us", lineArray[1]);
				specimenType.setDescription(description);
				//lkpService.createLkp(specimenType);
			}
			b.close();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void importSpecimenTemperature() {
		try {

			File file = new File(
					"C:\\Users\\mpoladian\\eclipse-workspace\\EHOPE-LIS\\trunk\\Lkp Generation Util\\specimen-temperatures.tsv");

			BufferedReader b = new BufferedReader(new FileReader(file));

			String readLine = "";

			System.out.println("Reading file using Buffered Reader");

			while ((readLine = b.readLine()) != null) {
				String[] lineArray = readLine.split("\t");
				System.out.println(lineArray[0] + ": " + lineArray[1]);
				LkpSpecimenTemperature specimenTemperature = new LkpSpecimenTemperature();
				specimenTemperature.setCode(lineArray[0]);
				TransField name = new TransField();
				name.put("en_us", lineArray[1]);
				specimenTemperature.setName(name);
				TransField description = new TransField();
				description.put("en_us", lineArray[1]);
				specimenTemperature.setDescription(description);
				//lkpService.createLkp(specimenTemperature);
			}
			b.close();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void createSpecimenStabilityUnits() {
		LkpSpecimenStabilityUnit hourUnit = new LkpSpecimenStabilityUnit();
		hourUnit.setCode("hour");
		TransField hourName = new TransField();
		hourName.put("en_us", "Hour");
		hourName.put("ar_jo", "ساعة");
		hourUnit.setName(hourName);
		TransField hourDescription = new TransField();
		hourDescription.put("en_us", "Hour");
		hourDescription.put("ar_jo", "ساعة");
		hourUnit.setDescription(hourDescription);

		LkpSpecimenStabilityUnit dayUnit = new LkpSpecimenStabilityUnit();
		dayUnit.setCode("day");
		TransField dayName = new TransField();
		dayName.put("en_us", "Day");
		dayName.put("ar_jo", "يوم");
		dayUnit.setName(dayName);
		TransField dayDescription = new TransField();
		dayDescription.put("en_us", "Day");
		dayDescription.put("ar_jo", "يوم");
		dayUnit.setDescription(dayDescription);

		lkpService.createTenantedLkp(hourUnit.getClass(), hourUnit);
		lkpService.createTenantedLkp(dayUnit.getClass(), dayUnit);
	}

	public void mapSpecimensToStability() {
		//		List<TestAltSpecimen> specimens = altSpecimenRepo.findAll();
		List<LkpSpecimenStabilityUnit> units = specimenStabilityUnitRepo.findAll();
		LkpSpecimenStabilityUnit hourUnit = null;
		LkpSpecimenStabilityUnit dayUnit = null;
		for (LkpSpecimenStabilityUnit unit : units) {
			if (unit.getCode().equals("hour")) {
				hourUnit = unit;
			} else if (unit.getCode().equals("day")) {
				dayUnit = unit;
			}
		}
		//		for (TestAltSpecimen sp : specimens) {
		//			String stability = sp.getStability();
		//			if (stability != null && !stability.isEmpty()) {
		//				Pattern pattern = Pattern.compile("(\\d+)\\s*(\\w+)s?");
		//				Matcher matcher = pattern.matcher(stability);
		//				if (matcher.find()) {
		//					Long digit = Long.parseLong(matcher.group(1));
		//					sp.setStabilityDigit(digit);
		//
		//					String unit = matcher.group(2);
		//					System.out.println(unit);
		//					if (unit.startsWith("hour")) {
		//						sp.setStabilityUnit(hourUnit);
		//					} else if (unit.startsWith("day")) {
		//						sp.setStabilityUnit(dayUnit);
		//					}
		//				}
		//				altSpecimenRepo.save(sp);
		//			}
		//		}
	}

	//	public void makeOneSpecimenPerTest() {
	//		//		SearchCriterion filterByMayoCode = new SearchCriterion();
	//		//		filterByMayoCode.setField("mayoCode");
	//		//		filterByMayoCode.setValue("mtp");
	//		//		filterByMayoCode.setOperator(FilterOperator.neq);
	//		List<SearchCriterion> filters = new ArrayList<SearchCriterion>();
	//		//		filters.add(filterByMayoCode);
	//		List<TestSpecimen> allSpecimes = specimenRepo.find(filters, TestSpecimen.class, JunctionOperator.And, "testDefinition");
	//		allSpecimes.forEach(ts ->
	//			{
	//				TestDefinition td = ts.getTestDefinition();
	//				td.setSpecimen(ts);
	//				testRepo.save(td);
	//			});
	//	}

	@Override
	protected TestDefinitionRepo getRepository() {
		return testRepo;
	}

}

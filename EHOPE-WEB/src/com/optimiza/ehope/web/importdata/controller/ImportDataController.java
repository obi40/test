package com.optimiza.ehope.web.importdata.controller;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import com.optimiza.core.base.helper.SearchCriterion;
import com.optimiza.core.base.helper.SearchCriterion.FilterOperator;
import com.optimiza.core.common.business.exception.BusinessException;
import com.optimiza.core.common.business.exception.BusinessException.ErrorSeverity;
import com.optimiza.core.common.util.CollectionUtil;
import com.optimiza.core.common.util.StringUtil;
import com.optimiza.core.lkp.model.LkpGender;
import com.optimiza.core.lkp.service.LkpService;
import com.optimiza.ehope.lis.helper.ExcelSheet;
import com.optimiza.ehope.lis.lkp.model.LkpAgeUnit;
import com.optimiza.ehope.lis.model.EmrPatientInfo;
import com.optimiza.ehope.lis.model.TestNormalRange;
import com.optimiza.ehope.lis.model.TestResult;
import com.optimiza.ehope.lis.service.DoctorService;
import com.optimiza.ehope.lis.service.EmrPatientInfoService;
import com.optimiza.ehope.lis.service.HistoricalOrderService;
import com.optimiza.ehope.lis.service.InsProviderService;
import com.optimiza.ehope.lis.service.TestDefinitionService;
import com.optimiza.ehope.lis.service.TestResultService;
import com.optimiza.ehope.lis.util.ExcelUtil;
import com.optimiza.ehope.web.helper.ExcelView;

@RestController
@RequestMapping("/services")
public class ImportDataController {

	@Autowired
	private HistoricalOrderService historicalOrderService;
	@Autowired
	private DoctorService doctorService;
	@Autowired
	private EmrPatientInfoService patientService;
	@Autowired
	private TestDefinitionService testService;
	@Autowired
	private TestResultService resultService;
	//	@Autowired
	//	private TestNormalRangeService normalRangeService;
	@Autowired
	private LkpService lkpService;
	@Autowired
	private ExcelView excelView;
	@Autowired
	private InsProviderService providerService;

	@RequestMapping(value = "/importDoctors.srvc", method = RequestMethod.POST, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<String> importDoctors(@RequestParam(value = "doctors", required = false) MultipartFile excel)
			throws IOException {

		doctorService.importDoctors(excel);

		return new ResponseEntity<String>("Success", HttpStatus.OK);
	}

	@RequestMapping(value = "/importPatients.srvc", method = RequestMethod.POST, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<List<EmrPatientInfo>> importPatients(@RequestParam(value = "patients", required = false) MultipartFile excel)
			throws IOException {

		List<EmrPatientInfo> invalidPatients = patientService.importPatients(excel);

		return new ResponseEntity<List<EmrPatientInfo>>(invalidPatients, HttpStatus.OK);
	}

	@RequestMapping(value = "/importTests.srvc", method = RequestMethod.POST, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<List<Integer>> importTests(@RequestParam(value = "tests", required = true) MultipartFile excel)
			throws IOException {

		List<Integer> failed = testService.importTest(excel);

		return new ResponseEntity<List<Integer>>(failed, HttpStatus.OK);
	}

	@RequestMapping(value = "/importMultiResultTests.srvc", method = RequestMethod.POST, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<String> importMultiResultTests(@RequestParam(value = "multiResultTests", required = true) MultipartFile excel)
			throws IOException {

		testService.importMultiResultTests(excel);

		return new ResponseEntity<String>("Success", HttpStatus.OK);
	}

	@RequestMapping(value = "/importFailedNormalRanges.srvc", method = RequestMethod.POST, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public void importFailedNormalRanges(@RequestParam(value = "normalRanges", required = true) MultipartFile excel) throws IOException {
		testService.importFailedNormalRanges(excel);
	}

	@RequestMapping(value = "/fixNormalRanges.srvc", method = RequestMethod.POST, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<List<Map<String, String>>> fixNormalRanges(
			@RequestParam(value = "normalRanges", required = true) MultipartFile excel)
			throws IOException {
		return new ResponseEntity<List<Map<String, String>>>(testService.fixNormalRanges(excel), HttpStatus.OK);
	}

	@RequestMapping(value = "/importNormalRanges.srvc", method = RequestMethod.POST, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public void importNormalRanges(@RequestParam(value = "normalRanges", required = true) MultipartFile excel,
			@RequestParam(value = "resultCode", required = false) String debugCode)
			throws IOException {

		Integer accepted = 0;
		List<List<String>> rejectedList = new ArrayList<List<String>>();
		List<String> manipulatedList = new ArrayList<String>();

		String fileName = excel.getOriginalFilename();
		InputStream inputStream = excel.getInputStream();

		String extension = fileName.substring(fileName.lastIndexOf('.') + 1);
		Workbook workbook;
		if (extension.equals("xls")) {
			workbook = new HSSFWorkbook(inputStream);
		} else {
			workbook = new XSSFWorkbook(inputStream);
		}

		//		LkpAgeUnit dayUnit = null;
		//		LkpAgeUnit weekUnit = null;
		//		LkpAgeUnit monthUnit = null;
		LkpAgeUnit yearUnit = null;
		List<LkpAgeUnit> ageUnits = lkpService.findAnyLkp(Arrays.asList(), LkpAgeUnit.class, null);
		for (LkpAgeUnit ageUnit : ageUnits) {
			switch (ageUnit.getCode()) {
				//				case "day":
				//					dayUnit = ageUnit;
				//					break;
				//				case "week":
				//					weekUnit = ageUnit;
				//					break;
				//				case "month":
				//					monthUnit = ageUnit;
				//					break;
				case "year":
					yearUnit = ageUnit;
					break;
			}
		}
		List<LkpGender> genders = lkpService.findAnyLkp(Arrays.asList(), LkpGender.class, null);
		LkpGender male = null;
		LkpGender female = null;
		for (LkpGender gender : genders) {
			switch (gender.getCode()) {
				case "MALE":
					male = gender;
					break;
				case "FEMALE":
					female = gender;
					break;
			}
		}

		Sheet normalRangeSheet = workbook.getSheetAt(0);
		Iterator<Row> normalRangeIterator = normalRangeSheet.iterator();
		normalRangeIterator.next();
		normalRangeIterator.next();
		normalRangeIterator.next();
		while (normalRangeIterator.hasNext()) {
			Row row = normalRangeIterator.next();
			Boolean[] changedValues = null;
			try {
				String resultCode = ExcelUtil.getStringFromCell(row.getCell(1));
				if (resultCode.equals(debugCode)) {
					System.out.println(debugCode);
				}
				TestResult testResult = resultService.findOne(
						Arrays.asList(
								new SearchCriterion("standardCode", resultCode, FilterOperator.eq),
								new SearchCriterion("testDefinition.standardCode", resultCode, FilterOperator.eq)),
						TestResult.class, "resultValueType", "primaryUnitType", "testDefinition");
				if (testResult == null) {
					throw new BusinessException("Test Result not found", "testResultNotFound", ErrorSeverity.ERROR);
				}

				String norCat1 = ExcelUtil.getStringFromCell(row.getCell(2));
				//			String norFromConv = getStringFromCell(row.getCell(6));
				//			String norToConv = getStringFromCell(row.getCell(7));
				//			String norFromSi = getStringFromCell(row.getCell(8));
				//			String norToSi = getStringFromCell(row.getCell(9));
				String convNormalRange = ExcelUtil.getStringFromCell(row.getCell(11));
				String[] convNormalRanges = convNormalRange.split("\\R");
				String siNormalRange = ExcelUtil.getStringFromCell(row.getCell(12));
				String[] siNormalRanges = siNormalRange.split("\\R");
				if (StringUtil.isEmpty(convNormalRange) && StringUtil.isEmpty(siNormalRange)) {
					throw new BusinessException("Empty values (SI + Conv.)!", "emptySiAndConv", ErrorSeverity.ERROR);
				}

				Integer ageFrom = Integer.valueOf((int) row.getCell(13).getNumericCellValue());
				String ageFromUnit = row.getCell(14).getStringCellValue();
				Integer ageTo = Integer.valueOf((int) row.getCell(15).getNumericCellValue());
				String ageToUnit = row.getCell(16).getStringCellValue();

				String note1 = row.getCell(18).getStringCellValue().trim();
				double sex = row.getCell(19).getNumericCellValue();
				List<TestNormalRange> normalRanges = new ArrayList<TestNormalRange>();
				if (StringUtil.isEmpty(note1)) {
					TestNormalRange normalRange = new TestNormalRange();
					normalRange.setPrintOrder(1);
					normalRange.setTestResult(testResult);
					testService.setNormalRangeAge(normalRange, ageFrom, ageTo, ageFromUnit, ageToUnit, ageUnits);
					changedValues = testService.setNormalRangeValues(testResult, normalRange, siNormalRange, convNormalRange);
					if (norCat1.equals("F")) {
						if (sex == 1) {
							normalRange.setSex(male);
						} else if (sex == 2) {
							normalRange.setSex(female);
						}
					}
					normalRanges.add(normalRange);
				} else {
					String[] notes = note1.split("\\R");
					for (int i = 0; i < notes.length; i++) {
						String note = notes[i].replaceAll("", "").trim();
						TestNormalRange normalRange = new TestNormalRange();
						normalRange.setPrintOrder(i + 1);
						normalRange.setTestResult(testResult);
						testService.setNormalRangeAge(normalRange, ageFrom, ageTo, ageFromUnit, ageToUnit, ageUnits);
						switch (note.toLowerCase()) {
							case "adult":
							case "adults":
								normalRange.setAgeFromUnit(yearUnit);
								normalRange.setAgeFrom(18);
								normalRange.setAgeFromComparator(">=");
								normalRange.setAgeToUnit(null);
								normalRange.setAgeTo(null);
								normalRange.setAgeToComparator(null);
								break;
							case "child":
							case "children":
								normalRange.setAgeFromUnit(null);
								normalRange.setAgeFrom(null);
								normalRange.setAgeFromComparator(null);
								normalRange.setAgeToUnit(yearUnit);
								normalRange.setAgeTo(18);
								normalRange.setAgeToComparator("<");
								break;
							case "male":
								normalRange.setSex(male);
							case "female":
								normalRange.setSex(female);
								break;
							default:
								normalRange.setCriterionValue(note);
								break;
						}

						if (norCat1.equals("F")) {
							if (sex == 1) {
								normalRange.setSex(male);
							} else if (sex == 2) {
								normalRange.setSex(female);
							}
						}
						String conv = null;
						try {
							conv = convNormalRanges[i];
						} catch (Exception e) {

						}
						String si = null;
						try {
							si = siNormalRanges[i];
						} catch (Exception e) {

						}
						changedValues = testService.setNormalRangeValues(testResult, normalRange, si, conv);
						normalRanges.add(normalRange);
					}
				}
				try {
					//normalRangeService.saveNormalRanges(normalRanges, testResult);
					accepted++;
				} catch (Exception e) {
					throw e;
				}
				if (changedValues != null && (changedValues[0] || changedValues[1])) {
					manipulatedList.add(testResult.getStandardCode());
				}
			} catch (Exception e) {
				//				List<String> errorRow = new ArrayList<String>();
				//				Iterator<Cell> cellIterator = row.cellIterator();
				//				DataFormatter df = new DataFormatter();
				//				while (cellIterator.hasNext()) {
				//					Cell cell = cellIterator.next();
				//					String stringCellValue = df.formatCellValue(cell).toString();//format everything to a String	
				//					errorRow.add(stringCellValue);
				//				}
				//				errorRow.add(e.getMessage());
				//				rejectedList.add(errorRow);
			}

		}

		workbook.close();

		System.out.println("Accepted: " + accepted);
		System.out.println("Rejected: " + rejectedList.size());

		Workbook newWorkbook = new XSSFWorkbook(); // new HSSFWorkbook() for generating `.xls` file

		//		Sheet sheet = newWorkbook.createSheet("Failed");
		//
		//		for (int i = 0; i < rejectedList.size(); i++) {
		//			List<String> errorRow = rejectedList.get(i);
		//			Row newRow = sheet.createRow(i);
		//			for (int j = 0; j < errorRow.size(); j++) {
		//				newRow.createCell(j).setCellValue(errorRow.get(j));
		//			}
		//		}
		Sheet sheet = newWorkbook.createSheet("manipulated");
		for (int i = 0; i < manipulatedList.size(); i++) {
			String changed = manipulatedList.get(i);
			Row newRow = sheet.createRow(i);
			newRow.createCell(0).setCellValue(changed);
		}

		FileOutputStream fileOut = new FileOutputStream("manipulated.xlsx");
		newWorkbook.write(fileOut);
		fileOut.close();

		// Closing the workbook
		newWorkbook.close();
	}

	@RequestMapping(value = "/importReferralPrices.srvc", method = RequestMethod.POST, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<String> importReferralPrices(@RequestParam(value = "referral", required = false) MultipartFile excel)
			throws IOException {
		testService.importReferralPrices(excel);

		return new ResponseEntity<String>("Success", HttpStatus.OK);
	}

	@RequestMapping(value = "/importInsurance.srvc", method = RequestMethod.POST, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<Object> importInsurance(@RequestParam(value = "insurance", required = false) MultipartFile excel)
			throws IOException {

		providerService.loadTeamLabInsurances(excel);
		return new ResponseEntity<Object>("", HttpStatus.OK);
	}

	@RequestMapping(value = "/downloadDoctorsTemplate", method = RequestMethod.POST)
	public ModelAndView downloadDoctorsTemplate() {
		return new ModelAndView(excelView, ExcelUtil.getModelMap(doctorService.getDoctorSheet()));
	}

	@RequestMapping(value = "/uploadDoctorsTemplate", method = RequestMethod.POST, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ModelAndView uploadDoctorsTemplate(@RequestParam(value = "file", required = true) MultipartFile file) {
		ExcelSheet es = doctorService.uploadDoctorsData(file);
		if (!CollectionUtil.isMapEmpty(es.getFailedRows())) {
			return new ModelAndView(excelView, ExcelUtil.getModelMap(es));
		} else {
			return null;
		}
	}

	@RequestMapping(value = "/downloadHistoricalOrdersTemplate", method = RequestMethod.POST)
	public ModelAndView downloadHistoricalOrdersTemplate() {
		return new ModelAndView(excelView, ExcelUtil.getModelMap(historicalOrderService.getHistoricalOrderSheet()));
	}

	@RequestMapping(value = "/uploadHistoricalOrdersTemplate", method = RequestMethod.POST, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ModelAndView uploadHistoricalOrdersTemplate(@RequestParam(value = "file", required = true) MultipartFile file) {
		ExcelSheet es = historicalOrderService.uploadHistoricalOrders(file);
		if (!CollectionUtil.isMapEmpty(es.getFailedRows())) {
			return new ModelAndView(excelView, ExcelUtil.getModelMap(es));
		} else {
			return null;
		}
	}

	@RequestMapping(value = "/downloadPatientsTemplate", method = RequestMethod.POST)
	public ModelAndView downloadPatientsTemplate() {
		return new ModelAndView(excelView, ExcelUtil.getModelMap(patientService.getPatientSheet()));
	}

	@RequestMapping(value = "/uploadPatientsTemplate", method = RequestMethod.POST, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ModelAndView uploadPatientsTemplate(@RequestParam(value = "file", required = true) MultipartFile file) {
		ExcelSheet es = patientService.uploadPatientsData(file);
		if (!CollectionUtil.isMapEmpty(es.getFailedRows())) {
			return new ModelAndView(excelView, ExcelUtil.getModelMap(es));
		} else {
			return null;
		}
	}

	@RequestMapping(value = "/downloadTestDefinitionsTemplate", method = RequestMethod.POST)
	public ModelAndView downloadTestDefinitionsTemplate() {
		return new ModelAndView(excelView, ExcelUtil.getModelMap(testService.getTestDefinitionSheet()));
	}

	@RequestMapping(value = "/uploadTestDefinitionsTemplate", method = RequestMethod.POST, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ModelAndView uploadTestDefinitionsTemplate(@RequestParam(value = "file", required = true) MultipartFile file) {
		//		ExcelSheet es = testService.uploadTestData(file);
		//		if (!CollectionUtils.isListEmpty(es.getFailedRows())) {
		//			return new ModelAndView(excelView, ExcelUtil.getModelMap(es));
		//		} else {
		//			return null;
		//		}
		return null;
	}

	@RequestMapping(value = "/downloadTestResultsTemplate", method = RequestMethod.POST)
	public ModelAndView downloadTestResultsTemplate() {
		return new ModelAndView(excelView, ExcelUtil.getModelMap(testService.getTestResultSheet()));
	}

	//	@RequestMapping(value = "/downloadTestQuestionsTemplate", method = RequestMethod.POST)
	//	public ModelAndView downloadTestQuestionsTemplate() {
	//		return new ModelAndView(excelView, ExcelUtil.getModelMap(testService.getTestQuestionSheet()));
	//	}

	@RequestMapping(value = "/downloadTestPricingTemplate", method = RequestMethod.POST)
	public ModelAndView downloadTestPricingTemplate() {
		return new ModelAndView(excelView, ExcelUtil.getModelMap(testService.getTestPricingSheet()));
	}

	@RequestMapping(value = "/importTeamLabHistorical.srvc", method = RequestMethod.POST, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<String> importTeamLabHistorical(
			@RequestParam(value = "mapper", required = true) MultipartFile mapper,
			@RequestParam(value = "allergy", required = true) MultipartFile allergy,
			@RequestParam(value = "hematology", required = true) MultipartFile hematology,
			@RequestParam(value = "letter", required = true) MultipartFile letter,
			@RequestParam(value = "microDetails", required = true) MultipartFile microDetails,
			@RequestParam(value = "microMaster", required = true) MultipartFile microMaster,
			@RequestParam(value = "protein", required = true) MultipartFile protein,
			@RequestParam(value = "seminal", required = true) MultipartFile seminal,
			@RequestParam(value = "stool", required = true) MultipartFile stool,
			@RequestParam(value = "urine", required = true) MultipartFile urine)
			throws IOException {

		historicalOrderService.importTeamLabHistorical(mapper, allergy, hematology, letter, microDetails, microMaster,
				protein, seminal, stool, urine);

		return new ResponseEntity<String>("Success", HttpStatus.OK);
	}

}

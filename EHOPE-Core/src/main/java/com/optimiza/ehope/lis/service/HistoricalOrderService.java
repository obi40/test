package com.optimiza.ehope.lis.service;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.optimiza.core.base.helper.SearchCriterion;
import com.optimiza.core.base.helper.SearchCriterion.FilterOperator;
import com.optimiza.core.base.service.GenericService;
import com.optimiza.core.common.annotation.InterceptorFree;
import com.optimiza.core.common.util.CollectionUtil;
import com.optimiza.core.common.util.SecurityUtil;
import com.optimiza.core.common.util.StringUtil;
import com.optimiza.core.lkp.helper.FieldType;
import com.optimiza.ehope.lis.helper.ExcelColumn;
import com.optimiza.ehope.lis.helper.ExcelSheet;
import com.optimiza.ehope.lis.model.EmrPatientInfo;
import com.optimiza.ehope.lis.model.HistoricalOrder;
import com.optimiza.ehope.lis.model.HistoricalResult;
import com.optimiza.ehope.lis.model.HistoricalTest;
import com.optimiza.ehope.lis.model.LabBranch;
import com.optimiza.ehope.lis.repo.HistoricalOrderRepo;
import com.optimiza.ehope.lis.util.ExcelUtil;

/**
 * HistoricalOrderService.java
 * 
 * @author Murad Poladian <mpoladian@optimizasolutions.com>
 * @since Nov/11/2018
 * 
 */
@Service("HistoricalOrderService")
public class HistoricalOrderService extends GenericService<HistoricalOrder, HistoricalOrderRepo> {

	@Autowired
	private HistoricalOrderRepo repo;

	@Autowired
	private HistoricalResultService historicalResultService;

	@Autowired
	private HistoricalTestService historicalTestService;

	@Autowired
	private EmrPatientInfoService patientService;

	@Autowired
	private LabBranchService branchService;

	@Override
	protected HistoricalOrderRepo getRepository() {
		return repo;
	}

	public ExcelSheet uploadHistoricalOrders(MultipartFile excel) {
		Workbook workbook = ExcelUtil.getWorkbookFromExcel(excel);
		Sheet sheet = workbook.getSheetAt(0);
		Iterator<Row> rowIterator = sheet.iterator();
		rowIterator.next();//skip
		rowIterator.next();//skip
		ExcelSheet es = getHistoricalOrderSheet();
		List<ExcelColumn> columns = es.getColumns();

		LabBranch branch = branchService.findOne(
				SearchCriterion.generateRidFilter(SecurityUtil.getCurrentUser().getBranchId(), FilterOperator.eq), LabBranch.class);

		Map<String, HistoricalOrder> historicalOrders = new HashMap<String, HistoricalOrder>();
		List<HistoricalOrder> historicalOrderList = repo.findAll();
		for (HistoricalOrder historicalOrder : historicalOrderList) {
			if (!historicalOrders.containsKey(historicalOrder.getOrderNumber())) {
				historicalOrders.put(historicalOrder.getOrderNumber(), historicalOrder);
			}
		}

		Map<String, HistoricalTest> historicalTests = new HashMap<String, HistoricalTest>();
		List<HistoricalTest> historicalTestList = historicalTestService.find(new ArrayList<>(), HistoricalTest.class, "order");
		for (HistoricalTest historicalTest : historicalTestList) {
			String key = historicalTest.getOrder().getOrderNumber() + "|" + historicalTest.getTestCode();
			if (!historicalTests.containsKey(key)) {
				historicalTests.put(key, historicalTest);
			}
		}

		Map<String, Object> transientFields = new HashMap<String, Object>();
		Row row = null;
		try {
			while (rowIterator.hasNext()) {
				row = rowIterator.next();
				if (ExcelUtil.isRowEmpty(row)) {
					continue;
				}
				transientFields.clear();
				try {
					HistoricalOrder order = ExcelUtil.createObjectFromRow(row, HistoricalOrder.class, columns, null, transientFields);
					order.setOrderNumber(branch.getCode() + "-" + order.getOrderNumber());
					if (historicalOrders.containsKey(order.getOrderNumber())) {
						order = historicalOrders.get(order.getOrderNumber());
					} else {
						order = repo.save(order);
						historicalOrders.put(order.getOrderNumber(), order);
					}

					HistoricalTest test = historicalTests.get(order.getOrderNumber() + "|" + transientFields.get("testCode"));
					if (test == null) {
						test = new HistoricalTest();
						test.setOrder(order);
						test.setComments((String) transientFields.get("comments"));
						test.setTestCode((String) transientFields.get("testCode"));
						test = historicalTestService.createHistoricalTest(test);
						historicalTests.put(order.getOrderNumber() + "|" + transientFields.get("testCode"), test);
					} else if (!StringUtil.isEmpty((String) transientFields.get("comments"))) {
						String oldComments = test.getComments();
						String newComments = (String) transientFields.get("comments");
						if (StringUtil.isEmpty(oldComments)) {
							test.setComments(newComments);
							test = historicalTestService.updateHistoricalTest(test);
						} else if (!oldComments.contains(newComments)) {
							test.setComments(oldComments + " " + newComments);
							test = historicalTestService.updateHistoricalTest(test);
						}
					}

					HistoricalResult result = new HistoricalResult();
					result.setTest(test);
					result.setResultCode((String) transientFields.get("resultCode"));
					result.setResultValue((String) transientFields.get("resultValue"));
					result.setNormalRangePrefix((String) transientFields.get("normalRangePrefix"));
					result.setConvNormalRange((String) transientFields.get("convNormalRange"));
					result.setSiNormalRange((String) transientFields.get("siNormalRange"));
					result.setConvUnit((String) transientFields.get("convUnit"));
					result.setSiUnit((String) transientFields.get("siUnit"));
					historicalResultService.createHistoricalResult(result);
				} catch (Exception e) {
					ExcelUtil.handleExcelExceptions(es, row, e);
				}
			}
			workbook.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return es;
	}

	/**
	 * Get the columns that are generated for the download/upload process.
	 * 
	 * @return List
	 */
	public ExcelSheet getHistoricalOrderSheet() {
		List<ExcelColumn> rootColumns = new ArrayList<ExcelColumn>();
		rootColumns.add(new ExcelColumn("Patient File No.", FieldType.STRING, "patientFileNo"));
		rootColumns.add(new ExcelColumn("Order Number", FieldType.STRING, "orderNumber"));
		rootColumns.add(new ExcelColumn("Order Date", FieldType.DATE, "orderDate"));
		rootColumns.add(new ExcelColumn("Test Code", FieldType.STRING, "testCode"));
		rootColumns.add(new ExcelColumn("Result Code", FieldType.STRING, "resultCode"));
		rootColumns.add(new ExcelColumn("Result Value", FieldType.STRING, "resultValue"));
		rootColumns.add(new ExcelColumn("Normal Range Prefix", FieldType.STRING, "normalRangePrefix"));
		rootColumns.add(new ExcelColumn("Conventional Normal Range", FieldType.STRING, "convNormalRange"));
		rootColumns.add(new ExcelColumn("SI Normal Range", FieldType.STRING, "siNormalRange"));
		rootColumns.add(new ExcelColumn("Conventional Unit", FieldType.STRING, "convUnit"));
		rootColumns.add(new ExcelColumn("SI Unit", FieldType.STRING, "siUnit"));
		rootColumns.add(new ExcelColumn("Comments", FieldType.STRING, "comments"));

		ExcelSheet historicalOrderSheet = new ExcelSheet("Historical_Orders", HistoricalOrder.class, rootColumns);
		return historicalOrderSheet;
	}

	public List<String> importTeamLabHistorical(MultipartFile mapper, MultipartFile allergy,
			MultipartFile hematology, MultipartFile letter, MultipartFile microDetails,
			MultipartFile microMaster, MultipartFile protein, MultipartFile seminal,
			MultipartFile stool, MultipartFile urine)
			throws InstantiationException, IllegalAccessException, NoSuchFieldException, SecurityException, ParseException, IOException {

		List<String> nullPatients = new ArrayList<String>();
		Boolean shouldSave = Boolean.TRUE;

		//BRANCH
		LabBranch branch = branchService.findOne(
				SearchCriterion.generateRidFilter(SecurityUtil.getCurrentUser().getBranchId(), FilterOperator.eq), LabBranch.class);

		//PREPARE ORDERS
		Map<String, HistoricalOrder> historicalOrders = new HashMap<String, HistoricalOrder>();
		List<HistoricalOrder> historicalOrderList = repo.findAll();
		for (HistoricalOrder historicalOrder : historicalOrderList) {
			if (!historicalOrders.containsKey(historicalOrder.getOrderNumber())) {
				historicalOrders.put(historicalOrder.getOrderNumber(), historicalOrder);
			}
		}

		//PREPARE TESTS
		Map<String, HistoricalTest> historicalTests = new HashMap<String, HistoricalTest>();
		List<HistoricalTest> historicalTestList = historicalTestService.find(new ArrayList<>(), HistoricalTest.class, "order");
		for (HistoricalTest historicalTest : historicalTestList) {
			String key = historicalTest.getOrder().getOrderNumber() + "|" + historicalTest.getTestCode();
			if (!historicalTests.containsKey(key)) {
				historicalTests.put(key, historicalTest);
			}
		}

		//PREPARE PATIENTS
		Map<String, EmrPatientInfo> patients = new HashMap<String, EmrPatientInfo>();
		List<EmrPatientInfo> patientList = patientService.find(
				Arrays.asList(new SearchCriterion("sourceBranch.rid", branch.getRid(), FilterOperator.eq)),
				EmrPatientInfo.class);
		for (EmrPatientInfo patient : patientList) {
			patients.put(patient.getFileNo(), patient);
		}

		//MAPPER WORKBOOK
		Workbook mapperWorkbook = ExcelUtil.getWorkbookFromExcel(mapper);
		Sheet mapperSheet = mapperWorkbook.getSheetAt(0);
		Iterator<Row> mapperRowIterator = mapperSheet.iterator();
		mapperRowIterator.next(); //skip header
		while (mapperRowIterator.hasNext()) {
			Row row = mapperRowIterator.next();
			int cellnum = 0;
			HistoricalOrder order = new HistoricalOrder();
			order.setOrderNumber(branch.getCode() + "-" + ExcelUtil.getStringFromCell(row.getCell(cellnum++)));
			order.setOrderDate((Date) ExcelUtil.getDataFromCell(row.getCell(cellnum++), Date.class));
			String patientFileNo = branch.getCode() + "-" + ExcelUtil.getStringFromCell(row.getCell(cellnum++));
			order.setPatientFileNo(patientFileNo);
			EmrPatientInfo patient = patients.get(patientFileNo);
			if (patient == null) {
				String nullPatientDescription = "Order Number: " + order.getOrderNumber() + ", Patient File No: " + patientFileNo;
				System.out.println(nullPatientDescription);
				nullPatients.add(nullPatientDescription);
			}
			order.setPatient(patient);
			if (historicalOrders.containsKey(order.getOrderNumber())) {
				order = historicalOrders.get(order.getOrderNumber());
			} else {
				if (patient != null) {
					//if patient is null there is no point in saving the order
					if (shouldSave) {
						order = repo.save(order);
					}
					historicalOrders.put(order.getOrderNumber(), order);
				}
			}
		}

		//ALLERGY WORKBOOK
		Workbook allergyWorkbook = ExcelUtil.getWorkbookFromExcel(allergy);
		readHistoricalSheet(historicalOrders, historicalTests, branch.getCode(), allergyWorkbook, 0, 3, 29, Boolean.FALSE, "RASTI",
				null, shouldSave);
		readHistoricalSheet(historicalOrders, historicalTests, branch.getCode(), allergyWorkbook, 1, 3, 35, Boolean.FALSE, "RASTF",
				null, shouldSave);
		//add for RASTP when data is present

		//HEMATOLOGY WORKBOOK
		Workbook hematologyWorkbook = ExcelUtil.getWorkbookFromExcel(hematology);
		readHistoricalSheet(historicalOrders, historicalTests, branch.getCode(), hematologyWorkbook, 1, 2, 21, Boolean.FALSE, "CBCIND",
				Arrays.asList("Comments"), shouldSave);
		readHistoricalSheet(historicalOrders, historicalTests, branch.getCode(), hematologyWorkbook, 2, 2, 21, Boolean.FALSE, "CBCDF",
				Arrays.asList("Comments"), shouldSave);
		readHistoricalSheet(historicalOrders, historicalTests, branch.getCode(), hematologyWorkbook, 3, 2, 21, Boolean.FALSE, "CBC",
				Arrays.asList("Comments"), shouldSave);

		//Letter WORKBOOK
		Workbook letterWorkbook = ExcelUtil.getWorkbookFromExcel(letter);
		readHistoricalSheet(historicalOrders, historicalTests, branch.getCode(), letterWorkbook, 0, 2, 2, Boolean.TRUE, null,
				null, shouldSave);

		//MicroDetails WORKBOOK
		Workbook microDetailsWorkbook = ExcelUtil.getWorkbookFromExcel(microDetails);
		readHistoricalSheet(historicalOrders, historicalTests, branch.getCode(), microDetailsWorkbook, 0, 2, 5, Boolean.TRUE, null,
				Arrays.asList("Comment1", "Comment2"), shouldSave);

		//MicroMaster WORKBOOK
		Workbook microMasterWorkbook = ExcelUtil.getWorkbookFromExcel(microMaster);
		readHistoricalSheet(historicalOrders, historicalTests, branch.getCode(), microMasterWorkbook, 0, 2, 2, Boolean.FALSE, "CULO",
				Arrays.asList("Antibiotic", "Result"), shouldSave);

		//Protein WORKBOOK
		Workbook proteinWorkbook = ExcelUtil.getWorkbookFromExcel(protein);
		readHistoricalSheet(historicalOrders, historicalTests, branch.getCode(), proteinWorkbook, 1, 2, 6, Boolean.FALSE, "HMGEL",
				null, shouldSave);
		readHistoricalSheet(historicalOrders, historicalTests, branch.getCode(), proteinWorkbook, 2, 2, 8, Boolean.FALSE, "PRTEL",
				null, shouldSave);

		//Seminal WORKBOOK
		Workbook seminalWorkbook = ExcelUtil.getWorkbookFromExcel(seminal);
		readHistoricalSheet(historicalOrders, historicalTests, branch.getCode(), seminalWorkbook, 0, 2, 0, Boolean.FALSE, "SEM",
				null, shouldSave);

		//Stool WORKBOOK
		Workbook stoolWorkbook = ExcelUtil.getWorkbookFromExcel(stool);
		readHistoricalSheet(historicalOrders, historicalTests, branch.getCode(), stoolWorkbook, 0, 2, 17, Boolean.FALSE, "STA",
				Arrays.asList("Test Comment/Remark"), shouldSave);

		//URINE WORKBOOK
		Workbook urineWorkbook = ExcelUtil.getWorkbookFromExcel(urine);
		readHistoricalSheet(historicalOrders, historicalTests, branch.getCode(), urineWorkbook, 0, 2, 22, Boolean.FALSE, "URA",
				Arrays.asList("Test Remarks/comments"), shouldSave);

		//END
		mapperWorkbook.close();
		allergyWorkbook.close();
		hematologyWorkbook.close();
		letterWorkbook.close();
		microDetailsWorkbook.close();
		microMasterWorkbook.close();
		proteinWorkbook.close();
		seminalWorkbook.close();
		stoolWorkbook.close();
		urineWorkbook.close();

		return nullPatients;
	}

	private void readHistoricalSheet(Map<String, HistoricalOrder> historicalOrders,
			Map<String, HistoricalTest> historicalTests,
			String branchCode, Workbook workbook, Integer sheetIndex, Integer rowsToSkip, Integer numOfDataCells,
			Boolean readTestCodeFromCell, String testCode, List<String> commentCellNames,
			Boolean shouldSave) {
		Sheet sheet = workbook.getSheetAt(sheetIndex);
		Iterator<Row> rowIterator = sheet.iterator();

		List<String> resultCodes = new ArrayList<String>();

		Row header = rowIterator.next();
		Iterator<Cell> headerCellIterator = header.cellIterator();
		headerCellIterator.next(); //skip first cell since it contains order-number
		int cellCount = 0;
		while (cellCount < numOfDataCells && headerCellIterator.hasNext()) {
			Cell cell = headerCellIterator.next();
			String resultCode = ExcelUtil.getStringFromCell(cell);
			resultCodes.add(resultCode);
			cellCount++;
		}

		for (int i = 0; i < rowsToSkip; i++) {
			rowIterator.next();
		}

		while (rowIterator.hasNext()) {
			Row row = rowIterator.next();
			Iterator<Cell> cellIterator = row.cellIterator();
			String orderNumber = branchCode + "-" + ExcelUtil.getStringFromCell(cellIterator.next());
			cellCount = 0;
			HistoricalOrder order = historicalOrders.get(orderNumber);
			if (order == null || order.getRid() == null) {
				continue; //to deal with unsaved orders caused by missing patients
			}
			if (readTestCodeFromCell) {
				testCode = ExcelUtil.getStringFromCell(cellIterator.next());
			}

			HistoricalTest test = getHistoricalTest(order, testCode, historicalTests, shouldSave);
			while (cellCount < numOfDataCells && cellIterator.hasNext()) {
				String resultCode = resultCodes.get(cellCount++);
				String resultValue = ExcelUtil.getStringFromCell(cellIterator.next());
				if (!CollectionUtil.isCollectionEmpty(commentCellNames) && commentCellNames.contains(resultCode)) {
					String oldComments = test.getComments();
					String newComments = resultValue;
					if (!StringUtil.isEmpty(newComments)) {
						if (!StringUtil.isEmpty(oldComments)) {
							newComments = oldComments + " " + newComments;
						}
						test.setComments(newComments);
						if (shouldSave) {
							test = historicalTestService.updateHistoricalTest(test);
						}
					}
				} else {
					HistoricalResult result = new HistoricalResult();
					result.setResultCode(resultCode);
					result.setResultValue(resultValue);
					result.setTest(test);
					if (shouldSave) {
						historicalResultService.createHistoricalResult(result);
					}
				}
			}
		}
	}

	private HistoricalTest getHistoricalTest(HistoricalOrder order, String testCode, Map<String, HistoricalTest> historicalTests,
			Boolean shouldSave) {
		HistoricalTest test = historicalTests.get(order.getOrderNumber() + "|" + testCode);
		if (test == null) {
			test = new HistoricalTest();
			test.setOrder(order);
			test.setTestCode(testCode);
			if (shouldSave) {
				test = historicalTestService.createHistoricalTest(test);
			}
			historicalTests.put(order.getOrderNumber() + "|" + testCode, test);
		}
		return test;
	}

	@InterceptorFree
	public List<HistoricalOrder> getAllOrdersByPatient(Long patientRid) {
		return getRepository().find(Arrays.asList(new SearchCriterion("patient.rid", patientRid, FilterOperator.eq)), HistoricalOrder.class,
				"patient");
	}

	public List<HistoricalOrder> updateHistoricalOrders(List<HistoricalOrder> historicalOrders) {
		return getRepository().save(historicalOrders);
	}

}

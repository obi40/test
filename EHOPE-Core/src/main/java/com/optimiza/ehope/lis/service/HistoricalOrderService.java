package com.optimiza.ehope.lis.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.optimiza.core.base.service.GenericService;
import com.optimiza.core.common.util.StringUtil;
import com.optimiza.core.lkp.helper.FieldType;
import com.optimiza.ehope.lis.helper.ExcelColumn;
import com.optimiza.ehope.lis.helper.ExcelSheet;
import com.optimiza.ehope.lis.model.HistoricalOrder;
import com.optimiza.ehope.lis.model.HistoricalResult;
import com.optimiza.ehope.lis.model.HistoricalTest;
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

	public void importTeamLabHistorical(MultipartFile mapper, MultipartFile allergy,
			MultipartFile hematology, MultipartFile letter, MultipartFile microDetails,
			MultipartFile microMaster, MultipartFile protein, MultipartFile seminal,
			MultipartFile stool, MultipartFile urine) {

		Workbook workbook = ExcelUtil.getWorkbookFromExcel(mapper);
		Sheet sheet = workbook.getSheetAt(0);
		Iterator<Row> rowIterator = sheet.iterator();

	}

}

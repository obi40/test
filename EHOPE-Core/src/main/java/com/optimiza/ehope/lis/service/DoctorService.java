package com.optimiza.ehope.lis.service;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.optimiza.core.base.service.GenericService;
import com.optimiza.core.common.data.model.TransField;
import com.optimiza.core.common.helper.FilterablePageRequest;
import com.optimiza.core.common.util.ReflectionUtil;
import com.optimiza.core.lkp.helper.FieldType;
import com.optimiza.core.lkp.model.ComTenantLanguage;
import com.optimiza.core.lkp.service.ComTenantLanguageService;
import com.optimiza.ehope.lis.helper.EhopeRights;
import com.optimiza.ehope.lis.helper.ExcelColumn;
import com.optimiza.ehope.lis.helper.ExcelSheet;
import com.optimiza.ehope.lis.model.Doctor;
import com.optimiza.ehope.lis.model.NameDictionary;
import com.optimiza.ehope.lis.repo.DoctorRepo;
import com.optimiza.ehope.lis.util.ExcelUtil;

@Service("DoctorService")
public class DoctorService extends GenericService<Doctor, DoctorRepo> {

	@Autowired
	private DoctorRepo repo;
	@Autowired
	private ComTenantLanguageService tenantLanguageService;
	@Autowired
	private NameDictionaryService nameDictionaryService;
	@Value("${system.batchSize}")
	public String batchSize;
	@Autowired
	private ReflectionUtil reflectionUtil;

	@Override
	protected DoctorRepo getRepository() {
		return repo;
	}

	@PreAuthorize("hasAuthority('" + EhopeRights.VIEW_DOCTOR + "')")
	public Page<Doctor> getDoctorsPage(FilterablePageRequest filterablePageRequest) {
		return find(filterablePageRequest.getFilters(), filterablePageRequest.getPageRequest(), Doctor.class);
	}

	@PreAuthorize("hasAuthority('" + EhopeRights.ADD_DOCTOR + "')")
	public Doctor createDoctor(Doctor doctor) {
		return repo.save(doctor);
	}

	@PreAuthorize("hasAuthority('" + EhopeRights.UPD_DOCTOR + "')")
	public Doctor updateDoctor(Doctor doctor) {
		return repo.save(doctor);
	}

	@PreAuthorize("hasAuthority('" + EhopeRights.DEL_DOCTOR + "')")
	public void deleteDoctor(Long rid) {
		repo.delete(rid);
	}

	public void importDoctors(MultipartFile excel) throws IOException {
		String fileName = excel.getOriginalFilename();
		InputStream inputStream = excel.getInputStream();

		String extension = fileName.substring(fileName.lastIndexOf('.') + 1);
		Workbook workbook;
		if (extension.equals("xls")) {
			workbook = new HSSFWorkbook(inputStream);
		} else {
			workbook = new XSSFWorkbook(inputStream);
		}

		Sheet sheet = workbook.getSheetAt(0);
		Iterator<Row> rowIterator = sheet.iterator();

		rowIterator.next(); //skip the header row
		while (rowIterator.hasNext()) {
			Doctor doctor = new Doctor();
			Row nextRow = rowIterator.next();
			Cell nameCell = nextRow.getCell(0);
			TransField nameField = new TransField();
			nameField.put("ar_jo", nameCell.getStringCellValue().trim());
			nameField.put("en_us", nameCell.getStringCellValue().trim());
			doctor.setName(nameField);
			doctor.setDescription(nameField);

			DataFormatter df = new DataFormatter();

			Cell phoneCell = nextRow.getCell(1);
			String phone = df.formatCellValue(phoneCell).toString();
			doctor.setPhoneNo(phone.length() == 0 ? null : phone);

			Cell mobileCell = nextRow.getCell(2);
			String mobile = df.formatCellValue(mobileCell).toString();
			doctor.setMobileNo(mobile.length() == 0 ? null : mobile);

			Cell emailCell = nextRow.getCell(3);
			String email = df.formatCellValue(emailCell).toString();
			doctor.setEmail(email.length() == 0 ? null : email);
			createDoctor(doctor);
		}

		workbook.close();
	}

	/**
	 * Get the columns that are generated for the download/upload process.
	 * 
	 * @return List
	 */
	public ExcelSheet getDoctorSheet() {
		List<ExcelColumn> rootColumns = new ArrayList<ExcelColumn>();
		rootColumns.add(new ExcelColumn("Name", FieldType.TRANS_FIELD, "name"));
		rootColumns.add(new ExcelColumn("Email", FieldType.STRING, "email"));
		rootColumns.add(new ExcelColumn("Phone number", FieldType.STRING, "phoneNo"));
		rootColumns.add(new ExcelColumn("Mobile number", FieldType.STRING, "mobileNo"));
		rootColumns.add(new ExcelColumn("Description", FieldType.TRANS_FIELD, "description"));
		ExcelSheet doctorsSheet = new ExcelSheet("Doctors", Doctor.class, rootColumns);
		return doctorsSheet;
	}

	public ExcelSheet uploadDoctorsData(MultipartFile excel) {
		Workbook workbook = ExcelUtil.getWorkbookFromExcel(excel);
		Sheet sheet = workbook.getSheetAt(0);
		Iterator<Row> rowIterator = sheet.iterator();
		rowIterator.next();//skip
		rowIterator.next();//skip
		ExcelSheet es = getDoctorSheet();
		List<ExcelColumn> columns = es.getColumns();
		List<ComTenantLanguage> languages = tenantLanguageService.findTenantExcelLanguages();
		Map<String, Map<String, NameDictionary>> transliteration = nameDictionaryService.getAll();
		Row row = null;
		while (rowIterator.hasNext()) {
			row = rowIterator.next();
			if (ExcelUtil.isRowEmpty(row)) {
				continue;
			}
			Doctor doctor = null;
			try {
				doctor = ExcelUtil.createObjectFromRow(row, Doctor.class, columns, languages);
				doctor.setName(nameDictionaryService.translateTransFields(doctor.getName(), transliteration));
				reflectionUtil.saveEntitySeparate(getRepository(), doctor);
			} catch (Exception e) {
				ExcelUtil.handleExcelExceptions(es, row, e);
			}
		}
		try {
			workbook.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return es;
	}

}

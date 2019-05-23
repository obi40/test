package com.optimiza.ehope.lis.service;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.optimiza.core.base.helper.SearchCriterion;
import com.optimiza.core.base.helper.SearchCriterion.FilterOperator;
import com.optimiza.core.base.helper.SearchCriterion.JunctionOperator;
import com.optimiza.core.base.service.GenericService;
import com.optimiza.core.common.business.exception.BusinessException;
import com.optimiza.core.common.business.exception.BusinessException.ErrorSeverity;
import com.optimiza.core.common.data.model.TransField;
import com.optimiza.core.common.helper.FilterablePageRequest;
import com.optimiza.core.common.util.DateUtil;
import com.optimiza.core.common.util.ReflectionUtil;
import com.optimiza.core.common.util.StringUtil;
import com.optimiza.core.lkp.helper.FieldType;
import com.optimiza.core.lkp.model.ComTenantLanguage;
import com.optimiza.core.lkp.model.LkpCountry;
import com.optimiza.core.lkp.model.LkpGender;
import com.optimiza.core.lkp.service.ComTenantLanguageService;
import com.optimiza.core.lkp.service.LkpService;
import com.optimiza.ehope.lis.helper.BalanceTransactionType;
import com.optimiza.ehope.lis.helper.EhopeRights;
import com.optimiza.ehope.lis.helper.ExcelColumn;
import com.optimiza.ehope.lis.helper.ExcelSheet;
import com.optimiza.ehope.lis.lkp.helper.SerialType;
import com.optimiza.ehope.lis.lkp.model.LkpBloodType;
import com.optimiza.ehope.lis.lkp.model.LkpMaritalStatus;
import com.optimiza.ehope.lis.lkp.model.LkpPatientStatus;
import com.optimiza.ehope.lis.model.BillBalanceTransaction;
import com.optimiza.ehope.lis.model.EmrPatientInfo;
import com.optimiza.ehope.lis.model.EmrVisit;
import com.optimiza.ehope.lis.model.NameDictionary;
import com.optimiza.ehope.lis.model.SysSerial;
import com.optimiza.ehope.lis.repo.EmrPatientInfoRepo;
import com.optimiza.ehope.lis.util.ExcelUtil;

@Service("EmrPatientInfoService")
public class EmrPatientInfoService extends GenericService<EmrPatientInfo, EmrPatientInfoRepo> {

	@Autowired
	private EmrPatientInfoRepo repo;
	@Autowired
	private SysSerialService serialSerivce;
	@Autowired
	private NameDictionaryService nameDictionaryService;
	@Autowired
	private ComTenantLanguageService tenantLanguageService;
	@Autowired
	private PatientArtifactService patientArtifactService;
	@Autowired
	private PatientFingerprintService patientFingerprintService;
	@Autowired
	private LkpService lkpService;
	@Autowired
	private EmrVisitService visitService;
	@Value("${system.batchSize}")
	public String batchSize;
	@Autowired
	private ReflectionUtil reflectionUtil;
	@Autowired
	private BillBalanceTransactionService balanceTransactionService;
	@Autowired
	ApplicationContext applicationContext;

	@Override
	protected EmrPatientInfoRepo getRepository() {
		return repo;
	}

	public void deletePatient(EmrPatientInfo patient) {
		getRepository().delete(patient);
	}

	@PreAuthorize("hasAuthority('" + EhopeRights.VIEW_PATIENT_PROFILE + "')")
	public EmrPatientInfo getPatientInfo(Long rid) {
		List<SearchCriterion> filters = new ArrayList<SearchCriterion>();
		SearchCriterion filterByRid = new SearchCriterion("rid", rid, FilterOperator.eq);
		filters.add(filterByRid);
		EmrPatientInfo patient = repo.findOne(filters, EmrPatientInfo.class,
				"country", "gender", "maritalStatus", "bloodType", "emrPatientInsurance", "mergedToPatientInfo");
		patient.setArtifactDescriptions(patientArtifactService.getByPatientId(rid));
		return patient;
	}

	@PreAuthorize("hasAuthority('" + EhopeRights.VIEW_PATIENT_PROFILE + "')")
	public List<EmrPatientInfo> getPatientList() {
		List<SearchCriterion> filters = new ArrayList<SearchCriterion>();
		List<EmrPatientInfo> patientList = repo.find(filters, EmrPatientInfo.class,
				"gender", "emrPatientInsurance.insProvider");
		return patientList;
	}

	@PreAuthorize("hasAuthority('" + EhopeRights.VIEW_PATIENT_PROFILE + "')")
	public Page<EmrPatientInfo> getPatientLookupPage(FilterablePageRequest filterablePageRequest) {

		for (SearchCriterion sc : filterablePageRequest.getFilters()) {
			if (sc.getField().equals("lastOrderDate")) {
				Date d = new Date();
				d = DateUtil.addDays(d, -8);// before one week, should be 7 but we excluded today so 8
				d = DateUtil.setHour(d, 24);
				d = DateUtil.setMinute(d, 0);
				d = DateUtil.setSecond(d, 0);
				sc.setValue(d);
				break;
			}
		}
		Page<EmrPatientInfo> patientPage = repo.find(filterablePageRequest.getFilters(), filterablePageRequest.getPageRequest(),
				EmrPatientInfo.class);
		if (patientPage.getNumberOfElements() == 0) {
			return patientPage;
		}

		List<Long> idFilters = new ArrayList<Long>();
		for (EmrPatientInfo patient : patientPage) {
			idFilters.add(patient.getRid());
		}

		List<EmrPatientInfo> patientList = repo.find(Arrays.asList(new SearchCriterion("rid", idFilters, FilterOperator.in)),
				EmrPatientInfo.class, filterablePageRequest.getPageRequest().getSort(),
				"patientStatus", "emrPatientInsurance.insProvider", "gender", "mergedToPatientInfo");
		Set<EmrPatientInfo> patientSet = new LinkedHashSet<EmrPatientInfo>(patientList);
		patientList.clear();
		patientList.addAll(patientSet);
		Page<EmrPatientInfo> page = new PageImpl<EmrPatientInfo>(patientList, filterablePageRequest.getPageRequest(),
				patientPage.getTotalElements());
		return page;
	}

	/**
	 * General use for front end to fetch Patient pages
	 * 
	 * @param filterablePageRequest
	 * @return Page
	 */
	public Page<EmrPatientInfo> getPatientPage(FilterablePageRequest filterablePageRequest) {
		return repo.find(filterablePageRequest.getFilters(), filterablePageRequest.getPageRequest(), EmrPatientInfo.class);
	}

	@PreAuthorize("hasAuthority('" + EhopeRights.VIEW_PATIENT_PROFILE + "')")
	public List<EmrPatientInfo> getSimilarPatientList(EmrPatientInfo patient) {
		List<SearchCriterion> filters = new ArrayList<SearchCriterion>();
		patient.getFirstName().forEach((k, v) ->
			{
				filters.add(new SearchCriterion("firstName", v, FilterOperator.contains, JunctionOperator.Or));
			});
		patient.getSecondName().forEach((k, v) ->
			{
				filters.add(new SearchCriterion("secondName", v, FilterOperator.contains, JunctionOperator.Or));
			});
		patient.getThirdName().forEach((k, v) ->
			{
				filters.add(new SearchCriterion("thirdName", v, FilterOperator.contains, JunctionOperator.Or));
			});
		patient.getLastName().forEach((k, v) ->
			{
				filters.add(new SearchCriterion("lastName", v, FilterOperator.contains, JunctionOperator.Or));
			});
		filters.add(new SearchCriterion("gender.rid", patient.getGender().getRid(), FilterOperator.eq, JunctionOperator.And));
		filters.add(new SearchCriterion("dateOfBirth", patient.getDateOfBirth(), FilterOperator.eq, JunctionOperator.And));
		return repo.find(filters, EmrPatientInfo.class,
				"gender", "bloodType", "maritalStatus", "country", "patientStatus", "race", "religion");
	}

	@PreAuthorize("hasAuthority('" + EhopeRights.ADD_PATIENT_PROFILE + "')")
	public EmrPatientInfo createPatient(EmrPatientInfo patient, MultipartFile[] artifacts, String fingerprint) throws IOException {
		if (StringUtil.isEmpty(patient.getEmail())) {
			patient.setEmail(null);
		}
		String fileNo = serialSerivce.sequenceGeneration(SerialType.PATIENT_FILE_NO);
		patient.setFullName(generateFullName(patient));
		patient.setFileNo(fileNo);
		patient.setBalance(BigDecimal.ZERO);

		patient = repo.save(patient);
		if (!StringUtil.isEmpty(fingerprint)) {
			patientFingerprintService.savePatientFingerprint(patient, fingerprint);
		}
		patientArtifactService.saveArtifacts(artifacts, patient);
		return patient;
	}

	private TransField generateFullName(EmrPatientInfo patient) {
		TransField fullName = new TransField();
		List<ComTenantLanguage> languages = tenantLanguageService.findTenantLanguages(new ArrayList<>(), null);
		for (ComTenantLanguage language : languages) {
			String locale = language.getComLanguage().getLocale();
			String firstName = patient.getFirstName().get(locale);
			String secondName = patient.getSecondName().get(locale);
			String thirdName = patient.getThirdName().get(locale);
			String lastName = patient.getLastName().get(locale);
			String[] nameArray = new String[4];
			nameArray[0] = firstName == null ? "" : firstName;
			nameArray[1] = secondName == null ? "" : secondName;
			nameArray[2] = thirdName == null ? "" : thirdName;
			nameArray[3] = lastName == null ? "" : lastName;
			fullName.put(locale, String.join(" ", nameArray).replaceAll("\\s+", " ").trim());
		}
		return fullName;
	}

	//	public Map<String, String> getAllTenantsPatientsEmails() {
	//		entityManager.unwrap(Session.class).disableFilter(BaseAuditableTenantedEntity.TENANT_FILTER);
	//		List<SearchCriterion> filters = new ArrayList<>();
	//		filters.add(new SearchCriterion("email", null, FilterOperator.isnotnull));
	//		filters.add(new SearchCriterion("email", null, FilterOperator.isnotempty));
	//		Map<String, String> emailsMap = getRepository()	.find(filters, EmrPatientInfo.class).stream()
	//														.map(e -> e.getEmail().toLowerCase())
	//														.collect(Collectors.toMap(Function.identity(), Function.identity()));
	//
	//		entityManager.unwrap(Session.class).enableFilter(BaseAuditableTenantedEntity.TENANT_FILTER);
	//		return emailsMap;
	//	}

	@PreAuthorize("hasAuthority('" + EhopeRights.MERGE_PATIENT_PROFILE + "')")
	public void mergePatients(Long mergeFromRid, Long mergeToRid) {

		EmrPatientInfo mergeFromPatient = getRepository().findOne(SearchCriterion.generateRidFilter(mergeFromRid, FilterOperator.eq),
				EmrPatientInfo.class, "emrVisits.balanceTransactions.balanceTransactionType");
		EmrPatientInfo mergeToPatient = getRepository().findOne(mergeToRid);

		mergeFromPatient.setIsActive(Boolean.FALSE);
		mergeFromPatient.setMergedToPatientInfo(mergeToPatient);

		mergeToPatient.setBalance(mergeToPatient.getBalance().add(mergeFromPatient.getBalance()));

		//set the visits with the toPatient
		Set<EmrVisit> mergeFromVisits = mergeFromPatient.getEmrVisits();
		for (EmrVisit visit : mergeFromVisits) {
			visit.setEmrPatientInfo(mergeToPatient);
			visit.setMergedFromEmrPatientInfo(mergeFromPatient);
			for (BillBalanceTransaction bbt : visit.getBalanceTransactions()) {
				if (BalanceTransactionType.PATIENT == BalanceTransactionType.valueOf(bbt.getBalanceTransactionType().getCode())) {
					bbt.setPatientInfo(mergeToPatient);
				}
			}
		}
		balanceTransactionService.updateBalanceTransaction(
				mergeFromVisits.stream().flatMap(v -> v.getBalanceTransactions().stream()).collect(Collectors.toList()));
		visitService.updateVisit(mergeFromVisits);
		getRepository().save(mergeFromPatient);
		getRepository().save(mergeToPatient);
	}

	public void updateLastOrderDate(EmrPatientInfo patient, Date date) {
		repo.updateLastOrderDate(patient.getRid(), date);
	}

	@PreAuthorize("hasAuthority('" + EhopeRights.UPD_PATIENT_PROFILE + "')")
	public EmrPatientInfo editPatient(EmrPatientInfo patient, MultipartFile[] artifacts, List<Long> artifactIdsToDelete, String fingerprint)
			throws IOException {
		if (StringUtil.isEmpty(patient.getEmail())) {
			patient.setEmail(null);
		}
		patient.setFullName(generateFullName(patient));

		EmrPatientInfo savedPatient = repo.save(patient);
		if (!StringUtil.isEmpty(fingerprint)) {
			patientFingerprintService.savePatientFingerprint(savedPatient, fingerprint);
		}

		patientArtifactService.saveArtifacts(artifacts, savedPatient);
		for (Long ridToDelete : artifactIdsToDelete) {
			patientArtifactService.deleteArtifactById(ridToDelete);
		}
		return getPatientInfo(savedPatient.getRid());
	}

	public EmrPatientInfo updatePatient(EmrPatientInfo patient) {
		return getRepository().save(patient);
	}

	@PreAuthorize("hasAuthority('" + EhopeRights.DEACTIVATE_PATIENT_PROFILE + "')")
	public EmrPatientInfo deactivatePatient(Long rid) {
		EmrPatientInfo fetchedPatientInfo = getRepository().getOne(rid);
		fetchedPatientInfo.setIsActive(false);
		return repo.save(fetchedPatientInfo);
	}

	@PreAuthorize("hasAuthority('" + EhopeRights.ACTIVATE_PATIENT_PROFILE + "')")
	public EmrPatientInfo activatePatient(Long rid) {
		EmrPatientInfo fetchedPatientInfo = getRepository().findOne(SearchCriterion.generateRidFilter(rid, FilterOperator.eq),
				EmrPatientInfo.class, "mergedToPatientInfo");
		if (fetchedPatientInfo.getMergedToPatientInfo() != null) {
			throw new BusinessException("Cant Activate a Merged Patient", "activatingMergedPatient", ErrorSeverity.ERROR);
		}
		fetchedPatientInfo.setIsActive(true);
		return repo.save(fetchedPatientInfo);
	}

	public EmrPatientInfo findPatientByEmail(String email) {
		if (StringUtil.isEmpty(email)) {
			return null;
		}
		return getRepository().findByEmailIgnoreCase(email);
	}

	public List<EmrPatientInfo> importPatients(MultipartFile excel) throws IOException {
		String fileName = excel.getOriginalFilename();
		InputStream inputStream = excel.getInputStream();

		Map<String, Map<String, NameDictionary>> transliterationMaps = nameDictionaryService.getAll();

		String extension = fileName.substring(fileName.lastIndexOf('.') + 1);
		Workbook workbook;
		if (extension.equals("xls")) {
			workbook = new HSSFWorkbook(inputStream);
		} else {
			workbook = new XSSFWorkbook(inputStream);
		}

		Sheet sheet = workbook.getSheetAt(0);

		Iterator<Row> rowIterator = sheet.iterator();

		List<LkpGender> genders = lkpService.findAnyLkp(new ArrayList<>(), LkpGender.class, null);
		LkpGender male = null;
		LkpGender female = null;
		for (LkpGender lkpGender : genders) {
			if (lkpGender.getCode().equals("FEMALE")) {
				female = lkpGender;
			} else {
				male = lkpGender;
			}
		}
		Set<String> emails = new HashSet<String>();

		Pattern emailPattern = Pattern.compile("[\\d\\w.]+@[\\d\\w.]+\\.[\\d\\w.]+");

		Pattern agePattern = Pattern.compile("^([0-9]{2}-[a-zA-Z]{3}-)([0-9]{2})$");

		List<EmrPatientInfo> invalidPatients = new ArrayList<EmrPatientInfo>();

		int rowCount = 0;
		rowIterator.next(); //skip the header row
		while (rowIterator.hasNext()) {
			rowCount++;
			EmrPatientInfo patient = new EmrPatientInfo();
			try {
				int column = 0;
				Row nextRow = rowIterator.next();
				patient.setIsActive(true);
				patient.setIsSmsNotification(false);
				patient.setIsWhatsappNotification(false);
				patient.setIsEmailNotification(false);
				String fileNo = (String) ExcelUtil.getDataFromCell(nextRow.getCell(column++), String.class);
				Cell nameCell = nextRow.getCell(column++);
				getNameValueFromCell(patient, nameCell, transliterationMaps);

				Cell ageCell = nextRow.getCell(column++);
				Integer age = (int) ageCell.getNumericCellValue();

				Cell sexCell = nextRow.getCell(column++);
				String sex = sexCell.getStringCellValue();
				if (sex.equals("F")) {
					patient.setGender(female);
				} else {
					patient.setGender(male);
				}

				Cell dobCell = nextRow.getCell(column++);
				String dob = dobCell.getStringCellValue();

				Matcher ageMatcher = agePattern.matcher(dob);
				if (age <= 18) {
					dob = ageMatcher.replaceFirst("$120$2");
				} else {
					dob = ageMatcher.replaceFirst("$119$2");
				}

				SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yyyy");
				try {
					Date dateOfBirth = sdf.parse(dob);
					patient.setDateOfBirth(dateOfBirth);
				} catch (ParseException e) {
					e.printStackTrace();
				}

				Cell phoneCell = nextRow.getCell(column++);
				Long phoneNo = (long) phoneCell.getNumericCellValue();
				patient.setMobileNo("0" + phoneNo.toString());

				Cell mobileCell = nextRow.getCell(column++);
				Long mobileNo = (long) mobileCell.getNumericCellValue();
				patient.setSecondaryMobileNo("0" + mobileNo.toString());

				Cell emailCell = nextRow.getCell(column++);
				String email = emailCell.getStringCellValue();
				email = email.equals("") ? null : email;
				if (email != null) {
					Matcher emailMatcher = emailPattern.matcher(email);
					if (!emailMatcher.matches()) {
						email = null;
					}
				}
				if (!emails.add(email)) {
					email = null;
				}
				patient.setEmail(email);

				Cell historyCell = nextRow.getCell(column++);
				String medicalHistory = historyCell.getStringCellValue();
				patient.setMedicalHistory(medicalHistory);

				//CREATE PATIENT
				//String fileNo = serialSerivce.sequenceGeneration(SerialType.PATIENT_FILE_NO);
				patient.setFileNo(fileNo);
				patient.setFullName(generateFullName(patient));
				patient = repo.save(patient);
				//balanceService.createBalance(BalanceType.PATIENT, patient);
			} catch (Exception e) {
				System.out.println(rowCount);
				invalidPatients.add(patient);
			}
		}

		workbook.close();
		return invalidPatients;
	}

	public void getNameValueFromCell(EmrPatientInfo patient, Cell nameCell, Map<String, Map<String, NameDictionary>> transliterationMaps) {
		String fullName = nameCell.getStringCellValue();
		String firstName = "";
		String secondName = "";
		String thirdName = "";
		String lastName = "";

		String[] nameParts = fullName.trim().split("\\s+");
		for (int i = 0; i < nameParts.length - 1; i++) {
			switch (i) {
				case 0:
					firstName = nameParts[i];
					break;
				case 1:
					secondName = nameParts[i];
					break;
				case 2:
					thirdName = nameParts[i];
					break;
			}
		}
		for (int i = 3; i < nameParts.length; i++) {
			lastName += nameParts[i] + " ";
		}
		lastName = lastName.trim();

		Pattern p = Pattern.compile("[A-Za-z]");
		Matcher m = p.matcher(fullName);
		if (m.find()) {
			//en_us
			translitrateFullName(patient, firstName, secondName, thirdName, lastName, "en_us", "ar_jo", transliterationMaps);
		} else {
			//ar_jo
			translitrateFullName(patient, firstName, secondName, thirdName, lastName, "ar_jo", "en_us", transliterationMaps);
		}
	}

	private void translitrateFullName(EmrPatientInfo patient, String firstName, String secondName, String thirdName,
			String lastName, String sourceLanguage, String destinationLanguage,
			Map<String, Map<String, NameDictionary>> transliterationMaps) {
		TransField firstNameField = new TransField();
		TransField secondNameField = new TransField();
		TransField thirdNameField = new TransField();
		TransField lastNameField = new TransField();

		firstNameField.put(sourceLanguage, firstName);
		firstNameField.put(destinationLanguage, nameDictionaryService.transliterate(firstName, sourceLanguage, transliterationMaps));

		secondNameField.put(sourceLanguage, secondName);
		secondNameField.put(destinationLanguage, nameDictionaryService.transliterate(secondName, sourceLanguage, transliterationMaps));

		thirdNameField.put(sourceLanguage, thirdName);
		thirdNameField.put(destinationLanguage, nameDictionaryService.transliterate(thirdName, sourceLanguage, transliterationMaps));

		lastNameField.put(sourceLanguage, lastName);
		lastNameField.put(destinationLanguage, nameDictionaryService.transliterate(lastName, sourceLanguage, transliterationMaps));

		patient.setFirstName(firstNameField);
		patient.setSecondName(secondNameField);
		patient.setThirdName(thirdNameField);
		patient.setLastName(lastNameField);
	}

	/**
	 * Get the columns that are generated for the download/upload process.
	 * 
	 * @return List
	 */
	public ExcelSheet getPatientSheet() {
		List<ExcelColumn> rootColumns = new ArrayList<ExcelColumn>();

		rootColumns.add(new ExcelColumn("First Name", FieldType.TRANS_FIELD, "firstName"));
		rootColumns.add(new ExcelColumn("Second Name", FieldType.TRANS_FIELD, "secondName"));
		rootColumns.add(new ExcelColumn("Third Name", FieldType.TRANS_FIELD, "thirdName"));
		rootColumns.add(new ExcelColumn("Last Name", FieldType.TRANS_FIELD, "lastName"));
		rootColumns.add(new ExcelColumn("Address", FieldType.TRANS_FIELD, "address"));
		rootColumns.add(new ExcelColumn("Blood Type", FieldType.STRING, "bloodType", LkpBloodType.class, "code"));
		rootColumns.add(new ExcelColumn("Date Of Birth", FieldType.STRING, "dateOfBirth"));
		rootColumns.add(new ExcelColumn("Email", FieldType.STRING, "email"));
		rootColumns.add(new ExcelColumn("Gender", FieldType.STRING, "gender", LkpGender.class, "code"));
		rootColumns.add(new ExcelColumn("Marital Status", FieldType.STRING, "maritalStatus", LkpMaritalStatus.class, "code"));
		rootColumns.add(new ExcelColumn("Mobile Number", FieldType.STRING, "mobileNo"));
		rootColumns.add(new ExcelColumn("Secondary Mobile Number", FieldType.STRING, "secondaryMobileNo"));
		rootColumns.add(new ExcelColumn("Mother Name", FieldType.TRANS_FIELD, "motherName"));
		rootColumns.add(new ExcelColumn("National ID", FieldType.STRING, "nationalId"));
		rootColumns.add(new ExcelColumn("Country", FieldType.STRING, "country", LkpCountry.class, "code"));
		rootColumns.add(new ExcelColumn("Patient Status", FieldType.STRING, "patientStatus", LkpPatientStatus.class, "code"));
		rootColumns.add(new ExcelColumn("Phone Number", FieldType.STRING, "phoneNo"));
		rootColumns.add(new ExcelColumn("Active", FieldType.BOOLEAN, "isActive"));
		rootColumns.add(new ExcelColumn("VIP", FieldType.BOOLEAN, "isVip"));
		rootColumns.add(new ExcelColumn("Email Notifications", FieldType.BOOLEAN, "isEmailNotification"));
		rootColumns.add(new ExcelColumn("SMS Notifications", FieldType.BOOLEAN, "isSmsNotification"));
		rootColumns.add(new ExcelColumn("Whatsapp Notifications", FieldType.BOOLEAN, "isWhatsappNotification"));
		ExcelSheet patientSheet = new ExcelSheet("Patients", EmrPatientInfo.class, rootColumns);
		return patientSheet;
	}

	//TODO: Lock adding
	public ExcelSheet uploadPatientsData(MultipartFile excel) {
		Workbook workbook = ExcelUtil.getWorkbookFromExcel(excel);
		Sheet sheet = workbook.getSheetAt(0);
		Iterator<Row> rowIterator = sheet.iterator();
		rowIterator.next();//skip
		rowIterator.next();//skip
		ExcelSheet es = getPatientSheet();
		List<ExcelColumn> columns = es.getColumns();
		List<ComTenantLanguage> languages = tenantLanguageService.findTenantExcelLanguages();
		SysSerial fileNoSerial = serialSerivce.getSerialGenerationData(SerialType.PATIENT_FILE_NO);
		Map<String, Map<String, NameDictionary>> transliteration = nameDictionaryService.getAll();
		Row row = null;
		while (rowIterator.hasNext()) {
			row = rowIterator.next();
			if (ExcelUtil.isRowEmpty(row)) {
				continue;
			}
			EmrPatientInfo patient = null;
			try {
				patient = ExcelUtil.createObjectFromRow(row, EmrPatientInfo.class, columns, languages);
				patient.setFileNo(serialSerivce.sequenceBuilder(fileNoSerial));
				patient.setFirstName(nameDictionaryService.translateTransFields(patient.getFirstName(), transliteration));
				patient.setSecondName(nameDictionaryService.translateTransFields(patient.getSecondName(), transliteration));
				patient.setThirdName(nameDictionaryService.translateTransFields(patient.getThirdName(), transliteration));
				patient.setLastName(nameDictionaryService.translateTransFields(patient.getLastName(), transliteration));
				patient.setMotherName(nameDictionaryService.translateTransFields(patient.getMotherName(), transliteration));
				patient.setBalance(BigDecimal.ZERO);
				reflectionUtil.saveEntitySeparate(getRepository(), patient);
			} catch (Exception e) {
				e.printStackTrace();
				ExcelUtil.handleExcelExceptions(es, row, e);
			}
		}
		serialSerivce.updateSerial(fileNoSerial);//update serial,will be flushed with balances(session.update(...) throws version exception)
		try {
			workbook.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return es;
	}

	public Long getActivePatientCount(Boolean isActive) {
		return repo.countActivePatients(isActive);
	}

	public Long getTotalNewPatients() {
		Date from = new Date();
		from = DateUtil.addDays(from, -7);
		from = DateUtil.setHour(from, 24);
		from = DateUtil.setMinute(from, 0);
		from = DateUtil.setSecond(from, 0);
		Date to = new Date();
		return repo.countNewPatients(from, to);
	}

}

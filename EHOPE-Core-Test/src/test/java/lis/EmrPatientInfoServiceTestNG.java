package lis;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.Test;

import com.optimiza.core.base.helper.SearchCriterion;
import com.optimiza.core.base.helper.SearchCriterion.FilterOperator;
import com.optimiza.core.common.helper.FilterablePageRequest;
import com.optimiza.core.common.helper.FilterablePageRequest.JunctionOperator;
import com.optimiza.core.common.helper.FilterablePageRequest.OrderObject;
import com.optimiza.ehope.lis.model.EmrPatientInfo;
import com.optimiza.ehope.lis.model.LabSection;
import com.optimiza.ehope.lis.service.EmrPatientInfoService;
import com.optimiza.ehope.lis.service.LabSectionService;

@Test(priority = 1)
@ContextConfiguration(locations = { "classpath:spring-test-config.xml" })
public class EmrPatientInfoServiceTestNG extends AbstractTestNGSpringContextTests {

	@Autowired
	private EmrPatientInfoService service;

	@Autowired
	private LabSectionService sectionService;

	@Test(priority = 1, enabled = true)
	public void patientFindAll() {
		System.out.println("TestNew");

		List<LabSection> sections = new ArrayList<LabSection>();
		sections = sectionService.sectionSearch();
		System.out.println(sections);
		//		System.out.println("############ PatientInfo List = " + service.patientSearch().size());

		//		System.out.println("####### Patient with id 1 = " + service.findById(15L));
		//		EmrPatientInfo patient = new EmrPatientInfo();
		//		TransField firstName = new TransField();
		//		firstName.put("en_us", "eshraq");
		//		firstName.put("ar_jo", "");
		//		patient.setFirstName(firstName);
		//		TransField familyName = new TransField();
		//		familyName.put("en_us", "Albakri");
		//		familyName.put("ar_jo", "");
		//		patient.setFamilyName(familyName);
		//		TransField secondName = new TransField();
		//		secondName.put("en_us", "Ghaleb");
		//		secondName.put("ar_jo", "");
		//		TransField thirdName = new TransField();
		//		thirdName.put("en_us", "alii");
		//		thirdName.put("ar_jo", "");
		//		patient.setSecondName(secondName);
		//		patient.setThirdName(thirdName);
		//		patient.setEmail("bakri.eshraq@gmail.com");
		//
		//		patient.setMobileNo("0798682516");
		//		DateUtil myDate = new DateUtil();
		//		@SuppressWarnings("deprecation")
		//		Date myDate1 = new Date(2014, 01, 22);
		//		Calendar cal = Calendar.getInstance();
		//		cal.setTime(myDate1);
		//		System.out.println("######### Date" + myDate1);
		//		Date myDate2 = cal.getTime();
		//		patient.setFileNo("1233");
		//		//		Date myDate = new Date(2014, 02, 11);
		//		System.out.println("########## myDate " + myDate2);
		//		patient.setDateOfBirth(myDate2);
		//		patient.setTenantId(1L);
		//
		//		System.out.println("####### inserted patient = " + service.addPatient(patient));
		//		EmrPatientInfo oldPatient = new EmrPatientInfo();
		//		oldPatient = service.findById(16L);
		//		oldPatient.setAddress("Qatar");
		//		service.deletePatient(16L);
		//		System.out.println("####### updated patient = " + service.updatePatient(oldPatient).getAddress());
	}

	@Test(priority = 1, enabled = true)
	public void testFindPag() {

		FilterablePageRequest filterablePageRequest = new FilterablePageRequest();
		filterablePageRequest.setPage(0);
		filterablePageRequest.setSize(10);
		filterablePageRequest.setSortList(new ArrayList<OrderObject>());

		List<SearchCriterion> filters = new ArrayList<SearchCriterion>();

		SearchCriterion searchCriterion = new SearchCriterion();
		//		searchCriterion.setField("emrPatientInsurance.insProvider.name");
		//		searchCriterion.setValue("cbc");
		searchCriterion.setOperator(FilterOperator.contains);
		filters.add(searchCriterion);

		filterablePageRequest.setFilters(filters);
		Page<EmrPatientInfo> result = service.find(filterablePageRequest.getFilters(), filterablePageRequest.getPageRequest(),
				EmrPatientInfo.class, JunctionOperator.And, "emrPatientInsurance.insProvider");

		System.out.println("WWWWWWWWWWWWW  ################# " + result.getContent().size());
	}

}

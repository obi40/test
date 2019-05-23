package com.optimiza.ehope.lis.test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.Test;

import com.optimiza.core.admin.lkp.service.LkpUserStatusService;
import com.optimiza.core.admin.model.SecUser;
import com.optimiza.core.admin.service.SecUserService;
import com.optimiza.core.common.data.model.TransField;
import com.optimiza.core.lkp.service.ComLanguageService;
import com.optimiza.core.lkp.service.LkpGenderService;

/**
 * SecUserTestNG.java
 * 
 * @author Abdullah Imran <aImran@optimizasolutions.com>
 * @since Sep/27/2017
 **/

@Test(priority = 1)
@ContextConfiguration(locations = { "classpath:spring-test-config.xml" })
public class SecUserTestNG extends AbstractTestNGSpringContextTests {

	@Autowired
	private SecUserService secUserService;

	@Autowired
	private LkpUserStatusService lkpUserStatusService;

	@Autowired
	private LkpGenderService lkpGenderService;

	@Autowired
	private ComLanguageService comLanguageService;

	private Long createdSecUserRid;

	@Test(priority = 1, enabled = false)
	public void testCreateSecUser() {
		System.out.println("##########");
		System.out.println("testCreateSecUser()");

		TransField tf = new TransField();
		TransField tf2 = new TransField();
		TransField tf3 = new TransField();
		TransField tf4 = new TransField();
		TransField tf5 = new TransField();

		tf.put("en_us", "address1");
		tf2.put("en_us", "Imran");
		tf3.put("en_us", "Abdullah");
		tf4.put("en_us", "John");
		tf5.put("en_us", "Jawdat");

		SecUser secUser = new SecUser();
		secUser.setIsActive(Boolean.TRUE);
		secUser.setLkpUserStatus(this.lkpUserStatusService.findById(3L));
		secUser.setLkpGender(this.lkpGenderService.findById(1L));
		secUser.setComLanguage(this.comLanguageService.findById(1L));
		secUser.setAddress(tf);
		secUser.setFamilyName(tf2);
		secUser.setFirstName(tf3);
		secUser.setSecondName(tf4);
		secUser.setThirdName(tf5);

		secUser.setMobileNo("079");
		secUser.setEmail("aji@hotmail.com");
		secUser.setPassword("pass");
		secUser.setUsername("username");
		secUser.setNationalId(123L);
		//secUser = this.secUserService.createSecUser(secUser, new ArrayList<>(), new ArrayList<>());
		this.createdSecUserRid = secUser.getRid();

		System.out.println("Created SecUser with Id[" + secUser.getRid() + "]");
		System.out.println("##########");

	}

	@Test(priority = 2, enabled = false)
	public void testFindSecUserById() {
		System.out.println("##########");
		System.out.println("testFindSecUserById()");

		SecUser secUser = this.secUserService.findSecUserById(this.createdSecUserRid);

		System.out.println("Found SecUser with Id[" + secUser.getRid() + "]: " + secUser);
		System.out.println("##########");

	}

	@Test(priority = 3, enabled = false)
	public void testUpdateSecUser() {
		System.out.println("##########");
		System.out.println("testUpdateSecUser()");

		SecUser secUser = this.secUserService.findSecUserById(this.createdSecUserRid);

		// UPDATE VALUES

		//secUser = this.secUserService.updateSecUser(secUser);

		System.out.println("Updated SecUser with Id[" + secUser.getRid() + "]: " + secUser);
		System.out.println("##########");

	}

	@Test(priority = 4, enabled = false)
	public void testDeleteSecUser() {
		System.out.println("##########");
		System.out.println("testDeleteSecUser()");

		SecUser secUser = this.secUserService.findSecUserById(this.createdSecUserRid);

		System.out.println("CreationDate: " + secUser.getCreationDate());

		//this.secUserService.deleteSecUser(secUser);
		System.out.println("Deleted SecUser with Id[" + secUser.getRid() + "]");

		System.out.println("##########");

	}

}

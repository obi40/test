package com.optimiza.core.admin.helper;

public class AdminRights {

	//Views
	public final static String VIEW_TENANT_MANAGEMENT = "VIEW_TENANT_MANAGEMENT";
	public final static String VIEW_LKP_MANAGEMENT = "VIEW_LKP_MANAGEMENT";

	public final static String VIEW_USERS_MANAGEMENT = "VIEW_USERS_MANAGEMENT";
	public final static String VIEW_GROUPS_MANAGEMENT = "VIEW_GROUPS_MANAGEMENT";
	public final static String VIEW_ROLES_MANAGEMENT = "VIEW_ROLES_MANAGEMENT";

	//SecTenantService	
	public final static String UPD_TENANT = "UPD_TENANT";

	//SecUserService
	public final static String ADD_USER = "ADD_USER";
	public final static String UPD_USER = "UPD_USER";
	public final static String DEACTIVATE_USER = "DEACTIVATE_USER";
	public final static String ACTIVATE_USER = "ACTIVATE_USER";
	public final static String RESET_PASS_USER = "RESET_PASS_USER";
	public final static String CHANGE_PASS_EMAIL_USER = "CHANGE_PASS_EMAIL_USER";

	//SecGroupService	
	public final static String ADD_GROUP = "ADD_GROUP";
	public final static String UPD_GROUP = "UPD_GROUP";
	public final static String DEL_GROUP = "DEL_GROUP";

	//SecRoleService	
	public final static String ADD_ROLE = "ADD_ROLE";
	public final static String UPD_ROLE = "UPD_ROLE";
	public final static String DEL_ROLE = "DEL_ROLE";

	//LkpService
	public final static String ADD_LKP = "ADD_LKP";
	public final static String DEL_LKP = "DEL_LKP";
	public final static String UPD_LKP = "UPD_LKP";

}

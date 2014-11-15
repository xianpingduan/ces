package com.xiexin.ces;

public class Constants {
	public final static boolean DEBUG = false;

	// login
	public final static String USER_ID = "user_id";
	public final static String USER_NAME = "user_name";
	public final static String PWD = "pwd";
	public final static String DEPART = "depart";
	public final static String TITLE = "title";
	public final static String JOB = "job";
	public final static String LOCKED = "locked";

	// zhangtao
	public final static String ZHANG_TAO_CONN_NAME = "zhang_tao_conn_name";
	public final static String ZHANG_TAO_ACCINFO = "zhang_tao_accinfo";
	public final static String ZHANG_TAO_LIST = "zhangtao_list";

	// server config
	public final static String SERVER_CONFIG_URL = "server_config_url";
	public final static String SERVER_CONFIG_PORT = "server_config_port";
	public final static String SERVER_CONFIG_FIRST_IN = "server_config_first_in";

	// net
	public static String ROOT = "http://core130.com:8081";
	public final static String ROOT_URL = ROOT + "/api/CESApp/";
	public final static String LOGIN_URL = "Login";
	public final static String ZHANG_TAO_URL = "GetAccountByUserID";
	public final static String GET_WORK_MESSAGE_URL = "GetWorkMessage";

	// approval type

	// 1.待审批任务2.暂存代办3.已审批事项4.已发事项

	public final static int TYPE_PEND_APPROVAL_TASKS = 1;
	public final static int TYPE_SCRATCH_UPCOME_TASKS = 2;
	public final static int TYPE_APPROVED_TASKS = 3;
	public final static int TYPE_SEND_ITEM_TASKS = 4;
	
	
	//list data add type
	public final static int TYPE_LIST_ADD_APPEND = 1;
	public final static int TYPE_LIST_ADD_COVER = 2;
	
	
	//page size
	public final static int PAGE_SIZE=25;

}

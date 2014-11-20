package com.xiexin.ces;

public class Constants
{
    public final static boolean DEBUG = false;

    // login
    public final static String USER_ID = "user_id";
    public final static String USER_NAME = "user_name";
    public final static String PWD = "pwd";
    public final static String PWD_MD5 = "pwd_md5";
    public final static String DEPART = "depart";
    public final static String TITLE = "title";
    public final static String JOB = "job";
    public final static String LOCKED = "locked";

    public final static String AUTO_LOGIN = "auto_login";
    public final static String REMEBER_PWD = "remeber_pwd";

    // zhangtao
    public final static String ZHANG_TAO_CONN_NAME = "zhang_tao_conn_name";
    public final static String ZHANG_TAO_ACCINFO = "zhang_tao_accinfo";
    public final static String ZHANG_TAO_LIST = "zhangtao_list";

    // server config
    public final static String SERVER_CONFIG_URL = "server_config_url";
    public final static String SERVER_CONFIG_PORT = "server_config_port";
    public final static String SERVER_CONFIG_FIRST_IN = "server_config_first_in";
    public final static String SERVER_CONFIG_REQ = "server_config_req";
    // public final static String
    // SERVER_CONFIG_SET_FROM="server_config_set_from";
    // public final static int SERVER_CONFIG_SET_FROM_LOGIN=1;
    // public final static int SERVER_CONFIG_SET_FROM_MENU=2;

    // net
    //    public static String ROOT = "http://core130.com:8081";

    public static String ROOT = App.getSharedPreference( ).getString( Constants.SERVER_CONFIG_URL , "http://core130.com" ) + ":"
	    + App.getSharedPreference( ).getString( Constants.SERVER_CONFIG_PORT , "8081" );

    public final static String ROOT_URL = ROOT + "/api/CESApp/";
    public final static String LOGIN_URL = "Login";
    public final static String ZHANG_TAO_URL = "GetAccountByUserID";
    public final static String GET_WORK_MESSAGE_URL = "GetWorkMessage";
    public final static String GET_APPROVAL_ROAD_LIST = "GetApprListForDataNbr";
    public final static String GET_EMPLOYEE_LIST = "GetEmployee";
    public final static String SUBMIT_WORK_FLOW = "SubmitWorkFlow";
    public final static String SET_PLUS_SIGN = "SetPlusSign";
    public final static String SET_APPR_ATTENTION = "SetApprAttention";
    public final static String GET_MOBILE_CFG = "GetMobileCfg";
    public final static String GET_DOC_INFORMATION = "GetDocInformation";
    // approval type

    // 1.待审批任务2.暂存代办3.已审批事项4.已发事项

    public final static int TYPE_PEND_APPROVAL_TASKS = 1;
    public final static int TYPE_SCRATCH_UPCOME_TASKS = 2;
    public final static int TYPE_APPROVED_TASKS = 3;
    public final static int TYPE_SEND_ITEM_TASKS = 4;
    public final static String INVOICE_TYPE = "invoice_type";

    //list data add type
    public final static int TYPE_LIST_ADD_APPEND = 1;
    public final static int TYPE_LIST_ADD_COVER = 2;

    //page size
    public final static int PAGE_SIZE = 25;

    //road
    public final static String PRGID = "prgid";
    public final static String DATANBR = "datanbr";

    //select employee
    public final static String CHECK_EMPLOYEE_FROM = "check_employee_from";
    public final static int CHECK_EMPLOYEE_FROM_SETPLUGIN = 1;
    public final static int CHECK_EMPLOYEE_FROM_NOTIFY = 2;
    
    //info
    public final static String DET_INFO = "det_info";
    public final static String DET_CONFIG = "det_config";
    public final static String FILES_PATH = "files_path";

}

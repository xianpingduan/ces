package com.xiexin.ces;

import org.json.JSONException;
import org.json.JSONObject;

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
    // public static String ROOT = "http://core130.com:8081";

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
    public final static String GET_MSG_URL = "GetMessage";
    public final static String SET_MESSAGE_READ = "SetMessageRead";
    public final static String DEL_MSG = "DeleteMessage";
    // approval type

    // 1.待审批任务2.暂存代办3.已审批事项4.已发事项

    public final static int TYPE_PEND_APPROVAL_TASKS = 1;
    public final static int TYPE_SCRATCH_UPCOME_TASKS = 2;
    public final static int TYPE_APPROVED_TASKS = 3;
    public final static int TYPE_SEND_ITEM_TASKS = 4;
    public final static String INVOICE_TYPE = "invoice_type";

    // list data add type
    public final static int TYPE_LIST_ADD_APPEND = 1;
    public final static int TYPE_LIST_ADD_COVER = 2;

    // page size
    public final static int PAGE_SIZE = 25;

    // road
    public final static String PRGID = "prgid";
    public final static String DATANBR = "datanbr";

    // select employee
    public final static String CHECK_EMPLOYEE_FROM = "check_employee_from";
    public final static int CHECK_EMPLOYEE_FROM_SETPLUGIN = 1;
    public final static int CHECK_EMPLOYEE_FROM_NOTIFY = 2;

    // info
    public final static String DET_INFO = "det_info";
    public final static String DET_CONFIG = "det_config";
    public final static String DET_HEAD_CONFIG = "det_head_config";
    public final static String FILES_PATH = "files_path";
    public final static String APPR_LIST = "appr_list";
    
    
    public final static String APPR_LIST_RESULT_FROM = "appr_list_result_from";
    public final static int APPR_LIST_RESULT_FROM_RETURN = 0;
    public final static int APPR_LIST_RESULT_FROM_APPRSUC = 1;
    
    

    public final static String THE_LAST_REQUEST_MSG_TIME = "the_last_request_msg_time";
    public final static int DEFAULT_GAP_TIME = 60 * 1000;

    public final static String RQ001 = "{\"Data\":{\"RqNbr\":\"申请单号\",\"AccType\":\"核算类型\",\"Duty\":\"申请人\",\"Title\":\"职别\",\"Depart\":\"申请人部门\",\"Channel\":\"归属渠道\",\"CtrlType\":\"控制类型\",\"Currency\":\"币种\",\"TotalCost\":\"申请金额\",\"BrCost\":\"借款金额\",\"AccItem\":\"核算项目\",\"Project\":\"项目\",\"Reason\":\"申请事由\",\"IsClosed\":\"是否关闭\",\"Period\":\"账期\",\"Status\":\"业务状态\",\"UpdateUser\":\"更新人\",\"UpdateDT\":\"更新日期\",\"CrtDate\":\"创建时间\",\"Remark\":\"备注\",\"Version\":\"版本号\",\"EffDate\":\"生效日期\",\"Exceed\":\"是否超预算\"},\"Det\":{\"RqNbr\":\"申请单号\",\"LineNbr\":\"行号\",\"Summary\":\"核准说明\",\"PeopNbr\":\"人数\",\"BelongKind\":\"归属类型\",\"BelongObj\":\"归属对象\",\"BelongDep\":\"归属部门\",\"BelongChannel\":\"归属渠道\",\"BelongCus\":\"归属客户\",\"BelongVendor\":\"归属供应商\",\"CurrID\":\"币种编码\",\"AccItem\":\"核算项目\",\"Leader\":\"上级项目\",\"TotalCost\":\"申请金额\",\"VerCost\":\"核销金额\",\"UpdateUser\":\"更新人\",\"UpdateDT\":\"更新日期\",\"CrtDate\":\"创建时间\",\"Status\":\"业务状态\",\"Remark\":\"备注\",\"EffDate\":\"生效日期\",\"Version\":\"版本号\",\"HappenDate\":\"发生日期\",\"MSummary\":\"核准说明\"}}";
    public final static String AC001 = "{\"Data\":{\"VerNbr\":\"核销单号\",\"CrtDate\":\"创建时间\",\"RqNbr\":\"申请单号\",\"Duty\":\"申请人\",\"Title\":\"申请人职别\",\"Depart\":\"申请人部门\",\"Channel\":\"归属渠道\",\"TotalCost\":\"报销金额\",\"VerBrCost\":\"冲借款金额\",\"PayTotal\":\"已付总金额\",\"Period\":\"账期\",\"Currency\":\"币种\",\"PayMethod\":\"核销方式\",\"AccType\":\"核算类型\",\"Bank\":\"收款人开户行\",\"Account\":\"银行账号\",\"AccName\":\"开户人\",\"AccItem\":\"核算项目\",\"Reason\":\"申请事由\",\"Project\":\"项目\",\"BackInvoice\":\"是否先付款后回票据\",\"BusStatus\":\"业务状态\",\"Status\":\"业务状态\",\"UpdateUser\":\"更新人\",\"UpdateDT\":\"更新日期\",\"Remark\":\"备注\",\"Version\":\"版本号\",\"EffDate\":\"生效日期\",\"VerBrDate\":\"核销日期\",\"PayDate\":\"付款日期\",\"SuspDate\":\"挂账日期\",\"IsClosed\":\"是否关闭\",\"Exceed\":\"是否超预算\",\"PayMark\":\"付款标识\",\"SuspendMark\":\"挂账标识\",\"VerBorrowMark\":\"冲借款标识\",\"PayBank\":\"付款银行\",\"BorrowList\":\"借款单号集\"},\"Det\":{\"Summary\":\"核准摘要\",\"VerNbr\":\"核销单号\",\"LineNbr\":\"行号\",\"PeopNbr\":\"人数\",\"BelongKind\":\"归属类型\",\"BelongObj\":\"归属对象\",\"ObjDescr\":\"对象详细信息\",\"CurrID\":\"币种编码\",\"ActCost\":\"报销金额\",\"AccItem\":\"核算项目\",\"UpdateUser\":\"更新人\",\"UpdateDT\":\"更新日期\",\"CrtDate\":\"创建时间\",\"Status\":\"业务状态\",\"Remark\":\"备注\",\"EffDate\":\"生效日期\",\"Version\":\"版本号\",\"BelongDep\":\"归属部门\",\"BelongChannel\":\"归属渠道\",\"BelongCus\":\"归属客户\",\"BelongVendor\":\"归属供应商\",\"MAccItem\":\"核准项目\",\"MSummary\":\"核准摘要\",\"MActCost\":\"核准金额\",\"HappenDate\":\"发生日期\"}}";
    public final static String BT001 = "{\"Data\":{\"BtripNbr\":\"出差申请单号\",\"CrtDate\":\"创建时间\",\"Period\":\"账期\",\"Duty\":\"申请人\",\"Area\":\"申请人所在地区\",\"Depart\":\"申请人部门\",\"Title\":\"职别\",\"Job\":\"申请人岗位\",\"TotalCost\":\"申请金额\",\"Remark\":\"备注\",\"PayMethod\":\"核销方式\",\"AccType\":\"核算类型\",\"Channel\":\"归属渠道\",\"Currency\":\"币种\",\"AccName\":\"开户人\",\"Account\":\"银行账号\",\"Bank\":\"申请人开户行\",\"Reason\":\"出差事由\",\"Project\":\"项目\",\"Status\":\"业务状态\",\"BrCost\":\"借款金额\",\"AccItem\":\"核算项目\",\"UpdateUser\":\"更新人\",\"UpdateDT\":\"更新日期\",\"Version\":\"版本号\",\"EffDate\":\"生效日期\",\"IsClosed\":\"是否关闭\",\"Exceed\":\"是否超预算\",\"IsMerge\":\"同行人合并填单\"},\"Det\":{\"BtripNbr\":\"出差申请单号\",\"LineNbr\":\"行号\",\"Summary\":\"核准说明\",\"DateFm\":\"开始时间\",\"DateTo\":\"结束时间\",\"Days\":\"天数\",\"AreaFm\":\"起始地点\",\"AreaTo\":\"终止地点\",\"AreaKind\":\"城市类别\",\"Colleague\":\"同行人\",\"Traffic\":\"交通工具编码\",\"Item1\":\"机票费\",\"Item2\":\"长途车船费\",\"Item3\":\"市内交通费\",\"Item4\":\"住宿费\",\"Item5\":\"交通补助\",\"Item6\":\"伙食补助\",\"Item7\":\"电话补助\",\"Item8\":\"包干补助\",\"Item9\":\"其他\",\"Item10\":\"预留项目1\",\"Item11\":\"预留项目2\",\"Item12\":\"预留项目3\",\"Item13\":\"预留项目4\",\"Item14\":\"预留项目5\",\"Item15\":\"预留项目6\",\"TotalCost\":\"申请金额\",\"CurrID\":\"币种编码\",\"Process\":\"处理方\",\"AccItem\":\"核算项目\",\"BelongDep\":\"归属部门\",\"BelongChannel\":\"归属渠道\",\"BelongCus\":\"归属客户\",\"BelongVendor\":\"归属供应商\",\"Status\":\"业务状态\",\"Descr\":\"详细信息\",\"CrtDate\":\"创建时间\",\"UpdateUser\":\"更新人\",\"UpdateDT\":\"更新日期\",\"Remark\":\"备注\",\"Version\":\"版本号\",\"EffDate\":\"生效日期\",\"MSummary\":\"核准说明\",\"IsValid\":\"\",\"HappenDate\":\"发生日期\"}}";
    public final static String BT003 = "{\"Data\":{\"BtripNbr\":\"出差申请单号\",\"VerNbr\":\"核销单号\",\"CrtDate\":\"创建时间\",\"Period\":\"账期\",\"Duty\":\"申请人\",\"Depart\":\"申请人部门\",\"Title\":\"申请人职别\",\"Job\":\"申请人岗位\",\"Currency\":\"币种\",\"Area\":\"申请人所在地区\",\"TotalCost\":\"报销金额\",\"VerBrCost\":\"冲借款金额\",\"PayTotal\":\"已付总金额\",\"PayMethod\":\"核销方式\",\"AccName\":\"开户人\",\"Bank\":\"收款人开户行\",\"Account\":\"银行账号\",\"Reason\":\"出差事由\",\"Remark\":\"备注\",\"AccType\":\"核算类型\",\"Channel\":\"归属渠道\",\"BackInvoice\":\"是否先付款后回票据\",\"BusStatus\":\"业务状态\",\"Project\":\"项目\",\"Status\":\"业务状态\",\"AccItem\":\"核算项目\",\"UpdateUser\":\"更新人\",\"UpdateDT\":\"更新日期\",\"Version\":\"版本号\",\"EffDate\":\"生效日期\",\"SuspDate\":\"挂账日期\",\"VerBrDate\":\"核销日期\",\"PayDate\":\"付款日期\",\"IsClosed\":\"是否关闭\",\"Exceed\":\"是否超预算\",\"PayMark\":\"付款标识\",\"SuspendMark\":\"挂账标识\",\"VerBorrowMark\":\"冲借款标识\",\"IsMerge\":\"同行人合并填单\",\"PayBank\":\"付款银行\",\"BorrowList\":\"借款单号集\"},\"Det\":{\"VerNbr\":\"核销单号\",\"LineNbr\":\"行号\",\"Summary\":\"核准摘要\",\"DateFm\":\"开始时间\",\"DateTo\":\"结束时间\",\"Days\":\"天数\",\"AreaFm\":\"起始地点\",\"AreaTo\":\"终止地点\",\"AreaKind\":\"城市类别\",\"Colleague\":\"同行人\",\"Traffic\":\"交通工具编码\",\"Item1\":\"机票费\",\"Item2\":\"长途车船费\",\"Item3\":\"市内交通费\",\"Item4\":\"住宿费\",\"Item5\":\"交通补助\",\"Item6\":\"伙食补助\",\"Item7\":\"电话补助\",\"Item8\":\"包干补助\",\"Item9\":\"其他\",\"Item10\":\"预留项目1\",\"Item11\":\"预留项目2\",\"Item12\":\"预留项目3\",\"Item13\":\"预留项目4\",\"Item14\":\"预留项目5\",\"Item15\":\"预留项目6\",\"ActCost\":\"报销金额\",\"CurrID\":\"币种编码\",\"Process\":\"处理方\",\"AccItem\":\"核算项目\",\"CrtDate\":\"创建时间\",\"UpdateUser\":\"更新人\",\"UpdateDT\":\"更新日期\",\"Remark\":\"备注\",\"Version\":\"版本号\",\"EffDate\":\"生效日期\",\"BelongDep\":\"归属部门\",\"BelongChannel\":\"归属渠道\",\"BelongCus\":\"归属客户\",\"BelongVendor\":\"归属供应商\",\"MAccItem\":\"核准项目\",\"MSummary\":\"核准摘要\",\"MActCost\":\"核准金额\",\"Status\":\"业务状态\",\"Descr\":\"详细信息\",\"IsValid\":\"是否核销\",\"HappenDate\":\"发生日期\"}}";
    public final static String FA001 = "{\"Data\":{\"AccItem\":\"核算项目\",\"Account\":\"银行账号\",\"AccType\":\"核算类型\",\"BackTime\":\"预计还款时间\",\"Bank\":\"申请人开户行\",\"bDepBorrow\":\"是否部门借款\",\"BorrowType\":\"借款类型\",\"BRNbr\":\"借款单号\",\"Channel\":\"归属渠道\",\"CrtDate\":\"创建时间\",\"Currency\":\"币种\",\"Depart\":\"申请人部门\",\"Duty\":\"申请人\",\"EffDate\":\"生效日期\",\"ID\":\"自增列\",\"PayBank\":\"付款银行\",\"PayCost\":\"付款金额\",\"PayDate\":\"付款日期\",\"PayMethod\":\"支付方式\",\"Period\":\"账期\",\"Project\":\"项目\",\"Reason\":\"借款事由\",\"Remark\":\"备注\",\"RePayCost\":\"还款金额\",\"Status\":\"业务状态\",\"Title\":\"职别\",\"TotalCost\":\"借款金额\",\"UpdateDT\":\"更新日期\",\"UpdateUser\":\"更新人\",\"VerBrDate\":\"冲借款日期\",\"VerCost\":\"核销金额\",\"Version\":\"版本号\"},\"Det\":{\"BRNbr\":\"借款单号\",\"LineNbr\":\"行号\",\"DocType\":\"来源单据类型\",\"DataNbr\":\"来源单据\",\"BrCost\":\"借款金额\",\"UpdateUser\":\"更新人\",\"UpdateDT\":\"更新日期\",\"CrtDate\":\"创建时间\",\"Status\":\"业务状态\",\"Remark\":\"备注\",\"EffDate\":\"生效日期\",\"Version\":\"版本号\",\"MSummary\":\"核准说明\"}}";
    public final static String FA002 = "{\"Data\":{\"RePayNbr\":\"还款单号\",\"CrtDate\":\"创建时间\",\"Duty\":\"申请人\",\"Title\":\"职别\",\"Depart\":\"申请人部门\",\"Channel\":\"归属渠道\",\"RtnCost\":\"还款金额\",\"Currency\":\"币种\",\"PayMethod\":\"还款方式\",\"AccType\":\"核算类型\",\"AccItem\":\"核算项目\",\"Bank\":\"收款人开户行\",\"AccName\":\"开户人\",\"Account\":\"银行账号\",\"Remark\":\"备注\",\"UpdateUser\":\"更新人\",\"UpdateDT\":\"更新日期\",\"Status\":\"业务状态\",\"EffDate\":\"生效日期\",\"Version\":\"版本号\",\"Period\":\"账期\",\"bRePayment\":\"是否还款\",\"RePayDesc\":\"还款描述\",\"VerBrDate\":\"核销借款日期\",\"VerBrCost\":\"核销借款金额\",\"RePayType\":\"还款类型\",\"Reason\":\"借款事由\"},\"Det\":{\"RePayNbr\":\"还款单号\",\"LineNbr\":\"行号\",\"DocType\":\"来源单据类型\",\"DataNbr\":\"来源单据\",\"BrCost\":\"借款金额\",\"CurRtnCost\":\"还款金额\",\"UpdateUser\":\"更新人\",\"UpdateDT\":\"更新日期\",\"CrtDate\":\"创建时间\",\"Status\":\"业务状态\",\"Remark\":\"备注\",\"EffDate\":\"生效日期\",\"Version\":\"版本号\",\"MSummary\":\"核准摘要\"}}";
    public final static String IS001 = "{\"Data\":{\"LoanNbr\":\"借出单号\",\"CrtDate\":\"创建时间\",\"Duty\":\"借出人\",\"Title\":\"职别\",\"Depart\":\"部门\",\"TotalCost\":\"核准金额\",\"Currency\":\"币种\",\"AccType\":\"核算类型\",\"UpdateUser\":\"更新人\",\"UpdateDT\":\"更新日期\",\"Status\":\"业务状态\",\"Remark\":\"备注\",\"EffDate\":\"生效日期\",\"Version\":\"版本号\",\"LoanType\":\"借出类型\",\"Recipient\":\"收件人\",\"Address\":\"地址\",\"Tel\":\"电话\",\"ExprCompany\":\"快递公司\"},\"Det\":{\"LoanNbr\":\"借出单号\",\"LineNbr\":\"行号\",\"TotalCost\":\"借出金额\",\"AccItem\":\"核算项目\",\"ExpRtnDate\":\"预计归还时间\",\"InvoiceType\":\"票据类型\",\"DocType\":\"来源单据类型\",\"DataNbr\":\"来源单据\",\"PayNbr\":\"还款单号\",\"RtnNbr\":\"归还单号\",\"RePayNbr\":\"还款单号\",\"ActRtnDate\":\"实际归还时间\",\"ActRtnCost\":\"实际收回金额\",\"CrtDate\":\"创建时间\",\"UpdateUser\":\"更新人\",\"UpdateDT\":\"更新日期\",\"Status\":\"业务状态\",\"Remark\":\"备注\",\"EffDate\":\"生效日期\",\"Version\":\"版本号\",\"InvNbr\":\"发票号\",\"MSummary\":\"核准摘要\"}}";
    public final static String IS002 = "{\"Data\":{\"RtnNbr\":\"还票单号\",\"CrtDate\":\"创建时间\",\"Duty\":\"归还人\",\"Title\":\"职别\",\"Depart\":\"部门\",\"Totalcost\":\"申请金额\",\"Currency\":\"币种\",\"AccType\":\"核算类型\",\"Returned\":\"归还确认\",\"Remark\":\"备注\",\"UpdateUser\":\"更新人\",\"UpdateDT\":\"更新日期\",\"Status\":\"业务状态\",\"EffDate\":\"生效日期\",\"Version\":\"版本号\"},\"Det\":{\"RtnNbr\":\"归还单号\",\"LineNbr\":\"行号\",\"TotalCost\":\"归还金额\",\"AccItem\":\"核算项目\",\"InvoiceType\":\"票据类型\",\"InvNbr\":\"发票号\",\"DocType\":\"来源单据类型\",\"DataNbr\":\"来源单据\",\"sInvoiceType\":\"票据类型\",\"CrtDate\":\"创建时间\",\"UpdateUser\":\"更新人\",\"UpdateDT\":\"更新日期\",\"Status\":\"业务状态\",\"Remark\":\"备注\",\"EffDate\":\"生效日期\",\"Version\":\"版本号\",\"MSummary\":\"核准摘要\"}}";

    public static JSONObject getData( String type )
    {

	String jsonStr = "{}";
	if( type.equals( "RQ001" ) )
	{
	    jsonStr = Constants.RQ001;
	}
	else if( type.equals( "AC001" ) )
	{
	    jsonStr = Constants.AC001;
	}
	else if( type.equals( "BT001" ) )
	{
	    jsonStr = Constants.BT001;
	}
	else if( type.equals( "BT003" ) )
	{
	    jsonStr = Constants.BT003;
	}
	else if( type.equals( "FA001" ) )
	{
	    jsonStr = Constants.FA001;
	}
	else if( type.equals( "FA002" ) )
	{
	    jsonStr = Constants.FA002;
	}
	else if( type.equals( "IS001" ) )
	{
	    jsonStr = Constants.IS001;
	}
	else if( type.equals( "IS002" ) )
	{
	    jsonStr = Constants.IS002;
	}
	try
	{
	    JSONObject obj = new JSONObject( jsonStr );
	    return obj.getJSONObject( "Data" );
	}
	catch ( JSONException e )
	{
	    e.printStackTrace( );
	}
	return null;
    }

    public static JSONObject getDet( String type )
    {
	String jsonStr = "{}";
	if( type.equals( "RQ001" ) )
	{
	    jsonStr = Constants.RQ001;
	}
	else if( type.equals( "AC001" ) )
	{
	    jsonStr = Constants.AC001;
	}
	else if( type.equals( "BT001" ) )
	{
	    jsonStr = Constants.BT001;
	}
	else if( type.equals( "BT003" ) )
	{
	    jsonStr = Constants.BT003;
	}
	else if( type.equals( "FA001" ) )
	{
	    jsonStr = Constants.FA001;
	}
	else if( type.equals( "FA002" ) )
	{
	    jsonStr = Constants.FA002;
	}
	else if( type.equals( "IS001" ) )
	{
	    jsonStr = Constants.IS001;
	}
	else if( type.equals( "IS002" ) )
	{
	    jsonStr = Constants.IS002;
	}
	try
	{
	    JSONObject obj = new JSONObject( jsonStr );
	    return obj.getJSONObject( "Det" );
	}
	catch ( JSONException e )
	{
	    e.printStackTrace( );
	}
	return null;
    }

    // 1.提交、2.同意、3.已阅、4.拒绝、5.中止、6.还原、7.回退、8.回收、9.暂存、10.撤回
    public static String getType( int i )
    {
	String type = "";
	switch ( i )
	{
	    case 2 :
		type = "同意";
		break;
	    case 3 :
		type = "已阅";
		break;
	    case 4 :
		type = "拒绝";
		break;
	    case 5 :
		type = "中止";
		break;
	    case 6 :
		type = "还原";
		break;
	    case 7 :
		type = "回退";
		break;
	    case 8 :
		type = "回收";
		break;
	    case 9 :
		type = "暂存";
		break;
	    case 10 :
		type = "撤回";
		break;
	    default :
		break;
	}
	return type;
    }

    public final static String [] RQ001_DATA_DEFAULT = { "RqNbr" , "AccType" , "Duty" , "Title" , "Depart" , "Channel" , "CtrlType" , "Currency" , "TotalCost" , "BrCost" , "AccItem" , "Project" , "Reason" , "IsClosed" , "Period" , "Status" , "UpdateUser" , "UpdateDT" , "CrtDate" , "Remark" , "Version" , "EffDate" , "Exceed" };

    public final static String [] RQ001_DET_DEFAULT = { "RqNbr" , "LineNbr" , "Summary" , "PeopNbr" , "BelongKind" , "BelongObj" , "BelongDep" , "BelongChannel" , "BelongCus" , "BelongVendor" , "CurrID" , "AccItem" , "Leader" , "TotalCost" , "VerCost" , "UpdateUser" , "UpdateDT" , "CrtDate" , "Status" , "Remark" , "EffDate" , "Version" , "HappenDate" , "MSummary" };

    public final static String [] AC001_DATA_DEFAULT = { "VerNbr" , "CrtDate" , "RqNbr" , "Duty" , "Title" , "Depart" , "Channel" , "TotalCost" , "VerBrCost" , "PayTotal" , "Period" , "Currency" , "PayMethod" , "AccType" , "Bank" , "Account" , "AccName" , "AccItem" , "Reason" , "Project" , "BackInvoice" , "BusStatus" , "Status" , "UpdateUser" , "UpdateDT" , "Remark" , "Version" , "EffDate" , "VerBrDate" , "PayDate" , "SuspDate" , "IsClosed" , "Exceed" , "PayMark" , "SuspendMark" , "VerBorrowMark" , "PayBank" , "BorrowList" };

    public final static String [] AC001_DET_DEFAULT = { "Summary" , "VerNbr" , "LineNbr" , "PeopNbr" , "BelongKind" , "BelongObj" , "ObjDescr" , "CurrID" , "ActCost" , "AccItem" , "UpdateUser" , "UpdateDT" , "CrtDate" , "Status" , "Remark" , "EffDate" , "Version" , "BelongDep" , "BelongChannel" , "BelongCus" , "BelongVendor" , "MAccItem" , "MSummary" , "MActCost" , "HappenDate" };

    public final static String [] BT001_DATA_DEFAULT = { "BtripNbr" , "CrtDate" , "Period" , "Duty" , "Area" , "Depart" , "Title" , "Job" , "TotalCost" , "Remark" , "PayMethod" , "AccType" , "Channel" , "Currency" , "AccName" , "Account" , "Bank" , "Reason" , "Project" , "Status" , "BrCost" , "AccItem" , "UpdateUser" , "UpdateDT" , "Version" , "EffDate" , "IsClosed" , "Exceed" , "IsMerge" };

    public final static String [] BT001_DET_DEFAULT = { "BtripNbr" , "LineNbr" , "Summary" , "DateFm" , "DateTo" , "Days" , "AreaFm" , "AreaTo" , "AreaKind" , "Colleague" , "Traffic" , "Item1" , "Item2" , "Item3" , "Item4" , "Item5" , "Item6" , "Item7" , "Item8" , "Item9" , "Item10" , "Item11" , "Item12" , "Item13" , "Item14" , "Item15" , "TotalCost" , "CurrID" , "Process" , "AccItem" , "BelongDep" , "BelongChannel" , "BelongCus" , "BelongVendor" , "Status" , "Descr" , "CrtDate" , "UpdateUser" , "UpdateDT" , "Remark" , "Version" , "EffDate" , "MSummary" , "IsValid" , "HappenDate" };

    public final static String [] BT003_DATA_DEFAULT = { "BtripNbr" , "VerNbr" , "CrtDate" , "Period" , "Duty" , "Depart" , "Title" , "Job" , "Currency" , "Area" , "TotalCost" , "VerBrCost" , "PayTotal" , "PayMethod" , "AccName" , "Bank" , "Account" , "Reason" , "Remark" , "AccType" , "Channel" , "BackInvoice" , "BusStatus" , "Project" , "Status" , "AccItem" , "UpdateUser" , "UpdateDT" , "Version" , "EffDate" , "SuspDate" , "VerBrDate" , "PayDate" , "IsClosed" , "Exceed" , "PayMark" , "SuspendMark" , "VerBorrowMark" , "IsMerge" , "PayBank" , "BorrowList" };

    public final static String [] BT003_DET_DEFAULT = { "VerNbr" , "LineNbr" , "Summary" , "DateFm" , "DateTo" , "Days" , "AreaFm" , "AreaTo" , "AreaKind" , "Colleague" , "Traffic" , "Item1" , "Item2" , "Item3" , "Item4" , "Item5" , "Item6" , "Item7" , "Item8" , "Item9" , "Item10" , "Item11" , "Item12" , "Item13" , "Item14" , "Item15" , "ActCost" , "CurrID" , "Process" , "AccItem" , "CrtDate" , "UpdateUser" , "UpdateDT" , "Remark" , "Version" , "EffDate" , "BelongDep" , "BelongChannel" , "BelongCus" , "BelongVendor" , "MAccItem" , "MSummary" , "MActCost" , "Status" , "Descr" , "IsValid" , "HappenDate" };

    public final static String [] FA001_DATA_DEFAULT = { "AccItem" , "Account" , "AccType" , "BackTime" , "Bank" , "bDepBorrow" , "BorrowType" , "BRNbr" , "Channel" , "CrtDate" , "Currency" , "Depart" , "Duty" , "EffDate" , "ID" , "PayBank" , "PayCost" , "PayDate" , "PayMethod" , "Period" , "Project" , "Reason" , "Remark" , "RePayCost" , "Status" , "Title" , "TotalCost" , "UpdateDT" , "UpdateUser" , "VerBrDate" , "VerCost" , "Version" };

    public final static String [] FA001_DET_DEFAULT = { "BRNbr" , "LineNbr" , "DocType" , "DataNbr" , "BrCost" , "UpdateUser" , "UpdateDT" , "CrtDate" , "Status" , "Remark" , "EffDate" , "Version" , "MSummary" };

    public final static String [] FA002_DATA_DEFAULT = { "RePayNbr" , "CrtDate" , "Duty" , "Title" , "Depart" , "Channel" , "RtnCost" , "Currency" , "PayMethod" , "AccType" , "AccItem" , "Bank" , "AccName" , "Account" , "Remark" , "UpdateUser" , "UpdateDT" , "Status" , "EffDate" , "Version" , "Period" , "bRePayment" , "RePayDesc" , "VerBrDate" , "VerBrCost" , "RePayType" , "Reason" };

    public final static String [] FA002_DET_DEFAULT = { "RePayNbr" , "LineNbr" , "DocType" , "DataNbr" , "BrCost" , "CurRtnCost" , "UpdateUser" , "UpdateDT" , "CrtDate" , "Status" , "Remark" , "EffDate" , "Version" , "MSummary" };

    public final static String [] IS001_DATA_DEFAULT = { "LoanNbr" , "CrtDate" , "Duty" , "Title" , "Depart" , "TotalCost" , "Currency" , "AccType" , "UpdateUser" , "UpdateDT" , "Status" , "Remark" , "EffDate" , "Version" , "LoanType" , "Recipient" , "Address" , "Tel" , "ExprCompany" };

    public final static String [] IS001_DET_DEFAULT = { "LoanNbr" , "LineNbr" , "TotalCost" , "AccItem" , "ExpRtnDate" , "InvoiceType" , "DocType" , "DataNbr" , "PayNbr" , "RtnNbr" , "RePayNbr" , "ActRtnDate" , "ActRtnCost" , "CrtDate" , "UpdateUser" , "UpdateDT" , "Status" , "Remark" , "EffDate" , "Version" , "InvNbr" , "MSummary" };

    public final static String [] IS002_DATA_DEFAULT = { "RtnNbr" , "CrtDate" , "Duty" , "Title" , "Depart" , "Totalcost" , "Currency" , "AccType" , "Returned" , "Remark" , "UpdateUser" , "UpdateDT" , "Status" , "EffDate" , "Version" };

    public final static String [] IS002_DET_DEFAULT = { "RtnNbr" , "LineNbr" , "TotalCost" , "AccItem" , "InvoiceType" , "InvNbr" , "DocType" , "DataNbr" , "sInvoiceType" , "CrtDate" , "UpdateUser" , "UpdateDT" , "Status" , "Remark" , "EffDate" , "Version" , "MSummary" };

    public static String [] getDataDefaultConfig( String type )
    {

	String [] strs = null;
	if( type.equals( "RQ001" ) )
	{
	    strs = Constants.RQ001_DATA_DEFAULT;
	}
	else if( type.equals( "AC001" ) )
	{
	    strs = Constants.AC001_DATA_DEFAULT;
	}
	else if( type.equals( "BT001" ) )
	{
	    strs = Constants.BT001_DATA_DEFAULT;
	}
	else if( type.equals( "BT003" ) )
	{
	    strs = Constants.BT003_DATA_DEFAULT;
	}
	else if( type.equals( "FA001" ) )
	{
	    strs = Constants.FA001_DATA_DEFAULT;
	}
	else if( type.equals( "FA002" ) )
	{
	    strs = Constants.FA002_DATA_DEFAULT;
	}
	else if( type.equals( "IS001" ) )
	{
	    strs = Constants.IS001_DATA_DEFAULT;
	}
	else if( type.equals( "IS002" ) )
	{
	    strs = Constants.IS002_DATA_DEFAULT;
	}
	return strs;
    }

    public static String [] getDetDefaultConfig( String type )
    {

	String [] strs = null;
	if( type.equals( "RQ001" ) )
	{
	    strs = Constants.RQ001_DET_DEFAULT;
	}
	else if( type.equals( "AC001" ) )
	{
	    strs = Constants.AC001_DET_DEFAULT;
	}
	else if( type.equals( "BT001" ) )
	{
	    strs = Constants.BT001_DET_DEFAULT;
	}
	else if( type.equals( "BT003" ) )
	{
	    strs = Constants.BT003_DET_DEFAULT;
	}
	else if( type.equals( "FA001" ) )
	{
	    strs = Constants.FA001_DET_DEFAULT;
	}
	else if( type.equals( "FA002" ) )
	{
	    strs = Constants.FA002_DET_DEFAULT;
	}
	else if( type.equals( "IS001" ) )
	{
	    strs = Constants.IS001_DET_DEFAULT;
	}
	else if( type.equals( "IS002" ) )
	{
	    strs = Constants.IS002_DET_DEFAULT;
	}
	return strs;
    }
}

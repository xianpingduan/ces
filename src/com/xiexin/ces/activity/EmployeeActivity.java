package com.xiexin.ces.activity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SectionIndexer;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request.Method;
import com.android.volley.RequestQueue;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.xiexin.ces.App;
import com.xiexin.ces.Constants;
import com.xiexin.ces.R;
import com.xiexin.ces.db.EmployeeManager;
import com.xiexin.ces.entry.Employee;
import com.xiexin.ces.utils.Logger;
import com.xiexin.ces.widgets.LoadingDialog;
import com.xiexin.sortlistview.CharacterParser;
import com.xiexin.sortlistview.ClearEditText;
import com.xiexin.sortlistview.PinyinComparator;
import com.xiexin.sortlistview.SideBar;
import com.xiexin.sortlistview.SideBar.OnTouchingLetterChangedListener;

public class EmployeeActivity extends Activity implements OnClickListener {

	private final static String TAG = "EmployeeActivity";

	// header start
	private LinearLayout mReturnLl;
	private ImageView mReturnIv;
	private TextView mReturnTv;
	private TextView mTitle;
	private Button mBtn1;
	private Button mBtn2;

	// header end

	// private LoadingUIListView mListView;
	private EmployeeAdapter mEmployeeAdapter;

	private LoadingDialog mLoadingDialog;

	private String mConnName;// 账套信息
	private RequestQueue mQueue;
	private Employee mEmployeeChecked;

	private EditText mSearchEt;
	private Button mSearchBtn;

	private int mFrom;
	private HashMap<String, Employee> mSelectMap = new HashMap<String, Employee>();

	// 字母排序
	private ListView sortListView;
	private SideBar sideBar;
	private TextView dialog;
	// private SortAdapter adapter;
	private ClearEditText mClearEditText;
	/**
	 * 汉字转换成拼音的类
	 */
	private CharacterParser characterParser;
	// private List<SortModel> SourceDateList;
	/**
	 * 根据拼音来排列ListView里面的数据类
	 */
	private PinyinComparator pinyinComparator;

	private void initViews() {
		// 实例化汉字转拼音类
		characterParser = CharacterParser.getInstance();
		pinyinComparator = new PinyinComparator();

		sideBar = (SideBar) findViewById(R.id.sidebar);
		dialog = (TextView) findViewById(R.id.dialog);
		sideBar.setTextView(dialog);

		// 设置右侧触摸监听
		sideBar.setOnTouchingLetterChangedListener(new OnTouchingLetterChangedListener() {

			@Override
			public void onTouchingLetterChanged(String s) {
				// 该字母首次出现的位置
				int position = mEmployeeAdapter.getPositionForSection(s
						.charAt(0));
				if (position != -1) {
					sortListView.setSelection(position);
				}

			}
		});

		sortListView = (ListView) findViewById(R.id.employee_list);
		// sortListView.setOnItemClickListener(new OnItemClickListener() {
		//
		// @Override
		// public void onItemClick(AdapterView<?> parent, View view,
		// int position, long id) {
		// // 这里要利用adapter.getItem(position)来获取当前position所对应的对象
		// Toast.makeText(getApplication(),
		// ((SortModel) mEmployeeAdapter.getItem(position)).getName(),
		// Toast.LENGTH_SHORT).show();
		// }
		// });

		mClearEditText = (ClearEditText) findViewById(R.id.search_et);
		// 根据输入框输入值的改变来过滤搜索
		mClearEditText.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				// 当输入框里面的值为空，更新为原来的列表，否则为过滤数据列表
				Logger.d(TAG, "Text [" + s + "]");
				String filter = s.toString();
				if (filter != null) {
					filter = filter.trim();
					Message msg = Message.obtain();
					msg.what = MSG_SEARCH_REFRESH_LIST;
					msg.obj = filter;
					mUiHandler.sendMessage(msg);
				}
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {

			}

			@Override
			public void afterTextChanged(Editable s) {
			}
		});

	}

	private void dismissDialog() {

		if (mLoadingDialog != null && mLoadingDialog.isShowing()) {
			mLoadingDialog.dismiss();
		}

	}

	private void showDialog() {
		if (mLoadingDialog == null) {
			mLoadingDialog = new LoadingDialog(this);
		}

		mLoadingDialog.show();

	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_employee);

		mQueue = Volley.newRequestQueue(App.getAppContext());
		initView();

		initViews();

		initData();

	}

	private void initView() {

		// header start
		mReturnLl = (LinearLayout) findViewById(R.id.return_ll);
		mReturnIv = (ImageView) findViewById(R.id.return_iv);
		mReturnTv = (TextView) findViewById(R.id.return_tv);
		mTitle = (TextView) findViewById(R.id.title);
		mBtn1 = (Button) findViewById(R.id.btn1);
		mBtn2 = (Button) findViewById(R.id.btn2);
		// /header end

		// mInvoiceType = getIntent( ).getIntExtra( Constants.INVOICE_TYPE , 0
		// );
		mReturnTv.setText(getString(R.string.invoice_info));
		mTitle.setText(getString(R.string.please_select_employee));

		mBtn1.setVisibility(View.VISIBLE);
		mBtn1.setText(getString(R.string.finish_to));
		mBtn2.setVisibility(View.GONE);
		mReturnLl.setVisibility(View.VISIBLE);

		mBtn1.setOnClickListener(this);
		// mBtn2.setOnClickListener( this );
		mReturnLl.setOnClickListener(this);

		// mListView = (LoadingUIListView) findViewById(R.id.employee_list);
		// mListView.setFooterPullEnable(false);
		// mListView.setHeaderPullEnable(false);
		// mSearchEt = (EditText) findViewById(R.id.search_et);
		// mSearchEt.addTextChangedListener(new TextWatcher() {
		// @Override
		// public void onTextChanged(CharSequence s, int start, int before,
		// int count) {
		// Logger.d(TAG, "Text [" + s + "]");
		// String filter = s.toString();
		// if (filter != null) {
		// filter = filter.trim();
		// Message msg = Message.obtain();
		// msg.what = MSG_SEARCH_REFRESH_LIST;
		// msg.obj = filter;
		// mUiHandler.sendMessage(msg);
		// }
		// }
		// @Override
		// public void beforeTextChanged(CharSequence s, int start, int count,
		// int after) {
		//
		// }
		// @Override
		// public void afterTextChanged(Editable s) {
		// }
		// });

	}

	private void initData() {

		Intent intent = getIntent();
		mConnName = intent.getStringExtra(Constants.ZHANG_TAO_CONN_NAME);
		mFrom = intent.getIntExtra(Constants.CHECK_EMPLOYEE_FROM, 0);
		if (mConnName == null || mConnName.isEmpty()) {
			mConnName = App.getSharedPreference().getString(
					Constants.ZHANG_TAO_CONN_NAME, "");
		}

		Logger.d(TAG, "mFrom=" + mFrom);

		if (mEmployeeAdapter == null)
			mEmployeeAdapter = new EmployeeAdapter();
		sortListView.setAdapter(mEmployeeAdapter);

		requestEmployees("");
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.return_ll:
			onBackPressed();
			break;
		case R.id.btn1:
			if (mEmployeeChecked != null || mSelectMap.size() > 0) {
				setResult();
			} else {
				Toast.makeText(EmployeeActivity.this,
						getString(R.string.please_select_employee),
						Toast.LENGTH_SHORT).show();
			}
			break;
		default:
			break;
		}

	}

	private String mCheckUserId = null;
	private String mCheckUserName = null;

	private void generateCheckedUser() {

		switch (mFrom) {
		case Constants.CHECK_EMPLOYEE_FROM_NOTIFY:
			StringBuffer userIdSbf = new StringBuffer("");
			StringBuffer userNameSbf = new StringBuffer("");
			for (Map.Entry<String, Employee> ee : mSelectMap.entrySet()) {
				userIdSbf.append(ee.getKey()).append(",");
				userNameSbf.append(ee.getValue().getDescr()).append(",");
			}
			String ids = userIdSbf.toString();
			String names = userNameSbf.toString();
			Log.d(TAG, "ids=" + ids + ",names=" + names);
			mCheckUserId = ids.substring(0, ids.lastIndexOf(","));
			mCheckUserName = names.substring(0, names.lastIndexOf(","));

			break;
		case Constants.CHECK_EMPLOYEE_FROM_SETPLUGIN:
			mCheckUserId = mEmployeeChecked.getEmployeeid();
			mCheckUserName = mEmployeeChecked.getDescr();
			Log.d(TAG, "mCheckUserId=" + mCheckUserId + ",mCheckUserName="+ mCheckUserName);
			break;

		default:
			break;
		}

	}

	//设置返回结果
	private void setResult() {
		generateCheckedUser();
		Intent in = new Intent();
		// in.putExtra(Constants.ZHANG_TAO_CONN_NAME, mCheckConnName);
		// in.putExtra(Constants.ZHANG_TAO_ACCINFO, mCheckAccInfo);
		Logger.d(TAG, mCheckUserId + "," + mCheckUserName);
		in.putExtra("userid", mCheckUserId);
		in.putExtra("userName", mCheckUserName);
		setResult(RESULT_OK, in);
		finish();
	}

	private void doSearch() {

		String filter = mSearchEt.getText().toString();

		if (filter == null || filter.isEmpty()) {
			Toast.makeText(
					App.getAppContext(),
					App.getAppContext().getString(
							R.string.please_enter_employee_name),
					Toast.LENGTH_SHORT).show();
			return;
		} else {
			requestEmployees(filter);
		}

	}

	private List<Employee> getEmployeeList(String jsonStr) {

		List<Employee> invoiceList = new ArrayList<Employee>();
		try {
			JSONArray arrays = new JSONArray(jsonStr);
			for (int i = 0; i < arrays.length(); i++) {

				JSONObject obj = arrays.getJSONObject(i);
				Employee employee = new Employee();
				employee.setEmployeeid(obj.getString("employeeid"));
				employee.setDescr(obj.getString("descr"));
				employee.setDepart(obj.getString("depart"));
				employee.setJob(obj.getString("job"));
				invoiceList.add(employee);
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return invoiceList;

	}

	private List<Employee> mEmployees = null;

	private void requestEmployees(String filter) {

		showDialog();

		mEmployees = EmployeeManager.getInstance(App.getAppContext()).loadByAcount(mConnName);
		
		Log.d(TAG, "mEmployees.size= "+mEmployees.size());
		Log.d(TAG, "mConnName= "+mConnName);

		if (mEmployees != null && mEmployees.size() > 0) {
			mUiHandler.sendEmptyMessage(MSG_GET_LOCAL_EMPLOYEE_LIST_SUCCESS);
		} else {
			StringBuffer urlSbf = new StringBuffer(App.getRootUrl()
					+ Constants.GET_EMPLOYEE_LIST + "?");
			urlSbf.append("account=").append(mConnName);
			urlSbf.append("&filter=").append(filter);

			JsonObjectRequest json = new JsonObjectRequest(Method.GET,
					urlSbf.toString(), null, new Listener<JSONObject>() {
						@Override
						public void onResponse(JSONObject response) {
							Logger.d(TAG,
									"----response----" + response.toString());
							try {
								int resCode = response.getInt("success");
								Message msg = Message.obtain();
								if (resCode == 0) {
									msg.what = MSG_GET_EMPLOYEE_LIST_SUCCESS;
									msg.obj = response.getString("data");
								} else {
									msg.what = MSG_GET_EMPLOYEE_LIST_ERROR;
									msg.obj = response.get("msg");
								}
								mUiHandler.sendMessage(msg);
							} catch (JSONException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
					}, new ErrorListener() {
						@Override
						public void onErrorResponse(VolleyError error) {
							Logger.d(TAG, "----e----" + error.toString());
							mUiHandler
									.sendEmptyMessage(MSG_GET_EMPLOYEE_LIST_ERROR);
						}
					});
			json.setShouldCache(true);
			mQueue.add(json);
			mQueue.start();
		}

	}

	private static final int MSG_GET_EMPLOYEE_LIST_SUCCESS = 1;
	private static final int MSG_GET_EMPLOYEE_LIST_ERROR = 2;
	private static final int MSG_REFRESH_EMPLOYEE_LIST = 3;
	private static final int MSG_SEARCH_REFRESH_LIST = 4;
	private static final int MSG_GET_LOCAL_EMPLOYEE_LIST_SUCCESS = 5;

	private Handler mUiHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);

			switch (msg.what) {
			case MSG_GET_EMPLOYEE_LIST_SUCCESS:
				dismissDialog();

				String dataStr = (String) msg.obj;
				mEmployees = getEmployeeList(dataStr);
				if (mEmployees != null && mEmployees.size() > 0) {
					mEmployeeAdapter.addData(mEmployees);
				}
				mEmployeeAdapter.notifyDataSetChanged();
				break;

			case MSG_GET_EMPLOYEE_LIST_ERROR:
				dismissDialog();
				Toast.makeText(
						App.getAppContext(),
						App.getAppContext().getString(
								R.string.request_appr_road_list_error),
						Toast.LENGTH_SHORT).show();
				break;

			case MSG_REFRESH_EMPLOYEE_LIST:
				mEmployeeAdapter.notifyDataSetChanged();
				break;

			case MSG_SEARCH_REFRESH_LIST:
				Logger.d(TAG, "MSG_SEARCH_REFRESH_LIST,filter="
						+ (String) msg.obj);
				String filter = (String) msg.obj;
				List<Employee> tempList = new ArrayList<Employee>();
				tempList.clear();
				if (filter.isEmpty() && mEmployees != null) {
					tempList.addAll(mEmployees);
				} else {
					for (Employee e : mEmployees) {
						if (e.getDescr().contains(filter)) {
							tempList.add(e);
						}
					}
				}
				// 根据a-z进行排序
				Collections.sort(tempList, pinyinComparator);
				mEmployeeAdapter.addData(tempList);
				mEmployeeAdapter.notifyDataSetChanged();
				// mEmployeeAdapter.getFilter().filter((String) msg.obj);
				break;
			case MSG_GET_LOCAL_EMPLOYEE_LIST_SUCCESS:
				dismissDialog();

				// 根据a-z进行排序源数据
				Collections.sort(mEmployees, pinyinComparator);
				mEmployeeAdapter.addData(mEmployees);
				mEmployeeAdapter.notifyDataSetChanged();
				break;
			default:
				break;
			}
		}

	};

	private class EmployeeAdapter extends BaseAdapter implements SectionIndexer {

		private List<Employee> list = new ArrayList<Employee>();

		private HashMap<String, Boolean> mMap = new HashMap<String, Boolean>();

		public void addData(List<Employee> data) {
			list.clear();
			list.addAll(data);

			// 初始化
			for (Employee e : list) {
				mMap.put(e.getEmployeeid(), false);
			}
		}

		public List<Employee> getData() {
			return list;
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return list.size();
		}

		@Override
		public Object getItem(int arg0) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {

			ViewHolder holder;
			if (convertView == null) {
				convertView = App.getLayoutInflater().inflate(
						R.layout.activity_employee_list_item, null);
				holder = new ViewHolder();
				holder.employeeFrameRl = (RelativeLayout) findViewById(R.id.employee_frame);
				holder.employeeNameTv = (TextView) convertView
						.findViewById(R.id.employee_name_tv);
				holder.employeeDepartTv = (TextView) convertView
						.findViewById(R.id.employee_depart_tv);
				holder.employeeJobTv = (TextView) convertView
						.findViewById(R.id.employee_job_tv);
				holder.employeeCheckCb = (CheckBox) convertView
						.findViewById(R.id.employee_check_cb);
				holder.letterTv = (TextView) convertView.findViewById(R.id.catalog);

				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			
			Employee employee = list.get(position);
			
			int section = getSectionForPosition(position);
			if(position == getPositionForSection(section)){
				holder.letterTv.setVisibility(View.VISIBLE);
				holder.letterTv.setText(employee.getSortLetters());
			}else{
				holder.letterTv.setVisibility(View.GONE);
			}

			bindData(holder, employee);
			return convertView;
		}

		private void bindData(final ViewHolder holder, final Employee employee) {

			holder.employeeNameTv.setText(employee.getDescr());
			holder.employeeDepartTv.setText(employee.getDepart());
			holder.employeeJobTv.setTag(employee.getJob());
			holder.employeeCheckCb.setTag(employee);

			switch (mFrom) {
			case Constants.CHECK_EMPLOYEE_FROM_NOTIFY:
				Employee empId = mSelectMap.get(employee.getEmployeeid());
				if (empId != null
						&& empId.getEmployeeid().equals(
								employee.getEmployeeid())) {
					holder.employeeCheckCb.setChecked(true);
					mMap.put(employee.getEmployeeid(), true);
				} else {
					holder.employeeCheckCb.setChecked(false);
					mMap.put(employee.getEmployeeid(), false);
				}
				break;
			case Constants.CHECK_EMPLOYEE_FROM_SETPLUGIN:
				if (mEmployeeChecked != null
						&& mEmployeeChecked.getEmployeeid().equals(
								employee.getEmployeeid())) {
					holder.employeeCheckCb.setChecked(true);
					mMap.put(employee.getEmployeeid(), true);
				} else {
					holder.employeeCheckCb.setChecked(false);
					mMap.put(employee.getEmployeeid(), false);
				}
				break;
			default:
				break;
			}

			holder.employeeCheckCb
					.setOnClickListener(new View.OnClickListener() {
						@Override
						public void onClick(View v) {
							Employee ele = (Employee) holder.employeeCheckCb
									.getTag();
							if (!holder.employeeCheckCb.isChecked()) {
								holder.employeeCheckCb.setChecked(false);
								mMap.put(ele.getEmployeeid(), false);
								switch (mFrom) {
								case Constants.CHECK_EMPLOYEE_FROM_NOTIFY:
									mSelectMap.remove(ele.getEmployeeid());
									break;
								case Constants.CHECK_EMPLOYEE_FROM_SETPLUGIN:
									mEmployeeChecked = null;
									break;
								default:
									break;
								}

							} else {
								holder.employeeCheckCb.setChecked(true);
								mMap.put(ele.getEmployeeid(), true);

								switch (mFrom) {
								case Constants.CHECK_EMPLOYEE_FROM_NOTIFY:
									mSelectMap.put(ele.getEmployeeid(), ele);
									break;
								case Constants.CHECK_EMPLOYEE_FROM_SETPLUGIN:
									mEmployeeChecked = ele;
									break;

								default:
									break;
								}
							}
							if (mFrom == Constants.CHECK_EMPLOYEE_FROM_SETPLUGIN) {
								mUiHandler
										.sendEmptyMessage(MSG_REFRESH_EMPLOYEE_LIST);
							}
						}
					});
			// 同步checkBox事件
			// Log.d(TAG, "holder.employeeFrameRl=" + holder.employeeFrameRl);
			// holder.employeeFrameRl.setOnClickListener(new
			// View.OnClickListener() {
			// @Override
			// public void onClick(View v) {
			// ((ViewHolder) v.getTag()).employeeCheckCb.performClick();
			// }
			// });
		}

		/**
		 * 根据ListView的当前位置获取分类的首字母的Char ascii值
		 */
		public int getSectionForPosition(int position) {
			return list.get(position).getSortLetters().charAt(0);
		}

		/**
		 * 根据分类的首字母的Char ascii值获取其第一次出现该首字母的位置
		 */
		public int getPositionForSection(int section) {
			for (int i = 0; i < getCount(); i++) {
				String sortStr = list.get(i).getSortLetters();
				char firstChar = sortStr.toUpperCase().charAt(0);
				if (firstChar == section) {
					return i;
				}
			}

			return -1;
		}

		@Override
		public Object[] getSections() {
			// TODO Auto-generated method stub
			return null;
		}

		// public Filter tmpFilter = new Filter() {
		//
		// @Override
		// protected FilterResults performFiltering(CharSequence constraint) {
		// FilterResults results = new FilterResults();
		// Log.d(TAG, "constraint=" + constraint);
		// // We implement here the filter logic
		// if (constraint == null || constraint.length() == 0
		// || constraint.toString().isEmpty()) {
		// // No filter implemented we return all the list
		// Log.d(TAG, "constraint is empty");
		// results.values = list;
		// results.count = list.size();
		// } else {
		// // We perform filtering operation
		// ArrayList<Employee> nEmployeeList = new ArrayList<Employee>();
		// nEmployeeList.clear();
		// for (Employee p : list) {
		// if (p.getDescr().contains(
		// constraint.toString().toUpperCase())
		// && !constraint.toString().toUpperCase()
		// .isEmpty()) {
		// nEmployeeList.add(p);
		// } else if (constraint.toString().toUpperCase()
		// .isEmpty()) {
		// nEmployeeList.add(p);
		// }
		// }
		//
		// results.values = nEmployeeList;
		// results.count = nEmployeeList.size();
		//
		// }
		// return results;
		// }
		//
		// @Override
		// protected void publishResults(CharSequence constraint,
		// FilterResults results) {
		// // Now we have to inform the adapter about the new list
		// // filtered
		// Logger.d(TAG, "publishResults,results.count=" + results.count);
		// list=(ArrayList<Employee>) results.values;
		// notifyDataSetChanged();
		// }
		//
		// };
		//
		// @Override
		// public Filter getFilter() {
		// return tmpFilter;
		// }
	}

	class ViewHolder {
		RelativeLayout employeeFrameRl;
		TextView employeeNameTv;
		TextView employeeDepartTv;
		TextView employeeJobTv;
		CheckBox employeeCheckCb;
		TextView letterTv;
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		finish();
	}

}

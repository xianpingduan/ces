package com.xiexin.ces.db;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import pada.juidownloadmanager.utils.ThreadTask;
import android.content.Context;
import android.database.Cursor;
import android.os.Handler;

import com.xiexin.ces.activity.MenuActivity;
import com.xiexin.ces.entry.Employee;
import com.xiexin.ces.utils.Logger;
import com.xiexin.sortlistview.CharacterParser;

public class EmployeeManager {

	private final Object removelock = new Object();

	private static EmployeeManager mEmployeeManager;

	private final EmployeeDbAdapter mEmployeeDbAdapter;

	/**
	 * 汉字转换成拼音的类
	 */
	private final CharacterParser characterParser;

	private Handler mHandler;

	public static EmployeeManager getInstance(Context context) {
		if (mEmployeeManager == null) {
			mEmployeeManager = new EmployeeManager(context);
		}
		return mEmployeeManager;
	}

	private EmployeeManager(Context context) {
		mEmployeeDbAdapter = new EmployeeDbAdapter(context);
		characterParser = CharacterParser.getInstance();
	}

	private Employee createTaskFromCursor(Cursor cursor) {
		Employee employee = new Employee();
		employee.setEmployeeid(cursor.getString(1));
		employee.setDescr(cursor.getString(2));
		employee.setSex(cursor.getString(3));
		employee.setDepart(cursor.getString(4));
		employee.setJob(cursor.getString(5));
		employee.setMobile(cursor.getString(6));
		employee.setTelnbr(cursor.getString(7));
		employee.setEmail(cursor.getString(8));
		employee.setAccount(cursor.getString(9));
		// 汉字转换成拼音
		String pinyin = characterParser.getSelling(cursor.getString(2));
		String sortString = pinyin.substring(0, 1).toUpperCase();
		// 正则表达式，判断首字母是否是英文字母
		if (sortString.matches("[A-Z]")) {
			employee.setSortLetters(sortString.toUpperCase());
		} else {
			employee.setSortLetters("#");
		}
		
		return employee;
	}

	public synchronized void saveEmpToDB(final Employee employee) {
		ThreadTask.postTask(new Runnable() {
			@Override
			public void run() {
				synchronized (removelock) {
					mEmployeeDbAdapter.insert(employee);
				}
			}
		});

	}

	public synchronized void updateEmpToDB(final Employee employee) {
		ThreadTask.postTask(new Runnable() {
			@Override
			public void run() {
				synchronized (removelock) {
					mEmployeeDbAdapter.update(employee);
				}
			}
		});

	}

	public synchronized Employee findEmployeeById(String eId) {
		Cursor cursor = mEmployeeDbAdapter.queryById(eId);

		if (cursor != null && cursor.getCount() > 0 && cursor.moveToFirst()) {
			return createTaskFromCursor(cursor);
		}
		if (cursor != null) {
			cursor.close();
		}
		return null;
	}

	public synchronized ArrayList<Employee> loadAll() {
		ArrayList<Employee> list = new ArrayList<Employee>();
		Cursor cursor = mEmployeeDbAdapter.queryAll();

		Logger.d("Cursor", cursor.getCount() + "");

		if (cursor != null && cursor.getCount() > 0 && cursor.moveToFirst()) {

			while (cursor.moveToNext()) {
				Employee employee = createTaskFromCursor(cursor);
				list.add(employee);
			}

		}
		if (cursor != null) {
			cursor.close();
		}
		return list;

	}
	
	//根据账套
	public synchronized ArrayList<Employee> loadByAcount(String account) {
		ArrayList<Employee> list = new ArrayList<Employee>();
		Cursor cursor = mEmployeeDbAdapter.queryByAccount(account);
		Logger.d("Cursor", cursor.getCount() + "");
		if (cursor != null && cursor.getCount() > 0 && cursor.moveToFirst()) {
			while (cursor.moveToNext()) {
				Employee employee = createTaskFromCursor(cursor);
				list.add(employee);
			}
		}
		if (cursor != null) {
			cursor.close();
		}
		return list;
		
	}
	
	

	public void saveResult(String jsonStr) {
		ArrayList<Employee> employeeList = new ArrayList<Employee>();
		try {
			JSONArray arrays = new JSONArray(jsonStr);
			for (int i = 0; i < arrays.length(); i++) {
				JSONObject obj = arrays.getJSONObject(i);
				Employee employee = new Employee();
				employee.setEmployeeid(obj.getString("employeeid"));
				employee.setDescr(obj.getString("descr"));
				employee.setDepart(obj.getString("depart"));
				employee.setJob(obj.getString("job"));
				employee.setTitle(obj.getString("title"));
				employee.setChannel(obj.getString("channel"));
				employee.setMobile(obj.getString("mobile"));
				employee.setAccount(obj.getString("account"));
				employeeList.add(employee);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}

		mEmployeeDbAdapter.delAll();

		new Thread(new SaveDataToDBRunnable(employeeList)).start();
	}

	// clear
	public void delAll() {
		mEmployeeDbAdapter.delAll();
	}

	private class SaveDataToDBRunnable implements Runnable {

		private final List<Employee> data;

		private SaveDataToDBRunnable(List<Employee> data) {
			this.data = data;
		}

		@Override
		public void run() {

			synchronized (removelock) {
                insertRecord(data);
				mHandler.sendEmptyMessage(MenuActivity.MSG_SAVE_EMPLOYEE_LIST_SUCCESS);
			}
		}
		private void insertRecord(List<Employee> employees) {
			mEmployeeDbAdapter.insert(employees);
		}
	}

	public void setHandler(Handler handler) {
		mHandler = handler;
	}

}

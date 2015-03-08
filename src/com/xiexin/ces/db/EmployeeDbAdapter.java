package com.xiexin.ces.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

public class EmployeeDbAdapter {

	private SQLiteDatabase db;

	private final Context context;

	private static final String DATABASE_NAME = "xiexin_db";
	private static int DATABASE_VERSION = 1;
	private static int DATABASE_VERSION_2=2;
	private EmployeeDbHelper dbHelper;
	private Cursor mCursor;

	public interface Tables {
		// 下载记录表
		public static final String EmployeeInfoes = "xiexin_employee_infoes";
	}

	public interface EmployeeInfoColumns {
		public static final String EMPLOYEE_ID = "employee_id";
		public static final String DESCR = "descr";
		public static final String SEX = "sex";
		public static final String DEPART = "depart";
		public static final String JOB = "job";
		public static final String MOBILE = "mobile";
		public static final String TELNBR = "telnbr";
		public static final String EMIAL = "email";
		public static final String ACCOUNT = "account";

	}

	private static final String CREATE_TABLE_EMPLOYEEINOES = "CREATE TABLE IF NOT EXISTS "
			+ Tables.EmployeeInfoes
			+ "("
			+ BaseColumns._ID
			+ " INTEGER PRIMARY KEY AUTOINCREMENT,"
			+ EmployeeInfoColumns.EMPLOYEE_ID
			+ " TEXT,"
			+ EmployeeInfoColumns.DESCR
			+ " TEXT,"
			+ EmployeeInfoColumns.SEX
			+ " TEXT,"
			+ EmployeeInfoColumns.DEPART
			+ " TEXT,"
			+ EmployeeInfoColumns.JOB
			+ " TEXT,"
			+ EmployeeInfoColumns.MOBILE
			+ " TEXT,"
			+ EmployeeInfoColumns.TELNBR
			+ " TEXT,"
			+ EmployeeInfoColumns.EMIAL
			+ " TEXT,"
			+ EmployeeInfoColumns.ACCOUNT + " TEXT" + ");";
	
	private static final String DROP_TABLE_EMPLOYEEINOES="drop table if exists xiexin_employee_infoes";

	public EmployeeDbAdapter(Context _context) {
		context = _context;
		dbHelper = new EmployeeDbHelper(context, DATABASE_NAME, null,
				DATABASE_VERSION);
	}

	public EmployeeDbAdapter open() throws SQLException {
		if (db == null || (db != null && !db.isOpen()))
			db = dbHelper.getWritableDatabase();
		return this;
	}

	public void close() {
		if (db != null)
			db.close();
	}

	// Database open/upgrade helper
	private static class EmployeeDbHelper extends SQLiteOpenHelper {

		public EmployeeDbHelper(Context context, String name,
				CursorFactory factory, int version) {
			super(context, name, factory, version);
		}

		// Called when no database exists in disk
		// and the helper class needs to create a new one.
		@Override
		public void onCreate(SQLiteDatabase _db) {
			_db.execSQL(CREATE_TABLE_EMPLOYEEINOES);
		}

		@Override
		public void onUpgrade(SQLiteDatabase _db, int oldVersion, int newVersion) {
			// TODO Auto-generated method stub
			
			if(newVersion == DATABASE_VERSION_2){
				//先drop
				_db.execSQL(DROP_TABLE_EMPLOYEEINOES); 
				//后创建
				_db.execSQL(CREATE_TABLE_EMPLOYEEINOES);
			}
			
		}
	}

	public void insert(ContentValues values) {
		if (!isOpen()) {
			open();
		}

		db.insert(Tables.EmployeeInfoes, // table name
				null, values // column name-value pairs
		);

		db.close();
	}

	public Cursor queryAll() throws SQLException {
		if (!isOpen()) {
			open();
		}
		mCursor = db.query(true, // isdistinct
				Tables.EmployeeInfoes, // table name
				null,// select clause
				null, // where cluase
				null, // where clause parameters
				null, // group by
				null, // having
				null, // orderby
				null);// limit
		return mCursor;
	}

	public Cursor queryByAccount(String account) throws SQLException {
		if (!isOpen()) {
			open();
		}
		mCursor = db.query(true, // isdistinct
				Tables.EmployeeInfoes, // table name
				null,// select clause
				EmployeeInfoColumns.ACCOUNT + "= ?", // where cluase
				new String[] { account }, // where clause parameters
				null, // group by
				null, // having
				null, // orderby
				null);// limit
		return mCursor;
	}

	public Cursor queryById(String eId) {
		if (!isOpen()) {
			open();
		}
		mCursor = db.query(true, // isdistinct
				Tables.EmployeeInfoes, // table name
				null,// select clause
				EmployeeInfoColumns.EMPLOYEE_ID + "= ?", // where cluase
				new String[] { eId }, // where clause parameters
				null, // group by
				null, // having
				null, // orderby
				null);// limit
		return mCursor;
	}

	public int update(String eId, ContentValues values) {
		int result = -1;
		if (!isOpen()) {
			open();
		}
		result = db.update(Tables.EmployeeInfoes, // table
				values, // values to be updated
				EmployeeInfoColumns.EMPLOYEE_ID + "=?",// where clause
				new String[] { eId });
		close();
		return result;
	}

	public int delete(String eId) {
		if (!isOpen()) {
			open();
		}
		int result = db.delete(Tables.EmployeeInfoes,// table name
				EmployeeInfoColumns.EMPLOYEE_ID + "=?",// where clause
				new String[] { eId });
		close();
		return result;
	}

	public void batchDel(String[] strs) {

	}

	public void delAll() {
		if (!isOpen()) {
			open();
		}
		db.execSQL("delete from " + Tables.EmployeeInfoes + ";");

		close();
	}

	// 删除表
	public void dropTable(String tableName) {
		if (!isOpen()) {
			open();
		}
		db.execSQL("drop table" + tableName // table name
		);
		close();
	}

	private boolean isOpen() {
		if (db == null)
			return false;
		return db.isOpen();
	}

	// 关闭游标，回收资源
	public void closeCursor() {
		if (mCursor != null)
			mCursor.close();
	}

}

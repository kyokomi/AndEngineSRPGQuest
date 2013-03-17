package com.kyokomi.core.db;

import java.util.List;

import com.kyokomi.core.db.utils.SqlFileUtil;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class GameBaseDBOpenHelper extends SQLiteOpenHelper {

	/** データベース名. */
	private static final String DB_NAME = "DB_GAMEBASE";
	/** データベースバージョン. */
	private static final int DB_VERSION = 1;

	/** コンテキスト. */
	private final Context mContext;
	
	/**
	 * コンストラクタ.
	 * DB生成前の初期処理
	 * @param context
	 */
	public GameBaseDBOpenHelper(Context context) {
		super(context, DB_NAME, null, DB_VERSION);
		mContext = context;
		Log.d("DBLog", DB_NAME + "database open");
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// DDLを呼び出してDBを構築
		List<String > sqlList = SqlFileUtil.readSqlFile(mContext, "sql/create");
		for (String sql : sqlList) {
			if (sql != null) {
				db.execSQL(sql);
			}
		}
		Log.d("DBLog", DB_NAME + "create DDLcount = " + sqlList.size());
		
		initInsert(db);
	}
	
	private void initInsert(SQLiteDatabase db) {
		List<String > sqlList = SqlFileUtil.readSqlFile(mContext, "sql/insert");
		for (String sql : sqlList) {
			db.execSQL(sql);
		}
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		
	}
}

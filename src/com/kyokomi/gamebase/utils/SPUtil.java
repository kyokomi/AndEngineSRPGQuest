package com.kyokomi.gamebase.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * SharedPreferenceのユーティリティクラス.
 * @author kyokomi
 *
 */
public class SPUtil {

	/** 自身のインスタンス(シングルトン). */
	private static SPUtil instance;
	public static synchronized SPUtil getInstance(Context context) {
		if (instance == null) {
			instance = new SPUtil(context);
		}
		return instance;
	}
	
	private static SharedPreferences settings;
	private static SharedPreferences.Editor editor;
	
	private SPUtil(Context context) {
		settings = context.getSharedPreferences("shared_preference_1.0", 0);
		editor = settings.edit();
	}
}

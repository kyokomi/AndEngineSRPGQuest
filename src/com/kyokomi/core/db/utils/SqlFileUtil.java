package com.kyokomi.core.db.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.res.AssetManager;

/**
 * SQLiteの.sqlファイルを扱うユーティリティクラス.
 * @author kyokomi
 *
 */
public class SqlFileUtil {
  
	/** デフォルトエンコードタイプ. */
	private static final String DEFAULT_ENCODE_TYPE = "UTF-8";
	
	/** コンストラクタ封印. */
	private SqlFileUtil() {	
	}
	
	/**
	 * 指定ディレクトリパス内のファイルをすべて読み込んでSQL単位でListに格納した結果を返却.
	 * 読み込みの際にデフォルトエンコードとしてUTF-8を指定する。
	 * @param dirPath 読み込みディレクトリパス
	 * @return SQLリスト
	 */
	public static List<String> readSqlFile(Context context, String dirPath) {
		return readSqlFile(context, DEFAULT_ENCODE_TYPE, dirPath);
	}
	
	/**
	 * 指定ディレクトリパス内のファイルをすべて読み込んでSQL単位でListに格納した結果を返却.
	 * @param dirPath 読み込みディレクトリパス
	 * @return SQLリスト
	 */
	private static List<String> readSqlFile(Context context, String encodeType, String dirPath) {
		List<String> sqlList = new ArrayList<String>();// 返却用リスト
		
		AssetManager assetManager = context.getResources().getAssets();
		try {
			String files[] = assetManager.list(dirPath);
			for (int i = 0; i < files.length; i++) {
                String str = readFile(assetManager.open(dirPath + "/" + files[i]), encodeType);
                for (String sql: str.split(";")){ // セミコロンでSQLを区切る
                	sqlList.add(sql);
                }
            }
		} catch (IOException e) {
			e.printStackTrace();
		}
		return sqlList;
	}

	/** 
     * ファイルから文字列を読み込み.
     * <pre>普通にJavaってます。</pre>
     * @param is ファイルインプットストリーム
     * @return ファイルの文字列
     * @throws IOException
     */
    private static String readFile(InputStream is, String encodeType) throws IOException {
        BufferedReader br = null;
        try {
            br = new BufferedReader(new InputStreamReader(is, encodeType));
            StringBuilder sb = new StringBuilder();
            String line;
            while((line = br.readLine()) != null){
            	if (isTargetLine(line)) {
//            		sb.append(line +"\n");
            		sb.append(line);
            	}
            }
            return sb.toString();
        } finally {
            if (br != null) br.close();
        }
    }
    
    /**
     * 読み込み対象かチェック.
     * <pre>対象外にする正規パターンとか用意してもいいかも？</pre>
     * @param line １行分の文字列
     * @return true:読み込み対象 / false:読み込み対象外
     */
    private static boolean isTargetLine(String line) {
    	boolean result = true; // 返却用
        // 空行は無視
        if (line.length() == 0) {
        	result = false;
        }
    	// コメント行は無視(途中のコメントも行毎無視するので注意)
        if (line.startsWith("/*")) {
        	result = false;
        }
        return result;
    }
}

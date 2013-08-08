package com.kyokomi.core.utils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import android.content.Context;
import android.util.Log;

public class ZipUtil {

	public interface ZipProgressListener {
		public void progress(int progress);
	}
	public static boolean unZipInternalStorage(Context context, String assetFilePath) {
		boolean isUnZip = false;
		try {
			isUnZip = unZipInternalStorage(context, context.getAssets().open(assetFilePath), null);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return isUnZip;
	}
	/**
	 * ローカルストレージにzipを解凍する。
	 * @param context
	 * @param zipInputStream
	 */
	public static boolean unZipInternalStorage(Context context, InputStream zipInputStream, ZipProgressListener listener) {
		boolean isUnZip = false;
		try {
			ZipInputStream in = new ZipInputStream(new BufferedInputStream(zipInputStream));
			ZipEntry zipEntry = null;
			BufferedOutputStream out = null;
			int len = 0;
			while ((zipEntry = in.getNextEntry()) != null) {
				// 出力先を作成
				String outPutPath  = getAbsolutePathOnInternalStorage(context, "/" + zipEntry.getName());

				// ディレクトリの場合
				if (zipEntry.isDirectory()) {
					// ディレクトリが未作成なら作る
					File zipDir = new File(outPutPath);
					if (!zipDir.isDirectory()) {
						zipDir.mkdirs();
					}
				} else {
		            // 出力用ファイルストリームの生成
		            out = new BufferedOutputStream(new FileOutputStream(outPutPath));
		 
		            int size = 0;
		            // エントリの内容を出力
		            byte[] buffer = new byte[1024];
		            while ((len = in.read(buffer)) != -1) {
		            	size += len;
		                out.write(buffer, 0, len);
		            }
					
		            if (listener != null) {
		            	listener.progress(size);
		            }
		            Log.d("ZIP", "Name = " + zipEntry.getName() + " size = " + size);
		            
		            in.closeEntry();
		            out.close();
				}
			}
			in.close();
			
			isUnZip = true;
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return isUnZip;
	}
	
	public static String getAbsolutePathOnInternalStorage(final Context pContext, final String pFilePath) {
		return pContext.getFilesDir().getAbsolutePath() + pFilePath;
	}
}

package com.kyokomi.andengine.opengl.texture.atlas.bitmap.source;

import java.io.IOException;
import java.io.InputStream;

import org.andengine.opengl.texture.atlas.bitmap.source.IBitmapTextureAtlasSource;
import org.andengine.opengl.texture.atlas.source.BaseTextureAtlasSource;
import org.andengine.util.StreamUtils;
import org.andengine.util.debug.Debug;

import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Bitmap.Config;

/**
 * Assetから取得Bitmapをx,y,width,heightを指定してリサイズするテクスチャ。
 * @author kyokomi
 *
 */
public class AssetBitmapFitSizeTextureAtlasSource extends BaseTextureAtlasSource implements IBitmapTextureAtlasSource {

	private final AssetManager mAssetManager;
	private final String mAssetPath;

	private int mFitTextureX;
	private int mFitTextureY;
	private int mFitTextureWidth;
	private int mFitTextureHeight;
	
	public static AssetBitmapFitSizeTextureAtlasSource create(final AssetManager pAssetManager, final String pAssetPath, final int pTextureX, final int pTextureY, final int pOutWidth, final int pOutHeight) {

		return new AssetBitmapFitSizeTextureAtlasSource(pAssetManager, pAssetPath, 
				pTextureX, pTextureY, pOutWidth, pOutHeight);
	}
	
	AssetBitmapFitSizeTextureAtlasSource(AssetManager pAssetManager, String pAssetPath, 
			int pTextureX, int pTextureY, final int pTextureWidth, final int pTextureHeight) {
		super(pTextureX, pTextureY, pTextureWidth, pTextureHeight);
		this.mAssetManager = pAssetManager;
		this.mAssetPath = pAssetPath;
		
		this.mFitTextureX = pTextureX;
		this.mFitTextureY = pTextureY;
		this.mFitTextureWidth = pTextureWidth;
		this.mFitTextureHeight = pTextureHeight;
	}

	@Override
	public IBitmapTextureAtlasSource deepCopy() {
		return new AssetBitmapFitSizeTextureAtlasSource(this.mAssetManager, this.mAssetPath, this.mTextureX, this.mTextureY, this.mTextureWidth, this.mTextureHeight);
	}

	@Override
	public Bitmap onLoadBitmap(final Config pBitmapConfig) {
		InputStream in = null;
		try {
			final BitmapFactory.Options decodeOptions = new BitmapFactory.Options();
			decodeOptions.inPreferredConfig = pBitmapConfig;
			in = this.mAssetManager.open(this.mAssetPath);
			return Bitmap.createBitmap(BitmapFactory.decodeStream(in, null, decodeOptions), 
					mFitTextureX, mFitTextureY, mFitTextureWidth, mFitTextureHeight);
			
		} catch (final IOException e) {
			Debug.e("Failed loading Bitmap in " + this.getClass().getSimpleName() + ". AssetPath: " + this.mAssetPath, e);
			return null;
		} finally {
			StreamUtils.close(in);
		}
	}

	@Override
	public String toString() {
		return this.getClass().getSimpleName() + "(" + this.mAssetPath + ")";
	}
}

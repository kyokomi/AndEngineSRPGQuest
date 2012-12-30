package com.kyokomi.scrollquest.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import javax.microedition.khronos.opengles.GL10;

import org.andengine.entity.sprite.AnimatedSprite;
import org.andengine.entity.sprite.ButtonSprite;
import org.andengine.entity.sprite.Sprite;
import org.andengine.opengl.texture.TextureOptions;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.andengine.opengl.texture.atlas.bitmap.BuildableBitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.source.IBitmapTextureAtlasSource;
import org.andengine.opengl.texture.atlas.buildable.builder.BlackPawnTextureAtlasBuilder;
import org.andengine.opengl.texture.atlas.buildable.builder.ITextureAtlasBuilder.TextureAtlasBuilderException;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.opengl.texture.region.TiledTextureRegion;
import org.andengine.ui.activity.BaseGameActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

/**
 * Spriteのインスタンス化を簡単に行う為のクラス.
 * シングルトンです。
 * TextureRegionの再生性を防ぎ再利用する為、プールを保持している。
 * 
 * @author kyokomi
 *
 */
public class ResourceUtil {

	/** 自身のインスタンス. */
	private static ResourceUtil mSelf;
	/** Context. */
	private static BaseGameActivity mGameActivity;
	/** TextureRegionの無駄な生成を防ぎ、再利用する為の一時的な格納場所. */
	private static Map<String, ITextureRegion> mTextureRegionPool;
	/** TiledTextureRegionの無駄な生成を防ぎ、再利用する為の一時的な格納場所. */
	private static Map<String, TiledTextureRegion> mTiledTextureRegionPool;
	
	private ResourceUtil() {
		
	}
	
	/**
	 * イニシャライズ.
	 * @param gameActivity
	 * @return
	 */
	public static ResourceUtil getInstance(BaseGameActivity gameActivity) {
		if (mSelf == null) {
			mSelf = new ResourceUtil();
			ResourceUtil.mGameActivity = gameActivity;
			BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/");
			
			mTextureRegionPool = new HashMap<String, ITextureRegion>();
			mTiledTextureRegionPool = new HashMap<String, TiledTextureRegion>();
		}
		
		return mSelf;
	}
	
	/**
	 * 指定ファイルのSpriteを取得.
	 * 再生性しないようにプールしている。
	 * @param fileName ファイル名
	 * @return Sprite
	 */
	public Sprite getSprite(String fileName) {
		// 同名のファイルからITextureRegionが生成済みであれば再利用
		if (mTextureRegionPool.containsKey(fileName)) {
			Sprite s = new Sprite(0, 0, mTextureRegionPool.get(fileName), 
					mGameActivity.getVertexBufferObjectManager());
			s.setBlendFunction(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
			return s;
		}
		// サイズを自動的に取得する為にBitmapとして読み込み
		InputStream is = null;
		try {
			is = mGameActivity.getResources().getAssets().open("gfx/" + fileName);
		} catch (IOException e) {
			e.printStackTrace();
		}
		Bitmap bm = BitmapFactory.decodeStream(is);
		// Bitmapのサイズを基に2のべき乗の値を取得、BitmapTextureAtlasの生成
		BitmapTextureAtlas bta = new BitmapTextureAtlas(mGameActivity.getTextureManager(), 
				getTwoPowerSize(bm.getWidth()), getTwoPowerSize(bm.getHeight()),
				TextureOptions.BILINEAR_PREMULTIPLYALPHA);
		mGameActivity.getEngine().getTextureManager().loadTexture(bta);
		
		ITextureRegion btr = BitmapTextureAtlasTextureRegionFactory.createFromAsset(
				bta, mGameActivity, fileName, 0, 0);
		Sprite s = new Sprite(0, 0, btr, mGameActivity.getVertexBufferObjectManager());
		s.setBlendFunction(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
		
		// 再生性を防ぐ為、プールの登録
		mTextureRegionPool.put(fileName, btr);
		
		return s;
	}
	
	public AnimatedSprite getAnimatedSprite(String fileName, int column, int row) {
		if (mTiledTextureRegionPool.containsKey(fileName)) {
			AnimatedSprite s = new AnimatedSprite(0, 0, 
					mTiledTextureRegionPool.get(fileName), 
					mGameActivity.getVertexBufferObjectManager());
			s.setBlendFunction(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
			return s;
		}
		
		// サイズを自動的に取得する為にBitmapとして読み込み
		InputStream is = null;
		try {
			is = mGameActivity.getResources().getAssets().open("gfx/" + fileName);
		} catch (IOException e) {
			e.printStackTrace();
		}
		Bitmap bm = BitmapFactory.decodeStream(is);
		// Bitmapのサイズを基に2のべき乗の値を取得、BitmapTextureAtlasの生成
		BitmapTextureAtlas bta = new BitmapTextureAtlas(mGameActivity.getTextureManager(), 
				getTwoPowerSize(bm.getWidth()), getTwoPowerSize(bm.getHeight()),
				TextureOptions.BILINEAR_PREMULTIPLYALPHA);
		mGameActivity.getEngine().getTextureManager().loadTexture(bta);
		
		TiledTextureRegion ttr = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(
				bta, mGameActivity, fileName, 0, 0, column, row);
		AnimatedSprite s = new AnimatedSprite(0, 0, ttr, mGameActivity.getVertexBufferObjectManager());
		s.setBlendFunction(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
		
		// 再生性を防ぐ為、プールの登録
		mTiledTextureRegionPool.put(fileName, ttr);
		
		return s;
	}
	
	/**
	 * ボタン生成.
	 * @param normal
	 * @param pressed
	 * @return
	 */
	public ButtonSprite getButtonSprite(String normal, String pressed) {
		if (mTextureRegionPool.containsKey(normal) && mTextureRegionPool.containsKey(pressed)) {
			ButtonSprite s = new ButtonSprite(0, 0, 
					mTextureRegionPool.get(normal), mTextureRegionPool.get(pressed),
					mGameActivity.getVertexBufferObjectManager());
			s.setBlendFunction(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
			return s;
		}
				
		// サイズを自動的に取得する為にBitmapとして読み込み
		InputStream is = null;
		try {
			is = mGameActivity.getResources().getAssets().open("gfx/" + normal);
		} catch (IOException e) {
			e.printStackTrace();
		}
		Bitmap bm = BitmapFactory.decodeStream(is);
		// Bitmapのサイズを基に2のべき乗の値を取得、BitmapTextureAtlasの生成
		BuildableBitmapTextureAtlas bta = new BuildableBitmapTextureAtlas(
				mGameActivity.getTextureManager(), 
				getTwoPowerSize(bm.getWidth() * 2), 
				getTwoPowerSize(bm.getHeight()));
		
		ITextureRegion trNormal = BitmapTextureAtlasTextureRegionFactory.createFromAsset(
				bta, mGameActivity, normal);
		ITextureRegion trPressed = BitmapTextureAtlasTextureRegionFactory.createFromAsset(
				bta, mGameActivity, pressed);
		
		try {
			bta.build(new BlackPawnTextureAtlasBuilder<IBitmapTextureAtlasSource, BitmapTextureAtlas>(0, 0, 0));
			bta.load();
		} catch (TextureAtlasBuilderException e) {
			e.printStackTrace();
		}

		mTextureRegionPool.put(normal, trNormal);
		mTextureRegionPool.put(pressed, trPressed);
		
		ButtonSprite s = new ButtonSprite(0, 0, trNormal, trPressed, 
				mGameActivity.getVertexBufferObjectManager());
		s.setBlendFunction(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
		
		return s;
	}
	
	/**
	 * プールを開放、シングルトンを削除する.
	 */
	public void resetAllTexture() {
		/*
		 * Activity.finish()だけだとシングルトンなクラスがnullにならない為、明示的にnullを代入
		 */
		mSelf = null;
		mTextureRegionPool.clear();
		mTiledTextureRegionPool.clear();
	}
	
	/**
	 * 2のべき乗を求めて返す.
	 * @param size
	 * @return
	 */
	public int getTwoPowerSize(float size) {
		int value = (int) (size + 1);
		int pow2Value = 64;
		while (pow2Value < value) { 
			pow2Value *= 2;
		}
		return pow2Value;
	}
}

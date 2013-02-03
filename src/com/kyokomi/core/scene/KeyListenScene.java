package com.kyokomi.core.scene;

import java.io.IOException;

import org.andengine.audio.music.Music;
import org.andengine.audio.music.MusicFactory;
import org.andengine.audio.sound.Sound;
import org.andengine.audio.sound.SoundFactory;
import org.andengine.entity.Entity;
import org.andengine.entity.IEntity;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.shape.IAreaShape;
import org.andengine.entity.sprite.AnimatedSprite;
import org.andengine.entity.sprite.ButtonSprite;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.sprite.TiledSprite;
import org.andengine.entity.sprite.ButtonSprite.OnClickListener;
import org.andengine.entity.text.Text;
import org.andengine.entity.util.AverageFPSCounter;
import org.andengine.entity.util.FPSCounter;
import org.andengine.opengl.font.Font;
import org.andengine.opengl.texture.Texture;
import org.andengine.opengl.texture.TextureOptions;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.util.color.Color;

import com.kyokomi.core.activity.MultiSceneActivity;

import android.content.Intent;
import android.graphics.Typeface;
import android.view.KeyEvent;

/**
 * ActivityからのKeyEventなどを受け取り処理を行うリスナーを持つSceneクラスのサブクラス.
 * @author kyokomi
 *
 */
public abstract class KeyListenScene extends Scene {

	/**
	 * Sceneを管理するActivityのインスタンスを保持.
	 * アプリの場合のContextと同じように利用できる。
	 */
	private MultiSceneActivity baseActivity;
	
	/**
	 * コンストラクタ.
	 * @param baseGameActivity Sceneを管理するActivity
	 */
	public KeyListenScene(MultiSceneActivity baseActivity) {
		setTouchAreaBindingOnActionDownEnabled(true);
		this.baseActivity = baseActivity;
		prepareSoundAndMusic();
	}
	
	public MultiSceneActivity getBaseActivity() {
		return baseActivity;
	}
	
	/** イニシャライザ. */
	public abstract void init();
	
	/** サウンドの準備. */
	public abstract void prepareSoundAndMusic();
	
	/** KeyEventのリスナー. */
	public abstract boolean dispatchKeyEvent(KeyEvent e);
	
	/**
	 * Spriteの座標を画面中央に設定する（Spriteの中央が画面中央に）.
	 * @param sp Sprite画像
	 * @return 座標が画面中央になったSprite
	 */
	public IAreaShape placeToCenter(IAreaShape sp) {
		sp.setPosition(
				baseActivity.getEngine().getCamera().getWidth() / 2.0f - sp.getWidth() / 2.0f, 
				baseActivity.getEngine().getCamera().getHeight() / 2.0f - sp.getHeight() / 2.0f);
		return sp;
	}
	
	/**
	 * Spriteのx座標を画面中央に設定する.
	 * (Spriteのx座標の中心が画面のx座標の中心に）y座標は任意の値。
	 * @param sp Sprite画像
	 * @param y y座標
	 * @return x座標が画面中央になったSprite
	 */
	public IAreaShape placeToCenterX(IAreaShape sp, float y) {
		sp.setPosition(baseActivity.getEngine().getCamera().getWidth() / 2.0f - sp.getWidth() / 2.0f, y);
		return sp;
	}
	
	/**
	 * Spriteのy座標を画面中央に設定する.
	 * (Spriteのy座標の中心が画面のy座標の中心に）x座標は任意の値。
	 * @param sp Sprite画像
	 * @param x x座標
	 * @return y座標が画面中央になったSprite
	 */
	public IAreaShape placeToCenterY(IAreaShape sp, float x) {
		sp.setPosition(x, baseActivity.getEngine().getCamera().getHeight() / 2.0f - sp.getHeight() / 2.0f);
		return sp;
	}
	
	// -------------------
	// 汎用追加メソッド
	// -------------------
	/**
	 * ボタンの配置.
	 * @param baseEntity   配置先
	 * @param tag          押下時のメニュー判断用のタグ
	 * @param buttonSprite 配置するボタン
	 * @param y            配置するY座標
	 * @param listener     押下時のイベントリスナー
	 */
	public void attachButtonSprite(final IEntity baseEntity, int tag, 
			ButtonSprite buttonSprite, int y, final OnClickListener listener) {
		placeToCenterX(buttonSprite, y);
		buttonSprite.setTag(tag);
		buttonSprite.setOnClickListener(listener);
		baseEntity.attachChild(buttonSprite);
		registerTouchArea(buttonSprite);
	}
	
	/**
	 * リソースファイルからSpriteを取得.
	 * @param fileName ファイル名
	 * @return Sprite
	 */
	public Sprite getResourceSprite(String fileName) {
		return getBaseActivity().getResourceUtil().getSprite(fileName);
	}
	
	/**
	 * リソースファイルからSpriteを取得.
	 * @param fileName ファイル名
	 * @return TiledSprite
	 */
	public TiledSprite getResourceTiledSprite(String fileName, int column, int row) {
		return getBaseActivity().getResourceUtil().getTiledSprite(fileName, column, row);
	}
	
	/**
	 * リソースファイルからSpriteを取得.
	 * @param normalFileName 通常時ファイル名
	 * @param pressedFileName 押下時ファイル名
	 * @return Sprite
	 */
	public ButtonSprite getResourceButtonSprite(String normalFileName, String pressedFileName) {
		return getBaseActivity().getResourceUtil().getButtonSprite(normalFileName, pressedFileName);
	}
	
	/**
	 * リソースファイルからAnimatedSpriteを取得.
	 * @param fileName ファイル名
	 * @param column 横のコマ数
	 * @param row 縦のコマ数
	 * @return Sprite
	 */
	public AnimatedSprite getResourceAnimatedSprite(String fileName, int column, int row) {
		return getBaseActivity().getResourceUtil().getAnimatedSprite(fileName, column, row);
	}
	
	/**
	 * 画面横サイズを取得.
	 * @return 画面横サイズ
	 */
	public float getWindowWidth() {
		return getBaseActivity().getEngine().getCamera().getWidth();
	}
	
	/**
	 * 画面縦サイズを取得.
	 * @return 画面横サイズ
	 */
	public float getWindowHeight() {
		return getBaseActivity().getEngine().getCamera().getHeight();
	}
	
	/**
	 * 画面破棄.
	 * detachChildrenとdetachSelfを呼ぶときは別スレッドで行う。
	 */
	protected void detachEntity(final Entity entity) {
		getBaseActivity().runOnUpdateThread(new Runnable() {
			@Override
			public void run() {
				for (int i = 0; i < entity.getChildCount(); i++) {
					// タッチの検知も無効にする
					if (entity.getChildByIndex(i) instanceof ButtonSprite) {
						unregisterTouchArea((ButtonSprite) entity.getChildByIndex(i));
					}
				}
				entity.detachChildren();
				entity.detachSelf();
			}
		});
	}
	
	// ------- サウンド ------
	protected Sound createSoundFromFileName(String fileName) throws IOException {
		return SoundFactory.createSoundFromAsset(
				getBaseActivity().getSoundManager(), 
				getBaseActivity(), 
				fileName);
	}
	// ------- BGM -------
	protected Music createMusicFromFileName(String fileName) throws IOException {
		return MusicFactory.createMusicFromAsset(
				getBaseActivity().getMusicManager(),
				getBaseActivity(),
				fileName);
	}
	// ------- Intent ------
	/**
	 * TWEET送信.
	 * @param text 本文
	 */
	public void sendTweet(String text) {
		Intent intent = new Intent(Intent.ACTION_SEND);
		intent.setType("text/plain");
		intent.putExtra(Intent.EXTRA_TEXT, text);
		getBaseActivity().startActivity(intent);
	}
	
	// -------- FPS --------
	
	private FPSCounter mFpsCounter;
	private Text mFpsText;
	/**
	 * FPSの画面表示.
	 */
	protected void initFps(float x, float y, Font font) {
		mFpsText= new Text(x, y, font, "FPS:0.000000000000000000000000", 
				getBaseActivity().getVertexBufferObjectManager());
		attachChild(mFpsText);

		mFpsCounter = new AverageFPSCounter(1) {
			
			@Override
			protected void onHandleAverageDurationElapsed(float pFPS) {
				mFpsText.setText("FPS:" + pFPS);
			}
		};
		registerUpdateHandler(mFpsCounter);
		
	}
	protected void clearFps() {
		unregisterUpdateHandler(mFpsCounter);
		detachChild(mFpsText);
		
		mFpsCounter = null;
		mFpsText = null;
	}
	
	// --------フォント -------
	
	private Font mBaseFont;
	
	protected void initFont(int fontSize) {
		mBaseFont = createFont(Typeface.DEFAULT, fontSize, Color.WHITE);
	}
	protected Font getFont() {
		if (mBaseFont == null) {
			initFont(16);
		}
		return mBaseFont;
	}
	
	protected Font createFont(Typeface typeface, int fontSize, Color color) {
		Texture texture = new BitmapTextureAtlas(
				this.getBaseActivity().getTextureManager(), 512, 512, 
				TextureOptions.BILINEAR_PREMULTIPLYALPHA);
		
		Font font = new Font(this.getBaseActivity().getFontManager(), 
				texture, typeface, fontSize, true, color);
		
		this.getBaseActivity().getTextureManager().loadTexture(texture);
		this.getBaseActivity().getFontManager().loadFont(font);
		
		return font;
	}
}

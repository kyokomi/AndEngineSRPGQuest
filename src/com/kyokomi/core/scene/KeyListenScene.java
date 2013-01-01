package com.kyokomi.core.scene;

import java.io.IOException;

import org.andengine.audio.sound.Sound;
import org.andengine.audio.sound.SoundFactory;
import org.andengine.entity.Entity;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.sprite.AnimatedSprite;
import org.andengine.entity.sprite.ButtonSprite;
import org.andengine.entity.sprite.Sprite;

import com.kyokomi.core.activity.MultiSceneActivity;

import android.content.Intent;
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
	public Sprite placeToCenter(Sprite sp) {
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
	public Sprite placeToCenterX(Sprite sp, float y) {
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
	public Sprite placeToCenterY(Sprite sp, float x) {
		sp.setPosition(x, baseActivity.getEngine().getCamera().getHeight() / 2.0f - sp.getHeight() / 2.0f);
		return sp;
	}
	
	// -------------------
	// 汎用追加メソッド
	// -------------------
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
					unregisterTouchArea((ButtonSprite) entity.getChildByIndex(i));
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
}

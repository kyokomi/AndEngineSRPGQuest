package com.kyokomi.scrollquest;

import org.andengine.entity.scene.Scene;
import org.andengine.entity.sprite.Sprite;

import android.view.KeyEvent;
import com.kyokomi.scrollquest.R;

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
}

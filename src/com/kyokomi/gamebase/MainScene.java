package com.kyokomi.gamebase;

import android.view.KeyEvent;
import com.kyokomi.scrollquest.R;

/**
 * 全画面を描画範囲として持つクラス.
 * ゲーム起動中は常に1つのSceneクラスのインスタンスが最前面に表示されている状態。
 * 
 * 以下のような処理を行う。
 * <li>オブジェクトを追加したり</li>
 * <li>それ自体をタッチのリスナーとして登録したり</li>
 * <li>毎フレーム呼び出してオブジェクトの位置やスコアを更新するアップデートハンドラーを登録したり</li>
 * 
 * @author kyokomi
 *
 */
public class MainScene extends KeyListenScene {

	/**
	 * コンストラクタ.
	 * @param baseActivity Sceneを管理するActivity
	 */
	public MainScene(MultiSceneActivity baseActivity) {
		super(baseActivity);
		init();
	}
	
	/**
	 * イニシャライズ.
	 */
	public void init() {
		attachChild(getBaseActivity().getResourceUtil().getSprite("main_bg.png"));
	}

	@Override
	public void prepareSoundAndMusic() {
		
	}

	@Override
	public boolean dispatchKeyEvent(KeyEvent e) {
		// バックボタンが押された時
		if (e.getAction() == KeyEvent.ACTION_DOWN && e.getKeyCode() == KeyEvent.KEYCODE_BACK) {
			
			return false;
			
		// メニューキーを押した時
		} else if (e.getAction() == KeyEvent.ACTION_DOWN && e.getKeyCode() == KeyEvent.KEYCODE_MENU) {
			
			return false;
		}
		return false;
	}
}

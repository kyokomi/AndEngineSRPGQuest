package com.kyokomi.gamebase;

import org.andengine.engine.options.EngineOptions;
import org.andengine.entity.scene.Scene;
import org.andengine.ui.activity.BaseGameActivity;

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
public class MainScene extends Scene {

	/**
	 * Sceneを管理するActivityのインスタンスを保持.
	 * アプリの場合のContextと同じように利用できる。
	 */
	private BaseGameActivity mBaseGameActivity;
	
	/**
	 * コンストラクタ.
	 * @param baseGameActivity Sceneを管理するActivity
	 */
	public MainScene(BaseGameActivity baseGameActivity) {
		this.mBaseGameActivity = baseGameActivity;
		init();
	}
	
	/**
	 * イニシャライズ.
	 */
	public void init() {
		
	}
	
	
}

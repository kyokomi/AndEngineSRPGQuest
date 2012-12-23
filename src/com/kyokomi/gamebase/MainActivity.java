package com.kyokomi.gamebase;

import org.andengine.engine.camera.Camera;
import org.andengine.engine.options.EngineOptions;
import org.andengine.engine.options.ScreenOrientation;
import org.andengine.engine.options.resolutionpolicy.RatioResolutionPolicy;
import org.andengine.entity.scene.Scene;
import org.andengine.ui.activity.SimpleLayoutGameActivity;

/**
 * {@link BaseGameActivity}のサブクラスであり、XMLLayoutを利用してActivityを生成するクラス.
 * 逆にXMLLayoutを使わずにゲームを描画する際は、{@link SimpleBaseGameActivity}を利用する。
 * Layoutにしておくと後から広告を入れたり一部をWebViewにしたり簡単にできる。
 * 
 * 以下、ライフサイクル。
 * 
 * onCreate
 * onResume
 * onSurfaceCreated
 * onCreateGame
 * 
 * onCreateResources
 * onCreateScene
 * 
 * onPopulateScene
 * onGameCreated
 * onSurfaceChanged
 * onResumeGame
 * 
 * - Activity実行中 -
 * 
 * onPause
 * onPauseGame
 * onDestroy
 * onDestroyResources
 * onGameDestroyed
 * 
 * @author kyokomi
 *
 */
public class MainActivity extends SimpleLayoutGameActivity {

	// 画面サイズ
	private int CAMERA_WIDTH = 480;
	private int CAMERA_HEIGHT = 800;
	
	@Override
	public EngineOptions onCreateEngineOptions() {
		// サイズを指定し描画範囲をインスタンス化
		final Camera camera = new Camera(0, 0, CAMERA_WIDTH, CAMERA_HEIGHT);
		// ゲームのエンジンを初期化
		EngineOptions eo = new EngineOptions(
				// タイトルバー非表示モード
				true, 
				// 画面縦向き
				ScreenOrientation.PORTRAIT_FIXED,  
				// 画面（480 * 800）解像度の縦横比を保ったまま最大まで拡大
				new RatioResolutionPolicy(CAMERA_WIDTH, CAMERA_HEIGHT),
				 // 描画範囲
				camera);
		
		return eo;
	}

	/**
	 * リソース類管理クラスを準備.
	 */
	@Override
	protected void onCreateResources() {

	}

	/**
	 * Sceneサブクラスを返す.
	 */
	@Override
	protected Scene onCreateScene() {
		// MainSceneをインスタンス化し、エンジンにセット.
		MainScene mainScene = new MainScene(this);
		return mainScene;
	}

	/**
	 * ActivityのレイアウトのIDを返す.
	 */
	@Override
	protected int getLayoutID() {
		return R.layout.activity_main;
	}

	/**
	 * SceneがセットされるViewのIDを返す.
	 */
	@Override
	protected int getRenderSurfaceViewID() {
		return R.id.renderview;
	}
}

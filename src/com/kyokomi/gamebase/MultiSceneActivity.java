package com.kyokomi.gamebase;

import java.util.ArrayList;
import java.util.List;

import org.andengine.ui.activity.SimpleLayoutGameActivity;

import com.kyokomi.gamebase.utils.ResourceUtil;

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
public abstract class MultiSceneActivity extends SimpleLayoutGameActivity {

	/** ResourceUtilのインスタンス. */
	private ResourceUtil mResourceUtil;
	
	/** 起動済みのSceneの配列. */
	private List<KeyListenScene> mSceneArray;

	/**
	 * リソース類管理クラスを準備.
	 */
	@Override
	protected void onCreateResources() {
		mResourceUtil = ResourceUtil.getInstance(this);
		mSceneArray = new ArrayList<KeyListenScene>();
	}

	public ResourceUtil getResourceUtil() {
		return mResourceUtil;
	}
	
	public List<KeyListenScene> getSceneArray() {
		return mSceneArray;
	}
	
	/**
	 * 起動済みのKeyListenSceneを格納する為の抽象メソッド.
	 * @param scene 
	 */
	public abstract void appendScene(KeyListenScene scene);

	/**
	 * 最初のシーンに戻る為の抽象メソッド.
	 */
	public abstract void backToInitial();
	
	/**
	 * シーンとシーン格納配列を更新する抽象メソッド.
	 * @param scene
	 */
	public abstract void refreshRunningScene(KeyListenScene scene);
}

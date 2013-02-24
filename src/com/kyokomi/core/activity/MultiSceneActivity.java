package com.kyokomi.core.activity;

import java.util.ArrayList;
import java.util.List;

import org.andengine.ui.activity.SimpleLayoutGameActivity;

import android.database.sqlite.SQLiteDatabase;
import android.widget.Toast;

import com.kyokomi.core.db.GameBaseDBOpenHelper;
import com.kyokomi.core.scene.KeyListenScene;
import com.kyokomi.core.utils.ResourceUtil;
import com.kyokomi.core.manager.GameController;
import com.kyokomi.core.manager.MediaManager;

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
	
	/** ゲーム制御用. */
	private GameController mGameController;
	public GameController getGameController() {
		return mGameController;
	}
	public void initGameController() {
		mGameController = new GameController();
		boolean isLoading = mGameController.load(this);
		if (!isLoading) {
			mGameController.start(this);
		}
	}
	
	/** ResourceUtilのインスタンス. */
	private ResourceUtil mResourceUtil;
	/** BGM, SE管理のインスタンス. */
	private MediaManager mMediaManager;
	
	/** 起動済みのSceneの配列. */
	private List<KeyListenScene> mSceneArray;

	/**
	 * リソース類管理クラスを準備.
	 */
	@Override
	protected void onCreateResources() {
		mResourceUtil = ResourceUtil.getInstance(this);
		mMediaManager = MediaManager.getInstance(this);
		mSceneArray = new ArrayList<KeyListenScene>();
	}

	public ResourceUtil getResourceUtil() {
		return mResourceUtil;
	}
	public MediaManager getMediaManager() {
		return mMediaManager;
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
	
	public void showToast(final CharSequence text) {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				Toast.makeText(getBaseContext(), text, Toast.LENGTH_SHORT).show();
			}
		});
	}
	// DB
	private GameBaseDBOpenHelper mBaseDBOpenHelper; 
	public GameBaseDBOpenHelper getBaseDBOpenHelper() {
		return mBaseDBOpenHelper;
	}
	public void initBaseDB() {
		this.mBaseDBOpenHelper = new GameBaseDBOpenHelper(this);
	}
	private SQLiteDatabase mDB;
	public void openDB() {
		if (mDB == null || !mDB.isOpen()) {
			mDB = getBaseDBOpenHelper().getWritableDatabase();
		}
	}
	public void closeDB() {
		if (mDB != null && mDB.isOpen()) {
			mDB.close();
		}
	}
	public SQLiteDatabase getDB() {
		if (mDB == null || !mDB.isOpen()) {
			openDB();
		}
		return mDB;
	}
}

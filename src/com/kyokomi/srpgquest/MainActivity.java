package com.kyokomi.srpgquest;

import java.io.InputStream;

import org.andengine.audio.music.MusicFactory;
import org.andengine.audio.sound.SoundFactory;
import org.andengine.engine.camera.Camera;
import org.andengine.engine.options.EngineOptions;
import org.andengine.engine.options.ScreenOrientation;
import org.andengine.engine.options.resolutionpolicy.RatioResolutionPolicy;
import org.andengine.entity.scene.Scene;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.kyokomi.core.activity.MultiSceneActivity;
import com.kyokomi.core.dto.SaveDataDto;
import com.kyokomi.core.scene.KeyListenScene;
import com.kyokomi.core.utils.ResourceUtil;
import com.kyokomi.core.utils.ZipUtil;
import com.kyokomi.download.DownloadReceiver;
import com.kyokomi.srpgquest.R;
import com.kyokomi.srpgquest.scene.InitialScene;
import com.kyokomi.srpgquest.scene.MainScene;
import com.kyokomi.srpgquest.scene.SandBoxScene;
import com.kyokomi.srpgquest.scene.SrpgBaseScene;
import com.kyokomi.vollery.InputStreamRequest;

import android.app.DownloadManager;
import android.app.DownloadManager.Request;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;


/**
 * {@link BaseGameActivity}のサブクラスであり、XMLLayoutを利用してActivityを生成するクラス.
 * 逆にXMLLayoutを使わずにゲームを描画する際は、{@link SimpleBaseGameActivity}を利用する。
 * Layoutにしておくと後から広告を入れたり一部をWebViewにしたり簡単にできる。
 * 
 * @author kyokomi
 *
 */
public class MainActivity extends MultiSceneActivity {

	// 画面サイズ
	private int CAMERA_WIDTH = (int)(800 / 1.0);
	private int CAMERA_HEIGHT = (int)(480 / 1.0);
	
	@Override
	protected void onCreate(Bundle pSavedInstanceState) {
		super.onCreate(pSavedInstanceState);
		// DB初期化
		initBaseDB();
		
		// Gameデータ初期化
		initGameController();

		// ダウンロード初期化
//		initDownload();
	}
	@Override
	public EngineOptions onCreateEngineOptions() {
		// サイズを指定し描画範囲をインスタンス化
		final Camera camera = new Camera(0, 0, CAMERA_WIDTH, CAMERA_HEIGHT);
		// ゲームのエンジンを初期化
		EngineOptions eo = new EngineOptions(
				// タイトルバー非表示モード
				true, 
				// 画面横向き
				ScreenOrientation.LANDSCAPE_FIXED,  
				// 画面解像度の縦横比を保ったまま最大まで拡大
				new RatioResolutionPolicy(CAMERA_WIDTH, CAMERA_HEIGHT),
				 // 描画範囲
				camera);
		
		// 効果音とBGMの使用を許可する
		eo.getAudioOptions().setNeedsSound(true);
		eo.getAudioOptions().setNeedsMusic(true);
		
		return eo;
	}

	/**
	 * Sceneサブクラスを返す.
	 */
	@Override
	protected Scene onCreateScene() {
		// サウンドファイルの格納場所を指定
		SoundFactory.setAssetBasePath("mfx/");
		// サウンドファイルの格納場所を指定
		MusicFactory.setAssetBasePath("mfx/");
		
		InitialScene initialScene = new InitialScene(this);
		// 遷移管理用配列に追加
//		getSceneArray().add(initialScene);
		return initialScene;
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

	@Override
	public void appendScene(KeyListenScene scene) {
		getSceneArray().clear();
		getSceneArray().add(scene);
	}

	@Override
	public void backToInitial() {
		// 遷移管理用配列をクリア
		getSceneArray().clear();
		// 新たにInitialSceneからスタート
		KeyListenScene scene = new InitialScene(this);
		getSceneArray().add(scene);
		getEngine().setScene(scene);
	}

	@Override
	public void refreshRunningScene(KeyListenScene scene) {
		// 配列の最後の要素を削除し、新しいものに入れる
		getSceneArray().remove(getSceneArray().size() - 1);
		getSceneArray().add(scene);
		getEngine().setScene(scene);
	}
	
	@Override
	public boolean dispatchKeyEvent(KeyEvent e) {
		// バックボタンが押された時
		if (e.getAction() == KeyEvent.ACTION_DOWN && e.getKeyCode() == KeyEvent.KEYCODE_BACK) {

			// 起動中のSceneのdispatchEvent関数を呼び出す
			// 追加の処理が必要なときは、falseがかえってくるため処理する
			if (!getSceneArray().get(getSceneArray().size() - 1).dispatchKeyEvent(e)) {
				// Sceneが1つしか起動していないときはゲーム終了
				if (getSceneArray().size() == 1) {
					ResourceUtil.getInstance(this).resetAllTexture();
					finish();
				} else {
					getEngine().setScene(getSceneArray().get(getSceneArray().size() - 2));
					getSceneArray().remove(getSceneArray().size() - 1);
				}
			}
			return true;
//		} else if (e.getAction() == KeyEvent.ACTION_DOWN && e.getKeyCode() == KeyEvent.KEYCODE_MENU) {
//			getSceneArray().get(getSceneArray().size() - 1).dispatchKeyEvent(e);
//			return true;
		}
		return false;
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		
		// SrpgBaseScene実行中なら一時停止
		if (getEngine().getScene() instanceof SrpgBaseScene) {
			((SrpgBaseScene) getEngine().getScene()).onPause();
		}
	}
	@Override
	protected synchronized void onResume() {
		super.onResume();

		// SrpgBaseScene実行中なら一時停止
		if (getEngine().getScene() instanceof SrpgBaseScene) {
			((SrpgBaseScene) getEngine().getScene()).onResume();
		}
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(Menu.NONE, 0, Menu.NONE, "Clear");
		menu.add(Menu.NONE, 2, Menu.NONE, "SandBox");
//		menu.add("");
		return super.onCreateOptionsMenu(menu);
	}
	
	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		if (item.getItemId() == 0) {
			// デバッグクリア
			getMediaManager().stopPlayingMusic();
			// 次のシナリオへ
			if (getEngine().getScene() instanceof MainScene) {
				((MainScene) getEngine().getScene()).nextScenario();
			}
			
		// ExpとGoldをリセット(DB保存はしません)
		} else if (item.getItemId() == 1) {
			if (getEngine().getScene() instanceof KeyListenScene) {
				SaveDataDto saveDataDto = getGameController().createSaveDataDto(
						(KeyListenScene) getEngine().getScene());
				getGameController().addExp(saveDataDto.getExp() * -1);
				getGameController().addGold(saveDataDto.getGold() * -1);
			}
		} else if (item.getItemId() == 2) {
			// サンドボックス起動
			if (getEngine().getScene() instanceof KeyListenScene) {
				ResourceUtil.getInstance(this).resetAllTexture();
				SandBoxScene scene = new SandBoxScene(this);
				getEngine().setScene(scene);
				// 履歴は残さない
				appendScene(scene);
			}
		}
		return super.onMenuItemSelected(featureId, item);
	}
	
	
//	// ----------------------------------------
//	// 通信関連
//	// ----------------------------------------
//	
//	private DownloadManager mDownloadManager;
//	private DownloadReceiver mDownloadReceiver;
//	
//	private void initDownload() {
//		mDownloadManager = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
//		
//		IntentFilter filter = new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE);
//		 
//		mDownloadReceiver = new DownloadReceiver();
//		
//		registerReceiver(mDownloadReceiver, filter);
//	}
//	
//	// --- TODO: 非推奨なダウンロード ----
//	private RequestQueue mQueue;
//	private Long mDownloadId;
//	
////	private void initDownload() {
////		mProgressDialog = new ProgressDialog(this);
////		mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
////		mProgressDialog.setCancelable(false);
////		mProgressDialog.setMessage("ダウンロード中...");
////	}
//	
//	public void startDownload(final ZipDownloadWithUnZipListener listener) {
//		runOnUiThread(new Runnable() {
//			@Override
//			public void run() {
//				Uri.Builder builder = new Uri.Builder();
//				builder.scheme("http");
//				builder.authority("s3-ap-northeast-1.amazonaws.com");
//				builder.path("srpg-data/hoge.zip");
//				
//				Request request = new Request(builder.build());
//				request.setDestinationInExternalFilesDir(getApplicationContext(), 
//						Environment.DIRECTORY_DOWNLOADS, "/gfx.zip");
////				request.setDestinationInExternalFilesDir(getApplicationContext(), 
////						ZipUtil.getAbsolutePathOnInternalStorage(getApplicationContext(), "/download"), "/gfx.zip");
//				request.setTitle("gfx");
//				request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE | DownloadManager.Request.NETWORK_WIFI);
//				request.setMimeType("application/zip");
//				
////				mQueue = Volley.newRequestQueue(getApplicationContext());
////				zipDownloadWithUnZip(listener);
//				mDownloadId = mDownloadManager.enqueue(request);
//			}
//		});		
//	}
//	
//	private ProgressDialog mProgressDialog;
//	private void showProgressMessage(final String message) {
//		runOnUiThread(new Runnable() {
//			@Override
//			public void run() {
//				mProgressDialog.setMessage(message);				
//			}
//		});
//	}
//	
//	public interface ZipDownloadWithUnZipListener {
//		public void finish();
//	}
//	private void zipDownloadWithUnZip(final ZipDownloadWithUnZipListener listener) {
//		
//		String url = "https://s3-ap-northeast-1.amazonaws.com/srpg-data/gfx.zip";
//		InputStreamRequest request = new InputStreamRequest(url, new Response.Listener<InputStream>() {
//			@Override
//			public void onResponse(final InputStream response) {
//				showProgressMessage("ダウンロード完了");
//				
//				runOnUpdateThread(new Runnable() {
//					@Override
//					public void run() {
//						
//						ZipUtil.unZipInternalStorage(getApplicationContext(), response, 
//								new ZipUtil.ZipProgressListener() {
//							@Override
//							public void progress(int progress) {
//								showProgressMessage("展開中... " + progress);
//							}
//						});
//						mProgressDialog.dismiss();
//						showToast("初期ダウンロード処理完了");
//						
//						listener.finish();
//					}
//				});
//			}
//		}, new Response.ErrorListener() {
//			@Override
//			public void onErrorResponse(VolleyError error) {
//				Log.e(TAG, "zipDownloadWithUnZip", error);
//			}
//		});
//		
//		mProgressDialog.show();
//		
//		mQueue.add(request);
//	}
//	
//	private static final String TAG = "MainActivity";
}

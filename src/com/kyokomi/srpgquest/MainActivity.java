package com.kyokomi.srpgquest;


import org.andengine.audio.music.MusicFactory;
import org.andengine.audio.sound.SoundFactory;
import org.andengine.engine.camera.Camera;
import org.andengine.engine.options.EngineOptions;
import org.andengine.engine.options.ScreenOrientation;
import org.andengine.engine.options.resolutionpolicy.RatioResolutionPolicy;
import org.andengine.entity.scene.Scene;

import com.google.ads.Ad;
import com.google.ads.AdListener;
import com.google.ads.AdRequest;
import com.google.ads.AdSize;
import com.google.ads.AdView;
import com.google.ads.AdRequest.ErrorCode;
import com.google.android.gcm.GCMRegistrar;
import com.kyokomi.billing.util.IabHelper;
import com.kyokomi.billing.util.IabResult;
import com.kyokomi.billing.util.Inventory;
import com.kyokomi.billing.util.Purchase;
import com.kyokomi.billing.util.SkuDetails;
import com.kyokomi.core.activity.MultiSceneActivity;
import com.kyokomi.core.dto.SaveDataDto;
import com.kyokomi.core.scene.KeyListenScene;
import com.kyokomi.core.utils.ResourceUtil;
import com.kyokomi.srpgquest.R;
import com.kyokomi.srpgquest.scene.InitialScene;
import com.kyokomi.srpgquest.scene.MainScene;
import com.kyokomi.srpgquest.scene.SandBoxScene;
import com.kyokomi.srpgquest.scene.SrpgBaseScene;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.RelativeLayout;


/**
 * {@link BaseGameActivity}のサブクラスであり、XMLLayoutを利用してActivityを生成するクラス.
 * 逆にXMLLayoutを使わずにゲームを描画する際は、{@link SimpleBaseGameActivity}を利用する。
 * Layoutにしておくと後から広告を入れたり一部をWebViewにしたり簡単にできる。
 * 
 * @author kyokomi
 *
 */
public class MainActivity extends MultiSceneActivity {

	private static final String TAG = "MainActivity";
	
	@Override
	protected void onCreate(Bundle pSavedInstanceState) {
		super.onCreate(pSavedInstanceState);

		// push通知
		initGCMPush();
		
		// 課金初期化
		//initBilling();
		// 広告初期化
		//initAdMob();
        
		// DB初期化
		initBaseDB();
		
		// Gameデータ初期化
		initGameController();
	}
	
	// ----------- GCM ------------------
	// TODO: これもどっかからもらわないと？アプリに埋め込むのはなんかなぁ
	public static final String SENDER_ID = "766558119619";
	
	private void initGCMPush() {
		/*
		 * GCM への登録に成功してもサーバへの登録 ID の送信に失敗する可能性がありますが、その場合はリトライしなければなりません。
		 */
		
		GCMRegistrar.checkDevice(MainActivity.this);
		GCMRegistrar.checkManifest(MainActivity.this);
		final String regId = GCMRegistrar.getRegistrationId(this);
		if (regId.equals("")) {
			// 未登録時
			GCMRegistrar.register(this, SENDER_ID);
		} else {
			// 登録時
			Log.d(TAG, "Already registered regId = " + regId);
		}
		String regId2 = GCMRegistrar.getRegistrationId(this);
		Log.d(TAG, "regId2 = " + regId2);
	}
	
	// ----------- 広告 ------------------
	AdView mAdView;
	
	private void initAdMob() {
		mAdView = new AdView(this, AdSize.BANNER, "a1520f83371e652");
		mAdView.setAdListener(new AdListener() {
			@Override
			public void onReceiveAd(Ad arg0) {
				Log.d(TAG, "onReceiveAd " + arg0);
			}
			@Override
			public void onPresentScreen(Ad arg0) {
				Log.d(TAG, "onPresentScreen " + arg0);
			}
			@Override
			public void onLeaveApplication(Ad arg0) {
				Log.d(TAG, "onLeaveApplication " + arg0);
			}
			@Override
			public void onFailedToReceiveAd(Ad arg0, ErrorCode arg1) {
				Log.d(TAG, "onFailedToReceiveAd " + arg1);
			}
			@Override
			public void onDismissScreen(Ad arg0) {
				Log.d(TAG, "onDismissScreen " + arg0);
			}
		});
		
		RelativeLayout layout = (RelativeLayout)findViewById(R.id.mainlayout);
		layout.addView(mAdView);
		AdRequest adRequest = new AdRequest();
		adRequest.addTestDevice(AdRequest.TEST_EMULATOR);            // エミュレータ
		// 端末のデバイスIDはlogcatに出ます
		mAdView.loadAd(adRequest);
	}
	
	// ------------ 課金 --------------
	IabHelper mHelper;
	
	private static final String COIN_100 = "com.kyokomi.coin100";

	private void initBilling() {
		
		// 課金初期化
		// TODO: コミットできねー！のでCONSTRUCT_YOURでコミット
		// TODO: サーバーからもらう方式かな。。。
		String base64EncodedPublicKey = "CONSTRUCT_YOUR";
		mHelper = new IabHelper(this, base64EncodedPublicKey);
        // TODO: コピペしてんじゃねーぞ判定1
        if (base64EncodedPublicKey.contains("CONSTRUCT_YOUR")) {
            throw new RuntimeException("Please put your app's public key in MainActivity.java. See README.");
        }
        mHelper.enableDebugLogging(true);
        Log.d(TAG, "Starting setup.");
        mHelper.startSetup(new IabHelper.OnIabSetupFinishedListener() {
            public void onIabSetupFinished(IabResult result) {
                Log.d(TAG, "Setup finished.");

                if (!result.isSuccess()) {
                    return;
                }

                // Have we been disposed of in the meantime? If so, quit.
                if (mHelper == null) return;

                // IAB is fully set up. Now, let's get an inventory of stuff we own.
                // IABは、完全に設定されています。さて、我々が所有してもののインベントリを取得しましょう。
                Log.d(TAG, "Setup successful. Querying inventory.");
                mHelper.queryInventoryAsync(mGotInventoryListener);
            }
        });
	}
	
    // Listener that's called when we finish querying the items and subscriptions we own
    IabHelper.QueryInventoryFinishedListener mGotInventoryListener = new IabHelper.QueryInventoryFinishedListener() {
        public void onQueryInventoryFinished(IabResult result, Inventory inventory) {
            Log.d(TAG, "Query inventory finished.");

            // Have we been disposed of in the meantime? If so, quit.
            if (mHelper == null) return;

            // Is it a failure?
            if (result.isFailure()) {
                return;
            }

            Log.d(TAG, "Query inventory was successful.");
            
            // 商品一覧チェック
            SkuDetails details = inventory.getSkuDetails(COIN_100);
            Log.d(TAG, "Title: " + details.getTitle());
            
            /*
             * Check for items we own. Notice that for each purchase, we check
             * the developer payload to see if it's correct! See
             * verifyDeveloperPayload().
             */

            /**
             * TODO：購入の開発ペイロードが正しいことを確認します。
             * それは、購入を開始したときに送られたものと同じものになります。
             * 
             * 警告：購入を開始するときにローカルでランダムな文字列を生成し、それをここで検証には良い方法のように思えるかもしれませんが、
             * 上にあるため、これは、ユーザが1つのデバイス上のアイテムを購入した後、
             * 別のデバイス上のアプリを使用した場合に失敗します他のデバイスは、あなたが最初に生成されるランダムな文字列にアクセスできません。
             * 
             * だから、良い開発者ペイロードは、次の特性があります。
             * 
             *  1。二つの異なるユーザーがアイテムを購入した場合、ペイロードは1ユーザの購入は別のユーザに再生することができないように、
             *  それらの間で異なっている。
             *  
             *  2。アプリ購入の流れを（そう、そのユーザーが所有している他のデバイス上の1つのデバイスの仕事上のユーザーが購入したアイテム）が
             *  開始されたものではなかった場合でも、ペイロードは、あなたがそれを検証できるようなものでなければならない。
             *  
             *  アプリのインストール全体で開発ペイロードを格納し、検証するために、独自のサーバを使用することをお勧めします。
             */
            
            /**
             * 検討:
             * base64Keyをどこで持つか。。。
             * 
             * 購入時:
             *  1.サーバーにプロダクト一覧を貰う（android <- サーバー）
             *  2.ユーザーが購入を選択
             *  3.サーバーに購入予定を送信し、requestCodeを貰う(int)（android -> サーバー）
             *  4.購入処理呼び出し（android -> googlePlay）
             *  5.onIabPurchaseFinishedで結果を受け取る
             *  6.サーバーにresultCodeとか送信してチェック（android -> サーバー）
             */
            Log.d(TAG, "Initial inventory query finished; enabling main UI.");
        }
    };
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG, "onActivityResult(" + requestCode + "," + resultCode + "," + data);
        if (mHelper == null) return;

        // Pass on the activity result to the helper for handling
        if (!mHelper.handleActivityResult(requestCode, resultCode, data)) {
            // not handled, so handle it ourselves (here's where you'd
            // perform any handling of activity results not related to in-app
            // billing...
            super.onActivityResult(requestCode, resultCode, data);
        }
        else {
            Log.d(TAG, "onActivityResult handled by IABUtil.");
        }
    }
    
    @Override
    public void onDestroy() {
        super.onDestroy();

        // very important:
        Log.d(TAG, "Destroying helper.");
        if (mHelper != null) {
            mHelper.dispose();
            mHelper = null;
        }
    }
    
    // (arbitrary) request code for the purchase flow
    static final int RC_REQUEST = 10001;
    
    // User clicked the "Buy Gas" button
    public void onBuyCoin() {
        Log.d(TAG, "Upgrade button clicked; launching purchase flow for upgrade.");
//      setWaitScreen(true);

      /* TODO: for security, generate your payload here for verification. See the comments on
       *        verifyDeveloperPayload() for more info. Since this is a SAMPLE, we just use
       *        an empty string, but on a production app you should carefully generate this. */
      String payload = "";

      // TODO: 購入呼び出し
      mHelper.launchPurchaseFlow(this, COIN_100, RC_REQUEST,
    		  new IabHelper.OnIabPurchaseFinishedListener() {
				
				@Override
				public void onIabPurchaseFinished(IabResult result, Purchase info) {
		            Log.d(TAG, "Purchase finished: " + result + ", purchase: " + info);

		            // if we were disposed of in the meantime, quit.
		            if (mHelper == null) return;

		            if (result.isFailure()) {
//		                complain("Error purchasing: " + result);
//		                setWaitScreen(false);
		                return;
		            }
//		            if (!verifyDeveloperPayload(info)) {
//		                complain("Error purchasing. Authenticity verification failed.");
//		                setWaitScreen(false);
//		                return;
//		            }

		            Log.d(TAG, "Purchase successful.");

		            if (info.getSku().equals(COIN_100)) {
		                // bought the premium upgrade!
		                Log.d(TAG, "Purchase is premium upgrade. Congratulating user.");
//		                alert("Thank you for upgrading to premium!");
//		                mIsPremium = true;
//		                updateUi();
//		                setWaitScreen(false);
		            }
		            else {
		                // bought the infinite gas subscription
		                Log.d(TAG, "Infinite error.");
		            }
				}
			}, payload);
    }
    
    // ------------------------------- ここまで課金 -----------------------------
   
	// 画面サイズ
	private int CAMERA_WIDTH = (int)(800 / 1.0);
	private int CAMERA_HEIGHT = (int)(480 / 1.0);

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
}

package com.kyokomi.scrollquest;

import org.andengine.engine.handler.timer.ITimerCallback;
import org.andengine.engine.handler.timer.TimerHandler;
import org.andengine.entity.scene.IOnSceneTouchListener;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.sprite.AnimatedSprite;
import org.andengine.entity.sprite.Sprite;
import org.andengine.input.touch.TouchEvent;

import android.util.Log;
import android.view.KeyEvent;

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
public class MainScene extends KeyListenScene implements IOnSceneTouchListener {

	// ----------------------------------------
	// オブジェクト
	// ----------------------------------------
	/** 背景. */
	private Sprite background;
	/** 草1. */
	private Sprite grass01;
	/** 草2. */
	private Sprite grass02;
	
	/** 武器（アイコンセット）. */
	private AnimatedSprite weapon;
	/** 攻撃エフェクト. */
	private AnimatedSprite attackEffect;
	
	/** プレイヤーキャラクター. */
	private AnimatedSprite player;
	/** プレイヤーキャラクター(回避). */
	private AnimatedSprite playerDefense;
	/** プレイヤーキャラクター(攻撃). */
	private AnimatedSprite playerAttack;
	
	// ----------------------------------------
	// ゲーム設定パラメータ
	// ----------------------------------------
	/** スクロールの速度. */
	private float scrollSpeed;

	// ----------------------------------------
	// ゲームステータス
	// ----------------------------------------
	/** タッチ可否. */
	private boolean isTouchEnabled;
	
	// ---- ユーザーアクション ----
	/** 攻撃中フラグ. */
	private boolean isAttacking;
	/** ジャンプ中フラグ. */
	private boolean isJumping;
	/** スライディング中フラグ. */
	private boolean isSlideing;
	
	/** ドラッグ開始座標. */
	private float[] touchStartPoint;
	
	// -------------------------------------------------------
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
		background = getResourceSprite("main_bg.png");
		background.setZIndex(-1);
		attachChild(background);
		
		// --------- 設定、初期化 -----------
		touchStartPoint = new float[2];
		isTouchEnabled = true;
		isJumping      = false;
		isAttacking    = false;
		isSlideing     = false;
		
		// スクロール速度の初期値を設定
		scrollSpeed = 6;

		// --------- オブジェクト関連 -----------
		
		// 草1オブジェクトを追加
		grass01 = getResourceSprite("main_grass.png");
		grass01.setPosition(0, 420);
		attachChild(grass01);
		
		// 草2オブジェクトを追加
		grass02 = getResourceSprite("main_grass.png");
		grass02.setPosition(getBaseActivity().getEngine().getCamera().getWidth(), 420);
		attachChild(grass02);
		
		// アイコンオブジェクトを追加（プレイヤーの下に表示）
		weapon = getResourceAnimatedSprite("icon_set.png", 16, 48);
		weapon.setScale(2f);
		weapon.setAlpha(0.0f);
		attachChild(weapon);
		
		// エフェクトオブジェクトを追加（武器の上に表示）
		attackEffect = getResourceAnimatedSprite("effect002_b2.png", 5, 1);
		attackEffect.setAlpha(0.0f);
		attackEffect.setScale(0.5f);
		attachChild(attackEffect);
		
		// playerキャラを追加
		player = getResourceAnimatedSprite("actor110_0_s.png", 3, 4);
		player.setScale(2f);
		// 6-8の右向きのコマのみアニメーション
		setPlayerToDefaultPosition();
		attachChild(player);
		// 攻撃と防御のスプライトも読み込んでおく
		playerDefense = getResourceAnimatedSprite("actor110_2_s2.png", 3, 4);
		playerDefense.setScale(2f);
		playerDefense.setPosition(80, 400);// 325
		playerDefense.setAlpha(0.0f);
		attachChild(playerDefense);
		playerAttack = getResourceAnimatedSprite("actor110_3_s2.png", 3, 4);
		playerAttack.setScale(2f);
		playerAttack.setPosition(80, 400);// 325
		playerAttack.setAlpha(0.0f);
		attachChild(playerAttack);
		
		// 1秒間に60回、updateHandlerを呼び出す
		registerUpdateHandler(updateHandler);
		
		// Sceneのタッチリスナーを登録
		setOnSceneTouchListener(this);
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
	
	/**
	 * アップデートハンドラ.
	 * 1秒間に60回呼び出される。
	 */
	public TimerHandler updateHandler = new TimerHandler(1 / 60f, true, new ITimerCallback() {
		@Override
		public void onTimePassed(TimerHandler pTimerHandler) {
			// 草をscrollSpeedの分移動させる
			grass01.setX(grass01.getX() - scrollSpeed);
			if (grass01.getX() <= -getWindowWidth()) {
				// 草の右側が画面左端より左に移動した場合は、画面幅＊２分だけ右へ移動
				grass01.setX(grass01.getX() + (getWindowWidth() * 2));
			}
			grass02.setX(grass02.getX() - scrollSpeed);
			if (grass02.getX() <= -getWindowWidth()) {
				// 草の右側が画面左端より左に移動した場合は、画面幅＊２分だけ右へ移動
				grass02.setX(grass02.getX() + (getWindowWidth() * 2));
			}
		}
	});
	
	// --------------------------------------------------
	// IOnSceneTouchListener
	// --------------------------------------------------
	
	/**
	 * タッチイベント発生時に呼び出される.
	 */
	@Override
	public boolean onSceneTouchEvent(Scene pScene, TouchEvent pSceneTouchEvent) {
		
		// タッチの座標を取得
		float x = pSceneTouchEvent.getX();
		float y = pSceneTouchEvent.getY();
		
		if (pSceneTouchEvent.getAction() == TouchEvent.ACTION_DOWN) {
			
			// 攻撃判定(画面中心より右側の場合)
			if (x > getWindowWidth() / 2) {
				// スライディング中でなく、攻撃中でもなければ攻撃
				if (!isSlideing && !isAttacking && !isJumping) {
					// 攻撃
					attackSprite();
				}
				return true;
			}
		}
		
		if (!isTouchEnabled) {
			return true;
		}
	
		if (pSceneTouchEvent.getAction() == TouchEvent.ACTION_DOWN) {
			
			// 開始点を登録
			touchStartPoint[0] = x;
			touchStartPoint[1] = y;
			
		} else if (pSceneTouchEvent.getAction() == TouchEvent.ACTION_UP 
				|| pSceneTouchEvent.getAction() == TouchEvent.ACTION_CANCEL) {
			float[] touchEndPoint = new float[2];
			touchEndPoint[0] = x;
			touchEndPoint[1] = y;
			
			// フリックの距離が短すぎるときにはフリックと判定しない
			if (!isTouchFlick(touchStartPoint, touchEndPoint)) {
				return true;
			}
			
			// フリックの角度を求める
			double angle = getAngleByTwoPostion(touchStartPoint, touchEndPoint);
			// 下から上へのフリックを0度に調整
			angle -= 180;
			// 下から上へのフリック（ジャンプ）
			if (angle > -45 && angle < 45) {
				jumpSprite();
			// 上から下へのフリック（スライディング）
			} else if (angle > 135 && angle < 225) {
				slideSprite();
			}
		}
		return false;
	}
	
	// --------------------------------------------------
	// プレイヤー関連メソッド
	// --------------------------------------------------
	
	public void attackSprite() {
		// 攻撃の連射を防ぐ
		isAttacking = true;
		
		// プレイヤーの歩行を停止
		player.stopAnimation();
		player.setAlpha(0.0f);
		// 攻撃
		setPlayerToAttackPosition();
		// 武器とエフェクト
		setWeaponPosition();
		
		// ジャンプ終了時ハンドラー設定
		registerUpdateHandler(new TimerHandler(0.5f, new ITimerCallback() {
			@Override
			public void onTimePassed(TimerHandler pTimerHandler) {
				// 元に戻す
				playerAttack.setAlpha(0.0f);
				attackEffect.setAlpha(0.0f);
				weapon.setAlpha(0.0f);
				setPlayerToDefaultPosition();
				isAttacking = false;
			}
		}));
	}
	/**
	 * プレイヤージャンプ.
	 */
	public void jumpSprite() {
		// ジャンプ中はタップを受け付けない
		isTouchEnabled = false;
		isJumping = true;
		
		// プレイヤーの歩行を停止
		player.stopAnimation();
		player.setAlpha(0.0f);
		// ジャンプ
		setPlayerToJumpPositon();
		// ジャンプ終了時ハンドラー設定
		registerUpdateHandler(new TimerHandler(0.5f, new ITimerCallback() {
			@Override
			public void onTimePassed(TimerHandler pTimerHandler) {
				// 元に戻す
				playerDefense.setAlpha(0.0f);
				setPlayerToDefaultPosition();
				isTouchEnabled = true;
				isJumping = false;
			}
		}));
	}
	
	/**
	 * プレイヤースライディング.
	 */
	public void slideSprite() {
		// スライディング中はタップを受け付けない
		isTouchEnabled = false;
		isSlideing = true;
		
		// プレイヤーの歩行を停止
		player.stopAnimation();
		player.setAlpha(0.0f);
		// スライディングポジションへ
		setPlayerToSlidePositon();
		// ジャンプ終了時ハンドラー設定
		registerUpdateHandler(new TimerHandler(0.5f, new ITimerCallback() {
			@Override
			public void onTimePassed(TimerHandler pTimerHandler) {
				// 元に戻す
				playerDefense.setAlpha(0.0f);
				setPlayerToDefaultPosition();
				isTouchEnabled = true;
				isSlideing = false;
			}
		}));
	}
	
	/**
	 * プレイヤーデフォルトポジション設定.
	 */
	public void setPlayerToDefaultPosition() {
		player.animate(
				new long[]{100, 100, 100}, 
				new int[]{6, 7, 8}, 
				true);
		player.setPosition(80, 400);// 325
		player.setAlpha(1.0f);
	}
	
	/**
	 * 武器ポジション設定.
	 */
	public void setWeaponPosition() {
		
		weapon.setCurrentTileIndex(16 * 16 + 5);
		weapon.setZIndex(playerAttack.getZIndex() + 1);
		sortChildren();// Z-indexを反映
		
		weapon.setRotation(340f);
		weapon.setPosition(playerAttack.getX() - 25, playerAttack.getY() + 10);
		weapon.setAlpha(1.0f);
		
		// 武器
		registerUpdateHandler(new TimerHandler(0.3f, new ITimerCallback() {
			@Override
			public void onTimePassed(TimerHandler pTimerHandler) {
				float time = pTimerHandler.getTimerSeconds();
				Log.d("Timer", "TimerSeconds = " + time);
				
				weapon.setZIndex(playerAttack.getZIndex() - 1);
				sortChildren();// Z-indexを反映
				
				weapon.setRotation(100f);
				weapon.setPosition(playerAttack.getX() + 50, playerAttack.getY() - 10);
			}
		}));
		
		// エフェクト
		registerUpdateHandler(new TimerHandler(0.2f, new ITimerCallback() {
			@Override
			public void onTimePassed(TimerHandler pTimerHandler) {
				attackEffect.animate(
						new long[]{100, 100, 100}, 
						new int[]{4, 3, 2}, 
						false);
				attackEffect.setPosition(playerAttack.getX(), playerAttack.getY() - 70);
				attackEffect.setAlpha(1.0f);
			}
		}));
	}
	
	/**
	 * プレイヤー攻撃ポジション設定.
	 */
	public void setPlayerToAttackPosition() {
		playerAttack.animate(
				new long[]{100, 100, 100}, 
				new int[]{8, 7, 6}, 
				false);
		playerAttack.setPosition(player.getX(), player.getY());
		playerAttack.setAlpha(1.0f);
//		registerUpdateHandler(new TimerHandler(0.2f, new ITimerCallback() {
//			@Override
//			public void onTimePassed(TimerHandler pTimerHandler) {
//
//			}
//		}));
	}
	
	/**
	 * プレイヤージャンプポジション設定.
	 */
	public void setPlayerToJumpPositon() {
		playerDefense.animate(
				new long[]{200, 300}, 
				new int[]{0, 3}, 
				true);
		playerDefense.setPosition(player.getX(), player.getY());
		playerDefense.setAlpha(1.0f);
		// 上に飛ぶ感じのアニメーション
		registerUpdateHandler(new TimerHandler(0.2f, new ITimerCallback() {
			@Override
			public void onTimePassed(TimerHandler pTimerHandler) {
				playerDefense.setPosition(player.getX(), player.getY() - 100);
			}
		}));
	}
	
	/**
	 * プレイヤースライディングポジション設定.
	 */
	public void setPlayerToSlidePositon() {
		playerDefense.animate(
				new long[]{200, 300}, 
				new int[]{0, 11}, 
				false);
		playerDefense.setPosition(player.getX(), player.getY());
		playerDefense.setAlpha(1.0f);
		// 前に倒れこむ感じのアニメーション
		registerUpdateHandler(new TimerHandler(0.2f, new ITimerCallback() {
			@Override
			public void onTimePassed(TimerHandler pTimerHandler) {
				playerDefense.setPosition(player.getX() + 70, player.getY());
			}
		}));
	}
	
	// --------------------------------------------------
	// 汎用メソッド
	// --------------------------------------------------
			
	/**
	 * リソースファイルからSpriteを取得.
	 * @param fileName ファイル名
	 * @return Sprite
	 */
	private Sprite getResourceSprite(String fileName) {
		return getBaseActivity().getResourceUtil().getSprite(fileName);
	}
	
	/**
	 * リソースファイルからAnimatedSpriteを取得.
	 * @param fileName ファイル名
	 * @param column 横のコマ数
	 * @param row 縦のコマ数
	 * @return Sprite
	 */
	private AnimatedSprite getResourceAnimatedSprite(String fileName, int column, int row) {
		return getBaseActivity().getResourceUtil().getAnimatedSprite(fileName, column, row);
	}
	
	/**
	 * 画面横サイズを取得.
	 * @return 画面横サイズ
	 */
	private float getWindowWidth() {
		return getBaseActivity().getEngine().getCamera().getWidth();
	}
	
	/**
	 * タッチ開始位置と終了位置を元にフリックなのか判定.
	 * float[]は new float[2]で [0]がx座標 [1]がy座標
	 * @param startPoints タッチ開始位置
	 * @param endPoint タッチ終了位置
	 * @return true:フリック / false:フリックとみなさない
	 */
	private boolean isTouchFlick(float[] startPoints, float[] endPoint) {
		
		float xDistance = endPoint[0] -startPoints[0];
		float yDistance = endPoint[1] -startPoints[1];
		
		if (Math.abs(xDistance) < 50 && Math.abs(yDistance) < 50) {
			return false;
		}
		return true;
	}
	
	/**
	 * タッチ開始位置と終了位置を元に2点間の角度を求める.
	 * @param startPoints
	 * @param endPoint
	 * @return 2点間の角度
	 */
	private double getAngleByTwoPostion(float[] startPoints, float[] endPoint) {
		double result = 0;
		
		float xDistance = endPoint[0] -startPoints[0];
		float yDistance = endPoint[1] -startPoints[1];
		
		result = Math.atan2((double) yDistance, (double) xDistance) * 180 / Math.PI;
		
		result += 270;
		
		return result;
	}
}

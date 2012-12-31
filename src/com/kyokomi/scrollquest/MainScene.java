package com.kyokomi.scrollquest;

import java.util.ArrayList;

import org.andengine.engine.handler.timer.ITimerCallback;
import org.andengine.engine.handler.timer.TimerHandler;
import org.andengine.entity.modifier.FadeInModifier;
import org.andengine.entity.modifier.FadeOutModifier;
import org.andengine.entity.modifier.LoopEntityModifier;
import org.andengine.entity.modifier.SequenceEntityModifier;
import org.andengine.entity.scene.IOnSceneTouchListener;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.sprite.AnimatedSprite;
import org.andengine.entity.sprite.Sprite;
import org.andengine.input.touch.TouchEvent;

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
	// 障害物用enum
	// ----------------------------------------
	enum ObstacleTag {
		TAG_OBSTACLE_DEFAULT(0, 0, "", 0, 0),
		TAG_OBSTACLE_TRAP(1, 50, "main_trap.png", 0, 0),
		TAG_OBSTACLE_FIRE(2, 40, "main_fire.png", 0, 0),
		TAG_OBSTACLE_ENEMY(3, 80, "main_enemy.png", 1, 2),
		TAG_OBSTACLE_EAGLE(4, 70, "main_eagle.png", 1, 2),
		;
		/** 値. */
		private Integer value;
		/** 衝突許容値. */
		private Integer allowable;
		/** ファイル名. */
		private String fileName;
		/** Spriteコマ数(横). */
		private Integer column;
		/** Spriteコマ数(縦). */
		private Integer row;
		
		ObstacleTag(Integer value, Integer allowable, String fileName, Integer column, Integer row) {
			this.value = value;
			this.allowable = allowable;
			this.fileName = fileName;
			this.column = column;
			this.row = row;
		}
		public static ObstacleTag getObstacleTag(Integer tag) {
			ObstacleTag[] values = values();
			for (ObstacleTag obstacleTag : values) {
				if (obstacleTag.getValue() == tag) {
					return obstacleTag;
				}
			}
			return ObstacleTag.TAG_OBSTACLE_DEFAULT;
		}
		public Integer getValue() {
			return value;
		}
		public Integer getAllowable() {
			return allowable;
		}
		public String getFileName() {
			return fileName;
		}
		public Integer getColumn() {
			return column;
		}
		public Integer getRow() {
			return row;
		}
	}
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
	/** 死亡中フラグ. */
	private boolean isDeading;
	/** 死んだ後の回復中フラグ. */
	private boolean isRecovering;
	
	/** ドラッグ開始座標. */
	private float[] touchStartPoint;
	
	/** 画面外に移動した障害物を除去する為に利用する配列. */
	private ArrayList<Sprite> spriteOutOfBoundArray;
	
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
		spriteOutOfBoundArray = new ArrayList<Sprite>();
		touchStartPoint = new float[2];
		isTouchEnabled = true;
		isJumping      = false;
		isAttacking    = false;
		isSlideing     = false;
		isRecovering   = false;
		isDeading      = false;
		
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
		// 1秒毎に障害物出現関数を呼び出し
		registerUpdateHandler(obstacleAppearHandler);
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
			
			// 障害物を移動
			for (int i = 0; i < getChildCount(); i++) {
				if (getChildByIndex(i).getTag() == ObstacleTag.TAG_OBSTACLE_TRAP.getValue() 
						|| getChildByIndex(i).getTag() == ObstacleTag.TAG_OBSTACLE_FIRE.getValue()
						|| getChildByIndex(i).getTag() == ObstacleTag.TAG_OBSTACLE_ENEMY.getValue()
						|| getChildByIndex(i).getTag() == ObstacleTag.TAG_OBSTACLE_EAGLE.getValue()) {
					
					// 鷹のみ上空から滑空させる
					if (getChildByIndex(i).getTag() == ObstacleTag.TAG_OBSTACLE_EAGLE.getValue()) {
						if (getChildByIndex(i).getX() < 300) {
							getChildByIndex(i).setY(getChildByIndex(i).getY() + 10);
						}
					}
					getChildByIndex(i).setPosition(
							getChildByIndex(i).getX() - scrollSpeed,
							getChildByIndex(i).getY());
					if (getChildByIndex(i).getX() + ((Sprite) getChildByIndex(i)).getWidth() < 0) {
						// すぐに削除するとインデックスがずれるため一旦配列に追加
						spriteOutOfBoundArray.add((Sprite) getChildByIndex(i));
					}
					
					// 回復中は無視
					if (!isRecovering && !isDeading) {
						// Sprite同士が衝突している時のみ高度な衝突判定を行う
						Sprite obj = (Sprite) getChildByIndex(i);
						if (obj.collidesWith(player)) {
							
							// プレイヤーのｘ座標と障害物のx座標中心間の距離
							float distanceBetweenCenterXOfPlayerAndObstacle = getDistanceBetween(player, obj);
							
							// 衝突を許容する距離
							ObstacleTag objTag = ObstacleTag.getObstacleTag(obj.getTag());
							float allowableDistance = getAllowableDistance(player, obj, objTag.getAllowable());
							if (distanceBetweenCenterXOfPlayerAndObstacle < allowableDistance) {
								// 敵の攻撃
								enemyAttackSprite(objTag); 
							}
						}
					}
				}
			}
			// 配列の中身を削除
			for (Sprite sp : spriteOutOfBoundArray) {
				sp.detachSelf();
			}
		}
	});
	
	/**
	 * 障害物生成タイマー.
	 */
	public TimerHandler obstacleAppearHandler = new TimerHandler(1, true, new ITimerCallback() {
		@Override
		public void onTimePassed(TimerHandler pTimerHandler) {
			Sprite obstacle = null;
			AnimatedSprite animatedObstacle = null;
			// 敵の種類をランダムに選択
			int r = (int) (Math.random() * 4);
			switch (r) {
			case 0:
				// 竹でできた罠
				obstacle = getResourceSprite(ObstacleTag.TAG_OBSTACLE_TRAP.getFileName());
				obstacle.setPosition(getWindowWidth() + obstacle.getWidth(), 380);
				obstacle.setTag(ObstacleTag.TAG_OBSTACLE_TRAP.getValue());
				attachChild(obstacle);
				break;
			case 1:
				// 火の玉
				obstacle = getResourceSprite(ObstacleTag.TAG_OBSTACLE_FIRE.getFileName());
				obstacle.setPosition(getWindowWidth() + obstacle.getWidth(), 260);
				obstacle.setTag(ObstacleTag.TAG_OBSTACLE_FIRE.getValue());
				attachChild(obstacle);
				break;
			case 2:
				// 敵
				animatedObstacle = getResourceAnimatedSprite(
						ObstacleTag.TAG_OBSTACLE_ENEMY.getFileName(), 
						ObstacleTag.TAG_OBSTACLE_ENEMY.getColumn(), 
						ObstacleTag.TAG_OBSTACLE_ENEMY.getRow());
				animatedObstacle.setPosition(getWindowWidth() + animatedObstacle.getWidth(), 325);
				animatedObstacle.setTag(ObstacleTag.TAG_OBSTACLE_ENEMY.getValue());
				attachChild(animatedObstacle);
				animatedObstacle.animate(100);
			case 3:
				// 鷹
				animatedObstacle = getResourceAnimatedSprite(
						ObstacleTag.TAG_OBSTACLE_EAGLE.getFileName(), 
						ObstacleTag.TAG_OBSTACLE_ENEMY.getColumn(), 
						ObstacleTag.TAG_OBSTACLE_ENEMY.getRow());
				animatedObstacle.setPosition(getWindowWidth() + animatedObstacle.getWidth(), 30);
				animatedObstacle.setTag(ObstacleTag.TAG_OBSTACLE_EAGLE.getValue());
				attachChild(animatedObstacle);
				animatedObstacle.animate(200);
				break;
			default:
				break;
			}
			sortChildren();
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
		// 死亡中は受け付けない
		if (isDeading) {
			return true;
		}
		
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
	public void enemyAttackSprite(ObstacleTag enemyObstacleTag) {
		
		// プレイヤーの歩行を停止
		player.stopAnimation();
		player.setAlpha(0.0f);
		
		// 倒れ画像にする
		playerDefense.stopAnimation();
		playerDefense.setCurrentTileIndex(11);
		playerDefense.setPosition(player.getX(), player.getY());
		playerDefense.setAlpha(1.0f);

		// 死亡
		isDeading = true;
		
		registerUpdateHandler(new TimerHandler(1.0f, new ITimerCallback() {
			@Override
			public void onTimePassed(TimerHandler pTimerHandler) {
				// 回復後は一定時間無敵状態
				isDeading = false;
				isRecovering = true;
				
				playerDefense.setAlpha(0.0f);
				setPlayerToDefaultPosition();
				
				// 4回ループでフェードアウトとフェードインを繰り返して点滅させる
				player.registerEntityModifier(new LoopEntityModifier(
							new SequenceEntityModifier(
									new FadeOutModifier(0.25f),
									new FadeInModifier(0.25f)
							), 4));
				
				// 無敵状態を解除
				registerUpdateHandler(new TimerHandler(2.0f, new ITimerCallback() {
					@Override
					public void onTimePassed(TimerHandler pTimerHandler) {
						isRecovering = false;
					}
				}));
			}
		}));
	}
	
	public void attackSprite() {
		// 攻撃の連射を防ぐ
		isAttacking = true;
		isTouchEnabled = false;
		
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
				isTouchEnabled = true;
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
	
	/**
	 * Sprite同士のx座標中心間の距離を求める.
	 * @param sprite1 左側
	 * @param sprite2 右側
	 * @return x座標中心間の距離
	 */
	private float getDistanceBetween(Sprite sprite1, Sprite sprite2) {
		return Math.abs((sprite2.getX() + sprite2.getWidth() / 2) - (sprite1.getX() + sprite1.getWidth() / 2));
	}
	
	/**
	 * 衝突の許容値を考慮した距離を求める.
	 * @param sprite1 左側
	 * @param sprite2 右側
	 * @param allowable 衝突の許容値
	 * @return 衝突の許容値を考慮した距離
	 */
	private float getAllowableDistance(Sprite sprite1, Sprite sprite2, float allowable) {
		return (sprite1.getWidth() / 2) + (sprite2.getWidth() / 2 - allowable);
	}
}

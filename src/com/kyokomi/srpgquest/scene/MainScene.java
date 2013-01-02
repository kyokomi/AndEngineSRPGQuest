package com.kyokomi.srpgquest.scene;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.microedition.khronos.opengles.GL10;

import org.andengine.audio.sound.Sound;
import org.andengine.engine.handler.timer.ITimerCallback;
import org.andengine.engine.handler.timer.TimerHandler;
import org.andengine.entity.IEntity;
import org.andengine.entity.modifier.DelayModifier;
import org.andengine.entity.modifier.FadeInModifier;
import org.andengine.entity.modifier.FadeOutModifier;
import org.andengine.entity.modifier.LoopEntityModifier;
import org.andengine.entity.modifier.MoveModifier;
import org.andengine.entity.modifier.ScaleModifier;
import org.andengine.entity.modifier.SequenceEntityModifier;
import org.andengine.entity.primitive.Rectangle;
import org.andengine.entity.scene.IOnSceneTouchListener;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.sprite.AnimatedSprite;
import org.andengine.entity.sprite.ButtonSprite;
import org.andengine.entity.sprite.ButtonSprite.OnClickListener;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.text.Text;
import org.andengine.entity.text.TextOptions;
import org.andengine.input.touch.TouchEvent;
import org.andengine.opengl.font.BitmapFont;
import org.andengine.opengl.font.Font;
import org.andengine.opengl.texture.Texture;
import org.andengine.opengl.texture.TextureOptions;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.util.HorizontalAlign;
import org.andengine.util.color.Color;
import org.andengine.util.modifier.ease.EaseQuadOut;

import com.kyokomi.core.activity.MultiSceneActivity;
import com.kyokomi.core.handler.CustomTimerHandler;
import com.kyokomi.core.scene.KeyListenScene;
import com.kyokomi.core.utils.CollidesUtil;
import com.kyokomi.core.utils.SPUtil;
import com.kyokomi.srpgquest.constant.ObstacleType;

import android.graphics.Typeface;
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
public class MainScene extends KeyListenScene 
	implements IOnSceneTouchListener, ButtonSprite.OnClickListener {

	// ----------------------------------------
	// サウンド
	// ----------------------------------------
	private Sound playerAttackSound;
	private Sound enemyAttackSound;
	private Sound recoverySound;
	private Sound gameOverSound;
	/** ボタンが押された時のサウンド. */
	private Sound btnPressedSound;
	
	// ----------------------------------------
	// オブジェクト
	// ----------------------------------------
	/** 遊び方画面. */
	private Sprite instructionSprite;
	/** 遊び方画面のボタン. */
	private ButtonSprite instructionBtn;
	
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
	
	/** ライフ表示. */
	private ArrayList<Sprite> lifeSpriteArray;
	
	// --- スコア ---
	
	/** 現在のスコアを表示するテキスト. */
	private Text currentScoreText;
	/** 過去最高のスコアを表示するテキスト. */
	private Text highScoreText;
	/** 現在のスコア. */
	private int currentScore;
	
	// --- ポーズ ---
	private Rectangle pauseBackground;
	
	// ----------------------------------------
	// ゲーム設定パラメータ
	// ----------------------------------------
	/** スクロールの速度. */
	private float scrollSpeed;

	/** 結果画面用. */
	private int zIndexResult = 2;
	
	// ----------------------------------------
	// プレイ情報
	// ----------------------------------------
	/** 残りライフ. */
	private int life;
	
	// ----------------------------------------
	// ゲームステータス
	// ----------------------------------------
	/** タッチ可否. */
	private boolean isTouchEnabled;
	/** ポーズ中か否か. */
	private boolean isPaused;
	
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
	/** ゲームオーバーフラグ. */
	private boolean isGameOver;
	
	/** ドラッグ開始座標. */
	private float[] touchStartPoint;
	
	/** 画面外に移動した障害物を除去する為に利用する配列. */
	private ArrayList<Sprite> spriteOutOfBoundArray;
	
	/** 登録済みアップデートハンドラを格納する配列. */
	private List<CustomTimerHandler> updateHandlerList;
	
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
		updateHandlerList = new ArrayList<CustomTimerHandler>();
		touchStartPoint = new float[2];
		isTouchEnabled = true;
		isJumping      = false;
		isAttacking    = false;
		isSlideing     = false;
		isRecovering   = false;
		isDeading      = false;
		isPaused       = false;
		isGameOver     = false;
		currentScore = 0;
		
		// スクロール速度の初期値を設定
		scrollSpeed = 6;

		// ------ テキスト ------
		Texture texture = new BitmapTextureAtlas(
				getBaseActivity().getTextureManager(), 512, 512, 
				TextureOptions.BILINEAR_PREMULTIPLYALPHA);
		Font font = new Font(getBaseActivity().getFontManager(), 
				texture, Typeface.DEFAULT_BOLD, 22, true, Color.YELLOW);
		// EngineのTextureManagerにフォントTextureを読み込み
		getBaseActivity().getTextureManager().loadTexture(texture);
		getBaseActivity().getFontManager().loadFont(font);
		
		// 読み込んだフォントを利用して得点を表示
		currentScoreText = new Text(20, 20, font, "得点：" + currentScore, 20, 
				new TextOptions(HorizontalAlign.LEFT), 
				getBaseActivity().getVertexBufferObjectManager());
		attachChild(currentScoreText);
		highScoreText = new Text(20, 50, font, "ハイスコア：" + SPUtil.getInstance(getBaseActivity()).getHighScore(), 20, 
				new TextOptions(HorizontalAlign.LEFT), 
				getBaseActivity().getVertexBufferObjectManager());
		attachChild(highScoreText);
		
		// ------ ライフ -------
		
		// ライフの初期値を3とし、画面左上にSpriteを表示
		lifeSpriteArray = new ArrayList<Sprite>();
		life = 3;
		for (int i = 0; i < life; i++) {
			Sprite heart = getResourceSprite(ObstacleType.TAG_OBSTACLE_HEART.getFileName());
			heart.setScale(0.6f);
			heart.setPosition(10 + 45 * i, 90);
			attachChild(heart);
			lifeSpriteArray.add(heart);
		}
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
		
		// ハイスコアが500以下の時のみヘルプ画面を出す
		if (SPUtil.getInstance(getBaseActivity()).getHighScore() > 500) {
			startGame();
		} else {
			showHelp();
		}
	}

	@Override
	public void prepareSoundAndMusic() {
		try {
			playerAttackSound = createSoundFromFileName("SE_ATTACK_ZANGEKI_01.wav");
			enemyAttackSound = createSoundFromFileName("SE_ATTACK_DAGEKI_01.wav");
			recoverySound = createSoundFromFileName("SE_HP_CURE.wav");
			gameOverSound = createSoundFromFileName("SE_LOSE.wav");
			btnPressedSound = createSoundFromFileName("clock00.wav");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public boolean dispatchKeyEvent(KeyEvent e) {
		
		// バックボタンが押された時
		if (e.getAction() == KeyEvent.ACTION_DOWN && e.getKeyCode() == KeyEvent.KEYCODE_BACK) {
			// ゲームオーバー済みなら何もしない
			if (isGameOver) {
				return false;
			}
			// ポーズ中ならポーズ画面を消去
			if (isPaused) {
				// 別スレッドで破棄
				detachEntity(pauseBackground);
				
				// ゲーム再開
				resumeGame();
				isPaused = false;
				return true;
			} else {
				// ポーズ中以外のBackキー何もしない
				return false;
			}
			
		// メニューキーを押した時
		} else if (e.getAction() == KeyEvent.ACTION_DOWN && e.getKeyCode() == KeyEvent.KEYCODE_MENU) {
			// ポーズ中でなければメニューを表示
			if (!isPaused) {
				showMenu();
			}
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
			
			// スコアの増加とセット
			if (!isRecovering) {
				// スコアをインクリメント
				currentScore++;
				// スコアをセット
				currentScoreText.setText("得点：" + currentScore);
			}
			
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
				if (getChildByIndex(i).getTag() == ObstacleType.TAG_OBSTACLE_TRAP.getValue() 
						|| getChildByIndex(i).getTag() == ObstacleType.TAG_OBSTACLE_FIRE.getValue()
						|| getChildByIndex(i).getTag() == ObstacleType.TAG_OBSTACLE_ENEMY.getValue()
						|| getChildByIndex(i).getTag() == ObstacleType.TAG_OBSTACLE_EAGLE.getValue()
						|| getChildByIndex(i).getTag() == ObstacleType.TAG_OBSTACLE_HEART.getValue()) {
					
					// 鷹のみ上空から滑空させる
					if (getChildByIndex(i).getTag() == ObstacleType.TAG_OBSTACLE_EAGLE.getValue()) {
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
						
						if (obj.getTag() == ObstacleType.TAG_OBSTACLE_HEART.getValue()) {
							spriteOutOfBoundArray.add(obj);
							addLifeSprite();
							
						} else {
							
							if (obj.collidesWith(player)) {
								
								// プレイヤーのｘ座標と障害物のx座標中心間の距離
								float distanceBetweenCenterXOfPlayerAndObstacle = CollidesUtil.getDistanceBetween(player, obj);
								
								// 衝突を許容する距離
								ObstacleType objTag = ObstacleType.getObstacleType(obj.getTag());
								float allowableDistance = CollidesUtil.getAllowableDistance(
										player, obj, objTag.getAllowable());
								if (distanceBetweenCenterXOfPlayerAndObstacle < allowableDistance) {
									// 敵の攻撃
									enemyAttackSprite(objTag); 
								}
							}
						}
					}
				}
			}
			// 配列の中身を削除
			for (Sprite sp : spriteOutOfBoundArray) {
				sp.detachSelf();
			}
			
			// 残りライフが3未満かつステージ上に回復アイテムが無い時
			if (life < 3 && getChildByTag(ObstacleType.TAG_OBSTACLE_HEART.getValue()) == null) {
				// 500分の1の確率で回復アイテムを出現
				if ((int) (Math.random() * 500) == 1) {
					Sprite obstacle = getResourceSprite(ObstacleType.TAG_OBSTACLE_HEART.getFileName());
					// 画面右外に追加。y座標はランダム
					obstacle.setPosition(getWindowWidth(), 350 - (int) (Math.random() * 200));
					obstacle.setTag(ObstacleType.TAG_OBSTACLE_HEART.getValue());
					attachChild(obstacle);
				}
			}
		}
	});
	
	/**
	 * 障害物生成タイマー.
	 */
	public TimerHandler obstacleAppearHandler = new TimerHandler(1, true, new ITimerCallback() {
		@Override
		public void onTimePassed(TimerHandler pTimerHandler) {
			// 敵の種類をランダムに選択
			int r = (int) (Math.random() * 4);
			switch (r) {
			case 0:
				attachChild(makeObstacleSprite(ObstacleType.TAG_OBSTACLE_TRAP));
				break;
			case 1:
				attachChild(makeObstacleSprite(ObstacleType.TAG_OBSTACLE_FIRE));
				break;
			case 2:
				attachChild(makeObstacleSprite(ObstacleType.TAG_OBSTACLE_ENEMY));
				break;
			case 3:
				attachChild(makeObstacleSprite(ObstacleType.TAG_OBSTACLE_EAGLE));
				break;
			default:
				break;
			}
			// ZIndexを並べ直し
			sortChildren();
		}
	});
	
	// ------------------------------------------------
	// 画面表示など
	// ------------------------------------------------
	
	// メニュー用定数
	private static final int MENU_MENU    = 21;
	private static final int MENU_TWEET   = 22;
	private static final int MENU_RANKING = 23;
	private static final int MENU_RETRY   = 24;
	private static final int MENU_RESUME  = 25;
	
	/**
	 * メニュー表示.
	 */
	public void showMenu() {
		// ゲームオーバー時は無視
		if (isGameOver) {
			return;
		}
		// ゲームを一時停止させる
		pauseGame();
		
		// 四角形を描画(TODO: 汎用メソッドにしてもいいかも)
		pauseBackground = new Rectangle(0, 0, getWindowWidth(), 
				getWindowHeight(), getBaseActivity().getVertexBufferObjectManager());
		pauseBackground.setBlendFunction(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
		pauseBackground.setColor(0, 0, 0);
		pauseBackground.setAlpha(0.7f);
		attachChild(pauseBackground);
		
		try {
			ButtonSprite btnResume = getResourceButtonSprite("menu_btn_05.png", "menu_btn_05_p.png");
			attachMenuButton(pauseBackground, MENU_RESUME, btnResume, 100, this);
			
			ButtonSprite btnRetry = getResourceButtonSprite("menu_btn_02.png", "menu_btn_02_p.png");
			attachMenuButton(pauseBackground, MENU_RETRY, btnRetry, 220, this);
			
			ButtonSprite btnMenu = getResourceButtonSprite("menu_btn_04.png", "menu_btn_04_p.png");
			attachMenuButton(pauseBackground, MENU_MENU, btnMenu, 340, this);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		isPaused = true;
	}

	/**
	 * ヘルプ表示.
	 */
	public void showHelp() {
		instructionSprite = getResourceSprite("instruction.png");
		placeToCenter(instructionSprite);
		attachChild(instructionSprite);
		
		instructionBtn = getResourceButtonSprite(
				"instruction_btn.png", "instruction_btn_p.png");
		placeToCenterX(instructionBtn, 380);
		attachChild(instructionBtn);
		registerTouchArea(instructionBtn);
		instructionBtn.setOnClickListener(new ButtonSprite.OnClickListener() {
			@Override
			public void onClick(ButtonSprite pButtonSprite, float pTouchAreaLocalX,
					float pTouchAreaLocalY) {
				instructionSprite.detachSelf();
				instructionBtn.detachSelf();
				unregisterTouchArea(instructionBtn);
				
				// ゲーム開始
				startGame();
			}
		});
	}
	
	/**
	 * ゲームオーバー表示.
	 */
	public void showGameOver() {
	
		// Sceneのタッチリスナーを解除
		setOnSceneTouchListener(null);
		
		// ハイスコア更新時は保存
		if (currentScore > SPUtil.getInstance(getBaseActivity()).getHighScore()) {
			SPUtil.getInstance(getBaseActivity()).setHighScore(currentScore);
		}
		
		// 透明な背景を作成
		Rectangle resultBackground = new Rectangle(
				getWindowWidth(), 0,
				getWindowWidth(), getWindowHeight(), 
				getBaseActivity().getVertexBufferObjectManager());
		// 透明にする
		resultBackground.setColor(Color.TRANSPARENT);
		// 敵キャラクターより全面に表示
		resultBackground.setZIndex(zIndexResult);
		attachChild(resultBackground);
		sortChildren();
		
		// ビットマップフォントを作成
		BitmapFont bitmapFont = new BitmapFont(
				getBaseActivity().getTextureManager(), 
				getBaseActivity().getAssets(), 
				"font/result.fnt");
		bitmapFont.load();
		
		// ビットマップフォントを元にスコアを表示
		Text resultText = new Text(0, 0, bitmapFont, 
				"" + currentScore + " pts", 
				getBaseActivity().getVertexBufferObjectManager());
		resultText.setPosition(getWindowWidth() / 2.0f - resultText.getWidth() / 2.0f, 60);
		resultBackground.attachChild(resultText);
		
		// 各ボタン配置
		ButtonSprite btnRanking = getResourceButtonSprite("menu_btn_01.png", "menu_btn_01_p.png");
		attachMenuButton(resultBackground, MENU_RANKING, btnRanking, 145, this);
		
		ButtonSprite btnRetry = getResourceButtonSprite("menu_btn_02.png", "menu_btn_02_p.png");
		attachMenuButton(resultBackground, MENU_RETRY, btnRetry, 260, this);
		
		ButtonSprite btnTweet = getResourceButtonSprite("menu_btn_03.png", "menu_btn_03_p.png");
		attachMenuButton(resultBackground, MENU_TWEET, btnTweet, 330, this);
		
		ButtonSprite btnMenu = getResourceButtonSprite("menu_btn_04.png", "menu_btn_04_p.png");
		attachMenuButton(resultBackground, MENU_MENU, btnMenu, 400, this);
		
		// 横から移動してくるアニメーション
		resultBackground.registerEntityModifier(new SequenceEntityModifier(
				new DelayModifier(1.0f), 
				new MoveModifier(1.0f, 
						resultBackground.getX(), 
						resultBackground.getX() - getWindowWidth(), 
						resultBackground.getY(),
						resultBackground.getY(),
						EaseQuadOut.getInstance())));
	}

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
			if (!CollidesUtil.isTouchFlick(touchStartPoint, touchEndPoint)) {
				return true;
			}
			
			// フリックの角度を求める
			double angle = CollidesUtil.getAngleByTwoPostion(touchStartPoint, touchEndPoint);
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
	
	/**
	 * ButtonSpriteのClick判定.
	 */
	@Override
	public void onClick(ButtonSprite pButtonSprite, float pTouchAreaLocalX,
			float pTouchAreaLocalY) {
		btnPressedSound.play();
		
		switch (pButtonSprite.getTag()) {
		case MENU_RESUME:
			// detachChildrenとdetachSelfを同じタイミングで呼ぶときは別スレッドで
			detachEntity(pauseBackground);
			resumeGame();
			isPaused = false;
			break;
		case MENU_RETRY:
			MainScene scene = new MainScene(getBaseActivity());
			getBaseActivity().refreshRunningScene(scene);
			break;
		case MENU_MENU:
			getBaseActivity().backToInitial();
			break;
		case MENU_TWEET:
			break;
		default:
			break;
		}
	}
	// --------------------------------------------------
	// ゲーム状況変更
	// --------------------------------------------------
	
	/**
	 * ゲーム開始.
	 */
	public void startGame() {
		// 1秒間に60回、updateHandlerを呼び出す
		registerUpdateHandler(updateHandler);
		// 1秒毎に障害物出現関数を呼び出し
		registerUpdateHandler(obstacleAppearHandler);
		// Sceneのタッチリスナーを登録
		setOnSceneTouchListener(MainScene.this);
	}
	
	/**
	 * ゲーム一時停止.
	 */
	public void pauseGame() {
		// 全てのAnimatedSpriteのアニメーションをストップ
		for (int i = 0; i < getChildCount(); i++) {
			if (getChildByIndex(i) instanceof AnimatedSprite) {
				((AnimatedSprite) getChildByIndex(i)).stopAnimation();
			}
		}
		unregisterUpdateHandler(updateHandler);
		unregisterUpdateHandler(obstacleAppearHandler);
		
		for (CustomTimerHandler handler : updateHandlerList) {
			handler.pause();
		}
	}
	
	/**
	 * ゲーム再開.
	 */
	public void resumeGame() {
		// 1秒間に60回、updateHandlerを呼び出す
		registerUpdateHandler(updateHandler);
		// 1秒毎に障害物出現関数を呼び出し
		registerUpdateHandler(obstacleAppearHandler);
		
		// AnimatedSpriteのアニメーションを再開
		
		// --- 障害物のアニメーション ---
		for (int i = 0; i < getChildCount(); i++) {
			if (getChildByIndex(i) instanceof AnimatedSprite) {
				AnimatedSprite animeSprite = (AnimatedSprite) getChildByIndex(i);
				ObstacleType obstacleType = ObstacleType.getObstacleType(animeSprite.getTag());
				if (obstacleType.getDuration() > 0) {
					animeSprite.animate(obstacleType.getDuration());
				}
			}
		}
		
		// --- プレイヤーのアニメーション --- 
		for (CustomTimerHandler handler : updateHandlerList) {
			handler.resume();
		}
	}

	// --------------------------------------------------
	// TimerHandler
	// --------------------------------------------------
	/**
	 * プレイヤー攻撃時ハンドラー.
	 */
	private CustomTimerHandler playerIsAttackTimerHandler = 
			new CustomTimerHandler(0.5f, new ITimerCallback() {
		@Override
		public void onTimePassed(TimerHandler pTimerHandler) {
			Log.d("Timer", "playerIsAttackTimerHandler");
			
			// 元に戻す
			playerAttack.setAlpha(0.0f);
			attackEffect.setAlpha(0.0f);
			weapon.setAlpha(0.0f);
			setPlayerToDefaultPosition();
			isAttacking = false;
			isTouchEnabled = true;
			
			unregisterCustomUpdateHandler(playerIsAttackTimerHandler);
		}
	});
	
	private CustomTimerHandler weaponIsTimerHandler = 
			new CustomTimerHandler(0.3f, new ITimerCallback() {
		@Override
		public void onTimePassed(TimerHandler pTimerHandler) {
			Log.d("Timer", "weaponIsTimerHandler");
			
			weapon.setZIndex(playerAttack.getZIndex() - 1);
			sortChildren();// Z-indexを反映
			
			weapon.setRotation(100f);
			weapon.setPosition(playerAttack.getX() + 50, playerAttack.getY() - 10);
			
			unregisterCustomUpdateHandler(weaponIsTimerHandler);
		}
	}); 
	
	private CustomTimerHandler weaponIsEffectTimerHandler = 
			new CustomTimerHandler(0.2f, new ITimerCallback() {
		@Override
		public void onTimePassed(TimerHandler pTimerHandler) {
			Log.d("Timer", "weaponIsEffectTimerHandler");
			
			attackEffect.animate(
					new long[]{100, 100, 100}, 
					new int[]{4, 3, 2}, 
					false);
			attackEffect.setPosition(playerAttack.getX(), playerAttack.getY() - 70);
			attackEffect.setAlpha(1.0f);
			
			// 削除するSpriteを一旦格納する配列
			List<AnimatedSprite> spToRemoveArray = new ArrayList<AnimatedSprite>();
			
			for (int i = 0; i < getChildCount(); i++) {
				// 攻撃で倒せるのは敵と鷹のみ
				if (getChildByIndex(i).getTag() == ObstacleType.TAG_OBSTACLE_ENEMY.getValue() 
						|| getChildByIndex(i).getTag() == ObstacleType.TAG_OBSTACLE_EAGLE.getValue()) {
					// 衝突判定
					AnimatedSprite obj = (AnimatedSprite) getChildByIndex(i);
					if (obj.collidesWith(weapon) || obj.collidesWith(attackEffect)) {
						spToRemoveArray.add(obj);
					}
				}
			}
			// 削除
			for (AnimatedSprite sp :spToRemoveArray) {
				sp.detachSelf();
			}
			
			unregisterCustomUpdateHandler(weaponIsEffectTimerHandler);
		}
	});
			
	/**
	 * プレイヤージャンプ時ハンドラー.
	 */
	private CustomTimerHandler playerIsJumpTimerHandler = 
			new CustomTimerHandler(0.5f, new ITimerCallback() {
		@Override
		public void onTimePassed(TimerHandler pTimerHandler) {
			// 元に戻す
			playerDefense.setAlpha(0.0f);
			setPlayerToDefaultPosition();
			isTouchEnabled = true;
			isJumping = false;
			
			unregisterCustomUpdateHandler(playerIsJumpTimerHandler);
		}
	});
	
	private CustomTimerHandler playerIsSlideTimerHandler = 
			new CustomTimerHandler(0.5f, new ITimerCallback() {
		@Override
		public void onTimePassed(TimerHandler pTimerHandler) {
			// 元に戻す
			playerDefense.setAlpha(0.0f);
			setPlayerToDefaultPosition();
			isTouchEnabled = true;
			isSlideing = false;
			
			unregisterCustomUpdateHandler(playerIsSlideTimerHandler);
		}
	});
	
	/**
	 * プレイヤー死亡時ハンドラー.
	 */
	private CustomTimerHandler playerIsDeadTimerHandler = 
			new CustomTimerHandler(1.0f, new ITimerCallback() {
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
			registerCustomUpdateHandler(playerIsFadeClearTimerHandler);
			
			unregisterCustomUpdateHandler(playerIsDeadTimerHandler);
		}
	});
	
	/**
	 * プレイヤー無敵状態の解除.
	 */
	private CustomTimerHandler playerIsFadeClearTimerHandler = 
			new CustomTimerHandler(2.0f, new ITimerCallback() {
		@Override
		public void onTimePassed(TimerHandler pTimerHandler) {
			isRecovering = false;
			
			unregisterCustomUpdateHandler(playerIsFadeClearTimerHandler);
		}
	});
	
	// --------------------------------------------------
	// プレイヤー関連メソッド
	// --------------------------------------------------
		
	/**
	 * 敵攻撃.
	 * @param enemyObstacleType 敵オブジェクトTag
	 */
	public void enemyAttackSprite(ObstacleType enemyObstacleType) {
		
		// プレイヤーの歩行を停止
		player.stopAnimation();
		player.setAlpha(0.0f);
		
		// 倒れ画像にする
		playerDefense.stopAnimation();
		playerDefense.setCurrentTileIndex(11);
		playerDefense.setPosition(player.getX(), player.getY());
		playerDefense.setAlpha(1.0f);

		// 攻撃を食らう効果音
		enemyAttackSound.play();
		
		// 死亡
		isDeading = true;
					
		// ライフを減らす
		lifeSpriteArray.get(life - 1).detachSelf();
		life--;
		
		// ライフが0ならゲームオーバー
		if (life == 0) {
			// 画面止める
			pauseGame();
			// ゲームオーバー効果音
			gameOverSound.play();
			// ゲームオーバー画面表示
			showGameOver();
		}
		
		registerCustomUpdateHandler(playerIsDeadTimerHandler);
	}
	
	public void addLifeSprite() {
		if (life < 3) {
			// 効果音再生
			recoverySound.play();
			
			life++;
			attachChild(lifeSpriteArray.get(life - 1));
			lifeSpriteArray.get(life - 1).registerEntityModifier(
					new LoopEntityModifier(new SequenceEntityModifier(
					new ScaleModifier(0.25f, 0.6f, 1.3f),
					new ScaleModifier(0.25f, 1.3f, 0.6f)), 4));
		}
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
		
		// 攻撃効果音
		playerAttackSound.play();
		
		// 攻撃終了時ハンドラー設定
		registerCustomUpdateHandler(playerIsAttackTimerHandler);
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
		registerCustomUpdateHandler(playerIsJumpTimerHandler);
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
		// スライディング終了時ハンドラー設定
		registerCustomUpdateHandler(playerIsSlideTimerHandler);
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
		registerCustomUpdateHandler(weaponIsTimerHandler);
		// エフェクト
		registerCustomUpdateHandler(weaponIsEffectTimerHandler);
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
		playerDefense.registerEntityModifier(new MoveModifier(0.2f, 
				playerDefense.getX(), playerDefense.getX(), 
				playerDefense.getY(), playerDefense.getY() - 100));
	}
				
	/**
	 * プレイヤースライディングポジション設定.
	 */
	public void setPlayerToSlidePositon() {
		playerDefense.animate(
				new long[]{200, 100}, 
				new int[]{0, 11}, 
				false);
		playerDefense.setPosition(player.getX(), player.getY());
		playerDefense.setAlpha(1.0f);
		// 前に倒れこむ感じのアニメーション
		playerDefense.registerEntityModifier(new MoveModifier(0.3f, 
				playerDefense.getX(), playerDefense.getX() + 70, 
				playerDefense.getY(), playerDefense.getY()));
	}
	
	// --------------------------------------------------
	// 汎用メソッド
	// --------------------------------------------------
		
	/**
	 * メニューボタンの配置.
	 * @param baseEntity   配置先
	 * @param tag          押下時のメニュー判断用のタグ
	 * @param buttonSprite 配置するボタン
	 * @param y            配置するY座標
	 * @param listener     押下時のイベントリスナー
	 */
	private void attachMenuButton(final IEntity baseEntity, int tag, 
			ButtonSprite buttonSprite, int y, final OnClickListener listener) {
		placeToCenterX(buttonSprite, y);
		buttonSprite.setTag(tag);
		buttonSprite.setOnClickListener(listener);
		baseEntity.attachChild(buttonSprite);
		registerTouchArea(buttonSprite);
	}
	
	/**
	 * 障害物Sprite生成.
	 * @param obstacleType 障害物タイプ
	 * @return 障害物Sprite
	 */
	private Sprite makeObstacleSprite(ObstacleType obstacleType) {
		
		// 縦横が設定されていない場合は通常Spriteとする
		if (obstacleType.getColumn() == 0 && obstacleType.getRow() == 0) {
			Sprite obstacle = getResourceSprite(obstacleType.getFileName());
			obstacle.setPosition(
					getWindowWidth() + obstacle.getWidth() + obstacleType.getX(), 
					obstacleType.getY());
			obstacle.setTag(obstacleType.getValue());
			return obstacle;
		} else {
			AnimatedSprite animatedObstacle = getResourceAnimatedSprite(obstacleType.getFileName(), 
					obstacleType.getColumn(), obstacleType.getRow());
			animatedObstacle.setPosition(
					getWindowWidth() + animatedObstacle.getWidth() + obstacleType.getX(), 
					obstacleType.getY());
			animatedObstacle.setTag(obstacleType.getValue());
			animatedObstacle.animate(obstacleType.getDuration());
			return animatedObstacle;
		}
	}
	
	/**
	 * カスタムハンドラー登録.
	 * @param customTimerHandler
	 */
	public void registerCustomUpdateHandler(CustomTimerHandler customTimerHandler) {
		super.registerUpdateHandler(customTimerHandler);
		updateHandlerList.add(customTimerHandler);
	}
	/**
	 * カスタムハンドラー削除.
	 * @param customTimerHandler
	 */
	public void unregisterCustomUpdateHandler(CustomTimerHandler customTimerHandler) {
		customTimerHandler.reset();
		super.unregisterUpdateHandler(customTimerHandler);
		updateHandlerList.remove(customTimerHandler);
	}
}

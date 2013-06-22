package com.kyokomi.srpgquest.scene.part;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.andengine.entity.IEntity;
import org.andengine.entity.modifier.DelayModifier;
import org.andengine.entity.modifier.IEntityModifier;
import org.andengine.entity.modifier.MoveModifier;
import org.andengine.entity.modifier.ParallelEntityModifier;
import org.andengine.entity.modifier.ScaleModifier;
import org.andengine.entity.modifier.SequenceEntityModifier;
import org.andengine.entity.primitive.Rectangle;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.shape.IAreaShape;
import org.andengine.entity.sprite.AnimatedSprite;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.text.Text;
import org.andengine.input.touch.TouchEvent;
import org.andengine.opengl.font.Font;
import org.andengine.util.color.Color;
import org.andengine.util.modifier.IModifier;
import org.andengine.util.modifier.ease.EaseBackInOut;

import android.graphics.Typeface;
import android.util.Log;

import com.kyokomi.core.dto.ActorPlayerDto;
import com.kyokomi.core.dto.SaveDataDto;
import com.kyokomi.core.sprite.TextButton;
import com.kyokomi.srpgquest.constant.BattleActorType;
import com.kyokomi.srpgquest.constant.BattleMenuType;
import com.kyokomi.srpgquest.constant.LayerZIndexType;
import com.kyokomi.srpgquest.constant.MoveDirectionType;
import com.kyokomi.srpgquest.dto.BattleSelectDto;
import com.kyokomi.srpgquest.layer.TextCutInTouchLayer;
import com.kyokomi.srpgquest.logic.BattleLogic;
import com.kyokomi.srpgquest.scene.SrpgBaseScene;
import com.kyokomi.srpgquest.sprite.ActorSprite;

public class BattlePart extends AbstractGamePart {
	// ==================================================
	// TAG
	// ==================================================
	
	private static final int DAMAGE_TEXT_TAG = 1000;
	private static final int TARGET_CURSOR_TAG = 2000;
	
	private static final int BATTLE_MENU_TAG = 10000;
	
	private static final int BATTLE_START_CUTIN_TAG = 20000;
	private static final int BATTLE_END_CUTIN_TAG = 20001;
	
	// ==================================================
	
	private Rectangle mBaseLayer;
	
	private static int TURN_COUNT_LIMIT = 1;
	private int mTurnCount = 0;
	
	public enum BattleInitType {
		PLAYER_ATTACK(10),
		ENEMY_ATTACK(20)
		;
		private Integer value;

		private BattleInitType(Integer value) {
			this.value = value;
		}
		
		public Integer getValue() {
			return value;
		}
		public static BattleInitType get(Integer value) {
			BattleInitType[] values = values();
			for (BattleInitType type : values) {
				if (type.getValue() == value) {
					return type;
				}
			}
			throw new RuntimeException("find not tag type.");
		}
	}
	public enum BattleStateType {
		INIT(0),
		START(1),
		
		PLAYER_TURN(1000),
		PLAYER_TURN_TARGET_SELECT(1001),
		
		ENEMY_TURN(2000),
		
		BATTLE_START(3000),
		BATTLE_SELECT(4000),
		BATTLE_ANIMATION(5000),
		BATTLE_END(6000),
		
		END(9000),
		EXIT(9999)
		;
		private Integer value;

		private BattleStateType(Integer value) {
			this.value = value;
		}
		
		public Integer getValue() {
			return value;
		}
		public static BattleStateType get(Integer value) {
			BattleStateType[] values = values();
			for (BattleStateType type : values) {
				if (type.getValue() == value) {
					return type;
				}
			}
			throw new RuntimeException("find not tag type.");
		}
	}
	private BattleStateType mBattleState;
	public BattleStateType getBattleState() {
		return mBattleState;
	}
	private BattleInitType mBattleInitType;
	private List<ActorPlayerDto> mPlayerList;
	private List<ActorPlayerDto> mEnemyList;
	
	private BattleSelectDto mTempSelect;
	private List<BattleSelectDto> mBattleSelectList;
	
	// ==================================================
	// ボタンイベント
	// ==================================================
	private TextButton.OnClickListener mBattleMenuOnClickListener = new TextButton.OnClickListener() {
		@Override
		public void onClick(TextButton pTextButtonSprite, float pTouchAreaLocalX,
				float pTouchAreaLocalY) {
			Log.d("showBattleMenuLayer", "onClick");

			// メニュー閉じる
			hideBattleMenuLayer();
			
			if (pTextButtonSprite.getTag() == BattleMenuType.ATTACK.getValue()) {
				mTempSelect.setBattleMenuType(BattleMenuType.ATTACK);
				// ターゲット選択
				changeState(BattleStateType.PLAYER_TURN_TARGET_SELECT);
				
			} else if (pTextButtonSprite.getTag() == BattleMenuType.DEFENCE.getValue()) {
				// 行動確定
				// TODO: あとで実装
				// とりあえず攻撃
				mTempSelect.setBattleMenuType(BattleMenuType.ATTACK);
				changeState(BattleStateType.PLAYER_TURN_TARGET_SELECT);
				
			} else if (pTextButtonSprite.getTag() == BattleMenuType.SKILL.getValue()) {
				// スキルウィンドウ表示
				// TODO: あとで実装
				// とりあえず攻撃
				mTempSelect.setBattleMenuType(BattleMenuType.ATTACK);
				changeState(BattleStateType.PLAYER_TURN_TARGET_SELECT);
				
			} else if (pTextButtonSprite.getTag() == BattleMenuType.ITEM.getValue()) {
				// アイテムウィンドウ表示
				// TODO: あとで実装
				// とりあえず攻撃
				mTempSelect.setBattleMenuType(BattleMenuType.ATTACK);
				changeState(BattleStateType.PLAYER_TURN_TARGET_SELECT);
			}
		}
	};
	
	// ==================================================
	// コンストラクタ
	// ==================================================
	public BattlePart(SrpgBaseScene pBaseScene) {
		super(pBaseScene);
	}

	// ==================================================
	// メソッド
	// ==================================================
	/**
	 * @deprecated init(ActorPlayerDto player, ActorPlayerDto enemy)使って下さい
	 * @param saveDataDto
	 */
	@Override
	public void init(SaveDataDto saveDataDto) {
		
	}
	public void init(ActorPlayerDto player, ActorPlayerDto enemy, BattleInitType pBattleInitType) {
		Log.d("BattlePart", player.toString());
		Log.d("BattlePart", enemy.toString());
		
		changeState(BattleStateType.INIT);

		mBattleInitType = pBattleInitType;
		mPlayerList = new ArrayList<ActorPlayerDto>();
		mEnemyList = new ArrayList<ActorPlayerDto>();
		mPlayerList.add(player);
		mEnemyList.add(enemy);
		
		// 上に重ねる用にBaseを用意
		mBaseLayer = new Rectangle(0, 0, 
				getBaseScene().getWindowWidth(), getBaseScene().getWindowHeight(), 
				getBaseScene().getBaseActivity().getVertexBufferObjectManager());
		mBaseLayer.setColor(Color.TRANSPARENT);
		
		initDamageText(mBaseLayer);
		
		// 背景表示
		initBackground();
		// 背景画像の都合で表示位置が決まる
		float acotrBaseY = getBaseScene().getWindowHeight() / 2;
		
		// キャラ表示
		ActorSprite playerSprite = new ActorSprite(player, getBaseScene(), 0, 0, 64, 64);
		playerSprite.setSize(64, 64);
		playerSprite.setPlayerDirection(MoveDirectionType.MOVE_LEFT);
		playerSprite.setTag(player.getPlayerId());
		// 右上から表示
		playerSprite.setPosition(getBaseScene().getWindowWidth() - 
				(getBaseScene().getWindowWidth() / 8) -
				playerSprite.getWidth(), 
				acotrBaseY);
		mBaseLayer.attachChild(playerSprite);
		
		// キャラ表示
		ActorSprite enemySprite = new ActorSprite(enemy, getBaseScene(), 0, 0, 64, 64);
		enemySprite.setSize(64, 64);
		enemySprite.setPlayerDirection(MoveDirectionType.MOVE_DOWN);
		enemySprite.setTag(enemy.getPlayerId());
		// 左上から表示
		enemySprite.setPosition(getBaseScene().getWindowWidth() / 8, 
				acotrBaseY);
		mBaseLayer.attachChild(enemySprite);
		
		// ベースレイヤをattach
		getBaseScene().attachChild(mBaseLayer);
		mBaseLayer.sortChildren();
		
		changeState(BattleStateType.START);
	}
	
	/**
	 * 背景表示.
	 */
	private void initBackground() {
		Sprite backgroundSprite = getBaseScene().getResourceSprite("bk/main_bg.jpg");
		backgroundSprite.setSize(getBaseScene().getWindowWidth(), getBaseScene().getWindowHeight());
		backgroundSprite.setZIndex(-1);
		mBaseLayer.attachChild(backgroundSprite);
	}
	
	@Override
	public void touchEvent(Scene pScene, TouchEvent pSceneTouchEvent) {
		float x = pSceneTouchEvent.getX();
		float y = pSceneTouchEvent.getY();
		Log.d("touchEvent", "touch");
		
		if (pSceneTouchEvent.isActionUp()) {
			Log.d("touchEvent", "up mBattleState = " + mBattleState);
			
			if (mBattleState == BattleStateType.START) {
				// カットインを非表示
				TextCutInTouchLayer battleStartLayer = (TextCutInTouchLayer) mBaseLayer.getChildByTag(BATTLE_START_CUTIN_TAG);
				battleStartLayer.hideTouchLayer();
				
				// バトル開始
				changeState(BattleStateType.PLAYER_TURN);
				
			} else if (mBattleState == BattleStateType.END) {
				// カットインを非表示
				TextCutInTouchLayer battleEndLayer = (TextCutInTouchLayer) mBaseLayer.getChildByTag(BATTLE_END_CUTIN_TAG);
				battleEndLayer.hideTouchLayer();
				
				// バトルパート終了
				end();
				
			} else if (mBattleState == BattleStateType.PLAYER_TURN_TARGET_SELECT) {
				for (ActorPlayerDto enemyDto : mEnemyList) {
					if (enemyDto.getHitPoint() <= 0) {
						continue;
					} else {
						AnimatedSprite acotorSprite = findActorSprite(enemyDto.getPlayerId()).getPlayer();
						if (acotorSprite.contains(x, y)) {
							Log.d("touchEvent", "target select end");
							// タッチした時
							// 攻撃対象決定
							mTempSelect.setTargetDto(enemyDto);
							mBattleSelectList.add(mTempSelect);
							
							// ターゲットカーソルを消す
							hideTargetCursor();
							
							changeState(BattleStateType.PLAYER_TURN);
							break;
						}
					}
				}
			}
		}
	}
	
	private void hideBattleMenuLayer() {
		Rectangle battleMenu = (Rectangle) mBaseLayer.getChildByTag(BATTLE_MENU_TAG);
		if (battleMenu != null) {
			battleMenu.setVisible(false);
			battleMenu.setPosition(getBaseScene().getWindowWidth() * 4, 
					getBaseScene().getWindowHeight() * 4);
		}
	}
	private boolean showBattleMenuLayer(float x, float y) {
		if (mBaseLayer.getChildByTag(BATTLE_MENU_TAG) != null) {
			if (mBaseLayer.getChildByTag(BATTLE_MENU_TAG).isVisible()) {
				return false;
			} else {
				mBaseLayer.getChildByTag(BATTLE_MENU_TAG).setVisible(true);
				float menuWidth = ((Rectangle) mBaseLayer.getChildByTag(BATTLE_MENU_TAG)).getWidth();
				float menuHeight = ((Rectangle) mBaseLayer.getChildByTag(BATTLE_MENU_TAG)).getHeight();
				mBaseLayer.getChildByTag(BATTLE_MENU_TAG).setPosition(x - menuWidth / 2, y - menuHeight / 2);
				return true;
			}
		}
		
		Rectangle battleMenuLayer = new Rectangle(
				getBaseScene().getWindowWidth()/ 2, 
				getBaseScene().getWindowHeight() / 2, 
				getBaseScene().getWindowWidth() / 4, 
				getBaseScene().getWindowHeight() / 2, 
				getBaseScene().getBaseActivity().getVertexBufferObjectManager());
		battleMenuLayer.setColor(Color.TRANSPARENT);
		
		Font menuFont = getBaseScene().createFont(Typeface.DEFAULT_BOLD, 20, Color.WHITE);
		List<TextButton> textButtonList = new ArrayList<TextButton>();
		float sizeX = 0;
		float sizeY = 0;
		for (BattleMenuType menu : BattleMenuType.values()) {
			Text text = new Text(0, 0, menuFont, "********", 
					getBaseScene().getBaseActivity().getVertexBufferObjectManager());
			text.setText(menu.getText());
			if (sizeX == 0 && sizeY == 0) {
				sizeX = text.getWidth();
				sizeY = text.getHeight();
			} else {
				text.setSize(sizeX, sizeY);
			}
			TextButton textButton = new TextButton(text, 0, 0, 80, 30, 
					getBaseScene().getBaseActivity().getVertexBufferObjectManager(), 
					mBattleMenuOnClickListener);
			textButton.setTag(menu.getValue());
			textButtonList.add(textButton);
		}
		float startX = 0;
		float startY = 0;
		float addY = 0;
		float addX = 0;
		int index = 0;
		for (TextButton textButton : textButtonList) {
			getBaseScene().registerTouchArea(textButton);
			battleMenuLayer.attachChild(textButton);
			if (index == 0) {
				textButton.setX(startX);
				textButton.setY(startY);
				addX = textButton.getWidth();
				addY = textButton.getHeight();
			} else if (index == 1) {
				textButton.setX(startX + addX);
				textButton.setY(startY);
			} else if (index == 2) {
				textButton.setX(startX);
				textButton.setY(startY + addY);
			} else if (index == 3) {
				textButton.setX(startX + addX);
				textButton.setY(startY + addY);
			}
			index++;
		}
		battleMenuLayer.setSize(addX * 2, addY * 2);
		battleMenuLayer.setTag(BATTLE_MENU_TAG);
		battleMenuLayer.setPosition(x - battleMenuLayer.getWidth(), 
				y - battleMenuLayer.getHeight());
		mBaseLayer.attachChild(battleMenuLayer);
		
		return true;
	}
	
	@Override
	public void end() {
		changeState(BattleStateType.EXIT);
		
		if (mBaseLayer != null) {
			getBaseScene().detachEntity(mBaseLayer);
		}
//		
//		// ここだけ仕方ない...
//		((MainScene)getBaseScene()).endBattlePart();
	}
	
	// ==================================================
	// インラインメソッド
	// ==================================================
	/**
	 * 状態変更
	 * @param pBattleStateType
	 */
	private void changeState(BattleStateType pBattleStateType) {
		Log.d("BattlePart", "battleState [" + this.mBattleState + "] -> [" + pBattleStateType + "]");
		BattleStateType beforeStateType = this.mBattleState;
		this.mBattleState = pBattleStateType;
		
		// 開始時カットイン
		if (pBattleStateType == BattleStateType.START) {
		
			TextCutInTouchLayer battleStartLayer = new TextCutInTouchLayer(getBaseScene(), "バトル開始");
			battleStartLayer.setTag(BATTLE_START_CUTIN_TAG);
			mBaseLayer.attachChild(battleStartLayer);
			// 表示
			battleStartLayer.showTouchLayer();
			
		// 最初またはバトルフェイズ後にプレイヤーターンになったとき
		} else if (pBattleStateType == BattleStateType.PLAYER_TURN) {
			// バトル終了か開始から来た場合
			if ((beforeStateType == BattleStateType.BATTLE_END || beforeStateType == BattleStateType.START)) {
				// 初期化
				mBattleSelectList = new ArrayList<BattleSelectDto>();
			}
			
			ActorPlayerDto player = null;
			for (ActorPlayerDto playerDto : mPlayerList) {
				if (playerDto.getHitPoint() <= 0) {
					continue;
				} else {
					// 攻撃用意していない場合が対象
					boolean isSelect = true;
					for (BattleSelectDto battleSelectDto  : mBattleSelectList) {
						if (battleSelectDto.getActorPlayerDto().getPlayerId().intValue() == 
								playerDto.getPlayerId().intValue()) {
							isSelect = false;
							break;
						}
					}
					if (isSelect) {
						player = playerDto;
						break;	
					}
				}
			}
			if (player != null) {
				Log.d("BattlePart", "playerId = " + player.getPlayerId());
				AnimatedSprite playerSprite = findActorSprite(player.getPlayerId()).getPlayer();
				mTempSelect = new BattleSelectDto();
				mTempSelect.setBattleActorType(BattleActorType.PLAYER);
				mTempSelect.setActorPlayerDto(player);
				mTempSelect.setAction(false);
				// プレイヤーの攻撃ウィンドウを表示
				showBattleMenuLayer(playerSprite.getX(), playerSprite.getY());
			} else {
				// 敵行動選択へ
				changeState(BattleStateType.ENEMY_TURN);
			}
				
		} else if (pBattleStateType == BattleStateType.PLAYER_TURN_TARGET_SELECT) {
			// 攻撃可能な敵にターゲットカーソルを表示
			for (ActorPlayerDto enemyDto : mEnemyList) {
				if (enemyDto.getHitPoint() <= 0) {
					continue;
				} else {
					// カーソル表示
					showTargetCursor(enemyDto);
				}
			}
		} else if (pBattleStateType == BattleStateType.ENEMY_TURN) {
			BattleSelectDto battleSelect = new BattleSelectDto();
			for (ActorPlayerDto enemyDto : mEnemyList) {
				if (enemyDto.getHitPoint() <= 0) {
					continue;
				} else {
					// TODO: 攻撃対象をHPの量とか強さで判断するようにする
					battleSelect.setActorPlayerDto(enemyDto);
					battleSelect.setAction(false);
					battleSelect.setBattleActorType(BattleActorType.ENEMY);
					battleSelect.setBattleMenuType(BattleMenuType.ATTACK);
					// 攻撃対象を選択
					for (ActorPlayerDto playerDto : mPlayerList) {
						if (playerDto.getHitPoint() <= 0) {
							continue;
						} else {
							// 攻撃対象を選択
							battleSelect.setTargetDto(playerDto);
							break;
						}
					}
					mBattleSelectList.add(battleSelect);
				}
			}
			// バトル開始
			changeState(BattleStateType.BATTLE_START);
		} else if (pBattleStateType == BattleStateType.BATTLE_START) {
			// TODO: 素早い順に並び替える
			
			// TODO: とりあえず攻撃しかけた順にする
			final BattleActorType firstAttackActorType;
			if (mBattleInitType == BattleInitType.PLAYER_ATTACK) {
				firstAttackActorType = BattleActorType.PLAYER;
			} else if (mBattleInitType == BattleInitType.ENEMY_ATTACK) {
				firstAttackActorType = BattleActorType.ENEMY;
			} else {
				firstAttackActorType = null;
			}
			if (firstAttackActorType != null) {
				Collections.sort(mBattleSelectList, new Comparator<BattleSelectDto>() {
					@Override
					public int compare(BattleSelectDto p1, BattleSelectDto p2) {
						Log.d("sort", "p1 = " + p1.getBattleActorType() + " p2 = " + p2.getBattleActorType() + " first = " + firstAttackActorType );
						if (p1.getBattleActorType() == p2.getBattleActorType()) {
							return 0;
						} else if (p1.getBattleActorType() == firstAttackActorType) {
							return -1;
						} else if (p2.getBattleActorType() == firstAttackActorType) {
							return 1;
						} else {
							return 0;
						}
					}
				});
			}
			
			changeState(BattleStateType.BATTLE_SELECT);
			
		} else if (pBattleStateType == BattleStateType.BATTLE_SELECT) {
			for (BattleSelectDto battleSelect : mBattleSelectList) {
				if (battleSelect.getActorPlayerDto().getHitPoint() <= 0) {
					// バトルフェイズに死亡した
					continue;
				}
				if (battleSelect.isAction()) {
					// 行動済み
					continue;
				}

				// 攻撃対象が死亡しているときは、攻撃対象をランダムに検索
				if (battleSelect.getTargetDto().getHitPoint() <= 0) {
					battleSelect.setTargetDto(null);
					List<ActorPlayerDto> targetList = new ArrayList<ActorPlayerDto>();
					if (battleSelect.getBattleActorType() == BattleActorType.PLAYER) {
						targetList = mEnemyList;
					} else if (battleSelect.getBattleActorType() == BattleActorType.ENEMY) {
						targetList = mPlayerList;
					} else {
						continue;
					}
					for (ActorPlayerDto target : targetList) {
						if (target.getHitPoint() <= 0) {
							continue;
						}
						battleSelect.setTargetDto(target);
						break;
					}
				}
				if (battleSelect.getTargetDto() == null) {
					// 攻撃可能な敵がいないので諦める
					continue;
				}
				BattleLogic battleLogic = new BattleLogic();
				int damage = battleLogic.attack(battleSelect.getActorPlayerDto(), battleSelect.getTargetDto());
				Log.d("battleLogic", battleSelect.getActorPlayerDto().toString());
				Log.d("battleLogic", battleSelect.getTargetDto().toString());
				
				// 攻撃アニメーション開始
				changeState(BattleStateType.BATTLE_ANIMATION);
				attackAnimation(battleSelect.getActorPlayerDto(), battleSelect.getTargetDto(), damage);

				// 行動済みにして一旦抜ける（アニメーションのコールバックで改めてBATTLE_SELECTモードに移行する
				battleSelect.setAction(true);
				break;
			}
			
			// アニメーション中はバトル終了はしない
			if (mBattleState != BattleStateType.BATTLE_ANIMATION) {
				changeState(BattleStateType.BATTLE_END);				
			}
			
		} else if (pBattleStateType == BattleStateType.BATTLE_END) {
			mTurnCount++;
			if (mTurnCount < TURN_COUNT_LIMIT) {
				// バトル継続
				changeState(BattleStateType.PLAYER_TURN);
			} else {
				// バトル終了
				changeState(BattleStateType.END);
			}
		} else if (pBattleStateType == BattleStateType.END) {
			// バトルパート終了
			TextCutInTouchLayer battleEndLayer = new TextCutInTouchLayer(getBaseScene(), "バトル終了");
			battleEndLayer.setTag(BATTLE_END_CUTIN_TAG);
			mBaseLayer.attachChild(battleEndLayer);
			// 表示
			battleEndLayer.showTouchLayer();
			
		} else if (pBattleStateType == BattleStateType.EXIT) {
			// 終わり
		}
	}
	
	private void attackAnimation(final ActorPlayerDto attackFrom, final ActorPlayerDto attackTo, final int damage) {
		
		final AnimatedSprite attackFromSprite = findActorSprite(attackFrom.getPlayerId()).getPlayer();
		final AnimatedSprite attackToSprite = findActorSprite(attackTo.getPlayerId()).getPlayer();
		
		// 移動方向を算出
		float directionDiff_x = attackFromSprite.getX() - attackToSprite.getX();
		if (directionDiff_x > 0) {
			// 攻撃者が右側にいる
			directionDiff_x = 1;
		} else {
			// 攻撃者が左側にいる
			directionDiff_x = -1;
		}
		
		// 移動先を算出
		final float pFromX = attackFromSprite.getX();
		final float pToX = attackToSprite.getX() + (20 * directionDiff_x);
		final float pFromY = attackFromSprite.getY();
		final float pToY = attackToSprite.getY();
		attackFromSprite.registerEntityModifier(new SequenceEntityModifier(
			// 攻撃対象に向かって移動するアニメーション
			new MoveModifier(0.5f, pFromX, pToX, pFromY, pToY, 
					new IEntityModifier.IEntityModifierListener() {
				@Override
				public void onModifierStarted(IModifier<IEntity> pModifier, IEntity pItem) {
				}
				
				@Override
				public void onModifierFinished(IModifier<IEntity> pModifier, IEntity pItem) {
				}
			}),
			
			// TODO: 攻撃アニメーションとエフェクト表示
			
			// ダメージテキスト表示
			new DelayModifier(0.5f, new IEntityModifier.IEntityModifierListener() {
				
				@Override
				public void onModifierStarted(IModifier<IEntity> pModifier, IEntity pItem) {
					// ダメージテキスト表示
					showDamageText(damage, attackToSprite);
				}
				
				@Override
				public void onModifierFinished(IModifier<IEntity> pModifier, IEntity pItem) {
					// 死亡してたら死亡画像にする
					if (attackTo.getHitPoint() <= 0) {
						// TODO: 死亡アニメーションとりあえず消滅
						attackToSprite.setVisible(false);
					}
				}
			}),
			
			// 戻りアニメーション
			new MoveModifier(0.5f, pToX, pFromX, pToY, pFromY, 
					new IEntityModifier.IEntityModifierListener() {
				@Override
				public void onModifierStarted(IModifier<IEntity> pModifier, IEntity pItem) {
				}
				
				@Override
				public void onModifierFinished(IModifier<IEntity> pModifier, IEntity pItem) {
					// アニメーション終わり
					changeState(BattleStateType.BATTLE_SELECT);
				}
			})
		));
	}

	/**
	 * ダメージテキスト初期化
	 */
	private void initDamageText(IEntity entity) {
		Text damageText = new Text(0, 0, getBaseScene().getFont(), "00000", 
				getBaseScene().getBaseActivity().getVertexBufferObjectManager());
		damageText.setColor(Color.TRANSPARENT);
		damageText.setZIndex(LayerZIndexType.TEXT_LAYER.getValue());
		damageText.setTag(DAMAGE_TEXT_TAG);
		entity.attachChild(damageText);
	}
	
	/**
	 * ダメージテキスト表示.
	 */
	private void showDamageText(int damage, final IAreaShape areaShape) {
		final Text damageText = (Text) mBaseLayer.getChildByTag(DAMAGE_TEXT_TAG);
		
		damageText.setScale(0.5f);
		// 頭の上くらいに表示
		damageText.setX(areaShape.getX() + areaShape.getWidth() / 2);
		damageText.setY(areaShape.getY() - areaShape.getHeight() / 2);
		damageText.setText(String.valueOf(damage));
		damageText.setColor(Color.WHITE);
		
		damageText.registerEntityModifier(new SequenceEntityModifier(
			new ParallelEntityModifier(
				new ScaleModifier(0.5f, 0.5f, 2.0f, EaseBackInOut.getInstance()),
				new SequenceEntityModifier(
						new MoveModifier(0.25f, damageText.getX(), damageText.getX(), 
								damageText.getY(), damageText.getY() - 15, 
								EaseBackInOut.getInstance()),
						new MoveModifier(0.25f, damageText.getX(), damageText.getX(), 
								damageText.getY() - 15, damageText.getY(), 
								EaseBackInOut.getInstance()))
				),
			new DelayModifier(0.2f, new IEntityModifier.IEntityModifierListener() {
				@Override
				public void onModifierStarted(IModifier<IEntity> pModifier, IEntity pItem) {
				}
				@Override
				public void onModifierFinished(IModifier<IEntity> pModifier, IEntity pItem) {
					damageText.setColor(Color.TRANSPARENT);
				}
		})));
	}
	
	/**
	 * ターゲットカーソル表示。
	 * @param actorPlayerDto アクター情報
	 */
	private void showTargetCursor(ActorPlayerDto actorPlayerDto) {
		// TODO: ターゲットカーソルは後で画像を用意する
		AnimatedSprite actorSprite = findActorSprite(actorPlayerDto.getPlayerId()).getPlayer();
		Rectangle cursorRectangle = new Rectangle(actorSprite.getX(), actorSprite.getY(), 50, 50, 
				getBaseScene().getBaseActivity().getVertexBufferObjectManager());
		cursorRectangle.setColor(Color.YELLOW);
		cursorRectangle.setAlpha(0.5f);
		cursorRectangle.setTag(TARGET_CURSOR_TAG);
		mBaseLayer.attachChild(cursorRectangle);
	}
	/**
	 * ターゲットカーソル非表示。
	 */
	private void hideTargetCursor() {
		final Rectangle targetRectangle = (Rectangle) mBaseLayer.getChildByTag(TARGET_CURSOR_TAG);
		targetRectangle.detachSelf();
	}
	
	/**
	 * アクタースプライト検索
	 * @param actorId アクターID
	 * @return アクタースプライト
	 */
	private ActorSprite findActorSprite(int actorId) {
		return (ActorSprite) mBaseLayer.getChildByTag(actorId);
	}
}

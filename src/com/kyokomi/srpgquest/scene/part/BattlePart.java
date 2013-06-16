package com.kyokomi.srpgquest.scene.part;

import java.util.ArrayList;
import java.util.List;

import org.andengine.entity.primitive.Rectangle;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.sprite.AnimatedSprite;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.text.Text;
import org.andengine.input.touch.TouchEvent;
import org.andengine.opengl.font.Font;
import org.andengine.util.color.Color;

import android.graphics.Typeface;
import android.util.Log;

import com.kyokomi.core.dto.ActorPlayerDto;
import com.kyokomi.core.dto.SaveDataDto;
import com.kyokomi.core.sprite.TextButton;
import com.kyokomi.srpgquest.constant.BattleActorType;
import com.kyokomi.srpgquest.constant.BattleMenuType;
import com.kyokomi.srpgquest.dto.BattleSelectDto;
import com.kyokomi.srpgquest.scene.SrpgBaseScene;
import com.kyokomi.srpgquest.sprite.ActorSprite;

public class BattlePart extends AbstractGamePart {

	private Rectangle mBaseLayer;
	
	private static int TURN_COUNT_LIMIT = 1;
	private int mTurnCount = 0;
	
	public enum BattleStateType {
		START(0),
		
		PLAYER_TURN(1000),
		PLAYER_TURN_TARGET_SELECT(1001),
		
		ENEMY_TURN(2000),
		
		BATTLE_START(3000),
		BATTLE_SELECT(4000),
		BATTLE_ANIMATION(5000),
		BATTLE_END(6000),
		
		END(9000),
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
	private List<ActorPlayerDto> mPlayerList;
	private List<ActorPlayerDto> mEnemyList;
	
	private BattleSelectDto mTempSelect;
	private List<BattleSelectDto> mBattleSelectList;
	
	public BattlePart(SrpgBaseScene pBaseScene) {
		super(pBaseScene);
	}

	/**
	 * @deprecated init(ActorPlayerDto player, ActorPlayerDto enemy)使って下さい
	 * @param saveDataDto
	 */
	@Override
	public void init(SaveDataDto saveDataDto) {
		
	}
	public void init(ActorPlayerDto player, ActorPlayerDto enemy) {
		changeState(BattleStateType.START);
		
		mPlayerList = new ArrayList<ActorPlayerDto>();
		mEnemyList = new ArrayList<ActorPlayerDto>();
		mPlayerList.add(player);
		mEnemyList.add(enemy);
		
		// 上に重ねる用にBaseを用意
		mBaseLayer = new Rectangle(0, 0, 
				getBaseScene().getWindowWidth(), getBaseScene().getWindowHeight(), 
				getBaseScene().getBaseActivity().getVertexBufferObjectManager());
		mBaseLayer.setColor(Color.TRANSPARENT);
		
		// 背景表示
		initBackground();
		// 背景画像の都合で表示位置が決まる
		float acotrBaseY = getBaseScene().getWindowHeight() / 2;
		
		// キャラ表示
		AnimatedSprite playerSprite = getBaseScene().getResourceAnimatedSprite(
				ActorSprite.getMoveFileName(player.getImageResId()), 3, 4);
		playerSprite.setSize(64, 64);
		playerSprite.setTag(player.getPlayerId());
		// 右上から表示
		playerSprite.setPosition(getBaseScene().getWindowWidth() - 
				(getBaseScene().getWindowWidth() / 8) -
				playerSprite.getWidth(), 
				acotrBaseY);
		mBaseLayer.attachChild(playerSprite);
		
		// キャラ表示
		AnimatedSprite enemySprite = getBaseScene().getResourceAnimatedSprite(
				ActorSprite.getMoveFileName(enemy.getImageResId()), 3, 4);
		enemySprite.setSize(64, 64);
		enemySprite.setTag(enemy.getPlayerId());
		// 左上から表示
		enemySprite.setPosition(getBaseScene().getWindowWidth() / 8, 
				acotrBaseY);
		mBaseLayer.attachChild(enemySprite);
		
		// ベースレイヤをattach
		getBaseScene().attachChild(mBaseLayer);
		
		changeState(BattleStateType.PLAYER_TURN);
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
			if (mBattleState == BattleStateType.PLAYER_TURN_TARGET_SELECT) {
				for (ActorPlayerDto enemyDto : mEnemyList) {
					if (enemyDto.getHitPoint() <= 0) {
						continue;
					} else {
						AnimatedSprite acotorSprite = (AnimatedSprite) mBaseLayer.getChildByTag(enemyDto.getPlayerId());
						if (acotorSprite.contains(x, y)) {
							Log.d("touchEvent", "target select end");
							// タッチした時
							// 攻撃対象決定
							mTempSelect.setTargetDto(enemyDto);
							mBattleSelectList.add(mTempSelect);
							changeState(BattleStateType.PLAYER_TURN);
							break;
						}
					}
				}
			}
//			Log.d("touchEvent", "onClick");
//			if (showBattleMenuLayer(x, y) == false) {
//				Log.d("showBattleMenuLayer", "false");
//				mBaseLayer.getChildByTag(10000).setVisible(false);
//				mBaseLayer.getChildByTag(10000).setPosition(-1000, -1000);
//			} else {
//				Log.d("showBattleMenuLayer", "true");
//			}
		}
		//end();
	}
	
	
	public boolean showBattleMenuLayer(float x, float y) {
		if (mBaseLayer.getChildByTag(10000) != null) {
			if (mBaseLayer.getChildByTag(10000).isVisible()) {
				return false;	
			} else {
				mBaseLayer.getChildByTag(10000).setVisible(true);
				float menuWidth = ((Rectangle) mBaseLayer.getChildByTag(10000)).getWidth();
				float menuHeight = ((Rectangle) mBaseLayer.getChildByTag(10000)).getHeight();
				mBaseLayer.getChildByTag(10000).setPosition(x - menuWidth / 2, y - menuHeight / 2);
				return true;
			}
		}
		
		Rectangle battleMenuLayer = new Rectangle(getBaseScene().getWindowWidth()/ 2, getBaseScene().getWindowHeight() / 2, 
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
					battleMenuOnClickListener);
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
		battleMenuLayer.setTag(10000); // TODO: とりあえず
		battleMenuLayer.setPosition(x - battleMenuLayer.getWidth() / 2, 
				y - battleMenuLayer.getHeight() / 2);
		mBaseLayer.attachChild(battleMenuLayer);
		
		return true;
	}
	
	private TextButton.OnClickListener battleMenuOnClickListener = new TextButton.OnClickListener() {
		
		@Override
		public void onClick(TextButton pTextButtonSprite, float pTouchAreaLocalX,
				float pTouchAreaLocalY) {
			Log.d("showBattleMenuLayer", "onClick");
			
			if (pTextButtonSprite.getTag() == BattleMenuType.ATTACK.getValue()) {
				mTempSelect.setBattleMenuType(BattleMenuType.ATTACK);
				// ターゲット選択
				changeState(BattleStateType.PLAYER_TURN_TARGET_SELECT);
				
			} else if (pTextButtonSprite.getTag() == BattleMenuType.DEFENCE.getValue()) {
				// 行動確定
				// TODO: あとで実装
				
			} else if (pTextButtonSprite.getTag() == BattleMenuType.SKILL.getValue()) {
				// スキルウィンドウ表示
				// TODO: あとで実装
			} else if (pTextButtonSprite.getTag() == BattleMenuType.ITEM.getValue()) {
				// アイテムウィンドウ表示
				// TODO: あとで実装
			}
		}
	};

	@Override
	public void end() {
		if (mBaseLayer != null) {
			mBaseLayer.detachChildren();
			mBaseLayer.detachSelf();
			mBaseLayer = null;
		}
	}
	
	/**
	 * 状態変更
	 * @param pBattleStateType
	 */
	private void changeState(BattleStateType pBattleStateType) {
		Log.d("BattlePart", "battleState [" + this.mBattleState + "] -> [" + pBattleStateType + "]");
		BattleStateType beforeStateType = this.mBattleState;
		this.mBattleState = pBattleStateType;
		
		// 最初またはバトルフェイズ後にプレイヤーターンになったとき
		if (pBattleStateType == BattleStateType.PLAYER_TURN) {
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
				AnimatedSprite playerSprite = (AnimatedSprite) mBaseLayer.getChildByTag(player.getPlayerId());
				mTempSelect = new BattleSelectDto();
				mTempSelect.setBattleActorType(BattleActorType.PLAYER);
				mTempSelect.setActorPlayerDto(player);
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
			BattleSelectDto battleSelectDto = new BattleSelectDto();
			for (ActorPlayerDto enemyDto : mEnemyList) {
				if (enemyDto.getHitPoint() <= 0) {
					continue;
				} else {
					// TODO: 攻撃対象をHPの量とか強さで判断するようにする
					battleSelectDto.setActorPlayerDto(enemyDto);
					battleSelectDto.setBattleActorType(BattleActorType.PLAYER);
					battleSelectDto.setBattleMenuType(BattleMenuType.ATTACK);
					// 攻撃対象を選択
					for (ActorPlayerDto playerDto : mPlayerList) {
						if (playerDto.getHitPoint() <= 0) {
							continue;
						} else {
							// 攻撃対象を選択
							battleSelectDto.setTargetDto(playerDto);
							break;
						}
					}
					mBattleSelectList.add(battleSelectDto);
				}
			}
			// バトル開始
			changeState(BattleStateType.BATTLE_START);
		} else if (pBattleStateType == BattleStateType.BATTLE_START) {
			// TODO: 素早い順に並び替える
			changeState(BattleStateType.BATTLE_SELECT);
			
		} else if (pBattleStateType == BattleStateType.BATTLE_SELECT) {
			for (BattleSelectDto battleSelect : mBattleSelectList) {
				if (battleSelect.getActorPlayerDto().getHitPoint() <= 0) {
					// バトルフェイズに死亡した
					//mBattleSelectList.remove(battleSelect);
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
					//mBattleSelectList.remove(battleSelect);
					continue;
				}
				// TODO: 攻撃アニメーション開始
				//changeState(BattleStateType.BATTLE_ANIMATION);

				// TODO: 攻撃で倒した場合はHpが減ったりする
				
				// TODO: アニメーションが終わったらリストから削除
				//mBattleSelectList.remove(battleSelect);
			}
			changeState(BattleStateType.BATTLE_END);
			
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
			end();
		}
	}
	
	private void showTargetCursor(ActorPlayerDto actorPlayerDto) {
		AnimatedSprite actorSprite = (AnimatedSprite) mBaseLayer.getChildByTag(actorPlayerDto.getPlayerId());
		Rectangle cursorRectangle = new Rectangle(actorSprite.getX(), actorSprite.getY(), 50, 50, 
				getBaseScene().getBaseActivity().getVertexBufferObjectManager());
		cursorRectangle.setColor(Color.YELLOW);
		cursorRectangle.setAlpha(0.5f);
		mBaseLayer.attachChild(cursorRectangle);
	}
}

package com.kyokomi.core.sprite;

import java.util.ArrayList;
import java.util.List;

import org.andengine.entity.IEntity;
import org.andengine.entity.modifier.DelayModifier;
import org.andengine.entity.modifier.IEntityModifier;
import org.andengine.entity.modifier.MoveModifier;
import org.andengine.entity.modifier.ParallelEntityModifier;
import org.andengine.entity.modifier.SequenceEntityModifier;
import org.andengine.entity.primitive.Rectangle;
import org.andengine.entity.sprite.AnimatedSprite;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.sprite.TiledSprite;
import org.andengine.opengl.vbo.VertexBufferObjectManager;
import org.andengine.util.color.Color;
import org.andengine.util.modifier.IModifier;

import com.kyokomi.core.constants.PlayerSpriteType;
import com.kyokomi.core.scene.KeyListenScene;
import com.kyokomi.srpgquest.map.common.MapPoint;

public class PlayerSprite extends Rectangle {
	
	private Integer playerId;
	
	/**
	 * @return the playerId
	 */
	public Integer getPlayerId() {
		return playerId;
	}

	/**
	 * @param playerId the playerId to set
	 */
	public void setPlayerId(Integer playerId) {
		this.playerId = playerId;
	}

	public PlayerSprite(KeyListenScene baseScene, 
			float pX, float pY, float pWidth, float pHeight,
			int imageId, int tag, float scale,
			VertexBufferObjectManager pVertexBufferObjectManager) {
		super(pX, pY, pWidth, pHeight, pVertexBufferObjectManager);
		
		setColor(Color.TRANSPARENT);
		
		playerSpriteInit(baseScene, imageId, tag, pX, pY, scale);
	}
	
	/** 武器（アイコンセット）. */
	private AnimatedSprite weapon;
	/** 攻撃エフェクト. */
	private AnimatedSprite attackEffect;
	/** プレイヤーカットイン. */
	private Sprite playerCutIn;

	/** プレイヤーキャラクター. */
	private AnimatedSprite player;
	/** プレイヤーキャラクター(回避). */
	private AnimatedSprite playerDefense;
	/** プレイヤーキャラクター(攻撃). */
	private AnimatedSprite playerAttack;
	
	/** プレイヤー会話. */
	private TiledSprite playerFace;
	public TiledSprite getPlayerTalk() {
		return playerFace;
	}
	
	private void playerSpriteInit(KeyListenScene baseScene, int imageId, int playerId, float x, float y, float scale) {
		this.playerId = playerId;
		
		// playerキャラを追加 攻撃と防御のスプライトもセットで読み込んでおく
		String baseFileName = "actor" + imageId;
		player        = baseScene.getResourceAnimatedSprite(baseFileName + "_0_s.png", 3, 4);
		playerDefense = baseScene.getResourceAnimatedSprite(baseFileName + "_2_s.png", 3, 4);
		playerAttack  = baseScene.getResourceAnimatedSprite(baseFileName + "_3_s.png", 3, 4);
		playerCutIn   = baseScene.getResourceSprite(baseFileName + "_cutin_s.jpg");
		playerFace    = baseScene.getResourceTiledSprite(baseFileName + "_f.png", 4, 2);
		
		attachChild(player);
		attachChild(playerDefense);
		attachChild(playerAttack);

		// カットイン
		playerCutIn.setPosition(baseScene.getWindowWidth() / 2, 
				baseScene.getWindowHeight() / 2 - playerCutIn.getHeight() / 2);
		playerCutIn.setAlpha(0.0f);
		attachChild(playerCutIn);

		// デフォルト表示
		showPlayer(PlayerSpriteType.PLAYER_TYPE_DEFENSE);
		setPlayerScale(scale);
		setPlayerPosition(80, 200);
		playerDefense.setCurrentTileIndex(0);
		
		// 武器設定
		initWeaponAndEffect(baseScene, scale);
		
		// タグ設定
		setTag(playerId);
		playerFace.setTag(playerId);
	}

	/**
	 * 武器初期化.
	 * @param baseScene
	 * @param scale
	 */
	public void initWeaponAndEffect(KeyListenScene baseScene, float scale) {
		// アイコンオブジェクトを追加（プレイヤーの下に表示）
		weapon = baseScene.getResourceAnimatedSprite("icon_set.png", 16, 48);
		weapon.setScale(scale);
		weapon.setAlpha(0.0f);
		attachChild(weapon);
		
		// エフェクトオブジェクトを追加（武器の上に表示）
		attackEffect = baseScene.getResourceAnimatedSprite("effect002_b2_4.png", 5, 1);
		attackEffect.setAlpha(0.0f);
		attackEffect.setScale(scale);
		attachChild(attackEffect);
	}
	// ----------------------------------------------
	// Sprite設定系
	// ----------------------------------------------
	@Override
	public void setScale(float pScale) {
		setPlayerScale(pScale);
	}
	public void setPlayerScale(float scale) {
		player.setScale(scale);
		playerDefense.setScale(scale);
		playerAttack.setScale(scale);
	}
	@Override
	public void setPosition(IEntity pOtherEntity) {
		setPlayerPosition(pOtherEntity);
	}
	public void setPlayerPosition(IEntity pOtherEntity) {
		player.setPosition(pOtherEntity.getX(), pOtherEntity.getY());
		playerDefense.setPosition(pOtherEntity.getX(), pOtherEntity.getY());
		playerAttack.setPosition(pOtherEntity.getX(), pOtherEntity.getY());
	}
	@Override
	public void setPosition(float pX, float pY) {
		setPlayerPosition(pX, pY);
	}
	public void setPlayerPosition(float x, float y) {
		player.setPosition(x, y);
		playerDefense.setPosition(x, y);
		playerAttack.setPosition(x, y);
	}
	@Override
	public void setSize(float pWidth, float pHeight) {
		setPlayerSize(pWidth, pHeight);
	}
	public void setPlayerSize(float w, float h) {
		player.setSize(w, h);
		playerDefense.setSize(w, h);
		playerAttack.setSize(w, h);
	}
	
	/**
	 * 水平方向反転.
	 * @param pFlippedHorizontal
	 */
	public void setPlayerFlippedHorizontal(boolean pFlippedHorizontal) {
		player.setFlippedHorizontal(pFlippedHorizontal);
		playerDefense.setFlippedHorizontal(pFlippedHorizontal);
		playerAttack.setFlippedHorizontal(pFlippedHorizontal);
	}
	
//	@Override
//	public void setRotationCenterX(float pRotationCenterX) {
//		setPlayerRotationCenterX(pRotationCenterX);
//	}
//	public void setPlayerRotationCenterX(float pRotationCenterX) {
//		player.setRotationCenterX(pRotationCenterX);
//		playerDefense.setRotationCenterX(pRotationCenterX);
//		playerAttack.setRotationCenterX(pRotationCenterX);
//	}
	
	// ----------------------------------------------
	// 表示とか
	// ----------------------------------------------
	
	public void showPlayer(PlayerSpriteType playerType) {
		float normal = 0.0f;
		float attack = 0.0f;
		float defense = 0.0f;
		
		switch (playerType) {
		case PLAYER_TYPE_NORMAL:
			normal = 1.0f;
			break;
		case PLAYER_TYPE_ATTACK:
			attack = 1.0f;
			break;
		case PLAYER_TYPE_DEFENSE:
			defense = 1.0f;
			break;
		case PLAYER_TYPE_HIDE:
			break;
		default:
			break;
		}
		player.setAlpha(normal);
		playerAttack.setAlpha(attack);
		playerDefense.setAlpha(defense);
	}
	
	// ----------------------------------------------
	// 汎用モーション系
	// ----------------------------------------------
	/**
	 * 攻撃モーション再生(Ver.2).
	 * スケールとか解像度関係なくやれるようにする
	 */
	public void attack2() {
		attack2(null);
	}
	
	private void attackWeaponLeft() {
		weapon.setRotation(0f);
		weapon.setPosition(
				playerAttack.getX() - 
					weapon.getWidthScaled() + (weapon.getWidthScaled() / 4) + (weapon.getWidthScaled() / 10),
				playerAttack.getY() + 
					(playerAttack.getHeightScaled() / 2) + (playerAttack.getHeightScaled() / 4) - 
					weapon.getHeightScaled() 
					);
	}
	
	private void attackWeaponRight() {
		weapon.setRotation(100f);
		weapon.setPosition(
				playerAttack.getX() + playerAttack.getWidthScaled() - (weapon.getWidthScaled() / 10),
				playerAttack.getY() + (playerAttack.getHeightScaled() / 2) - (weapon.getHeightScaled()));
	}
	/**
	 * 攻撃モーション再生(Ver.2).
	 * スケールとか解像度関係なくやれるようにする
	 */
	public void attack2(IEntityModifier.IEntityModifierListener callBack) {
		
		showPlayer(PlayerSpriteType.PLAYER_TYPE_ATTACK);
		
		// 攻撃
		setPlayerToAttackPosition();
		playerAttack.registerEntityModifier(new ParallelEntityModifier(
			// 武器
			new DelayModifier(0.3f, new IEntityModifier.IEntityModifierListener() {
				@Override public void onModifierStarted(IModifier<IEntity> pModifier, IEntity pItem) {
					weapon.setCurrentTileIndex(16 * 16 + 5);
					attackWeaponLeft();					
					weapon.setAlpha(1.0f);
				}
				@Override
				public void onModifierFinished(IModifier<IEntity> pModifier, IEntity pItem) {
					attackWeaponRight();
				}
			}),
			// エフェクト
			new DelayModifier(0.2f, new IEntityModifier.IEntityModifierListener() {
				@Override public void onModifierStarted(IModifier<IEntity> pModifier, IEntity pItem) {
					attackEffect.setCurrentTileIndex(0);
					attackEffect.setPosition(
							playerAttack.getX() + attackEffect.getWidthScaled(),
							playerAttack.getY());
					attackEffect.setAlpha(1.0f);
				}
				@Override
				public void onModifierFinished(IModifier<IEntity> pModifier, IEntity pItem) {
					attackEffect.animate(
							new long[]{100, 100, 100}, 
							new int[]{4, 3, 2}, 
							false);
				}
			}),
			new SequenceEntityModifier(
				// 全体
				new DelayModifier(0.5f, new IEntityModifier.IEntityModifierListener() {
					@Override public void onModifierStarted(IModifier<IEntity> pModifier, IEntity pItem) {}
					@Override
					public void onModifierFinished(IModifier<IEntity> pModifier, IEntity pItem) {
						weapon.setAlpha(0.0f);
						attackEffect.setAlpha(0.0f);
						// デフォルトに戻す
						showPlayer(PlayerSpriteType.PLAYER_TYPE_DEFENSE);
						playerDefense.setCurrentTileIndex(0);
						
					}
				}),
				// CallBack
				new DelayModifier(0.0f, callBack))
		));
	}
	/**
	 * 攻撃モーション再生.
	 * @deprecated scaleを変えたりするとずれるので廃止予定
	 */
	public void attack() {
		
		showPlayer(PlayerSpriteType.PLAYER_TYPE_ATTACK);
		
		// 攻撃
		setPlayerToAttackPosition();
		playerAttack.setFlippedHorizontal(true);
		playerAttack.registerEntityModifier(new ParallelEntityModifier(
			// 武器
			new DelayModifier(0.3f, new IEntityModifier.IEntityModifierListener() {
				@Override public void onModifierStarted(IModifier<IEntity> pModifier, IEntity pItem) {
					weapon.setCurrentTileIndex(16 * 16 + 5);
					weapon.setRotation(0f);
					weapon.setPosition(playerAttack.getX() - 30, playerAttack.getY());
					weapon.setAlpha(1.0f);
				}
				@Override
				public void onModifierFinished(IModifier<IEntity> pModifier, IEntity pItem) {
					weapon.setRotation(100f);
					weapon.setPosition(playerAttack.getX() + 50, playerAttack.getY() - 10);
				}
			}),
			// エフェクト
			new DelayModifier(0.2f, new IEntityModifier.IEntityModifierListener() {
				@Override public void onModifierStarted(IModifier<IEntity> pModifier, IEntity pItem) {
					attackEffect.setCurrentTileIndex(0);
					attackEffect.setAlpha(1.0f);
				}
				@Override
				public void onModifierFinished(IModifier<IEntity> pModifier, IEntity pItem) {
					attackEffect.animate(
							new long[]{100, 100, 100}, 
							new int[]{4, 3, 2}, 
							false);
					attackEffect.setPosition(playerAttack.getX(), playerAttack.getY() - 70);
				}
			}),
			// 全体
			new DelayModifier(0.5f, new IEntityModifier.IEntityModifierListener() {
				@Override public void onModifierStarted(IModifier<IEntity> pModifier, IEntity pItem) {}
				@Override
				public void onModifierFinished(IModifier<IEntity> pModifier, IEntity pItem) {
					weapon.setAlpha(0.0f);
					attackEffect.setAlpha(0.0f);
					// デフォルトに戻す
					showPlayer(PlayerSpriteType.PLAYER_TYPE_DEFENSE);
					playerDefense.setCurrentTileIndex(0);
				}
			})
		));
	}

	/**
	 * プレイヤー移動.
	 * @param duration 移動時間
	 * @param moveMapPointList 移動経路リスト
	 */
	public void move(float duration, List<MapPoint> moveMapPointList) {
		setPlayerToDefaultPosition();
		List<IEntityModifier> modifierList = new ArrayList<IEntityModifier>();
		// TODO: durationをlistの件数で割るべきか？トータルの移動時間を一定にするか？
		float moveStartX = player.getX();
		float moveStartY = player.getY();
		for (MapPoint mapPoint : moveMapPointList) {
			modifierList.add(new MoveModifier(duration, moveStartX, mapPoint.getX(), moveStartY, mapPoint.getY()));
			moveStartX = mapPoint.getX();
			moveStartY = mapPoint.getY();
		}
		SequenceEntityModifier sequenceEntityModifier  = new SequenceEntityModifier(
				modifierList.toArray(new IEntityModifier[0]));
		// 移動
		player.registerEntityModifier(sequenceEntityModifier);
	}

	boolean isCutInMoved;
	
	public void showCutIn(float duration, float x) {
		if (isCutInMoved) {
			return;
		}
		
		playerCutIn.registerEntityModifier(new SequenceEntityModifier(
				new MoveModifier(duration / 3, 
					-x, 0, 
					playerCutIn.getY(), playerCutIn.getY(),  
					new IEntityModifier.IEntityModifierListener() {
						@Override
						public void onModifierStarted(IModifier<IEntity> pModifier, IEntity pItem) {
							playerCutIn.setAlpha(1.0f);
							isCutInMoved = true;
						}
						@Override
						public void onModifierFinished(IModifier<IEntity> pModifier, IEntity pItem) {
						}
					}
				),
				new DelayModifier(duration / 3), 
				new MoveModifier(duration / 3, 
					0, x, 
					playerCutIn.getY(), playerCutIn.getY(),  
					new IEntityModifier.IEntityModifierListener() {
						@Override
						public void onModifierStarted(IModifier<IEntity> pModifier, IEntity pItem) {
						}
						@Override
						public void onModifierFinished(IModifier<IEntity> pModifier, IEntity pItem) {
							playerCutIn.setAlpha(0.0f);
							isCutInMoved = false;
						}
					}
				)
			));
	}
	// ----------------------------------------------
	// 汎用ポジション設定
	// ----------------------------------------------
	/**
	 * プレイヤーデフォルトポジション設定.
	 */
	public void setPlayerToDefaultPosition() {
		player.animate(
				new long[]{100, 100, 100}, 
				new int[]{6, 7, 8}, 
				true);
		showPlayer(PlayerSpriteType.PLAYER_TYPE_NORMAL);
	}
	
	/**
	 * プレイヤー攻撃ポジション設定.
	 */
	public void setPlayerToAttackPosition() {
		playerAttack.animate(
				new long[]{100, 100, 100}, 
				new int[]{6, 7, 8}, 
				false);
		showPlayer(PlayerSpriteType.PLAYER_TYPE_ATTACK);
		
		setPlayerPosition(playerAttack.getX(), playerAttack.getY());
	}
	
	/**
	 * プレイヤージャンプポジション設定.
	 */
	public void setPlayerToJumpPositon() {
		playerDefense.animate(
				new long[]{200, 300}, 
				new int[]{0, 3}, 
				false);
		showPlayer(PlayerSpriteType.PLAYER_TYPE_DEFENSE);
		
		// 上に飛ぶ感じのアニメーション
		playerDefense.registerEntityModifier(new SequenceEntityModifier(
			new MoveModifier(0.2f, 
					playerDefense.getX(), playerDefense.getX(), 
					playerDefense.getY(), playerDefense.getY() - 100),
			new DelayModifier(0.1f),
			new MoveModifier(0.2f, 
					playerDefense.getX(), playerDefense.getX(), 
					playerDefense.getY() - 100, playerDefense.getY())) {
				@Override
				public void onModifierFinished(
						IModifier<IEntity> pModifier, IEntity pItem) {
					super.onModifierFinished(pModifier, pItem);
					setPlayerPosition(playerDefense.getX(), playerDefense.getY());
				}
			});
	}
				
	/**
	 * プレイヤースライディングポジション設定.
	 */
	public void setPlayerToSlidePositon() {
		playerDefense.animate(
				new long[]{200, 100}, 
				new int[]{0, 11}, 
				false);
		showPlayer(PlayerSpriteType.PLAYER_TYPE_DEFENSE);
	}
}
package com.kyokomi.srpgquest.sprite;

import java.util.ArrayList;
import java.util.List;

import org.andengine.entity.IEntity;
import org.andengine.entity.modifier.DelayModifier;
import org.andengine.entity.modifier.IEntityModifier;
import org.andengine.entity.modifier.MoveModifier;
import org.andengine.entity.modifier.SequenceEntityModifier;
import org.andengine.entity.primitive.Rectangle;
import org.andengine.entity.sprite.AnimatedSprite;
import org.andengine.util.color.Color;
import org.andengine.util.modifier.IModifier;

import com.kyokomi.core.constants.PlayerSpriteType;
import com.kyokomi.core.dto.ActorPlayerDto;
import com.kyokomi.core.scene.KeyListenScene;
import com.kyokomi.srpgquest.map.common.MapPoint;

/**
 * アクター
 * TODO: メンバ変数でspriteもつのをやめる
 * @author kyokomi
 *
 */
public class ActorSprite extends Rectangle {
	/**
	 * アクター情報.
	 */
	private ActorPlayerDto mActorPlayer;
	public ActorPlayerDto getActorPlayer() {
		return mActorPlayer;
	}
	public void setActorPlayer(ActorPlayerDto pActorPlayer) {
		this.mActorPlayer = pActorPlayer;
	}
	public Integer getPlayerId() {
		return mActorPlayer.getPlayerId();
	}

	/** プレイヤーキャラクター(TODO: 持たないようにする). */
	private AnimatedSprite player;
	public AnimatedSprite getPlayer() {
		return player;
	}
	/** 最後に移動した時の向き. */
	private int lastMoveCurrentIndex = 0;
	
	/**
	 * コンストラクタ.
	 * @param pActorPlayer
	 * @param baseScene
	 * @param pX
	 * @param pY
	 * @param pWidth
	 * @param pHeight
	 * @param scale
	 * @param pVertexBufferObjectManager
	 */
	public ActorSprite(ActorPlayerDto pActorPlayer, KeyListenScene baseScene, 
			float pX, float pY, float pWidth, float pHeight, float scale) {
		super(pX, pY, pWidth, pHeight, baseScene.getBaseActivity().getVertexBufferObjectManager());
		
		this.mActorPlayer = pActorPlayer;
		
		setColor(Color.TRANSPARENT);
		
		playerSpriteInit(baseScene, pX, pY, scale);
	}
	
	public static String getBaseFileName(int imageResId) {
		return "actor/actor" + imageResId;
	}	
	public static String getFaceFileName(int imageResId) {
		return getBaseFileName(imageResId) + "_f.png";
	}
	public static String getMoveFileName(int imageResId) {
		return getBaseFileName(imageResId) + "_5_s.png";
	}
	private AnimatedSprite getMoveAnimatedSprite(KeyListenScene baseScene, int imageResId) {
		return baseScene.getResourceAnimatedSprite(getMoveFileName(imageResId), 3, 4);
	}
	
	private void playerSpriteInit(KeyListenScene baseScene, float x, float y, float scale) {
		// playerキャラを追加 攻撃と防御のスプライトもセットで読み込んでおく
		player = getMoveAnimatedSprite(baseScene, getActorPlayer().getImageResId());
		attachChild(player);

		// デフォルト表示
		showPlayer(PlayerSpriteType.PLAYER_TYPE_NORMAL);
		setPlayerScale(scale);
		
		// タグ設定
		setTag(mActorPlayer.getPlayerId());
	}

	// ----------------------------------------------
	// Sprite設定系
	// ----------------------------------------------
	@Override
	public void setScale(float pScale) {
		setPlayerScale(pScale);
	}
	public void setPlayerScale(float scale) {
		if (player != null) {
			player.setScale(scale);
		}
	}
	@Override
	public void setPosition(IEntity pOtherEntity) {
		setPlayerPosition(pOtherEntity);
	}
	public void setPlayerPosition(IEntity pOtherEntity) {
		if (player != null) {
			player.setPosition(pOtherEntity.getX(), pOtherEntity.getY());
		}
	}
	@Override
	public void setPosition(float pX, float pY) {
		setPlayerPosition(pX, pY);
	}
	public void setPlayerPosition(float x, float y) {
		if (player != null) {
			player.setPosition(x, y);
		}
	}
	@Override
	public void setSize(float pWidth, float pHeight) {
		setPlayerSize(pWidth, pHeight);
	}
	public void setPlayerSize(float w, float h) {
		if (player != null) {
			player.setSize(w, h);
		}
	}
	
	/**
	 * 水平方向反転.
	 * @param pFlippedHorizontal
	 */
	public void setPlayerFlippedHorizontal(boolean pFlippedHorizontal) {
		if (player != null) {
			player.setFlippedHorizontal(pFlippedHorizontal);
		}
	}
	
	// ----------------------------------------------
	// 表示とか
	// ----------------------------------------------
	
	public void showPlayer(PlayerSpriteType playerType) {
		float normal = 0.0f;
		switch (playerType) {
		case PLAYER_TYPE_NORMAL:
			normal = 1.0f;
			break;
		case PLAYER_TYPE_HIDE:
			break;
		default:
			break;
		}
		if (player != null) {
			player.setAlpha(normal);
		}
	}
	
	// ----------------------------------------------
	// 汎用モーション系
	// ----------------------------------------------

	/**
	 * プレイヤー移動.
	 * @param duration 移動時間（トータル）
	 * @param moveMapPointList 移動経路リスト
	 */
	public void move(float duration, List<MapPoint> moveMapPointList, 
			IEntityModifier.IEntityModifierListener pEntityModifierListener) {
		if (player == null) {
			return;
		}
		
		List<IEntityModifier> modifierList = new ArrayList<IEntityModifier>();
		float moveStartX = player.getX();
		float moveStartY = player.getY();
		float stepDuration = duration / moveMapPointList.size();
		if (stepDuration == duration) {
			stepDuration = stepDuration / 4;
		}
		for (final MapPoint mapPoint : moveMapPointList) {
			modifierList.add(new SequenceEntityModifier(
					new MoveModifier(stepDuration, moveStartX, mapPoint.getX(), moveStartY, mapPoint.getY()),
					new DelayModifier(0.0f, new IEntityModifier.IEntityModifierListener() {
						@Override
						public void onModifierStarted(IModifier<IEntity> pModifier, IEntity pItem) {
						}
						
						@Override
						public void onModifierFinished(IModifier<IEntity> pModifier, IEntity pItem) {
							lastMoveCurrentIndex = 0;
							switch (mapPoint.getDirection()) {
							case MOVE_DOWN:
								lastMoveCurrentIndex = 6;//0
								break;
							case MOVE_LEFT:
								lastMoveCurrentIndex = 0;//3
								break;
							case MOVE_RIGHT:
								lastMoveCurrentIndex = 9;//6
								break;
							case MOVE_UP:
								lastMoveCurrentIndex = 3;//9
								break;
							default:
								break;
							}
							player.setCurrentTileIndex(lastMoveCurrentIndex);
							setPlayerToDefaultPosition();
						}
					})
					
					));
			moveStartX = mapPoint.getX();
			moveStartY = mapPoint.getY();
		}
		// コールバックがあれば設定
		if (pEntityModifierListener != null) {
			modifierList.add(new DelayModifier(0.0f, pEntityModifierListener));
		}
		SequenceEntityModifier sequenceEntityModifier  = new SequenceEntityModifier(
				modifierList.toArray(new IEntityModifier[0]));
		// 移動
		player.registerEntityModifier(sequenceEntityModifier);
	}

	// ----------------------------------------------
	// 汎用ポジション設定
	// ----------------------------------------------
	/**
	 * プレイヤーデフォルトポジション設定.
	 */
	public void setPlayerToDefaultPosition() {
		if (player != null) {
			player.animate(
					new long[]{100, 100, 100}, 
					new int[]{lastMoveCurrentIndex, lastMoveCurrentIndex+1, lastMoveCurrentIndex+2}, 
					true);
			showPlayer(PlayerSpriteType.PLAYER_TYPE_NORMAL);
		}
	}
	/**
	 * プレイヤーデフォルトポジション設定.
	 */
	public void setPlayerToDefaultPositionStop() {
		if (player != null) {
			player.stopAnimation(lastMoveCurrentIndex);
			showPlayer(PlayerSpriteType.PLAYER_TYPE_NORMAL);
		}
	}
}
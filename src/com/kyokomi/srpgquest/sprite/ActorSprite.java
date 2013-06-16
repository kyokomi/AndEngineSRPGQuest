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
import com.kyokomi.srpgquest.constant.MoveDirectionType;
import com.kyokomi.srpgquest.map.common.MapPoint;
import com.kyokomi.srpgquest.utils.ActorSpriteUtil;

/**
 * アクター
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

	/** プレイヤーキャラクター. */
	public AnimatedSprite getPlayer() {
		return (AnimatedSprite) getChildByTag(getPlayerId());
	}
	
	private int lastMoveCurrentIndex;
	public int getLastMoveCurrentIndex() {
		return lastMoveCurrentIndex;
	}
	public void setLastMoveCurrentIndex(int lastMoveCurrentIndex) {
		this.lastMoveCurrentIndex = lastMoveCurrentIndex;
	}
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
	
	private void playerSpriteInit(KeyListenScene baseScene, float x, float y, float scale) {
		// playerキャラを追加 攻撃と防御のスプライトもセットで読み込んでおく
		AnimatedSprite player = ActorSpriteUtil.getMoveAnimatedSprite(baseScene, getActorPlayer().getImageResId());
		player.setTag(getPlayerId());
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
		getPlayer().setScale(scale);
	}
	@Override
	public void setPosition(IEntity pOtherEntity) {
		setPlayerPosition(pOtherEntity);
	}
	public void setPlayerPosition(IEntity pOtherEntity) {
		getPlayer().setPosition(pOtherEntity.getX(), pOtherEntity.getY());
	}
	@Override
	public void setPosition(float pX, float pY) {
		setPlayerPosition(pX, pY);
	}
	public void setPlayerPosition(float x, float y) {
		getPlayer().setPosition(x, y);
	}
	@Override
	public void setSize(float pWidth, float pHeight) {
		setPlayerSize(pWidth, pHeight);
	}
	public void setPlayerSize(float w, float h) {
		getPlayer().setSize(w, h);
	}
	
	/**
	 * 水平方向反転.
	 * @param pFlippedHorizontal
	 */
	public void setPlayerFlippedHorizontal(boolean pFlippedHorizontal) {
		getPlayer().setFlippedHorizontal(pFlippedHorizontal);
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
		getPlayer().setAlpha(normal);
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
		if (getPlayer() == null) {
			return;
		}
		
		List<IEntityModifier> modifierList = new ArrayList<IEntityModifier>();
		float moveStartX = getPlayer().getX();
		float moveStartY = getPlayer().getY();
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
							setPlayerDirection(mapPoint.getDirection());
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
		getPlayer().registerEntityModifier(sequenceEntityModifier);
	}

	// ----------------------------------------------
	// 汎用ポジション設定
	// ----------------------------------------------
	public void setPlayerDirection(MoveDirectionType moveDirectionType) {
		this.lastMoveCurrentIndex = moveDirectionType.getDirection();
		ActorSpriteUtil.setPlayerDirection(getPlayer(), moveDirectionType);
	}
	public void setPlayerToDefaultPosition() {
		ActorSpriteUtil.setPlayerToDefaultPosition(getPlayer(), this.lastMoveCurrentIndex);
	}
	
	public void stopAnimation() {
		getPlayer().stopAnimation(lastMoveCurrentIndex);
	}
}
package com.kyokomi.srpgquest.layer;

import org.andengine.entity.IEntity;
import org.andengine.entity.modifier.FadeInModifier;
import org.andengine.entity.modifier.IEntityModifier;
import org.andengine.entity.shape.IAreaShape;
import org.andengine.entity.sprite.Sprite;
import org.andengine.util.modifier.IModifier;

import com.kyokomi.core.scene.KeyListenScene;
import com.kyokomi.srpgquest.scene.MapBattleScene.LayerZIndex;
import com.kyokomi.srpgquest.scene.SrpgBaseScene;

public class MapBattleCutInLayer {
	public enum MapBattleCutInLayerType {
		PLAYER_TURN_CUTIN(1, "cutin/player_turn.png"),
		ENEMY_TURN_CUTIN(2,  "cutin/enemy_turn.png"),
		PLAYER_WIN_CUTIN(3,  "cutin/player_win.png"),
		GAME_OVER_CUTIN(4,   "cutin/game_over.jpg"),
		;
		private Integer value;
		private String fileName;
		private MapBattleCutInLayerType(Integer value, String fileName) {
			this.value = value;
			this.fileName = fileName;
		}
		public Integer getValue() {
			return value;
		}
		public String getFileName() {
			return fileName;
		}
		public static MapBattleCutInLayerType get(Integer value) {
			MapBattleCutInLayerType[] values = values();
			for (MapBattleCutInLayerType type : values) {
				if (type.getValue() == value) {
					return type;
				}
			}
			throw new RuntimeException("find not tag type.");
		}
	}
	
	/** プレイヤーターンカットイン. */
	private Sprite mPlayerTurnCutInSprite;
	/** エネミーターンカットイン. */
	private Sprite mEnemyTurnCutInSprite;
	/** プレイヤー勝利カットイン. */
	private Sprite mPlayerWinCutInSprite;
	/** ゲームオーバーカットイン. */
	private Sprite mGameOverCutInSprite;
	
	/**
	 * カットインのコールバック.
	 * @author kyokomi
	 *
	 */
	public interface ICutInCallback {
		public void doAction();
	}
	
	public MapBattleCutInLayer(SrpgBaseScene pBaseScene) {
		initCutInSprite(pBaseScene);
	}
	private void initCutInSprite(SrpgBaseScene pBaseScene) {
		mPlayerTurnCutInSprite = createAttachCutInSprite(pBaseScene,
				MapBattleCutInLayerType.PLAYER_TURN_CUTIN);
		mEnemyTurnCutInSprite = createAttachCutInSprite(pBaseScene,
				MapBattleCutInLayerType.ENEMY_TURN_CUTIN);
		mPlayerWinCutInSprite = createAttachCutInSprite(pBaseScene,
				MapBattleCutInLayerType.PLAYER_WIN_CUTIN);
		
		mGameOverCutInSprite = createAttachCutInSprite(pBaseScene,
				MapBattleCutInLayerType.GAME_OVER_CUTIN);
		mGameOverCutInSprite.setPosition(0, 0);
		mGameOverCutInSprite.setSize(pBaseScene.getWindowWidth(), 
				pBaseScene.getWindowHeight());
	}
	
	private Sprite createAttachCutInSprite(KeyListenScene pBaseScene, 
			MapBattleCutInLayerType pMapBattleCutInLayerType) {
		Sprite areaShape = pBaseScene.getResourceSprite(pMapBattleCutInLayerType.getFileName());
		attachChildCutInSprite(pBaseScene, areaShape);
		return areaShape;
	}
	private void attachChildCutInSprite(KeyListenScene pBaseScene, IAreaShape sprite) {
		pBaseScene.placeToCenter(sprite);
		sprite.setAlpha(0.0f);
		sprite.setVisible(false);
		sprite.setZIndex(LayerZIndex.CUTIN_LAYER.getValue());
		pBaseScene.attachChild(sprite);
	}
	
	public void showCutIn(MapBattleCutInLayerType pMapBattleCutInLayerType, final ICutInCallback cutInCallback) {
		IAreaShape pEntity = null;
		switch (pMapBattleCutInLayerType) {
		case PLAYER_TURN_CUTIN:
			pEntity = mPlayerTurnCutInSprite;
			break;
		case ENEMY_TURN_CUTIN:
			pEntity = mEnemyTurnCutInSprite;
			break;
		case PLAYER_WIN_CUTIN:
			pEntity = mPlayerWinCutInSprite;
			break;
		case GAME_OVER_CUTIN:
			pEntity = mGameOverCutInSprite;
			break;
		default:
			// 終了
			return;
		}
		showCutInSprite(2.0f, pEntity, cutInCallback);
	}

	private void showCutInSprite(final float pDuration, final IAreaShape pEntity, final ICutInCallback cutInCallback) {
		pEntity.registerEntityModifier(new FadeInModifier(pDuration, new IEntityModifier.IEntityModifierListener() {
			@Override public void onModifierStarted(IModifier<IEntity> pModifier, IEntity pItem) {
				pEntity.setVisible(true);
			}
			@Override public void onModifierFinished(IModifier<IEntity> pModifier, IEntity pItem) {
				cutInCallback.doAction(); // コールバック呼び出し
				pEntity.setAlpha(0.0f);
				pEntity.setVisible(false);
			}
		}));
	}
}

package com.kyokomi.srpgquest.scene.part;

import java.util.List;

import org.andengine.entity.scene.Scene;
import org.andengine.entity.sprite.TiledSprite;
import org.andengine.input.touch.TouchEvent;

import android.util.SparseArray;

import com.kyokomi.core.dto.PlayerTalkDto;
import com.kyokomi.core.dto.SaveDataDto;
import com.kyokomi.core.sprite.TalkLayer;
import com.kyokomi.srpgquest.constant.CommonTag;
import com.kyokomi.srpgquest.constant.LayerZIndexType;
import com.kyokomi.srpgquest.layer.ScenarioStartCutInTouchLayer;
import com.kyokomi.srpgquest.logic.TalkLogic;
import com.kyokomi.srpgquest.scene.SrpgBaseScene;

public class NovelPart extends AbstractGamePart {

	public NovelPart(SrpgBaseScene pBaseScene) {
		super(pBaseScene);
	}

	@Override
	public void init(SaveDataDto saveDataDto) {
		// 会話内容取得
		TalkLogic talkLogic = new TalkLogic();
		List<PlayerTalkDto> talks = talkLogic.getTalkDtoList(getBaseScene(),
				saveDataDto.getScenarioNo(), 
				saveDataDto.getSeqNo());
		// 顔画像作成
		SparseArray<TiledSprite> actorFaces = talkLogic.getTalkFaceSparse(getBaseScene(), talks);
		// 会話レイヤー作成
		TalkLayer talkLayer = new TalkLayer(getBaseScene());
		talkLayer.initTalk(actorFaces, talks);
		talkLayer.hide();
		talkLayer.setZIndex(LayerZIndexType.TALK_LAYER.getValue());
		talkLayer.setTag(CommonTag.TALK_LAYER_TAG.getValue());
		getBaseScene().attachChild(talkLayer);
		
		// まずは章開始カットイン
		if (saveDataDto.getSeqNo().intValue() == 1) {
			ScenarioStartCutInTouchLayer scenarioStartCutInTouchLayer = 
					new ScenarioStartCutInTouchLayer(getBaseScene(), saveDataDto);
			getBaseScene().attachChild(scenarioStartCutInTouchLayer);
			scenarioStartCutInTouchLayer.showTouchLayer();
		} else {
			talkLayer.nextTalk();
		}
	}

	@Override
	public void touchEvent(Scene pScene, TouchEvent pSceneTouchEvent) {
		float x = pSceneTouchEvent.getX();
		float y = pSceneTouchEvent.getY();
		
		if (pSceneTouchEvent.getAction() == TouchEvent.ACTION_DOWN) {
			ScenarioStartCutInTouchLayer startTouchLayer =
					(ScenarioStartCutInTouchLayer) getBaseScene().getChildByTag(ScenarioStartCutInTouchLayer.TAG);
			TalkLayer talkLayer = (TalkLayer) getBaseScene().getChildByTag(CommonTag.TALK_LAYER_TAG.getValue());
			if (startTouchLayer != null && startTouchLayer.isTouchLayer(x, y)) {
				// タップで消える
				startTouchLayer.hideTouchLayer();
				// 会話を開始
				if (talkLayer != null) {
					talkLayer.nextTalk();
				}
				getBaseScene().detachEntity(startTouchLayer);
				
			} else if (talkLayer != null && talkLayer.contains(x, y)) {
				// TODO: SE再生
				
				if (talkLayer.isNextTalk()) {
					talkLayer.nextTalk();
					
				} else {
					talkLayer.hide();
					// 次の会話がなくなれば、会話レイヤーを開放
					getBaseScene().detachEntity(talkLayer);
					
					// ノベルパート終了
					end();
				}
			}
		}
	}

	@Override
	public void end() {
		// 次のシナリオへ
		getBaseScene().nextScenario();
	}
}

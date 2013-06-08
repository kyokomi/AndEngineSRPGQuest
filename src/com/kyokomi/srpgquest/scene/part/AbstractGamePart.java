package com.kyokomi.srpgquest.scene.part;

import org.andengine.entity.scene.Scene;
import org.andengine.input.touch.TouchEvent;

import com.kyokomi.core.dto.SaveDataDto;
import com.kyokomi.srpgquest.scene.SrpgBaseScene;

public abstract class AbstractGamePart implements IGamePart {

	private SrpgBaseScene mBaseScene;
	public SrpgBaseScene getBaseScene() {
		return mBaseScene;
	}
	
	public AbstractGamePart(SrpgBaseScene pBaseScene) {
		this.mBaseScene = pBaseScene;
	}
	
	@Override
	public abstract void init(SaveDataDto saveDataDto);

	@Override
	public abstract void touchEvent(Scene pScene, TouchEvent pSceneTouchEvent);

	@Override
	public abstract void end();
}

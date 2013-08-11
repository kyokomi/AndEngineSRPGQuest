package com.kyokomi.srpgquest.scene.part;

import org.andengine.entity.scene.Scene;
import org.andengine.input.touch.TouchEvent;

import com.kyokomi.core.dto.SaveDataDto;

public interface IGamePart {

	void init(SaveDataDto saveDataDto);
	
	void touchEvent(Scene pScene, TouchEvent pSceneTouchEvent);
	
	void end();
}

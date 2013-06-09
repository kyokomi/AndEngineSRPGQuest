package com.kyokomi.srpgquest.scene;

import com.kyokomi.core.activity.MultiSceneActivity;
import com.kyokomi.srpgquest.scene.part.BattlePart;

import android.view.KeyEvent;

public class SandBoxBattleScene extends SrpgBaseScene {

	public SandBoxBattleScene(MultiSceneActivity baseActivity) {
		super(baseActivity);
		
		init();
		
		// FPS表示
		initFps(getWindowWidth() - 100, getWindowHeight() - 20, getFont());
	}

	@Override
	public void initSoundAndMusic() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onPause() {
		// TODO Auto-generated method stub

	}

	@Override
	public void init() {
		
	}

	@Override
	public boolean dispatchKeyEvent(KeyEvent e) {
		// バックボタンが押された時
		if (e.getAction() == KeyEvent.ACTION_DOWN && e.getKeyCode() == KeyEvent.KEYCODE_BACK) { 
			return true;
		}
		return false;
	}

}

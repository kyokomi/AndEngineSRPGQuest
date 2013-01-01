package com.kyokomi.core.handler;

import org.andengine.engine.handler.timer.ITimerCallback;
import org.andengine.engine.handler.timer.TimerHandler;

public class CustomTimerHandler extends TimerHandler {

	/** ポーズ中フラグ. */
	private boolean isPaused = false;
	
	public CustomTimerHandler(float pTimerSeconds, ITimerCallback pTimerCallback) {
		super(pTimerSeconds, pTimerCallback);
	}

	public CustomTimerHandler(float pTimerSeconds, boolean pAutoReset,
			ITimerCallback pTimerCallback) {
		super(pTimerSeconds, pAutoReset, pTimerCallback);
	}
	
	@Override
	public void onUpdate(float pSecondsElapsed) {
		// ポーズ中はupdate関数を呼び出さない
		if (!isPaused) {
			super.onUpdate(pSecondsElapsed);
		}
	}
	
	/**
	 * タイマーの一時停止.
	 */
	public void pause() {
		this.isPaused = true;
	}
	
	/**
	 * タイマー再開.
	 */
	public void resume() {
		if (this.isPaused) {
			this.isPaused = false;
		}
	}
}

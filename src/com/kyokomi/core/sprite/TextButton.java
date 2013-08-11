package com.kyokomi.core.sprite;

import java.util.ArrayList;
import java.util.List;

import org.andengine.entity.primitive.Rectangle;
import org.andengine.entity.text.Text;

import org.andengine.input.touch.TouchEvent;
import org.andengine.opengl.vbo.VertexBufferObjectManager;
import org.andengine.util.color.Color;

public class TextButton extends Rectangle {
	// ===========================================================
	// Constants
	// ===========================================================

	// ===========================================================
	// Fields
	// ===========================================================

	private OnClickListener mOnClickListener;

	private boolean mEnabled = true;
	private List<TextButtonState> mStateList;
	private TextButtonState mState;
	private Text mButtonText;

	// ===========================================================
	// Constructors
	// ===========================================================
	public TextButton(List<TextButtonState> textButtonStateList, Text pText, float pX, float pY, float pAddWidth, float pAddHeight,
			VertexBufferObjectManager pVertexBufferObjectManager, final OnClickListener pOnClickListener) {
		super(pX, pY, pText.getWidth() + pAddWidth, pText.getHeight() + pAddHeight, pVertexBufferObjectManager);
		this.setColor(Color.BLACK);
		this.setAlpha(0.8f);
		this.mOnClickListener = pOnClickListener;
		
		this.mButtonText = pText;
		this.mButtonText.setPosition(pAddWidth / 2, pAddHeight / 2);
		attachChild(mButtonText);
		
		this.mStateList = textButtonStateList;
		this.changeState(getButtonState(State.NORMAL));
	}
	
	public TextButton(Text pText, float pX, float pY, float pAddWidth, float pAddHeight,
			VertexBufferObjectManager pVertexBufferObjectManager, final OnClickListener pOnClickListener) {
		super(pX, pY, pText.getWidth() + pAddWidth, pText.getHeight() + pAddHeight, pVertexBufferObjectManager);
		this.setColor(Color.BLACK);
		this.setAlpha(0.8f);
		this.mOnClickListener = pOnClickListener;
		
		this.mButtonText = pText;
		this.mButtonText.setPosition(pAddWidth / 2, pAddHeight / 2);
		attachChild(mButtonText);
		
		this.mStateList = TextButton.createTextButtonStateList(pText.getText().toString());
		this.changeState(getButtonState(State.NORMAL));
	}

	// ===========================================================
	// Getter & Setter
	// ===========================================================

	public boolean isEnabled() {
		return this.mEnabled;
	}

	public void setEnabled(final boolean pEnabled) {
		this.mEnabled = pEnabled;

		if(this.mEnabled && this.mState.getState() == State.DISABLED) {
			this.changeState(getButtonState(State.NORMAL));
		} else if(!this.mEnabled) {
			this.changeState(getButtonState(State.DISABLED));
		}
	}

	public boolean isPressed() {
		return this.mState.getState() == State.PRESSED;
	}

	public State getState() {
		return this.mState.getState();
	}

	public void setOnClickListener(final OnClickListener pOnClickListener) {
		this.mOnClickListener = pOnClickListener;
	}

	// ===========================================================
	// Methods for/from SuperClass/Interfaces
	// ===========================================================

	@Override
	public boolean onAreaTouched(final TouchEvent pSceneTouchEvent, final float pTouchAreaLocalX, final float pTouchAreaLocalY) {
		if(!this.isEnabled()) {
			this.changeState(getButtonState(State.DISABLED));
		} else if(pSceneTouchEvent.isActionDown() && this.contains(pSceneTouchEvent.getX(), pSceneTouchEvent.getY())) {
			this.changeState(getButtonState(State.PRESSED));
		} else if(pSceneTouchEvent.isActionCancel() || !this.contains(pSceneTouchEvent.getX(), pSceneTouchEvent.getY())) {
			this.changeState(getButtonState(State.NORMAL));
		} else if(pSceneTouchEvent.isActionUp() && this.mState.getState() == State.PRESSED && this.contains(pSceneTouchEvent.getX(), pSceneTouchEvent.getY())) {
			this.changeState(getButtonState(State.NORMAL));
			
			if(this.mOnClickListener != null) {
				this.mOnClickListener.onClick(this, pTouchAreaLocalX, pTouchAreaLocalY);
			}
		}
		
		return true;
	}

	private TextButtonState getButtonState(State state) {
		TextButtonState buttonState = new TextButtonState(state);
		if (mStateList != null) {
			for (TextButtonState textButtonState : mStateList) {
				if (textButtonState.getState() == state) {
					buttonState = textButtonState; 
				}
			}
		}
		return buttonState;
	}
	@Override
	public boolean contains(final float pX, final float pY) {
		if(!this.isVisible()) {
			return false;
		} else {
			return super.contains(pX, pY);
		}
	}

	// ===========================================================
	// Methods
	// ===========================================================

	/**
	 * ボタンの状態変化に応じた画像変更とか.
	 * @param pState
	 */
	private void changeState(final TextButtonState pState) {
		if(pState == this.mState) {
			return;
		}

		// TODO: 色の変化、スケール変化、透明度変化、背景色変化など色々できるようにしたい
		this.mState = pState;

		this.mButtonText.setText(mState.getText());
		this.setColor(mState.getColor());
		this.setAlpha(mState.getAlpha());
	}

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================

	public interface OnClickListener {
		// ===========================================================
		// Constants
		// ===========================================================

		// ===========================================================
		// Methods
		// ===========================================================

		public void onClick(final TextButton pTextButtonSprite, final float pTouchAreaLocalX, final float pTouchAreaLocalY);
	}
	
	/**
	 * デフォルト設定のtextリストを作成.
	 * @param text
	 * @return デフォルトテキストリスト
	 */
	public static List<TextButtonState> createTextButtonStateList(String text) {
		List<TextButtonState> textButtonStateList = new ArrayList<TextButtonState>();
		for (State state : State.values()) {
			TextButtonState textButtonState = new TextButtonState(state);
			textButtonState.setText(text);
			textButtonStateList.add(textButtonState);	
		}
		return textButtonStateList;
	}

	public static enum State {
		// ===========================================================
		// Elements
		// ===========================================================

		NORMAL(0,   "NORMAL", Color.BLACK, 0.8f),
		PRESSED(1,  "PRESSED", Color.BLUE, 0.8f),
		DISABLED(2, "DISABLED", new Color(0.7f, 0.7f, 0.7f, 1), 0.8f);

		// ===========================================================
		// Constants
		// ===========================================================

		// ===========================================================
		// Fields
		// ===========================================================

		private final int mTiledTextureRegionIndex;
		private final String mText;
		private final Color mColor;
		private final float mAlpha;

		// ===========================================================
		// Constructors
		// ===========================================================

		private State(final int pTiledTextureRegionIndex, final String pText, final Color pColor, float pAlpha) {
			this.mTiledTextureRegionIndex = pTiledTextureRegionIndex;
			this.mText = pText;
			this.mColor = pColor;
			this.mAlpha = pAlpha;
		}

		// ===========================================================
		// Getter & Setter
		// ===========================================================

		public int getTiledTextureRegionIndex() {
			return this.mTiledTextureRegionIndex;
		}

		public String getText() {
			return mText;
		}

		public Color getColor() {
			return mColor;
		}

		public float getAlpha() {
			return mAlpha;
		}
		// ===========================================================
		// Methods for/from SuperClass/Interfaces
		// ===========================================================

		// ===========================================================
		// Methods
		// ===========================================================

		// ===========================================================
		// Inner and Anonymous Classes
		// ===========================================================
	}
}


package com.kyokomi.core.sprite;

import org.andengine.util.color.Color;

import com.kyokomi.core.sprite.TextButton.State;

public class TextButtonState {
	public TextButtonState(State state) {
		this.state = state;
		this.text = state.getText();
		this.color = state.getColor();
		this.alpha = state.getAlpha();
	}
	private State state;
	private String text;
	private Color color;
	private float alpha;
	
	public State getState() {
		return state;
	}
	public void setState(State state) {
		this.state = state;
	}
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	public Color getColor() {
		return color;
	}
	public void setColor(Color color) {
		this.color = color;
	}
	public float getAlpha() {
		return alpha;
	}
	public void setAlpha(float alpha) {
		this.alpha = alpha;
	}
}
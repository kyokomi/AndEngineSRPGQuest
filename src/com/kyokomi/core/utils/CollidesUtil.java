package com.kyokomi.core.utils;

import org.andengine.entity.shape.IAreaShape;
import org.andengine.entity.sprite.Sprite;

public class CollidesUtil {

	/**
	 * タッチ開始位置と終了位置を元にフリックなのか判定.
	 * float[]は new float[2]で [0]がx座標 [1]がy座標
	 * @param startPoints タッチ開始位置
	 * @param endPoint タッチ終了位置
	 * @return true:フリック / false:フリックとみなさない
	 */
	public static boolean isTouchFlick(float[] startPoints, float[] endPoint) {
		return isTouchFlick(50, startPoints, endPoint);
	}
	/**
	 * タッチ開始位置と終了位置を元にフリックなのか判定.
	 * float[]は new float[2]で [0]がx座標 [1]がy座標
	 * @param baseDistance 判定の元となるフリック距離
	 * @param startPoints タッチ開始位置
	 * @param endPoint タッチ終了位置
	 * @return true:フリック / false:フリックとみなさない
	 */
	public static boolean isTouchFlick(float baseDistance, float[] startPoints, float[] endPoint) {
		
		float xDistance = endPoint[0] -startPoints[0];
		float yDistance = endPoint[1] -startPoints[1];
		
		if (Math.abs(xDistance) < baseDistance && Math.abs(yDistance) < baseDistance) {
			return false;
		}
		return true;
	}
	
	/**
	 * タッチ開始位置と終了位置を元に2点間の角度を求める.
	 * @param startPoints
	 * @param endPoint
	 * @return 2点間の角度
	 */
	public static double getAngleByTwoPostion(float[] startPoints, float[] endPoint) {
		double result = 0;
		
		float xDistance = endPoint[0] -startPoints[0];
		float yDistance = endPoint[1] -startPoints[1];
		
		result = Math.atan2((double) yDistance, (double) xDistance) * 180 / Math.PI;
		
		result += 270;
		
		return result;
	}
	
	public enum TouchEventFlick {
		UN_FLICK(0),
		
		DOWN_TO_UP(1),
		UP_TO_DOWN(2),
		LEFT_TO_RIGHT(3),
		RIGHT_TO_LEFT(4),
		;
		private Integer value;
		private TouchEventFlick(Integer value) {
			this.value = value;
		}
		public Integer getValue() {
			return value;
		}
		public static TouchEventFlick get(Integer value) {
			TouchEventFlick[] values = values();
			for (TouchEventFlick type : values) {
				if (type.getValue() == value) {
					return type;
				}
			}
			throw new RuntimeException("find not tag type.");
		}
	}
	
	/**
	 * フリック方向チェック.
	 * @param startPoints
	 * @param endPoint
	 * @return
	 */
	public static TouchEventFlick checkToushFlick(float[] startPoints, float[] endPoint) {
		TouchEventFlick touchEventFlick = TouchEventFlick.UN_FLICK;
		if (CollidesUtil.isTouchFlick(10, startPoints, endPoint)) {
			// フリックの角度を求める
			double angle = CollidesUtil.getAngleByTwoPostion(startPoints, endPoint);
			// 下から上へのフリックを0度に調整
			angle -= 180;
			// 下から上へのフリック
			if (angle > -45 && angle < 45) {
				touchEventFlick = TouchEventFlick.DOWN_TO_UP;
			// 上から下へのフリック
			} else if (angle > 135 && angle < 225) {
				touchEventFlick = TouchEventFlick.UP_TO_DOWN;
			// 左から右へのフリック
			} else if (angle > 45 && angle < 135) {
				touchEventFlick = TouchEventFlick.LEFT_TO_RIGHT;
			// 右から左へのフリック
			} else if ((angle > 225 && angle < 260) || (angle > -90 && angle < -45)) {
				touchEventFlick = TouchEventFlick.RIGHT_TO_LEFT;
			}
		}
		return touchEventFlick;
	}
	
	/**
	 * Sprite同士のx座標中心間の距離を求める.
	 * @param sprite1 左側
	 * @param sprite2 右側
	 * @return x座標中心間の距離
	 */
	public static float getDistanceXBetween(IAreaShape sprite1, IAreaShape sprite2) {
		return Math.abs((sprite2.getX() + sprite2.getWidth() / 2) - (sprite1.getX() + sprite1.getWidth() / 2));
	}
	/**
	 * Sprite同士のx座標中心間の距離を求める.
	 * @param sprite1 左側
	 * @param sprite2 右側
	 * @return x座標中心間の距離
	 */
	public static float getDistanceXBetween(float sprite1X, float sprite1Width, IAreaShape sprite2) {
		return Math.abs((sprite2.getX() + sprite2.getWidth() / 2) - (sprite1X + sprite1Width / 2));
	}
	/**
	 * Sprite同士のy座標中心間の距離を求める.
	 * @param sprite1 左側
	 * @param sprite2 右側
	 * @return y座標中心間の距離
	 */
	public static float getDistanceYBetween(IAreaShape sprite1, IAreaShape sprite2) {
		return Math.abs((sprite2.getY() + sprite2.getHeight() / 2) - (sprite1.getY() + sprite1.getHeight() / 2));
	}
	/**
	 * Sprite同士のy座標中心間の距離を求める.
	 * @param sprite1 左側
	 * @param sprite2 右側
	 * @return y座標中心間の距離
	 */
	public static float getDistanceYBetween(float sprite1Y, float sprite1Height, IAreaShape sprite2) {
		return Math.abs((sprite2.getY() + sprite2.getHeight() / 2) - (sprite1Y + sprite1Height / 2));
	}
	/**
	 * 衝突の許容値を考慮した距離を求める.
	 * @param sprite1 左側
	 * @param sprite2 右側
	 * @param allowable 衝突の許容値
	 * @return 衝突の許容値を考慮した距離
	 */
	public static float getAllowableDistance(Sprite sprite1, Sprite sprite2, float allowable) {
		return (sprite1.getWidth() / 2) + (sprite2.getWidth() / 2 - allowable);
	}
}

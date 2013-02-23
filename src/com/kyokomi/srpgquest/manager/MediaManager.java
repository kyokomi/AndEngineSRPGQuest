package com.kyokomi.srpgquest.manager;

import java.io.IOException;

import org.andengine.audio.music.Music;
import org.andengine.audio.music.MusicFactory;
import org.andengine.audio.sound.Sound;
import org.andengine.audio.sound.SoundFactory;
import org.andengine.ui.activity.BaseGameActivity;

import android.util.SparseArray;

public class MediaManager {
	public enum MusicType {
		TITLE_BGM     (1,   "title_bgm1.mp3"),
		TUTORIAL_BGM  (2,   "tutorial_bgm1.mp3"),
		BATTLE1_BGM   (10,  "battle_bgm1.mp3"),
		CLEAR_BGM     (100, "clear_bgm1.mp3"),
		GAME_OVER_BGM (200, "game_over_bgm1.mp3"),
		;
		private Integer value;
		private String fileName;
		private MusicType(Integer value, String fileName) {
			this.value = value;
			this.fileName = fileName;
		}
		
		public Integer getValue() {
			return value;
		}
		public String getFileName() {
			return fileName;
		}
		public static MusicType get(Integer value) {
			MusicType[] values = values();
			for (MusicType type : values) {
				if (type.getValue().intValue() == value.intValue()) {
					return type;
				}
			}
			throw new RuntimeException("find not tag type = " + value);
		}
	}
	
	public enum SoundType {
		ATTACK_SE      (1, "SE_ATTACK_ZANGEKI_01.wav"),
		BTN_PRESSED_SE (2, "btn_se1.wav"),
		;
		private Integer value;
		private String fileName;
		
		private SoundType(Integer value, String fileName) {
			this.value = value;
			this.fileName = fileName;
		}
		public Integer getValue() {
			return value;
		}
		public String getFileName() {
			return fileName;
		}
		public static SoundType get(Integer value) {
			SoundType[] values = values();
			for (SoundType type : values) {
				if (type.getValue().intValue() == value.intValue()) {
					return type;
				}
			}
			throw new RuntimeException("find not tag type = " + value);
		}
	}
	
	/** 自身のインスタンス. */
	private static MediaManager self;
	private static BaseGameActivity mBaseActivity;
	
	private MusicType mPauseMusicType;
	private static SparseArray<Music> mMusicArray;
	private static SparseArray<Sound> mSoundArray;
	// コンストラクタ
	private MediaManager() {
	}
	
	public static MediaManager getInstance(BaseGameActivity pBaseActivity) {
		if (self == null) {
			self = new MediaManager();
			MediaManager.mBaseActivity = pBaseActivity;
			MediaManager.mMusicArray = new SparseArray<Music>();
			MediaManager.mSoundArray = new SparseArray<Sound>();
		}
		return self;
	}
	
	public void createMedia(SoundType pSoundType) throws IOException {
		if (mSoundArray.indexOfKey(pSoundType.getValue()) >= 0) {
			return;
		}
		mSoundArray.put(pSoundType.getValue(), 
				SoundFactory.createSoundFromAsset(
					mBaseActivity.getSoundManager(), mBaseActivity, 
					pSoundType.getFileName()));
	}
	public void createMedia(MusicType pMusicType) throws IOException {
		if (mMusicArray.indexOfKey(pMusicType.getValue()) >= 0) {
			return;
		}
		mMusicArray.put(pMusicType.getValue(), 
				MusicFactory.createMusicFromAsset(
					mBaseActivity.getMusicManager(), mBaseActivity, 
					pMusicType.getFileName()));
	}
	
	// BGM
	
	public void playStart(MusicType pMusicType) {
		play(pMusicType, true);
	}
	public void play(MusicType pMusicType) {
		play(pMusicType, false);
	}
	public void play(MusicType pMusicType, boolean isStart) {
		Music music = get(pMusicType);
		// 再生中
		if (music.isPlaying()) {
			return;
		}
		stopPlayingMusic(); // 再生中のBGMは全部止めてから再生する
		
		// 再生する（基本ループ）
		music.setLooping(true);
		music.play();
	}
	public void pause(MusicType pMusicType) {
		Music music = get(pMusicType);
		// 再生中
		if (music.isPlaying()) {
			// 一時停止
			music.pause();
		}
	}
//	public void stop(MusicType pMusicType) {
//		Music music = get(pMusicType);
//		music.stop();
//	}
	public void playPauseingMusic() {
		if (mPauseMusicType != null) {
			play(mPauseMusicType);
		}
	}
	
	public void pausePlayingMusic() {
		int size = mMusicArray.size();
		for (int i = 0; i < size; i++) {
			MusicType musicType = MusicType.get(mMusicArray.keyAt(i));
			Music music = get(musicType);
			if (music.isPlaying()) {
				music.pause();
				mPauseMusicType = musicType;
			}
		}
	}
	public void stopPlayingMusic() {
		pausePlayingMusic();
	}
//	public void stopPlayingMusic() {
//		int size = mMusicArray.size();
//		for (int i = 0; i < size; i++) {
//			Music music = get(MusicType.get(mMusicArray.keyAt(i)));
//			if (music.isPlaying()) {
//				music.stop();
//			}
//		}
//	}
	
	private Music get(MusicType pMusicType) {
		Music music = mMusicArray.get(pMusicType.getValue());
		// 未生成, 解放済み
		if (music == null) {
			throw new RuntimeException("not create musicType = " + pMusicType);
		}
		if (music.isReleased()) {
			throw new RuntimeException("released musicType = " + pMusicType);
		}
		return music;
	}
	
	// SE
	
	public void play(SoundType pSoundType) {
		Sound sound = get(pSoundType);
		// 再生する
		sound.play();
	}
//	public void stop(SoundType pSoundType) {
//		Sound sound = get(pSoundType);
//		sound.stop();
//	}
	
	private Sound get(SoundType pSoundType) {
		Sound sound = mSoundArray.get(pSoundType.getValue());
		// 未生成, 解放済み
		if (sound == null) {
			throw new RuntimeException("not create pSoundType = " + pSoundType);
		}
		if (sound.isReleased()) {
			throw new RuntimeException("released pSoundType = " + pSoundType);
		}
		return sound;
	}
	
	/**
	 * プールを開放、シングルトンを削除する.
	 */
	public void resetAllMedia() {
		stopPlayingMusic();
		/*
		 * Activity.finish()だけだとシングルトンなクラスがnullにならない為、明示的にnullを代入
		 */
		self = null;
		mMusicArray.clear();
		mSoundArray.clear();
	}
}

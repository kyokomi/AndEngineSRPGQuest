package com.kyokomi.core.logic;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.andengine.entity.sprite.TiledSprite;

import android.util.SparseArray;

import com.kyokomi.core.dao.MActorDao;
import com.kyokomi.core.dto.PlayerTalkDto;
import com.kyokomi.core.dto.PlayerTalkDto.TalkDirection;
import com.kyokomi.core.entity.MActorEntity;
import com.kyokomi.core.scene.KeyListenScene;
import com.kyokomi.core.sprite.ActorSprite;
import com.kyokomi.srpgquest.scene.AbstractGameScene;
import com.kyokomi.srpgquest.scene.SrpgBaseScene;

public class TalkLogic {

	public List<PlayerTalkDto> getTalkDtoList(KeyListenScene pBaseScene, int scenarioNo, int seqNo) {
		// ファイル読み込み
		List<String> talkDataList = new ArrayList<String>();
		try {
			InputStream in = pBaseScene.getBaseActivity().getAssets().open(
					"scenario/" + scenarioNo + "_" + seqNo + ".txt");
			BufferedReader reader = 
		        new BufferedReader(new InputStreamReader(in));
		    String str;
		    while ((str = reader.readLine()) != null) {
		    	talkDataList.add(str);
		    }
		    in.close();
		    reader.close();
		    
		} catch (IOException e) {
			e.printStackTrace();
		}
		// 会話内容作成
		List<PlayerTalkDto> talks = new ArrayList<PlayerTalkDto>();
		for (String data :talkDataList) {
			String[] tokens = data.split(",");
			if (tokens.length < 5) {
				continue;
			}
			int i = 0;
			int id = Integer.parseInt(tokens[i]);
			i++;
			String name = tokens[i];
			i++;
			int faceIndex = Integer.parseInt(tokens[i]);
			i++;
			TalkDirection talkDirection = TalkDirection.get(Integer.parseInt(tokens[i]));
			i++;
			String text = tokens[i];
			
			PlayerTalkDto talkDto = new PlayerTalkDto(id, name, faceIndex, talkDirection, text);
			talks.add(talkDto);
		}
		
		return talks;
	}
	
	public SparseArray<TiledSprite> getTalkFaceSparse(SrpgBaseScene pBaseScene, List<PlayerTalkDto> talks) {
		pBaseScene.getBaseActivity().openDB();// DB OPEN
		
		MActorDao mActorDao = new MActorDao();
		// 顔画像作成
		SparseArray<TiledSprite> actorFaces = new SparseArray<TiledSprite>();
		int count = talks.size();
		for (int i = 0; i < count; i++) {
			PlayerTalkDto playerTalkDto = talks.get(i);
			MActorEntity mActorEntity = mActorDao.selectById(pBaseScene.getBaseActivity().getDB(), playerTalkDto.getPlayerId());
			playerTalkDto.setName(mActorEntity.getActorName());
			if (actorFaces.indexOfKey(mActorEntity.getActorId()) >= 0 ) {
				continue;
			}
			String faceName = ActorSprite.getFaceFileName(mActorEntity.getImageResId());
			TiledSprite faceSprite = pBaseScene.getResourceFaceSprite(
					mActorEntity.getActorId(), faceName);
			actorFaces.put(mActorEntity.getActorId(), faceSprite);
		}
		pBaseScene.getBaseActivity().closeDB(); // DB CLOSE
		return actorFaces;
	}

}

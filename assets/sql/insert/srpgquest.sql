
insert into M_SCENARIO values(1, 1, 1, 1, 1, "とある村の話");
insert into M_SCENARIO values(2, 1, 2, 2, 1, "バトル１");
insert into M_SCENARIO values(3, 1, 3, 3, 1, "バトル１リザルト");
insert into M_SCENARIO values(4, 1, 4, 1, 1, "バトル後");

insert into M_SCENARIO values(5, 2, 1, 1, 1, "洞窟の中は・・・");
insert into M_SCENARIO values(6, 2, 2, 2, 2, "バトル２");
insert into M_SCENARIO values(7, 2, 3, 3, 2, "バトル２リザルト");
insert into M_SCENARIO values(8, 2, 4, 1, 1, "バトル後");

insert into M_SCENARIO values(9, 3, 1, 1, 1, "モンスターも馬鹿ではない");
insert into M_SCENARIO values(10, 3, 2, 2, 3, "バトル３");
insert into M_SCENARIO values(11, 3, 3, 3, 3, "バトル３リザルト");
insert into M_SCENARIO values(12, 3, 4, 1, 1, "バトル後");

insert into M_SCENARIO values(99, 99, 1, 1, 1, "開発中により・・・");
insert into M_SCENARIO values(100, 99, 99, 99, 99, "クリア済み・・・");

insert into M_ACTOR values(1, "アスリーン", 110);
insert into M_ACTOR values(2, "ラーティ・クルス", 34);
insert into M_ACTOR values(3, "スライム", 901);
insert into M_ACTOR values(4, "村長", 902);
insert into M_ACTOR values(5, "ミア", 11);
insert into M_ACTOR values(6, "ケルティ", 20);
insert into M_ACTOR values(7, "ルーテシア", 33);
insert into M_ACTOR values(8, "カティナ", 37);
insert into M_ACTOR values(9, "サチ", 4);
insert into M_ACTOR values(10, "レーネ", 5);
insert into M_ACTOR values(11, "メルティナ", 61);
insert into M_ACTOR values(12, "エリオ", 63);
insert into M_ACTOR values(13, "タルト", 64);
insert into M_ACTOR values(14, "マリク", 70);
insert into M_ACTOR values(15, "スミレ", 8);


insert into M_ITEM values(1000, "ポーション", 64,	 3, 1);
insert into M_ITEM values(2000, "赤魔道師のマント", 47, 3, 3);
insert into M_ITEM values(2001, "風のマント", 46, 3, 4);
insert into M_ITEM values(2002, "皮の靴", 48, 3, 2);
insert into M_ITEM values(2003, "普通の指輪", 33, 3, 1);
insert into M_ITEM values(3000, "レイピア", 3, 2, 1);

insert into M_WEAPON values(1, 10, 90, 1, 1, 0, 0);

insert into M_ACCESSORY values(1, 10, 0, 0);
insert into M_ACCESSORY values(2, 12, 0, 0);
insert into M_ACCESSORY values(3, 15, 0, 0);
insert into M_ACCESSORY values(4, 8, 0, 0);

insert into T_ACTOR_STATUS values(1, 1, 0, 100, 60, 30, 100, 0, 5, 1, 3000, 2001);
insert into T_ACTOR_STATUS values(2, 1, 0, 100, 30, 20, 100, 0, 5, 1, 0, 0);
insert into T_ACTOR_STATUS values(3, 1, 0, 100, 10, 10,  40, 0, 4, 1, 0, 0);
insert into T_ACTOR_STATUS values(4, 1, 0, 100, 10, 10,  40, 0, 4, 1, 0, 0);
insert into T_ACTOR_STATUS values(5, 1, 0, 100, 30, 20, 100, 0, 5, 1, 0, 0);
insert into T_ACTOR_STATUS values(6, 1, 0, 100, 30, 20, 100, 0, 5, 1, 0, 0);
insert into T_ACTOR_STATUS values(7, 1, 0, 100, 30, 20, 100, 0, 5, 1, 0, 0);
insert into T_ACTOR_STATUS values(8, 1, 0, 100, 30, 20, 100, 0, 5, 1, 0, 0);
insert into T_ACTOR_STATUS values(9, 1, 0, 100, 30, 20, 100, 0, 5, 1, 0, 0);
insert into T_ACTOR_STATUS values(10, 1, 0, 100, 30, 20, 100, 0, 5, 1, 0, 0);
insert into T_ACTOR_STATUS values(11, 1, 0, 100, 30, 20, 100, 0, 5, 1, 0, 0);
insert into T_ACTOR_STATUS values(12, 1, 0, 100, 30, 20, 100, 0, 5, 1, 0, 0);
insert into T_ACTOR_STATUS values(13, 1, 0, 100, 30, 20, 100, 0, 5, 1, 0, 0);
insert into T_ACTOR_STATUS values(14, 1, 0, 100, 30, 20, 100, 0, 5, 1, 0, 0);
insert into T_ACTOR_STATUS values(15, 1, 0, 100, 30, 20, 100, 0, 5, 1, 0, 0);

insert into M_MAP_BATTLE_REWARD values(1, 1, 1, 100);
insert into M_MAP_BATTLE_REWARD values(2, 1, 2, 1000);
insert into M_MAP_BATTLE_REWARD values(3, 1, 3, 1000);
insert into M_MAP_BATTLE_REWARD values(4, 1, 3, 2003);
insert into M_MAP_BATTLE_REWARD values(5, 2, 1, 300);
insert into M_MAP_BATTLE_REWARD values(6, 2, 2, 5000);
insert into M_MAP_BATTLE_REWARD values(7, 3, 1, 500);
insert into M_MAP_BATTLE_REWARD values(8, 3, 2, 7000);
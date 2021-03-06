
insert into M_SCENARIO values(1, 1, 1, 1, 1, "アスリーンの旅立ち", "bk/002-Woods01.jpg");
insert into M_SCENARIO values(2, 1, 2, 2, 1, "バトル１", "bk/002-Woods01.jpg");
insert into M_SCENARIO values(3, 1, 3, 3, 1, "バトル１リザルト", "bk/002-Woods01.jpg");
insert into M_SCENARIO values(4, 1, 4, 1, 1, "バトル後", "bk/002-Woods01.jpg");

insert into M_SCENARIO values(5, 2, 1, 1, 1, "２の大陸[セレスティーン]", "bk/023-FarmVillage01.jpg");
insert into M_SCENARIO values(6, 2, 2, 2, 2, "バトル２", "bk/011-PortTown01.jpg");
insert into M_SCENARIO values(7, 2, 3, 3, 2, "バトル２リザルト", "bk/011-PortTown01.jpg");
insert into M_SCENARIO values(8, 2, 4, 1, 1, "バトル後", "bk/011-PortTown01.jpg");

insert into M_SCENARIO values(9, 3, 1, 1, 1, "３の大陸[ガンドォール]", "bk/019-DesertTown01.jpg");
insert into M_SCENARIO values(10, 3, 2, 2, 3, "バトル３", "bk/019-DesertTown01.jpg");
insert into M_SCENARIO values(11, 3, 3, 3, 3, "バトル３リザルト", "bk/019-DesertTown01.jpg");
insert into M_SCENARIO values(12, 3, 4, 1, 1, "バトル後", "bk/019-DesertTown01.jpg");

insert into M_SCENARIO values(99, 99, 1, 1, 1, "開発中により・・・", "bk/002-Woods01.jpg");
insert into M_SCENARIO values(100, 99, 99, 99, 99, "クリア済み・・・", "bk/002-Woods01.jpg");

insert into M_ACTOR values(0, "システムメッセージ", 0, 0);
insert into M_ACTOR values(1, "アスリーン", 110, 0);
insert into M_ACTOR values(2, "ラーティ・クルス", 34, 0);
insert into M_ACTOR values(3, "スライム", 901, 0);
insert into M_ACTOR values(4, "村長", 902, 0);
insert into M_ACTOR values(5, "ミア", 11, 0);
insert into M_ACTOR values(6, "ケルティ", 20, 0);
insert into M_ACTOR values(7, "ルーテシア", 33, 0);
insert into M_ACTOR values(8, "カティナ", 37, 0);
insert into M_ACTOR values(9, "サチ", 4, 0);
insert into M_ACTOR values(10, "レーネ", 5, 0);
insert into M_ACTOR values(11, "メルティナ", 61, 0);
insert into M_ACTOR values(12, "エリオ", 63, 0);
insert into M_ACTOR values(13, "タルト", 64, 0);
insert into M_ACTOR values(14, "マリク", 70, 0);
insert into M_ACTOR values(15, "スミレ", 8, 0);

insert into M_ACTOR values(901, "兵士A", 903, 5);
insert into M_ACTOR values(902, "兵士B", 903, 6);
insert into M_ACTOR values(903, "兵隊長", 904, 0);

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

insert into T_ACTOR_STATUS values(0, 1, 0, 1, 1, 1, 1, 0, 1, 1, 0, 0);

insert into T_ACTOR_STATUS values(1, 1, 0, 100, 60, 30, 100, 0, 5, 1, 3000, 2001);
insert into T_ACTOR_STATUS values(2, 1, 0, 100, 30, 20, 100, 0, 5, 1, 0, 2003);
insert into T_ACTOR_STATUS values(3, 1, 0, 100, 10, 10,  40, 0, 4, 1, 0, 0);
insert into T_ACTOR_STATUS values(4, 1, 0, 100, 10, 10,  40, 0, 4, 1, 0, 0);
insert into T_ACTOR_STATUS values(5, 1, 0, 100, 30, 20, 100, 0, 5, 1, 0, 0);
insert into T_ACTOR_STATUS values(6, 1, 0, 100, 30, 20, 100, 0, 5, 1, 0, 0);
insert into T_ACTOR_STATUS values(7, 1, 0, 100, 30, 20, 100, 0, 5, 1, 0, 0);
insert into T_ACTOR_STATUS values(8, 1, 0, 100, 30, 20, 100, 0, 5, 1, 0, 0);
insert into T_ACTOR_STATUS values(9, 1, 0, 100, 30, 20, 100, 0, 5, 1, 0, 0);
insert into T_ACTOR_STATUS values(10, 1, 0, 100, 30, 20, 100, 0, 5, 1, 0, 0);
insert into T_ACTOR_STATUS values(11, 1, 0, 100, 30, 20, 100, 0, 5, 1, 0, 0);
insert into T_ACTOR_STATUS values(12, 1, 0, 100, 45, 35, 100, 0, 5, 1, 0, 2002);
insert into T_ACTOR_STATUS values(13, 1, 0, 100, 30, 20, 100, 0, 5, 1, 0, 0);
insert into T_ACTOR_STATUS values(14, 1, 0, 100, 30, 20, 100, 0, 5, 1, 0, 0);
insert into T_ACTOR_STATUS values(15, 1, 0, 100, 30, 20, 100, 0, 5, 1, 0, 0);

insert into T_ACTOR_STATUS values(901, 1, 0, 50, 20, 10, 100, 0, 4, 1, 0, 0);
insert into T_ACTOR_STATUS values(902, 1, 0, 50, 20, 10, 100, 0, 4, 1, 0, 0);
insert into T_ACTOR_STATUS values(903, 1, 0, 100, 30, 20, 100, 0, 5, 1, 0, 0);

insert into M_MAP_BATTLE_REWARD values(1, 1, 1, 100);
insert into M_MAP_BATTLE_REWARD values(2, 1, 2, 1000);
insert into M_MAP_BATTLE_REWARD values(3, 1, 3, 1000);
insert into M_MAP_BATTLE_REWARD values(4, 1, 3, 2003);
insert into M_MAP_BATTLE_REWARD values(5, 2, 1, 300);
insert into M_MAP_BATTLE_REWARD values(6, 2, 2, 5000);
insert into M_MAP_BATTLE_REWARD values(7, 3, 1, 500);
insert into M_MAP_BATTLE_REWARD values(8, 3, 2, 7000);
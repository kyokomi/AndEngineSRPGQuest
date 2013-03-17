
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

insert into M_ACTOR values(1, "アスリーン", 110);
insert into M_ACTOR values(2, "ラーティ・クルス", 34);
insert into M_ACTOR values(3, "スライム的な何か", 901);
insert into M_ACTOR values(4, "村長", 902);

insert into M_ITEM values(1, "レイピア", 3, 2, 1);
insert into M_ITEM values(2, "普通の指輪", 33, 3, 1);

insert into M_WEAPON values(1, 10, 90, 1, 1, 0, 0);
insert into M_ACCESSORY values(1, 10, 0, 0);

insert into T_ACTOR_STATUS values(1, 1, 0, 100, 60, 30, 100, 0, 5, 1, 1, 2);
insert into T_ACTOR_STATUS values(2, 1, 0, 100, 30, 20, 100, 0, 5, 1, 0, 0);
insert into T_ACTOR_STATUS values(3, 1, 0, 100, 10, 10,  40, 0, 4, 1, 0, 0);
insert into T_ACTOR_STATUS values(4, 1, 0, 100, 10, 10,  40, 0, 4, 1, 0, 0);

insert into M_MAP_BATTLE_REWARD values(1, 1, 1, 100);
insert into M_MAP_BATTLE_REWARD values(2, 1, 2, 1000);
insert into M_MAP_BATTLE_REWARD values(3, 2, 1, 300);
insert into M_MAP_BATTLE_REWARD values(4, 2, 2, 5000);
insert into M_MAP_BATTLE_REWARD values(5, 3, 1, 500);
insert into M_MAP_BATTLE_REWARD values(6, 3, 2, 7000);
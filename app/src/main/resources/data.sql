INSERT INTO _user(id, full_name, role, balance, username,password) VALUES
    (200, 'Sándor Nagy', 'ADMIN', 0, 'snagy', 'sn-secret'),
    (201, 'Péter Kovács', 'USER', 150000, 'pkovacs','pk-secret');

INSERT INTO property(id, name, description, category, owner_name) VALUES
    (1, 'Huszti Zoltán nagyfestő hitvalló, Betlehem Csillagai', 'Huszti Zoltán kortárs pap festőművész festménye. Vászonra festve. Absztrakt, Expresszionista stílusú.', 'ART', 'Dóra Selmeciné Szalay'),
    (2, 'Commodore 64', 'H1991-ben gyártott működőképes Commodore 64 számítógép', 'TECHNOLOGY','Boldizsár Szepesi'),
    (3, 'Vizsolyi Biblia', 'Károly Gáspár által fordított, 1590-ben nyomtatott Biblia másolata', 'ANTIQUITIES','Zsolt Török'),
    (4, '2 szobás panel lakás a József Attila lakótelepen', 'Jó állapotú, 66 nm-es 2 szobás panel lakás a József Attila lakótelepen, kis családok számára ideális.', 'APARTMAN', 'Kovács Árpád');

INSERT INTO personal_property(id, weight, width, height, depth) VALUES
    (1, 4000, 80, 120, 4),
    (2, 1200, 40, 8, 22),
    (3, 6000, 30, 20, 15);

INSERT INTO real_property(id, location, area_size) VALUES
    (4, 'Budapest, IX. kerület, Pöttyös utca 171', 66);

INSERT INTO auction_item(id, property_id, state, deposit, upset_price, price, min_bid_increment, start_bidding, end_bidding) VALUES
    (100, 1, 'IN_PROGRESS', 25000, 100000, 0, 10000, '2023-02-15T08:00:00', '2023-02-15T22:00:00'),
    (101, 3, 'NOT_STARTED', 0, 10000, 0, 1000, '2023-03-20T08:00:00', '2023-03-21T20:00:00');

ALTER TABLE property ALTER COLUMN ID RESTART WITH (SELECT MAX(ID) FROM property) + 1;
ALTER TABLE _user ALTER COLUMN ID RESTART WITH (SELECT MAX(ID) FROM _user) + 1;
ALTER TABLE auction_item ALTER COLUMN ID RESTART WITH (SELECT MAX(ID) FROM auction_item) + 1;

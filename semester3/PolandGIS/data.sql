SELECT UpdateGeometrySRID('wojew','geom',2180);
INSERT INTO Voivodeships (name,location)
SELECT woj_name,(ST_DUMP(geom)).geom::geometry(Polygon,2180) FROM wojew;

SELECT UpdateGeometrySRID('pow','geom',2180);
INSERT INTO Powiaty (voiv_gid,name,location)
SELECT Voivodeships.gid,pow.pow_name,(ST_DUMP(pow.geom)).geom::geometry(Polygon,2180) FROM pow INNER JOIN Voivodeships ON pow.woj_name = Voivodeships.name;

SELECT UpdateGeometrySRID('gminy','geom',2180);
INSERT INTO Municipalities (type,powiat_gid,name,location)
SELECT 2,Powiaty.gid,gminy.gm_name,(ST_DUMP(gminy.geom)).geom::geometry(Polygon,2180) FROM gminy INNER JOIN Powiaty on gminy.pow_name = Powiaty.name

SELECT UpdateGeometrySRID('miej','geom',2180);
INSERT INTO Settlements (type,mun_gid,name,location)
SELECT 2,Municipalities.gid,miej.city_name,(ST_DUMP(miej.geom)).geom::geometry(Polygon,2180) FROM miej INNER JOIN Municipalities on miej.gm_name = Municipalities.name

SELECT * FROM miej;
SELECT * FROM Settlements;
SELECT * FROM Powiaty;
SELECT * FROM Voivodeships;

INSERT INTO cadastres (nr,powiat_gid,location)
VALUES 
	(123,12,ST_GeomFromText('Polygon((0 0,10 0,10 10,0 10,0 0))',2180)),
	(142,13,ST_GeomFromText('Polygon((10 0,40 0,40 40,10 40,10 0))',2180)),
	(412,16,ST_GeomFromText('Polygon((0 0,-5 0,-5 -5,0 -5,0 0))',2180)),
	(54,33,ST_GeomFromText('POLYGON((9 11,9 20,0 20, 0 11, 9 11))',2180)),
	(25,23,ST_GeomFromText('POLYGON((-8 -12,-8 -30,0 -30, 0 -12,-8 -12))',2180))
;
SELECT * FROM cadastres ;

SELECT * FROM Settlements;
INSERT INTO Settlements(name,location)
values ('test',ST_GeomFromText('POLYGON((-50 -50,-50 50,50 50, 50 -50,-50 -50))',2180))

INSERT INTO Streets (type,name,prefix,location)
VALUES 
	('[ul.]','Mickiewicza','Adama',ST_GeomFromText('LINESTRING(0 0,1 1)',2180)),
	('[al.]','Długosza','Jana',ST_GeomFromText('LINESTRING(3 3,11 11)',2180)),
	('[pl.]','Kopernika','Mikołaja',ST_GeomFromText('LINESTRING(7 7,22 22)',2180)),
	('[ul.]','Długa','',ST_GeomFromText('LINESTRING(14 14,35 35)',2180))
; 
SELECT * FROM Streets  ;
UPDATE Streets SET settl_gid = (SELECT gid FROM Settlements WHERE name = 'test' LIMIT 1);
UPDATE Streets SET settl_gid = 55421 WHERE name = 'Mickiewicza';

INSERT INTO Buildings (street_gid,location)
VALUES 
	(4,ST_GeomFromText('Polygon((0 0,0 1,1 1,1 0,0 0))',2180)),
	(4,ST_GeomFromText('Polygon((1 1,4 1,4 4,1 4,1 1))',2180)),
	(1,ST_GeomFromText('Polygon((1 1,1 1,1 1,1 1,1 1))',2180)),
	(1,ST_GeomFromText('Polygon((5 5,6 5,6 6,5 6,5 5))',2180)),
	(2,ST_GeomFromText('Polygon((12 12,13 12,13 13,12 13,12 12))',2180)),
	(2,ST_GeomFromText('Polygon((17 17,18 17,18 18,17 18,17 17))',2180)),
	(3,ST_GeomFromText('Polygon((33 33,34 33,34 34,33 34,33 33))',2180))
;
SELECT * FROM Buildings ;



INSERT INTO Addresses (nr,street_gid,location)
VALUES 
	(12,1,ST_GeomFromText('POINT(0 0)',2180)),
	(10,1,ST_GeomFromText('POINT(2 20)',2180)),
	(8,2,ST_GeomFromText('POINT(20 2)',2180)),
	(6,3,ST_GeomFromText('POINT(5 10)',2180)),
	(4,4,ST_GeomFromText('POINT(10 5)',2180))
;
SELECT * FROM Addresses ;


SELECT * FROM getStreetsWithinSettlement('Point(-49 -49)');
SELECT * FROM getNearbyAddresses('Point(11 6)',10);
SELECT * FROM getConcatSettlements('Point(6278131.2 5642745.7)',';');
SELECT * FROM getConcatVoivodeships('Point(6278131.2 5642745.7)',';');


CREATE OR REPLACE FUNCTION getNearbyPowiaty(geom text) RETURNS TABLE (
	gid integer,
	name text,
	voiv_gid integer,
	location geometry
) AS $$ 
	SELECT * FROM Powiaty
	WHERE ST_Intersects(location,ST_GeomFromText(geom,2180))
$$LANGUAGE SQL;

CREATE OR REPLACE FUNCTION getConcatPowiaty(geom text,delim text) RETURNS TABLE (address text) AS $$ 
SELECT string_agg(A.name::text,delim) FROM (SELECT * FROM getNearbyPowiaty(geom)) AS A
$$LANGUAGE SQL;


CREATE OR REPLACE FUNCTION getNearbyCadastres(geom text) RETURNS TABLE (
	gid integer,
	nr integer,
	powiat_gid integer,
	location geometry
) AS $$ 
	SELECT * FROM Cadastres
	WHERE ST_Intersects(location,ST_GeomFromText(geom,2180))
$$LANGUAGE SQL;

CREATE OR REPLACE FUNCTION getConcatCadastres(geom text,delim text) RETURNS TABLE (address text) AS $$ 
SELECT string_agg(A.nr::text,delim) FROM (SELECT * FROM getNearbyCadastres(geom)) AS A
$$LANGUAGE SQL;

--Powiaty

CREATE OR REPLACE FUNCTION VoivRelTrig() RETURNS trigger AS $Trig$ 
BEGIN
	IF( NEW.voiv_gid IS NOT NULL) THEN	
		IF(NOT (SELECT ST_Intersects(
			NEW.location,
			(SELECT location FROM Voivodeships WHERE gid = NEW.voiv_gid LIMIT 1)
			)) 
		)THEN
			if TG_OP='UPDATE' then
				NEW.voiv_gid = OLD.voiv_gid;
			ELSE
				NEW.voiv_gid = NULL;
			END IF;
		END IF;
	END IF;
	RETURN NEW;
	
END;
$Trig$ LANGUAGE plpgsql;

DROP TRIGGER IF EXISTS PowiatyInsertTrig ON Powiaty;
DROP TRIGGER IF EXISTS PowiatyUpdateTrig ON Powiaty;
CREATE TRIGGER PowiatyInsertTrig BEFORE INSERT ON Powiaty
	FOR EACH ROW EXECUTE PROCEDURE VoivRelTrig();
CREATE TRIGGER PowiatyUpdateTrig BEFORE UPDATE ON Powiaty
	FOR EACH ROW EXECUTE PROCEDURE VoivRelTrig();

--Gminy i działki 

CREATE OR REPLACE FUNCTION PowRelTrig() RETURNS trigger AS $Trig$ 
BEGIN
	IF( NEW.powiat_gid IS NOT NULL) THEN	
		IF(NOT (SELECT ST_Intersects(
			NEW.location,
			(SELECT location FROM Powiaty WHERE gid = NEW.powiat_gid LIMIT 1)
			)) 
		)THEN
			if TG_OP='UPDATE' then
				NEW.powiat_gid = OLD.powiat_gid;
			ELSE
				NEW.powiat_gid = NULL;
			END IF;
		END IF;
	END IF;
	RETURN NEW;
	
END;
$Trig$ LANGUAGE plpgsql;

DROP TRIGGER IF EXISTS MunicipalitiesInsertTrig ON Municipalities;
DROP TRIGGER IF EXISTS MunicipalitiesUpdateTrig ON Municipalities;
CREATE TRIGGER MunicipalitiesInsertTrig BEFORE INSERT ON Municipalities
	FOR EACH ROW EXECUTE PROCEDURE PowRelTrig();
CREATE TRIGGER MunicipalitiesUpdateTrig BEFORE UPDATE ON Municipalities
	FOR EACH ROW EXECUTE PROCEDURE PowRelTrig();
	
DROP TRIGGER IF EXISTS CadastresInsertTrig ON Cadastres;
DROP TRIGGER IF EXISTS CadastresUpdateTrig ON Cadastres;
CREATE TRIGGER CadastresInsertTrig BEFORE INSERT ON Cadastres
	FOR EACH ROW EXECUTE PROCEDURE PowRelTrig();
CREATE TRIGGER CadastresUpdateTrig BEFORE UPDATE ON Cadastres
	FOR EACH ROW EXECUTE PROCEDURE PowRelTrig();



--Miejscowości

CREATE OR REPLACE FUNCTION MunRelTrig() RETURNS trigger AS $Trig$ 
BEGIN
	IF( NEW.mun_gid IS NOT NULL) THEN	
		IF(NOT (SELECT ST_Intersects(
			NEW.location,
			(SELECT location FROM Municipalities WHERE gid = NEW.mun_gid LIMIT 1)
			)) 
		)THEN
			if TG_OP='UPDATE' then
				NEW.mun_gid = OLD.mun_gid;
			ELSE
				NEW.mun_gid = NULL;
			END IF;
		END IF;
	END IF;
	RETURN NEW;
	
END;
$Trig$ LANGUAGE plpgsql;

DROP TRIGGER IF EXISTS SettlementsInsertTrig ON Settlements;
DROP TRIGGER IF EXISTS SettlementsUpdateTrig ON Settlements;
CREATE TRIGGER SettlementsInsertTrig BEFORE INSERT ON Settlements
	FOR EACH ROW EXECUTE PROCEDURE MunRelTrig();
CREATE TRIGGER SettlementsUpdateTrig BEFORE UPDATE ON Settlements
	FOR EACH ROW EXECUTE PROCEDURE MunRelTrig();

--ulice

CREATE OR REPLACE FUNCTION SettlRelTrig() RETURNS trigger AS $Trig$ 
BEGIN
	IF( NEW.settl_gid IS NOT NULL) THEN	
		IF(NOT (SELECT ST_Intersects(
			NEW.location,
			(SELECT location FROM Settlements WHERE gid = NEW.settl_gid LIMIT 1)
			)) 
		)THEN
			if TG_OP='UPDATE' then
				NEW.settl_gid = OLD.settl_gid;
			ELSE
				NEW.settl_gid = NULL;
			END IF;
		END IF;
	END IF;
	RETURN NEW;
	
END;
$Trig$ LANGUAGE plpgsql;

DROP TRIGGER IF EXISTS StreetsInsertTrig ON Streets;
DROP TRIGGER IF EXISTS StreetsUpdateTrig ON Streets;
CREATE TRIGGER StreetsInsertTrig BEFORE INSERT ON Streets
	FOR EACH ROW EXECUTE PROCEDURE SettlRelTrig();
CREATE TRIGGER StreetsUpdateTrig BEFORE UPDATE ON Streets
	FOR EACH ROW EXECUTE PROCEDURE SettlRelTrig();


--budynki --Adresy

CREATE OR REPLACE FUNCTION StrtRelTrig() RETURNS trigger AS $Trig$ 
BEGIN
	IF( NEW.street_gid IS NOT NULL) THEN	
		IF(NOT (SELECT ST_Intersects(
			NEW.location,(
				SELECT Settlements.location 
				FROM Streets INNER JOIN Settlements ON Streets.settl_gid = Settlements.gid
				WHERE Streets.gid = NEW.street_gid 
				LIMIT 1
			)
		)))THEN
			if TG_OP='UPDATE' then
				NEW.street_gid = OLD.street_gid;
			ELSE
				NEW.street_gid = NULL;
			END IF;
		END IF;
	END IF;
	RETURN NEW;
	
END;
$Trig$ LANGUAGE plpgsql;
DROP TRIGGER IF EXISTS BuildingsInsertTrig ON Streets;
DROP TRIGGER IF EXISTS BuildingsUpdateTrig ON Streets;
CREATE TRIGGER BuildingsInsertTrig BEFORE INSERT ON Buildings
	FOR EACH ROW EXECUTE PROCEDURE StrtRelTrig();
CREATE TRIGGER BuildingsUpdateTrig BEFORE UPDATE ON Buildings
	FOR EACH ROW EXECUTE PROCEDURE StrtRelTrig();
	
DROP TRIGGER IF EXISTS AddressesInsertTrig ON Addresses;
DROP TRIGGER IF EXISTS AddressesUpdateTrig ON Addresses;
CREATE TRIGGER AddressesInsertTrig BEFORE INSERT ON Addresses
	FOR EACH ROW EXECUTE PROCEDURE StrtRelTrig();
CREATE TRIGGER AddressesUpdateTrig BEFORE UPDATE ON Addresses
	FOR EACH ROW EXECUTE PROCEDURE StrtRelTrig();
	
	
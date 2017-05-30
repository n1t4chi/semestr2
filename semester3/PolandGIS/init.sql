DROP TABLE IF EXISTS Voivodeships CASCADE;
DROP TABLE IF EXISTS Powiaty CASCADE;
DROP TABLE IF EXISTS Municipalities CASCADE;
DROP TABLE IF EXISTS Settlements CASCADE;
DROP TABLE IF EXISTS Streets CASCADE;
DROP TABLE IF EXISTS Cadastres CASCADE;
DROP TABLE IF EXISTS Buildings CASCADE;
DROP TABLE IF EXISTS Addresses CASCADE;
DROP TABLE IF EXISTS MunicipalitiesType CASCADE;
DROP TABLE IF EXISTS SettlementsType CASCADE;
/*
DELETE FROM Voivodeships;
DELETE FROM Powiaty;
DELETE FROM Municipalities;
DELETE FROM Settlements;
DELETE FROM Streets;
DELETE FROM Cadastres;
DELETE FROM Buildings;
DELETE FROM Addresses;
*/
CREATE TABLE Voivodeships (
	gid serial,
	name text,
	--location GEOGRAPHY(POLYGON,4326),
	primary key(gid)
);
SELECT AddGeometryColumn('voivodeships', 'location', 2180, 'POLYGON', 2, true);
CREATE TABLE Powiaty (
	gid serial,
	name text,
	voiv_gid integer references Voivodeships(gid) ON DELETE SET NULL,
	primary key(gid)
);
SELECT * FROM powiaty
SELECT AddGeometryColumn('powiaty', 'location', 2180, 'POLYGON', 2, true);
CREATE TABLE SettlementsType (
	gid serial,
	name text,
	primary key(gid)
);
CREATE TABLE MunicipalitiesType (
	gid serial,
	name text,
	primary key(gid)
);
INSERT INTO MunicipalitiesType (gid,name)
VALUES (1,'gmina miejska'),(2,'gmina wiejska'),(3,'gmina miejsko-wiejska');
INSERT INTO SettlementsType (name)
VALUES 	('wieś'),
	('miasto'),
	('część kolonii'),
	('część miasta'),
	('część osady'),
	('część wsi'),
	('kolonia'),
	('kolonia kolonii'),
	('kolonia osady'),
	('kolonia wsi'),
	('osada'),
	('osada kolonii'),
	('osada leśna'),
	('osada leśna wsi'),
	('osada osady'),
	('osada wsi'),
	('osiedle'),
	('osiedle wsi'),
	('przysiółek'),
	('przysiółek kolonii'),
	('przysiółek osady'),
	('przysiółek wsi'),
	('schronisko turystyczne');
SELECT * FROM MunicipalitiesType ;
SELECT * FROM SettlementsType ;
CREATE TABLE Municipalities (
	gid serial,
	name text,
	type integer references MunicipalitiesType(gid) ON DELETE SET NULL,
	powiat_gid integer references Powiaty(gid) ON DELETE SET NULL,
	--location GEOGRAPHY(POLYGON,4326),
	primary key(gid)
);
SELECT AddGeometryColumn('municipalities', 'location', 2180, 'POLYGON', 2, true);
CREATE TABLE Settlements (
	gid serial,
	name text,
	type integer references SettlementsType(gid) ON DELETE SET NULL,
	mun_gid integer references Municipalities(gid) ON DELETE SET NULL,
	--location GEOGRAPHY(POLYGON,4326),
	primary key(gid)
);
SELECT AddGeometryColumn('settlements', 'location', 2180, 'POLYGON', 2, true);
CREATE TABLE Streets (
	gid serial,
	type text,
	prefix text,
	name text,
	suffix text,
	settl_gid integer references Settlements(gid) ON DELETE SET NULL,
	--location GEOGRAPHY(LINESTRING,4326),
	primary key(gid)
);
SELECT AddGeometryColumn('streets', 'location', 2180, 'LINESTRING', 2, true);
CREATE TABLE Cadastres (
	gid serial,
	nr integer,
	powiat_gid integer references Powiaty(gid) ON DELETE SET NULL,
	--location GEOGRAPHY(POLYGON,4326),
	primary key(gid)
);
SELECT AddGeometryColumn('cadastres', 'location', 2180, 'POLYGON', 2, true);
CREATE TABLE Buildings (
	gid serial,
	street_gid integer references Streets(gid) ON DELETE SET NULL,
	--location GEOGRAPHY(POLYGON,4326),
	primary key(gid)
);
SELECT AddGeometryColumn('buildings', 'location', 2180, 'POLYGON', 2, true);
CREATE TABLE Addresses (
	gid serial,
	nr integer,
	street_gid integer references Streets(gid) ON DELETE SET NULL,
	--location GEOGRAPHY(POINT,4326),
	primary key(gid)
);
SELECT AddGeometryColumn('addresses', 'location', 2180, 'POINT', 2, true);



--funkcje
/*


DROP FUNCTION getSettlement(text);
DROP FUNCTION getAddressesWithinCadastres(text);
DROP FUNCTION getAddressesWithinBuildings(text);
DROP FUNCTION getstreetswithinsettlement(text);
DROP FUNCTION getNearbyAddresses(text,float);
DROP FUNCTION getConcatAddresses(text,float,text);
DROP FUNCTION getNearbyStreets(text,float);
DROP FUNCTION getConcatFullAddresses(text,float,text);
DROP FUNCTION getNearbySettlements(text);
DROP FUNCTION getConcatSettlements(text,text);


*/




--ulice

CREATE OR REPLACE FUNCTION getSettlement(geom text) RETURNS TABLE (gid integer) AS $$ 
	SELECT gid FROM Settlements
	WHERE ST_Intersects(location,ST_GeomFromText(geom,2180))
$$LANGUAGE SQL;

DROP FUNCTION getstreetswithinsettlement(text);
CREATE OR REPLACE FUNCTION getStreetsWithinSettlement(geom text) RETURNS TABLE (
	gid integer,
	type text,
	prefix text,
	name text,
	suffix text,
	settl_gid integer,
	location geometry
) AS $$ 
	SELECT * FROM Streets
	WHERE 
		settl_gid IN (SELECT gid FROM getSettlement(geom)) 
		OR
		ST_Distance(location,ST_GeomFromText(geom,2180))<200
$$LANGUAGE SQL;

CREATE OR REPLACE FUNCTION getNearbyStreets(geom text,range float) RETURNS TABLE (
	gid integer,
	type text,
	prefix text,
	name text,
	suffix text,
	settl_gid integer,
	location geometry
) AS $$ 
	SELECT * FROM Streets
	WHERE ST_Distance(location,ST_GeomFromText(geom,2180))<range
$$LANGUAGE SQL;



CREATE OR REPLACE FUNCTION getConcatStreets(geom text,range float,delim text) RETURNS TABLE (streets text) AS $$ 
SELECT string_agg(name,delim) FROM (SELECT * FROM getNearbyStreets(geom,range)) AS A
$$LANGUAGE SQL;

CREATE OR REPLACE FUNCTION getConcatFullStreets(geom text,range float,delim text) RETURNS TABLE (streets text) AS $$ 
SELECT string_agg(concat_ws(' ',type,prefix,name,suffix),delim) FROM (SELECT * FROM getNearbyStreets(geom,range)) AS A
$$LANGUAGE SQL;



--adresy

CREATE OR REPLACE FUNCTION getAddressesWithinCadastres(geom text) RETURNS TABLE (gid integer) AS $$ 
	SELECT Addresses.gid FROM Addresses 
	INNER JOIN Cadastres ON ST_Intersects(Addresses.location,Cadastres.location)
	WHERE ST_Intersects(Cadastres.location,ST_GeomFromText(geom,2180))
$$LANGUAGE SQL;

CREATE OR REPLACE FUNCTION getAddressesWithinBuildings(geom text) RETURNS TABLE (gid integer) AS $$ 
	SELECT Addresses.gid FROM Addresses 
	INNER JOIN Buildings ON ST_Intersects(Addresses.location,Buildings.location)
	WHERE ST_Intersects(Buildings.location,ST_GeomFromText(geom,2180))
$$LANGUAGE SQL;


CREATE OR REPLACE FUNCTION getNearbyAddresses(geom text,range float) RETURNS TABLE (
	gid integer,
	nr integer,
	street_gid integer,
	location geometry
) AS $$ 
	SELECT * FROM Addresses
	WHERE 
		gid IN (SELECT gid FROM getAddressesWithinBuildings(geom))
	OR
		gid IN (SELECT gid FROM getAddressesWithinCadastres(geom))
	OR
		ST_Distance(Addresses.location,ST_GeomFromText(geom,2180))<range

$$LANGUAGE SQL;


CREATE OR REPLACE FUNCTION getConcatAddresses(geom text,range float,delim text) RETURNS TABLE (address text) AS $$ 
SELECT string_agg(A.nr::text,delim) FROM (SELECT * FROM getNearbyAddresses(geom,range)) AS A
$$LANGUAGE SQL;

--pełne adresy
CREATE OR REPLACE FUNCTION getConcatFullAddresses(geom text,range float,delim text) RETURNS TABLE (address text) AS $$ 
SELECT string_agg(concat_ws(' ',nr,strn,setn),delim) FROM (
	SELECT A.nr AS nr,Streets.name AS strn,Settlements.name AS setn FROM getNearbyAddresses(geom,range) AS A 
	INNER JOIN Streets ON A.street_gid = Streets.gid
	INNER JOIN Settlements ON Streets.settl_gid = Settlements.gid
) AS AA
$$LANGUAGE SQL;

--Miasta
CREATE OR REPLACE FUNCTION getNearbySettlements(geom text) RETURNS TABLE (
	gid integer,
	name text,
	type integer,
	mun_gid integer,
	location geometry
) AS $$ 
	SELECT * FROM Settlements
	WHERE ST_Intersects(location,ST_GeomFromText(geom,2180))
$$LANGUAGE SQL;

CREATE OR REPLACE FUNCTION getConcatSettlements(geom text,delim text) RETURNS TABLE (address text) AS $$ 
SELECT string_agg(A.name::text,delim) FROM (SELECT * FROM getNearbySettlements(geom)) AS A
$$LANGUAGE SQL;

--powiaty
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


--działki 
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


--województwa

CREATE OR REPLACE FUNCTION public.getnearbyvoivodeships(IN geom text)
  RETURNS TABLE(gid integer, name text, location geometry) AS
$BODY$ 
	SELECT * FROM Voivodeships
	WHERE ST_Intersects(location,ST_GeomFromText(geom,2180))
$BODY$
  LANGUAGE sql VOLATILE
  COST 100
  ROWS 1000;
ALTER FUNCTION public.getnearbyvoivodeships(text)
  OWNER TO postgres;

CREATE OR REPLACE FUNCTION public.getconcatvoivodeships(
    IN geom text,
    IN delim text)
  RETURNS TABLE(address text) AS
$BODY$ 
SELECT string_agg(A.name::text,delim) FROM (SELECT * FROM getNearbyVoivodeships(geom)) AS A
$BODY$
  LANGUAGE sql VOLATILE
  COST 100
  ROWS 1000;
ALTER FUNCTION public.getconcatvoivodeships(text, text)
  OWNER TO postgres;


--gminy
CREATE OR REPLACE FUNCTION public.getnearbymunicipalities(IN geom text)
  RETURNS TABLE(gid integer, name text, type integer, powiat_gid integer, location geometry) AS
$BODY$ 
	SELECT * FROM Municipalities
	WHERE ST_Intersects(location,ST_GeomFromText(geom,2180))
$BODY$
  LANGUAGE sql VOLATILE
  COST 100
  ROWS 1000;
ALTER FUNCTION public.getnearbymunicipalities(text)
  OWNER TO postgres;

  
CREATE OR REPLACE FUNCTION public.getconcatmunicipalities(
    IN geom text,
    IN delim text)
  RETURNS TABLE(address text) AS
$BODY$ 
SELECT string_agg(A.name::text,delim) FROM (SELECT * FROM getNearbyMunicipalities(geom)) AS A
$BODY$
  LANGUAGE sql VOLATILE
  COST 100
  ROWS 1000;
ALTER FUNCTION public.getconcatmunicipalities(text, text)
  OWNER TO postgres;


--

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

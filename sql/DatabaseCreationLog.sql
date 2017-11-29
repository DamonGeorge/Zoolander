--liquibase formatted sql

--changeset damon:1
--basic create table statements
Set sql_mode = STRICT_ALL_TABLES;

DROP TABLE IF EXISTS species_eats;
DROP TABLE IF EXISTS employee_training;
DROP TABLE IF EXISTS animal;
DROP TABLE IF EXISTS species;
DROP TABLE IF EXISTS user;
DROP TABLE IF EXISTS enclosure;
DROP TABLE IF EXISTS food;

CREATE TABLE user (
	username 	VARCHAR(255) 	NOT NULL,
	first_name 	VARCHAR(255) 	NOT NULL,
	last_name 	VARCHAR(255) 	NOT NULL,
	birthday	DATE 			NOT NULL,
	email 		VARCHAR(255) 	NOT NULL,
	password 	CHAR(60) 		NOT NULL,  
	salary 		DOUBLE(12,2) 	NOT NULL,
	active		BOOLEAN 		NOT NULL,
	admin 		BOOLEAN 		NOT NULL,
	PRIMARY KEY (username)
) ENGINE=InnoDB;

CREATE Table enclosure (
	enclosure_id INT NOT NULL AUTO_INCREMENT,
	name 		VARCHAR(255) 	NOT NULL,
	environment	VARCHAR(255) 	NOT NULL,
	open 		BOOLEAN 		NOT NULL,
	PRIMARY KEY (enclosure_id)
)ENGINE=InnoDB;

CREATE TABLE species (
	species_name 	VARCHAR(255) NOT NULL,
	common_name 	VARCHAR(255) NOT NULL,
	enclosure_id 	INT 		NOT NULL,
	description 	VARCHAR(1023) NOT NULL,					
	PRIMARY KEY (species_name),		-- ???? or id???
	FOREIGN KEY (enclosure_id) REFERENCES enclosure(enclosure_id)
)ENGINE=InnoDB;

CREATE TABLE animal (
	animal_id INT NOT NULL AUTO_INCREMENT,
	name VARCHAR(255) NOT NULL,
	description VARCHAR(1023),
	species_name VARCHAR(255) NOT NULL,
	birthday	DATE NOT NULL,
	food_quantity INT NOT NULL, 
	last_feeding DATETIME NOT NULL, 
	PRIMARY KEY (animal_id),
	FOREIGN KEY (species_name) REFERENCES species(species_name)
)ENGINE=InnoDB;

CREATE TABLE food (
	food_id INT NOT NULL AUTO_INCREMENT,
	name VARCHAR(255) NOT NULL,
	brand VARCHAR(255) NOT NULL,
	cost DOUBLE NOT NULL,
	quantity DOUBLE NOT NULL, 
	nutritional_index DOUBLE NOT NULL,
	PRIMARY KEY (food_id)
)ENGINE=InnoDB;

CREATE TABLE employee_training (
	username 		VARCHAR(255) NOT NULL,
	species_name 	VARCHAR(255) NOT NULL,
	date_trained	DATE 		NOT NULL,
	years_to_renew	INT 		NOT NULL,
	PRIMARY KEY (username, species_name),
	FOREIGN KEY (username) REFERENCES user(username),
	FOREIGN KEY (species_name) REFERENCES species(species_name)
)ENGINE=InnoDB;

CREATE TABLE species_eats (
	species_name VARCHAR(255) NOT NULL,
	food_id		 INT NOT NULL,
	PRIMARY KEY (species_name, food_id),
	FOREIGN KEY (species_name) REFERENCES species(species_name),
	FOREIGN KEY (food_id) REFERENCES food(food_id)
)ENGINE=InnoDB;


--changeset damon:2
--som
INSERT INTO user VALUES 
('tdog','Anthony','Niehuser', '1996-09-09', 'tdog@gmail.com', '$2a$10$c.GIMd7DRCdrt4EmRj.dYuxDd9kwnIgbalixiCZJWD6k2CgIq/o/a', 1000000.00, 1, 1),
('bro','Damon','George','1995-06-04', 'dlkj@ldkjf','$2a$10$c.GIMd7DRCdrt4EmRj.dYuxDd9kwnIgbalixiCZJWD6k2CgIq/o/a', 50000, 1, 0);

INSERT INTO enclosure VALUES 
(1, 'Arctic Enclosure', 'Arctic', 1);

INSERT INTO species VALUES 
('Spheniscidae', 'Penguin', 1, 'Pretty cute huh?');

INSERT INTO animal VALUES 
(1, 'Charlie', 'Charlie enjoys long walks on the beach', 'Spheniscidae', '2012-02-04', 3, '2017-11-15 12:00:00');

INSERT INTO food VALUES 
(1, 'Fruity Pebbles', 'Kellogs', 20.00, 50000, 0.5);

INSERT INTO employee_training VALUES 
('tdog', 'Spheniscidae', '2015-06-24', 4);

INSERT INTO species_eats VALUES 
('Spheniscidae', 1);



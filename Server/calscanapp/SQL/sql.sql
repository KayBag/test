DROP TABLE IF EXISTS users_items;
DROP TABLE IF EXISTS items;
DROP TABLE IF EXISTS users;

CREATE TABLE users (
	username varchar(50) PRIMARY KEY,
	password_hash varchar(32) NOT NULL
);

CREATE TABLE items (
	upc_code varchar(20) PRIMARY KEY,
	name varchar(50) NOT NULL,
	description varchar(500) NOT NULL,
	nutrition varchar(500) NOT NULL,
	energy integer,
	image varchar(100),
	ingredients varchar(500)
);

CREATE TABLE users_items (
	username varchar(50),
	upc_code varchar(20),
	rating DECIMAL(1),
	PRIMARY KEY(username,upc_code),
	FOREIGN KEY(username) REFERENCES users(username),
	FOREIGN KEY(upc_code) REFERENCES items(upc_code)
);


<?php

/*
 * Conor Killeen
 * 5/4/2014
 * 
 * This file contains all of the information to connect ot the database.
 */

	$mysql_host = 'localhost';
	$mysql_user = 'root';
	$mysql_pass = '';
	$mysql_db = 'dbtest';

	if(!@mysql_connect($mysql_host,$mysql_user,$mysql_pass) || !@mysql_select_db($mysql_db)){
		die(mysql_error());
	}
?>
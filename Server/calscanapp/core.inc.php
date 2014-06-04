<?php

/*
 * Conor Killeen
 * 5/4/2014
 * 
 * This file contains the logic for checking is a user is logged in.
 * It was mainly used for testing, because you cannot set a session variable with an android device.
 */

	ob_start();
	session_start();
	
	$current_file = $_SERVER['SCRIPT_NAME'];
	
	if(isset($_SERVER['HTTP_REFERER'])) {
		$http_referer = $_SERVER['HTTP_REFERER'];
	}
	
	
	function loggedin() {
		if(isset($_SESSION['user_id']) && !empty($_SESSION['user_id'])) {
			return true;
		} else {
			return false;
		}
	}
?>
<?php

/*
 * Conor Killeen
 * 5/4/2014
 * 
 * Destroys session
 */

	require 'core.inc.php';
	
	session_destroy();
	header('Location: '.$http_referer);
?>
<?php

/*
 * Conor Killeen
 * 5/4/2014
 * 
 * This code is the partially RESTful API.
 */

	require 'connect.inc.php';
	require 'core.inc.php';
	require 'Items.php';
	include 'ChromePhp.php';
	
	function get_data($username,$code) {
		$app_key = "4xQ9FQinLQ1";
		$auth_key = "Ny08C5f1c0Ra4Qq8";
		
		/* 
		 * Each request to the Digit Eyes for information must be signed. 
		 * The signiture is an encrypted version of the UPC code that is
		 * SHA1 hashed with the authorisation key
		*/
		$signature = base64_encode(hash_hmac('sha1', $code, $auth_key, $raw_output = true));
		
		$itemInsert = new Items();
		
		/* 
		 * Checks if item already exists in database.
		 * If item does not, the information is requested from the API
		*/
		if($itemInsert->itemExist($code) == false){
			try{
				$url = "https://www.digit-eyes.com/gtin/v2_0/?upcCode={$code}&field_names=all&language=en&app_key=/{$app_key}&signature={$signature}";
					
				$ch = curl_init();
				$timeout = 5;
					
				curl_setopt($ch, CURLOPT_URL, $url);
				curl_setopt($ch, CURLOPT_RETURNTRANSFER, 1);
				curl_setopt($ch, CURLOPT_CONNECTTIMEOUT, $timeout);
				curl_setopt($ch, CURLOPT_SSLVERSION, 3);
				curl_setopt($ch, CURLOPT_SSL_VERIFYPEER, true);
				curl_setopt($ch, CURLOPT_SSL_VERIFYHOST, 2);
				curl_setopt($ch, CURLOPT_CAINFO, dirname(__FILE__)."/cacert.pem");
					
				$data = curl_exec($ch);
				
				if (FALSE === $data)
					throw new Exception(curl_error($ch), curl_errno($ch));
					
				curl_close($ch);
				$json = json_decode($data, true);
				
				//If request was sucessful, insert item into item table
				if (!isset($json['return_message']) || (isset($json['return_message']) && $json['return_message'] == "success")){
					$result = $itemInsert -> insert($json, $username);
					if($result != false)
						$result = $itemInsert -> userItemInsert($json,$username);
				}
				
			}catch(Exception $e) {
				trigger_error(sprintf('Curl failed with error #%d: %s', $e->getCode(), $e->getMessage()), E_USER_ERROR);
			}
			
			return array("result" => $result);
		}else{
			$json = $itemInsert->getItem($code);
			$result = $itemInsert -> userItemInsert($json,$username);
			
			return array("result" => $result);
		}
		
		$itemInsert = null;
		
	}
	
	//Returns list of user items
	function get_barcode($username)
	{
		$itemInsert = new Items();
		$result = $itemInsert->getUserItems($username);
		$itemInsert = null;
		$itemInsert = null;
		return $result;
	}
	
	//Remove item from user list
	function delete($code,$username)
	{
		$itemInsert = new Items();
		$result = $itemInsert->deleteUserItem($code,$username);
		$itemInsert = null;
		
		return array("delete" => $result);
	}
	
	//Rates a user item
	function rate($code,$username)
	{
		$itemInsert = new Items();
		$result = $itemInsert->addUserRating($code,$username);
		$itemInsert = null;
		
		return array("rate" => $result);
	}
	
	function login($username, $password)
	{
		$password_hash = md5($password);
	
		$query = "SELECT username FROM users WHERE username='$username' AND password_hash='$password_hash'";
			
		if($query_run = mysql_query($query)) {
			
			$query_num = mysql_num_rows($query_run);
			
			//Return username if username exists
			if ($query_num == 1) {
				$_SESSION['user_id'] = mysql_result($query_run, 0, 'username');
				
				return array("login" => $_SESSION['user_id']);
			}
		}
		
		return array("login" => "FAIL");
	}
	
	function register($username, $password)
	{
		$password_hash = md5($password);
	
		$query = "SELECT * FROM users WHERE username LIKE '$username'";
		$query_run = mysql_query($query);
		$query_num = mysql_num_rows($query_run);
		
		if($query_num == 0){
			$insert = "INSERT INTO users (username, password_hash) VALUES ('$username','$password_hash')";
			$insert_run = mysql_query($insert);
			$_SESSION['user_id']=$username;
			return array("login" => $_SESSION['user_id']);
		}
		
		return array("register" => "FAIL");
	}
	
	//Return list of suggests for user
	function suggest($username)
	{
		$itemInsert = new Items();
		
		$result = $itemInsert->suggest($username);
		$itemInsert = null;
		return $result;
	}

	$value = "An error has occurred";
	
	if (isset($_POST["action"]))
	{
		switch ($_POST["action"])
		{
			case "get_barcode":
				if (isset($_POST["username"])){
					$value = get_barcode($_POST["username"]);
					break;
				}
			case "suggest":
				if (isset($_POST["username"])){
					$value = suggest($_POST["username"]);
					break;
				}
			case "get_data":
				if (isset($_POST["code"])&& isset($_POST["username"])){
					$value = get_data($_POST["username"],$_POST["code"]);
					break;
				}
			case "delete":
				if (isset($_POST["code"])&& isset($_POST["username"])){
					$value = delete($_POST["code"],$_POST["username"]);
					break;
				}
			case "rate":
				if (isset($_POST["code"]) && isset($_POST["username"])){
					$value = rate($_POST["code"],$_POST["username"]);
					break;
				}
			case "login":
				if (isset($_POST["username"]) && isset($_POST["password"])){
					$value = login($_POST["username"], $_POST["password"]);
					break;
				}
			case "register":
				if (isset($_POST["username"]) && isset($_POST["password"])){
					$value = register($_POST["username"], $_POST["password"]);
					break;
				}
			else
				$value = "Missing argument";
				break;
		}
	}
	//return JSON array
	exit(json_encode($value));
?>
<?php

/*
 * Conor Killeen
 * 5/4/2014
 * 
 * This class is responsible for handleing all functions relating to an item.
 * This class also includes the function that generates all of the suggestions for a user.
 */

class Items {

	public function insert($json, $username) {
		
		$upc = $json['upc_code'];
		$name = $json['description'];
		$desc = $json['description'];
		$nut = $json['nutrition'];
		$img = $json['image'];
		$ing = $json['ingredients'];
		
		$chars = array('/','(',')');
		$new = str_replace($chars, ' ', $nut);
		
		if($this->itemExist($upc) == false){
			$energy = null;
			if($json['nutrition'] != null && $json['nutrition'] != ""){
				if($nut != null && $nut != "")
					$energy = $this->getNutrition($new);
			}
			
			$insert = "INSERT INTO items VALUES ('$upc','$name','$desc','$new','$energy','$img','$ing')";
			$insert_run = mysql_query($insert);
			return $insert_run;
		}
		return false;
	}
	
	public function suggest($username) {
		
		$rows = $this->getAllItems();
		$items = $this->getUserItems($username);
		$weights = $this->weight();
	
		$codes = array();
		
		for($i = 0;$i < count($items);$i++){
			array_push($codes,$items[$i]['upc_code']);
		}
		
		for($i = 0;$i < count($rows);$i++){
			
			$rows[$i]["count"] = 0;
			
			$rows[$i]['ingredients'] = $this->strip_punctuation($rows[$i]['ingredients']);
			
			$ing1 = array();
			$ing1 = explode(' ',$rows[$i]['ingredients']);
			
			if(!in_array($rows[$i]['upc_code'],$codes)){
				
				for($j = 0;$j < count($items);$j++){
					
					ChromePhp::log($rows[$i]['upc_code'] . " " . $items[$j]['upc_code']);
					
					$count = 0;
					
					$items[$j]['ingredients'] = $this->strip_punctuation($items[$j]['ingredients']);
					
					$ing2 = array();
					$ing2 = explode(' ',$items[$j]['ingredients']);
					
					for($k = 0;$k < count($ing2);$k++){
						$weight = 1;
						if(array_key_exists($ing2[$k], $weights))
							$weight = $weights[$ing2[$k]];
							ChromePhp::log($ing2[$k] . ": " . $weight);
						if(in_array($ing2[$k],$ing1))
							$count += 1/$weight;
							
					}
				
					if($items[$j]['rating'] == "0")
						$count *= 1/2;
						
					$rows[$i]["count"] += $count;
				}							
			}
		}
		
		$x = count($rows);
		for($i = 0;$i < $x;$i++){
			if($rows[$i]["count"] == 0)
				unset($rows[$i]);
		}
		usort($rows, array($this, 'sort_objects_by_count'));
		
		return $rows;
	}
	
	function strip_punctuation($string) {
		$chars = array('/','(',')');
		
	    $string = strtolower($string);
	    $string = str_replace($chars, "", $string);
	    $string = str_replace(",", " ", $string);
	    return $string;
	}
	
	public function weight() {
		
		$rows = $this->getAllItems();
		
		$ing1 = array();
		
		for($i = 0;$i < count($rows);$i++){
			$ing1 = array_merge($ing1,explode(',',$rows[$i]['ingredients']));
		}
		
		return array_count_values($ing1);
	}
	
	public function sort_objects_by_count($a, $b) {
		if($a['count'] == $b['count'])
			return 0; 
		return ($a['count'] < $b['count']) ? 1 : -1;
	}
	
	public function userItemInsert($json,$username) {
		
		$upc = $json['upc_code'];
		if($this->userItemExist($upc,$username) == false){
			$insert = "INSERT INTO users_items (username,upc_code) VALUES ('$username','$upc')";
			$insert_run = mysql_query($insert);
			
			return $insert_run;
		}else{
			return false;
		}
	}
	
	public function getAllItems() {
		$query = "SELECT * FROM items";

		$query_run = mysql_query($query);
		$query_num = mysql_num_rows($query_run);
		if($query_num != 0){
			while($row = mysql_fetch_assoc($query_run)) {
				$rows[] = $row;
			}
			return $rows;
		}else{
			return $query_run;
		}
	}
	
	public function getItem($upc) {
		$query = "SELECT * FROM items WHERE upc_code LIKE '$upc'";
		
		$query_run = mysql_query($query);
		$query_num = mysql_num_rows($query_run);
		if($query_num != 0){
			$row = mysql_fetch_assoc($query_run);
				
			return $row;
		}else{
			return false;
		}
	}
	
	public function getNutrition($nut) {
		
		$nut = $this->strip_punctuation($nut);
		$nuts = explode('-',$nut);
		
		$energy = explode(' ',$nuts[1]);
		
		for($i = 0;$i < count($energy);$i++){
			if(strpos($energy[$i],"kj") != false){
				$kj = $energy[$i];
				$kj = str_replace("kj", "", $kj);
				
				return $kj;
			}
		}
		
		ChromePhp::log($energy);
	}
	
	public function getUserItems($username) {
		
		$query = "SELECT i.*,ui.rating
				FROM users u
				JOIN users_items ui ON u.username=ui.username
				JOIN items i ON ui.upc_code=i.upc_code
				WHERE u.username LIKE '$username';";

		$query_run = mysql_query($query);
		$query_num = mysql_num_rows($query_run);
		if($query_num != 0){
			while($row = mysql_fetch_assoc($query_run)) {
					
				$rows[] = $row;
			}
			return $rows;
		}else{
			return false;
		}
		
		
	}
	
	public function getUserItem($username,$upc) {
		
		$query = "SELECT i.*,ui.rating
				FROM users u
				JOIN users_items ui ON u.username=ui.username
				JOIN items i ON ui.upc_code=i.upc_code
				WHERE u.username LIKE '$username'
				AND i.upc_code LIKE '$upc';";

		$query_run = mysql_query($query);
		$query_num = mysql_num_rows($query_run);
		if($query_num != 0){
			$row = mysql_fetch_assoc($query_run);
				
			return $row;
		}else{
			return false;
		}
		
	}
	
	public function addUserRating($upc,$username) {
		
		$row = $this->getUserItem($username,$upc);
		if(isset($row['rating'])){
			
			if($row['rating'] == "1")
				$update = "UPDATE users_items SET rating='0' WHERE upc_code LIKE '$upc' AND username LIKE '$username'";
			else 
				$update = "UPDATE users_items SET rating='1' WHERE upc_code LIKE '$upc' AND username LIKE '$username'";
		}else{ 
			$update = "UPDATE users_items SET rating='1' WHERE upc_code LIKE '$upc' AND username LIKE '$username'";
		}
		
		$update_run = mysql_query($update);
		
		return $update_run;
	}
	
	public function deleteUserItem($upc,$username) {
		$delete = "DELETE FROM users_items WHERE upc_code LIKE '$upc' AND username LIKE '$username'";

		$delete_run = mysql_query($delete);
		
		return $delete_run;
	}
	
	public function itemExist($upc) {
		$query = "SELECT * FROM items WHERE upc_code LIKE '$upc'";
		$query_run = mysql_query($query);
		$query_num = mysql_num_rows($query_run);
		
		if($query_num == 0)
			return false;
		else
			return true;
	}
	
	public function userItemExist($upc,$username) {
		$query = "SELECT * FROM users_items WHERE upc_code LIKE '$upc' AND username LIKE '$username'";
		$query_run = mysql_query($query);
		$query_num = mysql_num_rows($query_run);
		
		if($query_num == 0)
			return false;
		else
			return true;
	}
}

?>
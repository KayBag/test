<?php

/*
 * Conor Killeen
 * 5/4/2014
 * 
 * This code is used to test the server functionality.
 */

	require 'core.inc.php';
	
	if(loggedin()) {
		$username = $_SESSION['user_id'];
		echo "You're logged in as {$username}. <a href='logout.php'>Log out </a></br></br>";
		?>
		
		Select Test
		<form action="api.php" method="POST">
			<input type="hidden" value="<?php echo $_SESSION['user_id'] ?>" name="username"/>
			<input type="submit" value="get_barcode" name="action"/>
		</form>
		
		Rate Test
		<form action="api.php" method="POST">
			barcode: <input type="text" name="code"/>
			<input type="hidden" value="<?php echo $_SESSION['user_id'] ?>" name="username"/>
			<input type="submit" value="rate" name="action"/>
		</form>
		
		delete Test
		<form action="api.php" method="POST">
			barcode: <input type="text" name="code"/>
			<input type="hidden" value="<?php echo $_SESSION['user_id'] ?>" name="username"/>
			<input type="submit" value="delete" name="action"/>
		</form>
		
		Suggest Test
		<form action="api.php" method="POST">
			<input type="hidden" value="<?php echo $_SESSION['user_id'] ?>" name="username"/>
			<input type="submit" value="suggest" name="action"/>
		</form>
		
		UPC/EAN Test
		<form action="api.php" method="POST">
			<input type="hidden" value="<?php echo $_SESSION['user_id'] ?>" name="username"/>
			barcode: <input type="text" name="code"/>
			<input type="submit" value="get_data" name="action"/>
		</form>
		<?php
	}else{
		?>
		Login Test
		<form action="api.php" method="POST">
			username: <input type="text" name="username"/>
			password: <input type="password" name="password"/>
			<input type="submit" value="login" name="action"/>
		</form>
		
		Register Test
		<form action="api.php" method="POST">
			username: <input type="text" name="username"/>
			password: <input type="password" name="password"/>
			<input type="submit" value="register" name="action"/>
		</form>
		<?php
	}
?>
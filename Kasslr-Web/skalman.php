<?php

/**


	SPAGHETTI BELOW

	PLEASE ENJOY




*/

	require("db.php");

	$action = "none";
	if(isset($_GET["action"]))
		$action = $_GET["action"];

	switch ($action) {

/**

*/

case 'login':

$deviceRequest = $_GET["device"];

$userId = hash("sha256", "nah guys dont worry this is totally a good way to do this" . $deviceRequest . "_u salty bruh? kek");

		//hehe
$userId = substr($userId, 32);

$find = $db->prepare('SELECT * FROM users WHERE id=?');
$find->execute(array($userId));
$result = $find->fetchAll();

$response = array("user"=>null, "name"=>"Anonym", "score"=>0);

		// user is not registered lul
if(sizeof($result) < 1){

	var_dump("not registered lul");

	$request = $db->prepare('INSERT INTO users (id) VALUES (?)');
	$request->execute(array($userId));


}else{

	$response["name"] = $result[0]["name"];
	$response["score"] = $result[0]["score"];

}

$response["user"] = $userId;

header('Content-Type: application/json;charset=utf-8');
echo json_encode($response);

break;

/**

*/

case 'new_vocabulary':

$userId = preg_replace("/[^a-zA-Z0-9]+/", "", $_POST["user"]);
$title = $_POST["title"];

$request = $db->prepare('INSERT INTO vocabularies (owner, title) VALUES (?, ?)');
$request->execute(array($userId, $title));
$id = $db->lastInsertId();

$response = array("vocabularyId"=>$id);
header('Content-Type: application/json;charset=utf-8');
echo json_encode($response);

break;

/**

*/

case 'new_item':

$userId = preg_replace("/[^a-zA-Z0-9]+/", "", $_POST["user"]);
$word = $_POST["word"];
$vocabularyId = intval($_POST["vocabularyId"]);

var_dump($_FILES['image']);


$request = $db->prepare('INSERT INTO items (vocabulary, word) VALUES (?, ?)');
$request->execute(array($vocabularyId, $word));
$id = $db->lastInsertId();

$fileName = "user_images/" . $userId . "-" . $vocabularyId . "-" . $id . ".jpg";


$good = move_uploaded_file($_FILES['image']['tmp_name'], $fileName);

$response = array("itemId"=>$id, "uploaded"=>$good);

header('Content-Type: application/json;charset=utf-8');
echo json_encode($response);

break;

/**

*/

default:
header('Content-Type: application/json;charset=utf-8');
echo json_encode(array("error"=>"you done goofed up"));
break;
}
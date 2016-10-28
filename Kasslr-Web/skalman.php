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

case 'feed':

$find = $db->prepare('SELECT v.title, u.id as userid, v.id as vocabulary, u.name FROM vocabularies v JOIN users u ON u.id = v.owner ORDER BY v.submitted DESC, v.id DESC');
$find->execute();
$result = $find->fetchAll();

$feed = array();

foreach ($result as $row) {

	$finder = $db->prepare('SELECT * FROM items WHERE vocabulary = ?');
	$finder->execute(array($row["vocabulary"]));
	$itemArray = $finder->fetchAll();

	$items = array();

	foreach ($itemArray as $item) {
		$itemUrl = $baseUrl.'user_images/' . $row["userid"] . "-" . $row["vocabulary"] . "-" . $item["id"] . ".jpg";
		$items[] = array($item["word"], $itemUrl);
	}

	$owner = array("id" => $row["userid"], "name" => $row["name"], "image" => $baseUrl . 'user_images/' . $row["userid"] . ".jpg");

	$feed[] = array("id"=>$row["vocabulary"], "title"=>$row["title"], "owner"=>$owner, "items"=>$items);

}

$response = array("feed" => $feed);

header('Content-Type: application/json;charset=utf-8');
echo json_encode($response);

break;

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

$width = 720;
$height = 960;

list($w, $h) = getimagesize($_FILES['image']['tmp_name']);


$ratio = max($width/$w, $height/$h);
$h = ceil($height / $ratio);
$x = ($w - $width / $ratio) / 2;
$w = ceil($width / $ratio);

$path = "user_images/" . $userId . "-" . $vocabularyId . "-" . $id . ".jpg";
$imgString = file_get_contents($_FILES['image']['tmp_name']);

$image = imagecreatefromstring($imgString);
$tmp = imagecreatetruecolor($width, $height);
imagecopyresampled($tmp, $image,
	0, 0,
	$x, 0,
	$width, $height,
	$w, $h);

imagejpeg($tmp, $path, 80);

imagedestroy($image);
imagedestroy($tmp);

$response = array("itemId"=>$id, "uploaded"=>$good);

header('Content-Type: application/json;charset=utf-8');
echo json_encode($response);

break;
/**

*/

case 'update_user':

$userId = preg_replace("/[^a-zA-Z0-9]+/", "", $_POST["user"]);
$name = $_POST["name"];


$request = $db->prepare('UPDATE users SET name = ? WHERE id = ?');
$request->execute(array($name, $userId));

$response = array("nehedu"=>true);
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
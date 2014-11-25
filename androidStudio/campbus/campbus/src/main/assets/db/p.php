<?php

mb_internal_encoding('UTF-8');

$files = scandir("./");

foreach($files as $file)
{
	if (strpos($file, ".json")!== false)
	{
		$text = utf8_encode(file_get_contents($file));
		$json = json_decode($text);
		file_put_contents($file, json_encode($json));
	}
}

?>

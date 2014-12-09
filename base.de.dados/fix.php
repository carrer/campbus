<?php

$files = scandir("./");

foreach($files as $file)
{
	exec("gzip $file");
}
?>

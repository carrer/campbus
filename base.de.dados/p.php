<?php


$linhas=array();

if ($handle = opendir('./')) {
    while (false !== ($entry = readdir($handle))) {
        if (strpos( $entry, ".json")!==false)
	{
//		exec("iconv -f ISO-8859-1 -t UTF-8 $entry > tmp.json");
//		exec("mv tmp.json $entry");

/*		$obj = json_decode(file_get_contents($entry));
		if (isset($obj->numero))
		{

			$itinerario = array_unique(array_merge($obj->ida->itinerario, $obj->volta->itinerario));
			foreach($itinerario as $rua)
				$linhas[$rua][] = $obj->numero;
		}
*/
	}
    }
    closedir($handle);
	ksort($linhas);
	file_put_contents("paradas.json", json_encode($linhas));
}

?>

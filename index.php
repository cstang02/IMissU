<?php

$date = $_GET["date"];
$id = $_GET["id"];

if ($date == null || $date == "")
{
    exit();
}

$mmc = memcache_init();

$result = memcache_get($mmc, $date);

memcache_set($mmc, $date, $id, 0, 10);

$mmc->close();

echo $result == $id ? "" : $result;

?>
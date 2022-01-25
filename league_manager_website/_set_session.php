<?php
    session_start();

    $sessionId=$_GET['sessionId'];

    $_SESSION["userId"] = $sessionId;
?>
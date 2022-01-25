<?php
    $name=$_GET['name'];

    /*$url='https://league-manager.azurewebsites.net/leagues';
    $data=array('name'=>$name);

    $options = array(
        'http' => array (
            'header' => "Content-type: application/x-www-form-urlencoded\r\n",
            'method' => 'POST',
            'content' => http_build_query($data)
        )
    );

    $content = stream_context_create($options);
    $result = file_get_contents($url, false, $content);

    if ($result === FALSE) {

    }*/

    
    $response = httpPost("https://league-manager.azurewebsites.net/leagues", array("name"=>$name));

    //using php curl (sudo apt-get install php-curl) 
    /*function httpPost($url, $data){
        $curl = curl_init($url);
        curl_setopt($curl, CURLOPT_POST, true);
        curl_setopt($curl, CURLOPT_POSTFIELDS, http_build_query($data));
        curl_setopt($curl, CURLOPT_RETURNTRANSFER, true);
        $response = curl_exec($curl);
        curl_close($curl);
        return $response;
    }*/

    //Non curl Method
    function httpPost($url, $data){
        $options = array(
            'http' => array(
                'header'  => "Content-type: application/x-www-form-urlencoded\r\n",
                'method'  => 'POST',
                'content' => http_build_query($data)
            )
        );
        $context  = stream_context_create($options);
        return file_get_contents($url, false, $context);
    }
    
    var_dump($response);
?>
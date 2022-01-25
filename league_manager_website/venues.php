<?php
  include('templates/header.php')
?>




<br><br>

<!-- Main Content -->
<div class="container">
    <div class="row">
        <div class="col-lg-8 col-md-10 mx-auto">

        <h2 class="post-title">Stadiums</h2>
        <br>

<?php
  try {
    $url = 'https://league-manager.azurewebsites.net/venues';
    $venues = file_get_contents($url);

    $json = json_decode($venues, true);

    //echo '<pre>';
    //print_r($json);

foreach($json as $item) {
  $output = '<div class="post-preview">
  <a href="#">
    <h3 class="post-subtitle">'.$item['name'].
    '</h3>

  </a>
</div>
<hr>';

//<h3 class="post-subtitle">
//Problems look mighty small from 150 miles up
//</h3>

echo $output;
}


    //echo $leagues;
  } catch(Exception $e) {
    echo $e;
  }
?>



      </div>
    </div>
  </div>


<?php
  include('templates/footer.php')
?>
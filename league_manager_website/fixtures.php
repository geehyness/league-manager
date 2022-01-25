<?php
  include('templates/header.php')
?>













<br><br>

<!-- Upcoming Games -->
<div class="container">
    <div class="row">
      <div class="col-lg-8 col-md-10 mx-auto">

      <h2 class="post-title">Upcoming Games</h2>
      <br>

<?php
  try {
    $leagueId = $_GET['leagueId'];
    $url = 'https://league-manager.azurewebsites.net/fixtures/upcoming/'.$leagueId;
    $fixtures = file_get_contents($url);

    $json = json_decode($fixtures, true);

    //echo '<pre>';
    //print_r($json);

foreach($json as $item) {

  $url = 'https://league-manager.azurewebsites.net/teams/'.$item['team1Id'];
  $team1 = file_get_contents($url);

  $fixturesJson1 = json_decode($team1, true);

  $url = 'https://league-manager.azurewebsites.net/teams/'.$item['team2Id'];
  $team2 = file_get_contents($url);

  $fixturesJson2 = json_decode($team2, true);

  $url = 'https://league-manager.azurewebsites.net/venues/'.$item['venueId'];
  $venue = file_get_contents($url);

  $venueJson = json_decode($venue, true);

    //echo '<pre>';
    //print_r($fixturesJson);

  $mil = $item['time'];
  $seconds = $mil / 1000;
  echo date("d/m/Y - H:i", $seconds);
  echo " &rArr; " . $venueJson['name'];

  $output = '<div class="post-preview">
  <a href="#">
    <h3 class="post-subtitle">'
      .$fixturesJson1['name'].' vs '.$fixturesJson2['name'].
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

<!-- Pager
        <div class="clearfix">
          <a class="btn btn-primary float-right" href="#">Add Fixture</a>
        </div>
 -->
      </div>
    </div>
  </div>
<br>
<hr>



<?php
  include('templates/footer.php')
?>
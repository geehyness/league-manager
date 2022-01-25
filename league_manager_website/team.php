<?php
  include('templates/header.php')
?>


<script>
  function edit_team() {
    var name =$("#leagueName").val();
    var teamId = <?php echo json_encode($_GET['teamId']); ?>;
    var leagueId = <?php echo json_encode($_GET['leagueId']); ?>;
    var url = 'https://league-manager.azurewebsites.net/teams/'+teamId;
    var data = {
      name:name,
      leagueId:leagueId
    }
    
    axios.patch(url, data)
    .then(function (response) {
      console.log(response);
      history.back();
    })
    .catch(function (error) {
      console.log(error.response);
    });
  }

  function delete_team() {
    var teamId = <?php echo json_encode($_GET['teamId']); ?>;
    var url = 'https://league-manager.azurewebsites.net/teams/'+teamId;
    
    var r = confirm("Are you sure you want to delete?");
    if (r == true) {
      axios.delete(url)
      .then(function (response) {
        console.log(response);
        history.back();
      })
      .catch(function (error) {
        console.log(error.response);
      });
    } else {
      //don't delete
    }
  }  

  function add_player() {
    var name =$("#name").val();
    var leagueId = <?php echo json_encode($_GET['leagueId']); ?>;
    var url = 'https://league-manager.azurewebsites.net/teams';
    var data = {
      name:name,
      leagueId:leagueId
    }
    axios.post(url, data)
    .then(function (response) {
      console.log(response);
    })
    .catch(function (error) {
      console.log(error);
    });
  }
</script>








<br><br>

<!-- Main Content -->
<div class="container">
    <div class="row">
      <div class="col-lg-8 col-md-10 mx-auto">

<?php
  $name = $_GET['name'];
  echo '<h2 class="post-title">'.$name.' Players</h2>'
?>
      <br>
      <table>
  <thead>
    <th>#</th>
    <th>Player name</th>
    <th>Goals Scored</th>
  </thead>
  <tbody>

<?php
  try {
    $teamId = $_GET['teamId'];
    $url = 'https://league-manager.azurewebsites.net/players/byTeam/'.$teamId;
    $players = file_get_contents($url);

    $json = json_decode($players, true);

    //echo '<pre>';
    //print_r($json);

    $count = 0;

foreach($json as $item) {
  $count ++;
  /*$output = '<div class="post-preview">
  <a href="team.php?teamId='.$item['_id'].'">
    <h3 class="post-subtitle">'.$item['name'].
    '</h3>

  </a>
</div>
<hr>';*/

$output = '<tr class="post-preview">
              <td>'.$count.'</td>
              <td>              
                <h6 class="post-subtitle">'.$item['name'].
                '</h6>
              </td>
              <td>
                <h6 class="post-subtitle">'.$item['goals'].'</h6>
              </td>
            </tr>';

//<h6 class="post-subtitle">
//Problems look mighty small from 150 miles up
//</h6>

echo $output;
}


    //echo $leagues;
  } catch(Exception $e) {
    echo $e;
  }
?>

</tbody>
</table>

<!--
<div class="clearfix">
          <a class="btn btn-primary float-right" href="" data-toggle="modal" data-target="#myModal">Add Player</a>
        </div> 
-->

        <!-- Modal -->
        <div class="modal fade" id="myModal" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
          <div class="modal-dialog">
            <div class="modal-content">
              <div class="modal-header">
                <h4 class="modal-title" id="myModalLabel">New Team in <?php echo $_GET['name'] ?></h4>
              </div>
              <div class="modal-body">
                <form>
                  Team Name <input name="name" id="name" type="text">
                  <br>
                  <hr>
                  <button type="button" class="btn btn-default" data-dismiss="modal">Close</button>
                  <input type="button" class="btn btn-primary" value="Add League" onclick="add_player()">
                </form>
              </div>
            </div>
          </div>
        </div>

        <br>

<!--
        <div class="clearfix">
          <a class="btn btn-primary float-right" href="" data-toggle="modal" data-target="#myModalEditLeague">Edit Team</a>
        </div> 
-->

        <!-- Modal -->
        <div class="modal fade" id="myModalEditLeague" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
          <div class="modal-dialog">
            <div class="modal-content">
              <div class="modal-header">
                <h4 class="modal-title" id="myModalLabel">Edit <?php echo $_GET['name'] ?></h4>
              </div>
              <div class="modal-body">
                <form>
                  <?php echo 'League Name <input name="name" id="leagueName" type="text" value="'.$_GET['name'].'">' ?>
                  <br>
                  <hr>
                  <button type="button" class="btn btn-default" data-dismiss="modal">Close</button>
                  <input type="button" class="btn btn-primary" value="Save" onclick="edit_team()">
                </form>
              </div>
            </div>
          </div>
        </div>

<br>

<!--
        <div class="clearfix">
          <input type="button" class="btn btn-danger float-right" value="Delete Team" onclick="delete_team()">
        </div> 
-->

      </div>
    </div>
  </div>


<?php
  include('templates/footer.php')
?>
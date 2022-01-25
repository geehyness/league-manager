<?php
  include('templates/header.php')
?>


<script>
  function edit_league() {
    var name =$("#leagueName").val();
    var leagueId = <?php echo json_encode($_GET['leagueId']); ?>;
    var url = 'https://league-manager.azurewebsites.net/leagues/'+leagueId;
    var data = {
      name:name
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

  function delete_league() {
    var leagueId = <?php echo json_encode($_GET['leagueId']); ?>;
    var url = 'https://league-manager.azurewebsites.net/leagues/'+leagueId;
    
    

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

  function add_team() {
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

<!-- Teams -->
<div class="container">
    <div class="row">
      <div class="col-lg-8 col-md-10 mx-auto">

      <?php
        echo '<a href="fixtures.php?leagueId='.$_GET["leagueId"].'&name='.$_GET["name"].'>
        <button type="submit" class="btn btn-primary">View Fixtures</button>
        </a>'

      ?>


<br><br>
<?php
  $name = $_GET['name'];
  echo '<h2 class="post-title">'.$name.' Teams</h2>'
?>
      <br>

<table>
  <thead>
    <th>#</th>
    <th>Team name</th>
    <th>P</th>
    <th>W</th>
    <th>D</th>
    <th>L</th>
    <th>GS</th>
    <th>GA</th>
    <th>GD</th>
    <th>Points</th>
  </thead>
  <tbody>
  

<?php
  try {
    $leagueId = $_GET['leagueId'];
    $url = 'https://league-manager.azurewebsites.net/teams/log/'.$leagueId;
    $teams = file_get_contents($url);

    $json = json_decode($teams, true);

    //echo '<pre>';
    //print_r($json);

$count = 0;
    
foreach($json as $item) {
  $count += 1;
  $output = '<tr class="post-preview">
              <td>'.$count.'</td>
              <td>
                <a href="team.php?teamId='.$item['_id'].'&name='.$item['name'].'&leagueId='.$_GET['leagueId'].'">
                  <h6 class="post-subtitle">'.$item['name'].
                '</h6>
                </a>
              </td>
              <td>
                <h6 class="post-subtitle">'.$item['played'].'</h6>
              </td>
              <td>
                <h6 class="post-subtitle">'.$item['wins'].'</h6>
              </td>
              <td>
                <h6 class="post-subtitle">'.$item['draws'].'</h6>
              </td>
              <td>
                <h6 class="post-subtitle">'.$item['losses'].'</h6>
              </td>
              <td>
                <h6 class="post-subtitle">'.$item['goalsScored'].'</h6>
              </td>
              <td>
                <h6 class="post-subtitle">'.$item['goalsAgainst'].'</h6>
              </td>
              <td>
                <h6 class="post-subtitle">'.$item['goalDifference'].'</h6>
              </td>
              <td>
                <h6 class="post-subtitle">'.$item['points'].'</h6>
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

<!-- Pager 
        <div class="clearfix">
          <a class="btn btn-primary float-right" href="" data-toggle="modal" data-target="#myModal">Add Team</a>
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
                  <input type="button" class="btn btn-primary" value="Add League" onclick="add_team()">
                </form>
              </div>
            </div>
          </div>
        </div>

        <br>

<!--
        <div class="clearfix">
          <a class="btn btn-primary float-right" href="" data-toggle="modal" data-target="#myModalEditLeague">Edit League</a>
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
                  <input type="button" class="btn btn-primary" value="Save" onclick="edit_league()">
                </form>
              </div>
            </div>
          </div>
        </div>

<br>

<!--
        <div class="clearfix">
          <input type="button" class="btn btn-danger float-right" value="Delete League" onclick="delete_league()">
        </div> 
        -->
      </div>
    </div>
  </div>


<?php
  include('templates/footer.php')
?>
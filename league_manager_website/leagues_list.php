
 <?php
  include('templates/header.php')
?>

  <br><br>
  
  <!-- Main Content -->
<div class="container">
    <div class="row">
      <div class="col-lg-8 col-md-10 mx-auto">

        <h2 class="post-title">Leagues</h2>
        <br>

<?php
  try {
    $url = 'https://league-manager.azurewebsites.net/leagues';
    $leagues = file_get_contents($url);

    $json = json_decode($leagues, true);

    //echo '<pre>';
    //print_r($json);

    foreach($json as $item) {
      $output = '<div class="post-preview">
                  <a href="league.php?leagueId='.$item['_id'].'&name='.$item['name'].'">
                    <h3 class="post-subtitle">'
                      .$item['name'].
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
          <a class="btn btn-primary float-right" href="" data-toggle="modal" data-target="#myModal">Add League</a>
        </div> 
-->        
<script>
  function add_league() {
    var name =$("#name").val();
    var url = 'https://league-manager.azurewebsites.net/leagues';
    var data = {
        name: name
    }

    axios.post(url, data)
    .then(function (response) {
      console.log(response);
    })
    .catch(function (error) {
      console.log(error);
    });

    /*var xhr = new XMLHttpRequest();
    xhr.onload = function(e) {
      location.reload();
      return false;
    }
    xhr.open("POST", url, true);
    xhr.setRequestHeader('Content-Type', 'application/json');
    xhr.send(JSON.stringify(data));*/
    
    /*$.get("_add_league.php", {name:response.data["_id"]},
    function(data){
      location.reload();
      return false;
    })*/
  }
</script>

        <!-- Modal -->
        <div class="modal fade" id="myModal" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
          <div class="modal-dialog">
            <div class="modal-content">
              <div class="modal-header">
                <h4 class="modal-title" id="myModalLabel">New League</h4>
              </div>
              <div class="modal-body">
                <form>
                  League Name <input name="name" id="name" type="text">
                  <br>
                  <hr>
                  <button type="button" class="btn btn-default" data-dismiss="modal">Close</button>
                  <input type="button" class="btn btn-primary" value="Add League" onclick="add_league()">
                </form>
              </div>
            </div>
          </div>
        </div>

        

      </div>
    </div>
  </div>

  






<!-- Button trigger modal 
<input type="text" data-toggle="modal" data-target="#myModal">
-->

<!-- Modal
<div class="modal fade" id="myModal" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
  <div class="modal-dialog">
    <div class="modal-content">
      <div class="modal-header">
        <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span></button>
        <h4 class="modal-title" id="myModalLabel">Modal title</h4>
      </div>
      <div class="modal-body">
        ...
      </div>
      <div class="modal-footer">
        <button type="button" class="btn btn-default" data-dismiss="modal">Close</button>
        <button type="button" class="btn btn-primary">Save changes</button>
      </div>
    </div>
  </div>
</div>
 -->


  
  
<?php
  include('templates/footer.php')
?>
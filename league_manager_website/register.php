<?php
    include('templates/loginHeader.php')
?>


<script>
    function register() {
    var email =$("#email").val();
    var password =$("#password").val();

    var url = 'https://league-manager.azurewebsites.net/users/register';
    var data = {
      email:email,
      password:password
    }
    axios.post(url, data)
    .then(function (response) {
      $.get("_set_session.php", {sessionId:response.data["_id"]},
      function(data){
        location.reload();
        return false;
      })
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
        <h2>Register</h2>
        
        <form name="sentMessage" id="contactForm" novalidate>
          <div class="control-group">
            <div class="form-group floating-label-form-group controls">
              <label>Name</label>
              <input type="email" class="form-control" placeholder="Email" id="email" required data-validation-required-message="Please enter your name.">
              <p class="help-block text-danger"></p>
            </div>
          </div>
          <div class="control-group">
            <div class="form-group floating-label-form-group controls">
              <label>Email Address</label>
              <input type="password" class="form-control" placeholder="Password" id="password" required data-validation-required-message="Please enter your email address.">
              <p class="help-block text-danger"></p>
            </div>
          </div>
          <br>
          <div id="success"></div>
          <input type="button" class="btn btn-primary" value="Register" onclick="register()">
        </form>
      </div>
    </div>
  </div>    


<?php
    include('templates/footer.php')
?>
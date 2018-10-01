
$(document).on("click", "#loginBtn", function(e) {
    if(!globalValidator.validateLoginForm()){
        return false;
    }
    e.preventDefault();
    var newUser = new Object();
    newUser["username"] = $("#logusername").val();
    newUser["password"] = $("#logpassword").val();
    globalAuthenticator.login(newUser);
});


$(document).on("click", "#logoutBtn", function(){
	globalAuthenticator.logout();
});
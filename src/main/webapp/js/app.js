$(document).ready(function(e){
	checkIfLoged();
});

function checkIfLoged(){
	$.ajax({
        url : '/api/users/me',
        type : 'GET',
        async: 'false',
        success : function(userDTO) {
            if(userDTO != null && userDTO != ""){
            	globalAuthenticator.loggedInUser = userDTO;
            	var name = userDTO.firstname + " " + userDTO.lastname;
            	var $navbar = $(".mainNavbar").show();
            	var $loginDiv = $("#loginDiv").hide();
            	var $mainDiv = $("#mainDiv").show();
            	$("#userLabel").text(name);
            }else {
            	var $navbar = $(".mainNavbar").hide();
            	var $loginDiv = $("#loginDiv").show();
            	$("#loginForm").find("input").val("");
            	var $mainDiv = $("#mainDiv").hide();
            }
        },
        error : function(xhr, textStatus, errorThrown) {
        	if(xhr.status == 401){
        		var $navbar = $(".mainNavbar").hide();
            	var $loginDiv = $("#loginDiv").show();
            	var $mainDiv = $("#mainDiv").hide();
        	}else {
        		toastr.error('Error authenitication user!  Status = ' + xhr.status);
        	}
        	
        }
    })
}

function FormBuilder() {

    this.buildFormInput = function(inputData, formData){
        var self = this;
        console.log(inputData);

        var $formGroup = $("<div>");
        var $label = $("<label>");
        var $inputDiv = $("<div>");
        var $input = null;

        $formGroup.attr("class", "form-group row");
        $label.attr("class", "col-sm-2 col-form-label");
        $label.attr("for", inputData.id);
        $label.text(inputData.name);
        $inputDiv.attr("class", "col-sm-10");

        var type = self.resolveInputType(inputData);
        if(type != "select"){
            $input = $("<input>");
            $input.attr("id", inputData.id);
            $input.attr("class", "form-control");
            $input.prop("required", inputData.required);
            $input.prop("hidden", !inputData.readable);
            $input.prop("disabled", !inputData.writable);
        }else{
            $input = self.buildSelectInput(inputData, formData);
        }

        $inputDiv.append($input);
        $formGroup.append($label);
        $formGroup.append($inputDiv);

        return $formGroup;
    },

    this.resolveInputType = function (input) {
        var type = ""
        var typeName = input.type.name;
        var inputName = input.id;
        if(typeName == "string" || typeName == "long"){
        	if(inputName == "kategorija" || inputName == "kategorijaPosla"){
        		type = "select";
        	}else{
        		type = "text";
        	}            
        }else if(typeName == "boolean"){
            type = "checkbox";
        }else if(typeName == "enum"){
            type = "select";
        }
        return type;
    }
    
    this.buildSelectInput = function(inputData, formData){
    	var $selectInput = $("<select>");
    	$selectInput.attr("class", "form-control");
    	if(inputData.writable){
    		$selectInput.attr("name", inputData.id);
    		$selectInput.attr("id", inputData.id);
    	}else {
    		$selectInput.prop("disabled", true);
    	}
    	var enumMap = formData.enumMap;
    	Object.keys(enumMap).forEach(key => {
    		var $option = $("<option>");
    		$option.attr("value", key);
    		$option.text(enumMap[key]);
    		$selectInput.append($option);
    	});
    	
    	return $selectInput;
    }

    this.buildFormButton = function(type, id, value){
        var $formGroup = $("<div>");
        var $button = $("<button>");

        $formGroup.attr("class", "form-group text-center");
        $button.attr("class", "btn btn-"+type);
        $button.attr("id", id);
        $button.text(value);

        $formGroup.append($button);

        return $formGroup;
    }
}

function Authenticator(){
	
	this.loggedInUser = null;
	
	this.login = function(user){
		var self = this;
		var strUser = self.format(user);
		$.ajax({
	        url : '/api/users/login',
	        type : 'POST',
	        contentType : 'application/x-www-form-urlencoded',
	        data : strUser,
	        success : function() {
	            self.checkUser();
	        },
	        error : function(xhr, textStatus, errorThrown) {
	        	if(xhr.status == 401){
	        		toastr.error("Nema korisnika sa unesenim kredencijalima. Pokušajte ponovo");
	        	}else{
	        		toastr.error('Error!  Status = ' + xhr.status);
	        	}
	        }
	    });
	},
	
	this.checkUser = function(){
		var self = this;
		$.ajax({
	       url : '/api/users/me',
	       type : 'GET',
	       async: 'false',
	       success : function(userDTO) {
	    	   if(userDTO != null){
	    		   var name = userDTO.firstname + " " + userDTO.lastname;
	               self.setLoggedInView(name);
	               self.loggedInUser = userDTO;
	           }else {
	        	   self.setLoggedOutView();
	        	   self.loggedInUser = null;
	           }
	        },
	        error : function(xhr, textStatus, errorThrown) {
	            toastr.error('Error authenitication user!  Status = ' + xhr.status);
	        }
	    });
	},
	
	this.logout = function(){
		var self = this;
		if(self.loggedInUser != null){
			var name = self.loggedInUser.firstname + " " + self.loggedInUser.lastname;
			var username = self.loggedInUser.username;
			 $.ajax({
				url: '/api/logout',
				type : 'POST',
			    data: username,
			    success: function(data){
			    	console.log("izlogovan uspesno");
			    	self.setLoggedOutView();
			    },
			    error: function(data){
			    	console.log("nije izlogovan");
			    	self.setLoggedInView(name);
			    }
			 });
		}else {
			console.log("No one is logged in.");
		}
	},
	
	this.format = function(obj){
		var str = [];
	    for (var p in obj)
	        str.push(encodeURIComponent(p) + "=" + encodeURIComponent(obj[p]));
    	return str.join("&");
	},
	
	this.getLoggedInUser = function(){
		var self = this;
		return self.loggedInUser;
	},
	
	this.setLoggedInView = function(name){
		$(".mainNavbar").show();
		$("#loginDiv").hide();
		$("#mainDiv").show();
		$("#userLabel").text(name);
	}

	this.setLoggedOutView = function(){
		$(".mainNavbar").hide();
		$("#loginDiv").show();
		$("#loginForm").find("input").val("");
		$("#mainDiv").hide();
		$("#userLabel").text("");
		location.reload();
	}
}

function Storage(){
	
	this.ponuda = null;
	this.mesto = null;
}

toastr.options = {
		"closeButton" : true,
		"debug" : false,
		"newestOnTop" : false,
		"progressBar" : false,
		"positionClass" : "toast-top-right",
		"preventDuplicates" : false,
		"onclick" : null,
		"showDuration" : "200",
		"hideDuration" : "1000",
		"timeOut" : "5000",
		"extendedTimeOut" : "1000",
		"showEasing" : "swing",
		"hideEasing" : "linear",
		"showMethod" : "fadeIn",
		"hideMethod" : "fadeOut"
}

var globalAuthenticator = new Authenticator();
var globalFormBuilder = new FormBuilder();
var globalValidator = new ToastrValidator();
var globalStorage = new Storage();





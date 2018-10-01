
var errorCounter = 0;

//Link
$(document).on("click", "#registerBtn", function () {
    $.ajax({
        url : '/api/registration/start',
        type : 'GET',
        async: false,
        success : function(formDTO) {
        	if(formDTO.formProperties.length == 0){
                getTasks(false);
            }else {
            	toastr.error("Greska 1");
            }
        },
        error : function(xhr, textStatus, errorThrown) {
            toastr.error('Error!  Status = ' + xhr.status);
        }
    })
});

//Imas li taskove koji nisu izvrseni za localhost
function getTasks(isPravnoLice){
	$.ajax({
		url : '/api/registration/tasks',
        type : 'GET',
        async: false,
        success: function(tasks){
        	if(tasks.length != 0){
        		var task = tasks[0]; //Uzima prvi zadatak (forma sa registracijom)
        		var id = task.id;
        		var taskName = task.name;
        		getFormForTaskReg(id, !isPravnoLice, taskName);
        	}else{
        		alert("Proverite vašu email adresu. Poslat je mail za potvrdu registracije koju možete izvršiti u narednih 24h. (2min)");
        		errorCounter = 0;
        	}
        },
        error : function(xhr, textStatus, errorThrown) {
            toastr.error('Error!  Status = ' + xhr.status);
        }
	});
}

function getFormForTaskReg(id, zajdenicko, taskName){
	$.ajax({
		url : '/api/registration/task/' + id,
        type : 'GET',
        async: false,
        success: function(formDTO){       	
        	errorCounter = errorCounter + 1;
        	if(formDTO.formProperties.length != 0){
                $("#modalRegister").modal("toggle");
                var $registerForm = $("#registerUserForm").empty();
                $registerForm.append("<fieldset>");
                
                for(i=0; i<formDTO.formProperties.length; i++){
                    var formProperty = formDTO.formProperties[i];
                    var $content = globalFormBuilder.buildFormInput(formProperty, formDTO);
                    console.log($content);
                    $registerForm.append($content);
                }
                
                $registerForm.append("<input hidden type='text' value='" + id + "' id='registerTaskId'/>")
                
                if(!zajdenicko && taskName != "Unos zajednickih podataka"){
                	$registerForm.append("<p>Popunite neophodne podatke vezane za vašu firmu.</p>");
                	getKategorije();
                	var $button = globalFormBuilder.buildFormButton("success", "registerAgentBtn", "Potvrdi");
                    $registerForm.append($button);
                    
                }else {
                	var $button = globalFormBuilder.buildFormButton("success", "registerConfirmBtn", "Potvrdi");
                    $registerForm.append($button);
                }
                
                if(errorCounter == 3){
                	$registerForm.append("<p style='text-align:center;font-style:italic'>Korisničko ime i email moraju biti jedinstveni. Pokušajte ponovo.</p>");
                }
                
            }else {
               toastr.info("Proverite mail za potvrdu registracije!");
            }
        },
        error : function(xhr, textStatus, errorThrown) {
            toastr.error('Error!  Status = ' + xhr.status);
        }
	});        
}

//Zajednicka forma
$(document).on("click", "#registerConfirmBtn", function(){
	var params = {};
	var taskId = $("#registerTaskId").val();
	var valueTip = $("#tip option:selected").val();
	var valueIme = $("#ime").val();
	var valuePrezime = $("#prezime").val();
	var valueUsername = $("#username").val();
	var valuePassword = $("#password").val();
	var valuePtt = $("#ptt").val();
	var valueAdresa = $("#adresa").val();
	var valueMesto = $("#mesto").val();
	var valueEmail = $("#email").val();
	params["tip"] = valueTip;
	params["ime"] = valueIme;
	params["prezime"] = valuePrezime;
	params["username"] = valueUsername;
	params["password"] = valuePassword;
	params["ptt"] = valuePtt;
	params["adresa"] = valueAdresa;
	params["mesto"] = valueMesto;
	params["email"] = valueEmail;
	var jparams = JSON.stringify(params);
	$.ajax({
		url : '/api/registration/confirm/' + taskId,
        type : 'POST',
        data: jparams,
        contentType: "application/json",
        async: false,
        success: function(formDTO){
        	var tip = valueTip;
        	console.log(formDTO);
        	if(formDTO.message == "Task finished successfully!"){
        		$("#modalRegister").modal("toggle");
        		if(tip == "pravno"){
        			getTasks(true);
        		} else {
        			getTasks(false);
        		}       		
        	}else{
        		toastr.error('Error! ' + formDTO.message);
        	}
        },
	    error : function(xhr, textStatus, errorThrown) {
	        toastr.error('Error!  Status = ' + xhr.status);
	    }
	});
});

$(document).on("click", "#registerAgentBtn", function(){
	var params = {};
	var taskId = $("#registerTaskId").val();
	var kategorija = $("#kategorija option:selected").text();
	var naziv = $("#naziv").val();
	var udaljenost = $("#udaljenost").val();
	params["naziv"] = naziv;
	params["kategorija"] = kategorija;
	params["udaljenost"] = udaljenost;
	var jparams = JSON.stringify(params);
	$.ajax({
		url : '/api/registration/confirm/' + taskId,
        type : 'POST',
        data: jparams,
        contentType: "application/json",
        async: false,
        success: function(formDTO){
        	if(formDTO.message == "Task finished successfully!"){
        		$("#modalRegister").modal("toggle");
        		getTasks(true);
        	}else{
        		toastr.error('Error! ' + formDTO.message);
        	}
        },
	    error : function(xhr, textStatus, errorThrown) {
	        toastr.error('Error!  Status = ' + xhr.status);
	    }
	});
});




function getKategorije(){
	$.ajax({
		url : '/api/categories',
        type : 'GET',
        async: false,
        success: function(kategorije){
        	if(kategorije.length != 0){
        		var $selectKat = $("#kategorija");
        		$selectKat.empty();
        		for(var i=0; i<kategorije.length; i++){
        			var kat = kategorije[i];
        			var $option = $("<option>");
        			$option.attr("value", kat.id);
        			$option.text(kat.name);
        			$selectKat.append($option);
        		}
        	}
        },
        error : function(xhr, textStatus, errorThrown) {
            toastr.error('Error!  Status = ' + xhr.status);
        }
	});
}
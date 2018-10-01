

$(document).ready(function(){
	$(".ams-user-task-form").hide();
});


$(document).on("click", "#pokreniAukcije", function(){
	$.ajax({
		url: '/auction/start',
		type : 'GET',
	    success: function(data){
	    	getAllTasksForLoggedUser();
	    },
	    error : function(xhr, textStatus, errorThrown) {
            toastr.error('Error authenitication user!  Status = ' + xhr.status);
        }
	})
});

$(document).on("click", "#getZadaciLogovani", function(){
	getAllTasksForLoggedUser();
});


$(document).on("click", "#podnesiZahtev", function(){
	var params = pripremiParametreZahteva();
	var taskId = $("#podnesiZahtevTaskId").val();
	var url = "/auction/confirmZahtev/" + taskId;
	submitFormData(params, url);
});


$(document).on("click", "#potvrdiOdlukuManjiBr", function(){
	var params = pripremiParametreOdlukeManjiBr();
	var taskId = $("#potvrdiOdlukuManjiBrId").val();
	var url = "/auction/confirmGen/" + taskId;
	submitFormData(params, url);
});

$(document).on("click", "#potvrdiOdlukuOPonudiAgent", function(){
	var params = pripremiParametreOdlukaOPonudiAgent();
	var taskId = $("#potvrdiOdlukuOPonudiAgentId").val();
	var url = "/auction/confirmGen/" + taskId;
	submitFormData(params, url);
});

$(document).on("click", "#potvrdiPopunjenuPonuduAgent", function(){
	var params = pripremiParametrePonudeAgent();
	var taskId = $("#potvrdiPopunjenuPonuduAgentId").val();
	var url = "/auction/confirmPonuda/" + taskId;
	submitFormData(params, url);
});

$(document).on("click", "#potvrdiUvidUStanjeAgent", function(){
	var params = pripremiParametreUvidUStanjeAgent();
	var taskId = $("#potvrdiUvidUStanjeAgentId").val();
	var url = "/auction/confirmGen/" + taskId;
	submitFormData(params, url);
});

$(document).on("click", "#potvrdiPromenaPonudeAgent", function(){
	var params = pripremiParametrePromenaPonudeAgent();
	var taskId = $("#potvrdiPromenaPonudeAgentId").val();
	var url = "/auction/confirmGen/" + taskId;
	submitFormData(params, url);
});

$(document).on("click", "#potvrdiOdlukaProsiriFirme", function(){
	var params = pripremiParametreOdlukaProsiriFirme();
	var taskId = $("#potvrdiOdlukaProsiriFirmeId").val();
	var url = "/auction/confirmGen/" + taskId;
	submitFormData(params, url);
});

$(document).on("click", "#potvrdiOdlukaNijednaPonuda", function(){
	var params = pripremiParametreOdlukaNijednaPonuda();
	var taskId = $("#potvrdiOdlukaNijednaPonudaId").val();
	var url = "/auction/confirmGen/" + taskId;
	submitFormData(params, url);
});

$(document).on("click", "#potvrdiOdlukaProduziRok", function(){
	var params = pripremiNoviDatum();
	var taskId = $("#potvrdiOdlukaProduziRokId").val();
	var url = "/auction/confirmGen/" + taskId;
	submitFormData(params, url);
});

$(document).on("click", "#potvrdiOdlukaOOdustanku", function(){
	var params = pripremiOdlukaOOdustanku();
	var taskId = $("#potvrdiOdlukaOOdustankuId").val();
	var url = "/auction/confirmGen/" + taskId;
	submitFormData(params, url);
});

$(document).on("click", "#potvrdiOdlukaDodatniInfo", function(){
	var params = pripremiDodatniInfo();
	var taskId = $("#potvrdiOdlukaDodatniInfoId").val();
	var url = "/auction/confirmGen/" + taskId;
	submitFormData(params, url);
});

$(document).on("click", "#potvrdiOdlukaPoslePojasnjenja", function(){
	var params = pripremiParametreNakonPojasnjenja();
	var taskId = $("#potvrdiOdlukaPoslePojasnjenjaId").val();
	var url = "/auction/confirmGen/" + taskId;
	submitFormData(params, url);
});

$(document).on("click", "#potvrdiPrihvatanjePosla", function(){
	var params = pripremiParametrePrihvatanjePosla();
	var taskId = $("#potvrdiPrihvatanjePoslaId").val();
	var url = "/auction/confirmGen/" + taskId;
	submitFormData(params, url);
});

$(document).on("click", "#potvrdiOcenaAgenta", function(){
	var params = pripremiOcenaAgenta();
	var taskId = $("#potvrdiOcenaAgentaId").val();
	var url = "/auction/ocenaAgenta/" + taskId;
	submitFormData(params, url);
});

$(document).on("click", "#potvrdiOcenaKlijenta", function(){
	var params = pripremiOcenaKlijenta();
	var taskId = $("#potvrdiOcenaKlijentaId").val();
	var url = "/auction/ocenaKlijenta/" + taskId;
	submitFormData(params, url);
});

function getAllTasksForLoggedUser(){
	$.ajax({
		url:'/auction/tasks',
		type: 'GET',
		success: function(tasks){
        	if(tasks.length != 0){
        		var task = tasks[0];
        		var id = task.id;
        		var taskName = task.name;
        		getFormForTask(id, taskName);
        	}else{
        		toastr.info("Trenutno nema zadataka za vas.");
        	}
        },
        error : function(xhr, textStatus, errorThrown) {
            toastr.error('Error!  Status = ' + xhr.status);
        }
	});
}


function submitFormData(formData, url){
	$.ajax({
		url : url,
        type : 'POST',
        data: formData,
        contentType: "application/json",
        async: false,
        success: function(status){
        	if(status == "ok"){
        		toastr.info("Proverite vaš email ili dostupne zadatke.");
        		$(".ams-user-task-form").hide();
        		return false;
        	}else if(status.indexOf("*") !== -1){
        		toastr.info("Preuzimamo ponudu koju ste potvrdili radi izmene. Zadatak za izmenu je sledeći koji treba da preuzmete.")
        		var executionId = status.split("*")[1];
        		traziPonudu(executionId);
        		$(".ams-user-task-form").hide();
        		return false;
        	}else if(status.indexOf("|") !== -1){
        		var mesto = status.split("|");
        		toastr.info("Vaša ponuda se nalazi trenutno na poziciji: " + mesto[1]);        		
        		toastr.info("Proverite vaš email ili dostupne zadatke.");
        		$(".ams-user-task-form").hide();
        		return false;
        	}else{
        		toastr.info("Došlo je do greške prilikom unosa podataka. Pogledajte ponovo dostupne zadatke.");
        		$(".ams-user-task-form").hide();
        		return false;
        	}
        },
	    error : function(xhr, textStatus, errorThrown) {
	        toastr.error('Error!  Status = ' + xhr.status);
	    }
	});
}

function getFormForTask(id, taskName){
	$.ajax({
		url : '/auction/task/' + id,
        type : 'GET',
        async: false,
        success: function(formDTO) {
        	var taskId = id;
        	var formProperties = formDTO.formProperties;
        	
        	if (formProperties.length != 0) {        		
        		if(taskName == "Prijava zahteva"){
        			prikaziFormuZahteva(taskId, formProperties, formDTO);
        		}else if (taskName == "Donošenje odluke o prihvtanju manjeg broja"){
        			prikaziFormuOdlukaManjiBroj(taskId,formProperties, formDTO)
        		}else if (taskName == "Odluka o prihvatanju ponude"){
					prikaziFormuOdlukaOPonudiAgent(taskId, formProperties, formDTO);
        		}else if (taskName == "Popunjavanje ponude"){
        			prikaziFormuPopunjavanjePonude(taskId, formProperties, formDTO);
        		}else if(taskName == "Uvid u stanje?"){
        			prikaziFormuUvidUStanjeAgent(taskId, formProperties, formDTO);
        		}else if(taskName == "Pregled ponude"){
        			prikaziFormuOdlukaOPromeniPonudeAgent(taskId, formProperties, formDTO);
        		}else if(taskName == "Da li proslediti na jos firmi?"){
        			prikazOdlukaProsiriFirme(taskId, formProperties, formDTO);
        		}else if(taskName == "Nijedna ponuda nije stigla?"){
        			prikaziFormuOdlukaNijednaPonuda(taskId, formProperties, formDTO);
        		}else if(taskName == "Unos novog datuma"){
        			prikaziFormuUnosNovogDatuma(taskId, formProperties, formDTO);
        		}else if(taskName == "Odluka o odustanku"){
        			prikaziFormuOdlukaOOdustanku(taskId, formProperties, formDTO);
        		}else if(taskName == "Dodatne informacije"){
        			prikaziFormuOdlukaODodatnimInformacijama(taskId, formProperties, formDTO);
        		}else if(taskName == "Uvid u pojasnjenje i odluka"){
        			prikaziFormuOdlukaPoslePojasnjenja(taskId, formProperties, formDTO);
        		}else if(taskName == "Prihvatanje posla"){
        			prikazFormeZaPrihvatanjePosla(taskId, formProperties, formDTO);
        		}else if(taskName == "Ocenjivanje klijenta"){
        			prikaziFormuZaOcenuKlijenta(taskId, formProperties, formDTO);
        		}else if(taskName == "Potvrda završenog posla i ocena agenta"){
        			prikaziFormuZaOcenuAgenta(taskId, formProperties, formDTO);
        		}
        	}else{
        		if(taskName == "Zahtev za pojasnjenje"){
        			panelPojasnjenje(taskId);
        		}else if(taskName == "Pojasnjenje"){
        			prikazZahtevaZaPojasnjenjeIUnos(taskId);
        		}else if(taskName == "Pregled i odabir ponude"){
            		prikaziPanelSaPonudama(taskId);
        		}else {
        			toastr.info("Potvrdili ste task.");
        		}
        	}
        },
        error : function(xhr, textStatus, errorThrown) {
            toastr.error('Error!  Status = ' + xhr.status);
        }
	}); 
}


// Prikaz formi
function prikaziFormuZahteva(taskId, formProperties, form){
	$(".ams-user-task-form").hide(); //sakrij sve forme
	var $divZahtev = $("#divZahtevAukcija").show();
    var $zahtevForm = $("#formaZahtevAukcija").empty();
    $zahtevForm.append("<fieldset>");
    
    for(i=0; i<formProperties.length; i++){
        var formProperty = formProperties[i];
        var $content = globalFormBuilder.buildFormInput(formProperty, form);
        $zahtevForm.append($content);
    }
    
    $zahtevForm.append("<input hidden type='text' value='" + taskId + "' id='podnesiZahtevTaskId'/>")
    getKategorijePoslova();
	var $button = globalFormBuilder.buildFormButton("success", "podnesiZahtev", "Potvrdi");
    $zahtevForm.append($button);
    
    var dateToday = new Date();
    $("#rokZaPonude").datepicker({dateFormat: 'yy-mm-dd', changeYear: true, changeMonth: true, minDate : dateToday});
    $("#rokIzvrsenja").datepicker({dateFormat: 'yy-mm-dd', changeYear: true, changeMonth: true, minDate : dateToday});
}

function prikaziFormuOdlukaManjiBroj(taskId, formProperties, form){
	$(".ams-user-task-form").hide(); 	//sakrij sve forme
	var $div = $("#divOdlukaManjiBr").show();
    var $forma = $("#formaOdlukaManjiBr").empty();
    $forma.append("<fieldset>");
    
    for(i=0; i<formProperties.length; i++){
        var formProperty = formProperties[i];
        var $content = globalFormBuilder.buildFormInput(formProperty, form);
        $forma.append($content);
    }
    
    $forma.append("<input hidden type='text' value='" + taskId + "'id='potvrdiOdlukuManjiBrId'/>")

	var $button = globalFormBuilder.buildFormButton("success", "potvrdiOdlukuManjiBr", "Potvrdi");
    $forma.append($button);
}

function prikaziFormuOdlukaOPonudiAgent(taskId, formProperties, form){
	$(".ams-user-task-form").hide(); 	//sakrij sve forme
	var $div = $("#divOdlukaOPonudiAgent").show();
	var $forma = $("#formaOdlukaOPonudiAgent").empty();
	$forma.append("<fieldset>");
    
    for(i=0; i<formProperties.length; i++){
        var formProperty = formProperties[i];
        var $content = globalFormBuilder.buildFormInput(formProperty, form);
        $forma.append($content);
    }
    
    $forma.append("<input hidden type='text' value='" + taskId + "'id='potvrdiOdlukuOPonudiAgentId'/>")

	var $button = globalFormBuilder.buildFormButton("success", "potvrdiOdlukuOPonudiAgent", "Potvrdi");
    $forma.append($button);
}

function prikaziFormuPopunjavanjePonude(taskId, formProperties, form){
	$(".ams-user-task-form").hide(); 	//sakrij sve forme
	var $div = $("#divPopunjavanjePonudeAgent").show();
	var $forma = $("#formaPopunjavanjePonudeAgent").empty();
	$forma.append("<fieldset>");
    
    for(i=0; i<formProperties.length; i++){
        var formProperty = formProperties[i];
        var $content = globalFormBuilder.buildFormInput(formProperty, form);
        $forma.append($content);
    }
    
    $forma.append("<input hidden type='text' value='" + taskId + "'id='potvrdiPopunjenuPonuduAgentId'/>")

	var $button = globalFormBuilder.buildFormButton("success", "potvrdiPopunjenuPonuduAgent", "Potvrdi");
    $forma.append($button);
    
    var dateToday = new Date();
    $("#rokPonude").datepicker({dateFormat: 'yy-mm-dd', changeYear: true, changeMonth: true, minDate : dateToday});
    
    //Ako radi izmenu setuj mu sta je bilo prethodno
    if(globalStorage.ponuda != null){    	
    	var datum = new Date(globalStorage.ponuda.rokPonude);
    	var formatDatum = formatDate(datum);
    	$("#cena").val(globalStorage.ponuda.cena);
    	$("#rokPonude").val(formatDatum);
    }
}

function prikaziFormuUvidUStanjeAgent(taskId, formProperties, form){
	$(".ams-user-task-form").hide();
	var $div = $("#divUvidUStanjeAgent").show();
    var $forma = $("#formaUvidUStanjeAgent").empty();
    $forma.append("<fieldset>");
    
    for(i=0; i<formProperties.length; i++){
        var formProperty = formProperties[i];
        var $content = globalFormBuilder.buildFormInput(formProperty, form);
        $forma.append($content);
    }
    
    $forma.append("<input hidden type='text' value='" + taskId + "'id='potvrdiUvidUStanjeAgentId'/>")

	var $button = globalFormBuilder.buildFormButton("success", "potvrdiUvidUStanjeAgent", "Potvrdi");
    $forma.append($button);
}

function prikaziFormuOdlukaOPromeniPonudeAgent(taskId, formProperties, form){
	$(".ams-user-task-form").hide();
	var $div = $("#divPromenaPonudeAgent").show();
    var $forma = $("#formaPromenaPonudeAgent").empty();
    $forma.append("<fieldset>");
    
    for(i=0; i<formProperties.length; i++){
        var formProperty = formProperties[i];
        var $content = globalFormBuilder.buildFormInput(formProperty, form);
        $forma.append($content);
    }
    
    $forma.append("<input hidden type='text' value='" + taskId + "'id='potvrdiPromenaPonudeAgentId'/>")

	var $button = globalFormBuilder.buildFormButton("success", "potvrdiPromenaPonudeAgent", "Potvrdi");
    $forma.append($button);
}

function prikazOdlukaProsiriFirme(taskId, formProperties, form){
	$(".ams-user-task-form").hide();
	var $div = $("#divOdlukaProsiriFirme").show();
    var $forma = $("#formaOdlukaProsiriFirme").empty();
    $forma.append("<fieldset>");
    
    for(i=0; i<formProperties.length; i++){
        var formProperty = formProperties[i];
        var $content = globalFormBuilder.buildFormInput(formProperty, form);
        $forma.append($content);
    }
    
    $forma.append("<input hidden type='text' value='" + taskId + "'id='potvrdiOdlukaProsiriFirmeId'/>")

	var $button = globalFormBuilder.buildFormButton("success", "potvrdiOdlukaProsiriFirme", "Potvrdi");
    $forma.append($button);
}

function prikaziFormuOdlukaNijednaPonuda(taskId, formProperties, form){
	$(".ams-user-task-form").hide();
	var $div = $("#divOdlukaNijednaPonuda").show();
    var $forma = $("#formaOdlukaNijednaPonuda").empty();
    $forma.append("<fieldset>");
    
    for(i=0; i<formProperties.length; i++){
        var formProperty = formProperties[i];
        var $content = globalFormBuilder.buildFormInput(formProperty, form);
        $forma.append($content);
    }
    
    $forma.append("<input hidden type='text' value='" + taskId + "'id='potvrdiOdlukaNijednaPonudaId'/>")

	var $button = globalFormBuilder.buildFormButton("success", "potvrdiOdlukaNijednaPonuda", "Potvrdi");
    $forma.append($button);
}

function prikaziFormuUnosNovogDatuma(taskId, formProperties, form){
	$(".ams-user-task-form").hide();
	var $div = $("#divOdlukaProduziRok").show();
    var $forma = $("#formaOdlukaProduziRok").empty();
    $forma.append("<fieldset>");
    
    for(i=0; i<formProperties.length; i++){
        var formProperty = formProperties[i];
        var $content = globalFormBuilder.buildFormInput(formProperty, form);
        $forma.append($content);
    }
    
    $forma.append("<input hidden type='text' value='" + taskId + "'id='potvrdiOdlukaProduziRokId'/>")

	var $button = globalFormBuilder.buildFormButton("success", "potvrdiOdlukaProduziRok", "Potvrdi");
    $forma.append($button);
    
    var dateToday = new Date();
    $("#noviRokZaPonude").datepicker({dateFormat: 'yy-mm-dd', changeYear: true, changeMonth: true, minDate : dateToday});
}

//Prikaz panela sa ponudama
function prikaziPanelSaPonudama(taskId){
	console.log("Prikaz panela sa ponudama: " + taskId);
	$.ajax({
		url: 'auction/dobaviPonude/' + taskId,
		type: 'GET',
		async: false,
		success: function(ponude){
			$(".ams-user-task-form").hide();
			var $div = $("#divPanelPonuda").show();
			var $kont = $("#ponudeKontejner").empty();
			$("#panelPonudaTaskId").text(taskId);
			if(ponude.length != 0){
				for(var i=0; i<ponude.length; i++){
					var ponuda = ponude[i];
					var cena = ponuda.cena;
					var agent = ponuda.agent;
					var datum = new Date(ponuda.rokPonude);
			    	var formatDatum = formatDate(datum);
			    	$kont.append("<div>" +
			    				 "<p>Rok izvršenja: '" + formatDatum + "'</p>" + 
			    				 "<p>Cena: '" + cena + "' </p>" +
			    				 "<p>Agent: '" + agent + "' </p> " +
			    				 "<p><a class='odabirPonude' id='" + agent + "'>Odaberi ponudu</a></p>"+
			    				 "</div>"+
			    				 "<hr stle='borderd-color:black'>");
				}
				return true;
			}else {
				console.log("nema ponuda");
				var $kont = $("#ponudeKontejner").empty();
				$kont.append("<p>Nema ponuda!Greska</p>");
				return false;
			}			
		},
		error : function(xhr, textStatus, errorThrown) {
	        toastr.error('Error!  Status = ' + xhr.status);
	    }
	});	
}


$(document).on("click", ".odabirPonude", function(){
	var id = $(this).attr("id");
	var taskId = $("#panelPonudaTaskId").text();
	$.ajax({
		url: '/auction/odabirPonude/' + id + '/' + taskId,
		type: 'GET',
		success: function(data){
			console.log(data);
			$(".ams-user-task-form").hide();
			return false;
		},
		error : function(xhr, textStatus, errorThrown) {
	        toastr.error('Error!  Status = ' + xhr.status);
	    }
	});
});

$(document).on("click", "#odbijaPonude", function(){
	var taskId = $("#panelPonudaTaskId").text();
	$.ajax({
		url: '/auction/odbijSvePonude/' + taskId,
		type: 'GET',
		success: function(data){
			console.log(data);
			$(".ams-user-task-form").hide();
			return false;
		},
		error : function(xhr, textStatus, errorThrown) {
	        toastr.error('Error!  Status = ' + xhr.status);
	    }
	});
});

//Donje forme
function prikaziFormuOdlukaOOdustanku(taskId, formProperties, form){
	$(".ams-user-task-form").hide();
	var $div = $("#divOdlukaOOdustanku").show();
    var $forma = $("#formaOdlukaOOdustanku").empty();
    $forma.append("<fieldset>");
    
    for(i=0; i<formProperties.length; i++){
        var formProperty = formProperties[i];
        var $content = globalFormBuilder.buildFormInput(formProperty, form);
        $forma.append($content);
    }
    
    $forma.append("<input hidden type='text' value='" + taskId + "'id='potvrdiOdlukaOOdustankuId'/>")

	var $button = globalFormBuilder.buildFormButton("success", "potvrdiOdlukaOOdustanku", "Potvrdi");
    $forma.append($button);
}

function prikaziFormuOdlukaODodatnimInformacijama(taskId, formProperties, form){
	$(".ams-user-task-form").hide();
	var $div = $("#divOdlukaDodatniInfo").show();
    var $forma = $("#formaOdlukaDodatniInfo").empty();
    $forma.append("<fieldset>");
    
    for(i=0; i<formProperties.length; i++){
        var formProperty = formProperties[i];
        var $content = globalFormBuilder.buildFormInput(formProperty, form);
        $forma.append($content);
    }
    
    $forma.append("<input hidden type='text' value='" + taskId + "'id='potvrdiOdlukaDodatniInfoId'/>")

	var $button = globalFormBuilder.buildFormButton("success", "potvrdiOdlukaDodatniInfo", "Potvrdi");
    $forma.append($button);
}

function prikaziFormuOdlukaPoslePojasnjenja(taskId,formProperties, form){
	$(".ams-user-task-form").hide();
	var $div = $("#divOdlukaPoslePojasnjenja").show();
    var $forma = $("#formaOdlukaPoslePojasnjenja").empty();
    $forma.append("<fieldset>");
    
    for(i=0; i<formProperties.length; i++){
        var formProperty = formProperties[i];
        var $content = globalFormBuilder.buildFormInput(formProperty, form);
        $forma.append($content);
    }
    
    $forma.append("<input hidden type='text' value='" + taskId + "'id='potvrdiOdlukaPoslePojasnjenjaId'/>")

	var $button = globalFormBuilder.buildFormButton("success", "potvrdiOdlukaPoslePojasnjenja", "Potvrdi");
    $forma.append($button);
}


function prikazFormeZaPrihvatanjePosla(taskId,formProperties, form){
	$(".ams-user-task-form").hide();
	var $div = $("#divPrihvatanjePosla").show();
    var $forma = $("#formaPrihvatanjePosla").empty();
    $forma.append("<fieldset>");
    
    for(i=0; i<formProperties.length; i++){
        var formProperty = formProperties[i];
        var $content = globalFormBuilder.buildFormInput(formProperty, form);
        $forma.append($content);
    }
    
    $forma.append("<input hidden type='text' value='" + taskId + "'id='potvrdiPrihvatanjePoslaId'/>")

	var $button = globalFormBuilder.buildFormButton("success", "potvrdiPrihvatanjePosla", "Potvrdi");
    $forma.append($button);
    
    var dateToday = new Date();
    $("#terminPocetkaRadova").datepicker({dateFormat: 'yy-mm-dd', changeYear: true, changeMonth: true, minDate : dateToday});
}

function prikaziFormuZaOcenuKlijenta(taskId,formProperties, form){
	$(".ams-user-task-form").hide();
	var $div = $("#divOcenaKlijenta").show();
    var $forma = $("#formaOcenaKlijenta").empty();
    $forma.append("<fieldset>");
    
    for(i=0; i<formProperties.length; i++){
        var formProperty = formProperties[i];
        var $content = globalFormBuilder.buildFormInput(formProperty, form);
        $forma.append($content);
    }
    
    $forma.append("<input hidden type='text' value='" + taskId + "'id='potvrdiOcenaKlijentaId'/>")

	var $button = globalFormBuilder.buildFormButton("success", "potvrdiOcenaKlijenta", "Potvrdi");
    $forma.append($button);
}

function prikaziFormuZaOcenuAgenta(taskId,formProperties, form){
	$(".ams-user-task-form").hide();
	var $div = $("#divOcenaAgenta").show();
    var $forma = $("#formaOcenaAgenta").empty();
    $forma.append("<fieldset>");
    
    for(i=0; i<formProperties.length; i++){
        var formProperty = formProperties[i];
        var $content = globalFormBuilder.buildFormInput(formProperty, form);
        $forma.append($content);
    }
    
    $forma.append("<input hidden type='text' value='" + taskId + "'id='potvrdiOcenaAgentaId'/>")

	var $button = globalFormBuilder.buildFormButton("success", "potvrdiOcenaAgenta", "Potvrdi");
    $forma.append($button);
}


function panelPojasnjenje(taskId){
	$(".ams-user-task-form").hide();
	var $div = $("#panelPojasnjenje").show();
	var $taskP = $("#potvrdiZahtevZaPojasnjenjeId").text(taskId);
}

$(document).on("click", "#potvrdiZahtevZaPojasnjenje", function(){
	var params = {};
	params["zahtevPojasnjenje"] = $("textarea#zahtevPojasnjenje").val();
	var taskId = $("#potvrdiZahtevZaPojasnjenjeId").text();
	var data = JSON.stringify(params);
	$.ajax({
		url: '/auction/zahtev/' + taskId,
		type: 'POST',
		data: data,
		contentType: "application/json",
		async: false,
		success: function(msg){
			console.log(msg);
		},
		error : function(xhr, textStatus, errorThrown) {
	        toastr.error('Error!  Status = ' + xhr.status);
	    }
	})
});

function prikazZahtevaZaPojasnjenjeIUnos(taskId){
	$(".ams-user-task-form").hide();
	var $div = $("#panelPojasnjenjeOdgovor").show();
	var $taskP = $("#potvrdiZahtevZaPojasnjenjeOdgId").text(taskId);
	$.ajax({
		url: '/auction/zahtevPojasnjenje/' + taskId,
		type : 'GET',
        async: false,
        success: function(zahtev){
        	$("#prilozenZahtev").text(zahtev);
        },
        error : function(xhr, textStatus, errorThrown) {
	        toastr.error('Error!  Status = ' + xhr.status);
	    }        	
	});
}

$(document).on("click", "#potvrdiZahtevZaPojasnjenjeOdg", function(){
	var params = {};
	params["zahtevPojasnjenje"] = $("textarea#zahtevPojasnjenjeOdg").val();
	var taskId = $("#potvrdiZahtevZaPojasnjenjeOdgId").text();
	$.ajax({
		url: '/auction/pojasnjenje/' + taskId,
		type: 'POST',
		data: JSON.stringify(params),
		contentType: "application/json",
		async: false,
		success: function(msg){
			console.log(msg);
			traziPojasnjenje(msg);
		},
		error : function(xhr, textStatus, errorThrown) {
	        toastr.error('Error!  Status = ' + xhr.status);
	    }
	})
});


// Priprema parametara
function pripremiParametreZahteva(){
	var params = {};
	var kategorija = $("#kategorijaPosla option:selected").text();
	var opis = $("#opis").val();
	var procenjenaVrednost = $("#procenjenaVrednost").val();
	console.log($("#rokZaPonude").val());
	var rokZaPonude = $("#rokZaPonude").val();
	var maxPonuda = $("#maxPonuda").val();
	var minPonuda = $("#minPonuda").val();
	var rokIzvrsenja = $("#rokIzvrsenja").val();
	
	params["kategorijaPosla"] = kategorija;
	params["opis"] = opis;
	params["procenjenaVrednost"] = procenjenaVrednost;
	params["rokZaPonude"] = rokZaPonude;
	params["maxPonuda"] = maxPonuda;
	params["minPonuda"] = minPonuda;
	params["rokIzvrsenja"] = rokIzvrsenja;
	
	var jparams = JSON.stringify(params);
	return jparams;
}

function pripremiParametreOdlukeManjiBr(){
	var params = {};
	var odlukaManjiBroj = $("#odlukaManjiBroj option:selected").val();
	params["odlukaManjiBroj"] = odlukaManjiBroj;
	
	var jparams = JSON.stringify(params);
	return jparams;
}

function pripremiParametreOdlukaOPonudiAgent(){
	var params = {};
	var odlukaOPonudi = $("#odlukaOPonudi option:selected").val();
	params["odlukaOPonudi"] = odlukaOPonudi;
	
	var jparams = JSON.stringify(params);
	return jparams;
}

function pripremiParametrePonudeAgent(){
	var params = {};
	var cena = $("#cena").val();
	var rokPonude = $("#rokPonude").val();
	var agent = globalAuthenticator.loggedInUser.username;
	params["cena"] = cena;
	params["rokPonude"] = rokPonude;
	params["agent"] = agent;
	
	var jparams = JSON.stringify(params);
	return jparams;
}

function pripremiParametreUvidUStanjeAgent(){
	var params = {};
	var uvidUStanje = $("#uvidUStanje option:selected").val();
	params["uvidUStanje"] = uvidUStanje;
	
	var jparams = JSON.stringify(params);
	return jparams;
}

function pripremiParametrePromenaPonudeAgent(){
	var params = {};
	var izmenaPonude = $("#izmenaPonude option:selected").val();
	params["izmenaPonude"] = izmenaPonude;
	
	var jparams = JSON.stringify(params);
	return jparams;
}

function pripremiParametreOdlukaProsiriFirme(){
	var params = {};
	var odlukaProslediti = $("#odlukaProslediti option:selected").val();
	params["odlukaProslediti"] = odlukaProslediti;
	
	var jparams = JSON.stringify(params);
	return jparams;
}

function pripremiParametreOdlukaNijednaPonuda(){
	var params = {};
	var odlukaNemaPonuda = $("#odlukaNemaPonuda option:selected").val();
	params["odlukaNemaPonuda"] = odlukaNemaPonuda;
	
	var jparams = JSON.stringify(params);
	return jparams;
}

function pripremiOdlukaOOdustanku(){
	var params = {};
	var odlukaOdustanak = $("#odlukaOdustanak option:selected").val();
	params["odlukaOdustanak"] = odlukaOdustanak;
	
	var jparams = JSON.stringify(params);
	return jparams;
}

function pripremiNoviDatum(){
	var params = {};
	var noviRokZaPonude = $("#noviRokZaPonude").val();
	params["noviRokZaPonude"] = noviRokZaPonude;
	
	var jparams = JSON.stringify(params);
	return jparams;
}

function pripremiDodatniInfo(){
	var params = {};
	var odlukaDodatneInformacije = $("#odlukaDodatneInformacije option:selected").val();
	params["odlukaDodatneInformacije"] = odlukaDodatneInformacije;
	
	var jparams = JSON.stringify(params);
	return jparams;
}

function pripremiParametreNakonPojasnjenja(){
	var params = {};
	var odlukaPoslePojasnjenja = $("#odlukaPoslePojasnjenja option:selected").val();
	params["odlukaPoslePojasnjenja"] = odlukaPoslePojasnjenja;
	
	var jparams = JSON.stringify(params);
	return jparams;
}

function pripremiParametrePrihvatanjePosla(){
	var params = {};
	var terminPocetkaRadova = $("#terminPocetkaRadova").val();
	params["terminPocetkaRadova"] = terminPocetkaRadova;
	
	var jparams = JSON.stringify(params);
	return jparams;
}

function pripremiOcenaAgenta(){
	var params = {};
	var ocenaAgenta = $("#ocenaAgenta option:selected").val();
	var potvrdaPosaoIzvrsen = $("#potvrdaPosaoIzvrsen").val();
	params["ocenaAgenta"] = ocenaAgenta;
	params["potvrdaPosaoIzvrsen"] = potvrdaPosaoIzvrsen;
	
	var jparams = JSON.stringify(params);
	return jparams;
}

function pripremiOcenaKlijenta(){
	var params = {};
	var ocenaKlijenta = $("#ocenaKlijenta option:selected").val();
	params["ocenaKlijenta"] = ocenaKlijenta;
	
	var jparams = JSON.stringify(params);
	return jparams;
}


//Pomocno
function getKategorijePoslova(){
	$.ajax({
		url : '/api/categories',
        type : 'GET',
        async: false,
        success: function(kategorije){
        	if(kategorije.length != 0){
        		var $selectKat = $("#kategorijaPosla");
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

function traziPonudu(execId){
	$.ajax({
		url: '/auction/ponuda/' + execId,
		type: 'GET',
		async: false,
		success: function(tenderDTO){
			console.log(tenderDTO);			
			globalStorage.ponuda = tenderDTO;
			return false;
        },
        error : function(xhr, textStatus, errorThrown) {
            toastr.error('Error!  Status = ' + xhr.status);
        }
	});
}

function traziPojasnjenje(execId){
	$.ajax({
		url: '/auction/pojasnjenje/' + execId,
		type: 'GET',
		async: false,
		succes: function(pojasnjenjne){
			console.log(pojasnjenjne);
			globalStorage.pojasnjenje = pojasnjenje;
			return false;
		},
        error : function(xhr, textStatus, errorThrown) {
            toastr.error('Error!  Status = ' + xhr.status);
        }
	});
}

function formatDate(date) {
    var d = new Date(date),
        month = '' + (d.getMonth() + 1),
        day = '' + d.getDate(),
        year = d.getFullYear();

    if (month.length < 2) month = '0' + month;
    if (day.length < 2) day = '0' + day;

    return [year, month, day].join('-');
}
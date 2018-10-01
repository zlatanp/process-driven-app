package upp.project.controllers;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.transaction.Transactional;

import org.activiti.engine.FormService;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.form.FormProperty;
import org.activiti.engine.form.TaskFormData;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.Execution;
import org.activiti.engine.task.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import upp.project.model.CustomUser;
import upp.project.model.dto.CustomUserDTO;
import upp.project.model.dto.FormDTO;
import upp.project.model.dto.RequestDTO;
import upp.project.model.dto.TaskDTO;
import upp.project.model.dto.TenderDTO;
import upp.project.model.dto.UserDTO;
import upp.project.repository.CustomUserRepo;
import upp.project.service.UserService;
import upp.project.util.Auth;
import upp.project.util.DateConverter;
import upp.project.util.Messages;
import upp.project.util.Validator;

@RestController
@RequestMapping("/auction")
public class AuctionController {
	
	@Autowired
	private RepositoryService repositoryService;
	
	@Autowired
	private FormService formService;
	
	@Autowired
	private RuntimeService runtimeService;
	
	@Autowired
	private TaskService taskService;
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private CustomUserRepo customUserRepo;
	
	
	@GetMapping(value="/start")
	public void newInstance(HttpServletResponse response) throws IOException{
		
		//Proveri ulogovanog
		UserDTO userDTO = userService.read();
		if(userDTO == null){
			response.sendRedirect("/index.html");
		}else {		
			ProcessDefinition procDef = repositoryService.createProcessDefinitionQuery().processDefinitionKey("aukcije").latestVersion().singleResult(); //Dobavlja proces aukcije iz baze
			HashMap<String, Object> variables = new HashMap<>();
			variables.put("klijent", userDTO);
			
			runtimeService.startProcessInstanceByKey("aukcije", variables); //Startovanje procesa i smestanje varijabli
			String message = "Nova instanca je uspešno pokrenuta";
			System.out.println(message);	
		}
	}
	
	@GetMapping(value="/tasks")
	public ResponseEntity<List<TaskDTO>> getTasks(HttpServletResponse response) throws IOException{
		//Proveri ulogovanog
		UserDTO userDTO = userService.read();
		if(userDTO == null){
			response.sendRedirect("/index.html");
			return null;
		}else{
			List<Task> myTasks = taskService.createTaskQuery().taskCandidateOrAssigned(userDTO.getUsername()).list(); //Nalazenje taskova osobi. prihvatanje, dodavanje taskova
			List<TaskDTO> taskDTOs = new ArrayList<>();
			for (Task task : myTasks) {
				TaskDTO taskDTO = new TaskDTO(task);
				taskDTOs.add(taskDTO);
			}
			return new ResponseEntity<List<TaskDTO>>(taskDTOs, HttpStatus.OK); 
		}
	}
	
	@GetMapping("/task/{id}")
	public ResponseEntity<FormDTO> getFormForTask(@PathVariable String id, HttpServletRequest request, HttpServletResponse response) throws IOException {
		
		Task task= taskService.createTaskQuery().taskId(id).singleResult(); //koji task treba da se izvrsi
		String instanceId = task.getProcessInstanceId(); //instanca
		FormDTO formDTO = new FormDTO(); //forma
		
		HashMap<String, Object> variables=(HashMap<String, Object>) runtimeService.getVariables(instanceId);
	
		TaskFormData taskFormData = formService.getTaskFormData(id); //Rukovanje formom iz taska
		List<FormProperty> formProperties = taskFormData.getFormProperties();
		if (formProperties.size() == 0){
//			taskService.complete(id); //samo jedan task nema formu ne smes da ga prekines
			formDTO.setMessage(Messages.SUCCESSFUL_TASK);
			System.out.println("Dobio task bez forme");
		}else {
			formDTO.setFormKey(taskFormData.getFormKey());
			formDTO.setFormProperties(formProperties);
			for(FormProperty formProperty : formProperties){
				if(formProperty.getType().getName().equals("enum")){
					LinkedHashMap<String, String> information = (LinkedHashMap<String, String>) formProperty.getType().getInformation("values");
					formDTO.setEnumMap(information);
				}
			}
		}
		
		return new ResponseEntity<FormDTO>(formDTO, HttpStatus.OK);
	}
	
	
	@PostMapping(value="/confirmZahtev/{taskId}")
	public ResponseEntity<String> confirmDataRequest(@PathVariable String taskId, @RequestBody RequestDTO requestDTO, HttpServletRequest request){
		Task task= taskService.createTaskQuery().taskId(taskId).singleResult();
		String message = "";
		requestDTO = DateConverter.changeRequestDates(requestDTO);
		
		HashMap<String, Object> variables=(HashMap<String, Object>) runtimeService.getVariables(task.getProcessInstanceId());
		UserDTO klijent = (UserDTO) variables.get("klijent");
		String userId = klijent.getUsername();
		requestDTO.setRequestMaker(userId);
		
		if(canExecute(taskId, userId)){
			variables.put("zahtev", requestDTO);
			variables.put("minPonuda", requestDTO.getMinPonuda());
			variables.put("maxPonuda", requestDTO.getMaxPonuda());
			taskService.complete(taskId,variables);
			message = "ok";
		}else {
			message = "fail";
		}
		return new ResponseEntity<String>(message, HttpStatus.OK);
	}
	
	@Transactional
	@PostMapping(value="/confirmGen/{taskId}")
	public ResponseEntity<String> confirmGenData(@PathVariable String taskId, @RequestBody Map<String, String> params, HttpServletRequest request){
		String message = "";
		
		TaskFormData formData = formService.getTaskFormData(taskId); //za validaciju ostavi		
		boolean valid = Validator.validateForm(formData, params);   //genericka validacija forme
		boolean submitForm = true;
		UserDTO logovani = userService.read();
		String userId = logovani.getUsername();
		if(canExecute(taskId, userId) && valid){
			if(params.containsKey("uvidUStanje") && params.get("uvidUStanje").equals("da")){
				String execId = taskService.createTaskQuery().taskId(taskId).singleResult().getExecutionId();
				List<TenderDTO> svePonude = (List<TenderDTO>) runtimeService.getVariable(execId, "ponude");
				int mesto = 0;
				for(TenderDTO ponuda : svePonude){
					if(ponuda.getAgent().equals(userId)){
						mesto = ponuda.getRating();
					}
				}
				message = "ok|" + mesto;
			}else if(params.containsKey("izmenaPonude") && params.get("izmenaPonude").equals("da")){
				String execId = taskService.createTaskQuery().taskId(taskId).singleResult().getExecutionId();
				message = "ok*" + execId;
			}else if(params.containsKey("noviRokZaPonude")){
				String execId = taskService.createTaskQuery().taskId(taskId).singleResult().getProcessInstanceId();
				HashMap<String, Object> variableProcesa=(HashMap<String, Object>) runtimeService.getVariables(execId);
				RequestDTO zahtev = (RequestDTO) variableProcesa.get("zahtev");
				String valueDate = params.get("noviRokZaPonude");
				zahtev = DateConverter.changeDate(zahtev, valueDate);
				runtimeService.setVariable(execId, "zahtev", zahtev);
			}else if(params.containsKey("terminPocetkaRadova")){
				String execId = taskService.createTaskQuery().taskId(taskId).singleResult().getProcessInstanceId();
				String pocetakRadovaStr = params.get("terminPocetkaRadova");
				Date pocetakRadovaDat = DateConverter.formatDate(pocetakRadovaStr);
				runtimeService.setVariable(execId, "terminPocetkaRadova", pocetakRadovaDat);
				submitForm = false;
				message = "ok";
			}else {
				message = "ok";
			}
			
			if(submitForm){
				formService.submitTaskFormData(taskId, params);
			}
		}else{
			message = "fail";
		}
		
		return new ResponseEntity<String>(message, HttpStatus.OK);
	}
	
	@SuppressWarnings("unchecked")
	@PostMapping(value="/confirmPonuda/{taskId}")
	public ResponseEntity<String> confirmDataTender(@PathVariable String taskId, @RequestBody TenderDTO tenderDTO, HttpServletRequest request){
		System.out.println("Potvrdjuje ponudu");
		Task task= taskService.createTaskQuery().taskId(taskId).singleResult();
		String processInstanceId = task.getProcessInstanceId();
		String executionId = task.getExecutionId();
		
		HashMap<String, Object> variableProcesa=(HashMap<String, Object>) runtimeService.getVariables(processInstanceId);
		HashMap<String, Object> variableIzvrsavanja=(HashMap<String, Object>) runtimeService.getVariables(executionId);
		
		String message = "";
		tenderDTO = DateConverter.changeTenderDate(tenderDTO);
		
		UserDTO ulogovani = userService.read();
		String userId = ulogovani.getUsername();
		
		if(canExecute(taskId, userId)){
			RequestDTO zahtev = (RequestDTO) variableProcesa.get("zahtev");
			tenderDTO.setZahtev(zahtev);
			
			List<TenderDTO> ponude;
			
			if(!variableProcesa.containsKey("ponude")){
				ponude = new ArrayList<TenderDTO>();
				ponude.add(tenderDTO);
				System.out.println("duzina ponuda: " + ponude.size());
				variableProcesa.put("ponude", ponude); //za sve
			}else{
				ponude = (List<TenderDTO>) variableProcesa.get("ponude");
				boolean exists = false;
				for(TenderDTO t : ponude){
					if(t.getAgent().equals(tenderDTO.getAgent())){
						exists = true;
						break;
					}
				}
				if(exists){
					for(TenderDTO t : ponude){
						if(t.getAgent().equals(tenderDTO.getAgent())){
							t.setCena(tenderDTO.getCena());
							t.setRokPonude(tenderDTO.getRokPonude());
							break;
						}
					}
				}else {
					ponude.add(tenderDTO);
				}
				System.out.println("duzina ponuda: " + ponude.size());
				variableProcesa.put("ponude", ponude);
			}
			variableIzvrsavanja.put("ponuda", tenderDTO);
			runtimeService.setVariables(executionId, variableIzvrsavanja);		
			taskService.complete(taskId, variableProcesa);
			message = "ok";
		}else {
			message = "fail";
		}
		
		return new ResponseEntity<String>(message, HttpStatus.OK);
	}
	
	@SuppressWarnings("unchecked")
	@GetMapping(value="/dobaviPonude/{taskId}")
	public ResponseEntity<List<TenderDTO>> getSvePonude(@PathVariable String taskId){
		Task task= taskService.createTaskQuery().taskId(taskId).singleResult();
		String processId = task.getProcessInstanceId();
		HashMap<String, Object> variables=(HashMap<String, Object>) runtimeService.getVariables(processId);
		List<TenderDTO> ponude = (List<TenderDTO>) variables.get("ponude");
		return new ResponseEntity<List<TenderDTO>>(ponude, HttpStatus.OK);
	}
	
	@SuppressWarnings("unchecked")
	@GetMapping(value="/ponuda/{execId}")
	public ResponseEntity<TenderDTO> getPonuda(@PathVariable String execId){
		HashMap<String, Object> variables=(HashMap<String, Object>) runtimeService.getVariables(execId);
		List<TenderDTO> ponude = (List<TenderDTO>) variables.get("ponude");
		UserDTO logovani = userService.read();
		TenderDTO trazena = null;
		for(TenderDTO ponuda : ponude){
			if(ponuda.getAgent().equals(logovani.getUsername())){
				trazena = ponuda;
			}
		}
		return new ResponseEntity<TenderDTO>(trazena, HttpStatus.OK);
	}
	
	@PostMapping(value="/ocenaKlijenta/{taskId}")
	public ResponseEntity<String> ocenaKlijenta(@PathVariable String taskId, @RequestBody Map<String, String> params, HttpServletRequest request){
		List<CustomUser> sviKorisnici = customUserRepo.findAll();
		Task task= taskService.createTaskQuery().taskId(taskId).singleResult();
		String processInstanceId = task.getProcessInstanceId();
		HashMap<String, Object> variableProcesa=(HashMap<String, Object>) runtimeService.getVariables(processInstanceId);
		UserDTO klijent = (UserDTO) variableProcesa.get("klijent");
		UserDTO ulogovani = userService.read();
		TaskFormData formData = formService.getTaskFormData(taskId); //za validaciju ostavi		
		boolean valid = Validator.validateForm(formData, params);   //genericka validacija forme
		String message = "";
		if(canExecute(taskId, ulogovani.getUsername()) && valid){
			for(CustomUser c : sviKorisnici){
				if(c.getUsername().equals(klijent.getUsername())){
					double ocena = c.getOcena();
					String novaOcena = params.get("ocenaKlijenta");
					double novaDouble = Double.parseDouble(novaOcena);
					ocena = (novaDouble + ocena) / 2;
					c.setOcena(ocena);
					break;
				}
			}
			customUserRepo.save(sviKorisnici);
			formService.submitTaskFormData(taskId, params);
			message = "ok";
		}else {
			message = "fail";
		}
		return new ResponseEntity<String>(message, HttpStatus.OK);
	}
	
	@PostMapping(value="/ocenaAgenta/{taskId}")
	public ResponseEntity<String> ocenaAgenta(@PathVariable String taskId, @RequestBody Map<String, String> params, HttpServletRequest request){
		List<CustomUser> sviKorisnici = customUserRepo.findAll();
		Task task= taskService.createTaskQuery().taskId(taskId).singleResult();
		String processInstanceId = task.getProcessInstanceId();
		HashMap<String, Object> variableProcesa=(HashMap<String, Object>) runtimeService.getVariables(processInstanceId);
		CustomUserDTO agent = (CustomUserDTO) variableProcesa.get("odabraniAgent");
		UserDTO ulogovani = userService.read();
		TaskFormData formData = formService.getTaskFormData(taskId); //za validaciju ostavi		
		boolean valid = Validator.validateForm(formData, params);   //genericka validacija forme
		String message = "";
		if(canExecute(taskId, ulogovani.getUsername()) && valid){
			for(CustomUser c : sviKorisnici){
				if(c.getUsername().equals(agent.getUsername())){
					double ocena = c.getOcena();
					String novaOcena = params.get("ocenaAgenta");
					double novaDouble = Double.parseDouble(novaOcena);
					ocena = (novaDouble + ocena) / 2;
					c.setOcena(ocena);
					break;
				}
			}
			customUserRepo.save(sviKorisnici);
			formService.submitTaskFormData(taskId, params);
			message = "ok";
		}else {
			message = "fail";
		}
		return new ResponseEntity<String>(message, HttpStatus.OK);
	}
	
	@GetMapping(value="/zahtevPojasnjenje/{taskId}")
	public ResponseEntity<String> getZahtevZaPojasnjenje(@PathVariable String taskId){
		String zahtev = "";
		Task task= taskService.createTaskQuery().taskId(taskId).singleResult();
		String processInstanceId = task.getProcessInstanceId();
		String execId = task.getExecutionId();
		HashMap<String, Object> variableProcesa=(HashMap<String, Object>) runtimeService.getVariables(processInstanceId);
		HashMap<String, Object> variableIzvrsavanja=(HashMap<String, Object>) runtimeService.getVariables(execId);
		if(variableProcesa.containsKey("zahtevPojasnjenje") ||variableIzvrsavanja.containsKey("zahtevPojasnjenje")){
			if(variableProcesa.get("zahtevPojasnjenje") != "" && variableProcesa.get("zahtevPojasnjenje") != null){
				zahtev = (String) variableProcesa.get("zahtevPojasnjenje");
			}else {
				zahtev = (String) variableIzvrsavanja.get("zahtevPojasnjenje");
			}
		}
		return new ResponseEntity<String>(zahtev, HttpStatus.OK);
	}
	
	@GetMapping(value="/pojasnjenje/{taskId}")
	public ResponseEntity<String> getPojasnjenje(@PathVariable String taskId){
		String pojasnjenje = "";
		Task task= taskService.createTaskQuery().taskId(taskId).singleResult();
		String processInstanceId = task.getProcessInstanceId();
		String execId = task.getExecutionId();
		HashMap<String, Object> variableProcesa=(HashMap<String, Object>) runtimeService.getVariables(processInstanceId);
		HashMap<String, Object> variableIzvrsavanja=(HashMap<String, Object>) runtimeService.getVariables(execId);
		if(variableProcesa.containsKey("pojasnjenje") ||variableIzvrsavanja.containsKey("pojasnjenje")){
			if(variableProcesa.get("pojasnjenje") != "" && variableProcesa.get("pojasnjenje") != null){
				pojasnjenje = (String) variableProcesa.get("pojasnjenje");
			}else {
				pojasnjenje = (String) variableIzvrsavanja.get("pojasnjenje");
			}
		}
		return new ResponseEntity<String>(pojasnjenje, HttpStatus.OK);
	}
	
	@PostMapping(value="/zahtev/{taskId}")
	public void postZahtevZaPojasnjenje(@PathVariable String taskId, @RequestBody Map<String, String> params){
		Task task= taskService.createTaskQuery().taskId(taskId).singleResult();
		String processInstanceId = task.getProcessInstanceId();
		String execId = task.getExecutionId();
		HashMap<String, Object> variableProcesa=(HashMap<String, Object>) runtimeService.getVariables(processInstanceId);
		HashMap<String, Object> variableIzvrsavanja=(HashMap<String, Object>) runtimeService.getVariables(execId);
		String zahtevPojasnjenje = params.get("zahtevPojasnjenje");
		variableProcesa.put("zahtevPojasnjenje",zahtevPojasnjenje);
		variableIzvrsavanja.put("zahtevPojasnjenje",zahtevPojasnjenje);
		taskService.complete(taskId, variableProcesa);
		return;
	}
	
	@PostMapping(value="/pojasnjenje/{taskId}")
	public ResponseEntity<String> postPojasnjenje(@PathVariable String taskId, @RequestBody Map<String, String> params){
		Task task= taskService.createTaskQuery().taskId(taskId).singleResult();
		String processInstanceId = task.getProcessInstanceId();
		String execId = task.getExecutionId();
		HashMap<String, Object> variableProcesa=(HashMap<String, Object>) runtimeService.getVariables(processInstanceId);
		HashMap<String, Object> variableIzvrsavanja=(HashMap<String, Object>) runtimeService.getVariables(execId);
		String zahtevPojasnjenje = params.get("pojasnjenje");
		variableProcesa.put("pojasnjenje",zahtevPojasnjenje);
		variableIzvrsavanja.put("pojasnjenje",zahtevPojasnjenje);
		taskService.complete(taskId, variableProcesa);
		return new ResponseEntity<String>(processInstanceId, HttpStatus.OK);
	}
	
	@GetMapping(value="/odbijSvePonude/{taskId}")
	public void odbijSvePonude(@PathVariable String taskId){
		Task task= taskService.createTaskQuery().taskId(taskId).singleResult();
		String processId = task.getProcessInstanceId();
		HashMap<String, Object> variableProcesa=(HashMap<String, Object>) runtimeService.getVariables(processId);
		variableProcesa.put("odabraoPonudu", false);
		taskService.complete(taskId, variableProcesa);
	}
	
	@SuppressWarnings("unchecked")
	@GetMapping(value="/odabirPonude/{idPonude}/{taskId}")
	public void odobriPonudu(@PathVariable String idPonude, @PathVariable String taskId){
		Task task= taskService.createTaskQuery().taskId(taskId).singleResult();
		String processId = task.getProcessInstanceId();
		HashMap<String, Object> variableProcesa=(HashMap<String, Object>) runtimeService.getVariables(processId);
		List<TenderDTO> ponude = (List<TenderDTO>) variableProcesa.get("ponude");
		TenderDTO odabrana = null;
		CustomUserDTO odabraniAgent = null;
		for(TenderDTO t : ponude){
			if(t.getAgent().equals(idPonude)){
				odabrana = t;
				break;
			}
		}
		List<CustomUser> users = customUserRepo.findAll();
		for(CustomUser c : users){
			if(c.getUsername().equals(idPonude)){
				odabraniAgent = new CustomUserDTO(c, false);
				break;
			}
		}
		variableProcesa.put("odabraoPonudu", true);
		variableProcesa.put("odabranaPonuda", odabrana);
		variableProcesa.put("odabraniAgent", odabraniAgent);
		taskService.complete(taskId, variableProcesa);
	}
	
	
	@Transactional
	@GetMapping(value="/acceptLower/{processInstanceId}/{executionId}")
	public void activeProfile(@PathVariable String processInstanceId, @PathVariable String executionId, HttpServletResponse response) throws IOException{
		Execution execution = runtimeService.createExecutionQuery().processInstanceId(processInstanceId).executionId(executionId).singleResult();
		if(execution != null){
			execution = runtimeService.createExecutionQuery().processInstanceId(processInstanceId).signalEventSubscriptionName("prihvataManjePonuda").singleResult();
			runtimeService.signalEventReceived("prihvataManjePonuda", execution.getId());
			System.out.println("Potvrdjen manji broj redirektuj na indeks.");
			response.sendRedirect("/index.html");
		}else {
			System.out.println("iseklo vreme vrati ga na neku drugu stranicu.");
			response.sendRedirect("/timeout.html");
		}	
	}
	
	
	
	private boolean canExecute(String taskId, String userId){
		for (Task t : taskService.createTaskQuery().taskAssignee(userId).list())
			if (t.getId().equals(taskId))
				return true;
		return false;
	}
	
	
	private boolean canClaim(String taskId, String userId){
		for (Task t : taskService.createTaskQuery().taskCandidateUser(userId).list())
			if (t.getId().equals(taskId))
				return true;
		return false;
	}
}

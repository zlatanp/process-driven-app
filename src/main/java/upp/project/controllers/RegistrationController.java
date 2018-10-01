package upp.project.controllers;

import com.google.gson.Gson;
import org.activiti.engine.FormService;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.form.FormProperty;
import org.activiti.engine.form.StartFormData;
import org.activiti.engine.form.TaskFormData;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.Execution;
import org.activiti.engine.task.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import upp.project.model.dto.FormDTO;
import upp.project.model.dto.TaskDTO;
import upp.project.util.Messages;
import upp.project.util.Validator;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.transaction.Transactional;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/registration")
public class RegistrationController {

	@Autowired
	private TaskService taskService;

	@Autowired
	private FormService formService;
	
	@Autowired
	private RepositoryService repositoryService;
	
	@Autowired
	private RuntimeService runtimeService;
	
	private static final Gson gson = new Gson();
	
	@Transactional
	@GetMapping(value="/start")
	public ResponseEntity<FormDTO> newInstance(HttpServletRequest request){
		ProcessDefinition procDef = repositoryService.createProcessDefinitionQuery().processDefinitionKey("registracija").latestVersion().singleResult(); //Nadje proces
		StartFormData formData = formService.getStartFormData(procDef.getId()); //Dobavlja formu
		List<FormProperty> formProperties = formData.getFormProperties(); //Iz forme pravi FORMU
		FormDTO formDTO = new FormDTO();
		
		if(formProperties.size() == 0){
			HashMap<String, Object> variables = new HashMap<>();
			variables.put("korisnik", request.getServerName());
			runtimeService.startProcessInstanceByKey("registracija", variables); //Pokrece proces Registracija
			formDTO.setMessage("Uspesno pokrenut proces registracije. Pristupite unosu podataka.");
		}else {
			formDTO.setFormKey(formData.getFormKey());
			formDTO.setMessage("Forma na startu.");
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
	
	//Dobavi task prvi posle starta
	@Transactional
	@GetMapping(value="/tasks")
	public ResponseEntity<List<TaskDTO>> getTasks(HttpServletRequest request){
		System.out.println("Svi taskovi");
		String assigne = request.getServerName();
		List<Task> myTasks = taskService.createTaskQuery().taskCandidateOrAssigned(assigne).list();
		List<TaskDTO> taskDTOs = new ArrayList<>();
		for (Task task : myTasks) {
			TaskDTO taskDTO = new TaskDTO(task);
			taskDTOs.add(taskDTO);
		}
		return new ResponseEntity<List<TaskDTO>>(taskDTOs, HttpStatus.OK); 
	}
	
	//Unos podataka forma simulacija prihvatanja taska
	@Transactional
	@GetMapping("/task/{id}")
	public ResponseEntity<FormDTO> test(@PathVariable String id, HttpServletRequest request) {
		Task task= taskService.createTaskQuery().taskId(id).singleResult(); //Uzme taj task
		String instanceId = task.getProcessInstanceId();
		FormDTO formDTO = new FormDTO();
		
		HashMap<String, Object> variables=(HashMap<String, Object>) runtimeService.getVariables(instanceId);
		if(variables.containsKey("korisnik") && variables.get("korisnik").equals(request.getServerName())){ //Ako je bas za tog korisnika od kog je dosao zahtev
			TaskFormData taskFormData = formService.getTaskFormData(id);
			List<FormProperty> formProperties = taskFormData.getFormProperties();
			if (formProperties.size() == 0){
				taskService.complete(id);
				formDTO.setMessage(Messages.SUCCESSFUL_TASK);
				System.out.println("Zavrsio je task");
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
		}else {
			formDTO.setMessage(Messages.FAIL_TASK);
		}
		return new ResponseEntity<FormDTO>(formDTO, HttpStatus.OK);
	}
	
	@Transactional
	@PostMapping(value="/confirm/{taskId}")
	public ResponseEntity<FormDTO> confirmData(@PathVariable String taskId, @RequestBody Map<String, String> params, HttpServletRequest request){
		
		Task task= taskService.createTaskQuery().taskId(taskId).singleResult();
		
		System.out.println("Task: " + task.getName());
		
		HashMap<String, Object> variables=(HashMap<String, Object>) runtimeService.getVariables(task.getProcessInstanceId());
		
		FormDTO formDTO = new FormDTO();
		String userId = request.getServerName();
		boolean valid = false;
		if(params.containsKey("naziv")){
			valid = Validator.validAgentGorm(params);
		}else {
			valid = Validator.validRegistrationForm(params);
			variables.put("naziv", "");
			variables.put("kategorija", "");
			variables.put("udaljenost", "");
			runtimeService.setVariables(task.getProcessInstanceId(), variables);
		}
		
		boolean canExec = canExecute(taskId, userId);
		
		if(canExec && valid){
			formService.submitTaskFormData(taskId, params);
			formDTO.setMessage(Messages.SUCCESSFUL_TASK);
			System.out.println("ok");
		}else {
			System.out.println("fail");
			formDTO.setMessage(Messages.FAIL_TASK);
		}
		System.out.println("kraj");
		return new ResponseEntity<FormDTO>(formDTO, HttpStatus.OK);
	}
	
	@Transactional
	@GetMapping(value="/active/{processInstanceId}/{executionId}")
	public void activeProfile(@PathVariable String processInstanceId, @PathVariable String executionId, HttpServletResponse response) throws IOException{
		Execution execution = runtimeService.createExecutionQuery().processInstanceId(processInstanceId).executionId(executionId).singleResult();
		if(execution != null){
			execution = runtimeService.createExecutionQuery().processInstanceId(processInstanceId).signalEventSubscriptionName("Alert za mail").singleResult();
			runtimeService.signalEventReceived("Alert za mail", execution.getId());
			System.out.println("potvrdjen mail redirektuj");
			response.sendRedirect("/index.html");
		}else {
			System.out.println("iseklo vreme vrati ga na neku drugu stranicu");
			response.sendRedirect("/timeout.html");
		}	
	}
	
	private boolean canExecute(String taskId, String userId){
		for (Task t : taskService.createTaskQuery().taskAssignee(userId).list())
			if (t.getId().equals(taskId))
				return true;
		return false;
	}
}

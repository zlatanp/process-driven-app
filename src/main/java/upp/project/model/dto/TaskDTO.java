package upp.project.model.dto;

import java.io.Serializable;

import org.activiti.engine.task.Task;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class TaskDTO implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -2652455673961761613L;

	private String id;
	
	private String name;
	
	private String assigne;
	
	private String processInstanceId;
	
	public TaskDTO(Task task){
		this.id = task.getId();
		this.name = task.getName();
		this.assigne = task.getAssignee();
		this.processInstanceId = task.getProcessInstanceId();
	}
}

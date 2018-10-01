package upp.project.service;


import upp.project.model.dto.UserDTO;

public interface MailService {

	public void send(String executionId, String processInstanceId, String emailTo);
	
	public void sendNemaFirmi(UserDTO user, String kategorija);
}

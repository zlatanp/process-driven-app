package upp.project.service;

import java.util.List;

import upp.project.model.dto.CustomUserDTO;
import upp.project.model.dto.RequestDTO;
import upp.project.model.dto.TenderDTO;

public interface CompanyService {

	List<CustomUserDTO> findCandidates(RequestDTO requestDTO, String processInstanceId);
	
	List<TenderDTO> sortPonude(CustomUserDTO agent, String proccessInstanceId, String executionId);
	
	void test(String proccesInstanceId, String executionId);
	
	List<TenderDTO> ranking(String processInstanceId);
	
	List<CustomUserDTO> findMoreCompanies(String processInstanceId);
}

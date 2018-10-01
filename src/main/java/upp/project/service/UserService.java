package upp.project.service;

import upp.project.model.dto.UserDTO;

public interface UserService {

	UserDTO read();
		
	void activate(String username);
	
	void deactivate(String username);
	
	boolean check(String email, String username, String adresa, String mesto, String ptt, String password, String ime, String prezime, String tip, String naziv, String kategorija, String udaljenost);
}

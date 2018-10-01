package upp.project.model.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

import org.activiti.engine.identity.User;

@Data
@NoArgsConstructor
public class UserDTO implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 6796588639410594561L;
	
	private String username;
	private String password;
	private String email;
	private String firstname;
	private String lastname;


	public UserDTO(User user){
		this.username = user.getId();
		this.password = user.getPassword();
		this.email = user.getEmail();
		this.firstname = user.getFirstName();
		this.lastname = user.getLastName();
	}
}

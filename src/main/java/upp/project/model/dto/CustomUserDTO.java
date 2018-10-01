package upp.project.model.dto;

import java.io.Serializable;

import lombok.Data;
import lombok.NoArgsConstructor;
import upp.project.model.CustomUser;

@Data
@NoArgsConstructor
public class CustomUserDTO implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Long id;
	private String username;
	private String password;
	private String email;
	private String firstname;
	private String lastname;
	private String adresa;
	private String mesto;
	private String ptt;
	private String tip;
	private boolean isActive;
	private double maxDistance = 0.0;
	private String name;
	private JobCategoryDTO jobCategory;
	private double ocena = 0.0;
	
	
	public CustomUserDTO(CustomUser customUser){
		this(customUser, true);
	}
	
	public CustomUserDTO(CustomUser customUser, boolean cascade){
		this.id = customUser.getId();
		this.username = customUser.getUsername();
		this.password = customUser.getPassword();
		this.email = customUser.getEmail();
		this.firstname = customUser.getFirstname();
		this.lastname = customUser.getLastname();
		this.mesto = customUser.getMesto();
		this.ptt = customUser.getPtt();
		this.adresa = customUser.getAdresa();
		this.tip = customUser.getTip().name();
		this.isActive = customUser.isActive();
		this.ocena = customUser.getOcena();
		this.maxDistance = customUser.getMaxDistance();
		if(customUser.getJobCategory() != null){
			this.jobCategory = new JobCategoryDTO(customUser.getJobCategory(), false);
		}
	}
}

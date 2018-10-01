package upp.project.model;


import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
public class CustomUser{

	/**
	 * 
	 */
	private static final long serialVersionUID = 5922828365260003590L;

	public enum Type {
        PRAVNO,
        FIZICKO,
    }
	
	@Id
	@GeneratedValue
	private Long id;
	
	@Column
	private String username;
	
	@Column
	private String password;
	
	@Column
	private String email;
	
	@Column
	private String firstname;
	
	@Column
	private String lastname;
	
	@Column
	private String adresa;
	
	@Column
	private String mesto;
	
	@Column
	private String ptt;
	
	@Enumerated(EnumType.STRING)
	private Type tip;
	
	@Column
	private boolean isActive;
	
	@Column
	private double maxDistance = 0.0;
	
	@Column
	private String name;
	
	@ManyToOne
	private JobCategory jobCategory;
	
	@Column
	private double ocena = 0.0;
}

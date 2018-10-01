package upp.project.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
public class Location {

	@Id
    @GeneratedValue
    private Long id;
	
	@Column
	private double longitude;
	
	@Column
	private double latitude;
	
	@Column
	private String user;
}

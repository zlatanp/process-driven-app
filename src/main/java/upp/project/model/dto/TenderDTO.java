package upp.project.model.dto;

import java.io.Serializable;
import java.util.Date;

import org.springframework.format.annotation.DateTimeFormat;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class TenderDTO implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -7463921421659421648L;
	
	private RequestDTO zahtev;
	
	private double cena;
	
	@DateTimeFormat(pattern="yyyy-MM-dd")
	private Date rokPonude;
	
	private String agent;
	
	private int rating;
}

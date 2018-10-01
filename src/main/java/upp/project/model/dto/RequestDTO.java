package upp.project.model.dto;

import java.io.Serializable;
import java.util.Date;

import org.springframework.format.annotation.DateTimeFormat;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class RequestDTO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5584126555450708852L;

	private String kategorijaPosla;
	
	private String opis;
	
	private double procenjenaVrednost;
	
	@DateTimeFormat(pattern="yyyy-MM-dd")
	private Date rokZaPonude;
	
	private int minPonuda;
	
	private int maxPonuda;
	
	@DateTimeFormat(pattern="yyyy-MM-dd")
	private Date rokIzvrsenja;
	
	private String requestMaker;//username preko kojeg cemo traziti lokaciju
}

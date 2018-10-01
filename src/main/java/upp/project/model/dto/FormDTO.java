package upp.project.model.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.activiti.engine.form.FormProperty;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

@Data
@NoArgsConstructor
public class FormDTO implements Serializable {

    /**
	 * 
	 */
	private static final long serialVersionUID = -432931471176956980L;

	private String message;

    private String formKey;

    private List<FormProperty> formProperties = new ArrayList<>();
    
    private LinkedHashMap<String, String> enumMap = new LinkedHashMap<>();
}

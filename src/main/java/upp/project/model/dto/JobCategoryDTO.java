package upp.project.model.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import upp.project.model.JobCategory;

import java.io.Serializable;
import java.util.List;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
public class JobCategoryDTO implements Serializable{

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private long id;
    private String name;
    private List<CustomUserDTO> companies;

    public JobCategoryDTO(JobCategory jobCategory){
        this(jobCategory, true);
    }

    public JobCategoryDTO(JobCategory jobCategory, boolean cascade){
        this.id = jobCategory.getId();
        this.name = jobCategory.getName();
        if(cascade){
            this.companies = jobCategory.getCompanies().stream().map(company -> new CustomUserDTO(company, false)).collect(Collectors.toList());
        }
    }
}

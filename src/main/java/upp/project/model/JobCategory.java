package upp.project.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

import java.io.Serializable;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
public class JobCategory implements Serializable{

    /**
	 * 
	 */
	private static final long serialVersionUID = 5679461157032563698L;

	@Id
    @GeneratedValue
    private Long id;

    @Column
    private String name;

    @OneToMany(mappedBy = "jobCategory", cascade = CascadeType.ALL)
    private List<CustomUser> companies;
}

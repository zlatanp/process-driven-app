package upp.project.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import upp.project.model.JobCategory;

public interface JobCategoryRepo  extends JpaRepository<JobCategory, Long>{

	JobCategory findByName(String name);
}

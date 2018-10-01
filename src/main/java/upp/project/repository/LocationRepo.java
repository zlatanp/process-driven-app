package upp.project.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import upp.project.model.Location;

public interface LocationRepo extends JpaRepository<Location, Long>{

	Location findByUser(String user);
}

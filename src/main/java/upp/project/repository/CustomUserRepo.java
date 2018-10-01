package upp.project.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import upp.project.model.CustomUser;

public interface CustomUserRepo extends JpaRepository<CustomUser, Long>{

	CustomUser findByUsername(String name);
	
	List<CustomUser> findByTip(CustomUser.Type tip);
}

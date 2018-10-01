package upp.project.service.impl;

import org.activiti.engine.IdentityService;
import org.activiti.engine.identity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import upp.project.model.CustomUser;
import upp.project.model.JobCategory;
import upp.project.model.Location;
import upp.project.model.dto.UserDTO;
import upp.project.repository.CustomUserRepo;
import upp.project.repository.JobCategoryRepo;
import upp.project.repository.LocationRepo;
import upp.project.service.UserService;

import java.util.List;

@Service("userService")
public class UserServiceImpl implements UserService {

	@Autowired
	private IdentityService identityService;
	
	@Autowired
	private CustomUserRepo customUserRepo;
	
	@Autowired
	private LocationRepo locationRepo;
	
	@Autowired
	private JobCategoryRepo jobCategoryRepo;
	
	
	@Override
	public UserDTO read() {
		final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		String username = authentication.getName();
		List<User> users = identityService.createUserQuery().userId(username).list(); //Dodavanje, pretraga, brisanje clanova i grupa
		if(users.size() != 0){
			User user = users.get(0);
			return new UserDTO(user);
		}else {
			return null;
		}
	}

	@Override
	public void activate(String username) {
		System.out.println("Aktiviram korisnika sa username-om: " + username);
		CustomUser custom = customUserRepo.findByUsername(username);
		if(custom != null){
			User newUser = identityService.newUser(username);
			newUser.setEmail(custom.getEmail());
			newUser.setFirstName(custom.getFirstname());
			newUser.setLastName(custom.getLastname());
			newUser.setPassword(custom.getPassword());
			identityService.saveUser(newUser);
			custom.setActive(true);
			customUserRepo.save(custom);
			
			if(custom.getTip().equals(CustomUser.Type.PRAVNO)){
				identityService.createMembership(username, "pravno");
			}else {
				identityService.createMembership(username, "fizicko");
			}
		}
		System.out.println("Kraj aktivan je");
	}

	@Override
	public void deactivate(String username) {
		System.out.println("Deaktivacija. Brisanje mockup-a iz baze: " + username);
		CustomUser custom = customUserRepo.findByUsername(username);
		if(custom != null){
			customUserRepo.delete(custom);
		}
		Location location = locationRepo.findByUser(username);
		if(location != null){
			locationRepo.delete(location);
		}
		System.out.println("Kraj nije aktivan");
	}

	@Override
	public boolean check(String email, String username, String adresa, String mesto, String ptt, String password, String ime, String prezime, String tip, String naziv, String kategorija, String udaljenost) {
		boolean valid = true;
		List<User> users = identityService.createUserQuery().list();
		for(User u : users){
			if(u.getEmail().equals(email) || u.getId().equals(username)){
				valid = false;
				break;
			}
		}
		
		//Ako je sve ok sacuvaj podatke koje imas u bazi za CustomUser-a
		if(valid){
			CustomUser custom = new CustomUser();
			custom.setActive(false);
			custom.setEmail(email);
			custom.setUsername(username);
			custom.setPassword(password);
			custom.setAdresa(adresa);
			custom.setMesto(mesto);
			custom.setPtt(ptt);
			custom.setPassword(password);
			custom.setFirstname(ime);
			custom.setLastname(prezime);
			
			if(tip.equals("fizicko")){
				System.out.println("Novo fizicko lice");
				custom.setTip(CustomUser.Type.FIZICKO);				
				custom = customUserRepo.save(custom);
				System.out.println("Sacuvam fizicko lice");
			}else {
				System.out.println("Novo pravno lice");
				custom.setTip(CustomUser.Type.PRAVNO);
				custom.setName(naziv);
				custom.setMaxDistance(Double.parseDouble(udaljenost));
				JobCategory jobCategory = jobCategoryRepo.findByName(kategorija);
				if(jobCategory != null){
					System.out.println("Nasao kategoriju");
					custom.setJobCategory(jobCategory);
				}
				custom = customUserRepo.save(custom);
				System.out.println("Sacuvam pravno lice");
				
				jobCategory.getCompanies().add(custom);
				System.out.println("Sacuvam kategoriju");
				jobCategoryRepo.save(jobCategory);
			}
		}
		
		return valid;
	}
}

package upp.project.config;

import com.esotericsoftware.yamlbeans.YamlException;
import com.esotericsoftware.yamlbeans.YamlReader;
import org.activiti.engine.IdentityService;
import org.activiti.engine.identity.Group;
import org.activiti.engine.identity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class DataInit implements ApplicationListener<ApplicationReadyEvent> {

	private static final String groupsPath = "./src/main/resources/groups.yml";
	private static final String usersPath = "./src/main/resources/users.yml";
	
	
	@Autowired
	private IdentityService identityService;
	
	
	@Override
	public void onApplicationEvent(ApplicationReadyEvent arg0) {
		initGroupsYml();
		initUsersYml();
		System.out.println("Users and groups initialized!");
	}
	
	@SuppressWarnings("rawtypes")
	private void initGroupsYml(){
		YamlReader reader = null;
		Map map;
		Group newGroup;
		try {
			reader = new YamlReader(new FileReader(groupsPath));
			while (true) {
				map = (Map) reader.read();
				if (map == null)
					break;
				newGroup = identityService.newGroup((String) map.get("id"));
				newGroup.setName((String) map.get("name"));
				newGroup.setType((String) map.get("type"));
				identityService.saveGroup(newGroup);
			}
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		} catch (YamlException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Kreira korisnike i clanstva grupama na osnovu podataka iz yml fajla 
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private void initUsersYml(){
		YamlReader reader = null;
		Map map;
		User newUser;
		try {
			reader = new YamlReader(new FileReader(usersPath));
			while (true) {
				map = (Map) reader.read();
				if (map == null)
					break;
				newUser = identityService.newUser((String) map.get("id"));
				newUser.setFirstName((String) map.get("firstName"));
				newUser.setLastName((String) map.get("lastName"));
				newUser.setEmail((String) map.get("email"));
				newUser.setPassword((String) map.get("password"));
				identityService.saveUser(newUser);
				
				for (HashMap recordMap : (List<HashMap>) map.get("groups"))
					identityService.createMembership(newUser.getId(),(String) recordMap.get("id"));
			}
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (YamlException e) {
			e.printStackTrace();
		}
	}
}

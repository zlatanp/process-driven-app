package upp.project.config;

import org.activiti.engine.IdentityService;
import org.activiti.engine.identity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Component
public class ActivitiUserDetailServiceImpl implements UserDetailsService{

	@Autowired
	private IdentityService identityService;
	
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		List<User> users = identityService.createUserQuery().userId(username).list();
		if (users.size() == 0) {
		        throw new UsernameNotFoundException("Invalid username/password.");
		    }
		User user = users.get(0);
		
		Collection<GrantedAuthority> grandedAuthorities = new ArrayList<GrantedAuthority>();
		grandedAuthorities.add(new SimpleGrantedAuthority("ROLE_USER"));
		return new org.springframework.security.core.userdetails.User(user.getId(), user.getPassword(), true, true, true, true, grandedAuthorities);
	}
}

package upp.project.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import javax.servlet.http.HttpServletResponse;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter{

	private final RestAuthenticationEntryPoint restAuthenticationEntryPoint;
	
	private final ActivitiUserDetailServiceImpl activitiUserDetailServiceImpl;
	
	public SecurityConfig(RestAuthenticationEntryPoint restAuthenticationEntryPoint, ActivitiUserDetailServiceImpl activitiUserDetailServiceImpl){
		this.restAuthenticationEntryPoint = restAuthenticationEntryPoint;
		this.activitiUserDetailServiceImpl = activitiUserDetailServiceImpl;
	}

	
	@Autowired
    public void configAuthentication(AuthenticationManagerBuilder auth) throws Exception {
        final DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider();
        daoAuthenticationProvider.setUserDetailsService(activitiUserDetailServiceImpl);
        
        
        auth.authenticationProvider(daoAuthenticationProvider);
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http
			.formLogin()
			.loginPage("/").permitAll()
			.loginProcessingUrl("/api/users/login").permitAll()
			.usernameParameter("username")
            .successHandler((request, response, authentication) -> response.setStatus(HttpServletResponse.SC_OK))
            .failureHandler((request, response, exception) -> response.setStatus(HttpServletResponse.SC_UNAUTHORIZED))
            .and()
            .httpBasic()
            .and()
            .logout()
            .logoutUrl("/api/logout")
            .deleteCookies("JSESSIONID")
            .and()
            .authorizeRequests()
            .antMatchers("/home/**").authenticated()
            .and()
            .rememberMe()
            .and()
            .csrf()
            .disable().exceptionHandling()
            .authenticationEntryPoint(restAuthenticationEntryPoint);
	}

}

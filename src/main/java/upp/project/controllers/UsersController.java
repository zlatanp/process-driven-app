package upp.project.controllers;


import org.activiti.engine.impl.util.json.HTTP;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import upp.project.service.UserService;
import upp.project.util.Auth;

import javax.transaction.Transactional;

@RestController
@RequestMapping("/api/users")
public class UsersController {

	private final UserService userService;
	
	@Autowired
	public UsersController(UserService userService) {
		this.userService = userService;
	}

	@Transactional
    @PreAuthorize(Auth.AUTHENTICATED)
    @GetMapping(value = "/me")
    public ResponseEntity read() {
        return new ResponseEntity<>(userService.read(), HttpStatus.OK);
    }

    @PostMapping(value = "/login")
    public ResponseEntity login(){
		return new ResponseEntity(HttpStatus.OK);
	}
}

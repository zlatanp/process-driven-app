package upp.project.controllers;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import upp.project.service.JobCategoryService;

@RestController
@RequestMapping("/api/categories")
public class JobCategoryController {

	@Autowired
	private JobCategoryService jobCategoryService;
	
	@Transactional
	@GetMapping
	public ResponseEntity getCategories(){
		return new ResponseEntity<>(jobCategoryService.read(), HttpStatus.OK);
	}
}

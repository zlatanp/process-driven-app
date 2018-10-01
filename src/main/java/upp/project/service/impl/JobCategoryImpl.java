package upp.project.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import upp.project.model.JobCategory;
import upp.project.model.dto.JobCategoryDTO;
import upp.project.repository.JobCategoryRepo;
import upp.project.service.JobCategoryService;

@Service
public class JobCategoryImpl implements JobCategoryService{

	private final JobCategoryRepo jobCategoryRepo;
	
	public JobCategoryImpl(JobCategoryRepo jobCategoryRepo) {
		this.jobCategoryRepo = jobCategoryRepo;
	}
	
	@Override
	public List<JobCategoryDTO> read() {
		List<JobCategory> jobCategories = jobCategoryRepo.findAll();
		List<JobCategoryDTO> jobCategoryDTOs = new ArrayList<>();
		for (JobCategory jobCategory : jobCategories) {
			JobCategoryDTO jobCategoryDTO = new JobCategoryDTO(jobCategory);
			jobCategoryDTOs.add(jobCategoryDTO);
		}
		return jobCategoryDTOs;
	}

}

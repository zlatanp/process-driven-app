package upp.project.service.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import org.activiti.engine.RuntimeService;
import org.activiti.engine.runtime.Execution;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import upp.project.model.CustomUser;
import upp.project.model.JobCategory;
import upp.project.model.Location;
import upp.project.model.dto.CustomUserDTO;
import upp.project.model.dto.RequestDTO;
import upp.project.model.dto.TenderDTO;
import upp.project.repository.CustomUserRepo;
import upp.project.repository.JobCategoryRepo;
import upp.project.service.CompanyService;
import upp.project.service.LocationService;
import upp.project.util.TenderComparator;

@Service("companyService")
public class CompanyServiceImpl implements CompanyService {
	
	@Autowired
	private CustomUserRepo customUserRepo;
	
	@Autowired
	private RuntimeService runtimeService;
	
	@Autowired
	private LocationService locationService;
	
	@Autowired
	private JobCategoryRepo jobCategoryRepo;
	
	@Override
	public List<CustomUserDTO> findCandidates(RequestDTO requestDTO, String processInstanceId) {
		List<CustomUserDTO> firme = new ArrayList<CustomUserDTO>();
		String requsetMaker = requestDTO.getRequestMaker();
		int maxPonuda = requestDTO.getMaxPonuda();
		HashMap<String, Object> variables=(HashMap<String, Object>) runtimeService.getVariables(processInstanceId);
		
		if(!variables.containsKey("ponude")){  //Ako prvi put poziva izbaci mu samo one za tu kategoriju, u tom opsegu i preseci na max broj slucajno
			if(requsetMaker != ""){
				Location userLocation = locationService.getLocationForUser(requsetMaker);			
				List<CustomUser> all = customUserRepo.findByTip(CustomUser.Type.PRAVNO);
				
				for(CustomUser company : all){
					if(company.getJobCategory().getName().equals(requestDTO.getKategorijaPosla())){
						Location companyLocation = locationService.getLocationForUser(company.getUsername());
						double distance = locationService.getDistanceFromLatLonInKm(companyLocation.getLatitude(), companyLocation.getLongitude(), userLocation.getLatitude(), companyLocation.getLongitude());
						if(distance <= company.getMaxDistance()){
							CustomUserDTO companyDTO = new CustomUserDTO(company, true);
							firme.add(companyDTO);
						}
					}
				}
				
				if(maxPonuda < firme.size()){
					Collections.shuffle(firme);
					firme = firme.subList(0, maxPonuda);
				}
				
				variables.put("firme", firme);
				variables.put("ponavljanjeProcesa", 1);
				runtimeService.setVariables(processInstanceId, variables);	
			}
		}else { //ako poziva drugi put treba ponuditi drugim firmama
			
			int brPonavljanja = (int) variables.get("ponavljanjeProcesa");
			if(brPonavljanja <= 2){
				firme = findMoreCompanies(processInstanceId);
				brPonavljanja++;
			}
			runtimeService.setVariable(processInstanceId, "ponavljanjeProcesa", brPonavljanja);
		}
		
		return firme;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<TenderDTO> sortPonude(CustomUserDTO agent, String proccessInstanceId, String executionId) {
		HashMap<String, Object> variables=(HashMap<String, Object>) runtimeService.getVariables(proccessInstanceId);
		List<TenderDTO> ponude = (List<TenderDTO>) variables.get("ponude");
		System.out.println("sort id procesa velikog: " + proccessInstanceId);
		System.out.println("sort size ponuda: " + ponude.size());
		
		if(ponude.size() != 0){
			Collections.sort(ponude, new TenderComparator());
		}
		
		//Namesti svima trenutni rejting
		int brojac = 1;
		for(TenderDTO ponuda : ponude){
			ponuda.setRating(brojac);
			brojac++;
		}
		runtimeService.setVariable(proccessInstanceId, "ponude", ponude);		
		return ponude;
	}

	@Override
	public void test(String proccesInstanceId, String executionId) {
		System.out.println("Servis test");
		HashMap<String, Object> variables=(HashMap<String, Object>) runtimeService.getVariables(proccesInstanceId);
		Execution ex = runtimeService.createExecutionQuery().processInstanceId(proccesInstanceId).singleResult();
		
		HashMap<String, Object> variables1=(HashMap<String, Object>) runtimeService.getVariables(executionId);
		Execution ex1 = runtimeService.createExecutionQuery().processInstanceId(executionId).singleResult();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<TenderDTO> ranking(String processInstanceId) {
		System.out.println("Usao u metodu za rangiranje");
		HashMap<String, Object> variables=(HashMap<String, Object>) runtimeService.getVariables(processInstanceId);
		RequestDTO zahtev = (RequestDTO) variables.get("zahtev");
		List<TenderDTO> svePonude = (List<TenderDTO>)variables.get("ponude");
		
		System.out.println("Proveriti da li su lepo sortirane u prethodnoj metodi jesu: cena1 datum2");
		List<TenderDTO> ponude = new ArrayList<>(svePonude);
		
		for(TenderDTO ponuda : svePonude){
			if(ponuda.getCena()>zahtev.getProcenjenaVrednost() || ponuda.getRokPonude().after(zahtev.getRokIzvrsenja())){
				ponude.set(ponude.size()-1, ponuda);
			}
		}
		runtimeService.setVariable(processInstanceId, "ponude", ponude);
		runtimeService.setVariable(processInstanceId, "odabraoPonudu", false);
		return ponude;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<CustomUserDTO> findMoreCompanies(String processInstanceId) {
		System.out.println("Usao u metodu za trazenje dodatnih kompanija bez promene zahteva"); //kada hoce do maksimalnog broja da trazi
		HashMap<String, Object> variables=(HashMap<String, Object>) runtimeService.getVariables(processInstanceId);
		RequestDTO zahtev = (RequestDTO) variables.get("zahtev");
		List<TenderDTO> postojecePonude = (List<TenderDTO>) variables.get("ponude");
		List<CustomUser> noviKandidati = new ArrayList<>(); //novi kandidati
		List<CustomUserDTO> firme = new ArrayList<>();
		
		List<String> postojeceFirme = new ArrayList<>();
		for(TenderDTO ponuda : postojecePonude){
			postojeceFirme.add(ponuda.getAgent());
		}
		
		JobCategory jobCategory = jobCategoryRepo.findByName(zahtev.getKategorijaPosla());
		List<CustomUser> companies = customUserRepo.findByTip(CustomUser.Type.PRAVNO);
		for(CustomUser company : companies){
			if(company.getJobCategory().equals(jobCategory)){
				if(!postojeceFirme.contains(company.getUsername())){
					noviKandidati.add(company);
				}
			}
		}
		
		for(CustomUser c : noviKandidati){
			CustomUserDTO customUserDTO = new CustomUserDTO(c, true);
			firme.add(customUserDTO);
		}
		
		return firme;
	}
	
	

}

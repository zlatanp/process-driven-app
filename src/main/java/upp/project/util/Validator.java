package upp.project.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.activiti.engine.form.FormProperty;
import org.activiti.engine.form.TaskFormData;

public class Validator {
	
	public static boolean validateForm(TaskFormData formData, Map<String, String> params){
		boolean valid = true;
		List<FormProperty> formProperties = formData.getFormProperties();
		if(!formProperties.isEmpty()){
			for(FormProperty formProperty : formProperties){
				String propId = formProperty.getId();
				String propType = formProperty.getType().getName();
				boolean isRequired = formProperty.isRequired();
				boolean isWritable = formProperty.isWritable();
				boolean isReadable = formProperty.isReadable();
				valid = checkOneProperty(propId, propType, isRequired, isReadable, isWritable, params);
				
				if(!valid)
					break;
			}
		}else {
			valid = false;
		}
		return valid;
	}
	
	
	private static boolean checkOneProperty(String propId, String propType, boolean isReq, boolean isRead, boolean isWri, Map<String, String> params){
		boolean propValid = true;
		if(isReq){
			if(!params.containsKey(propId)){
				propValid = false;
			}else if(params.get(propId).isEmpty() || params.get(propId).isEmpty()){
				propValid = false;
			}
		}
		if(params.containsKey(propId)){
			String val = params.get(propId);
			if(propType.equals("long")){
				try{
					double dVal = Double.parseDouble(val);
					int intVal = Integer.parseInt(val);
				}catch(NumberFormatException e){
					propValid = false;
				}
			}
		}
		return propValid;
	}
	
	
	public static String changeDateFormat(String dateString){
		String reformattedStr;
		SimpleDateFormat fromUser = new SimpleDateFormat("yyyy-mm-dd");
		SimpleDateFormat activitFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm");
		try {
		    reformattedStr = activitFormat.format(fromUser.parse(dateString));
		} catch (ParseException e) {
		    reformattedStr = "";
		}
		return reformattedStr;
	}
	
	//Fali za email i za tipove
	public static boolean validRegistrationForm(Map<String, String> params){
		boolean valid = true;
		boolean ime = checkField(params, "ime");
		boolean prezime = checkField(params, "prezime");
		boolean email = checkField(params, "email");
		boolean username = checkField(params, "username");
		boolean password = checkField(params, "password");
		boolean mesto = checkField(params, "mesto");
		boolean adresa = checkField(params, "adresa");
		boolean ptt = checkField(params, "ptt");
		boolean tip = checkTip(params);
		
		if(!tip || !ptt || !adresa || !mesto || !password || !username || !email || !prezime || !ime){
			valid = false;
		}
		
		return valid;
	}
	
	//Proveri da li je udaljenost broj
	public static boolean validAgentGorm(Map<String, String> params){
		boolean valid = true;
		boolean naziv = checkField(params, "naziv");
		boolean kategorija = checkField(params, "kategorija"); //proveriti da nije mozda neki pogresan broj tj id.
		boolean udaljenost = checkField(params, "udaljenost");
		
		if(!naziv || !kategorija || !udaljenost){
			valid = false;
		}
		return valid;
	}
	
	
	public static boolean validRequestFrom(Map<String, String> params){
		boolean valid = true;
//		boolean kat = checkField(params, "kategorijaPosla");
//		boolean opis = checkField(params, "opis");
//		boolean maxVr = checkField(params, "maxVrednost");
//		boolean rokPon = checkField(params, "rokZaPonude");
//		boolean maxPon = checkField(params, "maxPonuda");
//		boolean minPon = checkField(params, "minPonuda");
//		boolean rokIzv = checkField(params, "rokIzvrsenja");
//		
//		if(!kat || !opis || !maxVr || !rokPon || !maxPon || !minPon || !rokIzv ){
//			valid = false;
//		}
		
		return valid;
	}
	
	private static boolean checkTip(Map<String, String> params){
		boolean valid = true;
		if(!params.keySet().contains("tip")){
			valid = false;
		}else {
			String value = params.get("tip");
			if(!value.equals("pravno") && !value.equals("fizicko")){
				valid = false;
			}
		}		
		return valid;
	}
	
	private static boolean checkField(Map<String, String> params, String field){
		boolean valid = true;
		if(!params.keySet().contains(field)){
			valid = false;
		}else {
			String value = params.get(field);
			if(value.isEmpty() || value == null){
				valid = false;
			}
		}
		return valid;
	}
	

}

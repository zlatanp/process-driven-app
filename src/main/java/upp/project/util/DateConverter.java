package upp.project.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import upp.project.model.dto.RequestDTO;
import upp.project.model.dto.TenderDTO;

public class DateConverter {

	public static Date formatDate(String dateToFormat){
		DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		Calendar c = Calendar.getInstance();
		try{
			Date novi = formatter.parse(dateToFormat);
			c.setTime(novi);
			c.add(Calendar.HOUR, -1);
			return novi;
		}catch (ParseException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public static RequestDTO changeRequestDates(RequestDTO requestDTO){
		Calendar c = Calendar.getInstance(); 
		c.setTime(requestDTO.getRokZaPonude()); 
		c.add(Calendar.HOUR, -1);
		requestDTO.setRokZaPonude(c.getTime());
		
		c = Calendar.getInstance();
		c.setTime(requestDTO.getRokIzvrsenja()); 
		c.add(Calendar.HOUR, -1);
		requestDTO.setRokIzvrsenja(c.getTime());
		
		return requestDTO;
	}
	
	public static TenderDTO changeTenderDate(TenderDTO tenderDTO){
		Calendar c = Calendar.getInstance(); 
		c.setTime(tenderDTO.getRokPonude()); 
		c.add(Calendar.HOUR, -1);
		tenderDTO.setRokPonude(c.getTime());
		
		return tenderDTO;
	}
	
	public static RequestDTO changeDate(RequestDTO request, String dateStr){
		DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		try{
			Date novi = formatter.parse(dateStr);
			request.setRokZaPonude(novi);
			Calendar c = Calendar.getInstance();
			c.setTime(request.getRokZaPonude());
			c.add(Calendar.HOUR, -1);
			request.setRokZaPonude(c.getTime());
			return request;
		}catch (ParseException e) {
			e.printStackTrace();
			return request;
		}
	}
}

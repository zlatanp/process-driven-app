package upp.project.util;

import java.util.Comparator;
import java.util.Date;

import upp.project.model.dto.TenderDTO;

public class TenderComparator implements Comparator<TenderDTO> {

	@Override
	public int compare(TenderDTO o1, TenderDTO o2) {
		int result = 0;
		double cena1 = o1.getCena();
		double cena2 = o2.getCena();
		Date datum1 = o1.getRokPonude();
		Date datum2 = o2.getRokPonude();
		
		if(cena1 < cena2 || datum1.before(datum2)){
			result = -1;
		}else if (cena1 < cena2 || datum1.after(datum2)){
			result = -1;
		}else if(cena1 > cena2 || datum2.before(datum1)){
			result = 1;
		}else if(cena1 > cena2 || datum2.after(datum1)){
			result = 1;
		}else if(cena1 == cena2){
			result = 0;
		}
		
		return result;
	}

}

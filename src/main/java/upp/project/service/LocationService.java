package upp.project.service;

import upp.project.model.Location;

public interface LocationService {
	
	Location getLocationForUser(String username);

	Location bindLocation(String address, String postalCode, String place, String username);
	
	double getDistanceFromLatLonInKm(double lat1, double lon1, double lat2, double lon2);
}

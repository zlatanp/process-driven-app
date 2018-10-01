package upp.project.service.impl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;

import org.activiti.engine.impl.util.json.JSONArray;
import org.activiti.engine.impl.util.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import upp.project.model.Location;
import upp.project.repository.LocationRepo;
import upp.project.service.LocationService;

@Service("locationService")
public class LocationServiceImpl implements LocationService{

	
	@Autowired
	private LocationRepo locationRepo;
	
	@Override
	public Location bindLocation(String address, String postalCode, String place, String username) {
		System.out.println("Location service call");
		System.out.println("Stigli podaci: " + address + " " + postalCode + " " + place + " " + username);
		String longitude = "0";
		String latitude = "0";
		Location location = new Location();
        
		try {
			URL yahoo = new URL("http://maps.googleapis.com/maps/api/geocode/json?address=" + place.replaceAll("\\s", "%20") + "&sensor=true?key=AIzaSyDBA98Ede6vQdKz5MtGRc1Ov6uGr6B3Xyk");
	        URLConnection yc;
			yc = yahoo.openConnection();
			BufferedReader in = new BufferedReader(new InputStreamReader(yc.getInputStream()));
			String inputLine, response = "";
	        while ((inputLine = in.readLine()) != null){
	            response += inputLine;
	        }
	        in.close();
	        JSONObject jsonObj = new JSONObject(response);
	        JSONArray results = jsonObj.getJSONArray("results");
	        if(results.length() > 0){
		        JSONObject locationJSON = results.getJSONObject(0).getJSONObject("geometry").getJSONObject("location");
		        latitude = locationJSON.getString("lat");
		        longitude = locationJSON.getString("lng");
	        }else{
	        	Thread.sleep(1000);
	        	bindLocation(address, postalCode, place, username);
	        }
		} catch (IOException | InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		location.setLatitude(Double.parseDouble(latitude));
		location.setLongitude(Double.parseDouble(longitude));
		location.setUser(username);
		
		System.out.println("Kraj: " + longitude + " " + latitude);
			
		location = saveLocation(location);	
		
		return location;
	}
	
	private Location saveLocation(Location location){
		String username = location.getUser();
		Location locationForReturn = null;
		List<Location> locations = locationRepo.findAll();
		boolean postoji = false;
		for(Location loc : locations){
			if(loc.getUser().equals(username)){
				postoji = true;
				locationForReturn = loc;
				break;
			}
		}
		if(!postoji){
			locationForReturn = locationRepo.save(location);
		}
		return locationForReturn;
	}

	@Override
	public double getDistanceFromLatLonInKm(double lat1, double lon1, double lat2, double lon2) {
		int R = 6371; // Radius of the earth in km
		  double dLat = deg2rad(lat2-lat1);  // deg2rad below
		  double dLon = deg2rad(lon2-lon1); 
		  double a = 
		    Math.sin(dLat/2) * Math.sin(dLat/2) +
		    Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2)) * 
		    Math.sin(dLon/2) * Math.sin(dLon/2)
		    ; 
		  double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a)); 
		  double d = R * c; // Distance in km
		  return d;
	}
	
	private double deg2rad(double deg) {
		  return deg * (Math.PI/180);
	}

	@Override
	public Location getLocationForUser(String username) {
		Location location = locationRepo.findByUser(username);
		return location;
	}

}

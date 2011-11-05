package controllers;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.io.IOUtils;

import models.Place;
import models.User;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;

import sun.net.www.URLConnection;

public class Places extends Application {
	
	public static void listPlaces(String to) throws IOException {
		
		User user = Application.connectedUser();
		
		URL url = new URL("https://maps.googleapis.com/maps/api/place/search/json?location="+user.latitude+","+user.longitude+"&radius=5000&types=food&name="+to+"&sensor=false&key=AIzaSyA9wDnQVhOadohykQVfbRYh_9i3Y7dgFwk");

		InputStreamReader isr = new InputStreamReader(url.openStream());
	
		String jsonTxt = IOUtils.toString( isr );
     
        JSONObject json = (JSONObject) JSONSerializer.toJSON( jsonTxt );        
        JSONArray results = json.getJSONArray("results");

        List<Place> places = new ArrayList();
        
        for (int x = 0; x < results.size(); x++) {
        	JSONObject jsonPlace = results.getJSONObject(x);
        	String placeName = jsonPlace.getString("name");
        	Place place = new Place();
        	place.name = placeName;
        	JSONObject location = jsonPlace.getJSONObject("geometry").getJSONObject("location");
        	place.latitude = Float.valueOf(location.getString("lat"));
        	place.longitude = Float.valueOf(location.getString("lng"));
        	places.add(place);
        }
        
        render(places);
	}
	
}
package controllers;

import java.util.Date;
import java.util.List;

import controllers.Application.MD5Util;

import play.cache.Cache;
import siena.embed.EmbeddedMap;

import models.Request;
import models.User;

@EmbeddedMap
public class Users extends Application {

	public static void showInfo(Long id) {
		User user = User.findById(id);
		double distance = Application.roundTwoDecimals(distance(user.latitude, user.longitude, connectedUser().latitude, connectedUser().longitude));
		render(user, distance);
	}
	
	public static void updateDest(String destination, String latitude, String longitude) {
    	User user = Application.connectedUser();
    	if (user != null) {
    		Cache.set("destination::"+user.id, destination, "30mn");
    		user.destination_lat = Float.valueOf(latitude);
    		user.destination_lon = Float.valueOf(longitude);
    		user.lastSeen = new Date();
    		user.update();
    		Application.usersMatching();
    	}
    }
	
    public static void updateLoc(String latitude, String longitude) {
    	User user = Application.connectedUser();
    	if (user != null) {
    		user.latitude = Float.valueOf(latitude);
    		user.longitude = Float.valueOf(longitude);
    		user.lastSeen = new Date();
    		user.update();
    	}
    }
    
    public static void sendRequest(Long to) {
    	User user = User.findById(to);
    	if (user != null) {
	    	Request request = new Request();
	    	request.from = connectedUser();
	    	request.to = user;
	    	request.date = new Date();
	    	request.insert();
    	}
    	render();
    }
    
    public static void fetchIncomingRequests() {
    	User user = connectedUser();
    	if (user != null) {
	    	List<Request> requests = Request.findIncomingByUser(user.id);
	    	renderJSON(requests);
    	}
    }
}

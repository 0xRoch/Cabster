package controllers;

import java.util.Date;

import models.User;

public class Users extends Application {

    public static void updateLoc(Double latitude, Double longitude) {
    	User user = Application.connectedUser();
    	if (user != null) {
    		user.latitude = latitude;
    		user.longitude = longitude;
    		user.lastSeen = new Date();
    		user.update();
    	}
    }    
}

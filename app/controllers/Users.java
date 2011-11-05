package controllers;

import models.User;

public class Users extends Application {

    public static void updateLoc(Double latitude, Double longitude) {
    	User user = Application.connectedUser();
    	if (user != null) {
    		user.latitude = latitude;
    		user.longitude = longitude;
    		user.update();
    	}
    }    
}

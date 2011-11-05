package controllers;

import java.util.Date;

import models.User;

public class Users extends Application {

    public static void updateLoc(String latitude, String longitude) {
    	User user = Application.connectedUser();
    	if (user != null) {
    		user.latitude = Float.valueOf(latitude);
    		user.longitude = Float.valueOf(longitude);
    		System.out.print(latitude);
    		user.lastSeen = new Date();
    		user.update();
    	}
    }    
}

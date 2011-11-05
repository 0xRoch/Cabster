package controllers;

import play.*;
import play.mvc.*;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;

import models.*;

public class Application extends Controller {

    @Before
    static void globals() {
        renderArgs.put("connected", connectedUser());
    }
	
    public static void index() {
        render();
    }
    
    public static void login() {
    	render();
    }
    
    public static void usersNearby(Long South_Lat, Long South_Lng, Long North_Lat, Long North_Lng) {
    	List<User> users = User.findByLocation(South_Lat, South_Lng, North_Lat, North_Lng);
    	render(users);
    }
    
    // Utils
    static void connect(User user) {
        session.put("logged", user.id);
    }

    public static User connectedUser() {
        String userId = session.get("logged");
        return userId == null ? null : (User) User.findById(Long.parseLong(userId));
    }

    
    public static class MD5Util {    
	    public static String hex(byte[] array) {
	        StringBuilder sb = new StringBuilder();
	        for (int i = 0; i < array.length; ++i) {
	            sb.append(Integer.toHexString((array[i]
	                & 0xFF) | 0x100).substring(1,3));
	        }
	        return sb.toString();
	    }
	    public static String md5Hex (String message) {
	        try {
	            MessageDigest md =
	                MessageDigest.getInstance("MD5");
	            if(message!=null)
	            return hex (md.digest(message.getBytes("CP1252")));
	        } catch (NoSuchAlgorithmException e) {
	        } catch (UnsupportedEncodingException e) {
	        }
	        return null;
	    }
	}
}
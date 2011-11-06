package controllers;

import play.*;
import play.mvc.*;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DecimalFormat;
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
    
    public static void shareACab() {
    	render();
    }
    
    public static void usersMatching() {
    	List<User> users = User.findByDestination(connectedUser().destination_lat, connectedUser().destination_lat);
    	render(users);
    }
    
    public static void usersNearby(float latitude, float longitude) {
    	List<User> users = User.findByLocation(latitude, longitude);
    	String output = "[";
    	for (User user:users) {
    		if (connectedUser() != null) {
    			output += "{\"id\":"+ (user.id != connectedUser().id ? user.id : "\"me\"")+",\"latitude\":"+(float) user.latitude+",\"longitude\":"+(float) user.longitude+"},";
    		}
    	}
    	output = output.substring(0, output.length() -1) + "]";
    	renderText(output);
    }
    
    // Utils
    static void connect(User user) {
        session.put("logged", user.id);
    }

    public static User connectedUser() {
        String userId = session.get("logged");
        return userId == null ? null : (User) User.findById(Long.parseLong(userId));
    }

    public static double roundTwoDecimals(double d) {
        DecimalFormat twoDForm = new DecimalFormat("#.##");
        return Double.valueOf(twoDForm.format(d));
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
    
    public static List sortByValue(Map map) {
        List list = new LinkedList(map.entrySet());
        Collections.sort(list, new Comparator() {
             public int compare(Object o1, Object o2) {
                  return ((Comparable) ((Map.Entry) (o1)).getValue()).compareTo(((Map.Entry) (o2)).getValue());
             }
        });

       Map result = new LinkedHashMap();
       for (Iterator it = list.iterator(); it.hasNext();) {
           Map.Entry entry = (Map.Entry)it.next();
           result.put(entry.getKey(), entry.getValue());
       }
       Collections.reverse(list);
       return list;
    }
    
    public static double distance(double lat1, double lon1, double lat2, double lon2) {
	  double theta = lon1 - lon2;
	  double dist = Math.sin(deg2rad(lat1)) * Math.sin(deg2rad(lat2)) + Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2)) * Math.cos(deg2rad(theta));
	  dist = Math.acos(dist);
	  dist = rad2deg(dist);
	  dist = dist * 60 * 1.1515;
	  return (dist);
	}

    public static double deg2rad(double deg) {
	  return (deg * Math.PI / 180.0);
	}

    public static double rad2deg(double rad) {
	  return (rad * 180.0 / Math.PI);
	}
}
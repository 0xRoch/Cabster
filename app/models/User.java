package models;

import siena.*;

import controllers.Application;
import controllers.Application.MD5Util;
import play.libs.*;
import play.data.validation.*;

import com.google.gson.*;
import play.cache.Cache;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.math.*;

import org.apache.commons.lang.WordUtils;
import org.junit.Ignore;

import play.mvc.Scope.Session;
import siena.embed.EmbedIgnore;
import siena.embed.Embedded;
import siena.embed.EmbeddedMap;

@EmbeddedMap
public class User extends Model {

    @Id
    public Long id;

    public String openId;

    @Email
    public String email;

    @Index("name")
    public String name;
    
    public String twitter;
    public Long twitterId;

    @EmbedIgnore
    public String needConfirmation;

    public float latitude;
    public float longitude;
    public Date lastSeen;
    
    public float destination_lat;
    public float destination_lon;
    
    @EmbedIgnore @Filter("from")
    public Query<Request> outgoing_requests;
    
    @EmbedIgnore @Filter("to")
    public Query<Request> incoming_requests;
    
    // ~~~~~~~~~~~~ 
    
    public User() {}
    
    public User(String openId, String email) {
    	if(findByEmail(email)==null) {
            this.openId = openId;
            this.email = email;
            this.needConfirmation = Codec.UUID();
    	} 
    }

    public User(String email) {
        if(findByEmail(email)==null) {
            this.email = email;
            this.needConfirmation = Codec.UUID();
    	}
    }
    
    public User(String twitter, Long twitterId) {
        if(findByTwitterId(twitterId)==null) {
            this.twitter = twitter;
            this.twitterId = twitterId;
            this.needConfirmation = Codec.UUID();
    	}
    }
    
    public String preferredTime() {
    	return (String) Cache.get("preferredTime::" + this.id);
    }
    
    public String getDestination() {
    	return (String) Cache.get("destination::" + this.id);
    }

    @Override
    public String toString() {
        if(this.twitter != null) {
            return this.twitter;
        } else {
            return this.email;
        }
    }
    
    public String getAvatar() {
    	String hash = MD5Util.md5Hex(this.email);
    	String url = "http://www.gravatar.com/avatar/";
    	return url+hash;
    }
    // ~~~~~~~~~~~~ 
    
    static Query<User> all() {
        return Model.all(User.class);
    }

    public static List<User> contacts() {
        return all().fetch();
    }

    public static User findById(Long id) {
        return all().filter("id", id).get();
    }

    public static User findByEmail(String email) {
        return all().filter("email", email).get();
    }

    public static User findByTwitterId(Long twitterId) {
        return all().filter("twitterId", twitterId).get();
    }
    
    public static User findByName(String name) {
        return all().filter("name", name).get();
    }
    
    public static List<User> findByDestination(float latitude, float longitude) {
    	Calendar cal = new GregorianCalendar();
    	cal.setTime(new Date());
    	cal.add(Calendar.DAY_OF_YEAR,-1);
    	Date oneDayBefore= cal.getTime();
    	List<User> users = all().filter("lastSeen>", oneDayBefore).fetch();
    	Map<User, Double> counter = new HashMap<User, Double>();
    	for(User user:users) {
    		double count = Application.distance(latitude, longitude, user.latitude, user.longitude);
    		counter.put(user, count);
    	}
    	List sorted = Application.sortByValue(counter);//.subList(0, 10);
    	
    	List<User> output = new ArrayList();
    	for (Iterator it = sorted.iterator(); it.hasNext();) {
    		Map.Entry entry = (Map.Entry)it.next();
            entry.getKey();
    		output.add((User) entry.getKey());
    	}
    	return output;
    }
    
    public static List<User> findByLocation(float latitude, float longitude) {
    	Calendar cal = new GregorianCalendar();
    	cal.setTime(new Date());
    	cal.add(Calendar.DAY_OF_YEAR,-1);
    	Date oneDayBefore= cal.getTime();
    	List<User> users = all().filter("lastSeen>", oneDayBefore).fetch();
    	Map<User, Double> counter = new HashMap<User, Double>();
    	for(User user:users) {
    		double count = Application.distance(latitude, longitude, user.latitude, user.longitude);
    		counter.put(user, count);
    	}
    	List sorted = Application.sortByValue(counter);//.subList(0, 10);
    	
    	List<User> output = new ArrayList();
    	for (Iterator it = sorted.iterator(); it.hasNext();) {
    		Map.Entry entry = (Map.Entry)it.next();
            entry.getKey();
    		output.add((User) entry.getKey());
    	}
    	return output;
    }

    public static boolean isEmailAvailable(String email) {
        return findByEmail(email) == null;
    }

    public String gravatar() {
    	String hash = MD5Util.md5Hex(email);
    	String url = "http://www.gravatar.com/avatar/";
    	return url+hash;
    }

    public static void facebookOAuthCallback(JsonObject data){
        String email = data.get("email").getAsString();
        if(findByEmail(email)==null) {
        	User user = new User(email);
            user.insert();
            Session.current().put("logged", user.id);
        } else {
        	User user = User.findByEmail(email);
            Session.current().put("logged", user.id);
        }
    }
}

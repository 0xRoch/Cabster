package models;

import siena.*;

import controllers.Application;
import controllers.Application.MD5Util;
import play.libs.*;
import play.data.validation.*;

import com.google.gson.*;
import play.cache.Cache;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.apache.commons.lang.WordUtils;
import org.junit.Ignore;

import play.mvc.Scope.Session;
import siena.embed.EmbedIgnore;
import siena.embed.Embedded;
import siena.embed.EmbeddedMap;

@EmbeddedMap
public class Request extends Model {

    @Id
    public Long id;
    
    public Date date;
    
    @Join @Column("from")
    public User from;
    
    @Join @Column("to")
    public User to;
    
    public boolean accepted;
    public boolean opened;
    
    static Query<Request> all() {
        return Model.all(Request.class);
    }

    public static Request findById(Long id) {
        return all().filter("id", id).get();
    }
    
    public static List<Request> findIncomingByUser(Long id) {
    	User user = User.findById(id);
    	List<Request> toRead = all().filter("to", user).filter("opened", false).fetch();
    	List<Request> accepted = all().filter("from", user).filter("accepted", false).fetch();
    	List<Request> res = new ArrayList<Request>();
    	res.addAll(toRead);
    	res.addAll(accepted);
    	return res;
    }
}

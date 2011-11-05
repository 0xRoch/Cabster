package models;

import siena.*;

import controllers.Application;
import controllers.Application.MD5Util;
import play.libs.*;
import play.data.validation.*;

import com.google.gson.*;
import play.cache.Cache;

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
    
    @Column("from")
    public User from;
    
    @Column("to")
    public User to;
    
    static Query<Request> all() {
        return Model.all(Request.class);
    }

    public static Request findById(Long id) {
        return all().filter("id", id).get();
    }
    
}

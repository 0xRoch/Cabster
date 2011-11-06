package controllers;

import java.util.Date;
import java.util.List;

import play.cache.Cache;

import models.Request;
import models.User;

public class Requests extends Application {

	public static void show(Long id) {
		Request request = Request.findById(id);
		if (connectedUser().id == request.to.id) {
			request.opened = true;
			request.update();
			User from = request.from;
			String leavingWhen = (String) Cache.get("leavingWhen::"+from.id);
			String leavingFrom = (String) Cache.get("leavingFrom::"+from.id);
			render(request, from, leavingWhen, leavingFrom);
		}
	}
	
    public static void markAsRead(Long id) {
    	Request request = Request.findById(id);
    	if (request != null && connectedUser() != null && connectedUser() == request.to) {
    		request.opened = true;
    		request.update();
    	}
    }

}

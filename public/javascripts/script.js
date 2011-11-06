$(function(){
  App.init()
});

var App = {
  
  init: function() {
	  map = new google.maps.Map(document.getElementById("map_canvas"), {
	      zoom: 17,
	      center: new google.maps.LatLng(48.000000, 2.347198),
		  disableDefaultUI: true,
	      mapTypeId: google.maps.MapTypeId.ROADMAP
	  });
	  google.maps.event.addListenerOnce(map, 'idle', function(){
		  if (navigator.geolocation) {
				var watchId = navigator.geolocation.watchPosition(App.successCallback, null, {enableHighAccuracy:true});
		  } else {
				alert("Votre navigateur ne prend pas en compte la géolocalisation HTML5");
		  }
	  });
  },
  
  successCallback: function(position) {
	map.panTo(new google.maps.LatLng(position.coords.latitude, position.coords.longitude));  
	
	var newLineCoordinates = [new google.maps.LatLng(position.coords.latitude, position.coords.longitude)];
	
	var newLine = new google.maps.Polyline({
		path: newLineCoordinates,	       
		strokeColor: "#FF0000",
		strokeOpacity: 1.0,
		strokeWeight: 2
	});
	newLine.setMap(map);
	
	App.usersNearby();
	App.fetchRequests();
	
	$.ajax({
	  type: "POST",
	  url: "/Users/updateLoc",
	  data: "latitude="+position.coords.latitude+"&longitude="+position.coords.longitude,
	}).done(function( msg ) {
	  //alert( "Data Saved: " + msg );
	});
  },
  
  usersNearby: function() {
	   bds = map.getBounds();
	   var South_Lat = bds.getSouthWest().lat();
	   var South_Lng = bds.getSouthWest().lng();
	   var North_Lat = bds.getNorthEast().lat();
	   var North_Lng = bds.getNorthEast().lng();
	   $.getJSON("/Application/usersNearby?slat="+South_Lat+"&slon="+South_Lng+"&nlat="+North_Lat+"&nlon="+North_Lng,
	   function(json) {
		   var markersArray = [];
		   $.each(json, function(i,item){
			   if (item.id == "me") {
				   var marker = new google.maps.Marker({
					   position: new google.maps.LatLng(item.latitude, item.longitude), 
					   map: map
				   });
			   } else {
				   var marker = new google.maps.Marker({
					   position: new google.maps.LatLng(item.latitude, item.longitude), 
					   map: map
				   });
				   google.maps.event.addListener(marker, "click", function() {
				        $.mobile.changePage("/Users/showInfo?id="+item.id)
				   });
			   }
		   });
		});
  },
  
  sendRequest: function(to) {
	  $.ajax({
		  type: "POST",
		  url: "/Users/sendRequest",
		  data: "to="+to,
		});
  },
  
  fetchRequests: function() {
	  $.getJSON("/Users/fetchIncomingRequests",
		   function(json) {
			   $.each(json, function(i,item){
				   //$.mobile.changePage("/Requests/show?id="+item.id)
			   });
			});
  }
}
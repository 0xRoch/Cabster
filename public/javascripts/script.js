$(function(){
  App.init()
});

var App = {

  var markersArray = [],
  map = new google.maps.Map(document.getElementById("map_canvas"), {
      zoom: 17,
      center: new google.maps.LatLng(48.000000, 2.347198),
	  disableDefaultUI: true,
      mapTypeId: google.maps.MapTypeId.ROADMAP
  }),
  
  init: function() {
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
	App.addMarker(new google.maps.LatLng(item.latitude, item.longitude));
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
		   $.each(json, function(i,item){
			   App.addMarker(new google.maps.LatLng(item.latitude, item.longitude));
			   google.maps.event.addListener(marker, "click", function() {
			        $.mobile.changePage("/Users/showInfo?id="+item.id)
			   });
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
  },
  
  addMarker: function(location) {
	  marker = new google.maps.Marker({
	    position: location,
	    map: map
	  });
	  markersArray.push(marker);
  }
}
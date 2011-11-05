$(function(){
  App.init()
});

var App = {

  init: function() {
	App.init()
	App.usersNearby()
  },
  
  init: function() {
	  map = new google.maps.Map(document.getElementById("map_canvas"), {
	      zoom: 10,
	      center: new google.maps.LatLng(48.000000, 2.347198),
		  disableDefaultUI: true,
	      mapTypeId: google.maps.MapTypeId.ROADMAP
	    });	
	  if (navigator.geolocation) {
			var watchId = navigator.geolocation.watchPosition(App.successCallback, null, {enableHighAccuracy:true});
	  } else {
			alert("Votre navigateur ne prend pas en compte la géolocalisation HTML5");
	  }
  },
  
  successCallback: function(position) {
	map.panTo(new google.maps.LatLng(position.coords.latitude, position.coords.longitude));
	var marker = new google.maps.Marker({
		position: new google.maps.LatLng(position.coords.latitude, position.coords.longitude), 
		map: map
	});  
	var previousPosition = null;
	if (previousPosition){
		var newLineCoordinates = [
									 new google.maps.LatLng(previousPosition.coords.latitude, previousPosition.coords.longitude),
									 new google.maps.LatLng(position.coords.latitude, position.coords.longitude)
								   ];
		
		var newLine = new google.maps.Polyline({
			path: newLineCoordinates,	       
			strokeColor: "#FF0000",
			strokeOpacity: 1.0,
			strokeWeight: 2
		});
		newLine.setMap(map);
	}
	previousPosition = position;
	
	App.usersNearby();
	
	$.ajax({
	  type: "POST",
	  url: "/Users/updateLoc",
	  data: "lat="+position.coords.latitude+"&lon="+position.coords.longitude,
	}).done(function( msg ) {
	  //alert( "Data Saved: " + msg );
	});
  },
  
  usersNearby: function() {
	  var bds = map.getBounds();
	   var South_Lat = bds.getSouthWest().lat();
	   var South_Lng = bds.getSouthWest().lng();
	   var North_Lat = bds.getNorthEast().lat();
	   var North_Lng = bds.getNorthEast().lng();
	   $.ajax({
		  type: "POST",
		  url: "/Application/usersNearby",
		  data: "slat="+South_Lat+"&slon="+South_Lng+"&nlat="+North_Lat+"&nlon="+North_Lng,
		}).done(function( msg ) {
		  //alert( "Data Saved: " + msg );
		});
  }
}
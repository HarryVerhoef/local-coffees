var map;
var markers = [];
var circles = [];
var loc;
var radius;
var labels = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
function initMap() {
    var manchester = {lat: 53.4808, lng: -2.2426};
    setLoc(manchester.lng, manchester.lat);
    map = new google.maps.Map(
        document.getElementById('map'), {zoom: 15, center: manchester});
    allCoffees();
    loc = currentLocation(manchester);
    google.maps.event.addListener(map, 'click', function(event) {
        loc.setMap(null);
        loc = currentLocation(event.latLng);
        setLoc(event.latLng.lng, event.latLng.lat);
    });
}
function currentLocation(position) {
    var pinColor = "29d";
    var pinImage = new google.maps.MarkerImage("http://chart.apis.google.com/chart?chst=d_map_pin_letter&chld=%E2%80%A2|" + pinColor,
        new google.maps.Size(21, 34),
        new google.maps.Point(0,0),
        new google.maps.Point(10, 34));
    var pinShadow = new google.maps.MarkerImage("http://chart.apis.google.com/chart?chst=d_map_pin_shadow",
        new google.maps.Size(40, 37),
        new google.maps.Point(0, 0),
        new google.maps.Point(12, 35));
    return new google.maps.Marker({
        position: position, 
        map: map,
        icon: pinImage,
        shadow: pinShadow
    });
}
function setLoc(lng, lat) {
    $("#longitude").val(lng);
    $("#latitude").val(lat);
}
function clearCoffees() {
    markers.forEach( element => {
        element.setMap(null);
    });
    markers = [];
}
function allCoffees() {
    clearCoffees();
    $.get({
        url: "/place",
        success: function(e) {
            placeMarkers(e);
        }
    });
}
function coffeesNearMe(lng, lat, rad) {
    clearCoffees();
    $.get({
        url: "/near/" + lng + "/" + lat + "/" + rad,
        success: function(e) {
            placeMarkers(e);
            circles.forEach( circle => {circle.setMap(null)});
            circles = [];
            circles.push(new google.maps.Circle({
                strokeColor: '#29d',
                strokeOpacity: 0.8,
                strokeWeight: 2,
                fillColor: '#29d',
                fillOpacity: 0.25,
                map: map,
                center: loc.position,
                radius: $("#radius").val() * 0.9144,
                clickable: false
            }));
        }
    });
}
function placeMarkers(e) {
    var labelIndex = 0;
    $("#coffeeLabels").html("");
    e.forEach(element => {
        var coords = {lat: element.location.coordinates[0], lng: element.location.coordinates[1]};
        var marker = new google.maps.Marker({position: coords, map: map});
        var infowindow = new google.maps.InfoWindow({
            content: "<h3 class='title'>" + element.name + "</h3>" + element.email
        });
        marker.addListener("click", function() {
            infowindow.open(map,marker)
        });
        markers.push(marker);
        
    });
}
$(document).ready(function() {
    $("#all-coffees").click(function() {
        allCoffees();
    });
    $("#near-me-buttons").unbind("submit").submit(function() {
        coffeesNearMe($("#longitude").val(), $("#latitude").val(), $("#radius").val());
        return false;
    });
});

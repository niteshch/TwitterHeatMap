var map, heatmap;
var pointArray;
var markers = [];
var host = window.location.hostname;
var socket = new WebSocket("ws://52.22.54.197:8080/stream");
// socket.onopen = onOpen;
socket.onmessage = onMessage;
function onOpen() {
	pointArray = new google.maps.MVCArray();

	map = new google.maps.Map(document.getElementById('map'), {
		zoom : 2,
		center : {
			lat : 37.775,
			lng : -122.434
		},
		mapTypeId : google.maps.MapTypeId.SATELLITE
	});

	heatmap = new google.maps.visualization.HeatmapLayer({
		data : pointArray,
		map : map
	});

	loadData();
	
	$('select').on('change', function() {
		for (var i = 0; i < markers.length; i++) {
			markers[i].setMap(null);
		}
		var keyword = this.value;
		var newPointArray = new google.maps.MVCArray();
		if(keyword === "None"){
			loadData();
			socket = new WebSocket("ws://52.22.54.197:8080/stream");
			socket.onmessage = onMessage;
		}else{
			socket.close();
			$.ajax({
				url : "http://" + host + "/rest/load/tweetsByKeyword?keyword="+keyword
			}).then(
					function(data) {
						var tweetArray = JSON.parse(data);
						$.each(tweetArray, function(index, tweet) {
							var tweetLoc = new google.maps.LatLng(tweet.tweetLatitude,
									tweet.tweetLongitude);
							plotMarker(tweet,tweetLoc,false);
							newPointArray.push(tweetLoc);
						});
					});
					heatmap.data = newPointArray;
		}
    });
}

function loadData() {
	$.ajax({
		url : "http://" + host + "/rest/load/tweets"
	}).then(
			function(data) {
				var tweetArray = JSON.parse(data);
				$.each(tweetArray, function(index, tweet) {
					pointArray.push(new google.maps.LatLng(tweet.tweetLatitude,
							tweet.tweetLongitude));
				});
			});
}

function onMessage(event) {
	console.log(event.data);
	var tweet = JSON.parse(event.data);
	var tweetLoc = new google.maps.LatLng(tweet.tweetLatitude,
			tweet.tweetLongitude);
	plotMarker(tweet,tweetLoc,true);
	pointArray.push(tweetLoc);
}

function plotMarker(tweet,tweetLoc,expire){
	var marker = new google.maps.Marker({
		position : tweetLoc,
		map : map,
		animation : google.maps.Animation.DROP
	})
	markers.push(marker);
	var infowindow = new google.maps.InfoWindow({
	    content: "@"+tweet.tweetUser+"-->"+tweet.tweetText
	});
	google.maps.event.addListener(marker, 'click', (function () {
	    infowindow.open(map, marker);
	}));
	if(expire === true){
		window.setTimeout(function() {
			for (var i = 0; i < markers.length-3; i++) {
				markers[i].setMap(null);
			}
		}, 3000);
	}
}

function toggleHeatmap() {
	heatmap.setMap(heatmap.getMap() ? null : map);
}

function changeGradient() {
	var gradient = [ 'rgba(0, 255, 255, 0)', 'rgba(0, 255, 255, 1)',
			'rgba(0, 191, 255, 1)', 'rgba(0, 127, 255, 1)',
			'rgba(0, 63, 255, 1)', 'rgba(0, 0, 255, 1)', 'rgba(0, 0, 223, 1)',
			'rgba(0, 0, 191, 1)', 'rgba(0, 0, 159, 1)', 'rgba(0, 0, 127, 1)',
			'rgba(63, 0, 91, 1)', 'rgba(127, 0, 63, 1)', 'rgba(191, 0, 31, 1)',
			'rgba(255, 0, 0, 1)' ]
	heatmap.set('gradient', heatmap.get('gradient') ? null : gradient);
}

function changeRadius() {
	heatmap.set('radius', heatmap.get('radius') ? null : 20);
}

function changeOpacity() {
	heatmap.set('opacity', heatmap.get('opacity') ? null : 0.2);
}
<%@ page language="java" pageEncoding="UTF-8" %>
<html>
	<head>
		<title>测试openlayers环境</title>
	</head>
	<link rel="stylesheet" type="text/css" href="../js/openlayers/theme/default/style.css" />
	<script src="..js/openlayers/OpenLayers.js" type="text/javascript"></script>
	<script src="http://maps.google.com/maps?file=api&amp;v=2&amp;sensor=true_or_false&amp;key=ABQIAAAAQfJrm1ytpxlOB-dYDYbthBRTHVHOiibqsgm0Pv9Iars8uByrAhTXznbpdO-XDNbMBWHtTVyArdY0Gg" type="text/javascript"></script>
	<script type="text/javascript">
	    function initialize() {
	      if (GBrowserIsCompatible()) {
	        var map = new GMap2(document.getElementById("map_canvas"));
	        map.setCenter(new GLatLng(39.917, 116.397), 12);
	      }
	    }
	</script>
	<body onload="initialize()">
    	<div id="map_canvas" style="width: 1200px; height: 768px"></div>
  	</body>
</html>
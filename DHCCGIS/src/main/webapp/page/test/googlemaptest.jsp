<%@ page language="java" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<title>Google Map Test</title>
<link rel="stylesheet" type="text/css" href="../openlayer/theme/default/style.css" />
<script src="../openlayer/OpenLayers.js" type="text/javascript"></script>
<script src="http://maps.google.com/maps?file=api&amp;v=2&amp;sensor=true_or_false&amp;key=ABQIAAAAQfJrm1ytpxlOB-dYDYbthBRTHVHOiibqsgm0Pv9Iars8uByrAhTXznbpdO-XDNbMBWHtTVyArdY0Gg" type="text/javascript">
</script>
<script type="text/javascript">
    function initialize() {
      if (GBrowserIsCompatible()) {
        var map = new GMap2(document.getElementById("map_canvas"));
        map.setCenter(new GLatLng(39.917, 116.397), 12);
      }
    }
</script>
</head>
<body onload="initialize()" onunload="GUnload()">
    <div id="map_canvas" style="width: 1200px; height: 768px"></div>
  </body>
</html>
<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<html>
<head>
<title>geoservertest.jsp</title>
<link rel="stylesheet" type="text/css" href="../openlayer/theme/default/style.css" />
<script src="../openlayer/OpenLayers.js" type="text/javascript"></script>
<script type="text/javascript">
	var map, drawControls;
	OpenLayers.Feature.Vector.style['default']['strokeWidth'] = '2';
	function init(){
	    map = new OpenLayers.Map('map');
	    var wmsLayer = new OpenLayers.Layer.WMS(
	        "nurc:mosaic", 
	        "http://localhost:8686/geoserver/wms",
	        {layers: 'taianlayers'}
	    ); 
	    map.addLayers([wmsLayer]);
	}
</script>
</head>
<body onload="init()">
	<div id="map" class="smallmap"></div>
</body>
</html>
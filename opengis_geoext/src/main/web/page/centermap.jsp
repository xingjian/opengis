<%@ page language="java" pageEncoding="UTF-8" %>
<html>
	<head>
	<script type="text/javascript">
			    Ext.onReady(function(){
			    	var map = new OpenLayers.Map();
			    	var wmsLayer = new OpenLayers.Layer.WMS(
						    	        "nurc:mosaic", 
						    	        "http://localhost:8686/geoserver/wms",
						    	        {layers: 'ta_dhccgis_map'}
			    	    ); 
			    	map.addLayer(wmsLayer);
					map.addControl(new OpenLayers.Control.Navigation());
				    new GeoExt.MapPanel({
					    	border:false,
				            renderTo:'gxmap',
				            map:map,
				            height:800,
				            center:new OpenLayers.LonLat(117.08095, 36.20024),
				            zoom:15
				           
				        });
				    });
	</script>
	</head>
	<body>
	<div id="gxmap" />
	</body>
</html>

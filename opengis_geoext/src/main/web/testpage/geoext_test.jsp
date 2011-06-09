<%@ page language="java" pageEncoding="UTF-8" %>
<html>
	<head>
		<title>测试geoext环境</title>
		<script type="text/javascript" src="<%=request.getContextPath()%>/js/ext/ext-base.js"></script>
		<script type="text/javascript" src="<%=request.getContextPath()%>/js/ext/ext-all.js"></script>
		<link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/js/ext/resources/css/ext-all.css"/>
		<link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/js/ext/example/shared/examples.css" />
		<script src="<%=request.getContextPath()%>/js/openlayers/OpenLayers.js" type="text/javascript"></script>
		<script src="<%=request.getContextPath()%>/js/geoext/GeoExt.js" type="text/javascript"></script>
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
				    	border:true,
			            renderTo:'gxmap',
			            height:400,
			            width:600,
			            map:map,
			            center:new OpenLayers.LonLat(117.08095, 36.20024),
			            zoom:15,
			            title: '测试GEOEXT加载地图'
			        });
			    });

		</script>
	</head>
	<body>
	<%=request.getContextPath() %>
	<div id="gxmap"></div>
	</body>
</html>
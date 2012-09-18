package com.dhccgis.cn.layers
{
	import flash.system.Security;
	
	import mx.controls.Alert;
	import mx.formatters.DateFormatter;
	
	import org.openscales.core.layer.TMS;
	import org.openscales.geometry.basetypes.Bounds;
	import org.openscales.geometry.basetypes.Location;
	import org.openscales.proj4as.ProjProjection;

	public class BaiduMapLayer extends TMS
	{
		private var _serviceVersion:String = "1.0.0";       
		private var _format:String = "png";     
		private var _layerName:String;
		private var _type:String;
		public static const HYBRID:String="mt0.google.com/vt/lyrs=h@159000000&hl=ru";
		public static const SAT:String="khm0.google.ru/kh/v=90";
		public static const STANDART:String="mt0.google.com/vt/lyrs=m@159000000&hl=ru";
		
		public function BaiduMapLayer(name:String, url:String, layerName:String="", type:String=SAT){
			super(name, url, layerName);
//			Security.loadPolicyFile("http://google.com/crossdomain.xml");
//			Security.allowDomain( "*" );
//			Security.allowInsecureDomain( "*" );
			this.projection = new ProjProjection("EPSG:4326");
//			this._layerName = layerName;
			//this.maxExtent = new Bounds(-180,-90,90,180);
		}
		
		override public function getURL(bounds:Bounds):String {
			
			var res:Number = this.getSupportedResolution(this.map.resolution).value;
			var x:Number = Math.round((bounds.left - this.maxExtent.left) / (res * this.tileWidth));
			var y:Number = Math.round((this.maxExtent.top - bounds.top) / (res * this.tileHeight));
			var z:Number = this.getZoomForResolution(this.map.resolution.reprojectTo(this.projection).value);
			
			
			var zoom:int = z - 1; 
			var offsetX:int = Math.pow(2, zoom) as int; 
			var offsetY:int = offsetX - 1; 
			var numX:int = x - offsetX; 
			var numY:int = (-y) + offsetY; 
			zoom = z + 1; 
			var num:int = (x + y) % 8 + 1; 
			var url:String = null; 
			url = "http://q" + num + ".baidu.com/it/u=x=" + numX + ";y=" + numY + ";z=" + zoom + ";v=012;type=web&fm=44"; 
			trace(url);
			return url;
	}
		
		
	}
}
package com.promisepb.gisserver.server.rest;

import javax.servlet.ServletContext;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import com.promisepb.gisserver.server.service.WMSMapService;
import com.promisepb.gisserver.server.service.impl.WMSMapServiceImpl;

/**  
 * 功能描述:
 * @author:<a href="mailto:xingjian@yeah.net">邢健</a>  
 * @version: V1.0
 * 日期:2016年6月24日 上午10:34:43  
 */
@Controller
@Path("/WMSServer")
@Scope("prototype")
@Produces( { MediaType.TEXT_HTML, MediaType.APPLICATION_OCTET_STREAM})
public class RestWMSMapService {
	
	final Logger logger  =  LoggerFactory.getLogger(RestWMSMapService.class);
	
	@Context
	private ServletContext context;
	
	@Autowired
    private WMSMapService wmsMapService;
	
	/**
	 * version=1.1.1 —— WMS版本号
	 * request=getmap —— 操作动词，可以为GetCapabilities, GetMap和GetFeatureInfo等
	 * layers=layerName —— 请求地图所包含的图层名，可以为多层
	 * styles=stylename —— 指定图层绘制的样式名
	 * SRS=EPSG:4326 —— 指定地图的坐标投影系统代码
	 * bbox=-125,24,-67,50 —— 请求地图的范围（The Bounding Box）
	 * width=400 —— 地图的像素宽度
	 * height=200 —— 地图的像素高度，宽度和高度的不同设置可能会引起返回图像的变形
	 * format=image/png —— 返回地图图像的格式，可以为Image/gif, image/jpg, image/svg+xml等
	 * request=GetMap
	 * @return
	 */
	@GET
	public byte[] getResult(@QueryParam("VERSION") @DefaultValue("1.1.1") String version,
			@QueryParam("LAYERS") String layers,@QueryParam("STYLES") String styles,
			@QueryParam("CRS") String srs,@QueryParam("BBOX") String bbox,
			@QueryParam("WIDTH") String width,@QueryParam("HEIGHT") String height,
			@QueryParam("FORMAT") @DefaultValue("image/png") String format,
			@QueryParam("REQUEST")  String request){
		return result(version, layers, styles, srs, bbox, width, height, format,request);
	}
	
	@POST
	public byte[] postResult(@QueryParam("version") @DefaultValue("1.1.1") String version,
			@QueryParam("layers") String layers,@QueryParam("styles") String styles,
			@QueryParam("SRS") String srs,@QueryParam("bbox") String bbox,
			@QueryParam("width") String width,@QueryParam("height") String height,
			@QueryParam("format") @DefaultValue("image/png") String format,
			@QueryParam("REQUEST")  String request){
		return result(version, layers, styles, srs, bbox, width, height, format,request);
	}

	private byte[] result(String version,String layers,String styles,String srs,String bbox,String width
			,String height,String format,String request) {
		logger.info("version:"+version+",layers:"+layers+",styles:"+styles+",srs:"+srs+",bbox:"+bbox+",width:"+width+",height:"+height+",format:"+format+",request:"+request);
		return ("version:"+version+",layers:"+layers+",styles:"+styles+",srs:"+srs+",bbox:"+bbox+",width:"+width+",height:"+height+",format:"+format+",request:"+request).getBytes();
	}
	
}

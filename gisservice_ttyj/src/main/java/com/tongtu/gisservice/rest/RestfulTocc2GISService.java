package com.tongtu.gisservice.rest;

import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import com.promise.cn.util.StringUtil;
import com.tongtu.gisservice.service.Tocc2GISService;

import net.sf.json.JSONObject;

/**  
 * 功能描述:
 * @author:<a href="mailto:xingjian@yeah.net">邢健</a>  
 * @version: V1.0
 * 日期:2016年8月7日 下午3:03:31  
 */
@Controller
@Path("/tocc2gisservice")
@Scope("prototype")
public class RestfulTocc2GISService {

	private Logger log = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	private Tocc2GISService tocc2GISService;
	
	@GET
    @Path("/getTSACodeByXY")
	@Produces(MediaType.APPLICATION_JSON)
    public String getTrafficSmallAreaCodeByXY(@QueryParam("x") String x,@QueryParam("y") String y) {
        return tocc2GISService.getTrafficSmallAreaCodeByXY(Double.parseDouble(x), Double.parseDouble(y));
    }
	
	@GET
    @Path("/getBJQXNameByXY")
	@Produces(MediaType.APPLICATION_JSON)
    public String getBeijingQXNameByXY(@QueryParam("x") String x,@QueryParam("y") String y) {
        return tocc2GISService.getBQNameByXY(Double.parseDouble(x), Double.parseDouble(y));
    }
	
	@GET
    @Path("/transWGS84TOGoogle")
	@Produces(MediaType.APPLICATION_JSON)
	public String wgs84TO900913(@QueryParam("x") double x,@QueryParam("y") double y){
		Map<String,String[]> result = new HashMap<String, String[]>();
		double[] xyDouble = tocc2GISService.wgs84TO900913(x, y);
		String formatDouble = "0.000000";
		result.put("result",new String[]{StringUtil.FormatDoubleStr(formatDouble, xyDouble[0]),StringUtil.FormatDoubleStr(formatDouble, xyDouble[1])} );
		JSONObject jsObj = JSONObject.fromObject(result);
		return jsObj.toString();
	}
	
	@GET 
    @Path("/transGoogleToWGS84")
	@Produces(MediaType.APPLICATION_JSON)
	public String googleTOWGS84(@QueryParam("x") double x,@QueryParam("y") double y){
		Map<String,double[]> result = new HashMap<String, double[]>();
		result.put("result", tocc2GISService.googleTOWGS84(x, y));
		JSONObject jsObj = JSONObject.fromObject(result);
		return jsObj.toString();
	}
	
	@GET
    @Path("/getWKTByXY")
	public String getWKTByXY(@QueryParam("x") double x,@QueryParam("y") double y){
		return tocc2GISService.getWKTByXY(x, y);
	}
	
}

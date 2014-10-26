/** @文件名: WMSAction.java @创建人：邢健  @创建日期： 2013-4-2 上午9:09:31 */

package com.pbgis.wms.action;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.geotools.data.ows.GetCapabilitiesRequest;
import org.geotools.data.wms.WMS1_3_0;
import org.geotools.data.wms.request.GetFeatureInfoRequest;
import org.geotools.data.wms.request.GetMapRequest;

/**   
 * @类名: WMSAction.java 
 * @包名: com.pbgis.wms.action 
 * @描述: wms请求处理响应 
 * @作者: xingjian xingjian@yeah.net   
 * @日期:2013-4-2 上午9:09:31 
 * @版本: V1.0   
 */
@SuppressWarnings("all")
public class WMSAction extends HttpServlet{

	@Override
	protected void service(HttpServletRequest request, HttpServletResponse response)throws ServletException, IOException {
		Map<String,String> map = request.getParameterMap();
	    Map param = new HashMap();
	    for (String k:map.keySet()){
	        String s1 = "";
	        if (param.containsKey(k.toUpperCase())) {
	            s1 = param.get(k.toUpperCase()) + ",";
	        }
//	        String[] s2 = (String[]) map.get(k);
//	        for (int i = 0; i < s2.length; i++) {
//	            s1 += s2[i] + (i == 0 ? "" : ",");
//	        }
	        param.put(k.toUpperCase(), s1);
	    }
	    if (!param.containsKey("REQUEST")) {
//	        WMSException.exception(response);
	    } else {
	        String wmsRequest = param.get("REQUEST").toString();
	        if (wmsRequest.equals("GetCapabilities")) {
	            GetCapabilitiesRequest gcr = new WMS1_3_0.GetCapsRequest(new URL(""));
	            doGetCapabilities(gcr, response);
	        } else if (wmsRequest.equals("GetMap")) {
	            GetMapRequest gmr = new WMS1_3_0.GetMapRequest(new URL(""));
	            doGetMap(gmr, response);
	        } else if (wmsRequest.equals("GetFeatureInfo")) {
	        	GetMapRequest gmr = new WMS1_3_0.GetMapRequest(new URL(""));
	            GetFeatureInfoRequest gfr = new WMS1_3_0.GetFeatureInfoRequest(new URL(""),gmr);
	            doGetFeatureInfo(gfr, response);
	        } else {
//	            WMSException.exception(response);
	        }
	    }

	}
	
	protected void doGetCapabilities(GetCapabilitiesRequest gcr,HttpServletResponse response){
		
	}
	
	protected void doGetMap(GetMapRequest gcr,HttpServletResponse response){
		
	}
	
	protected void doGetFeatureInfo(GetFeatureInfoRequest gcr,HttpServletResponse response){
		
	}
}

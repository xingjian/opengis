/** @文件名: WMSRequestController.java @创建人：邢健  @创建日期： 2014-1-18 下午2:10:33 */

package com.pbgis.server.wms;

import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.geotools.styling.Style;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.pbgis.server.utils.KVPUtil;

/**   
 * @类名: WMSRequestController.java 
 * @包名: com.pbgis.server.wms 
 * @描述: 处理wms请求的控制器 
 * @作者: xingjian xingjian@yeah.net   
 * @日期:2014-1-18 下午2:10:33 
 * @版本: V1.0   
 */
@Controller
public class WMSRequestController {
	private Logger logger = LoggerFactory.getLogger(WMSRequestController.class); 
	@Resource(name="webMapService")
	private WebMapService webMapService;
	
	@RequestMapping(value="/wms")
	public void handleWMSRequest(HttpServletRequest request,HttpServletResponse response) throws Exception{
		logger.debug("WMSRequestController-----handleWMSRequest");
		Map<String,Object> map = KVPUtil.parseQueryString(request.getQueryString());
		String uri=request.getRequestURI()+"?"+request.getQueryString();
		if(map.get("REQUEST").equals("GetMap")){
			GetMapRequest gmr = new GetMapRequest(uri);
			gmr.initGetMapRequest(map);
			byte[] imageByte = webMapService.getMap(gmr);
			response.setContentType("image/png");
			OutputStream stream = response.getOutputStream();
	        stream.write(imageByte);
	        stream.flush();
	        stream.close();
		}
		
	}
	
	
}

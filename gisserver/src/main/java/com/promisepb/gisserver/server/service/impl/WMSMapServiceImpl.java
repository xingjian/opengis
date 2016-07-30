package com.promisepb.gisserver.server.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.promisepb.gisserver.server.service.WMSMapService;

/**  
 * 功能描述: wms服务接口实现类
 * @author:<a href="mailto:xingjian@yeah.net">邢健</a>  
 * @version: V1.0
 * 日期:2016年6月24日 上午10:26:01  
 */
@Service("wmsMapService")
public class WMSMapServiceImpl implements WMSMapService {
	final Logger logger = LoggerFactory.getLogger(WMSMapServiceImpl.class);
	
	@Override
	public byte[] getMap() {
		return null;
	}
	
	
}

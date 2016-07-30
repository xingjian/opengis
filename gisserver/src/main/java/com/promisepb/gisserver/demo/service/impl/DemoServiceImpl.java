package com.promisepb.gisserver.demo.service.impl;

import org.springframework.stereotype.Service;

import com.promisepb.gisserver.demo.service.DemoService;

/**  
 * 功能描述:
 * @author:<a href="mailto:xingjian@yeah.net">邢健</a>  
 * @version: V1.0
 * 日期:2016年6月21日 下午4:56:55  
 */
@Service("demoService")
public class DemoServiceImpl implements DemoService {

	
	@Override
	public String say() {
		return "welecome use spring rest !";
	}

}

package com.promisepb.gisserver.demo.rest;

import javax.ws.rs.GET;
import javax.ws.rs.Path;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import com.promisepb.gisserver.demo.service.DemoService;

/**  
 * 功能描述:
 * @author:<a href="mailto:xingjian@yeah.net">邢健</a>  
 * @version: V1.0
 * 日期:2016年6月21日 下午4:56:55  
 */
@Controller
@Path("/demo")
@Scope("prototype")
public class RestfulDemoService {
	@Autowired
    private DemoService demoService;
	
	@GET
    @Path("/say")
    public String sayHello() {
        return demoService.say();
    }
}

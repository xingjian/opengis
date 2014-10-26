/** @文件名: ManagerController.java @创建人：邢健  @创建日期： 2014-1-27 下午3:59:04 */

package com.pbgis.server.manager;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.pbgis.server.manager.service.ManagerService;

/**   
 * @类名: ManagerController.java 
 * @包名: com.pbgis.server.manager 
 * @描述: ManagerController 
 * @作者: xingjian xingjian@yeah.net   
 * @日期:2014-1-27 下午3:59:04 
 * @版本: V1.0   
 */
@Controller
public class ManagerController {

	@Resource(name="managerService")
	private ManagerService managerService;
	
	@RequestMapping(value="/login",method=RequestMethod.POST)
	public @ResponseBody String login(String userName,String passwd,HttpServletRequest request,HttpServletResponse response) throws Exception{
		if(managerService.checkUser(userName, passwd)){
			return "/manager/main";
		}else{
			return "false";
		}
	}

	@RequestMapping(value="/manager/main")
	public String loginSuccess(Model model){
		model.addAttribute("userName", "xingjian");
		return "/manager/main";
	}
	
}

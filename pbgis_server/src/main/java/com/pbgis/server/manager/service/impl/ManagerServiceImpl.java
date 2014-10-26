/** @文件名: ManagerServiceImpl.java @创建人：邢健  @创建日期： 2014-2-11 上午9:00:49 */

package com.pbgis.server.manager.service.impl;

import java.io.File;
import java.util.List;
import java.util.UUID;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Text;

import com.pbgis.server.core.config.ConfigManager;
import com.pbgis.server.core.config.ServerConfig;
import com.pbgis.server.manager.service.ManagerService;
import com.pbgis.server.manager.vo.ServerService;

/**   
 * @类名: ManagerServiceImpl.java 
 * @包名: com.pbgis.server.manager.service.impl 
 * @描述: ManagerService接口实现类 
 * @作者: xingjian xingjian@yeah.net   
 * @日期:2014-2-11 上午9:00:49 
 * @版本: V1.0   
 */
public class ManagerServiceImpl implements ManagerService {

	private Logger logger = LoggerFactory.getLogger(ManagerServiceImpl.class);
	
	/**
	 * 初始化管理配置文件
	 */
	public boolean initConfig(){
		boolean result = false;
		try {
			String configDir = this.getClass().getClassLoader().getResource("").getPath();
			String serverConfigDir = configDir+"server.properties";
			ConfigManager.loadServerConfig(serverConfigDir);
			
		} catch (Exception ex) {
			ex.printStackTrace();
			logger.error("init Config error,please check file path or file content!");
		}
		return result;
	}

	/**
	 * 校验登录用户
	 */
	@Override
	public boolean checkUser(String userName, String password) {
		ServerConfig serverConfig = ConfigManager.serverConfig;
		if(userName.equals(serverConfig.getServerLoginName())&&
				password.equals(serverConfig.getServerLoginPass())){
			return true;
		}else{
			return false;
		}
		
	}

	public Document loadInit(String filePath){
        Document document = null;
        try{
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            document = builder.parse(new File(filePath));
            document.normalize();
            return document;
        }catch(Exception e){
            e.printStackTrace();
            return null;
        }
    }
	
	@Override
	public void addServerService(ServerService serverService) {
		try{
			//读取传入的路径，返回一个document对象
			Document document = loadInit(ConfigManager.serverConfig.getServerServiceFilePath());
			Element eltService = document.createElement("service");
			Element eltName = document.createElement("name");
			Element eltInterfaceNames = document.createElement("interfaceNames");
			Element eltWorkspacePath = document.createElement("workspacePath");
			Element eltUseCache = document.createElement("useCache");
			Element eltUpdateTime = document.createElement("updateTime");
			Element eltCreateTime = document.createElement("createTime");
			eltService.appendChild(eltName);
			eltService.appendChild(eltWorkspacePath);
			eltService.appendChild(eltInterfaceNames);
			eltService.appendChild(eltUseCache);
			eltService.appendChild(eltUpdateTime);
			eltService.appendChild(eltCreateTime);
			//获取根节点
			Element eltRoot = document.getDocumentElement();
			//把叶节点加入到根节点下
			eltRoot.appendChild(eltService);
			//更新修改后的源文件
			saveXML(document, ConfigManager.serverConfig.getServerServiceFilePath());
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	@Override
	public void editServerService(ServerService serverService) {
		
	}

	@Override
	public void deleteServerService(ServerService serverService) {
		
	}

	@Override
	public List<ServerService> queryAllServerService(ServerService serverService) {
		return null;
	}
	
	public boolean saveXML(Document document, String filePath){
        try{
            TransformerFactory tFactory = TransformerFactory.newInstance();
            Transformer transformer = tFactory.newTransformer();
            DOMSource source = new DOMSource(document);
            StreamResult result = new StreamResult(new File(filePath));
            transformer.transform(source, result);
            return true;
        }catch(Exception e){
            e.printStackTrace();
            return false;
        }
    }
}

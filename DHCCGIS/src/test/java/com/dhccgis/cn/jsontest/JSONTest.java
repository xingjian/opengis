/**@Title: JSONTest.java @author promisePB xingjian@yeah.net @date 2011-1-6 上午09:24:44 */

package com.dhccgis.cn.jsontest;

import java.util.HashMap;
import java.util.Map;

import net.sf.json.JSONObject;

import org.junit.Test;

/**   
 * @Title: JSONTest.java 
 * @Package com.dhccgis.cn.jsontest 
 * @Description: 测试json使用 
 * @author promisePB xingjian@yeah.net   
 * @date 2011-1-6 上午09:24:44 
 * @version V1.0   
 */

public class JSONTest {

	/**
	 * 功能：测试json环境
	 * 描述：添加json依赖的包，测试环境
	 */
	@Test
	public void testAddJSONJar(){
		 String json = "{\"name\":\"reiz\"}"; 
         JSONObject jsonObj = JSONObject.fromObject(json); 
         String name = jsonObj.getString("name"); 
         jsonObj.put("initial", name.substring(0, 1).toUpperCase()); 
         String[] likes = new String[] { "JavaScript", "Skiing", "Apple Pie" }; 
         jsonObj.put("likes", likes); 
         Map <String, String> ingredients = new HashMap <String, String>(); 
         ingredients.put("apples", "3kg"); 
         ingredients.put("sugar", "1kg"); 
         ingredients.put("pastry", "2.4kg"); 
         ingredients.put("bestEaten", "outdoors"); 
         jsonObj.put("ingredients",ingredients); 
         System.out.println(jsonObj); 

	}
}

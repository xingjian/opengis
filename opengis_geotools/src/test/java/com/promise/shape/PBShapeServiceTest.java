/** @文件名: PBShapeServiceTest.java @创建人：邢健  @创建日期： 2013-3-1 上午9:11:56 */
package com.promise.shape;

import org.junit.Before;
import org.junit.Test;

import com.promise.shape.service.PBShapeService;
import com.promise.shape.service.impl.PBShapeServiceImpl;

/**   
 * @类名: PBShapeServiceTest.java 
 * @包名: com.promise.shape 
 * @描述: PBShapeService测试用例
 * @作者: xingjian xingjian@yeah.net   
 * @日期:2013-3-1 上午9:11:56 
 * @版本: V1.0   
 */
public class PBShapeServiceTest {
	
	private PBShapeService pbss;
	
	@Before
	public void initPBSS(){
		pbss = new PBShapeServiceImpl();
	}
	
	@Test
	public void testReadShapeFile(){
		String fileURL = "E:\\zy\\bjdata\\tx.dbf";
		pbss.readShapeFile(fileURL,"GBK");
	}
	
}

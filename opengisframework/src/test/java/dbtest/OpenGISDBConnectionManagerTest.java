/*@文件名: OpenGISDBConnectionManagerTest.java  @创建人: 邢健   @创建日期: 2011-9-15 下午5:13:50*/
package dbtest;

import org.junit.Assert;
import org.junit.Test;

import com.promise.opengis.db.dbconnect.GISDBConnectionFactory;
import com.promise.opengis.db.dbconnect.OpenGISDBConnectionManager;

/**   
 * @类名: OpenGISDBConnectionManagerTest.java 
 * @包名: dbtest 
 * @描述: 测试数据库连接 
 * @作者: 邢健 xingjian@yeah.net   
 * @日期: 2011-9-15 下午5:13:50 
 * @版本 V1.0   
 */
public class OpenGISDBConnectionManagerTest {

	
	@Test
	public void testConnectByJDBC() {
		String url = "jdbc:postgresql://localhost:5432/dhccgis";
		OpenGISDBConnectionManager ogdcm = GISDBConnectionFactory.getGISDBConnectionByType("postgis", "dhccgis", "dhccgis", url);
		Assert.assertTrue(ogdcm.conectStatus());
	}

}

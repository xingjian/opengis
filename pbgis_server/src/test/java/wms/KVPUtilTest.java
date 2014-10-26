/** @文件名: KVPUtilTest.java @创建人：邢健  @创建日期： 2014-1-21 上午10:52:43 */

package wms;

import java.util.Map;

import org.junit.Test;

import com.pbgis.server.utils.KVPUtil;

/**   
 * @类名: KVPUtilTest.java 
 * @包名: wms 
 * @描述: KVPUtil测试类 
 * @作者: xingjian xingjian@yeah.net   
 * @日期:2014-1-21 上午10:52:43 
 * @版本: V1.0   
 */
public class KVPUtilTest {

	@Test
	public void testParseQueryString(){
		String getMapRequestString = "http://localhost:8888/pbgis_server/wms?VERSION=1.3.0&REQUEST=GetMap&CRS=CRS:84&BBOX=-97.105,24.913,-78.794,36.358&WIDTH=560&HEIGHT=350&LAYERS=BUILTUPA_1M,COASTL_1M,POLBNDL_1M&STYLES=0XFF8080,0X101040,BLACK&FORMAT=image/png&BGCOLOR=0xFFFFFF&TRANSPARENT=TRUE&EXCEPTIONS=INIMAGE";
		Map<String,Object> result = KVPUtil.parseQueryString(getMapRequestString);
		for(String key : result.keySet()){
			System.out.println(key+"----"+result.get(key));
		}
	}
}

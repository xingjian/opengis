/**@文件名: SpaceQueryServiceImpl.java @作者： promisePB xingjian@yeah.net @日期 2011-1-6 上午11:19:37 */

package com.dhccgis.cn.app.service.impl;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.flex.remoting.RemotingDestination;
import org.springframework.flex.remoting.RemotingInclude;
import org.springframework.stereotype.Service;
import com.dhccgis.cn.app.service.SpaceQueryService;
import com.dhccgis.cn.app.vo.Hospital;
import com.dhccgis.cn.client.db.GISDBConnectionFactory;
import com.dhccgis.cn.client.db.PostGISDBConnection;

/**   
 * @类名: SpaceQueryServiceImpl.java 
 * @包名 com.dhccgis.cn.app.service.impl 
 * @描述: 空间查询服务接口实现类 
 * @作者： promisePB xingjian@yeah.net   
 * @日期： 2011-1-6 上午11:19:37 
 * @版本： V1.0   
 */
@Service("spaceQueryService")
@RemotingDestination(channels={"my-amf","my-secure-amf"})
public class SpaceQueryServiceImpl implements SpaceQueryService {

	private Log log = LogFactory.getLog(this.getClass());
	
	/**
	 * 功能：空间查询
	 * 描述：通过名字进行模糊查询，返回list集合
	 */
	@Override
	@RemotingInclude
	public List<Hospital> getListByName(String name, String tableName) {
		String sql = "select *,st_asgeojson(the_geom) from hospital where \"NAME\" like '%"+name+"%'";
		System.out.println(sql);
		List<Hospital> listHospital = new ArrayList<Hospital>();
		PostGISDBConnection gisConnection = GISDBConnectionFactory.getPostGISDBConnection("localhost", "dhccgis", "dhccgis", "5432", "dhccgis");
		Connection con = gisConnection.con;
		try {
			ResultSet rs = con.createStatement().executeQuery(sql);
			while(rs.next()){
				String hospitalName = rs.getString("NAME");
				String mis_id = rs.getString("MIS_ID");
				System.out.println(rs.getString("st_asgeojson"));
				JSONObject jsonObj = JSONObject.fromObject(rs.getString("st_asgeojson"));
				System.out.println("jsonObj==="+jsonObj);
				

				JSONArray array = jsonObj.getJSONArray("coordinates");
				System.out.println("array.getDouble(0)===="+array.getDouble(0));
				String coordinates = jsonObj.getString("coordinates");
				coordinates = coordinates.replaceAll("[\\[|\\]]", "");
				System.out.println(coordinates);
				String[] coordinateArray = coordinates.split(",");
				String x = coordinateArray[0];
				String y = coordinateArray[1];
				System.out.println(x+"****"+y);
				Hospital hospital = new Hospital();
				hospital.x = x;
				hospital.y = y;
				hospital.setId(mis_id);
				hospital.setName(hospitalName);
				listHospital.add(hospital);
			}
			rs.close();
			//con.close();
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		System.out.println(listHospital.size());
		return listHospital;
	}

}

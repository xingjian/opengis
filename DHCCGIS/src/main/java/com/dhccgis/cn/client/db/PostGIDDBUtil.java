/**@Title: PostGIDDBUtil.java @author promisePB xingjian@yeah.net @date 2011-1-5 下午04:33:27 */

package com.dhccgis.cn.client.db;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**   
 * @Title: PostGIDDBUtil.java 
 * @Package com.dhccgis.cn.client.db 
 * @Description: PostGIS数据库帮助类 
 * @author promisePB xingjian@yeah.net   
 * @date 2011-1-5 下午04:33:27 
 * @version V1.0   
 */

public class PostGIDDBUtil {

	public static Connection con;
	
	/**
	 * 通过表的 名称来获取表的信息
	 * @param tableName
	 * @return
	 */
	public static List<String> getTableMessage(String catalog,String schemaPattern,String tableNamePattern,String columnNamePattern){
		List<String> tableColumnsList = new ArrayList<String>();
		try {
			DatabaseMetaData dmd = con.getMetaData();
			ResultSet rs = dmd.getColumns(catalog, schemaPattern, tableNamePattern, columnNamePattern);
			while(rs.next()) {
			   String columnName = rs.getString("COLUMN_NAME");
			   String columnType = rs.getString("TYPE_NAME");
			   tableColumnsList.add(columnName+":"+columnType);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return tableColumnsList;
		
	}
}

package com.pbgis.engine.datasource;

import java.io.File;
import java.io.Serializable;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.HashMap;
import org.geotools.arcsde.ArcSDEDataStoreFactory;
import org.geotools.data.DataStore;
import org.geotools.data.DataStoreFinder;
import org.geotools.data.mysql.MySQLDataStoreFactory;
import org.geotools.data.oracle.OracleNGDataStoreFactory;
import org.geotools.data.postgis.PostgisNGDataStoreFactory;
import org.geotools.data.shapefile.ShapefileDataStore;
import org.geotools.data.wfs.WFSDataStoreFactory;
/**
 * 
* @类名: DataStoreFactory.java 
* @包名: com.pbgis.engine.datasource 
* @描述: DataStoreFactory 
* @作者: xingjian xingjian@yeah.net   
* @日期:2014-1-24 上午10:19:15 
* @版本: V1.0
 */
public class DataStoreFactory {

	/**
	 * 通过shapefile 返回DataStore
	 * @param file
	 * @param charsetName
	 * @return
	 * @throws Exception
	 */
	public static DataStore getDataStoreFromFile(File file, String charsetName)throws Exception {
		HashMap<String, Serializable> params = new HashMap<String, Serializable>();
		params.put("url", file.toURI().toURL());
		DataStore ds = DataStoreFinder.getDataStore(params);

		if (ds instanceof ShapefileDataStore && charsetName != null) {
			ShapefileDataStore shpDs = (ShapefileDataStore) ds;
			try {
				shpDs.setCharset(Charset.forName(charsetName));
			} catch (Exception ex) {
			}
		}
		return ds;
	}

	public static DataStore getDataStoreFromWFS(String url) throws Exception {
		String getCapabilities = url;
		if (url.contains("?")) {
			getCapabilities += "&request=GetCapabilities";
		} else {
			getCapabilities += "?request=GetCapabilities";
		}
		URL endPoint = new URL(getCapabilities);
		HashMap<String, Serializable> params = new HashMap<String, Serializable>();
		params.put(WFSDataStoreFactory.URL.key, endPoint);
		WFSDataStoreFactory dsFactory = new WFSDataStoreFactory();
		DataStore ds = dsFactory.createDataStore(params);
		return ds;
	}

	public static DataStore getDataStoreFromPostGIS(String host, int port,
			String user, String passwd, String database, String schema)throws Exception {
		HashMap<String, Serializable> params = new HashMap<String, Serializable>();
		params.put(PostgisNGDataStoreFactory.DBTYPE.key, "postgis");
		params.put(PostgisNGDataStoreFactory.HOST.key, host);
		params.put(PostgisNGDataStoreFactory.PORT.key, port);
		params.put(PostgisNGDataStoreFactory.USER.key, user);
		params.put(PostgisNGDataStoreFactory.PASSWD.key, passwd);
		params.put(PostgisNGDataStoreFactory.DATABASE.key, database);
		params.put(PostgisNGDataStoreFactory.SCHEMA.key, schema);
		PostgisNGDataStoreFactory dsFactory = new PostgisNGDataStoreFactory();
		DataStore ds = dsFactory.createDataStore(params);
		return ds;
	}

	public static DataStore getDataStoreFromMySQL(String host, int port,
			String user, String passwd, String database) throws Exception {
		HashMap<String, Serializable> params = new HashMap<String, Serializable>();
		params.put(MySQLDataStoreFactory.DBTYPE.key, "mysql");
		params.put(MySQLDataStoreFactory.HOST.key, host);
		params.put(MySQLDataStoreFactory.PORT.key, port);
		params.put(MySQLDataStoreFactory.USER.key, user);
		params.put(MySQLDataStoreFactory.PASSWD.key, passwd);
		params.put(MySQLDataStoreFactory.DATABASE.key, database);
		MySQLDataStoreFactory dsFactory = new MySQLDataStoreFactory();
		DataStore ds = dsFactory.createDataStore(params);
		return ds;
	}

	public static DataStore getDataStoreFromArcSDE(String server, int port,
			String instance, String user, String passwd) throws Exception {
		HashMap<String, Serializable> params = new HashMap<String, Serializable>();
		params.put(ArcSDEDataStoreFactory.DBTYPE_PARAM.key, "arcsde");
		params.put(ArcSDEDataStoreFactory.SERVER_PARAM.key, server);
		params.put(ArcSDEDataStoreFactory.PORT_PARAM.key, port);
		params.put(ArcSDEDataStoreFactory.INSTANCE_PARAM.key, instance);
		params.put(ArcSDEDataStoreFactory.USER_PARAM.key, user);
		params.put(ArcSDEDataStoreFactory.PASSWORD_PARAM.key, passwd);
		ArcSDEDataStoreFactory dsFactory = new ArcSDEDataStoreFactory();
		DataStore ds = dsFactory.createDataStore(params);
		return ds;
	}

	public static DataStore getDataStoreFromOracle(String host, int port,
			String user, String passwd, String instance) throws Exception {
		HashMap<String, Serializable> params = new HashMap<String, Serializable>();
		params.put("dbtype", "oracle");
		params.put("host", host);
		params.put("port", port);
		params.put("user", user);
		params.put("passwd", passwd);
		params.put("instance", instance);
		OracleNGDataStoreFactory dsFactory = new OracleNGDataStoreFactory();
		DataStore ds = dsFactory.createDataStore(params);
		return ds;
	}
}

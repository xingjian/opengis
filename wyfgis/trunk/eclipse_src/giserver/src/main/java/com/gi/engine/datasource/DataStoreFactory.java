package com.gi.engine.datasource;

import java.io.File;
import java.io.Serializable;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.HashMap;

import org.geotools.arcsde.ArcSDEDataStoreFactory;
import org.geotools.data.DataStore;
import org.geotools.data.DataStoreFinder;
import org.geotools.data.mysql.MySQLDataStoreFactory;
import org.geotools.data.oracle.OracleDataStoreFactory;
import org.geotools.data.postgis.PostgisDataStoreFactory;
import org.geotools.data.shapefile.ShapefileDataStore;
import org.geotools.data.wfs.WFSDataStoreFactory;

public class DataStoreFactory {

	public static DataStore getDataStoreFromFile(File file, String charsetName)
			throws Exception {
		// For vector data files
		HashMap<String, Serializable> params = new HashMap<String, Serializable>();
		params.put("url", file.toURI().toURL());
		DataStore ds = DataStoreFinder.getDataStore(params);

		// Shapefile DBF's charset
		if (ds instanceof ShapefileDataStore && charsetName != null) {
			ShapefileDataStore shpDs = (ShapefileDataStore) ds;
			try {
				shpDs.setStringCharset(Charset.forName(charsetName));
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
			String user, String passwd, String database, String schema)
			throws Exception {
		HashMap<String, Serializable> params = new HashMap<String, Serializable>();
		params.put(PostgisDataStoreFactory.DBTYPE.key, "postgis");
		params.put(PostgisDataStoreFactory.HOST.key, host);
		params.put(PostgisDataStoreFactory.PORT.key, port);
		params.put(PostgisDataStoreFactory.USER.key, user);
		params.put(PostgisDataStoreFactory.PASSWD.key, passwd);
		params.put(PostgisDataStoreFactory.DATABASE.key, database);
		params.put(PostgisDataStoreFactory.SCHEMA.key, schema);
		PostgisDataStoreFactory dsFactory = new PostgisDataStoreFactory();
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
		OracleDataStoreFactory dsFactory = new OracleDataStoreFactory();
		DataStore ds = dsFactory.createDataStore(params);
		return ds;
	}
}

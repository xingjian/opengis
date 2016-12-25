package com.tongtu.gisservice.service.impl;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mchange.v2.c3p0.ComboPooledDataSource;
import com.promise.gistool.util.GISCoordinateTransform;
import com.promise.gistool.util.GeoToolsGeometry;
import com.tongtu.gisservice.service.Tocc2GISService;
import com.tongtu.gisservice.vo.BeijingQX;
import com.tongtu.gisservice.vo.TrafficSmallArea;
import com.vividsolutions.jts.geom.Point;

/**  
 * 功能描述:
 * @author:<a href="mailto:xingjian@yeah.net">邢健</a>  
 * @version: V1.0
 * 日期:2016年8月7日 下午1:55:54  
 */
@Service("tocc2GISService")
public class Tocc2GISServiceImpl implements Tocc2GISService {

	@Autowired
	private ComboPooledDataSource dataSource;
	private List<TrafficSmallArea> listTSA = new ArrayList<TrafficSmallArea>();
	private List<BeijingQX> listBJQX = new ArrayList<BeijingQX>();
	/**
	 * 初始化方法
	 */
	@PostConstruct
	protected void init(){
		initTrafficSmallAreaGeometry();
		initBJQXData();
	}
	
	private void initBJQXData(){
		String sql = "select admincode,name,st_astext(the_geom) from beijingqx";
		try {
			Statement statement = dataSource.getConnection().createStatement();
			ResultSet rs = statement.executeQuery(sql);
			while(rs.next()){
				String adminCode = rs.getString(1);
				String name = rs.getString(2);
				String wkt = rs.getString(3);
				BeijingQX bqx = new BeijingQX();
				bqx.setAdminCode(adminCode);
				bqx.setName(name);
				bqx.setGeom(GeoToolsGeometry.createGeometrtyByWKT(wkt));
				listBJQX.add(bqx);
			}
			rs.close();
			statement.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void initTrafficSmallAreaGeometry(){
		String sql = "select no,st_astext(the_geom) wkt from traffic_area_small_84";
		try {
			Statement statement = dataSource.getConnection().createStatement();
			ResultSet rs = statement.executeQuery(sql);
			while(rs.next()){
				String no = rs.getInt(1)+"";
				String wkt = rs.getString(2);
				TrafficSmallArea tsa = new TrafficSmallArea();
				tsa.setId(no);
				tsa.setGeom(GeoToolsGeometry.createGeometrtyByWKT(wkt));
				listTSA.add(tsa);
			}
			rs.close();
			statement.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	@Override
	public String getTrafficSmallAreaCodeByXY(double x, double y) {
		String result = "null";
		Point point = GeoToolsGeometry.createPoint(x, y);
		for(TrafficSmallArea tsaTemp:listTSA){
			if(tsaTemp.getGeom().contains(point)){
				result = tsaTemp.getId();
				break;
			}
		}
		return result;
	}

	@Override
	public String getBQNameByXY(double x, double y) {
		String result = "null";
		Point point = GeoToolsGeometry.createPoint(x, y);
		for(BeijingQX bqxTemp:listBJQX){
			if(bqxTemp.getGeom().contains(point)){
				result = bqxTemp.getName();
				break;
			}
		}
		return result;
	}

	@Override
	public double[] wgs84TO900913(double x, double y) {
		return GISCoordinateTransform.From84To900913(x, y);
	}

	@Override
	public double[] googleTOWGS84(double x, double y) {
		return GISCoordinateTransform.From900913To84(x, y);
	}

	@Override
	public String getWKTByXY(double x, double y) {
		Point point = GeoToolsGeometry.createPoint(x, y);
		return point.toText();
	}

	@Override
	public List<String> getGeometryStr(String strType, String tableName, String geomColumnName) {
		return null;
	}

	@Override
	public List<String> getGeometryStrByFilter(String strType, String tableName, String geomColumnName,
			String filterStr) {
		return null;
	}

}

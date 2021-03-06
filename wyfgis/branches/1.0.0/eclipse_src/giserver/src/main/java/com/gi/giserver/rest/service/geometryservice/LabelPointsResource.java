package com.gi.giserver.rest.service.geometryservice;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletContext;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;

import org.json.JSONArray;
import org.json.JSONObject;

import com.gi.giserver.core.config.ConfigManager;
import com.gi.giserver.core.service.geometryservice.LabelPoints;
import com.gi.giserver.core.service.tokenservice.TokenService;
import com.gi.giserver.core.util.error.ServiceError;
import com.gi.giserver.core.util.json.EsriJsonGeometryUtil;
import com.gi.giserver.core.util.json.EsriJsonUtil;
import com.gi.giserver.rest.html.ServiceHTML;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryCollection;
import com.vividsolutions.jts.geom.Point;

/**
 * @author Wu Yongfeng
 * 
 */
@Path("/GeometryService/labelPoints")
public class LabelPointsResource {

	@Context
	ServletContext context;

	@GET
	public String getResult(@QueryParam("token") String token, @QueryParam("f") @DefaultValue("html") String f,
			@QueryParam("polygons") String polygons, @QueryParam("sr") String sr) {
		return result(token, f, polygons, sr);
	}

	@POST
	public String postResult(@FormParam("token") String token, @FormParam("f") @DefaultValue("html") String f,
			@FormParam("polygons") String polygons, @FormParam("sr") String sr) {
		return result(token, f, polygons, sr);
	}

	private String result(String token, String f, String polygons, String sr) {
		String result = null;

		try {
			if (ConfigManager.getServerConfig().isInternalServiceNeedTokenVerify()) {
				if (!TokenService.verifyToken(token) && !("html".equals(f) && polygons == null)) {
					return TokenService.TOKEN_INVALID_TIP;
				}
			}

			// Handle polygons
			List<Geometry> geometriesList = EsriJsonGeometryUtil.json2Geometries(polygons);
			GeometryCollection[] geometryCollections = null;
			if (geometriesList != null) {
				geometryCollections = new GeometryCollection[geometriesList.size()];
				geometriesList.toArray(geometryCollections);
			}

			// Do labelPoints
			Point[] points = LabelPoints.labelPoints(geometryCollections);

			// Various out format
			if ("json".equals(f)) {
				result = this.generateJSONResult(points, polygons);
			} else if ("html".equals(f)) {
				result = this.generateHTMLResult(points, token, sr, polygons);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return result;
	}

	private String generateJSONResult(Point[] points, String polygons) {
		String result = null;

		try {
			JSONObject obj = new JSONObject();
			JSONObject objTemp = null;
			JSONArray arrayTemp = null;

			obj.put("about", "Powered by GIServer");
			if (polygons != null && points == null) {
				ArrayList<String> details = new ArrayList<String>();
				details.add("Result is NULL.");
				obj.put("error", EsriJsonUtil.generateJSONError(ServiceError.GEOMETRY_NO_RESULT_ERROR, details));
			} else {
				Point pt = null;
				arrayTemp = new JSONArray();
				for (int i = 0; i < points.length; i++) {
					pt = points[i];
					objTemp = new JSONObject();
					objTemp.put("x", pt.getX());
					objTemp.put("y", pt.getY());
					arrayTemp.put(objTemp);
				}
				obj.put("labelPoints", arrayTemp);
			}

			result = obj.toString();
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return result;
	}

	private String generateHTMLResult(Point[] points, String token, String sr, String polygons) {
		String result = "";

		try {
			String restBody = "";
			restBody += "<form><table style='border:1px solid #000000;'>";
			restBody += "<tr><td>Token:</td><td><input size='50' type='text' name='token' value='${TOKEN}' /></td></tr>";
			restBody += "<tr><td>Spatial Reference (WKID):</td><td><input type='text' name='sr' value='${SR}' /></td></tr>";
			restBody += "<tr valign='top'><td>Polygons:</td><td><textarea name='polygons' rows='10' cols='50'>${POLYGONS}</textarea></td></tr>";
			restBody += "<tr><td colspan='2'><input type='submit' value='Compute Label Points' /></td></tr>";
			restBody += "</table></form>";

			restBody = restBody.replace("${TOKEN}", token == null ? "" : token);
			restBody = restBody.replace("${SR}", sr == null ? "" : sr);
			restBody = restBody.replace("${POLYGONS}", polygons == null ? "" : polygons);

			if (!(polygons == null)) {
				if (points != null) {
					int resultCount = points.length;
					if (resultCount > 0) {
						restBody += "<div style='color:#aaffaa'>Results number : " + resultCount + "<br/></div><br/>";
						for (int i = 0; i < resultCount; i++) {
							restBody += "<div>";
							restBody += EsriJsonGeometryUtil.geometry2JSON(points[i]).toString();
							restBody += "<br/></div><tr><td><hr/></td></tr>";
						}
					} else {
						restBody += "<div style='color:#ffaaaa'>No results!<br/></div>";
					}
				}
			}

			String contextRoot = context.getContextPath();
			result += ServiceHTML.getHeader(contextRoot).replace("${TITLE}", "Label Points");
			result += ServiceHTML.getNav(contextRoot).replace(
					"${CATALOG}",
					" &gt; <a href='" + contextRoot
							+ "/rest/service/GeometryService'>Geometry Service</a> &gt; Label Points");
			result += ServiceHTML.getH2().replace("${TITLE}", "Label Points");
			result += ServiceHTML.getRestBody().replace("${RESTBODY}", restBody);
			result += ServiceHTML.getFooter();
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return result;
	}

}

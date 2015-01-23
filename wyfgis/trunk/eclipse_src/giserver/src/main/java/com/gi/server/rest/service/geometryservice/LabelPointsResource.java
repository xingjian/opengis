package com.gi.server.rest.service.geometryservice;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletContext;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import org.json.JSONArray;
import org.json.JSONObject;

import com.gi.engine.arcgis.EsriJsonUtil;
import com.gi.engine.util.VersionUtil;
import com.gi.server.core.config.ConfigManager;
import com.gi.server.core.service.tokenservice.TokenService;
import com.gi.server.rest.html.ServiceHTML;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.Point;


/**
 * @author wuyf
 *
 */
@Path("/GeometryService/labelPoints")
@Produces( { MediaType.TEXT_HTML, MediaType.APPLICATION_JSON })
public class LabelPointsResource {

	@Context
	ServletContext context;

	@GET
	public String getResult(@QueryParam("token") String token,
			@QueryParam("f") @DefaultValue("html") String f,
			@QueryParam("polygons") String polygons, @QueryParam("sr") String sr) {
		return result(token, f, polygons, sr);
	}

	@POST
	public String postResult(@FormParam("token") String token,
			@FormParam("f") @DefaultValue("html") String f,
			@FormParam("polygons") String polygons, @FormParam("sr") String sr) {
		return result(token, f, polygons, sr);
	}

	private String result(String token, String f, String polygons, String sr) {
		String result = null;

		try {
			if (ConfigManager.getServerConfig()
					.isInternalServiceNeedTokenVerify()) {
				if (!TokenService.verifyToken(token)
						&& !("html".equals(f) && polygons == null)) {
					return TokenService.TOKEN_INVALID_TIP;
				}
			}
			Point[] points = null;
			if (polygons != null && !"".equals(polygons)) {
				// Handle polygons
				List<Geometry> geometriesList = EsriJsonUtil
						.json2Geometries(polygons);

				// Do labelPoints
				int count = geometriesList.size();
				points = new Point[count];
				for (int i = 0; i < count; i++) {
					Geometry geo = geometriesList.get(i);
					points[i] = geo.getInteriorPoint();
				}
			}

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

			obj.put("GIServerVersion", VersionUtil.getCurrentversion());
			if (polygons != null && points == null) {
				ArrayList<String> details = new ArrayList<String>();
				details.add("Result is NULL.");
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

	private String generateHTMLResult(Point[] points, String token, String sr,
			String polygons) {
		String result = "";

		try {
			StringBuilder sb = new StringBuilder();
			sb.append("<form><table class='formTable'>");
			sb
					.append("<tr><td>Token:</td><td><input size='50' type='text' name='token' value='${TOKEN}' /></td></tr>");
			sb
					.append("<tr><td>Spatial Reference (WKID):</td><td><input type='text' name='sr' value='${SR}' /></td></tr>");
			sb
					.append("<tr valign='top'><td>Polygons:</td><td><textarea name='polygons' rows='10' cols='50'>${POLYGONS}</textarea></td></tr>");
			sb
					.append("<tr><td colspan='2'><input type='submit' value='Compute Label Points' /></td></tr>");
			sb.append("</table></form>");
			String restBody = sb.toString();

			restBody = restBody.replace("${TOKEN}", token == null ? "" : token);
			restBody = restBody.replace("${SR}", sr == null ? "" : sr);
			restBody = restBody.replace("${POLYGONS}", polygons == null ? ""
					: polygons);

			if (!(polygons == null)) {
				if (points != null) {
					StringBuilder sbResult = new StringBuilder();
					int resultCount = points.length;
					if (resultCount > 0) {
						sbResult
								.append("<div style='color:#aaffaa'>Results number : "
										+ resultCount + "<br/></div><br/>");
						for (int i = 0; i < resultCount; i++) {
							sbResult.append("<div>");
							sbResult.append(EsriJsonUtil.geometry2JSON(
									points[i]).toString());
							sbResult
									.append("<br/></div><tr><td><hr/></td></tr>");
						}
					} else {
						sbResult
								.append("<div style='color:#ffaaaa'>No results!<br/></div>");
					}
					restBody += sbResult.toString();
				}
			}

			String contextRoot = context.getContextPath();

			ServiceHTML html = new ServiceHTML();
			html.setContextRoot(contextRoot);
			html.setTitle("Label Points");
			html
					.setCatalog(" &gt; <a href='"
							+ contextRoot
							+ "/rest/service/GeometryService'>Geometry Service</a> &gt; Label Points");
			html.setHeader("Label Points");
			html.setRestBody(restBody);
			result = html.toString();
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return result;
	}

}

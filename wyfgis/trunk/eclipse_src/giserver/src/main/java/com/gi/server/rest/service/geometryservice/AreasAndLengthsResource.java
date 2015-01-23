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


/**
 * @author wuyf
 *
 */
@Path("/GeometryService/areasAndLengths")
@Produces( { MediaType.TEXT_HTML, MediaType.APPLICATION_JSON })
public class AreasAndLengthsResource {

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

			double[] areas = null;
			double[] lengths = null;
			if (polygons != null && !"".equals(polygons)) {
				// Handle polygons
				List<Geometry> geometriesList = EsriJsonUtil
						.json2Geometries(polygons);

				// Do areasAndLengths
				int count = geometriesList.size();
				areas = new double[count];
				lengths = new double[count];
				for (int i = 0; i < count; i++) {
					Geometry geo = geometriesList.get(i);
					areas[i] = geo.getArea();
					lengths[i] = geo.getLength();
				}
			}

			// Various out format
			if ("json".equals(f)) {
				result = this.generateJSONResult(areas, lengths, polygons);
			} else if ("html".equals(f)) {
				result = this.generateHTMLResult(areas, lengths, token, sr,
						polygons);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return result;
	}

	private String generateJSONResult(double[] areas, double[] lengths,
			String polygons) {
		String result = null;

		try {
			JSONObject obj = new JSONObject();
			JSONArray arrayTemp = null;

			obj.put("GIServerVersion", VersionUtil.getCurrentversion());
			if (polygons != null && (areas == null || lengths == null)) {
				ArrayList<String> details = new ArrayList<String>();
				details.add("Result is NULL.");
			} else {
				arrayTemp = new JSONArray();
				for (int i = 0; i < areas.length; i++) {
					arrayTemp.put(areas[i]);
				}
				obj.put("areas", arrayTemp);

				arrayTemp = new JSONArray();
				for (int i = 0; i < lengths.length; i++) {
					arrayTemp.put(lengths[i]);
				}
				obj.put("lengths", arrayTemp);
			}

			result = obj.toString();
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return result;
	}

	private String generateHTMLResult(double[] areas, double[] lengths,
			String token, String sr, String polygons) {
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
					.append("<tr><td colspan='2'><input type='submit' value='Compute Areas And Lengths' /></td></tr>");
			sb.append("</table></form>");
			String restBody = sb.toString();

			restBody = restBody.replace("${TOKEN}", token == null ? "" : token);
			restBody = restBody.replace("${SR}", sr == null ? "" : sr);
			restBody = restBody.replace("${POLYGONS}", polygons == null ? ""
					: polygons);

			if (!(polygons == null)) {
				if (areas != null && lengths != null) {
					StringBuilder sbResult = new StringBuilder();
					int areaCount = areas.length;
					int lengthCount = lengths.length;
					int count = Math.max(areaCount, lengthCount);
					if (count > 0) {
						sbResult
								.append("<div style='color:#aaffaa'>Results number : "
										+ count + "<br/></div><br/>");
						for (int i = 0; i < count; i++) {
							sbResult.append("<div>" + i + ":");
							if (i < areaCount) {
								sbResult.append("<br/>" + areas[i]);
							}
							if (i < lengthCount) {
								sbResult.append("<br/>" + lengths[i]);
							}
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
			html.setTitle("Areas And Lengths");
			html
					.setCatalog(" &gt; <a href='"
							+ contextRoot
							+ "/rest/service/GeometryService'>Geometry Service</a> &gt; Areas And Lengths");
			html.setHeader("Areas And Lengths");
			html.setRestBody(restBody);
			result = html.toString();
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return result;
	}

}

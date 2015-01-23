package com.gi.server.rest.service.geometryservice;

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

import org.json.JSONObject;

import com.gi.engine.arcgis.EsriJsonUtil;
import com.gi.engine.util.VersionUtil;
import com.gi.server.core.config.ConfigManager;
import com.gi.server.core.service.tokenservice.TokenService;
import com.gi.server.rest.html.ServiceHTML;
import com.vividsolutions.jts.geom.Geometry;

/**
 * @author Wu Yongfeng
 * 
 */
@Path("/GeometryService/distance")
@Produces( { MediaType.TEXT_HTML, MediaType.APPLICATION_JSON })
public class DistanceResource {

	@Context
	ServletContext context;

	@GET
	public String getResult(@QueryParam("token") String token,
			@QueryParam("f") @DefaultValue("html") String f,
			@QueryParam("geometry1") String geometry1,
			@QueryParam("geometry2") String geometry2,
			@QueryParam("sr") String sr) {
		return result(token, f, geometry1, geometry2, sr);
	}

	@POST
	public String postResult(@FormParam("token") String token,
			@FormParam("f") @DefaultValue("html") String f,
			@FormParam("geometry1") String geometry1,
			@FormParam("geometry2") String geometry2, @FormParam("sr") String sr) {
		return result(token, f, geometry1, geometry2, sr);
	}

	private String result(String token, String f, String geometry1,
			String geometry2, String sr) {
		String result = null;

		try {
			if (ConfigManager.getServerConfig()
					.isInternalServiceNeedTokenVerify()) {
				if (!TokenService.verifyToken(token)
						&& !("html".equals(f) && geometry1 == null && geometry2 == null)) {
					return TokenService.TOKEN_INVALID_TIP;
				}
			}

			double distance = 0;
			if (geometry1 != null && !geometry1.equals("") && geometry2 != null
					&& !geometry2.equals("")) {
				// Handle geometry
				Geometry geo1 = EsriJsonUtil.json2Geometry(geometry1);
				Geometry geo2 = EsriJsonUtil.json2Geometry(geometry2);

				// Do lengths
				distance = geo1.distance(geo2);
			}

			// Various out format
			if ("json".equals(f)) {
				result = this.generateJSONResult(distance);
			} else if ("html".equals(f)) {
				result = this.generateHTMLResult(distance, token, sr,
						geometry1, geometry2);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return result;
	}

	private String generateJSONResult(double distance) {
		String result = null;

		try {
			JSONObject obj = new JSONObject();

			obj.put("GIServerVersion", VersionUtil.getCurrentversion());
			obj.put("distance", distance);

			result = obj.toString();
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return result;
	}

	private String generateHTMLResult(double distance, String token, String sr,
			String geometry1, String geometry2) {
		String result = "";

		try {
			StringBuilder sb = new StringBuilder();
			sb.append("<form><table class='formTable'>");
			sb
					.append("<tr><td>Token:</td><td><input size='50' type='text' name='token' value='${TOKEN}' /></td></tr>");
			sb
					.append("<tr><td>Spatial Reference (WKID):</td><td><input type='text' name='sr' value='${SR}' /></td></tr>");
			sb
					.append("<tr valign='top'><td>From:</td><td><textarea name='geometry1' rows='10' cols='50'>${GEOMETRY1}</textarea></td></tr>");
			sb
					.append("<tr valign='top'><td>To:</td><td><textarea name='geometry2' rows='10' cols='50'>${GEOMETRY2}</textarea></td></tr>");
			sb
					.append("<tr><td colspan='2'><input type='submit' value='Distance' /></td></tr>");
			sb.append("</table></form>");
			String restBody = sb.toString();

			restBody = restBody.replace("${TOKEN}", token == null ? "" : token);
			restBody = restBody.replace("${SR}", sr == null ? "" : sr);
			restBody = restBody.replace("${GEOMETRY1}", geometry1 == null ? ""
					: geometry1);
			restBody = restBody.replace("${GEOMETRY2}", geometry2 == null ? ""
					: geometry2);

			if (!(geometry1 == null || geometry2 == null)) {
				StringBuilder sbResult = new StringBuilder();
				if (distance != Double.NaN) {
					sbResult.append("<div style='color:#aaffaa'>Distance : "
							+ distance + "<br/></div><br/>");
				} else {
					sbResult
							.append("<div style='color:#ffaaaa'>Caculate error!<br/></div>");
				}
				restBody += sbResult.toString();
			}

			String contextRoot = context.getContextPath();

			ServiceHTML html = new ServiceHTML();
			html.setContextRoot(contextRoot);
			html.setTitle("Distance");
			html
					.setCatalog(" &gt; <a href='"
							+ contextRoot
							+ "/rest/service/GeometryService'>Geometry Service</a> &gt; Distance");
			html.setHeader("Distance");
			html.setRestBody(restBody);
			result = html.toString();
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return result;
	}

}

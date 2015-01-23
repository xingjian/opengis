package com.gi.server.rest.service.geometryservice;

import java.util.ArrayList;

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
import com.gi.engine.geometry.GeometryToolkit;
import com.gi.engine.util.VersionUtil;
import com.gi.server.core.config.ConfigManager;
import com.gi.server.core.service.tokenservice.TokenService;
import com.gi.server.rest.html.ServiceHTML;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.LineString;

/**
 * @author wuyf
 * 
 */
@Path("/GeometryService/reshape")
public class ReshapeResource {

	@Context
	ServletContext context;

	@GET
	@Produces( { MediaType.TEXT_HTML, MediaType.APPLICATION_JSON })
	public String getResult(@QueryParam("token") String token,
			@QueryParam("f") @DefaultValue("html") String f,
			@QueryParam("target") String target,
			@QueryParam("reshaper") String reshaper) {
		return result(token, f, target, reshaper);
	}

	@POST
	@Produces( { MediaType.TEXT_HTML, MediaType.APPLICATION_JSON })
	public String postResult(@FormParam("token") String token,
			@FormParam("f") @DefaultValue("html") String f,
			@FormParam("target") String target,
			@FormParam("reshaper") String reshaper) {
		return result(token, f, target, reshaper);
	}

	private String result(String token, String f, String target, String reshaper) {
		String result = null;

		try {
			if (ConfigManager.getServerConfig()
					.isInternalServiceNeedTokenVerify()) {
				if (!TokenService.verifyToken(token)
						&& !("html".equals(f) && (target == null || reshaper == null))) {
					return TokenService.TOKEN_INVALID_TIP;
				}
			}

			Geometry resultGeometry = null;
			if (target != null && !"".equals(target) && reshaper != null
					&& !"".equals(reshaper)) {
				// Handle geometries
				Geometry targetGeometry = EsriJsonUtil
						.json2Geometry(target);
				Geometry reshaperGeometry = EsriJsonUtil
						.json2Geometry(reshaper);
				if (reshaperGeometry.getGeometryType().equals(
						LineString.class.getSimpleName())) {
					LineString reshaperLine = (LineString) reshaperGeometry;

					resultGeometry = GeometryToolkit.reshape(
							targetGeometry, reshaperLine);
				}
			}

			// Various out format
			if ("json".equals(f)) {
				result = this.generateJSONResult(resultGeometry);
			} else if ("html".equals(f)) {
				result = this.generateHTMLResult(resultGeometry, token,
						target, reshaper);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return result;
	}

	private String generateJSONResult(Geometry resultGeometry) {
		String result = null;

		try {
			JSONObject obj = new JSONObject();

			obj.put("GIServerVersion", VersionUtil.getCurrentversion());
			if (resultGeometry == null) {
				ArrayList<String> details = new ArrayList<String>();
				details.add("Result is NULL.");
			} else {
				obj.put("geometryType", EsriJsonUtil.geometryType2String(resultGeometry.getClass()));
				obj.put("geometry", EsriJsonUtil.geometry2JSON(resultGeometry));
			}

			result = obj.toString();
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return result;
	}

	private String generateHTMLResult(Geometry resultGeometry,
			String token, String target, String reshaper) {
		String result = "";

		try {
			StringBuilder sb = new StringBuilder();
			sb.append("<form><table class='formTable'>");
			sb
					.append("<tr><td>Token:</td><td><input size='50' type='text' name='token' value='${TOKEN}' /></td></tr>");
			sb
					.append("<tr valign='top'><td>Target Geometry:</td><td><textarea name='target' rows='10' cols='50'>${TARGET}</textarea></td></tr>");
			sb
					.append("<tr valign='top'><td>Reshaper:</td><td><textarea name='reshaper' rows='10' cols='50'>${RESHAPER}</textarea></td></tr>");

			sb
					.append("<tr><td colspan='2'><input type='submit' value='Reshape' /></td></tr>");
			sb.append("</table></form>");
			String restBody = sb.toString();

			restBody = restBody.replace("${TOKEN}", token == null ? "" : token);
			restBody = restBody.replace("${TARGET}", target == null ? ""
					: target);
			restBody = restBody.replace("${RESHAPER}",
					reshaper == null ? "" : reshaper);

			if (!(target == null || reshaper == null)) {
				StringBuilder sbResult = new StringBuilder();
				if (resultGeometry != null && !resultGeometry.isEmpty()) {
					sbResult
							.append("<div style='color:#aaffaa'>Result:<br/></div><br/>");
						sbResult.append("<div>");
						sbResult.append(EsriJsonUtil.geometry2JSON(resultGeometry)
								.toString());
						sbResult.append("<br/></div><tr><td><hr/></td></tr>");
				} else {
					sbResult
							.append("<div style='color:#ffaaaa'>No results!<br/></div>");
				}
				restBody += sbResult.toString();
			}

			String contextRoot = context.getContextPath();

			ServiceHTML html = new ServiceHTML();
			html.setContextRoot(contextRoot);
			html.setTitle("Reshape");
			html
					.setCatalog(" &gt; <a href='"
							+ contextRoot
							+ "/rest/service/GeometryService'>Geometry Service</a> &gt; Reshape");
			html.setHeader("Reshape");
			html.setRestBody(restBody);
			result = html.toString();
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return result;
	}

}

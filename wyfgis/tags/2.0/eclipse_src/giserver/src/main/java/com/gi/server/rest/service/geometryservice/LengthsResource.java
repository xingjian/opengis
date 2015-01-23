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
@Path("/GeometryService/lengths")
public class LengthsResource {

	@Context
	ServletContext context;

	@GET
	@Produces( { MediaType.TEXT_HTML, MediaType.APPLICATION_JSON })
	public String getResult(@QueryParam("token") String token,
			@QueryParam("f") @DefaultValue("html") String f,
			@QueryParam("polylines") String polylines,
			@QueryParam("sr") String sr) {
		return result(token, f, polylines, sr);
	}

	@POST
	@Produces( { MediaType.TEXT_HTML, MediaType.APPLICATION_JSON })
	public String postResult(@FormParam("token") String token,
			@FormParam("f") @DefaultValue("html") String f,
			@FormParam("polylines") String polylines, @FormParam("sr") String sr) {
		return result(token, f, polylines, sr);
	}

	private String result(String token, String f, String polylines, String sr) {
		String result = null;

		try {
			if (ConfigManager.getServerConfig()
					.isInternalServiceNeedTokenVerify()) {
				if (!TokenService.verifyToken(token)
						&& !("html".equals(f) && polylines == null)) {
					return TokenService.TOKEN_INVALID_TIP;
				}
			}

			double[] lengths = null;
			if (polylines != null && !"".equals(polylines)) {
				// Handle polylines
				List<Geometry> geometriesList = EsriJsonUtil
						.json2Geometries(polylines);

				// Do lengths
				int count = geometriesList.size();
				lengths = new double[count];
				for (int i = 0; i < count; i++) {
					Geometry geo = geometriesList.get(i);
					lengths[i] = geo.getLength();
				}
			}

			// Various out format
			if ("json".equals(f)) {
				result = this.generateJSONResult(lengths, polylines);
			} else if ("html".equals(f)) {
				result = this.generateHTMLResult(lengths, token, sr, polylines);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return result;
	}

	private String generateJSONResult(double[] lengths, String polylines) {
		String result = null;

		try {
			JSONObject obj = new JSONObject();
			JSONArray arrayTemp = null;

			obj.put("GIServerVersion", VersionUtil.getCurrentversion());
			if (polylines != null && lengths == null) {
				ArrayList<String> details = new ArrayList<String>();
				details.add("Result is NULL.");
			} else {
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

	private String generateHTMLResult(double[] lengths, String token,
			String sr, String polylines) {
		String result = "";

		try {
			StringBuilder sb = new StringBuilder();
			sb.append("<form><table class='formTable'>");
			sb
					.append("<tr><td>Token:</td><td><input size='50' type='text' name='token' value='${TOKEN}' /></td></tr>");
			sb
					.append("<tr><td>Spatial Reference (WKID):</td><td><input type='text' name='sr' value='${SR}' /></td></tr>");
			sb
					.append("<tr valign='top'><td>Polylines:</td><td><textarea name='polylines' rows='10' cols='50'>${POLYLINES}</textarea></td></tr>");
			sb
					.append("<tr><td colspan='2'><input type='submit' value='Compute Lengths' /></td></tr>");
			sb.append("</table></form>");
			String restBody = sb.toString();

			restBody = restBody.replace("${TOKEN}", token == null ? "" : token);
			restBody = restBody.replace("${SR}", sr == null ? "" : sr);
			restBody = restBody.replace("${POLYLINES}", polylines == null ? ""
					: polylines);

			if (!(polylines == null)) {
				if (lengths != null) {
					StringBuilder sbResult = new StringBuilder();
					int lengthCount = lengths.length;
					if (lengthCount > 0) {
						sbResult
								.append("<div style='color:#aaffaa'>Results number : "
										+ lengthCount + "<br/></div><br/>");
						for (int i = 0; i < lengthCount; i++) {
							sbResult.append("<div>" + i + ":");
							sbResult.append("<br/>" + lengths[i]);
							sbResult
									.append("<br/></div><tr><td><hr/></td></tr>");
						}
					} else {
						restBody += "<div style='color:#ffaaaa'>No results!<br/></div>";
					}
					restBody += sbResult.toString();
				}
			}

			String contextRoot = context.getContextPath();

			ServiceHTML html = new ServiceHTML();
			html.setContextRoot(contextRoot);
			html.setTitle("Lengths");
			html
					.setCatalog(" &gt; <a href='"
							+ contextRoot
							+ "/rest/service/GeometryService'>Geometry Service</a> &gt; Lengths");
			html.setHeader("Lengths");
			html.setRestBody(restBody);
			result = html.toString();
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return result;
	}

}

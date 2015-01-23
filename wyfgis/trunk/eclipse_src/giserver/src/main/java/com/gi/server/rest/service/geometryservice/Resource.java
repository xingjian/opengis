package com.gi.server.rest.service.geometryservice;

import javax.servlet.ServletContext;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import org.json.JSONException;
import org.json.JSONObject;

import com.gi.engine.util.VersionUtil;
import com.gi.server.core.config.ConfigManager;
import com.gi.server.rest.html.ServiceHTML;

/**
 * @author wuyf
 * 
 */
@Path("/GeometryService")
@Produces( { MediaType.TEXT_HTML, MediaType.APPLICATION_JSON })
public class Resource {

	@Context
	ServletContext context;

	@GET
	public String getResult(@QueryParam("f") @DefaultValue("html") String f) {
		return result(f);
	}

	private String result(String f) {
		String result = null;

		try {
			boolean needToken = ConfigManager.getServerConfig()
					.isInternalServiceNeedTokenVerify();

			// Various out format
			if ("json".equals(f)) {
				result = this.generateJSONResult(needToken);
			} else if ("html".equals(f)) {
				result = this.generateHTMLResult(needToken);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return result;
	}

	private String generateJSONResult(boolean needToken) {
		String result = null;

		try {
			JSONObject obj = new JSONObject();

			obj.put("GIServerVersion", VersionUtil.getCurrentversion());
			obj.put("needToken", needToken);

			result = obj.toString();
		} catch (JSONException ex) {
			ex.printStackTrace();
		}

		return result;
	}

	private String generateHTMLResult(boolean needToken) {
		String result = "";

		String contextRoot = context.getContextPath();

		StringBuilder sb = new StringBuilder();
		sb.append("<b>Supported Operations:</b>");
		if (needToken) {
			sb.append("<img src='" + contextRoot + "/image/lock.png'>");
		}
		sb.append("&nbsp;&nbsp;<a href='" + contextRoot
				+ "/rest/service/GeometryService/project'>Project</a>");
		sb.append("&nbsp;&nbsp;<a href='" + contextRoot
				+ "/rest/service/GeometryService/simplify'>Simplify</a>");
		sb.append("&nbsp;&nbsp;<a href='" + contextRoot
				+ "/rest/service/GeometryService/buffer'>Buffer</a>");
		sb
				.append("&nbsp;&nbsp;<a href='"
						+ contextRoot
						+ "/rest/service/GeometryService/areasAndLengths'>Areas And Lengths</a>");
		sb.append("&nbsp;&nbsp;<a href='" + contextRoot
				+ "/rest/service/GeometryService/lengths'>Lengths</a>");
		sb.append("&nbsp;&nbsp;<a href='" + contextRoot
				+ "/rest/service/GeometryService/relation'>Relation</a>");
		sb
				.append("&nbsp;&nbsp;<a href='"
						+ contextRoot
						+ "/rest/service/GeometryService/labelPoints'>Label Points</a>");
		sb.append("&nbsp;&nbsp;<a href='" + contextRoot
				+ "/rest/service/GeometryService/distance'>Distance</a>");
		sb.append("&nbsp;&nbsp;<a href='" + contextRoot
				+ "/rest/service/GeometryService/trimExtend'>Trim/Extend</a>");
		sb.append("&nbsp;&nbsp;<a href='" + contextRoot
				+ "/rest/service/GeometryService/convexHull'>Convex Hull</a>");
		sb.append("&nbsp;&nbsp;<a href='" + contextRoot
				+ "/rest/service/GeometryService/densify'>Densify</a>");
		sb.append("&nbsp;&nbsp;<a href='" + contextRoot
				+ "/rest/service/GeometryService/union'>Union</a>");
		sb.append("&nbsp;&nbsp;<a href='" + contextRoot
				+ "/rest/service/GeometryService/intersect'>Intersect</a>");
		sb.append("&nbsp;&nbsp;<a href='" + contextRoot
				+ "/rest/service/GeometryService/difference'>Difference</a>");
		sb.append("&nbsp;&nbsp;<a href='" + contextRoot
				+ "/rest/service/GeometryService/reshape'>Reshape</a>");
		sb.append("&nbsp;&nbsp;<a href='" + contextRoot
				+ "/rest/service/GeometryService/cut'>Cut</a>");
		sb.append("&nbsp;&nbsp;<a href='" + contextRoot
				+ "/rest/service/GeometryService/generalize'>Generalize</a>");
		sb.append("<br/><br/>");
		String restBody = sb.toString();

		ServiceHTML html = new ServiceHTML();
		html.setContextRoot(contextRoot);
		html.setTitle("Geometry Service");
		html.setCatalog(" &gt; Geometry Service");
		html.setHeader("Geometry Service");
		html.setRestBody(restBody);
		result = html.toString();

		return result;
	}

}

package com.gi.giserver.rest.service.geometryservice;

import java.util.ArrayList;
import java.util.Iterator;
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
import com.gi.giserver.core.service.geometryservice.Project;
import com.gi.giserver.core.service.tokenservice.TokenService;
import com.gi.giserver.core.util.error.ServiceError;
import com.gi.giserver.core.util.json.EsriJsonGeometryUtil;
import com.gi.giserver.core.util.json.EsriJsonUtil;
import com.gi.giserver.rest.html.ServiceHTML;
import com.vividsolutions.jts.geom.Geometry;

/**
 * @author Wu Yongfeng
 * 
 */
@Path("/GeometryService/project")
public class ProjectResource {

	@Context
	ServletContext context;

	@GET
	public String getResult(@QueryParam("token") String token, @QueryParam("f") @DefaultValue("html") String f,
			@QueryParam("geometries") String geometries, @QueryParam("inSR") String inSR,
			@QueryParam("outSR") String outSR) {
		return result(token, f, geometries, inSR, outSR);
	}

	@POST
	public String postResult(@FormParam("token") String token, @FormParam("f") @DefaultValue("html") String f,
			@FormParam("geometries") String geometries, @FormParam("inSR") String inSR, @FormParam("outSR") String outSR) {
		return result(token, f, geometries, inSR, outSR);
	}

	private String result(String token, String f, String geometries, String inSR, String outSR) {
		String result = null;

		try {
			if (ConfigManager.getServerConfig().isInternalServiceNeedTokenVerify()) {
				if (!TokenService.verifyToken(token) && !("html".equals(f) && geometries == null)) {
					return TokenService.TOKEN_INVALID_TIP;
				}
			}

			// Handle geometries
			List<Geometry> geometriesList = EsriJsonGeometryUtil.json2Geometries(geometries);

			// Do project
			List<Geometry> projectGeometries = Project.projects(geometriesList, inSR, outSR);

			// Various out format
			if ("json".equals(f)) {
				result = this.generateJSONResult(projectGeometries, geometries);
			} else if ("html".equals(f)) {
				result = this.generateHTMLResult(projectGeometries, token, inSR, outSR, geometries);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return result;
	}

	private String generateJSONResult(List<Geometry> geometriesList, String geometries) {
		String result = null;

		try {
			JSONObject obj = new JSONObject();
			JSONArray arrayTemp = null;

			obj.put("about", "Powered by GIServer");
			if (geometries != null && geometriesList == null) {
				ArrayList<String> details = new ArrayList<String>();
				details.add("Result is NULL.");
				obj.put("error", EsriJsonUtil.generateJSONError(ServiceError.GEOMETRY_NO_RESULT_ERROR, details));
			} else {
				Geometry geo = null;
				arrayTemp = new JSONArray();
				Iterator<Geometry> it = geometriesList.iterator();
				while (it.hasNext()) {
					geo = it.next();
					arrayTemp.put(EsriJsonGeometryUtil.geometry2JSON(geo));
				}
				obj.put("geometries", arrayTemp);
			}

			result = obj.toString();
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return result;
	}

	private String generateHTMLResult(List<Geometry> geometryList, String token, String inSR, String outSR,
			String geometries) {
		String result = "";

		try {
			String restBody = "";
			restBody += "<form><table style='border:1px solid #000000;'>";
			restBody += "<tr><td>Token:</td><td><input size='50' type='text' name='token' value='${TOKEN}' /></td></tr>";
			restBody += "<tr><td>Input Spatial Reference (WKID):</td><td><input type='text' name='inSR' value='${IN_SR}' /></td></tr>";
			restBody += "<tr><td>Output Spatial Reference (WKID):</td><td><input type='text' name='outSR' value='${OUT_SR}' /></td></tr>";
			restBody += "<tr valign='top'><td>Geometries:</td><td><textarea name='geometries' rows='10' cols='50'>${GEOMETRIES}</textarea></td></tr>";
			restBody += "<tr><td colspan='2'><input type='submit' value='Project' /></td></tr>";
			restBody += "</table></form>";

			restBody = restBody.replace("${TOKEN}", token == null ? "" : token);
			restBody = restBody.replace("${IN_SR}", inSR == null ? "" : inSR);
			restBody = restBody.replace("${OUT_SR}", outSR == null ? "" : outSR);
			restBody = restBody.replace("${GEOMETRIES}", geometries == null ? "" : geometries);

			if (!(geometries == null)) {
				int resultCount = geometryList.size();
				if (resultCount > 0) {
					restBody += "<div style='color:#aaffaa'>Results number : " + resultCount + "<br/></div><br/>";
					Iterator<Geometry> it = geometryList.iterator();
					Geometry geo = null;
					while (it.hasNext()) {
						geo = it.next();
						restBody += "<div>";
						restBody += EsriJsonGeometryUtil.geometry2JSON(geo).toString();
						restBody += "<br/></div><tr><td><hr/></td></tr>";
					}
				} else {
					restBody += "<div style='color:#ffaaaa'>No results!<br/></div>";
				}
			}

			String contextRoot = context.getContextPath();
			result += ServiceHTML.getHeader(contextRoot).replace("${TITLE}", "Project");
			result += ServiceHTML.getNav(contextRoot).replace(
					"${CATALOG}",
					" &gt; <a href='" + contextRoot
							+ "/rest/service/GeometryService'>Geometry Service</a> &gt; Project");
			result += ServiceHTML.getH2().replace("${TITLE}", "Project Geometries");
			result += ServiceHTML.getRestBody().replace("${RESTBODY}", restBody);
			result += ServiceHTML.getFooter();
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return result;
	}

}

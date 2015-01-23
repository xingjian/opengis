package com.gi.server.rest.service.geometryservice;

import java.util.ArrayList;
import java.util.Iterator;
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
import com.gi.engine.geometry.GeometryToolkit;
import com.gi.engine.util.VersionUtil;
import com.gi.server.core.config.ConfigManager;
import com.gi.server.core.service.tokenservice.TokenService;
import com.gi.server.rest.html.ServiceHTML;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.MultiLineString;

/**
 * @author wuyf
 * 
 */
@Path("/GeometryService/trimExtend")
@Produces( { MediaType.TEXT_HTML, MediaType.APPLICATION_JSON })
public class TrimExtendResource {

	@Context
	ServletContext context;

	@GET
	public String getResult(@QueryParam("token") String token,
			@QueryParam("f") @DefaultValue("html") String f,
			@QueryParam("polylines") String polylines,
			@QueryParam("trimExtendTo") String trimExtendTo) {
		return result(token, f, polylines, trimExtendTo);
	}

	@POST
	public String postResult(@FormParam("token") String token,
			@FormParam("f") @DefaultValue("html") String f,
			@FormParam("polylines") String polylines,
			@FormParam("trimExtendTo") String trimExtendTo) {
		return result(token, f, polylines, trimExtendTo);
	}

	private String result(String token, String f, String polylines,
			String trimExtendTo) {
		String result = null;

		try {
			if (ConfigManager.getServerConfig()
					.isInternalServiceNeedTokenVerify()) {
				if (!TokenService.verifyToken(token)
						&& !("html".equals(f) && (polylines == null || trimExtendTo == null))) {
					return TokenService.TOKEN_INVALID_TIP;
				}
			}

			List<LineString> trimExtendResults = null;
			if (polylines != null && !"".equals(polylines)
					&& trimExtendTo != null && !"".equals(trimExtendTo)) {
				// Handle geometries
				List<Geometry> polylineList = EsriJsonUtil
						.json2Geometries(polylines);
				Geometry trimExtendToPolyline = EsriJsonUtil
						.json2Geometry(trimExtendTo);
				List<LineString> trimExtendToLineList = new ArrayList<LineString>();
				if(trimExtendToPolyline.getGeometryType().equals(LineString.class.getSimpleName())){
					trimExtendToLineList.add((LineString)trimExtendToPolyline);
				}else if(trimExtendToPolyline.getGeometryType().equals(MultiLineString.class.getSimpleName())){
					MultiLineString ml = (MultiLineString)trimExtendToPolyline;
					for(int i=0,count=ml.getNumGeometries();i<count; i++){
						LineString l = (LineString)ml.getGeometryN(i);
						trimExtendToLineList.add(l);
					}					
				}

				trimExtendResults = new ArrayList<LineString>();
				for (Iterator<Geometry> itr = polylineList.iterator(); itr
						.hasNext();) {
					Geometry geo = itr.next();

					if (geo != null
							&& geo.getGeometryType().equals(
									LineString.class.getSimpleName()) && trimExtendToLineList.size()>0) {						
						LineString source = (LineString) geo;
						ArrayList<LineString> ls = GeometryToolkit.trimExtend(
								source, trimExtendToLineList);
						for (Iterator<LineString> itrResult = ls.iterator(); itrResult
								.hasNext();) {
							LineString l = itrResult.next();
							trimExtendResults.add(l);
						}
					}
				}
			}

			// Various out format
			if ("json".equals(f)) {
				result = this.generateJSONResult(trimExtendResults);
			} else if ("html".equals(f)) {
				result = this.generateHTMLResult(trimExtendResults, token,
						polylines, trimExtendTo);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return result;
	}

	private String generateJSONResult(List<LineString> trimExtendResults) {
		String result = null;

		try {
			JSONObject obj = new JSONObject();
			JSONArray arrayTemp = null;

			obj.put("GIServerVersion", VersionUtil.getCurrentversion());
			if (trimExtendResults == null) {
				ArrayList<String> details = new ArrayList<String>();
				details.add("Result is NULL.");
			} else {
				arrayTemp = new JSONArray();				
				for (Iterator<LineString> itr = trimExtendResults.iterator();itr.hasNext();) {
					LineString line = itr.next();
					arrayTemp.put(EsriJsonUtil.geometry2JSON(line));
				}
				obj.put("geometries", arrayTemp);
			}

			result = obj.toString();
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return result;
	}

	private String generateHTMLResult(List<LineString> trimExtendResults, String token,
			String polylines, String trimExtendTo) {
		String result = "";

		try {
			StringBuilder sb = new StringBuilder();
			sb.append("<form><table class='formTable'>");
			sb
					.append("<tr><td>Token:</td><td><input size='50' type='text' name='token' value='${TOKEN}' /></td></tr>");
			sb
					.append("<tr valign='top'><td>Input Polylines:</td><td><textarea name='polylines' rows='10' cols='50'>${POLYLINES}</textarea></td></tr>");
			sb
					.append("<tr valign='top'><td>Trim/Extend Line:</td><td><textarea name='trimExtendTo' rows='10' cols='50'>${TRIM_EXTEND_TO}</textarea></td></tr>");
			
			sb
					.append("<tr><td colspan='2'><input type='submit' value='Trim/Extend' /></td></tr>");
			sb.append("</table></form>");
			String restBody = sb.toString();

			restBody = restBody.replace("${TOKEN}", token == null ? "" : token);
			restBody = restBody.replace("${POLYLINES}",
					polylines == null ? "" : polylines);
			restBody = restBody.replace("${TRIM_EXTEND_TO}",
					trimExtendTo == null ? "" : trimExtendTo);

			if (!(polylines == null || trimExtendTo == null)) {
				StringBuilder sbResult = new StringBuilder();
				int resultCount = trimExtendResults.size();
				if (resultCount > 0) {
					sbResult
							.append("<div style='color:#aaffaa'>Results number : "
									+ resultCount + "<br/></div><br/>");
					for (Iterator<LineString> itr = trimExtendResults
							.iterator(); itr.hasNext();) {
						LineString line = itr.next();
						sbResult.append("<div>");
						sbResult.append(EsriJsonUtil.geometry2JSON(line)
								.toString());
						sbResult.append("<br/></div><tr><td><hr/></td></tr>");
					}
				} else {
					sbResult
							.append("<div style='color:#ffaaaa'>No results!<br/></div>");
				}
				restBody += sbResult.toString();
			}

			String contextRoot = context.getContextPath();

			ServiceHTML html = new ServiceHTML();
			html.setContextRoot(contextRoot);
			html.setTitle("Trim/Extend");
			html
					.setCatalog(" &gt; <a href='"
							+ contextRoot
							+ "/rest/service/GeometryService'>Geometry Service</a> &gt; TrimExtend");
			html.setHeader("Trim/Extend");
			html.setRestBody(restBody);
			result = html.toString();
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return result;
	}

}

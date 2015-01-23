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
import com.vividsolutions.jts.geom.GeometryCollection;
import com.vividsolutions.jts.geom.GeometryFactory;


/**
 * @author wuyf
 *
 */
@Path("/GeometryService/buffer")
public class BufferResource {

	@Context
	ServletContext context;

	@GET
	@Produces( { MediaType.TEXT_HTML, MediaType.APPLICATION_JSON })
	public String getResult(
			@QueryParam("token") String token,
			@QueryParam("f") @DefaultValue("html") String f,
			@QueryParam("geometries") String geometries,
			@QueryParam("inSR") String inSR,
			@QueryParam("outSR") String outSR,
			@QueryParam("bufferSR") String bufferSR,
			@QueryParam("distances") String distances,
			@QueryParam("unit") String unit,
			@QueryParam("unionResults") @DefaultValue("false") boolean unionResults) {
		return result(token, f, geometries, inSR, outSR, bufferSR, distances,
				unit, unionResults);
	}

	@POST
	@Produces( { MediaType.TEXT_HTML, MediaType.APPLICATION_JSON })
	public String postResult(
			@FormParam("token") String token,
			@FormParam("f") @DefaultValue("html") String f,
			@FormParam("geometries") String geometries,
			@FormParam("inSR") String inSR,
			@FormParam("outSR") String outSR,
			@FormParam("bufferSR") String bufferSR,
			@FormParam("distances") String distances,
			@FormParam("unit") String unit,
			@FormParam("unionResults") @DefaultValue("false") boolean unionResults) {
		return result(token, f, geometries, inSR, outSR, bufferSR, distances,
				unit, unionResults);
	}

	private String result(String token, String f, String geometries,
			String inSR, String outSR, String bufferSR, String distances,
			String unit, boolean unionResults) {
		String result = null;

		try {
			if (ConfigManager.getServerConfig()
					.isInternalServiceNeedTokenVerify()) {
				if (!TokenService.verifyToken(token)
						&& !("html".equals(f) && geometries == null)) {
					return TokenService.TOKEN_INVALID_TIP;
				}
			}

			List<Geometry> resultGeometries = null;
			if (geometries != null && !"".equals(geometries)) {

				// Handle geometries
				List<Geometry> geometriesList = EsriJsonUtil
						.json2Geometries(geometries);

				// Handle inSR, outSR, bufferSR
				outSR = "".equals(bufferSR) ? bufferSR : inSR;
				bufferSR = "".equals(outSR) ? outSR : inSR;

				// Handle distances
				double[] doubleDistances = null;
				if (distances != null && distances != "") {
					String[] strDistances = distances.split(",");
					int distanceCount = strDistances.length;
					doubleDistances = new double[distanceCount];
					for (int i = 0; i < distanceCount; i++) {
						doubleDistances[i] = Double
								.parseDouble(strDistances[i]);
					}
				}

				// Do buffer
				resultGeometries = new ArrayList<Geometry>();
				for (Iterator<Geometry> itr = geometriesList.iterator(); itr
						.hasNext();) {
					Geometry geo = itr.next();
					for (int i = 0, count = doubleDistances.length; i < count; i++) {
						Geometry bufferGeo = GeometryToolkit.buffer(geo, inSR,
								outSR, bufferSR, doubleDistances[i]);
						resultGeometries.add(bufferGeo);
					}
				}

				// Handle union
				if (unionResults) {
					GeometryFactory geoFactory = new GeometryFactory();
					Geometry[] geos = new Geometry[resultGeometries.size()];
					resultGeometries.toArray(geos);
					GeometryCollection geoCollection = geoFactory
							.createGeometryCollection(geos);
					resultGeometries = new ArrayList<Geometry>();
					resultGeometries.add(geoCollection.union());
				}
			}

			// Various out format
			if ("json".equals(f)) {
				result = this.generateJSONResult(resultGeometries, geometries);
			} else if ("html".equals(f)) {
				result = this.generateHTMLResult(resultGeometries, token,
						geometries, inSR, outSR, bufferSR, distances, unit);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return result;
	}

	private String generateJSONResult(List<Geometry> resultGeometries,
			String geometries) {
		String result = null;

		try {
			JSONObject obj = new JSONObject();
			JSONArray arrayTemp = null;

			obj.put("GIServerVersion", VersionUtil.getCurrentversion());
			if (geometries != null && resultGeometries == null) {
				ArrayList<String> details = new ArrayList<String>();
				details.add("Result is NULL.");
			} else {
				Geometry geo = null;
				arrayTemp = new JSONArray();
				Iterator<Geometry> it = resultGeometries.iterator();
				while (it.hasNext()) {
					geo = it.next();
					arrayTemp.put(EsriJsonUtil.geometry2JSON(geo));
				}
				obj.put("geometries", arrayTemp);
			}

			result = obj.toString();
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return result;
	}

	private String generateHTMLResult(List<Geometry> resultGeometries,
			String token, String geometries, String inSR, String outSR,
			String bufferSR, String distances, String unit) {
		String result = "";

		try {
			StringBuilder sb = new StringBuilder();
			sb.append("<form><table class='formTable'>");
			sb
					.append("<tr><td>Token:</td><td><input size='50' type='text' name='token' value='${TOKEN}' /></td></tr>");
			sb
					.append("<tr valign='top'><td>Geometries:</td><td><textarea name='geometries' rows='10' cols='50'>${GEOMETRIES}</textarea></td></tr>");
			sb
					.append("<tr><td>Input Spatial Reference (WKID):</td><td><input type='text' name='inSR' value='${IN_SR}' /></td></tr>");
			sb
					.append("<tr><td>Output Spatial Reference (WKID):</td><td><input type='text' name='outSR' value='${OUT_SR}' /></td></tr>");
			sb
					.append("<tr><td>Buffer Spatial Reference (WKID):</td><td><input type='text' name='bufferSR' value='${BUFFER_SR}' /></td></tr>");
			sb
					.append("<tr><td>Buffer Distances:</td><td><input type='text' name='distances' value='${DISTANCES}' /></td></tr>");
			sb
					.append("<tr><td>Distance Unit (WKID):</td><td><input type='text' name='unit' value='${UNIT}' /></td></tr>");
			sb
					.append("<tr valign='top'><td>Union Results:</td><td><input type='radio' name='unionResults' value='true'   /> Yes &nbsp;<input type='radio' name='unionResults' value='false' checked='true'  /> No</td></tr>");
			sb
					.append("<tr><td colspan='2'><input type='submit' value='Buffer' /></td></tr>");
			sb.append("</table></form>");
			String restBody = sb.toString();

			restBody = restBody.replace("${TOKEN}", token == null ? "" : token);
			restBody = restBody.replace("${GEOMETRIES}",
					geometries == null ? "" : geometries);
			restBody = restBody.replace("${IN_SR}", inSR == null ? "" : inSR);
			restBody = restBody
					.replace("${OUT_SR}", outSR == null ? "" : outSR);
			restBody = restBody.replace("${BUFFER_SR}", bufferSR == null ? ""
					: bufferSR);
			restBody = restBody.replace("${DISTANCES}", distances == null ? ""
					: distances);
			restBody = restBody.replace("${UNIT}", unit == null ? "" : unit);

			if (!(geometries == null)) {
				StringBuilder sbResult = new StringBuilder();
				int resultCount = resultGeometries.size();
				if (resultCount > 0) {
					sbResult
							.append("<div style='color:#aaffaa'>Results number : "
									+ resultCount + "<br/></div><br/>");
					for (Iterator<Geometry> itr = resultGeometries.iterator(); itr
							.hasNext();) {
						Geometry geo = itr.next();
						sbResult.append("<div>");
						sbResult.append(EsriJsonUtil.geometry2JSON(geo)
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
			html.setTitle("Buffer");
			html
					.setCatalog(" &gt; <a href='"
							+ contextRoot
							+ "/rest/service/GeometryService'>Geometry Service</a> &gt; Buffer");
			html.setHeader("Buffer Geometries");
			html.setRestBody(restBody);
			result = html.toString();
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return result;
	}
}

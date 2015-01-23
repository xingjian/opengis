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
import com.gi.giserver.core.service.geometryservice.Buffer;
import com.gi.giserver.core.service.tokenservice.TokenService;
import com.gi.giserver.core.util.error.ServiceError;
import com.gi.giserver.core.util.json.EsriJsonGeometryUtil;
import com.gi.giserver.core.util.json.EsriJsonUtil;
import com.gi.giserver.rest.html.ServiceHTML;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryCollection;
import com.vividsolutions.jts.geom.GeometryFactory;

/**
 * @author Wu Yongfeng
 * 
 */
@Path("/GeometryService/buffer")
public class BufferResource {

	@Context
	ServletContext context;

	@GET
	public String getResult(@QueryParam("token") String token, @QueryParam("f") @DefaultValue("html") String f,
			@QueryParam("geometries") String geometries, @QueryParam("inSR") String inSR,
			@QueryParam("outSR") String outSR, @QueryParam("bufferSR") String bufferSR,
			@QueryParam("distances") String distances, @QueryParam("unit") String unit,
			@QueryParam("unionResults") @DefaultValue("false") boolean unionResults) {
		return result(token, f, geometries, inSR, outSR, bufferSR, distances, unit, unionResults);
	}

	@POST
	public String postResult(@FormParam("token") String token, @FormParam("f") @DefaultValue("html") String f,
			@FormParam("geometries") String geometries, @FormParam("inSR") String inSR,
			@FormParam("outSR") String outSR, @FormParam("bufferSR") String bufferSR,
			@FormParam("distances") String distances, @FormParam("unit") String unit,
			@FormParam("unionResults") @DefaultValue("false") boolean unionResults) {
		return result(token, f, geometries, inSR, outSR, bufferSR, distances, unit, unionResults);
	}

	private String result(String token, String f, String geometries, String inSR, String outSR, String bufferSR,
			String distances, String unit, boolean unionResults) {
		String result = null;

		try {
			if (ConfigManager.getServerConfig().isInternalServiceNeedTokenVerify()) {
				if (!TokenService.verifyToken(token) && !("html".equals(f) && geometries == null)) {
					return TokenService.TOKEN_INVALID_TIP;
				}
			}

			// Handle geometries
			List<Geometry> geometriesList = EsriJsonGeometryUtil.json2Geometries(geometries);

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
					doubleDistances[i] = Double.parseDouble(strDistances[i]);
				}
			}

			// Do buffer
			List<Geometry> bufferGeometries = Buffer.buffers(geometriesList, inSR, outSR, bufferSR, doubleDistances);

			// Handle union
			if (unionResults) {
				GeometryFactory geoFactory = new GeometryFactory();
				Geometry[] geos = new Geometry[bufferGeometries.size()];
				bufferGeometries.toArray(geos);
				GeometryCollection geoCollection = geoFactory.createGeometryCollection(geos);
				bufferGeometries = new ArrayList<Geometry>();
				bufferGeometries.add(geoCollection.union());
			}

			// Various out format
			if ("json".equals(f)) {
				result = this.generateJSONResult(bufferGeometries, geometries);
			} else if ("html".equals(f)) {
				result = this.generateHTMLResult(bufferGeometries, token, geometries, inSR, outSR, bufferSR, distances,
						unit);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return result;
	}

	private String generateJSONResult(List<Geometry> geometryList, String geometries) {
		String result = null;

		try {
			JSONObject obj = new JSONObject();
			JSONArray arrayTemp = null;

			obj.put("about", "Powered by GIServer");
			if (geometries != null && geometryList == null) {
				ArrayList<String> details = new ArrayList<String>();
				details.add("Result is NULL.");
				obj.put("error", EsriJsonUtil.generateJSONError(ServiceError.GEOMETRY_NO_RESULT_ERROR, details));
			} else {
				Geometry geo = null;
				arrayTemp = new JSONArray();
				Iterator<Geometry> it = geometryList.iterator();
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

	private String generateHTMLResult(List<Geometry> resultGeometries, String token, String geometries, String inSR,
			String outSR, String bufferSR, String distances, String unit) {
		String result = "";

		try {
			String restBody = "";
			restBody += "<form><table style='border:1px solid #000000;'>";
			restBody += "<tr><td>Token:</td><td><input size='50' type='text' name='token' value='${TOKEN}' /></td></tr>";
			restBody += "<tr valign='top'><td>Geometries:</td><td><textarea name='geometries' rows='10' cols='50'>${GEOMETRIES}</textarea></td></tr>";
			restBody += "<tr><td>Input Spatial Reference (WKID):</td><td><input type='text' name='inSR' value='${IN_SR}' /></td></tr>";
			restBody += "<tr><td>Output Spatial Reference (WKID):</td><td><input type='text' name='outSR' value='${OUT_SR}' /></td></tr>";
			restBody += "<tr><td>Buffer Spatial Reference (WKID):</td><td><input type='text' name='bufferSR' value='${BUFFER_SR}' /></td></tr>";
			restBody += "<tr><td>Buffer Distances:</td><td><input type='text' name='distances' value='${DISTANCES}' /></td></tr>";
			restBody += "<tr><td>Distance Unit (WKID):</td><td><input type='text' name='unit' value='${UNIT}' /></td></tr>";
			restBody += "<tr valign='top'><td>Union Results:</td><td><input type='radio' name='unionResults' value='true'   /> Yes &nbsp;<input type='radio' name='unionResults' value='false' checked='true'  /> No</td></tr>";
			restBody += "<tr><td colspan='2'><input type='submit' value='Buffer' /></td></tr>";
			restBody += "</table></form>";

			restBody = restBody.replace("${TOKEN}", token == null ? "" : token);
			restBody = restBody.replace("${GEOMETRIES}", geometries == null ? "" : geometries);
			restBody = restBody.replace("${IN_SR}", inSR == null ? "" : inSR);
			restBody = restBody.replace("${OUT_SR}", outSR == null ? "" : outSR);
			restBody = restBody.replace("${BUFFER_SR}", bufferSR == null ? "" : bufferSR);
			restBody = restBody.replace("${DISTANCES}", distances == null ? "" : distances);
			restBody = restBody.replace("${UNIT}", unit == null ? "" : unit);

			if (!(geometries == null)) {
				int resultCount = resultGeometries.size();
				if (resultCount > 0) {
					restBody += "<div style='color:#aaffaa'>Results number : " + resultCount + "<br/></div><br/>";
					Iterator<Geometry> it = resultGeometries.iterator();
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
			result += ServiceHTML.getHeader(contextRoot).replace("${TITLE}", "Buffer");
			result += ServiceHTML.getNav(contextRoot)
					.replace(
							"${CATALOG}",
							" &gt; <a href='" + contextRoot
									+ "/rest/service/GeometryService'>Geometry Service</a> &gt; Buffer");
			result += ServiceHTML.getH2().replace("${TITLE}", "Buffer Geometries");
			result += ServiceHTML.getRestBody().replace("${RESTBODY}", restBody);
			result += ServiceHTML.getFooter();
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return result;
	}

}

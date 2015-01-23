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
import com.gi.engine.geometry.RelationType;
import com.gi.engine.util.VersionUtil;
import com.gi.server.core.config.ConfigManager;
import com.gi.server.core.service.geometryservice.GeometryIndexCouple;
import com.gi.server.core.service.tokenservice.TokenService;
import com.gi.server.rest.html.ServiceHTML;
import com.vividsolutions.jts.geom.Geometry;

/**
 * @author Wu Yongfeng
 * 
 */
@Path("/GeometryService/relation")
public class RelationResource {

	@Context
	ServletContext context;

	@GET
	@Produces( { MediaType.TEXT_HTML, MediaType.APPLICATION_JSON })
	public String getResult(@QueryParam("token") String token,
			@QueryParam("f") @DefaultValue("html") String f,
			@QueryParam("geometries1") String geometries1,
			@QueryParam("geometries2") String geometries2,
			@QueryParam("sr") String sr,
			@QueryParam("relation") String relation,
			@QueryParam("relationParam") String relationParam) {
		return result(token, f, geometries1, geometries2, sr, relation,
				relationParam);
	}

	@POST
	@Produces( { MediaType.TEXT_HTML, MediaType.APPLICATION_JSON })
	public String postResult(@FormParam("token") String token,
			@FormParam("f") @DefaultValue("html") String f,
			@FormParam("geometries1") String geometries1,
			@FormParam("geometries2") String geometries2,
			@FormParam("sr") String sr, @FormParam("relation") String relation,
			@FormParam("relationParam") String relationParam) {
		return result(token, f, geometries1, geometries2, sr, relation,
				relationParam);
	}

	private String result(String token, String f, String geometries1,
			String geometries2, String sr, String relation, String relationParam) {
		String result = null;

		try {
			if (ConfigManager.getServerConfig()
					.isInternalServiceNeedTokenVerify()) {
				if (!TokenService.verifyToken(token)
						&& !("html".equals(f) && (geometries1 == null || geometries2 == null))) {
					return TokenService.TOKEN_INVALID_TIP;
				}
			}

			// Handle geometries
			List<Geometry> geometriesList1 = EsriJsonUtil
					.json2Geometries(geometries1);
			List<Geometry> geometriesList2 = EsriJsonUtil
					.json2Geometries(geometries2);

			// Handle relation
			RelationType relationType;
			if ("esriGeometryRelationCross".equals(relation)) {
				relationType = RelationType.CROSS;
			} else if ("esriGeometryRelationDisjoint".equals(relation)) {
				relationType = RelationType.DISJOINT;
			} else if ("esriGeometryRelationIn".equals(relation)) {
				relationType = RelationType.IN;
			} else if ("esriGeometryRelationInteriorIntersection"
					.equals(relation)) {
				relationType = RelationType.INTERIOR_INTERSECTION;
			} else if ("esriGeometryRelationIntersection".equals(relation)) {
				relationType = RelationType.INTERSECTION;
			} else if ("esriGeometryRelationLineCoincidence".equals(relation)) {
				relationType = RelationType.LINE_COINCIDENCE;
			} else if ("esriGeometryRelationLineTouch".equals(relation)) {
				relationType = RelationType.LINE_TOUCH;
			} else if ("esriGeometryRelationOverlap".equals(relation)) {
				relationType = RelationType.OVERLAP;
			} else if ("esriGeometryRelationPointTouch".equals(relation)) {
				relationType = RelationType.POINT_TOUCH;
			} else if ("esriGeometryRelationTouch".equals(relation)) {
				relationType = RelationType.TOUCH;
			} else if ("esriGeometryRelationWithin".equals(relation)) {
				relationType = RelationType.WITHIN;
			} else {
				relationType = RelationType.RELATION;
			}

			// Handle relationParam
			// String MUST like this: RELATE(G1, G2, "FFFTTT***")
			// String like "G1 IN G2" not supported as far
			String relationPattern = "";
			try {
				if (relationParam != null) {
					if (relationParam.contains("'")) {
						String[] relationParams = relationParam.split("'");
						relationPattern = relationParams[1];
					} else if (relationParam.contains("\"")) {
						String[] relationParams = relationParam.split("\"");
						relationPattern = relationParams[1];
					}
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			}

			// Do relation
			List<GeometryIndexCouple> relationCouples = new ArrayList<GeometryIndexCouple>();
			if (geometries1 != null) {
				for (int i = 0, count1 = geometriesList1.size(); i < count1; i++) {
					Geometry geo1 = geometriesList1.get(i);

					for (int j = 0, count2 = geometriesList2.size(); j < count2; j++) {
						Geometry geo2 = geometriesList2.get(j);

						if (GeometryToolkit.relation(geo1, geo2, relationType,
								relationPattern)) {
							GeometryIndexCouple couple = new GeometryIndexCouple(
									i, j);
							relationCouples.add(couple);
						}

					}
				}
			}

			// Various out format
			if ("json".equals(f)) {
				result = this.generateJSONResult(relationCouples, geometries1,
						geometries2);
			} else if ("html".equals(f)) {
				result = this.generateHTMLResult(relationCouples, token, sr,
						geometries1, geometries2, relationParam);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return result;
	}

	private String generateJSONResult(
			List<GeometryIndexCouple> relationCouples, String geometries1,
			String geometries2) {
		String result = null;

		try {
			JSONObject obj = new JSONObject();
			JSONObject objTemp = null;
			JSONArray arrayTemp = null;

			obj.put("GIServerVersion", VersionUtil.getCurrentversion());
			if (geometries1 != null && geometries2 != null
					&& relationCouples == null) {
				ArrayList<String> details = new ArrayList<String>();
				details.add("Result is NULL.");
			} else {
				GeometryIndexCouple couple = null;
				arrayTemp = new JSONArray();
				Iterator<GeometryIndexCouple> it = relationCouples.iterator();
				while (it.hasNext()) {
					couple = it.next();
					objTemp = new JSONObject();
					objTemp.put("geometry1Index", couple.getGeometry1Index());
					objTemp.put("geometry2Index", couple.getGeometry2Index());
					arrayTemp.put(objTemp);
				}
				obj.put("relations", arrayTemp);
			}

			result = obj.toString();
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return result;
	}

	private String generateHTMLResult(
			List<GeometryIndexCouple> relationCouples, String token, String sr,
			String geometries1, String geometries2, String relationParam) {
		String result = "";

		try {
			StringBuilder sb = new StringBuilder();
			sb.append("<form><table class='formTable'>");
			sb
					.append("<tr><td>Token:</td><td><input size='50' type='text' name='token' value='${TOKEN}' /></td></tr>");
			sb
					.append("<tr><td>Spatial Reference (WKID):</td><td><input type='text' name='sr' value='${SR}' /></td></tr>");
			sb
					.append("<tr valign='top'><td>Geometries1:</td><td><textarea name='geometries1' rows='10' cols='50'>${GEOMETRIES1}</textarea></td></tr>");
			sb
					.append("<tr valign='top'><td>Geometries2:</td><td><textarea name='geometries2' rows='10' cols='50'>${GEOMETRIES2}</textarea></td></tr>");
			sb
					.append("<tr valign='top'><td>Relation:</td><td><select name='relation'><option value='esriGeometryRelationCross' >Cross</option><option value='esriGeometryRelationDisjoint' >Disjoint</option><option value='esriGeometryRelationIn' >In</option><option value='esriGeometryRelationInteriorIntersection' >Interior Intersection</option><option value='esriGeometryRelationIntersection' >Intersection</option><option value='esriGeometryRelationLineCoincidence' >Line Coincidence</option><option value='esriGeometryRelationLineTouch' >Line Touch</option><option value='esriGeometryRelationOverlap' >Overlap</option><option value='esriGeometryRelationPointTouch' >Point Touch</option><option value='esriGeometryRelationTouch' >Touch</option><option value='esriGeometryRelationWithin' >Within</option><option value='esriGeometryRelationRelation' >Relation Relation</option></select></td></tr>");
			sb
					.append("<tr><td>Relation Parameter:</td><td><input type='text' name='relationParam' size='50' value='${RELATION_PARAM}' /></td></tr>");
			sb
					.append("<tr><td colspan='2'><input type='submit' value='Compute Relations' /></td></tr>");
			sb.append("</table></form>");
			String restBody = sb.toString();

			restBody = restBody.replace("${TOKEN}", token == null ? "" : token);
			restBody = restBody.replace("${SR}", sr == null ? "" : sr);
			restBody = restBody.replace("${GEOMETRIES1}",
					geometries1 == null ? "" : geometries1);
			restBody = restBody.replace("${GEOMETRIES2}",
					geometries2 == null ? "" : geometries2);
			restBody = restBody.replace("${RELATION_PARAM}",
					relationParam == null ? "" : relationParam);

			if (!(geometries1 == null || geometries2 == null)) {
				StringBuilder sbResult = new StringBuilder();
				int resultCount = relationCouples.size();
				if (resultCount > 0) {
					sbResult
							.append("<div style='color:#aaffaa'>Results number : "
									+ resultCount + "<br/></div><br/>");
					for (Iterator<GeometryIndexCouple> itr = relationCouples
							.iterator(); itr.hasNext();) {
						GeometryIndexCouple couple = itr.next();
						sbResult.append("<div>");
						sbResult.append("geometry1: "
								+ couple.getGeometry1Index());
						sbResult.append("<br/>");
						sbResult.append("geometry2: "
								+ couple.getGeometry2Index());
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
			html.setTitle("Relation");
			html
					.setCatalog(" &gt; <a href='"
							+ contextRoot
							+ "/rest/service/GeometryService'>Geometry Service</a> &gt; Relation");
			html.setHeader("Relation");
			html.setRestBody(restBody);
			result = html.toString();
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return result;
	}

}

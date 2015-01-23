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

import com.gi.giengine.geometry.relation.RelationCouple;
import com.gi.giengine.geometry.relation.RelationType;
import com.gi.giserver.core.config.ConfigManager;
import com.gi.giserver.core.service.geometryservice.Relation;
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
@Path("/GeometryService/relation")
public class RelationResource {

	@Context
	ServletContext context;

	@GET
	public String getResult(@QueryParam("token") String token, @QueryParam("f") @DefaultValue("html") String f,
			@QueryParam("geometries1") String geometries1, @QueryParam("geometries2") String geometries2,
			@QueryParam("sr") String sr, @QueryParam("relation") String relation,
			@QueryParam("relationParam") String relationParam) {
		return result(token, f, geometries1, geometries2, sr, relation, relationParam);
	}

	@POST
	public String postResult(@FormParam("token") String token, @FormParam("f") @DefaultValue("html") String f,
			@FormParam("geometries1") String geometries1, @FormParam("geometries2") String geometries2,
			@FormParam("sr") String sr, @FormParam("relation") String relation,
			@FormParam("relationParam") String relationParam) {
		return result(token, f, geometries1, geometries2, sr, relation, relationParam);
	}

	private String result(String token, String f, String geometries1, String geometries2, String sr, String relation,
			String relationParam) {
		String result = null;

		try {
			if (ConfigManager.getServerConfig().isInternalServiceNeedTokenVerify()) {
				if (!TokenService.verifyToken(token)
						&& !("html".equals(f) && (geometries1 == null || geometries2 == null))) {
					return TokenService.TOKEN_INVALID_TIP;
				}
			}

			// Handle geometries
			List<Geometry> geometriesList1 = EsriJsonGeometryUtil.json2Geometries(geometries1);
			List<Geometry> geometriesList2 = EsriJsonGeometryUtil.json2Geometries(geometries2);

			// Handle relation
			RelationType relationType;
			if ("esriGeometryRelationCross".equals(relation)) {
				relationType = RelationType.CROSS;
			} else if ("esriGeometryRelationDisjoint".equals(relation)) {
				relationType = RelationType.DISJOINT;
			} else if ("esriGeometryRelationIn".equals(relation)) {
				relationType = RelationType.IN;
			} else if ("esriGeometryRelationInteriorIntersection".equals(relation)) {
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
			List<RelationCouple> relationCouples = Relation.relations(geometriesList1, geometriesList2, relationType,
					relationPattern);

			// Various out format
			if ("json".equals(f)) {
				result = this.generateJSONResult(relationCouples, geometries1, geometries2);
			} else if ("html".equals(f)) {
				result = this.generateHTMLResult(relationCouples, token, sr, geometries1, geometries2, relationParam);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return result;
	}

	private String generateJSONResult(List<RelationCouple> relationCouples, String geometries1, String geometries2) {
		String result = null;

		try {
			JSONObject obj = new JSONObject();
			JSONObject objTemp = null;
			JSONArray arrayTemp = null;

			obj.put("about", "Powered by GIServer");
			if (geometries1 != null && geometries2 != null && relationCouples == null) {
				ArrayList<String> details = new ArrayList<String>();
				details.add("Result is NULL.");
				obj.put("error", EsriJsonUtil.generateJSONError(ServiceError.GEOMETRY_NO_RESULT_ERROR, details));
			} else {
				RelationCouple couple = null;
				arrayTemp = new JSONArray();
				Iterator<RelationCouple> it = relationCouples.iterator();
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

	private String generateHTMLResult(List<RelationCouple> relationCouples, String token, String sr,
			String geometries1, String geometries2, String relationParam) {
		String result = "";

		try {
			String restBody = "";
			restBody += "<form><table style='border:1px solid #000000;'>";
			restBody += "<tr><td>Token:</td><td><input size='50' type='text' name='token' value='${TOKEN}' /></td></tr>";
			restBody += "<tr><td>Spatial Reference (WKID):</td><td><input type='text' name='sr' value='${SR}' /></td></tr>";
			restBody += "<tr valign='top'><td>Geometries1:</td><td><textarea name='geometries1' rows='10' cols='50'>${GEOMETRIES1}</textarea></td></tr>";
			restBody += "<tr valign='top'><td>Geometries2:</td><td><textarea name='geometries2' rows='10' cols='50'>${GEOMETRIES2}</textarea></td></tr>";
			restBody += "<tr valign='top'><td>Relation:</td><td><select name='relation'><option value='esriGeometryRelationCross' >Cross</option><option value='esriGeometryRelationDisjoint' >Disjoint</option><option value='esriGeometryRelationIn' >In</option><option value='esriGeometryRelationInteriorIntersection' >Interior Intersection</option><option value='esriGeometryRelationIntersection' >Intersection</option><option value='esriGeometryRelationLineCoincidence' >Line Coincidence</option><option value='esriGeometryRelationLineTouch' >Line Touch</option><option value='esriGeometryRelationOverlap' >Overlap</option><option value='esriGeometryRelationPointTouch' >Point Touch</option><option value='esriGeometryRelationTouch' >Touch</option><option value='esriGeometryRelationWithin' >Within</option><option value='esriGeometryRelationRelation' >Relation Relation</option></select></td></tr>";
			restBody += "<tr><td>Relation Parameter:</td><td><input type='text' name='relationParam' size='50' value='${RELATION_PARAM}' /></td></tr>";
			restBody += "<tr><td colspan='2'><input type='submit' value='Compute Relations' /></td></tr>";
			restBody += "</table></form>";

			restBody = restBody.replace("${TOKEN}", token == null ? "" : token);
			restBody = restBody.replace("${SR}", sr == null ? "" : sr);
			restBody = restBody.replace("${GEOMETRIES1}", geometries1 == null ? "" : geometries1);
			restBody = restBody.replace("${GEOMETRIES2}", geometries2 == null ? "" : geometries2);
			restBody = restBody.replace("${RELATION_PARAM}", relationParam == null ? "" : relationParam);

			if (!(geometries1 == null || geometries2 == null)) {
				int resultCount = relationCouples.size();
				if (resultCount > 0) {
					restBody += "<div style='color:#aaffaa'>Results number : " + resultCount + "<br/></div><br/>";
					Iterator<RelationCouple> it = relationCouples.iterator();
					RelationCouple couple = null;
					while (it.hasNext()) {
						couple = it.next();
						restBody += "<div>";
						restBody += "geometry1: " + couple.getGeometry1Index();
						restBody += "<br/>";
						restBody += "geometry2: " + couple.getGeometry2Index();
						restBody += "<br/></div><tr><td><hr/></td></tr>";
					}
				} else {
					restBody += "<div style='color:#ffaaaa'>No results!<br/></div>";
				}
			}

			String contextRoot = context.getContextPath();
			result += ServiceHTML.getHeader(contextRoot).replace("${TITLE}", "Relation");
			result += ServiceHTML.getNav(contextRoot).replace(
					"${CATALOG}",
					" &gt; <a href='" + contextRoot
							+ "/rest/service/GeometryService'>Geometry Service</a> &gt; Relation");
			result += ServiceHTML.getH2().replace("${TITLE}", "Relation");
			result += ServiceHTML.getRestBody().replace("${RESTBODY}", restBody);
			result += ServiceHTML.getFooter();
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return result;
	}

}

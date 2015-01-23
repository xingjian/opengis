package com.gi.giserver.core.service.geometryservice;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.gi.giengine.geometry.RelationEngine;
import com.gi.giengine.geometry.relation.RelationCouple;
import com.gi.giengine.geometry.relation.RelationType;
import com.gi.giengine.util.ToTextUtil;
import com.gi.giserver.core.i18n.ResourceManager;
import com.vividsolutions.jts.geom.Geometry;

public class Relation {
	static Logger logger = Logger.getLogger(Relation.class);

	/**
	 * @param geometries1
	 * @param geometries2
	 * @param sr
	 * @param relation
	 * @param relationParam
	 *            like FFFTTT***, take effect when relation parameter is 0
	 *            (Relation.RELATION)
	 * @return
	 */
	public static List<RelationCouple> relations(List<Geometry> geometries1,
			List<Geometry> geometries2, RelationType relation,
			String relationPattern) {
		List<RelationCouple> result = new ArrayList<RelationCouple>();

		try {
			result = RelationEngine.relations(geometries1, geometries2,
					relation, relationPattern);
		} catch (Exception ex) {
			logger.error(ResourceManager.getResourceBundleGeometryServiceLog()
					.getString("ERROR.RELATION")
					+ "<"
					+ relation.toString()
					+ "-"
					+ relationPattern
					+ ":"
					+ ToTextUtil.toText(geometries1)
					+ "-"
					+ ToTextUtil.toText(geometries2) + ">", ex);
		}

		return result;
	}

	public static boolean relation(Geometry geometry1, Geometry geometry2,
			RelationType relation, String relationPattern) {
		boolean result = false;

		try {
			result = RelationEngine.relation(geometry1, geometry2, relation,
					relationPattern);
		} catch (Exception ex) {
			logger.error(ResourceManager.getResourceBundleGeometryServiceLog()
					.getString("ERROR.RELATION")
					+ "<"
					+ relation.toString()
					+ "-"
					+ relationPattern
					+ ":"
					+ ToTextUtil.toText(geometry1)
					+ "-"
					+ ToTextUtil.toText(geometry2) + ">", ex);
		}

		return result;
	}

}

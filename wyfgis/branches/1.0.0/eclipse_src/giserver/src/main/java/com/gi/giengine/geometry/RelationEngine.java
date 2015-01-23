package com.gi.giengine.geometry;

import java.util.ArrayList;
import java.util.List;

import com.gi.giengine.geometry.relation.RelationCouple;
import com.gi.giengine.geometry.relation.RelationType;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.Point;

public class RelationEngine {

	/**
	 * @param geometries1
	 * @param geometries2
	 * @param sr
	 * @param relation
	 * @param relationPattern
	 *            like FFFTTT***, take effect when relation parameter is  Relation.RELATION
	 * @return
	 */
	public static List<RelationCouple> relations(List<Geometry> geometries1,
			List<Geometry> geometries2, RelationType relation,
			String relationPattern) {
		List<RelationCouple> result = new ArrayList<RelationCouple>();

		Geometry geo1 = null;
		Geometry geo2 = null;
		RelationCouple couple = null;
		if (geometries1 != null) {
			for (int i = 0; i < geometries1.size(); i++) {
				geo1 = geometries1.get(i);

				for (int j = 0; j < geometries2.size(); j++) {
					geo2 = geometries2.get(j);

					if (relation(geo1, geo2, relation, relationPattern)) {
						couple = new RelationCouple(i, j);
						result.add(couple);
					}

				}
			}
		}

		return result;
	}

	public static boolean relation(Geometry geometry1, Geometry geometry2,
			RelationType relation, String relationPattern) {
		boolean result = false;

		// Determine whether geometry1 and geometry2 satisfy the given
		// relation
		if (relation.equals(RelationType.CROSS)) {
			result = geometry1.crosses(geometry2);
		} else if (relation.equals(RelationType.DISJOINT)) {
			result = geometry1.disjoint(geometry2);
		} else if (relation.equals(RelationType.IN)) {
			result = geometry1.within(geometry2)
					&& !(geometry1.coveredBy(geometry2));
		} else if (relation.equals(RelationType.INTERIOR_INTERSECTION)) {
			result = geometry1.intersects(geometry2)
					&& !(geometry1.touches(geometry2));
		} else if (relation.equals(RelationType.INTERSECTION)) {
			result = geometry1.intersects(geometry2);
		} else if (relation.equals(RelationType.LINE_COINCIDENCE)) {
			result = (geometry1.intersection(geometry2)).getClass().equals(
					LineString.class);
		} else if (relation.equals(RelationType.LINE_TOUCH)) {
			result = geometry1.touches(geometry2)
					&& (geometry1.intersection(geometry2)).getClass().equals(
							LineString.class);
		} else if (relation.equals(RelationType.OVERLAP)) {
			result = geometry1.overlaps(geometry2);
		} else if (relation.equals(RelationType.POINT_TOUCH)) {
			result = geometry1.touches(geometry2)
					&& (geometry1.intersection(geometry2)).getClass().equals(
							Point.class);
		} else if (relation.equals(RelationType.RELATION)) {
			result = geometry1.relate(geometry2, relationPattern);
		} else if (relation.equals(RelationType.TOUCH)) {
			result = geometry1.touches(geometry2);
		} else if (relation.equals(RelationType.WITHIN)) {
			result = geometry1.within(geometry2);
		}

		return result;
	}
}

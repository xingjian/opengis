package com.gi.engine.carto;

import org.geotools.data.FeatureSource;
import org.geotools.factory.CommonFactoryFinder;
import org.geotools.factory.GeoTools;
import org.geotools.feature.FeatureCollection;
import org.geotools.filter.text.ecql.ECQL;
import org.opengis.feature.Feature;
import org.opengis.feature.type.FeatureType;
import org.opengis.filter.Filter;
import org.opengis.filter.FilterFactory2;
import org.opengis.filter.expression.PropertyName;

import com.vividsolutions.jts.geom.Geometry;

public class FeatureLayer extends Layer {
	
	public FeatureType getFeatureType() {
		return this.getMapLayer().getFeatureSource().getSchema();
	}

	public FeatureCollection<? extends FeatureType, ? extends Feature> query(
			QueryParam queryParam) throws Exception {
		FeatureCollection<? extends FeatureType, ? extends Feature> result = null;

		FeatureSource<? extends FeatureType, ? extends Feature> featureSource = this
				.getMapLayer().getFeatureSource();
		if (featureSource != null) {
			FilterFactory2 ff = CommonFactoryFinder.getFilterFactory2(GeoTools
					.getDefaultHints());

			Filter geometryFilter = null;
			Geometry geometry = queryParam.getGeometry();
			if (geometry != null) {
				SpatialFilterType spatialFilterType = queryParam
				.getSpatialFilterType();

				String geomtryField = featureSource.getSchema()
						.getGeometryDescriptor().getLocalName();
				PropertyName geometryPropertyName = ff.property(geomtryField);
				if (spatialFilterType.equals(SpatialFilterType.INTERSECTS)) {
					geometryFilter = ff.intersects(ff.literal(geometry),
							geometryPropertyName);
				} else if (spatialFilterType.equals(SpatialFilterType.CONTAINS)) {
					geometryFilter = ff.contains(ff.literal(geometry),
							geometryPropertyName);
				} else if (spatialFilterType.equals(SpatialFilterType.CROSSES)) {
					geometryFilter = ff.crosses(ff.literal(geometry),
							geometryPropertyName);
				} else if (spatialFilterType
						.equals(SpatialFilterType.ENVELOPE_INTERSECTS)) {
					geometryFilter = ff.intersects(ff.literal(geometry
							.getEnvelope()), geometryPropertyName);
				} else if (spatialFilterType
						.equals(SpatialFilterType.INDEX_INTERSECTS)) {
					// TODO
					// It is from ArcGIS, but not clear what means
					geometryFilter = ff.intersects(ff.literal(geometry),
							geometryPropertyName);
				} else if (spatialFilterType.equals(SpatialFilterType.OVERLAPS)) {
					geometryFilter = ff.overlaps(ff.literal(geometry),
							geometryPropertyName);
				} else if (spatialFilterType.equals(SpatialFilterType.TOUCHES)) {
					geometryFilter = ff.touches(ff.literal(geometry),
							geometryPropertyName);
				} else if (spatialFilterType.equals(SpatialFilterType.WITHIN)) {
					geometryFilter = ff.within(ff.literal(geometry),
							geometryPropertyName);
				}
			}

			Filter attributesFilter = null;
			String where = queryParam.getWhere();
			if (where != null && !"".equals(where.trim())
					&& !"1=1".equals(where.trim())) {
				try {
					attributesFilter = ECQL.toFilter(where);
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}

			if (geometryFilter != null && attributesFilter == null) {
				result = featureSource.getFeatures(geometryFilter);
			} else if (geometryFilter == null && attributesFilter != null) {
				result = featureSource.getFeatures(attributesFilter);
			} else if (geometryFilter == null && attributesFilter == null) {
				// I like 1=1 :)
				if ("1=1".equals(where)) {
					result = featureSource.getFeatures();
				} else {
					result = null;
				}
			} else {
				result = featureSource.getFeatures(geometryFilter)
						.subCollection(attributesFilter);
			}
		}

		return result;
	}

}

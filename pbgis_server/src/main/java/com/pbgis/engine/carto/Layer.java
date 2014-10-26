package com.pbgis.engine.carto;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.geotools.data.FeatureSource;
import org.geotools.data.FeatureStore;
import org.geotools.factory.CommonFactoryFinder;
import org.geotools.factory.GeoTools;
import org.geotools.feature.FeatureCollection;
import org.geotools.feature.FeatureIterator;
import org.geotools.filter.text.ecql.ECQL;
import org.geotools.map.MapLayer;
import org.opengis.feature.Feature;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.feature.type.FeatureType;
import org.opengis.filter.Filter;
import org.opengis.filter.FilterFactory2;
import org.opengis.filter.expression.PropertyName;
import org.opengis.filter.identity.FeatureId;

import com.vividsolutions.jts.geom.Geometry;

public abstract class Layer {

	/*
	 * mapLayer is a geotools object for Layer it provides most functions but
	 * Layer contains more information like editable
	 */
	private MapLayer mapLayer = null;

	private boolean editable = true;

	public MapLayer getMapLayer() {
		return mapLayer;
	}

	public void setMapLayer(MapLayer maplayer) {
		this.mapLayer = maplayer;
	}

	public boolean isEditable() {
		return editable;
	}

	public void setEditable(boolean editable) {
		this.editable = editable;
	}

	private Filter generateGeometryFilter(String geomtryField,
			QueryParam queryParam) {
		FilterFactory2 ff = CommonFactoryFinder.getFilterFactory2(GeoTools
				.getDefaultHints());

		Filter geometryFilter = null;
		Geometry geometry = queryParam.getGeometry();
		SpatialFilterType spatialFilterType = queryParam.getSpatialFilterType();
		if (geometry != null) {
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

		return geometryFilter;
	}

	private Filter generateAttributesFilter(QueryParam queryParam) {
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

		return attributesFilter;
	}

	private Filter generateIdFilter(String featureTypeName, String[] featureIds) {
		FilterFactory2 ff = CommonFactoryFinder.getFilterFactory2(GeoTools
				.getDefaultHints());

		Set<FeatureId> fids = new HashSet<FeatureId>();
		for (int i = 0, count = featureIds.length; i < count; i++) {
			String id = featureIds[i];
			FeatureId fid = ff.featureId(featureTypeName + "." + id);
			fids.add(fid);
		}
		Filter filter = ff.id(fids);

		return filter;
	}

	public FeatureCollection<? extends FeatureType, ? extends Feature> query(
			QueryParam queryParam) throws Exception {
		FeatureCollection<? extends FeatureType, ? extends Feature> result = null;

		FeatureSource<? extends FeatureType, ? extends Feature> featureSource = mapLayer
				.getFeatureSource();

		if (featureSource != null) {
			String geomtryField = featureSource.getSchema()
					.getGeometryDescriptor().getLocalName();
			Filter geometryFilter = generateGeometryFilter(geomtryField,
					queryParam);
			Filter attributesFilter = generateAttributesFilter(queryParam);

			if (geometryFilter != null && attributesFilter == null) {
				result = featureSource.getFeatures(geometryFilter);
			} else if (geometryFilter == null && attributesFilter != null) {
				result = featureSource.getFeatures(attributesFilter);
			} else if (geometryFilter == null && attributesFilter == null) {
				// I like 1=1 :)
				if ("1=1".equals(queryParam.getWhere())) {
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

	public FeatureCollection<? extends FeatureType, ? extends Feature> query(
			String[] featureIds) throws Exception {
		FeatureCollection<? extends FeatureType, ? extends Feature> result = null;

		FeatureSource<? extends FeatureType, ? extends Feature> featureSource = mapLayer
				.getFeatureSource();

		if (featureSource != null) {
			String featureTypeName = featureSource.getSchema().getName()
					.getLocalPart();
			Filter idFilter = generateIdFilter(featureTypeName, featureIds);
			result = featureSource.getFeatures(idFilter);
		}

		return result;
	}

	@SuppressWarnings("unchecked")
	public List<FeatureId> add(
			FeatureCollection<SimpleFeatureType, SimpleFeature> featureCollection) {
		List<FeatureId> result = null;

		FeatureSource<? extends FeatureType, ? extends Feature> featureSource = mapLayer
				.getFeatureSource();
		if (featureCollection != null && featureSource != null
				&& featureSource instanceof FeatureStore<?, ?>) {
			// String transactionId = "insert|" + index + "|" +
			// layerId + "|" + featureCollection.hashCode();
			// DefaultTransaction transaction = new
			// DefaultTransaction(transactionId);
			FeatureStore<SimpleFeatureType, SimpleFeature> featureStore = (FeatureStore<SimpleFeatureType, SimpleFeature>) featureSource;
			// featureStore.setTransaction(transaction);
			try {
				result = featureStore.addFeatures(featureCollection);
				// transaction.commit();
			} catch (Exception ex) {
				// transaction.rollback();
			} finally {
				// transaction.close();
			}
		}

		return result;
	}

	@SuppressWarnings("unchecked")
	public HashMap<FeatureId, Boolean> update(
			FeatureCollection<SimpleFeatureType, SimpleFeature> featureCollection) {
		HashMap<FeatureId, Boolean> result = new HashMap<FeatureId, Boolean>();

		FeatureSource<? extends FeatureType, ? extends Feature> featureSource = mapLayer
				.getFeatureSource();
		if (featureCollection != null && featureSource != null
				&& featureSource instanceof FeatureStore<?, ?>) {
			// String transactionId = "insert|" + index + "|" +
			// layerId + "|" + featureCollection.hashCode();
			// DefaultTransaction transaction = new
			// DefaultTransaction(transactionId);
			FeatureStore<SimpleFeatureType, SimpleFeature> featureStore = (FeatureStore<SimpleFeatureType, SimpleFeature>) featureSource;
			// featureStore.setTransaction(transaction);
			FilterFactory2 ff = CommonFactoryFinder.getFilterFactory2(GeoTools
					.getDefaultHints());

			FeatureIterator<SimpleFeature> iterator = featureCollection.features();
			 try {
			     while( iterator.hasNext() ){
			    	 SimpleFeature feature = iterator.next();
			    	 FeatureId featureId = feature.getIdentifier();
						try {
							Set<FeatureId> fids = new HashSet<FeatureId>();
							fids.add(featureId);
							Filter filter = ff.id(fids);
							featureStore.modifyFeatures(feature.getDescriptor(),
									feature.getAttributes().toArray(), filter);
							// transaction.commit();

							result.put(featureId, true);
						} catch (Exception ex) {
							// transaction.rollback();
							result.put(featureId, false);
						} finally {
							// transaction.close();
						}
			     }
			 }
			 finally {
			     iterator.close();
			 }
		}

		return result;
	}

	public boolean delete(QueryParam queryParam) throws Exception {
		boolean result = false;

		FeatureSource<? extends FeatureType, ? extends Feature> featureSource = mapLayer
				.getFeatureSource();
		if (queryParam != null && featureSource != null
				&& featureSource instanceof FeatureStore<?, ?>) {
			// String transactionId = "insert|" + index + "|" +
			// layerId + "|" + featureCollection.hashCode();
			// DefaultTransaction transaction = new
			// DefaultTransaction(transactionId);
			FeatureStore<? extends FeatureType, ? extends Feature> featureStore = (FeatureStore<? extends FeatureType, ? extends Feature>) featureSource;
			try {
				String geomtryField = featureSource.getSchema()
						.getGeometryDescriptor().getLocalName();
				Filter geometryFilter = generateGeometryFilter(geomtryField,
						queryParam);
				Filter attributesFilter = generateAttributesFilter(queryParam);

				Filter filter = null;
				if (geometryFilter != null && attributesFilter == null) {
					filter = geometryFilter;
				} else if (geometryFilter == null && attributesFilter != null) {
					filter = attributesFilter;
				} else if (geometryFilter == null && attributesFilter == null) {
					// Do nothing
				} else {
					FilterFactory2 ff = CommonFactoryFinder
							.getFilterFactory2(GeoTools.getDefaultHints());
					filter = ff.and(geometryFilter, attributesFilter);
				}

				featureStore.removeFeatures(filter);
				result = true;
			} catch (Exception ex) {
				// transaction.rollback();
			} finally {
				// transaction.close();
			}
		}

		return result;
	}

	public boolean delete(String[] featureIds) throws Exception {
		boolean result = false;

		FeatureSource<? extends FeatureType, ? extends Feature> featureSource = mapLayer
				.getFeatureSource();

		if (featureIds != null && featureSource != null
				&& featureSource instanceof FeatureStore<?, ?>) {
			// String transactionId = "insert|" + index + "|" +
			// layerId + "|" + featureCollection.hashCode();
			// DefaultTransaction transaction = new
			// DefaultTransaction(transactionId);
			FeatureStore<? extends FeatureType, ? extends Feature> featureStore = (FeatureStore<? extends FeatureType, ? extends Feature>) featureSource;
			try {
				String featureTypeName = featureSource.getSchema().getName()
						.getLocalPart();
				Filter idFilter = generateIdFilter(featureTypeName, featureIds);

				featureStore.removeFeatures(idFilter);
				result = true;
			} catch (Exception ex) {
				// transaction.rollback();
			} finally {
				// transaction.close();
			}
		}

		return result;
	}

}

/** @文件名: WebMapServiceImpl.java @创建人：邢健  @创建日期： 2014-1-22 上午10:20:00 */

package com.pbgis.server.wms;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.geotools.data.DefaultQuery;
import org.geotools.data.Query;
import org.geotools.filter.text.ecql.ECQL;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.map.MapLayer;
import org.opengis.filter.Filter;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import com.pbgis.engine.carto.MapImage;
import com.pbgis.engine.carto.MapUtil;
import com.pbgis.engine.carto.RenderParam;
import com.pbgis.engine.geometry.GeometryToolkit;
import com.pbgis.engine.spatialreference.SpatialReferenceManager;
import com.sun.media.jai.codec.PNGEncodeParam;
import com.sun.media.jai.codecimpl.PNGImageEncoder;
import com.vividsolutions.jts.geom.Envelope;


/**   
 * @类名: WebMapServiceImpl.java 
 * @包名: com.pbgis.server.wms 
 * @描述: TODO 
 * @作者: xingjian xingjian@yeah.net   
 * @日期:2014-1-22 上午10:20:00 
 * @版本: V1.0   
 */
public class WebMapServiceImpl implements WebMapService {

	@Override
	public void getCapabilities() {
		
	}

	@Override
	public byte[] getMap(GetMapRequest wmsRequest) {
		byte[] result = null;
//		int dpi = 96;
//		String format = "png";
//		ByteArrayOutputStream outputStream = null;
//		try {
//			String lowFormat = format.toLowerCase();
//			boolean supportTransparent = false;
//			if (lowFormat.startsWith("png")) {
//				supportTransparent = true;
//			}
//			// Handle size
//			int width = wmsRequest.getWidth();
//			int height = wmsRequest.getHeight();
//			// Handle bbox
//			double xmin = 0, ymin = 0, xmax = 0, ymax = 0;
//			String bbox = wmsRequest.getBbox();
//			if (bbox != null && bbox.indexOf(",") > 0) {
//				String[] bboxs = bbox.split(",");
//				if (bbox.length() > 3) {
//					xmin = Double.parseDouble(bboxs[0]);
//					ymin = Double.parseDouble(bboxs[1]);
//					xmax = Double.parseDouble(bboxs[2]);
//					ymax = Double.parseDouble(bboxs[3]);
//				}
//			}
//			if ((xmax - xmin) * (ymax - ymin) == 0) {
//				Envelope extent = mapDesc.getInitialExtent();
//				xmin = extent.getMinX();
//				xmax = extent.getMaxX();
//				ymin = extent.getMinY();
//				ymax = extent.getMaxY();
//			}
//			CoordinateReferenceSystem bboxCRS = SpatialReferenceManager.wkidToCRS("", true);
//			ReferencedEnvelope env = new ReferencedEnvelope(xmin, xmax, ymin, ymax, bboxCRS);
//			env = GeometryToolkit.project(env, imageSR);
//			env = MapUtil.adjustEnvelopeToSize(env,
//					width, height);
//			RenderParam renderParam = new RenderParam();
//			renderParam.setAntiAlias(mapDesc.isAntiAlias());
//			renderParam.setExtent(env);
//			renderParam.setImageHeight(height);
//			renderParam.setImageWidth(width);
//			renderParam.setLayerDefs(mapLayerDefs);
//			renderParam.setTransparent(Boolean.parseBoolean(transparent));
//			renderParam.setVisibleLayerIds(layerIds);
//			RenderParam fixedRenderParam = renderParam.clone();
//			fixedRenderParam.setTransparent(renderParam.isTransparent()&& supportTransparent);
//			BufferedImage image = this.render(fixedRenderParam);
//			outputStream = new ByteArrayOutputStream();
//			if (lowFormat.startsWith("png")) {
//				PNGEncodeParam pngParam = PNGEncodeParam.getDefaultEncodeParam(image);
//				pngParam.setPhysicalDimension(dpi * 3937, dpi * 3937, 100);
//				PNGImageEncoder pngEncoder = new PNGImageEncoder(outputStream,pngParam);
//				pngEncoder.encode(image);
//			}
//			result = outputStream.toByteArray();
//		} catch (Exception e) {
//			
//		} finally {
//			try {
//				outputStream.close();
//			} catch (Exception ex) {
//			}
//		}
		return result;
	}

	private BufferedImage render(RenderParam renderParam) throws Exception {
		BufferedImage image = null;
//		try {
//			int layerCount = map.getMapContext().getLayerCount();
//			for (int i = 0; i < layerCount; i++) {
//				MapLayer layer = map.getLayer(i).getMapLayer();
//				layer.setVisible(false);
//			}
//			List<String> visibleLayerIds = renderParam.getVisibleLayerIds();
//			HashMap<String, String> layerDefs = renderParam.getLayerDefs();
//			if (visibleLayerIds == null) {
//				visibleLayerIds = new ArrayList<String>();
//				for (int i = 0; i < layerCount; i++) {
//					visibleLayerIds.add(String.valueOf(i));
//				}
//			}
//			for (int i = 0, count = visibleLayerIds.size(); i < count; i++) {
//				int layerId = Integer.parseInt(visibleLayerIds.get(i));
//				MapLayer layer = map.getLayer(i).getMapLayer();
//				layer.setVisible(true);
//
//				// Handle layerDefs
//				String key = String.valueOf(layerId);
//				if (layerDefs != null && layerDefs.containsKey(key)) {
//					String def = layerDefs.get(key);
//					if (def != null && !"".equals(def)) {
//						try {
//							Filter filter = ECQL.toFilter(def);
//							DefaultQuery query = new DefaultQuery();
//							query.setFilter(filter);
//							layer.setQuery(query);
//						} catch (Exception e) {
//							
//						}
//					} else {
//						layer.setQuery(Query.ALL);
//					}
//				}
//			}
//
//			MapImage mapImage = map.render(renderParam);
//			image = mapImage.getImage();
//		} catch (Exception e) {
//			
//		}

		return image;
	}
	
	
	@Override
	public void getFeatureInfo() {
		
	}

	

}

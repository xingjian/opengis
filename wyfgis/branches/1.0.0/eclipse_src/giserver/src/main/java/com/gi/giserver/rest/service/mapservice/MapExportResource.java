package com.gi.giserver.rest.service.mapservice;

import java.io.File;
import java.io.FileOutputStream;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.HashMap;

import javax.servlet.ServletContext;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;

import org.geotools.geometry.jts.ReferencedEnvelope;
import org.json.JSONObject;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import sun.misc.BASE64Encoder;

import com.gi.giengine.geometry.ProjectEngine;
import com.gi.giengine.map.desc.ExtentDesc;
import com.gi.giengine.map.desc.LayerDesc;
import com.gi.giengine.map.desc.MapDesc;
import com.gi.giengine.map.param.RenderParam;
import com.gi.giengine.map.util.MapUtil;
import com.gi.giengine.sr.SpatialReferenceEngine;
import com.gi.giengine.util.PathUtil;
import com.gi.giserver.core.config.ConfigManager;
import com.gi.giserver.core.service.ServiceManager;
import com.gi.giserver.core.service.mapservice.MapService;
import com.gi.giserver.core.service.mapservice.desc.MapServiceDesc;
import com.gi.giserver.core.service.tokenservice.TokenService;
import com.gi.giserver.core.util.MapServiceUtil;
import com.gi.giserver.core.util.error.ServiceError;
import com.gi.giserver.core.util.json.EsriJsonUtil;
import com.gi.giserver.rest.html.ServiceHTML;
import com.vividsolutions.jts.geom.Envelope;

/**
 * @author Wu Yongfeng
 * 
 */
@Path("/MapService/{mapName}/export")
public class MapExportResource {

	@Context
	ServletContext context;

	@GET
	public byte[] getResult(@PathParam("mapName") String mapName, @QueryParam("token") String token,
			@QueryParam("f") @DefaultValue("html") String f, @QueryParam("bbox") String bbox,
			@QueryParam("size") @DefaultValue("400,400") String size,
			@QueryParam("dpi") @DefaultValue("72") String dpi, @QueryParam("imageSR") String imageSR,
			@QueryParam("bboxSR") String bboxSR, @QueryParam("format") @DefaultValue("png") String format,
			@QueryParam("layerDefs") String layerDefs, @QueryParam("layers") String layers,
			@QueryParam("transparent") @DefaultValue("false") String transparent) {
		return result(mapName, token, f, bbox, size, dpi, imageSR, bboxSR, format, layerDefs, layers, transparent);
	}

	@POST
	public byte[] postResult(@PathParam("mapName") String mapName, @FormParam("token") String token,
			@FormParam("f") @DefaultValue("html") String f, @FormParam("bbox") String bbox,
			@FormParam("size") @DefaultValue("400,400") String size, @FormParam("dpi") @DefaultValue("72") String dpi,
			@FormParam("imageSR") String imageSR, @FormParam("bboxSR") String bboxSR,
			@FormParam("format") @DefaultValue("png") String format, @FormParam("layerDefs") String layerDefs,
			@FormParam("layers") String layers, @FormParam("transparent") @DefaultValue("false") String transparent) {
		return result(mapName, token, f, bbox, size, dpi, imageSR, bboxSR, format, layerDefs, layers, transparent);
	}

	private byte[] result(String mapName, String token, String f, String bbox, String size, String dpi, String imageSR,
			String bboxSR, String format, String layerDefs, String layers, String transparent) {
		byte[] result = null;

		try {
			MapService mapService = ServiceManager.getMapService(mapName);

			if (mapService != null) {
				MapDesc mapDesc = mapService.getMapDesc();
				MapServiceDesc mapServiceDesc = mapService.getMapServiceDesc();
				if (mapServiceDesc.isNeedToken()) {
					if (!TokenService.verifyToken(token) && !("html".equals(f) && bbox==null)) {
						return TokenService.TOKEN_INVALID_TIP.getBytes();
					}
				}

				// Handle SRs
				String sr = mapDesc.getWkid();
				if (bboxSR == null || "".equals(bboxSR)) {
					bboxSR = sr;
				}
				if (imageSR == null || "".equals(imageSR)) {
					imageSR = sr;
				}

				// Handle size
				String[] sizes = size.split(",");
				int width = Integer.parseInt(sizes[0]);
				int height = Integer.parseInt(sizes[1]);

				// Handle bbox
				double xmin = 0, ymin = 0, xmax = 0, ymax = 0;
				if (bbox != null && bbox.indexOf(",") > 0) {
					String[] bboxs = bbox.split(",");
					if (bbox.length() > 3) {
						xmin = Double.parseDouble(bboxs[0]);
						ymin = Double.parseDouble(bboxs[1]);
						xmax = Double.parseDouble(bboxs[2]);
						ymax = Double.parseDouble(bboxs[3]);
					}
				}
				if ((xmax - xmin) * (ymax - ymin) == 0) {
					ExtentDesc extentDesc = mapDesc.getInitialExtentDesc();
					xmin = extentDesc.getXmin();
					xmax = extentDesc.getXmax();
					ymin = extentDesc.getYmin();
					ymax = extentDesc.getYmax();
				}
				CoordinateReferenceSystem bboxCRS = SpatialReferenceEngine.wkidToCRS(bboxSR, true);
				ReferencedEnvelope env = new ReferencedEnvelope(xmin, xmax, ymin, ymax, bboxCRS);
				env = ProjectEngine.projectReferencedEnvelope(env, imageSR);
				ReferencedEnvelope mapArea = MapUtil.adjustEnvelopeToSize(env, width, height);

				// Handle dpi
				int nDPI = Integer.parseInt(dpi);

				// Handle layerDefs
				HashMap<String, String> mapLayerDefs = null;
				if (layerDefs != null && !"".equals(layerDefs)) {
					try {
						mapLayerDefs = new HashMap<String, String>();
						String[] defs = layerDefs.split(";");
						for (int i = 0; i < defs.length; i++) {
							String[] def = defs[i].split(":");
							mapLayerDefs.put(def[0], def[1]);
						}
					} catch (Exception ex) {
						ex.printStackTrace();
					}
				}

				// Handle layers
				ArrayList<String> layerIds = new ArrayList<String>();
				if (layers != null && !"".equals(layers) && !"*".equals(layers)) {
					String[] strLayers = layers.split(",");
					int layerCount = strLayers.length;
					for (int i = 0; i < layerCount; i++) {
						layerIds.add(strLayers[i]);
					}
				} else {
					int layerCount = mapDesc.getLayerDescs().size();
					for (int i = 0; i < layerCount; i++) {
						LayerDesc layerDesc = mapDesc.getLayerDesc(i);
						if (layerDesc.isVisible()) {
							layerIds.add(String.valueOf(i));
						}
					}
				}

				// Handle export
				RenderParam renderParam = new RenderParam();
				renderParam.setAntiAlias(mapDesc.isAntiAlias());
				renderParam.setBbox(new ExtentDesc (xmin, xmax, ymin, ymax));
				renderParam.setBboxSR(bboxSR);
				renderParam.setImageHeight(height);
				renderParam.setImageWidth(width);
				renderParam.setImageSR(imageSR);
				renderParam.setLayerDefs(mapLayerDefs);
				renderParam.setTransparent(Boolean.parseBoolean(transparent));
				renderParam.setVisibleLayerIds(layerIds);
				byte[] image = mapService.export(renderParam, format, nDPI);

				// Various out format
				if ("json".equals(f)) {
					result = this.generateJSONResult(mapName, image, format, mapArea, width, height, bboxSR);
				} else if ("html".equals(f)) {
					result = this.generateHTMLResult(token, mapName, image, format, mapArea, xmin, ymin, xmax, ymax,
							width, height, bboxSR, layers, layerDefs, size, imageSR);
				} else if ("image".equals(f)) {
					result = this.generateImageResult(image);
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return result;
	}

	private byte[] generateJSONResult(String mapName, byte[] image, String format, Envelope env, int width, int height,
			String bboxSR) {
		byte[] result = null;

		try {
			JSONObject obj = new JSONObject();
			JSONObject objSR = null;
			JSONObject objTemp = null;

			obj.put("about", "Powered by GIServer");
			if (image == null) {
				ArrayList<String> details = new ArrayList<String>();
				details.add("Export result is NULL.");
				obj.put("error", EsriJsonUtil.generateJSONError(ServiceError.MAP_EXPORT_ERROR, details));
			} else {
				String imageURL = writeToFile(mapName, image, format);
				obj.put("href", imageURL);
				obj.put("width", width);
				obj.put("height", height);
				objTemp = new JSONObject();
				objTemp.put("xmin", env.getMinX());
				objTemp.put("xmax", env.getMaxX());
				objTemp.put("ymin", env.getMinY());
				objTemp.put("ymax", env.getMaxY());
				objSR = new JSONObject();
				objSR.put("wkid", bboxSR);
				objTemp.put("spatialReference", objSR);
				obj.put("extent", objTemp);
			}

			result = obj.toString().getBytes();
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return result;
	}

	private byte[] generateHTMLResult(String token, String mapName, byte[] image, String format, Envelope env,
			double xmin, double ymin, double xmax, double ymax, int width, int height, String bboxSR, String layers,
			String layerDefs, String size, String imageSR) {
		byte[] result = null;

		try {
			String imageURL = writeToFile(mapName, image, format);

			String contextRoot = context.getContextPath();

			String strResult = "";

			String restBody = "";
			restBody += "<table cellspacing='5'><tr valign='top'>";
			restBody += "<td><img style='border:2px solid #000000;' src='" + imageURL + "' /></td>";
			restBody += "<td>Width: " + width + "<br/>Height: " + height + "<br/>Extent: <ul> xmin: " + env.getMinX()
					+ "<br/>xmax: " + env.getMaxX() + "<br/>ymin: " + env.getMinY() + "<br/>ymax: " + env.getMaxY()
					+ "<br/>Spatial Reference (WKID): " + imageSR + "<br/></ul>";
			restBody += "</tr></table>";

			restBody += "<form><table style='border:1px solid #000000;'>";
			restBody += "<tr><td>Token:</td><td><input size='50' type='text' name='token' value='${TOKEN}' /></td></tr>";
			restBody += "<tr valign='top'><td>Bounding Box:</td><td><input type='text' name='bbox' value='${BBOX}' size='75'/></td></tr>";
			restBody += "<tr valign='top'><td>Bounding Box Spatial Reference (WKID):</td><td><input type='text' name='bboxSR' value='${BBOX_SR}' /></td></tr>";
			restBody += "<tr valign='top'><td>Layers:</td><td><input type='text' name='layers' value='${LAYERS}' /></td></tr>";
			restBody += "<tr valign='top'><td>Layer Definitions:</td><td><input type='text' name='layerDefs' value='${LAYER_DEFS}' size='75' /></td></tr>";
			restBody += "<tr valign='top'><td>Image Size:</td><td><input type='text' name='size' value='${SIZE}' /></td></tr>";
			restBody += "<tr valign='top'><td>Image Spatial Reference (WKID):</td><td><input type='text' name='imageSR' value='${IMAGE_SR}' /></td></tr>";
			restBody += "<tr valign='top'><td>Image Format:</td><td><select name='format'><option value='png' >png</option><option value='jpg' >jpg</option><option value='gif' >gif</option><option value='bmp' >bmp</option></select></td></tr>";
			restBody += "<tr><td>Background Transparent:</td><td><input type='radio' name='transparent' value='true' /> Yes &nbsp;<input type='radio' name='transparent' value='false' checked='true'  /> No</td></tr>";
			restBody += "<tr><td>Format:</td><td><select name='f'><option value='html' >HTML</option><option value='image' >Image</option><option value='json' >JSON</option></select></td></tr>";
			restBody += "<tr><td colspan='2' align='left'><input type='submit' value='Export Map Image' /></td></tr>";
			restBody += "</table></form>";

			restBody = restBody.replace("${TOKEN}", token == null ? "" : token);
			restBody = restBody.replace("${BBOX}", "" + xmin + "," + ymin + "," + xmax + "," + ymax);
			restBody = restBody.replace("${BBOX_SR}", bboxSR == null ? "" : bboxSR);
			restBody = restBody.replace("${LAYERS}", layers == null ? "" : layers);
			restBody = restBody.replace("${LAYER_DEFS}", layerDefs == null ? "" : layerDefs);
			restBody = restBody.replace("${SIZE}", size == null ? "" : size);
			restBody = restBody.replace("${IMAGE_SR}", imageSR == null ? "" : imageSR);

			strResult += ServiceHTML.getHeader(contextRoot).replace("${TITLE}", "Export");
			strResult += ServiceHTML.getNav(contextRoot)
					.replace(
							"${CATALOG}",
							" &gt; <a href='" + contextRoot
									+ "/rest/service/MapService'>Map Service</a> &gt; <a href='" + contextRoot
									+ "/rest/service/MapService/" + mapName + "'>" + mapName + "</a> &gt; Export");
			strResult += ServiceHTML.getH2().replace("${TITLE}", "Export");
			strResult += ServiceHTML.getRestBody().replace("${RESTBODY}", restBody);
			strResult += ServiceHTML.getFooter();

			result = strResult.getBytes();
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return result;
	}

	private byte[] generateImageResult(byte[] image) {
		byte[] result = null;

		try {
			result = image;
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return result;
	}

	private String writeToFile(String mapName, byte[] image, String format) {
		String result = null;

		try {
			String contextRoot = context.getContextPath();

			String lowFormat = format.toLowerCase();
			String suffix = lowFormat;
			if ("jpg".equals(lowFormat) || "jpeg".equals(lowFormat)) {
				suffix = "jpg";
			} else if ("gif".equals(lowFormat)) {
				suffix = "gif";
			} else if ("bmp".equals(lowFormat)) {
				suffix = "bmp";
			}

			MessageDigest md = MessageDigest.getInstance("MD5");
			BASE64Encoder encoder = new BASE64Encoder();
			String fileName = encoder.encode(md.digest(image)) + "." + suffix;
			fileName = PathUtil.fakePath2Real(fileName);
			fileName = fileName.replace(File.separator, "WU");

			String fakeDirPath = MapServiceUtil.getMapDir(mapName) + "output/";
			String dirPath = PathUtil.fakePath2Real(fakeDirPath);
			String fakeFilePath = fakeDirPath + fileName;
			String filePath = PathUtil.fakePath2Real(fakeFilePath);
			File dirFile = new File(dirPath);
			if (!dirFile.exists()) {
				dirFile.mkdirs();
			}
			boolean isGetOutput = false;
			File fileFile = new File(filePath);
			if (fileFile.exists()) {
				isGetOutput = true;
			} else {
				if (fileFile.createNewFile()) {
					FileOutputStream outputStream = new FileOutputStream(fileFile);
					outputStream.write(image);
					outputStream.flush();
					outputStream.close();
					isGetOutput = true;
				}
			}
			if (isGetOutput) {
				result = ConfigManager.getServerConfig().getWebRoot() + contextRoot + "/rest/service/MapService/"
						+ mapName + "/output/" + fileName;
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return result;
	}

}

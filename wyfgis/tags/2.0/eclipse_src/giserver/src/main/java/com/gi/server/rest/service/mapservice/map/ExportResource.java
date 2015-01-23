package com.gi.server.rest.service.mapservice.map;

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
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import org.geotools.geometry.jts.ReferencedEnvelope;
import org.json.JSONObject;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import sun.misc.BASE64Encoder;

import com.gi.engine.carto.LayerInfo;
import com.gi.engine.carto.MapDesc;
import com.gi.engine.carto.MapUtil;
import com.gi.engine.carto.RenderParam;
import com.gi.engine.geometry.GeometryToolkit;
import com.gi.engine.server.service.MapServiceDesc;
import com.gi.engine.spatialreference.SpatialReferenceManager;
import com.gi.engine.util.VersionUtil;
import com.gi.engine.util.common.PathUtil;
import com.gi.server.core.config.ConfigManager;
import com.gi.server.core.service.ServiceManager;
import com.gi.server.core.service.mapservice.MapService;
import com.gi.server.core.service.mapservice.MapServiceInstance;
import com.gi.server.core.service.tokenservice.TokenService;
import com.gi.server.rest.html.ServiceHTML;
import com.vividsolutions.jts.geom.Envelope;

/**
 * @author wuyf
 * 
 */
@Path("/MapService/{serviceName}/export")
public class ExportResource {

	@Context
	ServletContext context;

	@GET
	@Produces( { "image/png", "image/jpeg", "image/gif", "image/bmp",
			MediaType.TEXT_HTML, MediaType.APPLICATION_JSON })
	public byte[] getResult(@PathParam("serviceName") String serviceName,
			@QueryParam("token") String token,
			@QueryParam("f") @DefaultValue("html") String f,
			@QueryParam("bbox") String bbox,
			@QueryParam("size") @DefaultValue("400,400") String size,
			@QueryParam("dpi") @DefaultValue("96") String dpi,
			@QueryParam("imageSR") String imageSR,
			@QueryParam("bboxSR") String bboxSR,
			@QueryParam("format") @DefaultValue("png") String format,
			@QueryParam("layerDefs") String layerDefs,
			@QueryParam("layers") String layers,
			@QueryParam("transparent") @DefaultValue("false") String transparent) {
		return result(serviceName, token, f, bbox, size, dpi, imageSR, bboxSR,
				format, layerDefs, layers, transparent);
	}

	@POST
	@Produces( { "image/png", "image/jpeg", "image/gif", "image/bmp",
			MediaType.TEXT_HTML, MediaType.APPLICATION_JSON })
	public byte[] postResult(@PathParam("serviceName") String serviceName,
			@FormParam("token") String token,
			@FormParam("f") @DefaultValue("html") String f,
			@FormParam("bbox") String bbox,
			@FormParam("size") @DefaultValue("400,400") String size,
			@FormParam("dpi") @DefaultValue("96") String dpi,
			@FormParam("imageSR") String imageSR,
			@FormParam("bboxSR") String bboxSR,
			@FormParam("format") @DefaultValue("png") String format,
			@FormParam("layerDefs") String layerDefs,
			@FormParam("layers") String layers,
			@FormParam("transparent") @DefaultValue("false") String transparent) {
		return result(serviceName, token, f, bbox, size, dpi, imageSR, bboxSR,
				format, layerDefs, layers, transparent);
	}

	private byte[] result(String serviceName, String token, String f,
			String bbox, String size, String dpi, String imageSR,
			String bboxSR, String format, String layerDefs, String layers,
			String transparent) {
		byte[] result = null;

		try {
			MapService mapService = ServiceManager.getMapService(serviceName);

			if (mapService != null) {
				MapDesc mapDesc = mapService.getMapDesc();
				MapServiceDesc mapServiceDesc = mapService.getMapServiceDesc();
				if (mapServiceDesc.isNeedToken()) {
					if (!TokenService.verifyToken(token)
							&& !("html".equals(f) && bbox == null)) {
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
					Envelope extent = mapDesc.getInitialExtent();
					xmin = extent.getMinX();
					xmax = extent.getMaxX();
					ymin = extent.getMinY();
					ymax = extent.getMaxY();
				}
				CoordinateReferenceSystem bboxCRS = SpatialReferenceManager
						.wkidToCRS(bboxSR, true);
				ReferencedEnvelope env = new ReferencedEnvelope(xmin, xmax,
						ymin, ymax, bboxCRS);
				env = GeometryToolkit.project(env, imageSR);
				ReferencedEnvelope mapArea = MapUtil.adjustEnvelopeToSize(env,
						width, height);

				// Handle dpi
				int nDPI = Integer.parseInt(dpi);

				// Handle layerDefs
				HashMap<String, String> mapLayerDefs = null;
				if (layerDefs != null && !"".equals(layerDefs)) {
					try {
						mapLayerDefs = new HashMap<String, String>();
						String[] defs = layerDefs.split(",");
						for (int i = 0; i < defs.length; i++) {
							String def = defs[i];
							if (def.contains(":")) {
								String[] deff = def.split(":");
								if (deff.length > 1) {
									mapLayerDefs.put(deff[0], deff[1]);
								}
							} else {
								mapLayerDefs.put(String.valueOf(i), def);
							}
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
					for (int i = 0, count = mapDesc.getLayerInfos().size(); i < count; i++) {
						LayerInfo layerInfo = mapDesc.getLayerInfo(i);
						if (layerInfo.isVisible()) {
							layerIds.add(String.valueOf(i));
						}
					}
				}

				// Handle export
				RenderParam renderParam = new RenderParam();
				renderParam.setAntiAlias(mapDesc.isAntiAlias());
				renderParam.setExtent(env);
				renderParam.setImageHeight(height);
				renderParam.setImageWidth(width);
				renderParam.setLayerDefs(mapLayerDefs);
				renderParam.setTransparent(Boolean.parseBoolean(transparent));
				renderParam.setVisibleLayerIds(layerIds);

				byte[] image = null;
				double scale = 0;
				MapServiceInstance instance = (MapServiceInstance) mapService
						.getMapServicePool().checkoutIdleInstance();
				try {
					image = instance.export(renderParam, format, nDPI);
					scale = instance.getMap().computeScale(env, width, height,
							nDPI);
				} finally {
					if (instance != null) {
						mapService.getMapServicePool().checkinIdelInstance(
								instance);
					}
				}

				// Various out format
				if ("json".equals(f)) {
					result = this.generateJSONResult(serviceName, image, scale,
							format, mapArea, width, height, bboxSR);
				} else if ("html".equals(f)) {
					result = this.generateHTMLResult(token, serviceName, image,
							scale, format, mapArea, xmin, ymin, xmax, ymax,
							width, height, bboxSR, layers, layerDefs, size,
							imageSR);
				} else if ("image".equals(f)) {
					result = this.generateImageResult(image);
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return result;
	}

	private byte[] generateJSONResult(String serviceName, byte[] image,
			double scale, String format, Envelope env, int width, int height,
			String bboxSR) {
		byte[] result = null;

		try {
			JSONObject obj = new JSONObject();
			JSONObject objSR = null;
			JSONObject objTemp = null;

			obj.put("GIServerVersion", VersionUtil.getCurrentversion());
			if (image == null) {
				ArrayList<String> details = new ArrayList<String>();
				details.add("Export result is NULL.");
			} else {
				String imageURL = writeToFile(serviceName, image, format);
				obj.put("href", imageURL);
				obj.put("width", width);
				obj.put("height", height);
				obj.put("scale", scale);
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

	private byte[] generateHTMLResult(String token, String serviceName,
			byte[] image, double scale, String format, Envelope env,
			double xmin, double ymin, double xmax, double ymax, int width,
			int height, String bboxSR, String layers, String layerDefs,
			String size, String imageSR) {
		byte[] result = null;

		try {
			String imageURL = writeToFile(serviceName, image, format);

			String contextRoot = context.getContextPath();

			String strResult = "";

			StringBuilder sb = new StringBuilder();

			sb.append("<table cellspacing='5'><tr valign='top'>");
			sb.append("<td><img style='border:2px solid #000000;' src='"
					+ imageURL + "' /></td>");
			sb.append("<td>Width: " + width + "<br/>Height: " + height
					+ "<br/>Extent: <ul> xmin: " + env.getMinX()
					+ "<br/>xmax: " + env.getMaxX() + "<br/>ymin: "
					+ env.getMinY() + "<br/>ymax: " + env.getMaxY()
					+ "<br/>Spatial Reference (WKID): " + imageSR
					+ "<br/></ul><br/>Scale:" + scale + "</td>");
			sb.append("</tr></table>");

			sb.append("<form action='" + contextRoot
					+ "/rest/service/MapService/" + serviceName
					+ "/export'><table class='formTable'>");
			sb
					.append("<tr><td>Token:</td><td><input size='50' type='text' name='token' value='${TOKEN}' /></td></tr>");
			sb
					.append("<tr valign='top'><td>Bounding Box:</td><td><input type='text' name='bbox' value='${BBOX}' size='75'/></td></tr>");
			sb
					.append("<tr valign='top'><td>Bounding Box Spatial Reference (WKID):</td><td><input type='text' name='bboxSR' value='${BBOX_SR}' /></td></tr>");
			sb
					.append("<tr valign='top'><td>Layers:</td><td><input type='text' name='layers' value='${LAYERS}' /></td></tr>");
			sb
					.append("<tr valign='top'><td>Layer Definitions:</td><td><input type='text' name='layerDefs' value='${LAYER_DEFS}' size='75' /></td></tr>");
			sb
					.append("<tr valign='top'><td>Image Size:</td><td><input type='text' name='size' value='${SIZE}' /></td></tr>");
			sb
					.append("<tr valign='top'><td>Image Spatial Reference (WKID):</td><td><input type='text' name='imageSR' value='${IMAGE_SR}' /></td></tr>");
			sb
					.append("<tr valign='top'><td>Image Format:</td><td><select name='format'><option value='png' >png</option><option value='jpg' >jpg</option><option value='gif' >gif</option><option value='bmp' >bmp</option></select></td></tr>");
			sb
					.append("<tr><td>Background Transparent:</td><td><input type='radio' name='transparent' value='true' /> Yes &nbsp;<input type='radio' name='transparent' value='false' checked='true'  /> No</td></tr>");
			sb
					.append("<tr><td>Format:</td><td><select name='f'><option value='html' >HTML</option><option value='image' >Image</option><option value='json' >JSON</option></select></td></tr>");
			sb
					.append("<tr><td colspan='2' align='left'><input type='submit' value='Export Map Image (GET)' /><input type='submit' onclick='this.form.method = 'post';' value='Export Map Image (POST)' /></td></tr>");
			sb.append("</table></form>");

			String restBody = sb.toString();

			restBody = restBody.replace("${TOKEN}", token == null ? "" : token);
			restBody = restBody.replace("${BBOX}", "" + xmin + "," + ymin + ","
					+ xmax + "," + ymax);
			restBody = restBody.replace("${BBOX_SR}", bboxSR == null ? ""
					: bboxSR);
			restBody = restBody.replace("${LAYERS}", layers == null ? ""
					: layers);
			restBody = restBody.replace("${LAYER_DEFS}", layerDefs == null ? ""
					: layerDefs);
			restBody = restBody.replace("${SIZE}", size == null ? "" : size);
			restBody = restBody.replace("${IMAGE_SR}", imageSR == null ? ""
					: imageSR);

			ServiceHTML html = new ServiceHTML();
			html.setContextRoot(contextRoot);
			html.setTitle("Export (" + serviceName + "}");
			html
					.setCatalog(" &gt; <a href='"
							+ contextRoot
							+ "/rest/service/MapService'>Map Service</a> &gt; <a href='"
							+ contextRoot + "/rest/service/MapService/"
							+ serviceName + "'>" + serviceName
							+ "</a> &gt; Export");
			html.setHeader("Export Map Image");
			html.setRestBody(restBody);
			strResult = html.toString();

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

	private String writeToFile(String serviceName, byte[] image, String format) {
		String result = null;

		try {
			String contextRoot = context.getContextPath();

			String lowFormat = format.toLowerCase();
			String suffix = lowFormat;
			if (lowFormat.startsWith("png")) {
				suffix = "png";
			} else if ("gif".equals(lowFormat)) {
				suffix = "gif";
			} else if ("bmp".equals(lowFormat)) {
				suffix = "bmp";
			} else if ("jpg".equals(lowFormat) || "jpeg".equals(lowFormat)) {
				suffix = "jpg";
			}

			MessageDigest md = MessageDigest.getInstance("MD5");
			BASE64Encoder encoder = new BASE64Encoder();
			String fileName = encoder.encode(md.digest(image)) + "." + suffix;
			fileName = PathUtil.fakePathToReal(fileName);// Some String may
			// contains "/" or
			// "\"
			fileName = fileName.replaceAll("\\" + File.separator, "_");

			String fakeDirPath = ServiceManager.getMapServicesDir()
					.getAbsolutePath()
					+ File.separator
					+ PathUtil.nameToRealPath(serviceName)
					+ File.separator + "output" + File.separator;
			String dirPath = PathUtil.fakePathToReal(fakeDirPath);
			String fakeFilePath = fakeDirPath + fileName;
			String filePath = PathUtil.fakePathToReal(fakeFilePath);
			File dirFile = new File(dirPath);
			if (!dirFile.exists()) {
				dirFile.mkdirs();
			}
			boolean outputExists = false;
			File fileFile = new File(filePath);
			if (fileFile.exists()) {
				outputExists = true;
			} else {
				if (fileFile.createNewFile()) {
					FileOutputStream outputStream = new FileOutputStream(
							fileFile);
					outputStream.write(image);
					outputStream.flush();
					outputStream.close();
					outputExists = true;
				}
			}
			if (outputExists) {
				result = ConfigManager.getServerConfig().getWebRoot()
						+ contextRoot + "/rest/service/MapService/"
						+ serviceName + "/output/" + fileName;
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return result;
	}

}

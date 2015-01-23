package com.gi.giserver.rest.service.mapservice;

import java.io.File;
import java.io.FileInputStream;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

import com.gi.giserver.core.util.MapServiceUtil;

/**
 * @author Wu Yongfeng
 * 
 */
@Path("/MapService/{mapName}/output/{fileName}")
public class MapOutputResource {

	@GET
	public byte[] getResult(@PathParam("mapName") String mapName, @PathParam("fileName") String fileName) {
		return result(mapName, fileName);
	}

	private synchronized byte[] result(String mapName, String fileName) {
		byte[] result = null;

		try {
			String outputDir = MapServiceUtil.getMapDir(mapName) + "output" + File.separator;
			File dirFile = new File(outputDir);
			if (!dirFile.exists()) {
				dirFile.mkdirs();
			}

			String filePath = outputDir + fileName;
			File fileFile = new File(filePath);
			if (fileFile.exists()) {
				FileInputStream stream = new FileInputStream(fileFile);
				result = new byte[(int) fileFile.length()];
				stream.read(result);
				stream.close();
				fileFile.deleteOnExit();
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return result;
	}
}

package com.gi.server.rest.service.mapservice.map;

import java.io.File;
import java.io.FileInputStream;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

import com.gi.engine.util.common.PathUtil;
import com.gi.server.core.service.ServiceManager;


/**
 * @author wuyf
 *
 */
@Path("/MapService/{serviceName}/output/{fileName}")
public class OutputResource {

	@GET
	@Produces( { "image/png", "image/jpeg", "image/gif", "image/bmp" })
	public byte[] getResult(@PathParam("serviceName") String serviceName,
			@PathParam("fileName") String fileName) {
		return result(serviceName, fileName);
	}

	private byte[] result(String serviceName, String fileName) {
		byte[] result = null;

		try {
			String outputDir = ServiceManager.getMapServicesDir()
					.getAbsolutePath()
					+ File.separator
					+ PathUtil.nameToRealPath(serviceName)
					+ File.separator
					+ "output"
					+ File.separator;
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

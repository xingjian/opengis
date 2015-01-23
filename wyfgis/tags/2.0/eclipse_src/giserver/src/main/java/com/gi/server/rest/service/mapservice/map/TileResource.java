package com.gi.server.rest.service.mapservice.map;

import javax.servlet.ServletContext;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;

import com.gi.engine.server.service.MapServiceDesc;
import com.gi.server.core.service.ServiceManager;
import com.gi.server.core.service.mapservice.MapService;
import com.gi.server.core.service.mapservice.MapServiceInstance;
import com.gi.server.core.service.tokenservice.TokenService;

/**
 * @author wuyf
 * 
 */
@Path("/MapService/{serviceName}/tile/{level}/{row}/{col}")
public class TileResource {

	@Context
	ServletContext context;

	@GET
	@Produces( { "image/png", "image/jpeg", "image/gif", "image/bmp" })
	public byte[] getResult(@PathParam("serviceName") String serviceName,
			@PathParam("level") String level, @PathParam("row") String row,
			@PathParam("col") String col, @QueryParam("token") String token) {
		return result(serviceName, level, row, col, token);
	}

	@POST
	@Produces( { "image/png", "image/jpeg", "image/gif", "image/bmp" })
	public byte[] postResult(@PathParam("serviceName") String serviceName,
			@PathParam("level") String level, @PathParam("row") String row,
			@PathParam("col") String col, @FormParam("token") String token) {
		return result(serviceName, level, row, col, token);
	}

	private byte[] result(String serviceName, String level, String row,
			String col, String token) {
		byte[] result = null;

		try {
			MapService mapService = ServiceManager.getMapService(serviceName);

			if (mapService != null) {
				MapServiceDesc mapServiceDesc = mapService.getMapServiceDesc();
				if (mapServiceDesc.isNeedToken()) {
					if (!TokenService.verifyToken(token)) {
						return TokenService.TOKEN_INVALID_TIP.getBytes();
					}
				}

				// Handle level, row, col
				// col may contains suffix
				if (col.contains(".")) {
					col = col.substring(0, col.indexOf("."));
				}
				int nLevel = Integer.parseInt(level);
				int nRow = Integer.parseInt(row);
				int nCol = Integer.parseInt(col);

				// Handle export
				MapServiceInstance instance = (MapServiceInstance) mapService
						.getMapServicePool().checkoutIdleInstance();
				try {
					if (mapServiceDesc.getTileInfo().isCreateOnDemand()) {
						result = instance.getOrCreateTile(nLevel, nRow, nCol);
					} else {
						result = instance.getTile(nLevel, nRow, nCol);
					}
				} finally {
					if (instance != null) {
						mapService.getMapServicePool().checkinIdelInstance(
								instance);
					}
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return result;
	}

}

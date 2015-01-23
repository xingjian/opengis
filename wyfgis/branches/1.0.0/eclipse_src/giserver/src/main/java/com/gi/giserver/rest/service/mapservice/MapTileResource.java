package com.gi.giserver.rest.service.mapservice;

import javax.servlet.ServletContext;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;

import com.gi.giserver.core.service.ServiceManager;
import com.gi.giserver.core.service.mapservice.MapService;
import com.gi.giserver.core.service.mapservice.desc.MapServiceDesc;
import com.gi.giserver.core.service.tokenservice.TokenService;

/**
 * @author Wu Yongfeng
 * 
 */
@Path("/MapService/{mapName}/tile/{level}/{row}/{col}")
public class MapTileResource {

	@Context
	ServletContext context;

	@GET
	public byte[] getResult(@PathParam("mapName") String mapName, @PathParam("level") String level,
			@PathParam("row") String row, @PathParam("col") String col, @QueryParam("token") String token) {
		return result(mapName, level, row, col, token);
	}

	@POST
	public byte[] postResult(@PathParam("mapName") String mapName, @PathParam("level") String level,
			@PathParam("row") String row, @PathParam("col") String col, @FormParam("token") String token) {
		return result(mapName, level, row, col, token);
	}

	private synchronized byte[] result(String mapName, String level, String row, String col, String token) {
		byte[] result = null;

		try {
			MapService mapService = ServiceManager.getMapService(mapName);

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
				if (mapServiceDesc.getTileDesc().isCreateOnDemand()) {
					result = mapService.getOrCreateTile(nLevel, nRow, nCol);
				} else {
					result = mapService.getTile(nLevel, nRow, nCol);
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return result;
	}

}

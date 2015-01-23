package com.gi.giserver.wms.service;

import javax.jws.WebParam;
import javax.jws.WebService;

@WebService
public interface WMS_1_3_0 {
	String getCapabilities(@WebParam(name="version") String version);
}

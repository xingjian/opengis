package com.gi.giserver.wms.service;

import javax.jws.WebService;

@WebService(endpointInterface = "com.gi.giserver.wms.service.WMS_1_3_0",
        serviceName = "WMS")
public class WMSImpl_1_3_0 implements WMS_1_3_0 {
	
	public String getCapabilities(String version) {
		String result = "hello";

		return result;
	}
}

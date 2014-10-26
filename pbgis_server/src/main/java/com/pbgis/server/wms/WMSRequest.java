/** @文件名: WMSRequest.java @创建人：邢健  @创建日期： 2014-1-18 下午2:10:33 */

package com.pbgis.server.wms;
import java.util.Map;
/**
 * 
* @类名: WMSRequest.java 
* @包名: com.pbgis.server.wms 
* @描述: 封装WMSRequest其它 
* @作者: xingjian xingjian@yeah.net   
* @日期:2014-1-18 下午2:24:14 
* @版本: V1.0
 */
public abstract class WMSRequest {
	protected String baseUrl;
	protected Map<String, String> rawKvp;
    protected boolean get;
    protected String request;
    private String requestCharset;
    protected String version;

    public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	protected WMSRequest(final String request) {
        setRequest(request);
    }

    public boolean isGet() {
        return get;
    }

    public void setGet(boolean get) {
        this.get = get;
    }

    public String getRequest() {
        return request;
    }

    public void setRequest(String request) {
        this.request = request;
    }

    public void setRawKvp(Map<String, String> rawKvp) {
        this.rawKvp = rawKvp;
    }

    public void setBaseUrl(final String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public String getBaseUrl() {
        return this.baseUrl;
    }

    public Map<String, String> getRawKvp() {
        return rawKvp;
    }

    public String getRequestCharset() {
        return requestCharset;
    }

    public void setRequestCharset(String requestCharset) {
        this.requestCharset = requestCharset;
    }
}

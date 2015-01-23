package com.gi.giserver.rest.service.tokenservice;

import javax.servlet.ServletContext;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;

import org.json.JSONObject;

import com.gi.giserver.core.service.tokenservice.TokenService;
import com.gi.giserver.rest.html.ServiceHTML;

/**
 * @author Wu Yongfeng
 * 
 */
@Path("/TokenService/verify")
public class VerifyResource {

	@Context
	ServletContext context;

	@GET
	public String getResult(@QueryParam("f") @DefaultValue("html") String f, @QueryParam("token") String token) {
		return result(f, token);
	}

	@POST
	public String postResult(@FormParam("f") @DefaultValue("html") String f, @FormParam("token") String token) {
		return result(f, token);
	}

	private String result(String f, String token) {
		String result = null;

		try {
			boolean isVerified = TokenService.verifyToken(token);

			// Various out format
			if ("json".equals(f)) {
				result = this.generateJSONResult(isVerified);
			} else if ("html".equals(f)) {
				result = this.generateHTMLResult(isVerified, token);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return result;
	}

	private String generateJSONResult(boolean isVerified) {
		String result = null;

		try {
			JSONObject obj = new JSONObject();

			obj.put("about", "Powered by GIServer");
			obj.put("verify", isVerified);

			result = obj.toString();
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return result;
	}

	private String generateHTMLResult(boolean isVerified, String token) {
		String result = "";

		try {
			String contextRoot = context.getContextPath();

			String restBody = "";

			restBody += "<form><table style='border:1px solid #000000;'>";
			restBody += "<tr><td>Token:</td><td><input size='50' type='text' name='token' /></td></tr>";
			restBody += "<tr><td colspan='2'><input type='submit' value='Verify Token' /></td></tr>";
			restBody += "</table></form>";

			if (token != null) {
				if (isVerified) {
					restBody += "<div style='color:#aaffaa'>Token '" + token + "' is verified!<br/></div><br/>";
				} else {
					restBody += "<div style='color:#ffaaaa'>Token is NOT verified.<br/></div>";
				}
			}

			result += ServiceHTML.getHeader(contextRoot).replace("${TITLE}", "Verify Token");
			result += ServiceHTML.getNav(contextRoot).replace("${CATALOG}",
					" &gt; <a href='" + contextRoot + "/rest/service/TokenService'>Token Service</a> &gt; Verify");
			result += ServiceHTML.getH2().replace("${TITLE}", "Verify Token");
			result += ServiceHTML.getRestBody().replace("${RESTBODY}", restBody);
			result += ServiceHTML.getFooter();
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return result;
	}

}

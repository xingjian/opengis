package com.gi.server.rest.service.tokenservice;

import javax.servlet.ServletContext;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import org.json.JSONObject;

import com.gi.engine.util.VersionUtil;
import com.gi.server.core.service.tokenservice.TokenService;
import com.gi.server.rest.html.ServiceHTML;

/**
 * @author wuyf
 * 
 */
@Path("/TokenService/verify")
@Produces( { MediaType.TEXT_HTML, MediaType.APPLICATION_JSON })
public class VerifyResource {

	@Context
	ServletContext context;

	@GET
	public String getResult(@QueryParam("f") @DefaultValue("html") String f,
			@QueryParam("token") String token) {
		return result(f, token);
	}

	@POST
	public String postResult(@FormParam("f") @DefaultValue("html") String f,
			@FormParam("token") String token) {
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

			obj.put("GIServerVersion", VersionUtil.getCurrentversion());
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

			StringBuilder sb = new StringBuilder();
			sb.append("<form><table style='border:1px solid #000000;'>");
			sb
					.append("<tr><td>Token:</td><td><input size='50' type='text' name='token' /></td></tr>");
			sb
					.append("<tr><td colspan='2'><input type='submit' value='Verify Token' /></td></tr>");
			sb.append("</table></form>");
			if (token != null) {
				if (isVerified) {
					sb.append("<div style='color:#aaffaa'>Token '" + token
							+ "' is verified!<br/></div><br/>");
				} else {
					sb
							.append("<div style='color:#ffaaaa'>Token is NOT verified.<br/></div>");
				}
			}
			String restBody = sb.toString();

			ServiceHTML html = new ServiceHTML();
			html.setContextRoot(contextRoot);
			html.setTitle("Verify Token");
			html
					.setCatalog(" &gt; <a href='"
							+ contextRoot
							+ "/rest/service/TokenService'>Token Service</a> &gt; Verify");
			html.setHeader("Verify Token");
			html.setRestBody(restBody);
			result = html.toString();
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return result;
	}

}

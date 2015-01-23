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
@Path("/TokenService/get")
@Produces( { MediaType.TEXT_HTML, MediaType.APPLICATION_JSON })
public class GetResource {

	@Context
	ServletContext context;

	@GET
	public String getResult(@QueryParam("f") @DefaultValue("html") String f,
			@QueryParam("username") String username,
			@QueryParam("password") String password,
			@QueryParam("client") String client,
			@QueryParam("expire") String expire) {
		return result(f, username, password, client, expire);
	}

	@POST
	public String postResult(@FormParam("f") @DefaultValue("html") String f,
			@FormParam("username") String username,
			@FormParam("password") String password,
			@FormParam("client") String client,
			@FormParam("expire") String expire) {
		return result(f, username, password, client, expire);
	}

	private String result(String f, String username, String password,
			String client, String expire) {
		String result = null;

		try {
			int expireMinutes = 0;
			if (expire != null) {
				try {
					expireMinutes = Integer.valueOf(expire);
				} catch (NumberFormatException ex) {
					ex.printStackTrace();
				}
			}
			String token = TokenService.getToken(username, password, client,
					expireMinutes);

			// Various out format
			if ("json".equals(f)) {
				result = this.generateJSONResult(token);
			} else if ("html".equals(f)) {
				result = this.generateHTMLResult(token, username, password,
						client, expire);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return result;
	}

	private String generateJSONResult(String token) {
		String result = null;

		try {
			JSONObject obj = new JSONObject();

			obj.put("GIServerVersion", VersionUtil.getCurrentversion());
			obj.put("token", token);

			result = obj.toString();
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return result;
	}

	private String generateHTMLResult(String token, String username,
			String password, String client, String expire) {
		String result = "";

		try {
			String contextRoot = context.getContextPath();

			StringBuilder sb = new StringBuilder();
			sb.append("<form><table style='border:1px solid #000000;'>");
			sb
					.append("<tr><td>User Name:</td><td><input type='text' name='username' value='${USERNAME}' /></td></tr>");
			sb
					.append("<tr><td>Password:</td><td><input type='password' name='password' value='${PASSWORD}' /></td></tr>");
			sb
					.append("<tr><td>Client ID:</td><td><input type='text' name='client' value='${CLIENT}' /></td></tr>");
			sb
					.append("<tr><td>Expire:</td><td><input type='text' name='expire' value='${EXPIRE}' /></td></tr>");
			sb
					.append("<tr><td colspan='2'><input type='submit' value='Get Token' /></td></tr>");
			sb.append("</table></form>");
			String restBody = sb.toString();

			restBody = restBody.replace("${USERNAME}", username == null ? ""
					: username);
			restBody = restBody.replace("${PASSWORD}", password == null ? ""
					: password);
			restBody = restBody.replace("${CLIENT}", client == null ? ""
					: client);
			restBody = restBody.replace("${EXPIRE}", expire == null ? ""
					: expire);

			if (token != null) {
				restBody += "<br/><b>Token:</b><br/><div style='color:#33ff33'>"
						+ token + "</div>";
			}

			ServiceHTML html = new ServiceHTML();
			html.setContextRoot(contextRoot);
			html.setTitle("Get Token");
			html.setCatalog(" &gt; <a href='" + contextRoot
					+ "/rest/service/TokenService'>Token Service</a> &gt; Get");
			html.setHeader("Get Token");
			html.setRestBody(restBody);
			result = html.toString();
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return result;
	}

}

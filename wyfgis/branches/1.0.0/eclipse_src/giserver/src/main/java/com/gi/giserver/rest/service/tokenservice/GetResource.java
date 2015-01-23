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
@Path("/TokenService/get")
public class GetResource {

	@Context
	ServletContext context;

	@GET
	public String getResult(@QueryParam("f") @DefaultValue("html") String f, @QueryParam("username") String username,
			@QueryParam("password") String password, @QueryParam("client") String client,
			@QueryParam("expire") String expire) {
		return result(f, username, password, client, expire);
	}

	@POST
	public String postResult(@FormParam("f") @DefaultValue("html") String f, @FormParam("username") String username,
			@FormParam("password") String password, @FormParam("client") String client,
			@FormParam("expire") String expire) {
		return result(f, username, password, client, expire);
	}

	private String result(String f, String username, String password, String client, String expire) {
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
			String token = TokenService.getToken(username, password, client, expireMinutes);

			// Various out format
			if ("json".equals(f)) {
				result = this.generateJSONResult(token);
			} else if ("html".equals(f)) {
				result = this.generateHTMLResult(token, username, password, client, expire);
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

			obj.put("about", "Powered by GIServer");
			obj.put("token", token);

			result = obj.toString();
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return result;
	}

	private String generateHTMLResult(String token, String username, String password, String client, String expire) {
		String result = "";

		try {
			String contextRoot = context.getContextPath();

			String restBody = "";

			restBody += "<form><table style='border:1px solid #000000;'>";
			restBody += "<tr><td>User Name:</td><td><input type='text' name='username' value='${USERNAME}' /></td></tr>";
			restBody += "<tr><td>Password:</td><td><input type='password' name='password' value='${PASSWORD}' /></td></tr>";
			restBody += "<tr><td>Client ID:</td><td><input type='text' name='client' value='${CLIENT}' /></td></tr>";
			restBody += "<tr><td>Expire:</td><td><input type='text' name='expire' value='${EXPIRE}' /></td></tr>";
			restBody += "<tr><td colspan='2'><input type='submit' value='Get Token' /></td></tr>";
			restBody += "</table></form>";

			restBody = restBody.replace("${USERNAME}", username == null ? "" : username);
			restBody = restBody.replace("${PASSWORD}", password == null ? "" : password);
			restBody = restBody.replace("${CLIENT}", client == null ? "" : client);
			restBody = restBody.replace("${EXPIRE}", expire == null ? "" : expire);

			if(token != null){
				restBody += "<br/><b>Token:</b><br/><div style='color:#33ff33'>" + token + "</div>";
			}

			result += ServiceHTML.getHeader(contextRoot).replace("${TITLE}", "Get Token");
			result += ServiceHTML.getNav(contextRoot).replace("${CATALOG}",
					" &gt; <a href='" + contextRoot + "/rest/service/TokenService'>Token Service</a> &gt; Get");
			result += ServiceHTML.getH2().replace("${TITLE}", "Get Token");
			result += ServiceHTML.getRestBody().replace("${RESTBODY}", restBody);
			result += ServiceHTML.getFooter();
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return result;
	}

}

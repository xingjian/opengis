package com.gi.server.rest.admin;

import javax.servlet.ServletContext;
import javax.ws.rs.CookieParam;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import com.gi.server.core.auth.Administrator;
import com.gi.server.rest.html.AdministratorHTML;

/**
 * @author wuyf
 * 
 */
@Path("/reconfig")
@Produces( { MediaType.TEXT_HTML })
public class ReconfigResource {

	@Context
	ServletContext context;

	@GET
	public String getResult(@QueryParam("f") @DefaultValue("html") String f,
			@CookieParam("username") String username,
			@CookieParam("password") String password) {
		return result(f, username, password);
	}

	@POST
	public String postResult(@FormParam("f") @DefaultValue("html") String f,
			@CookieParam("username") String username,
			@CookieParam("password") String password) {
		return result(f, username, password);
	}

	private String result(String f, String username, String password) {
		String result = null;

		try {
			Administrator admin = new Administrator(username, password);
			boolean isSucceed = admin.reloadConfigs(context);

			if ("html".equals(f)) {
				result = this.generateHTMLResult(admin, isSucceed);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return result;
	}

	private String generateHTMLResult(Administrator admin, boolean isSucceed) {
		String result = "";

		String contextRoot = context.getContextPath();
		String rootURI = contextRoot + "/rest/admin/";
		String thisURI = rootURI + "reconfig";

		String restBody = "";
		if (!admin.isAuthorized()) {
			restBody = AdministratorHTML.getLoginForm();
			restBody = restBody.replace("${POST_URI}", thisURI);
		} else {
			if (isSucceed) {
				restBody += "<b>Succeed reload configurations !</b>";
			} else {
				restBody += "<b>Reload configurations failed !</b>";
			}
		}

		AdministratorHTML html = new AdministratorHTML();
		html.setContextRoot(contextRoot);
		html.setTitle("Reload Configurations");
		html.setHome(rootURI);
		html.setCatalog(" &gt; Reload Configurations");
		html.setHeader("Reload Configurations");
		html.setRestBody(restBody);
		result = html.toString();

		return result;
	}

}

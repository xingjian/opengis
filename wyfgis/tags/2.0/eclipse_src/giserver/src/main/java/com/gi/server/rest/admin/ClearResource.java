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

import com.gi.server.core.auth.Administrator;
import com.gi.server.rest.html.AdministratorHTML;


/**
 * @author wuyf
 *
 */
@Path("/clear")
public class ClearResource {

	@Context
	ServletContext context;

	@GET
	@Produces("text/html")
	public String getResult(@QueryParam("f") @DefaultValue("html") String f, @CookieParam("username") String username,
			@CookieParam("password") String password) {
		return result(f, username, password);
	}

	@POST
	@Produces("text/html")
	public String postResult(@FormParam("f") @DefaultValue("html") String f, @CookieParam("username") String username,
			@CookieParam("password") String password) {
		return result(f, username, password);
	}

	private String result(String f, String username, String password) {
		String result = null;

		try {
			Administrator admin = new Administrator(username, password);
			admin.clearServer();

			if ("html".equals(f)) {
				result = this.generateHTMLResult(admin);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return result;
	}

	private String generateHTMLResult(Administrator admin) {
		String result = "";

		String contextRoot = context.getContextPath();
		String rootURI = contextRoot + "/rest/admin/";
		String thisURI = rootURI + "clear";

		String restBody = "";
		if (!admin.isAuthorized()) {
			restBody = AdministratorHTML.getLoginForm();
			restBody = restBody.replace("${POST_URI}", thisURI);
		} else {
			restBody += "<b>Succeed clear not used resource !</b>";
		}

		AdministratorHTML html = new AdministratorHTML();
		html.setContextRoot(contextRoot);
		html.setTitle("Clear");
		html.setHome(rootURI);
		html.setCatalog(" &gt; Clear");
		html.setHeader("Clear");
		html.setRestBody(restBody);		
		result = html.toString();

		return result;
	}

}
